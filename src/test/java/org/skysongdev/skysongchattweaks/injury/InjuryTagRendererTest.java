package org.skysongdev.skysongchattweaks.injury;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skysongdev.skysongchattweaks.concurrent.DirectMainThread;
import org.skysongdev.skysongstats.api.CharacterId;
import org.skysongdev.skysongstats.api.Injuries;
import org.skysongdev.skysongstats.api.InjuryType;
import org.skysongdev.skysongstats.api.Profiles;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InjuryTagRendererTest {

    private static final UUID SPEAKER = UUID.randomUUID();
    private static final CharacterId CHARACTER = new CharacterId(SPEAKER, "Default");

    private final Injuries injuries = mock(Injuries.class);
    private final Profiles profiles = mock(Profiles.class);
    private final DirectMainThread mainThread = new DirectMainThread();
    private final InjuryTagRenderer renderer = new InjuryTagRenderer(injuries, profiles, mainThread);

    @BeforeEach
    void injuriesYml() {
        when(injuries.type(anyString())).thenReturn(Optional.empty());
        when(injuries.tag(anyString())).thenReturn(Optional.empty());
        define("broken-leg");
        define("cut_2");
        when(profiles.active(SPEAKER)).thenReturn(Optional.of(CHARACTER));
    }

    /** An injury that SkysongStats finds ignoring case, as it does. */
    private void define(String id) {
        InjuryType type = new InjuryType(id, id, "", "BONE", "minor", "", List.of(), Map.of());
        for (String typed : List.of(id, id.toUpperCase())) {
            when(injuries.type(typed)).thenReturn(Optional.of(type));
        }
        when(injuries.tag(id)).thenReturn(Optional.of("<tag:" + id + ">"));
    }

    @Test
    void showsAnInjuryTheSpeakerKnows() {
        when(injuries.knows(CHARACTER, "broken-leg")).thenReturn(true);

        InjuryTagRenderer.Rendered out = renderer.render(SPEAKER, "my [injury_broken-leg] hurts");

        assertEquals("my <tag:broken-leg> hurts", out.message());
        assertEquals(Set.of("broken-leg"), out.shown());
    }

    @Test
    void acceptsDigitsUnderscoresAndAnyCase() {
        when(injuries.knows(CHARACTER, "cut_2")).thenReturn(true);
        when(injuries.knows(CHARACTER, "broken-leg")).thenReturn(true);

        InjuryTagRenderer.Rendered out = renderer.render(SPEAKER, "[injury_cut_2] [injury_BROKEN-LEG]");

        assertEquals("<tag:cut_2> <tag:broken-leg>", out.message());
        assertEquals(List.of("cut_2", "broken-leg"), List.copyOf(out.shown()));
    }

    @Test
    void leavesAnInjuryTheSpeakerDoesNotKnow() {
        InjuryTagRenderer.Rendered out = renderer.render(SPEAKER, "[injury_broken-leg]");

        assertEquals("[injury_broken-leg]", out.message());
        assertTrue(out.shown().isEmpty());
    }

    @Test
    void leavesUnknownInjuriesAndNeverAsksTheMainThread() {
        InjuryTagRenderer.Rendered out = renderer.render(SPEAKER, "[injury_nope] [injury_] plain");

        assertEquals("[injury_nope] [injury_] plain", out.message());
        verify(profiles, never()).active(any());
    }

    @Test
    void aPlayerWithoutCharacterKnowsNothing() {
        when(profiles.active(SPEAKER)).thenReturn(Optional.empty());

        assertEquals("[injury_broken-leg]", renderer.render(SPEAKER, "[injury_broken-leg]").message());
    }

    @Test
    void aFailedReadLeavesTheMessageAsTyped() {
        when(injuries.knows(CHARACTER, "broken-leg")).thenReturn(true);
        mainThread.failReads();

        InjuryTagRenderer.Rendered out = renderer.render(SPEAKER, "[injury_broken-leg]");

        assertEquals("[injury_broken-leg]", out.message());
        assertTrue(out.shown().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"$1", "\\", "$"})
    void keepsSpecialCharactersOfATag(String special) {
        when(injuries.knows(CHARACTER, "broken-leg")).thenReturn(true);
        when(injuries.tag("broken-leg")).thenReturn(Optional.of("<hover:show_text:'" + special + "'>Leg"));

        String out = renderer.render(SPEAKER, "[injury_broken-leg]").message();

        assertEquals("<hover:show_text:'" + special + "'>Leg", out);
    }

    @Test
    void asksAboutEachInjuryOnce() {
        when(injuries.knows(CHARACTER, "broken-leg")).thenReturn(true);

        InjuryTagRenderer.Rendered out = renderer.render(SPEAKER, "[injury_broken-leg] and [injury_broken-leg]");

        assertEquals("<tag:broken-leg> and <tag:broken-leg>", out.message());
        verify(injuries).knows(CHARACTER, "broken-leg");
        assertFalse(out.shown().isEmpty());
    }
}

package org.skysongdev.skysongchattweaks.injury;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.skysongdev.skysongchattweaks.chat.ChatAudience;
import org.skysongdev.skysongstats.api.CharacterId;
import org.skysongdev.skysongstats.api.Injuries;
import org.skysongdev.skysongstats.api.Profiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InjuryTeacherTest {

    private final ChatAudience audience = mock(ChatAudience.class);
    private final Profiles profiles = mock(Profiles.class);
    private final Injuries injuries = mock(Injuries.class);
    private final InjuryTeacher teacher = new InjuryTeacher(audience, profiles, injuries);

    private Player player(Optional<CharacterId> character) {
        Player player = mock(Player.class);
        UUID id = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(id);
        when(profiles.active(id)).thenReturn(character.map(c -> new CharacterId(id, c.profile())));
        return player;
    }

    @Test
    void teachesEveryListenerWithACharacter() {
        Player speaker = player(Optional.of(new CharacterId(UUID.randomUUID(), "Default")));
        Player listener = player(Optional.of(new CharacterId(UUID.randomUUID(), "Rook")));
        Player newcomer = player(Optional.empty());
        when(audience.listeners(speaker, "in-character")).thenReturn(List.of(speaker, listener, newcomer));

        teacher.teach(speaker, "in-character", List.of("broken-leg", "cut"));

        CharacterId rook = profiles.active(listener.getUniqueId()).orElseThrow();
        verify(injuries).discover(rook, "broken-leg");
        verify(injuries).discover(rook, "cut");
        verify(injuries).discover(profiles.active(speaker.getUniqueId()).orElseThrow(), "broken-leg");
    }

    @Test
    void nothingToTeachSkipsTheAudience() {
        Player speaker = player(Optional.empty());

        teacher.teach(speaker, "in-character", List.of());

        verify(audience, never()).listeners(any(), anyString());
        verify(injuries, never()).discover(any(), anyString());
    }
}

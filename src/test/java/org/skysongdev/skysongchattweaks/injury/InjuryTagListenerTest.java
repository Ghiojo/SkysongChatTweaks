package org.skysongdev.skysongchattweaks.injury;

import openrp.chat.events.ORPChatEvent;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.skysongdev.skysongchattweaks.concurrent.DirectMainThread;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InjuryTagListenerTest {

    private final InjuryTagRenderer renderer = mock(InjuryTagRenderer.class);
    private final InjuryTeacher teacher = mock(InjuryTeacher.class);
    private final DirectMainThread mainThread = new DirectMainThread();
    private final InjuryTagListener listener = new InjuryTagListener(renderer, teacher, mainThread);
    private final Player speaker = mock(Player.class);

    private ORPChatEvent chat(String message, Set<String> shown, String rendered) {
        UUID id = UUID.randomUUID();
        when(speaker.getUniqueId()).thenReturn(id);
        when(speaker.isOnline()).thenReturn(true);
        when(renderer.render(id, message)).thenReturn(new InjuryTagRenderer.Rendered(rendered, shown));
        return new ORPChatEvent(speaker, message, "in-character");
    }

    @Test
    void showsTheTagsThenTeachesOnTheMainThread() {
        ORPChatEvent event = chat("[injury_cut]", Set.of("cut"), "<cut>");

        listener.onChat(event);

        assertEquals("<cut>", event.getMessage());
        verify(teacher, never()).teach(any(), anyString(), any());
        mainThread.runPending();
        verify(teacher).teach(speaker, "in-character", Set.of("cut"));
    }

    @Test
    void aMessageWithNothingShownIsLeftAlone() {
        ORPChatEvent event = chat("hello", Set.of(), "hello");

        listener.onChat(event);

        assertEquals("hello", event.getMessage());
        assertEquals(0, mainThread.pendingCount());
    }

    @Test
    void aSpeakerWhoLeftTeachesNobody() {
        ORPChatEvent event = chat("[injury_cut]", Set.of("cut"), "<cut>");
        listener.onChat(event);
        when(speaker.isOnline()).thenReturn(false);

        mainThread.runPending();

        verify(teacher, never()).teach(any(), anyString(), any());
    }
}

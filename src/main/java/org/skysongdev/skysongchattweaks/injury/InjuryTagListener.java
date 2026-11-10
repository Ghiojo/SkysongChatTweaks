package org.skysongdev.skysongchattweaks.injury;

import openrp.chat.events.ORPChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.skysongdev.skysongchattweaks.concurrent.MainThread;

/**
 * {@code [injury_x]} in chat: shows the injury when the speaker knows it, then teaches it to
 * everyone who hears the message.
 */
public final class InjuryTagListener implements Listener {

    private final InjuryTagRenderer renderer;
    private final InjuryTeacher teacher;
    private final MainThread mainThread;

    public InjuryTagListener(InjuryTagRenderer renderer, InjuryTeacher teacher, MainThread mainThread) {
        this.renderer = renderer;
        this.teacher = teacher;
        this.mainThread = mainThread;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(ORPChatEvent event) {
        Player speaker = event.getPlayer();
        InjuryTagRenderer.Rendered rendered = renderer.render(speaker.getUniqueId(), event.getMessage());
        if (rendered.shown().isEmpty()) {
            return;
        }
        event.setMessage(rendered.message());

        String channel = event.getChannel();
        mainThread.run(() -> {
            if (speaker.isOnline()) {
                teacher.teach(speaker, channel, rendered.shown());
            }
        });
    }
}

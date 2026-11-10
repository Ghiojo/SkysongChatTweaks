package org.skysongdev.skysongchattweaks.condition;

import openrp.chat.events.ORPChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/** {@code [condition_x_N]} in chat. Needs no main thread, so it renders wherever the event fires. */
public final class ConditionTagListener implements Listener {

    private final ConditionTagRenderer renderer;

    public ConditionTagListener(ConditionTagRenderer renderer) {
        this.renderer = renderer;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(ORPChatEvent event) {
        String rendered = renderer.render(event.getMessage());
        if (!rendered.equals(event.getMessage())) {
            event.setMessage(rendered);
        }
    }
}

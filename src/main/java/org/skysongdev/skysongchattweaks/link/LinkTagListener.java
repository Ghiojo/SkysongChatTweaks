package org.skysongdev.skysongchattweaks.link;

import openrp.chat.events.ORPChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.skysongdev.skysongchattweaks.chat.TagReplacer;

import java.util.Optional;
import java.util.regex.Pattern;

/** Turns web links in chat into a clickable {@code [Link]} that shows the address on hover. */
public final class LinkTagListener implements Listener {

    static final Pattern LINK = Pattern.compile("(https://)?www\\.[a-zA-ZñÑ0-9&\\-]+\\.[a-zA-ZñÑ0-9&/?=\\-_]+");

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(ORPChatEvent event) {
        String rendered = render(event.getMessage());
        if (!rendered.equals(event.getMessage())) {
            event.setMessage(rendered);
        }
    }

    static String render(String message) {
        return TagReplacer.replace(message, LINK, match -> Optional.of(
                "<hover:show_text:'<gray>Link: <gold>" + match.group() + "'><click:open_url:" + match.group()
                        + "><blue>[Link]</blue></click></hover>"));
    }
}

package org.skysongdev.skysongchattweaks.listeners;

import openrp.chat.events.ORPChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkChecker implements Listener {
    Pattern pattern = Pattern.compile("(https:\\/\\/)?www\\.[a-zA-ZñÑ0-9&\\-]+\\.[a-zA-ZñÑ0-9&\\/\\?\\=\\-\\_]+");


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onORPChat(ORPChatEvent e){
        StringBuilder sb = new StringBuilder();
        String newmessage = e.getMessage();
        Matcher matcher = pattern.matcher(newmessage);
        while(matcher.find()){
            matcher.appendReplacement(sb, "<hover:show_text:'<gray>Link: <gold>" + matcher.group() + "'><click:open_url:"+ matcher.group() + "><blue>[Link]</blue></click></hover>");
        }
        matcher.appendTail(sb);
        e.setMessage(sb.toString());
        Bukkit.getLogger().info(sb.toString());
    }
}

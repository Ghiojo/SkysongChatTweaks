package org.skysongdev.skysongchattweaks.listeners;

import openrp.chat.events.ORPChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.skysongdev.skysongchattweaks.SkysongChatTweaks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.getPlugin;

public class ConditionChecker implements Listener {
    Pattern pattern = Pattern.compile("\\[condition_([a-zA-Z\\-]*)_?([1-9]*)?]");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onORPChat(ORPChatEvent e){
        StringBuilder sb = new StringBuilder();
        String newmessage = e.getMessage();
        Matcher matcher = pattern.matcher(newmessage);
        while(matcher.find()){
            for(int i = 0; i < getPlugin().conditions.size(); i++){
                if(getPlugin().conditions.get(i).getId().equals(matcher.group(1))){
                    try{
                        matcher.appendReplacement(sb, "<hover:show_text:\"" + getPlugin().conditions.get(i).getDescription(Integer.parseInt(matcher.group(2))) + "\"><dark_gray>[</dark_gray><white>" + getPlugin().conditions.get(i).getName() + " " + matcher.group(2) +"</white><dark_gray>]</dark_gray></hover>");
                    }catch (NumberFormatException ex){
                        matcher.appendReplacement(sb, "<hover:show_text:\"" + getPlugin().conditions.get(i).getDescription() + "\"><dark_gray>[</dark_gray><white>" + getPlugin().conditions.get(i).getName() +"</white><dark_gray>]</dark_gray></hover>");
                    }

                }
            }
        }
        matcher.appendTail(sb);
        e.setMessage(sb.toString());
        Bukkit.getLogger().info(sb.toString());
    }
}

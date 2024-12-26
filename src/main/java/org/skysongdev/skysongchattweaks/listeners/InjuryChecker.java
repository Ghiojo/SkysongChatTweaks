package org.skysongdev.skysongchattweaks.listeners;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import openrp.chat.events.ORPChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.skysongdev.skysongchattweaks.utils.Injury;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.*;

public class InjuryChecker implements Listener {
    Pattern pattern = Pattern.compile("\\[injury_([a-zA-Z\\-]*)]");

    @EventHandler
    public void onORPChat(ORPChatEvent e){
        StringBuilder sb = new StringBuilder();
        String newmessage = e.getMessage();
        Matcher matcher = pattern.matcher(newmessage);
        ArrayList<Injury> displayedInjuries = new ArrayList<>();
        while(matcher.find()){
            for(int i = 0; i < getPlugin().injuries.size(); i++){
                if(getPlugin().injuries.get(i).getId().equals(matcher.group(1))){
                    if(!getPlugin().injuries.get(i).doesPlayerHavePermission(e.getPlayer())){
                        break;
                    }
                    matcher.appendReplacement(sb, "<hover:show_text:\"" + getPlugin().injuries.get(i).getDescription() + "\"><dark_gray>[</dark_gray><white>" + getPlugin().injuries.get(i).getName() +"</white><dark_gray>]</dark_gray></hover>");
                    displayedInjuries.add(getPlugin().injuries.get(i));
                }
            }
        }
        matcher.appendTail(sb);
        e.setMessage(sb.toString());
        for(Player player : Bukkit.getOnlinePlayers()){
            if (player.getWorld().equals(e.getPlayer().getWorld())) {
                if (player.getLocation().distance(e.getPlayer().getLocation()) <= getPlugin().getOpenRP().getChat().getConfig().getInt("channels." + e.getChannel() + ".range")) {
                    for(Injury injury : displayedInjuries){
                        if(!injury.doesPlayerHavePermission(player)){
                            User user = getLuckPerms().getUserManager().getUser(player.getUniqueId());
                            if (user != null && !injury.getPermissions().isEmpty()) {
                                String permission = injury.getPermissions().getFirst();
                                Node node = PermissionNode.builder(permission).build();
                                user.data().add(node);
                                getLuckPerms().getUserManager().saveUser(user);
                                getPlugin().getProfile(getSkysongStats().getUtils().getProfileManager().getActiveProfileName(player.getUniqueId().toString()), player.getUniqueId().toString()).addPermission(injury.getPermissions().get(0));
                            }
                        }
                    }
                }
            }
        }
        Bukkit.getLogger().info(sb.toString());
    }
}

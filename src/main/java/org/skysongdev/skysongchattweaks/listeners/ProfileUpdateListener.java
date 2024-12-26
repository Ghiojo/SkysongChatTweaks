package org.skysongdev.skysongchattweaks.listeners;

import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.skysongdev.skysongchattweaks.database.Profile;
import org.skysongdev.skysongchattweaks.utils.Injury;
import org.skysongdev.skysongstats.events.ProfileUpdateEvent;

import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.getPlugin;

public class ProfileUpdateListener implements Listener {
    @EventHandler
    public void onProfileUpdate(ProfileUpdateEvent event) {
        Player player = event.getPlayer();
        for(Injury injury : getPlugin().injuries){
            if (!injury.getPermissions().isEmpty()) {
                String firstPermission = injury.getPermissions().get(0);
                getPlugin().getLuckPerms().getUserManager().modifyUser(player.getUniqueId(), user -> {
                    Node node = PermissionNode.builder(firstPermission).build();
                    user.data().remove(node);
                });
            }
        }
        Profile profile = getPlugin().getProfile(event.getProfile(), event.getPlayerUUID());
        for(String permission : profile.getPermissions()){
            getPlugin().getLuckPerms().getUserManager().modifyUser(player.getUniqueId(), user -> {
                Node node = PermissionNode.builder(permission).build();
                user.data().add(node);
            });
        }
    }
}

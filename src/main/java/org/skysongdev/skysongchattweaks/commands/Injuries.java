package org.skysongdev.skysongchattweaks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skysongdev.skysongchattweaks.gui.InjuriesGUI;
import org.skysongdev.skysongchattweaks.utils.Condition;
import org.skysongdev.skysongchattweaks.utils.Injury;

import java.util.ArrayList;
import java.util.List;

import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.PLUGIN_TAG;
import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.getPlugin;

public class Injuries implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if(args.length == 0){
            player.openInventory(new InjuriesGUI(player).getInventory());
        } else {
            switch (args[0]) {
                case "search":
                    if(args.length < 2){
                        player.sendMessage(getPlugin().getMiniMessage().deserialize(PLUGIN_TAG + "Usage: /injuries search <injury>"));
                        return true;
                    }
                    InjuriesGUI gui = new InjuriesGUI(player);
                    gui.setupInventory(searchInjuries(args[1]));
                    player.openInventory(gui.getInventory());
                    break;
                case "view":
                    if(args.length < 2){
                        player.openInventory(new InjuriesGUI(player).getInventory());
                    } else {
                        InjuriesGUI gui1 = new InjuriesGUI(player);
                        gui1.fetchViewableInjuries(args[1]);
                        gui1.setupInventory();
                        player.openInventory(gui1.getInventory());
                    }
                    break;
                default:
                    player.sendMessage(getPlugin().getMiniMessage().deserialize(PLUGIN_TAG + "Usage: /injuries [search|view]"));
                    break;
            }
        }
        return true;
    }

    private ArrayList<Injury> searchInjuries(String search){
        ArrayList<Injury> injuries = new ArrayList<>();
        for(Injury injury : getPlugin().injuries){
            if(injury.getName().toLowerCase().contains(search.toLowerCase())){
                injuries.add(injury);
            }
        }
        return injuries;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return List.of("search", "view");
        }
        if(args.length == 2) {
            if (args[0].equalsIgnoreCase("view")) {
                return List.of("minor", "moderate", "severe", "permanent");
            }
        }
        return List.of();
    }
}

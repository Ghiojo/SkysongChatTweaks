package org.skysongdev.skysongchattweaks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skysongdev.skysongchattweaks.gui.ConditionsGUI;
import org.skysongdev.skysongchattweaks.utils.Condition;

import java.util.ArrayList;
import java.util.List;

import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.PLUGIN_TAG;
import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.getPlugin;

public class Conditions implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if(strings.length == 0){
            player.openInventory(new ConditionsGUI().getInventory());
            return true;
        } else{
            switch(strings[0]){
                case "search":
                    if(strings.length < 2){
                        player.sendMessage(getPlugin().getMiniMessage().deserialize(PLUGIN_TAG + "<red>Usage: /conditions search <search>"));
                        return true;
                    }
                    if(searchConditions(strings[1]).size() == 0){
                        player.sendMessage(getPlugin().getMiniMessage().deserialize(PLUGIN_TAG + "<red>No conditions found with that name."));
                        return true;
                    }
                    ConditionsGUI gui = new ConditionsGUI();
                    gui.setupInventory(searchConditions(strings[1]));
                    player.openInventory(gui.getInventory());
                    break;
                case "view":
                    player.openInventory(new ConditionsGUI().getInventory());
                    break;
                default:
                    player.sendMessage(getPlugin().getMiniMessage().deserialize(PLUGIN_TAG + "<red>Usage: /conditions [search|view]"));
                    break;
            }
            return true;
        }
    }

    public ArrayList<Condition> searchConditions(String search){
        ArrayList<Condition> conditions = new ArrayList<>();
        for(Condition condition : getPlugin().conditions){
            if(condition.getName().toLowerCase().contains(search.toLowerCase())){
                conditions.add(condition);
            }
        }
        return conditions;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of("search", "view").stream().filter(a -> a.startsWith(strings[0])).toList();
    }
}

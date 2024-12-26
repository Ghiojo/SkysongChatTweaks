package org.skysongdev.skysongchattweaks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.PLUGIN_TAG;
import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.getPlugin;

public class Reload implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        getPlugin().reloadConfig();
        getPlugin().InitializeConditions();
        getPlugin().InitializeInjuries();
        sender.sendMessage(getPlugin().getMiniMessage().deserialize(PLUGIN_TAG + "Reloaded SkysongChatTweaks config"));
        return true;
    }
}

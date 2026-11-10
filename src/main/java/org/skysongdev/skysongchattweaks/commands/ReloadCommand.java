package org.skysongdev.skysongchattweaks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * {@code /screload}: reloads config.yml. Injuries and conditions are SkysongStats', so
 * {@code /ssreload} reloads those.
 */
public final class ReloadCommand implements CommandExecutor {

    private final Runnable reloadConfig;

    /** @param reloadConfig reloads config.yml, such as {@code plugin::reloadConfig} */
    public ReloadCommand(Runnable reloadConfig) {
        this.reloadConfig = reloadConfig;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        reloadConfig.run();
        sender.sendMessage(PluginMessages.prefixed("Reloaded SkysongChatTweaks config"));
        return true;
    }
}

package org.skysongdev.skysongchattweaks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.logging.Logger;

/** {@code /play <link> <title>}: says in OpenRP's {@code playing} channel which song the player plays. */
public final class PlayCommand implements CommandExecutor {

    /** Sends a message as a player into an OpenRP channel, such as {@code openrp.getChat()::sendMessage}. */
    @FunctionalInterface
    public interface ChannelSender {
        void send(Player player, String message, String channel);
    }

    static final String CHANNEL = "playing";

    private final ChannelSender chat;
    private final Logger logger;

    public PlayCommand(ChannelSender chat, Logger logger) {
        this.chat = chat;
        this.logger = logger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PluginMessages.prefixed("<gray>Only players can play songs."));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(PluginMessages.prefixed("<gray>You need to put in a link and a name for the song!"));
            return true;
        }
        String link = args[0];
        String title = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        chat.send(player, message(link, title), CHANNEL);
        logger.info(player.getName() + " plays " + title + " (" + link + ")");
        return true;
    }

    static String message(String link, String title) {
        return "is playing <gold><hover:show_text:'<gray>Link: <gold>" + link + "'><click:open_url:" + link + ">"
                + title + "</click></hover><reset>";
    }
}

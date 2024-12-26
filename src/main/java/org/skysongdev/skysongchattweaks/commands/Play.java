package org.skysongdev.skysongchattweaks.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.skysongdev.skysongchattweaks.SkysongChatTweaks;

import java.util.Arrays;

public class Play implements CommandExecutor {
    private SkysongChatTweaks plugin;
    public Play(SkysongChatTweaks plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length < 2){
            Component message = plugin.mm.deserialize("<dark_gray>[<gold>SkysongChat<dark_gray>] <gray>You need to put in a Link and A name for the song!");
            commandSender.sendMessage(message);
        }
        String[] buffer = Arrays.copyOfRange(strings, 1, strings.length);
        StringBuilder title = new StringBuilder(strings[1]);
        for(int i = 2; i <= buffer.length; i++){
            title.append(" ").append(strings[i]);
        }
        String preppedString = "is playing <gold><hover:show_text:'<gray>Link: <gold>"+ strings[0] +"'><click:open_url:" + strings[0] + ">" + title + "</click></hover><reset>";
        SkysongChatTweaks.getOpenRP().getChat().sendMessage((Player)commandSender, preppedString, "playing");

        Bukkit.getLogger().info(commandSender.getName() + " " + title + "(" + strings[0] + ")");
        return true;
    }
}

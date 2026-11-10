package org.skysongdev.skysongchattweaks.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class PlayCommandTest {

    private final PlayCommand.ChannelSender chat = mock(PlayCommand.ChannelSender.class);
    private final PlayCommand play = new PlayCommand(chat, Logger.getAnonymousLogger());
    private final Command command = mock(Command.class);

    @Test
    void saysTheWholeTitleInThePlayingChannel() {
        Player player = mock(Player.class);

        assertTrue(play.onCommand(player, command, "play", new String[]{"https://x.y", "Song", "of", "Storms"}));

        verify(chat).send(player, PlayCommand.message("https://x.y", "Song of Storms"), "playing");
    }

    @Test
    void aOneWordTitleWorks() {
        Player player = mock(Player.class);

        play.onCommand(player, command, "play", new String[]{"https://x.y", "Song"});

        verify(chat).send(player, PlayCommand.message("https://x.y", "Song"), "playing");
    }

    @Test
    void needsALinkAndATitle() {
        Player player = mock(Player.class);

        play.onCommand(player, command, "play", new String[]{"https://x.y"});

        verify(player).sendMessage(any(Component.class));
        verify(chat, never()).send(any(), anyString(), anyString());
    }

    @Test
    void refusesTheConsoleInsteadOfCrashing() {
        CommandSender console = mock(CommandSender.class);

        play.onCommand(console, command, "play", new String[]{"https://x.y", "Song"});

        verify(console).sendMessage(any(Component.class));
        verify(chat, never()).send(any(), anyString(), anyString());
    }
}

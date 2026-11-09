package org.skysongdev.skysongchattweaks.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReloadCommandTest {

    @Test
    void reloadsTheConfigAndSaysSo() {
        AtomicInteger reloads = new AtomicInteger();
        CommandSender sender = mock(CommandSender.class);

        new ReloadCommand(reloads::incrementAndGet).onCommand(sender, mock(Command.class), "screload", new String[0]);

        assertEquals(1, reloads.get());
        verify(sender).sendMessage(any(Component.class));
    }
}

package org.skysongdev.skysongchattweaks.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/** Replies of this plugin's commands, with its chat prefix. */
final class PluginMessages {

    static final String PREFIX = "<dark_gray>[<gold>SkysongChat<dark_gray>] ";

    private PluginMessages() {
    }

    /** The MiniMessage text after the prefix. */
    static Component prefixed(String miniMessage) {
        return MiniMessage.miniMessage().deserialize(PREFIX + miniMessage);
    }
}

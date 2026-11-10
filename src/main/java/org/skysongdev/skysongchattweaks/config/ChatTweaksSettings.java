package org.skysongdev.skysongchattweaks.config;

import org.bukkit.configuration.Configuration;

import java.util.function.Supplier;

/**
 * The settings of config.yml. Read each time they are asked for, so {@code /screload} applies
 * them without anything else to refresh.
 */
public final class ChatTweaksSettings {

    /** OpenRP's default in-character channel. */
    static final String DEFAULT_ANNOUNCE_CHANNEL = "in-character";

    private final Supplier<? extends Configuration> config;

    /** @param config the plugin's current config, such as {@code plugin::getConfig} */
    public ChatTweaksSettings(Supplier<? extends Configuration> config) {
        this.config = config;
    }

    /**
     * The OpenRP channel injury award messages are said in. Servers whose config.yml predates
     * the setting get {@value #DEFAULT_ANNOUNCE_CHANNEL}.
     */
    public String announceChannel() {
        String channel = config.get().getString("announce-channel");
        return channel == null || channel.isBlank() ? DEFAULT_ANNOUNCE_CHANNEL : channel.trim();
    }
}

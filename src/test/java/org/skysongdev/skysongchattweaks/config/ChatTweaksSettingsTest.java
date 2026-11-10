package org.skysongdev.skysongchattweaks.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatTweaksSettingsTest {

    private final AtomicReference<YamlConfiguration> config = new AtomicReference<>(new YamlConfiguration());
    private final ChatTweaksSettings settings = new ChatTweaksSettings(config::get);

    @Test
    void readsTheAnnounceChannel() {
        config.get().set("announce-channel", " action ");
        assertEquals("action", settings.announceChannel());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void fallsBackToInCharacterWhenBlank(String blank) {
        config.get().set("announce-channel", blank);
        assertEquals("in-character", settings.announceChannel());
    }

    @Test
    void fallsBackToInCharacterWhenMissing() {
        assertEquals("in-character", settings.announceChannel());
    }

    @Test
    void followsAReloadedConfig() {
        config.get().set("announce-channel", "action");
        YamlConfiguration reloaded = new YamlConfiguration();
        reloaded.set("announce-channel", "out-of-character");
        config.set(reloaded);

        assertEquals("out-of-character", settings.announceChannel());
    }
}

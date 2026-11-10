package org.skysongdev.skysongchattweaks.injury;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.skysongdev.skysongchattweaks.chat.ChatAudience;
import org.skysongdev.skysongchattweaks.config.ChatTweaksSettings;
import org.skysongdev.skysongstats.api.CharacterId;
import org.skysongdev.skysongstats.api.Injury;
import org.skysongdev.skysongstats.api.event.ChangeCause;
import org.skysongdev.skysongstats.api.event.InjuryAwardEvent;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class InjuryAwardAnnouncerTest {

    private static final String MESSAGE = "<red>Rook <hover:show_text:'$1 \\o/'>[injury_x]</hover> broke a leg";

    private final UUID injuredId = UUID.randomUUID();
    private final Player injured = mock(Player.class);
    private final Player nearby = mock(Player.class);
    private final ChatAudience audience = mock(ChatAudience.class);
    private final InjuryTeacher teacher = mock(InjuryTeacher.class);
    private final YamlConfiguration config = new YamlConfiguration();
    private final Map<UUID, Player> online = new java.util.HashMap<>(Map.of(injuredId, injured));
    private final InjuryAwardAnnouncer announcer = new InjuryAwardAnnouncer(
            new ChatTweaksSettings(() -> config), audience, teacher, online::get);

    private InjuryAwardEvent award(String message) {
        return new InjuryAwardEvent(new CharacterId(injuredId, "Rook"), new Injury("broken-leg", Instant.now()),
                true, message, ChangeCause.COMMAND);
    }

    @Test
    void saysTheMessageAsItIsInTheChannelAndTeachesWhoHeardIt() {
        config.set("announce-channel", "local-out-of-character");
        when(audience.listeners(injured, "local-out-of-character")).thenReturn(List.of(injured, nearby));

        announcer.onAward(award(MESSAGE));

        Component said = MiniMessage.miniMessage().deserialize(MESSAGE);
        verify(injured).sendMessage(said);
        verify(nearby).sendMessage(said);
        verify(teacher).teach(List.of(injured, nearby), List.of("broken-leg"));
    }

    @Test
    void usesTheInCharacterChannelByDefault() {
        when(audience.listeners(injured, "in-character")).thenReturn(List.of(injured));

        announcer.onAward(award(MESSAGE));

        verify(injured).sendMessage(any(Component.class));
    }

    @Test
    void skipsAnInjuryWithoutMessage() {
        announcer.onAward(award(null));

        verifyNoInteractions(audience, teacher);
    }

    @Test
    void skipsAnOfflinePlayer() {
        online.clear();

        announcer.onAward(award(MESSAGE));

        verify(audience, never()).listeners(any(), anyString());
        verify(teacher, never()).teach(anyCollection(), anyCollection());
    }
}

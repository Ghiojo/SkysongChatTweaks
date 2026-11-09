package org.skysongdev.skysongchattweaks.injury;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.skysongdev.skysongchattweaks.chat.ChatAudience;
import org.skysongdev.skysongchattweaks.config.ChatTweaksSettings;
import org.skysongdev.skysongstats.api.event.InjuryAwardEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Says injury award messages in chat. SkysongStats stops sending them privately while this
 * plugin is registered with {@code injuries().announceAwards(plugin)}.
 *
 * <p>The message is said from the injured player's position to everyone who hears the
 * {@code announce-channel} there, who then learn the injury. It is already MiniMessage with
 * the character and the injury filled in, so it is sent as it is: no chat tag or channel
 * format touches it.
 */
public final class InjuryAwardAnnouncer implements Listener {

    private final ChatTweaksSettings settings;
    private final ChatAudience audience;
    private final InjuryTeacher teacher;
    private final Function<UUID, Player> onlinePlayer;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    /** @param onlinePlayer the online player with this UUID, or null, such as {@code Bukkit::getPlayer} */
    public InjuryAwardAnnouncer(ChatTweaksSettings settings, ChatAudience audience, InjuryTeacher teacher,
                                Function<UUID, Player> onlinePlayer) {
        this.settings = settings;
        this.audience = audience;
        this.teacher = teacher;
        this.onlinePlayer = onlinePlayer;
    }

    /** Fired on the main thread by SkysongStats. */
    @EventHandler
    public void onAward(InjuryAwardEvent event) {
        Optional<String> message = event.getMessage();
        Player injured = onlinePlayer.apply(event.getCharacter().player());
        if (message.isEmpty() || injured == null) {
            return; // An offline player has nobody near them to hear it.
        }

        List<Player> listeners = audience.listeners(injured, settings.announceChannel());
        Component said = miniMessage.deserialize(message.get());
        listeners.forEach(listener -> listener.sendMessage(said));
        teacher.teach(listeners, List.of(event.getInjury().typeId()));
    }
}

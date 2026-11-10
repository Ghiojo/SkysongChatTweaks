package org.skysongdev.skysongchattweaks.injury;

import org.bukkit.entity.Player;
import org.skysongdev.skysongchattweaks.chat.ChatAudience;
import org.skysongdev.skysongstats.api.Injuries;
import org.skysongdev.skysongstats.api.Profiles;

import java.util.Collection;

/**
 * Teaches injuries to everyone who heard them mentioned, so they show in their
 * {@code /injuries}. Players without a SkysongStats character are skipped.
 *
 * <p>Main thread only.
 */
public final class InjuryTeacher {

    private final ChatAudience audience;
    private final Profiles profiles;
    private final Injuries injuries;

    public InjuryTeacher(ChatAudience audience, Profiles profiles, Injuries injuries) {
        this.audience = audience;
        this.profiles = profiles;
        this.injuries = injuries;
    }

    /** Teaches the injuries to the players who hear the speaker in this channel. */
    public void teach(Player speaker, String channel, Collection<String> injuryIds) {
        if (injuryIds.isEmpty()) {
            return;
        }
        for (Player listener : audience.listeners(speaker, channel)) {
            profiles.active(listener.getUniqueId()).ifPresent(character -> {
                for (String injuryId : injuryIds) {
                    injuries.discover(character, injuryId);
                }
            });
        }
    }
}

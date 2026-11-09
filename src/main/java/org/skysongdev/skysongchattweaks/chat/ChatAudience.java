package org.skysongdev.skysongchattweaks.chat;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Who hears a player in an OpenRP channel, the same way OpenRP decides it: everyone online
 * when the channel has no {@code range}, otherwise the players in the speaker's world within
 * that many blocks. Chat tags and award messages both teach exactly these players.
 *
 * <p>Reads player locations, so call it on the main thread.
 */
public final class ChatAudience {

    private final Supplier<? extends Collection<? extends Player>> onlinePlayers;
    private final Function<String, OptionalInt> channelRange;

    /**
     * @param onlinePlayers the players online now
     * @param channelRange  a channel's {@code range} in OpenRP's chat config, empty when it has none
     */
    public ChatAudience(Supplier<? extends Collection<? extends Player>> onlinePlayers,
                        Function<String, OptionalInt> channelRange) {
        this.onlinePlayers = onlinePlayers;
        this.channelRange = channelRange;
    }

    /** The players who hear the speaker in this channel, the speaker included. */
    public List<Player> listeners(Player speaker, String channel) {
        OptionalInt range = channelRange.apply(channel);
        Location origin = speaker.getLocation();
        List<Player> listeners = new ArrayList<>();
        for (Player player : onlinePlayers.get()) {
            if (player.equals(speaker) || range.isEmpty() || isWithin(origin, player.getLocation(), range.getAsInt())) {
                listeners.add(player);
            }
        }
        if (!listeners.contains(speaker)) {
            listeners.add(speaker);
        }
        return listeners;
    }

    private static boolean isWithin(Location origin, Location other, int range) {
        return origin.getWorld() != null
                && origin.getWorld().equals(other.getWorld())
                && origin.distanceSquared(other) <= (double) range * range;
    }
}

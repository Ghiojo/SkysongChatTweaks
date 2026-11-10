package org.skysongdev.skysongchattweaks.chat;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatAudienceTest {

    private final World world = mock(World.class);
    private final World nether = mock(World.class);

    private Player at(World in, double x) {
        Player player = mock(Player.class);
        when(player.getLocation()).thenAnswer(call -> new Location(in, x, 64, 0));
        return player;
    }

    private ChatAudience audience(List<Player> online, OptionalInt range) {
        return new ChatAudience(() -> online, channel -> range);
    }

    @Test
    void hearsWithinTheRangeInTheSameWorld() {
        Player speaker = at(world, 0);
        Player near = at(world, 15);
        Player far = at(world, 15.5);
        Player elsewhere = at(nether, 1);

        List<Player> heard = audience(List.of(speaker, near, far, elsewhere), OptionalInt.of(15))
                .listeners(speaker, "in-character");

        assertEquals(List.of(speaker, near), heard);
    }

    @Test
    void aChannelWithoutRangeReachesEveryone() {
        Player speaker = at(world, 0);
        Player far = at(world, 10_000);
        Player elsewhere = at(nether, 1);

        List<Player> heard = audience(List.of(speaker, far, elsewhere), OptionalInt.empty())
                .listeners(speaker, "out-of-character");

        assertEquals(List.of(speaker, far, elsewhere), heard);
    }

    @Test
    void alwaysIncludesTheSpeaker() {
        Player speaker = at(world, 0);
        Player near = at(world, 1);

        List<Player> heard = audience(List.of(near), OptionalInt.of(0)).listeners(speaker, "whisper");

        assertEquals(List.of(speaker), heard);
    }
}

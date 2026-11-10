package org.skysongdev.skysongchattweaks.injury;

import org.skysongdev.skysongchattweaks.chat.TagReplacer;
import org.skysongdev.skysongchattweaks.concurrent.MainThread;
import org.skysongdev.skysongstats.api.CharacterId;
import org.skysongdev.skysongstats.api.Injuries;
import org.skysongdev.skysongstats.api.InjuryType;
import org.skysongdev.skysongstats.api.Profiles;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Turns {@code [injury_x]} into the injury's SkysongStats tag, when the speaker's character
 * knows the injury. Anything else stays as typed.
 *
 * <p>Safe from any thread: the speaker's knowledge is read through {@link MainThread}.
 */
public final class InjuryTagRenderer {

    /** SkysongStats injury ids: letters, digits, {@code _} and {@code -}, matched ignoring case. */
    static final Pattern TAG = Pattern.compile("\\[injury_([A-Za-z0-9_-]+)]");

    private final Injuries injuries;
    private final Profiles profiles;
    private final MainThread mainThread;

    public InjuryTagRenderer(Injuries injuries, Profiles profiles, MainThread mainThread) {
        this.injuries = injuries;
        this.profiles = profiles;
        this.mainThread = mainThread;
    }

    /**
     * @param message the message, with the injury ids that were shown
     * @param shown   the real ids (as in injuries.yml) of the injuries shown, in message order
     */
    public record Rendered(String message, Set<String> shown) {
    }

    /** Renders the injury tags the speaker knows. */
    public Rendered render(UUID speaker, String message) {
        Map<String, String> idsByTyped = existingInjuries(message);
        if (idsByTyped.isEmpty()) {
            return new Rendered(message, Set.of());
        }
        Set<String> candidates = new LinkedHashSet<>(idsByTyped.values());
        Set<String> known = mainThread.read(() -> knownBy(speaker, candidates), Set.of());
        if (known.isEmpty()) {
            return new Rendered(message, Set.of());
        }

        Set<String> shown = new LinkedHashSet<>();
        String rendered = TagReplacer.replace(message, TAG, match -> {
            String id = idsByTyped.get(match.group(1));
            if (id == null || !known.contains(id)) {
                return Optional.empty();
            }
            Optional<String> tag = injuries.tag(id);
            tag.ifPresent(found -> shown.add(id));
            return tag;
        });
        return new Rendered(rendered, Collections.unmodifiableSet(shown));
    }

    /** Each typed id that names an injury, with that injury's real id. */
    private Map<String, String> existingInjuries(String message) {
        Map<String, String> idsByTyped = new HashMap<>();
        Matcher matcher = TAG.matcher(message);
        while (matcher.find()) {
            String typed = matcher.group(1);
            if (!idsByTyped.containsKey(typed)) {
                injuries.type(typed).map(InjuryType::id).ifPresent(id -> idsByTyped.put(typed, id));
            }
        }
        return idsByTyped;
    }

    /** Main thread only. */
    private Set<String> knownBy(UUID speaker, Set<String> injuryIds) {
        Optional<CharacterId> character = profiles.active(speaker);
        if (character.isEmpty()) {
            return Set.of();
        }
        return injuryIds.stream()
                .filter(id -> injuries.knows(character.get(), id))
                .collect(Collectors.toUnmodifiableSet());
    }
}

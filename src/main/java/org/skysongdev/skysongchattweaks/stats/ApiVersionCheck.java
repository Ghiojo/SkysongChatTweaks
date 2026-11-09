package org.skysongdev.skysongchattweaks.stats;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks that the installed SkysongStats implements the API version this plugin was built
 * against. Without it, an old SkysongStats would only fail later with a
 * {@link NoSuchMethodError} in the middle of a chat message.
 *
 * <p>SkysongStats raises the minor version when it adds methods and the major version when it
 * removes or changes one, so any version with the same major and at least the required minor
 * and patch works.
 */
public final class ApiVersionCheck {

    /** The API version every SkysongStats call in this plugin exists in. */
    public static final String REQUIRED = "1.2.0";

    private static final Pattern VERSION = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

    private ApiVersionCheck() {
    }

    /**
     * Why the installed API can't be used.
     *
     * @param found what {@code SkysongStatsApi#apiVersion()} returned
     * @return the line to log before disabling, empty when the version is supported
     */
    public static Optional<String> problemWith(String found) {
        return problemWith(found, REQUIRED);
    }

    static Optional<String> problemWith(String found, String required) {
        int[] have = parse(found);
        int[] need = parse(required);
        if (need == null) {
            throw new IllegalArgumentException("Not a major.minor.patch version: " + required);
        }
        if (have == null) {
            return Optional.of("SkysongChatTweaks could not read the SkysongStats API version '" + found
                    + "'; it needs " + required + " or newer.");
        }
        if (have[0] != need[0]) {
            return Optional.of("SkysongChatTweaks needs SkysongStats API " + need[0] + ".x (" + required
                    + " or newer), found " + found + ".");
        }
        if (compare(have, need) < 0) {
            return Optional.of("SkysongChatTweaks needs SkysongStats API " + required + " or newer, found "
                    + found + ".");
        }
        return Optional.empty();
    }

    private static int[] parse(String version) {
        if (version == null) {
            return null;
        }
        Matcher matcher = VERSION.matcher(version.trim());
        if (!matcher.matches()) {
            return null;
        }
        try {
            return new int[]{
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))};
        } catch (NumberFormatException tooLarge) {
            return null;
        }
    }

    private static int compare(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            int byPart = Integer.compare(a[i], b[i]);
            if (byPart != 0) {
                return byPart;
            }
        }
        return 0;
    }
}

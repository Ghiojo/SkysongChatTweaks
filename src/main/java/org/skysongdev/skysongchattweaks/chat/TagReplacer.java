package org.skysongdev.skysongchattweaks.chat;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replaces every match of a chat tag pattern, such as {@code [injury_x]}, with what the
 * caller renders for it.
 */
public final class TagReplacer {

    private TagReplacer() {
    }

    /**
     * @param text        the chat message
     * @param pattern     the tag pattern
     * @param replacement the text for one match, or empty to leave that match as typed
     * @return the message with every rendered match replaced; replacements are inserted
     *         literally, so a {@code $} or {@code \} in them is kept as it is
     */
    public static String replace(String text, Pattern pattern, Function<MatchResult, Optional<String>> replacement) {
        Matcher matcher = pattern.matcher(text);
        StringBuilder out = new StringBuilder(text.length());
        while (matcher.find()) {
            String rendered = replacement.apply(matcher.toMatchResult()).orElse(matcher.group());
            matcher.appendReplacement(out, Matcher.quoteReplacement(rendered));
        }
        matcher.appendTail(out);
        return out.toString();
    }
}

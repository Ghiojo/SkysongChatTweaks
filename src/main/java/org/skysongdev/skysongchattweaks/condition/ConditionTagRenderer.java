package org.skysongdev.skysongchattweaks.condition;

import org.skysongdev.skysongchattweaks.chat.TagReplacer;
import org.skysongdev.skysongstats.api.Conditions;

import java.util.regex.Pattern;

/**
 * Turns {@code [condition_x]} and {@code [condition_x_N]} into the condition's SkysongStats
 * tag at level N (1 when left out). Unknown conditions stay as typed. Conditions are never
 * learnt, so anyone can show any of them.
 *
 * <p>Safe from any thread: {@link Conditions#tag(String, int)} is.
 */
public final class ConditionTagRenderer {

    /**
     * SkysongStats condition ids: letters, digits and {@code -}, never {@code _}, which comes
     * before the level. The level is 1 to 999999999, so it always fits an int.
     */
    static final Pattern TAG = Pattern.compile("\\[condition_([A-Za-z0-9-]+)(?:_([1-9][0-9]{0,8}))?]");

    private final Conditions conditions;

    public ConditionTagRenderer(Conditions conditions) {
        this.conditions = conditions;
    }

    /** The message with every known condition tag rendered. */
    public String render(String message) {
        return TagReplacer.replace(message, TAG, match -> {
            int level = match.group(2) == null ? 1 : Integer.parseInt(match.group(2));
            return conditions.tag(match.group(1), level);
        });
    }
}

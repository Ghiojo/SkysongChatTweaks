package org.skysongdev.skysongchattweaks.chat;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagReplacerTest {

    private static final Pattern TAG = Pattern.compile("\\[x_([a-z]+)]");

    @Test
    void replacesEveryRenderedMatch() {
        String out = TagReplacer.replace("a [x_one] b [x_two] c", TAG,
                match -> Optional.of("<" + match.group(1) + ">"));
        assertEquals("a <one> b <two> c", out);
    }

    @Test
    void leavesUnrenderedMatchesAsTyped() {
        String out = TagReplacer.replace("[x_keep] [x_swap]", TAG,
                match -> match.group(1).equals("swap") ? Optional.of("S") : Optional.empty());
        assertEquals("[x_keep] S", out);
    }

    @Test
    void insertsDollarsAndBackslashesLiterally() {
        String out = TagReplacer.replace("costs [x_price]", TAG, match -> Optional.of("$1 \\o/"));
        assertEquals("costs $1 \\o/", out);
    }

    @Test
    void keepsAMessageWithoutTags() {
        assertEquals("hello $1", TagReplacer.replace("hello $1", TAG, match -> Optional.of("never")));
    }
}

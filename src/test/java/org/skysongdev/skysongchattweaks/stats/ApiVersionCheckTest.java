package org.skysongdev.skysongchattweaks.stats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiVersionCheckTest {

    @ParameterizedTest
    @ValueSource(strings = {"1.2.0", "1.2.1", "1.3.0", "1.10.0", " 1.2.0 "})
    void acceptsTheRequiredVersionAndNewerMinors(String found) {
        assertTrue(ApiVersionCheck.problemWith(found).isEmpty());
    }

    @Test
    void rejectsAnOlderMinorWithOneClearLine() {
        assertEquals("SkysongChatTweaks needs SkysongStats API 1.2.0 or newer, found 1.1.0.",
                ApiVersionCheck.problemWith("1.1.0").orElseThrow());
    }

    @Test
    void rejectsAnOlderPatch() {
        assertTrue(ApiVersionCheck.problemWith("1.2.0", "1.2.1").isPresent());
    }

    @Test
    void comparesNumbersNotText() {
        // As text "1.10.0" < "1.9.0"; as versions it is newer.
        assertTrue(ApiVersionCheck.problemWith("1.10.0", "1.9.0").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2.0.0", "0.9.0"})
    void rejectsAnotherMajor(String found) {
        assertEquals("SkysongChatTweaks needs SkysongStats API 1.x (1.2.0 or newer), found " + found + ".",
                ApiVersionCheck.problemWith(found).orElseThrow());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"1.2", "1.2.0-SNAPSHOT", "v1.2.0", "99999999999.0.0"})
    void rejectsWhatItCannotRead(String found) {
        assertTrue(ApiVersionCheck.problemWith(found).orElseThrow().contains("could not read"));
    }

    @Test
    void refusesABadRequirement() {
        assertThrows(IllegalArgumentException.class, () -> ApiVersionCheck.problemWith("1.2.0", "1.2"));
    }
}

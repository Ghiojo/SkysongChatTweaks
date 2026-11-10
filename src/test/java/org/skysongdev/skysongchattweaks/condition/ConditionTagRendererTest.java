package org.skysongdev.skysongchattweaks.condition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skysongdev.skysongstats.api.Conditions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConditionTagRendererTest {

    private final Conditions conditions = mock(Conditions.class);
    private final ConditionTagRenderer renderer = new ConditionTagRenderer(conditions);

    @BeforeEach
    void conditionsYml() {
        when(conditions.tag(anyString(), anyInt())).thenReturn(Optional.empty());
        for (String typed : new String[]{"bleeding", "BLEEDING", "stage-2"}) {
            when(conditions.tag(org.mockito.ArgumentMatchers.eq(typed), anyInt()))
                    .thenAnswer(call -> Optional.of("<" + typed.toLowerCase() + " " + call.getArgument(1) + ">"));
        }
    }

    @Test
    void rendersTheLevel() {
        assertEquals("I am <bleeding 2>", renderer.render("I am [condition_bleeding_2]"));
    }

    @Test
    void leavingTheLevelOutMeansOne() {
        assertEquals("<bleeding 1>", renderer.render("[condition_bleeding]"));
    }

    @Test
    void acceptsDigitsDashesAndAnyCase() {
        assertEquals("<stage-2 3> <bleeding 1>", renderer.render("[condition_stage-2_3] [condition_BLEEDING]"));
    }

    @Test
    void acceptsTheLargestLevel() {
        assertEquals("<bleeding 999999999>", renderer.render("[condition_bleeding_999999999]"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "[condition_bleeding_]",
            "[condition_bleeding_0]",
            "[condition_bleeding_02]",
            "[condition_bleeding_1000000000]",
            "[condition_]",
            "[condition_unknown_2]",
            "[condition_has_underscore]"})
    void leavesAnythingElseAsTyped(String typed) {
        assertEquals(typed, renderer.render(typed));
    }

    @Test
    void keepsSpecialCharactersOfATag() {
        when(conditions.tag("bleeding", 1)).thenReturn(Optional.of("costs $5 \\o/"));
        assertEquals("costs $5 \\o/", renderer.render("[condition_bleeding]"));
    }
}

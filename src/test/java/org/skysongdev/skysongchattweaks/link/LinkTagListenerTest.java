package org.skysongdev.skysongchattweaks.link;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkTagListenerTest {

    @Test
    void turnsALinkIntoAClickableTag() {
        assertEquals("see <hover:show_text:'<gray>Link: <gold>https://www.example.com/a?b=c'>"
                        + "<click:open_url:https://www.example.com/a?b=c><blue>[Link]</blue></click></hover> now",
                LinkTagListener.render("see https://www.example.com/a?b=c now"));
    }

    @Test
    void acceptsALinkWithoutScheme() {
        assertEquals("<hover:show_text:'<gray>Link: <gold>www.example.com'><click:open_url:www.example.com>"
                + "<blue>[Link]</blue></click></hover>", LinkTagListener.render("www.example.com"));
    }

    @Test
    void leavesOtherTextAlone() {
        assertEquals("example.com costs $5", LinkTagListener.render("example.com costs $5"));
    }
}

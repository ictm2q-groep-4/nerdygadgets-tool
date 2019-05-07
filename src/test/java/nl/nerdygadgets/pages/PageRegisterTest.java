package nl.nerdygadgets.pages;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageRegisterTest {

    @Test
    void testGet() {
        assertAll(
                () -> assertEquals(null, PageRegister.get("UnknownIdentifierForTest"), "'UnknownIdentifierForTest' should make 'get' return null."),
                () -> assertEquals(PageRegister.MAIN, PageRegister.get("MainScene"), "The identifier 'MainScene' should return 'PageRegister.MAIN'")
        );
    }
}
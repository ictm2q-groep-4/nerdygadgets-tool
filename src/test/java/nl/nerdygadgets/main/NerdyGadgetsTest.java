package nl.nerdygadgets.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Lucas Ouwens
 */
class NerdyGadgetsTest {

    /**
     * The object which we will use to test this class.
     */
    private NerdyGadgets nerdyGadgets;

    /**
     * Initialize the nerdyGadgets object before running the test.
     */
    @BeforeEach
    public void createNerdyGadgetsObject() {
        nerdyGadgets = new NerdyGadgets();
    }


    @Test
    void testSetScene() {
        assertThrows(NullPointerException.class, () -> nerdyGadgets.setScene("file_that_does_not_exist.fxml"), "File should throw IOException.");
    }
}
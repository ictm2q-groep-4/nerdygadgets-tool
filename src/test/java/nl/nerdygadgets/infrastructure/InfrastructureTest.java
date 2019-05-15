package nl.nerdygadgets.infrastructure;

import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.HAL9003DB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Lucas Ouwens
 */
class InfrastructureTest {

    private Infrastructure infrastructure;

    @BeforeEach
    public void setUp() {
        infrastructure = new Infrastructure();
    }

    @Test
    public void testAddComponent() {
        assertAll(
                () -> assertTrue(infrastructure.addComponent(new HAL9003DB("HOST", 0, 0)), "Adding a valid component should return TRUE")
        );
    }

    @Test
    public void testRemoveComponent() {
        assertAll(
                () -> assertTrue(true)
        );
    }

    @Test
    public void testSave() {
        assertAll(
                () -> assertTrue(true)
        );
    }
}
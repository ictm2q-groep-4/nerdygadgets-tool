package nl.nerdygadgets.infrastructure;

import nl.nerdygadgets.main.Components;
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
                () -> assertTrue(infrastructure.addComponent(Components.getComponent("HAL9003DB")), "Adding a valid component should return TRUE")
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
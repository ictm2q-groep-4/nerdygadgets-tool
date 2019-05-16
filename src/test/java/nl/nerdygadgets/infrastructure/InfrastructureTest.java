package nl.nerdygadgets.infrastructure;

import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.ComponentType;
import nl.nerdygadgets.main.Components;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;

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
        final Component input = new Component("HAL9003DB", 99.99, 15, ComponentType.DATABASESERVER);

        assertAll(
                () -> assertTrue(infrastructure.addComponent(input), "Adding a valid component should return TRUE"),
                () -> assertFalse(infrastructure.addComponent(input), "Adding a component already in the list should return FALSE")
        );
    }

    @Test
    public void testRemoveComponent() {

        // prepare arraylist for tests
        infrastructure.getComponents().add(new Component("HAL9003DB", 99.99, 15, ComponentType.DATABASESERVER));

        final Component input = infrastructure.getComponents().get(0);

        // Should return false when a component isn't in the list
        assertAll(
                () -> assertTrue(infrastructure.removeComponent(input)),
                () -> assertFalse(infrastructure.removeComponent(input))
        );
    }

    @Test
    public void testSave() {
        assertAll(
                () -> assertDoesNotThrow(()->infrastructure.save("test.xml"), "File should be created without any error.")
        );
    }
}
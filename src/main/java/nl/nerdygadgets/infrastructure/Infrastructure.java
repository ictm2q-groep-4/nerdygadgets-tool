package nl.nerdygadgets.infrastructure;

import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.design.DesignManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The infrastructure class. It keeps track of the whole infrastructure design.
 *
 * @author Joris Vos
 */
public class Infrastructure {

    /**
     * This list contains all the components that are currently in the infrastructure design.
     */
    private List<Component> components;

    /**
     * This equals to the total availability of the infrastructure design.
     */
    private double availability = 0;

    /**
     * The currently loaded infrastructure
     */
    private static Infrastructure currentInfrastructure;

    /**
     * Boolean which specifies if the design has been loaded already.
     */
    private boolean loaded;

    /**
     * The constructor for Infrastructure.
     */
    public Infrastructure() {
        components = new ArrayList<>();
    }

    /**
     * Returns false if the component already exists in the list. Adds the component and returns true if the component does not exists in the list.
     *
     * @param component Component
     * @return boolean
     */
    public boolean addComponent(Component component) {
        if (components.contains(component)) {
            return false;
        }

        components.add(component);
        return true;
    }

    /**
     * Add each component to the infrastructure.
     *
     * @param components List<Component>
     */
    public void addComponents(List<Component> components) {
        components.forEach(this::addComponent);
    }

    /**
     * Returns false if the component does not exists in the list. Removes the component and returns true if the component does exist in the list.
     *
     * @param component Component
     * @return boolean
     */
    public boolean removeComponent(Component component) {
        if (!components.contains(component)) {
            return false;
        }

        components.remove(component);
        return true;
    }

    /**
     * Call the method to start the operation of saving the infrastructure to an XML file
     *
     * @return boolean
     */
    public boolean save(String filePath) {
        return DesignManager.getDesignManager().save(filePath, components);
    }

    // region Getters
    public List<Component> getComponents() {
        return components;
    }

    public double getAvailability() {
        return availability;
    }

    /**
     * Returns the infrastructure if it has already been loaded before, will otherwise return null.
     *
     * @return Infrastructure|null
     */
    public static Infrastructure getCurrentInfrastructure() {
        return currentInfrastructure;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public static void setCurrentInfrastructure(Infrastructure currentInfrastructure) {
        Infrastructure.currentInfrastructure = currentInfrastructure;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    // endregion
}

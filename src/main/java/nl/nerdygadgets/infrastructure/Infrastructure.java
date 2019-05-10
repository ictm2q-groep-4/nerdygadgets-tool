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
     * The constructor for Infrastructure.
     */
    public Infrastructure() {
        components = new ArrayList<>();
        calculateAvailability();
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
        calculateAvailability();
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
        calculateAvailability();
        return true;
    }

    /**
     * @return
     */
    public boolean save() {
        try {
            //DesignManager.getDesignManager().save(filePath);
            return true;
        } catch (Exception e) {
            return false;
        }
        //TODO edit this method according to DesignManager (if it throws an error or not. Don't know at the time of writing this)
        // option 1: remove try catch and make boolean a void -> option 2: leave it as it is
    }

    //TODO add javadoc comment
    private void calculateAvailability() {
        //TODO get web servers and database separate by using the ENUM
    }

    // region Getters
    public List<Component> getComponents() {
        return components;
    }

    public double getAvailability() {
        return availability;
    }

    // endregion
}

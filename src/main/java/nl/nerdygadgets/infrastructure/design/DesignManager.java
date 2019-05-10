package nl.nerdygadgets.infrastructure.design;

import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.Component;

import java.io.File;
import java.util.ArrayList;

/**
 * A Singleton manager for the design import/export (for infrastructure)
 *
 * @author Lucas Ouwens
 * @author <your name>
 */
public class DesignManager {

    /**
     * A variable which holds our *only* instance of this class.
     */
    private static DesignManager designManagerInstance;

    /**
     * A private database constructor to block anything outside from making a new instance of this class.
     */
    private DesignManager() {
        // ...
    }

    /**
     * Get access to the Singleton DesignManager class.
     *
     * @return Database
     */
    public static DesignManager getDesignManager() {
        if (designManagerInstance == null) {
            designManagerInstance = new DesignManager();
        }

        return designManagerInstance;
    }

    /**
     * Load the infrastructure from an XML file.
     *
     * @param file      File an XML file to be parsed
     * @return          Infrastructure with the components in it
     */
    public Infrastructure load(File file) {
        Infrastructure infrastructure = new Infrastructure();

        ArrayList<Component> components = XMLImporter.getXMLImporter(file.getPath()).getComponents();

        infrastructure.addComponents(components);

        return infrastructure;
    }

}

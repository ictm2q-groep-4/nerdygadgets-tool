package nl.nerdygadgets.infrastructure.design;

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
     *  Get access to the Singleton DesignManager class.
     * @return Database
     */
    public static DesignManager getDesignManager() {
        if(designManagerInstance == null) {
            designManagerInstance = new DesignManager();
        }

        return designManagerInstance;
    }
}

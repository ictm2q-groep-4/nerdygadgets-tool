package nl.nerdygadgets.pages;

import java.util.Arrays;

/**
 * An enumeration for the registration of scenes.
 *
 * @author Lucas Ouwens
 * @author Joris Vos
 */
public enum PageRegister {

    MAIN("MainScene", "Main.fxml"),
    DESIGNER("InfrastructureDesigner", "InfrastructureDesigner.fxml"),
    MONITOR("InfrastructureMonitor", "InfrastructureMonitor.fxml"),
    OPTIMIZER("InfrastructureOptimizer", "InfrastructureOptimizer.fxml"),
    OPTIMIZERALERT("OptimizerAlert", "components/OptimizerAlert.fxml");


    /**
     * A way to easily identify each PageRegister enum
     */
    private String identifier;

    /**
     * The path + filename of the view(scene)
     */
    private String filePath;

    /**
     * The constructor which defines the values each enum must have.
     *
     * @param identifier    String an 'identifier' to easily load the scene
     * @param filePath      String the 'path' including the filename itself. Starts from the 'pages' folder. (Location of the 'view')
     */
    PageRegister(String identifier, String filePath) {
        this.identifier = identifier;
        this.filePath = filePath;
    }


    /**
     * Easily get a PageRegister enum by its identifier, will return null if the identifier does not exist.
     * This is done using Arrays.stream
     *
     * @param identifier    String the identifier
     * @return              PageRegister|null
     */
    public static PageRegister get(String identifier) {
        return Arrays.stream(PageRegister.values())
                .filter(pageRegister -> pageRegister.identifier.equalsIgnoreCase(identifier))
                .findFirst()
                .orElse(null);
    }

    /**
     * get the identifier of a specific enum (page)
     *
     * @return String
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Get the file path (path + filename) of a specific enum's view(scene).
     *
     * @return String
     */
    public String getFilePath() {
        return "/pages/" + filePath;
    }
}



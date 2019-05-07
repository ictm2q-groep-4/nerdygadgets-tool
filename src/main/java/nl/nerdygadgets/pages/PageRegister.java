package nl.nerdygadgets.pages;

import javafx.fxml.FXMLLoader;

import java.util.Arrays;

/**
 *
 */
public enum PageRegister {

    // pages/<page>
    // pages/templates

    MAIN("MainScene", "main_scene.fxml");

    private String identifier;
    private String filePath;

    /**
     *
     * @param identifier
     * @param filePath
     */
    PageRegister(String identifier, String filePath) {
        this.identifier = identifier;
        this.filePath = filePath;
    }


    /**
     *
     * @param identifier
     * @return
     */
    public static PageRegister get(String identifier) {
        return Arrays.stream(PageRegister.values())
                .filter(pageRegister -> pageRegister.identifier.equalsIgnoreCase(identifier))
                .findFirst()
                .orElse(null);
    }

    /**
     *
     * @return
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     *
     * @return
     */
    public String getFilePath() {
        return "/pages/" + filePath;
    }


}



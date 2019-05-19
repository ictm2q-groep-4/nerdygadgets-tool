package nl.nerdygadgets.pages.popups;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * A generic popup class.
 *
 * @author Lucas Ouwens
 */
public class GenericPopup {

    private Stage stage;
    private Pane container;

    private boolean ok;

    /**
     * Constructor for creating a generic popup, this will create a stage with the given scene, but will not display it.
     * See getStage().showAndWait() or getStage().show() for ways to display the popup.
     *
     * @param FXMLPath String   The path within the resources folder to the FXML file you are loading
     * @param title String      The title of the popup
     */
    public GenericPopup(String FXMLPath, String title) {
        try {
            this.stage = new Stage();
            this.container = FXMLLoader.load(getClass().getResource(FXMLPath));

            // Set the title for the popup
            stage.setTitle(title);

            // set the modality for the popup
            stage.initModality(Modality.APPLICATION_MODAL);

            // Set the scene using the container.
            stage.setScene(new Scene(container));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public boolean isOk() {
        return ok;
    }

    public Stage getStage() {
        return stage;
    }

    public Pane getContainer() {
        return container;
    }
}

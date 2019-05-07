package nl.nerdygadgets.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.nerdygadgets.pages.PageRegister;

import java.io.IOException;

/**
 * The nerdyGadgets method for the application, it just starts everything.
 * Includes some helpful methods and objects.
 *
 * @author Lucas Ouwens
 */
public class NerdyGadgets extends Application {

    /**
     * A variable to get access to the 'stage' object, usage is mainly for custom actions
     */
    private Stage stage;

    /**
     * A variable to get access to this class.
     */
    private static NerdyGadgets nerdyGadgets;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Store objects for later usage
        NerdyGadgets.nerdyGadgets = this;
        this.stage = stage;

        // Set the default scene for the application.
        this.setScene(PageRegister.MAIN.getIdentifier());

        // Set the title, width and height for the stage (NOTE: The stage is the whole application, including the exit/minimize/maximize buttons)
        stage.setTitle("NerdyGadgets | multipurpose network tool");
        stage.setWidth(1280);
        stage.setHeight(720);

        // center on screen and make it non-resizable (To not need responsive design)
        stage.setResizable(false);
        stage.centerOnScreen();

        // Show the application
        stage.show();
    }

    /**
     * get access to the Stage
     * @return Stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Load a view by its identifier.
     *
     * Example, to load the nerdyGadgets view(see PageRegister.MAIN):
     *
     * @param identifier String
     * @throws IOException
     */
    public void setScene(String identifier) throws IOException {
        // load the view from the resources folder
        Parent view = FXMLLoader.load(getClass().getResource(PageRegister.get(identifier).getFilePath()));
        // create a new scene using the view & set the scene to the new view.
        Scene scene = new Scene(view);

        getStage().setScene(scene);
    }

    /**
     * Get access to the main class, mainly for the usage of 'setScene'
     * @return NerdyGadgets
     */
    public static NerdyGadgets getNerdyGadgets() {
        return nerdyGadgets;
    }
}

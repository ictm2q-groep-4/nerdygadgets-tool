package nl.nerdygadgets.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.ComponentManager;
import nl.nerdygadgets.pages.PageRegister;

import java.io.IOException;

/**
 * The nerdyGadgets method for the application, it just starts everything.
 * Includes some helpful methods and objects.
 *
 * @author Lucas Ouwens
 * @author Joris Vos
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

    /**
     * This is needed by IntelliJ to start the application
     *
     * @param args  String[]
     */
    public static void main(String[] args) {

        launch(args);
    }

    /**
     * This is the main entry point for our GUI application
     *
     * @param stage         Stage
     */
    @Override
    public void start(Stage stage) {
        // Store objects for later usage
        NerdyGadgets.nerdyGadgets = this;
        this.stage = stage;

        ComponentManager.instance();

        // Set the default scene for the application.
        this.setScene(PageRegister.MAIN.getIdentifier());

        // Set the title, width and height for the stage (NOTE: The stage is the whole application, including the exit/minimize/maximize buttons)
        stage.setTitle("NerdyGadgets | multipurpose network tool");

        // center on screen and make it non-resizable (To not need responsive design)
        stage.setResizable(false);
        stage.centerOnScreen();

        stage.getIcons().add(new Image(NerdyGadgets.class.getResourceAsStream("/images/logo.png")));

        // Show the application
        stage.show();
    }

    /**
     * A static method to show an 'alert', very useful in many cases.
     *
     * @param title      String the title which the alert will show
     * @param headerText String the header text which the alert will show
     * @param alertType  Alert.AlertType the type of alert it is.
     */
    public static void showAlert(String title, String headerText, Alert.AlertType alertType) {
        Alert XMLAlert = new Alert(alertType);
        XMLAlert.setTitle(title);
        XMLAlert.setHeaderText(headerText);
        XMLAlert.showAndWait();
    }

    // region Getters

    /**
     * Get access to the main class, mainly for the usage of 'setScene'
     *
     * @return NerdyGadgets
     */
    public static NerdyGadgets getNerdyGadgets() {
        return nerdyGadgets;
    }

    /**
     * get access to the Stage
     *
     * @return Stage
     */
    public Stage getStage() {
        return stage;
    }

    // endregion

    // region Setters

    /**
     * Load a view by its identifier.
     * <p>
     * Example, to load the nerdyGadgets view(see PageRegister.MAIN):
     *
     * @param identifier    String
     */
    public void setScene(String identifier) {
        try {
            // load the view from the resources folder
            Parent view = FXMLLoader.load(getClass().getResource(PageRegister.get(identifier).getFilePath()));
            // create a new scene using the view & set the scene to the new view.
            Scene scene = new Scene(view);

            getStage().setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // endregion
}

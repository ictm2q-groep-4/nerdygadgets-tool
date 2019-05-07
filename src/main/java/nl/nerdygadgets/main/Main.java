package nl.nerdygadgets.main;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main method for the application, it just starts everything.
 *
 * NOTE: Currently contains an 'example' of an application, implementation is a TODO.
 *
 * @author Lucas Ouwens
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("NerdyGadgets | multipurpose network tool");
        stage.setWidth(1280);
        stage.setHeight(720);

        stage.setResizable(false);
        stage.centerOnScreen();

        stage.show();
    }
}

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
        stage.setTitle("Monitoring tool");
        stage.setMinHeight(500);
        stage.setMinWidth(500);
        stage.show();
    }
}

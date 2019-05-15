package nl.nerdygadgets.pages.controllers;

import javafx.fxml.FXML;

import nl.nerdygadgets.main.NerdyGadgets;
import nl.nerdygadgets.pages.Controller;
import nl.nerdygadgets.pages.PageRegister;

import java.io.IOException;

/**
 * The controller for the 'Main.fxml' file.
 *
 * @author Lucas Ouwens
 * @author Joris Vos
 */
public class MainController implements Controller {

    /**
     * When clicking the 'components' button, this should move you to the components view.
     */
    @FXML
    private void handleMonitorButton() {
        NerdyGadgets.getNerdyGadgets().setScene(PageRegister.MONITOR.getIdentifier());
    }

    /**
     * When clicking the 'optimizer' button, this should move you to the optimizer view.
     */
    @FXML
    private void handleOptimizerButton() {
        NerdyGadgets.getNerdyGadgets().setScene(PageRegister.OPTIMIZER.getIdentifier());
    }

    /**
     * When clicking the 'builder' button, this should move you to the designer view.
     */
    @FXML
    private void handleBuilderButton() {
        NerdyGadgets.getNerdyGadgets().setScene(PageRegister.DESIGNER.getIdentifier());
    }
}

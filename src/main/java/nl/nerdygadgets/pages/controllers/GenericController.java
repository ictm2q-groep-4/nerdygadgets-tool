package nl.nerdygadgets.pages.controllers;

import javafx.fxml.FXML;
import nl.nerdygadgets.main.NerdyGadgets;
import nl.nerdygadgets.pages.Controller;
import nl.nerdygadgets.pages.PageRegister;

import java.io.IOException;

/**
 *
 * All things which need to be handled the same way on every page should be registered here.
 *
 * @author Lucas Ouwens
 */
public class GenericController implements Controller {

    /**
     * The controller for the 'back to main menu' controller in (almost) every view.
     */
    @FXML
    private void handleBackButton() {
        try {
            NerdyGadgets.getNerdyGadgets().setScene(PageRegister.MAIN.getIdentifier());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

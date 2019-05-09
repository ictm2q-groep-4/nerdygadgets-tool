package nl.nerdygadgets.pages.controllers;

import javafx.fxml.FXML;
import nl.nerdygadgets.main.NerdyGadgets;
import nl.nerdygadgets.pages.PageRegister;

import java.io.IOException;

public class BackButtonController {

    @FXML
    private void handleBackButton() {
        try {
            NerdyGadgets.getNerdyGadgets().setScene(PageRegister.MAIN.getIdentifier());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

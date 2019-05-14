package nl.nerdygadgets.pages.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.main.NerdyGadgets;
import nl.nerdygadgets.pages.Controller;

/**
 * @author Lucas Ouwens
 */
public class MonitorController extends GenericController implements Controller {

    @FXML
    private void handleOpenCurrentDesign() {
        if (Infrastructure.getCurrentInfrastructure() != null && Infrastructure.getCurrentInfrastructure().getComponents() != null) {
            if (Infrastructure.getCurrentInfrastructure().isLoaded()) {
                NerdyGadgets.showAlert("Er is een fout opgetreden!", "Deze infrastructuur is al ingeladen.", Alert.AlertType.WARNING);
            } else {
                this.loadDesignIntoMonitor();
            }
        } else {
            NerdyGadgets.showAlert("Er is een fout opgetreden!", "Er is geen beschikbare infrastructuur. Gebruik 'open ontwerp'.", Alert.AlertType.WARNING);
        }
    }

}


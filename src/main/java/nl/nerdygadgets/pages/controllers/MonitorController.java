package nl.nerdygadgets.pages.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import nl.nerdygadgets.infrastructure.design.DesignManager;
import nl.nerdygadgets.main.NerdyGadgets;
import nl.nerdygadgets.pages.Controller;

import java.io.File;

/**
 * @author Lucas Ouwens
 */
public class MonitorController extends GenericController implements Controller {

    @FXML
    private Rectangle monitorPanel;

    @FXML
    private Button openDesignButton;

    @FXML
    private Button openCurrentDesign;

    @FXML
    private Label totalAvailability;

    @FXML
    private Label totalCosts;

    @FXML
    private void handleOpenDesign() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an infrastructure design");
        File infrastructureFile;
        if((infrastructureFile = fileChooser.showOpenDialog(NerdyGadgets.getNerdyGadgets().getStage())) != null) {
            DesignManager.getDesignManager().load(infrastructureFile);
        }
    }


}

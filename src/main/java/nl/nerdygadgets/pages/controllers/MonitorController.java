package nl.nerdygadgets.pages.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import nl.nerdygadgets.infrastructure.design.DesignManager;
import nl.nerdygadgets.main.NerdyGadgets;
import nl.nerdygadgets.pages.Controller;

import java.io.File;
import java.nio.file.Files;

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
            // check if file extension is .xml
            if ((infrastructureFile.getName().toLowerCase().endsWith(".xml"))) {
                try {
                    // verify mime type
                    if (Files.probeContentType(infrastructureFile.toPath()).equals("application/xml")) {
                        DesignManager.getDesignManager().load(infrastructureFile);
                    } else {
                        System.out.println("Not an XML file");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Alert XMLalert = new Alert(Alert.AlertType.ERROR);
                XMLalert.setTitle("Er is een fout opgetreden");
                XMLalert.setHeaderText("Bestand is geen infrastructuur design");
                XMLalert.showAndWait();
            }
        }
    }


}

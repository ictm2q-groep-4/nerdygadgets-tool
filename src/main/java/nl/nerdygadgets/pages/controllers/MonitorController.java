package nl.nerdygadgets.pages.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.design.DesignManager;
import nl.nerdygadgets.main.NerdyGadgets;
import nl.nerdygadgets.pages.Controller;

import java.io.File;
import java.io.IOException;
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
    private AnchorPane monitorAnchorPane;

    @FXML
    private void handleOpenDesign() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an infrastructure design");

        File infrastructureFile;

        if ((infrastructureFile = fileChooser.showOpenDialog(NerdyGadgets.getNerdyGadgets().getStage())) != null) {
            // check if file extension is .xml
            if ((infrastructureFile.getName().toLowerCase().endsWith(".xml"))) {
                try {
                    // verify mime type
                    if (Files.probeContentType(infrastructureFile.toPath()).equals("application/xml")) {
                        Infrastructure.setCurrentInfrastructure(DesignManager.getDesignManager().load(infrastructureFile));
                        this.loadDesignIntoMonitor();
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

    @FXML
    private void handleOpenCurrentDesign() {
        if(Infrastructure.getCurrentInfrastructure() != null && Infrastructure.getCurrentInfrastructure().getComponents() != null) {
            this.loadDesignIntoMonitor();
        } else {
            Alert XMLalert = new Alert(Alert.AlertType.ERROR);
            XMLalert.setTitle("Er is een fout opgetreden");
            XMLalert.setHeaderText("Er is geen beschikbare infrastructuur. Gebruik 'open ontwerp'.");
            XMLalert.showAndWait();
        }
    }

    /**
     * Load the components into the monitor field
     */
    private void loadDesignIntoMonitor() {
        double startX = monitorPanel.getLayoutX();
        double startY = monitorPanel.getLayoutY();

        Infrastructure current = Infrastructure.getCurrentInfrastructure();

        if (current != null && current.getComponents() != null) {
            current.getComponents().forEach(component -> {
                try {
                    Pane componentPane = FXMLLoader.load(getClass().getResource("/pages/monitor/PaneComponent.fxml"));
                    Label hostName = (Label) componentPane.getChildren().get(1);
                    Rectangle box = (Rectangle) componentPane.getChildren().get(0);

                    // set the layout axises of the box
                    box.setLayoutX(0);
                    box.setLayoutY(0);

                    // Set the text and layout of the hostname
                    hostName.setText(component.getHostname());
                    hostName.setLayoutX(0);
                    hostName.setLayoutY(box.getHeight() + 5);

                    // set the layout axises of the component pane
                    componentPane.setLayoutX(startX + component.getX());
                    componentPane.setLayoutY(startY + component.getY());

                    componentPane.getChildren().set(1, hostName);
                    if (componentPane.getLayoutX() <= 1280) {
                        if (componentPane.getLayoutY() <= (monitorPanel.getLayoutY() + monitorPanel.getHeight())) {
                            monitorAnchorPane.getChildren().add(componentPane);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("Can't find any components to load.");
        }
    }
}


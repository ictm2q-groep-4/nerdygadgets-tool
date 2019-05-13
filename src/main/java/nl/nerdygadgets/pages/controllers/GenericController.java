package nl.nerdygadgets.pages.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.*;
import nl.nerdygadgets.infrastructure.design.DesignManager;
import nl.nerdygadgets.main.NerdyGadgets;
import nl.nerdygadgets.pages.PageRegister;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

/**
 * All things which need to be handled the same way on every page should be registered here.
 *
 * @author Lucas Ouwens
 */
public class GenericController implements Initializable {

    @FXML
    protected Rectangle componentPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label totalAvailability;

    @FXML
    private Label totalCosts;

    @FXML
    private AnchorPane componentContainer;

    /**
     * The controller for the 'back to main menu' controller in (almost) every view.
     */
    @FXML
    private void handleBackButton() {
        try {
            NerdyGadgets.getNerdyGadgets().setScene(PageRegister.MAIN.getIdentifier());

            // since we're leaving a view which could contain already loaded components, we set 'loaded' to false again
            // so the design can be loaded again at a later time. We don't want duplicated elements!
            if (Infrastructure.getCurrentInfrastructure() != null) {
                Infrastructure.getCurrentInfrastructure().setLoaded(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                        // Load the infrastructure which was picked using the filechooser
                        Infrastructure.setCurrentInfrastructure(DesignManager.getDesignManager().load(infrastructureFile));

                        // draw the components, set the availability and costs
                        this.loadDesignIntoMonitor();
                    } else {
                        NerdyGadgets.showAlert("Er is een fout opgetreden!", "Het aangereikte bestand is van een onjuist formaat.", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                NerdyGadgets.showAlert("Er is een fout opgetreden!", "Bestand is geen infrastructuur design", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Load the components into the components field
     */
    protected void loadDesignIntoMonitor() {
        double startX = componentPane.getLayoutX();
        double startY = componentPane.getLayoutY();

        Infrastructure current = Infrastructure.getCurrentInfrastructure();

        if (current != null && current.getComponents() != null) {
            current.getComponents().forEach(component -> {
                try {
                    Pane componentPane = FXMLLoader.load(getClass().getResource("/pages/components/PaneComponent.fxml"));
                    componentPane.setUserData(component);
                    Label hostName = (Label) componentPane.getChildren().get(1);
                    Rectangle box = (Rectangle) componentPane.getChildren().get(0);

                    Tooltip statisticTooltip = new Tooltip();
                    statisticTooltip.setPrefSize(220, 180);

                    if(component.componentType == ComponentType.DATABASESERVER || component.componentType == ComponentType.WEBSERVER) {

//                        if(component.isOnline()) {
//                            box.setFill(Color.GREEN);
//                        } else {
//                            box.setFill(Color.DARKRED);
//                        }

                        statisticTooltip.setText(
//                                "Currently: " + (component.isOnline() ? "online" : "offline") + "\n" +
//                                "Disk usage: " + (component.isOnline()) ? (component.getDiskUsage()) : "Unavailable" + "\n" +
//                                "Processor usage: " + (component.isOnline()) ? (component.getProcessorUsage()) : "Unavailable"
                                "" // remove this and uncomment the above once implemented.
                        );
                    }

                    Tooltip.install(componentPane, statisticTooltip);
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
                        if (componentPane.getLayoutY() <= (componentPane.getLayoutY() + componentPane.getHeight())) {
                            anchorPane.getChildren().add(componentPane);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Set 'loaded' to true to tell the application that it should no longer try to load in the design.
            Infrastructure.getCurrentInfrastructure().setLoaded(true);

            // Set the availability and total costs of the components
            this.setTotalAvailability(Infrastructure.getCurrentInfrastructure().getComponents());
            this.setTotalCosts(Infrastructure.getCurrentInfrastructure().getComponents());
        } else {
            NerdyGadgets.showAlert("Er is een fout opgetreden!", "We kunnen geen componenten binnen het aangereikte bestand vinden om toe te voegen.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Calculate the total costs for the infrastructure.
     *
     * @param components List<Component>
     */
    private void setTotalCosts(List<Component> components) {
        double price = 0.00;
        for (Component c : components) {
            price += c.price;
        }

        totalCosts.setText("Totale kosten: € " + price);
    }

    /**
     * TODO @Stefan use the proper calculation: 1-(1-availability A) x (1-availability B)…x (1-availability n)
     * TODO important: Availability in the calculation is the components availability divided by 100.
     * Calculation for availability.
     *
     * @param components List<Component>
     */
    private void setTotalAvailability(List<Component> components) {
        double availability = 0.00;
        for (Component c : components) {
            availability += c.availability;
        }
        totalAvailability.setText("Totale beschikbaarheid: " + (availability / components.size()) + "%");
    }

    /**
     * Load the elements which will be used to fill the design/optimizer.
     */
    private void loadSelectableElements() {
        // Create an array of all the currently existing components.
        // We assume these will be the only ones in existence.
        Component[] components = {
                new DBloadbalancer("DBLoadbalancer", 0,0),
                new HAL9001DB("HAL9001DB", 0, 0),
                new HAL9002DB("HAL9002DB", 0, 0),
                new HAL9003DB("HAL9003DB", 0, 0),
                new HAL9001W("HAL9001W", 0, 0),
                new HAL9002W("HAL9002W", 0, 0),
                new HAL9003W("HAL9003W", 0, 0),
                new pfSense("pfSense", 0, 0)
        };

        try {
            for (int i = 0; i < components.length; i++) {
                Pane componentPane = FXMLLoader.load(getClass().getResource("/pages/components/DraggableDesignerElement.fxml"));
                Label title = (Label) componentPane.getChildren().get(1);
                Label availability = (Label) componentPane.getChildren().get(2);
                Label cost = (Label) componentPane.getChildren().get(3);

                title.setText(components[i].getHostname());
                availability.setText("Beschikbaarheid: " + (components[i].availability) + "%");
                cost.setText("Prijs: € " + components[i].price);

                componentPane.setStyle("-fx-background-color: #fff");

                if(i != 0) {
                    componentPane.setLayoutY(componentPane.getLayoutX() + componentPane.getPrefHeight()*i);
                }

                componentContainer.getChildren().add(componentPane);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(!(url.getFile().endsWith("InfrastructureMonitor.fxml"))) {
            this.loadSelectableElements();
        }
    }
}

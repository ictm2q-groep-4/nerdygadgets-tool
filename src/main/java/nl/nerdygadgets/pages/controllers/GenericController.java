package nl.nerdygadgets.pages.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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

    @FXML
    private ComboBox selectableCategory;

    /**
     * A boolean to check if we're in the 'optimizer' view.
     */
    private boolean optimizer = false;

    /**
     * The controller for the 'back to main menu' controller in (almost) every view.
     * <p>
     * Used in: All pages except the main menu.
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

    /**
     * A method which handles the choosing of files, making sure it is an XML file & making sure it is an infrastructure design.
     * <p>
     * Used in: InfrastructureDesigner, InfrastructureMonitor
     */
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
     * <p>
     * Used in: InfrastructureMonitor, InfrastructureDesigner
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

                    if (component.componentType == ComponentType.DATABASESERVER || component.componentType == ComponentType.WEBSERVER) {

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
    private void loadSelectableElements(ComponentType type) {
        // Create an array of all the currently existing components.
        // We assume these will be the only ones in existence.
        Component[] components = {
                new DBLoadBalancer("DBLoadBalancer", 0, 0),
                new HAL9001DB("HAL9001DB", 0, 0),
                new HAL9002DB("HAL9002DB", 0, 0),
                new HAL9003DB("HAL9003DB", 0, 0),
                new HAL9001W("HAL9001W", 0, 0),
                new HAL9002W("HAL9002W", 0, 0),
                new HAL9003W("HAL9003W", 0, 0),
                new pfSense("pfSense", 0, 0)
        };

        try {

            // Load the components
            this.loadComponents(componentContainer, components, type);

        } catch (IOException e) {
            // In case of errors: Activate panic-mode (Not implemented, but the devs will panic.)
            e.printStackTrace();
        }
    }

    /**
     * Add the component type categories into a specified ComboBox
     *
     * @param comboBox ComboBox the specified combobox to add the elements to.
     */
    public void loadCategoriesIntoTypeSelector(ComboBox comboBox) {
        // add a 'general' category filter so we can see all components
        comboBox.getItems().add("Algemeen");

        // add the other components
        for (ComponentType type : ComponentType.values()) {
            comboBox.getItems().add(type.name().toLowerCase());
        }
    }

    @FXML
    public void filterComponents(ActionEvent action) {
        // get the combobox which triggered this event
        ComboBox eventTrigger = (ComboBox) action.getSource();

        // find the category, null means it is the 'general' category (so show everything)
        ComponentType category = null;
        if (!(eventTrigger.getValue().toString().equalsIgnoreCase("algemeen"))) {
            category = ComponentType.valueOf(eventTrigger.getValue().toString().toUpperCase());
        }

        // clear the current elements
        componentContainer.getChildren().clear();

        // add them again, but this time filtered.
        this.loadSelectableElements(category);
    }


    protected void loadComponents(AnchorPane container, Component[] components, ComponentType type) throws IOException {
        int multiplier = container.getChildren().size();
        for (int i = 0; i < components.length; i++) {
            if (type == null || components[i].componentType == type) {
                // Get the necessary labels to modify
                Pane componentPane = FXMLLoader.load(getClass().getResource("/pages/components/DraggableDesignerElement.fxml"));
                Label title = (Label) componentPane.getChildren().get(1);
                Label availability = (Label) componentPane.getChildren().get(2);
                Label cost = (Label) componentPane.getChildren().get(3);

                // Set the user data, will be useful for status checks
                componentPane.setUserData(components[i]);
                componentPane.setId("is-addable");
                componentPane.setOnDragDetected(mouseEvent -> {
                    DesignerController.handleDragDetection(mouseEvent);
                });

                if (this.optimizer) {
                    componentPane.setOnMouseClicked(OptimizerController::selectElement);
                }

                // set the data
                title.setText(components[i].getHostname());
                availability.setText("Beschikbaarheid: " + (components[i].availability) + "%");
                cost.setText("Prijs: € " + components[i].price);

                // add a white background, this is for beauty purposes
                componentPane.setStyle("-fx-background-color: #fff");

                // We're only adding a layoutY if it's not the first element, which is determined by the value of the multipleir
                if (multiplier > 0) {
                    componentPane.setLayoutY(componentPane.getLayoutX() + componentPane.getPrefHeight() * multiplier);
                }
                multiplier++;

                // add the component pane to the container.
                container.getChildren().add(componentPane);
            }
        }
    }

    /**
     * This method executes *after* the FXML loads.
     * <p>
     * Prepares the page for usage, depending on what page it is.
     *
     * @param url            URL
     * @param resourceBundle ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load multi-page specific parts
        if (!(url.getFile().endsWith("InfrastructureMonitor.fxml"))) {
            if (url.getFile().endsWith("InfrastructureOptimizer.fxml")) {
                this.optimizer = true;
            }

            // load the 'general' selectable elements
            this.loadSelectableElements(null);

            // load the categories into the combobox
            this.loadCategoriesIntoTypeSelector(selectableCategory);
        }
    }
}

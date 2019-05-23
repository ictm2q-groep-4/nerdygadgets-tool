package nl.nerdygadgets.pages.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import nl.nerdygadgets.algorithms.Backtracking;
import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.*;
import nl.nerdygadgets.infrastructure.design.DesignManager;
import nl.nerdygadgets.infrastructure.components.ComponentManager;
import nl.nerdygadgets.main.NerdyGadgets;
import nl.nerdygadgets.pages.PageRegister;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * All things which need to be handled the same way on every page should be registered here.
 *
 * @author Lucas Ouwens
 * @author Joris Vos
 */
public class GenericController implements Initializable {

    @FXML
    protected AnchorPane componentPane;

    @FXML
    protected AnchorPane anchorPane;

    @FXML
    protected Label totalAvailability;

    @FXML
    protected Label totalCosts;

    @FXML
    protected Label totalConfigurationsTested;

    @FXML
    private AnchorPane componentContainer;

    @FXML
    private ComboBox<String> selectableCategory;

    /**
     * A boolean to check if we're in the 'optimizer' view.
     */
    private boolean optimizer = false;

    /**
     * A boolean to check if we're on the monitor page.
     */
    private boolean monitor = false;

    /**
     * A boolean to check if we're on the builder page.
     */
    private boolean builder = false;

    /**
     * The controller for the 'back to main menu' controller in (almost) every view.
     * <p>
     * Used in: All pages except the main menu.
     */
    @FXML
    private void handleBackButton() {
        NerdyGadgets.getNerdyGadgets().setScene(PageRegister.MAIN.getIdentifier());

        // since we're leaving a view which could contain already loaded components, we set 'loaded' to false again
        // so the design can be loaded again at a later time. We don't want duplicated elements!
        if (Infrastructure.getCurrentInfrastructure() != null) {
            Infrastructure.getCurrentInfrastructure().setLoaded(false);
        }
    }

    /**
     * This enables the user to open the current design (Infrastructure.getCurrentInfrastructure())
     */
    @FXML
    private void handleOpenCurrentDesign() {
        if (Infrastructure.getCurrentInfrastructure() != null && Infrastructure.getCurrentInfrastructure().getComponents() != null) {
            if (Infrastructure.getCurrentInfrastructure().isLoaded()) {
                NerdyGadgets.showAlert(this.getClass().getSimpleName(), "Er is een fout opgetreden!\nDeze infrastructuur is al ingeladen.", Alert.AlertType.WARNING);
            } else {
                this.loadDesignIntoMonitor();
            }
        } else {
            NerdyGadgets.showAlert(this.getClass().getSimpleName(), "Er is een fout opgetreden!\nEr is geen beschikbare infrastructuur. Gebruik 'open ontwerp'.", Alert.AlertType.WARNING);
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
                    if (Files.probeContentType(infrastructureFile.toPath()).equals("application/xml") || Files.probeContentType(infrastructureFile.toPath()).equals("text/xml")) {
                        // Load the infrastructure which was picked using the filechooser
                        Infrastructure.setCurrentInfrastructure(DesignManager.getDesignManager().load(infrastructureFile));

                        // draw the components, set the availability and costs
                        this.loadDesignIntoMonitor();
                    } else {
                        NerdyGadgets.showAlert(this.getClass().getSimpleName(), "Er is een fout opgetreden tijdens het laden van een design!\n" +
                                "Het aangereikte bestand is van een onjuist formaat.", Alert.AlertType.WARNING);
                    }
                } catch (IOException e) {
                    NerdyGadgets.showAlert(this.getClass().getSimpleName(), "Er is een fout opgetreden tijdens het laden van een design!\n" +
                            "Er is een iets fout gegaan, controlleer de terminal voor de 'stacktrace'.", Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            } else {
                NerdyGadgets.showAlert(this.getClass().getSimpleName(), "Er is een fout opgetreden tijdens het laden van een design!\n" +
                        "Bestand is geen infrastructuur design (eindigend op .xml)", Alert.AlertType.WARNING);
            }
        } else {
            NerdyGadgets.showAlert(this.getClass().getSimpleName(), "Er is een fout opgetreden tijdens het openen van een design!\n" +
                    "Er is geen bestand geselecteerd!", Alert.AlertType.WARNING);
        }
    }

    /**
     * Load the components into the components field
     * <p>
     * Used in: InfrastructureMonitor, InfrastructureDesigner
     */
    void loadDesignIntoMonitor() {
        if (!(componentPane.getChildren().isEmpty())) {
            if (this.builder) {
                Rectangle rectangle = (Rectangle) componentPane.getChildren().get(0); // first child is a rectangle (the background)
                ArrayList<Node> toRemove = new ArrayList<>();
                for (Node n : componentPane.getChildren()) {
                    if (n.equals(rectangle)) {
                        continue;
                    }
                    toRemove.add(n);
                }
                componentPane.getChildren().removeAll(toRemove);
            } else {
                ArrayList<Node> toRemove = new ArrayList<>();
                for (Node n : componentPane.getChildren()) {
                    toRemove.add(n);
                }
                componentPane.getChildren().removeAll(toRemove);
            }
        }

        Infrastructure currentInfrastructure = Infrastructure.getCurrentInfrastructure();

        if (currentInfrastructure != null && currentInfrastructure.getComponents() != null) {
            // The monitor connects with ssh, this connection has a timeout of around 2000 milliseconds which is 2 seconds
            if(this.monitor) {
                NerdyGadgets.showAlert("Een ogenblikje!", "De componenten worden nu ingeladen, dit duurt maximaal "
                        + (currentInfrastructure.getComponents().size() * 2)
                        + " seconden.\nHet is mogelijk dat u een melding krijgen dat de applicatie niet reageert, sluit de applicatie niet. Het laden duurt lang, dat vindt de operating system niet leuk.", Alert.AlertType.INFORMATION);
            }
            currentInfrastructure.getComponents().forEach(component -> {
                try {
                    Pane pane = FXMLLoader.load(getClass().getResource("/pages/components/PaneComponent.fxml"));
                    pane.setUserData(component);
                    Label hostName = (Label) pane.getChildren().get(1);
                    Rectangle box = (Rectangle) pane.getChildren().get(0);

                    // Only add tooltip with statistics when we're on the monitor page.
                    if (this.monitor) {

                        // Only the hardware components should gain a tooltip with statistics
                        if (component.componentType == ComponentType.DATABASESERVER || component.componentType == ComponentType.WEBSERVER) {
                            Tooltip statisticTooltip = new Tooltip();
                            statisticTooltip.setUserData(component);
                            statisticTooltip.setOnShowing(windowEvent -> {
                                try {
                                    Tooltip statistic = (Tooltip) windowEvent.getSource();
                                    Component tooltipComponent = (Component) statistic.getUserData();



                                    if (tooltipComponent != null) {
                                        // Set tooltip data
                                        setTooltipDetails(pane, box, statisticTooltip, component, component.isOnline());
                                    }
                                } catch (NullPointerException e) {
                                    NerdyGadgets.showAlert(this.getClass().getSimpleName(), "Er is een fout opgetreden tijdens het laden van de tooltip bij een component!\n" +
                                            "Er is een 'NullPointerException' opgetreden tijdens het laden van de tooltip.\n" +
                                            "Controleer de terminal voor de 'stacktrace'.", Alert.AlertType.ERROR);
                                    e.printStackTrace();
                                }
                            });

                            // Set tooltip data
                            setTooltipDetails(pane, box, statisticTooltip, component, component.isOnline());
                        }
                    }

                    if (this.getClass().isAssignableFrom(DesignerController.class)) {
                        pane.setOnDragDetected(DesignerController::handleDragDetection);
                        pane.setOnMouseClicked(DesignerController::handleMouseClickDetection);
                    }

                    // Only hardware components should have IP addresses shown.
                    if (component.componentType == ComponentType.DATABASESERVER || component.componentType == ComponentType.WEBSERVER) {
                        Label ipv4Label = (Label) pane.getChildren().get(2);
                        Label ipv6Label = (Label) pane.getChildren().get(3);

                        ipv4Label.setText(String.valueOf(component.ipv4).replaceAll("/", "").replaceAll("localhost", ""));
                        ipv6Label.setText(String.valueOf(component.ipv6).replaceAll("/", "").replaceAll("localhost", ""));
                    }

                    hostName.setText(component.getHostname());

                    pane.setLayoutX(component.getX());
                    pane.setLayoutY(component.getY());

                    pane.getChildren().set(1, hostName);
                    if (pane.getLayoutX() <= 1280) {
                        componentPane.getChildren().add(pane);
                    }

                } catch (IOException e) {
                    NerdyGadgets.showAlert(this.getClass().getSimpleName(), "Er is een fout opgetreden tijdens het laden en tekenen van de infrastructuur in de monitor.\n" +
                            "Controleer de terminal voor de 'stacktrace'.", Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            });

            // Set 'loaded' to true to tell the application that it should no longer try to load in the design.
            Infrastructure.getCurrentInfrastructure().setLoaded(true);

            // Set the availability and total costs of the components
            this.setTotalAvailability(Infrastructure.getCurrentInfrastructure().getComponents());
            this.setTotalCosts(Infrastructure.getCurrentInfrastructure().getComponents());
        } else {
            NerdyGadgets.showAlert(this.getClass().getSimpleName(), "Er is een fout opgetreden tijdens het laden en tekenen van de infrastructuur in de monitor.\n" +
                    "We kunnen geen componenten binnen het aangereikte bestand vinden om toe te voegen.", Alert.AlertType.WARNING);
        }
    }

    /**
     * Calculate the total costs for the infrastructure.
     *
     * @param components List<Component>
     */
    private void setTotalCosts(List<Component> components) {
        int price = 0;

        for (Component component : components) {
            price += component.price;
        }

        totalCosts.setText("Totale kosten: €" + price + ",-");
    }

    /**
     * Calculation for availability.
     *
     * @param components List<Component>
     */
    private void setTotalAvailability(List<Component> components) {
        double availability = 1;

        List<Component> webComponents = new ArrayList<>();
        List<Component> databaseComponents = new ArrayList<>();
        List<Component> otherComponents = new ArrayList<>();

        for (Component component : components) {
            if (component.componentType.equals(ComponentType.WEBSERVER)) {
                webComponents.add(component);
            } else if (component.componentType.equals(ComponentType.DATABASESERVER)) {
                databaseComponents.add(component);
            } else {
                otherComponents.add(component);
            }
        }

        if (otherComponents.size() > 0) {
            for (Component component : otherComponents) {
                availability *= (component.availability * 0.01);
            }
        }

        if (webComponents.size() > 0) {
            availability *= (Backtracking.getAvailability(webComponents.toArray(Component[]::new)) * 0.01);
        }

        if (databaseComponents.size() > 0) {
            availability *= (Backtracking.getAvailability(databaseComponents.toArray(Component[]::new)) * 0.01);
        }

        availability *= 100;

        totalAvailability.setText("Totale beschikbaarheid: " + new DecimalFormat("#.###").format(availability).replace(',', '.') + "%");
    }

    /**
     * Load the elements which will be used to fill the design/optimizer.
     */
    private void loadSelectableElements(ComponentType type) {
        try {
            // Load the components
            this.loadComponents(componentContainer, ComponentManager.getAllComponents(), type);

        } catch (IOException e) {
            // In case of errors: Activate panic-mode (Not implemented, but the devs will panic.)
            NerdyGadgets.showAlert(this.getClass().getSimpleName(), "Er is een fout opgetreden tijdens het laden van alle verschillende componenten in de select box.\n" +
                    "Controleer de terminal voor een eventuele 'stacktrace'.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Add the component type categories into a specified ComboBox
     *
     * @param comboBox ComboBox the specified combobox to add the elements to.
     */
    private void loadCategoriesIntoTypeSelector(ComboBox<String> comboBox) {
        // add a 'general' category filter so we can see all components
        comboBox.getItems().add("Algemeen");

        // add the other components
        for (ComponentType type : ComponentType.values()) {
            comboBox.getItems().add(type.name().toLowerCase());
        }
    }

    /**
     * This filters the component
     *
     * @param action    ActionEvent
     */
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

    /**
     * This method loads all the components in a given pane
     *
     * @param container     AnchorPane
     * @param components    Component[]
     * @param type          ComponentType
     * @throws IOException
     */
    void loadComponents(AnchorPane container, Component[] components, ComponentType type) throws IOException {
        int multiplier = container.getChildren().size();
        for (int i = 0; i < components.length; i++) {
            if (type == null || components[i].componentType == type) {
                // Get the necessary labels to modify
                Pane pane = FXMLLoader.load(getClass().getResource("/pages/components/DraggableDesignerElement.fxml"));
                Label title = (Label) pane.getChildren().get(1);
                Label availability = (Label) pane.getChildren().get(2);
                Label cost = (Label) pane.getChildren().get(3);


                // Set the user data, will be useful for status checks
                pane.setUserData(components[i]);
                pane.setId("is-addable");

                // set event, static reference to the handleDragDetection method in the DesignerController class
                if (this.getClass().isAssignableFrom(DesignerController.class)) {
                    pane.setOnDragDetected(DesignerController::handleDragDetection);

                }

                if (this.optimizer) {
                    pane.setOnMouseClicked(OptimizerController::selectElement);
                }

                // set the data
                title.setText(components[i].name);
                availability.setText("Beschikbaarheid: " + (components[i].availability) + "%");
                cost.setText("Prijs: €" + components[i].price + ",-");

                // add a white background, this is for beauty purposes
                pane.setStyle("-fx-background-color: #fff");

                // We're only adding a layoutY if it's not the first element, which is determined by the value of the multiplier
                if (multiplier > 0) {
                    pane.setLayoutY(pane.getLayoutX() + pane.getPrefHeight() * multiplier);
                }
                multiplier++;

                // add the component pane to the container.
                container.getChildren().add(pane);
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
            } else if ((url.getFile().endsWith("InfrastructureDesigner.fxml"))) {
                this.builder = true;
            }

            // load the 'general' selectable elements
            this.loadSelectableElements(null);

            // load the categories into the combobox
            this.loadCategoriesIntoTypeSelector(selectableCategory);
        } else {
            this.monitor = true;
        }
    }

    private void setTooltipDetails(Pane pane, Rectangle box, Tooltip statisticTooltip, Component component, boolean isOnline) {
        if (isOnline) {
            box.setFill(Color.GREEN);
            statisticTooltip.setText(
                    "Status: Online\n" +
                            "Schijfruimte: " + component.getDiskUsage() + "\n" +
                            "Processor gebruik: " + (component.getProcessorUsage())
            );
        } else {
            box.setFill(Color.DARKRED);
            statisticTooltip.setText(
                    "Status: Offline\n" +
                            "Schrijfruimte: Onbeschikbaar\n" +
                            "Processor gebruik: Onbeschikbaar"
            );
        }

        Tooltip.install(pane, statisticTooltip);
    }

}

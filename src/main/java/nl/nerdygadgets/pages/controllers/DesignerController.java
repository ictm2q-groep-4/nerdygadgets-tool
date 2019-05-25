package nl.nerdygadgets.pages.controllers;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.stage.*;

import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.ComponentType;
import nl.nerdygadgets.main.NerdyGadgets;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import nl.nerdygadgets.pages.PageRegister;
import nl.nerdygadgets.pages.popups.GenericPopup;
import nl.nerdygadgets.pages.popups.PopupMenu;


/**
 * This handles the logic for the Designer (Builder)
 *
 * @author Stefan Booij
 * @author Joris Vos
 */
public class DesignerController extends GenericController {

    /**
     * A variable that transfers events between methods.
     */
    @FXML
    private static Event transferEvent;


    /**
     * Detects dragging over the imageview
     *
     * @param dragEvent DragEvent
     */
    @FXML
    private void handleDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasString()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }

    }


    /**
     * Handles dropping. It alters the coordinates for the component on the layout
     *
     * @param dragEvent DragEvent
     */

    @FXML
    private void handleDrop(DragEvent dragEvent) {
        if (Infrastructure.getCurrentInfrastructure() == null) {
            Infrastructure.setCurrentInfrastructure(new Infrastructure());
        }

        Pane component = (Pane) getTransferEvent().getSource();

        // If a component is dropped & the current design wasn't loaded, we clear the infrastructure component list.
        if (Infrastructure.getCurrentInfrastructure() != null && !Infrastructure.getCurrentInfrastructure().isLoaded()
                && !Infrastructure.getCurrentInfrastructure().getComponents().isEmpty()) {

            // We are creating a 'generic' popup to make sure they want to continue with the action
            GenericPopup popup = new GenericPopup("/pages/components/DragComponentAlert.fxml", "Waarschuwing!");

            // Get the necessary pieces of data to register the button events
            HBox buttonContainer = (HBox) popup.getContainer().getChildren().get(1);

            Button cancelAction = (Button) buttonContainer.getChildren().get(0);
            Button continueAction = (Button) buttonContainer.getChildren().get(1);

            // just close, because isOk is false by default.
            cancelAction.setOnMouseClicked(event -> popup.getStage().close());

            // We're continuing
            continueAction.setOnMouseClicked(event -> {
                popup.setOk(true);
                popup.getStage().close();
            });

            // show the popup
            popup.getStage().showAndWait();

            // if they agree with clearing the currently existing infrastructure, clear it and set 'loaded' to true so this popup does not happen again.
            if (popup.isOk()) {
                Infrastructure.getCurrentInfrastructure().getComponents().clear();
            } else {
                // cancel the drop if he does not agree.
                return;
            }
        }

        // If it wasn't loaded before, it is now.
        if(!Infrastructure.getCurrentInfrastructure().isLoaded()) {
            Infrastructure.getCurrentInfrastructure().setLoaded(true);
        }

        //Boolean to check if component is already in the layout
        boolean existenceCheck = false;

        PopupMenu menu = new PopupMenu(component, false);

        //If the component does not exist in the layout, it will create a new instance of it
        if (!componentPane.getChildren().contains(component) && !anchorPane.getChildren().contains(component)) {
            menu.getWindow().showAndWait();

            if (menu.isOk()) {
                component = copyAttributes(component);

                component.setOnMouseClicked(DesignerController::handleMouseClickDetection);

                componentPane.getChildren().add(component);
            } else {
                return;
            }
        } else {
            existenceCheck = true;
        }

        double borderRight = componentPane.getWidth();
        double borderBottom = componentPane.getHeight();
        double componentWidth = component.getWidth();
        double componentHeight = component.getHeight();

        //TODO A check that wil prevent components from overlapping in the overlay
        //Sets X coordinates for component
        if (dragEvent.getX() <= componentWidth / 2) {
            component.setLayoutX(0);
        } else if (dragEvent.getX() >= borderRight - (componentWidth / 2)) {
            component.setLayoutX(borderRight - componentWidth);
        } else {
            component.setLayoutX(dragEvent.getX() - (componentWidth / 2));
        }

        //Sets Y coordinates
        if (dragEvent.getY() <= componentHeight / 2) {
            component.setLayoutY(0);
        } else if (dragEvent.getY() >= borderBottom - (componentHeight / 2)) {
            component.setLayoutY(borderBottom - componentHeight);
        } else {
            component.setLayoutY(dragEvent.getY() - (componentHeight / 2));
        }

        if (existenceCheck) {
            //Edits the coordinates of an existing component
            editComponentCoordinates(component);
        } else {
            addComponentToInfrastructure(component);
        }

        if (Infrastructure.getCurrentInfrastructure().getComponents().size()>0) {
            // Set the availability and total costs of the components
            setTotalAvailability(Infrastructure.getCurrentInfrastructure().getComponents());
            setTotalCosts(Infrastructure.getCurrentInfrastructure().getComponents());
        }
    }

    /**
     * Adds a component to the infrastructure when it's placed in the layout.
     *
     * @param component Pane
     */
    private void addComponentToInfrastructure(Pane component) {
        List<Component> infraComponents = Infrastructure.getCurrentInfrastructure().getComponents();

        Component newComponent = (Component) component.getUserData();

        newComponent.setX((int) component.getLayoutX());
        newComponent.setY((int) component.getLayoutY());

        infraComponents.add(newComponent);
        System.out.println("Component added to infrastructure. X and Y coordinates of host '" + newComponent.hostname + "' are: X: " + newComponent.getX() + " Y: " + newComponent.getY());

    }


    /**
     * @param component Pane
     */
    private void editComponentCoordinates(Pane component) {
        Component draggedComponent = (Component) component.getUserData();

        draggedComponent.setX((int) component.getLayoutX());
        draggedComponent.setY((int) component.getLayoutY());

        System.out.println("X and Y coordinates of host '" + draggedComponent.hostname + "' are changed. X: " + draggedComponent.getX() + " Y: " + draggedComponent.getY());
    }

    /**
     * This changes the attributelabel
     *
     * @param component Pane
     */
    private static void editComponentAttributeLabels(Pane component) {
        Component clickedComponent = (Component) component.getUserData();


        Label hostname = (Label) component.getChildren().get(1);
        hostname.setText(clickedComponent.hostname);

        // We only show the IP addresses if it is a hardware component.
        if (clickedComponent.componentType == ComponentType.WEBSERVER || clickedComponent.componentType == ComponentType.DATABASESERVER) {
            Label ipv4Label = (Label) component.getChildren().get(2);
            Label ipv6Label = (Label) component.getChildren().get(3);

            ipv4Label.setText(String.valueOf(clickedComponent.ipv4).replaceAll("/", "").replaceAll("localhost", ""));
            ipv6Label.setText(String.valueOf(clickedComponent.ipv6).replaceAll("/", "").replaceAll("localhost", ""));
        }
    }

    /**
     * Copies the attributes from the component out of componentlist, and places it into a draggable pane with the createDraggablePane method.
     *
     * @param component Pane
     * @return          Pane
     */
    private Pane copyAttributes(Pane component) {
        Pane draggablePane = createDraggablePane(component);

        Component dataContainer = (Component) component.getUserData();

        //Copy icon to componentpane
        Label hostnameLabel = (Label) draggablePane.getChildren().get(1);
        hostnameLabel.setText(dataContainer.getHostname());

        // Only hardware components should display the IP addresses
        if (dataContainer.componentType == ComponentType.DATABASESERVER || dataContainer.componentType == ComponentType.WEBSERVER) {
            Label ipv4Label = (Label) draggablePane.getChildren().get(2);
            Label ipv6Label = (Label) draggablePane.getChildren().get(3);

            ipv4Label.setText(String.valueOf(dataContainer.ipv4).replaceAll("/", "").replaceAll("localhost", ""));
            ipv6Label.setText(String.valueOf(dataContainer.ipv6).replaceAll("/", "").replaceAll("localhost", ""));
        }

        return draggablePane;
    }

    /**
     * Returns an achorpane with dragdetection
     *
     * @return AnchorPane draggableComponent
     */
    public AnchorPane createDraggablePane(Pane component) {
        try {
            AnchorPane draggableComponent = FXMLLoader.load(getClass().getResource(PageRegister.get("PaneComponent").getFilePath()));

            draggableComponent.setOnDragDetected(DesignerController::handleDragDetection);

            draggableComponent.setUserData(component.getUserData());

            return draggableComponent;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Handles the detection of dragging
     *
     * @param mouseEvent    MouseEvent
     */
    @FXML
    public static void handleDragDetection(MouseEvent mouseEvent) {
        AnchorPane component = (AnchorPane) mouseEvent.getSource();

        // Initialize dragging operation
        Dragboard db = component.startDragAndDrop(TransferMode.ANY);
        // The dragboard requires content to initialize dragging, but the transferring of content is done through transferEvent.
        ClipboardContent cb = new ClipboardContent();
        // Used for check in handleDragOver that will ignore content from outside of the application
        cb.putString("Check for draggable components");
        db.setContent(cb);

        // Transfers this event to get access to the attributes of the components in other methods
        setTransferEvent(mouseEvent);
        mouseEvent.consume();
    }

    /**
     * This handles the mouse click.
     * This enables the user to edit component attributes
     *
     * @param event MouseEvent
     */
    public static void handleMouseClickDetection(MouseEvent event) {
        System.out.println(event.getSource());
        PopupMenu eventMenu = new PopupMenu((Pane) event.getSource(), true);
        eventMenu.getWindow().showAndWait();

        if (eventMenu.isOk()) {
            editComponentAttributeLabels((Pane) event.getSource());
        }
    }

    /**
     * Starts the operation of saving the XML file.
     */
    @FXML
    private void handleSaveDesign() {
        //Choose directory where to save file
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("XML File", "*.xml");
        fileChooser.getExtensionFilters().add(fileExtensions);

        File selectedDirectory = fileChooser.showSaveDialog(NerdyGadgets.getNerdyGadgets().getStage());

        if (selectedDirectory != null && selectedDirectory.getName().endsWith(".xml")) {
            try {
                Infrastructure.getCurrentInfrastructure().save(selectedDirectory.getAbsolutePath());

            } catch (Exception E) {
                E.printStackTrace();
                NerdyGadgets.showAlert("Er is een fout opgetreden!", "Er moet een naam opgegeven worden en er moeten ook componenten in het ontwerp zitten!", Alert.AlertType.ERROR);
            }
        } else {
            NerdyGadgets.showAlert("Er is een fout opgetreden!", "Geen XML bestand!", Alert.AlertType.ERROR);
        }
    }

    /**
     * Return the transferevent
     *
     * @return  Event
     */
    public static Event getTransferEvent() {
        return transferEvent;
    }

    /**
     * Set the transferevent
     *
     * @param event Event
     */
    public static void setTransferEvent(Event event) {
        transferEvent = event;
    }
}
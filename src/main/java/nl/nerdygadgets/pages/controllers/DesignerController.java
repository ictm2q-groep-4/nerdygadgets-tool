package nl.nerdygadgets.pages.controllers;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.input.*;
import javafx.stage.*;
import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.main.NerdyGadgets;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import nl.nerdygadgets.pages.PopupMenu;


/**
 * @author Stefan Booij
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
     * @param dragEvent
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
     * @param dragEvent
     */

    @FXML
    private void handleDrop(DragEvent dragEvent) {
        Pane component = (Pane) getTransferEvent().getSource();

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
        }else{
            addComponentToInfrastructure(component);
        }

    }

    /**
     * Adds a component to the infrastructure when it's placed in the layout.
     *
     * @param component
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
     * @param component
     */
    private void editComponentCoordinates(Pane component) {
        List<Component> components = Infrastructure.getCurrentInfrastructure().getComponents();

        Component draggedComponent = (Component) component.getUserData();

        draggedComponent.setX((int) component.getLayoutX());
        draggedComponent.setX((int) component.getLayoutY());

        System.out.println("X and Y coordinates of host '" + draggedComponent.hostname + "' are changed. X: " + draggedComponent.getX() + " Y: " + draggedComponent.getY());
    }

    private static void editComponentAttributeLabels(Pane component){
        Component clickedComponent = (Component) component.getUserData();


        Label hostname = (Label) component.getChildren().get(1);
        Label ipv4Label = (Label) component.getChildren().get(2);
        Label ipv6Label = (Label) component.getChildren().get(3);

        hostname.setText(clickedComponent.hostname);
        ipv4Label.setText(String.valueOf(clickedComponent.ipv4).replaceAll("/", "").replaceAll("localhost", ""));
        ipv6Label.setText(String.valueOf(clickedComponent.ipv6).replaceAll("/", "").replaceAll("localhost", ""));
    }

    /**
     * Copies the attributes from the component out of componentlist, and places it into a draggable pane with the createDraggablePane method.
     *
     * @param component
     * @return Pane component
     */
    private Pane copyAttributes(Pane component) {
        Pane draggablePane = createDraggablePane(component);

        Component dataContainer = (Component) component.getUserData();

        //Copy icon to componentpane
        Label hostnameLabel = (Label) draggablePane.getChildren().get(1);
        hostnameLabel.setText(dataContainer.getHostname());

        Label ipv4Label = (Label) draggablePane.getChildren().get(2);
        Label ipv6Label = (Label) draggablePane.getChildren().get(3);

        ipv4Label.setText(String.valueOf(dataContainer.ipv4).replaceAll("/", "").replaceAll("localhost", ""));
        ipv6Label.setText(String.valueOf(dataContainer.ipv6).replaceAll("/", "").replaceAll("localhost", ""));

        return draggablePane;
    }

    /**
     * Returns an achorpane with dragdetection
     *
     * @return AnchorPane draggableComponent
     */
    public AnchorPane createDraggablePane(Pane component) {
        try {
            AnchorPane draggableComponent = FXMLLoader.load(getClass().getResource("/pages/components/PaneComponent.fxml"));

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
     * @param mouseEvent
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

    public static void handleMouseClickDetection(MouseEvent event) {
        System.out.println(event.getSource());
        PopupMenu eventMenu = new PopupMenu((Pane) event.getSource(), true);
        eventMenu.getWindow().showAndWait();

        if (eventMenu.isOk()) {
            editComponentAttributeLabels((Pane) event.getSource());
            System.out.println("New data.");
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
                NerdyGadgets.showAlert("Er is een fout opgetreden!", "", Alert.AlertType.ERROR);
            }
        } else {
            NerdyGadgets.showAlert("Er is een fout opgetreden!", "Geen XML bestand!", Alert.AlertType.ERROR);
        }
    }

    public static Event getTransferEvent() {
        return transferEvent;
    }

    public static void setTransferEvent(Event event) {
        transferEvent = event;
    }
}
package nl.nerdygadgets.pages.controllers;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.main.NerdyGadgets;

import java.io.File;
import java.util.List;

import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javafx.scene.layout.VBox;


/**
 * @author Stefan Booij
 */
public class DesignerController extends GenericController {

    /**
     * A variable that transfers events between methods.
     */
    @FXML
    private static Event transferEvent;

    public boolean attributeOK;


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

        //If the component does not exist in the layout, it will create a new instance of it
        if (!componentPane.getChildren().contains(component)) {
            component = copyAttributes(component);
            componentPane.getChildren().add(component);
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
            editComponentObject(component);
        }

    }


    /**
     * @param component
     */
    private void editComponentObject(Pane component) {
        List<Component> components = Infrastructure.getCurrentInfrastructure().getComponents();

//        for (Component x : components) {
//            String hostname = getHostname(component);
//            if (x.getHostname().equals(hostname)) {
//                x.setX((int) component.getLayoutX());
//                x.setY((int) component.getLayoutY());
//                System.out.println("X and Y coordinates of host '" + hostname + "' are changed. X: " + x.getX() + " Y: " + x.getY());
//            }
    }

    /**
     * Copies the attributes from the component out of componentlist, and places it into a draggable pane with the createDraggablePane method.
     *
     * @param component
     * @return Pane component
     */
    private Pane copyAttributes(Pane component) {
        Pane draggablePane = createDraggablePane(component);
        VBox attributes = new VBox();

        //Copy icon to componentpane
        Label typeCopy = (Label) component.getChildren().get(1);
        Label componentType = new Label(typeCopy.getText());
        componentType.setVisible(false);

        Rectangle iconCopy = (Rectangle) component.getChildren().get(0);
        Rectangle componentIcon = new Rectangle(iconCopy.getHeight(), iconCopy.getWidth());

        attributes.getChildren().add(componentIcon);

        draggablePane.getChildren().add(componentType);
        draggablePane.getChildren().add(attributes);

        return draggablePane;
    }

    /**
     * Returns an achorpane with dragdetection
     *
     * @return AnchorPane draggableComponent
     */
    public AnchorPane createDraggablePane(Pane component) {
        AnchorPane draggableComponent = new AnchorPane();

        draggableComponent.setOnDragDetected(mouseEvent -> {
            handleDragDetection(mouseEvent);
        });

        return draggableComponent;
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

        //TODO Check that the component has IPv4 for saving
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
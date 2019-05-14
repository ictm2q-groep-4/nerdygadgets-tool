package nl.nerdygadgets.pages.controllers;


import javafx.event.Event;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import nl.nerdygadgets.infrastructure.components.*;

import java.io.IOException;

/**
 * @author Stefan Booij
 */
public class DesignerController extends GenericController {

    /**
     * A variable that transfers events between methods.
     */
    private Event transferEvent;


    /**
     * Detects dragging over the imageview
     *
     * @param dragEvent
     */
    @FXML
    private void handleDragOver(DragEvent dragEvent) {
        dragEvent.acceptTransferModes(TransferMode.ANY);
    }

    /**
     * Handles dropping. It alters the coordinates for the component on the layout
     *
     * @param dragEvent
     */
    @FXML
    private void handleDrop(DragEvent dragEvent) {
        //Gets ImageView of dragged component
        AnchorPane component = (AnchorPane) getTransferEvent().getSource();

        Double borderRight = componentPane.getWidth();
        Double borderBottom = componentPane.getHeight();
        Double componentWidth = component.getWidth();
        Double componentHeight = component.getHeight();

        //Sets X coordinates for component
        if (dragEvent.getX() <= componentWidth / 2) {
            component.setLayoutX(0);
        } else if (dragEvent.getX() >= borderRight - (componentWidth / 2)) {
            component.setLayoutX(borderRight - componentWidth);
        } else {
            component.setLayoutX(dragEvent.getX() - (component.getWidth() / 2));
        }

        //Sets Y coordinates
        if (dragEvent.getY() <= componentHeight / 2) {
            component.setLayoutY(0);
        } else if (dragEvent.getY() >= borderBottom - (componentHeight / 2)) {
            component.setLayoutY(borderBottom - componentHeight);
        } else {
            component.setLayoutY(dragEvent.getY() - (componentHeight / 2));
        }


    }

    /**
     * Handles dragging and initializes drag and drop operation
     *
     * @param mouseEvent
     */
    @FXML
    public void handleDragDetection(MouseEvent mouseEvent) {
        AnchorPane component = (AnchorPane) mouseEvent.getSource();

//        // Initialize dragging operation
        Dragboard db = component.startDragAndDrop(TransferMode.ANY);
        //This did not recognize the image. Not sure why.
        ClipboardContent cb = new ClipboardContent();
        cb.putString("Yeet"); // Not sure
        db.setContent(cb);

        //Transfers this event to get access to the attributes of the components in other methods
        setTransferEvent(mouseEvent);
        mouseEvent.consume();
    }



    private Event getTransferEvent() {
        return transferEvent;
    }

    private void setTransferEvent(Event event) {
        transferEvent = event;
    }

}

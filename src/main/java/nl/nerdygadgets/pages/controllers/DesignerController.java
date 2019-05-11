package nl.nerdygadgets.pages.controllers;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.List;

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
     * @throws FileNotFoundException
     */
    @FXML
    private void handleDrop(DragEvent dragEvent) throws FileNotFoundException {
        //Gets image of component
        ImageView component = (ImageView) getTransferEvent().getSource();

        //Sets coordinates for component
        component.setX(dragEvent.getX() - (component.getFitWidth() / 2));
        component.setY(dragEvent.getY() - (component.getFitHeight() / 2));

        dragEvent.consume();
    }

    /**
     * Handles dragging and initializes drag and drop operation
     *
     * @param mouseEvent
     */
    @FXML
    public void handleDragDetection(MouseEvent mouseEvent) {
        ImageView component = (ImageView) mouseEvent.getSource();
        // Initialize dragging operation
        Dragboard db = component.startDragAndDrop(TransferMode.ANY);

        //This did not recognize the image. Not sure why.
        ClipboardContent cb = new ClipboardContent();
        cb.putImage(component.getImage());
        db.setContent(cb);

        //Transfers this event to get access to the attributes of the components in other methods
        setTransferEvent(mouseEvent);
        mouseEvent.consume();
    }

    private Event getTransferEvent(){
        return transferEvent;
    }

    private void setTransferEvent (Event event){
        transferEvent = event;
    }
}

package nl.nerdygadgets.pages.controllers;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;

import java.io.FileNotFoundException;


/**
 * @author Stefan Booij
 */
public class DesignerController extends GenericController {
    /**
     * A variable that transfers events between methods.
     */

    private Event transferEvent;

    @FXML
    public ImageView componentLayout;

//    public AnchorPane componentPane;

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
        //Gets ImageView of dragged component
        ImageView component = (ImageView) getTransferEvent().getSource();

//        if(!componentPane.getChildren().contains(component)){
//            componentPane.getChildren().add(component);
//        }

        Double borderRight = componentLayout.getFitWidth();
        Double borderBottom = componentLayout.getFitHeight();
        Double componentWidth = component.getFitWidth();
        Double componentHeight = component.getFitHeight();

        //Sets X coordinates for component
        if (dragEvent.getX() <= componentWidth / 2) {
            component.setLayoutX(0);
        } else if (dragEvent.getX() >= borderRight - (componentWidth / 2)) {
            component.setLayoutX(borderRight - componentWidth);
        } else {
            component.setLayoutX(dragEvent.getX() - (component.getFitWidth() / 2));
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
        ImageView component = (ImageView) mouseEvent.getSource();

//        // Initialize dragging operation
        Dragboard db = component.startDragAndDrop(TransferMode.ANY);
        //This did not recognize the image. Not sure why.
        ClipboardContent cb = new ClipboardContent();
        cb.putImage(component.getImage());
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

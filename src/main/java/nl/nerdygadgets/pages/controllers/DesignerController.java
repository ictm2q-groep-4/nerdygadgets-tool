package nl.nerdygadgets.pages.controllers;


import javafx.event.Event;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import nl.nerdygadgets.infrastructure.components.*;

import java.io.File;
import java.io.IOException;
import java.util.IllegalFormatWidthException;
import java.util.List;

/**
 * @author Stefan Booij
 */
public class DesignerController extends GenericController {

    /**
     * A variable that transfers events between methods.
     */
    public AnchorPane componentLayout;


    /**
     * Detects dragging over the imageview
     *
     * @param dragEvent
     */
    @FXML
    private void handleDragOver(DragEvent dragEvent) {
        if(dragEvent.getDragboard().hasString()){
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
        //TODO Create two methods that set coordination for an element that gets dragged into the layout, and a method for one that's already in it.

        AnchorPane component = (AnchorPane) getTransferEvent().getSource();

        //If the component does not exist in the layout, it will create a new instance of it
        if (!componentLayout.getChildren().contains(component)) {
            component = copyComponentElements(component);
            component = addComponentAttributes(component);
            componentLayout.getChildren().add(component);
        } else{

        }



        double borderRight = componentLayout.getWidth();
        double borderBottom = componentLayout.getHeight();
        double componentWidth = component.getWidth();
        double componentHeight = component.getHeight();

        //Sets X coordinates for component
        if (dragEvent.getX() <= componentWidth / 2) {
            component.setLayoutX(0);
        } else if (dragEvent.getX() >= borderRight - (componentWidth / 2)) {
            component.setLayoutX(borderRight - componentWidth);
        } else {
            component.setLayoutX(dragEvent.getX() - (componentWidth / 2));
            System.out.println("YES");
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
     * Copies the elements of the component in the component list and creates a new AnchorPane to place in the layout.
     *
     * @param listComponent
     * @return
     */
    private AnchorPane copyComponentElements(AnchorPane listComponent) {
        AnchorPane newComponent = createDraggablePane();

        //Fetches elements of the dragged component
        AnchorPane componentElements = (AnchorPane) listComponent.getChildren().get(0);

        //Places elements in the created component
        Label title = (Label) componentElements.getChildren().get(1);
        Label titleCopy = new Label(title.getText());

        newComponent.getChildren().add(titleCopy);
        newComponent.setStyle("-fx-background-color: #88ffff");

        return newComponent;
    }

    private AnchorPane addComponentAttributes(AnchorPane component){
        VBox attributes = new VBox();

        //TODO Add all the attributes that need to be displayed
        Label title = (Label) component.getChildren().get(0);

        //TODO Add styling to TextField
        TextField name = new TextField();
        TextField ipv4 = new TextField();
        TextField ipv6 = new TextField();

        attributes.getChildren().add(title);

        attributes.getChildren().add(name);
        attributes.getChildren().add(ipv4);
        attributes.getChildren().add(ipv6);

        component.getChildren().add(attributes);

        return component;
    }

}

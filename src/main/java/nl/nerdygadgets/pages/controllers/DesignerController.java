package nl.nerdygadgets.pages.controllers;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.stage.*;
import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.main.NerdyGadgets;

import java.io.File;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import static javafx.application.Application.launch;


/**
 * @author Stefan Booij
 */
public class DesignerController extends GenericController {

    /**
     * A variable that transfers events between methods.
     */

    public AnchorPane componentLayout;
    @FXML
    public static Event transferEvent;


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
        boolean existanceCheck = false;
        HostnameAlert hostnameInput = new HostnameAlert();

        //If the component does not exist in the layout, it will create a new instance of it
        if (!componentLayout.getChildren().contains(component)) {
            if (hostnameInput.display()) {
                String hostname = hostnameInput.getHostname();
                component = copyComponentElements(component);
                component = addComponentAttributes(component, hostname);
                componentLayout.getChildren().add(component);
//            addComponentToInfrastructure(component, hostname);
            }else{
                //TODO Catch destruction of component
            }
        }else{
                existanceCheck = true;
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
        }

        //Sets Y coordinates
        if (dragEvent.getY() <= componentHeight / 2) {
            component.setLayoutY(0);
        } else if (dragEvent.getY() >= borderBottom - (componentHeight / 2)) {
            component.setLayoutY(borderBottom - componentHeight);
        } else {
            component.setLayoutY(dragEvent.getY() - (componentHeight / 2));
        }

        if (existanceCheck) {
            //TODO create method that edits the coordinates of component object
            //editComponentObject();
        }

    }

    //TODO When hostname is cancelled, it destroys the component
    class HostnameAlert {
        private boolean isOk;
        TextField hostnameField;

        public void main(String[] args) {
            launch(args);
        }

        public boolean display() {
            Stage window = new Stage();
            window.initStyle(StageStyle.UTILITY);

            VBox hostnameDialog = null;
            try {
                hostnameDialog = FXMLLoader.load(getClass().getResource("/pages/components/HostnameAlert.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            hostnameField = (TextField) hostnameDialog.getChildren().get(1);
            Button okButton = (Button) hostnameDialog.getChildren().get(2);

            okButton.setOnAction(actionEvent -> {
                isOk = true;
                window.close();
            });

            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Hostname");
            window.setScene(new Scene(hostnameDialog));

            window.showAndWait();

            return isOk;
        }

        public String getHostname(){
            return hostnameField.getText();
        }
    }


    //TODO Create this method!
    private void addComponentToInfrastructure(Pane component, String hostname) {
        List<Component> infraComponents = Infrastructure.getCurrentInfrastructure().getComponents();
        try {
            Component componentObject = createComponentObject(component, hostname);
            infraComponents.add(componentObject);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            System.out.println("Creating component failed");
            e.printStackTrace();
        }
    }

    public Component createComponentObject(Pane component, String hostname) throws NoSuchMethodException, ClassNotFoundException {
        Label labelType = (Label) component.getChildren().get(0);

        String type = labelType.getText();
        String fullClassPath = "nl.nerdygadgets.infrastructure.components." + type;

        //TODO hostname from textfield
//        String hostname;
//        int x = (int) component.getLayoutX();
//        int y = (int) component.getLayoutY();
//
//        Class<?> cls = Class.forName(fullClassPath);
//        return (Component) cls.getConstructor(String.class, int.class, int.class).newInstance(hostname, x, y);
        return null;
    }

    /**
     * Copies the elements of the component in the component list and creates a new AnchorPane to place in the layout.
     *
     * @param listComponent
     * @return
     */

    private Pane copyComponentElements(Pane listComponent) {
        Pane newComponent = createDraggablePane();

        //Places elements in the created component
        Label title = (Label) listComponent.getChildren().get(1);
        Label titleCopy = new Label(title.getText());

        newComponent.getChildren().add(titleCopy);
        newComponent.setStyle("-fx-background-color: #88ffff");

        return newComponent;

    }

    public AnchorPane createDraggablePane() {
        AnchorPane draggableComponent = new AnchorPane();

        draggableComponent.setOnDragDetected(mouseEvent -> {
            handleDragDetection(mouseEvent);
        });

        return draggableComponent;
    }

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


        if (selectedDirectory != null && selectedDirectory.getName().endsWith(".xml")) {
            try {
                Infrastructure.getCurrentInfrastructure().save(selectedDirectory.getAbsolutePath());

            } catch (Exception E) {
                E.printStackTrace();
                NerdyGadgets.showAlert("Er is een fout opgetregen!", "", Alert.AlertType.ERROR);
            }
        } else {
            NerdyGadgets.showAlert("Er is een fout opgetregen!", "Geen XML bestand!", Alert.AlertType.ERROR);
        }
    }


    private Pane addComponentAttributes(Pane component, String hostname) {
        VBox attributes = new VBox();

        //TODO Add all the attributes that need to be displayed
        Label title = (Label) component.getChildren().get(0);

        //TODO Add styling to TextField
        Label name = new Label(hostname);
        TextField ipv4 = new TextField();
        TextField ipv6 = new TextField();

        attributes.getChildren().add(title);

        attributes.getChildren().add(name);
        attributes.getChildren().add(ipv4);
        attributes.getChildren().add(ipv6);

        component.getChildren().add(attributes);

        return component;
    }

    public static Event getTransferEvent() {
        return transferEvent;
    }

    public static void setTransferEvent(Event event) {
        transferEvent = event;
    }

}

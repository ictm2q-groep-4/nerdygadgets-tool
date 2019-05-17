package nl.nerdygadgets.pages.controllers;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.ComponentType;
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

                component.setOnMouseClicked(event -> {
                    System.out.println(event.getSource());
                    PopupMenu eventMenu = new PopupMenu((Pane) event.getSource(), true);
                    eventMenu.getWindow().showAndWait();

                    if (eventMenu.isOk()) {
                        System.out.println("New data.");
                    }
                });

                componentPane.getChildren().add(component);
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

        draggableComponent.setUserData(component.getUserData());

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

    class PopupMenu {

        private Stage window;

        private TextField
                hostNameField,
                IPV4Field,
                IPV6Field,
                SSHUsernameField,
                SSHPasswordField;

        private Button submit;

        private Pane pane;

        private Label errorLabel;

        private boolean ok;

        public PopupMenu(Pane pane, boolean editing) {
            this.pane = pane;
            this.window = new Stage();

            window.initModality(Modality.APPLICATION_MODAL);

            try {

                VBox dialog;
                Component dataContainer = (Component) pane.getUserData();

                boolean isHardware = dataContainer.componentType == ComponentType.DATABASESERVER || dataContainer.componentType == ComponentType.WEBSERVER;

                if (isHardware) {
                    dialog = FXMLLoader.load(getClass().getResource("/pages/components/AttributePopup.fxml"));

                    IPV4Field = (TextField) dialog.getChildren().get(3);
                    IPV6Field = (TextField) dialog.getChildren().get(5);
                    SSHUsernameField = (TextField) dialog.getChildren().get(7);
                    SSHPasswordField = (TextField) dialog.getChildren().get(9);
                } else {
                    dialog = FXMLLoader.load(getClass().getResource("/pages/components/HostnameAlert.fxml"));
                }

                hostNameField = (TextField) dialog.getChildren().get(1);


                if (editing) {

                    hostNameField.setText(dataContainer.getHostname());

                    if (isHardware) {
                        if (dataContainer.ipv4 != null && dataContainer.ipv4.toString() != null) {
                            IPV4Field.setText(dataContainer.ipv4.toString());
                        }

                        if (dataContainer.ipv6 != null && dataContainer.ipv6.toString() != null) {
                            IPV6Field.setText(dataContainer.ipv6.toString());
                        }

                        if (dataContainer.username != null) {
                            SSHUsernameField.setText(dataContainer.username);
                        }

                        if (dataContainer.password != null) {
                            SSHPasswordField.setText(dataContainer.password);
                        }
                    }

                }

                if (isHardware) {
                    errorLabel = (Label) dialog.getChildren().get(10);
                    submit = (Button) dialog.getChildren().get(11);
                } else {
                    errorLabel = (Label) dialog.getChildren().get(2);
                    submit = (Button) dialog.getChildren().get(3);
                }

                submit.setOnAction(event -> {

                    if (hostNameField.getText().trim().isEmpty()) {
                        errorLabel.setText("Vul alstublieft een hostnaam in.");
                        errorLabel.setVisible(true);
                        return;
                    }

                    dataContainer.hostname = hostNameField.getText();

                    if(isHardware) {
                        dataContainer.setIpv4(IPV4Field.getText());
                        dataContainer.setIpv6(IPV6Field.getText());

                        dataContainer.setUser(SSHUsernameField.getText());
                        dataContainer.setPass(SSHPasswordField.getText());
                    }

                    pane.setUserData(dataContainer);

                    this.ok = true;
                    window.close();
                });

                window.setScene(new Scene(dialog));

            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

        }

        public Pane getPane() {
            return pane;
        }

        public Stage getWindow() {
            return window;
        }

        public Label getErrorLabel() {
            return errorLabel;
        }

        public TextField getHostNameField() {
            return hostNameField;
        }

        public TextField getIPV4Field() {
            return IPV4Field;
        }

        public TextField getIPV6Field() {
            return IPV6Field;
        }

        public TextField getSSHUsernameField() {
            return SSHUsernameField;
        }

        public TextField getSSHPasswordField() {
            return SSHPasswordField;
        }

        public Button getSubmit() {
            return submit;
        }

        public boolean isOk() {
            return ok;
        }

    }

}
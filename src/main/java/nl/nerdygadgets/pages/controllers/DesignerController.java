package nl.nerdygadgets.pages.controllers;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.main.NerdyGadgets;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
        PopupMenu inputMenu = new PopupMenu();
        String hostname = null;

        //Boolean to check if component is already in the layout
        boolean existenceCheck = false;

        //If the component does not exist in the layout, it will create a new instance of it
        if (!componentLayout.getChildren().contains(component)) {
            if (inputMenu.displayHostname()) {
                component = copyAttributes(component, inputMenu);
                componentLayout.getChildren().add(component);
            } else {
                return;
            }
        } else {
            if (attributeOK) {
                //TODO Add component attribute labels method
//                addComponentAttributesLabels(component);
                attributeOK = false;
            }

            existenceCheck = true;
        }

        double borderRight = componentLayout.getWidth();
        double borderBottom = componentLayout.getHeight();
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
        } else {
            addComponentToInfrastructure(component, inputMenu);
        }

    }

//    public void editLabels() {
//
//    }

    /**
     * @param component
     */
    private void editComponentObject(Pane component) {
        List<Component> components = Infrastructure.getCurrentInfrastructure().getComponents();

        for (Component x : components) {
            String hostname = getHostname(component);
            if (x.getHostname().equals(hostname)) {
                x.setX((int) component.getLayoutX());
                x.setY((int) component.getLayoutY());
                System.out.println("X and Y coordinates of host '" + hostname + "' are changed. X: " + x.getX() + " Y: " + x.getY());
            }
        }
    }


    /**
     * Adds a component to the infrastructure when it's placed in the layout.
     *
     * @param component
     */
    private void addComponentToInfrastructure(Pane component, PopupMenu inputMenu) {
        List<Component> infraComponents = Infrastructure.getCurrentInfrastructure().getComponents();

        String hostname = inputMenu.getHostname();
        try {
            Component componentObject = createComponentObject(component, inputMenu);
            infraComponents.add(componentObject);
            System.out.println("Component added to infrastructure. X and Y coordinates of host '" + hostname + "' are: X: " + componentObject.getX() + " Y: " + componentObject.getY());

        } catch (NoSuchMethodException | ClassNotFoundException e) {
            System.out.println("Creating component failed");
            e.printStackTrace();
        }
    }


    /**
     * Creates an object of a component when it's placed in the layout
     *
     * @param component
     * @param hostname
     * @return
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     */
    public Component createComponentObject(Pane component, PopupMenu inputMenu) throws NoSuchMethodException, ClassNotFoundException {
        Label labelType = (Label) component.getChildren().get(0);

        String type = labelType.getText();
        String fullClassPath = "nl.nerdygadgets.infrastructure.components." + type;

        String hostname = inputMenu.getHostname();
        int x = (int) component.getLayoutX();
        int y = (int) component.getLayoutY();

        String ipv4 = inputMenu.getIPv4();
        String ipv6 = inputMenu.getIPv6();
        String sshUser = inputMenu.getSSHUser();
        String sshPass = inputMenu.getSSHPass();

        Class<?> cls = Class.forName(fullClassPath);
        try {
            Component newComponent = (Component) cls.getConstructor(String.class, int.class, int.class).newInstance(hostname, x, y);
            newComponent.setIpv4(ipv4);
            newComponent.setIpv6(ipv6);
            newComponent.setUser(sshUser);
            newComponent.setPass(sshPass);
            System.out.println("New " + type + " created. \tIPv4: " + ipv4 + " \tIPv6: " + ipv6 + " \tUsername: " + sshUser + " \tPassword: " + sshPass);

            return newComponent;
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Copies the attributes from the component out of componentlist, and places it into a draggable pane with the createDraggablePane method.
     *
     * @param component
     * @param inputMenu
     * @return Pane component
     */
    private Pane copyAttributes(Pane component, PopupMenu inputMenu) {
        Pane draggablePane = createDraggablePane(component);
        VBox attributes = new VBox();

        //Copy icon to componentpane
        Label typeCopy = (Label) component.getChildren().get(1);
        Label componentType = new Label(typeCopy.getText());
        componentType.setVisible(false);

        Rectangle iconCopy = (Rectangle) component.getChildren().get(0);
        Rectangle componentIcon = new Rectangle(iconCopy.getHeight(), iconCopy.getWidth());

        Label name = new Label(inputMenu.getHostname());
        Label ipv4 = new Label(inputMenu.getIPv4());
        Label ipv6 = new Label(inputMenu.getIPv6());
        Label sshUser = new Label(inputMenu.getSSHUser());

        attributes.getChildren().add(componentIcon);
        attributes.getChildren().add(name);
        attributes.getChildren().add(ipv4);
        attributes.getChildren().add(ipv6);
        attributes.getChildren().add(sshUser);

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

        PopupMenu attributeDialog = new PopupMenu();
        draggableComponent.setOnMouseClicked(mouseEvent -> {
            attributeOK = attributeDialog.displayAttributes(component);
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
//
//    //TODO addComponentAttributeLabels
//    private void addComponentAttributesLabels(Pane component) {
//        VBox componentPane = (VBox) component.getChildren().get(1);
//    }

//    private String getComponentAttribute(String method, String hostname) {
//        List<Component> components = Infrastructure.getCurrentInfrastructure().getComponents();
//
//
//        for (Component x : components) {
//            //TODO Find a way to identify component in loop
////            if (x.getHostname().equals(hostname)) {
//            if (method.equals("getHostname")) {
//                return x.getHostname();
//            }
//            if (method.equals("getipv4")) {
//                //TODO Fix null
//                return String.valueOf(x.getIpv4());
//            }
//            if (method.equals("getipv6")) {
//                //TODO Fix null
//                return String.valueOf(x.getIpv6());
//            }
//            if (method.equals("getUser")) {
//                return x.getUser();
//            }
//            if (method.equals("getPass")) {
//                return x.getPass();
//            }
//        }
//
////        }
//        return "";
//    }
//
////    private void setComponentAttribute(String method, String attribute) {
////        List<Component> components = Infrastructure.getCurrentInfrastructure().getComponents();
////
//////            if (x.getHostname().equals(hostname)) {
////        if (method.equals("setHostname")) {
////            System.out.print(x.getHostname() + "\t Hostname changed to: ");
////            x.setHostname(attribute);
////            System.out.println(x.getHostname());
////        }
////        if (method.equals("setipv4")) {
////            x.setIpv4(attribute);
////            System.out.println(x.getHostname() + "\t IPv4: " + x.getIpv4());
////        }
////        if (method.equals("setipv6")) {
////
////            x.setIpv6(attribute);
////            System.out.println(x.getHostname() + "\t IPv6: " + x.getIpv6());
////        }
////        if (method.equals("setUser")) {
////            x.setUser(attribute);
////            System.out.println(x.getHostname() + "\t SSH Username: " + x.getUser());
////        }
////        if (method.equals("setPass")) {
////            x.setPass(attribute);
////            System.out.println(x.getHostname() + "\t SSH Password: " + x.getPass() + " (Don't tell anyone!)");
////        }
////    }
//    // }
//    //}

    public static Event getTransferEvent() {
        return transferEvent;
    }

    public static void setTransferEvent(Event event) {
        transferEvent = event;
    }

    public String getHostname(Pane component) {
        VBox componentBox = (VBox) component.getChildren().get(1);
        Label labelType = (Label) componentBox.getChildren().get(1);

        return labelType.getText();
    }

    /**
     * A class for the hostnameAlert box
     */
    class PopupMenu {
        private boolean isOk;
        private ArrayList componentAttributes = new ArrayList();

        TextField hostnameField;
        TextField ipv4Field;
        TextField ipv6Field;
        TextField sshUserField;
        TextField sshPassField;

        public void main(String[] args) {
            launch(args);
        }

        public boolean displayHostname() {
            Stage window = new Stage();
            window.initStyle(StageStyle.UTILITY);

            VBox hostnameDialog = null;
            try {
                hostnameDialog = FXMLLoader.load(getClass().getResource("/pages/components/AttributePopup.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            hostnameField = (TextField) hostnameDialog.getChildren().get(1);
            ipv4Field = (TextField) hostnameDialog.getChildren().get(3);
            ipv6Field = (TextField) hostnameDialog.getChildren().get(5);
            sshUserField = (TextField) hostnameDialog.getChildren().get(7);
            sshPassField = (TextField) hostnameDialog.getChildren().get(9);

            Label hostnameWarning = (Label) hostnameDialog.getChildren().get(10);
            Button okButton = (Button) hostnameDialog.getChildren().get(11);
            okButton.setDefaultButton(true);

            okButton.setOnAction(actionEvent -> {
                if (hostnameField.getText().isEmpty()) {
                    System.out.println("No hostname!");
                    hostnameWarning.setText("Geen hostnaam ingevuld!");
                    hostnameWarning.setVisible(true);
                    //TODO A check to see if the hostname is unique in the components list
                } else {
                    isOk = true;
                    window.close();
                }
            });

            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Hostname");
            window.setScene(new Scene(hostnameDialog));

            window.showAndWait();

            return isOk;
        }

//        private void componentAttributes(String hostname, String ipv4, String ipv6, String sshuser, String sshpass) {
//            componentAttributes = new ArrayList();
//
//            componentAttributes.add(hostname);
//            componentAttributes.add(ipv4);
//            componentAttributes.add(ipv6);
//            componentAttributes.add(sshuser);
//            componentAttributes.add(sshpass);
//
//        }

        private ArrayList transferComponentAttributes() {
            return componentAttributes;
        }

        public boolean displayAttributes(Pane component) {
            Stage window = new Stage();

            VBox attributeDialog = null;

            try {
                attributeDialog = FXMLLoader.load(getClass().getResource("/pages/components/AttributePopup.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            hostnameField = (TextField) attributeDialog.getChildren().get(1);

            //TODO Retrieve component attributes

            Button okButton = (Button) attributeDialog.getChildren().get(11);

            okButton.setOnAction(actionEvent -> {
                if (hostnameField.getText().isEmpty()) {
                    //TODO A check to see if the hostname is unique in the components list
                    System.out.println("No hostname");
                } else {
                    isOk = true;
                    window.close();
                }
            });

            window.setScene(new Scene(attributeDialog));

            window.showAndWait();

            return isOk;
        }

        public String getHostname() {
            return hostnameField.getText();
        }

        public String getIPv4() {
            return ipv4Field.getText();
        }

        public String getIPv6() {
            return ipv6Field.getText();
        }

        public String getSSHUser() {
            return sshUserField.getText();
        }

        public String getSSHPass() {
            return sshPassField.getText();
        }
    }
}
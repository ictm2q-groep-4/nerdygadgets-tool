package nl.nerdygadgets.pages.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import nl.nerdygadgets.algorithms.Backtracking;
import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.ComponentType;
import nl.nerdygadgets.main.NerdyGadgets;
import nl.nerdygadgets.pages.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lucas Ouwens
 * @author Joris Vos
 */
public class OptimizerController extends GenericController implements Controller {

    @FXML
    private AnchorPane selectedComponentContainer;

    private static List<AnchorPane> selectedComponents = new ArrayList<>();

    private static Infrastructure infrastructureToOptimize = new Infrastructure();

    private static Infrastructure newInfrastructure = new Infrastructure();

    /**
     * Handles the component in the select list being clicked
     *
     * @param e MouseEvent
     */
    public static void selectElement(MouseEvent e) {
        // We know that we're clicking on an anchor pane
        AnchorPane pane = (AnchorPane) e.getSource();
        toggleSelected(pane);
    }

    /**
     * Handles the 'Toevoegen' button
     * Adds all the selected elements to the 'gekozen elementen' and re-adds all components which should be in the list.
     *
     * @param e ActionEvent
     */
    @FXML
    public void addComponentsToList(ActionEvent e) {
        if (!this.reloadComponents()) {
            NerdyGadgets.showAlert("Er is een fout opgetreden!", "Er zijn geen componenten geselecteerd om toe te voegen.", Alert.AlertType.WARNING);
        }
        selectedComponents.clear();

    }

    /**
     * Handles the 'Alles deselecteren' button
     */
    @FXML
    private void deselectAllComponents() {
        if (!selectedComponents.isEmpty()) {
            // Set the background of each selected component back to white (from the original blue-ish color) and clear the selected components
            for (AnchorPane pane : selectedComponents) {
                pane.setStyle("-fx-background-color: #fff");
            }

            selectedComponents.clear();
        } else {
            NerdyGadgets.showAlert("Er is een fout opgetreden!", "Er zijn geen componenten geselecteerd.", Alert.AlertType.WARNING);
        }
    }

    /**
     * Handles the 'alles terugzetten' button
     */
    @FXML
    private void returnAllComponents() {
        if (!selectedComponentContainer.getChildren().isEmpty()) {
            // clear the infrastructure of all components and clear all the picked components.
            infrastructureToOptimize.getComponents().clear();
            selectedComponentContainer.getChildren().clear();
        } else {
            NerdyGadgets.showAlert("Er is een fout opgetreden!", "Er zijn geen componenten die teruggezet kunnen worden.", Alert.AlertType.WARNING);
        }
    }


    /**
     * Handles the 'geselecteerde terugzetten' button
     */
    @FXML
    private void returnSelectedComponents() {
        List<Node> toRemove = new ArrayList<>();
        if (!selectedComponentContainer.getChildren().isEmpty()) {
            for (Node node : selectedComponentContainer.getChildren()) {
                if (node.getStyle().equalsIgnoreCase("-fx-background-color: #4455ff")) {

                    // register the node for deletion.
                    toRemove.add(node);

                    // We don't need this component anymore, we remove it.
                    Component componentToRemove = (Component) node.getUserData();
                    infrastructureToOptimize.getComponents().remove(componentToRemove);
                } else {
                    selectedComponents.add((AnchorPane) node);
                }
            }
            selectedComponentContainer.getChildren().clear();
            this.reloadComponents();
        } else {
            // Show a warning if there's no components selected.
            NerdyGadgets.showAlert("Er is iets fout gegaan!", "Er zijn geen componenten geselecteerd om terug te zetten.", Alert.AlertType.WARNING);
        }
    }


    /**
     * Add an element to the 'selected' list if they aren't in it yet, remove it otherwise.
     *
     * @param pane AnchorPane the clicked pane.
     */
    private static void toggleSelected(AnchorPane pane, boolean registerInList) {
        if (pane.getStyle().contains("-fx-background-color: #4455ff")) {
            pane.setStyle("-fx-background-color: #fff");
            if (registerInList) {
                selectedComponents.remove(pane);
            }
        } else {
            pane.setStyle("-fx-background-color: #4455ff");
            if (registerInList) {
                selectedComponents.add(pane);
            }
        }
    }

    /**
     * Overloaded method for selection of pane event
     *
     * @param pane AnchorPAne
     */
    private static void toggleSelected(AnchorPane pane) {
        toggleSelected(pane, true);
    }


    public boolean reloadComponents() {
        if (!selectedComponents.isEmpty()) {
            List<Component> components = new ArrayList<>();
            for (AnchorPane pane : selectedComponents) {
                if (pane.getUserData() != null) {
                    components.add((Component) pane.getUserData());
                    pane.setStyle("-fx-background-color: #fff");
                }
            }
            selectedComponents.clear();

            try {
                this.loadComponents(selectedComponentContainer, (components.toArray(new Component[0])), null);

                // Add a new event listener for the 'picked' components
                selectedComponentContainer.getChildren().forEach((node) -> {
                    AnchorPane pane = (AnchorPane) node;
                    pane.setOnMouseClicked(ev -> toggleSelected(pane, false));
                });
                return true;
            } catch (IOException err) {
                err.printStackTrace();
            }
        }

        return false;
    }

    @FXML
    private void handleOptimizeButton() {
        infrastructureToOptimize.getComponents().clear();
        for (Node n : selectedComponentContainer.getChildren()) {
            AnchorPane pane = (AnchorPane) n;
            if (pane.getUserData() != null && Component.class.isAssignableFrom(pane.getUserData().getClass())) {
                Component component = (Component) n.getUserData();
                infrastructureToOptimize.getComponents().add(component);
            }
        }

        List<Component> webComponents = new ArrayList<>();
        List<Component> databaseComponents = new ArrayList<>();
        List<Component> otherComponents = new ArrayList<>();

        for (Component component : infrastructureToOptimize.getComponents()) {
            if (component.componentType.equals(ComponentType.DATABASESERVER)) {
                databaseComponents.add(component);
            } else if (component.componentType.equals(ComponentType.WEBSERVER)) {
                webComponents.add(component);
            } else {
                otherComponents.add(component);
            }
        }

        Backtracking backtracking = new Backtracking();

        backtracking.setAvailableDatabaseComponents(databaseComponents.toArray(Component[]::new));
        backtracking.setAvailableWebComponents(webComponents.toArray(Component[]::new));
        backtracking.setUsedOtherComponents(otherComponents.toArray(Component[]::new));

        if (backtracking.start()) {
            backtracking.printSolution();
            newInfrastructure.getComponents().clear();
            newInfrastructure.getComponents().addAll(backtracking.getAllComponents());
        } else {
            NerdyGadgets.showAlert("Backtracking ERROR", "An error occurred while optimizing the infrastructure. Probably because you didn't select at least 1 component of the types DATABASESERVER or WEBSERVER", Alert.AlertType.ERROR);
        }
    }

}

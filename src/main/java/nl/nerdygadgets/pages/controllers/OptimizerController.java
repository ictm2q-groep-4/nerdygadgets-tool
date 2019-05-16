package nl.nerdygadgets.pages.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import nl.nerdygadgets.algorithms.Backtracking;
import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.ComponentType;
import nl.nerdygadgets.main.NerdyGadgets;
import nl.nerdygadgets.pages.Controller;
import nl.nerdygadgets.pages.PageRegister;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static javafx.application.Application.launch;

/**
 * @author Lucas Ouwens
 * @author Joris Vos
 */
public class OptimizerController extends GenericController implements Controller {

    @FXML
    private AnchorPane selectedComponentContainer;

    @FXML
    private AnchorPane componentLayout;

    @FXML
    private TextField minimumAvailability;

    @FXML
    private TextArea backtrackingOutput;

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
            int counter = 0;
            for (Node node : selectedComponentContainer.getChildren()) {
                if (node.getStyle().equalsIgnoreCase("-fx-background-color: #4455ff")) {
                    counter++;
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
            if(counter == 0) {
                NerdyGadgets.showAlert("Er is iets fout gegaan!", "Er zijn geen componenten geselecteerd om terug te zetten.", Alert.AlertType.WARNING);
            }
        } else {
            // Show a warning if there's no components selected.
            NerdyGadgets.showAlert("Er is iets fout gegaan!", "Er zijn geen componenten in de 'gekozen componenten' lijst.", Alert.AlertType.WARNING);
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
        double minimumAvailability = 0;

        if (this.minimumAvailability.getText().length()<=0) {
            NerdyGadgets.showAlert("Optimizer: warning", "Er is geen beschikbaarheid ingevuld! Deze moet ingevuld zijn!\nDit kan links bovenaan. Bijvoorbeeld: 99.99", Alert.AlertType.WARNING);
            return;
        } else {
            try {
                minimumAvailability = Double.valueOf(this.minimumAvailability.getText());
            } catch (NumberFormatException e) {
                NerdyGadgets.showAlert("Optimizer: error", "Er is een error opgetreden tijdens het converteren van de String beschikbaarheid naar een double.\nDit betekend dat het geen geldige input is. Geldige input is bijvoorbeeld: 99.99", Alert.AlertType.ERROR);
                return;
            }
        }

        if (selectedComponentContainer.getChildren().size()<=0) {
            NerdyGadgets.showAlert("Optimizer: warning", "Er zijn geen componenten geselecteerd, kies minimaal 1 database server en 1 web server.\nAangezien deze nodig zijn voor het backtracking algoritme.", Alert.AlertType.WARNING);
            return;
        }

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
        backtracking.setMinimumAvailability(minimumAvailability);

        if (backtracking.start()) {
            totalAvailability.setText("Totale beschikbaarheid: "+new DecimalFormat("#.###").format(backtracking.getTotalAvailability()).replace(',', '.')+"%");
            totalCosts.setText("Totale kosten: €"+backtracking.getTotalPrice()+",-");
            totalConfigurationsTested.setText("Configuraties getest: "+backtracking.getConfigurationsTested());

            backtracking.printSolution();
            backtrackingOutput.setText(backtracking.getSolution());
            newInfrastructure.getComponents().clear();

            int minX = 50;
            int minY = 50;

            int maxX = (int)Math.round(componentLayout.getWidth()-50);
            int maxY = (int)Math.round(componentLayout.getHeight()-50);

            int currentX = minX;
            int currentY = minY;

            int i = 1;

            for (Component component : backtracking.getAllComponents()) {
                newInfrastructure.getComponents().add(new Component(component, component.name+"-"+(i++), currentX, currentY));

                currentX += 150;

                if (currentX >= maxX) {
                    currentY += 100;
                    currentX = minX;
                }

                if (currentY >= maxY) {
                    NerdyGadgets.showAlert("Backtracking ERROR", "Er zijn teveel componenten nodig voor de infrastructuur om te laten zien!", Alert.AlertType.ERROR);
                    return;
                }
            }

            OptimizerAlert optimizerAlert = new OptimizerAlert();
            ACTION action = optimizerAlert.display();

            if (action.equals(ACTION.CLOSE)) {
                return;
            } else if (action.equals(ACTION.SET)) {
                Infrastructure.setCurrentInfrastructure(newInfrastructure);
                NerdyGadgets.showAlert("Optimizer", "Het nieuwe ontwerp is geladen als het huidige ontwerp.\nAls je nu naar Monitor of Builder gaat en op open huidig ontwerp klikt word deze geladen.", Alert.AlertType.INFORMATION);
            } else if (action.equals(ACTION.SAVE)) {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML File", "*.xml");
                fileChooser.getExtensionFilters().add(extensionFilter);

                File filePath = fileChooser.showSaveDialog(NerdyGadgets.getNerdyGadgets().getStage());

                if (filePath != null && filePath.getName().endsWith(".xml")) {
                    try {
                        newInfrastructure.save(filePath.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                        NerdyGadgets.showAlert("Optimizer", "Er is een fout opgetreden tijdens het opslaan van de nieuwe infrastructuur.", Alert.AlertType.ERROR);
                    }
                } else {
                    NerdyGadgets.showAlert("Optimizer", "Het bestand moet een XML bestand zijn en dus eindigen op .xml", Alert.AlertType.ERROR);
                }
            }
        } else {
            NerdyGadgets.showAlert("Backtracking ERROR", "Er is een error opgetreden terwijl het backtracking algoritme gestart is.\nDit is mogelijk omdat je geen database server of web server geselecteerd hebt.\nVan beiden moet er minstens 1 geselecteerd zijn!", Alert.AlertType.ERROR);
        }
    }

    public enum ACTION {
        SAVE,
        SET,
        CLOSE,
    }

    class OptimizerAlert {
        private ACTION action = ACTION.CLOSE;

        public void main(String[] args) {
            launch(args);
        }

        public ACTION display() {
            Stage window = new Stage();
            window.initStyle(StageStyle.UTILITY);

            VBox optimizerDialog = null;
            try {
                optimizerDialog = FXMLLoader.load(getClass().getResource(PageRegister.get("OptimizerAlert").getFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            HBox buttonContainer = (HBox) optimizerDialog.getChildren().get(1);
            Button setCurrentInfrastructure = (Button) buttonContainer.getChildren().get(1);
            Button saveInfrastructure = (Button) buttonContainer.getChildren().get(0);
            Button cancelDialog = (Button) buttonContainer.getChildren().get(2);

            setCurrentInfrastructure.setOnAction(actionEvent -> {
                action = ACTION.SET;
                window.close();
            });
            saveInfrastructure.setOnAction(actionEvent -> {
                action = ACTION.SAVE;
                window.close();
            });
            cancelDialog.setOnAction(actionEvent -> {
                action = ACTION.CLOSE;
                window.close();
            });

            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Optimizer");
            window.setScene(new Scene(optimizerDialog));

            window.showAndWait();

            return action;
        }
    }
}

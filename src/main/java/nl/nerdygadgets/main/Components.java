package nl.nerdygadgets.main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import nl.nerdygadgets.infrastructure.Infrastructure;
import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.ComponentType;
import nl.nerdygadgets.infrastructure.design.DesignManager;
import nl.nerdygadgets.infrastructure.design.XMLImporter;
import nl.nerdygadgets.pages.PageRegister;
import nl.nerdygadgets.pages.controllers.OptimizerController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static javafx.application.Application.launch;

public class Components {
    private static Components instance;

    public static Components instance() {
        if (instance == null) {
            instance = new Components();
        }
        return instance;
    }

    private static Map<String, Component> allComponents;

    private Components() {
        ComponentsAlert componentsAlert = new ComponentsAlert();
        componentsAlert.display();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecteer een xml bestand dat alle componenten bevat.");
        File componentsFile = fileChooser.showOpenDialog(NerdyGadgets.getNerdyGadgets().getStage());

        allComponents = new HashMap<>();

        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(componentsFile.getAbsolutePath());
            document.getDocumentElement().normalize();
            NodeList nodes = document.getElementsByTagName("components").item(0).getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {
                String name;
                double availability;
                int price;
                ComponentType type;

                Node node = nodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    if (element.getTagName() != null) {
                        name = element.getElementsByTagName("name").item(0).getTextContent();
                        availability = Double.valueOf(element.getElementsByTagName("availability").item(0).getTextContent());
                        price = Integer.valueOf(element.getElementsByTagName("price").item(0).getTextContent());
                        type = ComponentType.valueOf(element.getElementsByTagName("type").item(0).getTextContent());

                        allComponents.putIfAbsent(name, new Component(name, availability, price, type));
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Component[] getAllComponents() { return allComponents.values().toArray(Component[]::new); }
    public static Component getComponent(String componentName) { return allComponents.get(componentName); }

    class ComponentsAlert {
        public void main(String[] args) {
            launch(args);
        }

        public void display() {
            Stage window = new Stage();
            window.initStyle(StageStyle.UTILITY);

            VBox componentsDialog = null;
            try {
                componentsDialog = FXMLLoader.load(getClass().getResource(PageRegister.get("ComponentsAlert").getFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Label label = (Label) componentsDialog.getChildren().get(0);
            Button button = (Button) componentsDialog.getChildren().get(1);

            label.setText("Je moet in het volgende scherm een .xml bestand selecteren wat alle componenten bevat.");
            button.setOnAction(actionEvent -> {
                window.close();
            });

            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Optimizer");
            window.setScene(new Scene(componentsDialog));

            window.showAndWait();
        }
    }
}

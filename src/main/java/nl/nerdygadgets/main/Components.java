package nl.nerdygadgets.main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.ComponentType;
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
import java.io.IOException;
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

    private Components(String filePath) {


        allComponents = new HashMap<>();

        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filePath);
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
                componentsDialog = FXMLLoader.load(getClass().getResource(PageRegister.get("OptimizerAlert").getFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            HBox buttonContainer = (HBox) optimizerDialog.getChildren().get(1);
            Button setCurrentInfrastructure = (Button) buttonContainer.getChildren().get(1);
            Button saveInfrastructure = (Button) buttonContainer.getChildren().get(0);
            Button cancelDialog = (Button) buttonContainer.getChildren().get(2);

            setCurrentInfrastructure.setOnAction(actionEvent -> {
                action = OptimizerController.ACTION.SET;
                window.close();
            });
            saveInfrastructure.setOnAction(actionEvent -> {
                action = OptimizerController.ACTION.SAVE;
                window.close();
            });
            cancelDialog.setOnAction(actionEvent -> {
                action = OptimizerController.ACTION.CLOSE;
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

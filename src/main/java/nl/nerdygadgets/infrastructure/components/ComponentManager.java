package nl.nerdygadgets.infrastructure.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import nl.nerdygadgets.main.NerdyGadgets;
import nl.nerdygadgets.pages.PageRegister;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static javafx.application.Application.launch;

/**
 * This class loads all the necesary components.
 *
 * @author Joris Vos
 */
public class ComponentManager {
    /**
     * This holds the 1 instance of this class that is initialized.
     */
    private static ComponentManager instance;

    /**
     * This returns the instance of ComponentManager
     *
     * @return  ComponentManager
     */
    public static ComponentManager instance() {
        if (instance == null) {
            instance = new ComponentManager();
        }
        return instance;
    }

    /**
     * This contains all the components loaded from a xml file
     */
    private static Map<String, Component> allComponents;

    /**
     * The default constructor. This is called once, and initializes all the components
     */
    private ComponentManager() {
        ComponentsAlert componentsAlert = new ComponentsAlert();
        componentsAlert.display();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecteer een xml bestand dat alle componenten bevat.");
        File componentsFile = fileChooser.showOpenDialog(NerdyGadgets.getNerdyGadgets().getStage());

        //Loads the components file instantaneous. For testing purposes
        //File componentsFile = new File("Components.xml");

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

    /**
     * Returns all the components as an array
     *
     * @return  Component[]
     */
    public static Component[] getAllComponents() { return allComponents.values().toArray(Component[]::new); }

    /**
     * Returns a component by a name
     *
     * @param componentName String
     * @return              Component
     */
    public static Component getComponent(String componentName) { return allComponents.get(componentName); }

    /**
     * This is the alert box
     */
    class ComponentsAlert {
        /**
         * This is called when initializing the alert box
         *
         * @param args  String[]
         */
        public void main(String[] args) {
            launch(args);
        }

        /**
         * This displays the alertbox
         */
        public void display() {
            Stage window = new Stage();
            window.initStyle(StageStyle.UTILITY);

            VBox componentsDialog = null;
            try {
                componentsDialog = FXMLLoader.load(getClass().getResource(PageRegister.get("ComponentsAlert").getFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Button button = (Button) componentsDialog.getChildren().get(1);
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

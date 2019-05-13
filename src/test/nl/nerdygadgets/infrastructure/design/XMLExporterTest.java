package nl.nerdygadgets.infrastructure.design;

import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.HAL9001DB;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XMLExporterTest {
    List<Component> components = new ArrayList<>();
    String filePath;

    @Test
    void getXMLExporterInstance() {
    }

    @Test
    void exportXML() {
        filePath = "D:\\Java\\nerdygadgets-tool\\src\\main\\resources\\test.xml";

        //Test
        Component c1 = new HAL9001DB(5, 8);
        Component c2 = new HAL9001DB(5, 8);

        components.add(c1);
        components.add(c2);


        setComponents(components);

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = documentFactory.newDocumentBuilder();
//            newXMLExport(documentFactory, builder);
            Document document = builder.newDocument();

            this.addElements(document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();


            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(filePath));

            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();

        }

    }


    /**
     * Adds elements and its values to the XML file
     */
    private void addElements(Document document) {
        // Root element
        Element root = document.createElement("root");
        document.appendChild(root);


        // Component elements
        for (int i = 0; i < components.size(); i++) {

            Element component = document.createElement(components.get(i).getClass().getSimpleName());
            root.appendChild(component);

            Element x = document.createElement("x");
            component.appendChild(x);
            x.appendChild(document.createTextNode(String.valueOf(components.get(i).getX())));

            Element y = document.createElement("y");
            component.appendChild(y);
            y.appendChild(document.createTextNode(String.valueOf(components.get(i).getY())));
        }
    }

    private void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private void setComponents(List<Component> components) {
        this.components = components;
    }
}

package nl.nerdygadgets.infrastructure.design;

import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.HAL9001DB;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A Singleton manager for the design import/export (for infrastructure)
 *
 * @author Stefan Booij
 */

class XMLExporter {
    /**
     * A variable which holds our *only* instance of this class.
     */
    private static XMLExporter XMLExporterInstance;

    private String filePath;
    private List<Component> components;

    /**
     * A private constructor to block anything outside from making a new instance of this class.
     */
    private XMLExporter() {
        //
    }

    /**
     * Get access to the Singleton XMLExporter class.
     *
     * @return XMLExporterInstance
     */
    public static XMLExporter getXMLExporterInstance() {
        if (XMLExporterInstance == null) {
            XMLExporterInstance = new XMLExporter();
        }
        return XMLExporterInstance;
    }

    /**
     * Checks if the given filepath parameter exists. Based on that it will call one of two methods: NewXMLExport or ExistingXMLExport.
     */
    boolean exportXML(String filePath, List<Component> components) {
        setFilePath(filePath);
        setComponents(components);

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = null;

        try {
            builder = documentFactory.newDocumentBuilder();
            Document doc = builder.parse(filePath);
            newXMLExport(documentFactory, builder);
        } catch (FileNotFoundException e){
            newXMLExport(documentFactory, builder);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Will create a new XML file
     */

    public void newXMLExport(DocumentBuilderFactory documentFactory, DocumentBuilder builder) {
        Document document = builder.newDocument();

        this.addElements(document);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(filePath));

            transformer.transform(domSource, streamResult);

        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    /**
     * Adds elements and its values to the XML file
     */
    public void addElements(Document document) {
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

    public void setComponents(List<Component> components) {
        this.components = components;
    }
}
package nl.nerdygadgets.infrastructure.design;

import nl.nerdygadgets.infrastructure.components.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
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

    /**
     * Contains the path to the location where file will be saved
     */
    private String filePath;

    /**
     * Contains the components that were added to the design
     */
    private List<Component> components;

    /**
     * A private constructor to block anything outside from making a new instance of this class.
     */
    private XMLExporter() {}

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
     * Creates a new XML files to the given filepath
     *
     * @param filePath
     * @param components
     * @return boolean
     */
    public boolean exportXML(String filePath, List<Component> components) {
        setFilePath(filePath);
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
            return false;
        }
        return true;
    }


    /**
     * Adds elements and its values to the XML file
     *
     * @param document
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
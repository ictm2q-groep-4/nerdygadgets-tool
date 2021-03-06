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
 * @author Lou Elzer
 * @author Joris Vos
 */

public class XMLExporter {
    /**
     * A variable which holds our *only* instance of this class.
     */
    private static XMLExporter XMLExporterInstance;

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
     * @param filePath      String
     * @param components    List<ComponentManager>
     * @return boolean
     */
    public boolean exportComponents(String filePath, List<Component> components) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentFactory.newDocumentBuilder();
            Document document = builder.newDocument();

            addElements(document, components);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(filePath));

            transformer.transform(domSource, streamResult);
        } catch (ParserConfigurationException | TransformerException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Adds elements and its values to the XML file
     *
     * @param document      Document
     * @param components    List<Component>
     */
    private void addElements(Document document, List<Component> components) {
        // Root element
        Element root = document.createElement("design");
        document.appendChild(root);

        // Component elements
        for (Component component : components) {
            // Create component node
            Element componentNode = document.createElement(component.name);
            root.appendChild(componentNode);

            // Create component child nodes
            Element hostname = document.createElement("hostname");
            componentNode.appendChild(hostname);
            hostname.appendChild(document.createTextNode(component.hostname));

            Element x = document.createElement("x");
            componentNode.appendChild(x);
            x.appendChild(document.createTextNode(String.valueOf(component.x)));

            Element y = document.createElement("y");
            componentNode.appendChild(y);
            y.appendChild(document.createTextNode(String.valueOf(component.y)));

            if (component.componentType.equals(ComponentType.DATABASESERVER) || component.componentType.equals(ComponentType.WEBSERVER)) {
                Element ipv4 = document.createElement("ipv4");
                componentNode.appendChild(ipv4);
                ipv4.appendChild(document.createTextNode(component.ipv4.getHostAddress()));

                Element ipv6 = document.createElement("ipv6");
                componentNode.appendChild(ipv6);
                ipv6.appendChild(document.createTextNode(component.ipv6.getHostAddress()));

                Element sshUsername = document.createElement("sshusername");
                componentNode.appendChild(sshUsername);
                sshUsername.appendChild(document.createTextNode(component.username));

                Element sshPassword = document.createElement("sshpassword");
                componentNode.appendChild(sshPassword);
                sshPassword.appendChild(document.createTextNode(component.password));
            }
        }
    }
}
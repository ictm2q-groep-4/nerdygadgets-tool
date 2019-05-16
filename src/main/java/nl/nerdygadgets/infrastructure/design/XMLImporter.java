package nl.nerdygadgets.infrastructure.design;

import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.ComponentType;
import nl.nerdygadgets.main.Components;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 *  A Singleton class that parses XML files into infrastructure designs
 *
 * @author Lucas Ouwens
 * @author Lou Elzer
 * @author Joris Vos
 */
public class XMLImporter {

    /**
     * Variable which holds our only instance of this class
     */
    private static XMLImporter XMLImporterInstance;

    /**
     *  Private constructor to prevent object creation from outside the class
     */
    private XMLImporter () {}

    /**
     * Get access to the XMLImporter class
     *
     * @return      XMLImporter returns the XMLImporter object
     */
    public static XMLImporter getXMLImporter () {
        if (XMLImporterInstance == null) {
            XMLImporterInstance = new XMLImporter();
        }
        return XMLImporterInstance;
    }

    /**
     * Calls ImportFile() and turns the document into a Component ArrayList.
     * This returns an ArrayList filled with Component objects
     *
     * @return List<Component>
     */
    public List<Component> importComponents(String filePath) {
        // import XML file
        Document file = importFile(filePath);
        // create node list
        NodeList nodes;

        // try to fill the NodeList with all component tags
        try {
            nodes = file.getElementsByTagName("design").item(0).getChildNodes();
        } catch (NullPointerException e) {
            return null;
        }

        // init new ArrayList for the component objects
        List<Component> components = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i ++) {
            String hostname;
            int x;
            int y;
            String sshUsername;
            String sshPassword;
            String ipv4;
            String ipv6;

            Node node = nodes.item(i);

            // if node is an element node
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                // Cast node to element type
                Element element = (Element) node;

                // parse the element values
                if (element.getTagName() != null) {
                    Component component = Components.getComponent(element.getTagName());
                    hostname = element.getElementsByTagName("hostname").item(0).getTextContent();
                    x = Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent());
                    y = Integer.parseInt(element.getElementsByTagName("y").item(0).getTextContent());

                    if (component.componentType.equals(ComponentType.DBLOADBALANCER) || component.componentType.equals(ComponentType.FIREWALL)) {
                        components.add(createDeviceObject(component, hostname, x, y));
                    } else {
                        sshUsername = element.getElementsByTagName("sshusername").item(0).getTextContent();
                        sshPassword = element.getElementsByTagName("sshpassword").item(0).getTextContent();
                        ipv4 = element.getElementsByTagName("ipv4").item(0).getTextContent();
                        ipv6 = element.getElementsByTagName("ipv6").item(0).getTextContent();

                        // create objects and add them to components
                        components.add(createDeviceObject(component, hostname, x, y, sshUsername, sshPassword, ipv4, ipv6));
                    }
                }
            }
        }
        return components;
    }

    /**
     * Opens an XML file and turns it into a document
     *
     * @return Document
     */
    private Document importFile (String filePath) {
        try {
            // create factory and builder objects
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // parse the XML file
            Document doc = dBuilder.parse(filePath);

            // normalize
            doc.getDocumentElement().normalize();

            // return the document
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Component createDeviceObject(Component component, String hostname, int x, int y) {
        Component _component = new Component(component);
        _component.hostname = hostname;
        _component.x = x;
        _component.y = y;

        return _component;
    }

    private Component createDeviceObject(Component component, String hostname, int x, int y, String sshUsername, String sshPassword, String ipv4, String ipv6) {
        try {
            Component _component = new Component(component);
            _component.hostname = hostname;
            _component.x = x;
            _component.y = y;

            _component.username = sshUsername;
            _component.password = sshPassword;
            _component.ipv4 = InetAddress.getByName(ipv4);
            _component.ipv6 = InetAddress.getByName(ipv6);

            return _component;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }
}

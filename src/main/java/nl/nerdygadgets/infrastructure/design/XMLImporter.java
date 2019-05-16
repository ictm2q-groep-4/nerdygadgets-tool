package nl.nerdygadgets.infrastructure.design;

import nl.nerdygadgets.infrastructure.components.Component;
import org.w3c.dom.*;
import javax.xml.parsers.*;
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
            String type;
            int x;
            int y;
            String ipv4;
            String ipv6;
            String sshUsername;
            String sshPassword;

            Node node = nodes.item(i);

            // if node is an element node
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                // Cast node to element type
                Element element = (Element) node;

                // parse the element values
                if (element.getTagName() != null) {
                    type = element.getTagName();
                    hostname = element.getElementsByTagName("name").item(0).getTextContent();
                    x = Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent());
                    y = Integer.parseInt(element.getElementsByTagName("y").item(0).getTextContent());

                    if (type.equals("DBLoadBalancer") || type.equals("pfSense")) {
                        components.add(createDeviceObject(type, hostname, x, y));
                    } else {
                        ipv4 = element.getElementsByTagName("ipv4").item(0).getTextContent();
                        ipv6 = element.getElementsByTagName("ipv6").item(0).getTextContent();
                        sshUsername = element.getElementsByTagName("sshusername").item(0).getTextContent();
                        sshPassword = element.getElementsByTagName("sshpassword").item(0).getTextContent();

                        // create objects and add them to components
                        components.add(createDeviceObject(type, hostname, x, y, sshUsername, sshPassword, ipv4, ipv6));
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

    /**
     *  Method that creates the component objects
     *
     * @param       type the device type and class name
     * @param       hostname the device name
     * @param       x the x coordinate of the device in the designer
     * @param       y the y coordinate of the device in the designer
     * @param       sshUsername the ssh username of the server
     * @param       sshPassword the ssh password of the server
     * @param       ipv4 the ipv4 of the server
     * @param       ipv6 the ipv6 of the server
     * @return      Component returns a component object of the right type
     */
    private Component createDeviceObject(String type, String hostname, int x, int y, String sshUsername, String sshPassword, String ipv4, String ipv6) {
        String fullClassPath = "nl.nerdygadgets.infrastructure.components." + type;

        try {
            Class<?> cls = Class.forName(fullClassPath);
            return (Component) cls.getConstructor(String.class, int.class, int.class, String.class, String.class, String.class, String.class).newInstance(hostname, x, y, sshUsername, sshPassword, ipv4, ipv6);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Method that creates the component objects
     *
     * @param       type the device type and class name
     * @param       hostname the device name
     * @param       x the x coordinate of the device in the designer
     * @param       y the y coordinate of the device in the designer
     * @return      Component returns a component object of the right type
     */
    private Component createDeviceObject(String type, String hostname, int x, int y) {
        String fullClassPath = "nl.nerdygadgets.infrastructure.components." + type;

        try {
            Class<?> cls = Class.forName(fullClassPath);
            return (Component) cls.getConstructor(String.class, int.class, int.class).newInstance(hostname, x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

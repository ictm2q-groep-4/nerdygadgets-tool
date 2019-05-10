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
 */
public class XMLImporter {

    /**
     * Variable which holds our only instance of this class
     */
    private static XMLImporter XMLImporterInstance;

    /**
     * The path to the XML file
     */
    private String path;

    /**
     *  Private constructor to prevent object creation from outside the class
     */
    private XMLImporter () {
        //
    }

    /**
     * Opens an XML file and turns it into a document
     *
     * @return Document
     */
    private Document importFile () {
        try {
            // create factory and builder objects
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // parse the XML file
            Document doc = dBuilder.parse(path);

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
     * Calls ImportFile() and turns the document into a Component ArrayList
     *
     * @return List<Component> returns an ArrayList filled with Component objects
     */
    public List<Component> getComponents (String path) {
        XMLImporterInstance.setPath(path);
        // import XML file
        Document file = importFile();
        // create nodelist
        NodeList nodes;

        // try to fill the NodeList with all component tags
        try {
            nodes = file.getElementsByTagName("design").item(0).getChildNodes();
        } catch (Exception e) {
            return null;
        }
        // init new ArrayList for the component objects
        ArrayList<Component> components = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i ++) {
            String hostname = null;
            String type = null;
            int x = 0;
            int y = 0;

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

                    // create objects and add them to components
                    components.add(createDeviceObject(type, hostname, x, y));
                }
            }
        }

        return components;
    }

    /**
     *  Method that creates the component objects
     *
     * @param       type the device type and class name
     * @param       hostname the device name
     * @param       x the x coordinate of the device in the designer
     * @param       y the y coordinate of the device in the designer
     * @return      Component returns a component object of the right type
     */
    private Component createDeviceObject (String type, String hostname, int x, int y) {
        String fullClassPath = "nl.nerdygadgets.infrastructure.components." + type;

        try {
            Class<?> cls = Class.forName(fullClassPath);
            return (Component) cls.getConstructor(String.class, int.class, int.class).newInstance(hostname, x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sets the XML file path
     *
     * @param path the path to the XML file
     */
    private void setPath (String path) {
        this.path = path;
    }

    /**
     * Get access to the XMLImporter class
     *
     * @param       path the path to the XML file
     * @return      XMLImporter returns the XMLImporter object
     */
    public static XMLImporter getXMLImporter () {
        if (XMLImporterInstance == null) {
            XMLImporterInstance = new XMLImporter();
        }
        return XMLImporterInstance;
    }
}

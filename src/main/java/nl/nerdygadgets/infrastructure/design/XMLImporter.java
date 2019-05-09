package nl.nerdygadgets.infrastructure.design;

import nl.nerdygadgets.infrastructure.components.Component;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.util.ArrayList;

/**
 *  A Singleton class that parses XML files into infrastructure designs
 *
 * @author Lou Elzer
 */

public class XMLImporter {
    // Variable which holds our only instance of this class
    private static XMLImporter XMLImporterInstance;
    // The path to the XML file
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
     * @return returns a document object
     */
    private Document ImportFile () {
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
     * @return returns an ArrayList filled with Component objects
     */
    public ArrayList<Component> getComponents () {
        // import XML file
        Document file = ImportFile();
        // create NodeList with all component tags
        NodeList nodes = file.getElementsByTagName("root").item(0).getChildNodes();
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
                    components.add(CreateDeviceObject(type, hostname, x, y));
                }
            }

            // create new component object
            //components.add(new Component (name, x, y));

//            System.out.println(type);
//            System.out.println(name);
//            System.out.println(x);
//            System.out.println(y);
//            System.out.println("--------------------------");
        }

        return components;
    }

    /**
     *  Method that creates the component objects
     *
     * @param type the device type and class name
     * @param name the device name
     * @param x the x coordinate of the device in the designer
     * @param y the y coordinate of the device in the designer
     * @return returns a component object of the right type
     */
    private Component CreateDeviceObject (String type, String hostname, int x, int y) {
        String fullClassPath = "nl.nerdygadgets.infrastructure.components." + type;

        try {
            Class<?> cls = Class.forName(fullClassPath);
            Component component = (Component) cls.getConstructor(String.class, int.class, int.class).newInstance(hostname, x, y);
            return component;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setPath (String path) {
        this.path = path;
    }

    public static XMLImporter getXMLImporter () {
        if (XMLImporterInstance == null) {
            XMLImporterInstance = new XMLImporter();
        }

        return XMLImporterInstance;
    }



}

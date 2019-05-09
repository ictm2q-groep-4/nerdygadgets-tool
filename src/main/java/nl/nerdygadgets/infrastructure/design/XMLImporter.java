package nl.nerdygadgets.infrastructure.design;

import nl.nerdygadgets.infrastructure.components.Component;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
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

            // parse the XML file and return the document
            return dBuilder.parse(path);

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
        NodeList nodes = file.getElementsByTagName("component");
        // init new ArrayList for the component objects
        ArrayList<Component> components = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i ++) {
            String name = null;
            int x = 0;
            int y = 0;

            Node node = nodes.item(i);

            // if node is an element node
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                // Cast node to element type
                Element element = (Element) node;

                // parse the element values
                name = element.getElementsByTagName("name").item(0).getTextContent();
                x = Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent());
                y = Integer.parseInt(element.getElementsByTagName("y").item(0).getTextContent());
            }

            // create new component object
            //components.add(new Component (name, x, y));

            System.out.println("name: " + name);
            System.out.println("x: " + x);
            System.out.println("y: " + y);
            System.out.println("---------------------------------");
        }

        return components;
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

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
    void XMLExport(String filePath, List<Component> components) {
        setFilePath("D:\\Java\\nerdygadgets-tool\\src\\main\\resources\\test.xml");
        setComponents(components);

        //test code
        HAL9001DB c1 = new HAL9001DB(5, 7);
        HAL9001DB c2 = new HAL9001DB(8, 3);
        HAL9001DB c6 = new HAL9001DB(8, 10);
        components = new ArrayList<>();
        components.add(c1);
        components.add(c2);
        components.add(c6);
        //test code

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = documentFactory.newDocumentBuilder();
            try {
                Document doc = builder.parse(filePath);
                ExistingXMLExport(doc, documentFactory, builder); //This will be called when the filepath is found.
            } catch (FileNotFoundException e) {
                NewXMLExport(documentFactory, builder);     //This will be called when the filepath is not found.
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will create a new XML file
     */

    public void NewXMLExport(DocumentBuilderFactory documentFactory, DocumentBuilder builder) {
        Document document = builder.newDocument();

        this.addElements(document);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(filePath));

            transformer.transform(domSource, streamResult);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    /**
     * Will update an existing XML file
     */

    public void ExistingXMLExport(Document doc, DocumentBuilderFactory documentFactory, DocumentBuilder builder) {
        NodeList componentList = doc.getElementsByTagName("component");

        //Creates a list of components to save to the existing XML file
        List<Component> toAddComponentList = checkForDuplicateCoordinates(componentList);

        for (Component component : toAddComponentList) {
            System.out.println(component.getClass().getSimpleName());
            System.out.println(component.getX());
            System.out.println(component.getY());
        }

    }
    /**
     * Compares the existing XML file against the component list for any duplicate coordinate entries
     *
     * @return toAddComponentList
     */
    public List<Component> checkForDuplicateCoordinates(NodeList componentList) {
        boolean foundDuplicateX;
        boolean foundDuplicateY;

        //A list of component with no duplicate coordinates
        List<Component> toAddComponentList = new ArrayList<>();

        //Loop through given components to save
        for (int i = 0; i < components.size(); i++) {
            foundDuplicateX = false;
            foundDuplicateY = false;

            //Loop through given XML file
            for (int j = 0; j < componentList.getLength(); j++) {
                //Create a list of component elements
                Node component = componentList.item(j);
                //Get childnodes from component element
                NodeList coordinateList = component.getChildNodes();
                //Loop through childnodes
                for (int k = 0; k < coordinateList.getLength(); k++) {
                    Node c = coordinateList.item(k);
                    if (c.getNodeType() == Node.ELEMENT_NODE) {
                        System.out.println(c.getNodeName());
                        //Check for duplicate coordinates
                        if ((c.getNodeName() == "x" && components.get(i).getX() == Integer.parseInt(c.getTextContent()))) {
                            foundDuplicateX = true;
                        }
                        if (c.getNodeName() == "y" && components.get(i).getY() == Integer.parseInt(c.getTextContent())) {
                            foundDuplicateY = true;
                        }
                    }


                }
            }
            //Adds components with no duplicate coordinates to the list
            if (!foundDuplicateX && !foundDuplicateY) {
                System.out.println(foundDuplicateX);
                System.out.println(foundDuplicateY);
                toAddComponentList.add(components.get(i));
            }
        }

        return toAddComponentList;
    }

    /**
     * Adds elements to an XML file
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
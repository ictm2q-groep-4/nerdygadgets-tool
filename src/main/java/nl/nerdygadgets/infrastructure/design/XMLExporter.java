package nl.nerdygadgets.infrastructure.design;

import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.HAL9001DB;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XMLExporter {
    private String filePath;
    private List<Component> components;



    public XMLExporter(String filePath, List<Component> components){
        this.filePath = filePath;
        this.components = components;
    }

    /**
     * Checks if the given filepath parameter exists. Based on that it will call one of two methods: NewXMLExport or ExistingXMLExport.
     */
    void XMLExport() {
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

        this.addElements(components, document);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(System.out);//(new File(filePath));
            transformer.transform(domSource, streamResult);
            System.out.println("Succes!");
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
        System.out.println("oh whaddup");
    }


    /**
     * Adds elements to the XML file
     */
    public void addElements(List<Component> components, Document document){
        // Root element
        Element root = document.createElement("root");
        document.appendChild(root);


        // Component elements
        for (int i = 0; i < components.size(); i++) {

            Element component = document.createElement("component");
            root.appendChild(component);

            Element name = document.createElement("name");
            component.appendChild(name);
            name.appendChild(document.createTextNode(components.get(i).getClass().getSimpleName()));

            Element x = document.createElement("x");
            component.appendChild(x);
            x.appendChild(document.createTextNode(String.valueOf(components.get(i).getX())));

            Element y = document.createElement("y");
            component.appendChild(y);
            y.appendChild(document.createTextNode(String.valueOf(components.get(i).getY())));
        }
    }
}
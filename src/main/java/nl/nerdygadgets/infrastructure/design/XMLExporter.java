package nl.nerdygadgets.infrastructure.design;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;



public class XMLExporter {

    static void XMLExporterr() {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // Root element
            Element root = document.createElement("root");
            document.appendChild(root);

            // Component element
            Element component = document.createElement("Component");
            root.appendChild(component);



        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }



    }

}

package at.jku.evaluation.protospace.fileloader.UML;


import at.jku.evaluation.protospace.fileloader.UML.model.ModelConstant;
import at.jku.evaluation.protospace.fileloader.UML.model.ModelElement;
import at.jku.evaluation.protospace.fileloader.UML.model.ModelView;
import at.jku.evaluation.protospace.fileloader.UML.model.UMLModel;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.text.html.Option;
import javax.xml.parsers.*;
import java.io.*;
import java.util.Optional;


public class UMLFileLoader {

    boolean printModel = false;
    int artifactIndex;
    UMLModel umlModel;

    public UMLModel loadFile(String filename) throws ParserConfigurationException, IOException, SAXException
    {

        umlModel = new UMLModel();
        File fXmlFile = new File("src/main/resources/evaluationData/"+filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        Element umlPackage = doc.getDocumentElement();
        //System.out.println("------- Loading UML File: "+umlPackage.getAttribute("name")+"-------");

           NodeList viewList = umlPackage.getChildNodes();

           for(int i = 0; i<viewList.getLength(); i++)
           {
               Node node = viewList.item(i);
               //System.out.println(i+": "+node.getNodeName());

               NamedNodeMap attributes = node.getAttributes();
               if(attributes!=null)
               {
                   Node nameAttribute  = attributes.getNamedItem("name");
                   if(nameAttribute!=null)
                   {
                       switch(nameAttribute.getNodeValue())
                       {
                           case "Use Case View":
                               loadUseCaseView(node);
                               break;
                           case "Logical View":
                               loadLogicalView(node);
                               break;
                           case "Component View":
                               loadComponentView(node);
                               break;
                           case "Deployment View":
                               loadDeploymentViewView(node);
                               break;
                       }
                   }
               }
           }
        return umlModel;
    }


    private void loadUseCaseView(Node root)
    {
        //System.out.println("Loading UseCase View");
        NodeList directDescendants = root.getChildNodes();

        for (int i = 0; i < directDescendants.getLength(); i++) {
            Node currentPackagedElement = directDescendants.item(i);
            if(!skipableElement(currentPackagedElement))
            {
                digElement(currentPackagedElement, 0, null, ModelView.USECASE);
            }
        }



    }

    private void loadLogicalView(Node root)
    {
        //System.out.println("Loading Logical View");
        NodeList directDescendants = root.getChildNodes();

        for (int i = 0; i < directDescendants.getLength(); i++) {
            Node currentPackagedElement = directDescendants.item(i);
            if(!skipableElement(currentPackagedElement))
            {
                digElement(currentPackagedElement, 0, null, ModelView.LOGICAL);
            }
        }

    }

    private void loadComponentView(Node root) {
        //System.out.println("Loading Component View");
        NodeList directDescendants = root.getChildNodes();

        for (int i = 0; i < directDescendants.getLength(); i++) {
            Node currentPackagedElement = directDescendants.item(i);
            if(!skipableElement(currentPackagedElement))
            {
                digElement(currentPackagedElement, 0, null, ModelView.COMPONENT);
            }
        }
    }

    private void loadDeploymentViewView(Node root)
    {
        //System.out.println("Loading Deployment View");
        NodeList directDescendants = root.getChildNodes();

        for (int i = 0; i < directDescendants.getLength(); i++) {
            Node currentPackagedElement = directDescendants.item(i);
            if(!skipableElement(currentPackagedElement))
            {
                digElement(currentPackagedElement, 0, null, ModelView.DEPLOYMENT);
            }
        }

    }


    private ModelElement createModelElement(Node element, ModelView view, ModelElement parent)
    {
        ModelElement me = new ModelElement(artifactIndex, "", "", "", parent );

        artifactIndex = artifactIndex + 1;
        //Other fields
        NamedNodeMap elementAttributes = element.getAttributes();
        for (int i = 0; i < elementAttributes.getLength() ; i++) {
            Node item = elementAttributes.item(i);
            me.addProperty(item.getNodeName(), item.getNodeValue());
        }

        if(me.getProperty("xmi:type").isEmpty()||element.getNodeName().startsWith("owned")||element.getNodeName().equals("lifeline"))
        {
            me.addProperty("xmi:type", element.getNodeName());
        }

        if(printModel) {
            System.out.println(element.getNodeName() + ": " + me.getProperties());
        }
        umlModel.addModelElement(me, view);
        return me;
    }

    private void digElement(Node element, int level, ModelElement parent, ModelView view)
    {
        if(printModel)
        {
            for (int i = 0; i <level ; i++) {
                System.out.print("-");
            }
        }


        ModelElement me = createModelElement(element, view, parent);
        if(parent!=null)
        {
            parent.addOwnedElement(element.getNodeName(), me);
        }

        NodeList childElements = element.getChildNodes();
        for (int i = 0; i < childElements.getLength(); i++) {
            if(!skipableElement(childElements.item(i)))
            {
                digElement(childElements.item(i), level+1, me ,view);
            }
        }


    }


    private boolean skipableElement(Node item) {

        return item.getNodeName().equals("#text") || item.getNodeName().equals("body");
    }

    private String getAttributeValue(Node n, String name)
    {
        NamedNodeMap attributes = n.getAttributes();

        Node attribute = attributes.getNamedItem(name);

        if(attribute!=null)
        {
            return attribute.getNodeValue();
        }
        return "";
    }

}

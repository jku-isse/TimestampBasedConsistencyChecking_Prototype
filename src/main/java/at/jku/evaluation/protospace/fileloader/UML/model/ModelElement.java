package at.jku.evaluation.protospace.fileloader.UML.model;

import at.jku.evaluation.protospace.core.model.ArtifactType;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelElement {


    int artifactID;
    ArtifactType artifactType;
    HashMap<String, String> properties;
    HashMap<String, ArrayList<ModelElement>> ownedElements;
    ModelElement parent;

    private ModelElement(){};

    public ModelElement(int id, String xmiType, String xmiID, String name, ModelElement parent)
    {
        this.artifactID = id;
        properties = new HashMap<String, String>();
        ownedElements = new HashMap<String, ArrayList<ModelElement>>();
        this.parent = parent;
        properties.put("xmi:id", xmiID);
        properties.put("xmi:type", xmiType);
        parseType(xmiType);
        properties.put("name", name);
    }


    public ModelElement getParent(){
        return this.parent;
    }

    public String getXmiID()
    {
        return properties.get("xmi:id");
    }

    public String getXmiType()
    {
        return properties.get("xmi:type");
    }

    public String getName()
    {
        return properties.get("name");
    }

    public void addProperty(String key, String value)
    {
        properties.put(key, value);
        if(key.equals("xmi:type"))
        {
            parseType(value);
        }
    }

    private void parseType(String value) {
        switch (value)
        {
            case "lifeline":
                artifactType=ArtifactType.LIFELINE;
                break;
            case "uml:Class":
                artifactType=ArtifactType.CLASS;
                break;
            case "transition":
                artifactType=ArtifactType.TRANSITION;
                break;
            case "message":
                artifactType=ArtifactType.MESSAGE;
                break;
            case "ownedOperation":
                artifactType=ArtifactType.OPERATION;
                break;
            case "uml:Interface":
                artifactType=ArtifactType.INTERFACE;
                break;
            case "uml:Collaboration":
                artifactType=ArtifactType.COLLABORATION;
                break;
            default:
                artifactType=ArtifactType.GENERAL;
        }

    }

    public String getProperty(String key)
    {
        return properties.get(key);
    }

    public HashMap<String, String> getProperties()
    {
        return properties;
    }


    public void addOwnedElement(String key, ModelElement value)
    {
        ArrayList<ModelElement> linkedElements = ownedElements.get(key);

        if(linkedElements==null)
        {
            linkedElements = new ArrayList<>();
            ownedElements.put(key, linkedElements);
        }
        linkedElements.add(value);

    }

    public String getOwnedElements(String key)
    {
        return properties.get(key);
    }

    public HashMap<String, ArrayList<ModelElement>> getOwnedElements()
    {
        return ownedElements;
    }

    public int getAssignedID()
    {
        return artifactID;
    }

    public ArtifactType getArtifactType() {
        return artifactType;
    }
}

package at.jku.evaluation.protospace.core.model;

import at.jku.evaluation.protospace.core.model.consistencychecker.ConsistencyRuleEvaluation;
import java.util.ArrayList;
import java.util.HashMap;


public class Artifact {



    int artifactID;
    ArtifactType artifactType;
    HashMap<String, Property> properties;
    ArrayList<ConsistencyRuleEvaluation> belongingScopes;

    public Artifact(int id, ArtifactType type)
    {
        this.artifactID = id;
        this.artifactType = type;
        this.properties = new HashMap<>();
        this.belongingScopes = new ArrayList<>();
    }

    public void setProperty(String key, String val, boolean listItem, long timestamp) {
        Property property = properties.get(key);
        if(property!=null) {
            if(listItem)
            {
                property.setValue(property.getLastValue(Long.MAX_VALUE)+""+val, timestamp);
            }else{
                property.setValue(""+val, timestamp); //deep copy of string object necessary?
            }
        }
        else{
            Property newProperty = new Property(key);
            newProperty.setValue(""+val, timestamp);
            properties.put(key, newProperty);

        }
    }


    public void merge(Artifact otherArtifact, long timestamp) {

        HashMap<String, Property> otherArtifactProperties = otherArtifact.getProperties();
        for(String otherPropertyKey : otherArtifactProperties.keySet())
        {
            setProperty(otherArtifactProperties.get(otherPropertyKey).getKey(),
                        otherArtifactProperties.get(otherPropertyKey).getLastValue(Long.MAX_VALUE), false, timestamp);
        }
    }

    public HashMap<String, Property> getProperties()
    {
        return properties;
    }

    @Override
    public String toString() {
        return "ARTIFACT ["+artifactID+"] / TYPE ["+artifactType+"]: "+properties.toString();
    }

    public ArtifactType getArtifactType() {
        return artifactType;
    }

    public int getArtifactID() {
        return artifactID;
    }

    public Property getProperty(String key) {
        return properties.get(key);
    }

    public ArrayList<ConsistencyRuleEvaluation> getBelongingScopes()
    {
        return belongingScopes;
    }

    public void addBelongingScope(ConsistencyRuleEvaluation cre)
    {
        if(!belongingScopes.contains(cre))
        {
            belongingScopes.add(cre);
        }
    }

    public ArrayList<Integer> getReferences(String referencePropertyName)
    {
        ArrayList<Integer> referenceList = new ArrayList<>();
        Property references = getProperty(referencePropertyName);

        if(references!=null)
        {
            String allReferences = references.getLastValue(Long.MAX_VALUE);
            String[] splitReferences = allReferences.split("_@ref");
            //System.out.println(allReferences);
            for(int i = 0; i<splitReferences.length; i++)
            {

                //System.out.println(splitReferences[i]);
                if(!splitReferences[i].equals(""))
                {
                    referenceList.add(Integer.parseInt(splitReferences[i]));
                }
            }

        }

        return referenceList;
    }
}

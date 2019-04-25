package at.jku.evaluation.protospace.core.model;

import at.jku.evaluation.protospace.core.communication.ChangeEvent;
import at.jku.evaluation.protospace.core.communication.ChangeEventType;
import at.jku.evaluation.protospace.core.communication.EventBus;
import at.jku.evaluation.protospace.core.model.consistencychecker.ConsistencyRuleEvaluation;
import at.jku.evaluation.protospace.core.service.consistencychecker.ConsistencyCheckerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Workspace {

    long timestamp;
    boolean timestampActive;
    int workspaceID;
    int accessCount;
    ConsistencyCheckerService cc;
    Workspace parent;
    HashMap<Integer, Artifact> artifactStorage;
    HashMap<ArtifactType, ArrayList<Integer>> typeMap;
    EventBus eventBus;

    public Workspace(int wsid, Workspace parent, EventBus eventBus, ConsistencyCheckerService cc)
    {
        this.workspaceID = wsid;
        this.parent = parent;
        this.eventBus = eventBus;
        this.timestamp = System.currentTimeMillis();
        artifactStorage = new HashMap<>();
        typeMap = new HashMap<>();
        timestampActive = false;
        this.cc = cc;
    }

    public int getAccessCount()
    {
        return accessCount;
    }

    public void toggleTimestamp()
    {
        timestampActive = !timestampActive;
        System.out.println("Workspace ["+workspaceID+"] timestamp activity set: "+timestampActive);
    }

    public void createArtifact(int id, ArtifactType type)
    {
        Artifact newArtifact = new Artifact(id, type);
        artifactStorage.put(id, newArtifact);
        addToTypeMap(id, type);

        eventBus.fireEvent(new ChangeEvent(workspaceID, id, ChangeEventType.CREATE, "", ""));
    }

    private void addToTypeMap(int id, ArtifactType type)
    {
        if(typeMap.containsKey(type))
        {
            ArrayList<Integer> ids = typeMap.get(type);
            ids.add(id);
        }else{
            ArrayList<Integer> newTypeList = new ArrayList<Integer>();
            newTypeList.add(id);
            typeMap.put(type, newTypeList);
        }
    }

    public void updateProperty(int id, String key, String val, boolean listItem) {
        Artifact storedArtifact = artifactStorage.get(id);
        if (storedArtifact != null)
        {
            storedArtifact.setProperty(key, val, listItem, timestamp);
            eventBus.fireEvent(new ChangeEvent(workspaceID, id, ChangeEventType.UPDATE, key, val));
        }
        else
        {
            ///System.err.println("Artifact ["+id+"] not found! (UPDATE: ["+key+"//"+val+"])");
        }
    }

    public void commitWorkspace()
    {
        //System.out.println("============== COMMIT ================");
        cc.fullReEvaluation(this);

        if(parent!=null)
        {
            parent.mergeArtifactStorage(artifactStorage);
            artifactStorage.clear();
            typeMap.clear();
        }
        else
        {
            System.err.println("Could not commit! (Parent null // perhaps Public Workspace?)");
        }

    }

    public void mergeArtifactStorage(HashMap<Integer, Artifact> otherArtifactStorage)
    {
        long mergerTimestamp = timestamp;
        if(parent == null)
        {
            mergerTimestamp = System.currentTimeMillis();
        }

        for(Integer id : otherArtifactStorage.keySet())
        {
            Artifact otherArtifact = otherArtifactStorage.get(id);
            Artifact storedArtifact = artifactStorage.get(id);
                    if(storedArtifact!=null)
                    {
                        storedArtifact.merge(otherArtifact, mergerTimestamp);
                    }
            artifactStorage.put(id, otherArtifact);
            addToTypeMap(id, otherArtifact.getArtifactType());
        }
    }

    public Integer getWSID() {
        return workspaceID;
    }

    public void printFullWorkspaceContent() {
        System.out.println("========CONTENT WS ["+getWSID()+"]========");
        for(int key : artifactStorage.keySet())
        {
            Artifact a = artifactStorage.get(key);
            if(a.getArtifactType()==ArtifactType.CRE)
            {
                if(a.getProperty("result").getLastValue(Long.MAX_VALUE).equals("true"))
                {
                    System.out.println(a);
                }else{
                    System.err.println(a);
                }
            }else{
                System.out.println(a);
            }

        }


    }

    public Artifact getArtifact(int artifactID) {
        //only in context of this workspace!!
        return artifactStorage.get(artifactID);
    }

    public void appendArtifact(Artifact a)
    {
        artifactStorage.put(a.getArtifactID(), a);
        addToTypeMap(a.getArtifactID(), a.getArtifactType());
    }


    public ArrayList<Artifact> getArtifacts(ArrayList<Integer> referencesList) {
        ArrayList<Artifact> artifactList = new ArrayList<>();

        for (Integer i : referencesList)
        {
            Artifact a = getArtifact(i);
            if(a!=null)
            {
                artifactList.add(a);
            }
        }
        return artifactList;
    }

    public String getPropertyValue(Integer artifactID, String propertyName, long timestamp)
    {

        long retrievalTimestamp = timestamp;
        if(!timestampActive)
        {
            retrievalTimestamp = Long.MAX_VALUE;
        }

        String result = null;
        //find property in hierarchy (artifact search history)
        Property p = findProperty(artifactID, propertyName);

        if(p!=null)
        {
            result = p.getLastValue(retrievalTimestamp);
        }

        return result;
    }

    protected Property findProperty(Integer artifactID, String propertyName) {
        //System.out.println("trying to find id/property "+artifactID+"/"+propertyName+ " in WS: "+workspaceID);
        //get artifact from this workspace
        accessCount++;
        Artifact a = getArtifact(artifactID);
        Property p = null;
        //if artifact is not there look in parent
        if(a==null)
        {
             if(parent != null)
             {
                p = parent.findProperty(artifactID, propertyName);
             }
             if(p == null)
             {
                 //System.err.println("Couldn't find artifact ["+artifactID+"] in WS ["+getWSID()+"] (findProperty: "+propertyName+")");
                 return null;
             }else{
                 return  p;
             }
        }


        //if artifact is there try retrieving property
        p = a.getProperty(propertyName);

        //if property is not there look in parent
        if(p == null)
        {
            if(parent != null)
            {
                p = parent.findProperty(artifactID,propertyName);
            }

            if(p == null){
                //System.err.println("Couldn't find property ["+propertyName+"] in WS ["+getWSID()+"]");
                return null;
            }else{
                return  p;
            }
        }

        //if property is there return
        return p;

    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void printWorkspaceInfo(){
        System.out.println("Workspace ["+getWSID()+"]: {Accessed: "+accessCount+"} "+" {Artifacts: "+artifactStorage.keySet().size()+"}");
    }

    public ArrayList<Integer> getArtifactsOfType(ArtifactType type) {
        ArrayList<Integer> artifactIDs = typeMap.get(type);
        if(artifactIDs==null)
        {
            return new ArrayList<>();
        }

        return artifactIDs;
    }

    public HashMap<ArtifactType, ArrayList<Integer>> getTypeMap() {
        return typeMap;
    }
}

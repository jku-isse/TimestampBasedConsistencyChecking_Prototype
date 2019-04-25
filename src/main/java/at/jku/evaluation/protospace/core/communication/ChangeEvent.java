package at.jku.evaluation.protospace.core.communication;

public class ChangeEvent {


    int workspaceID;
    int artifactID;
    ChangeEventType eventType;
    String key;
    String value;

    public ChangeEvent(int workspaceID, int artifactID, ChangeEventType eventType, String key, String value) {
        this.workspaceID = workspaceID;
        this.artifactID = artifactID;
        this.eventType = eventType;
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Artifact ["+artifactID+"]; ChangeType ["+eventType+"]; Key/Value ["+key+"/"+value+"]; Workspace ["+workspaceID+"])";
    }

    public int getWorkspaceID() {
        return workspaceID;
    }

    public int getArtifactID() {
        return artifactID;
    }

    public ChangeEventType getEventType() {
        return eventType;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

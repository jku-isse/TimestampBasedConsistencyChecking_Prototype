package at.jku.evaluation.protospace.user;

import at.jku.evaluation.protospace.core.model.ArtifactType;

public class UserCommand {


    int artifactID;
    UserCommandType commandType;
    ArtifactType artifactType;
    String key;
    String value;

    public UserCommand(int artifactID, UserCommandType commandType, ArtifactType type, String key, String value){
        this.artifactID = artifactID;
        this.commandType = commandType;
        this.artifactType = type;
        this.key = key;
        this.value = value;
    }

    public UserCommandType getCommandType()
    {
        return commandType;
    }

    public String getKey()
    {
        return this.key;
    }

    public String getValue()
    {
        return this.value;
    }

    public int getID()
    {
        return this.artifactID;
    }

    @Override
    public String toString() {
        return commandType.toString()+" ["+artifactID+"]:  "+key+" // "+value + " -- "+artifactType;
    }

    public void setValue(String value) {
        this.value = value;

    }

    public ArtifactType getArtifactType() {
        return artifactType;

    }
}

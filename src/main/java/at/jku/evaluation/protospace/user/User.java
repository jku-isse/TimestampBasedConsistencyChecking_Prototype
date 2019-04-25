package at.jku.evaluation.protospace.user;

import at.jku.evaluation.protospace.core.model.Artifact;
import at.jku.evaluation.protospace.core.model.Workspace;
import at.jku.evaluation.protospace.fileloader.UML.model.ModelElement;
import com.sun.corba.se.spi.orbutil.threadpool.Work;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;


/*
Issues a command to Protospace
 */
public class User {

    String name;
    ArrayList<UserCommand> commands;
    Workspace assignedWorkspace;
    int numCREATES;
    int numUPDATES;

    //ProtoWorkspace workspace;


    public User(String name)
    {
        this.name = name;
        commands = new ArrayList<UserCommand>();
    }

    public User(String name, ArrayList<UserCommand> commands)
    {
        this.name = name;
        this.commands = commands;
    }

    public void executeAllCommands()
    {
        while(!commands.isEmpty()) {
            UserCommand currentCommand = commands.get(0);
            executeCommand(currentCommand);
            commands.remove(0);
        }
    }

    public void executeCommandsTillCommit()
    {

        while(!commands.isEmpty()) {
            UserCommand currentCommand = commands.get(0);
            executeCommand(currentCommand);
            if(currentCommand.getCommandType()==UserCommandType.COMMIT)
            {
                commands.remove(0);
                break;
            }
            commands.remove(0);
        }
    }

    private void executeCommand(UserCommand currentCommand) {
        if(assignedWorkspace!=null)
        {
            switch (currentCommand.getCommandType()) {
                case CREATE:
                    assignedWorkspace.createArtifact(currentCommand.getID(), currentCommand.getArtifactType());
                    break;
                case UPDATE:
                    assignedWorkspace.updateProperty(currentCommand.getID(), currentCommand.getKey(), currentCommand.getValue(), false);
                    break;
                case UPDATELIST:
                    assignedWorkspace.updateProperty(currentCommand.getID(), currentCommand.getKey(), "_"+currentCommand.getValue(), true);
                    break;
                case COMMIT:
                    assignedWorkspace.commitWorkspace();
                    break;
            }

        }else{
            System.err.println("No workspace assigned to "+name+"!");
        }

    }

    public void assignCommand(UserCommand command) {
        commands.add(command);
    }

    public void assignCommand(ModelElement me)
    {
        commands.add(new UserCommand(me.getAssignedID(), UserCommandType.CREATE, me.getArtifactType(), "", ""));
        numCREATES++;
        HashMap<String, String> properties = me.getProperties();
        for(String k : properties.keySet())
        {
            commands.add(new UserCommand(me.getAssignedID(), UserCommandType.UPDATE, me.getArtifactType(), k, properties.get(k)));
            numUPDATES++;
        }

        HashMap<String, ArrayList<ModelElement>> ownedElements = me.getOwnedElements();
        for(String k : ownedElements.keySet())
        {
            ArrayList<ModelElement> elementList = ownedElements.get(k);
            for(ModelElement m : elementList)
            {
                commands.add(new UserCommand(me.getAssignedID(), UserCommandType.UPDATELIST, me.getArtifactType(), m.getXmiType(), m.getXmiID()));
                numUPDATES++;
            }
        }

        if(me.getParent()!=null)
        {
            commands.add(new UserCommand(me.getAssignedID(), UserCommandType.UPDATE, me.getArtifactType(), "parentModelElement", me.getParent().getXmiID()));
            numUPDATES++;
        }

    }

    public void sortCommandHierarchy()
    {
        //sort
        HashMap<Integer, ArrayList<UserCommand>> commandMap = new HashMap<>();

        for (UserCommand uc : commands)
        {
            ArrayList<UserCommand> idCommands = commandMap.get(uc.getID());
            if(idCommands==null)
            {
                idCommands = new ArrayList<>();
                idCommands.add(uc);
                commandMap.put(uc.getID(), idCommands);
            }else
            {
                idCommands.add(uc);
            }
        }

        ArrayList<UserCommand> sortedCommands = new ArrayList<>();
        for(int i =0; i<commandMap.size();i++) {
            ArrayList<UserCommand> commandListForID = commandMap.get(i);
            if(commandListForID==null)
            {
                System.out.println("Something went wrong! No command list for id "+i);
            }
            sortedCommands.addAll(commandListForID);
        }

        commands=sortedCommands;
    }

    public ArrayList<UserCommand> getCommands()
    {
        return commands;
    }


    public int getUpdateCount()
    {
        return numUPDATES;
    }

    public int getCreateCount()
    {
        return numCREATES;
    }

    public void countCommands() {
        System.out.println("User "+name+" has "+commands.size()+" commands! [CREATE: "+numCREATES+" || UPDATE: "+numUPDATES+"]");
    }

    public void assignWorkspace(Workspace ws)
    {
        assignedWorkspace = ws;
    }

    public void printCommands() {
        for(UserCommand uc : commands)
        {
            System.out.println(uc);
        }
    }

    public void setCommands(ArrayList<UserCommand> commands) {
        this.commands = commands;
    }


}

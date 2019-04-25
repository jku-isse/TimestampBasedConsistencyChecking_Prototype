package at.jku.evaluation.protospace.utils;

import at.jku.evaluation.protospace.user.User;
import at.jku.evaluation.protospace.user.UserCommand;
import at.jku.evaluation.protospace.user.UserCommandType;

import java.util.ArrayList;
import java.util.HashMap;

public class UserUtils {

    public static void fixUMLReferencesOnCommands(User u)
    {
        HashMap<String, Integer> idMapping = new HashMap<>();
        fixUMLRefRun(u, idMapping, "xmi:id");       //first gather all XMI:IDs

        //||c.getKey().equals("association")||c.getKey().equals("ownedAttribute")||c.getKey().equals("ownedEnd")
        //                    ||c.getKey().equals("end")||c.getKey().equals("message")||c.getKey().equals("lifeline")
        fixUMLRefRun(u, idMapping, "ownedAttribute"); //then assign
        fixUMLRefRun(u, idMapping, "ownedOperation"); //then assign
        fixUMLRefRun(u, idMapping, "ownedParameter"); //and so on...
        fixUMLRefRun(u, idMapping, "generalization");
        fixUMLRefRun(u, idMapping, "ownedBehavior");
        fixUMLRefRun(u, idMapping, "region");
        fixUMLRefRun(u, idMapping, "transition");
        fixUMLRefRun(u, idMapping, "source");
        fixUMLRefRun(u, idMapping, "target");
        fixUMLRefRun(u, idMapping, "type");
        fixUMLRefRun(u, idMapping, "parentModelElement");
        fixUMLRefRun(u, idMapping, "receiveEvent");
        fixUMLRefRun(u, idMapping, "covered");
        fixUMLRefRun(u, idMapping, "represents");
        //rewrite: First gather all xmi:ids, then assign!
    }

    private static void fixUMLRefRun(User u, HashMap<String, Integer> idMapping, String tag)
    {
        ArrayList<UserCommand> commands = u.getCommands();
        for(UserCommand c : commands)
        {
            if((c.getKey().equals(tag))
                    &&!c.getKey().startsWith("@ref"))
            {

                if(idMapping.containsKey(c.getValue()))
                {
                    c.setValue("@ref"+idMapping.get(c.getValue()));
                }else{
                        idMapping.put(c.getValue(), c.getID());
                        c.setValue("@ref"+c.getID());
                }
            }
        }
    }


    public static void sortReferenceCommandsToEnd(User u) {
        ArrayList<UserCommand> commands = u.getCommands();
        ArrayList<UserCommand> listCommands = new ArrayList<>();

        for(int i = 0; i<commands.size(); i++)
        {
            UserCommand c = commands.get(i);
            if(c.getCommandType()==UserCommandType.UPDATELIST)
            {
                listCommands.add(c);
            }

        }


        for(UserCommand uc: listCommands)
        {
            commands.remove(uc);
        }

        commands.addAll(listCommands);

        u.setCommands(commands);

    }
}

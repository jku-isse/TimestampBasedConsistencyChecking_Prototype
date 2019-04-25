package at.jku.evaluation.protospace.starter;

import at.jku.evaluation.protospace.core.ProtoSpace;
import at.jku.evaluation.protospace.core.model.ArtifactType;
import at.jku.evaluation.protospace.core.model.Workspace;
import at.jku.evaluation.protospace.fileloader.UML.UMLFileLoader;
import at.jku.evaluation.protospace.fileloader.UML.model.ModelElement;
import at.jku.evaluation.protospace.fileloader.UML.model.UMLModel;
import at.jku.evaluation.protospace.user.User;
import at.jku.evaluation.protospace.user.UserCommand;
import at.jku.evaluation.protospace.user.UserCommandType;
import at.jku.evaluation.protospace.utils.EvaluationResult;
import at.jku.evaluation.protospace.utils.UserUtils;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class EvaluationStarter {

    static ProtoSpace protoSpace;


    public static void main(String[] args) {


        ArrayList<EvaluationResult> results = new ArrayList<>();


        results.add(evaluate("VOD Repair_Paper.uml"));
        results.add(evaluate("ATM.uml"));
        results.add(evaluate("Curriculum Planner.uml"));
        results.add(evaluate("Dice3.uml"));
        results.add(evaluate("Home Appliance Control System.uml"));
        results.add(evaluate("iTalks.uml"));
        results.add(evaluate("Hotel Management System.uml"));
        results.add(evaluate("Calendarium2.1.uml"));
        results.add(evaluate("dSpace3.2.uml"));
        results.add(evaluate("Word Pad.uml"));


        for (EvaluationResult r: results
             ) {
            System.out.println(r);

        }

        System.out.println();
        System.out.println();

        int i = 1;
        for (EvaluationResult r: results
        ) {
            System.out.println(r.toLatexTable(i));
            i++;

        }

    }

    private static EvaluationResult evaluate(String filename) {
        int modelElements=0;
        int creationCommands;
        int updateCommands;
        int isolatedAccesses;
        int integratedAccesses;
        int evaluations;
        float meanTime;

        //SET UP PROTOSPACE
        protoSpace = new ProtoSpace();
        protoSpace.startService();


        //LOAD FILE
        UMLFileLoader umlFileLoader = new UMLFileLoader();
        UMLModel umlModel = null;
        try {
            umlModel = umlFileLoader.loadFile(filename);
            modelElements = umlModel.getNumberOfModelElements();
            //System.out.println("Number of Elements: "+modelElements);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        //CREATE USERS
        User userAlice = new User("Alice");
        //User userBob = new User("Bob");

        //create workspaces
        Workspace ws1 = protoSpace.createNewWorkspace();
        //Workspace ws2 = protoSpace.createNewWorkspace();

        //ws1.toggleTimestamp();

        userAlice.assignWorkspace(ws1);
        //userBob.assignWorkspace(ws2);

        //ASSIGN COMMANDS
        assignCommands(umlModel, userAlice);
        //userAlice.printCommands();

        creationCommands = userAlice.getCreateCount();
        updateCommands = userAlice.getUpdateCount();


        //EXECUTE
        userAlice.executeAllCommands();
        //userBob.executeAllCommands();
        //protoSpace.printServiceInfo();
        //protoSpace.resetServiceData();

        //assignCommands(umlModel, userAlice);
        //userAlice.executeAllCommands();

        isolatedAccesses = protoSpace.getWorkspace(1).getAccessCount();
        integratedAccesses = protoSpace.getWorkspace(0).getAccessCount();
        evaluations = protoSpace.getService().getReevaluationCount();
        meanTime = protoSpace.getService().getMeanEvaluationTime();
        //protoSpace.printWorkspace(1, false); //print Alice's WS
        //protoSpace.printWorkspace(0, false); //print public
        //protoSpace.printServiceInfo();


        return new EvaluationResult(filename, modelElements, creationCommands, updateCommands, isolatedAccesses, integratedAccesses, evaluations, meanTime);
    }

    private static void assignCommands(UMLModel umlModel, User user) {
        ArrayList<ModelElement> modelElements = umlModel.getModelElements();
//        HashMap<String, ModelElement> useCaseView = umlModel.getUseCaseView();
//        HashMap<String, ModelElement> componentView = umlModel.getComponentView();
//        HashMap<String, ModelElement> deploymentView = umlModel.getDeploymentView();
//        HashMap<String, ModelElement> logicalView = umlModel.getLogicalView();
//
//        for(String k : useCaseView.keySet())
//        {
//            //System.out.println(useCaseView.get(k).getAssignedID());
//            user.assignCommand(useCaseView.get(k));
//        }
//
//        for(String k : componentView.keySet())
//        {
//            //System.out.println(componentView.get(k).getAssignedID());
//            user.assignCommand(componentView.get(k));
//        }
//
//        for(String k : deploymentView.keySet())
//        {
//            //System.out.println(deploymentView.get(k).getAssignedID());
//            user.assignCommand(deploymentView.get(k));
//        }
//
//        for(String k : logicalView.keySet())
//        {
//            //System.out.println(logicalView.get(k).getAssignedID());
//            user.assignCommand(logicalView.get(k));
//        }

        for(int i=0; i<modelElements.size();i++)
        {
            user.assignCommand(modelElements.get(i));
        }

        //.out.println("----------------------------------------------");
        //POST ASSIGNMENT FIXUP

        UserUtils.fixUMLReferencesOnCommands(user);
        user.sortCommandHierarchy();
        //UserUtils.sortReferenceCommandsToEnd(user);
        user.assignCommand(new UserCommand(-1, UserCommandType.COMMIT, ArtifactType.GENERAL, "",""));
        //user.countCommands();

    }

}

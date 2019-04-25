package at.jku.evaluation.protospace.core;

import at.jku.evaluation.protospace.core.communication.EventBus;
import at.jku.evaluation.protospace.core.model.ArtifactType;
import at.jku.evaluation.protospace.core.model.Workspace;
import at.jku.evaluation.protospace.core.model.consistencychecker.ConsistencyRule;
import at.jku.evaluation.protospace.core.service.consistencychecker.ConsistencyCheckerService;
import java.util.HashMap;

public class ProtoSpace {

    Workspace publicArea;
    HashMap<Integer, Workspace> workspaces;
    EventBus eventBus;
    ConsistencyCheckerService consistencyChecker;

    int workspaceIndex;

    public ProtoSpace()
    {
        workspaces = new HashMap<>();
        eventBus = new EventBus();

        consistencyChecker = new ConsistencyCheckerService(this, eventBus);

        publicArea = new Workspace(workspaceIndex++, null, eventBus, consistencyChecker);
        workspaces.put(publicArea.getWSID(), publicArea);
    }

    public void startService()
    {

        consistencyChecker.createDefinition(ConsistencyRule.CR01, ArtifactType.LIFELINE);
        consistencyChecker.createDefinition(ConsistencyRule.CR02, ArtifactType.TRANSITION);
        consistencyChecker.createDefinition(ConsistencyRule.CR03, ArtifactType.TRANSITION);
        consistencyChecker.createDefinition(ConsistencyRule.CR04, ArtifactType.MESSAGE);
        consistencyChecker.createDefinition(ConsistencyRule.CR05, ArtifactType.OPERATION);
        consistencyChecker.createDefinition(ConsistencyRule.CR06, ArtifactType.OPERATION);
        consistencyChecker.createDefinition(ConsistencyRule.CR07, ArtifactType.INTERFACE);
        consistencyChecker.createDefinition(ConsistencyRule.CR08, ArtifactType.INTERFACE);
        consistencyChecker.createDefinition(ConsistencyRule.CR09, ArtifactType.CLASS);
        consistencyChecker.createDefinition(ConsistencyRule.CR10, ArtifactType.CLASS);

    }

    public void printServiceInfo()
    {
        consistencyChecker.printServiceInfo();
    }

    public void resetServiceData() {
        consistencyChecker.resetServiceData();
    }

    public Workspace createNewWorkspace() {
        Workspace newWorkspace = new Workspace(workspaceIndex++, publicArea, eventBus, consistencyChecker);
        workspaces.put(newWorkspace.getWSID(), newWorkspace);

        return newWorkspace;
    }

    public Workspace getWorkspace(int id)
    {
        return workspaces.get(id);
    }

    public void printWorkspace(int wsid, boolean printContent) {
        System.out.println("---------------------- Workspace ["+wsid+"] ----------------------");
        workspaces.get(wsid).printWorkspaceInfo();
        if(printContent)
        {
            workspaces.get(wsid).printFullWorkspaceContent();
        }

    }


    public ConsistencyCheckerService getService() {
        return consistencyChecker;
    }


}

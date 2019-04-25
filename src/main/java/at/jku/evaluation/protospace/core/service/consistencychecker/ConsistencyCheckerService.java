package at.jku.evaluation.protospace.core.service.consistencychecker;

import at.jku.evaluation.protospace.core.ProtoSpace;
import at.jku.evaluation.protospace.core.communication.ChangeEvent;
import at.jku.evaluation.protospace.core.communication.EventBus;
import at.jku.evaluation.protospace.core.model.Artifact;
import at.jku.evaluation.protospace.core.model.ArtifactType;
import at.jku.evaluation.protospace.core.model.Workspace;
import at.jku.evaluation.protospace.core.model.consistencychecker.ConsistencyRule;
import at.jku.evaluation.protospace.core.model.consistencychecker.ConsistencyRuleDefinition;
import at.jku.evaluation.protospace.core.model.consistencychecker.ConsistencyRuleEvaluation;
import at.jku.evaluation.protospace.core.service.ProtoSpaceService;

import java.util.ArrayList;

public class ConsistencyCheckerService extends ProtoSpaceService {

    ArrayList<ConsistencyRuleDefinition> definitions;
    int reevaluations;
    int falseEvaluations;
    int trueEvaluations;
    float totalEvaluationTime;

    public ConsistencyCheckerService(ProtoSpace ps, EventBus eventBus) {
        super(ps, eventBus);
        definitions = new ArrayList<>();
    }

    public void fullReEvaluation(Workspace contextWorkspace)
    {
        long startTime=0;
        long endTime=0;
        ArrayList<Integer> ids = contextWorkspace.getTypeMap().get(ArtifactType.CRE);
        if(ids != null)
        {
            for(int i : ids)
            {

                ConsistencyRuleEvaluation cre = (ConsistencyRuleEvaluation) contextWorkspace.getArtifact(i);
                startTime = System.nanoTime();
                boolean result = cre.evaluate(contextWorkspace);
                endTime = System.nanoTime();
                if(result)
                {
                    trueEvaluations++;
                }else
                {
                    falseEvaluations++;
                }
                reevaluations++;
            }
        }
        totalEvaluationTime = totalEvaluationTime + (endTime-startTime);
    }

    @Override
    public void runServiceMechanism(ChangeEvent changeEvent) {

        long startTime=0;
        long endTime=0;
        Workspace contextWS = this.protoSpace.getWorkspace(changeEvent.getWorkspaceID());
        Artifact changedArtifact = contextWS.getArtifact(changeEvent.getArtifactID());
        boolean result;
        switch (changeEvent.getEventType())
        {
            case CREATE:
                //see if definition for type exists
                for(ConsistencyRuleDefinition crd : definitions)
                {
                    if(crd.getContext()==changedArtifact.getArtifactType())
                    {
                        //create CRE in contextWS
                        ConsistencyRuleEvaluation cre = new ConsistencyRuleEvaluation((changedArtifact.getArtifactID()*-1),crd,changedArtifact);
                        changedArtifact.addBelongingScope(cre);

                        contextWS.appendArtifact(cre);
                        startTime = System.nanoTime();
                        result = cre.evaluate(contextWS);
                        endTime = System.nanoTime();
                        if(result)
                        {
                            trueEvaluations++;
                        }else{
                            falseEvaluations++;
                        }

                        reevaluations++;
                    }
                }
                break;
            case UPDATE:
                //see if property is in a scope
                for(ConsistencyRuleEvaluation cre : changedArtifact.getBelongingScopes())
                {
                    startTime = System.nanoTime();
                    result = cre.evaluate(contextWS);
                    endTime = System.nanoTime();
                    if(result)
                    {
                        trueEvaluations++;
                    }else{
                        falseEvaluations++;
                    }
                    reevaluations++;
                }
                break;

        }

        totalEvaluationTime = totalEvaluationTime + (endTime-startTime);
        //System.out.println(totalEvaluationTime);
    }

    public float getMeanEvaluationTime()
    {
        return ((float)totalEvaluationTime)/1000000;
    }

    public int getReevaluationCount()
    {
        return reevaluations;
    }

    public int getFalseEvaluations()
    {
        return falseEvaluations;
    }

    public int getTrueEvaluations()
    {
        return trueEvaluations;
    }



    @Override
    public void printServiceInfo() {
        System.out.println("RE-EVALUATIONS: "+reevaluations);
        System.out.println("FALSE: "+falseEvaluations+" || TRUE: "+trueEvaluations);
    }

    @Override
    public void resetServiceData() {
        reevaluations = 0;
        falseEvaluations = 0;
        trueEvaluations = 0;
    }

    public void createDefinition(ConsistencyRule rule, ArtifactType context)
    {
        definitions.add(new ConsistencyRuleDefinition(-1, rule, context));
    }




}

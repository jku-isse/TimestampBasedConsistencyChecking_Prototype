package at.jku.evaluation.protospace.core.model.consistencychecker;

import at.jku.evaluation.protospace.core.model.Artifact;
import at.jku.evaluation.protospace.core.model.ArtifactType;
import at.jku.evaluation.protospace.core.model.Workspace;
import at.jku.evaluation.protospace.core.model.consistencychecker.rules.RuleEvaluationUtils;

public class ConsistencyRuleEvaluation extends Artifact {

    ConsistencyRuleDefinition definition;
    Artifact contextElement;
    //ArrayList<Integer> scope;

    public ConsistencyRuleEvaluation(int id, ConsistencyRuleDefinition crd, Artifact contextElement) {
        super(id, ArtifactType.CRE);
        //scope = new ArrayList<>();
        this.definition=crd;
        this.contextElement=contextElement;

    }

    public boolean evaluate(Workspace contextWorkspace){
        boolean result = RuleEvaluationUtils.evaluateConsistencyRule(definition.getRule(), this, contextElement, contextWorkspace);
        this.setProperty("result", ""+result, false, contextWorkspace.getTimestamp());

        if(result)
        {
            //System.out.println(this + " >> WS ["+contextWorkspace.getWSID()+"] >> " + contextElement);
        }
        else
        {
            //System.err.println(this + " >> WS ["+contextWorkspace.getWSID()+"] >> " + contextElement);
        }
        return result;
    }

    @Override
    public String toString() {
        return super.toString()+" // Context Element: ["+contextElement.getArtifactID()+"]"+" // Rule: ["+definition.getRule()+"]";
    }
}

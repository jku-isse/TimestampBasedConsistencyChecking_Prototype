package at.jku.evaluation.protospace.core.model.consistencychecker;

import at.jku.evaluation.protospace.core.model.Artifact;
import at.jku.evaluation.protospace.core.model.ArtifactType;



public class ConsistencyRuleDefinition extends Artifact {

    private ConsistencyRule rule;
    private ArtifactType context;


    public ConsistencyRuleDefinition(int id, ConsistencyRule rule, ArtifactType context) {
        super(id, ArtifactType.CRD);
        this.rule = rule;
        this.context = context;
    }

    public ConsistencyRule getRule() {
        return rule;
    }

    public ArtifactType getContext() {
        return context;
    }



}

package at.jku.evaluation.protospace.core.model;

import at.jku.evaluation.protospace.core.model.consistencychecker.ConsistencyRuleEvaluation;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Property {
    String key;
    ArrayList<Delta> deltas;
    ArrayList<ConsistencyRuleEvaluation> scopes;

    public Property(String key) {
        this.key = key;
        deltas = new ArrayList<>();
        scopes = new ArrayList<>();
    }

    public void setValue(String val, long timestamp) {
        deltas.add(new Delta(val, timestamp));
    }

    public String getKey() {
        return key;
    }

    public String getLastValue(long timestamp) {
        for(int i = deltas.size()-1; i>=0; i--)
        {
            long deltaTimestamp = deltas.get(i).getTimestamp();
            if(deltaTimestamp<=timestamp)
            {
                return deltas.get(i).getValue();
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return getLastValue(Long.MAX_VALUE);
    }

    public ArrayList<ConsistencyRuleEvaluation> getScopes() {
        return scopes;
    }

    public void addToScope(ConsistencyRuleEvaluation cre)
    {
        scopes.add(cre);
    }
}

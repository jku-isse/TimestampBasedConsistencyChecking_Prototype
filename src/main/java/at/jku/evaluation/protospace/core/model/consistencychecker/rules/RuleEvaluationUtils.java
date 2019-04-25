package at.jku.evaluation.protospace.core.model.consistencychecker.rules;

import at.jku.evaluation.protospace.core.model.Artifact;
import at.jku.evaluation.protospace.core.model.ArtifactType;
import at.jku.evaluation.protospace.core.model.Property;
import at.jku.evaluation.protospace.core.model.Workspace;
import at.jku.evaluation.protospace.core.model.consistencychecker.ConsistencyRule;
import at.jku.evaluation.protospace.core.model.consistencychecker.ConsistencyRuleEvaluation;
import com.sun.corba.se.spi.orbutil.threadpool.Work;

import java.util.ArrayList;
import java.util.HashMap;

public class RuleEvaluationUtils {

    public static boolean evaluateConsistencyRule(ConsistencyRule rule, ConsistencyRuleEvaluation cre, Artifact contextElement, Workspace contextWorkspace) {
        //System.out.println("----- Evaluating "+rule+" on "+contextElement);
        switch (rule)
        {
            case CR01:
                return evaluateCR01(cre, contextElement, contextWorkspace);
            case CR02:
                return evaluateCR02(cre, contextElement, contextWorkspace);
            case CR03:
                return evaluateCR03(cre, contextElement, contextWorkspace);
            case CR04:
                return evaluateCR04(cre, contextElement, contextWorkspace);
            case CR05:
                return evaluateCR05(cre, contextElement, contextWorkspace);
            case CR06:
                return evaluateCR06(cre, contextElement, contextWorkspace);
            case CR07:
                return evaluateCR07(cre, contextElement, contextWorkspace);
            case CR08:
                return evaluateCR08(cre, contextElement, contextWorkspace);
            case CR09:
                return evaluateCR09(cre, contextElement, contextWorkspace);
            case CR10:
                return evaluateCR10(cre, contextElement, contextWorkspace);

                default:
                    return true;
        }

    }


    /*
    CR01
    Context Element:    uml:Lifeline
    Description:        "Every lifeline has to have a corresponding class";
    */
    private static boolean evaluateCR01(ConsistencyRuleEvaluation cre, Artifact contextElement, Workspace contextWorkspace)
    {
        String lifelineName = contextWorkspace.getPropertyValue(contextElement.getArtifactID(),"name", Long.MAX_VALUE);
        if(lifelineName==null)
        {
            return false;
        }

        Artifact collaborationArtifact = findParentOfType(contextElement.getArtifactID(), contextWorkspace, ArtifactType.COLLABORATION);
        if(collaborationArtifact==null)
        {
            return false;
        }

        ArrayList<Integer> ownedAttributes = collaborationArtifact.getReferences("ownedAttribute");
        String ref = null;
        for(int i : ownedAttributes)
        {
            String name = contextWorkspace.getPropertyValue(i, "name", Long.MAX_VALUE);
            if(lifelineName.equals(name))
            {
                ref = contextWorkspace.getPropertyValue(i, "type", Long.MAX_VALUE);
            }

        }

        if(ref==null)
        {
            return false;
        }

        Artifact artifact = contextWorkspace.getArtifact(Integer.parseInt(ref.substring(4)));
        if(artifact.getArtifactType()==ArtifactType.CLASS)
        {
            return true;
        }

        return false;

    }


    /*
    CR02
    Context Element:    uml:Transition
    Description:        "Every transition has to have a corresponding message"
    */
    private static boolean evaluateCR02(ConsistencyRuleEvaluation cre, Artifact contextElement, Workspace contextWorkspace)
    {
        ArrayList<Integer> messageArtifactIDs = contextWorkspace.getArtifactsOfType(ArtifactType.MESSAGE);
        String name = contextWorkspace.getPropertyValue(contextElement.getArtifactID(), "name", Long.MAX_VALUE);
        //System.out.println("For: "+name);
        if(name==null)
        {
            return false;
        }

        for(int i : messageArtifactIDs)
        {
            String messageName = contextWorkspace.getPropertyValue(i, "name", Long.MAX_VALUE);
            //System.out.println(i+": "+messageName);
            if(messageName.equals(name))
            {
                return true;
            }
        }

        return false;
    }

    /*
    CR03
    Context Element:    uml:Transition
    Description:        “Statechart Action must be defined as an Operation in the Owner’s Class”
    */
    private static boolean evaluateCR03(ConsistencyRuleEvaluation cre, Artifact contextElement, Workspace contextWorkspace)
    {
        contextElement.addBelongingScope(cre);
        String name = contextWorkspace.getPropertyValue(contextElement.getArtifactID(),"name", Long.MAX_VALUE);

        if(name==null)
        {
            return false;
        }

        Artifact parentClass = findParentOfType(contextElement.getArtifactID(), contextWorkspace, ArtifactType.CLASS);
        //System.out.println(parentClass);
        if(parentClass == null)
        {
            return false;
        }

        parentClass.addBelongingScope(cre);
        ArrayList<Integer> ownedOperation = parentClass.getReferences("ownedOperation");
        boolean found = false;
        for (int i: ownedOperation)
        {
            String operationName = contextWorkspace.getPropertyValue(i, "name", Long.MAX_VALUE);
            if(name.equals(operationName))
            {
                found = true;
            }
        }
        return found;
    }



    /*
    CR04
    Context Element:    uml:Message
    Description:        "Message Action must be defined as an Operation in Receiver’s Class"
    */
    private static boolean evaluateCR04(ConsistencyRuleEvaluation cre, Artifact contextElement, Workspace contextWorkspace)
    {

        //receiveEvent.covered.represents.type

        String mName = contextWorkspace.getPropertyValue(contextElement.getArtifactID(),"name", Long.MAX_VALUE);
        if(mName==null)
        {
            return false;
        }

        String refFragment = contextWorkspace.getPropertyValue(contextElement.getArtifactID(), "receiveEvent", Long.MAX_VALUE);
        if(refFragment==null)
        {
            return false;
        }

        String refLifeline = contextWorkspace.getPropertyValue(Integer.parseInt(refFragment.substring(4)), "covered", Long.MAX_VALUE);
        if(refLifeline==null)
        {
            return false;
        }

        String refAttribute = contextWorkspace.getPropertyValue(Integer.parseInt(refLifeline.substring(4)), "represents", Long.MAX_VALUE);
        if(refAttribute==null)
        {
            return false;
        }


        String refType = contextWorkspace.getPropertyValue(Integer.parseInt(refAttribute.substring(4)), "type", Long.MAX_VALUE);
        if(refType==null)
        {
            return false;
        }


        Artifact classArtifact = contextWorkspace.getArtifact(Integer.parseInt(refType.substring(4)));
        if(classArtifact==null)
        {
            return false;
        }

        ArrayList<Integer> ownedOperation = classArtifact.getReferences("ownedOperation");

        if(ownedOperation!=null)
        {
            for(int i : ownedOperation)
            {
                String opName = contextWorkspace.getPropertyValue(i, "name", Long.MAX_VALUE);
                if(opName!=null && mName.equals(opName))
                {
                    return true;
                }
            }
        }else{
            return false;
        }


        return false;
    }

    /*
    CR05
    Context Element:    uml:Operation
    Description:        "Operation Parameters must have unique Names"
    */
    private static boolean evaluateCR05(ConsistencyRuleEvaluation cre, Artifact contextElement, Workspace contextWorkspace)
    {
        contextElement.addBelongingScope(cre);
        ArrayList<Integer> referencesList = contextElement.getReferences("ownedParameter");
        HashMap<String, Integer> parameterNames = new HashMap<>();
        boolean doubleFound=false;

        for(int i : referencesList)
        {
            addToScope(contextWorkspace, cre, i);
            String name = contextWorkspace.getPropertyValue(i, "name", Long.MAX_VALUE);
            if(name != null && !name.equals(""))
            {
                if(parameterNames.containsKey(name))
                {
                    doubleFound = true;
                }else{
                    parameterNames.put(name, 1);
                }
            }
        }

        return !doubleFound;
    }

    /*
    CR06
    Context Element:    uml:Operation
    Description:        "An Operation has at most one return Parameter"
    */
    private static boolean evaluateCR06(ConsistencyRuleEvaluation cre, Artifact contextElement, Workspace contextWorkspace)
    {
        contextElement.addBelongingScope(cre);
        ArrayList<Integer> referencesList = contextElement.getReferences("ownedParameter");
        int returnparameters = 0;
        for(int i : referencesList)
        {
            addToScope(contextWorkspace, cre, i);

            //System.out.println("looking in artifact: "+i);
            String direction = contextWorkspace.getPropertyValue(i, "direction", Long.MAX_VALUE);
            //System.out.println(direction);
            if(direction != null && direction.equals("return"))
            {

                returnparameters++;
            }
        }
        if(returnparameters<=1)
        {
            return true;
        }
        return false;
    }



    /*
    CR07
    Context Element:    uml:Interface
    Description:        “An interface can have at most one generalization”
    */
    private static boolean evaluateCR07(ConsistencyRuleEvaluation cre, Artifact contextElement, Workspace contextWorkspace)
    {

        contextElement.addBelongingScope(cre);
        ArrayList<Integer> referencesList = contextElement.getReferences("generalization");
        if((referencesList.size()>0))
        {
            for(int i : referencesList)
            {
                addToScope(contextWorkspace,cre,i);
                contextWorkspace.getPropertyValue(i, "general", Long.MAX_VALUE);
            }
        }


        return (referencesList.size()<=1);
    }

    /*
    CR08
    Context Element:    uml:Interface
    Description:        "An Interface can only contain Public Operations and no Attributes"
    */
    private static boolean evaluateCR08(ConsistencyRuleEvaluation cre, Artifact contextElement, Workspace contextWorkspace)
    {

        contextElement.addBelongingScope(cre);
        ArrayList<Integer> referencesList = contextElement.getReferences("ownedAttribute");
        if((referencesList.size()>0))
        {
            return false;
        }

        ArrayList<Integer> operationsReferencesList = contextElement.getReferences("ownedOperation");
        for(int i : operationsReferencesList)
        {
            addToScope(contextWorkspace,cre,i);
            String visibility = contextWorkspace.getPropertyValue(i, "visibility", Long.MAX_VALUE);
            //System.out.println("Visibility: "+visibility);
            if(visibility == null || !visibility.equals("public"))
            {
                return false;
            }
        }


        return true;
    }

    /*
    CR09
    Context Element:    uml:Class
    Description:        "No two Class Operations may have the same Signature"
    */
    private static boolean evaluateCR09(ConsistencyRuleEvaluation cre, Artifact contextElement, Workspace contextWorkspace)
    {
        boolean ruleHolds = true;
        contextElement.addBelongingScope(cre);

        ArrayList<Integer> referencesList = contextElement.getReferences("ownedOperation");
        ArrayList<Artifact> references = contextWorkspace.getArtifacts(referencesList);

        for(Artifact a : references)
        {
            a.addBelongingScope(cre);
            if ((sameSignatureTimestamped(a.getArtifactID(), referencesList, contextWorkspace)) && ruleHolds)
            {
                ruleHolds = false;
            }
        }

        return ruleHolds;
    }

    /*
    CR10
    Context Element:    uml:Class
    Description:        “No two fields may have the same name”
     */
    private static boolean evaluateCR10(ConsistencyRuleEvaluation cre, Artifact contextElement, Workspace contextWorkspace) {

        boolean ruleHolds = true;
        contextElement.addBelongingScope(cre);
        ArrayList<Integer> referencesList = contextElement.getReferences("ownedAttribute");
        ArrayList<Artifact> references = contextWorkspace.getArtifacts(referencesList);

        for(Artifact a : references)
        {
            a.addBelongingScope(cre);
            if ((sameSignatureTimestamped(a.getArtifactID(), referencesList, contextWorkspace)) && ruleHolds)
            {
                ruleHolds = false;
            }
        }

        return ruleHolds;
    }



    /*
    MISCELLANEOUS
     */
    private static boolean sameSignatureTimestamped(int comparantID, ArrayList<Integer> compareList, Workspace contextWorkspace)
    {
        boolean result = false;
        String lastName =  contextWorkspace.getPropertyValue(comparantID, "name", Long.MAX_VALUE);
        String lastVisibility =  contextWorkspace.getPropertyValue(comparantID, "visibility", Long.MAX_VALUE);

        //System.out.println(">>>>>>COMPARING: {name="+lastName+"; visibility="+lastVisibility+"} ID: ["+comparantID+"]");


        if(lastName==null || lastVisibility==null) {
            return result;
        }

        //System.out.println("Comparing signature ["+lastName+", "+lastVisibility+"] || Artifact ["+comparantID+"]");

        for(int i : compareList)
        {
            if(comparantID!=i)
            {
                String aName = contextWorkspace.getPropertyValue(i, "name", Long.MAX_VALUE);
                String aVisibility = contextWorkspace.getPropertyValue(i, "visibility", Long.MAX_VALUE);
                //System.out.println("TO: {name="+aName+"; visibility="+aVisibility+"} ID: ["+i+"] - "+(comparantID!=i));
                if(aName!=null && aVisibility!=null)
                {
                    //System.out.println("["+aName.getLastValue()+", "+aVisibility.getLastValue()+"] || Artifact ["+i+"]");
                    if(aName.equals(lastName) &&
                            aVisibility.equals(lastVisibility))
                    {
                        //System.out.println("NOT OK! (same)");
                        result = true;
                    }else{
                        //System.out.println("OK!");
                    }
                }

            }
        }
        return result;
    }

    private static Artifact findParentOfType(int id, Workspace contextWorkspace, ArtifactType type) {

        String parentModelRef = contextWorkspace.getPropertyValue(id, "parentModelElement", Long.MAX_VALUE);

        //System.out.println(parentModelRef);
        if(parentModelRef!=null)
        {
            int parentID = Integer.parseInt(parentModelRef.substring(4));
            Artifact parentArtifact = contextWorkspace.getArtifact(parentID);
            //System.out.println(parentID);
            if(parentArtifact==null)
            {
                return null;
            }
            if(parentArtifact.getArtifactType() == type)
            {
                return parentArtifact;
            }
            else
            {
                return findParentOfType(parentArtifact.getArtifactID(), contextWorkspace, type);
            }


        }
        return null;
    }

    private static void addToScope(Workspace contextWorkspace, ConsistencyRuleEvaluation cre, int artifactID) {
        Artifact scopeElement = contextWorkspace.getArtifact(artifactID);
        if(scopeElement!=null)
        {
            scopeElement.addBelongingScope(cre);
        }
    }

}

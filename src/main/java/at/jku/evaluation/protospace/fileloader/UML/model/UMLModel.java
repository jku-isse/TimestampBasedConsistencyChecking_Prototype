package at.jku.evaluation.protospace.fileloader.UML.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class UMLModel {

    private ArrayList<ModelElement> modelElements;
    private HashMap<String, ModelElement> useCaseView;
    private HashMap<String, ModelElement> logicalView;
    private HashMap<String, ModelElement> componentView;
    private HashMap<String, ModelElement> deploymentView;
    int modelElementIndex;

    public UMLModel()
    {
        useCaseView = new HashMap<String, ModelElement>();
        logicalView = new HashMap<String, ModelElement>();
        componentView = new HashMap<String, ModelElement>();
        deploymentView = new HashMap<String, ModelElement>();
        modelElements = new ArrayList<>();
        modelElementIndex=0;
    }


    public void addModelElement(ModelElement m, ModelView view)
    {
        switch (view) {
            case USECASE:
                useCaseView.put(m.getXmiID(), m);
                break;
            case LOGICAL:
                logicalView.put(m.getXmiID(), m);
                break;
            case COMPONENT:
                componentView.put(m.getXmiID(), m);
                break;
            case DEPLOYMENT:
                deploymentView.put(m.getXmiID(), m);
                break;
        }
        modelElements.add(m);
        modelElementIndex = modelElementIndex + 1;
    }

    public int getNumberOfModelElements()
    {
        return modelElementIndex;
        //return (useCaseView.size()+logicalView.size()+componentView.size()+deploymentView.size());
    }

    public HashMap<String, ModelElement> getUseCaseView() {
        return useCaseView;
    }

    public HashMap<String, ModelElement> getLogicalView() {
        return logicalView;
    }

    public HashMap<String, ModelElement> getComponentView() {
        return componentView;
    }

    public HashMap<String, ModelElement> getDeploymentView() {
        return deploymentView;
    }

    public ArrayList<ModelElement> getModelElements(){
        return modelElements;
    }
}

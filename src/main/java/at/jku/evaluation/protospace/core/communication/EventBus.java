package at.jku.evaluation.protospace.core.communication;

import at.jku.evaluation.protospace.core.service.ProtoSpaceService;

import java.util.ArrayList;

public class EventBus {

    ArrayList<ChangeEventListener> changeEventListeners;

    public EventBus()
    {
        changeEventListeners = new ArrayList<>();
    }

    public void fireEvent(ChangeEvent changeEvent) {
        for(ChangeEventListener cel : changeEventListeners)
        {
            cel.notifyEventListener(changeEvent);
        }
    }

    public void registerListener(ChangeEventListener cel) {
        changeEventListeners.add(cel);
    }
}

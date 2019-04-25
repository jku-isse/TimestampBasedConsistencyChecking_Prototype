package at.jku.evaluation.protospace.core.service;

import at.jku.evaluation.protospace.core.ProtoSpace;
import at.jku.evaluation.protospace.core.communication.ChangeEvent;
import at.jku.evaluation.protospace.core.communication.ChangeEventListener;
import at.jku.evaluation.protospace.core.communication.EventBus;

public abstract class ProtoSpaceService implements ChangeEventListener {

    public ProtoSpace protoSpace;

    public ProtoSpaceService(ProtoSpace ps, EventBus eventBus) {
        this.protoSpace = ps;
        eventBus.registerListener(this);
    }


    @Override
    public void notifyEventListener(ChangeEvent changeEvent) {
        runServiceMechanism(changeEvent);
    }

    public abstract void runServiceMechanism(ChangeEvent changeEvent);
    public abstract void printServiceInfo();
    public abstract void resetServiceData();
}

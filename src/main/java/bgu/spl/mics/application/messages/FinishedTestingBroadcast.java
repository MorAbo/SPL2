package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class FinishedTestingBroadcast implements Broadcast {
    private String modelName;

    public FinishedTestingBroadcast(String m){
        this.modelName=m;
    }

    public String getModel() { return modelName;
    }
}

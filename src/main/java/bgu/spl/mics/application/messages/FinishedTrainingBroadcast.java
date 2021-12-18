package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

public class FinishedTrainingBroadcast implements Broadcast {
    private String modelName;

    public FinishedTrainingBroadcast(String m){
        this.modelName=m;
    }

    public String getModel() { return modelName;
    }
}

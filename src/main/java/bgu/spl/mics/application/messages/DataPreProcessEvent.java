package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;

public class DataPreProcessEvent implements Event<Model> {
    private Future<Model> f;

    public DataPreProcessEvent(){
        f = null;
    }

}
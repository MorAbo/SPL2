package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;

public class PublishResultEvent implements Event<String> {
    private Future<String> f;

    public PublishResultEvent(){
        f = null;
    }

    public void Resolve(String s){
        f.resolve(s);
    }


}

package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.Model;

public class PublishResultEvent implements Event<String> {
    private Future<String> f;

    public PublishResultEvent(Model m){
        f = null;
    }

    public void Resolve(String s){
        f.resolve(s);
    }


}

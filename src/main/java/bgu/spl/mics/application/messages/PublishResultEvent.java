package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class PublishResultEvent implements Event<String> {
    private Future<String> f;
    public PublishResultEvent(){
        f = null;
    }
}

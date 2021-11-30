package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class PublishResultEvent implements Event<String> {
    public String result;

    public PublishResultEvent(){
        result = null;
    }
}

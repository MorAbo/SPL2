package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class PublishResultEvent<T> implements Event<T> {
    public T result;

    public PublishResultEvent(){
        result = null;
    }
}

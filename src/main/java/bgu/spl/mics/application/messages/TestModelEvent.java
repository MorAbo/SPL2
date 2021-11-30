package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class TestModelEvent implements Event<String> {
    public String result;

    public TestModelEvent(){
        result = null;
    }

}

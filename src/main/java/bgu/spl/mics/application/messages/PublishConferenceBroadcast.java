package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

import java.util.LinkedList;

public class PublishConferenceBroadcast implements Broadcast {
    private LinkedList<String> modelNames;


    public PublishConferenceBroadcast(LinkedList<String> names){
        modelNames=names;
    }

    public LinkedList<String> getModelNames(){return modelNames;}


}

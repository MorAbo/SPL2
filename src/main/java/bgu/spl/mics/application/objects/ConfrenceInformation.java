package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.PrimitiveIterator;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int EndDate;
    private int StartDate;
    private int tick;
    private LinkedList<String> modelsName;
    private LinkedList<Model> models;

    public ConfrenceInformation(String name, int EndDate, int StartDate){
        this.name = name;
        this.EndDate = EndDate;
        this.StartDate= StartDate;
        modelsName=new LinkedList<>();
        models=new LinkedList<>();
        tick=1;
    }

    public String getName(){return name;}

    public int getDate(){return EndDate;}

    public void IncreaseTick(){ tick++; }

    public boolean shouldRegister(){ return tick==StartDate;}
    public boolean shouldPublish(){ return tick==EndDate; }
    public void addToModels(Model m){ modelsName.add(m.getName()); models.add(m);}
    public LinkedList<String> getModelsNames(){return modelsName;}
    public LinkedList<Model> getModels(){return models;};

}

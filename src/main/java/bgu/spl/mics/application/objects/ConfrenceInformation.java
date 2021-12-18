package bgu.spl.mics.application.objects;

import bgu.spl.mics.ReadWriteList;
import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int EndDate;
    private int StartDate;
    private int tick;
    private ReadWriteList<Model> models;

    public ConfrenceInformation(String name, int EndDate, int StartDate){
        this.name = name;
        this.EndDate = EndDate;
        this.StartDate= StartDate;
        models=new ReadWriteList<>();
        tick=1;
    }

    public String getName(){return name;}
    public int getDate(){return EndDate;}
    public ReadWriteList<Model> getModels(){return models;};

    public void IncreaseTick(){ tick++; }

    public boolean shouldRegister(){ return tick==StartDate;}
    public boolean shouldPublish(){ return tick==EndDate; }
    public void addToModels(Model m){models.add(m);}

    public LinkedList<String> publish(){
        LinkedList<String > modelsName= new LinkedList<>();
        for (int i=0; i< models.size(); i++) {
            modelsName.add(models.get(i).getName());
        }
        return modelsName;}
}

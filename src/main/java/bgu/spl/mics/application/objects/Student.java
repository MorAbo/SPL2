package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.PublishResultEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {

    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private List<Model> models;

    public Student(String name, String department, String degree){
        this.name = name;
        this.department = department;
        publications = 0;
        papersRead = 0;
        models = new LinkedList();
        setDegree(degree);
    }

    public String getName(){return name;}
    public String getStatus(){ return status.toString();}
    public List<Model> getModels() {return models;}
    public String getDepartment() { return department;}
    public int getPublications() {return publications;}
    public int getPapersRead() { return papersRead;}

    public void addModel(Model model){
        models.add(model);
    }

    public void IncreasePublication(){publications++;}
    public void IncreasePapersRead(){papersRead++;}

    private void setDegree(String degree){
        if(degree.equals("MSc"))status = Degree.MSc;
        else status = Degree.PhD;
    }


    public boolean isMyModel(String name){
        for (Model m: models)
            if (m.getName().equals(name)) return true;
        return false;
    }

    public Model getModelByName(String name){
        for (Model m:models)
            if (m.getName()==name)
                return m;
        return null;
    }

}

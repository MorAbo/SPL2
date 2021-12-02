package bgu.spl.mics.application.objects;

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
    private int tick;

    private List<Model> models;

    public Student(String name, String department, String degree){
        this.name = name;
        this.department = department;
        publications = 0;
        papersRead = 0;
        models = new LinkedList();
        setDegree(degree);
        tick=0;
    }

    public void addModel(Model model){
        models.add(model);
    }

    private void setDegree(String degree){
        if(degree.equals("MSc")){
            status = Degree.MSc;
        }
        else{
            status = Degree.PhD;
        }
    }
}

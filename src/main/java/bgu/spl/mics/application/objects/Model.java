package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    enum status {PreTrained, Training, Trained, Tested};
    enum result {None, Good, Bad};

    private String name;
    private Data data;
    private Student student;
    private status isTrained;
    private result goodOrBad;

    public Model(String name, Data data, Student student){
        this.name = name;
        this.data = data;
        this.student = student;
        setStatus("PreTrained");
        setResult("None");
    }
    public void ProcessData(int amount){ data.ProcessData(amount);}
    public Data GetData(){ return data;}
    public String getName(){return name;}

    public void setStatus(String statusString){
        switch (statusString) {
            case "PreTrained":
                isTrained = status.PreTrained;
                break;
            case "Training":
                isTrained = status.Training;
                break;
            case "Trained":
                isTrained = status.Trained;
                break;
            case "Tested":
                isTrained = status.Tested;
                break;
        }
    }

    public void setResult(String resultString){
        switch (resultString) {
            case "None":
                goodOrBad = result.None;
                break;
            case "Good":
                goodOrBad = result.Good;
                break;
            case "Bad":
                goodOrBad = result.Bad;
                break;
        }
    }

    public boolean IsGood(){ return goodOrBad==result.Good; }
}

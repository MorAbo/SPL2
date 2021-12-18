package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    private Data data;
    private int start_index;
    private boolean processed;

    public DataBatch(Data data, int start_index){
        this.data = data;
        this.start_index = start_index;
        processed = false;
    }

    public Data getData() { return data; }
    public String getType(){return data.getType();}

    /**
     * processed the batch be increacing the process field by 1000 or the amount thats left
     */
    public void ProcessData(){
        int amount = Math.min(1000, data.getSize()-start_index);
        data.ProcessData(amount);
        processed = true;
    }

}

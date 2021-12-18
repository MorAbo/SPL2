package bgu.spl.mics.application.objects;


import java.util.LinkedList;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private Cluster cluster;
    private LinkedList<DataBatch> VRAM;
    private int time2train;
    private DataBatch training_now;
    private int TimeLeftForBatch;
    private double batchesLeftToProcessForModel;
    private boolean isTrainingBatch;
    private boolean isFinished;
    private int CalTime;


    public GPU(String type){
        this.type = setType(type);
        model = null;
        cluster = Cluster.getInstance();
        VRAM = new LinkedList<>();
        time2train=0;
        training_now=null;
        TimeLeftForBatch=0;
        batchesLeftToProcessForModel=0;
        isTrainingBatch=false;
        isFinished=false;
        CalTime= calTime();
    }

    public Model getModel(){return model;}
    public boolean isInTheMiddleOfTraining(){
        return model!=null;
    }
    public int getTime2train(){return time2train;}


    private int calTime(){
        if (type.equals(Type.RTX3090))
            return 1;
        if(type.equals(Type.RTX2080))
            return 2;
        else return 4;
    }

    private Type setType(String type){
        Type t = null;
        switch (type) {
            case "RTX3090":
                t = Type.RTX3090;
                break;
            case "RTX2080":
                t = Type.RTX2080;
                break;
            case "GTX1080":
                t = Type.GTX1080;
                break;
        }
        return t;
    }

    /**
     *sets the model with @model and restart the training fields- isFinished, batchesLeftToProcess
     * @param model = model to operate on.
     * @post model == model
     */
    public void setModel(Model model){
        this.model = model;
        isFinished=false;
        if (model!=null)
            batchesLeftToProcessForModel=Math.ceil((float)(model.GetData().getSize())/1000);
        else batchesLeftToProcessForModel=Integer.MAX_VALUE;
    }

    /**
     * @return how many more batches i can add to the vram
     */
    public int VramCapacityLeft() {
        if (this.type==Type.RTX3090) return 32-VRAM.size();
        else if (this.type==Type.RTX2080) return 16-VRAM.size();
        else return 8-VRAM.size();
    }

    /**
     *if in the middle of training a model decreases the time left to train by 1
     * if in the middle of training a batch decreasing the time left for training the batch
     * and increaces the GPURuntime in the cluster by 1
     * if TimeLeftForBatch = 0 calles the finishedTrainingBatch function
     */
    public void IncreaseTick() {
        time2train=time2train-1;
        time2train= Math.max(0, time2train);
        if (TimeLeftForBatch>0) cluster.IncreaseGpuRunTime();
        TimeLeftForBatch=TimeLeftForBatch-1;
        TimeLeftForBatch=Math.max(0, TimeLeftForBatch);
        if (TimeLeftForBatch==0) finishedTrainingBatch();
    }

    /**
    divided the data in model to batches of 1000 sends them one by one to the cluster
     */
    private void divideData(){
        for (int i=0; i<model.GetData().getSize(); i+=1000){
            cluster.recieveUnprocessedDataBatch( new DataBatch(model.GetData(), i), this);
        }
    }

    /**
     * after the batch has completed the process it will return the the gpu
     * the gpu will insert it to the vram and update the time2train.
     * if we are not in the middle of trainig a batch it will remove the batch from the vram
     * and train it
     */
    public void receiveProcessedDataBatch(DataBatch data){
        synchronized (VRAM){
            VRAM.add(data);
            time2train+=CalTime;
            if(!isTrainingBatch){ TrainBatch(VRAM.pop()); }
    }}

    /**
     * starts the new model training:
     * sets model, sets its status to training, and calls divide dada
     */
    public void Train(Model m){
        setModel(m);
        m.setStatus("Training");
        divideData();
    }

    /**
     * statrs the trainig of a new Batch
     * sets training now to @db, initialize the TimeLeftForBatch and
     * sets isTrainingBatch to true
     * @param db the databatch to train
     */
    private void TrainBatch(DataBatch db){
        training_now=db;
        TimeLeftForBatch=CalTime;
        isTrainingBatch=true;
    }


    /**
     * finishes the training of a batch.
     * decreases the batchesLeftToProcessForModel, if batchesLeftToProcessForModel=0
     * calls the function that finishes training the model
     * also it starts the training of the next batch from the Vram,
     * if there is none it will set training_now to be null and
     * isTrainingBatch to be false;
     */
    private void finishedTrainingBatch(){
        batchesLeftToProcessForModel--;
        if (batchesLeftToProcessForModel==0) finishedModel();
        else synchronized (VRAM) {
        if(!VRAM.isEmpty()) { TrainBatch(VRAM.pop()); }
        else {training_now=null; isTrainingBatch=false;}
        }
    }

    /**
     * sets the model status to trained
     * sets isFinished to true
     */
    private void finishedModel(){
        model.setStatus("Trained");
        isFinished=true;
    }

    /**
     * returns wether the trainig is finished.
     * if it is finished it will return true and restart the
     * isFinished flag to false;
     * @return
     */
    public boolean isFinished(){
        if (!isFinished) return false;
        else {isFinished=false; return true; }
    }


    public int getTimeLeftForBatch(){return TimeLeftForBatch;}

}

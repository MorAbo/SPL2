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
     *
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
    public Model getModel(){return model;}

    /**
     * @return how many more batches i can add to the vram
     */
    public int VramCapacityLeft() {
        if (this.type==Type.RTX3090) return 32-VRAM.size();
        else if (this.type==Type.RTX2080) return 16-VRAM.size();
        else return 8-VRAM.size();
    }

    /**
     *increase the tick by 1
     * @pre (tick)==@post(tick)-1
     */
    public void IncreaseTick() {
//        System.out.println("               "+Thread.currentThread().getName()+" TICK");
        time2train= Math.max(0, time2train--);
        if (TimeLeftForBatch>0) cluster.IncreaseGpuRunTime();
        TimeLeftForBatch=TimeLeftForBatch-1;
        TimeLeftForBatch=Math.max(0, TimeLeftForBatch);
        if (TimeLeftForBatch==0) finishedTrainingBatch();
    }



    /**
    divided the data in model to batches of 1000 and
    stores it in the disk
     * @pre DISK.isEmpty();
     * @post DISK.size() == Math.ceil(model.data.size() / 1000)
     */
    private void divideData(){
        for (int i=0; i<model.GetData().getSize(); i+=1000){
            cluster.recieveUnprocessedDataBatch( new DataBatch(model.GetData(), i), this);
        }
    }

    /**
     * after the cluster completed the task it will set the future of TrainModelEvent
     * as complete.
     * @pre (VRAM.size())+1 = @post(vram.size())
     */
    public void receiveProcessedDataBatch(DataBatch data){
        synchronized (VRAM){
        VRAM.add(data);
        time2train+=CalTime(data);
        if(!isTrainingBatch){ TrainBatch(VRAM.pop()); }
    }}

    /**
     * train the processed data batch by batch
     * @return the trained model
     * @post (Model.data.processed) = model.data.size
     */
    public void Train(Model m){
        setModel(m);
        m.setStatus("Training");
        divideData();
    }

    private boolean isThereAnythingToProcess() {
        if (!VRAM.isEmpty()) return true;
        else for (DataBatch db: cluster.GetProcessedData(this))
            VRAM.add(db);
        return !VRAM.isEmpty();
    }

    public boolean isInTheMiddleOfTraining(){
        return model!=null;
    }
    /**
     * trains a databatch
     * (train=wait the appropriate amount of ticks)
     * @param db the databatch to train
     */
    private void TrainBatch(DataBatch db){
        training_now=db;
        TimeLeftForBatch=CalTime(db);
        isTrainingBatch=true;
    }

    private int CalTime(DataBatch db){
        if (type.equals(Type.RTX3090))
            return 1;
        if(type.equals(Type.RTX2080))
            return 2;
        else return 4;
    }

    private void finishedTrainingBatch(){
        batchesLeftToProcessForModel--;
        if (batchesLeftToProcessForModel==0) finishedModel();
        else synchronized (VRAM) {
        if(!VRAM.isEmpty()) { TrainBatch(VRAM.pop()); }
        else {training_now=null; isTrainingBatch=false;}
        }
    }

    private void finishedModel(){
        cluster.addTrainedModel(model.getName());
        model.setStatus("Trained");
        isFinished=true;
        System.out.println("finished Model "+ model.getName());
    }

    public boolean isFinished(){
        if (!isFinished) return false;
        else {isFinished=false; return true; }}
    public int getTime2train(){return time2train;}

}

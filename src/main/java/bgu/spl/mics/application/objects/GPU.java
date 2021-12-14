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
    private int tick;
    private int time2train;


    public GPU(String type){
        this.type = setType(type);
        cluster = Cluster.getInstance();
//        Disk = new LinkedList<>();
        VRAM = new LinkedList<>();
        model = null;
        tick=1;
        time2train=0;
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
     *increase the tick by 1
     * @pre (tick)==@post(tick)-1
     */
    public void IncreaseTick() {
        tick++;
        synchronized (this) {
            this.notify();
        }
    }



    /**
    divided the data in model to batches of 1000 and
    stores it in the disk
     * @pre DISK.isEmpty();
     * @post DISK.size() == Math.ceil(model.data.size() / 1000)
     */
    public void divideData(){
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
        VRAM.add(data); notifyAll(); time2train+=CalTime(data);
    }

    /**
     * train the processed data batch by batch
     * @return the trained model
     * @post (Model.data.processed) = model.data.size
     */
    public Model Train(Model m) throws InterruptedException {
        setModel(m);
        m.setStatus("Training");
        divideData();
        double counter = Math.ceil((float)(model.GetData().getSize())/1000);
        while (counter>0) {//not finished
            while (!isThereAnythingToProcess()) wait();
            TrainBatch(VRAM.remove());
            counter--;
        }
        cluster.addTrainedModel(model.getName());
        m.setStatus("Trained");
        return model;
    }

    private boolean isThereAnythingToProcess() {
        if (!VRAM.isEmpty()) return true;
        else for (DataBatch db: cluster.GetProcessedData(this))
            VRAM.add(db);
        return !VRAM.isEmpty();
    }

    /**
     * trains a databatch
     * (train=wait the appropriate amount of ticks)
     * @param db the databatch to train
     */
    private void TrainBatch(DataBatch db) throws InterruptedException {
        waitByTick(CalTime(db));
    }

    private int CalTime(DataBatch db){
        if (type.equals(Type.RTX3090))
            return 1;
        if(type.equals(Type.RTX2080))
            return 2;
        else return 4;


    }

    private void waitByTick(int tickSum) throws InterruptedException {
        int CurrentTick=tick;
        while(CurrentTick+tickSum!=tick) {
            synchronized (this){
            this.wait();}
            time2train--;
            cluster.IncreaseGpuRunTime();
        }

    }

    public int getTime2train(){return time2train;}

}

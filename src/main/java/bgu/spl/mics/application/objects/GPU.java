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
    private LinkedList<DataBatch> Disk;
    private LinkedList<DataBatch> VRAM;
    private int tick;


    public GPU(String type){
        this.type = setType(type);
        cluster = Cluster.getInstance();
        Disk = new LinkedList<>();
        VRAM = new LinkedList<>();
        model = null;
        tick=0;
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
    public int DiskCapacity() {return Disk.size();}

    /**
     * @return how many more batches i can add to the vram
     */
    public int VramCapacityLeft() {
        if (this.type==Type.RTX3090) return 32-VRAM.size();
        else if (this.type==Type.RTX3090) return 16-VRAM.size();
        else return 8-VRAM.size();
    }

    /**
     *increase the tick by 1
     * @pre (tick)==@post(tick)-1
     */
    public void IncreaseTick(){tick++;}



    /**
    divided the data in model to batches of 1000 and
    stores it in the disk
     * @pre DISK.isEmpty();
     * @post DISK.size() == Math.ceil(model.data.size() / 1000)
     */
    public void divideData(){

    }

    /**
     * Sends chunks of unprocessed data from the model of batches of 1000 samples using DataBatch
     * to the cluster
     * GPU will only send data if it has room to store it when it returns
     * @pre model.data.processed == 0
     * @post Disk.isEmpty()
     */
    public void SendData(){

        //cluster.precessdata

    }

    /**
     * after the cluster completed the task it will set the future of TrainModelEvent
     * as complete.
     * @pre (VRAM.size())+1 = @post(vram.size())
     */
    public void receiveProcessedData(DataBatch data){
        //addes it to the vram and sends it to train
    }

    /**
     * train the processed data batch by batch
     * @return the trained model
     * @post (Model.data.processed) = model.data.size
     */
    public Model Train(){
//        int sum=0;
//        while(sum!=Math.ceil(model.GetData().getSize() / 1000)) {
//            while (VRAM.isEmpty()) wait;
//            DataBatch db = VRAM.pop();
//            TrainBatch(db);
//            sum++;
//        }
        //model.train(data.size)
        return model;
    }

    /**
     * trains a databatch
     * (train=wait the appropriate amount of ticks)
     * @param db the databatch to train
     */
    private void TrainBatch(DataBatch db){

    }


}

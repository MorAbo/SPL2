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

    public Model model;

    private Cluster cluster;

    public LinkedList<DataBatch> Disk;

    private LinkedList<DataBatch> VRAM;

    public GPU(String type){
    //    this.type = type;
        cluster = Cluster.getInstance();
        Disk = new LinkedList<>();
        VRAM = new LinkedList<>();
        model = null;
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
    divided the data in model to batches of 1000 and
    stores it in the disk
     * @pre DISK.isEmpty();
     * @post DISK.size() == Math.ceil(model.data.size() / 1000)
     * @post DISK[i] == databatch(model.data, i * 1000)
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
     * @pre model.data.processed == 0
     * @post model.data.processed == model.data.size
     * @post
     */
    public void receiveProcessedData(Data d){
        //wait(time by the type)
        //data.proccess = size
        //model.data=data

    }

}

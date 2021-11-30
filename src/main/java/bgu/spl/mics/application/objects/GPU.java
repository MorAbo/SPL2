package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;
import com.sun.tools.javac.util.List;

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

    private GPUService ms;

    private List<DataBatch> Disk;


    public GPU(){
        //bus.register ms to train and to test
    }

    /*
    devided the data in model to baches of 1000 and
    stores it in the disk
     */
    public void devideData(){

    }

    /*
    sets the model we are currnetly working on
     */
    public void SetModel(){

    }

    /**
     * Sends chunks of unprocessed data from the model of batches of 1000 samples using DataBatch
     * to the cluster
     * GPU will only send data if it has room to store it when it returns
     */
    public void SendData(){

        //cluster.precessdata

    }

    /**
     * after the cluster completed the task it will set the future of TrainModelEvent
     * as complete.
     */
    public  void reciveProcesedData(Data d){
        //wait(time by the type)
        //model.data=data

    }








}

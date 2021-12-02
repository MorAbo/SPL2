package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    public int cores;
    public Queue<DataBatch> data;
    public Cluster cluster;

    public CPU(int cores){
        this.cores = cores;
        cluster = Cluster.getInstance();
        data = new LinkedList<>();
    }

    /**
     * adds the dataBatch to the list of data/
     * @param dataBatch
     *
     */
    public void receiveData(DataBatch dataBatch){

    }

    /**
     * process one of the batches.
     * @return
     */
    public DataBatch processData(){
        //d = databatch.pop()
        //d.data.processed += 1000
        //return d
        return null;
    }


}

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

    private int cores;
    private Queue<DataBatch> data;
    private Cluster cluster;
    private int tick;

    public CPU(int cores){
        this.cores = cores;
        cluster = Cluster.getInstance();
        data = new LinkedList<>();
        tick=1;
    }

    /**
     *increase the tick by 1
     * @pre (tick)==@post(tick)-1
     */
    public void IncreaseTick(){tick++;}

    public int dataInLine(){return data.size();}
    /**
     * adds the dataBatch to the list of data.
     * @param dataBatch to add to the list data
     *@post (data.size)=@pre(data.size)+1
     */
    public void receiveData(DataBatch dataBatch){

    }

    /**
     * process one of the batches.
     * @return DataBatch after processing it
     * @post data.size() = pre(data.size() - 1)
     * @post dataBatch.isProcessed()
     */
    public DataBatch processData(){
//        DataBatch d= data.remove();
//        int currentTick = tick;
//        while (tick!=currentTick+tick2wait(d.getData())) wait();
//        d.ProcessData();
//        return d;
        return null;
    }

    private int tick2wait(Data d){
        if(d.getType()== Data.Type.Images) return (32/cores)*4;
        if(d.getType()== Data.Type.Text) return (32/cores)*2;
        else return (32/cores);

    }


}

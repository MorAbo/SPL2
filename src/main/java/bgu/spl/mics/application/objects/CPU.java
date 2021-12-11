package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

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
    private int TimeLeft;
    private int tick;

    public CPU(int cores){
        this.cores = cores;
        cluster = Cluster.getInstance();
        data = new LinkedList<>();
        tick=1;
        TimeLeft=0;
        cluster.addCPU(this);
    }

    public int getFutureTimeLeft(DataBatch db){return TimeLeft+CalTime(db);}

    /**
     *increase the tick by 1
     * @pre (tick)==@post(tick)-1
     */
    public void IncreaseTick(){tick++; TimeLeft=Math.max(TimeLeft--, 0); notifyAll();}

    private int CalTime(DataBatch db){
        if(db.getType().equals("Image")) return (32/cores)*4;
        else if(db.getType().equals("Text")) return (32/cores)*2;
        else return (32/cores);
    }

    public int dataInLine(){return data.size();}
    /**
     * adds the dataBatch to the list of data.
     * @param dataBatch to add to the list data
     *@post (data.size)=@pre(data.size)+1
     */
    public void receiveData(DataBatch dataBatch){
        data.add(dataBatch);
        TimeLeft+=CalTime(dataBatch);
    }

    /**
     * process one of the batches.
     * @return DataBatch after processing it
     * @post data.size() = pre(data.size() - 1)
     * @post dataBatch.isProcessed()
     */
    public void processData() throws InterruptedException {
        DataBatch d= data.remove();
        int currentTick = tick;
        while (tick!=currentTick+CalTime(d)) {
            wait();
            cluster.IncreaseCpuRunTime();
        }

        d.ProcessData();
        cluster.ReturnProcessedData(d);
    }


}

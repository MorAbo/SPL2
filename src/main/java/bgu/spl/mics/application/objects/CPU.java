package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

import java.util.Base64;
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
    private Cluster cluster;
    private int tick;
    private DataBatch processingBatch;
    private int timeLeftToProcessBatch;

    public CPU(int cores){
        this.cores = cores;
        cluster = Cluster.getInstance();
        tick=1;
        cluster.addWaitingCpu(this);
        processingBatch=null;
        timeLeftToProcessBatch=0;
    }

    //public int getFutureTimeLeft(DataBatch db){return TimeLeft+CalTime(db);}

    /**
     *increase the tick by 1
     * @pre (tick)==@post(tick)-1
     */
    public void IncreaseTick(){
        tick++;
        timeLeftToProcessBatch=Math.max(0, timeLeftToProcessBatch--);
        if (timeLeftToProcessBatch==0 & processingBatch!=null) finishedBatch();
    }

    public int getCores(){return cores;}

    private int CalTime(DataBatch db){
        if(db.getType().equals("Images")) return (32/cores)*4;
        else if(db.getType().equals("Text")) return (32/cores)*2;
        else return (32/cores);
    }

    public void recieveUnprocessedBatch(DataBatch db){
        processingBatch=db;
        timeLeftToProcessBatch=CalTime(db);
    }

    public void finishedBatch(){
        processingBatch.ProcessData();
        cluster.RecieveProcessedDataBatch(processingBatch);
        DataBatch db = cluster.getNextDataBatchFromCluster();
        if (db!=null) recieveUnprocessedBatch(db);
        else cluster.addWaitingCpu(this);
    }
    /**
     * adds the dataBatch to the list of data.
     *@post (data.size)=@pre(data.size)+1
     */
    public void process(){
        try {
            if (!cluster.isThereDataToProcess()) synchronized (this){this.wait();}
                DataBatch db = cluster.getNextDataBatchFromCluster();
                while (db != null) {
                    cluster.removeWaitingCpu(this);
                    int currentTick = tick;
                    while (tick != currentTick + CalTime(db)) {
                        int i = 1;
                        synchronized (this) {
                            this.wait();
                        }
                        if (tick == currentTick + i) {
                            cluster.IncreaseCpuRunTime();
                            i++;
                        }
                    }
                    db.ProcessData();
                    cluster.RecieveProcessedDataBatch(db);
                    while (db != null) {
                        while (!cluster.isThereDataToProcess()) {
                            cluster.addWaitingCpu(this);
                            synchronized (this) {
                                this.wait();
                            }
                        }
                        db = cluster.getNextDataBatchFromCluster();
                    }
                }
                cluster.addWaitingCpu(this);
                synchronized (this) {
                    this.wait();
                }
        }
        catch (InterruptedException e){};
    }



}

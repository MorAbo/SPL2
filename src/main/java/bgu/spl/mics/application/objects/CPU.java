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
    private Cluster cluster;
    private int tick;

    public CPU(int cores){
        this.cores = cores;
        cluster = Cluster.getInstance();
        tick=1;
        cluster.addWaitingCpu(this);
    }

    //public int getFutureTimeLeft(DataBatch db){return TimeLeft+CalTime(db);}

    /**
     *increase the tick by 1
     * @pre (tick)==@post(tick)-1
     */
    public void IncreaseTick(){
        tick++;
        synchronized (this) {
            this.notify();
        }
    }

    public int getCores(){return cores;}

    private int CalTime(DataBatch db){
        if(db.getType().equals("Images")) return (32/cores)*4;
        else if(db.getType().equals("Text")) return (32/cores)*2;
        else return (32/cores);
    }

    /**
     * adds the dataBatch to the list of data.
     *@post (data.size)=@pre(data.size)+1
     */
    public void process(){
        try {
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

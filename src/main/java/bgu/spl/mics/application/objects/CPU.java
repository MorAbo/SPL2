package bgu.spl.mics.application.objects;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private int cores;
    private Cluster cluster;
    private DataBatch processingBatch;
    private int timeLeftToProcessBatch;

    public CPU(int cores){
        this.cores = cores;
        cluster = Cluster.getInstance();
        cluster.addWaitingCpu(this);
        processingBatch=null;
        timeLeftToProcessBatch=0;
    }

    //public int getFutureTimeLeft(DataBatch db){return TimeLeft+CalTime(db);}

    /**
     *increase the tick by 1 which causes:
     * 1) if we are in the middle of processing the cluster will increase its cpuRunTime
     * 2) if we are in the middle of processing well decrese the time left to procccess
     * 3) if time left to process is 0 well call finishedBatch function
     */
    public void IncreaseTick(){
        if(timeLeftToProcessBatch>0) cluster.IncreaseCpuRunTime();
        timeLeftToProcessBatch=timeLeftToProcessBatch-1;
        timeLeftToProcessBatch=Math.max(0, timeLeftToProcessBatch);
        if (timeLeftToProcessBatch==0 & processingBatch!=null) {
            finishedBatch();}
    }

    /**
     * calculate the time it will take for the data batch to be processed by the cpu
     * @param db= the databatch to be processed
     * @return= the time it will take to proccess @db
     */
    private int CalTime(DataBatch db){
        if(db.getType().equals("Images")) return (32/cores)*4;
        else if(db.getType().equals("Text")) return (32/cores)*2;
        else return (32/cores);
    }

    /**
     * recieves an unprocessed batch and update the processingBatch to be @db
     * in addition updated the timeLeftToProcessBatch and removes the cpu from
     * waitingCpu list in the cluster
     * @param db = the databatch to be proccessed
     */
    public void recieveUnprocessedBatch(DataBatch db){
        cluster.removeWaitingCpu(this);
        processingBatch=db;
        timeLeftToProcessBatch=CalTime(db);
    }

    /**
     * finished the time it takes to process processingBatch
     * well call processdata function of processingBatch and return the processed batch
     * to the cluster. in addition try to get a new batch to process from the clusted. if there is
     * none insert itself to the waiting cpulist in the cluster
     */
    private void finishedBatch(){
        processingBatch.ProcessData();
        cluster.RecieveProcessedDataBatch(processingBatch);
        DataBatch db = cluster.getNextDataBatchFromCluster();
        if (db!=null) recieveUnprocessedBatch(db);
        else {
            cluster.addWaitingCpu(this);
            timeLeftToProcessBatch = Integer.MAX_VALUE;
            processingBatch = null;
        }
    }
}

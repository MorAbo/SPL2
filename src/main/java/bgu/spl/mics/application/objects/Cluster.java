package bgu.spl.mics.application.objects;

import bgu.spl.mics.ReadWriteMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private List<GPU> gpuList;
	private List<CPU> cpuList;
	private ReadWriteMap<DataBatch, GPU> relevantGpu;
	private List<String> modelsTrained;
	private int dataBatchesProcessedByCPUs;
	private int cpuTimeUnitsUsed;
	private int gpuTimeUnitsUsed;


	private static class ClusterHolder{
		private final static Cluster INSTANCE = new Cluster();
	}
	private Cluster(){
		gpuList = new LinkedList<>();
		cpuList = new LinkedList<>();
		modelsTrained = new LinkedList<>();
		dataBatchesProcessedByCPUs = 0;
		cpuTimeUnitsUsed = 0;
		gpuTimeUnitsUsed = 0;
		relevantGpu=new ReadWriteMap<>(new HashMap<>());
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return ClusterHolder.INSTANCE;
	}


	//recieves unprossed data from cpu and start the sequence to process it
	public void processdata(DataBatch db, GPU gpu) {
		relevantGpu.put(db, gpu);
		CPU target = cpuList.get(0);
		int minTime= target.getFutureTimeLeft(db);
		for (CPU cpu: cpuList)
			if (cpu.getFutureTimeLeft(db)<minTime){
				minTime=cpu.getFutureTimeLeft(db);
				target=cpu;
			}
		target.receiveData(db);
	}

	public void ReturnProcessedData(DataBatch db){
		relevantGpu.get(db).receiveProcessedData(db);
	}

}

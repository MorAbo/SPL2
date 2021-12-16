package bgu.spl.mics.application.objects;

import bgu.spl.mics.ReadWriteList;
import bgu.spl.mics.ReadWriteMap;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private ReadWriteMap<GPU, ConcurrentLinkedQueue<DataBatch>> unprocessedMap;
	private ReadWriteMap<GPU, ConcurrentLinkedQueue<DataBatch>> processedMap;
	private ReadWriteMap<DataBatch, GPU> relevantGpu;
	private ReadWriteList<String> modelsTrained;
	private Integer dataBatchesProcessedByCPUs;
	private Integer cpuTimeUnitsUsed;
	private Integer gpuTimeUnitsUsed;
	private ReadWriteList<CPU> waitingCpu;




	private static class ClusterHolder{
		private final static Cluster INSTANCE = new Cluster();
	}

	private Cluster(){
		unprocessedMap=new ReadWriteMap<>(new HashMap<>());
		processedMap=new ReadWriteMap<>(new HashMap<>());
		relevantGpu=new ReadWriteMap<>(new HashMap<>());
		modelsTrained = new ReadWriteList<>();
		dataBatchesProcessedByCPUs = 0;
		cpuTimeUnitsUsed = 0;
		gpuTimeUnitsUsed = 0;
		waitingCpu= new ReadWriteList<>();

	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return ClusterHolder.INSTANCE;
	}

	public void IncreaseCpuRunTime() {
		synchronized (cpuTimeUnitsUsed){
		cpuTimeUnitsUsed++;}
	}
	public void IncreaseGpuRunTime() {
		synchronized (gpuTimeUnitsUsed){
		gpuTimeUnitsUsed++;}
	}
	public void IncreaceDataBatchesProccesed(){
		synchronized (dataBatchesProcessedByCPUs){dataBatchesProcessedByCPUs++;}
	}
	public void addTrainedModel(String name) {
		modelsTrained.add(name);
	}
	public int getCpuTimeUnitsUsed(){return cpuTimeUnitsUsed;}
	public int getDataBatchesProcessedByCPUs() {return dataBatchesProcessedByCPUs;}
	public int getGpuTimeUnitsUsed(){return gpuTimeUnitsUsed;}
	public void addWaitingCpu(CPU cpu) { waitingCpu.add(cpu); synchronized (this){this.notify();}}
	public void removeWaitingCpu(CPU cpu){waitingCpu.remove(cpu);}

	public void recieveUnprocessedDataBatch(DataBatch dataBatch, GPU gpu) {
		if (!processedMap.containsKey(gpu))
			processedMap.put(gpu, new ConcurrentLinkedQueue<>());
		if (!unprocessedMap.containsKey(gpu))
			unprocessedMap.put(gpu, new ConcurrentLinkedQueue<>());
		unprocessedMap.get(gpu).add(dataBatch);
		relevantGpu.put(dataBatch, gpu);
		if (unprocessedMap.containsKey(gpu))
		synchronized (unprocessedMap.get(gpu)) {
			synchronized (waitingCpu) {
				if (waitingCpu.size() != 0) {
					waitingCpu.get(0).recieveUnprocessedBatch(unprocessedMap.get(gpu).poll());
					if (unprocessedMap.get(gpu).isEmpty()) unprocessedMap.remove(gpu);
					///unprocessedMap.get(gpu).remove(dataBatch);
				}
			}
		}
	}


	public void RecieveProcessedDataBatch(DataBatch db) {
			GPU relevantGPU = relevantGpu.remove(db);
			if (relevantGPU.VramCapacityLeft() > 0)
				relevantGPU.receiveProcessedDataBatch(db);
			else processedMap.get(relevantGPU).add(db);
			IncreaceDataBatchesProccesed();
	}

	public LinkedList<DataBatch> GetProcessedData(GPU gpu){
		LinkedList<DataBatch> ans = new LinkedList<>();
		int count = 0;
		while (!processedMap.get(gpu).isEmpty() & gpu.VramCapacityLeft()>count){
			ans.add(processedMap.get(gpu).remove()); count++;}
		if (processedMap.get(gpu).isEmpty()& !unprocessedMap.containsKey(gpu))
			processedMap.remove(gpu);
		return ans;
	}

	public DataBatch getNextDataBatchFromCluster(){
		try {
			GPU gpu = chooseGpu();
			DataBatch db =unprocessedMap.get(gpu).remove();
			if (unprocessedMap.get(gpu).isEmpty()) unprocessedMap.remove(gpu);
			return db;
		} catch (Exception e){return null;}

	}

	private GPU chooseGpu(){
		Iterator<Map.Entry<GPU, ConcurrentLinkedQueue<DataBatch>>> it =unprocessedMap.getSet().iterator();
		GPU target=it.next().getKey();
		int minTime=target.getTime2train();
		while (it.hasNext()) {
			GPU temp = it.next().getKey();
			if (minTime > temp.getTime2train()) {
				minTime=temp.getTime2train();
				target=temp;
			}
		}
		return target;
	}

	public boolean isThereDataToProcess(){ return !unprocessedMap.isEmpty();}

}

package bgu.spl.mics.application.objects;

import bgu.spl.mics.ReadWriteList;
import bgu.spl.mics.ReadWriteMap;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

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


	public int getCpuTimeUnitsUsed(){return cpuTimeUnitsUsed;}
	public int getDataBatchesProcessedByCPUs() {return dataBatchesProcessedByCPUs;}
	public int getGpuTimeUnitsUsed(){return gpuTimeUnitsUsed;}

	public void addWaitingCpu(CPU cpu) { waitingCpu.add(cpu); synchronized (this){this.notify();}}
	public void removeWaitingCpu(CPU cpu){waitingCpu.remove(cpu);}

	//increases cpuRunTime by 1
	public void IncreaseCpuRunTime() {
		synchronized (cpuTimeUnitsUsed){cpuTimeUnitsUsed++;}
	}
	//increases GpuRunTime by 1
	public void IncreaseGpuRunTime() {
		synchronized (gpuTimeUnitsUsed){gpuTimeUnitsUsed++;}
	}
	//increases dataBatchesProccessed by 1
	private void IncreaceDataBatchesProccesed(){
		synchronized (dataBatchesProcessedByCPUs){dataBatchesProcessedByCPUs++;}
	}


	/**
	 * gets an unprocessed databatch (from GPU) and adds it to unprocessed map and
	 * creates a new list for the processed map for the data to go back into.
	 * in addition adds a pair to the relevantGPU map so the databatch going back would know
	 * whoms is it.
	 * if the waiting gpu list is not empty (there are cpus waiting to get batches) we'll
	 * send one of them a batche from the unprocessed list of @gpu
	 * @param dataBatch = databatch the cluster received from the gpu
	 * @param gpu = the gpu who sent the batch
	 */
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
					}
				}
			}
	}

	/**
	 * recieves a prossed databatch from a cpu.
	 * we'll find the relavent gpu whos databatch belongs to and if the gpu's VRAM
	 * has place well send the batch to the GPU, othewise we'll add the batch to the
	 * processed map in the key of the gpu.
	 * in addition we'll increase the number of batched processed
	 * @param db = the databatch we recieved from the cpu
	 */
	public void RecieveProcessedDataBatch(DataBatch db) {
			GPU relevantGPU = relevantGpu.remove(db);
			if (relevantGPU.VramCapacityLeft() > 0)
				relevantGPU.receiveProcessedDataBatch(db);
			else processedMap.get(relevantGPU).add(db);
			IncreaceDataBatchesProccesed();
	}

	/**
	 * choose the most precent attach to train by how long will it take for its gpu to finished training its remaining batches.
	 * and removes the batch from the unproccessed map of the chosen gpu
	 * @return the batch to process
	 */
	public DataBatch getNextDataBatchFromCluster(){
		try {
			GPU gpu = chooseGpu();
			DataBatch db =unprocessedMap.get(gpu).remove();
			if (unprocessedMap.get(gpu).isEmpty()) unprocessedMap.remove(gpu);
			return db;
		} catch (Exception e){return null;}

	}

	/**
	 * chooses the most precent gpu to proccess a batch from
	 * @return the gpu we found.
	 */
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
}

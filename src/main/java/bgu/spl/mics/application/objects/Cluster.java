package bgu.spl.mics.application.objects;

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
	private ConcurrentLinkedQueue<CPU> UnoccupiedCpu;


	private List<String> modelsTrained;
	private int dataBatchesProcessedByCPUs;
	private int cpuTimeUnitsUsed;
	private int gpuTimeUnitsUsed;


	private static class ClusterHolder{
		private final static Cluster INSTANCE = new Cluster();
	}

	private Cluster(){
		unprocessedMap=new ReadWriteMap<>(new HashMap<>());
		processedMap=new ReadWriteMap<>(new HashMap<>());
		relevantGpu=new ReadWriteMap<>(new HashMap<>());
		UnoccupiedCpu = new ConcurrentLinkedQueue<>();
		modelsTrained = new LinkedList<>();
		dataBatchesProcessedByCPUs = 0;
		cpuTimeUnitsUsed = 0;
		gpuTimeUnitsUsed = 0;

	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return ClusterHolder.INSTANCE;
	}

	public void IncreaseCpuRunTime() {
		cpuTimeUnitsUsed++;
	}
	public void IncreaseGpuRunTime() {
		gpuTimeUnitsUsed++;
	}
	public void addTrainedModel(String name) {
		modelsTrained.add(name);
	}


	public void addCPU(CPU cpu){UnoccupiedCpu.add(cpu); notifyAll();}


	public void recieveUnprocessedDataBatch(DataBatch dataBatch, GPU gpu){
		if (!processedMap.containsKey(gpu))
			processedMap.put(gpu, new ConcurrentLinkedQueue<>());
		if (!unprocessedMap.containsKey(gpu))
			unprocessedMap.put(gpu,new ConcurrentLinkedQueue<>());
		unprocessedMap.get(gpu).add(dataBatch);
		relevantGpu.put(dataBatch,gpu);
		notifyAll();

	}


	public void RecieveProcessedDataBatch(DataBatch db) {
		GPU relevantGPU= relevantGpu.get(db);
		if (relevantGPU.VramCapacityLeft()>0)
			relevantGPU.receiveProcessedDataBatch(db);
		else processedMap.get(relevantGPU).add(db);
		relevantGpu.remove(db);
		unprocessedMap.get(relevantGPU).remove(db);
		if (unprocessedMap.get(relevantGPU).isEmpty())
			unprocessedMap.remove(relevantGPU);
		dataBatchesProcessedByCPUs++;
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

	public void Act(){
		try {
			while (unprocessedMap.isEmpty()) wait();
			GPU gpu = chooseGpu();
			DataBatch db = unprocessedMap.get(gpu).remove();
			while (UnoccupiedCpu.isEmpty()) wait();
			CPU cpu = chooseCpu();
			UnoccupiedCpu.remove(cpu);
			cpu.receiveUnprocessedDataBatch(db);
		} catch (InterruptedException e){}

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

	private CPU chooseCpu(){
		CPU target = UnoccupiedCpu.peek();
		int mincores=target.getCores();
		for (CPU cpu: UnoccupiedCpu){
			if (cpu.getCores()<mincores){
				mincores=cpu.getCores();
				target=cpu;
			}
		}
		return target;
	}


}

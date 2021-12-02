package bgu.spl.mics.application.objects;

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
	private List<String> modelsTrained;
	private int dataBatchesProcessedByCPUs;
	private int cpuTimeUnitsUsed;
	private int gpuTimeUnitsUsed;

	private final static Cluster INSTANCE = new Cluster();

	private Cluster(){
		gpuList = new LinkedList<>();
		cpuList = new LinkedList<>();
		modelsTrained = new LinkedList<>();
		dataBatchesProcessedByCPUs = 0;
		cpuTimeUnitsUsed = 0;
		gpuTimeUnitsUsed = 0;
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return INSTANCE;
	}

	public void fromGpuToCpu(DataBatch dataBatch){

	}

//	public void fromCpuToGpu();
}

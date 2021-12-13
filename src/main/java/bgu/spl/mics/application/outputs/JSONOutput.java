package bgu.spl.mics.application.outputs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSONOutput {

    private List<StudentOutput> studentOutputs;
    private List<ConferenceOutput> conferenceOutputs;
    private int cpuTimeUsed;
    private int gpuTimeUsed;
    private int batchesProcessed;

    public JSONOutput(List<StudentOutput> studentOutputs, List<ConferenceOutput> conferenceOutputs, int cpuTimeUsed, int gpuTimeUsed, int batchesProcessed){
        this.studentOutputs = studentOutputs;
        this.conferenceOutputs = conferenceOutputs;
        this.cpuTimeUsed = cpuTimeUsed;
        this.gpuTimeUsed = gpuTimeUsed;
        this.batchesProcessed = batchesProcessed;
    }

    public void buildJson(){
        JsonObject jsonObject = new JsonObject();
        JsonArray studentArray = new JsonArray();
        for (StudentOutput studentOutput: studentOutputs) {
            studentArray.add(studentOutput.toJson());
        }
        jsonObject.add("students", studentArray);
        JsonArray conArray = new JsonArray();
        for (ConferenceOutput conferenceOutput: conferenceOutputs) {
            conArray.add(conferenceOutput.toJson());
        }
        jsonObject.add("conferences", conArray);
        jsonObject.addProperty("cpuTimeUsed", cpuTimeUsed);
        jsonObject.addProperty("gpuTimeUsed", gpuTimeUsed);



        jsonObject.addProperty("batchesProcessed", batchesProcessed);

        try (FileWriter file = new FileWriter("assignment2.json")){
            file.write(jsonObject.toString());
            file.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}

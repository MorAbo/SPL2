package bgu.spl.mics.application.outputs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarEntry;

public class JSONOutput {

    private static class JsonOutputHolder{
        private static JSONOutput instance= new JSONOutput();

    }

    private List<StudentOutput> studentOutputs;
    private List<ConferenceOutput> conferenceOutputs;
    private int cpuTimeUsed;
    private int gpuTimeUsed;
    private int batchesProcessed;

    private JSONOutput(){
        this.studentOutputs = null;
        this.conferenceOutputs = null;
        this.cpuTimeUsed = -1;
        this.gpuTimeUsed = -1;
        this.batchesProcessed = -1;
    }

    public void addStudentOutputs(StudentOutput studentOutput){ studentOutputs.add(studentOutput);}
    public void addConferenceOutputs(ConferenceOutput conferenceOutput){ conferenceOutputs.add(conferenceOutput);}
    public void setCpuTimeUsed(int cpuTimeUsed){this.cpuTimeUsed=cpuTimeUsed;}
    public void setGpuTimeUsed(int gpuTimeUsed){this.gpuTimeUsed=gpuTimeUsed;}
    public void setBatchesProcessed(int batchesProcessed){this.batchesProcessed=batchesProcessed;}

    public static JSONOutput GetInstance(){
        return JsonOutputHolder.instance;
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

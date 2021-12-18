package bgu.spl.mics.application.outputs;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.ReadWriteList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;

public class JSONOutput {

    private static class JsonOutputHolder{
        private static JSONOutput instance= new JSONOutput();

    }

    private ReadWriteList<StudentOutput> studentOutputs;
    private ReadWriteList<ConferenceOutput> conferenceOutputs;
    private int cpuTimeUsed;
    private int gpuTimeUsed;
    private int batchesProcessed;

    private JSONOutput(){
        this.studentOutputs = new ReadWriteList<>();
        this.conferenceOutputs = new ReadWriteList<>();
        this.cpuTimeUsed = -1;
        this.gpuTimeUsed = -1;
        this.batchesProcessed = -1;
    }

    public void addStudentOutputs(StudentOutput studentOutput){
        synchronized (studentOutput){ studentOutputs.add(studentOutput);}}
    public void addConferenceOutputs(ConferenceOutput conferenceOutput){
        synchronized (conferenceOutputs){conferenceOutputs.add(conferenceOutput);}}
    public void setCpuTimeUsed(int cpuTimeUsed){this.cpuTimeUsed=cpuTimeUsed;}
    public void setGpuTimeUsed(int gpuTimeUsed){this.gpuTimeUsed=gpuTimeUsed;}
    public void setBatchesProcessed(int batchesProcessed){this.batchesProcessed=batchesProcessed;}

    public static JSONOutput GetInstance(){
        return JsonOutputHolder.instance;
    }

    public synchronized void buildJson(){
        JsonObject jsonObject = new JsonObject();
        JsonArray studentArray = new JsonArray();
            for (int i= 0; i<studentOutputs.size(); i++) {
                studentArray.add(studentOutputs.get(i).toJson());
            }
        jsonObject.add("students", studentArray);
        JsonArray conArray = new JsonArray();
        for (int i=0; i<conferenceOutputs.size(); i++) {
            conArray.add(conferenceOutputs.get(i).toJson());
        }
        jsonObject.add("conferences", conArray);
        jsonObject.addProperty("cpuTimeUsed", cpuTimeUsed);
        jsonObject.addProperty("gpuTimeUsed", gpuTimeUsed);
        jsonObject.addProperty("batchesProcessed", batchesProcessed);

        List<String> jsonList = new LinkedList<>();
        String jsonString = jsonObject.toString();
        int tab = 0;
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < jsonString.length(); i++) {
//            if (jsonString.charAt(i) != ){
            temp.append(jsonString.charAt(i));
//            }
            if (jsonString.charAt(i) == '[' | jsonString.charAt(i) == '{'){
                String tabString = " ";
                String repeated = new String(new char[tab]).replace("\0", tabString);
                temp.insert(0, repeated);
                tab += 4;
                jsonList.add(temp.toString());
                temp = new StringBuilder();
            }
            else if (jsonString.charAt(i) == ']' | jsonString.charAt(i) == '}'){
                String tabString = " ";
                String repeated = new String(new char[tab]).replace("\0", tabString);
                temp.insert(0, repeated);
                tab -= 4;
                jsonList.add(temp.toString());
                temp = new StringBuilder();
            }
            else if (jsonString.charAt(i) == ','){
                String tabString = " ";
                String repeated = new String(new char[tab]).replace("\0", tabString);
                temp.insert(0, repeated);
                jsonList.add(temp.toString());
                temp = new StringBuilder();
            }
        }

        try (FileWriter file = new FileWriter("assignment2.txt")){
            for (String s : jsonList) {
                file.write(s);
                file.write("\n");
            }
            file.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
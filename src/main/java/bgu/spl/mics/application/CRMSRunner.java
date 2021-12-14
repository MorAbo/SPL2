package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.outputs.JSONOutput;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {

    private static ArrayList<Thread> threads = new ArrayList<Thread>();

    public static void main(String[] args) {
        try {
            initializeBus(args[0]);
            for (Thread t : threads) {t.start();}
            for (Thread t : threads) {t.join();}
//            Thread timethread = threads.get(16);
//            Thread confThread= threads.get(12);
//            Thread confThread2 = threads.get(11);
//            Thread CPUThread = threads.get(4);
//            Thread GpuThread = threads.get(1);
//            Thread studentThread = threads.get(18);
//            timethread.start();
//            confThread.start();
//            confThread2.start();
//            GpuThread.start();
//            CPUThread.start();
//            studentThread.start();
//            timethread.join();
//            confThread.join();
//            confThread2.join();
//            GpuThread.join();
//            CPUThread.join();
//            studentThread.join();
        } catch (InterruptedException e){e.printStackTrace();}
        JSONOutput.GetInstance().setCpuTimeUsed(Cluster.getInstance().getCpuTimeUnitsUsed());
        JSONOutput.GetInstance().setBatchesProcessed(Cluster.getInstance().getDataBatchesProcessedByCPUs());
        JSONOutput.GetInstance().setGpuTimeUsed(Cluster.getInstance().getGpuTimeUnitsUsed());
        JSONOutput.GetInstance().buildJson();
    }

    private static void initializeBus(String arg){
        try {
            MessageBusImpl messageBus = MessageBusImpl.GetInstance();

            JsonParser parser = new JsonParser();
            Object obj = parser.parse(new FileReader(arg));
            JsonObject jsonObject = (JsonObject) obj;

            JsonArray studentsArray = (JsonArray) jsonObject.get("Students");

            JsonArray gpusArray = (JsonArray) jsonObject.get("GPUS");

            JsonArray cpusArray = (JsonArray) jsonObject.get("CPUS");

            JsonArray conferencesArray = (JsonArray) jsonObject.get("Conferences");

            int tickTime = jsonObject.get("TickTime").getAsInt();

            int duration = jsonObject.get("Duration").getAsInt();

            parseGpus(gpusArray);
            parseCpus(cpusArray);
            parseConferences(conferencesArray);
            TimeService timeService = new TimeService(tickTime, duration);
            threads.add(new Thread(timeService, "time service"));
            parseStudents(studentsArray);

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void parseStudents(JsonArray studentsArray) {
        MessageBusImpl messageBus = MessageBusImpl.GetInstance();
        int i=0;
        for (JsonElement studentElement : studentsArray) {
            JsonObject JSonStudent = studentElement.getAsJsonObject();
            String name = JSonStudent.get("name").getAsString();
            String department = JSonStudent.get("department").getAsString();
            String status = JSonStudent.get("status").getAsString();
            Student student = new Student(name, department, status);
            StudentService studentService = new StudentService("student service", student);
            JsonArray modelsArray = (JsonArray) JSonStudent.get("models");
            for (JsonElement modelElement : modelsArray) {
                JsonObject JSonModel = modelElement.getAsJsonObject();
                String modelName = JSonModel.get("name").getAsString();
                String type = JSonModel.get("type").getAsString();
                int size = JSonModel.get("size").getAsInt();
                Data data = new Data(type, size);
                Model model = new Model(modelName, data, student);
                student.addModel(model);
            }
            threads.add(new Thread(studentService, "student"+i));
            i++;
        }
    }

    private static void parseGpus(JsonArray gpusArray){
        MessageBusImpl messageBus = MessageBusImpl.GetInstance();
        int i=0;
        for (JsonElement gpuElement : gpusArray){
            String gpuType = gpuElement.getAsString();
            GPU gpu = new GPU(gpuType);
            GPUService gpuService = new GPUService("gpu service", gpu);
            threads.add(new Thread(gpuService, "gpu"+i));
            i++;

        }
    }

    private static void parseCpus(JsonArray cpusArray){
        MessageBusImpl messageBus = MessageBusImpl.GetInstance();
        int i=0;
        for (JsonElement cpuElement : cpusArray){
            int cpuSize = cpuElement.getAsInt();
            CPU cpu = new CPU(cpuSize);
            CPUService cpuService = new CPUService("cpu service", cpu);
            threads.add(new Thread(cpuService, "cpu"+i));
            i++;
        }
    }

    private static void parseConferences(JsonArray conferencesArray){
        MessageBusImpl messageBus = MessageBusImpl.GetInstance();
        int oldDate=1;
        int i=0;
        for (JsonElement conferenceElement : conferencesArray){
            JsonObject JSonConference = conferenceElement.getAsJsonObject();
            String name = JSonConference.get("name").getAsString();
            int date = JSonConference.get("date").getAsInt();
            ConfrenceInformation confrenceInformation = new ConfrenceInformation(name, date, oldDate);
            oldDate=date;
            ConferenceService conferenceService = new ConferenceService("conference service", confrenceInformation);
            threads.add(new Thread(conferenceService, "conferance"+i));
            i++;
        }
    }

}

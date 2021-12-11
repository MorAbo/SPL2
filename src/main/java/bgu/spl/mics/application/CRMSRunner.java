package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        initializeBus(args[0]);
    }

    private static void initializeBus(String arg){
        try {
            MessageBusImpl messageBus = MessageBusImpl.GetInstance();

            JsonParser parser = new JsonParser();
            Object obj = parser.parse(new FileReader(arg));
            JsonObject jsonObject = (JsonObject) obj;

            JsonArray studentsArray = (JsonArray) jsonObject.get("Students");
            parseStudents(studentsArray);

            JsonArray gpusArray = (JsonArray) jsonObject.get("GPUS");
            parseGpus(gpusArray);

            JsonArray cpusArray = (JsonArray) jsonObject.get("CPUS");
            parseCpus(cpusArray);

            JsonArray conferencesArray = (JsonArray) jsonObject.get("Conferences");
            parseConferences(conferencesArray);

            int tickTime = jsonObject.get("TickTime").getAsInt();

            int duration = jsonObject.get("Duration").getAsInt();

            TimeService timeService = new TimeService(tickTime, duration);
            messageBus.register(timeService);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void parseStudents(JsonArray studentsArray) {
        MessageBusImpl messageBus = MessageBusImpl.GetInstance();
        for (JsonElement studentElement : studentsArray) {
            JsonObject JSonStudent = studentElement.getAsJsonObject();
            String name = JSonStudent.get("name").getAsString();
            String department = JSonStudent.get("department").getAsString();
            String status = JSonStudent.get("status").getAsString();
            Student student = new Student(name, department, status);
            StudentService studentService = new StudentService("student service", student);
            messageBus.register(studentService);
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
        }
    }

    private static void parseGpus(JsonArray gpusArray){
        MessageBusImpl messageBus = MessageBusImpl.GetInstance();
        for (JsonElement gpuElement : gpusArray){
            String gpuType = gpuElement.getAsString();
            GPU gpu = new GPU(gpuType);
            GPUService gpuService = new GPUService("gpu service", gpu);
            messageBus.register(gpuService);
        }
    }

    private static void parseCpus(JsonArray cpusArray){
        MessageBusImpl messageBus = MessageBusImpl.GetInstance();
        for (JsonElement cpuElement : cpusArray){
            int cpuSize = cpuElement.getAsInt();
            CPU cpu = new CPU(cpuSize);
            CPUService cpuService = new CPUService("cpu service", cpu);
            messageBus.register(cpuService);
        }
    }

    private static void parseConferences(JsonArray conferencesArray){
        MessageBusImpl messageBus = MessageBusImpl.GetInstance();
        int oldDate=1;
        for (JsonElement conferenceElement : conferencesArray){
            JsonObject JSonConference = conferenceElement.getAsJsonObject();
            String name = JSonConference.get("name").getAsString();
            int date = JSonConference.get("date").getAsInt();
            ConfrenceInformation confrenceInformation = new ConfrenceInformation(name, date, oldDate);
            oldDate=date;
            ConferenceService conferenceService = new ConferenceService("conference service", confrenceInformation);
            messageBus.register(conferenceService);
        }
    }

}

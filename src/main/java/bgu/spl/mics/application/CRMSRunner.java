package bgu.spl.mics.application;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.TimeService;
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
        JsonParser parser = new JsonParser();
        try {
            MessageBus messageBus = MessageBusImpl.GetInstance();

            Object obj = parser.parse(new FileReader(args[0]));
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

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void parseStudents(JsonArray studentsArray) {
        for (JsonElement studentElement : studentsArray) {
            JsonObject JSonStudent = studentElement.getAsJsonObject();
            String name = JSonStudent.get("name").getAsString();
            String department = JSonStudent.get("department").getAsString();
            String status = JSonStudent.get("status").getAsString();
            Student student = new Student(name, department, status);
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
        for (JsonElement gpuElement : gpusArray){
            String gpuType = gpuElement.getAsString();
            GPU gpu = new GPU(gpuType);
        }
    }

    private static void parseCpus(JsonArray cpusArray){
        for (JsonElement cpuElement : cpusArray){
            int cpuSize = cpuElement.getAsInt();
            CPU cpu = new CPU(cpuSize);
        }
    }

    private static void parseConferences(JsonArray conferencesArray){
        for (JsonElement conferenceElement : conferencesArray){
            JsonObject JSonConference = conferenceElement.getAsJsonObject();
            String name = JSonConference.get("name").getAsString();
            int date = JSonConference.get("date").getAsInt();
            ConfrenceInformation confrenceInformation = new ConfrenceInformation(name, date);
        }
    }

}

package bgu.spl.mics.application.outputs;

import bgu.spl.mics.application.objects.Data;
import com.google.gson.JsonObject;

public class ModelOutput {

    private String name;
    private Data data;
    private String status;
    private String result;

    public ModelOutput(String name, Data data, String status, String result){
        this.name = name;
        this.data = data;
        this.status = status;
        this.result = result;
    }

    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        JsonObject dataObject = new JsonObject();
        dataObject.addProperty("type", data.getType());
        dataObject.addProperty("size", data.getSize());
        jsonObject.addProperty("data", String.valueOf(dataObject));
        jsonObject.addProperty("status", status);
        jsonObject.addProperty("results", result);
        return jsonObject;
    }
}

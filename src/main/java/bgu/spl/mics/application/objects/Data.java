package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;

    public Data(String type, int size){
        this.size = size;
        processed = 0;
        setType(type);
    }

    public Type getType(){ return type;}
    public int getProcessed(){return processed;}
    public int getSize(){return size;}

    public void ProcessData(int amount){ processed+=amount; }

    private void setType(String resultString){
        switch (resultString) {
            case "Images":
                type = Type.Images;
                break;
            case "Text":
                type = Type.Text;
                break;
            case "Tabular":
                type = Type.Tabular;
                break;
        }
    }

}

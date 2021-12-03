import bgu.spl.mics.application.objects.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GPUTest {

    GPU gpu3090;
    GPU gpu2080;
    GPU gpu1080;
    Model model;

    @BeforeEach
    void setUp() {
        gpu3090 = new GPU("RTX3090");
        gpu2080 = new GPU("RTX2080");
        gpu1080 = new GPU("GTX1080");
        model = new Model("model1",
                new Data("Images", 3000),
                new Student("studen 1", "CS", "PhD"));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void divideData() {
        gpu3090.setModel(model);
        gpu3090.divideData();
        assertEquals(gpu3090.DiskCapacity(), Math.ceil((float)(model.GetData().getSize()) / 1000));
    }

    @Test
    void sendData() {
        gpu3090.setModel(model);
        gpu3090.divideData();
        gpu3090.SendData();
        assertEquals(gpu3090.DiskCapacity(), 0);
    }

    @Test
    void reciveProcessedData() {
        int oldvramcapacity = gpu3090.VramCapacityLeft();
        gpu3090.receiveProcessedData(new DataBatch(new Data("Images",1000), 0));
        assertEquals(gpu3090.VramCapacityLeft(), oldvramcapacity+1);
    }

    @Test
    void Train() {
        gpu3090.setModel(model);
        Model m = gpu3090.Train();
        assertEquals(m.GetData().getProcessed(), model.GetData().getSize());

    }


}
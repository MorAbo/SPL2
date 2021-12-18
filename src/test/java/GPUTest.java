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
    DataBatch dataBatch;
    DataBatch dataBatch2;

    @BeforeEach
    void setUp() {
        gpu3090 = new GPU("RTX3090");
        gpu2080 = new GPU("RTX2080");
        gpu1080 = new GPU("GTX1080");
        model = new Model("model1",
                new Data("Images", 3000),
                new Student("student 1", "CS", "PhD"));
        dataBatch = new DataBatch(new Data("Images", 3000), 0);
        dataBatch2 = new DataBatch(new Data("Images", 3000), 0);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void VramCapacityLeft() {
        int capacity = gpu3090.VramCapacityLeft();
        assertEquals(32, capacity);
    }

    @Test
    void IncreaseTick() {
        gpu3090.receiveProcessedDataBatch(dataBatch);
        int timeLeftForBatch = gpu3090.getTime2train();
        gpu3090.IncreaseTick();
        assertEquals(timeLeftForBatch - 1, gpu3090.getTimeLeftForBatch());
    }

    @Test
    void receiveProcessedDataBatch() {
        gpu3090.receiveProcessedDataBatch(dataBatch);
        int timeToTrain = gpu3090.getTime2train();
        gpu3090.receiveProcessedDataBatch(dataBatch2);
        assertEquals(timeToTrain * 2, gpu3090.getTime2train());
    }

    @Test
    void Train() {
        gpu3090.Train(model);
        assertEquals("Training", model.getStatus());
    }


}
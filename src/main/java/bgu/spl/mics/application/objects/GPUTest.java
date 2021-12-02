package bgu.spl.mics.application.objects;

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
        gpu3090 = new GPU(GPU.Type.RTX3090);
        gpu2080 = new GPU(GPU.Type.RTX2080);
        gpu1080 = new GPU(GPU.Type.GTX1080);
        model = new Model();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setModel() {
        gpu3090.setModel(model);
        assertEquals(model, gpu3090.model);
    }

    @Test
    void divideData() {
        gpu3090.setModel(model);
        gpu3090.divideData();
        assertEquals(gpu3090.Disk.size(), Math.ceil(model.data.getSize() / 1000));
        for (int i = 0; i < gpu3090.Disk.size(); i++) {
            assertEquals(gpu3090.Disk.get(i), new DataBatch(model.data, 1000 * i));
        }
    }

    @Test
    void sendData() {
        gpu3090.setModel(model);
        gpu3090.divideData();
        gpu3090.SendData();
        assertTrue(gpu3090.Disk.isEmpty());
    }

    @Test
    void reciveProcesedData() {
    }

}

import bgu.spl.mics.application.objects.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {

    CPU cpu;
    DataBatch db;
    Cluster cluster;
    GPU gpu;

    @BeforeEach
    void setUp() {
        cpu= new CPU(2);
        db = new DataBatch(new Data("Images", 1000), 0);
        cluster = Cluster.getInstance();
        gpu = new GPU("RTX3090");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void IncreaseTick(){
        cpu.recieveUnprocessedBatch(db);
        int oldTick = cpu.getTimeLeftToProcessBatch();
        cpu.IncreaseTick();
        assertEquals(oldTick - 1, cpu.getTimeLeftToProcessBatch());
    }

    @Test
    void recieveUnprocessedBatch() {
        cpu.recieveUnprocessedBatch(db);
        assertEquals(db, cpu.getProcessingBatch());
    }

}

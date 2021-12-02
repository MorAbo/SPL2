package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {

    CPU cpu;

    @BeforeEach
    void setUp() {
        cpu= new CPU(2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void receiveData() {
        int oldDataAmount = cpu.dataInLine();
        cpu.receiveData(new DataBatch(new Data("Images", 1000), 0));
        assertEquals(cpu.dataInLine(), oldDataAmount+1);
    }

    @Test
    void processData() {
        cpu.receiveData(new DataBatch(new Data("Images", 1000), 0));
        DataBatch db = cpu.processData();
        assertEquals(db.getData().getProcessed(), db.getData().getSize());
    }

}
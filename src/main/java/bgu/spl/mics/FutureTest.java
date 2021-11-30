package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FutureTest {

    Future f;
    @BeforeEach
    void setUp() {
        f=new Future<Integer>();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void get() {
        assertEquals(f.get(), f.result);
    }

    @Test
    void resolve() {
        f.resolve(8);
        assertEquals(f.get(), f.result);
    }

    @Test
    void isDone() {
        assertEquals(f.isDone(),false);
        f.resolve(8);
        assertEquals(f.isDone(),true);
    }

    @Test
    void testGet() {
        //test 1: result un available- waits ans then still unavailable return null

        //test 2: result un available- waits ans then available

        //test 3: available

    }
}
package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
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
    void resolve() {
        f.resolve(8);
        assertEquals(f.get(), f.get());
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
        Integer result = (Integer) f.get(1000, TimeUnit.MILLISECONDS);
        assertEquals(result, null);
        //test 2: result un available- waits ans then available
        Thread a = new Thread( ()->{ assertEquals((Integer) f.get(1000, TimeUnit.MILLISECONDS),8);});
        Thread b  = new Thread(()->{ f.resolve(8);});
        a.start();
        try {wait(100);} catch (Exception ex){}
        b.start();
        try {a.join(); b.join();} catch (Exception e){}
        //test 3: available
        assertEquals((Integer) f.get(1000, TimeUnit.MILLISECONDS),8);
    }


}
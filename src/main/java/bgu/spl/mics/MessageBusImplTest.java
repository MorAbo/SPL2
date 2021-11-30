package bgu.spl.mics;

import static org.junit.jupiter.api.Assertions.*;

import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.services.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class MessageBusImplTest {

    MessageBus mb;

    @BeforeEach
    void setUp() {
        mb= MessageBusImpl.GetInstance();
    }

    @AfterEach
    void tearDown() {
        //for each ms in microservices
        //     ms.unregister
    }

    @Test

    void subscribeEvent() {
        //test 1: cannot subscribe an un registered microservice
        MicroService ms = new StudentService("Student 1");
        try {
            mb.subscribeEvent(TrainModelEvent.class, ms);
            fail("Exception expected!");
        }
        catch (Exception e){
            assertTrue(true);
        }
        //test 2: subscribing successfully
        mb.register(ms);
        mb.subscribeEvent(TrainModelEvent.class, ms);
        assertTrue();   //checking ms is in the traingmodelevent list
        //test3: resubscribing
        mb.subscribeEvent(TrainModelEvent.class, ms);
        assertEquals();     //checking ms is in the traingmodelevent list only once
        //test 4: subscribing to a different event
        mb.subscribeEvent(TestModelEvent.class, ms);
        assertEquals();  //checking ms is in the TestModelEvent list and in traingmodelevent list
        //test 5: subsribing to an already existing event
        MicroService ms2 = new StudentService("Student 2");
        mb.subscribeEvent(TrainModelEvent.class, ms2);
        assertEquals();     //checking ms and ms2 is in the traingmodelevent list
    }

    @Test
    void subscribeBroadcast() {
        //test 1: cannot subscribe an un registered microservice
        MicroService ms = new StudentService("Student 1");
        try {
            mb.subscribeBroadcast(TickBroadcast.class, ms);
            fail("Exception expected!");
        }
        catch (Exception e){
            assertTrue(true);
        }
        //test 2: subscribing successfully
        mb.register(ms);
        mb.subscribeBroadcast(TickBroadcast.class, ms);
        assertEquals();     //checking ms is in the TickBroadcast list
        //test3: resubscribing
        mb.subscribeBroadcast(TickBroadcast.class, ms);
        assertEquals();     //checking ms is in the TickBroadcast list only once
        //test 4: subscribing to a different event
        mb.subscribeBroadcast(PublishConferenceBroadcast.class, ms);
        assertEquals();     //checking ms is in the TickBroadcast list and in PublishConferenceBroadcast list
        //test 5: subsribing to an already existing event
        MicroService ms2 = new StudentService("Student 2");
        mb.subscribeBroadcast(TickBroadcast.class, ms);
        assertEquals();     //checking ms and ms2 is in the TickBroadcast list only once
    }

    @Test
    void complete() {
        //test 1: the future object in the event is with result T
        TrainModelEvent e = new TrainModelEvent();
        mb.complete(e,7);
        assertEquals(e.f.result,7);
        //test 2: twice complete on the same event result in an error
        try {
            mb.complete(e,7);
            fail("Exception expected!");
        }
        catch (Exception ex){
            assertTrue(true);
        }
    }

    @Test
    void sendBroadcast() {
        //test 1: each ms thats registered to the broadcast its in their queue
        MicroService m1 = new StudentService("S1");
        mb.register(m1);
        mb.subscribeBroadcast(TickBroadcast.class, m1);
        Broadcast b = new TickBroadcast();
        mb.sendBroadcast(b);
        assertEquals();     //checking b is in m1 queue
        //test 2: each ms not registered to the broadcast its not in their queue
        MicroService m2 = new StudentService("S2");
        mb.sendBroadcast(b);
        assertEquals();     //checking b is in m1 queue and not in m2 queue
        //test 3: if broadcast not in Broadcast dictionary, do nothing
        Broadcast b1 = new PublishConferenceBroadcast();
        mb.sendBroadcast(b1);
        assertEquals();     //checking b1 is not in m2 or m1 queue
    }

    @Test
    void sendEvent() {
        //test 1: the ms which is its turn recieves the message in its queue
        MicroService m1 = new StudentService("S1");
        mb.register(m1);
        mb.subscribeEvent(TrainModelEvent.class, m1);
        MicroService m2 = new StudentService("S2");
        mb.register(m2);
        MicroService m3 = new StudentService("S3");
        mb.register(m3);
        mb.subscribeEvent(TrainModelEvent.class, m2);
        Event e = new TrainModelEvent();
        mb.sendEvent(e);
        assertEquals();     //checking e is in m1 queue
        Event e1 = new TrainModelEvent();
        mb.sendEvent(e1);
        assertEquals();     //checking e is in m2 queue
        Event e2 = new TrainModelEvent();
        mb.sendEvent(e2);
        assertEquals();     //checking e is in m1 queue
        //test 2: no other ms got the message
        assertEquals();     //checking m3 queue is empty
        //test 3: if no ms sends null
        Event e3 = new TrainModelEvent();
        assertEquals(mb.sendEvent(e3), null);
    }

    @Test
    void register() {
        //test 1: register and see its in microservices
        MicroService m1 = new StudentService("S1");
        mb.register(m1);
        assertEquals();     //m1 is in microservices
        //test 2: if already registered - dont do anything
        mb.register(m1);
        assertEquals();     //m1 is in microservices only once

    }

    @Test
    void unregister() {
        //test 1: if already unregistered - do nothing
        MicroService m1 = new StudentService("S1");
        mb.unregister(m1);
        assertEquals();     //m1 is not in microservices, broadcasts, or events
        //test 2: unregister and see its not in microservices or in any list in messages
        mb.register(m1);
        mb.subscribeEvent(TrainModelEvent.class, m1);
        mb.subscribeBroadcast(TickBroadcast.class, m1);
        Broadcast b = new TickBroadcast();
        mb.sendBroadcast(b);
        MicroService m2 = new StudentService("S2");
        mb.subscribeEvent(TrainModelEvent.class, m2);
        assertEquals();     //m1 is not in microservices, broadcasts, or events
        //test 3: if a list in messages is empty delete the key
        assertEquals();     //tickBroadcast key is not in the dic, Train key is in dic
    }

    @Test
    void awaitMessage() {
        //test 1: if unregistered do nothing

        //test 2: if there is no messages do nothing

        //test 3: if there is messages delete the top one and process it

    }
}
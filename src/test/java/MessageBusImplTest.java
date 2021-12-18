import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.StudentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;


class MessageBusImplTest {

    MessageBus mb;
    Model m;
    Student s;

    @BeforeEach
    void setUp() {
        mb= MessageBusImpl.GetInstance();
        s = new Student("student1", "CS", "PhD");
        m = new Model("model1", new Data("Images", 10000), s);
    }

    @AfterEach
    void tearDown() {
        mb.Clear();
    }

    @Test
    void subscribeEvent() {
        //test 1: subscribing successfully
        MicroService ms = new StudentService("Student 1", s);
        mb.subscribeEvent(TrainModelEvent.class, ms);
        assertTrue(mb.IsSubscribedEvent(TrainModelEvent.class, ms));
        //test2: resubscribing
        mb.subscribeEvent(TrainModelEvent.class, ms);
        assertTrue(mb.IsSubscribedEvent(TrainModelEvent.class, ms));
        //test 3: subscribing to a different event
        mb.subscribeEvent(TestModelEvent.class, ms);
        assertTrue(mb.IsSubscribedEvent(TrainModelEvent.class, ms));
        assertTrue(mb.IsSubscribedEvent(TestModelEvent.class, ms));
        //test 4: subscribing to an already existing event
        MicroService ms2 = new StudentService("Student 2", new Student("student2", "CS", "PhD"));
        mb.subscribeEvent(TrainModelEvent.class, ms2);
        assertTrue(mb.IsSubscribedEvent(TrainModelEvent.class, ms2));
    }

    @Test
    void subscribeBroadcast() {
        //test 1: subscribing successfully
        MicroService ms = new StudentService("Student 1", s);
        mb.subscribeBroadcast(TickBroadcast.class, ms);
        assertTrue(mb.IsSubscribedBroadcast(TickBroadcast.class, ms));
        //test 2: resubscribing
        mb.subscribeBroadcast(TickBroadcast.class, ms);
        assertTrue(mb.IsSubscribedBroadcast(TickBroadcast.class, ms));
        //test 3: subscribing to a different event
        mb.subscribeBroadcast(PublishConferenceBroadcast.class, ms);
        assertTrue(mb.IsSubscribedBroadcast(TickBroadcast.class, ms));
        assertTrue(mb.IsSubscribedBroadcast(PublishConferenceBroadcast.class, ms));
        //test 4: subscribing to an already existing event
        MicroService ms2 = new StudentService("Student 2", new Student("student2", "CS", "PhD"));
        mb.subscribeBroadcast(TickBroadcast.class, ms2);
        assertTrue(mb.IsSubscribedBroadcast(TickBroadcast.class, ms2));
    }

    @Test
    void complete() {
        TrainModelEvent e = new TrainModelEvent(m);
        e.SetFuture(new Future<>());
        mb.complete(e, m);
        assertEquals(e.getFuture().get(), m);
    }

    @Test
    void sendBroadcast() {
        //test 1: each ms that's registered to the broadcast it's in their queue
        MicroService m1 = new StudentService("S1", s);
        mb.register(m1);
        mb.subscribeBroadcast(TickBroadcast.class, m1);
        Broadcast b = new TickBroadcast();
        mb.sendBroadcast(b);
        try{ Message msg = mb.awaitMessage(m1);
            assertEquals(msg, b);}
        catch (Exception ignored){}
        //test 2: each ms not registered to the broadcast its not in their queue
        MicroService m2 = new StudentService("S2", new Student("student2", "CS", "PhD"));
        mb.subscribeBroadcast(PublishConferenceBroadcast.class, m2);
        LinkedList<String> modelNames = new LinkedList<>();
        modelNames.add("YOLO9000"); modelNames.add("VIT2");
        Broadcast b_ = new PublishConferenceBroadcast(modelNames);
        mb.sendBroadcast(b);
        mb.sendBroadcast(b_);
        try{ Message msg = mb.awaitMessage(m2);
            assertEquals(msg, b_);}
        catch (Exception ignored){}
        //test 3: if no one is subscribed to broadcast, do nothing
        mb.unregister(m2);
        Broadcast b1 = new PublishConferenceBroadcast(modelNames);
        mb.sendBroadcast(b1);
        mb.sendBroadcast(b);
        try{ Message msg = mb.awaitMessage(m1);
            assertEquals(msg, b);}
        catch (Exception ignored){}
    }

    @Test
    void sendEvent() {
        //test 1: the ms which is its turn receives the message in its queue
        MicroService m1 = new StudentService("S1", s);
        mb.register(m1);
        mb.subscribeEvent(TrainModelEvent.class, m1);
        MicroService m2 = new StudentService("S2", new Student("student2", "CS", "PhD"));
        mb.register(m2);
        MicroService m3 = new StudentService("S3", new Student("student3", "CS", "PhD"));
        mb.register(m3);
        mb.subscribeEvent(TrainModelEvent.class, m2);
        Event<Model> e = new TrainModelEvent(m);
        mb.sendEvent(e);
        try{ Message msg = mb.awaitMessage(m1);
            assertEquals(msg, e);}
        catch (Exception ignored){}
        Event<Model> e1 = new TrainModelEvent(m);
        mb.sendEvent(e1);
        try{ Message msg = mb.awaitMessage(m2);
            assertEquals(msg, e1);}
        catch (Exception ignored){}
        Event<Model> e2 = new TrainModelEvent(m);
        mb.sendEvent(e2);
        try{ Message msg = mb.awaitMessage(m1);
            assertEquals(msg, e2);}
        catch (Exception ignored){}
    }

    @Test
    void register() {
        //test 1: register and see its in microservices
        MicroService m1 = new StudentService("S1", s);
        mb.register(m1);
        assertTrue(mb.IsRegistered(m1));
        //test 2: if already registered - don't do anything
        mb.register(m1);
        assertTrue(mb.IsRegistered(m1));

    }

    @Test
    void unregister() {
        //test 1: if already unregistered - do nothing
        MicroService m1 = new StudentService("S1", s);
        mb.unregister(m1);
        assertFalse(mb.IsRegistered(m1));
        //test 2: unregister and see it's not in microservices or in any list in messages
        mb.register(m1);
        mb.subscribeEvent(TrainModelEvent.class, m1);
        mb.subscribeBroadcast(TickBroadcast.class, m1);
        Broadcast b = new TickBroadcast();
        mb.sendBroadcast(b);
        MicroService m2 = new StudentService("S2", new Student("student2", "CS", "PhD"));
        mb.subscribeEvent(TrainModelEvent.class, m2);
        mb.subscribeBroadcast(TickBroadcast.class, m2);
        mb.unregister(m1);
        assertFalse(mb.IsSubscribedBroadcast(TickBroadcast.class, m1));
        assertFalse(mb.IsSubscribedEvent(TrainModelEvent.class, m1));
        assertFalse(mb.IsRegistered(m1));
    }

    @Test
    void awaitMessage() {
        //test 1: if unregistered throw IllegalStateException
        MicroService m1 = new StudentService("S1", s);
        mb.unregister(m1);
        try{
            mb.awaitMessage(m1); fail();
        } catch (IllegalStateException ignored){
            assertTrue(true);
        } catch (Exception e2) {
            fail();
        }
        //test 2: if there is no messages wait until interrupted or until new msg arrives
        //test 2.a: interrupted
        mb.subscribeBroadcast(TickBroadcast.class, m1);
        Thread a = new Thread(()-> {
            try{
                mb.register(m1); mb.awaitMessage(m1);
            } catch (InterruptedException ignored){}
        });
        a.start();
        a.interrupt();
        //test 2.a: new message
        Broadcast msg = new TickBroadcast();
        mb.register(m1);
        mb.sendBroadcast(msg);
        try{
            assertEquals(msg, mb.awaitMessage(m1));
        } catch (Exception e2) {
            fail();
        }
    }
}
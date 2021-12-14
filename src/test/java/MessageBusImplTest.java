import bgu.spl.mics.*;


class MessageBusImplTest {

    MessageBus mb;

//    @BeforeEach
//    void setUp() {
//        mb= MessageBusImpl.GetInstance();
//    }
//
//    @AfterEach
//    void tearDown() {
//        mb.Clear();
//    }
//
//    @Test
//    void subscribeEvent() {
//        //test 1: cannot subscribe an un registered microservice
//        MicroService ms = new StudentService("Student 1", new Student("student1", "CS", "PhD"));
//        mb.subscribeEvent(TrainModelEvent.class, ms);
//        assertFalse(mb.IsSubscribedEvent(TrainModelEvent.class, ms));
//        //test 2: subscribing successfully
//        mb.register(ms);
//        mb.subscribeEvent(TrainModelEvent.class, ms);
//        assertTrue(mb.IsSubscribedEvent(TrainModelEvent.class, ms));
//        //test3: resubscribing
//        mb.subscribeEvent(TrainModelEvent.class, ms);
//        assertTrue(mb.IsSubscribedEvent(TrainModelEvent.class, ms));
//        //test 4: subscribing to a different event
//        mb.subscribeEvent(TestModelEvent.class, ms);
//        assertTrue(mb.IsSubscribedEvent(TrainModelEvent.class, ms));
//        assertTrue(mb.IsSubscribedEvent(TestModelEvent.class, ms));
//        //test 5: subscribing to an already existing event
//        MicroService ms2 = new StudentService("Student 2", new Student("student2", "CS", "PhD"));
//        mb.subscribeEvent(TrainModelEvent.class, ms2);
//        assertTrue(mb.IsSubscribedEvent(TrainModelEvent.class, ms2));
//    }
//
//    @Test
//    void subscribeBroadcast() {
//        //test 1: cannot subscribe an un registered microservice
//        MicroService ms = new StudentService("Student 1", new Student("student1", "CS", "PhD"));
//        mb.subscribeBroadcast(TickBroadcast.class, ms);
//        assertFalse(mb.IsSubscribedBroadcast(TickBroadcast.class, ms));
//        //test 2: subscribing successfully
//        mb.register(ms);
//        mb.subscribeBroadcast(TickBroadcast.class, ms);
//        assertTrue(mb.IsSubscribedBroadcast(TickBroadcast.class, ms));
//        //test3: resubscribing
//        mb.subscribeBroadcast(TickBroadcast.class, ms);
//        assertTrue(mb.IsSubscribedBroadcast(TickBroadcast.class, ms));
//        //test 4: subscribing to a different event
//        mb.subscribeBroadcast(PublishConferenceBroadcast.class, ms);
//        assertTrue(mb.IsSubscribedBroadcast(TickBroadcast.class, ms));
//        assertTrue(mb.IsSubscribedBroadcast(PublishConferenceBroadcast.class, ms));
//        //test 5: subscribing to an already existing event
//        MicroService ms2 = new StudentService("Student 2", new Student("student2", "CS", "PhD"));
//        mb.subscribeBroadcast(TickBroadcast.class, ms2);
//        assertTrue(mb.IsSubscribedBroadcast(TickBroadcast.class, ms2));
//    }
//
//    @Test
//    void complete() {
//        //test 1: the future object in the event is with result T
//        Event<String> e = new ExampleEvent("test1");
//        Future<String> f= mb.sendEvent(e);
//        mb.complete(e, "result1");
//        assertEquals(f.get(), "result1");
//        //test 2: twice complete on the same event result in an error
//        mb.complete(e,"result2");
//        assertEquals(f.get(), "result1");
//    }
//
//    @Test
//    void sendBroadcast() {
//        //test 1: each ms that's registered to the broadcast it's in their queue
//        MicroService m1 = new StudentService("S1", new Student("student1", "CS", "PhD"));
//        mb.register(m1);
//        mb.subscribeBroadcast(TickBroadcast.class, m1);
//        Broadcast b = new TickBroadcast();
//        mb.sendBroadcast(b);
//        try{ Message msg = mb.awaitMessage(m1);
//            assertEquals(msg, b);}
//        catch (Exception ex){}
//        //test 2: each ms not registered to the broadcast its not in their queue
//        MicroService m2 = new StudentService("S2", new Student("student2", "CS", "PhD"));
//        mb.subscribeBroadcast(PublishConferenceBroadcast.class, m2);
//        Broadcast b_ = new PublishConferenceBroadcast();
//        mb.sendBroadcast(b);
//        mb.sendBroadcast(b_);
//        try{ Message msg = mb.awaitMessage(m2);
//            assertEquals(msg, b_);}
//        catch (Exception ex){}
//        //test 3: if no one is subscribed to broadcast, do nothing
//        mb.unregister(m2);
//        Broadcast b1 = new PublishConferenceBroadcast();
//        mb.sendBroadcast(b1);
//        mb.sendBroadcast(b);
//        try{ Message msg = mb.awaitMessage(m1);
//            assertEquals(msg, b);}
//        catch (Exception ex){}
//    }
//
//    @Test
//    void sendEvent() {
//        //test 1: the ms which is its turn receives the message in its queue
//        MicroService m1 = new StudentService("S1", new Student("student1", "CS", "PhD"));
//        mb.register(m1);
//        mb.subscribeEvent(TrainModelEvent.class, m1);
//        MicroService m2 = new StudentService("S2", new Student("student2", "CS", "PhD"));
//        mb.register(m2);
//        MicroService m3 = new StudentService("S3", new Student("student3", "CS", "PhD"));
//        mb.register(m3);
//        mb.subscribeEvent(TrainModelEvent.class, m2);
//        Event e = new TrainModelEvent(m);
//        mb.sendEvent(e);
//        try{ Message msg = mb.awaitMessage(m1);
//            assertEquals(msg, e);}
//        catch (Exception ex){}
//        Event e1 = new TrainModelEvent(m);
//        mb.sendEvent(e1);
//        try{ Message msg = mb.awaitMessage(m2);
//            assertEquals(msg, e1);}
//        catch (Exception ex){}
//        Event e2 = new TrainModelEvent(m);
//        mb.sendEvent(e2);
//        try{ Message msg = mb.awaitMessage(m1);
//            assertEquals(msg, e2);}
//        catch (Exception ex){}
//        //test 2: if no ms is subscribed to the event sends null
//        Event e3 = new TrainModelEvent(m);
//        assertEquals(mb.sendEvent(e3), null);
//    }
//
//    @Test
//    void register() {
//        //test 1: register and see its in microservices
//        MicroService m1 = new StudentService("S1", new Student("student1", "CS", "PhD"));
//        mb.register(m1);
//        assertTrue(mb.IsRegistered(m1));
//        //test 2: if already registered - dont do anything
//        mb.register(m1);
//        assertTrue(mb.IsRegistered(m1));
//
//    }
//
//    @Test
//    void unregister() {
//        //test 1: if already unregistered - do nothing
//        MicroService m1 = new StudentService("S1", new Student("student1", "CS", "PhD"));
//        mb.unregister(m1);
//        assertFalse(mb.IsRegistered(m1));
//        //test 2: unregister and see its not in microservices or in any list in messages
//        mb.register(m1);
//        mb.subscribeEvent(TrainModelEvent.class, m1);
//        mb.subscribeBroadcast(TickBroadcast.class, m1);
//        Broadcast b = new TickBroadcast();
//        mb.sendBroadcast(b);
//        MicroService m2 = new StudentService("S2", new Student("student2", "CS", "PhD"));
//        mb.subscribeEvent(TrainModelEvent.class, m2);
//        assertFalse(mb.IsSubscribedBroadcast(TickBroadcast.class, m1));
//        assertFalse(mb.IsSubscribedEvent(TrainModelEvent.class, m1));
//        assertFalse(mb.IsRegistered(m1));
//    }
//
//    @Test
//    void awaitMessage() {
//        //test 1: if unregistered throw IllegalStateException
//        MicroService m1 = new StudentService("S1", new Student("student1", "CS", "PhD"));
//        try{ mb.awaitMessage(m1); fail();}
//        catch (IllegalStateException e1){}
//        catch (InterruptedException e2) {fail();}
//        //test 2: if there is no messages wait until interupted or untill new msg arrives
//        //test 2.a: interrupted
//        mb.register(m1);
//        mb.subscribeBroadcast(TickBroadcast.class, m1);
//        Broadcast msg = new TickBroadcast();
//        Thread a = new Thread(()-> {try{ mb.awaitMessage(m1); fail();}
//                                    catch (IllegalStateException e1){fail();}
//                                    catch (InterruptedException e2) {assertTrue(true);}});
//        a.start();
//        a.interrupt();
//        //test 2.a: new message
//        Thread b = new Thread(()-> {try{ assertEquals(mb.awaitMessage(m1), msg);}
//                                    catch (Exception e){fail();} });
//        b.start();
//        try {wait(100);} catch (Exception ex){}
//        mb.sendBroadcast(msg);
//        //test 3: if there is messages delete the top one and process it
//        mb.sendBroadcast(msg);
//        try{ assertEquals(mb.awaitMessage(m1), msg);}
//            catch (Exception e){fail();}
//    }
}
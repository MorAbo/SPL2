package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu;
    Queue<Event> notTickEvents;
    private TrainModelEvent trainModelEvent;
    private TestModelEvent testModelEvent;


    public GPUService(String name, GPU gpu) {
        super(name);
        // TODO Implement this
        this.gpu = gpu;
        MessageBusImpl.GetInstance().register(this);
        notTickEvents= new LinkedList<>();
        trainModelEvent=null;

    }

    @Override
    protected void initialize() {
        subscribeEvent(TrainModelEvent.class, message->{
            if (gpu.isInTheMiddleOfTraining()) notTickEvents.add(message);
                else {gpu.Train(message.getModel()); trainModelEvent=message;}
        });
        subscribeEvent(TestModelEvent.class, message-> {
            if (gpu.isInTheMiddleOfTraining()) notTickEvents.add(message);
            else{handelingTest(message);
        }});
        subscribeBroadcast(TickBroadcast.class, message-> {gpu.IncreaseTick();
            if(gpu.isFinished()) {
                Model m = gpu.getModel();
                gpu.setModel(null);
                complete(trainModelEvent, m);
                sendBroadcast(new FinishedTrainingBroadcast(m.getName()));
            }
        });
        subscribeBroadcast(TerminateBroadcast.class, message-> {terminate();
            System.out.println("               "+Thread.currentThread().getName()+" terminated");});
    }

    @Override
    protected Message awaitMessage() throws InterruptedException {
        if (gpu.isInTheMiddleOfTraining() | notTickEvents.isEmpty())
            return super.awaitMessage();
        else return notTickEvents.remove();
    }

    private void handelingTest(TestModelEvent testModelEvent){
        if (testModelEvent.getModel().getStatus() == "Trained"){
        double d = Math.random();
        if (testModelEvent.getStudent().getStatus().equals("MSc")) {
            if (d >= 0.6) testModelEvent.getModel().setResult("Bad");
            else testModelEvent.getModel().setResult("Good");
        }
        else if (testModelEvent.getStudent().getStatus().equals("PhD")) {
            if (d >= 0.8) testModelEvent.getModel().setResult("Bad");
            else testModelEvent.getModel().setResult("Good");
        }
        complete(testModelEvent, testModelEvent.getModel());
        testModelEvent.getModel().setStatus("Tested");}
        sendBroadcast(new FinishedTestingBroadcast(testModelEvent.getModel().getName()));
    }

    }

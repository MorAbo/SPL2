package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.Queue;

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
    Queue<Event> events;

    public GPUService(String name, GPU gpu) {
        super(name);
        // TODO Implement this
        this.gpu = gpu;
        MessageBusImpl.GetInstance().register(this);
    }

    @Override
    protected void initialize() {
        subscribeEvent(TrainModelEvent.class, message->{
            try {Model m = gpu.Train(message.getModel());
            complete(message,m);} catch (InterruptedException e){terminate();}
        });
        subscribeEvent(TestModelEvent.class, message-> {
            double d = Math.random();
            if (message.getStudent().getStatus().equals("MSc")) {
                if (d >= 0.6) message.getModel().setResult("Bad");
                else message.getModel().setResult("Good");
            }
            else if (message.getStudent().getStatus().equals("PhD")) {
                if (d >= 0.8) message.getModel().setResult("Bad");
                else message.getModel().setResult("Good");
            }
            complete(message, message.getModel());
            message.getModel().setStatus("Tested");
        });
        subscribeBroadcast(TickBroadcast.class, message-> gpu.IncreaseTick());
        subscribeBroadcast(TerminateBroadcast.class, message-> terminate());

    }
}

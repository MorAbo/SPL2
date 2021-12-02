package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;

/*
Sent by the Student, this event is at the core of the system. It will
be processed by one of the GPU microservices. During its process, it will utilize both
the CPUS and the relevant GPU.
 */
public class TrainModelEvent implements Event<Model> {
    private Future<Model> f;

    public TrainModelEvent(){
        f=new Future<>();
    }

}

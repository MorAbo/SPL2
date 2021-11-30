package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

/*
Sent by the Student, this event is at the core of the system. It will
be processed by one of the GPU microservices. During its process, it will utilize both
the CPUS and the relevant GPU.
 */
public class TrainModelEvent implements Event<Integer> {
    public Future<Integer> f;

    public TrainModelEvent(){
        f=new Future<>();
    }

}

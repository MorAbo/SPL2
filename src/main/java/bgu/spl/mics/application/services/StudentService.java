package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.messages.PublishResultEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    Student student;

    public StudentService(String name, Student student) {
        super(name);
        this.student = student;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(PublishConferenceBroadcast.class, message-> {});
        for (Model m: student.getModels()) {
            sendEvent(new TrainModelEvent(m));
            sendEvent(new TestModelEvent(m));
            sendEvent(new PublishResultEvent(m));
        }
    }


}

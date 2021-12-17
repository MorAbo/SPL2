package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.outputs.JSONOutput;
import bgu.spl.mics.application.outputs.ModelOutput;
import bgu.spl.mics.application.outputs.StudentOutput;

import java.util.LinkedList;

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
        subscribeBroadcast(TerminateBroadcast.class, message-> terminate());
        subscribeBroadcast(PublishConferenceBroadcast.class, message-> {
            for (String name:message.getModelNames()){
                if (student.isMyModel(name)) student.IncreasePublication();
                else student.IncreasePapersRead();
            }
        });
        subscribeBroadcast(FinishedTrainingBroadcast.class, message->{
            Model m = student.getModelByName(message.getModel());
            if (m!=null){
                TestModelEvent testEvent = new TestModelEvent(m, student);
                testEvent.setFuture(sendEvent(testEvent));
                while (!testEvent.isSent())
                    testEvent.setFuture(sendEvent(testEvent));
            }
        });
        subscribeBroadcast(FinishedTestingBroadcast.class, message->{
            Model m = student.getModelByName(message.getModel());
            if (m!=null){
                sendEvent(new PublishResultEvent(m));
            }
        });
        for (Model m : student.getModels()){
            TrainModelEvent trainEvent= new TrainModelEvent(m);
            while (!trainEvent.isSent())
                trainEvent.SetFuture(sendEvent(trainEvent));
        }

    }
    @Override
    public void shut(){
        super.shut();
        LinkedList<ModelOutput> modelOutputs= new LinkedList<>();
        for (Model m: student.getModels()){
            if (m.getStatus().equals("Trained") | m.getStatus().equals("Tested"))
                modelOutputs.add(new ModelOutput(m.getName(),m.GetData(), m.getStatus(), m.getResult()));
        }
        StudentOutput so=new StudentOutput(student.getName(), student.getDepartment(), student.getStatus(),
                student.getPublications(), student.getPapersRead(), modelOutputs);
        JSONOutput.GetInstance().addStudentOutputs(so);
    }


}

package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import com.sun.org.apache.bcel.internal.generic.PUSH;

public class TestModelEvent implements Event<Model> {
    private Model m;
    private Student s;
    private Future<Model> f;

    public Student getStudent(){ return s;}
    public Model getModel(){ return m;}

    public TestModelEvent(Model m){
        f = null;
    }

}

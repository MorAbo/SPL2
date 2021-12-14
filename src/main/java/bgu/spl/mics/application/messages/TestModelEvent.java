package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModelEvent implements Event<Model> {
    private Model m;
    private Student s;
    private Future<Model> f;

    public TestModelEvent(Model m, Student s){
        this.m=m;
        this.s=s;
        f = null;
    }

    public Student getStudent(){ return s;}
    public Model getModel(){ return m;}
    public Future<Model> getFuture(){return f;}


    @Override
    public void Resolve(Model result) {
        m=result;
        f.resolve(result);
        notifyAll();
    }
    public boolean isSent(){return f!=null;}
    public boolean isResolved(){return f.isDone();}
    public void setFuture(Future<Model> f) {
        this.f=f;
    }
}

package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishResultEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.outputs.ConferenceOutput;
import bgu.spl.mics.application.outputs.JSONOutput;
import bgu.spl.mics.application.outputs.ModelOutput;

import java.util.LinkedList;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private ConfrenceInformation confrenceInformation;

    public ConferenceService(String name, ConfrenceInformation confrenceInformation) {
        super(name);
        this.confrenceInformation = confrenceInformation;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, message-> {terminate();
            System.out.println("conf got terminated");});
        if (confrenceInformation.shouldRegister())
            subscribeEvent(PublishResultEvent.class, message_-> {
                    confrenceInformation.addToModels(message_.getModel());
            });
        subscribeBroadcast(TickBroadcast.class, message-> {
            confrenceInformation.IncreaseTick();
            System.out.println("conf got tick");
            if (confrenceInformation.shouldRegister())
                subscribeEvent(PublishResultEvent.class, message_-> {
                        confrenceInformation.addToModels(message_.getModel());
                });
            if (confrenceInformation.shouldPublish()){
                sendBroadcast(new PublishConferenceBroadcast(confrenceInformation.publish()));
                terminate();
            }
        });

    }
    @Override
    protected void shut(){
        super.shut();
        LinkedList<ModelOutput> modelOutputs= new LinkedList<>();
        for (Model m: confrenceInformation.getModels()){
            modelOutputs.add(new ModelOutput(m.getName(),m.GetData(), m.getStatus(), m.getResult()));
        }
        ConferenceOutput cop = new ConferenceOutput(confrenceInformation.getName(), confrenceInformation.getDate(), modelOutputs);
        JSONOutput.GetInstance().addConferenceOutputs(cop);
    }
}

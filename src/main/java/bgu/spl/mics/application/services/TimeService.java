package bgu.spl.mics.application.services;

import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	int tickTime;
	int duration;

	public TimeService(int tickTime, int duration) {
		super("Time Service");
		// TODO Implement this
		this.tickTime = tickTime;
		this.duration = duration;
	}

	@Override
	protected void initialize() {
		while (Thread.currentThread().isInterrupted()){
			try{
				Message m= MessageBusImpl.GetInstance().awaitMessage(this);
				m.dosomthing;
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
		//shutdown or whatever you do in the end of the time
		
	}

}

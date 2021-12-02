package bgu.spl.mics;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private HashMap<MicroService, Queue<Message>> microservices=null;
	private HashMap<Class<? extends Event<?>>, List<MicroService>> events=null;
	private HashMap<Class<? extends Broadcast>, List<MicroService>> broadcasts=null;
	private final static MessageBusImpl INSTANCE = new MessageBusImpl();

	private  MessageBusImpl(){
		microservices = new HashMap<>();
		events= new HashMap<>();
		broadcasts=new HashMap<>();
	}

	public static MessageBusImpl GetInstance(){
		return INSTANCE ;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub
		//get microservices
		//m in microservices

		//syncronized on microservices and events


	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub
		//syncronized on microservices and broadcasts

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub
		//e.future.setresuts(result)

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub
		//syncronized on broadcasts microservices
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;

		//syncronized on events microservices
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub
		//syncronized on microservices
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub
		//syncronized on microservices, broadcasts, events

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean IsRegistered(MicroService m) {
		return false;
		//checks the ms is only once in the list

	}

	@Override
	public <T> boolean IsSubscribedEvent(Class<? extends Event<T>> type, MicroService m) {
		return false;
		//checks the ms is only once in the list
	}

	@Override
	public boolean IsSubscribedBroadcast(Class<? extends Broadcast> type, MicroService m) {
		return false;
		//checks the ms is only once in the list

	}

	public void Clear(){
		microservices.forEach((ms,q)-> unregister(ms));
	}
}

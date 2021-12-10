package bgu.spl.mics;

import com.sun.tools.javac.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ReadWriteMap<MicroService, ConcurrentLinkedQueue<Message>> microservices;
	private ReadWriteMap<Class<? extends Event<?>>, ConcurrentHashMap<List<MicroService>, Integer>> events;
	private ReadWriteMap<Class<? extends Broadcast>, ReadWriteList<MicroService>> broadcasts;

	private static class InstanceHolder{
		private static MessageBusImpl INSTANCE =new MessageBusImpl();
	}

	private MessageBusImpl(){
		microservices = new ReadWriteMap<>(new HashMap<>());
		events= new ReadWriteMap<>(new HashMap<>());
		broadcasts=new ReadWriteMap<>(new HashMap<>());
	}

	public static MessageBusImpl GetInstance(){
		return InstanceHolder.INSTANCE;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (events.containsKey(type)) {
			if (!events.get(type).keySet().iterator().next().contains(m))
				events.get(type).keySet().iterator().next().add(m);
		}
		else {
			events.put(type, new ConcurrentHashMap<>());
			events.get(type).put(new LinkedList<MicroService>(){{add(m);}},0);
		}
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
		//synchronized on microservices
		if (!microservices.containsKey(m)){
			microservices.put(m, new LinkedList<>());
		}
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

package bgu.spl.mics;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ReadWriteMap<MicroService, ConcurrentLinkedQueue<Message>> microservices;
	private ReadWriteMap<Class<? extends Message>, ReadWriteList<MicroService>> events;
	private ReadWriteMap<Class<? extends Message>, Integer> roundRobin;
	private ReadWriteMap<Class<? extends Broadcast>, ReadWriteList<MicroService>> broadcasts;

	private static class InstanceHolder{
		private static MessageBusImpl INSTANCE =new MessageBusImpl();
	}

	private MessageBusImpl(){
		microservices = new ReadWriteMap<>(new HashMap<>());
		events= new ReadWriteMap<>(new HashMap<>());
		roundRobin=new ReadWriteMap<>(new HashMap<>());
		broadcasts=new ReadWriteMap<>(new HashMap<>());
	}

	public static MessageBusImpl GetInstance(){
		return InstanceHolder.INSTANCE;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!events.containsKey(type))
			events.put(type, new ReadWriteList<>());
		if (!IsSubscribedEvent(type, m))
			events.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!broadcasts.containsKey(type))
			broadcasts.put(type, new ReadWriteList<>());
		if (!IsSubscribedBroadcast(type, m))
			broadcasts.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		e.Resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (broadcasts.containsKey(b.getClass()))
		synchronized(broadcasts.get(b.getClass())){ //queue of all the ms in event type b
			for (int i=0; i<broadcasts.get(b.getClass()).size(); i++) {
				microservices.get(broadcasts.get(b.getClass()).get(i)).add(b);
				synchronized (broadcasts.get(b.getClass()).get(i)){
				broadcasts.get(b.getClass()).get(i).notify();}
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (roundRobin.get(e.getClass())){
			Integer i = roundRobin.get(e.getClass());
			if (!events.containsKey(e.getClass())) return null;
			MicroService m= events.get(e.getClass()).get(i);
			microservices.get(m).add(e);
			i=(i+1)%events.get(e.getClass()).size();
			roundRobin.put(e.getClass(),i);
		}
		notifyAll();
		return new Future<>();
	}

	@Override
	public void register(MicroService m) {
		if (!IsRegistered(m))
			microservices.put(m, new ConcurrentLinkedQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		microservices.remove(m);
		//remove from broad cast
		for (Map.Entry<Class<? extends Broadcast>, ReadWriteList<MicroService>> pair: broadcasts.getSet()){
			pair.getValue().remove(m);
		}
		//remove from event
		for (Map.Entry<Class<? extends Message>, ReadWriteList<MicroService>> pair: events.getSet()) {
			int i = pair.getValue().whereIs(m);
			if (i != -1) {
				pair.getValue().remove(m);
				//round robin where we remove if i>= location of m in events than i--
				if (roundRobin.get(pair.getKey()) >= i) {
					i = (i - 1) % pair.getValue().size();
					roundRobin.put(pair.getKey(), i);
				}
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!IsRegistered(m)) throw new IllegalStateException("service not registered");
		synchronized (microservices.get(m)) {
			while (microservices.get(m).isEmpty())
				synchronized (m) {
					m.wait();
				}
		}
		return microservices.get(m).poll();
	}

	@Override
	public boolean IsRegistered(MicroService m) {
		return microservices.containsKey(m);
	}

	@Override
	public <T> boolean IsSubscribedEvent(Class<? extends Event<T>> type, MicroService m) {
		return events.get(type).contains(m);
	}

	@Override
	public boolean IsSubscribedBroadcast(Class<? extends Broadcast> type, MicroService m) {
		return broadcasts.get(type).contains(m);
	}

	public void terminate(){
		for(Map.Entry<MicroService, ConcurrentLinkedQueue<Message>> pair: microservices.getSet()){
			pair.getKey().terminate();
		}
	}

	public void Clear(){
		microservices.clear();
		broadcasts.clear();
		events.clear();
		roundRobin.clear();
	}
}

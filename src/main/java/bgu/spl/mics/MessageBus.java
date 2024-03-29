package bgu.spl.mics;

/**
 * The message-bus is a shared object used for communication between
 * micro-services.
 * It should be implemented as a thread-safe singleton.
 * The message-bus implementation must be thread-safe as
 * it is shared between all the micro-services in the system.
 * You must not alter any of the given methods of this interface. 
 * You cannot add methods to this interface.
 */
public interface MessageBus {

    /**
     * Subscribes {@code m} to receive {@link Event}s of type {@code type}.
     * <p>
     * @param <T>  The type of the result expected by the completed event.
     * @param type The type to subscribe to,
     * @param m    The subscribing micro-service.
     *
     * @pre isRegistered(m)
     * @post isSubscribed(type, m)
     */
    <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m);

    /**
     * Subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
     * <p>
     * @param type 	The type to subscribe to.
     * @param m    	The subscribing micro-service.
     *
     * @pre isRegistered(m)
     * @post isSubscribed(type, m)
     */
    void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m);

    /**
     * Notifies the MessageBus that the event {@code e} is completed and its
     * result was {@code result}.
     * When this method is called, the message-bus will resolve the {@link Future}
     * object associated with {@link Event} {@code e}.
     * <p>
     * @param <T>    The type of the result expected by the completed event.
     * @param e      The completed event.
     * @param result The resolved result of the completed event.
     *
     * @pre future in event is null
     * @post future in event isn't null
     * @post if @pre(future in event) isn't null, do nothing
     */
    <T> void complete(Event<T> e, T result);

    /**
     * Adds the {@link Broadcast} {@code b} to the message queues of all the
     * micro-services subscribed to {@code b.getClass()}.
     * <p>
     * @param b 	The message to added to the queues.
     *
     * @post each microservice in the list of b in the dictionary broadcast will add the message to their queue
     */
    void sendBroadcast(Broadcast b);

    /**
     * Adds the {@link Event} {@code e} to the message queue of one of the
     * micro-services subscribed to {@code e.getClass()} in a round-robin
     * fashion. This method should be non-blocking.
     * <p>
     * @param <T>    	The type of the result expected by the event and its corresponding future object.
     * @param e     	The event to add to the queue.
     * @return {@link Future<T>} object to be resolved once the processing is complete,
     * 	       null in case no micro-service has subscribed to {@code e.getClass()}.
     *
     * @post e in the queue on one on the microservices which is subscribed to this type of
     *          event and it's his turn receives a message
     * @post if no microservices is subscribed to e.class then return null
     */
    <T> Future<T> sendEvent(Event<T> e);

    /**
     * Allocates a message-queue for the {@link MicroService} {@code m}.
     * <p>
     * @param m the micro-service to create a queue for.
     *
     * @post if @pre (m not in microservices) m is in microservices
     * @post if @pre (m in microservices) do noting
     */
    void register(MicroService m);

    /**
     * Removes the message queue allocated to {@code m} via the call to
     * {@link #register(bgu.spl.mics.MicroService)} and cleans all references
     * related to {@code m} in this message-bus. If {@code m} was not
     * registered, nothing should happen.
     * <p>
     * @param m the micro-service to unregister.
     *
     * @post if @pre (m not in microservices) do nothing
     * @post if @pre (m in microservices) m is not in microservices, broadcasts, or events
     *
     * */
    void unregister(MicroService m);

    /**
     * Using this method, a <b>registered</b> micro-service can take message
     * from its allocated queue.
     * This method is blocking meaning that if no messages
     * are available in the micro-service queue it
     * should wait until a message becomes available.
     * The method should throw the {@link IllegalStateException} in the case
     * where {@code m} was never registered.
     * <p>
     * @param m The micro-service requesting to take a message from its message
     *          queue.
     * @return The next message in the {@code m}'s queue (blocking).
     * @throws InterruptedException if interrupted while waiting for a message
     *                              to became available.
     */
    Message awaitMessage(MicroService m) throws InterruptedException;

    /**
     * using this method you can query whether a microservice is registered to the message bus
     * @param m The micro-service on question whether its registered
     * @return true if m is registered to the messagebus and false otherwise
     */
    boolean IsRegistered(MicroService m);

    /**
     * using this method you can query whether a microservice is subscribed to a certain event
     * @param m The micro-service on question whether its subscribed to type
     * @param type the event in question
     * @return true if m is subscribed to the type and false otherwise
     */
    <T> boolean IsSubscribedEvent(Class<? extends Event<T>> type, MicroService m);

    /**
     * using this method you can query whether a microservice is subscribed to a certain broadcast
     * @param m The micro-service on question whether its subscribed to type
     * @param type the broadcast in question
     * @return true if m is subscribed to the type and false otherwise
     */
     boolean IsSubscribedBroadcast(Class<? extends Broadcast> type, MicroService m);

    /**
     * clears the object to its original (when created) state
     */
    void Clear();

}

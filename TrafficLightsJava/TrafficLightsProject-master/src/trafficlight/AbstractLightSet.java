package trafficlight;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class AbstractLightSet extends ObservableEntity implements Runnable {
	
	/* 
	 * The main abstract superclass for a single light set.
	 * Concrete subclasses must implement switchSet(Signal) method. 
	 * 
	 * A single light set normally controls of a pair of of tricolor vehicle lights. 
	 * The tricolor lights are used both for linear traffic and turns for vehicular traffic. 
	 * A light set starts running in its own thread automatically when created. 
	 * It has an event loop that processes events received from the owner intersection
	 * or from itself to execute a signal cycle. 
	 * The events are scheduled in a blocking FIFO queue. 
	 */
	
	// package members only meant to be used by tests
	// public members can be used by clients
	// check IntersectionTest to see how this class works
	// superclass ObservableEntity makes this class observable for testing and logging 

	/* Here are the set of events that can be processed by a light set
	 * INIT: initialize the light set when the intersection turns it on
	 * ADVANCE: initiate one cycle of the light set with a GREEN signal
	 * PRE_STOP: prepare to stop traffic with an ORANGE signal 
	 * STOP: terminate signal cycle and stop traffic with a RED signal
	 * ALL_STOP: set all lights to their stop signal (RED for linear lights and 
	 * turn lights and DONT_WALK for pedestrian lights)
	 * 
	 */
	public enum Event {
		INIT, ADVANCE, PRE_STOP, STOP, ALL_STOP
	}

	/* The following are signal delays in for each signal state. 
	 * Shorten them to speed up tests. Or set them to 0.  
	 * The traffic light system should work regardless of signal cycle timing. 
	 */
	public static long RED_DELAY = 0; 
	public static long ORANGE_DELAY = 0;
	public static long GREEN_DELAY = 0;
	
	// FIFO event queue of light set
	protected BlockingQueue<Event> eventQueue = new ArrayBlockingQueue<Event>(5);
	
	// signal state of light set: OFF, GREEN, RED, or ORANGE
	protected Signal status = Signal.OFF;
	
	// id of this light set, 0 or 1
	protected int id;
	
	// the owning intersection 
	protected Intersection owner = null;
	
	// the thread in which this light set's signal cycle will be running
	protected Thread signalCycle = null;
	
	/* 
	 * Each light set has at least two synchronized base lights.
	 * Subclasses may add extra lights. 
	 */
	protected VehicleLight[] light = new VehicleLight[2];
	
	// private constructor
	protected AbstractLightSet(int id) {
		this.id = id;
	}
	
	// public constructor that tests use, injecting an observer for logging
	public AbstractLightSet(int id, Observer observer) {
		this.id = id;		
		this.observer = observer;
		if (observing) addObserver(observer); // part of Observer pattern
		for (int i = 0; i < 2; i++) {
			light[i] = new VehicleLight(id, this);
		}
		startSignalCycle();
	}

	/* Real constructor that accepts a reference to owning intersection.
	 * The owner will double as the observer for logging and testing. 
	 */
	public AbstractLightSet(int id, Intersection owner) {
		this.id = id;
		this.owner = owner;
		this.observer = owner.observer; // part of Observer pattern 
		if (observing) addObserver(observer);
		for (int i = 0; i < 2; i++) {
			light[i] = new VehicleLight(id, this);
		}
		startSignalCycle();
	}
	
	// public constructors start the signal cycle thread
	protected void startSignalCycle() {
		signalCycle = new Thread(this);
		signalCycle.start();
		log("created");	
	}
	
	
	/* 
	 * Here is the signal cycle of this light set implemented using an 
	 * event loop. This is the most important part of a light set. 
	 */
	@Override
	public void run() {
		log("running");
		Event next;
		while (true) {
			try {
				log("waiting for event");
				next = eventQueue.take();
				log("received " + next);
				switch (status) {
				case OFF: 
					sateOFF(next);
					break;
				case RED:
					stateRED(next);
					break;
				case GREEN: 
					stateGREEN(next);
					break;
				case ORANGE: 
					stateORANGE(next);
					break;
				default:
					throw new InterruptedException(); 
				}
			} catch (InterruptedException e) {
				log("interrupted");
				failed();
			}
		}
	}
	
	/* 
	 * The following list of methods represent the different  
	 * states of the signal cycles, one for each signal value.
	 * 
	 * */
	public void sateOFF(Event next) throws InterruptedException {
		if (next == Event.INIT) {
			switchToRed();			
		} else {
			throw new InterruptedException();
		}
	}
	public void stateRED(Event next) throws InterruptedException {
		if (next == Event.ADVANCE) {
			switchToGreen();
		} else if (next == Event.ALL_STOP) {
			// not applicable to a turn light set
			switchAllLightsToStopSignal(); 
		} else {
			throw new InterruptedException();
		}
	}
	public void stateORANGE(Event next) throws InterruptedException {
		if (next == Event.STOP) {
			switchToRed();					
		} else {
			throw new InterruptedException();
		}
	}
	public void stateGREEN(Event next) throws InterruptedException {
		if (next == Event.PRE_STOP) {
			switchToOrange();
		} else {
			throw new InterruptedException();
		}
	}

	/*
	 * Client (intersection or observer) calls this method to initialize the light set.
	 * Note that this piece of code is running in the client's thread, not in the signal
	 * cycle's thread. The client must have a monitor lock on this light set before calling
	 * this method. The lock will suspend the signal cycle. The client schedules an INIT event 
	 * by inserting the event in the event queue. Then it offers the monitor lock to any thread
	 * waiting for it (in this case, the signal cycle of this light set). And then it waits
	 * to be notified.  
	 */
	public void turnOn() throws InterruptedException {
		log("turning on");
		if (owner != null) owner.waiting(id);
		eventQueue.put(Event.INIT);
		wait();
	}

	/* 
	 * Client calls this method to initiate a new signal cycle. 
	 * Same mechanism as above. 
	 */
	public void advance() throws InterruptedException {
		log("advancing");		
		if (owner != null) owner.waiting(id);
		eventQueue.put(Event.ADVANCE);
		wait();
	}

	/* 
	 * Client resets the light set, taking it back to its initial signal state.
	 * The main controlled lights are switched to their OFF state. 
	 */
	public void reset() {
		log("resetting");
		eventQueue.clear();
		switchSet(Signal.OFF);
	}

	/*
	 * Light set switches to its GREEN signal state upon receiving an ADVANCE event, sleeps
	 * for the specified signal state delay, and then schedules a PRE_STOP event. 
	 * The main controlled lights are switched to GREEN. 
	 */
	protected void switchToGreen() throws InterruptedException {
		log("switching to GREEN");
		switchSet(Signal.GREEN);
		eventQueue.put(Event.PRE_STOP);
		Thread.sleep(GREEN_DELAY);
	}
	
	/*
	 * Light set switches to its ORANGE signal state upon receiving a PRE_STOP event, sleeps
	 * for the specified signal state delay, and then schedules a STOP event to complete the 
	 * cycle. 
	 * The main controlled lights are switched to ORANGE. 
	 */
	protected void switchToOrange() throws InterruptedException {
		log("switching to ORANGE");
		switchSet(Signal.ORANGE);
		Thread.sleep(ORANGE_DELAY);
		eventQueue.put(Event.STOP);
	}

	/*
	 * Light set switches to its RED signal state upon receiving an INIT event or upon completing
	 * a signal cycle with a STOP event. At that point, it's ready to relinquish it monitor lock on itself
	 * back to the client, so the client can execute the next signal cycle or another 
	 * light set's signal cycle. notify() performs the release of the lock back to the client. 
	 * The method is synchronized because the signal cycle must first wait to get the lock from the client 
	 * before releasing it. 
	 * All main controlled lights are switched to RED. 
	 */
	protected synchronized void switchToRed() throws InterruptedException {
		log("switching to RED");
		switchSet(Signal.RED);
		log("getting ready");
		Thread.sleep(RED_DELAY);
		if (owner != null) owner.ready(id);	
		notify();
	}

	/* 
	 * The signal cycle executes this method if it receives an interrupt.
	 * Similarly this method is synchronized to force for the signal cycle to 
	 * wait for the monitor lock of the light set and let it release it before
	 * returning from the method.   
	 */
	protected synchronized void failed() {
		log("failing");
		eventQueue.clear();
		switchSet(Signal.OFF);
		if (owner != null) owner.interrupt();
		notify();
	}

	/* 
	 * This method switches all the vehicle lights controlled by this light set 
	 * to their new signal state. 
	 */
	protected void switchVehicleLights(Signal newStatus) {
		for (int i = 0; i < 2; i++) {
			light[i].switchLight(newStatus);
		}
	}
	
	/*  
	 * This method must be implemented by each subclass.
	 * It defines how the lights controlled by this light set are synchronized. 
	 * For example, the light light set controlling the linear traffic will
	 * add pedestrian lights and make sure that the vehicle traffic and pedestrian
	 * traffic lights will have opposite signal states. 
	 * This method may call switchStopLights. 
	 */
	protected abstract void switchSet(Signal newStatus);
	
	
	/* 
	 * This method will override the protocol of switchSet(Signal) and force the main
	 * lights controlled by this light set to be reset to their stop signal RED. 
	 * This will be necessary to allow protected left turns. 
	 */
	protected synchronized void switchAllLightsToStopSignal() throws InterruptedException {
		for (int i = 0; i < 2; i++) {
			light[i].switchLight(Signal.RED);
		}
		if (owner != null) owner.ready(id);	
		notify();
	}
		
    // getter for signal state of the light set
	public Signal getStatus() {
		return status;
	}

	// getter for id of the light set
	public int getId() {
		return id;
	}

	// getter for the signal cycle (event loop) thread of the light set
	public Thread getSignalCycle() {
		return signalCycle;
	}
	
	// getter for the main (vehicle) lights controlled by this light set, by id (0 or 1)
	public VehicleLight getTriColorLight(int i) {
		return light[i];
	}

	// getter for owning intersection
	public Intersection getOwner() {
		return owner;
	}
	
	// is the event queue empty? 
	public boolean isEventQueueEmpty() {
		return eventQueue.isEmpty();
	}
	
	
	/* These are for logging - part of Observer pattern. 
	 * To suppress logging, disable Observer pattern by setting observing field
	 * of ObservableEntity class.
	 */
	
	public void log() {
		send(id + " " + status.toString());
	}

	public void log(String message) {
		send(id + " " + status.toString(), message);
	}

}
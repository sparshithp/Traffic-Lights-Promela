package trafficlight;

public class Intersection extends ObservableEntity implements Runnable {

	/*
	 * An intersection object controls four sets of lights: two sets of stop
	 * lights and two sets of turn lights. Linear light sets include
	 * synchronized pedestrian lights and vehicle lights that together control
	 * bidirectional traffic along a line and pedestrian crossing in the
	 * perpendicular direction. Turn light sets control protected left turns and
	 * do not include pedestrian lights.
	 */

	// package members only meant to be used by tests
	// public members can be used by clients
	// check IntersectionTest to see how this class works
	// superclass ObservableEntity makes this class observable for testing and
	// logging

	// possible symbolic states of an intersection
	public enum Status {
		ENABLED, FAILED, DISABLED
	};

	private Status status = Status.DISABLED;

	/*
	 * Readiness status indicator for testing and logging: applies. isReady[i]
	 * is true if current set pair i ready for its next cycle.
	 */
	private boolean[] isReady = new boolean[2];

	/*
	 * light sets
	 */
	private LinearLightSet[] stopLightSet = new LinearLightSet[2];
	private TurnLightSet[] turnLightSet = new TurnLightSet[2];

	// When enabled, the intersection's scheduling loop will be running on this
	// tread:
	private Thread schedulingLoop = null;

	/*
	 * Constructor accepts a reference to an operator that can observe this
	 * intersection. This takes advantage of the Observer pattern, and is useful
	 * for testing and logging. The light sets, when initiated start running on
	 * their own separate threads executing their own signal cycles, stopping at
	 * the beginning of each cycle for the intersection to tell them to proceed
	 * with the next cycle. The light sets know about the intersection.
	 */
	public Intersection(Operator operator) {
		observer = operator;
		if (observing)
			addObserver(operator); // part of observer pattern
		for (int i = 0; i < 2; i++) {
			stopLightSet[i] = new LinearLightSet(i, this);
			turnLightSet[i] = new TurnLightSet(i, this);
			isReady[i] = false;
		}
		schedulingLoop = new Thread(this);
		log("created");
	}

	/*
	 * Enable this intersection: the intersection's scheduling loop starts
	 * running in its own thread. Once running, it executes its scheduling loop
	 * to control the light sets.
	 */
	public Thread enable() {
		status = Status.ENABLED;
		schedulingLoop.start();
		log("enabled");
		return schedulingLoop;
	}

	/*
	 * Get the symbolic state of this intersection.
	 */
	public Status getStatus() {
		return status;
	}

	/*
	 * Here is the scheduling loop of the intersection. This loop captures the
	 * central function of the intersection.
	 */
	@Override
	public void run() {
		try {
			turnOnStopLights();
			turnOnTurnLights();
			while (true) {
				advanceStopLights();
				blockPedestrians();
				advanceTurnLights();
				unblockPedestrians();
			}
		} catch (InterruptedException e) {
			log("interrupted");
			interrupted();
		}
	}

	/*
	 * Has a light set with id lightSetId completed its signal cycle and ready
	 * for next one? If the variable is false, intersection should wait for the
	 * light set to finish. Used for testing and observation.
	 */
	public boolean isReady(int lightSetId) {
		return isReady[lightSetId];
	}

	/*
	 * This is how a light set tells the intersection that it has completed a
	 * signal cycle. Used for testing and observation.
	 */
	public void ready(int lightSetId) {
		isReady[lightSetId] = true;
		log("ready from set " + lightSetId);
	}

	/*
	 * This is how light set tells the intersection that the intersection should
	 * be waiting for it to complete the current signal cycle. Used for testing
	 * and observation.
	 */
	public void waiting(int lightSetId) {
		isReady[lightSetId] = false;
		log("waiting for set " + lightSetId);
	}

	/*
	 * Initiate the pair of linear light sets one after the other.
	 * Intersection's scheduling loop thread must get a monitor lock on each
	 * light set first with a synchronize block. Then it calls the proper method
	 * of the light set and offers the lock to the light set via a wait(). The
	 * light set will inform the intersection when it's finished via a notify().
	 * Only then can the intersection's loop exit the synchronized block.
	 */
	void turnOnStopLights() throws InterruptedException {
		for (int i = 0; i < 2; i++) {
			synchronized (stopLightSet[i]) {
				stopLightSet[i].turnOn();
			}
			log("turned on linear light set " + i);
		}
		log("turned on linear light sets");
	}

	/*
	 * Initiate the pair of turn on light sets one after the other. Same idea as
	 * turning the linear light sets on.
	 */
	void turnOnTurnLights() throws InterruptedException {
		for (int i = 0; i < 2; i++) {
			synchronized (turnLightSet[i]) {
				turnLightSet[i].turnOn();
			}
			log("turned on turn light set " + i);
		}
		log("turned on protected left turns");
	}

	/*
	 * Advance the linear light sets, one after the other. The stop lights will
	 * go through their signal cycles once.
	 */
	void advanceStopLights() throws InterruptedException {
		for (int i = 0; i < 2; i++) {
			synchronized (stopLightSet[i]) {
				stopLightSet[i].advance();
			}
			log("advanced sopt light set " + i);
		}
	}

	/*
	 * Block the pedestrian lights of all the stop lights sets. This will cause
	 * the pedestrian lets to get stuck in DON'T WALK. It is necessary for
	 * protected left turns.
	 */
	void blockPedestrians() throws InterruptedException {
		for (int i = 0; i < 2; i++) {
			stopLightSet[i].forbidPedestrians();
			log("no pedestrains on linear light set " + i);
		}
	}

	/*
	 * Unblock pedestrian lights, allowing them to be synchronized with vehicle
	 * stop lights once again. This is necessary after protected left turns.
	 */
	void unblockPedestrians() throws InterruptedException {
		for (int i = 0; i < 2; i++) {
			stopLightSet[i].permitPedestrians();
			log("pedestrains ok on linear light set " + i);
		}
	}

	/*
	 * Advance the turn light sets, one after the other. This will execute
	 * alternating protected left turns in each direction. The turn lights will
	 * go through their signal cycles once.
	 */
	void advanceTurnLights() throws InterruptedException {
		for (int i = 0; i < 2; i++) {
			synchronized (turnLightSet[i]) {
				turnLightSet[i].advance();
			}
			log("advanced turn light set " + i);
		}
		log("protected left turns are over");
	}

	/*
	 * Disable this intersection. The scheduling loop will be interrupted and
	 * the intersection will go back to its initial symbolic state.
	 */
	public void disable() {
		status = Status.DISABLED;
		schedulingLoop.interrupt();
		resetLightSets();
		log("disabled");
	}

	/*
	 * Interrupt the scheduling loop, creating an InterruptedException in the
	 * intersection's scheduling loop thread.
	 */
	public void interrupt() {
		schedulingLoop.interrupt();
	}

	/*
	 * Reset all light sets following a disable, failure, or interrupt.
	 */
	private void resetLightSets() {
		log("resetting light sets");
		for (int i = 1; i < 2; i++) {
			stopLightSet[i].reset();
			turnLightSet[i].reset();
		}
	}

	/*
	 * This method is called when the scheduling loop is interrupted. If it's
	 * caused by a call to disable(), nothing else to do.
	 */
	private void interrupted() {
		if (status == Status.DISABLED)
			return;
		fail();
	}

	/*
	 * If the interrupt is caused by another reason, then the symbolic state
	 * will be set to FAILED. The light sets must be reset too.
	 */
	private void fail() {
		status = Status.FAILED;
		resetLightSets();
	}

	// getter for linear light set with given id
	public LinearLightSet getStopLightSet(int id) {
		return stopLightSet[id];
	}

	// getter for turn light set with given id
	public TurnLightSet getTurnLightSet(int id) {
		return turnLightSet[id];
	}

	/*
	 * These are for logging - part of Observer pattern. To suppress logging,
	 * disable Observer pattern by setting observing field of ObservableEntity
	 * class.
	 */

	public void log() {
		send(status.toString());
	}

	public void log(String message) {
		send(status.toString(), message);
	}
}

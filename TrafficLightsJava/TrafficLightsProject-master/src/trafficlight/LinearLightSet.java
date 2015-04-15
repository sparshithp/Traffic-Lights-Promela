package trafficlight;
import java.util.Observer;

public class LinearLightSet extends AbstractLightSet implements Runnable {

	/*
	 * A linear light set controls bidirectional vehicular and pedestrian traffic along
	 * one line. It has a main vehicular light set that has two tricolor vehicular lights inherited from
	 * superclass, and an extra light set with two bimodal pedestrian lights controls pedestrian
	 * cross traffic. 
	 */
	
	/* 
	 * Are pedestrian lights enabled? 
	 * Set to false before protected left turns are performed by a turn light set.
	 */
	private boolean pedestriansOn = true;
	
	/*
	 * Pedestrian light set with two bimodal pedestrian lights. 
	 * A pedestrian light has signal states WALK and DONT_WALK.
	 */
	private PedestrianLight[] pedestrianLight = new PedestrianLight[2];
	
	// public constructor for testing and logging, accepts an observer
	public LinearLightSet(int id, Observer observer) {
		super(id);
		this.observer = observer;
		addObserver(observer); // part of Observer pattern 
		for (int i = 0; i < 2; i++) {
			light[i] = new VehicleLight(id, this);
			pedestrianLight[i] = new PedestrianLight(id, this);
		}
		startSignalCycle();
	}

	/* Real constructor that accepts a reference to owning intersection.
	 * The owner will double as the observer for logging and testing. 
	 */
	public LinearLightSet(int id, Intersection owner) {
		super(id);	
		this.owner = owner;
		addObserver(owner.observer);		
		for (int i = 0; i < 2; i++) {
			light[i] = new VehicleLight(id, this);
			pedestrianLight[i] = new PedestrianLight(id, this);
		}
		startSignalCycle();
	}


	/* 
	 * Client (intersection or observer) schedules an ALL_STOP event to forbid pedestrian 
	 * crossing. The monitor locks on this object is released so that the signal cycle of
	 * the light set can process the event. 
	 */
	public void forbidPedestrians() throws InterruptedException {
		log("stopping pedestrians");
		if (owner != null) owner.waiting(id);
		pedestriansOn = false;
		eventQueue.put(Event.ALL_STOP);
	}
	
	// re-allow pedestrian crossing 
	public void permitPedestrians() {
		log("allowing pedestrians");
		pedestriansOn = true;
	}
	
	/* 
	 * Before protected turns by a turn light set, a linear light set must set all controlled lights 
	 * to their stop signal state. This includes bimodal pedestrian lights and tricolor vehicular lights.
	 * @see LightSet#switchAllLightsToStopSignal()
	 */
	@Override
	protected synchronized void switchAllLightsToStopSignal() throws InterruptedException {
		log("all lights go RED/DONT_WALK");
		super.switchAllLightsToStopSignal();
		switchPedestrianLights(Signal.DONT_WALK);
		if (owner != null) owner.ready(id);	
	}
	
	/* 
	 * Method for switching pedestrian lights.
	 * Switching method for the vehicular lights are provided by superclass.
	 */
	protected void switchPedestrianLights(Signal newStatus) {
		log("switching pedestrian lights");
		for (int i = 0; i < 2; i++) {
			pedestrianLight[i].switchLight(newStatus);
		}
	}

	/* 
	 * This is the switching protocol for a linear light set. It ensures
	 * an opposite stop-signal status between between vehicular and pedestrian lights
	 * and the right ordering of switching depending on the current signal state of the 
	 * light set. 
	 * 
	 * @see LightSet#switchSet(Signal)
	 */
	protected void switchSet(Signal newStatus) {
		status = newStatus;
		if(newStatus == Signal.RED) {
			/* stop lights go first */
			if (pedestriansOn) // not necessary, but double-checking for safety
				switchPedestrianLights(Signal.WALK); 
			switchVehicleLights(Signal.RED);
		} else if (newStatus == Signal.GREEN || newStatus == Signal.ORANGE) {
			/* pedestrian lights go first */
			switchVehicleLights(newStatus);
			switchPedestrianLights(Signal.DONT_WALK);
		} else {
			switchPedestrianLights(Signal.OFF);
			switchVehicleLights(Signal.OFF);
		}
		log();
	}
	
	// getter for pedestrian lights controlled by this light set, by id (0 or 1)
	public PedestrianLight getPedestrianLight(int i) {
		return pedestrianLight[i];
	}

}

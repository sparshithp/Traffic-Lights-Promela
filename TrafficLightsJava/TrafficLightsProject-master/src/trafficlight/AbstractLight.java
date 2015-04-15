package trafficlight;
import java.util.Observer;

public abstract class AbstractLight extends ObservableEntity {
	
	/*
	 * Abstract superclass that defines behavior common to all types of lights.
	 * Vehicle (tricolor) and pedestrian (bimodal) lights are concrete subclasses.
	 * 
	 * This is an observable class (for testing and logging). 
	 */
	
	// initial signal state
	Signal status = Signal.OFF;
	
	protected int id;
	protected AbstractLightSet owner;

	protected AbstractLight(int id) {
		this.id = id;
	}
	
	// public constructor for testing and logging, accepts and observer
	public AbstractLight(int id, Observer observer) {
		this(id);
		this.observer = observer;
		if (observing) addObserver(observer); // part of Observer pattern
	}

	// real public constructor that accepts a reference to owning light set
	public AbstractLight(int id, AbstractLightSet owner) {
		this(id);
		this.owner = owner;
		if (observing) {
				// for Observer pattern, to make the top-most composite object an observer
				if (owner.getOwner() != null) { 
					addObserver(owner.getOwner().observer); 
				} else if (owner != null) {
					addObserver(owner.observer); 
				} 
		}
	}

	/* 
	 * Change signal state to new state of light.
	 * This method is overridden by subclasses. 
	 */
	public void switchLight(Signal newStatus) throws IllegalArgumentException {
		status = newStatus;
		log();
	}

	// getter for signal state of light
	public Signal getStatus() {
		return status;
	}

	
	// the rest is for logging and  testing
	
	public void log() {
		send(id + " " + status.toString());
	}

	public void log(String message) {
		send(id + " " + status.toString(), message);
	}

}
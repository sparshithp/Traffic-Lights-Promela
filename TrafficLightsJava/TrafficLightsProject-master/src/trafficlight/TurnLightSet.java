package trafficlight;
import java.util.Observer;

public class TurnLightSet extends AbstractLightSet implements Runnable {
	
	/* 
	 * Concrete class for a light set that controls protected 
	 * opposite (diagonal-complementary)left turns. 
	 * It consists of two tricolor vehicular lights. 
	 * Most of the behavior is inherited from superclass.
	 */
	
	protected TurnLightSet(int id) {
		super(id);
	}
	
	// public constructor for tests and logging, accepts an observer
	public TurnLightSet(int id, Observer observer) {
		super(id, observer);		
	}

	// real public constructor that accepts a reference to the owning intersection 
	public TurnLightSet(int id, Intersection owner) {
		super(id, owner);		
	}

	/* 
	 * The switching protocol for turn lights is based on the superclass.  
	 * @see LightSet#switchSet(Signal)
	 */
	protected void switchSet(Signal newStatus) {
		log("switching vehicle lights");
		status = newStatus;
		switchVehicleLights(newStatus);  
		
	}
}

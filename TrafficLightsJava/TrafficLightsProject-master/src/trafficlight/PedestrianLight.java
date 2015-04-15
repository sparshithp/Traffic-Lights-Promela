package trafficlight;
import java.util.Observer;

public class PedestrianLight extends AbstractLight {

	/*
	 * A bimodal vehicle light with signal states WALK and DONT_WALK.
	 * Most of the behavior is inherited from superclass. 
	 * See superclass. 
	 */

	protected PedestrianLight(int id) {
		super(id);
	}
	
	public PedestrianLight(int id, Observer observer) {
		super(id, observer);
	}
	
	
	public PedestrianLight(int id, AbstractLightSet owner) {
		super(id, owner);
	}

	/* 
	 * Switch signal state to new state and check type.
	 * Signal state can be Signal.OFF, Signal.WALK, Signal.DONT_WALK.
	 * @see Light#switchLight(Signal)
	 */
	@Override
	public void switchLight(Signal newStatus) throws IllegalArgumentException {
		if (!Signal.isPedestrianSignal(newStatus)) {
			throw new IllegalArgumentException("Invalid light signal: " + newStatus.toString());
		}
		super.switchLight(newStatus);
	}	

}

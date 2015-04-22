package trafficlight;
import java.util.Observer;

public class VehicleLight extends AbstractLight {
	
	/*
	 * A tricolor vehicle light with signal states OFF, RED, GREEN, ORANGE.
	 * Most of the behavior is inherited from superclass. 
	 * See superclass.
	 */
	
	protected VehicleLight(int id) {
		super(id);
	}
	
	public VehicleLight(int id, AbstractLightSet owner) {
		super(id, owner);
	}
	
	public VehicleLight(int id, Observer observer) {
		super(id, observer);
	}
	
	/* 
	 * Switch signal state to new state and check type.
	 * Signal state can be Signal.OFF, Signal.RED, Signal.GREEN, or Signal.ORANGE.
	 * @see Light#switchLight(Signal)
	 */
	@Override
	public void switchLight(Signal newStatus) throws IllegalArgumentException {
		if (!Signal.isTriColorSignal(newStatus)) {
			throw new IllegalArgumentException("Invalid light signal: " + newStatus.toString());
		}
		super.switchLight(newStatus); 
	}	
}

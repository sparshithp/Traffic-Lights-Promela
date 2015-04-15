package trafficlight;
import java.util.Observable;
import java.util.Observer;

public class ObserverEntity implements Observer {
	
	/* 
	 * A specialized version of the Observer class implementing the Observer pattern
	 * for the traffic lights system. 
	 * The class that represents the operator of the system extends this class. 
	 */
	
	private boolean firstUpdate = false;
	
	public ObserverEntity() {
	}

	@Override
	public void update(Observable object, Object message) {
		if (!firstUpdate) {
			display(">> Observer created: " + this.getClass().getName());
			firstUpdate = true;
		}
		String observedObject = object.getClass().getName();
		String text = "> " + observedObject + ": " + message; 
		display(text);
	}
	
	private void display(String text) {
		System.out.println(text);
	}

}

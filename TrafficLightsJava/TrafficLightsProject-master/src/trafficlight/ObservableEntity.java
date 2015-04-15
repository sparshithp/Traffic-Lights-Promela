package trafficlight;
import java.util.Observable;
import java.util.Observer;

public abstract class ObservableEntity extends Observable {
	
	
	/* 
	 * A specialized version of the Observable class implementing the Observer pattern
	 * for the traffic lights system. 
	 * To be observable, a component of the traffic light system extends this class. 
	 */
	
	// setting this variable to false in any superclass instance suppresses console logging 
	public boolean observing = true;
	
	public Observer observer;
	
	public void send(String message) {
		setChanged();
		if (observing) {
			notifyObservers(message);
		}
	}
	
	public void send(String status, String message) {
		send(status + "--" + message);
	}
	
}

package trafficlight;
import java.util.EnumSet;

public enum Signal {
	
		/*
		 * All possible signal states of a traffic light and a light set. 
		 */
	
		OFF, RED,  GREEN, ORANGE, WALK, DONT_WALK;
		
		static final EnumSet<Signal> VEHICLE_SIGNALS = EnumSet.of(OFF, RED, GREEN, ORANGE);
		static final EnumSet<Signal> PEDESTRIAN_SIGNALS = EnumSet.of(OFF, WALK, DONT_WALK);
		
		static boolean isTriColorSignal(Signal signal) {
			return VEHICLE_SIGNALS.contains(signal);
		}
		
		static boolean isPedestrianSignal(Signal signal) {
			return PEDESTRIAN_SIGNALS.contains(signal);
		}

}
	
	
	
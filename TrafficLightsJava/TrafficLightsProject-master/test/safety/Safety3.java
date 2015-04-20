package safety;

import static org.junit.Assert.*;
import trafficlight.AbstractLight;
import trafficlight.Intersection;
import trafficlight.Operator;
import trafficlight.PedestrianLight;
import trafficlight.Signal;

public class Safety3 implements Runnable {
	// represent the client of an intersection
	Operator operator = new Operator();

	// milliseconds for running the runIntersection test
	final static long TIME_LIMIT = 1000;
	
	PedestrianLight pedestrian_West_East_1;
	PedestrianLight pedestrian_West_East_2;
	PedestrianLight pedestrian_North_South_1;
	PedestrianLight pedestrian_North_South_2;
	AbstractLight  stopLight_West_East_1;
	AbstractLight  stopLight_West_East_2;
	AbstractLight  stopLight_North_South_1;
	AbstractLight  stopLight_North_South_2;

	Intersection intersection;

	public Safety3() {
		intersection = new Intersection(operator);
		pedestrian_West_East_1=intersection.getStopLightSet(1).getPedestrianLight(0);
		pedestrian_West_East_2=intersection.getStopLightSet(1).getPedestrianLight(1);
		pedestrian_North_South_1=intersection.getStopLightSet(0).getPedestrianLight(0);
		pedestrian_North_South_2=intersection.getStopLightSet(0).getPedestrianLight(1);
		stopLight_West_East_1=intersection.getStopLightSet(0).getTriColorLight(0);
		stopLight_West_East_2=intersection.getStopLightSet(0).getTriColorLight(1);
		stopLight_North_South_1=intersection.getStopLightSet(1).getTriColorLight(0);
		stopLight_North_South_2=intersection.getStopLightSet(1).getTriColorLight(1);
		intersection.enable();
	}

	public void run() {
		while (true) {
			try {
				//System.out.println("intersection.getStopLightSet(1).getPedestrianLight(0):"+ intersection.getStopLightSet(1).getPedestrianLight(0).getStatus());
				if (pedestrian_West_East_1.getStatus().equals(Signal.WALK)||pedestrian_West_East_2.getStatus().equals(Signal.WALK)) {
					//System.out.println("*****************Catch pedestrianLight************************");
					if(!stopLight_North_South_1.getStatus().equals(Signal.RED)&&!stopLight_North_South_1.getStatus().equals(Signal.OFF)							
						&&!stopLight_North_South_2.getStatus().equals(Signal.RED)&&!stopLight_North_South_2.getStatus().equals(Signal.OFF)){
						System.out.println("First Condition");
						System.out.println(stopLight_North_South_1.getStatus());
						System.out.println(stopLight_North_South_2.getStatus());
						assert(false);
						System.exit(0);
					}
				}
				
				if (pedestrian_North_South_1.getStatus().equals(Signal.WALK)||pedestrian_North_South_2.getStatus().equals(Signal.WALK)) {
					//System.out.println("*****************Catch pedestrianLight************************");
					if(!stopLight_West_East_1.getStatus().equals(Signal.RED)&&!stopLight_West_East_1.getStatus().equals(Signal.OFF)							
						&&!stopLight_West_East_2.getStatus().equals(Signal.RED)&&!stopLight_West_East_2.getStatus().equals(Signal.OFF)){
						System.out.println("Seond Condition");
						System.out.println(stopLight_West_East_1.getStatus());
						System.out.println(stopLight_West_East_2.getStatus());
						assert(false);
						System.exit(0);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new Thread(new Safety3()).start();
		//Thread.sleep(50);
		//System.exit(0);

	}

}

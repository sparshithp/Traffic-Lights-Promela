package safety;

import static org.junit.Assert.*;
import trafficlight.AbstractLight;
import trafficlight.Intersection;
import trafficlight.Operator;
import trafficlight.PedestrianLight;
import trafficlight.Signal;

public class Safety4 implements Runnable {
	// represent the client of an intersection
	Operator operator = new Operator();

	// milliseconds for running the runIntersection test
	final static long TIME_LIMIT = 1000;

	Intersection intersection;
	
	PedestrianLight pedestrian_West_East_1;
	PedestrianLight pedestrian_West_East_2;
	PedestrianLight pedestrian_North_South_1;
	PedestrianLight pedestrian_North_South_2;
	AbstractLight  turnLight_West_East_1;
	AbstractLight  turnLight_West_East_2;
	AbstractLight  turnLight_North_South_1;
	AbstractLight  turnLight_North_South_2;

	public Safety4() {
		intersection = new Intersection(operator);
		pedestrian_West_East_1=intersection.getStopLightSet(1).getPedestrianLight(0);
		pedestrian_West_East_2=intersection.getStopLightSet(1).getPedestrianLight(1);
		pedestrian_North_South_1=intersection.getStopLightSet(0).getPedestrianLight(0);
		pedestrian_North_South_2=intersection.getStopLightSet(0).getPedestrianLight(1);
		turnLight_West_East_1=intersection.getTurnLightSet(0).getTriColorLight(0);
		turnLight_West_East_2=intersection.getTurnLightSet(0).getTriColorLight(1);
		turnLight_North_South_1=intersection.getTurnLightSet(1).getTriColorLight(0);
		turnLight_North_South_2=intersection.getTurnLightSet(1).getTriColorLight(1);
		intersection.enable();
	
		
	}

	public void run() {
		while (true) {
			try {
				if (pedestrian_West_East_1.getStatus().equals(Signal.WALK)||pedestrian_West_East_2.getStatus().equals(Signal.WALK)) {//L1 w-e direction
					//first condition
					if(!turnLight_North_South_1.getStatus().equals(Signal.RED)&&!turnLight_North_South_1.getStatus().equals(Signal.OFF)
						&&!turnLight_North_South_2.getStatus().equals(Signal.RED)&&!turnLight_North_South_2.getStatus().equals(Signal.OFF)){					
						System.out.println("First Condition");
						System.out.println(turnLight_North_South_1.getStatus());
						System.out.println(turnLight_North_South_2.getStatus());
						assert(false);
						System.exit(0);
					}
					//second condition
					if(!turnLight_West_East_1.getStatus().equals(Signal.RED)&&!turnLight_West_East_1.getStatus().equals(Signal.OFF)
						&&!turnLight_West_East_2.getStatus().equals(Signal.RED)&&!turnLight_West_East_2.getStatus().equals(Signal.OFF)){					
						System.out.println("second Condition");
						System.out.println(turnLight_West_East_1.getStatus());
						System.out.println(turnLight_West_East_2.getStatus());
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
		new Thread(new Safety4()).start();
		//Thread.sleep(50);
		//System.exit(0);

	}

}

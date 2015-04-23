/*Global variables*/
mtype = {OFF, RED,  GREEN, ORANGE, WALK, DONT_WALK};
chan to_L0 = [255] of {mtype};/*channel to vehicle light set 0*/
chan to_L1 = [255] of {mtype};/*channel to vehicle light set 1*/
chan to_T0 = [255] of {mtype}/*channel to  turnlight set 0*/
chan to_T1 = [255] of {mtype}/*channel to turn light set 1*/
mtype L[2] =  OFF;/*lights of vehicle light L[0] L[1]*/
mtype T[2] = OFF; /*lights of Turn light T[0] T[1]*/
mtype VehicleLights[4] = OFF;/*sub lights of vehicle lights*/
mtype TurnLights[4] = OFF;/*sub lights of turn lights*/
mtype PedestrianLights[4] = OFF;
int i;

/*Switch L[index] to OFF */
inline switchVehicleLightToOFF(index){
	L[index] = OFF;	
	atomic{ 
		i=(index+1)*2-2;
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 VehicleLights[i] = OFF;
		}
	}
}

/*Switch Pedestrian[index] to Walk */
inline switchPedestrianLightsToWALK( index){	
	atomic{
		i=(index+1)*2-2;
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
		 	 PedestrianLights[i] = WALK;
		}
	}
}

/*Switch Pedestrian[index] to DONT_WALK */
inline switchPedestrianLightsToDONT_WALK( index){
	atomic{
		i=(index+1)*2-2;
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 PedestrianLights[i] = DONT_WALK;
		}
	}
}


/*Switch L[index] to RED */
inline switchVehicleLightToRED( index){
	switchPedestrianLightsToWALK(index);/*This part loyal to implementation*/
	L[index] = RED;	
	atomic{
		i=(index+1)*2-2;
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 VehicleLights[i] = RED;
		}
	}
}

/*Switch L[index] to GREEN */
inline switchVehicleLightToGREEN( index){
	L[index] = GREEN;	
	atomic{
		i=(index+1)*2-2;
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 VehicleLights[i] = GREEN;
		}
	}
	switchPedestrianLightsToDONT_WALK(index);/*This part loyal to implementation*/
}

/*Switch L[index] to ORANGE */
inline switchVehicleLightToORANGE( index){
	L[index] = ORANGE;
	atomic{
		i=(index+1)*2-2;
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 VehicleLights[i] = ORANGE;
		}
	}
}

/*Switch T[index] to OFF */
inline switchTurnLightToOFF( index){
	T[index] = OFF;
	atomic{
		i=(index+1)*2-2;	
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 TurnLights[i] = OFF;
		}
	}
}

/*Switch T[index] to RED */
inline switchTurnLightToRED( index){
	T[index] = RED;	
	atomic{
		i=(index+1)*2-2;
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 TurnLights[i] = RED;
		}
	}
}

/*Switch T[index] to GREEN */
inline switchTurnLightToGREEN(index){
	T[index] = GREEN;
	atomic{
		i=(index+1)*2-2;
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 TurnLights[i] = GREEN;
		}
	}	
}

/*Switch T[index] to ORANGE */
inline switchTToORANGE(index){
	T[index] = ORANGE;
	atomic{
		i=(index+1)*2-2;
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 TurnLights[i] = ORANGE;
		}
	}
}


inline printVehicleLight(index){
	i=0;
	for (i :  (index+1)*2-2 .. (index+1)*2-1) {
		printf("VehicleLights[i] = %d\n", VehicleLights[i]);		
	}
}

/*init{
	run printVehicleLight(0);
	run switchVehicleLightToRED(0);
	run printVehicleLight(0);
	run printVehicleLight(1);
	run switchVehicleLightToRED(1);
	run printVehicleLight(1);
}*/

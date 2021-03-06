/*Global variables*/
mtype = {OFF, RED,  GREEN, ORANGE, WALK, DONT_WALK};/*Traffic Light Status*/
mtype = {INIT, ENABLE, ADVANCE, PRE_STOP, STOP, ALL_STOP};/*Event Status*/
mtype = {DISABLE, INTERRUPT} /*Special signal to disable intersection*/
mtype = {UnblockPed, L0PedBlock, L1PedBlock, LInitDone, TInitDone, L0_Done, L1_Done, T0_Done, T1_Done, BlockPed } /* Notification Events */

chan to_L0 = [1] of {mtype};/*Channel to vehicle light set 0*/
chan to_L1 = [1] of {mtype};/*Channel to vehicle light set 1*/
chan to_T0 = [1] of {mtype};/*Channel to  turnlight set 0*/
chan to_T1 = [1] of {mtype};/*Channel to turn light set 1*/
chan to_Inter = [1] of {mtype}; /* channel for intersection */

mtype L[2] = OFF;/*Lights of linear light L[0] L[1]*/
mtype T[2] = OFF; /*Lights of turn light T[0] T[1]*/
mtype VehicleLights[4] = OFF;/*sub lights of linear lights*/
mtype TurnLights[4] = OFF;/*sub lights of turn lights*/
mtype PedestrianLights[4] = OFF;/*pedertaisn light status of linear light*/

/*A function that set all the lights to stop*/
inline resetLightSets(){
	switchVehicleLightToOFF(0);
	switchTurnLightToOFF(0);
	switchVehicleLightToOFF(1);
	switchTurnLightToOFF(1);
}

/*Switch L[index] to OFF */
inline switchVehicleLightToOFF(index){
	L[index] = OFF;	
	atomic{ 
		int i=(index+1)*2-2;
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 VehicleLights[i] = OFF;
		}
		switchPedestrianLightsToOFF(index);
	}
}

/*Switch Pedestrian[index] to Walk */
inline switchPedestrianLightsToWALK( index){	
	atomic{
		int p=(index+1)*2-2;
		for (p :  (index+1)*2-2 .. (index+1)*2-1) {
		 	 PedestrianLights[p] = WALK;
		}
	}
}

/*Switch Pedestrian[index] to DONT_WALK */
inline switchPedestrianLightsToDONT_WALK(index){
	atomic{
		int q=(index+1)*2-2;
		for (q :  (index+1)*2-2 .. (index+1)*2-1) {
			 PedestrianLights[q] = DONT_WALK;
		}
	}
}

/*Switch Pedestrian[index] to OFF */
inline switchPedestrianLightsToOFF(index){
	atomic{
		int r=(index+1)*2-2;
		for (r :  (index+1)*2-2 .. (index+1)*2-1) {
			 PedestrianLights[r] = OFF;
		}
	}
}


/*Switch L[index] to RED */
inline switchVehicleLightToRED( index){
	switchPedestrianLightsToWALK(index);/*This part loyal to implementation*/
	L[index] = RED;	
	atomic{
		int i=(index+1)*2-2;
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 VehicleLights[i] = RED;
		}
	}
}

/*Switch L[index] to GREEN */
inline switchVehicleLightToGREEN( index){
	L[index] = GREEN;	
	atomic{
		int j=(index+1)*2-2;
		for (j :  (index+1)*2-2 .. (index+1)*2-1) {
			 VehicleLights[j] = GREEN;
		}
	}
	switchPedestrianLightsToDONT_WALK(index);/*This part loyal to implementation*/
}

/*Switch L[index] to ORANGE */
inline switchVehicleLightToORANGE( index){
	L[index] = ORANGE;
	atomic{
		int k=(index+1)*2-2;
		for (k :  (index+1)*2-2 .. (index+1)*2-1) {
			 VehicleLights[k] = ORANGE;
		}
	}
}

/*Switch T[index] to OFF */
inline switchTurnLightToOFF( index){
	T[index] = OFF;
	atomic{
		int i=(index+1)*2-2;	
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 TurnLights[i] = OFF;
		}
	}
}

/*Switch T[index] to RED */
inline switchTurnLightToRED( index){
	T[index] = RED;	
	atomic{
		int i=(index+1)*2-2;
		for (i :  (index+1)*2-2 .. (index+1)*2-1) {
			 TurnLights[i] = RED;
		}
	}
}

/*Switch T[index] to GREEN */
inline switchTurnLightToGREEN(index){
	T[index] = GREEN;
	atomic{
		int j=(index+1)*2-2;
		for (j :  (index+1)*2-2 .. (index+1)*2-1) {
			 TurnLights[j] = GREEN;
		}
	}	
}

/*Switch T[index] to ORANGE */
inline switchTurnLightToORANGE(index){
	T[index] = ORANGE;
	atomic{
		int k=(index+1)*2-2;
		for (k :  (index+1)*2-2 .. (index+1)*2-1) {
			 TurnLights[k] = ORANGE;
		}
	}
}


inline printVehicleLight(index){
	int i=0;
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



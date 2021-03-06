#include "GlobalFunction.pml"

/*
This model reveals the falut in second safety property, this model is just small enough to capture the falut 
*/


/*Continuely switch the light of L[0]*/
proctype L0(){
	int i;/*Paramaters that will used to in global function*/
	again:
	 switchVehicleLightToRED(0);
	 switchVehicleLightToGREEN(0);
	 switchVehicleLightToORANGE(0);		
	goto again
}
/*Continuely switch the light of L[1]*/
proctype L1(){
	int i;/*Parameters that will be used in global function*/
	again:
	 switchVehicleLightToRED(1);
	 switchVehicleLightToGREEN(1);
	 switchVehicleLightToORANGE(1);		
	goto again
}

proctype Monitor(){
	do	
	/*when a north-south pedestrian light is  WALK, the opposite vehicle stoplight must be RED (include sub light)*/
	::atomic{(PedestrianLights[0] == WALK|| PedestrianLights[1] == WALK) -> assert(L[0] == RED && VehicleLights[0] == RED && VehicleLights [1] == RED)};
	/*when a east-west pedestrian light is  WALK, the opposite vehicle stoplight must be RED (include sub light)*/
	::atomic{(PedestrianLights[2] == WALK|| PedestrianLights[3] == WALK) -> assert(L[1] == RED && VehicleLights[2] == RED && VehicleLights [3] == RED)};			
	od
}

init{
	run L0();
	run L1();
	run Monitor();
}
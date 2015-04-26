#include "GlobalFunction.pml"

/*
This model reveals the falut in third safety property, this model is just small enough to capture the falut 
*/


/*Continuely switch the light of L[0]*/
proctype L0(){
	again:
	 switchVehicleLightToRED(0);
	 switchVehicleLightToGREEN(0);
	 switchVehicleLightToORANGE(0);		
	goto again
}
/*Continuely switch the light of L[1]*/
proctype L1(){
	again:
	 switchVehicleLightToRED(1);
	 switchVehicleLightToGREEN(1);
	 switchVehicleLightToORANGE(1);		
	goto again
}

proctype Monitor(){
	/*The assertion to verify third and fifh safety property*/	
	do
	/*when a north-south pedestrian light is  WALK, the opposite vehicle stoplight must be RED (include sub light)*/
	::atomic{(PedestrianLights[0] == WALK|| PedestrianLights[1] == WALK) -> assert(L[0] == RED && VehicleLights[0] == RED && VehicleLights [1] == RED)};
	/*when a east-west pedestrian light is  WALK, the opposite vehicle stoplight must be RED (include sub light)*/
	::atomic{(PedestrianLights[2] == WALK|| PedestrianLights[3] == WALK) -> assert(L[1] == RED && VehicleLights[2] == RED && VehicleLights [3] == RED)};			
	od
	
	/*The assertion to verify sixth safety property*/
	do	
	/*when the opposite vehicle stoplight is GREEN (include sub light) the a north-south pedestrian light must be DONT_WALK,*/
	::atomic{(L[0] == GREEN && VehicleLights[0] == GREEN && VehicleLights [1] == GREEN) -> assert(PedestrianLights[0] == DONT_WALK|| PedestrianLights[1] == DONT_WALK)};
	/*when the opposite vehicle stoplight is GREEN (include sub light) the a east-west pedestrian light must be DONT_WALK,*/
	::atomic{(L[1] == GREEN && VehicleLights[2] == GREEN && VehicleLights [3] == GREEN) -> assert(PedestrianLights[2] == DONT_WALK|| PedestrianLights[3] == DONT_WALK)};		
	od

}

init{
	run L0();
	run L1();
	run Monitor();
}
#include "GlobalFunction.pml"

/*
This model reveals the falut in second safety property, this model is just small enough to capture the falut 
*/
/*Function to check the status of light*/
inline checkLightStatus(){
	for (i :  0 .. 3) {
		assert(VehicleLights[i] == OFF);
		assert(TurnLights[i] == OFF);
		assert(PedestrianLights[i] == OFF);
	}
}

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
/*Continuely swtich the light of T[0]*/
proctype T0(){
	int i;/*Parameters that will be used in global function*/
	again:
	 switchTurnLightToRED(0);
	 switchTurnLightToGREEN(0);
	 switchTurnLightToORANGE(0);		
	goto again
}
/*Continuely swtich the light of T[1]*/
proctype T1(){
	int i;/*Parameters that will be used in global function*/
	again:
	 switchTurnLightToRED(1);
	 switchTurnLightToGREEN(1);
	 switchTurnLightToORANGE(1);		
	goto again
}

proctype Monitor(){
	int i;/*Temporal parameters*/
	do
	::resetLightSets()->checkLightStatus();	
	od
}



init{
	run L0();
	run L1();
	run T1();
	run T0();
	run Monitor();
}
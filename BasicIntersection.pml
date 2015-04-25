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
/*Function to disable the 4 light set*/
proctype disable(){
	do
	::to_L0!DISABLE;
	::to_L1!DISABLE;
	::to_T0!DISABLE;
	::to_T1!DISABLE;
	od
}

/*Continuely switch the light of L[0]*/
proctype L0(){
	int i;/*Paramaters that will used to in global function*/
	 check:
	 if 
	 ::to_L0?DISABLE -> goto wait;
	 ::empty(to_L0)-> goto process;
	 fi
	process:
	 switchVehicleLightToRED(0);
	 switchVehicleLightToGREEN(0);
	 switchVehicleLightToORANGE(0);		
	goto check;
	wait:/*do nothing*/
}
/*Continuely switch the light of L[1]*/
proctype L1(){
	int i;/*Parameters that will be used in global function*/
	check:
	 if 
	 ::to_L1?DISABLE -> goto wait;
	 ::empty(to_L1)-> goto process;
	 fi
	process:
	 switchVehicleLightToRED(1);
	 switchVehicleLightToGREEN(1);
	 switchVehicleLightToORANGE(1);		
	goto check
	wait:/*do nothing*/
}
/*Continuely swtich the light of T[0]*/
proctype T0(){
	int i;/*Parameters that will be used in global function*/
	 check:
	 if 
	 ::to_T0?DISABLE -> goto wait;
	 ::empty(to_T0)-> goto process;
	 fi
	process:
	 switchTurnLightToRED(0);
	 switchTurnLightToGREEN(0);
	 switchTurnLightToORANGE(0);		
	goto check
	wait:/*do nothing*/
}
/*Continuely swtich the light of T[1]*/
proctype T1(){
	int i;/*Parameters that will be used in global function*/
	 check:
	 if 
	 ::to_T1?DISABLE -> goto wait;
	 ::empty(to_T0)-> goto process;
	 fi
	process:
	 switchTurnLightToRED(1);
	 switchTurnLightToGREEN(1);
	 switchTurnLightToORANGE(1);		
	goto check
	wait:/*do nothing*/
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
	/*run disable();*/
	run Monitor();
}
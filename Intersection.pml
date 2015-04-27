#include "GlobalFunction.pml"
#include "Property.pml"

mtype L0_status, L1_status;

/* handles interaction with all other lights */
proctype Intersection(){
	do
	:: to_Inter?ENABLE 		-> to_L0!INIT;		/* Recieve the signal from Init */
	:: to_Inter?LInitDone 	-> to_T0!INIT;		/* Initialize all the lights */
	:: to_Inter?TInitDone 	-> to_L0!ADVANCE;	/* Advance Light set L0 and L1 */
	:: to_Inter?L0_Done 	-> to_L1!ADVANCE;	
	:: to_Inter?L1_Done 	-> to_L0!BlockPed;	/* Block Ped of L0 and L1 light sets */
	:: to_Inter?L0PedBlock 	-> to_L1!BlockPed;
	:: to_Inter?L1PedBlock 	-> to_T0!ADVANCE;	/* Turn on Turn Lights Sets T0 and T1 */
	:: to_Inter?T0_Done 	-> to_T1!ADVANCE;
	:: to_Inter?T1_Done 	-> to_L0!UnblockPed;/* Unblock Pedestrians of all light sets */
	:: to_Inter?UnblockPed 	-> to_L0!ADVANCE;	/* Infinite repetition, advance L0 again */
	od
}


proctype L0(){
	bool pedOn = true;
	do
	:: to_L0?[INIT] && pedOn 	-> atomic { to_L0?INIT; switchVehicleLightToRED(0); to_L1!INIT;};	/* Recieve signal from intersection to start */
	:: to_L0?[INIT] && !pedOn 	-> atomic { to_L0?INIT; L0_status = RED; to_L1!INIT;};			/* Double check pedestrian lights */
	:: to_L0?ADVANCE 			-> atomic { switchVehicleLightToGREEN(0); to_L0!PRE_STOP; };	/* Start the Light signal flow, Red -> Green -> Orange -> Red */
	:: to_L0?PRE_STOP 			-> atomic { switchVehicleLightToORANGE(0); switchPedestrianLightsToDONT_WALK(0); to_L0!STOP;};
	:: to_L0?[STOP] && pedOn 	-> atomic { to_L0?STOP; switchPedestrianLightsToWALK(0); switchVehicleLightToRED(0); to_Inter!L0_Done }; /* Notify Intersection process */
	:: to_L0?[STOP] && !pedOn 	-> atomic { to_L0?STOP; switchVehicleLightToRED(0); to_Inter!L0_Done; };
	:: to_L0?BlockPed 			-> atomic { pedOn = false; to_L0!ALL_STOP};			/* Block Ped as instructed by Intersection */
	:: to_L0?ALL_STOP 			-> atomic { switchVehicleLightToRED(0); switchPedestrianLightsToDONT_WALK(0); to_Inter!L0PedBlock}; 
	:: to_L0?UnblockPed 		-> atomic { pedOn = true; to_L1!UnblockPed; }; /* Unblock the pedestrians */
	od
}

proctype L1(){
	bool pedOn = true;
	/* The below code represents same flow as L0 process, read comments above to understand the flow */
	do
	:: to_L1?[INIT] && pedOn 	-> atomic { to_L1?INIT; switchVehicleLightToRED(1); to_Inter!LInitDone; };
	:: to_L1?[INIT] && !pedOn 	-> atomic { to_L1?INIT; L1_status = RED;};
	:: to_L1?ADVANCE 			-> atomic { switchVehicleLightToGREEN(1); to_L1!PRE_STOP; };
	:: to_L1?PRE_STOP 			-> atomic { switchVehicleLightToORANGE(1); switchPedestrianLightsToDONT_WALK(1); to_L1!STOP; };
	:: to_L1?[STOP] && pedOn 	-> atomic { to_L1?STOP; switchPedestrianLightsToWALK(1); switchVehicleLightToRED(1); to_Inter!L1_Done };
	:: to_L1?[STOP] && !pedOn 	-> atomic { to_L1?STOP; switchVehicleLightToRED(1); to_Inter!L1_Done };
	:: to_L1?BlockPed 			-> atomic { pedOn = false; to_L1!ALL_STOP};
	:: to_L1?ALL_STOP 			-> atomic { switchVehicleLightToRED(0); switchPedestrianLightsToDONT_WALK(0); to_Inter!L1PedBlock};
	:: to_L1?UnblockPed 		-> atomic { pedOn = true; to_Inter!UnblockPed;};
	od
}

proctype T0(){
	do
	:: to_T0?INIT 		-> atomic { switchTurnLightToRED(0); 	to_T1!INIT;};	/* Initialize light set */
	:: to_T0?ADVANCE 	-> atomic { switchTurnLightToGREEN(0); 	to_T0!PRE_STOP;}; /* Adnace Turn light set Red->Green->Orange->Red */
	:: to_T0?PRE_STOP 	-> atomic { switchTurnLightToORANGE(0); to_T0!STOP;};	
	:: to_T0?STOP 		-> atomic { switchTurnLightToRED(0); 	to_Inter!T0_Done;};	/* Notify intersection process of its completion */
	od
}

proctype T1(){
	/* T1 follows same flow as T0, read the above comments to understand the below flow */
	do
	:: to_T1?INIT 		-> atomic { switchTurnLightToRED(1); to_Inter!TInitDone;}
	:: to_T1?ADVANCE 	-> atomic { switchTurnLightToGREEN(1); to_T1!PRE_STOP;};
	:: to_T1?PRE_STOP 	-> atomic { switchTurnLightToORANGE(1); to_T1!STOP;};
	:: to_T1?STOP 		-> atomic { switchTurnLightToRED(1); to_Inter!T1_Done;};
	od
}

init{
	run Intersection();
	run L0();
	run L1();
	run T0();
	run T1();
	to_Inter!ENABLE; /* Enable Intersection */
}

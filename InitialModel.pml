/* Different States of the system */
mtype ={GREEN, RED, WALK, DONT_WALK};

/* Channel for each light set */
chan to_L0 = [1] of {bit};
chan to_L1 = [1] of {bit};
chan to_T0 = [1] of {bit};
chan to_T1 = [1] of {bit};

/* Initial state of every light set */
mtype L0_status = RED;
mtype L1_status = RED;

mtype L0_walk = DONT_WALK;
mtype L1_walk = DONT_WALK;

mtype T0_status = RED;
mtype T1_status = RED;

/**************************** Liveliness Properties Verification *****************************/
/*
ltl pedCrossAnyDirEventually { [](<>(L0_walk == WALK) && <>(L1_walk == WALK)) }
ltl vehCrossAnyDirEventually { [](<>(L1_status == GREEN) && <>(L0_status == GREEN) && 
								  <>(T1_status == GREEN) && <>(T0_status == GREEN))
								}
ltl vehCanMakeProtLeftTurn { [](<>(T0_status == GREEN && L1_status == RED && L0_status == RED && T1_status == RED) &&
								<>(T1_status == GREEN && L1_status == RED && L0_status == RED && T0_status == RED))
							}
ltl redUntilGreen { [](	(L1_status == RED) -> X<>(L1_status == GREEN) || 
						(L0_status == RED) -> X<>(L0_status == GREEN) ||
						(T1_status == RED) -> X<>(T1_status == GREEN) || 
						(T0_status == RED) -> X<>(T0_status == GREEN))
						}
*/ 

/*************************** End of Liveliness property verification **************************/

/*************************** Process Modeling **********************************************/

/* Asserts below verify the safety properties */ 
proctype L0(){	
	do
		::to_L0?1 -> atomic{L0_status = GREEN; L1_walk = WALK; assert L1_status == RED; 
								assert T0_status == RED; assert T1_status == RED;};
		::L0_status == GREEN -> atomic{L0_status = RED; L1_walk=DONT_WALK; to_T1!1; assert L0_walk == DONT_WALK;};
		else 
	od
}

proctype L1(){	
	do
		::to_L1?1 -> atomic{L1_status = GREEN; L0_walk = WALK; assert L0_status == RED;
								assert T0_status == RED; assert T1_status == RED;};
		::L1_status == GREEN -> atomic{L1_status = RED; L0_walk = DONT_WALK; to_T0!1; assert L1_walk == DONT_WALK;};
		else 
	od
}

proctype T0(){
	do
		::to_T0?1 -> T0_status = GREEN;
		::T0_status == GREEN -> atomic{T0_status = RED; to_L0!1} ;
		else
	od
}

proctype T1(){
	do
		::to_T1?1 -> T1_status = GREEN;
		::T1_status == GREEN -> atomic{T1_status = RED; to_L1!1};
		else 
	od
}


init{
	to_L1!1;
	run L0();
	run L1();
	run T0();
	run T1();
}

/****************************************** End of Model ***********************************/

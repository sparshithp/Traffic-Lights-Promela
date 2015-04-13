mtype ={GREEN, RED, ORANGE, WALK, DONT_WALK};
chan to_L0 = [1] of {bit};
chan to_L1 = [1] of {bit};
chan to_T0 = [1] of {bit};
chan to_T1 = [1] of {bit};
mtype L0_status = RED;
mtype L1_status = RED;
mtype L0_walk = DONT_WALK;
mtype L1_walk = DONT_WALK;
mtype T0_status = RED;
mtype T1_status = RED;

proctype L0(){	
	do
		::to_L0?1 -> atomic{L0_status = GREEN; L1_walk = WALK};
		::L0_status == GREEN -> L0_status = ORANGE;
		::L0_status == ORANGE -> atomic{L0_status = RED; L1_walk=DONT_WALK; to_T1!1};
		else 
	od
}

proctype L1(){	
	do
		::to_L1?1 -> atomic{L1_status = GREEN; L0_walk = WALK};
		::L1_status == GREEN -> L1_status = ORANGE;
		::L1_status == ORANGE -> atomic{L1_status = RED; L0_walk = DONT_WALK; to_T0!1};
		else 
	od
}

proctype T0(){
	do
		::to_T0?1 -> T0_status = GREEN;
		::T0_status == GREEN -> T0_status = ORANGE;
		::T0_status == ORANGE -> atomic{T0_status = RED; to_L0!1} ;
		else
	od
}

proctype T1(){
	do
		::to_T1?1 -> T1_status = GREEN;
		::T1_status == GREEN -> T1_status = ORANGE;
		::T1_status == ORANGE -> atomic{T1_status = RED; to_L1!1};
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

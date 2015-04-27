
/********************************** Liveliness Properties ***********************************/
/*
########### Always, eventually: incoming pedestrians from any direction can cross the intersection in that direction.
ltl pedCrossAnyDirEventually { [](<>(PedestrianLights[0] == WALK) && <>(PedestrianLights[1] == WALK) 
								&& <>(PedestrianLights[2] == WALK) && <>(PedestrianLights[3] == WALK)) }

########### Always, eventually: incoming vehicles from any direction can cross the intersection in that direction.
ltl vehCrossAnyDirEventually { [](<>(VehicleLights[0] == GREEN && L[0] == GREEN ) 	&& <>(VehicleLights[1] == GREEN && L[0] == GREEN) 	&&
									<>(VehicleLights[2] == GREEN && L[1] == GREEN ) && <>(VehicleLights[3] == GREEN && L[1] == GREEN ) 	&& 
								 	<>(TurnLights[0] == GREEN && T[0] == GREEN) 	&& <>(TurnLights[1] == GREEN && T[0] == GREEN)		&&
								 	<>(TurnLights[2] == GREEN && T[1] == GREEN)		&& <>(TurnLights[3] == GREEN && T[1] == GREEN) ) }

########## Always, eventually: incoming vehicles from any direction can make a protected left turn
ltl vehCanMakeProtLeftTurn { []<>((TurnLights[0] == GREEN || T[0] == GREEN || TurnLights[1] == GREEN) -> (
								(PedestrianLights[0] == DONT_WALK && PedestrianLights[1] == DONT_WALK && PedestrianLights[2] == DONT_WALK && PedestrianLights[3] == DONT_WALK) &&
								(VehicleLights[0] == RED && VehicleLights[1] == RED && VehicleLights[2] == RED && VehicleLights[3] == RED && L[0] == RED && L[1] == RED) &&
								(TurnLights[2] == RED && TurnLights[3] == RED && T[1] == RED) &&
								(TurnLights[0] == GREEN && T[0] == GREEN && TurnLights[1] == GREEN)
							))}

########## For any vehicle light (stoplight or turn light), always: the signal eventually turns ORANGE.
ltl eventuallyOrange { [](<>(VehicleLights[0] == ORANGE && VehicleLights[1] == ORANGE && L[0] == ORANGE ) &&
							<>(VehicleLights[2] == ORANGE && VehicleLights[3] == ORANGE && L[1] == ORANGE ))}

########## For any vehicle light (stoplight or turn light), always: if a GREEN signal is on, it stays on until the signal turns ORANGE.
ltl greenUntilOrange { []((VehicleLights[0] == GREEN || VehicleLights[1] == GREEN || L[0] == GREEN) -> 
							(VehicleLights[0] == GREEN && VehicleLights[1] == GREEN && L[0] == GREEN) U (VehicleLights[0] == ORANGE && VehicleLights[1] == ORANGE && L[0] == ORANGE))
					&& []((VehicleLights[2] == GREEN || VehicleLights[3] == GREEN || L[1] == GREEN) -> 
							(VehicleLights[2] == GREEN && VehicleLights[3] == GREEN && L[1] == GREEN) U (VehicleLights[2] == ORANGE && VehicleLights[3] == ORANGE && L[1] == ORANGE))
					&& []((TurnLights[0] == GREEN || T[0] == GREEN || TurnLights[1] == GREEN) ->
							(TurnLights[0] == GREEN && T[0] == GREEN && TurnLights[1] == GREEN) U (TurnLights[0] == ORANGE && T[0] == ORANGE && TurnLights[1] == ORANGE))
					&& []((TurnLights[2] == GREEN || T[1] == GREEN || TurnLights[3] == GREEN) ->
							(TurnLights[2] == GREEN && T[1] == GREEN && TurnLights[3] == GREEN) U (TurnLights[2] == ORANGE && T[1] == ORANGE && TurnLights[3] == ORANGE))
					}

########## For any vehicle light (stoplight or turn light), always: if a RED signal is on, it stays on until the signal turns GREEN.
ltl greenUntilOrange { []((VehicleLights[0] == RED || VehicleLights[1] == RED || L[0] == RED) -> 
							(VehicleLights[0] == RED && VehicleLights[1] == RED && L[0] == RED) U (VehicleLights[0] == GREEN && VehicleLights[1] == GREEN && L[0] == GREEN))
					&& []((VehicleLights[2] == RED || VehicleLights[3] == RED || L[1] == RED) -> 
							(VehicleLights[2] == RED && VehicleLights[3] == RED && L[1] == RED) U (VehicleLights[2] == GREEN && VehicleLights[3] == GREEN && L[1] == GREEN))
					&& []((TurnLights[0] == RED || T[0] == RED || TurnLights[1] == RED) ->
							(TurnLights[0] == RED && T[0] == RED && TurnLights[1] == RED) U (TurnLights[0] == GREEN && T[0] == GREEN && TurnLights[1] == GREEN))
					&& []((TurnLights[2] == RED || T[1] == RED || TurnLights[3] == RED) ->
							(TurnLights[2] == RED && T[1] == RED && TurnLights[3] == RED) U (TurnLights[2] == GREEN && T[1] == GREEN && TurnLights[3] == GREEN))
					}
*/

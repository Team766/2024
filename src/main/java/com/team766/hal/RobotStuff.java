package com.team766.hal;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;

public interface RobotStuff {
	void initializeMechanisms();
	Procedure createOI();
	AutonomousMode[] getAutonomousModes();
}

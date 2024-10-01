package com.team766.robot.example;

import com.team766.framework.AutonomousMode;
import com.team766.framework.RuleEngine;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.example.mechanisms.*;
import com.team766.robot.example.procedures.*;

public class Robot implements RobotConfigurator {
    private ExampleMechanism exampleMechanism;

    @Override
    public void initializeMechanisms() {
        // Initialize mechanisms here
        exampleMechanism = new ExampleMechanism();
    }

    @Override
    public RuleEngine createOI() {
        return new OI(exampleMechanism);
    }

    @Override
    public RuleEngine createLights() {
        return new Lights();
    }

    @Override
    public AutonomousMode[] getAutonomousModes() {
        return new AutonomousMode[] {
            // Add autonomous modes here like this:
            //    new AutonomousMode("NameOfAutonomousMode", () -> new MyAutonomousProcedure()),
            //
            // If your autonomous procedure has constructor arguments, you can
            // define one or more different autonomous modes with it like this:
            //    new AutonomousMode("DriveFast", () -> new DriveStraight(1.0)),
            //    new AutonomousMode("DriveSlow", () -> new DriveStraight(0.4)),

            new AutonomousMode("DoNothing", () -> new DoNothing(exampleMechanism)),
        };
    }
}

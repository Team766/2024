package com.team766.framework3;

import static org.junit.jupiter.api.Assertions.*;

import com.team766.TestCase3;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import java.util.ArrayList;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AutonomousModeStateMachineTest extends TestCase3 {
    private int autonomousModeInstanceCounter;
    private int procedureAge;
    private AutonomousMode selectedAutonomousMode;
    private ArrayList<Command> commands;
    private AutonomousModeStateMachine sm;

    @BeforeEach
    public void setUp() {
        autonomousModeInstanceCounter = 0;
        commands = new ArrayList<>();
        Supplier<AutonomousMode> supplier = () -> selectedAutonomousMode;
        sm = new AutonomousModeStateMachine(supplier);
    }

    private void selectNewAutonomousMode() {
        final int thisInstanceCount = ++autonomousModeInstanceCounter;
        selectedAutonomousMode =
                new AutonomousMode(
                        "Auton" + thisInstanceCount,
                        () -> {
                            assertEquals(thisInstanceCount, autonomousModeInstanceCounter);
                            procedureAge = 0;
                            var procedure =
                                    new RunCommand(() -> ++procedureAge).ignoringDisable(true);
                            commands.add(procedure);
                            return procedure;
                        });
    }

    /// Represents the code running when the user hasn't selected an autonomous mode via the web UI.
    @Test
    public void testOperationsWithNullAutonomousMode() {
        selectedAutonomousMode = null;

        sm.stopAutonomousMode("stopping");
        sm.reinitializeAutonomousMode("reinitializing");
        sm.startAutonomousMode();
        sm.stopAutonomousMode("stopping");
    }

    /// This represents the case where the robot code enters teleop without first running
    /// autonomous.
    /// Doesn't result in any action being taken, but we want to make sure it doesn't trigger a
    /// NullPointerException or any other bad side effect.
    @Test
    public void testStopWithoutStart() {
        selectNewAutonomousMode();

        sm.stopAutonomousMode("stopping");

        assertEquals(0, commands.size());
    }

    /// This represents the case where the RoboRIO reboots during autonomous mode, so the
    /// robot code directly enters autonomous without being disabled first.
    /// The robot should initialize and run an instance of the autonomous Command.
    @Test
    public void testStartWithoutInitialize() {
        selectNewAutonomousMode();

        sm.startAutonomousMode();

        assertEquals(1, commands.size());
        assertTrue(commands.get(0).isScheduled());
    }

    /// This represents the usual case where a robot sits in disabled mode for a while
    /// before autonomous is enabled.
    /// The robot should initialize the autonomous Command while in disabled, and then use
    /// that Command when autonomous starts.
    @Test
    public void testStartAfterInitialize() {
        selectNewAutonomousMode();

        sm.reinitializeAutonomousMode("initializing");
        sm.reinitializeAutonomousMode("initializing again");

        assertEquals(1, commands.size());
        assertFalse(commands.get(0).isScheduled());

        sm.startAutonomousMode();

        assertEquals(1, commands.size());
        assertTrue(commands.get(0).isScheduled());
    }

    /// Calling startAutonomousMode twice is representative of what would happen if the robot
    /// becomes momentarily disabled during autonomous, for example from a transient wifi issue.
    /// No new Command should be created in the second call - the one created during the first
    /// call to startAutonomousMode should be continued.
    @Test
    public void testResume() {
        selectNewAutonomousMode();

        sm.startAutonomousMode();

        assertEquals(1, commands.size());
        assertTrue(commands.get(0).isScheduled());

        sm.startAutonomousMode();

        assertEquals(1, commands.size());
        assertTrue(commands.get(0).isScheduled());
    }

    /// Test the usual case where the robot runs autonomous and then enters teleop.
    @Test
    public void testStop() {
        selectNewAutonomousMode();

        sm.startAutonomousMode();

        assertEquals(1, commands.size());
        assertTrue(commands.get(0).isScheduled());

        sm.stopAutonomousMode("stopping");

        assertEquals(1, commands.size());
        assertFalse(commands.get(0).isScheduled());

        sm.stopAutonomousMode("stopping again");

        assertEquals(1, commands.size());
        assertFalse(commands.get(0).isScheduled());
    }

    /// Autonomous -> Teleop -> Autonomous. This could happen frequently during testing
    /// in the shop, or after a field fault in competition. Should create a second instance
    /// of the autononomus Command for the second run of autonomous.
    @Test
    public void testRunTwice() {
        selectNewAutonomousMode();

        sm.startAutonomousMode();

        assertEquals(1, commands.size());
        assertTrue(commands.get(0).isScheduled());

        sm.stopAutonomousMode("stopping");

        assertEquals(1, commands.size());
        assertFalse(commands.get(0).isScheduled());

        sm.startAutonomousMode();

        assertEquals(2, commands.size());
        assertFalse(commands.get(0).isScheduled());
        assertTrue(commands.get(1).isScheduled());
    }

    /// Autonomous -> Teleop -> Disabled -> Autonomous
    @Test
    public void testRunTwiceWithReinitialize() {
        selectNewAutonomousMode();

        sm.startAutonomousMode();

        assertEquals(1, commands.size());
        assertTrue(commands.get(0).isScheduled());

        sm.stopAutonomousMode("stopping");

        assertEquals(1, commands.size());
        assertFalse(commands.get(0).isScheduled());

        sm.reinitializeAutonomousMode("reinitializing");

        assertEquals(2, commands.size());
        assertFalse(commands.get(0).isScheduled());
        assertFalse(commands.get(1).isScheduled());

        sm.startAutonomousMode();

        assertEquals(2, commands.size());
        assertFalse(commands.get(0).isScheduled());
        assertTrue(commands.get(1).isScheduled());
    }

    /// If the user selects a different autonomous mode while the robot is in disabled mode,
    /// an instance of the new mode's Command should be instantiated and then used when the robot
    /// enters autnonomous.
    @Test
    public void testNewSelectionWhenDisabled() {
        selectNewAutonomousMode();

        sm.reinitializeAutonomousMode("initializing");

        assertEquals(1, commands.size());
        assertFalse(commands.get(0).isScheduled());

        selectNewAutonomousMode();

        sm.reinitializeAutonomousMode("reinitializing");

        assertEquals(2, commands.size());
        assertFalse(commands.get(0).isScheduled());
        assertFalse(commands.get(1).isScheduled());

        sm.startAutonomousMode();

        assertEquals(2, commands.size());
        assertFalse(commands.get(0).isScheduled());
        assertTrue(commands.get(1).isScheduled());
    }

    /// If the user selects a different autonomous mode between two autonomous runs,
    /// an instance of the new mode's Command should be instantiated and then used when the robot
    /// enters autnonomous the second time.
    @Test
    public void testRunTwiceWithNewSelection() {
        selectNewAutonomousMode();

        sm.startAutonomousMode();

        assertEquals(1, commands.size());
        assertTrue(commands.get(0).isScheduled());

        sm.stopAutonomousMode("stopping");

        selectNewAutonomousMode();

        sm.startAutonomousMode();

        assertEquals(2, commands.size());
        assertFalse(commands.get(0).isScheduled());
        assertTrue(commands.get(1).isScheduled());
    }

    /// If the user removes their selection of an autonomous mode after it has been initialized,
    /// the robot should not run any Command when the robot enters autonomous.
    @Test
    public void testDeselectionWhenDisabled() {
        selectNewAutonomousMode();

        sm.reinitializeAutonomousMode("initializing");

        assertEquals(1, commands.size());
        assertFalse(commands.get(0).isScheduled());

        selectedAutonomousMode = null;

        sm.reinitializeAutonomousMode("reinitializing");

        assertEquals(1, commands.size());
        assertFalse(commands.get(0).isScheduled());

        sm.startAutonomousMode();

        assertEquals(1, commands.size());
        assertFalse(commands.get(0).isScheduled());
    }

    /// If the user removes their selection of an autonomous mode between two autonomous runs,
    /// the robot should not run any Command when the robot enters autonomous  the second time.
    @Test
    public void testRunTwiceWithDeselection() {
        selectNewAutonomousMode();

        sm.startAutonomousMode();

        assertEquals(1, commands.size());
        assertTrue(commands.get(0).isScheduled());

        sm.stopAutonomousMode("stopping");

        selectedAutonomousMode = null;

        sm.startAutonomousMode();

        assertEquals(1, commands.size());
        assertFalse(commands.get(0).isScheduled());
    }
}

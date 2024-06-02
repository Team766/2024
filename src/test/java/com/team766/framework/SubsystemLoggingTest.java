package com.team766.framework;

import static org.junit.jupiter.api.Assertions.*;

import com.team766.TestCase;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.littletonrobotics.junction.LogTable;

public class SubsystemLoggingTest extends TestCase {
    private static class MySystem extends RobotSystem<MySystem.Status, MySystem.Goal> {
        public record Status(double angle, Map<String, SwerveModuleState> modules) {}

        public sealed interface Goal {}

        public record TheGoal(List<Integer> ints) implements Goal {}

        @StateVariable
        public double value;

        @StateVariable
        public Pose2d pose;

        @StateVariable
        public double[] array;

        @Override
        protected Status updateState() {
            return new Status(
                    42.0, Map.of("left", new SwerveModuleState(4, Rotation2d.fromRadians(5))));
        }

        @Override
        protected void dispatch(Status status, Goal goal, boolean goalChanged) {}
    }

    @Test
    public void testLogging() {
        var table = new LogTable(0);
        {
            var writeSystem = new MySystem();
            writeSystem.value = 24.0;
            writeSystem.pose = new Pose2d(10, 20, Rotation2d.fromDegrees(30));
            writeSystem.array = new double[] {36.0, 48.0};
            writeSystem.setGoal(new MySystem.TheGoal(List.of(1, 2, 3, 4, 5)));
            writeSystem.periodic();
            writeSystem.toLog(table);
        }
        {
            var readSystem = new MySystem();
            readSystem.fromLog(table);
            assertEquals(24.0, readSystem.value);
            assertEquals(new Pose2d(10, 20, Rotation2d.fromDegrees(30)), readSystem.pose);
            assertArrayEquals(new double[] {36.0, 48.0}, readSystem.array);
            assertEquals(
                    new MySystem.Status(
                            42.0,
                            Map.of("left", new SwerveModuleState(4, Rotation2d.fromRadians(5)))),
                    readSystem.getStatus());
            assertEquals(new MySystem.TheGoal(List.of(1, 2, 3, 4, 5)), readSystem.getGoal());
        }
    }
}

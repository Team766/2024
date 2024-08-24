Package for robot-specific code.  Copy this example package for each robot,
eg reva, revb, burrobotprime, gatorade, etc.

Each package will contain the following:
* `Robot.java` - contains static references to that robot's mechanisms.
* `OI.java` - procedure for reading joystick input, controlling the robot via the Operator Interface.
* `AutonomousModes.java` - contains array of autonomous procedures for this robot.
* `mechanisms` - sub-package containing mechanisms for this robot.
* `procedures` - sub-package containing procedures for this robot.

Once you've created the appropriate package for this robot, remember to update
`com.team766.hal.GenericRobotMain` to reference this code.  This will typically involve
updating the following imports:

    import com.team766.robot.example.AutonomousModes;
    import com.team766.robot.example.OI;
    import com.team766.robot.example.Robot;

By organizing your code with a sub-package for each robot, with the exception of `GenericRobotMain`,
the code for each of our robots should be able to co-exist across branches.

NOTE: **You should only update `GenericRobotMain` on the branch that's specific to your robot.**

package com.team766.robot.reva.mechanisms;

import org.photonvision.targeting.PhotonTrackedTarget;
import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.robot.reva.Robot;
import edu.wpi.first.math.geometry.Transform3d;


public class ArmCamera extends Mechanism {
        private GrayScaleCamera camera;
        private int[] tagIds = {11,12,13,14,15,16};
        private double targetX = 0;
        private double targetZ = 0;
        private PIDController xPID = new PIDController(0,0,0,0,0,0);
        private PIDController zPID = new PIDController(0,0,0,0,0,0);

        public ArmCamera() throws AprilTagGeneralCheckedException {
            try{
                camera = new GrayScaleCamera("Arm_Camera_2024");
            } catch (Exception e) {
                log("Unable to create GrayScaleCamera for Arm Camera");
            }
            xPID.setSetpoint(targetX);
            zPID.setSetpoint(targetZ);
        }

        public GrayScaleCamera getCamera() {
            return camera;
        }

        public void shootTrap(){
            checkContextOwnership();
            PhotonTrackedTarget target = null;
            int tagIdUsing = 0;
            for (int tagId: tagIds){
                try{
                    target = camera.getTrackedTargetWithID(tagId);
                    tagIdUsing = tagId;
                    break;
                } catch (Exception e){
                    log("Unable to get target with ID: " + tagId);
                    if (tagId == tagIds[tagIds.length - 1]){
                        Robot.lights.signalTrapNotWorking();
                        return;
                    }
                }
            }

            Transform3d toUse = GrayScaleCamera.getBestTargetTransform3d(target);

            double currentX = toUse.getTranslation().getX();
            double currentZ = toUse.getTranslation().getZ();


            xPID.calculate(currentX);
            zPID.calculate(currentZ);

            while (Math.abs(xPID.getOutput()) + Math.abs(zPID.getOutput()) > 0){
                try{
                    toUse = GrayScaleCamera.getBestTargetTransform3d(camera.getTrackedTargetWithID(tagIdUsing));
                } catch (Exception e){
                    log("Lost tag!");
                    Robot.lights.signalTrapNotWorking();
                    return;
                }
                currentX = toUse.getTranslation().getX();
                currentZ = toUse.getTranslation().getZ();

                xPID.calculate(currentX);
                zPID.calculate(currentZ);

                Robot.drive.controlRobotOriented(currentX, currentZ, 0);
                Robot.lights.signalTrapRunning();
            }
            Robot.lights.signalTrapFinished();

        }




    
}

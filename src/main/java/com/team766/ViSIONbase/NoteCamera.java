package com.team766.ViSIONbase;

import com.team766.framework.AprilTagGeneralCheckedException;
import edu.wpi.first.math.geometry.Transform3d;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonTrackedTarget;

public class NoteCamera extends PhotonCamera {

    public NoteCamera(String cameraName) {
        super(cameraName);
    }

    /*
     * Gets the best notes picked up by the camera. The PTT should be kept as a PhotonTrackedTarget for use.
     * The camera using this needs to be set in 2D mode.
     * @throws AprilTagGeneralCheckedException if there are no notes avalible for the camera to see
     * @author Max Spier, 1/20/2024
     */
    public PhotonTrackedTarget getRing() throws AprilTagGeneralCheckedException {
        var result = getLatestResult(); // getting the result from the camera
        boolean hasTargets =
                result.hasTargets(); // checking to see if there are any targets in the camera's
        // view. IF THERE ISN'T AND YOU USE result.getTargets() YOU
        // WILL GET AN ERROR

        if (hasTargets) {
            // List<PhotonTrackedTarget> targets = result.getTargets(); // getting targets

            PhotonTrackedTarget bestTrackedTarget =
                    result.getBestTarget(); // getting the best target that is currently being
            // picked up by the camera so that it can know where
            // it is
            return bestTrackedTarget;
        } else {

            throw new AprilTagGeneralCheckedException(
                    "There were no targets that could be picked up by the camera, so I'm gonna have to throw this error here.");
        }
    }

    public Transform3d getTransform3dOfRing() throws AprilTagGeneralCheckedException {
        PhotonTrackedTarget e = getRing();

        return e.getBestCameraToTarget();
    }

    // To be renamed once we know if yaw is X or Y
    public double getYawOfRing() throws AprilTagGeneralCheckedException {

        try {
            return getRing().getYaw();
        } catch (AprilTagGeneralCheckedException e) {
            throw new AprilTagGeneralCheckedException("No notes detected by the camera");
        }
    }

    public double getPitchOfRing() throws AprilTagGeneralCheckedException {

        try {
            return getRing().getPitch();
        } catch (AprilTagGeneralCheckedException e) {
            throw new AprilTagGeneralCheckedException("No notes detected by the camera");
        }
    }
}
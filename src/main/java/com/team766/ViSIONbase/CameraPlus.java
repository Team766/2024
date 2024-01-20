package com.team766.ViSIONbase;

import com.team766.framework.AprilTagGeneralCheckedException;
import edu.wpi.first.math.geometry.Transform3d;
import java.util.*;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonTrackedTarget;

public class CameraPlus extends PhotonCamera {

    /**
     * This is the constructor for the CameraPlus class
     * @param cameraName the name of the camera
     * @extends the PhotonCamera class and supers the cameraName
     */
    public CameraPlus(String cameraName) {
        super(cameraName);
    }

    /**
     * Here we can get a transform 3d of the best apriltag viewable.
     * @return the Transform3d object of the robot relative to the target
     * @throws ApriltagGeneralCheckedException the checked exception from before if there were any errors in the getter methods
     */
    public Transform3d getRobotToBestTag() throws AprilTagGeneralCheckedException {
        try {
            return getBestTargetTransform3d(getBestTrackedTarget());
        } catch (AprilTagGeneralCheckedException e) {
            throw e;
        }
    }

    /**
     * This method gets the tag ID of the best target picked up by the camera
     * This is useful for knowing which tag the Transform3d is looking at
     * @return the tag ID of the best tracked target
     * @throws AprilTagGeneralCheckedException the checked exception from before if there were any errors in the getter methods
     */
    public int getTagIdOfBestTarget() throws AprilTagGeneralCheckedException {
        try {
            return getBestTrackedTarget().getFiducialId();
        } catch (AprilTagGeneralCheckedException e) {
            throw e;
        }
    }

    /**
     * This method gets the best tracked target picked up by the camera
     * @return PhotonTrackedTarget the best tracked target picked up by the camera
     */
    public PhotonTrackedTarget getBestTrackedTarget() throws AprilTagGeneralCheckedException {
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

    /**
     * This method gets the transform 3d of the best target picked up by the camera
     * @param target the target that you want to get the transform 3d of
     * @return Transform3d the transform 3d of the target
     */
    public Transform3d getBestTargetTransform3d(PhotonTrackedTarget target) {
        return target.getBestCameraToTarget();
    }
}

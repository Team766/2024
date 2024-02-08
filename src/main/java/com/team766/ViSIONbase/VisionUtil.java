package com.team766.ViSIONbase;

import com.team766.framework.AprilTagGeneralCheckedException;

public class VisionUtil {
    /*
     * This method will find the camera that has a tag in it with the given tagId.
     *
     * @param tagId the tagId of the tag to find
     * @return CameraPlus that has the tag with the given tagId
     * @throws AprilTagGeneralCheckedException if no cameras have the tag with the given tagId
     * @author Max Spier, 1/7/2024
     */
    public static CameraPlus findApriltagCameraThatHas(int tagId)
            throws AprilTagGeneralCheckedException {

        for (CameraPlus camera : StaticCameras.apriltagCameras) {

            if (camera.getTagIdOfBestTarget() == tagId) {
                return camera;
            }
        }

        throw new AprilTagGeneralCheckedException("No cameras had the camera with tagId: " + tagId);
    }
}

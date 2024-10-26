package com.team766.orin;

import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import java.util.ArrayList;

public class GetApriltagPoseData {

    public static ArrayList<AprilTag> getAllTags() {
        ArrayList<AprilTag> apriltags = new ArrayList<AprilTag>();

        int[] tagIds;
        double[] tagData;

        try {
            tagData = GetOrinRawValue.getRawPoseData();
        } catch (ValueNotFoundOnTableError e) {
            return apriltags; // Can just return an array of zero apriltags here
        }

        if (tagData.length % 4 != 0 || tagData.length == 0) return apriltags;

        for (int i = 0; i < tagData.length; i += 4) {
            AprilTag tag =
                    new AprilTag(
                            (int) tagData[i],
                            new Pose3d(
                                    new Translation3d(
                                            tagData[i + 1], tagData[i + 2], tagData[i + 3]),
                                    new Rotation3d()));
            apriltags.add(tag);
        }
        return apriltags;
    }
}

package com.team766.ViSIONbase;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class GoPoint extends Pose2d{
	
	private int tagID;

	/*
	 * This class represents a point that a robot should go to in reference of an AprilTag of representative tagID.
	 * @param tagID the tagID of the tag to look for
	 * @param X the X position relative to the tag that the robot should go to
	 * @param Y the Y position relative to the tag that the robot should go to
	 */
	public GoPoint(int tagID, double X, double Y){
		super(X, Y, new Rotation2d());
		this.tagID = tagID;
	}

	public int getTagID(){ return tagID; }

}

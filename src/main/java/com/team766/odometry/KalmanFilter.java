package com.team766.odometry;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.numbers.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.apache.commons.math3.stat.correlation.Covariance;
import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.Matrix;
import org.apache.commons.math3.linear.RealMatrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.Num;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;

public class KalmanFilter {
    private Translation2d curPos;
    private Matrix<N2, N2> curCovariance; 
    private Matrix<N2, N2> odometryCovariancePerDist;
    private Matrix<N2, N2> measurementCovariance;
    
    public KalmanFilter(Translation2d curPos, Matrix<N2, N2> covariance) {
        this.curPos = curPos;
        this.curCovariance = covariance;
    }

    public KalmanFilter() {
        curPos = new Translation2d(0, 0);
        curCovariance = MatBuilder.fill(Nat.N2(), Nat.N2(), 1, 0, 0, 1);
        odometryCovariancePerDist = MatBuilder.fill(Nat.N2(), Nat.N2(), 0.2, 0, 0, 0.05);
        measurementCovariance = MatBuilder.fill(Nat.N2(), Nat.N2(), 0.010, 0, 0, 0.010);
    }

    public void predictPeriodic(Translation2d odometryInput) {
        curPos = curPos.plus(odometryInput);

        double angleRad = odometryInput.getAngle().getRadians();
        Matrix<N2, N2> track = MatBuilder.fill(Nat.N2(), Nat.N2(), Math.cos(angleRad), -Math.sin(angleRad), Math.sin(angleRad), Math.cos(angleRad));
        curCovariance = track.times(odometryInput.getNorm()).times(odometryCovariancePerDist.times(track.transpose())).plus(curCovariance);
        SmartDashboard.putString("cur covariance", curCovariance.toString());
    }

    public void updateWithMeasurement(Translation2d measurement) {
        Matrix<N2, N2> kalmanGain = curCovariance.times(curCovariance.plus(measurementCovariance).inv());
        SmartDashboard.putString("Kalman Gain", kalmanGain.toString());

        curPos = new Translation2d(new Vector<N2>(kalmanGain.times(measurement.toVector().minus(curPos.toVector())).plus(curPos.toVector())));
        curCovariance = Matrix.eye(Nat.N2()).minus(kalmanGain).times(curCovariance);
    }

    public Translation2d getPos() {
        return curPos;
    }

    public void setPos(Translation2d pos) {
        curPos = pos;
    }
}

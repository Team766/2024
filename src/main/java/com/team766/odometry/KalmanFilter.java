package com.team766.odometry;

import java.util.TreeMap;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.numbers.N4;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class KalmanFilter {
    private Matrix<N4, N1> curState;
    private Matrix<N4, N4> curCovariance; 
    private Matrix<N4, N4> noiseCovariance;
    private Matrix<N2, N2> odometryCovariancePerDist;
    private Matrix<N2, N2> visionCovariance;
    private TreeMap<Double, Translation2d> inputLog; 

    private Matrix<N2, N4> observationMatrix = MatBuilder.fill(Nat.N2(), Nat.N4(), 
1, 0, 0, 0,
        0, 1, 0, 0);

    private final double VELOCITY_INPUT_DELETION_TIME = 1;
    
    public KalmanFilter(Matrix<N4, N1> curState, Matrix<N4, N4> covariance, Matrix<N2, N2> odometryCovariancePerDist, Matrix<N2, N2> visionCovariance) {
        this.curState = curState;
        this.curCovariance = covariance;
        this.odometryCovariancePerDist = odometryCovariancePerDist;
        this.visionCovariance = visionCovariance;
        noiseCovariance = MatBuilder.fill(Nat.N4(), Nat.N4(), 
    0.0003, 0, 0, 0,
            0, 0.0003, 0, 0,
            0, 0, 0.0001, 0,
            0, 0, 0, 0.0001);
        inputLog = new TreeMap<>();
    }

    public KalmanFilter(Matrix<N4, N1> curState, Matrix<N4, N4> covariance, Matrix<N2, N2> visionCovariance) {
        this(curState, 
            covariance, 
            MatBuilder.fill(Nat.N2(), Nat.N2(), 0.2, 0, 0, 0.05), 
            visionCovariance);
    }

    public KalmanFilter(Matrix<N4, N1> curState, Matrix<N4, N4> covariance) {
        this(curState, 
            covariance, 
            MatBuilder.fill(Nat.N2(), Nat.N2(), 0.05, 0, 0, 0.05));
    }

    public KalmanFilter() {
        this(MatBuilder.fill(Nat.N4(), Nat.N1(), 0, 0, 0, 0), Matrix.eye(Nat.N4()));
    }

    public void addVelocityInput(Translation2d velocityInput, double time) {
        inputLog.put(time, velocityInput);
        predictCurrentState(inputLog.lowerKey(time));

        if(time - inputLog.firstKey() > VELOCITY_INPUT_DELETION_TIME) {
            inputLog.remove(inputLog.firstKey()); // delete old velocityInput values
        } 
        SmartDashboard.putNumber("Cur input x velocity", velocityInput.getX());
        SmartDashboard.putNumber("Cur State x velocity", curState.get(2, 0));
        SmartDashboard.putNumber("Number of entries inputLog", inputLog.size());
        Logger.get(Category.ODOMETRY).logRaw(Severity.INFO, "pos cov: " + getCovariance().toString());
        SmartDashboard.putString("Pos Covariance", "time: " + time + ", gain: " + getCovariance().toString());
        SmartDashboard.putString("Full Covariance", "time: " + time + ", gain: " + curCovariance);
    }

    private void predict(double time, double nextStepTime, double dt) {
        Translation2d velocityChange; 
        if (inputLog.containsKey(time)) {
            velocityChange = inputLog.get(nextStepTime).minus(inputLog.get(time)); // scalar multiplied to account for decreased velocity change if input targetTime is between two input entries
        } else {
            velocityChange = inputLog.get(nextStepTime).minus(getVelocity()).times(dt/(nextStepTime - time)); 
        }
        
        Matrix<N4, N4> transition = MatBuilder.fill(Nat.N4(), Nat.N4(), 
            1, 0, dt, 0,
                    0, 1, 0, dt,
                    0, 0, 1, 0,
                    0, 0, 0, 1);
                
        Matrix<N4, N1> input = MatBuilder.fill(Nat.N4(), Nat.N1(), 
            0, 
                    0, 
                    velocityChange.getX(), 
                    velocityChange.getY());

        curState = transition.times(curState).plus(input);
        curCovariance = transition.times(curCovariance.times(transition.transpose())).plus(noiseCovariance);
    }

    /**
     * changes curState and curCovariance to what it was at targetTime through backcalculation
     * @param targetTime in seconds
     */
    public void findPrevState(double targetTime) {
        double time = inputLog.lastKey();
        double prevTime;
        double dt; 

        while (time > targetTime) {
            try {
                prevTime = inputLog.lowerKey(time);
                dt = Math.max(prevTime, targetTime) - time; // will be negative

                predict(time, prevTime, dt);

                // curState = transition.inv().times(curState.minus(input)); // can also do using inverse
                // curCovariance = transition.inv().times(curCovariance.minus(noiseCovariance).times(transition.transpose().inv()));

                time += dt;
            } catch (Exception e) {
                Logger.get(Category.JAVA_EXCEPTION).logRaw(Severity.ERROR, "kalman filter inputTimes too short");
                break;
            } 
        }
    }

    /**
     * predicts the current state based on the input time of a previous state
     * @param initialTime in seconds
     */
    public void predictCurrentState(double initialTime) {
        double time = initialTime;
        // try {
        //     time = inputLog.ceilingKey(initialTime);
        // } catch (Exception e) {
        //     Logger.get(Category.JAVA_EXCEPTION).logRaw(Severity.WARNING, "no ceiling key");
        //     time = inputLog.floorKey(initialTime); // for updates, the time is sometimes slightly after the most recent velocity is input
        // }
        
        double currentTime = inputLog.lastKey();
        double nextTime;
        double dt; 
        double counter = 0;

        while (time < currentTime) {
            try {
                nextTime = inputLog.higherKey(time);
                
                dt = nextTime - time; // going forward, the target time (currentTime) will always be a key exactly since it is defined that way

                predict(time, nextTime, dt);

                time += dt;
                counter++;
            } catch (Exception e) {
                e.printStackTrace();
                Logger.get(Category.JAVA_EXCEPTION).logRaw(Severity.ERROR, "no higher key, counter: " + counter + ", execption: " + e.toString());
                SmartDashboard.putString("predictCurrentState error", e.toString());
                break;
            }
        }
        SmartDashboard.putNumber("predictCurrentState iterations", counter);
    }

    /**
     * 
     * @param measurement
     * @param measurementCovariance
     * @param time in seconds
     */
    public void updateWithPositionMeasurement(Translation2d measurement, Matrix<N2, N2> measurementCovariance, double time) {

        findPrevState(time);
        SmartDashboard.putNumber("prev X value", getPos().getX());
        SmartDashboard.putNumber("Prev state x velocity", curState.get(2, 0));
        SmartDashboard.putString("prev covariance", getCovariance().toString());
        Matrix<N4, N2> kalmanGain = curCovariance.times(observationMatrix.transpose().times(
            observationMatrix.times(curCovariance.times(observationMatrix.transpose())).plus(measurementCovariance).inv()));
        // Matrix<N2, N2> posKalmanGain = getCovariance().times(getCovariance().plus(measurementCovariance).inv());
        SmartDashboard.putString("Kalman Gain", "time: " + time + ", gain: " + kalmanGain.toString());

        // curState.assignBlock(0, 0, posKalmanGain.times(measurement.toVector().minus(getPos().toVector())).plus(getPos().toVector())); // updates values for ONLY position
        // curCovariance.assignBlock(0, 0, Matrix.eye(Nat.N2()).minus(posKalmanGain).times(getCovariance())); // updates values for ONLY position

        curState = kalmanGain.times(measurement.toVector().minus(observationMatrix.times(curState))).plus(curState);
        curCovariance = Matrix.eye(Nat.N4()).minus(kalmanGain.times(observationMatrix)).times(curCovariance.times(Matrix.eye(Nat.N4()).minus(kalmanGain.times(observationMatrix)).transpose())).plus(
            kalmanGain.times(measurementCovariance.times(kalmanGain.transpose())));
        SmartDashboard.putNumber("Updated prev state x velocity", curState.get(2, 0));

        predictCurrentState(time);
        SmartDashboard.putNumber("Predicted Cur State x velocity", curState.get(2, 0));
    }

    /**
     * 
     * @param measurement
     * @param time in seconds
     */
    public void updateWithVisionMeasurement(Translation2d measurement, double time) {
        updateWithPositionMeasurement(measurement, visionCovariance, time);
        // Logger.get(Category.ODOMETRY).logRaw(Severity.INFO, "cov: " + getCovariance().toString());
        // SmartDashboard.putString("Covariance", "time: " + time + ", gain: " + getCovariance().toString());
    }

    /**
     * 
     * @param odometryInput
     * @param initialTime in seconds
     * @param finalTime in seconds
     */
    public void updateWithOdometry(Translation2d odometryInput, double initialTime, double finalTime) {
        
        findPrevState(initialTime);
        Translation2d curPos = getPos().plus(odometryInput);
        predictCurrentState(initialTime);

        // Logger.get(Category.ODOMETRY).logRaw(Severity.INFO, odometryInput.toString());
        // Logger.get(Category.ODOMETRY).logRaw(Severity.INFO, curPos.toString());

        double angleRad = odometryInput.getAngle().getRadians();
        Matrix<N2, N2> track = MatBuilder.fill(Nat.N2(), Nat.N2(), Math.cos(angleRad), -Math.sin(angleRad), Math.sin(angleRad), Math.cos(angleRad));
        Matrix<N2, N2> odomCovariance = track.times(odometryInput.getNorm()).times(odometryCovariancePerDist.times(track.transpose())).plus(getCovariance());

        // Logger.get(Category.ODOMETRY).logRaw(Severity.INFO, "odom: " + odomCovariance.toString());
        Logger.get(Category.ODOMETRY).logRaw(Severity.INFO, "cov: " + getCovariance().toString());

        updateWithPositionMeasurement(curPos, odomCovariance, finalTime);
    }

    public Translation2d getPos() {
        return new Translation2d(new Vector<N2>(curState.block(2, 1, 0, 0)));
    }

    public Translation2d getVelocity() {
        return new Translation2d(new Vector<N2>(curState.block(2, 1, 2, 0)));
    }

    public Matrix<N2, N2> getCovariance() {
        return curCovariance.block(2, 2, 0, 0);
    }

    public void setPos(Translation2d pos) {
        curState.assignBlock(0, 0, pos.toVector());
    }
}

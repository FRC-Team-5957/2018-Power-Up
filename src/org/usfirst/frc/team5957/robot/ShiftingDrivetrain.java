package org.usfirst.frc.team5957.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class ShiftingDrivetrain {

	private VictorSP frontLeft, rearLeft, frontRight, rearRight;
	private DifferentialDrive drive;
	private Solenoid gear;
	private ADXRS450_Gyro gyro;
	private final boolean highGear = true;
	private final boolean lowGear = false;
	private double maxSpeed = 0.6;
	private double maxRotation = 1;
	private boolean rampingEnabled = false;
	private ArrayList<Double> speed;
	private ArrayList<Double> rotation;
	private int rampSize;
	// private PIDController PIDControl;
	// private PIDOutput output;

	public ShiftingDrivetrain(int frontLeft, int rearLeft, int frontRight, int rearRight, int PCM, int gear) {
		this.frontLeft = new VictorSP(frontLeft);
		this.rearLeft = new VictorSP(rearLeft);
		this.frontRight = new VictorSP(frontRight);
		this.rearRight = new VictorSP(rearRight);
		this.drive = new DifferentialDrive(new SpeedControllerGroup(this.frontLeft, this.rearLeft),
				new SpeedControllerGroup(this.frontRight, this.rearRight));
		this.gear = new Solenoid(PCM, gear);
		this.gear.set(lowGear);
		this.gyro = new ADXRS450_Gyro();
		this.gyro.reset();
		this.gyro.calibrate();
		/*
		 * We can figure this out later, probably will have to do it manually for drive
		 * but ill talk to Preston - Andrei
		 */
		// this.PIDControl = new PIDController(0.1, 0.001, 0.0, this.gyro, output);
		// this.PIDControl.enable();
		// this.PIDControl.setOutputRange(-0.6, 0.6);

	}

	public void drive(double speedVal, double rotationVal) {
		if (rampingEnabled) {
			updateRamps(speedVal, rotationVal);
			drive.arcadeDrive(maxSpeed * getRamped(speed), maxRotation * getRamped(rotation));
		} else {
			drive.arcadeDrive(maxSpeed * speedVal, maxRotation * rotationVal);
		}
	}

	public void deadStop() {
		if (rampingEnabled) {
			resetRamp();
			drive(0, 0);
		} else {
			drive(0, 0);
		}
	}

	public void disableRamping() {
		rampingEnabled = false;
		resetRamp();
	}

	public void enableRamping(double seconds) {
		rampingEnabled = true;
		rampSize = (int) seconds * 50;
		speed = new ArrayList<Double>(rampSize);
		rotation = new ArrayList<Double>(rampSize);
		resetRamp();
	}

	private double getRamped(ArrayList<Double> a) {
		double total = 0;
		for (int i = 0; i < a.size(); i++) {
			total += a.get(i);
		}
		return total / rampSize;
	}

	private void updateRamps(double speedVal, double rotationVal) {
		speed.remove(0);
		speed.add(speedVal);
		rotation.remove(0);
		rotation.add(rotationVal);
	}

	public void resetRamp() {
		for (int i = 0; i < rampSize; i++) {
			speed.add(0d);
			rotation.add(0d);
		}
	}

	public void setMaxSpeed(double speed) {
		maxSpeed = speed;
	}

	public void setMaxRotation(double rotation) {
		maxRotation = rotation;
	}

	public void setHighGear() {
		gear.set(highGear);
	}

	public void setLowGear() {
		gear.set(lowGear);
	}

	public void reset() {
		gyro.reset();
		gyro.calibrate();
		resetRamp();
	}

	// public double getPIDSource() {
	// return this.gyro.getAngle();
	// }
	//
	// public double getPIDOutput() {
	// return PIDControl.get();
	// }
	//
	// public void setPIDInput(double angle) {
	// PIDControl.setSetpoint(angle);
	// }

}

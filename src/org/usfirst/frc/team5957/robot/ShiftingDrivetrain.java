package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class ShiftingDrivetrain {

	private VictorSP frontLeft, rearLeft, frontRight, rearRight;
	private DifferentialDrive drive;
	private DoubleSolenoid gear;
	private ADXRS450_Gyro gyro;
	private double maxSpeed = 0.6;
	private double maxRotation = 1;
	// private PIDController PIDControl;
	// private PIDOutput output;

	public ShiftingDrivetrain(int frontLeft, int rearLeft, int frontRight, int rearRight, int PCM, int D1, int D2) {
		this.frontLeft = new VictorSP(frontLeft);
		this.rearLeft = new VictorSP(rearLeft);
		this.frontRight = new VictorSP(frontRight);
		this.rearRight = new VictorSP(rearRight);
		this.drive = new DifferentialDrive(new SpeedControllerGroup(this.frontLeft, this.rearLeft),
				new SpeedControllerGroup(this.frontRight, this.rearRight));
		this.gear = new DoubleSolenoid(PCM, D1, D2);
		this.gear.set(DoubleSolenoid.Value.kReverse);
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
		drive.arcadeDrive(maxSpeed * getAdjusted(speedVal), maxRotation * getAdjusted(rotationVal));
	}

	private double getAdjusted(double speed) {
		return speed * Math.pow(Math.abs(speed), 2);
	}

	public void deadStop() {
		drive(0, 0);
	}

	public void setMaxSpeed(double speed) {
		maxSpeed = speed;
	}

	public void setMaxRotation(double rotation) {
		maxRotation = rotation;
	}

	public void setHighGear() {
		gear.set(DoubleSolenoid.Value.kForward);
	}

	public void setLowGear() {
		gear.set(DoubleSolenoid.Value.kReverse);
	}

	public void reset() {
		gyro.reset();
		gyro.calibrate();
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
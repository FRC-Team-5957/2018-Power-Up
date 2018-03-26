package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class ShiftingDrivetrain {

	private VictorSP frontLeft, rearLeft, frontRight, rearRight;
	private DifferentialDrive drive;
	private DoubleSolenoid gear;
	private double maxSpeed = 1;
	private double maxRotation = 1;

	public ShiftingDrivetrain(int frontLeft, int rearLeft, int frontRight, int rearRight, int PCM, int D1, int D2) {
		this.frontLeft = new VictorSP(frontLeft);
		this.rearLeft = new VictorSP(rearLeft);
		this.frontRight = new VictorSP(frontRight);
		this.rearRight = new VictorSP(rearRight);
		this.drive = new DifferentialDrive(new SpeedControllerGroup(this.frontLeft, this.rearLeft),
				new SpeedControllerGroup(this.frontRight, this.rearRight));
		this.drive.setSafetyEnabled(false);
		this.gear = new DoubleSolenoid(PCM, D1, D2);

	}

	public void drive(double speedVal, double rotationVal) {
		drive.arcadeDrive(maxSpeed * speedVal, maxRotation * rotationVal, true);
	}

	public void deadStop() {
		drive.stopMotor();
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

}
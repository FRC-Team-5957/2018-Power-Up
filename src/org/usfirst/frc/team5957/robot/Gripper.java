package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.VictorSP;

public class Gripper {
	private VictorSP leftSpinny, rightSpinny;
	private DoubleSolenoid gripper;
	private static final DoubleSolenoid.Value closed = DoubleSolenoid.Value.kForward;
	private static final DoubleSolenoid.Value open = DoubleSolenoid.Value.kReverse;

	private static final double intakeSpeed = -1;
	private static final double stallSpeed = -0.15;
	private static final double dropSpeed = 0.2;
	private static final double ejectSpeed = 1;

	public Gripper(int leftMotor, int rightMotor, int PCM, int DS1, int DS2) {
		leftSpinny = new VictorSP(leftMotor);
		rightSpinny = new VictorSP(rightMotor);
		rightSpinny.setInverted(true);
		gripper = new DoubleSolenoid(PCM, DS1, DS2);
		gripper.set(closed);
	}

	public void intake() {
		setSpinny(intakeSpeed);
	}

	public void eject() {
		setSpinny(ejectSpeed);
	}

	public void drop() {
		open();
		setSpinny(dropSpeed);
	}

	public void stall() {
		setSpinny(stallSpeed);
	}

	public void stop() {
		setSpinny(0);
	}

	public void open() {
		gripper.set(open);
	}

	public void close() {
		gripper.set(closed);
	}

	public void switchState() {
		if (gripper.get() == open) {
			close();
		} else if (gripper.get() == closed) {
			open();
		}
	}

	private void setSpinny(double speed) {
		leftSpinny.set(speed);
		rightSpinny.set(speed);
	}
}

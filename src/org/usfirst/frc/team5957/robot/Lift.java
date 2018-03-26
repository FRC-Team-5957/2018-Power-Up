package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.Spark;

public class Lift {
	private Spark lift;
	private static final double climbSpeed = 0.9;
	private static final double descendSpeed = -0.3;
	private static final double stallSpeed = 0.22;

	public Lift(int liftMotor) {
		lift = new Spark(liftMotor);
	}

	public void climb() {
		lift.set(climbSpeed);
	}

	public void descend() {
		lift.set(descendSpeed);
	}

	public void stall() {
		lift.set(stallSpeed);
	}
}
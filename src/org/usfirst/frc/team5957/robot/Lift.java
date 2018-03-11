package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.Spark;

public class Lift {
	private Spark lift;
	// private Encoder encoder;
	private static final double climbSpeed = 0.7;
	private static final double descendSpeed = -0.4;
	private static final double stallSpeed = 0.2;

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

	// TODO implement PID and scale/switch positions
}
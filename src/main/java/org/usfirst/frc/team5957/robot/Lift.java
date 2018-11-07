package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.Spark;

public class Lift {
	private Spark lift;
	// private Encoder encoder;
	private static final double climbSpeed = -0.5;
	private static final double descendSpeed = 1;
	private static final double stallSpeed = 0.22;

	public Lift(int liftMotor) {
		lift = new Spark(liftMotor);
	}

	public void climb(double speed) {
		lift.set(speed * climbSpeed);
	}

	public void descend(double speed) {
		lift.set(Math.abs(speed) * descendSpeed);
	}

	public void stall() {
		lift.set(stallSpeed);
	}

	// TODO implement PID and scale/switch positions
}
package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;

public class Lift {
	private VictorSP lift;
	private DigitalInput topLimit, lowLimit;
	// private Encoder encoder;
	private static final double climbSpeed = 0.8;
	private static final double descendSpeed = -0.6;
	private static final double stallSpeed = 0.2;

	public Lift(int liftMotor, int lowChannel, int topChannel) {
		lift = new VictorSP(liftMotor);
		topLimit = new DigitalInput(topChannel);
		lowLimit = new DigitalInput(lowChannel);
	}

	public void climb() {
		if (!topLimit.get()) {
			lift.set(climbSpeed);
		} else {
			stall();
		}
	}

	public void descend() {
		if (!lowLimit.get()) {
			lift.set(descendSpeed);
		} else {
			stall();
		}
	}

	public void stall() {
		lift.set(stallSpeed);
	}

	// TODO implement PID and scale/switch positions
}

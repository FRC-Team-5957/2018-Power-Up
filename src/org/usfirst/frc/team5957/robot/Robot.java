package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	// Subsystems
	final int PCM = 1;
	ShiftingDrivetrain drive = new ShiftingDrivetrain(0, 1, 2, 3, PCM, 1, 2);
	Gripper gripper = new Gripper(4, 5, PCM, 0);
	Lift lift = new Lift(6);

	// Random variables
	char startPosition;
	String gameData;
	Timer time = new Timer();

	// Sensors and compressor
	Encoder leftEnc, rightEnc;
	DigitalInput leftAuto, rightAuto;
	Compressor compressor;
	final int sideSelectorA = 0;
	final int sideSelectorB = 1;
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();

	Joystick driver = new Joystick(0);
	Joystick operator = new Joystick(1);

	double limitedSpeed = 0;
	double limit = 1 / 15;
	boolean rampingEnabled = false;

	@Override
	public void robotInit() {
		// Drivetrain
		drive.setMaxSpeed(0.87);
		drive.setMaxRotation(0.77);
		gyro.reset();
		gyro.calibrate();

		// Sensors and subsystems
		leftEnc = new Encoder(3, 2, false);
		rightEnc = new Encoder(4, 5, false);
		leftAuto = new DigitalInput(1); // LEft
		rightAuto = new DigitalInput(0); // Right
		compressor = new Compressor(1);
		compressor.setClosedLoopControl(true);
		CameraServer.getInstance().startAutomaticCapture(0);
		CameraServer.getInstance().startAutomaticCapture(1);
	}

	@Override
	public void robotPeriodic() {
		SmartDashboard.putNumber("Left Encoder Distance", leftEnc.getDistance());
		SmartDashboard.putNumber("RightEncoder Distance", rightEnc.getDistance());
		SmartDashboard.putNumber("Left Encoder Get", leftEnc.get());
		SmartDashboard.putNumber("RightEncoder Get", rightEnc.get());
	}

	@Override
	public void autonomousInit() {
		time.reset();
		time.start();
		drive.setMaxSpeed(0.7);
		leftEnc.reset();
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		startPosition = getStartPosition();
		if (startPosition == 'C') {
			// Middle switch auto
			// go forward, turn based on side, go forward, turn to 0 and lift, go forward,
			// spit

			// VAPOR: go forward and raise lift for scale
			while (time.get() < 2) {
				drive.drive(1, -gyro.getAngle() * 0.1);
				if (time.get() < 1.95) {
					lift.climb();
				} else {
					lift.stall();
				}
			}

			// Center switch attempt time
			// while (time.get() < 1.5) {
			// drive.drive(1, -gyro.getAngle() * 0.1);
			// }
			// System.out.println("Went forward");
			// while (time.get() > 1.5 && time.get() < 1.7) {
			// drive.drive(-1, 0);
			// Timer.delay(0.02);
			// }
			// System.out.println("Braked");
			//
			// double target = 90;
			// int count = 0;
			// while (count < 25) {
			// double error = target - gyro.getAngle();
			// System.out.println(gyro.getAngle());
			// if (error < 2) {
			// count++;
			// }
			// drive.drive(0, target * 0.05);
			// }
			// System.out.println("done turning");
			//
		} else {
			// go forward, if i have it, spit the cube
			while (time.get() < 2) {
				if (time.get() == 0.2) {
					drive.setLowGear();
				}
				drive.drive(1, -gyro.getAngle() * 0.1);
				if (time.get() > 0.5)
					lift.climb();
			}
			lift.stall();
			if (startPosition == gameData.charAt(0)) {
				gripper.eject();
				System.out.println("Ejecting");
				Timer.delay(0.5);
			}

		}

		// if (startPosition == gameData.charAt(0)) {
		// while (leftEnc.getDistance() <= 5000) {
		//
		// // WHEN YOURE TOO LAZY TO USE BOTH THE $50 ENCODERS SO YOU JUST USE ONE
		// // @BEN..........
		// // TODO add some way to match encoder side values (possibly unnecessary) (can
		// do
		// // this with master/slave pairs on the victors but we will have to implement
		// // our own)
		//
		// // drive.drive(1, 0); // change this to
		// // }
		// // gripper.drop();
		// // } else if (startPosition != gameData.charAt(0)) {
		// // while (leftEnc.getDistance() <= 5000) {
		// // drive.drive(1, 0);
		// // }
		// // }
		//
		// /* NOT WRONG BUT EASIER TO DRIVE AND THEN CHECK - Andrei */
		// // like this
		// double timeToGetToSwitch = 3; // a guess (adjust until we reach the switch
		// but dont crash into it
		// double timeLift = 2; // another guess
		// while (time.get() < timeToGetToSwitch) {
		// drive.drive(1, 0); // can add correction and encoder usage later
		// // moves forward until the switch and raises lift at the same time
		// gripper.stall(); // added to make sure we dont drop the cube by accident into
		// the wrong switch
		// if (time.get() < timeLift) {
		// lift.climb();
		// } else {
		// lift.stall();
		// }
		// Timer.delay(1 / 50); // iterative normally runs 50 times a second, gotta make
		// sure this is the same
		// // speed
		// }
		// drive.deadStop(); // no real way to slow down drivetrain in auto with ramping
		// so have to deadstop
		// if (startPosition == gameData.charAt(0)) { // if the switch matches our
		// starting position, drop the cube
		// gripper.eject();
		// }

		// *************************************************************************************************
		// TODO list
		// math and PID tune for distances (encoders) (value adjustment to get 256)
		// PID tune for gyro and turnAngle
		// timing for lift
		// output time
		// *************************************************************************************************

	}

	@Override
	public void autonomousPeriodic() {
		System.out.println(gyro.getAngle());
	}

	@Override
	public void teleopInit() {
		drive.setMaxSpeed(0.95);
		drive.setMaxRotation(0.82);
	}

	@Override
	public void teleopPeriodic() {

		double rotation = driver.getRawAxis(ControlMap.rotationAxis);

		// Drive Ramping (rate-limit filter)
		if (rampingEnabled) {
			double speedVal = -driver.getRawAxis(ControlMap.speedAxis);
			double change = speedVal - limitedSpeed;
			if (change > limit)
				change = limit;
			else if (change < -limit)
				change = -limit;
			limitedSpeed += change;
		} else {
			limitedSpeed = -driver.getRawAxis(ControlMap.speedAxis);
		}

		// Drive gears and powers
		if (pressed(driver, ControlMap.lowGear)) {
			drive.setLowGear();
		} else if (pressed(driver, ControlMap.highGear)) {
			drive.setHighGear();
		}
		drive.drive(limitedSpeed, rotation);

		// Lift
		if (operator.getRawAxis(ControlMap.lift) == -1) {
			lift.climb();
		} else if (operator.getRawAxis(ControlMap.lift) == 1) {
			lift.descend();
		} else {
			lift.stall();
		}

		// Spinnies
		if (pressed(operator, ControlMap.intake)) {
			gripper.intake();
		} else if (pressed(operator, ControlMap.eject)) {
			gripper.eject();
		} else {
			gripper.stall();
		}

		// Gripper
		if (pressed(operator, 2)) {
			gripper.open();
			drive.setMaxSpeed(0.65);
		} else {
			gripper.close();
			drive.setMaxSpeed(0.95);
		}
	}

	@Override
	public void disabledInit() {
		gripper.close();
	}

	@Override
	public void disabledPeriodic() {
	}

	private boolean pressed(Joystick controller, int button) {
		return controller.getRawButton(button);
	}

	public char getStartPosition() {
		char auto;
		if (leftAuto.get() && !rightAuto.get()) {
			auto = 'L';
		} else if (!leftAuto.get() && rightAuto.get()) {
			auto = 'R';
		} else {
			auto = 'C';
		}
		return auto;
	}
}
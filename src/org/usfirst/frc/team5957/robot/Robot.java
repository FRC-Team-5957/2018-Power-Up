package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

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
	final int sideSelectorA = 6;
	final int sideSelectorB = 7;

	Joystick driver = new Joystick(0);
	Joystick operator = new Joystick(1);

	@Override
	public void robotInit() {
		// Drivetrain
		drive.reset();
		drive.setMaxSpeed(0.87);
		drive.setMaxRotation(0.77);

		// Sensors and subsystems
		leftEnc = new Encoder(2, 3, false, Encoder.EncodingType.k1X);
		rightEnc = new Encoder(4, 5, false, Encoder.EncodingType.k1X);
		leftAuto = new DigitalInput(sideSelectorA);
		rightAuto = new DigitalInput(sideSelectorB);
		compressor = new Compressor(PCM);
		compressor.setClosedLoopControl(true);
		CameraServer.getInstance().startAutomaticCapture();
		// Constants and Variables
	}

	@Override
	public void robotPeriodic() {
		// Updates during different modes
	}

	@Override
	public void autonomousInit() {
		time.reset();
		time.start();
		drive.setMaxSpeed(0.7);
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		startPosition = getStartPosition();

		while (time.get() < 3.5) {
			drive.drive(1, 0);
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

	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopInit() {
		drive.setMaxSpeed(0.87);
		drive.setMaxRotation(0.9);
	}

	@Override
	public void teleopPeriodic() {

		double speed = -driver.getRawAxis(ControlMap.speedAxis);
		double rotation = driver.getRawAxis(ControlMap.rotationAxis);

		// Drive gears and powers
		if (pressed(driver, ControlMap.lowGear)) {
			drive.setLowGear();
		} else if (pressed(driver, ControlMap.highGear)) {
			drive.setHighGear();
		}
		drive.drive(speed, rotation);

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
			drive.setMaxSpeed(0.5);
		} else {
			gripper.close();
			drive.setMaxSpeed(0.8);
		}
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
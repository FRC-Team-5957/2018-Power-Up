package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Robot extends IterativeRobot {

	// Shifting Drivetrain lol
	VictorSP frontLeft = new VictorSP(0);
	VictorSP rearLeft = new VictorSP(1);
	VictorSP frontRight = new VictorSP(2);
	VictorSP rearRight = new VictorSP(3);
	DifferentialDrive drive = new DifferentialDrive(new SpeedControllerGroup(frontLeft, rearLeft),
			new SpeedControllerGroup(frontRight, rearRight));
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();

	// Subsystems
	final int PCM = 1;
	private Solenoid gear = new Solenoid(PCM, 1);
	Gripper gripper = new Gripper(4, 5, PCM, 0);
	Lift lift = new Lift(6);

	// Random variables
	char startPosition;
	String gameData;
	Timer time = new Timer();
	boolean Scale = false;
	boolean Switch = true;

	char switchPos;
	char scalePos;
	// Sensors and compressor
	Encoder leftEnc, rightEnc;
	DigitalInput leftAuto, rightAuto, priority;
	Compressor compressor;
	final int sideSelectorA = 0;
	final int sideSelectorB = 1;

	Joystick driver = new Joystick(0);
	Joystick operator = new Joystick(1);

	// PD values
	final double kP = 0.16;
	final double kD = 0.37;
	double D = 0;
	double PCurrent = 0;
	double PLast = 0;
	double angle = 0;
	double target = 0;

	// Autonomous flags
	int action = 1;
	boolean turningAfter = true;

	// Drive values
	final double kBrakePower = -0.3;
	final double kBrakeTime = 0.25;
	double maxSpeed = 1;
	double maxRotation = 1;

	@Override
	public void robotInit() {
		// Drivetrain
		drive.setSafetyEnabled(false);
		// drive.reset();
		// drive.setMaxSpeed(0.87);
		// drive.setMaxRotation(0.77);

		// Sensors and subsystems
		leftEnc = new Encoder(2, 3, false, Encoder.EncodingType.k1X);
		rightEnc = new Encoder(4, 5, false, Encoder.EncodingType.k1X);
		rightAuto = new DigitalInput(sideSelectorA);
		leftAuto = new DigitalInput(sideSelectorB);
		priority = new DigitalInput(9);
		compressor = new Compressor(PCM);
		compressor.setClosedLoopControl(true);
		CameraServer.getInstance().startAutomaticCapture(0);
		CameraServer.getInstance().startAutomaticCapture(1);

		// Constants and Variables

	}

	@Override
	public void robotPeriodic() {
		// Updates during different modes
	}

	@Override
	public void autonomousInit() {
		drive.setMaxOutput(0.7);
		startPosition = getStartPosition();
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		switchPos = gameData.charAt(0);
		scalePos = gameData.charAt(1);
		if (priority.get() == Switch) { // Priority is switch
			if (startPosition == 'C') { // Starting from the center
				centerSwitchAuto();
			} else { // Starting from the left or right
				switchAuto();
			}
		} else if (priority.get() == Scale){ // Priority is scale

			if (scalePos != startPosition && switchPos == startPosition) { // If we can't to scale
				switchAuto();
			} else if (scalePos == startPosition) { // Do scale
				scaleAuto();
			} else if (startPosition == 'C') {
				System.out.println("Doing simple straight auto");
				if (action == 1) {
					timedDrive(1);
				}
			} else {
				System.out.println("Cannot determine starting position");
				straightAuto();
			}
		} else {
			System.out.println("Cannot determine priority");
		}
	}

	@Override
	public void autonomousPeriodic() {
		if ((Math.abs(PCurrent) > 5 || PCurrent == 0) && turningAfter) {
			angle = gyro.getAngle();
			PLast = PCurrent;
			PCurrent = target - angle;
			D = PLast - PCurrent;
			double output = (PCurrent * kP) - (D * kD);
			drive.arcadeDrive(0, output);
		} else {
			System.out.println("I exited");
			action++;
			brake();
			autonomousInit();
			brake();
		}

	}

	// Actual Autos
	public void scaleAuto() { // FIXME test
		if (action == 1) { // Initial movement
			System.out.println("Doing scale auto");
			gyro.reset();
			turningAfter = false;
			if (scalePos == startPosition) {
				timedDrive(2.5);
			} else {
				timedDrive(1.25);
			}
			System.out.println("Finished Action " + action + ": Moving forward for 2.5s");
		} else if (action == 2) { // Raise lift
			timedLift(2);
			System.out.println("Finished Action " + action + ": Raising lift for 2s");
		} else if (action == 3) { // Turn towards scale
			if (startPosition == 'L') {
				target = -90;
			} else {
				target = 90;
			}
			resetPID();
			turningAfter = true;
			System.out.println("Finished Action " + action + ": Turning to " + target + " degrees");
		} else if (action == 4) { // Move based on scale
			if (startPosition == scalePos) {
				timedDrive(0.25);
				System.out.println("Finished Action " + action + ": Moving forward for 0.25s");
			} else {
				timedReverse(0.25);
				System.out.println("Finished Action " + action + ": Moving backward for 0.25s");
			}
			turningAfter = false;
		} else if (action == 5) { // Decide to drop cube or not
			if (startPosition == scalePos) {
				gripper.eject();
				System.out.println("Finished Action " + action + ": Ejecting cube");
				Timer.delay(1);
				gripper.stall();
			}
			turningAfter = false;
		} else if (action == 6) {
			System.out.println("	Done!");
			turningAfter = false;
		}
	}

	public void scaleAutoOS() { // FIXME test
		if (action == 1) { // Initial forward
			turningAfter = false;
			timedDrive(1.75);
			System.out.println("Finished action " + action + ": ");
		} else if (action == 2) { // Turn to CoF
			if (startPosition == 'L') {
				target = -90;
			} else {
				target = 90;
			}
			resetPID();
			turningAfter = true;
			System.out.println("Finished action " + action + ": Turning to " + target + " degrees");
		} else if (action == 3) { // Drive across the field
			turningAfter = false;
			timedDrive(1);
			System.out.println("Finished action " + action + ": Moving forward for 1s");
		} else if (action == 4) { // Turn to scale
			gyro.reset();
			if (startPosition == 'L') {
				target = 90;
			} else {
				target = -90;
			}
			turningAfter = true;
			resetPID();
			System.out.println("Finished action " + action + ": Turning to " + target + " degrees");
		} else if (action == 5) { // Raise lift
			timedLift(2);
			System.out.println("Finished action " + action + ": Raising lift for 2s");
		} else if (action == 6) { // Drive to scale
			timedDrive(0.65);
			System.out.println("Finished action " + action + ": Moving forward for 0.65s");
		} else if (action == 7) {
			System.out.println("	Done!");
		}
	}

	public void centerSwitchAuto() { // XXX Works
		if (action == 1) { // Initial forward
			System.out.println("Doing center auto");
			gyro.reset();
			timedDrive(0.45);
			if (switchPos == 'L') {
				target = -55;
			} else {
				target = 42;
			}
			turningAfter = true;
			System.out.println("Finished Action " + action + ": Moving forward for 0.45s");
		} else if (action == 2) { // Side movement
			turningAfter = false;
			System.out.println("Finished Action " + action + "Turning to " + target + " degrees");
			if (switchPos == 'L') {
				timedDrive(0.95);
				System.out.println("Finished Action " + action + ": Moving forward for 0.95s");
			} else {
				timedDrive(0.8);
				System.out.println("Finished Action " + action + ": Moving forward for 0.8s");
			}
			target = 0;
			resetPID();
			turningAfter = true;
		} else if (action == 3) { // Raise lift
			turningAfter = false;
			resetPID();
			System.out.println("Finished Action " + action + ": Turning to " + target + "degrees");
			timedLift(0.8);
			System.out.println("Finished Action " + action + ": Raising lift for 1s");
			timedDrive(0.85);
			System.out.println("Finished Action " + action + ": forward movement for 0.5s");
			System.out.println("Ejecting");
			gripper.eject();
		} else if (action == 4) {
			Timer.delay(1);
			gripper.stall();
			System.out.println("	Done!");
		}

	}

	public void switchAuto() { // FIXME test
		if (action == 1) {
			System.out.println("Doing auto for side " + startPosition);
			gyro.reset();
			timedDrive(0.65);
			System.out.println("Finished Action " + action + ": forward movement for 2.5s");
		} else if (action == 2) {
			timedLift(1);
			System.out.println("Finished Action " + action + ": raising lift for 2s");
		} else if (action == 3) {
			if (startPosition == 'L') {
				target = 90;
			} else {
				target = -90;
			}
			resetPID();
			turningAfter = true;
		} else if (action == 4) {
			System.out.println("Finished Action " + (action - 1) + ": Turning to " + target + " degrees");
			brake();
			if (startPosition == switchPos) {
				gripper.eject();
				System.out.println("Ejecting cube :D");
				Timer.delay(1);
				gripper.stall();
				turningAfter = false;
			} else {
				System.out.println("Not ejecting cube D:");
				Timer.delay(1);
				gripper.stall();
				turningAfter = false;
			}
		} else if (action == 5) {
			System.out.println("	Done!");
		}
	}

	public void straightAuto() { // XXX Works
		if (action == 2) {
			System.out.println("Doing complex straight auto");
			turningAfter = false;
			timedDrive(1);
			System.out.println("Finished Action " + action + ": Moving forward for 1s");
		} else if (action == 1) {
			timedLift(1);
			System.out.println("Finished Action " + action + ": Raising lift for 2s");
		} else if (action == 3) {
			if (startPosition == 'L') {
				target = -90;
			} else if (startPosition == 'R') {
				target = 90;
			}
			gyro.reset();
			resetPID();
			turningAfter = true;
		} else if (action == 4) {
			turningAfter = false;
			System.out.println("Finished Action " + (action - 1) + ": Turning to " + target + " degrees");
			System.out.println("	Done!");
		}
	}

	// Auto methods
	public void brake() {
		drive.arcadeDrive(kBrakePower, 0);
		Timer.delay(kBrakeTime);
	}

	public void timedDrive(double time) {
		drive.arcadeDrive(1, 0);
		Timer.delay(time);
		brake();
	}

	public void timedReverse(double time) {
		drive.arcadeDrive(-1, 0);
		Timer.delay(time);
		brake();
	}

	public void timedLift(double time) {
		lift.climb();
		Timer.delay(time);
		lift.stall();
	}

	@Override
	public void teleopInit() {
		maxSpeed = 1;
		maxRotation = 0.9;
		drive.setMaxOutput(1);
	}

	@Override
	public void teleopPeriodic() {

		double speed = -driver.getRawAxis(ControlMap.speedAxis);
		double rotation = driver.getRawAxis(ControlMap.rotationAxis);

		// Drive gears and powers
		if (pressed(driver, ControlMap.lowGear)) {
			setLowGear();
		} else if (pressed(driver, ControlMap.highGear)) {
			setHighGear();
		}
		// drive.drive(speed, rotation);
		drive.arcadeDrive(maxSpeed * getAdjusted(speed), maxRotation * getAdjusted(rotation));

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
		} else {
			gripper.close();
		}
	}

	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		action = 1;
	}

	@Override
	public void testInit() {
		drive.setMaxOutput(0.7);
		if (priority.get() == Switch) { // Priority is switch
			if (startPosition == 'C') { // Starting from the center
				centerSwitchAuto();
			} else { // Starting from the left or right
				switchAuto();
			}
		} else { // Priority is scale
			if (scalePos != startPosition) { // If we can't to scale
				scaleAutoOS();
			} else if (scalePos == startPosition) { // Do scale
				scaleAuto();
			} else { // Something is wrong and neither is true
				straightAuto();
			}
		}
	}

	@Override
	public void testPeriodic() {
		if ((Math.abs(PCurrent) > 5 || PCurrent == 0) && turningAfter) {
			angle = gyro.getAngle();
			PLast = PCurrent;
			PCurrent = target - angle;
			D = PLast - PCurrent;
			double output = (PCurrent * kP) - (D * kD);
			drive.arcadeDrive(0, output);
		} else {
			action++;
			testInit();
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

	public void resetPID() {
		D = 0;
		PCurrent = 0;
		PLast = 0;
		angle = 0;
	}

	public void setHighGear() {
		gear.set(true);
	}

	public void setLowGear() {
		gear.set(false);
	}

	private double getAdjusted(double speed) {
		return speed * Math.pow(Math.abs(speed), 2);
	}
}
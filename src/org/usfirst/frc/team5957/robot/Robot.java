package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
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

	private DoubleSolenoid gear;

	// Subsystems
	final int PCM = 1;
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
	
	// PD values
		final double kP = 0.1;
		final double kD = 0.55;
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

	@Override
	public void robotInit() {
		// Drivetrain
		drive.setSafetyEnabled(false);
//		drive.reset();
//		drive.setMaxSpeed(0.87);
//		drive.setMaxRotation(0.77);

		// Sensors and subsystems
		leftEnc = new Encoder(2, 3, false, Encoder.EncodingType.k1X);
		rightEnc = new Encoder(4, 5, false, Encoder.EncodingType.k1X);
		leftAuto = new DigitalInput(sideSelectorA);
		rightAuto = new DigitalInput(sideSelectorB);
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
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		char switchPos = gameData.charAt(0);
		// char scalePos = gameData.charAt(1);
		drive.setMaxOutput(0.7);
		if (switchPos == 'L') {
			target = -55;
		} else {
			target = 42;
		}
		System.out.println("Finished Initialization: get game data and set autonomous values");

		if (action == 1) { // Initial forward
			gyro.reset();
			timedDrive(0.45);
			turningAfter = true;
			System.out.println("Finished Action " + action + ": forward movement for 0.45s");
		} else if (action == 2) { // Side movement
			System.out.println("Finished turn to" + target + " degrees");
			if (switchPos == 'L') {
				timedDrive(1);
			} else {
				timedDrive(0.85);
			}
			target = 0;
			resetPID();
			turningAfter = true;
			System.out.println("Finished Action " + action + ": forward movement for 0.85s");
		} else if (action == 3) { // Final Forward and coil movement
			System.out.println("Finished turn to: " + target + "degrees");
			timedDrive(0.5);
			System.out.println("Finished Action " + action + ": forward movement for 0.5s");
			turningAfter = false;
		}
	}

	@Override
	public void autonomousPeriodic() {

	}

	@Override
	public void teleopInit() {
//		drive.setMaxSpeed(0.87);
//		drive.setMaxRotation(0.9);
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
//		drive.drive(speed, rotation);
		drive.arcadeDrive(speed, rotation, true);
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
		gear.set(DoubleSolenoid.Value.kForward);
	}

	public void setLowGear() {
		gear.set(DoubleSolenoid.Value.kReverse);
	}
	
	// Drive methods (autonomous)
		public void brake() {
			drive.arcadeDrive(kBrakePower, 0);
			Timer.delay(kBrakeTime);
		}

		public void timedDrive(double time) {
			drive.arcadeDrive(1, 0);
			Timer.delay(time);
			brake();
		}
}
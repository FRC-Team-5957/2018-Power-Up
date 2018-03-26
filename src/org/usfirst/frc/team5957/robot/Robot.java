package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends IterativeRobot {

	// Subsystems
	final int PCM = 1;
	ShiftingDrivetrain drive = new ShiftingDrivetrain(0, 1, 2, 3, PCM, 1, 2);
	Gripper gripper = new Gripper(4, 5, PCM, 0);
	Lift lift = new Lift(6);

	// Auto things
	char startPosition;
	Timer time = new Timer();

	// Sensors and compressor
	DigitalInput leftAuto, rightAuto;
	Compressor compressor;
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();

	Joystick driver = new Joystick(0);
	Joystick operator = new Joystick(1);

	@Override
	public void robotInit() {
		// Drivetrain
		gyro.reset();
		gyro.calibrate();

		// Sensors and subsystems
		leftAuto = new DigitalInput(1); // LEft
		rightAuto = new DigitalInput(0); // Right
		compressor = new Compressor(1);
		compressor.setClosedLoopControl(true);
		CameraServer.getInstance().startAutomaticCapture(0);
		CameraServer.getInstance().startAutomaticCapture(1);
	}

	@Override
	public void robotPeriodic() {
		// somehow put the motor values and sensor outputs on the dashboard in HIGHLY
		// visible way
	}

	@Override
	public void autonomousInit() {
		time.reset();
		time.start();
		drive.setMaxSpeed(0.7);
		drive.setMaxRotation(0.8);
		drive.setLowGear();
		gripper.stall();
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		startPosition = getStartPosition();
		System.out.println("Finished Initialization");

		if (startPosition == 'C') {
			System.out.println("Straight Drive Selected");
			// VAPOR: go forward and raise lift for scale
			while (time.get() < 2) {
				drive.drive(1, 0);
				if (time.get() < 1.95) {
					lift.climb();
					System.out.println("Raising lift");
				} else {
					lift.stall();
					System.out.println("Lift Stalling");
				}
			}
		} else {
			// go forward and raise lift, if correct side, spit the cube
			System.out.println("50/50 Switch Selected");
			while (time.get() < 2) {
				drive.drive(1, -gyro.getAngle() * 0.1);
				if (time.get() > 0.5)
					lift.climb();
			}
			lift.stall();
			if (startPosition == gameData.charAt(0)) {
				System.out.println("Correct side");
				gripper.eject();
				System.out.println("Ejecting");
				Timer.delay(0.5);
			}
			gripper.stall();

		}

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
		double speed = -driver.getRawAxis(ControlMap.speedAxis);
		double cubeAdjust = operator.getRawAxis(ControlMap.cubeAdjustAxis);

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
		} else if (Math.abs(operator.getRawAxis(ControlMap.cubeAdjustAxis)) == 1) {
			gripper.rotate(cubeAdjust);
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
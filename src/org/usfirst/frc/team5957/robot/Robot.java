/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
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
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	// Declarations and constants
	// Drivetrain
	private VictorSP frontLeft, rearLeft, frontRight, rearRight;
	private DifferentialDrive drive;
	private Solenoid gear;
	private final boolean highGear = true;
	private final boolean lowGear = false;
	private final int frontLeftCh = 0;
	private final int rearLeftCh = 1;
	private final int frontRightCh = 2;
	private final int rearRightCh = 3;
	private final int gearCh = 0;
	private double maxSpeed = 0.6;

	// Elevator and intake
	private VictorSP leftSpinny, rightSpinny, lift;
	private DigitalInput topLimit, lowLimit;
	private Solenoid gripper;
	private final boolean closed = true;
	private final boolean open = false;
	private final int leftSpinnyCh = 4;
	private final int rightSpinnyCh = 5;
	private final int liftCh = 6;
	private final int topCh = 6;
	private final int lowCh = 7;
	private final int gripperCh = 1;
	// TODO test direction for elevator
	private final double testSpeed = 0.3;
	// TODO test direction for spinnies
	private final double intakeSpeed = -0.3;
	private final double spitSpeed = 1;
	private final double dropSpeed = 0.2;
	private final boolean commandStyle = true;
	private boolean gathering = false;

	// Climber ( L O L )
	// private VictorSP leftClimb, rightClimb;
	// private final int leftClimbCh = 7;
	// final int rightClimbCh = 8;

	// Sensors and subsystems
	private ADXRS450_Gyro gyro;
	private Encoder leftEnc, rightEnc;
	private DigitalInput leftAuto, rightAuto, scale;
	private Compressor compressor;
	private final int sideSelectorA = 0;
	private final int sideSelectorB = 1;
	private final int scaleCh = 2;

	// CAN Channels
	private final int PCM = 1;

	// OI
	private Joystick driver, operator, tester;
	private final int driverCh = 0;
	private final int operatorCh = 1;

	// PS4
	final int x = 1;
	final int circle = 2;
	final int square = 3;
	final int triangle = 4;
	final int LX = 0;
	final int LY = 1;
	final int RX = 4;
	final int RY = 5;
	final int L1 = 5;
	final int R1 = 6;
	final int L2 = 2;
	final int R2 = 3;
	// Logitech
	// N64

	// Constants and variables
	String gameData;
	String startPosition;

	// PID system values
	// Gyro
	double gyroCurrent;
	double gyroTarget;
	double gyro_kP;
	double gyro_kD;
	boolean turning;

	// Encoders

	@Override
	public void robotInit() {
		// Instantiation
		// Drivetrain
		frontLeft = new VictorSP(frontLeftCh);
		rearLeft = new VictorSP(rearLeftCh);
		frontRight = new VictorSP(frontRightCh);
		rearRight = new VictorSP(rearRightCh);
		drive = new DifferentialDrive(new SpeedControllerGroup(frontLeft, rearLeft),
				new SpeedControllerGroup(frontRight, rearRight));
		gear = new Solenoid(gearCh);
		gear.set(lowGear); // Setting initial gear

		// Lift and gripper
		lift = new VictorSP(liftCh);
		leftSpinny = new VictorSP(leftSpinnyCh);
		rightSpinny = new VictorSP(rightSpinnyCh);
		rightSpinny.setInverted(true);
		gripper = new Solenoid(gripperCh);
		topLimit = new DigitalInput(topCh);
		lowLimit = new DigitalInput(lowCh);
		gripper.set(closed);

		// Climber
		// leftClimb = new VictorSP(leftClimbCh);
		// rightClimb = new VictorSP(rightClimbCh);

		// Sensors and subsystems
		gyro = new ADXRS450_Gyro();
		gyro.reset();
		gyro.calibrate();
		leftEnc = new Encoder(2, 3, false, Encoder.EncodingType.k1X);
		rightEnc = new Encoder(4, 5, false, Encoder.EncodingType.k1X);
		leftAuto = new DigitalInput(sideSelectorA);
		rightAuto = new DigitalInput(sideSelectorB);
		scale = new DigitalInput(scaleCh);
		compressor = new Compressor(PCM);
		compressor.setClosedLoopControl(true);

		// OI
		driver = new Joystick(driverCh);
		operator = new Joystick(operatorCh);

	}

	@Override
	public void robotPeriodic() {
		gyroCurrent = gyro.getAngle();
		if (isAutonomous()) {

		} else if (isOperatorControl()) {
			if (held(driver, RX)) {
				turning = true;
			} else {
				turning = false;
				gyroTarget = gyro.getAngle();
			}
		}
	}

	@Override
	public void autonomousInit() {
		// Reset variables
		gyro.reset();
		leftEnc.reset();
		rightEnc.reset();

		// Get data
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		startPosition = getStartPosition();

		// Auto logic
		if (scale.get() && canScoreScale()) { // Scale Auto
			// move forward
			// turn based on position
			// switch to low gear
			// move towards scale
			// move lift to correct height
			// output cube
		} else if (!scale.get() && canScoreSwitch()) { // Switch Auto
			if (startPosition == "middle") {
				// turn based on gameData
				// move forward
				// turn toward switch based on gameData
				// move forward until close to switch
				// output cube
			} else {
				// turn towards switch
				// move forward until close to switch
				// output cube
			}
		} else { // Forward auto
			// move forward to break auto line
		}

	}

	@Override
	public void autonomousPeriodic() {
		Timer.delay(0.01);
	}

	@Override
	public void teleopPeriodic() {
		// Drivetrain power values
		double speedAxisVal = squaredish(driver.getRawAxis(LY));
		double rotationAxisVal = squaredish(driver.getRawAxis(RX));
		double speed = maxSpeed * speedAxisVal;
		double rotation = turning ? maxSpeed * rotationAxisVal : ((gyroTarget - gyroCurrent) * gyro_kP);

		// Drivetrain
		if (pressed(driver, L1)) {
			gear.set(lowGear);
			maxSpeed = 0.6;
		} else if (pressed(driver, R1)) {
			gear.set(highGear);
			maxSpeed = 1;
		}

		drive.arcadeDrive(speed, rotation);

		// Lift logic
		if (pressed(operator, triangle) && !topLimit.get()) {
			lift.set(testSpeed);
		} else if (pressed(operator, x) && !lowLimit.get()) {
			lift.set(-testSpeed);
		} else {
			lift.set(0); // eventually make value proportional to normal force on elevator (need encoder)
		}

		// Intake wheels
		if (commandStyle) {
			// Subsystem commands
			// TODO check command functionality
			gathering = !gathering && pressed(operator, L1);
			if (gathering) {
				startIntake();
			} else {
				stopIntake();
			}
		} else {
			// Just Wheels
			if (pressed(operator, L1)) {
				setSpinnies(intakeSpeed);
			} else if (pressed(operator, R1) && pressed(operator, square)) {
				setSpinnies(dropSpeed);
			} else if (pressed(operator, R1) && pressed(operator, circle)) {
				setSpinnies(spitSpeed);
			} else {
				setSpinnies(0);
			}

			// Just gripper
			if (held(operator, L2)) {
				gripper.set(open);
			} else if (held(operator, R2)) {
				gripper.set(closed);
			}
		}

		// Climb
		// lol what climb

		Timer.delay(0.01);
	}

	// Joystick methods
	private boolean pressed(Joystick controller, int button) {
		return controller.getRawButton(button);
	}

	private boolean held(Joystick controller, int axis) {
		return controller.getRawAxis(axis) != 0;
	}

	private double squaredish(double input) {
		return input * Math.abs(input);
	}

	// Intake and elevator methods
	private void setSpinnies(double power) {
		leftSpinny.set(power);
		rightSpinny.set(power);
	}

	private void startIntake() {
		gripper.set(open);
		setSpinnies(intakeSpeed);
		maxSpeed = 0.3;
	}

	private void stopIntake() {
		gripper.set(closed);
		setSpinnies(0);
		maxSpeed = 0.8;
	}
	// DANGER ZONE! EXPERIMENTAL CODE BEYOND THIS POINT!
	// -------------------------------------------------

	// Code runs periodically at all times

	// Methods to be tested
	public String getStartPosition() {
		// 3-position switch
		// Left position DIO0 = true, DIO1 = false
		// Right position DIO0 = false, DIO1 = true
		// Middle potition DIO0 = DIO1 = true
		// returns auto to run based on match between position switch and game data
		String auto;
		if (leftAuto.get() && !rightAuto.get()) {
			auto = "left";
		} else if (!leftAuto.get() && rightAuto.get()) {
			auto = "right";
		} else {
			auto = "middle";
		}
		return auto;
	}

	private boolean canScoreScale() {
		if (startPosition != "middle") {
			return startPosition == "left" && gameData.charAt(1) == 'L'
					|| startPosition == "right" && gameData.charAt(1) == 'R';
		} else {
			return false;
		}
	}

	private boolean canScoreSwitch() {
		if (startPosition == "middle") {
			return true;
		} else {
			return startPosition == "left" && gameData.charAt(0) == 'L'
					|| startPosition == "right" && gameData.charAt(0) == 'R';
		}
	}

	// Test variables
	boolean gtaActive;
	final int GTA = 1;
	final int arcade = 2;

	// Testing Encoders
	int countL = 0;
	int countR = 0;
	double distL = 0;
	double distR = 0;
	boolean stoppedL = true;
	boolean stoppedR = true;

	boolean testPeriodicFunctions = true;
	boolean testInitFunctions = false;

	@Override
	public void testInit() {
		if (testInitFunctions) {
			// Write testInit code in this loop
		}
	}

	@Override
	public void testPeriodic() {
		if (testPeriodicFunctions) {
			// Write testPeriodic code in this loop

			// Encoder value testing
			countL = leftEnc.get();
			countR = rightEnc.get();
			distL = leftEnc.getDistance(); // returns number of rotations of wheel shaft (adjusted by the k4X in the
											// constructor)
			distR = rightEnc.getRaw(); // returns number of rotations of the encoder shaft
			stoppedL = leftEnc.getStopped();
			stoppedR = rightEnc.getStopped();

			// Logic for switching between driving modes (GTA-style or arcade drive)
			// TODO: Decide which mode driver likes better
			if (tester.getRawButton(GTA) && !tester.getRawButton(arcade)) {
				gtaActive = true;
			}
			if (!tester.getRawButton(GTA) && tester.getRawButton(arcade)) {
				gtaActive = true;
			}

			if (gtaActive) {

				if (driver.getRawAxis(R2) != 0 && driver.getRawAxis(L2) == 0) {
					drive.arcadeDrive(driver.getRawAxis(R2), driver.getRawAxis(LX), true);
				} else if (driver.getRawAxis(L2) != 0 && driver.getRawAxis(R2) == 0) {
					drive.arcadeDrive(-driver.getRawAxis(L2), driver.getRawAxis(LX), true);
				} else {
					drive.arcadeDrive(0, driver.getRawAxis(LX));
				}

			} else {
				drive.arcadeDrive(driver.getRawAxis(LY), driver.getRawAxis(RX), true);
			}

			Timer.delay(0.01);

			// TODO Figure out where these values show up in the SmartDashboard
			SmartDashboard.putNumber("LeftEncoder", distL);
			SmartDashboard.putNumber("RightEncoder", distR);
			LiveWindow.add(leftEnc);
			LiveWindow.add(rightEnc);
		}
	}
}

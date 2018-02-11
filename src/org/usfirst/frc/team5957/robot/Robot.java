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
	VictorSP frontLeft, rearLeft, frontRight, rearRight;
	DifferentialDrive drive;
	Solenoid gear;
	final boolean highGear = true;
	final boolean lowGear = false;
	final int frontLeftCh = 0;
	final int rearLeftCh = 1;
	final int frontRightCh = 2;
	final int rearRightCh = 3;
	final int gearCh = 0;

	// Lift and gripper
	VictorSP leftSpinny, rightSpinny, lift;
	DigitalInput topLimit, lowLimit;
	Solenoid gripper;
	final boolean closed = true;
	final boolean open = false;
	final int leftSpinnyCh = 4;
	final int rightSpinnyCh = 5;
	final int liftCh = 6;
	final int topCh = 6;
	final int lowCh = 7;
	final int gripperCh = 1;

	// Climber
	VictorSP leftClimb, rightClimb;
	final int leftClimbCh = 7;
	final int rightClimbCh = 8;

	// Sensors and subsystems
	ADXRS450_Gyro gyro;
	Encoder leftEnc, rightEnc;
	DigitalInput leftAuto, rightAuto, scale;
	Compressor compressor;

	// CAN Channels
	final int PCM = 1;

	// OI
	Joystick driver, operator, tester;
	final int driverCh = 0;
	final int operatorCh = 1;
	final int testCh = 3;

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
	// TODO: figure out mapping for Logitech Controller
	// N64
	// TODO: figure out mapping for N64 controller

	// Constants and variables
	String gameData;
	String startPosition;

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
		gripper = new Solenoid(gripperCh);
		topLimit = new DigitalInput(topCh);
		lowLimit = new DigitalInput(lowCh);
		gripper.set(closed);

		// Climber
		leftClimb = new VictorSP(leftClimbCh);
		rightClimb = new VictorSP(rightClimbCh);

		// Sensors and subsystems
		gyro = new ADXRS450_Gyro();
		leftEnc = new Encoder(2, 3, false, Encoder.EncodingType.k1X);// TODO adjust values until its 1 rotation = 1
																		// count
		rightEnc = new Encoder(4, 5, false, Encoder.EncodingType.k1X);
		leftAuto = new DigitalInput(0);
		rightAuto = new DigitalInput(1);
		scale = new DigitalInput(2);
		compressor = new Compressor(PCM);

		// OI
		driver = new Joystick(driverCh);
		operator = new Joystick(operatorCh);
		tester = new Joystick(testCh);

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
		// TODO: Finish teleop outline

		// Shift
		if (driver.getRawButton(L1)) {
			gear.set(lowGear);
		} else if (driver.getRawButton(R2)) {
			gear.set(highGear);
		}

		// Lift logic

		// Intake wheels

		// Climb

		// Standard drive, squared inputs, no correction
		drive.arcadeDrive(driver.getRawAxis(LY), driver.getRawAxis(RX), true);

		Timer.delay(0.01);
	}

	// DANGER ZONE! EXPERIMENTAL CODE BEYOND THIS POINT!
	// -------------------------------------------------

	// Code runs periodically at all times
	@Override
	public void robotPeriodic() {
	}

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

	public boolean canScoreScale() {
		if (startPosition != "middle") {
			return startPosition == "left" && gameData.charAt(1) == 'L'
					|| startPosition == "right" && gameData.charAt(1) == 'R';
		} else {
			return false;
		}
	}

	public boolean canScoreSwitch() {
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

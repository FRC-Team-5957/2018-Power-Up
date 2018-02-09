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
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

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
	final int topCh = 0;
	final int lowCh = 1;
	final int gripperCh = 1;

	// Climber
	VictorSP leftClimb, rightClimb;
	final int leftClimbCh = 7;
	final int rightClimbCh = 8;

	// Sensors and subsystems
	ADXRS450_Gyro gyro;
	DigitalInput leftAuto, rightAuto;
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
	// TODO: finish mapping for PS4 controller
	// Logitech
	// TODO: figure out mapping for Logitech Controller
	// N64
	// TODO: figure out mapping for N64 controller

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
		gear.set(lowGear);

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
		leftAuto = new DigitalInput(0);
		rightAuto = new DigitalInput(1);
		compressor = new Compressor(PCM);

		// OI
		driver = new Joystick(driverCh);
		operator = new Joystick(operatorCh);
		tester = new Joystick(testCh);

	}

	@Override
	public void autonomousInit() {

		// Switch for selecting auto side
		String autoPosition = getAutoPosition();
		switch (autoPosition) {
		case "left":
			// TODO: Left auto logic (write left auto here)
			break;
		case "right":
			// TODO: Right auto logic (write right auto here)
			break;
		case "middle":
			// TODO: Middle auto logic (write middle auto here)
			break;
		}

	}

	@Override
	public void autonomousPeriodic() {

	}

	@Override
	public void teleopPeriodic() {

		// TODO: Finish teleop outline

		// Shift operation
		if (driver.getRawButton(L1)) {
			gear.set(lowGear);
		} else if (driver.getRawButton(R2)) {
			gear.set(highGear);
		}

		// Standard drive, squared inputs, no correction
		drive.arcadeDrive(driver.getRawAxis(LY), driver.getRawAxis(RX), true);

		// Standard timer delay
		Timer.delay(0.01);
	}

	// DANGER ZONE! EXPERIMENTAL CODE BEYOND THIS POINT!
	// -------------------------------------------------

	// Code runs periodically at all times
	@Override
	public void robotPeriodic() {

	}

	// Methods to be tested
	public String getAutoPosition() {

		/*
		 * 3-position switch left position { DIO0 = true, DIO1 = false} right position {
		 * DIO0 = false, DIO1 = true} middle potitiom { DIO0 = DIO1 = false}
		 */

		String autoPosition = "";
		if (leftAuto.get() && !rightAuto.get()) {
			autoPosition = "left";
		} else if (!leftAuto.get() && rightAuto.get()) {
			autoPosition = "right";
		} else {
			autoPosition = "middle";
		}

		return autoPosition;

	}

	// Test variables
	boolean gtaActive;
	final int GTA = 1;
	final int arcade = 2;

	@Override
	public void testInit() {

	}

	@Override
	public void testPeriodic() {

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

		Timer.delay(0.04);

	}
}

package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	// Subsystems
	ShiftingDrivetrain drive = new ShiftingDrivetrain(0, 1, 2, 3, 1, 0);
	Gripper gripper = new Gripper(4, 5, 1, 1, 2);
	Lift lift = new Lift(6, 0, 1);

	// Random variables
	private final boolean commandStyle = false;
	char startPosition;
	String gameData;
	Timer time = new Timer();

	// Climber (lol what climber)
	// private VictorSP leftClimb, rightClimb;
	// private final int leftClimbCh = 7;
	// final int rightClimbCh = 8;

	// Sensors and compressor
	Encoder leftEnc, rightEnc;
	DigitalInput leftAuto, rightAuto;
	Compressor compressor;
	final int sideSelectorA = 6;
	final int sideSelectorB = 7;
	final int PCM = 1;

	// OI TODO make an Operator Interface class so this stuff doesnt take up
	// 983409859082345 lines...
	Joystick driver, operator;
	final int driverCh = 0;
	final int operatorCh = 1;
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
	// Logitech TODO map
	// N64 TODO map

	@Override
	public void robotInit() {
		// Drivetrain
		drive.reset();
		drive.setMaxSpeed(0.6);
		drive.setMaxRotation(0.8);

		// Climber ( lol maybe at Portsmouth )
		// leftClimb = new VictorSP(leftClimbCh);
		// rightClimb = new VictorSP(rightClimbCh);

		// Sensors and subsystems
		leftEnc = new Encoder(2, 3, false, Encoder.EncodingType.k1X);
		rightEnc = new Encoder(4, 5, false, Encoder.EncodingType.k1X);
		leftAuto = new DigitalInput(sideSelectorA);
		rightAuto = new DigitalInput(sideSelectorB);
		compressor = new Compressor(PCM);
		compressor.setClosedLoopControl(true);

		// OI
		driver = new Joystick(driverCh);
		operator = new Joystick(operatorCh);

		// Constants and Variables
	}

	@Override
	public void robotPeriodic() {
		// Updates during different modes
	}

	@Override
	public void autonomousInit() {
		// Reset variables
		drive.reset();
		leftEnc.reset();
		rightEnc.reset();
		time.reset();
		time.start();
		Timer.delay(0.5);

		// Get data
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		startPosition = getStartPosition();

		// Auto logic
		// if (startPosition == gameData.charAt(0)) {
		// while (leftEnc.getDistance() <= 5000) {

		// WHEN YOURE TOO LAZY TO USE BOTH THE $50 ENCODERS SO YOU JUST USE ONE
		// @BEN..........
		// TODO add some way to match encoder side values (possibly unnecessary) (can do
		// this with master/slave pairs on the victors but we will have to implement
		// our own)

		// drive.drive(1, 0); // change this to
		// }
		// gripper.drop();
		// } else if (startPosition != gameData.charAt(0)) {
		// while (leftEnc.getDistance() <= 5000) {
		// drive.drive(1, 0);
		// }
		// }

		/* NOT WRONG BUT EASIER TO DRIVE AND THEN CHECK - Andrei */
		// like this
		double timeToGetToSwitch = 3; // a guess (adjust until we reach the switch but dont crash into it
		double timeLift = 2; // another guess
		while (time.get() < timeToGetToSwitch) {
			drive.drive(1, 0); // can add correction and encoder usage later
			// moves forward until the switch and raises lift at the same time
			gripper.stall(); // added to make sure we dont drop the cube by accident into the wrong switch
			if (time.get() < timeLift) {
				lift.climb();
			} else {
				lift.stall();
			}
			Timer.delay(1 / 50); // iterative normally runs 50 times a second, gotta make sure this is the same
									// speed
		}
		drive.deadStop(); // no real way to slow down drivetrain in auto with ramping so have to deadstop
		if (startPosition == gameData.charAt(0)) { // if the switch matches our starting position, drop the cube
			gripper.eject();
		}

	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopInit() {
		drive.enableRamping(0.4);

	}

	@Override
	public void teleopPeriodic() {

		double speed = -driver.getRawAxis(1);
		double rotation = driver.getRawAxis(4);
		boolean gathering = false;
		if (pressed(operator, L1)) {
			if (gathering) {
				gathering = false;
			} else if (gathering == false) {
				gathering = true;
			}
		}

		// Drivetrain TODO test and set ramping value as per driver
		if (pressed(driver, L1)) {
			drive.setLowGear();
		} else if (pressed(driver, R1)) {
			drive.setHighGear();
		}
		drive.drive(speed, rotation);

		// Lift logic TODO test and set speed value as per operator
		if (pressed(operator, triangle)) {
			lift.climb();
		} else if (pressed(operator, x)) {
			lift.descend();
		} else {
			lift.stall();
		}

		// Intake wheels TODO test and see what drive team prefer
		if (commandStyle) {
			// Commands
			if (gathering) {
				startIntake();
			} else {
				stopIntake();
			}
		} else {
			// Just Wheels
			if (pressed(operator, L1)) {
				gripper.intake();
			} else if (pressed(operator, R1) && pressed(operator, square)) {
				gripper.drop();
			} else if (pressed(operator, R1) && pressed(operator, circle)) {
				gripper.eject();
			} else {
				gripper.stall();
			}

			// Just gripper
			if (pressed(driver, R1)) {
				gripper.open();
			}
		}

		// (>O.O)> _|` -| (our team, trying to chase a climber but still having no
		// working lift)
	}

	// Joystick methods
	// Checks button press
	private boolean pressed(Joystick controller, int button) {
		return controller.getRawButton(button);
	}

	// Checks axis != 0
	// private boolean held(Joystick controller, int axis) {
	// return Math.abs(controller.getRawAxis(axis)) > 0.01;
	// }

	// open gripper, start spinnies at intake speed, lower max drive power
	private void startIntake() {
		gripper.open();
		gripper.intake();
		drive.setMaxSpeed(0.4);
	}

	// close grippers, stop spinnies, return max power to normal
	private void stopIntake() {
		gripper.close();
		gripper.stall();
		drive.setMaxSpeed(0.8);
	}

	// Autonomous methods
	// gets start position from autonomous switch
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

	// // Checks if scale is possible
	// private boolean canScoreScale() {
	// if (startPosition != 'C') {
	// return startPosition == 'L' && gameData.charAt(1) == 'L'
	// || startPosition == 'R' && gameData.charAt(1) == 'R';
	// } else {
	// return false;
	// }
	// }
	//
	// // Checks if switch is possible
	// private boolean canScoreSwitch() {
	// if (startPosition == 'C') {
	// return true;
	// } else {
	// return startPosition == 'L' && gameData.charAt(0) == 'L'
	// || startPosition == 'R' && gameData.charAt(0) == 'R';
	// }
	// }

	// Testing Encoders
	int countL = 0;
	int countR = 0;
	double distL = 0;
	double distR = 0;
	boolean stoppedL = true;
	boolean stoppedR = true;

	@Override
	public void testPeriodic() {

		// Encoder value testing
		countL = leftEnc.get();
		countR = rightEnc.get();
		distL = leftEnc.getDistance(); // returns number of rotations of wheel shaft (adjusted by the k4X in the
										// constructor)
		distR = rightEnc.getRaw(); // returns number of rotations of the encoder shaft
		stoppedL = leftEnc.getStopped();
		stoppedR = rightEnc.getStopped();

		SmartDashboard.putNumber("LeftEncoder", distL);
		SmartDashboard.putNumber("RightEncoder", distR);
		LiveWindow.add(leftEnc);
		LiveWindow.add(rightEnc);
	}
}

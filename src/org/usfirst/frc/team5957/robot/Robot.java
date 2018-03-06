package org.usfirst.frc.team5957.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	// Drivetrain
	ShiftingDrivetrain drive = new ShiftingDrivetrain(0, 1, 2, 3, 0);

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
	private final double stallSpeed = -0.15;
	private final boolean commandStyle = true;
	private boolean gathering = false;

	// Climber (lol what climber)
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
	private final int PCM = 1;

	// OI
	private Joystick driver, operator;
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
	// Logitech TODO map
	// N64 TODO buy and map

	// Constants and variables
	String gameData;
	String startPosition;
	final double delay = 0.005;

	// PID values
	// Gyro TODO tune constant values
	double gyroPCurrent, gyroPLast, gyroTarget, gyroCurrent, gyroP, gyroD, gyroOut;
	final double gyro_kP = 0.05;
	final double gyro_kD = 0;
	boolean turning;

	// Encoders TODO tune constant values
	double eDifference, eCurrent, eLast, eTarget, eP, eD;
	final double e_kP = 0.05;
	final double e_kD = 0;

	@Override
	public void robotInit() {
		// Drivetrain
		drive.setMaxSpeed(0.6);
		drive.setMaxRotation(0.8);

		// Lift and gripper
		lift = new VictorSP(liftCh);
		leftSpinny = new VictorSP(leftSpinnyCh);
		rightSpinny = new VictorSP(rightSpinnyCh);
		rightSpinny.setInverted(true);
		gripper = new Solenoid(gripperCh);
		topLimit = new DigitalInput(topCh);
		lowLimit = new DigitalInput(lowCh);
		gripper.set(closed); // Setting initial gripper position

		// Climber ( lol maybe at Portsmouth )
		// leftClimb = new VictorSP(leftClimbCh);
		// rightClimb = new VictorSP(rightClimbCh);

		// Sensors and subsystems
		gyro = new ADXRS450_Gyro();
		gyro.reset();
		gyro.calibrate();
		gyroCurrent = gyro.getAngle();
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
		// Updates during different modes
		if (isAutonomous()) {
			// PID
			gyroPLast = gyroPCurrent;
			gyroPCurrent = gyroTarget - gyroCurrent;
			gyroD = gyroPLast - gyroPCurrent;
			gyroOut = (gyroPCurrent * gyro_kP) - (gyroD - gyro_kD);

		} else if (isOperatorControl()) {
			// Driving correction TODO test functionality
			if (held(driver, RX)) {
				turning = true;
			} else {
				turning = false;
				gyroTarget = gyro.getAngle();
			}
		}

		Timer.delay(delay);
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
		Timer.delay(delay);
	}

	@Override
	public void teleopPeriodic() {

		double speed = -driver.getRawAxis(1);
		double rotation = driver.getRawAxis(4);

		// Drivetrain TODO test
		if (pressed(driver, L1)) {
			drive.setLowGear();
		} else if (pressed(driver, R1)) {
			drive.setHighGear();
		}
		drive.drive(speed, rotation);

		// Lift logic TODO test
		if (pressed(operator, triangle) && !topLimit.get()) {
			lift.set(testSpeed);
		} else if (pressed(operator, x) && !lowLimit.get()) {
			lift.set(-testSpeed);
		} else {
			lift.set(0); // eventually make value proportional to normal force on elevator (need encoder)
		}

		// Intake wheels TODO test
		if (commandStyle) {
			// Subsystem commands
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

		// Climb ( L O L )

		Timer.delay(delay);
	}

	// Joystick methods
	// Checks button press
	private boolean pressed(Joystick controller, int button) {
		return controller.getRawButton(button);
	}

	// Checks axis != 0
	private boolean held(Joystick controller, int axis) {
		return Math.abs(controller.getRawAxis(axis)) > 0.01;
	}

	// Intake and elevator methods
	private void setSpinnies(double power) {
		leftSpinny.set(power);
		rightSpinny.set(power);
	}

	// open gripper, start spinnies at intake speed, lower max drive power
	private void startIntake() {
		gripper.set(open);
		setSpinnies(intakeSpeed);
		drive.setMaxSpeed(0.4);
	}

	// close grippers, stop spinnies, return max power to normal
	private void stopIntake() {
		gripper.set(closed);
		setSpinnies(stallSpeed);
		drive.setMaxSpeed(0.8);
	}

	// Autonomous methods
	// gets start position from autonomous switch
	public String getStartPosition() {
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

	// Checks if scale is possible
	private boolean canScoreScale() {
		if (startPosition != "middle") {
			return startPosition == "left" && gameData.charAt(1) == 'L'
					|| startPosition == "right" && gameData.charAt(1) == 'R';
		} else {
			return false;
		}
	}

	// Checks if switch is possible
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

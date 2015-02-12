package org.usfirst.frc.team2984.robot;

import java.nio.ByteBuffer;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.vision.USBCamera;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	RobotDrive myRobot;
	private CANTalon talon2;
	private CANTalon talon1;
	private Jaguar jaguar;
	Joystick stick;
	int autoLoopCounter;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		talon2 = new CANTalon(2);
		talon1 = new CANTalon(1);
		stick = new Joystick(0);
	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	public void autonomousInit() {
		autoLoopCounter = 0;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		talon2.set(1);
	}

	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	public void teleopInit() {
//		camera.openCamera();
//		camera.startCapture();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		if(Math.abs(stick.getX())>0.1){
			talon2.set(stick.getX());
			talon1.set(-stick.getX());
		}
		else {
			talon2.set(0);
			talon1.set(0);
		}
		
		ByteBuffer buffer = null;
		System.out.println(buffer);
		
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		LiveWindow.run();
	}

}

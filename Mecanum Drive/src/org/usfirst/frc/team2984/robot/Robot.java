package org.usfirst.frc.team2984.robot;


import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;

/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive class.
 */
public class Robot extends IterativeRobot {
	
    MecanumDrive mecanumDrive;
    RobotDrive drive;
    boolean unSafeToggle = false;
    boolean unSafeMode = true;
    Joystick stick;
    
    // The channel on the driver station that the joystick is connected to
    final int joystickChannel	= 0;

    public void robotInit() {
        stick = new Joystick(joystickChannel);
        mecanumDrive = new MecanumDrive(3,4,5,1);
//        drive = new RobotDrive(new CANTalon(3), new CANTalon(5), new CANTalon(4), new CANTalon(1));
    }
        

    /**
     * Runs the motors with Mecanum drive.
     */
    public void teleopPeriodic() {
//        if(Math.abs(stick.getX())>-0.4 || Math.abs(stick.getY())>-0.4){
//        	if(leftButton){
//            	drive.mecanumDrive_Cartesian(stick.getX(), stick.getY(), 0.4, 0);
//        	} else if(rightButton) {
//            	drive.mecanumDrive_Cartesian(stick.getX(), stick.getY(), -0.4, 0);
//        	} else {
//            	drive.mecanumDrive_Cartesian(stick.getX(), stick.getY(), 0, 0);
//        	}
//        }
        	mecanumDrive.mecanumDrive_Cartesian(stick.getX(), stick.getY(), stick.getTwist(), 0);
        	mecanumDrive.update();
        	if(stick.getRawButton(4)){
        		if(!this.unSafeToggle){
        			this.unSafeToggle = true;
        			this.unSafeMode = !this.unSafeMode;
        			if(this.unSafeMode){
        				this.mecanumDrive.setVoltageRamp(2.5);
        			} else {
        				this.mecanumDrive.setVoltageRamp(-1);
        			}
        		}
        	} else {
        		this.unSafeToggle = false;
        	}
        	
    }
    
}

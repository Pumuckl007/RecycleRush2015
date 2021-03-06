package org.usfirst.frc.team2984.robot;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive class.
 */
public class Robot extends IterativeRobot {
	
    private MecanumDrive mecanumDrive;
    private boolean unSafeToggle = false;
    private boolean unSafeMode = true;
    private Joystick stick;
    
    // The channel on the driver station that the joystick is connected to
    final int joystickChannel	= 0;

    public void robotInit() {
        stick = new Joystick(joystickChannel);
        mecanumDrive = new MecanumDrive(3,4,5,1);
    }
        

    /**
     * Runs the motors with Mecanum drive.
     */
    public void teleopPeriodic() {
    	long time = System.currentTimeMillis();
    	this.mecanumDrive.drive(stick.getX(), -stick.getY(), stick.getTwist(), 0);
    	this.mecanumDrive.update();
    	if(stick.getRawButton(4)){
    		if(!this.unSafeToggle){
    			this.unSafeToggle = true;
    			this.unSafeMode = !this.unSafeMode;
    			if(this.unSafeMode){
    				this.mecanumDrive.setSafeMode(true, 4, 40);
    			} else {
    				this.mecanumDrive.setSafeMode(false, -1, -1);
    			}
    		}
    	} else {
    		this.unSafeToggle = false;
    	}
        SmartDashboard.putNumber("Length Of Cycle", System.currentTimeMillis()-time);	
    }
    
}

package org.usfirst.frc.team2984.robot;

import edu.wpi.first.wpilibj.CANTalon;

public class MecanumDrive {
	
	//The motors
	private CANTalon frontLeftMotor;
	private CANTalon frontRightMotor;
	private CANTalon backLeftMotor;
	private CANTalon backRightMotor;
	
	MecanumDrive(int frontLeftCANId, int frontRightCANId, int backLeftCANId, int backRightCANId){
		this.frontLeftMotor = new CANTalon(frontLeftCANId);
		this.frontRightMotor = new CANTalon(frontRightCANId);
		this.backLeftMotor = new CANTalon(backLeftCANId);
		this.backRightMotor = new CANTalon(backRightCANId);
	}
	
	public void drive(){
		
	}
}

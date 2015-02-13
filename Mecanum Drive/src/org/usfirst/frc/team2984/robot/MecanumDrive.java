package org.usfirst.frc.team2984.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MecanumDrive extends RobotDrive {
	
	//The motors
	private CANTalon frontLeftMotor;
	private CANTalon frontRightMotor;
	private CANTalon backLeftMotor;
	private CANTalon backRightMotor;
	
	MecanumDrive(int frontLeftCANId, int frontRightCANId, int backLeftCANId, int backRightCANId){
		super(new CANTalon(frontLeftCANId), new CANTalon(backLeftCANId), new CANTalon(frontRightCANId), new CANTalon(backRightCANId));
		this.frontLeftMotor = (CANTalon) this.m_frontLeftMotor;
		this.frontRightMotor = (CANTalon) this.m_frontRightMotor;
		this.backLeftMotor = (CANTalon) this.m_rearLeftMotor;
		this.backRightMotor = (CANTalon) this.m_rearRightMotor;
	}
	
	//Foward -1 to 1, right -1 to 1, rotate 0 to 2PI
	public void drive(double foward, double right, double rotate){
		double power = Math.min(Math.sqrt(foward * foward + (right*right)), 1);
		double angle;
		//Checks if right is 0
		if(right == 0.0){
			if(foward > 0){
				angle = -Math.PI;
			} else {
				angle = 0;
			}
		} else {
			angle = Math.atan(foward/right)-Math.PI/2;
		}
		//Calculates power for each motor
		double frontLeftPower = power*Math.sin(angle + (Math.PI/4))+rotate;
		double frontRightPower = power*Math.cos(angle + (Math.PI/4))-rotate;
		double backLeftPower = power*Math.cos(angle + (Math.PI/4))+rotate;
		double backRightPower = power*Math.sin(angle + (Math.PI/4))-rotate;
		SmartDashboard.putNumber("Drive Angle", angle);
		
		//sets power for each motor
		this.frontLeftMotor.set(frontLeftPower);
		this.frontRightMotor.set(frontRightPower);
		this.backLeftMotor.set(backLeftPower);
		this.backRightMotor.set(backRightPower);
		
	}
	public void coast(boolean coast){
		this.frontLeftMotor.enableBrakeMode(coast);
		this.frontRightMotor.enableBrakeMode(coast);
		this.backLeftMotor.enableBrakeMode(coast);
		this.backRightMotor.enableBrakeMode(coast);
	}
	
	public void setVoltageRamp(double rate){
		this.frontLeftMotor.setVoltageRampRate(rate);
		this.frontRightMotor.setVoltageRampRate(rate);
		this.backLeftMotor.setVoltageRampRate(rate);
		this.backRightMotor.setVoltageRampRate(rate);
	}
	public void update(){
		this.setValues(this.frontLeftMotor, "Front Left Motor");
		this.setValues(this.frontRightMotor, "Front Right Motor");
		this.setValues(this.backLeftMotor, "Back Left Motor");
		this.setValues(this.backRightMotor, "Back Right Motor");
	}
	private void setValues(CANTalon talon, String nameOfTalon){
		SmartDashboard.putNumber(nameOfTalon + " Bus Voltage", talon.getBusVoltage());
		SmartDashboard.putNumber(nameOfTalon + " Close Loop Ramp Rate", talon.getCloseLoopRampRate());
//		SmartDashboard.putNumber(nameOfTalon + " D", talon.getD());
//		SmartDashboard.putNumber(nameOfTalon + " F", talon.getF());
//		SmartDashboard.putNumber(nameOfTalon + " I", talon.getI());
//		SmartDashboard.putNumber(nameOfTalon + " P", talon.getP());
		SmartDashboard.putNumber(nameOfTalon + " Output Current", talon.getOutputCurrent());
		SmartDashboard.putNumber(nameOfTalon + " Output Voltage", talon.getOutputVoltage());
		SmartDashboard.putNumber(nameOfTalon + " Temp", talon.getTemp());
	}
}

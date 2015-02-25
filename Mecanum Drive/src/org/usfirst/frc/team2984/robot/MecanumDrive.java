package org.usfirst.frc.team2984.robot;

import java.util.HashMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MecanumDrive {
	
	private static final int NUMBER_OF_MOTORS = 4;
	private static final int VOLTAGE_RAMP_RATE_UP_INDEX = 0;
	private static final int VOLTAGE_RAMP_RATE_DOWN_INDEX = 1;
	
	//The motors
	private CANTalon frontLeftMotor;
	private CANTalon frontRightMotor;
	private CANTalon backLeftMotor;
	private CANTalon backRightMotor;
	private HashMap<CANTalon, Integer> invertedMotors = new HashMap<CANTalon, Integer>();
	private double[] voltageRampRates = new double[2];
	private double voltageRampRate = Double.MIN_VALUE;
	private double[] previousAbsoluteMotorSpeeds = new double[NUMBER_OF_MOTORS]; 
	
	MecanumDrive(int frontLeftCANId, int frontRightCANId, int backLeftCANId, int backRightCANId){
		this(new CANTalon(frontLeftCANId), new CANTalon(backLeftCANId), new CANTalon(frontRightCANId), new CANTalon(backRightCANId));
	}
	
	MecanumDrive(CANTalon frontLeftMotor, CANTalon frontRightMotor, CANTalon backLeftMotor, CANTalon backRightMotor){
		this.frontLeftMotor = frontLeftMotor;
		this.frontRightMotor = frontRightMotor;
		this.backLeftMotor = backLeftMotor;
		this.backRightMotor = backRightMotor;
		invertedMotors.put(this.frontLeftMotor, 1);
		invertedMotors.put(this.frontRightMotor, 1);
		invertedMotors.put(this.backLeftMotor, 1);
		invertedMotors.put(this.backRightMotor, 1);
		for(int i = 0; i<NUMBER_OF_MOTORS; i++){
			this.previousAbsoluteMotorSpeeds[i] = 0;
		}
	}
	
	/**
     *
     * @param x The speed that the robot should drive in the X direction. [-1.0..1.0]
     * @param y The speed that the robot should drive in the Y direction. [-1.0..1.0]
     * @param rotation The rate of rotation for the robot that is completely independent of
     * the translation. [-1.0..1.0]
     * @param gyroAngle The current angle reading from the gyro.  Use this to implement field-oriented controls. In radians.
     */
	public void drive(double x, double y, double rotation, double gyroAngle) {
        double xIn = x;
        double yIn = y;
        // Compenstate for gyro angle.
        double rotated[] = rotateVector(xIn, yIn, gyroAngle);
        xIn = rotated[0];
        yIn = rotated[1];

        double wheelSpeeds[] = new double[NUMBER_OF_MOTORS];
        wheelSpeeds[0] = xIn + yIn + rotation;
        wheelSpeeds[1] = -xIn + yIn + rotation;
        wheelSpeeds[2] = -xIn + yIn - rotation;
        wheelSpeeds[3] = xIn + yIn - rotation;

        normalize(wheelSpeeds);
        if(this.voltageRampRates[VOLTAGE_RAMP_RATE_UP_INDEX]!=-1 || this.voltageRampRates[VOLTAGE_RAMP_RATE_DOWN_INDEX]!=-1){
        	this.setVoltageRamp(this.caculateVotageRampRate(wheelSpeeds));
        }
        this.previousAbsoluteMotorSpeeds = absOfDoubleArray(wheelSpeeds);
        this.frontLeftMotor.set(wheelSpeeds[0] * invertedMotors.get(this.frontLeftMotor));
        this.frontRightMotor.set(wheelSpeeds[1] * invertedMotors.get(this.frontRightMotor));
        this.backLeftMotor.set(wheelSpeeds[2] * invertedMotors.get(this.backLeftMotor));
        this.backRightMotor.set(wheelSpeeds[3] * invertedMotors.get(this.backRightMotor));
    }
	
	/**
	 * sets the break mode to either coast(True) or break(False)
	 * @param coast
	 */
	public void setCoastMode(boolean coast){
		this.frontLeftMotor.enableBrakeMode(coast);
		this.frontRightMotor.enableBrakeMode(coast);
		this.backLeftMotor.enableBrakeMode(coast);
		this.backRightMotor.enableBrakeMode(coast);
	}
	
	/**
	 * sets the Ramp rate of the talons in amps per second. Anything lower that 1 will cancel the ramp
	 * @param rate
	 */
	public void setVoltageRamp(double rate){
		if(rate != this.voltageRampRate){
			this.frontLeftMotor.setVoltageRampRate(rate);
			this.frontRightMotor.setVoltageRampRate(rate);
			this.backLeftMotor.setVoltageRampRate(rate);
			this.backRightMotor.setVoltageRampRate(rate);
			this.voltageRampRate = rate;
		}
	}
	
	/**
	 * Sets the acceleration mode of the talons
	 * @param enabled enable or disable safeMode
	 * @param rampRateUp the rate in amps per second from [1,Infinity] that the talon ramps up. -1 will cause there to be no ramp
	 * @param rampRateDown the rate in amps per second from [1,Infinity] that the talon ramps down. -1 will cause there to be no ramp
	 */
	public void setSafeMode(boolean enabled, double rampRateUp, double rampRateDown){
		if(enabled){
			voltageRampRates[VOLTAGE_RAMP_RATE_UP_INDEX] = rampRateUp;
			voltageRampRates[VOLTAGE_RAMP_RATE_DOWN_INDEX] = rampRateDown;
			if(rampRateUp == rampRateDown){
				this.voltageRampRates[VOLTAGE_RAMP_RATE_UP_INDEX] = -1;
				this.voltageRampRates[VOLTAGE_RAMP_RATE_DOWN_INDEX] = -1;
				this.setVoltageRamp(rampRateUp);
			}
		} else {
			voltageRampRates[VOLTAGE_RAMP_RATE_UP_INDEX] = -1;
			voltageRampRates[VOLTAGE_RAMP_RATE_DOWN_INDEX] = -1;
			this.setVoltageRamp(-1);
		}
	}
	
	private double[] absOfDoubleArray(double input[]){
		double[] array = input.clone();
		for(int i = 0; i<array.length; i++){
			array[i] = Math.abs(array[i]);
		}
		return array;
	}
	
	/**
	 * Logs debug values to the dashboard
	 */
	public void update(){
		this.setValues(this.frontLeftMotor, "Front Left Motor");
		this.setValues(this.frontRightMotor, "Front Right Motor");
		this.setValues(this.backLeftMotor, "Back Left Motor");
		this.setValues(this.backRightMotor, "Back Right Motor");
	}
	
	/**
	 * sets Debug values of a talon
	 * @param talon talon to use for debug
	 * @param nameOfTalon an identifyer for the talon that will show up on the smart dashboard
	 */
	private void setValues(CANTalon talon, String nameOfTalon){
		SmartDashboard.putNumber(nameOfTalon + " Bus Voltage", talon.getBusVoltage());
		SmartDashboard.putNumber(nameOfTalon + " Close Loop Ramp Rate", talon.getCloseLoopRampRate());
		SmartDashboard.putNumber(nameOfTalon + " Output Current", talon.getOutputCurrent());
		SmartDashboard.putNumber(nameOfTalon + " Output Voltage", talon.getOutputVoltage());
		SmartDashboard.putNumber(nameOfTalon + " Temp", talon.getTemp());
	}
	
	/**
     * Normalize all wheel speeds if the magnitude of any wheel is greater than 1.0.
     */
    protected static void normalize(double wheelSpeeds[]) {
        double maxMagnitude = Math.abs(wheelSpeeds[0]);
        int i;
        for (i=1; i<NUMBER_OF_MOTORS; i++) {
            double asbsoluteWheelSpeed = Math.abs(wheelSpeeds[i]);
            if (maxMagnitude < asbsoluteWheelSpeed) maxMagnitude = asbsoluteWheelSpeed;
        }
        if (maxMagnitude > 1.0) {
            for (i=0; i<NUMBER_OF_MOTORS; i++) {
                wheelSpeeds[i] = wheelSpeeds[i] / maxMagnitude;
            }
        }
    }
    
    /**
     * finds the ramp rate average based on the motor speeds and up and down ramp rates
     */
    protected double caculateVotageRampRate(double wheelSpeeds[]){
    	double voltageRampRateAverage = 0;
    	int timesAveraged = 0;
        for(int i = 0; i<NUMBER_OF_MOTORS; i++){
        	if(Math.abs(wheelSpeeds[i])>this.previousAbsoluteMotorSpeeds[i]){
        		voltageRampRateAverage = ((voltageRampRateAverage * timesAveraged) + this.voltageRampRates[VOLTAGE_RAMP_RATE_UP_INDEX])/(timesAveraged+1);
        		timesAveraged++;
        	}
        	if(Math.abs(wheelSpeeds[i])<this.previousAbsoluteMotorSpeeds[i]){
        		voltageRampRateAverage = ((voltageRampRateAverage * timesAveraged) + this.voltageRampRates[VOLTAGE_RAMP_RATE_DOWN_INDEX])/(timesAveraged+1);
        		timesAveraged++;
        	}
        }
        return voltageRampRateAverage;
    }

    /**
     * Rotate a vector in Cartesian space.
     */
    protected static double[] rotateVector(double x, double y, double angle) {
        double cosA = Math.cos(angle * (Math.PI / 180.0));
        double sinA = Math.sin(angle * (Math.PI / 180.0));
        double out[] = new double[2];
        out[0] = x * cosA - y * sinA;
        out[1] = x * sinA + y * cosA;
        return out;
    }
}

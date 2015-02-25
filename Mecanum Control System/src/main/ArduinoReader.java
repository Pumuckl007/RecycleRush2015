package main;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ArduinoReader implements SerialPortEventListener {

	private static OutputWriter table;

	private static boolean isLoading, loadingStatusLightState,
			connectedLightState, errorLightState;

	private static Object syncer;

	SerialPort serialPort;

	private static final String PORT_NAMES[] = { "COM3", "COM4", "COM5", "/dev/ttyS82", "/dev/ttyS81", "/dev/ttyS83" };

	private static final Map<Integer, Double> knobToDirectionMap = new HashMap<Integer, Double>() {
		/**
		 * Generated serialVersionUID
		 */
		private static final long serialVersionUID = 572020344526700267L;

		{
			put(0b0000, -1.0);
			put(0b0001, -1.0);
			put(0b0011, -0.8);
			put(0b0010, -0.6);
			put(0b0110, -0.4);
			put(0b0111, -0.2);
			put(0b1111, 0.0);
			put(0b1110, 0.2);
			put(0b1010, 0.4);
			put(0b1011, 0.6);
			put(0b1001, 0.8);
			put(0b1000, 1.0);
		}
	};

	private BufferedReader input;
	private OutputStream output;
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 9600;

	public boolean initialize() {
		CommPortIdentifier portId = null;
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

		// First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum
					.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			WindowUpdater.exeption = "Could not find COM port. If on Linux create a sim link between /dev/ttyACM0 and /dev/ttyS81 and give read accsess (chmod 666) to /dev/ttyACM0";
			return false;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(
					serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return true;
	}

	/**
	 * This should be called when you stop using the port. This will prevent
	 * port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = input.readLine();

				String[] inputs = inputLine.trim().split(" ");

				switch (Integer.parseInt(inputs[0])) {
				case 0:
					table.putNumber("axis1", (Math.round(this.interpretYAxis(-Integer.parseInt(inputs[1]))*100))/100D);
					table.putNumber("axis2", (Math.round(this.interpretXAxis(-Integer.parseInt(inputs[2]))*100))/100D);
					interpretDigitalKnobData(inputs[3], inputs[4], inputs[5],
							inputs[6]);
					table.putBoolean("feederArm", inputs[7].equals("1"));
					table.putNumber("winder", Integer.parseInt(inputs[8]));
					table.putNumber("grabber", Integer.parseInt(inputs[9]));
					break;
				case 1:
					table.putBoolean("load", true);
					break;
				case 2:
					table.putBoolean("fire", true);
					break;

				}

			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other
		// ones.
	}

	private double interpretYAxis(double axis){
		if(Math.abs(axis)<5){
			return 0;
		}
		if(axis > 0){
			return axis/284;
		}
		return axis/235;
	}
	
	private double interpretXAxis(double axis){
		if(Math.abs(axis)<5){
			return 0;
		}
		if(axis > 0){
			return axis/293;
		}
		return axis/267;
	}
	
	private void interpretDigitalKnobData(String val1, String val2,
			String val3, String val4) {

		double val = knobToDirectionMap.get(Integer.parseInt(val1 + val2 + val3
				+ val4, 2));
		table.putNumber("yaw", val);

	}

	public void run(){
		try {
			ArduinoReader.main(new String[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {

		System.out.println("Starting...");

		isLoading = false;
		loadingStatusLightState = false;
		connectedLightState = false;
		errorLightState = false;

		syncer = new Object();

		OutputWriter.setClientMode();
		OutputWriter.setTeam(2984);
		table = OutputWriter.getTable("CustomData1");

		table.putBoolean("loading", false);
		table.putBoolean("loaded", false);
		table.putNumber("time", 0);

		final ArduinoReader main = new ArduinoReader();
		boolean didNotFail = main.initialize();
		if(didNotFail){
			reset(main);
	
			System.out.println("Started");
	
			new Thread() {
	
				public void run() {
	
					while (true) {
	
						try {
	
							while (isLoading) {
								setLoadingLight(main, !loadingStatusLightState);
								Thread.sleep(500);
							}
	
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
	
				}
	
			}.start();
	
			new Thread() {
	
				public void run() {
	
					while (true) {
						try {
							sendUpdate(main);
							Thread.sleep(2000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
	
			}.start();
	
			double lastTime = table.getNumber("time"), lastChange = System
					.currentTimeMillis();
	
			while (System.in.available() < 1) {
	
				try {
	
					if (lastTime != table.getNumber("time")) {
						lastTime = table.getNumber("time");
						lastChange = System.currentTimeMillis();
	
						setErrorLight(main, false);
						setConnectedLight(main, true);
	
					} else if (lastChange + 2000 < System.currentTimeMillis()) {
						setErrorLight(main, true);
						setConnectedLight(main, false);
					}
	
					isLoading = table.getBoolean("loading");
	
					if (!isLoading)
						setLoadingLight(main, table.getBoolean("loaded"));
	
					//sendUpdate(main);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	
			main.close();
	
			System.exit(0);
		}
	}

	private static void setLoadingLight(ArduinoReader main, boolean state)
			throws IOException {

		synchronized (syncer) {
			if (loadingStatusLightState != state) {
				main.output.write('#');
				main.output.write(3);
				main.output.write(state ? 1 : 0);
				loadingStatusLightState = state;
			}
		}
	}

	private static void setConnectedLight(ArduinoReader main, boolean state)
			throws IOException {
		synchronized (syncer) {
			if (connectedLightState != state) {
				main.output.write('#');
				main.output.write(1);
				main.output.write(state ? 1 : 0);
				connectedLightState = state;
			}
		}
	}

	private static void setErrorLight(ArduinoReader main, boolean state)
			throws IOException {
		synchronized (syncer) {

			if (errorLightState != state) {
				main.output.write('#');
				main.output.write(2);
				main.output.write(state ? 1 : 0);
				errorLightState = state;
			}
		}
	}

	private static void reset(ArduinoReader main) throws IOException {
		synchronized (syncer) {
			main.output.write('#');
			main.output.write(0);
			main.output.write(0);
			errorLightState = false;
			connectedLightState = false;
			loadingStatusLightState = false;
		}
	}

	private static void sendUpdate(ArduinoReader main) throws IOException {
		synchronized (syncer) {
			main.output.write('#');
			main.output.write(2);
			main.output.write(errorLightState ? 1 : 0);
			
			main.output.write('#');
			main.output.write(1);
			main.output.write(connectedLightState ? 1 : 0);
			
			main.output.write('#');
			main.output.write(3);
			main.output.write(loadingStatusLightState ? 1 : 0);
		}
	}
}
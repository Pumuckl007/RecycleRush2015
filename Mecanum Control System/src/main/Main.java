package main;

public class Main {
	public static void main(String[] args){
		System.out.println("Starting!");
		try {
			ArduinoReader.main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

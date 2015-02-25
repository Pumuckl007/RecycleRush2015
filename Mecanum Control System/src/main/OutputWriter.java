package main;

import java.util.HashMap;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class OutputWriter {
	public HashMap<String, Double> numbers = new HashMap<String, Double>();
	public HashMap<String, Boolean> booleans = new HashMap<String, Boolean>();
	private static HashMap<String,OutputWriter> instanses = new HashMap<String,OutputWriter>();
	private NetworkTable tabel;
	OutputWriter(NetworkTable tabel){
		this.tabel = tabel;
	}
	public void putNumber(String key, double value){
		numbers.put(key, value);
	}
	public void putBoolean(String key, boolean value){
		booleans.put(key, value);
	}
	public double getNumber(String key){
		return numbers.get(key);
	}
	public boolean getBoolean(String key){
		return booleans.get(key);
	}
	public static void setClientMode(){
		NetworkTable.setClientMode();
	}
	public static void setTeam(int number){
		NetworkTable.setTeam(number);
	}
	public static OutputWriter getTable(String name){
		System.out.println("Made " + name);
		OutputWriter writer = new OutputWriter(null);
		instanses.put(name, writer);
		return writer;
	}
	public static OutputWriter getWriter(String name){
		return instanses.get(name);
	}
	public static boolean hasInitilized(String name){
		return instanses.containsKey(name);
	}
}

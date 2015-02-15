package main;

import javax.swing.JTextPane;

public class WindowUpdater extends Thread {
	
	private JTextPane text;
	private OutputWriter writer;
	public static String exeption = "none";
	
	public void run(){
		this.writer = OutputWriter.getWriter("CustomData1");
		this.text = writer.getText();
		System.out.println("Starting Window Thread");
		while(true){
			String textInText = "";
			for(String id : this.writer.numbers.keySet()){
				textInText = textInText + id + " : "  + this.writer.getNumber(id) + "\n";
			}
			for(String id : this.writer.booleans.keySet()){
				textInText = textInText + id + " : " + this.writer.getBoolean(id) + "\n";
			}
			text.setText(textInText);
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!exeption.equals("none")){
				text.setText(exeption);
				break;
			}
		}
		System.out.println("Window Thread Shutting Down");
	}
	
}
package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class ConnectionIndicator extends JComponent{

	private static final long serialVersionUID = -5385133776832672049L;
	private boolean connected = false;
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Paint colorPaint = new Color(connected ? 0x00FF00 : 0xFF0000);
		g2.setPaint(colorPaint);
		g2.fillOval(10, 3, this.getHeight()-4, this.getHeight()-4);
		g2.setPaint(new Color(0));
		g2.drawOval(10, 2, this.getHeight()-4, this.getHeight()-4);
		g2.drawString(connected ? "Connected" : "Not Connected", 60, 24);
	}
	
	public void setConnected(boolean connected){
		this.connected = connected;
		this.repaint();
	}
	
	public boolean isConnected(){
		return this.connected;
	}
	
}

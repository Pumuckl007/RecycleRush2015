package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.JButton;

public class TwoColorButton extends JButton{
	private static final long serialVersionUID = 6069677968953768389L;
	private Paint paint1;
	private Paint paint2;
	private double percent;
    private TwoColorButton(Paint paint1, Paint paint2){
        super();
        this.paint1 = paint1;
        this.paint2 = paint2;
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setPaint(this.paint1);
        g2.fillRect(0, 0, (int)(getWidth()*this.percent), getHeight());
        g2.setPaint(this.paint2);
        g2.fillRect((int)(getWidth()*this.percent), 0, getWidth(), getHeight());
        g2.dispose();

        super.paintComponent(g);
    }
    
    public void setPercentage(double percent){
    	this.percent = percent;
    	this.repaint();
    }

    public static final TwoColorButton newInstance(Paint paint1, Paint paint2){
        return new TwoColorButton(paint1, paint2);
    }
}

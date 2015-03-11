package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class WindowUpdater extends Thread {
	
	private OutputWriter writer;
	public static String exeption = "none";
	private SpringLayout layout;
	private static int NUMBER_OF_BUTTONS = 5;
	private int lastIndexSelectedForLocationsTable = 0;
	
	public void run(){
		System.out.println("Starting Window Thread");

		JFrame frame = new JFrame("Team 2984 Custom Controler Reader");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(this.layout = new SpringLayout());
		Container contentPane = frame.getContentPane();

		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setFont(new Font("Arial", 0, 10));
		text.setText("Starting!");
		this.layout.putConstraint(SpringLayout.SOUTH, text, 0, SpringLayout.SOUTH, contentPane);
		this.layout.putConstraint(SpringLayout.EAST, text, 0, SpringLayout.EAST, contentPane);
		this.layout.putConstraint(SpringLayout.NORTH, text, 0, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.WEST, text, -120, SpringLayout.EAST, contentPane);

		contentPane.add(text);
		
		JComponent[] buttons = this.addButtons(contentPane);

		frame.setSize(700, 600);

		frame.setVisible(true);
		
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		this.writer = OutputWriter.getWriter("CustomData1");
		if(this.writer == null){
			this.writer = OutputWriter.getTable("CustomData1");
			this.writer.putBoolean("StartedFake", true);
		}
		double lastPullInTote = 0;
		while(true){
			String textInText = "";
			for(String id : this.writer.numbers.keySet()){
				textInText = textInText + id + " : "  + this.writer.getNumber(id) + "\n";
			}
			for(String id : this.writer.booleans.keySet()){
				textInText = textInText + id + " : " + this.writer.getBoolean(id) + "\n";
			}
			for(String id : this.writer.strings.keySet()){
				textInText = textInText + id + " : " + this.writer.getString(id) + "\n";
			}
			text.setText(textInText);
			if(lastIndexSelectedForLocationsTable  != this.writer.getNumber("Index")){
				buttons[4].repaint();
				this.lastIndexSelectedForLocationsTable = (int) this.writer.getNumber("Index");
			}
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(this.writer.getNumber("Pull In Tote") != lastPullInTote){
				double pulledIn = this.writer.getNumber("Pull In Tote");
				lastPullInTote = pulledIn;
				TwoColorButton button = ((TwoColorButton)buttons[3]);
				button.setPercentage(pulledIn);
			}
//			if(!exeption.equals("none")){
//				text.setText(exeption);
//				break;
//			}
		}
//		System.out.println("Window Thread Shutting Down");
	}
	
	private JComponent[] addButtons(Container contentPane){
		OutputWriter writer = OutputWriter.getWriter("CustomData1");
		JComponent[] components = new JComponent[NUMBER_OF_BUTTONS];
		writer.putBoolean("Drive Motor Break Mode", true);
		JButton breakMode = this.generateButton("Drive Motor Break Mode: ", "Coast", new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				JButton button = (JButton) event.getSource();
				OutputWriter writer = OutputWriter.getWriter("CustomData1");
				writer.putBoolean("Drive Motor Break Mode", !writer.getBoolean("Drive Motor Break Mode"));
				if(writer.getBoolean("Drive Motor Break Mode")){
					button.setBackground(new Color(0x00FF00));
					button.setText("Drive Motor Break Mode: Coast");
				} else {
					button.setBackground(new Color(0xFF0000));
					button.setText("Drive Motor Break Mode: Break");
				}
			}
			
		});
		breakMode.setBackground(new Color(0x00FF00));
		this.layout.putConstraint(SpringLayout.SOUTH, breakMode, 40, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.NORTH, breakMode, 0, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.WEST, breakMode, 0, SpringLayout.WEST, contentPane);
		this.layout.putConstraint(SpringLayout.EAST, breakMode, 290, SpringLayout.WEST, contentPane);
		contentPane.add(breakMode);
		components[0] = breakMode;
		
		//Inverted Control
		writer.putBoolean("Inverted Drive Control", false);
		JButton controlMode = this.generateButton("Normal Control Mode", "", new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				JButton button = (JButton) event.getSource();
				OutputWriter writer = OutputWriter.getWriter("CustomData1");
				writer.putBoolean("Inverted Drive Control", !writer.getBoolean("Inverted Drive Control"));
				if(writer.getBoolean("Inverted Drive Control")){
					button.setBackground(new Color(0xFF0000));
					button.setText("Inverted Control Mode");
				} else {
					button.setBackground(new Color(0x00FF00));
					button.setText("Normal Control Mode");
				}
			}
			
		});
		controlMode.setBackground(new Color(0x00FF00));
		this.layout.putConstraint(SpringLayout.SOUTH, controlMode, 40, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.NORTH, controlMode, 0, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.WEST, controlMode, 290, SpringLayout.WEST, contentPane);
		this.layout.putConstraint(SpringLayout.EAST, controlMode, -120, SpringLayout.EAST, contentPane);
		contentPane.add(controlMode);
		components[1] = controlMode;
		
		//Speed Regulator
		JLabel speedRegulatorLabel = new JLabel("Speed Regulator", SwingConstants.CENTER);
		this.layout.putConstraint(SpringLayout.SOUTH, speedRegulatorLabel, 60, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.NORTH, speedRegulatorLabel, 40, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.WEST, speedRegulatorLabel, 0, SpringLayout.WEST, contentPane);
		this.layout.putConstraint(SpringLayout.EAST, speedRegulatorLabel, 290, SpringLayout.WEST, contentPane);
		contentPane.add(speedRegulatorLabel);
		
		writer.putNumber("Speed Regulation", 1);
		JSlider speedRegulator = new JSlider(JSlider.HORIZONTAL,
                0, 100, 100);
		speedRegulator.addChangeListener(new ChangeListener(){
			private long time = System.currentTimeMillis();
			@Override
			public void stateChanged(ChangeEvent event) {
				JSlider slider = (JSlider) event.getSource();
				if(!slider.getValueIsAdjusting() || this.time+20 < System.currentTimeMillis()){
					this.time = System.currentTimeMillis();
					int scaleValue = slider.getValue();
			    	OutputWriter writer = OutputWriter.getWriter("CustomData1");
			    	writer.putNumber("Speed Regulation", scaleValue/100D);
				}
			}
			
		});
		
		//Turn on labels at major tick marks.
		speedRegulator.setMajorTickSpacing(20);
		speedRegulator.setMinorTickSpacing(5);
		speedRegulator.setPaintTicks(true);
		speedRegulator.setPaintLabels(true);
		
		this.layout.putConstraint(SpringLayout.SOUTH, speedRegulator, 110, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.NORTH, speedRegulator, 60, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.WEST, speedRegulator, 0, SpringLayout.WEST, contentPane);
		this.layout.putConstraint(SpringLayout.EAST, speedRegulator, 290, SpringLayout.WEST, contentPane);
		contentPane.add(speedRegulator);
		components[2] = speedRegulator;
		
		//Pull in tote
		writer.putNumber("Pull In Tote", 0);
		TwoColorButton pullInTote = this.generateGradientButton("Pull In Tote", "", new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				OutputWriter writer = OutputWriter.getWriter("CustomData1");
				writer.putNumber("Pull In Tote", 1.005);
			}
			
		}, new Color(0x00FF00), new Color(0xFF0000));
		this.layout.putConstraint(SpringLayout.SOUTH, pullInTote, 110, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.NORTH, pullInTote, 40, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.WEST, pullInTote, 290, SpringLayout.WEST, contentPane);
		this.layout.putConstraint(SpringLayout.EAST, pullInTote, -120, SpringLayout.EAST, contentPane);
		contentPane.add(pullInTote);
		components[3] = pullInTote;
		
		//Locations to move to
		Object[][] location =  new Object[][]{{new Integer(40)}};
		JTable locations = new JTable(new DefaultTableModel(location, new String[]{"Position"}){

			private static final long serialVersionUID = 7427939936223064483L;
			
			public Class<Integer> getColumnClass(int c) {
		        return Integer.class;
		    }
			
		});
		locations.getModel().addTableModelListener(new TableModelListener(){

			@Override
			public void tableChanged(TableModelEvent event) {
				OutputWriter writer = OutputWriter.getWriter("CustomData1");
				String locations = "";
				DefaultTableModel model = (DefaultTableModel)event.getSource();
				for(int i = 0; i<model.getRowCount(); i++){
					locations = locations + model.getValueAt(i, 0) + ",";
				}
				writer.putString("Encoder Locations", locations);
			}
			
		});
		components[4] = locations;
		OutputWriter.getWriter("CustomData1").putNumber("Index", 1);
		locations.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 3544108559208013281L;
			Color backgroundColor = getBackground();

	        @Override
	        public Component getTableCellRendererComponent(
	            JTable table, Object value, boolean isSelected,
	            boolean hasFocus, int row, int column) {
	            Component c = super.getTableCellRendererComponent(
	                table, value, isSelected, hasFocus, row, column);
	            OutputWriter writer = OutputWriter.getWriter("CustomData1");
	            if (row == ((int)writer.getNumber("Index"))) {
	                c.setBackground(Color.red);
	            } else if (!isSelected) {
	                c.setBackground(backgroundColor);
	            }
	            return c;
	        }
	    });
		
		JScrollPane locationsScroll = new JScrollPane(locations);
		this.layout.putConstraint(SpringLayout.SOUTH, locationsScroll, 300, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.NORTH, locationsScroll, 150, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.WEST, locationsScroll, 0, SpringLayout.WEST, contentPane);
		this.layout.putConstraint(SpringLayout.EAST, locationsScroll, 290, SpringLayout.WEST, contentPane);
		contentPane.add(locationsScroll);
		
		//add location
		JButton addLocation = new JButton("New", UIManager.getIcon("FileView.fileIcon"));
		addLocation.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				((DefaultTableModel)locations.getModel()).addRow(new Integer[]{0});
				OutputWriter.getWriter("CustomData1").putNumber("Number of values", 
						OutputWriter.getWriter("CustomData1").getNumber("Number of values") + 1);
			}
			
		});
		this.layout.putConstraint(SpringLayout.SOUTH, addLocation, 150, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.NORTH, addLocation, 110, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.WEST, addLocation, 0, SpringLayout.WEST, contentPane);
		this.layout.putConstraint(SpringLayout.EAST, addLocation, 130, SpringLayout.WEST, contentPane);
		contentPane.add(addLocation);
		
		//remove location
		JButton removeLocation = new JButton("Remove", UIManager.getIcon("OptionPane.errorIcon"));
		removeLocation.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(locations.getSelectedRow() != -1){
					((DefaultTableModel)locations.getModel()).removeRow(locations.getSelectedRow());
					OutputWriter.getWriter("CustomData1").putNumber("Number of values", 
							OutputWriter.getWriter("CustomData1").getNumber("Number of values") - 1);
				}
			}
			
		});
		this.layout.putConstraint(SpringLayout.SOUTH, removeLocation, 150, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.NORTH, removeLocation, 110, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.WEST, removeLocation, 130, SpringLayout.WEST, contentPane);
		this.layout.putConstraint(SpringLayout.EAST, removeLocation, 289, SpringLayout.WEST, contentPane);
		contentPane.add(removeLocation);
		
		//Connection Indicator
		ConnectionIndicator indicator = new ConnectionIndicator();
		this.layout.putConstraint(SpringLayout.SOUTH, indicator, 0, SpringLayout.SOUTH, contentPane);
		this.layout.putConstraint(SpringLayout.NORTH, indicator, -40, SpringLayout.SOUTH, contentPane);
		this.layout.putConstraint(SpringLayout.WEST, indicator, 0, SpringLayout.WEST, contentPane);
		this.layout.putConstraint(SpringLayout.EAST, indicator, -120, SpringLayout.EAST, contentPane);
		contentPane.add(indicator);
		
		ConnectionSetter setter = new ConnectionSetter();
		setter.setIndicator(indicator);
		setter.start();
		
		return components;
	}
	
	private JButton generateButton(String text, String value, ActionListener listener){
		JButton button = new JButton();
		button.setText(text + " " + value);
		button.addActionListener(listener);
		button.setFocusable(false);
		return button;
	}
	
	private TwoColorButton generateGradientButton(String text, String value, ActionListener listener, Paint paint1, Paint paint2){
		TwoColorButton button = TwoColorButton.newInstance(paint1, paint2);
		button.setText(text + " " + value);
		button.addActionListener(listener);
		button.setFocusable(false);
		return button;
	}
	
}

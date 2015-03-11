package main;


public class ConnectionSetter extends Thread{
	@SuppressWarnings("unused")
	private ConnectionIndicator indicator;
	private OutputWriter writer;
	private int time = 0;
	public void setIndicator(ConnectionIndicator indicator){
		this.indicator = indicator;
		OutputWriter.getWriter("CustomData1").putNumber("Number of values", 1);

	}
	public void run(){
		super.run();
		this.writer = OutputWriter.getWriter("CustomData1");
		while(true){
			if(this.writer.getNumber("Pull In Tote")>0){
				this.writer.putNumber("Pull In Tote", ((int)(this.writer.getNumber("Pull In Tote")*1000)-2)/1000D);
			}
			time ++;
			if(time > 100){
				OutputWriter.getWriter("CustomData1").putNumber("Index", 
						(OutputWriter.getWriter("CustomData1").getNumber("Index") + 1)%OutputWriter.getWriter("CustomData1").getNumber("Number of values"));
				time = 0;
			}
			try {
				Thread.sleep(1000L/60L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

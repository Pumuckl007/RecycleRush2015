package main;


public class ConnectionSetter extends Thread{
	@SuppressWarnings("unused")
	private ConnectionIndicator indicator;
	private OutputWriter writer;
	public void setIndicator(ConnectionIndicator indicator){
		this.indicator = indicator;
	}
	public void run(){
		super.run();
		this.writer = OutputWriter.getWriter("CustomData1");
		while(true){
			if(this.writer.getNumber("Pull In Tote")>0){
				this.writer.putNumber("Pull In Tote", ((int)(this.writer.getNumber("Pull In Tote")*1000)-2)/1000D);
			}
			try {
				Thread.sleep(1000L/60L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

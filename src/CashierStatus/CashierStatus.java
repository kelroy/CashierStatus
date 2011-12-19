package CashierStatus;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JLabel;
import javax.swing.JPanel;

import GUITools.TriStateIndicator;
import cashier.exceptions.connection.ConnectionDisabledException;
import cashier.net.Connection;


public class CashierStatus extends JPanel implements Runnable{
	
	private TriStateIndicator internetStatusIndicator = new TriStateIndicator();
	private TriStateIndicator cashierStatusIndicator = new TriStateIndicator();
	private JPanel internetStatusPanel, cashierStatusPanel;
	private GridLayout internetStatusGridLayout = new GridLayout(0, 2);
	private GridLayout cashierStatusGridLayout = new GridLayout(0, 2);
	
	private FlowLayout flow = new FlowLayout();
	private Thread runner;
	
	public CashierStatus(){
		this.setLayout(this.flow);
		//this.flow.addLayoutComponent("internetStatus", this.internetStatusIndicator);
		//this.flow.addLayoutComponent("casheirStatus", this.cashierStatusIndicator);
		this.internetStatusPanel = new JPanel();
		this.internetStatusPanel.setLayout(this.internetStatusGridLayout);
		this.internetStatusPanel.add(new JLabel("Internet\nStatus"));
		this.internetStatusPanel.add(this.internetStatusIndicator);
		
		this.cashierStatusPanel = new JPanel();
		this.cashierStatusPanel.setLayout(this.cashierStatusGridLayout);
		this.cashierStatusPanel.add(new JLabel("Cashier Server Status"));
		this.cashierStatusPanel.add(this.cashierStatusIndicator);
		
		
		this.add(this.internetStatusPanel);
		this.add(this.cashierStatusPanel);
		this.setVisible(true);
		this.setIndicators();
		this.start();
	};
	
	//returns true if there is an active connection to the internet open.
	boolean internetAlive(){
		try{
			URL url = new URL("http://www.google.com");
			URLConnection connection = url.openConnection();
			return connection.getDate() > 0;
		}catch(Exception e){
			return false;
		}
	}
	
	boolean cashierAlive(){
		try {
			return cashier.net.Connection.connected();
		} catch (Exception e) {
			return false;
		}
	}
	
	void setIndicators(){
		if(this.internetAlive()){
			
			this.internetStatusIndicator.setState(TriStateIndicator.POSITIVE);
			if(this.cashierAlive()){
				this.cashierStatusIndicator.setState(TriStateIndicator.POSITIVE);
			}else{
				this.cashierStatusIndicator.setState(TriStateIndicator.NEGATIVE);
			}
		}else{
			this.internetStatusIndicator.setState(TriStateIndicator.NEGATIVE);
			this.cashierStatusIndicator.setState(TriStateIndicator.NEUTRAL);
		}
		
	}
	
	//thread methods
		public void start(){
			if(this.runner == null) this.runner = new Thread(this);
			this.runner.start();
		}
		
		public void run(){
			while (runner == Thread.currentThread()){
				this.setIndicators();
					try{
						Thread.sleep(10000);
					}catch(InterruptedException e){
		                   //log error
					}
			}
		}
	
}

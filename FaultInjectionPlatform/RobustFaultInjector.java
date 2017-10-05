package FaultInjectionPlatform;

import java.io.IOException;
import java.util.ArrayList;

import unipi.sevax.utilities.Utilities;

//import java.io.IOException;

//import unipi.sevax.utilities.Utilities;


public class RobustFaultInjector extends FaultInjector {

	int comA_ID;
	boolean jtag;
	private SerialNew controlSerial;
	public RobustFaultInjector(
							int percent, 
							boolean percentBoolean,
							int numberInject,
							ArrayList<Integer> frames,
//							String fullBitstream, 
//							String maskBitstream,
//							String networkType,
//							int numOfVoters,
//							ArrayList<String> voterList,
							SerialNew injectionSerial,
							//SerialNew controlSerial,
							Logger logger,
							int comA_ID,
							int expected,
							int fmax,
							int latency,
							boolean jtag
			) {
		super(percent, percentBoolean, numberInject, frames, injectionSerial, logger, expected, fmax, latency);
		//this.controlSerial = controlSerial;
		this.comA_ID = comA_ID;
		this.jtag = jtag;
		super.injectionSerial = setupSerial(this.comA_ID);
	}
	
	public boolean randomInjectionCycle() {
		boolean done = false;
		while (done == false) {
			//powerCycle();
			if (jtag == true) {
				reconfigureJtag();
			} else {
				pressPROG();
			}
			
			//pressPROG();
			//waitTime(1000);
//			if (super.initVoters() == true) {
				//System.out.println("Successfully init voters");
				long startTime = System.currentTimeMillis();
				done = super.randomInjectionCycle();
				long endTime = System.currentTimeMillis();
				System.out.println("\nCycle Time Interval " + (endTime - startTime)/60000.0 + " Minutes");
				super.logger.filePrintln("\nCycle Time Interval " + (endTime - startTime)/60000.0 + " Minutes");
//			} else {
//				System.out.println("Failed to init voters");
//			}
		}
		//powerDown();
		super.injectionSerial.disconnect();
		//controlSerial.disconnect();
		return true;
	}
	
	public static SerialNew setupSerial(int com) {
		SerialNew serial = new SerialNew("COM" + com, 115200);
		serial.connect();
		serial.initListeners();
		return serial;
	}
	
	public void powerUp() {
		System.out.println("Powering Up");
		super.logger.filePrintln("Powering Up");
		//controlSerial.writeString("p");
		controlSerial.RTS(false);
	}
	
	public void powerDown() {
		System.out.println("Powering Down");
		super.logger.filePrintln("Powering Down");
		//controlSerial.writeString("q");
		controlSerial.RTS(true);
		waitTime(200);
	}
	
	public void powerCycle() {
		powerDown();
		powerUp();
		waitTime(2000);
	}
	
	public void switchConfig(String configID) {
		System.out.println("Switching to Configuration " + configID);
		super.logger.filePrintln("Switching to Configuration " + configID);
		controlSerial.writeString(configID);
		waitTime(500);
	}

	private void waitTime(int millis) {
		try {
		    Thread.sleep(millis);
		} catch(InterruptedException e) {
		    Thread.currentThread().interrupt();
		}
	}
	
	private void pressPROG(){
		controlSerial.RTS(true);
		waitTime(200);
		controlSerial.RTS(false);
		waitTime(3200);
	}
	
	private void reconfigureJtag(){ // The is a batch file that I run to configure the FPGA. Located in RapidsmithFolder/download.bat
		System.out.print("\nConfiguring FPGA...");
		try {
			Utilities.configure("ISE");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print(" DONE\n");
	}
}

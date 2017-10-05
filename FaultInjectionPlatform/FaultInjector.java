package FaultInjectionPlatform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import fccmDemo.CountTimerGUI;


public class FaultInjector {
	//private double percent;
	private final int BitsInFrame = 3232; 
	private int numToInject;
	private int numInjected;
	private int totalTries;
	private int dutTotalErrors;
	private int feedbackErrors;
	private int softErrors;
	private int unrecoverableErrors;
	private  int noResponse;
	private int expected;
//	private int counter = 0;
	//private int fmax;
	//private int latency;
	private int timeout;
	private ArrayList<Integer> frames;
//	private String networkType;
//	private int numOfVoters;
//	private ArrayList<String> voterList;
	protected SerialNew injectionSerial;
	private Random PRG;
	protected Logger logger;
//	private  EBD_analysis ebd;
	private char[] noerror_reply = {'X', 'Y', 'Z'};
	private char[] error_reply = {'U', 'V', 'W'};
	private char[] harderror_reply = {'R', 'S', 'T'};
	private int reply_cycle;
	public CountTimerGUI gui;
	
	//private ArrayList<Integer> columnsNotClb;
	private HashMap<Integer, String> columns = new HashMap<>();
	private ArrayList<Integer> BRAMColumns;
	private ArrayList<Integer> DSPColumns;
	private ArrayList<Integer> IOColumns;
	private ArrayList<Integer> ClockInterColumns; // Clock interconnection resources
	private ArrayList<Integer> ConfigColumns; // ICAP etc.
	private ArrayList<Integer> ClockManagementColumns; // ICAP etc.
	
	public FaultInjector(
						int percent, 
						boolean percentBoolean,
						int numberInject,
						ArrayList<Integer> frames,
//						String networkType,
//						int numOfVoters,
//						ArrayList<String> voterList,
						SerialNew injectionSerial,
						Logger logger,
						int expected,
						int fmax,
						int latency		
			) {
		
		this.BRAMColumns = new ArrayList<>(Arrays.asList(6, 17, 28, 40, 51, 58, 69, 88, 99));
		this.DSPColumns = new ArrayList<>(Arrays.asList(9, 14, 31, 43, 48, 61, 66, 91, 96));
		this.IOColumns = new ArrayList<>(Arrays.asList(0, 105));
		this.ClockInterColumns = new ArrayList<>(Arrays.asList(55));
		this.ConfigColumns = new ArrayList<>(Arrays.asList(24));
		this.ClockManagementColumns = new ArrayList<>(Arrays.asList(1,104));
		
		this.expected = expected;
		//this.fmax = fmax;
		//this.latency = latency;
		//this.percent = percent;
		this.numToInject = 0;
		this.numInjected = 0;
		this.totalTries = 0;
		this.dutTotalErrors = 0;
		this.softErrors = 0;
		this.feedbackErrors = 0;
		this.unrecoverableErrors = 0;
		this.noResponse = 0;
		this.frames = frames;
//		this.networkType = networkType;
//		this.numOfVoters = numOfVoters;
//		this.voterList = voterList;
		this.injectionSerial = injectionSerial;
		this.PRG = new Random(System.currentTimeMillis());
		this.logger = logger;
		//Analyse the EBD sensitive bit map
//		this.ebd = new EBD_analysis(this.fullBitstream, logger);
//		ebd.loadBitStream();
//		ebd.loadEBD();
//		ebd.getResults();
//		ebd.createDUTSensitiveBits();
		this.numToInject = numberInject; 
//		if (percentBoolean == true) {
//			numToInject = (int) ( ebd.getSDutBits() * ((double) (percent/100.)));
//		}
		
		this.timeout = (int) ( ((1.0 / (double) fmax ) * latency ) + 5);
		
//		logger.filePrint("\nNumber of DUT sensitive bits = " + ebd.getSDutBits());
		logger.filePrint("\nInjecting in " + numberInject + " DUT sensitive bits = " + numToInject);
		logger.filePrint("\nMicroblaze timeout = " + timeout + " us");
//		System.out.print("\nNumber of DUT sensitive bits = " + ebd.getSDutBits());
		System.out.print("\nInjecting in " + numberInject + " DUT sensitive bits = " + numToInject);
		System.out.print("\nMicroblaze timeout = " + timeout + " us");
		gui = new CountTimerGUI();
		gui.setBenchmarkLabel(FaultInjectionPlatform.getBenchmarkName());
		gui.setFault(0);
		gui.setInjected(0);
		//gui.setPercentageLabel( 0.0 );
	}
	
//	public boolean initVoters() {
//		if (parseSerialForXResponse() == false) {
//			return false;
//		}
//		System.out.println("SENDING VOTERS");
//		logger.filePrintln("");
//		logger.filePrintln("SENDING VOTERS");
//		injectionSerial.writeString(this.networkType + " " +  this.numOfVoters + "\r\n");
//		if (this.numOfVoters != voterList.size()) {
//			System.err.println("Voter list does not match number of voters.");
//			System.exit(1);
//		}
//		for (int i = 0 ; i < this.numOfVoters; i++) {
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				Thread.currentThread().interrupt();
//			}
//			injectionSerial.writeString(voterList.get(i).toString() + "\r\n");
//		}
//		System.out.println("Waiting for X Response");
//		if (parseSerialForXResponse() == false) {
//			return false;
//		}
//		return true;
//	}
	
	public boolean randomInjectionCycle() {
		//FrameExtractor extractor = new FrameExtractor();
		//ArrayList<Integer> frames = extractor.getFrames(this.maskBitstream);
		
		//ebd.createDUTSensitiveBits();
		
/*		XilinxConfigurationSpecification 	spec;
		spec = DeviceLookup.lookupPartV4V5V6V7withPackageName("XC7A200T");
		FrameAddressRegister far = new FrameAddressRegister(spec);
		BlockSubType block = null;
		block = spec.getBlockSubtype(spec, far);
		boolean SRFrame = false;
		if ( (block.toString() == "CLB") == true ) {
			SRFrame = true;
		}*/
		
		this.reply_cycle = 0;
		
		injectionSerial.writeString("I");
		injectionSerial.writeDataInteger(expected);
		injectionSerial.writeDataInteger(timeout);
		
		if (parseSerialForFeedback("INIT") == false) {
			// If hard error or no response, immediately return false
			return false;
		}
		// Ready to inject
		injectionSerial.writeString("I");
		
		int reset_count = 0;
		while (numInjected < numToInject) {
			int frameAddress = frames.get(PRG.nextInt(frames.size()-1));
			//skip BRAMContent and clock/IO/CMT
			/*while( (checkIfBRAMContent(frameAddress) || checkIfClkIOCMT(frameAddress)) == true ) {
				frameAddress = frames.get(PRG.nextInt(frames.size()-1));
			}*/
			
			
			while( (checkIfBRAMContent(frameAddress) ) == true ) {
			frameAddress = frames.get(PRG.nextInt(frames.size()-1));
			}
			
			//long word = PRG.nextInt(100);
			//long bit = PRG.nextInt(31);
			
			int bitPosition = PRG.nextInt(BitsInFrame);
			long num = (((long) frameAddress) << 12) | bitPosition;
			//long num = (frame << 12) | (word << 5) | bit;
			String address = "";
	    	//String address = String.format("M" + "%010X", num);
	    	if( columnCheck(frameAddress)  == 6 ) {
	    		address = String.format("M" + "%010X", num); // Sent to microblaze the frame and bitposition to inject
	    	} else {
	    		address = String.format("N" + "%010X", num); // Sent to microblaze the frame and bitposition to inject
	    	}
			
			inject(address);
	    	numInjected++;
	    	
			if ((numInjected % 5) == 1) {
				//System.out.print("\r" + "# 0x" + String.format("%08X", frame) + "\t");
				//printProgBar(percentage(numInjected, numToInject));
				gui.setInjected(numInjected);
				gui.setPercentageLabel( ((double) dutTotalErrors/ numInjected) * 100 );
				System.out.printf('\r' + "%.2f", percentage(numInjected, numToInject));
			}
			if (parseSerialForFeedback(address) == false) {
				// If hard error or no response, immediately return false
				System.out.println(((frameAddress >> 23) & 0x7));
				return false;
			}
			/*reset_count++;
			if (reset_count == 10000) {
				System.out.println("Scheduled reset");
				return false;
			}*/
			
			System.out.print("");
		}
		// Stop Microblaze
		injectionSerial.writeString("S");
		System.out.println("");
		logger.filePrintln("");
		System.out.println("Injected " + numInjected + " Faults");
		logger.filePrintln("Injected " + numInjected + " Faults");
		
		
		logger.filePrintln("\nRaw Data");
		logger.filePrintln("dutTotalErrors = " + dutTotalErrors);
		logger.filePrintln("noResponse = " + noResponse);
		logger.filePrintln("feedbackErrors = " + feedbackErrors);
		logger.filePrintln("unrecoverableErrors = " + unrecoverableErrors);
		
		System.out.println("\nRaw Data");
		System.out.println("dutTotalErrors = " + dutTotalErrors);
		System.out.println("noResponse = " + noResponse);
		System.out.println("feedbackErrors = " + feedbackErrors);
		System.out.println("unrecoverableErrors = " + unrecoverableErrors);	
		
		
		/*********************************************************************************/
		softErrors = dutTotalErrors - (feedbackErrors + unrecoverableErrors);
		feedbackErrors = feedbackErrors - unrecoverableErrors;
		
		logger.filePrintln("\nAnalysis");
		logger.filePrintln("totalErrors = " +  (dutTotalErrors + noResponse) );
		logger.filePrintln("softErrors  = " +  softErrors);
		logger.filePrintln("feedbackErrors = " + feedbackErrors);
		logger.filePrintln("unrecoverableErrors = " + (unrecoverableErrors));
		logger.filePrintln("noResponse = " + noResponse);
		logger.filePrintln("%Error = " + (( (double) (dutTotalErrors + noResponse) / (double) numInjected)*100.0) );
		
		System.out.println("\nAnalysis");
		System.out.println("totalErrors = " +  (dutTotalErrors + noResponse) );
		System.out.println("softErrors  = " +  softErrors);
		System.out.println("feedbackErrors = " + feedbackErrors);
		System.out.println("unrecoverableErrors = " + (unrecoverableErrors));
		System.out.println("noResponse = " + noResponse);
		System.out.println("%Error = " + (( (double) (dutTotalErrors + noResponse) / (double) numInjected)*100.0) );
		/*********************************************************************************/
		
		/*totalErrors = totalErrors + noResponse;
		softErrors  = totalErrors - (feedbackErrors + unrecoverableErrors + noResponse);
		feedbackErrors = softErrors - (unrecoverableErrors + noResponse);
		unrecoverableErrors = unrecoverableErrors - noResponse;
		
		
		System.out.println("Soft Errors: " + softErrors);
		logger.filePrintln("Soft Errors: " + softErrors);
		System.out.println("Feedbackpath Errors: " + feedbackErrors);
		logger.filePrintln("Feedbackpath Errors: " + feedbackErrors);
		System.out.println("Unrecoverable Errors: " + (unrecoverableErrors + noResponse) );
		logger.filePrintln("Unrecoverable Errors: " + (unrecoverableErrors + noResponse) );*/
		return true;
	}
	
	public void inject(String address) {
		injectionSerial.writeString(address);
	}
	
	// Returns true if one of the command words are found
	private boolean parseSerialForFeedback(String address) {
		boolean xFound = false;
		while (true) {
			BufferTuple bufferTuple = injectionSerial.readBuffer();
			if (bufferTuple.timeout == true) {
				System.out.println("");
				System.out.println("No Response");
				logger.filePrintln("No Response");
				noResponse++;
				return false;
			}
			String buffer = bufferTuple.string;
			for (char c : buffer.toCharArray()) {
				if (c == noerror_reply[reply_cycle]) {
					xFound = true;
//					counter = 0;
					this.reply_cycle = (this.reply_cycle+1)%3;
				} else if (c == error_reply[reply_cycle]) {
					System.out.println("");
					System.out.println("--ERROR DETECTED--");
					System.out.println(address);
					logger.filePrintln("Error: " + address + "        On Try: " + totalTries);
					dutTotalErrors++;
					gui.setFault(dutTotalErrors + unrecoverableErrors);
				} else if (c == harderror_reply[reply_cycle]) {
					System.out.println("");
					System.out.println("--ERROR = UNRECOVERABLE ERROR--");
					System.out.println(address);
					logger.filePrintln("Unrecoverable Error: " + address + "        On Try: " + totalTries);
					unrecoverableErrors++;
					return false;
				} else {
					System.out.print(c);
				}
			}
			if (xFound == true) {
				return true;
			}
		}
	}
	
//	private boolean parseSerialForXResponse() {
//		while (true) {
//			BufferTuple bufferTuple = injectionSerial.readBuffer();
//			if (bufferTuple.timeout == true) {
//				System.out.println("");
//				System.out.println("No Response");
//				logger.filePrintln("No Response");
//				return false;
//			}
//			String buffer = bufferTuple.string;
//			boolean xFound = false;
//			for (char c : buffer.toCharArray()) {
//				if (c == 'X') {
//					xFound = true;
//				} else {
//					System.out.print(c);
//				}
//			}
//			logger.filePrint(buffer);
//			if (xFound == true) {
//				return true;
//			}
//		}
//	}
	
	private float percentage(long value, long total) {
		return ( ((float) value * 100.0f) / (float) total);  
	}
	
/*	private void printProgBar(int percent) {
		StringBuilder bar = new StringBuilder("[");
	
	    for(int i = 0; i < 50; i++){
	        if ( i < (percent/2)){
	            bar.append("=");
	        } else if( i == (percent/2)) {
	            bar.append(">");
	        } else {
	            bar.append(" ");
	        }
	    }
	    bar.append("]   " + percent + "%");
	    System.out.print("\r" + bar);
	}*/
	
	/*private boolean checkIfCLBColumn(int frameAddress){
		frameAddress = (frameAddress & 0x001FF80) >> 7;
		if (columnsNotClb.contains(frameAddress)) {
			return false;
		}
		else {
			return true;
		}
	}*/
		
	private boolean checkIfClkIOCMT(int frameAddress){
		frameAddress = (frameAddress & 0x001FF80) >> 7;
		if ( (frameAddress == 55) || (frameAddress == 24) || (frameAddress == 0) || (frameAddress == 105) || (frameAddress == 1) || (frameAddress == 104) ) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkIfBRAMContent(int frameAddress){
		
		if ( ((frameAddress >> 23) & 0x7) == 1)  {
			return true;
		} else {
			return false;
		}
	}
	
	
	//Returns the following integers depending on what type of column it is. 
	// 0: BRAMColumns
	// 1: DSPColumns
	// 2: IOColumns;
	// 3: ClockInterColumns; // Clock interconnection resources
	// 4: ConfigColumns; // ICAP etc.
	// 5: ClockManagementColumns; // ICAP etc.
	// 6: CLBColumns; //
	private int columnCheck(int frameAddress) {
		int type = -1;
		frameAddress = (frameAddress & 0x001FF80) >> 7;
		if        (BRAMColumns.contains(frameAddress)){
			type = 0;
		} else if (DSPColumns.contains(frameAddress)) {
			type = 1;
		} else if (IOColumns.contains(frameAddress)) {
			type = 2;
		} else if (ClockInterColumns.contains(frameAddress)) {
			type = 3;
		} else if (ConfigColumns.contains(frameAddress)) {
			type = 4;
		} else if (ClockManagementColumns.contains(frameAddress)) {
			type = 5;
		} else {
			type = 6;
		}
		return type;
		
	}
	
	
			
		

}

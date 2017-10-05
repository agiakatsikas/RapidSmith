package FaultInjectionPlatform;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

//import java.util.ArrayList;
import edu.byu.ece.rapidSmith.bitstreamTools.examples.support.BitstreamOptionParser;
import joptsimple.OptionSet;
//import unipi.sevax.analysis.EBD_analysis;
//import unipi.sevax.utilities.Utilities;

public class FaultInjectionPlatform {
	
	private static String benchmarkName;
	
	private final static String FULL_BITSTREAM = "b";
	private final static String FULL_BITSTREAM_HELP =
			"Path to the full bitstream.";
	private final static String TOP_BITSTREAM = "t";
	private final static String TOP_BITSTREAM_HELP =
			"Path to the full bitstream.";
	private final static String MASK_BITSTREAM = "m";
	private final static String MASK_BITSTREAM_HELP =
			"Path to the mask bitstream.";
	private final static String BOARD_ID = "bi";
	private final static String BOARD_ID_HELP =
			"Board ID.";
	private final static String RANDOM_INJECTION_OPTION = "r";
	private final static String RANDOM_INJECTION_OPTION_HELP = 
		    "# of random faults";
	private final static String SERIAL_PORT_OPTION_A = "comA";
	private final static String SERIAL_PORT_OPTION_HELP_A = 
			"The serial port that will be used for fault injection.";
	private final static String EXPECTED_OPTION = "e";
	private final static String EXPECTED_OPTION_HELP = 
		    "The expected result of the circuit";
	private final static String FMAX_OPTION = "f";
	private final static String FMAX_OPTION_HELP = 
		    "The max frequency of the circuit given in MHz";
	private final static String LATENCY_OPTION = "l";
	private final static String LATENCY_OPTION_HELP = 
		    "The latency of the circuit given in # of cycles";
	private final static String DATE_OPTION = "d";
	private final static String DATE_OPTION_HELP = 
		    "The date that the benchmark was created";
	private final static String BENCHMARKNAME_OPTION = "n";
	private final static String BENCHMARKNAME_OPTION_HELP = 
		    "The date that the benchmark was created";
	//private final static String SERIAL_PORT_OPTION_B = "comB";
	//private final static String SERIAL_PORT_OPTION_HELP_B = 
	//		"The serial port that will be used for fault injection control.";
	//private final static String CONFIG_ID_OPTION = "c";
	//private final static String CONFIG_ID_OPTION_HELP = 
	//		"0 or 4";
	private final static String JTAG_OPTION = "j";
	private final static String JTAG_OPTION_HELP = 
			"configure with jtag the bitstream";
	private final static String PERCENT_OPTION = "p";
	private final static String PERCENT_OPTION_HELP = 
			"percent of essential bits to inject";
	private static Calendar cal = Calendar.getInstance();
	public static void main(String[] args) {

		// Setup Options Parser
		BitstreamOptionParser cmdLineParser = new BitstreamOptionParser();
		cmdLineParser.addInputBitstreamOption();
		cmdLineParser.addPartNameOption();
		cmdLineParser.addHelpOption();
		cmdLineParser.accepts(FULL_BITSTREAM, FULL_BITSTREAM_HELP).withRequiredArg().ofType(String.class);
		cmdLineParser.accepts(TOP_BITSTREAM, TOP_BITSTREAM_HELP).withRequiredArg().ofType(String.class);
		cmdLineParser.accepts(MASK_BITSTREAM, MASK_BITSTREAM_HELP).withRequiredArg().ofType(String.class);
		cmdLineParser.accepts(BOARD_ID, BOARD_ID_HELP).withRequiredArg().ofType(String.class);
		cmdLineParser.accepts(PERCENT_OPTION, PERCENT_OPTION_HELP).withOptionalArg().ofType(String.class);
		cmdLineParser.accepts(RANDOM_INJECTION_OPTION, RANDOM_INJECTION_OPTION_HELP).withOptionalArg().ofType(String.class);
		cmdLineParser.accepts(SERIAL_PORT_OPTION_A, SERIAL_PORT_OPTION_HELP_A).withRequiredArg().ofType(String.class);
		cmdLineParser.accepts(EXPECTED_OPTION, EXPECTED_OPTION_HELP).withRequiredArg().ofType(String.class);
		cmdLineParser.accepts(FMAX_OPTION, FMAX_OPTION_HELP).withRequiredArg().ofType(String.class);
		cmdLineParser.accepts(LATENCY_OPTION, LATENCY_OPTION_HELP).withRequiredArg().ofType(String.class);
		cmdLineParser.accepts(DATE_OPTION, DATE_OPTION_HELP).withRequiredArg().ofType(String.class);
		cmdLineParser.accepts(BENCHMARKNAME_OPTION, BENCHMARKNAME_OPTION_HELP).withRequiredArg().ofType(String.class);
		//cmdLineParser.accepts(SERIAL_PORT_OPTION_B, SERIAL_PORT_OPTION_HELP_B).withRequiredArg().ofType(String.class);
		//cmdLineParser.accepts(CONFIG_ID_OPTION, CONFIG_ID_OPTION_HELP).withRequiredArg().ofType(String.class);
		cmdLineParser.accepts(JTAG_OPTION, JTAG_OPTION_HELP).withOptionalArg().ofType(String.class);
		OptionSet options = cmdLineParser.parseArgumentsExitOnError(args);
		cmdLineParser.checkHelpOptionExitOnHelpMessage(options);
		
		// Get options
		
/*		if (options.has(RANDOM_INJECTION_OPTION) || options.has(PERCENT_OPTION)) {
			random = true;
		}*/
		boolean random = false;
		int numberInject = 0;
		boolean percentBoolean = false;
		if (options.has(RANDOM_INJECTION_OPTION) == true) {
			numberInject = cmdLineParser.getIntegerStringExitOnError(options, RANDOM_INJECTION_OPTION, 10, 10);
			random = true;
		} else if (options.has(PERCENT_OPTION) == true) {
			percentBoolean = true;
			random = true;
		} else {
			numberInject = 100;
			random = false;
		}
			
		
		int comA = cmdLineParser.getIntegerStringExitOnError(options, SERIAL_PORT_OPTION_A, 10, 1);
		int expected = cmdLineParser.getIntegerStringExitOnError(options, EXPECTED_OPTION, 10, 10);
		int fmax = cmdLineParser.getIntegerStringExitOnError(options, FMAX_OPTION, 10, 10);
		int latency = cmdLineParser.getIntegerStringExitOnError(options, LATENCY_OPTION, 10, 10);
		//int comB = cmdLineParser.getIntegerStringExitOnError(options, SERIAL_PORT_OPTION_B, 10, 1);
		int percent = cmdLineParser.getIntegerStringExitOnError(options, PERCENT_OPTION, 10, 10);
		
		boolean jtag = false;
		if (options.has(JTAG_OPTION) == true) {
			jtag = true;
		} 
		
		
		
		
		
		String fullBitstream = cmdLineParser.getStringExitOnError(options, FULL_BITSTREAM, "");
		String topBitstream = cmdLineParser.getStringExitOnError(options, TOP_BITSTREAM, "");
		String boardID = cmdLineParser.getStringExitOnError(options, BOARD_ID, "");
		String date = cmdLineParser.getStringExitOnError(options, DATE_OPTION, "");
		setBenchmarkName(cmdLineParser.getStringExitOnError(options, BENCHMARKNAME_OPTION, ""));
		
		String maskBitstream = cmdLineParser.getStringExitOnError(options, MASK_BITSTREAM, "");
		//String configID = cmdLineParser.getStringExitOnError(options, CONFIG_ID_OPTION, "");
		
		FrameExtractor extractor = new FrameExtractor();
		ArrayList<Integer> frames = extractor.getFrames(topBitstream);
		ArrayList<Integer> framesMask = extractor.getFrames(maskBitstream);
		frames.removeAll(framesMask);
		
		// Setup serial
		SerialNew serialA = null;
		//SerialNew serialB = setupSerial(comB);
		// Setup frames
		//ArrayList<Integer> frames = setupFramesSubtract(fullBitstream, maskBitstream);
		
		// Inject
		createDownloadBatch(fullBitstream, boardID);
		Logger logger = new Logger(fullBitstream.replace("download.bit", getBenchmarkName()));
		logger.filePrint("\ninjectionDate = " + cal.getTime()); 
		logger.filePrint("\nbenchmarkName = " + getBenchmarkName());
		logger.filePrint("\nbenchmarkDate = " + date);
		logger.filePrint("\nbenchmarkExpectedResult = " + expected);
		logger.filePrint("\nbenchmarkFmax = " + fmax + " MHz");
		logger.filePrint("\nbenchmarkLatency = " + latency + " clock cycles\n");
		
		System.out.print("\nInjectionDate = " + cal.getTime()); 
		System.out.print("\nBENCHMARK = " + getBenchmarkName());
		System.out.print("\nDATE = " + date);
		System.out.print("\nbenchmarkExpectedResult = " + expected);
		System.out.print("\nbenchmarkFmax = " + fmax + " MHz");
		System.out.print("\nbenchmarkLatency = " + latency + " clock cycles\n");
		
		
		RobustFaultInjector injector = new RobustFaultInjector(percent, percentBoolean, numberInject, frames,  serialA, /*serialB,*/ logger, comA, expected, fmax, latency, jtag);
		//RobustFaultInjector injector = new RobustFaultInjector(percent, percentBoolean, numberInject, frames,  serialA, /*serialB,*/ logger, comA, expected, fmax, latency, jtag);
//		FaultInjector injector = new FaultInjector(percent, fullBitstream, serialA, logger);
		//FaultInjector injector = new FaultInjector(numToInject, fullBitstream, serialA, logger);
		if (random == true) {	
			long startTime = System.currentTimeMillis();
			//injector.switchConfig(configID);
			/*injector.initVoters();*/
			injector.randomInjectionCycle();
//			serialA.disconnect();
			//serialB.disconnect();
			long endTime = System.currentTimeMillis();
			System.out.println("\nTotal Time Interval " + (endTime - startTime)/60000.0 + " Minutes");
		} else {
			System.err.println("ERROR -- Only random injection is supported.");
		}
	}
	
	public static SerialNew setupSerial(int com) {
		SerialNew serial = new SerialNew("COM" + com, 115200);
		serial.connect();
		serial.RTS(false);
		serial.initListeners();
		return serial;
	}
	
	
	
	private static void createDownloadBatch(String bitstreamPath, String boardID){
		PrintWriter pw = null;
		try {
			FileWriter fw = new FileWriter("Download.cmd", false);
			pw = new PrintWriter(fw);
			pw.println("setMode -bs");
			pw.println("setCable -port auto");
			pw.println("setCable -target \"digilent_plugin DEVICE=SN:" + boardID + " FREQUENCY=-1\"");
			pw.println("Identify -inferir");
			pw.println("identifyMPM");
			//String newFile = bitstreamPath.replaceAll("fie", "download");
			pw.println("assignFile -p 1 -file " + "\"" + bitstreamPath + "\"");
			//pw.println("setCable -port usb21 -baud 12000000");
			pw.println("Program -p 1");
			pw.println("exit");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}
	
/*	private static void writeFlash(String bitstreamPath){
		PrintWriter pw = null;
		try {
			FileWriter fw = new FileWriter("Download.cmd", false);
			pw.println("setMode -pff                                                                                                            ");
			pw.println("addConfigDevice  -name " + "\"" + add_1 + "\"" + " -path " + "E:\run_fie\FLASH"                                                                 ");
			pw.println("setSubmode -pffbpi                                                                                                      ");
			pw.println("setAttribute -configdevice -attr multibootBpiType -value "TYPE_BPI"                                                     ");
			pw.println("setAttribute -configdevice -attr multibootBpiDevice -value "VIRTEX6"                                                    ");
			pw.println("setAttribute -configdevice -attr multibootBpichainType -value "PARALLEL"                                                ");
			pw.println("addDesign -version 0 -name "0"                                                                                          ");
			pw.println("setMode -pff                                                                                                            ");
			pw.println("addDeviceChain -index 0                                                                                                 ");
			pw.println("setMode -pff                                                                                                            ");
			pw.println("addDeviceChain -index 0                                                                                                 ");
			pw.println("setAttribute -configdevice -attr compressed -value "FALSE"                                                              ");
			pw.println("setAttribute -configdevice -attr compressed -value "FALSE"                                                              ");
			pw.println("setAttribute -configdevice -attr autoSize -value "FALSE"                                                                ");
			pw.println("setAttribute -configdevice -attr fileFormat -value "mcs"                                                                ");
			pw.println("setAttribute -configdevice -attr fillValue -value "FF"                                                                  ");
			pw.println("setAttribute -configdevice -attr swapBit -value "FALSE"                                                                 ");
			pw.println("setAttribute -configdevice -attr dir -value "UP"                                                                        ");
			pw.println("setAttribute -configdevice -attr multiboot -value "FALSE"                                                               ");
			pw.println("setAttribute -configdevice -attr multiboot -value "FALSE"                                                               ");
			pw.println("setAttribute -configdevice -attr spiSelected -value "FALSE"                                                             ");
			pw.println("setAttribute -configdevice -attr spiSelected -value "FALSE"                                                             ");
			pw.println("setAttribute -configdevice -attr ironhorsename -value "1"                                                               ");
			pw.println("setAttribute -configdevice -attr flashDataWidth -value "16"                                                             ");
			pw.println("setCurrentDesign -version 0                                                                                             ");
			pw.println("setAttribute -design -attr RSPin -value ""                                                                              ");
			pw.println("setCurrentDesign -version 0                                                                                             ");
			pw.println("addPromDevice -p 1 -size 32768 -name 32M                                                                                ");
			pw.println("setMode -pff                                                                                                            ");
			pw.println("setMode -pff                                                                                                            ");
			pw.println("setMode -pff                                                                                                            ");
			pw.println("setMode -pff                                                                                                            ");
			pw.println("addDeviceChain -index 0                                                                                                 ");
			pw.println("setMode -pff                                                                                                            ");
			pw.println("addDeviceChain -index 0                                                                                                 ");
			pw.println("setMode -pff                                                                                                            ");
			pw.println("setSubmode -pffbpi                                                                                                      ");
			pw.println("setMode -pff                                                                                                            ");
			pw.println("setAttribute -design -attr RSPin -value "00"                                                                            ");
			pw.println("addDevice -p 1 -file "E:/run_fie/build/add_1/fie/fie.sdk/SDK/SDK_Export/mb_sys_hw_platform/download.bit"                ");
			pw.println("setAttribute -design -attr RSPinMsb -value "1"                                                                          ");
			pw.println("setAttribute -design -attr name -value "0"                                                                              ");
			pw.println("setAttribute -design -attr RSPin -value "00"                                                                            ");
			pw.println("setAttribute -design -attr endAddress -value "8ce03b"                                                                   ");
			pw.println("setAttribute -design -attr endAddress -value "8ce03b"                                                                   ");
			pw.println("setMode -pff                                                                                                            ");
			pw.println("setSubmode -pffbpi                                                                                                      ");
			pw.println("generate                                                                                                                ");
			pw.println("setCurrentDesign -version 0                                                                                             ");
			pw.println("setMode -bs                                                                                                             ");
			pw.println("setCable -p auto                                                                                                        ");
			pw.println("identify                                                                                                                ");
			pw.println("identifyMPM                                                                                                             ");
			pw.println("attachflash -position 2 -bpi "28F256P30"                                                                                ");
			pw.println("assignfiletoattachedflash -position 2 -file "add_1.mcs"                                                                 ");
			pw.println("Program -p 2 -dataWidth 16 -rs1 NONE -rs0 NONE -bpionly -e -v -loadfpga                                                 ");
			pw.println("closeCable                                                                                                              ");
			pw.println("quit                                                                                                                    ");
			                                                                                                                                    ");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}*/
	
	public static ArrayList<Integer> setupFramesSubtract(String fullBitstream, String maskBitstream) {
		FrameExtractor extractor = new FrameExtractor();
		ArrayList<Integer> frames = extractor.getFrames(fullBitstream);
		extractor.subtractFrames(frames, extractor.getFrames(maskBitstream));
		return frames;
	}
	
	public static ArrayList<String> setupVoterList(String voterListPath) {
		FrameExtractor extractor = new FrameExtractor();
		return extractor.readVoterList(voterListPath);
	}

	public static String getBenchmarkName() {
		return benchmarkName;
	}

	public static void setBenchmarkName(String benchmarkName) {
		FaultInjectionPlatform.benchmarkName = benchmarkName;
	}

}

/*
 * Copyright (c) 2010-2011 Brigham Young University
 * 
 * This file is part of the BYU RapidSmith Tools.
 * 
 * BYU RapidSmith Tools is free software: you may redistribute it 
 * and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * BYU RapidSmith Tools is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License is included with the BYU 
 * RapidSmith Tools. It can be found at doc/gpl2.txt. You may also 
 * get a copy of the license at <http://www.gnu.org/licenses/>.
 * 
 */
package unipi.sevax.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import edu.byu.ece.rapidSmith.design.Design;
import edu.byu.ece.rapidSmith.util.FileConverter;
import edu.byu.ece.rapidSmith.util.MessageGenerator;
/**
 * This Utilities class provides static methods used from the unipi package.
 * @author Aitzan Sari, Dimitris Agiakatsikas.
 * Created on: Oct 26, 2013
 */
public class Utilities {
	
	public static void deleteFile(String fileName){
		try{
			 
    		File file = new File(fileName);
 
    		if(file.delete()){
    			//System.out.println(file.getName() + " is deleted!");
    		}else{
    			//System.out.println("Delete operation is failed.");
    		}
 
    	}catch(Exception e){
 
    		e.printStackTrace();
 
    	}
	}
	
	/**
	 * Gets the value of alpha and returns the value of gamma. This method is involved for the temperature update scheme.
	 * @param alfa The value of á (alpha) i.e the fraction of moves being accepted.
	 * @return The ã (gamma) value.
	 */
	public static double getGama(double alfa) {
		
		if (alfa > 0.96) {
			return 0.5;
		} else if (alfa > 0.8) {
			return 0.9;
		} else if (alfa > 0.15) {
			return 0.95;
		} else {
			return 0.8;
		}
	}

	/**
	 * Gets a milliseconds and returns the time in hh:mm:ss:ms format
	 * @param millis
	 * @return String
	 * @throws ParseException
	 */
	public static String milliseconds2hms(long millis)  throws ParseException {
		return String.format("%02d:%02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
				TimeUnit.MILLISECONDS.toSeconds(millis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
				millis % 1000);   
	}
	
	
	/**
     * Loads the UCF (User Constraints File) and returns a list containing the IOs that must be locked.
     * @param  ucfPath The full path of the UCF file.
     * @return The list containing the locked IOs (e.g a list {AB2, G21,...}). 
     */
	public static ArrayList<String> loadUcf(String ucfPath) {
		ArrayList<String> ucfList = new ArrayList<String>();
		String thisLine;
		try {
		    @SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(ucfPath));
		       while ((thisLine = br.readLine()) != null) {
		    	   thisLine = parseString(thisLine);
		    	   if(thisLine != null)
		    		   ucfList.add(thisLine);
		       } 
		     }
		     catch (IOException e) {
		       System.err.println("Error: " + e);
		     }
		if(ucfList.isEmpty()) {
			return ucfList;
		} else {
			System.out.println("Locking the following IOBs " + ucfList.toString());
			return ucfList;
		}
	}
	
	/**
	 * Gets a String from an UCF file and returns a String with IO location that is locked. 
	 * @param thisLine A line from the UCF file
	 * @return The name of the locked IO or null.
	 */
	private static String parseString(String thisLine) {
		String subS = null;
		/*
		 * Check if the line contains the LOC keyword and it is not commented. 
		 */
		if(thisLine.contains("LOC") && !thisLine.contains("#NET")) {
			int startPosition = thisLine.indexOf("LOC=\"") + "LOC=\"".length();  
			int endPosition = thisLine.indexOf("\"", startPosition);  
			subS = thisLine.substring(startPosition, endPosition);
			return subS.replace("\"", "").toUpperCase();
		} else {
			return subS;
		}
	}
	
	
	/**
	 * Creates a new design and loads a XDL design.
	 * @param xdlFile
	 * @return Design
	 */
	public static Design loadDesign(String xdlFile) {
		
		//------------------------------- Create and load a design --------------------------------------------
		Design design = new Design();
		System.out.println("\nLoading design ..." );
		design.loadXDLFile(xdlFile);
		design.flattenDesign(); //flatten Design
		if(design.getModuleInstances().size() > 0){
			MessageGenerator.briefErrorAndExit("Loading design failed. Sorry, module instances unsupported.");
		} else {
			printDesignReport(design);
		}
		System.out.println("\nLoaded Design " + design.getName());
		return design;
	}
	
	/**
	 * Reports family PartName Columns and rows of the specific device
	 * @param design
	 */
	public static void printDesignReport(Design design) {
		System.out.println("\nFamily\t : "	+ design.getDevice().getFamilyType().toString());
		System.out.println("PartName : " + design.getDevice().getPartName().toString());
		System.out.println("Columns\t : " + design.getDevice().getColumns());
		System.out.println("Rows\t : " + design.getDevice().getRows());					
	}

	/**
	 * Executes a given command line 
	 * @param: myCommand 
	 * @throws IOException 
	 * @throws InterruptedException
	 * @return ErrorCode
	 */
	public static int runCommand(String myCommand) throws IOException, InterruptedException {
		System.out.println("\nRunning Command : " + myCommand + "\n");
		Runtime rt = Runtime.getRuntime();
		// Process pr = rt.exec("cmd /c dir");
		Process pr = rt.exec(myCommand);
		BufferedReader input = new BufferedReader(new InputStreamReader(
				pr.getInputStream()));
		String line = null;
		while ((line = input.readLine()) != null) {
			System.out.println(line);
		}
		int errorCode = pr.waitFor();
		return errorCode;

	 }
	
	/**
	 * Configures the FPGA >> Read the download.bat file
	 * @param: 
	 * @throws IOException 
	 * @throws InterruptedException
	 * @return ErrorCode
	 */
	public static int configure(String CAD, String VivadoPath) throws IOException, InterruptedException {
		Runtime rt = Runtime.getRuntime();
		Process pr = null;
		if (CAD == "ISE")
			pr = rt.exec("impact -batch download.cmd");
		else if(CAD == "VIVADO")
			pr = rt.exec(VivadoPath + " -mode batch -nojournal -nolog -source Download.tcl");
		else 
			return -1;
		
		/*BufferedReader input = new BufferedReader(new InputStreamReader(
				pr.getInputStream()));
		String line = null;
		while ((line = input.readLine()) != null) {
				System.out.println(line);
		}*/
		int errorCode = pr.waitFor();
		return errorCode;
	 }
	
	public static int configure(String CAD) throws IOException, InterruptedException {
		Runtime rt = Runtime.getRuntime();
		Process pr = null;
		if (CAD == "ISE")
			pr = rt.exec("impact -batch download.cmd");
		else if(CAD == "VIVADO")
			pr = rt.exec("E:/Xilinx/Vivado/2017.1/bin/vivado.bat -mode batch -nojournal -nolog -source Download.tcl");
		else 
			return -1;
		
		/*BufferedReader input = new BufferedReader(new InputStreamReader(
				pr.getInputStream()));
		String line = null;
		while ((line = input.readLine()) != null) {
				System.out.println(line);
		}*/
		int errorCode = pr.waitFor();
		return errorCode;
	 }
	
	
	
	/**
	 * Call the Xilinx PAR tool for routing a design. It also calls Xilinx reportgen,trce,xpwr and executes timing and power analysis of the routed design.
	 * @param ncdPlaced
	 * @param ncdOutRouted
	 */
	public static void routeDesign(String ncdPlaced, String ncdOutRouted) {
		System.out.println("\nRouting started...");
		// pcfFile = ncdOutRouted.replace("_route.ncd", ".pcf");
		String designFile = ncdOutRouted.replace("_route.ncd", "");
		String twxFile = designFile + ".twx";
		String twrFile = designFile + ".twr";
		String pwrFile = designFile + ".pwr";
		int errorCode = -1;
		try {  
			    //par -w -intstyle ise -ol high -power on oc_fpu_map.ncd oc_fpu.ncd oc_fpu.pcf
			    errorCode = Utilities.runCommand("par -w -intstyle xflow -ol high -power on " + " " + ncdPlaced + " " + ncdOutRouted);// + " " + pcfFile);
			    //reportgen.exe -intstyle ise -delay -o oc_fpu oc_fpu.ncd 
			    errorCode = Utilities.runCommand("reportgen.exe -delay -o " + designFile + " " + ncdOutRouted);
			    //reportgen.exe -intstyle ise -clock_regions -o oc_fpu oc_fpu.ncd 
			    errorCode = Utilities.runCommand("reportgen.exe -clock_regions -o " + designFile + " " + ncdOutRouted);
			    //trce -intstyle ise -v 3 -tsi oc_fpu.tsi -timegroups -a -s 2 -n 3 -fastpaths -xml oc_fpu.twx oc_fpu.ncd -o oc_fpu.twr oc_fpu.pcf
			    errorCode = Utilities.runCommand("trce -v 3 -tsi oc_fpu.tsi -timegroups -a -s 2 -n 3 -fastpaths -xml " + twxFile + " " + ncdOutRouted + " -o "  + twrFile);//+ " " + pcfFile);
			    //xpwr -intstyle ise oc_fpu.ncd oc_fpu.pcf -o oc_fpu.pwr
			    //errorCode = Utilities.runCommand("xpwr " + ncdOutRouted  + " " + pcfFile + " -o " + pwrFile);
			    errorCode = Utilities.runCommand("xpwr " + ncdOutRouted  + " -o " + pwrFile);
				System.out.println("Routing error code" + errorCode);
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("IOException");
				System.out.println("Error Code = " + errorCode);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error Code = " + errorCode);
		}
	}
	
	/**
	 * Calls the Xilinx bitgen tool to create the bistream and the essential (sensitive) bitmap of a routed NCD design.
	 * @param routedNCD
	 */
	public static void generateBitstream(String routedNCD) {
		System.out.println("\nBitstream generation started...");
		int errorCode = -1;
		try {
			errorCode = Utilities.runCommand("bitgen -w -g DebugBitstream:yes -g EssentialBits:yes " + routedNCD);
			//errorCode = Utilities.runCommand("bitgen -intstyle xflow -w " + routedNCD + " -r " + bitOut);
			System.out.println("Bitstream error code = " + errorCode);							
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("IOException");
			System.out.println("Error Code = " + errorCode);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error Code = " + errorCode);
		}
	}
	 
	/**
	 * Converts the file called myXdlFileName to NCD with a file by the same name
	 * but with an .ncd extension.
	 * @param myXdlFileName The XDL file to convert
	 * @return Name of the output NCD file, or null if conversion failed.
	 */
	public static String convertXdl2Ncd(String myXdlFileName) {
		String myNcdFileName;
		// XDL file. The file is converted from the ncdFileName
		myNcdFileName = FileConverter.convertXDL2NCD(myXdlFileName);
		// Check for error
		if (myNcdFileName == null) {
			MessageGenerator.briefErrorAndExit("ERROR: Conversion of " + myNcdFileName + " to ncd failed.");
		}else{
			System.out.println("\nSuccessfully converted xdl2ncd");
			System.out.println(myXdlFileName + " --> " + myNcdFileName);
		}
		return myNcdFileName;
	}
	
	/**
	 * Converts the file called ncdFileName to XDL with a file by the same name
	 * but with an .xdl extension.
	 * @param myNcdFileName The NCD file to convert
	 * @return Name of the output XDL file, or null if conversion failed.
	 */
	public static String convertNcd2Xdl(String myNcdFileName) {
		String myXdlFileName;
		// XDL file. The file is converted from the ncdFileName
		myXdlFileName = FileConverter.convertNCD2XDL(myNcdFileName);
		// Check for error
		if (myXdlFileName == null) {
			MessageGenerator.briefErrorAndExit("ERROR: Conversion of " + myNcdFileName + " to XDL failed.");
		}else {
			System.out.println("\nSuccessfully converted ncd2xdl");
			System.out.println(myNcdFileName + " --> " + myXdlFileName);
		}
		return myXdlFileName;
	}
	
	/**
	 * Redirects screen output to a text file.
	 * @param fileName
	 * @param logFile 
	 * @param redirect
	 */
	public static void redirectConsole(String fileName, PrintStream logFile, boolean redirect) {
		PrintStream console = null;
		console = System.out;
		File file = new File(fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			logFile = new PrintStream(fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Redirect console to log.txt
		if (redirect){
			System.setOut(logFile);
		} else {
			System.setOut(console);
		}
	}

}

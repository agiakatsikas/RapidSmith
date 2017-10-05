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
package unipi.sevax.userInterface;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import edu.byu.ece.rapidSmith.design.Design;
import edu.byu.ece.rapidSmith.util.FamilyType;

import unipi.sevax.analysis.SEUAnalysis;
import unipi.sevax.analysis.EBD_analysis;
 
import unipi.sevax.utilities.Utilities;

/**This class is a command prompt interface providing to the end-user an easy way to access out proposed soft-error vulnerability tools.
 * @author Dimitris
 * Created on: Oct 26, 2013
 */
public class UserInterface2 {
	/*=========================================================================
	 * Constants
	 *=========================================================================*/

	/*=========================================================================
	 * Global Variables
	 *=========================================================================*/
	/**redirect screen console to log.txt*/
	static boolean redirect2Console;
	/**convert NCD file to XDL file.*/
	static boolean ncd2Xdl;  
	/**convert XDL file to NCD file.*/
	static boolean xdl2Ncd;
	/**Route the design.*/
	static boolean route;
	/**Generate bitstream.*/
	static boolean bitstream;
	/**Place the design.*/
	static boolean place;
	/**Estimation of the vulnerability to soft errors of the routed design (Post-routing analysis)*/
	static boolean routeAnalisis;
	/**Estimation of the vulnerability to soft errors of the placed design (Post-placement analysis)*/
	static boolean placeAnalisis;
	/**Analysis of the Xilinx report for essential configuration bits.*/
	static boolean ebdAnalysis;
	/**Estimation of the vulnerability to soft errors of the mapped design (Post-mapping analysis)*/
	static boolean mapAnalysis;
	/**Soft-error vulnerability analysis. This flag must be enabled in order to perform a Post-mapping,Post-placement or Post-routing analysis*/
	public static boolean seu;
	/**The path of the UCF file. Assign module I/Os into the FPGA pins*/
	static String ucfPath = null;
	/**The project path of the design. Example c:\benchmarks\b14\benchmark*/
	static String projectPath = null; 
	/**The file name of the design. Example Project path = c:\benchmarks\b14\benchmark. FileName = benchmark*/
	static String fileName = null;
	/**A timestamp (ms)*/
	static String timeStamp = null; 
	/**Path of the placed XDL file.*/
	static String xlsName = null;
	/**Path of the excel simulated annealing perfomance file.*/
	static String logName = null; // Log path
	static String xdlFile = null; 
	static String xmlPath = null;
	
	/**This factor determines when the placement will terminate. It has a range between 0.005 and 0.05. 
	 * The value 0.005 will terminate the placement quicker. As value tends to 0.05 the placement takes more time
	 * giving slightly higher quality placement. The performance of the placement is not very sensitive to the factor e.  
	 */
	static double epsilon;
	static double s;
	/**This factor is multiplied by the number of swaps attempted in every temperature.
	 * The larger value o movesMult the higher effort will be attempted for placing the design. The default value is 10 
	 */
	static int movesMult;
	static PrintStream console = null;
	static PrintStream logFile = null;
	static SEUAnalysis analysis;	
	static unipi.sevax.placer.Placer placer;

	static HashMap<String, String> directoryFiles = null;
	
	/*=========================================================================
	 * Main
	 *=========================================================================*/
	public static void main(String[] args)
	{	
		
		
		
		//Parse input arguments
		parseArg(args);
		
		//-------------------------------- Resource Analysis -------------------------------------------------------
        if(mapAnalysis) 
        {
        	System.out.println("\nSearching for a mapped ncd file...");
    		if(!directoryFiles.containsKey("map.ncd")) 
    		{
        		System.out.println("\nNo file with \"_map.ncd\" postfix found. Please add a mapped ncd file in the design folder (e.g benchmark_map.ncd).");
        	} 
    		else 
    		{
    			System.out.println("\nFound a mapped ncd file: " + directoryFiles.get("map.ncd") + ".");
    			System.out.println("\nConverting the mapped ncd file to xdl, in order to start the Post-Mapping analysis.");
        		xdlFile = Utilities.convertNcd2Xdl(directoryFiles.get("map.ncd"));
        		System.out.println("\nStarting the Post-Mapping Analysis of the " + xdlFile + "....");
        		SEUAnalysis analysisResource = new SEUAnalysis(xdlFile, xdlFile.replaceAll("_map.xdl", ""), xmlPath); 
            	analysisResource.printResources();
            	System.out.println("\nPost-Mapping Analysis of the " + directoryFiles.get("map.ncd") + " has finished.");
        	}	
    	}	
        
        //-------------------------------- Placement Analysis -----------------------------------------------------
        if(placeAnalisis) 
        {
        	if(place)
        	{
        		System.out.println("\nSearching for a mapped ncd file, in order to place it with the simulated annealing placer...");
        		if(directoryFiles.containsKey("map.ncd")) 
        		{
        			System.out.println("\nFound a mapped ncd file: " + directoryFiles.get("map.ncd") + ".");
        			System.out.println("\nConverting the mapped ncd file to xdl, in order to start the Simulated annealing placement.");
        			xdlFile = Utilities.convertNcd2Xdl(directoryFiles.get("map.ncd"));
            		placer = new unipi.sevax.placer.Placer();
        			placer.loadDesign(xdlFile, ucfPath);
        			placer.setMovesMult(movesMult);
        			placer.setEpsilon(epsilon);
        			placer.place();
        			placer.saveXDL(xdlFile.replaceAll("_map.xdl", "_place.xdl"));	
        			placer.saveNCD();
        			directoryFiles = getDirectoryFiles(projectPath);
        			System.out.println("\nSimulated annealing placement finished. The placed ncd file is located at: " + directoryFiles.get("place.ncd") + ".");
        		}
        		else 
        		{
        			System.out.println("Cannot find a mapped file. A mapped ncd file must be in the working directory in order to continue with the simulated annealing placement."); 
        		}
        		
        	}   
        	
        	if(!directoryFiles.containsKey("place.ncd") && (place == false) ) 
        	{
        		System.out.println("\nCould not find a placed ncd file. Trying to find a Virtex5 or Virtex6 mapped ncd design.\nThe Xilinx ISE tool does mapping and placement" +
        				" simulnaneously.\nTherefore, the Post-placement analysis tool will search for a Virtex5 or Virtex6 mapped/placed design in order to continue..  ");
        		
        		if(directoryFiles.containsKey("map.ncd")) 
        		{
        			xdlFile = Utilities.convertNcd2Xdl(directoryFiles.get("map.ncd"));
        			Design des = Utilities.loadDesign(xdlFile);
        			if( (des.getFamilyType() == FamilyType.VIRTEX5) || (des.getFamilyType() == FamilyType.VIRTEX6)) 
        			{
        				System.out.println("Found a "+ des.getFamilyType() + " design.");
        				des.saveXDLFile(xdlFile.replaceAll("_map.xdl", "_place.xdl"));
            			directoryFiles = getDirectoryFiles(projectPath);
            			Utilities.convertXdl2Ncd(directoryFiles.get("place.xdl"));
            			directoryFiles = getDirectoryFiles(projectPath);
            			System.out.println("\n" + directoryFiles.get("place.ncd") + " created, in order to continue the post-placement analysis.");
            			xdlFile = directoryFiles.get("place.xdl");
        			} 
        			else 
        			{
        				System.out.println("Could not find a Virtex5 or Virtex6 mapped or placed design.\nPlease add a placed ncd file in order to continue the Post-Placement analysis.");
        			}
        			
        		}
        	} 
        	else 
        	{
        		System.out.println("\nFound a placed ncd file: " + directoryFiles.get("place.ncd") + ".");
    			System.out.println("\nConverting the placed ncd file to xdl, in order to start the Post-Placement analysis.");
        		xdlFile = Utilities.convertNcd2Xdl(directoryFiles.get("place.ncd"));		
        	}
        	
        	System.out.println("\nStarting the Post-Placement Analysis of the " + xdlFile + "....");
        	SEUAnalysis analysisPlace =  new SEUAnalysis(xdlFile, xdlFile.replaceAll("_place.xdl", ""), xmlPath); 
        	analysisPlace.placementAnalysis();
        	System.out.println("\nPost-Placement Analysis of the " + directoryFiles.get("place.ncd") + " has finished.");
        }
        
        //-------------------------------- Routing Analysis -------------------------------------------------------
        if(routeAnalisis) 
        {
        	if(!directoryFiles.containsKey("route.ncd")) 
        	{
        		System.out.println("\nNo routed file found. The tool will try to find a placed file, in order to route it.");
        		if(directoryFiles.containsKey("place.ncd")) 
        		{
        			System.out.println(directoryFiles.get("place.ncd") + " file found.\nThe Post-Routing tool is going to route the " + directoryFiles.get("place.ncd") + 
        					" in order to continue the Post-Routing analysis.");
        			Utilities.routeDesign(directoryFiles.get("place.ncd"), directoryFiles.get("place.ncd").replaceAll("_place.ncd", "_route.ncd"));
        			directoryFiles = getDirectoryFiles(projectPath);
        			xdlFile = Utilities.convertNcd2Xdl(directoryFiles.get("route.ncd"));
        			Utilities.loadDesign(xdlFile).saveXDLFile(xdlFile.replaceAll("_route.ncd", "_route.xdl"));
        		} 
        		else 
        		{
        			System.out.println("Cannot find a placed file. A routed or a placed ncd file must be in the working directory,\nin order to continue the post-routing analysis"); 
        			return;
        		}
        		
        		xdlFile = Utilities.convertNcd2Xdl(directoryFiles.get("place.ncd").replaceAll("_place.ncd", "_route.ncd"));
        	} 
        	else 
        	{
        		System.out.println(directoryFiles.get("route.ncd") + " found.");
        		xdlFile = Utilities.convertNcd2Xdl(directoryFiles.get("route.ncd"));
        	}
        	directoryFiles = getDirectoryFiles(projectPath);
        	System.out.println("\nStarting the Post-Routing Analysis of the " + xdlFile + "....");
        	SEUAnalysis analysisRoute = new SEUAnalysis(xdlFile, xdlFile.replaceAll("_route.xdl", ""), xmlPath); 
        	analysisRoute.routeAnalysis();
        	System.out.println("\nPost-Routing Analysis of the " + directoryFiles.get("route.ncd") + " has finished.");
        }       			

        //-------------------------------------- Ebd Analysis -------------------------------------------------------
        if(ebdAnalysis) 
        {
        	String bitstreamString;
			if(!directoryFiles.containsKey("bit") || !directoryFiles.containsKey("ebd")) 
			{
				System.out.println("\nNo bit or ebd files found. Will try to generate the bitstream from a routed file.");
				if(directoryFiles.containsKey("route.ncd")) 
				{
					System.out.println("\nRouted file found. Starting bitstream generation.");
					Utilities.generateBitstream(directoryFiles.get("route.ncd"));
					directoryFiles = getDirectoryFiles(projectPath);
					bitstreamString = directoryFiles.get("bit");
        		} 
				else 
				{
        			System.out.println("Cannot find a routed file. A routed must be in the working directory in order to continue the ebd analysis"); 
        			return;
        		} 
			} 
			else 
			{
				System.out.println(directoryFiles.get("bit") + " found.");
				bitstreamString = directoryFiles.get("bit");
			}
			
			directoryFiles = getDirectoryFiles(projectPath);
        	System.out.println("\nStarting the EBD Analysis of the " + directoryFiles.get("bit") + " and " + directoryFiles.get("ebd") + " files...");
			EBD_analysis ebd = new EBD_analysis(bitstreamString);
			ebd.loadBitStream();
			ebd.loadEBD();
			//ebd.deleteNonInjectionFrames();
			ebd.getResults();
			System.out.println("\nThe EBD Analysis of the " + directoryFiles.get("bit") + " and " + directoryFiles.get("ebd") + " files has finished.");
			
			/*while(true)
				ebd.getRandomSensitiveBit();*/
        }               
	}
	
	/**Argument parser and global variables initialization.
	 * @returns		: String[]
	 */
	@SuppressWarnings("unused")
	private static void parseArg(String[] args) {
		// create the command line parser
		org.apache.commons.cli.CommandLineParser parser = new BasicParser();
		// create the Options
		org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
		
		//Parser options
		options.addOption( "p", true, "The path of design." );
		options.addOption( "sa", false, "Place with SA");
		options.addOption( "e", true, "Epsilon value. Default 0.005" );
		options.addOption( "m", true, "Moves per temperature multiplier. Default 10" );
		options.addOption( "ucf", true, "Path of ucf file" );
		options.addOption( "xml", true, "Path of xml file" );
		options.addOption( "l", false, "Redirect Console to log file. ");
		options.addOption( "pm", false, "Post-Mapping Analysis");
		options.addOption( "pp", false, "Post-Placement Analysis");
		options.addOption( "pr", false, "Post-Routing Analysis");
		options.addOption( "ebd", false, "Ebd Analysis");
		options.addOption( "h", false, "Help.");
	
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		
		try {
			
			CommandLine cmd = parser.parse(options, args);
			
			if(cmd.hasOption("h") || (cmd.getOptions().length == 0))
			{
				System.out.println("SEVAX framework version = 1\n\nPlease use one of the four analysis methods:\n\nPost-Mapping (-pm)," +
				 		"\nPost-Placement (-pp),\nPost-Routing (-pr),\nEbd Analysis (-ebd). \n\nFor example, " +
				 		"type:\n\n\"java -jar sevax.jar -p C:\\DesignDirectory -xml c:\\sensitiveBits.xml -pm -pp\"" +
				 		"\n\nfor PM and PR analysis.\n\n");
				 formatter.printHelp( "ant", options );
				 return;
			}
			if(!cmd.hasOption("xml"))
			{
				 System.out.println("No Xml Path specified");
				 formatter.printHelp( "ant", options );
				 return;
			}
			
			if(!cmd.hasOption("-pm")
					&& !cmd.hasOption("-pp")
					&& !cmd.hasOption("-pr")
					&& !cmd.hasOption("-ebd"))
			{
				 System.out.println("Please use one of the four analysis methods:\n\nPost-Mapping (-pm)," +
				 		"\nPost-Placement (-pp),\nPost-Routing (-pr),\nEbd Analysis (-ebd). \n\nFor example, " +
				 		"type:\n\n\"java -jar sevax.jar -p C:\\DesignDirectory -xml c:\\sensitiveBits.xml -pm -pp\"" +
				 		"\n\nfor PM and PR analysis.\n\n");
				 formatter.printHelp( "ant", options );
				 return;
			}
			
			/*********************************************************/
			if(cmd.getOptionValue("p") == null) {
				projectPath = "";
			}
			else {
			    projectPath = cmd.getOptionValue("p");
			    System.out.println("ProjectPath = " + projectPath);
			    directoryFiles = getDirectoryFiles(projectPath);
			}
			/*********************************************************/
			if(cmd.hasOption("pm")) {
				mapAnalysis = true;
			}
			else {
				mapAnalysis = false;
			}
			/*********************************************************/
			if(cmd.getOptionValue("xml") == null) {
				System.out.println("Please specify the path of the xml, e.g c:\\sensitiveBits.xml\nFor example, " +
				 		"type:\n\n\"java -jar sevax.jar -p C:\\DesignDirectory -xml c:\\sensitiveBits.xml -pm -pp\"" +
				 		"\n\nfor PM and PR analysis.\n\n");
				formatter.printHelp( "ant", options );
				return;
			}
			else {
				xmlPath = cmd.getOptionValue("xml");
			}
			/*********************************************************/
			if(cmd.hasOption("pp")) {
				placeAnalisis = true;
			}
			else {
				placeAnalisis = false;
			}
			/*********************************************************/
			if(cmd.hasOption("sa")) {
				place = true;
				
				if(cmd.getOptionValue("ucf") == null) {
				    System.out.println("UCF file not specified. User constraints will not be used.");
				}
				else {
					ucfPath = cmd.getOptionValue("ucf");
					System.out.println("UCF path = " + ucfPath);
				}
				
				/*********************************************************/
				if(cmd.getOptionValue("e") == null) {
				    epsilon = 0.005;
				}
				else { 
					epsilon = Double.valueOf(cmd.getOptionValue("e"));
				}
				/*********************************************************/		
				if(cmd.getOptionValue("m") == null) {
				    
				    movesMult = 10;
				}
				else { 
					movesMult = Integer.valueOf(cmd.getOptionValue("m"));
				}
				/*********************************************************/	
				
			}
			else {
				place = false;
			}
			
			if(cmd.hasOption("pr")) {
				routeAnalisis = true;
			}else {
				routeAnalisis = false;
			}
			/*********************************************************/
			if(cmd.hasOption("ebd")) {
				ebdAnalysis = true;
			}
			else {
				ebdAnalysis = false;
			}
			/*********************************************************/	
			if(cmd.hasOption("l")) {
				redirect2Console = true;	
			}
			else {
				redirect2Console = false;
			}
			/*********************************************************/
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR ARGUMENTS");
			System.out.println("\n" + "Type sevax.jar -h to see the usage help of the tool.");
			return;
		}
		
		timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		console = null;
		logFile = null;
		logName = directoryFiles.get("path") + "\\" + directoryFiles.get("directoryName") + "_log.txt";	
		//Console redirection
		Utilities.redirectConsole(logName, logFile, redirect2Console);

		if(false) {
			System.out.println("Bitstream = " + (bitstream == true ? "TRUE" : "FALSE") );
			System.out.println("Route = " + (route == true ? "TRUE" : "FALSE") );
			System.out.println("Ncd2Xdl = " + (ncd2Xdl == true ? "TRUE" : "FALSE") );
			System.out.println("Xdl2Ncd = " + (xdl2Ncd == true ? "TRUE" : "FALSE") );
			System.out.println("Redirect2Console = " + (redirect2Console == true ? "TRUE" : "FALSE") );
		}
		
		if(redirect2Console == true) System.out.println("Log file is located at = " + logName);
		if(place == true) 
		{
			System.out.println("Simulated Annealing Placer = " + (place == true ? "TRUE" : "FALSE") );
			System.out.println("Epsilon = " + epsilon);
			System.out.println("Moves per Temperature multiplier = " + movesMult);
		}
		
	}
	
	private static HashMap<String, String> getDirectoryFiles(String directoryPath) {
		String files;
		HashMap<String, String> directoryFiles = new HashMap<String, String>();
		File directory = new File(directoryPath);
		File[] listOfFiles = directory.listFiles();
		directoryFiles.put("path", directory.getPath());
		directoryFiles.put("directoryName", directory.getName());
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				if (files.toLowerCase().endsWith("_map.ncd")) {
					directoryFiles.put("map.ncd", directoryPath + "/" + files);
				}
				if (files.toLowerCase().endsWith("_map.xdl")) {
					directoryFiles.put("map.xdl", directoryPath + "/" + files);
				}
				if (files.toLowerCase().endsWith("_place.ncd")) {
					directoryFiles.put("place.ncd", directoryPath + "/" + files);
				}
				if (files.toLowerCase().endsWith("_place.xdl")) {
					directoryFiles.put("place.xdl", directoryPath + "/" + files);
				}
				if (files.toLowerCase().endsWith("_route.ncd")) {
					directoryFiles.put("route.ncd", directoryPath + "/" + files);
				}
				if (files.toLowerCase().endsWith("_route.xdl") ) {
					directoryFiles.put("route.xdl", directoryPath + "/" + files);
				}
				if (files.toLowerCase().endsWith(".bit")) {
					directoryFiles.put("bit", directoryPath + "/" + files);
				}
				if (files.toLowerCase().endsWith(".ebd")) {
					directoryFiles.put("ebd", directoryPath + "/" + files);
				}
				if (files.toLowerCase().endsWith(".ncd") 
						&& !files.toLowerCase().endsWith("_map.ncd") 
						&& !files.toLowerCase().endsWith("_guide.ncd") ) 
				{
				directoryFiles.put("route.ncd", directoryPath + "/" + files);
				xdlFile = Utilities.convertNcd2Xdl(directoryFiles.get("route.ncd"));
				
				}
			}
		}
		return directoryFiles;
	}
}
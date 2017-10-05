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
package unipi.sevax.analysis;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import unipi.sevax.placer.*;

import unipi.sevax.utilities.Maths;
import unipi.sevax.utilities.Utilities;

import edu.byu.ece.rapidSmith.design.Attribute;
import edu.byu.ece.rapidSmith.design.Design;
import edu.byu.ece.rapidSmith.design.Instance;
import edu.byu.ece.rapidSmith.design.Net;
import edu.byu.ece.rapidSmith.design.NetType;
import edu.byu.ece.rapidSmith.design.PIP;
import edu.byu.ece.rapidSmith.design.Pin;
import edu.byu.ece.rapidSmith.device.PrimitiveSite;
import edu.byu.ece.rapidSmith.device.PrimitiveType;
import edu.byu.ece.rapidSmith.device.Tile;
import edu.byu.ece.rapidSmith.device.WireConnection;
import edu.byu.ece.rapidSmith.device.WireDirection;
import edu.byu.ece.rapidSmith.device.WireEnumerator;
import edu.byu.ece.rapidSmith.util.FamilyType;
import edu.byu.ece.rapidSmith.util.MessageGenerator;

/**
 * This class estimates the vulnerability of FPGA designs to soft errors at all phases of the FPGA implementation flow. 
 * In particular,it supports post-mapping analysis of the sensitive block configuration bits, post-placement analysis of the sensitive
 * interconnection bits and final (post-routing) analysis of the total sensitive configuration bits. 
 * @author University of Piraeus (Aitzan Sari - Agiakatsikas Dimitrios).
 * Created on: Oct 26, 2013
 */
public class SEUAnalysis {
	/*=========================================================================
	 * Class fields
	 *=========================================================================*/
	
	String input;
	/**The design that is going to be analyzed.*/
	Design design; 
	/**A Map with the Bonding box of each net in the design.*/
	HashMap<String, NetBB> netBboxes;
	/**A list with the Bonding box of each net in the design.*/
	ArrayList<NetBB> netBbList;
	/**Creating a new instance of Math class in order to use its methods in the analysis phase.*/
	Maths maths = new Maths();
	/**An two-dimensional array representing the switch matrices of the targeting FPGA device.*/
	Integer[][] fpga;
	/**A variable to hold the results of the total resource configuration sensitive bits analysis.*/
	long 	resourceBits;
	/**A variable to hold the results of the CLB configuration sensitive bits analysis.*/
	long 	clbSensitiveBits;
	/**A variable to hold the results of the CLB configuration sensitive bits analysis.*/
	long 	ramSensitiveBits;
	/**A variable to hold the results of the IO configuration sensitive bits analysis.*/
	long	ioSensitiveBits;
	/**A variable to hold the results of the DSP configuration sensitive bits analysis.*/
	long 	dspSensitiveBits;
	/**A variable to hold the number indicating the number of used SLICES.*/
	int		sliceBlocks;
	/**A variable to hold the number indicating the number of used IOs.*/
	int 	ioBlocks;
	/**A variable to hold the number indicating the number of used BRAM.*/
	int		bramBlocks;
	/**A variable to hold the number indicating the number of used DSPs.*/
	int		dspBlocks;
	/**The path of the design.*/
	String path;
	/**The path for writing the .rsba file (stands for routing sensitive bits analysis).*/
	String sbaFilePath;
	/**The path of the XML file (precompiled resource usage profile).*/
	String xmlPath;
	/**An ArrayList of the complex blocks of the design. In other words the tiles of the design and their relying instances.*/ 
	private  ArrayList<ComplexBlock> blocks;
	/** The number of sensitive bits (total) for control bits of the slices */
	int sliceControlBitsTotal;						
	/** The number of open sensitive bits (total) of the design analysis */
	long sensitiveBits_open;						
	/** The number of short sensitive bits (total) of the design analysis */
	long sensitiveBits_short;						
	/** The number of open sensitive bits for the IO blocks */
	int iobSensitiveInterconnectionBits_open;		
	/** The number of open sensitive bits for the CLB blocks */
	int clbSensitiveInterconnectionBits_open;		
	/** The number of open sensitive bits for the BRAM blocks */
	int bramSensitiveInterconnectionBits_open;		
	/** The number of open sensitive bits for the DSP blocks */
	int dspSensitiveInterconnectionBits_open;		
	/** The number of open sensitive bits for the CLK blocks */
	int clkSensitiveInterconnectionBits_open;		
	/** The number of short sensitive bits for the IO blocks */
	int iobSensitiveInterconnectionBits_short;		
	/** The number of short sensitive bits for the CLB blocks */
	int clbSensitiveInterconnectionBits_short;		
	/** The number of short sensitive bits for the BRAM blocks */
	int bramSensitiveInterconnectionBits_short;		
	/** The number of short sensitive bits for the DSP blocks */
	int dspSensitiveInterconnectionBits_short;		
	/** The number of short sensitive bits for the CLK blocks */
	int clkSensitiveInterconnectionBits_short;		
	
	/*=========================================================================
	 * Class Types
	 *=========================================================================*/ 
	/**
	 * //Enumeration for the types of the column that we currently can handle
	 * Created on: Oct 27, 2013
	 */
	enum ColumnType {CLB, IO, BRAM, DSP, CLK, UNKNOWN}
	/**
	 * Inner class used to represent a wire
	 * Created on: Oct 27, 2013
	 */
	 class wireSite
	{
		 	/**************************************************************************
			 * Class fields
			 **************************************************************************/
		 	/**The name of the site at which the wire resides.*/
		    public String 			siteName;
		    /**The wire.*/
			public int				wire;
			/**The name of the net at which the wire belongs.*/
			public String 			netName;
			/**The name of the wire.*/
			public String			wireName;
			
			/**************************************************************************
			 * Class constructors
			 ***************************************************************************/
			/**
			 * Constructor which initializes all member data structures.
			 */
			public wireSite()
			{
				this.siteName 	= "";
				this.wire	  	= -1;
				this.netName	= "";
				this.wireName	= "";
			}
			/**
			 * Initialize the wireSite class by name and wire.
			 * @param name
			 * @param wire
			 */
			public wireSite(String name, int wire)
			{
				this.siteName 	= name;
				this.wire	  	= wire;
				this.netName	= "";
				this.wireName	= "";
			}
			/**
			 * Initialize the wireSite class by name, wire and netName.
			 * @param name
			 * @param wire
			 * @param netName
			 */
			public wireSite(String name, int wire, String netName)
			{
				this.siteName 	= name;
				this.wire	  	= wire;
				this.netName  	= netName;
			}
			
			/**
			 * Initialize the wireSite class by name, wire, netName and wireName.
			 * @param name
			 * @param wire
			 * @param netName
			 * @param wireName
			 */
			public wireSite(String name, int wire, String netName, String wireName)
			{
				this.siteName 	= name;
				this.wire	  	= wire;
				this.netName  	= netName;
				this.wireName	= wireName;
			}
			/**************************************************************************
			 * Class methods
			 **************************************************************************/
			/**
			 * Overriding the java.lang.Object.equal method.
			 */
			public boolean equals(Object o)
			{
				wireSite ws = (wireSite)o;
				if(this.siteName.equals(ws.siteName) && this.wire == ws.wire)//.equals(ws.wireName))
					return true;
				else
					return false;
			}
		}
	 
	
	/*=========================================================================
	 * Class constructors
	 *=========================================================================*/
	 /**
	 * Constructor which initializes all member data structures.
	 */
	public SEUAnalysis() {
		this.input = "";
		this.path ="";
		this.sbaFilePath = "";
		this.xmlPath = "";
		this.iobSensitiveInterconnectionBits_open   = 0;
		this.clbSensitiveInterconnectionBits_open   = 0;
		this.bramSensitiveInterconnectionBits_open  = 0;
		this.dspSensitiveInterconnectionBits_open   = 0;
		this.clkSensitiveInterconnectionBits_short  = 0;
		this.iobSensitiveInterconnectionBits_short  = 0;
		this.clbSensitiveInterconnectionBits_short  = 0;
		this.bramSensitiveInterconnectionBits_short = 0;
		this.dspSensitiveInterconnectionBits_short  = 0;
		this.clkSensitiveInterconnectionBits_short  = 0;
		this.design = new Design();
		this.loadFile(input);
		this.netBboxes = new HashMap<String, NetBB>();
		this.netBbList = new ArrayList<NetBB>();
		fillDesBboxes();
		this.blocks = new ArrayList<ComplexBlock>();
		loadCompexBlocks(false);
		getTypeFreq();
		int rows = design.getDevice().getRows();
		int columns = design.getDevice().getColumns();
		this.fpga = new Integer[rows + 20][columns + 20];
		for(Integer x = 0; x <= rows + 19; x++) {
			for(Integer y = 0; y <= columns + 19; y++) {
				fpga[x][y] = 0;
			}
		}
		this.clbSensitiveBits = this.getCLBSensitiveBits();
		this.ioSensitiveBits  = this.getIOSensitiveBits();
		this.ramSensitiveBits = this.getRamSensitiveBits();
		this.dspSensitiveBits = this.getDspSensitiveBits();	
	}
	/**
	 * Constructor which initializes all member data structures 
	 * and performs the soft-error vulnerability analysis of the resource block configuration bits.
	 */
	public SEUAnalysis(String xdlIn, String path, String xmlPath){
		this.input = xdlIn;
		this.path = path;
		this.sbaFilePath	= path + ".sba";
		Utilities.deleteFile(sbaFilePath);
		this.xmlPath = xmlPath;
		this.iobSensitiveInterconnectionBits_open   = 0;
		this.clbSensitiveInterconnectionBits_open   = 0;
		this.bramSensitiveInterconnectionBits_open  = 0;
		this.dspSensitiveInterconnectionBits_open   = 0;
		this.clkSensitiveInterconnectionBits_short  = 0;
		this.iobSensitiveInterconnectionBits_short  = 0;
		this.clbSensitiveInterconnectionBits_short  = 0;
		this.bramSensitiveInterconnectionBits_short = 0;
		this.dspSensitiveInterconnectionBits_short  = 0;
		this.clkSensitiveInterconnectionBits_short  = 0;
		this.design = new Design();
		this.loadFile(input);
		this.netBboxes = new HashMap<String, NetBB>();
		this.netBbList = new ArrayList<NetBB>();
		fillDesBboxes();	
		this.blocks = new ArrayList<ComplexBlock>();
		loadCompexBlocks(false);
		getTypeFreq();
		int rows = design.getDevice().getRows();
		int columns = design.getDevice().getColumns();
		this.fpga = new Integer[rows + 20][columns + 20];
		for(Integer x = 0; x <= rows + 19; x++) {
			for(Integer y = 0; y <= columns + 19; y++) {
				fpga[x][y] = 0;
			}
		}
		this.clbSensitiveBits = this.getCLBSensitiveBits();
		this.ioSensitiveBits  = this.getIOSensitiveBits();
		this.ramSensitiveBits = this.getRamSensitiveBits();
		this.dspSensitiveBits = this.getDspSensitiveBits();
		this.resourceBits 	  = this.clbSensitiveBits + this.ramSensitiveBits + this.ioSensitiveBits + dspSensitiveBits;
	}
	
	/*=========================================================================
	 * Class Methods
	 *=========================================================================*/
	/**
	 * It prints the results of the resource block configuration bits analysis. 
	 * More specifically it prints the sensitive bits of the: 
	 * 1)Clbs, 
	 * 2)Ram, 
	 * 3)DSP, 
	 * 4)Total resource block configuration sensitive bits 
	 * and the number of used: 
	 * 1)SLICE blocks, 
	 * 2)IO blocks, 
	 * 3)BRAM blocks, 
	 * 4)DSP blocks.
	 */
	public void printResources() {		
		System.out.println("\nResource Blocks\n");
		System.out.printf("%-15.15s  %15.15s%n","SLICE blocks", this.sliceBlocks);
		System.out.printf("%-15.15s  %15.15s%n","IO blocks", this.ioBlocks);
		System.out.printf("%-15.15s  %15.15s%n","BRAM blocks", this.bramBlocks);
		System.out.printf("%-15.15s  %15.15s%n","DSP blocks", this.dspBlocks);
		System.out.println("\n\nResource Sensitive Bits\n");
		System.out.printf("%-15.15s  %15.15s%n","Clbs bits", this.clbSensitiveBits);
		System.out.printf("%-15.15s  %15.15s%n","Ram bits", this.ramSensitiveBits);
		System.out.printf("%-15.15s  %15.15s%n","IO bits", this.ioSensitiveBits);
		System.out.printf("%-15.15s  %15.15s%n","DSP bits", this.dspSensitiveBits);
		System.out.printf("%-15.15s  %15.15s%n","Resource bits", this.resourceBits);
		System.out.println("\n=====================================================================================================================");
		System.out.println("=====================================================================================================================");
		System.out.println("");
	}
	
	/*=========================================================================
	 * Route Analysis Methods
	 *=========================================================================*/
	
	/**
	 * Post-routing analysis. It provides a more accurate analysis since it relies on the final routed circuit. 
	 * It considers all possible defects that can be caused by a soft error in a programmable interconnection point,
	 * i.e. open faults, bridging faults and antenna faults. 
	 * The  analysis results are written in a text file (.rsba stands for routing sensitive bit analysis) for further processing.
	 * Final, this method prints to the console the Routing Analysis: 
	 * 1)Open sensitive bits, 
	 * 2)Short sensitive bits, 
	 * 3)Sum of Short and Open sensitive bits, 
	 * 4)Antennas sensitive bits, 
	 * 5)Sum of Short, Open and Antenna sensitive bits, 
	 * 6)Total sensitive bits, 
	 * 7)Total sensitive bits (with antennas),
	 * and also the discrimination of the interconnection sensitive bits to: 
	 * 1)CLB inerconnection sensitive bits, 
	 * 2)IO inerconnection sensitive bits, 
	 * 3)DSP inerconnection sensitive bits, 
	 * 4)BRAM inerconnection sensitive bits, 
	 * 5)CLK inerconnection sensitive bits.
	 */
	public void routeAnalysis() {
//		System.out.println("\n\n=====================================================================================================================");
//		System.out.println("=====================================================================================================================");
		
		if(!this.sbaFilePath.equals(""))
		{
			File file = new File(this.sbaFilePath);
			file.delete();
		}
		
		long openBits = getOpenBits_route();
		long shortBits = getShortBits_route();
		long shortAll = getAllShort();
		long antennas = shortAll - shortBits;
		System.out.println("\nRouting Analysis\n");
//		System.out.printf("%-15.15s  %15.15s%n","Open bits", openBits);
//		System.out.printf("%-15.15s  %15.15s%n","Short bits", shortBits);
//		System.out.printf("%-15.15s  %15.15s%n","Total no Ant bits", (openBits + shortBits + this.resourceBits));
//		System.out.printf("%-15.15s  %15.15s%n","Short Antennas bits" ,antennas);
//		System.out.printf("%-15.15s  %15.15s%n","Total with Ant bits", (openBits + shortBits + antennas + this.resourceBits));
		
		System.out.printf("%-40.40s  %40.40s%n","Open sensitive bits",openBits);
		System.out.printf("%-40.40s  %40.40s%n","Short sensitive bits",shortBits);
		System.out.printf("%-40.40s  %40.40s%n","Sum of Short and Open sensitive bits",shortBits + openBits);
		System.out.printf("%-40.40s  %40.40s%n","Antennas sensitive bits",antennas);
		System.out.printf("%-40.40s  %40.40s%n","Sum of Short, Open and Antenna sensitive bits",shortBits + openBits + antennas);
		System.out.printf("%-40.40s  %40.40s%n","Total sensitive bits ",(openBits + shortBits + this.resourceBits));
		System.out.printf("%-40.40s  %40.40s%n","Total sensitive bits (with antennas)",(openBits + shortBits + antennas + this.resourceBits));
		//System.out.printf("%-40.40s  %40.40s%n","Total sensitive bits with and without antennas",(openBits + shortBits + this.resourceBits));
		System.out.println("\n\nDiscriminating the interconnection sensitive bits to:\n");
		System.out.printf("%-40.40s  %40.40s%n","CLB inerconnection sensitive bits",(this.clbSensitiveInterconnectionBits_short + this.clbSensitiveInterconnectionBits_open));
		System.out.printf("%-40.40s  %40.40s%n","IO inerconnection sensitive bits",(this.iobSensitiveInterconnectionBits_short + this.iobSensitiveInterconnectionBits_open));
		System.out.printf("%-40.40s  %40.40s%n","DSP inerconnection sensitive bits",(this.dspSensitiveInterconnectionBits_short + this.dspSensitiveInterconnectionBits_open));
		System.out.printf("%-40.40s  %40.40s%n","BRAM inerconnection sensitive bits",(this.bramSensitiveInterconnectionBits_short + this.bramSensitiveInterconnectionBits_open));
		System.out.printf("%-40.40s  %40.40s%n","CLK inerconnection sensitive bits",(this.clkSensitiveInterconnectionBits_short + this.clkSensitiveInterconnectionBits_open));
		System.out.println("\n=====================================================================================================================");
		System.out.println("=====================================================================================================================");
		System.out.println("");

	}
	
	
	/**
	 * This mehtod returns a column type
	 * @param	primitiveSites	:The sites of the FPGA device
	 * @param	x				:The x-position of the block
	 * @return	ColumnType		:The column type of the block located in the x position of the FPGA layout.\n
	 * 							 Returns UNKNOWN in case the column type cannot be found.
	 * @author  University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
	 */
	ColumnType getColumTileType(HashMap<String, PrimitiveSite> primitiveSites, int x)
	{
		Set set = primitiveSites.entrySet();
		Iterator i = set.iterator();
		PrimitiveSite site;
		while(i.hasNext())
		{
			try
			{
				Map.Entry item = (Map.Entry)i.next();
				site = (PrimitiveSite)item.getValue();
				if(site.getTile().getTileXCoordinate() == x)
				{
					//if(site.getTile().getName().toString().contains("INT")) 		{continue;}
					if(site.getTile().getName().toString().contains("HCLK")) 		{continue;}
					
					// We are trying to find a column which should be type of the basic logic blocks of the FPGA
					if(site.getTile().getName().toString().contains("CLB")) 		{ return ColumnType.CLB; }
					else if(site.getTile().getName().toString().contains("CLK")) 	{ return ColumnType.CLK; }
					else if(site.getTile().getName().toString().contains("IO")) 	{ return ColumnType.IO; }
					else if(site.getTile().getName().toString().contains("OLOGIC")) { return ColumnType.IO; }
					else if(site.getTile().getName().toString().contains("ILOGIC")) { return ColumnType.IO; }
					else if(site.getTile().getName().toString().contains("RAM")) 	{ return ColumnType.BRAM; }
					else if(site.getTile().getName().toString().contains("DSP")) 	{ return ColumnType.DSP; }
					else {continue;}
				}
			}
			catch(Exception ex){}
		}
		return ColumnType.UNKNOWN;
	}
	
	/**
	 * Returns the open sensitive bits of a routed design. 
	 * @return long : open sensitive bits.
	 */
	private long getOpenBits_route()
	{
		long openSensitiveBits = 0;
		HashMap<String, Integer> tileSensitiveBits = new HashMap<String, Integer>();
		ArrayList<PIP> pips;
		Collection<Net> nets = this.design.getNets();
		
		for(Net net : nets)
		{
			pips = net.getPIPs();
			for(PIP pip : pips)
			{
				// A PIP is always an open sensitive bit 
				openSensitiveBits++;
				// Find the type of the column in which the PIP resides.
				// We are doing this in order to discriminate the interconnection bits to: CLB, IOB, CLK, BRAM, DSP interconnection bits 
				ColumnType type = getColumTileType(design.getDevice().getPrimitiveSites(), pip.getTile().getTileXCoordinate());
				switch(type)
				{
					case CLB:  { this.clbSensitiveInterconnectionBits_open++;  } break;
					case IO:   { this.iobSensitiveInterconnectionBits_open++;  } break;
					case BRAM: { this.bramSensitiveInterconnectionBits_open++; } break;
					case CLK:  { this.clkSensitiveInterconnectionBits_open++;  } break;
					case DSP:  { this.dspSensitiveInterconnectionBits_open++;  } break;
					default: break;
				}
				// Find the tile where the PIP resides and increase the sensitive bits of this particular tile by 1.
				// This information is saved in the .sba file so that can be used to the graphical representation of the
				// sensitive areas of the FPGA device
				if(tileSensitiveBits.containsKey(pip.getTile().getName()))
				{
					int value = tileSensitiveBits.get(pip.getTile().getName()).intValue();
					tileSensitiveBits.put(pip.getTile().getName(), value + 1);
				}
				else
				{
					tileSensitiveBits.put(pip.getTile().getName(), 1);
				}				
			}			
		}
		
		// Save the sensitive bits information of the tiles to the .sba file
		try 
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.sbaFilePath, true));
			Set set = tileSensitiveBits.entrySet();
			Iterator i = set.iterator();			
			while(i.hasNext()) 
			{
				Map.Entry obj 	= (Map.Entry)i.next();
				Integer bits 	= (Integer) obj.getValue();
				String  site	= (String) obj.getKey();
				writer.write(site + "_OPEN = " + bits + "\n");
			}
			writer.close();
	    }
		catch (IOException e) {}
		
		// Update the field of the openSensitiveBits and return its value
		this.sensitiveBits_open = openSensitiveBits;
		return this.sensitiveBits_open;
	 }

	/**
	 * Returns the short sensitive bits of a routed design.
	 * @return long : short sensitive bits.
	 */
	public long getAllShort() {
		Collection<Net> nets = this.design.getNets();
		Iterator<Net> itr = nets.iterator();
		ArrayList<WireConnection> wireconnectionList = new ArrayList<WireConnection>();
		while(itr.hasNext())
		{
			Net net = itr.next();
			ArrayList<PIP> pips = net.getPIPs();
			
			for(PIP pip : pips) {
//				if((pip.getTile().toString().contains("CLB"))  ||
//				   (pip.getTile().toString().contains("RAM")) ||
//				   (pip.getTile().toString().contains("DSP"))  ||
//				   (pip.getTile().toString().contains("IO")))
//					continue;
				if(!pip.getTile().toString().contains("INT_X")) continue;
				
				
				WireConnection[] wires1 = pip.getTile().getWireConnections(pip.getStartWire());
				WireConnection[] wires2 = pip.getTile().getWireConnections(pip.getEndWire());
				if(wires1 != null)
					wireconnectionList.addAll(new ArrayList<WireConnection>(Arrays.asList(wires1)));				
				if(wires2 != null)
					wireconnectionList.addAll(new ArrayList<WireConnection>(Arrays.asList(wires2)));	
			}
		}
		
		int noPipsNum = 0 ;
		for(WireConnection w : wireconnectionList) {
			if(!w.isPIP()){
				noPipsNum++;
			}
		}
		//System.out.println("Num of no pips= " + noPipsNum);
		return (wireconnectionList.size() - noPipsNum);
	}
	
	/**
	 * Returns the short sensitive bits of a routed design.
	 * @return long : short sensitive bits (does not include the antenna sensitive bits).
	 */
	public long getShortBits_route() {
		
		//int antennas = calculateUserNet_antennas(path + "_ruf.txt");
		//System.out.println("User def antennas " + antennas);
		
		long clockAntennas = calculateClock_antennas();
		System.out.println("\nClock antennas = " + clockAntennas);
		HashMap<String, Integer> tileSensitiveBits = new HashMap<String, Integer>();
		
		 // Holds all the nets of the design
        ArrayList<Net> nets               = new ArrayList<Net>(this.design.getNets());
        // Holds all the wires with their site which can form a connection
        Collection<wireSite> wires        = new ArrayList<wireSite>();
        // Holds all the start wires with their site which can form a connection
        Collection<wireSite> wiresStart   = new ArrayList<wireSite>();
        // Holds all the end wires with their site which can form a connection
        Collection<wireSite> wiresEnd     = new ArrayList<wireSite>();
        
        // The number of short sensitive bits
        double shortBits                  = 0;
        
        WireEnumerator wenum = design.getWireEnumerator();
       
        // Fill an ArrayList (wires) with all the possible connections of the used pips
        for(Net net : nets)
        {
               ArrayList<PIP> pips = net.getPIPs();                  
               for(PIP pip : pips)
               {                         
            	   //if(!pip.getTile().toString().contains("INT")) continue;
                   WireConnection[] startWires = pip.getTile().getWireConnections(pip.getStartWire());
                   if(startWires != null)
                   {
                	   for(WireConnection wConnection : startWires)
                       {
                		   if(pip.getTile().getName() == null)	System.out.println("pip.getTile().getName() == null");
                           if(wConnection.getWire() == 0) System.out.println("wConnection.getWire() == null");
                           // Add the wire to the global wires list
     					   wires.add(new wireSite(pip.getTile().getName(), wConnection.getWire(), net.getName(), wenum.getWireName(wConnection.getWire())));
     					   // Add the wire to the start wire list
     					   wiresStart.add(new wireSite(pip.getTile().getName(), wConnection.getWire(), net.getName(), wenum.getWireName(wConnection.getWire())));
                       }
     				   wiresStart.add(new wireSite(pip.getTile().getName(), pip.getStartWire(), net.getName(), wenum.getWireName(pip.getStartWire())));
                   }
                     
                   WireConnection[] endWires = pip.getTile().getWireConnections(pip.getEndWire());
     			   if(endWires != null)
     			   {
     				   for(WireConnection wConnection : endWires)
                       {
     					   //wires.add(new wireSite(pip.getTile().getName(), wConnection.getWire()));
     					   wires.add(new wireSite(pip.getTile().getName(), wConnection.getWire(), net.getName(), wenum.getWireName(wConnection.getWire())));
     					   wiresEnd.add(new wireSite(pip.getTile().getName(), wConnection.getWire(), net.getName(), wenum.getWireName(wConnection.getWire())));
                       }
     				   wiresEnd.add(new wireSite(pip.getTile().getName(), pip.getEndWire(), net.getName(), wenum.getWireName(pip.getEndWire())));
     			   }                 
               }
        }
       
        
        // Check for wires that could form a short connection
        int pipShortBits = 0;
        for(Net net : nets)
        {
               ArrayList<PIP> pips = net.getPIPs();                  
               for(PIP pip : pips)
               {
            	    wireSite ws1 = new wireSite(pip.getTile().getName(),pip.getStartWire(), net.getName(), wenum.getWireName(pip.getStartWire()));
                    wireSite ws2 = new wireSite(pip.getTile().getName(),pip.getEndWire(), net.getName(), wenum.getWireName(pip.getEndWire()));
                    pipShortBits = Collections.frequency(wires, ws1) + Collections.frequency(wires, ws2);
                    
                    if(((wiresStart.contains(ws1)) && (wiresEnd.contains(ws1))) || ((wiresStart.contains(ws2)) && (wiresEnd.contains(ws2))))
                    {
                    	pipShortBits /= 2;
                    }
                    
                    shortBits += pipShortBits;                 
                     
                    Tile tile = pip.getTile();
                    ColumnType type = getColumTileType(design.getDevice().getPrimitiveSites(), tile.getTileXCoordinate());
                    switch(type)
					{
						case CLB:  { this.clbSensitiveInterconnectionBits_short  += pipShortBits; } break;
						case IO:   { this.iobSensitiveInterconnectionBits_short  += pipShortBits; } break;
						case BRAM: { this.bramSensitiveInterconnectionBits_short += pipShortBits; } break;
						case CLK:  { this.clkSensitiveInterconnectionBits_short  += pipShortBits; } break;
						case DSP:  { this.dspSensitiveInterconnectionBits_short  += pipShortBits; } break;
						default: break;
					}
                     
 					if(tileSensitiveBits.containsKey(pip.getTile().getName()))
 					{
 						int value = tileSensitiveBits.get(pip.getTile().getName()).intValue();
 						tileSensitiveBits.put(pip.getTile().getName(), value + pipShortBits);
 					}
 					else
 					{
 						tileSensitiveBits.put(pip.getTile().getName(), pipShortBits);
 					}
               }
        }
        
        try 
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.sbaFilePath,true));
			Set set = tileSensitiveBits.entrySet();
			Iterator i = set.iterator();			
			while(i.hasNext()) 
			{
				Map.Entry obj 	= (Map.Entry)i.next();
				//Integer bits 	= ((Integer) obj.getValue())/2;
				Integer bits 	= ((Integer) obj.getValue());
				String  site	= (String) obj.getKey();
				writer.write(site + "_SHORT = " + bits + "\n");
			}
			writer.close();
	    }
		catch (IOException e) {}
        
        this.dspSensitiveInterconnectionBits_short -= this.dspSensitiveInterconnectionBits_open;
        this.clbSensitiveInterconnectionBits_short -= this.clbSensitiveInterconnectionBits_open;
        this.clkSensitiveInterconnectionBits_short -= this.clkSensitiveInterconnectionBits_open;
        this.iobSensitiveInterconnectionBits_short -= this.iobSensitiveInterconnectionBits_open;
        this.bramSensitiveInterconnectionBits_short -= this.bramSensitiveInterconnectionBits_open;
        
        this.sensitiveBits_short = (long) (shortBits - this.sensitiveBits_open);
        return this.sensitiveBits_short;// - pipsUsedInSwithcBoxes);//((shortBits - pipsUsedInSwithcBoxes) / 2);  	
	}
	
	/*
	 * @f			long getShortBits_route()	
	 * @brief		This function calculates the short sensitive bits of the design.
	 * @param[in]	none
	 * @param[out]	none
	 * @return		long	:The number of the short sensitive bits
	 * @author
	 */
	public long getShortBits_routeOls() {
		long nullEndWires = 0;
		Collection<Net> nets = this.design.getNets();
		Iterator<Net> itr = nets.iterator();
		//HashSet<wireSite> wires = new HashSet<wireSite>();
		//HashSet<wireSite> netWires = new HashSet<wireSite>();
		
		ArrayList<wireSite> wires = new ArrayList<wireSite>();
		ArrayList<wireSite> netWires = new ArrayList<wireSite>();
		
		
		HashMap<String, Integer> interconnectionsShortMap = new HashMap<String, Integer>();
		HashMap<String, HashSet<Net>> interconnectionsNetsMap = new HashMap<String, HashSet<Net>>();
		
		
		double shortBits = 0;
		while(itr.hasNext())
		{
			Net net = itr.next();
			ArrayList<PIP> pips = net.getPIPs();
			netWires.clear();// = new ArrayList<Integer>();
	
			
			for(PIP pip : pips)
			{				
//				if((pip.getTile().toString().contains("CLB"))  ||
//				   (pip.getTile().toString().contains("BRAM")) ||
//				   (pip.getTile().toString().contains("DSP"))  ||
//				   (pip.getTile().toString().contains("IO")))
//					continue;
				if(!pip.getTile().toString().contains("INT_X")) continue;
				if(pip.getTile().toString().equals("INT_X0Y0")){
					System.out.println("INT_X0Y0 READY");
				}
				if(interconnectionsShortMap.get(pip.getTile().toString()) == null) {
					interconnectionsShortMap.put(pip.getTile().toString(), new Integer(0));
					interconnectionsNetsMap.put(pip.getTile().toString(), new HashSet<Net>());
					//System.out.println(interconnectionsNetsMap.get(pip.getTile().toString()).size());
				}
				
				wireSite ws1 = new wireSite(pip.getTile().getName(),pip.getStartWire());
				//wireSite ws2 = new wireSite(pip.getTile().getName(),pip.getEndWire());
				if(wires.contains(ws1))//|| wires.contains(ws2))
				{
					//System.out.println("Short in " + pip.getTile().getName());
					shortBits++;
					if(interconnectionsShortMap.get(pip.getTile().toString()) != null) {
						Integer sh = interconnectionsShortMap.get(pip.getTile().toString());
						sh++;
						interconnectionsShortMap.put(pip.getTile().toString(),sh);//, new Integer[2]);
					}
				}
				// Get all the connections for the PIP StartWire
//				for(WireConnection wConnection : pip.getTile().getWireConnections(pip.getStartWire()))
//				{
//					netWires.add(new wireSite(pip.getTile().getName(),wConnection.getWire()));
//				}
				
				WireConnection[] endWires = pip.getTile().getWireConnections(pip.getEndWire());
				if(endWires != null) {
					// Get all the connections for the PIP EndWire
					for(WireConnection wConnection : pip.getTile().getWireConnections(pip.getEndWire()))
					{
						//netWires.add(wConnection.getWire());
						netWires.add(new wireSite(pip.getTile().getName(),wConnection.getWire()));
					}
				} else {
					nullEndWires++;
				}
				if(interconnectionsNetsMap.get(pip.getTile().toString()) != null) {
					HashSet<Net> intNets = interconnectionsNetsMap.get(pip.getTile().toString());
					intNets.add(net);
					interconnectionsNetsMap.put(pip.getTile().toString(), intNets);
				}
			}
			
			for(wireSite i : netWires)
			{					
				if(wires.contains(i))
					shortBits++;
			}
			
			for(wireSite i : netWires)
			{					
				//if(!wires.contains(i))
					wires.add(i);
			}			
		}
		Set<String> interconNames = interconnectionsShortMap.keySet();
		int shortvalue = 0;
		for(String intName : interconNames){
			System.out.println(intName + " has " +
					interconnectionsShortMap.get(intName) + " short bits and " + interconnectionsNetsMap.get(intName).size() + " nets");
			shortvalue += interconnectionsShortMap.get(intName);
		}
		System.out.println("shortvalue = " + shortvalue);
		System.out.println("Null End WireConnection[] = " + nullEndWires);
		System.out.println("wires size = " + wires.size());
		System.out.println("netwires size = " + netWires.size());
		System.out.println("nets size = " + nets.size());
		return (int)shortBits;		
	}
	

	
	/**
	 * @brief		This method calculates the sensitive bits for the CLB resources of the targeting device.
	 * @return		long	:The number of the sensitive bits
	 */
	private long getCLBSensitiveBits()
	{			
		long sensitiveBits = 0;
		//long lutUsed = getLUT_used();
		//int usedSlices = getUsedSlices();
		//long lutBits = usedSlices * 256;
		long lutUsedBits = getLUT_used();//lutUsed;// * 64;
		//System.out.println("getLut LUT = " + lutUsed);
		
		//sensitiveBits = (saplacement.SA_placement.layout.getUsedSlices() * 320) - (lutBits - lutUsedBits); 
		sensitiveBits = lutUsedBits +  + this.sliceControlBitsTotal; //(usedSlices * 64);//(saplacement.SA_placement.layout.getUsedSlices() * 320) - (lutBits - lutUsedBits);
		return sensitiveBits;
	}
	
	/**
	 * @brief		This method calculates the sensitive bits for the IOB resources of the targeting device.
	 * @return		long	:The number of the sensitive bits
	 */
	private long getIOSensitiveBits()
	{
		long sensitiveBits = 0;
		Instance iob = null;
		Instance ologic = null;
		Instance iodelay = null;
		Instance ilogic = null;
		int iobSensitiveBits 		= 0;
		int iodelaySensitiveBits 	= 0;
		int ilogicSensitiveBits 	= 0;
		int ologicSensitiveBits 	= 0;
		int iob_Input				= 0;
		int iob_Output				= 0;
		int ilogic_routethrough		= 0;
		int ilogic_Ilogic			= 0;
		int ilogic_ISERDES			= 0;
		int ologic_routethrough		= 0;
		int ologic_Ologic			= 0;
		int ologic_Ologic2			= 0;
		int ologic_OSERDES			= 0;
		int iodelay_Input			= 0;
		int iodelay_Output			= 0;
		HashMap<String, Integer> tileSensitiveBits = new HashMap<String, Integer>();
		
		File file = new File(xmlPath);
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(file);
			String familyName = design.getFamilyName();
			NodeList nodes = doc.getElementsByTagName(familyName);
			
			if(nodes.getLength() <= 0) {return 0;}
			
		    Element element = (Element) nodes.item(0);
			// The child node of familyName  = {Slice, IO}
			NodeList io = element.getElementsByTagName("IO");
			NodeList nd;
			String value_str;
			
			nd = ((Element)io.item(0)).getElementsByTagName("IOB_INPUT");
			value_str = nd.item(0).getTextContent();
			iob_Input = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("IOB_OUTPUT");
			value_str = nd.item(0).getTextContent();
			iob_Output = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("ILOGIC_ROUTETHROUGH");
			value_str = nd.item(0).getTextContent();
			ilogic_routethrough = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("ILOGIC_ILOGIC");
			value_str = nd.item(0).getTextContent();
			ilogic_Ilogic = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("ILOGIC_ISERDES");
			value_str = nd.item(0).getTextContent();
			ilogic_ISERDES = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("OLOGIC_ROUTETHROUGH");
			value_str = nd.item(0).getTextContent();
			ologic_routethrough = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("OLOGIC_OLOGIC");
			value_str = nd.item(0).getTextContent();
			ologic_Ologic = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("OLOGIC_OLOGIC2");
			value_str = nd.item(0).getTextContent();
			ologic_Ologic2 = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("OLOGIC_OSERDES");
			value_str = nd.item(0).getTextContent();
			ologic_OSERDES = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("IODELAY_INPUT");
			value_str = nd.item(0).getTextContent();
			iodelay_Input = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("IODELAY_OUTPUT");
			value_str = nd.item(0).getTextContent();
			iodelay_Output = Integer.parseInt(value_str);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//ArrayList<ComplexBlock> designBlocks = loadCompexBlocks();

		for(ComplexBlock cBlock : blocks)
		{
			if(cBlock.getTileType().toString().contains("IOI"))
			{
				iobSensitiveBits 		= 0;
				iodelaySensitiveBits 	= 0;
				ilogicSensitiveBits 	= 0;
				ologicSensitiveBits 	= 0;
				
				// IO pin 1
				if(design.getDevice().getFamilyType() == FamilyType.VIRTEX6) {
					iodelay = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(0).getSite());
					iob		= cBlock.getBlocks().get(1).getInstance();
					ilogic  = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(2).getSite());
					ologic 	= design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(3).getSite());			
			
				} else {
					ologic 	= design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(0).getSite());
					iob		= cBlock.getBlocks().get(1).getInstance();
					iodelay = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(2).getSite());
					ilogic  = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(3).getSite());
				}
				
//				ologic 	= design.getInstanceAtPrimitiveSite(findBlocksLogic(cBlock, "OLOGIC"));
//				iob		= design.getInstanceAtPrimitiveSite(findBlocksLogic(cBlock, "IOB"));
//				iodelay = design.getInstanceAtPrimitiveSite(findBlocksLogic(cBlock, "OLOGIC"));
//				ilogic  = design.getInstanceAtPrimitiveSite(findBlocksLogic(cBlock, "OLOGIC"));
				
//				if(ologic != null) System.out.println(ologic.toString());
//				if(iob != null) System.out.println(iob.toString());
//				if(iodelay != null) System.out.println(iodelay.toString());
//				if(ilogic != null) System.out.println(ilogic.toString());
				
				Attribute INBUF;
				Attribute OUTBUF;
				
				if(iob != null)
				{
					INBUF  = find_attribute(iob.getAttributes(),"INBUF");
					OUTBUF = find_attribute(iob.getAttributes(),"OUTBUF");
					
					// Check IOB for input
					if((INBUF != null) && (!INBUF.getValue().equals("#OFF")))
					{
						iobSensitiveBits = iob_Input;
						// The case of ILOGIC is null and the IOB is set as input
						// means that ILOGIC is ROUTETHROUGH
						if((ilogic == null)  || (ilogic.getName().contains("DUMMY")))
						{
							ilogicSensitiveBits = ilogic_routethrough;
						}
						// There is a USER LOGIC circuit inside the ILOGIC block
						// Check if the USER LOGIC uses the ILOGIC block as logic function or ISERDES
						else
						{
							// Check if the USER LOGIC uses the ILOGIC block as logic function or ISERDES
							if(ilogic.getType().toString().contains("ISERDES"))
							{
								ilogicSensitiveBits = ilogic_ISERDES;
							}
							// Check if the ILOGIC is configured as USER LOGIC
							else if(ilogic.getType().toString().contains("ILOGIC"))
							{
								ilogicSensitiveBits = ilogic_Ilogic;
							}
						}
						
						// Check for IODELAY
						if(iodelay != null)
						{
							Attribute IDELAY_VALUE  = find_attribute(iodelay.getAttributes(),"IDELAY_VALUE");
							if((IDELAY_VALUE != null) && (!IDELAY_VALUE.getValue().equals("0")))
							{
								iodelaySensitiveBits += iodelay_Input;
							}
						}
					}
					// Check IOB for Output
					if((OUTBUF != null) && (!OUTBUF.getValue().equals("#OFF")))
					{
						iobSensitiveBits = iob_Output;
						// The case of OLOGIC is null and the IOB is set as input
						// means that OLOGIC is ROUTETHROUGH
						if((ologic == null) || (ologic.getName().contains("DUMMY")))
						{
							ologicSensitiveBits = ologic_routethrough;
						}
						// There the USER LOGIC inside the OLOGIC block
						// Check if the USER LOGIC uses the OLOGIC block as logic function or OSERDES
						else
						{
							// Check if the OLOGIC is configured as OSERDES circuit
							if(ologic.getType().toString().contains("OSERDES"))
							{
								ologicSensitiveBits = ologic_OSERDES;
							}
							// Check if the OLOGIC is configured as USER LOGIC
							else if(ologic.getType().toString().contains("OLOGIC"))
							{
								// Get TFF and OUTFF configuration to check if both FFs are used
								Attribute TFF  = find_attribute(ologic.getAttributes(),"TFFTYPE");
								Attribute OUTFF  = find_attribute(ologic.getAttributes(),"OUTFFTYPE");
								// Both TFF and OUTFF are used
								if((TFF != null)    && (!TFF.getValue().equals("#OFF")) &&
								   (OUTFF != null)  && (!OUTFF.getValue().equals("#OFF")))
								{
									ologicSensitiveBits = ologic_Ologic2;
								}
								// Single FF usage
								else
								{
									ologicSensitiveBits = ologic_Ologic;
								}
							}
						}
						
						// Check for IODELAY
						if(iodelay != null)
						{
							Attribute ODELAY_VALUE  = find_attribute(iodelay.getAttributes(),"ODELAY_VALUE");
							if((ODELAY_VALUE != null) && (!ODELAY_VALUE.getValue().equals("0")))
							{
								iodelaySensitiveBits += iodelay_Output;
							}
						}
					}
					
					sensitiveBits += (iobSensitiveBits + ilogicSensitiveBits + ologicSensitiveBits + iodelaySensitiveBits);
				}
				// IO pin 2
				if(design.getDevice().getFamilyType() == FamilyType.VIRTEX6) {
					iodelay = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(0).getSite());
					iob		= cBlock.getBlocks().get(1).getInstance();
					ilogic  = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(2).getSite());
					ologic 	= design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(3).getSite());			
			
				} else {
					ologic 	= design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(0).getSite());
					iob		= cBlock.getBlocks().get(1).getInstance();
					iodelay = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(2).getSite());
					ilogic  = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(3).getSite());
				}
				
//				if(ologic != null) System.out.println(ologic.toString());
//				if(iob != null) System.out.println(iob.toString());
//				if(iodelay != null) System.out.println(iodelay.toString());
//				if(ilogic != null) System.out.println(ilogic.toString());
				
				if(iob != null)
				{
					iobSensitiveBits 		= 0;
					iodelaySensitiveBits 	= 0;
					ilogicSensitiveBits 	= 0;
					ologicSensitiveBits 	= 0;
					
					INBUF  = find_attribute(iob.getAttributes(),"INBUF");
					OUTBUF = find_attribute(iob.getAttributes(),"OUTBUF");
					
					// Check IOB for input
					if((INBUF != null) && (!INBUF.getValue().equals("#OFF")))
					{
						iobSensitiveBits = iob_Input;
						// The case of ILOGIC is null and the IOB is set as input
						// means that ILOGIC is ROUTETHROUGH
						if((ilogic == null)  || (ilogic.getName().contains("DUMMY")))
						{
							ilogicSensitiveBits = ilogic_routethrough;
						}
						// Check if the USER LOGIC uses the ILOGIC block as logic function or ISERDES
						else
						{
							// Check if the ILOGIC is configured as ISERDES circuit
							if(ilogic.getType().toString().contains("ISERDES"))
							{
								ilogicSensitiveBits = ilogic_ISERDES;
							}
							// Check if the ILOGIC is configured as USER LOGIC
							else if(ilogic.getType().toString().contains("ILOGIC"))
							{
								ilogicSensitiveBits = ilogic_Ilogic;
							}
						}
						
						// Check for IODELAY
						if(iodelay != null)
						{
							Attribute IDELAY_VALUE  = find_attribute(iob.getAttributes(),"IDELAY_VALUE");
							if((IDELAY_VALUE != null) && (!IDELAY_VALUE.getValue().equals("0")))
							{
								iodelaySensitiveBits = iodelay_Input;
							}
						}
					}
					// Check IOB for Output
					if((OUTBUF != null) && (!OUTBUF.getValue().equals("#OFF")))
					{
						iobSensitiveBits = iob_Output;
						// The case of OLOGIC is null and the IOB is set as input
						// means that OLOGIC is ROUTETHROUGH
						if((ologic == null)  || (ologic.getName().contains("DUMMY")))
						{
							ologicSensitiveBits = ologic_routethrough;
						}
						// There the USER LOGIC inside the OLOGIC block
						// Check if the USER LOGIC uses the OLOGIC block as logic function or OSERDES
						else
						{
							// Check if the OLOGIC is configured as OSERDES circuit
							if(ologic.getType().toString().contains("OSERDES"))
							{
								ologicSensitiveBits = ologic_OSERDES;
							}
							// Check if the OLOGIC is configured as USER LOGIC
							else if(ologic.getType().toString().contains("OLOGIC"))
							{
								// Get TFF and OUTFF configuration to check if both FFs are used
								Attribute TFF  = find_attribute(ologic.getAttributes(),"TFFTYPE");
								Attribute OUTFF  = find_attribute(ologic.getAttributes(),"OUTFFTYPE");
								// Both TFF and OUTFF are used
								if((TFF != null)    && (!TFF.getValue().equals("#OFF")) &&
								   (OUTFF != null)  && (!OUTFF.getValue().equals("#OFF")))
								{
									ologicSensitiveBits = ologic_Ologic2;
								}
								// Single FF usage
								else
								{
									ologicSensitiveBits = ologic_Ologic;
								}
							}
						}
						
						// Check for IODELAY
						if(iodelay != null)
						{
							Attribute ODELAY_VALUE  = find_attribute(iob.getAttributes(),"ODELAY_VALUE");
							if((ODELAY_VALUE != null) && (!ODELAY_VALUE.getValue().equals("0")))
							{
								iodelaySensitiveBits += iodelay_Output;
							}
						}
					}
					
					sensitiveBits += (iobSensitiveBits + ilogicSensitiveBits + ologicSensitiveBits + iodelaySensitiveBits);
				}
			}
			
			if(ologic != null)
			{
				if(tileSensitiveBits.containsKey(ologic.getPrimitiveSiteName()))
				{
					int value = tileSensitiveBits.get(ologic.getPrimitiveSiteName()).intValue();
					tileSensitiveBits.put(ologic.getPrimitiveSiteName(), value + ologicSensitiveBits);
				}
				else
				{
					tileSensitiveBits.put(ologic.getPrimitiveSiteName(), ologicSensitiveBits);
				}
			}
			
			if(ilogic != null)
			{
				if(tileSensitiveBits.containsKey(ilogic.getPrimitiveSiteName()))
				{
					int value = tileSensitiveBits.get(ilogic.getPrimitiveSiteName()).intValue();
					tileSensitiveBits.put(ilogic.getPrimitiveSiteName(), value + ilogicSensitiveBits);
				}
				else
				{
					tileSensitiveBits.put(ilogic.getPrimitiveSiteName(), ilogicSensitiveBits);
				}
			}
			
			if(iodelay != null)
			{
				if(tileSensitiveBits.containsKey(iodelay.getPrimitiveSiteName()))
				{
					int value = tileSensitiveBits.get(iodelay.getPrimitiveSiteName()).intValue();
					tileSensitiveBits.put(iodelay.getPrimitiveSiteName(), value + iodelaySensitiveBits);
				}
				else
				{
					tileSensitiveBits.put(iodelay.getPrimitiveSiteName(), iodelaySensitiveBits);
				}
			}
			
			if(iob != null)
			{
				if(tileSensitiveBits.containsKey(iob.getPrimitiveSiteName()))
				{
					int value = tileSensitiveBits.get(iob.getPrimitiveSiteName()).intValue();
					tileSensitiveBits.put(iob.getPrimitiveSiteName(), value + iobSensitiveBits);
				}
				else
				{
					tileSensitiveBits.put(iob.getPrimitiveSiteName(), iobSensitiveBits);
				}
			}
			
			
		}
        try 
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.sbaFilePath, true));
			Set set = tileSensitiveBits.entrySet();
			Iterator i = set.iterator();			
			while(i.hasNext()) 
			{
				Map.Entry obj 	= (Map.Entry)i.next();
				Integer bits 	= (Integer) obj.getValue();
				String  site	= (String) obj.getKey();
				writer.write(site + " = " + bits + "\n");
			}
			writer.close();
	    }
		catch (IOException e) {}
		
		return sensitiveBits;
	}
	
	/**
	 * @brief		Overloaded method. Calculates the sensitive bits for a specific IOB (PrimitiveSite and instance) resource of the targeting device.
	 * @return		long	:The number of the sensitive bits
	 */
	private long getIOSensitiveBits(PrimitiveSite iobSite, Instance iobInstance)
	{
		long sensitiveBits = 0;
		Instance iob;
		Instance ologic;
		Instance iodelay;
		Instance ilogic;
		int iobSensitiveBits 		= 0;
		int iodelaySensitiveBits 	= 0;
		int ilogicSensitiveBits 	= 0;
		int ologicSensitiveBits 	= 0;
		int iob_Input				= 0;
		int iob_Output				= 0;
		int ilogic_routethrough		= 0;
		int ilogic_Ilogic			= 0;
		int ilogic_ISERDES			= 0;
		int ologic_routethrough		= 0;
		int ologic_Ologic			= 0;
		int ologic_Ologic2			= 0;
		int ologic_OSERDES			= 0;
		int iodelay_Input			= 0;
		int iodelay_Output			= 0;
			
		File file = new File(xmlPath);
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(file);
			String familyName = design.getFamilyName();
			NodeList nodes = doc.getElementsByTagName(familyName);
			
			if(nodes.getLength() <= 0) {return 0;}
			
		    Element element = (Element) nodes.item(0);
			// The child node of familyName  = {Slice, IO}
			NodeList io = element.getElementsByTagName("IO");
			NodeList nd;
			String value_str;
			
			nd = ((Element)io.item(0)).getElementsByTagName("IOB_INPUT");
			value_str = nd.item(0).getTextContent();
			iob_Input = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("IOB_OUTPUT");
			value_str = nd.item(0).getTextContent();
			iob_Output = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("ILOGIC_ROUTETHROUGH");
			value_str = nd.item(0).getTextContent();
			ilogic_routethrough = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("ILOGIC_ILOGIC");
			value_str = nd.item(0).getTextContent();
			ilogic_Ilogic = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("ILOGIC_ISERDES");
			value_str = nd.item(0).getTextContent();
			ilogic_ISERDES = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("OLOGIC_ROUTETHROUGH");
			value_str = nd.item(0).getTextContent();
			ologic_routethrough = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("OLOGIC_OLOGIC");
			value_str = nd.item(0).getTextContent();
			ologic_Ologic = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("OLOGIC_OLOGIC2");
			value_str = nd.item(0).getTextContent();
			ologic_Ologic2 = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("OLOGIC_OSERDES");
			value_str = nd.item(0).getTextContent();
			ologic_OSERDES = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("IODELAY_INPUT");
			value_str = nd.item(0).getTextContent();
			iodelay_Input = Integer.parseInt(value_str);
			
			nd = ((Element)io.item(0)).getElementsByTagName("IODELAY_OUTPUT");
			value_str = nd.item(0).getTextContent();
			iodelay_Output = Integer.parseInt(value_str);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		for(ComplexBlock cBlock : blocks)
		{
			if(cBlock.getTileType().toString().contains("IOI"))
			{
				iobSensitiveBits 		= 0;
				iodelaySensitiveBits 	= 0;
				ilogicSensitiveBits 	= 0;
				ologicSensitiveBits 	= 0;
				
				// IO pin 1
				if(design.getDevice().getFamilyType() == FamilyType.VIRTEX6) {
					iodelay = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(0).getSite());
					iob		= cBlock.getBlocks().get(1).getInstance();
					ilogic  = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(2).getSite());
					ologic 	= design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(3).getSite());			
			
				} else {
					ologic 	= design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(0).getSite());
					iob		= cBlock.getBlocks().get(1).getInstance();
					iodelay = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(2).getSite());
					ilogic  = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(3).getSite());
				}
				
				
				System.out.println(ologic.toString());
				System.out.println(iob.toString());
				System.out.println(iodelay.toString());
				System.out.println(ilogic.toString());
				
				Attribute INBUF;
				Attribute OUTBUF;
				
				if(iob != null)
				{
					INBUF  = find_attribute(iob.getAttributes(),"INBUF");
					OUTBUF = find_attribute(iob.getAttributes(),"OUTBUF");
					
					// Check IOB for input
					if((INBUF != null) && (!INBUF.getValue().equals("#OFF")))
					{
						iobSensitiveBits = iob_Input;
						// The case of ILOGIC is null and the IOB is set as input
						// means that ILOGIC is ROUTETHROUGH
						if((ilogic == null)  || (ilogic.getName().contains("DUMMY")))
						{
							ilogicSensitiveBits = ilogic_routethrough;
						}
						// There is a USER LOGIC circuit inside the ILOGIC block
						// Check if the USER LOGIC uses the ILOGIC block as logic function or ISERDES
						else
						{
							// Check if the USER LOGIC uses the ILOGIC block as logic function or ISERDES
							if(ilogic.getType().toString().contains("ISERDES"))
							{
								ilogicSensitiveBits = ilogic_ISERDES;
							}
							// Check if the ILOGIC is configured as USER LOGIC
							else if(ilogic.getType().toString().contains("ILOGIC"))
							{
								ilogicSensitiveBits = ilogic_Ilogic;
							}
						}
						
						// Check for IODELAY
						if(iodelay != null)
						{
							Attribute IDELAY_VALUE  = find_attribute(iodelay.getAttributes(),"IDELAY_VALUE");
							if((IDELAY_VALUE != null) && (!IDELAY_VALUE.getValue().equals("0")))
							{
								iodelaySensitiveBits += iodelay_Input;
							}
						}
					}
					// Check IOB for Output
					if((OUTBUF != null) && (!OUTBUF.getValue().equals("#OFF")))
					{
						iobSensitiveBits = iob_Output;
						// The case of OLOGIC is null and the IOB is set as input
						// means that OLOGIC is ROUTETHROUGH
						if((ologic == null) || (ologic.getName().contains("DUMMY")))
						{
							ologicSensitiveBits = ologic_routethrough;
						}
						// There the USER LOGIC inside the OLOGIC block
						// Check if the USER LOGIC uses the OLOGIC block as logic function or OSERDES
						else
						{
							// Check if the OLOGIC is configured as OSERDES circuit
							if(ologic.getType().toString().contains("OSERDES"))
							{
								ologicSensitiveBits = ologic_OSERDES;
							}
							// Check if the OLOGIC is configured as USER LOGIC
							else if(ologic.getType().toString().contains("OLOGIC"))
							{
								// Get TFF and OUTFF configuration to check if both FFs are used
								Attribute TFF  = find_attribute(ologic.getAttributes(),"TFFTYPE");
								Attribute OUTFF  = find_attribute(ologic.getAttributes(),"OUTFFTYPE");
								// Both TFF and OUTFF are used
								if((TFF != null)    && (!TFF.getValue().equals("#OFF")) &&
								   (OUTFF != null)  && (!OUTFF.getValue().equals("#OFF")))
								{
									ologicSensitiveBits = ologic_Ologic2;
								}
								// Single FF usage
								else
								{
									ologicSensitiveBits = ologic_Ologic;
								}
							}
						}
						
						// Check for IODELAY
						if(iodelay != null)
						{
							Attribute ODELAY_VALUE  = find_attribute(iodelay.getAttributes(),"ODELAY_VALUE");
							if((ODELAY_VALUE != null) && (!ODELAY_VALUE.getValue().equals("0")))
							{
								iodelaySensitiveBits += iodelay_Output;
							}
						}
					}
					
					sensitiveBits += (iobSensitiveBits + ilogicSensitiveBits + ologicSensitiveBits + iodelaySensitiveBits);
				}
				// IO pin 2
				if(design.getDevice().getFamilyType() == FamilyType.VIRTEX6) {
					iodelay = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(0).getSite());
					iob		= cBlock.getBlocks().get(1).getInstance();
					ilogic  = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(2).getSite());
					ologic 	= design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(3).getSite());			
			
				} else {
					ologic 	= design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(0).getSite());
					iob		= cBlock.getBlocks().get(1).getInstance();
					iodelay = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(2).getSite());
					ilogic  = design.getInstanceAtPrimitiveSite(cBlock.getBlocks().get(3).getSite());
				}
			
				System.out.println(ologic.toString());
				System.out.println(iob.toString());
				System.out.println(iodelay.toString());
				System.out.println(ilogic.toString());
				
				if(iob != null)
				{
					iobSensitiveBits 		= 0;
					iodelaySensitiveBits 	= 0;
					ilogicSensitiveBits 	= 0;
					ologicSensitiveBits 	= 0;
					
					INBUF  = find_attribute(iob.getAttributes(),"INBUF");
					OUTBUF = find_attribute(iob.getAttributes(),"OUTBUF");
					
					// Check IOB for input
					if((INBUF != null) && (!INBUF.getValue().equals("#OFF")))
					{
						iobSensitiveBits = iob_Input;
						// The case of ILOGIC is null and the IOB is set as input
						// means that ILOGIC is ROUTETHROUGH
						if((ilogic == null)  || (ilogic.getName().contains("DUMMY")))
						{
							ilogicSensitiveBits = ilogic_routethrough;
						}
						// Check if the USER LOGIC uses the ILOGIC block as logic function or ISERDES
						else
						{
							// Check if the ILOGIC is configured as ISERDES circuit
							if(ilogic.getType().toString().contains("ISERDES"))
							{
								ilogicSensitiveBits = ilogic_ISERDES;
							}
							// Check if the ILOGIC is configured as USER LOGIC
							else if(ilogic.getType().toString().contains("ILOGIC"))
							{
								ilogicSensitiveBits = ilogic_Ilogic;
							}
						}
						
						// Check for IODELAY
						if(iodelay != null)
						{
							Attribute IDELAY_VALUE  = find_attribute(iob.getAttributes(),"IDELAY_VALUE");
							if(!IDELAY_VALUE.getValue().equals("0"))
							{
								iodelaySensitiveBits = iodelay_Input;
							}
						}
					}
					// Check IOB for Output
					if((OUTBUF != null) && (!OUTBUF.getValue().equals("#OFF")))
					{
						iobSensitiveBits = iob_Output;
						// The case of OLOGIC is null and the IOB is set as input
						// means that OLOGIC is ROUTETHROUGH
						if((ologic == null)  || (ologic.getName().contains("DUMMY")))
						{
							ologicSensitiveBits = ologic_routethrough;
						}
						// There the USER LOGIC inside the OLOGIC block
						// Check if the USER LOGIC uses the OLOGIC block as logic function or OSERDES
						else
						{
							// Check if the OLOGIC is configured as OSERDES circuit
							if(ologic.getType().toString().contains("OSERDES"))
							{
								ologicSensitiveBits = ologic_OSERDES;
							}
							// Check if the OLOGIC is configured as USER LOGIC
							else if(ologic.getType().toString().contains("OLOGIC"))
							{
								// Get TFF and OUTFF configuration to check if both FFs are used
								Attribute TFF  = find_attribute(ologic.getAttributes(),"TFFTYPE");
								Attribute OUTFF  = find_attribute(ologic.getAttributes(),"OUTFFTYPE");
								// Both TFF and OUTFF are used
								if((TFF != null)    && (!TFF.getValue().equals("#OFF")) &&
								   (OUTFF != null)  && (!OUTFF.getValue().equals("#OFF")))
								{
									ologicSensitiveBits = ologic_Ologic2;
								}
								// Single FF usage
								else
								{
									ologicSensitiveBits = ologic_Ologic;
								}
							}
						}
						
						// Check for IODELAY
						if(iodelay != null)
						{
							Attribute ODELAY_VALUE  = find_attribute(iob.getAttributes(),"ODELAY_VALUE");
							if(!ODELAY_VALUE.getValue().equals("0"))
							{
								iodelaySensitiveBits += iodelay_Output;
							}
						}
					}
					
					sensitiveBits += (iobSensitiveBits + ilogicSensitiveBits + ologicSensitiveBits + iodelaySensitiveBits);
				}
			}
		}
		
		return sensitiveBits;
	}

	/**
	 * Returns the number of used slices.
	 * @return int
	 */
	private int getUsedSlices() {
		Collection<Instance> instances = design.getInstances();
		int slices = 0;
		for(Instance instance : instances) {
			if(instance.getType().toString().contains("SLICE")) slices++;
		}
		return slices;
	}
	
	/**
	 * Gets an instance (only instances that reside on SLICES) and returns the control sensitive bits.
	 * @param inst
	 * @return int
	 */
	int getSLICE_controlSensitiveBits(Instance inst)
	{
		Collection<Attribute> 	attributes;
		int sensitiveBits = 0;
		
		if(inst == null) {return 0;}
		
		File file = new File(xmlPath);
		
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(file);
			String familyName = design.getFamilyName();
			NodeList nodes = doc.getElementsByTagName(familyName);
			
			if(nodes.getLength() <= 0) {return 0;}
			
		    Element element = (Element) nodes.item(0);
			// The child node of familyName  = {Slice, IO}
			NodeList slice = element.getElementsByTagName("Slice");
			
			attributes = inst.getAttributes();
			
			// LUTA
			Attribute A5 = find_attribute(attributes,"A5LUT");
			Attribute A6 = find_attribute(attributes,"A6LUT");
			// LUTB
			Attribute B5 = find_attribute(attributes,"B5LUT");
			Attribute B6 = find_attribute(attributes,"B6LUT");
			// LUTC
			Attribute C5 = find_attribute(attributes,"C5LUT");
			Attribute C6 = find_attribute(attributes,"C6LUT");
			// LUTC
			Attribute D5 = find_attribute(attributes,"D5LUT");
			Attribute D6 = find_attribute(attributes,"D6LUT");
			
			//AOUTMUX
			Attribute AOUTMUX = find_attribute(attributes,"AOUTMUX");
			//BOUTMUX
			Attribute BOUTMUX = find_attribute(attributes,"BOUTMUX");
			//BOUTMUX
			Attribute COUTMUX = find_attribute(attributes,"COUTMUX");
			//BOUTMUX
			Attribute DOUTMUX = find_attribute(attributes,"DOUTMUX");
			
			//AFF
			Attribute AFF = find_attribute(attributes,"AFF");
			//BFF
			Attribute BFF = find_attribute(attributes,"BFF");
			//CFF
			Attribute CFF = find_attribute(attributes,"CFF");
			//DFF
			Attribute DFF = find_attribute(attributes,"DFF");
			
			//CARRY
			Attribute CARRY = find_attribute(attributes,"CARRY4");
			
			int lutBits = 0;
			int ffBits  = 0;
			int globalBits = 0;
			int carryBits = 0;
			String name  = "";
			String value_str = "";
			int value = 0;
			boolean lutUsed = false;
			NodeList nd;
			
			if(inst.getPrimitiveSite().getType() == PrimitiveType.SLICEL) {name = "LUT_SLICEL";}
			else if(inst.getPrimitiveSite().getType() == PrimitiveType.SLICEM) {name = "LUT_SLICEM";}
			nd = ((Element)slice.item(0)).getElementsByTagName(name);
			value_str = nd.item(0).getTextContent();
			value = Integer.parseInt(value_str);
			
			boolean lutIsUsed = false;
//			if((A5 != null) && !A5.getValue().contains("#OFF")) { 
//			if(A5.getValue() != "#OFF") {
//				//System.out.println("Atribute = " + A5.getValue());
//				lutBits += value; 
//				lutIsUsed = true;
//				}
//			else
//			{
//				//System.out.println("Atribute = " + A5.getValue());
//				A5 = find_attribute(attributes,"A5LUT");
//				System.out.println("NULL " + inst.getPrimitiveSiteName());
//				
//			}
			
			if(((A5 != null) && (A5.getValue() != "#OFF")) || ((A6 != null) && (A6.getValue() != "#OFF"))){ lutBits += value; lutIsUsed = true;}
			if(((B5 != null) && (B5.getValue() != "#OFF")) || ((B6 != null) && (B6.getValue() != "#OFF"))){ lutBits += value; lutIsUsed = true;}
			if(((C5 != null) && (C5.getValue() != "#OFF")) || ((C6 != null) && (C6.getValue() != "#OFF"))){ lutBits += value; lutIsUsed = true;}
			if(((D5 != null) && (D5.getValue() != "#OFF")) || ((D6 != null) && (D6.getValue() != "#OFF"))){ lutBits += value; lutIsUsed = true;}
			

			
//			if((A6 != null) && !A6.getValue().equals("#OFF")) { lutBits += value; lutIsUsed = true;}
//			if((B5 != null) && !B5.getValue().equals("#OFF")) { lutBits += value; lutIsUsed = true;}
//			if((B6 != null) && !B6.getValue().equals("#OFF")) { lutBits += value; lutIsUsed = true;}
//			if((C5 != null) && !C5.getValue().equals("#OFF")) { lutBits += value; lutIsUsed = true;}
//			if((C6 != null) && !C6.getValue().equals("#OFF")) { lutBits += value; lutIsUsed = true;}
//			if((D5 != null) && !D5.getValue().equals("#OFF")) { lutBits += value; lutIsUsed = true;}
//			if((D6 != null) && !D6.getValue().equals("#OFF")) { lutBits += value; lutIsUsed = true;}
		
			if(inst.getPrimitiveSite().getType() == PrimitiveType.SLICEM)
			{
				if(lutIsUsed == true)
				{
					nd = ((Element)slice.item(0)).getElementsByTagName("LUT_SLICEM_GLOBAL"); 
					value_str = nd.item(0).getTextContent(); 
					globalBits += Integer.parseInt(value_str);
					lutUsed = true;
				}
			}
			
			nd = ((Element)slice.item(0)).getElementsByTagName("OUTMUX"); 
			value_str = nd.item(0).getTextContent();
			value = Integer.parseInt(value_str);
			if((AOUTMUX != null) && (AOUTMUX.getValue() != "#OFF")) { lutBits += value; }
			if((BOUTMUX != null) && (BOUTMUX.getValue() != "#OFF")) { lutBits += value; }
			if((COUTMUX != null) && (COUTMUX.getValue() != "#OFF")) { lutBits += value; }
			if((DOUTMUX != null) && (DOUTMUX.getValue() != "#OFF")) { lutBits += value; }
//			if(!BOUTMUX.getValue().equals("#OFF")) { lutBits += value; }
//			if(!COUTMUX.getValue().equals("#OFF")) { lutBits += value; }
//			if(!DOUTMUX.getValue().equals("#OFF")) { lutBits += value; }
			
			nd = ((Element)slice.item(0)).getElementsByTagName("FF"); 
			value_str = nd.item(0).getTextContent();
			value = Integer.parseInt(value_str);
			if((AFF != null) && (AFF.getValue() != "#OFF")) { ffBits += value; }
			if((BFF != null) && (BFF.getValue() != "#OFF")) { ffBits += value; }
			if((CFF != null) && (CFF.getValue() != "#OFF")) { ffBits += value; }
			if((DFF != null) && (DFF.getValue() != "#OFF")) { ffBits += value; }
//			if(!BFF.getValue().equals("#OFF")) { ffBits += value; }
//			if(!CFF.getValue().equals("#OFF")) { ffBits += value; }
//			if(!DFF.getValue().equals("#OFF")) { ffBits += value; }
			
			if( ((AFF != null) && (AFF.getValue() != "#OFF")) || 
				((BFF != null) && (BFF.getValue() != "#OFF")) ||
				((CFF != null) && (CFF.getValue() != "#OFF")) ||
				((DFF != null) && (DFF.getValue() != "#OFF"))) 
			{
				nd = ((Element)slice.item(0)).getElementsByTagName("FF_GLOBAL_CONTROL"); 
				value_str = nd.item(0).getTextContent(); 
				globalBits += Integer.parseInt(value_str);
				
				nd = ((Element)slice.item(0)).getElementsByTagName("CLKINV"); 
				value_str = nd.item(0).getTextContent();
				if(lutUsed){globalBits -= Integer.parseInt(value_str);}
			}
			
			if((CARRY != null) && (!CARRY.getValue().equals("#OFF")))
			{
				nd = ((Element)slice.item(0)).getElementsByTagName("CARRY"); 
				value_str = nd.item(0).getTextContent(); 
				carryBits += Integer.parseInt(value_str);				
			}
			
			int totalBits = lutBits + ffBits + globalBits + carryBits;
			return totalBits;
			
			
//			attributes.
//			for(Attribute attribute : attributes)
//			{
//				//Check if the attribute is used
//				if(!attribute.getValue().equals("#OFF")) 
//				{	
//					NodeList nd = ((Element)slice.item(0)).getElementsByTagName(attribute.getPhysicalName()); 
//					String value = nd.item(0).getTextContent(); 
//					sensitiveBits += Integer.parseInt(value);
//				}
//			}
		
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return sensitiveBits;
	}
	
	/**
	 * This method finds a specific attribute (by name) from an attribute list and returns it. 
	 * @param attributes
	 * @param attributeName
	 * @return
	 */
	Attribute find_attribute(Collection<Attribute> attributes, String attributeName)
	{
		Iterator<Attribute> itr = attributes.iterator();
		while(itr.hasNext())
		{
			Attribute atr = itr.next();
			if(atr.getPhysicalName().contains(attributeName)) {return atr;}
		}
		return null;
	}
		
	/**	
	 * This method calculates the number of the used LUT resources in the design.
	 * @return long :The number of the LUTs used in the design
	 */
	long getLUT_used()
	{
		ArrayList<Integer> 		lutUtil = new ArrayList<Integer>();
		int 					lutUsed = 0;
		int 					pinsUsed = 0;
		int 					lutBits  = 0;
		long 					bitsInLuts = 0;
		Collection<Instance> 	inst = this.design.getInstances();
		Collection<Attribute> 	atr;
		ArrayList<String> 		instancesB = new ArrayList<String>();
		String 					primitiveSite = "";
		int						sliceSensitiveBits = 0;
		HashMap<String, Integer> tileSensitiveBits = new HashMap<String, Integer>();
		int sliceControlBits 	 = 0;
		int undefined = 0;
		
		for(Instance i : inst)
		{			
			sliceSensitiveBits = 0;
			if((i.getType() == PrimitiveType.SLICE) 
				|| (i.getType() == PrimitiveType.SLICEL)
				|| (i.getType() == PrimitiveType.SLICEM)
				|| (i.getType() == PrimitiveType.SLICEX))
			{
				atr = i.getAttributes();
				instancesB.clear();
				sliceControlBits = getSLICE_controlSensitiveBits(i);
				sliceControlBitsTotal += sliceControlBits;
				
				for(Attribute a : atr)
				{
					pinsUsed = 0;
					lutBits  = 0;
					if(a.getPhysicalName().contains("A6LUT") ||
					   a.getPhysicalName().contains("A5LUT") ||
					   a.getPhysicalName().contains("AUSED"))
//					   a.getPhysicalName().contains("ACY0")  ||
//					   a.getPhysicalName().contains("AFF")   ||
//					   a.getPhysicalName().contains("AFFINIT") ||
//					   a.getPhysicalName().contains("AFFSR")   ||
//					   a.getPhysicalName().contains("AOUTMUX") ||
//					   a.getPhysicalName().contains("A5LUT")   ||
//					   a.getPhysicalName().contains("A5FFINIT") ||
//					   a.getPhysicalName().contains("A5FFSR")   ||
//					   a.getPhysicalName().contains("A5FFMUX"))
					{
						if(instancesB.contains("A"))
						{
							continue;
						}						
						
						if(!a.getValue().equals("#OFF"))
						{
							
							HashSet<Net> nets = i.getNetList();
							for(Net net : nets)
							{
								ArrayList<Pin> pins = net.getPins();
								for(Pin pin : pins)
								{
									if(pin.isOutPin()) continue;
									String pinName = pin.getName();
									
									if(pinName.contains("A1") ||
									   pinName.contains("A2") ||
									   pinName.contains("A3") ||
									   pinName.contains("A4") ||
									   pinName.contains("A5") ||
									   pinName.contains("A6")) 
									{
										if(pin.getInstance() != i) continue;
										primitiveSite = pin.getInstance().getPrimitiveSiteName();
										pinsUsed++;
									}
								}
							}							
							lutUsed++; instancesB.add("A");
						}
					}					
					else if(   a.getPhysicalName().contains("B5LUT") ||
							   a.getPhysicalName().contains("B6LUT") ||
							   a.getPhysicalName().contains("BUSED"))
//							   a.getPhysicalName().contains("BCY0"))//  ||
//							   a.getPhysicalName().contains("BFF")   ||
//							   a.getPhysicalName().contains("BFFINIT") ||
//							   a.getPhysicalName().contains("BFFSR")   ||
//							   a.getPhysicalName().contains("BOUTMUX") ||
//							   a.getPhysicalName().contains("B5FFINIT") ||
//							   a.getPhysicalName().contains("B5FFSR")   ||
//							   a.getPhysicalName().contains("B5FFMUX"))
							{
								if(instancesB.contains("B"))
								{
									continue;
								}
								
								if(!a.getValue().equals("#OFF"))
								{
									HashSet<Net> nets = i.getNetList();
									for(Net net : nets)
									{
										ArrayList<Pin> pins = net.getPins();
										for(Pin pin : pins)
										{
											if(pin.isOutPin()) continue;
											String pinName = pin.getName();
											if(pinName.contains("B1") ||
											   pinName.contains("B2") ||
											   pinName.contains("B3") ||
											   pinName.contains("B4") ||
											   pinName.contains("B5") ||
											   pinName.contains("B6"))
											{
												if(pin.getInstance() != i) continue;
												primitiveSite = pin.getInstance().getPrimitiveSiteName();
												pinsUsed++;
												if(pinsUsed > 60) System.out.println("pinsUsed > 60");
											}
										}
									}												
									lutUsed++; instancesB.add("B");
								}
					}
					
					else if(a.getPhysicalName().contains("C5LUT") ||
							   a.getPhysicalName().contains("C6LUT") ||
							   a.getPhysicalName().contains("BUSED"))
//							   a.getPhysicalName().contains("CCY0")  ||
//							   a.getPhysicalName().contains("CFF")   ||
//							   a.getPhysicalName().contains("CFFINIT") ||
//							   a.getPhysicalName().contains("CFFSR")   ||
//							   a.getPhysicalName().contains("COUTMUX") ||
//							   a.getPhysicalName().contains("C5LUT") ||
//							   a.getPhysicalName().contains("C5FFINIT") ||
//							   a.getPhysicalName().contains("C5FFSR")   ||
//							   a.getPhysicalName().contains("C5FFMUX"))
							{
								if(instancesB.contains("C"))
								{
									continue;
								}
								
								if(!a.getValue().equals("#OFF"))
								{
									HashSet<Net> nets = i.getNetList();
									for(Net net : nets)
									{
										ArrayList<Pin> pins = net.getPins();
										for(Pin pin : pins)
										{
											if(pin.isOutPin()) continue;
											String pinName = pin.getName();
											if(pinName.contains("C1") ||
											   pinName.contains("C2") ||
											   pinName.contains("C3") ||
											   pinName.contains("C4") ||
											   pinName.contains("C5") ||
											   pinName.contains("C6"))
											{
												if(pin.getInstance() != i) continue;
												primitiveSite = pin.getInstance().getPrimitiveSiteName();
												pinsUsed++;
												if(pinsUsed > 60) System.out.println("pinsUsed > 60");
											}
										}
									}													
									lutUsed++; instancesB.add("C");
								}
					}
					
					else if(a.getPhysicalName().contains("D5LUT") ||
							   a.getPhysicalName().contains("D6LUT") ||
							   a.getPhysicalName().contains("DUSED"))
//							   a.getPhysicalName().contains("DCY0")  ||
//							   a.getPhysicalName().contains("DFF")   ||
//							   a.getPhysicalName().contains("DFFINIT") ||
//							   a.getPhysicalName().contains("DFFSR")   ||
//							   a.getPhysicalName().contains("DOUTMUX") ||
//							   a.getPhysicalName().contains("D5LUT") ||
//							   a.getPhysicalName().contains("D5FFINIT") ||
//							   a.getPhysicalName().contains("D5FFSR")   ||
//							   a.getPhysicalName().contains("D5FFMUX"))
							{
								if(instancesB.contains("D"))
								{
									continue;
								}
								
								if(!a.getValue().equals("#OFF"))
								{
									HashSet<Net> nets = i.getNetList();
									for(Net net : nets)
									{
										ArrayList<Pin> pins = net.getPins();
										for(Pin pin : pins)
										{
											if(pin.isOutPin()) continue;
											String pinName = pin.getName();
											if(pinName.contains("D1") ||
											   pinName.contains("D2") ||
											   pinName.contains("D3") ||
											   pinName.contains("D4") ||
											   pinName.contains("D5") ||
											   pinName.contains("D6")) 
											{
												if(pin.getInstance() != i) continue;
												primitiveSite = pin.getInstance().getPrimitiveSiteName();
												pinsUsed++;
												if(pinsUsed > 60) System.out.println("pinsUsed > 60");
											}
										}
									}
									
									lutUsed++; instancesB.add("D");
								}
					}
					else
					{
						undefined++;
					}
					
					
					
					if(pinsUsed != 0)
					{
						lutBits = (int)(Math.pow(2, pinsUsed));
						sliceSensitiveBits += lutBits;
						bitsInLuts += lutBits;
						//System.out.println("LUT " + a.getPhysicalName().substring(0, 1) + " in " + i.getPrimitiveSite().getName() + " : Input pins = " + pinsUsed + ", Sensitive bits = " + lutBits);
						lutUtil.add(pinsUsed);
						//System.out.println("LUT " + a.getPhysicalName().substring(0, 1) + " in " + primitiveSite + " : Input pins = " + pinsUsed + ", Sensitive bits = " + lutBits);
					}
					else
					{
						lutUtil.add(pinsUsed);
					}
				}				
				if(tileSensitiveBits.containsKey(i.getPrimitiveSiteName()))
				{
					int value = tileSensitiveBits.get(i.getPrimitiveSiteName()).intValue();
					tileSensitiveBits.put(i.getPrimitiveSiteName(), value + sliceSensitiveBits + sliceControlBits);
				}
				else
				{
					tileSensitiveBits.put(i.getPrimitiveSiteName(), sliceSensitiveBits + sliceControlBits);
				}
			}
		}
		
		//System.out.println("Undefined attribute: " + undefined);
        try 
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.sbaFilePath, true));
			Set set = tileSensitiveBits.entrySet();
			Iterator i = set.iterator();			
			while(i.hasNext()) 
			{
				Map.Entry obj 	= (Map.Entry)i.next();
				Integer bits 	= (Integer) obj.getValue();
				String  site	= (String) obj.getKey();
				writer.write(site + " = " + bits + "\n");
			}
			writer.close();
	    }
		catch (IOException e) {}
		
		//#####################################################################################
    	
		//int usedSlices = getUsedSlices();
		//long lutUsedBitsold = lutUsed * 64;
		//long sensitiveBits = lutUsedBitsold + (usedSlices * 64);//(saplacement.SA_placement.layout.getUsedSlices() * 320) - (lutBits - lutUsedBits);
       // int lutunknown = 0;
        int lut1 = 0;//Collections.frequency(lutUtil, 1);
		int lut2 = 0;//Collections.frequency(lutUtil, 2);
		int lut3 = 0;//Collections.frequency(lutUtil, 3);
		int lut4 = 0;//Collections.frequency(lutUtil, 4);
		int lut5 = 0;//Collections.frequency(lutUtil, 5);
		int lut6 = 0;//Collections.frequency(lutUtil, 6);
		
		for(Integer lut : lutUtil) 
		{
			if(lut == 1) lut1++;
			else if(lut == 2) lut2++;
			else if(lut == 3) lut3++;
			else if(lut == 4) lut4++;
			else if(lut == 5) lut5++;
			else if(lut == 6) lut6++;
		}
			//else {lutunknown++;}
		
		
	
        System.out.println("");
        System.out.println("=====================================================================================================================");
		System.out.println("=====================================================================================================================");
        
		System.out.println("\nLUT Utilization\n");
		System.out.printf("%-30.30s  %30.30s%n","Used Lut", lutUsed);//lutUsed);
		//System.out.printf("%-30.30s  %30.30s%n","Used Slices", usedSlices);
		//System.out.printf("%-30.30s  %30.30s%n","Old Value", sensitiveBits);
		System.out.printf("%-30.30s  %30.30s%n","LUT sensitive bits", bitsInLuts);
		System.out.println("");	
		System.out.println("Lut with 1 Input = " + lut1 + "\t\tLut 1 Utilization = " + String.format("%6.2f", ( ((double)lut1 / (double)lutUsed) * 100)) + "%");
		System.out.println("Lut with 2 Input = " + lut2 + "\t\tLut 2 Utilization = " + String.format("%6.2f", ( ((double)lut2 / (double)lutUsed) * 100)) + "%");
		System.out.println("Lut with 3 Input = " + lut3 + "\t\tLut 3 Utilization = " + String.format("%6.2f", ( ((double)lut3 / (double)lutUsed) * 100)) + "%");
		System.out.println("Lut with 4 Input = " + lut4 + "\t\tLut 4 Utilization = " + String.format("%6.2f", ( ((double)lut4 / (double)lutUsed) * 100)) + "%");
		System.out.println("Lut with 5 Input = " + lut5 + "\t\tLut 5 Utilization = " + String.format("%6.2f", ( ((double)lut5 / (double)lutUsed) * 100)) + "%");
		System.out.println("Lut with 6 Input = " + lut6 + "\t\tLut 6 Utilization = " + String.format("%6.2f", ( ((double)lut6 / (double)lutUsed) * 100)) + "%");
		//System.out.println("Lut with 6 Input = " + lutunknown + "\t\tLut 0 Utilization = " + String.format("%6.2f", ( ((double)lutunknown / (double)lutUsed) * 100)) + "%");
	    //######################################################################################
		return bitsInLuts;
	}
	
	/**
	 * Calculates the sensitive antennas bits of a routed design. 
	 * @return long  :The sensitive antenna bits.
	 */
	private long calculateClock_antennas(){
		long clockAntennas = 0;
		WireEnumerator we = design.getWireEnumerator();
		Collection<Net> nets = this.design.getNets();
        ArrayList<PIP> pips;
		for(Net net : nets)
		{
			pips = net.getPIPs();
			for(PIP pip : pips)
			{	
//				if(pip.getTile().getName().contains("INT_X"))
//				{	
				if(we.getWireDirection(pip.getStartWire()) != WireDirection.CLK) continue;
				if(we.getWireDirection(pip.getEndWire()) != WireDirection.CLK) continue;
				clockAntennas += pip.getTile().getWireConnections(pip.getStartWire()).length;
				clockAntennas += pip.getTile().getWireConnections(pip.getEndWire()).length;

//				}
			}
		} 
		//System.out.println("Input Clocks  = " + j);
		return clockAntennas;
	}
	
	/**
	 * Calculates only the sensitive antennas specified by the given text file (userfilePath).
	 * @param userfilePath
	 * @return
	 */
	private int calculateUserNet_antennas(String userfilePath)
	{
		File file = new File(userfilePath);
		BufferedReader in = null;
        String line;
        int netAntennas = 0;
		try
		{			
			in = new BufferedReader(new FileReader(file));
			for(;(line = in.readLine()) != null; )
			{
	            if(!line.contains("NET_ANTENNA")) continue;
	            
	            line = line.replace("NET_ANTENNA", "");
	            line = line.trim().toUpperCase();	            
	            System.out.println("Line = " + line);
	            if(line.equals("")) continue;
	            
	            Collection<Net> nets = this.design.getNets();
	            ArrayList<PIP> pips;
	    		for(Net net : nets)
	    		{
	    			if(!net.getName().toUpperCase().contains((line))) continue;
	    			pips = net.getPIPs();
	    			for(PIP pip : pips)
	    			{	
	    				if(pip.getTile().getName().contains("INT_X"))
	    				{
	    					netAntennas += pip.getTile().getWireConnections(pip.getStartWire()).length - 1;
	    					netAntennas += pip.getTile().getWireConnections(pip.getEndWire()).length - 1;
	    				}
	    			}
	    		} 
	        }
		}
		catch (FileNotFoundException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return netAntennas;
	}
	
	int getLUT_used2()
	{
		int lutUsed = 0;
		Collection<Instance> inst = this.design.getInstances();
		Collection<Attribute> atr;
		//ArrayList<String> lutName = new ArrayList<String>();
		ArrayList<String> instancesB = new ArrayList<String>();
			
		for(Instance i : inst)
		{
//			if((i.getPrimitiveSite().getType() == PrimitiveType.SLICEL) ||
//			   (i.getPrimitiveSite().getType() == PrimitiveType.SLICEM))
			
			if(i.getType().toString().contains("SLICE")){
				atr = i.getAttributes();
				instancesB.clear();
				for(Attribute a : atr)
				{
					if(a.getPhysicalName().contains("A5LUT") ||
					   a.getPhysicalName().contains("A6LUT") ||
					   a.getPhysicalName().contains("ACY0")  ||
					   a.getPhysicalName().contains("AFF")   ||
					   a.getPhysicalName().contains("AFFINIT") ||
					   a.getPhysicalName().contains("AFFSR")   ||
					   a.getPhysicalName().contains("AOUTMUX") ||
					   a.getPhysicalName().contains("A5LUT"))
					{
						if(instancesB.contains("A"))
						{
							continue;
						}
						if(!a.getValue().equals("#OFF")) {lutUsed++; instancesB.add("A");}
					}
					
					if(a.getPhysicalName().contains("B5LUT") ||
							   a.getPhysicalName().contains("B6LUT") ||
							   a.getPhysicalName().contains("BCY0")  ||
							   a.getPhysicalName().contains("BFF")   ||
							   a.getPhysicalName().contains("BFFINIT") ||
							   a.getPhysicalName().contains("BFFSR")   ||
							   a.getPhysicalName().contains("BOUTMUX") ||
							   a.getPhysicalName().contains("B5LUT"))
							{
								if(instancesB.contains("B"))
								{
									continue;
								}
								if(!a.getValue().equals("#OFF")) {lutUsed++; instancesB.add("B");}
					}
					
					if(a.getPhysicalName().contains("C5LUT") ||
							   a.getPhysicalName().contains("C6LUT") ||
							   a.getPhysicalName().contains("CCY0")  ||
							   a.getPhysicalName().contains("CFF")   ||
							   a.getPhysicalName().contains("CFFINIT") ||
							   a.getPhysicalName().contains("CFFSR")   ||
							   a.getPhysicalName().contains("COUTMUX") ||
							   a.getPhysicalName().contains("C5LUT"))
							{
								if(instancesB.contains("C"))
								{
									continue;
								}
								if(!a.getValue().equals("#OFF")) {lutUsed++; instancesB.add("C");}
					}
					
					if(a.getPhysicalName().contains("D5LUT") ||
							   a.getPhysicalName().contains("D6LUT") ||
							   a.getPhysicalName().contains("DCY0")  ||
							   a.getPhysicalName().contains("DFF")   ||
							   a.getPhysicalName().contains("DFFINIT") ||
							   a.getPhysicalName().contains("DFFSR")   ||
							   a.getPhysicalName().contains("DOUTMUX") ||
							   a.getPhysicalName().contains("D5LUT"))
							{
								if(instancesB.contains("D"))
								{
									continue;
								}
								if(!a.getValue().equals("#OFF")) {lutUsed++; instancesB.add("D");}
					}
				}
			}
		}
		System.out.println("Used Lut = \t" + lutUsed);
		return lutUsed;
	}
	
	/*
	 * @f			int getLUT_used()	
	 * @brief		This function calculates the number of the used LUT resources in the design.
	 * @param[in]	none
	 * @param[out]	none
	 * @return		int		:The number of the LUTs used in the design
	 * @author
	 */
	int getLUT_usedOld()
	{
		int lutUsed = 0;
//		int lutNotUsed = 0;
		Collection<Instance> inst = this.design.getInstances();
		Collection<Attribute> atr;
		//ArrayList<String> lutName = new ArrayList<String>();
		
		for(Instance i : inst)
		{
//			if((i.getPrimitiveSite().getType() == PrimitiveType.SLICEL) ||
//			   (i.getPrimitiveSite().getType() == PrimitiveType.SLICEM))
			
			if(i.getType().toString().contains("SLICE")){
				atr = i.getAttributes();
				for(Attribute a : atr)
				{
					//System.out.println(a.getLogicalName() + " - " + a.getPhysicalName() + " - " + a.getValue());
					if((a.getPhysicalName().contains("AUSED") ||
					    a.getPhysicalName().contains("BUSED") ||
					    a.getPhysicalName().contains("CUSED") ||
					    a.getPhysicalName().contains("DUSED")))
					{
						System.out.println(a.getLogicalName() + " - " + a.getPhysicalName() + " - " + a.getValue());
					    if(!a.getValue().contains("OFF"))
						{
							//System.out.println(a.getLogicalName() + " - " + a.getPhysicalName() + " - " + a.getValue());
							lutUsed++;
						}
//					    else
//					    	lutNotUsed++;					    
					}
				}
			}
		}		
		return lutUsed;
	}
	

	
	/*=========================================================================
	 * Place Analysis Methods
	 *=========================================================================*/
	
	/**
	 * Post–placement analysis of the interconnection configuration bits: 
	 * It takes into consideration the actual sites of the used resources obtained by the placement process (extracted from the XDL netlist)
	 * and the goals of the routing algorithm and analyses the possibility of a net to become open- or short-wired with another net due to a
	 * soft error in a programmable interconnection point (PIP). So, it estimates the vulnerability of the interconnection configuration bits
	 * before the final routing. This tool is mainly based on sensitivity analysis methods previously proposed in the literature in the paper
	 * Abdul-Aziz, M.A.; Tahoori, M.B., "Soft error reliability aware placement and routing for FPGAs," IEEE International Test Conference (ITC), Nov. 2010. 
	 * This method prints to the console the post-placement Analysis: 
	 * 1)Open sensitive bits (manhattan distance * 3), 
	 * 2)Open sensitive bits (manhattan distance * q(terminals)),  
	 * 3)Open sensitive bits (manhattan distance * q(1.5 * terminals)),  
	 * 4)Short sensitive bits,  
	 * 5)Total sensitive bits. Sum of the [resource + Short + Open (manhattan distance * 3) ] sensitive bits, 
	 * 6)Total sensitive bits. Sum of the [resource + Short + Open q(terminals) ] sensitive bits, 
	 * 7)Total sensitive bits. Sum of the [resource + Short + Open q(1.5 * terminals) ] sensitive bits.
	 */
	public void placementAnalysis() {
		
		System.out.println("\nPlace Analysis\n");
		double openBits = getOpenBitsEst1_place();
		double shortBits = getShortBitsEst2_place();
		System.out.printf("%-30.30s  %30.30s%n","Open bits =", openBits);
		System.out.printf("%-30.30s  %30.30s%n","Short bits =", shortBits);
		System.out.printf("%-30.30s  %30.30s%n","Total bits =", (openBits + shortBits + this.resourceBits));
		System.out.println("\n=====================================================================================================================");
		System.out.println("=====================================================================================================================");
		System.out.println("");
	}
	
//	/**
//	 * Brief description : Calculates the open sensitive bits of a placed design. Tahoori Estimation1
//	 * @return long
//	 */
//	private double getOpenBitsEst1_place() {
//		
//		double openBits = 0;
//		
//		for(Bbox bbox : netBbList) {
//			//System.out.println("Terminals = " + bbox.getTerminals());
//			openBits += 3 * ( Math.abs(bbox.getxMin() - bbox.getxMax()) + Math.abs(bbox.getyMax() - bbox.getyMin()));
//		}
//		return openBits;
//	}
	
	/**
	 * Returns the summary of all terminals of the tiles in the design.
	 * @return
	 */
	private double getTerminals() {
		double terminals = 0;
		for(NetBB bbox : netBbList) {
			terminals += bbox.getTerminals();
		}
		return terminals;
	}
	
	/**
	 * Returns a double[] array with the open sensitive bits of the placed design.
	 * We have implemented the first method of the paper "Soft error reliability aware placement and routing for FPGA" to calculate the open sensitive bits.
	 * 
	 * Double openBits = (manhattan distance * q(terminals), 
	 * 
	 * @return double[]
	 */
	private double getOpenBitsEst1_place() {
		double values = 0;
		double openBits = 0;

		for(NetBB bbox : netBbList) {
			//System.out.println("Terminals = " + bbox.getTerminals());
			openBits += Maths.getCrossCount(bbox.getTerminals()) * ( Math.abs(bbox.getxMin() - bbox.getxMax()) + Math.abs(bbox.getyMax() - bbox.getyMin()));
		}
		 double terminals = getTerminals();
		 values = openBits + terminals;
		 return values;
	}
	

	/**
	 * Returns a Bounding Box indicating the overLap of two Bounding Boxes in the device. 
	 * If overlap does not exist, the method returns the bounding box with its initial values (i.e. xMax, xMin, yMax, yMax, terminals = 0)
	 * @param bboxA
	 * @param bboxB
	 * @return Bbox
	 */
	private NetBB calcOverLap(NetBB bboxA, NetBB bboxB) {

		NetBB overLapBB = new NetBB();

		
		for(Integer x = bboxA.getxMin(); x <= bboxA.getxMax(); x++) {
			for(Integer y = bboxA.getyMin(); y <= bboxA.getyMax(); y++) {
				fpga[x][y]++;
			}
		}
		
		for(Integer x = bboxB.getxMin(); x <= bboxB.getxMax(); x++) {
			for(Integer y = bboxB.getyMin(); y <= bboxB.getyMax(); y++) {
				fpga[x][y]++;
			}
		}
		
		int xMaxBB = bboxB.getxMax();
		if(bboxA.getxMax() > xMaxBB) xMaxBB = bboxA.getxMax();
		
		int yMaxBB = bboxB.getyMax();
		if(bboxA.getyMax() > yMaxBB) yMaxBB = bboxA.getyMax();
		
		int xMinBB = bboxB.getxMin();
		if(bboxA.getxMin() < xMinBB) xMinBB = bboxA.getxMin();
		
		int yMinBB = bboxB.getyMin();
		if(bboxA.getyMin() < yMinBB) yMinBB = bboxA.getyMin();
		
		ArrayList<Integer> xx = new ArrayList<Integer>();
		ArrayList<Integer> yy = new ArrayList<Integer>();
		
		for(Integer x = xMinBB; x <= xMaxBB; x++) {
			for(Integer y = yMinBB; y <= yMaxBB; y++) {
				if(fpga[x][y] > 1) {
					xx.add(x);
					yy.add(y);
				}
				fpga[x][y] = 0;
			}
		}
		
			
		if(xx.isEmpty()) {
			//System.out.println("Null xx");
			return overLapBB;
		}
		if(yy.isEmpty()){
			//System.out.println("Null yy");
			return overLapBB;
		}
		
		overLapBB.setxMin( xx.get(0) );
		overLapBB.setxMax( xx.get((int)xx.size()-1) );
		overLapBB.setyMin( yy.get(0) );
		overLapBB.setyMax( yy.get((int)yy.size()-1) );
		
		return overLapBB;
	}
	
	/**
	 * Returns the short sensitive bits of a placed design.
	 * We have implemented the second method of the paper "Soft error reliability aware placement and routing for FPGA" to calculate the short sensitive bits.
	 * @return Double :number of short sensitive bits
	 */
	private Double getShortBitsEst2_place() {
		Double shortBits = 0D;
		List<NetBB> netBbList2 = new ArrayList<NetBB>(netBbList);
		NetBB overLapBB;
		int l;
		int w;
		Double propAValue = 0.0;
		Double propBValue = 0.0;
		for(int a = 0; a < netBbList.size(); a++) {
			NetBB bboxA = new NetBB(netBbList.get(a));
			netBbList2.remove(0);
			for(int b = 0; b < netBbList2.size(); b++) {
				NetBB bboxB = netBbList2.get(b);	
				if(bboxA.getxMax() == bboxA.getxMin() && bboxA.getyMax() == bboxA.getyMin()) continue; 
				if(bboxB.getxMax() == bboxB.getxMin() && bboxB.getyMax() == bboxB.getyMin()) continue;
				overLapBB = new NetBB(calcOverLap(bboxA, bboxB));			
				l = overLapBB.getxMax() - overLapBB.getxMin();
				w = overLapBB.getyMax() - overLapBB.getyMin();
				if( (l == 0) && (w == 0) ) continue;
				MultiKeyMap<Integer,Double> propA  = calcProp(bboxA);
				MultiKeyMap<Integer,Double> propB  = calcProp(bboxB);
				for(Integer x = overLapBB.getxMin(); x < overLapBB.getxMax(); x++) {
					for(Integer y = overLapBB.getyMin(); y < overLapBB.getyMax(); y++) {
						propAValue = (Double) propA.get(x, y);
						propBValue = (Double) propB.get(x, y);
//						if(propBValue.equals(Double.NaN)) {
//							 propB  = calcProp(bboxB);
//							System.out.println(bboxB.getxMax() + " - " + bboxB.getxMin() + " - " + bboxB.getyMax() + " - " + bboxB.getyMin() + " - " + bboxB.getTerminals()); 
//							System.out.println(x + " " + y);
//							System.out.println("propAValue = " + propAValue + "\t\tpropBValue = " + propBValue + " = " +  (propAValue * propBValue));
//						}
						
						shortBits += (propAValue * propBValue);
					}
				}
			}
		}
		//long shortBitsLong = (long)(shortBits / 2);
		return (shortBits / 2);
	}
	
	/**
	 * Calculates the propability of a pip to be used in xy coords. Terminal cross count is taken into account.
	 * offset correction 
	 * xMin = 0 and xMax = xMax - xMin.
	 * yMin = 0 and yMax = yMax - yMin.
	 * @param bbox
	 * @return MultiKeyMap
	 */
	private MultiKeyMap<Integer,Double> calcProp(NetBB bbox) { 
		MultiKeyMap<Integer,Double> propXY = new MultiKeyMap<Integer,Double>();
//		Integer xMin = 0;//bbox.getxMin();
//		Integer yMin = 0;//bbox.getyMin();
		Integer xMax = bbox.getxMax() - bbox.getxMin();//xMin;
		Integer yMax = bbox.getyMax() - bbox.getyMin();//bbox.getyMax() - yMin;
		Integer terminals = bbox.getTerminals();
		
		for(Integer x = 0; x < xMax; x++){
			for(Integer y = 0; y < yMax; y++){
				propXY.put((x + bbox.getxMin()), (y + bbox.getyMin()), Maths.calcPijmn( (x + 1) , (y + 1), (xMax + 1), (yMax + 1), terminals));
//				if(maths.calcPijmn( (x + 1) , (y + 1), (xMax + 1), (yMax + 1), terminals).equals(Double.NaN))
//					System.out.println(maths.calcPijmn( (x + 1) , (y + 1), (xMax + 1), (yMax + 1), terminals));
				//System.out.println("x = " + x + " y = " + y + " prop = " + propXY.get(x, y));
			}
		}
		return propXY;
	}

	

	/*=========================================================================
	 * Place and Route Utilities - Methods
	 *=========================================================================*/

//	/**
//	 * Brief description : Returns the sensitive bits of the given primitive Type
//	 * @param PrimitiveType 
//	 * @return long 
//	 */
//	private long resource_sensitiveBits(PrimitiveType type)
//	{	
////		HashMap<String, Integer> virtex5TypeFrameBits = new HashMap<String, Integer>();
////		virtex5TypeFrameBits.put("SLICE", 320);
////		virtex5TypeFrameBits.put("DSP", 320);
//		
//		long sensitiveBits = 0;
////		HashMap<PrimitiveType, Integer> typeFreq = SA_placement.layout.getTypeFreq();
//		
////		Set keys = typeFreq.keySet();
////
////	   for (Iterator i = keys.iterator(); i.hasNext() {
////		   String key = (String) i.next();
////		   String value = (String) map.get(key);
////		   textview.setText(key + " = " + value);
////		   }
////	   }
//		
//		sensitiveBits = saplacement.SA_placement.layout.getUsedSlices() * 320; 
//		return sensitiveBits;
//	}	
		
	/**
	 * Calculates the Bounding Box for each Net and adds them in the global 
	 * HashMap<String, Bbox> netBboxes and ArrayList<Bbox> netBbList;
	 */
	private void fillDesBboxes() {
		this.netBboxes = new HashMap<String, NetBB>();
		this.netBbList = new ArrayList<NetBB>();
		Collection<Net> nets = this.design.getNets();
		Iterator<Net> itn = nets.iterator();
		Net n;
		int xMax;
		int xMin;
		int yMax;
		int yMin;
		while(itn.hasNext()) {
			n = itn.next();
			ArrayList<Pin> pins = n.getPins();
			Iterator<Pin> itr = pins.iterator();
			Pin pin;
			int terminals = pins.size();
			int[] x = new int[terminals];
			int[] y = new int[terminals];
			int i = 0;
			while(itr.hasNext()) {
				pin = itr.next();	
				x[i] = pin.getTile().getTileXCoordinate();
				y[i] = pin.getTile().getTileYCoordinate();
				i++;
			}	
			if(x.length == 0) continue;
			xMin = x[0]; //assume arr[0] is min value
			xMax = x[0];//assume arr[0] is max value
			
			for(int j=0;j < terminals;j++) {
				if(x[j]<xMin) 
					xMin=x[j];
				if(x[j]>xMax)
					xMax=x[j];
			}
			
			yMin = y[0]; //assume y[0] is min value
			yMax = y[0];//assume y[0] is max value

			for(int j=0;j < terminals;j++) {
				if(y[j]<yMin) 
					yMin=y[j];
				if(y[j]>yMax)
					yMax=y[j];
			}
			NetBB bbox = new NetBB(xMax, xMin, yMax, yMin, terminals, n.getName());
			netBboxes.put(n.getName(), bbox);
		}
		netBbList = new ArrayList<NetBB>(netBboxes.values());
	}
	
	/**
	 * Returns a list with the used PrimitiveTypes.
	 */
	public void getTypeFreq() {
		Collection<Instance> instances = design.getInstances();
		HashMap<String, Integer> tileSensitiveBits = new HashMap<String, Integer>();
		String tileName = "";
		int    tileBits = 0;
		
		//for(ComplexBlock cBlock : Fpga.blocks)
		for(Instance instance : instances) {
			
			tileName = "";
			tileBits = 0;
			if(instance.getType().toString().contains("SLICE")) this.sliceBlocks++;
			else{
				if(instance.getType().toString().contains("IOB")) 
				{
					HashSet<Net> nets = instance.getNetList();
					
					for(Net net : nets){
						if( (net.getType() != NetType.VCC) && (net.getType() != NetType.GND) ) {
							this.ioBlocks++;					
						}					
					}
				}
				else if(instance.getType().toString().contains("RAM"))
				{
					this.bramBlocks++;
					if(design.getFamilyType() == FamilyType.VIRTEX5)
						tileBits = 1280;
					else if(design.getFamilyType() == FamilyType.VIRTEX6)
						tileBits = 1280;
				}
				else if(instance.getType().toString().contains("DSP"))
				{
					this.dspBlocks++;
					if(design.getFamilyType() == FamilyType.VIRTEX5)
						tileBits = 320;
					else if(design.getFamilyType() == FamilyType.VIRTEX6)
						tileBits = 320;
				}
				
				
				
				if(tileName.contains("RAM")  || tileName.contains("DSP"))
				{
					if(tileSensitiveBits.containsKey(tileName))
					{
						int value = tileSensitiveBits.get(tileName).intValue();
						tileSensitiveBits.put(tileName, value + tileBits);
					}
					else
					{
						tileSensitiveBits.put(tileName, tileBits);
					}
				}
			}
		}

		try 
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.sbaFilePath,true));
			Set set = tileSensitiveBits.entrySet();
			Iterator i = set.iterator();			
			while(i.hasNext()) 
			{
				Map.Entry obj 	= (Map.Entry)i.next();
				Integer bits 	= ((Integer) obj.getValue());
				String  site	= (String) obj.getKey();
				writer.write(site + " = " + bits + "\n");
			}
			writer.close();
		}
		catch (IOException e) {}
	}
	
	/**
	 * Loads the XDL design.
	 * @param xdlFile
	 */
	private void loadFile(String xdlName) {
		this.design = new Design();
		System.out.println("\nLoading design...");
		design.loadXDLFile(xdlName);
		design.flattenDesign(); //flatten Design
		if(design.getModuleInstances().size() > 0){
			MessageGenerator.briefErrorAndExit("Loading design failed. Sorry, module instances unsupported.");
		} 
	}
	
	/**
	 * Loads the complex blocks of the design in the block ArralList. 
	 * This method is same with unipi.placer.FPGA loadCompexBlock(boolean debug) method, with the only difference that loads and the instances containing curry chains.
	 * @param debug while true, we output the tiles of the FPGA and their relying instances.
	 */
	private void loadCompexBlocks(boolean debug)
	{   	
		HashMap<String, Tile> tiles = design.getDevice().getTileMap();
		ComplexBlock cBlock = null;
//		HashSet<String> tilesNames = new HashSet<String>();
		for(Tile t : tiles.values())
		{
			//if(	(t.getType().toString().contains("IOI")  &&  t.getType() != TileType.IOI )   ) continue;
			//if(	(t.getType().toString().contains("IOB") && design.getDevice().getFamilyType() == FamilyType.VIRTEX5)   ) continue;
			//if	(t.getType() == TileType.INT)   continue;
			
			if( (t.getType().toString().contains("LIO")) ||
				(t.getType().toString().contains("RIO")) ||
				(t.getType().toString().contains("BIO")) ||
				(t.getType().toString().contains("CIO")) ||
				(t.getType().toString().contains("IOI")) ) 
			{
				cBlock = new ComplexBlock();
				Instance inst = null;
				Block block = null;	
				PrimitiveSite[] ioiSites = t.getPrimitiveSites();
				PrimitiveSite ioiSite;
				if(ioiSites == null) continue;
				for(int i = 0; i < ioiSites.length; i++)
				{	
					ioiSite = ioiSites[i];
//					if(ioiSite.toString().contains("OLOGIC")) {
//						System.out.println(ioiSite.toString());
//					}
					inst = design.getInstanceAtPrimitiveSite(ioiSite);
					block = new Block(ioiSite);
					block.addInstance(inst);
					cBlock.add(block);
					// Parse the name of the site to find its coordinates
					PrimitiveSite iob = findIOB(t.getTileXCoordinate(), t.getTileYCoordinate(), ioiSite.getInstanceX(), ioiSite.getInstanceY());
					if(iob != null)
					{
						inst = design.getInstanceAtPrimitiveSite(iob);
//						if(inst != null) {
//							System.out.println("Instance");
//						}
						block = new Block(iob);
						block.addInstance(inst);
						cBlock.add(block);
					}
				}
				blocks.add(cBlock);
			}
			else
			{
				PrimitiveSite[] sites = t.getPrimitiveSites();
				PrimitiveSite site;
				if(sites != null) {	
					cBlock = new ComplexBlock();
					for(int i = 0; i < sites.length; i++) {
						site = sites[i];
						Instance inst = design.getInstanceAtPrimitiveSite(site);
						cBlock.add(site, inst);
					}
					blocks.add(cBlock);			
				}
			}
		}
		
//		for(ComplexBlock cb : blocks) {
//			ArrayList<Block> blocksInCb = cb.blocks;
//			for(Block bl : blocksInCb) {
//				if(bl.getInstance() != null) {
//					cb.used = true;
//				}
//			}
//		}
//			
//		
//
//		for(ComplexBlock cb : blocks) {
//			System.out.println(cb.tileType.toString() + "_X" + cb.x + "Y" + cb.y);
//			ArrayList<Block> blocksInCb = cb.blocks;
//			for(Block bl : blocksInCb) {
//				System.out.println(bl.getSite().toString());
//				if(bl.getInstance() == null) System.out.println("Instance = null");
//				else System.out.println(bl.getInstance().toString());
//				System.out.println("*****************************");
//			}
//			System.out.println("***************************************************************");
//		}

	}
	
	/**
	 * Returns the correlated IODELAY or ILOGIC or OLOGIC PrimitiveSite of an IOB PrimitiveSite in order to be packed in one complex block.
	 * @param xTile
	 * @param yTile
	 * @param x
	 * @param y
	 * @return PrimitiveSite
	 */
	private PrimitiveSite findIOB(int xTile, int yTile, int x, int y)
	{
		HashMap<String, Tile> tiles = design.getDevice().getTileMap();

		for(Tile t : tiles.values())
		{
			if(t == null) continue;
			if(t.getPrimitiveSites() == null) continue;
			if(!(t.getType().toString().contains("IOB"))) continue;
			//System.out.println("Primitive sites = " + t.getPrimitiveSites().length);
			if((t.getTileXCoordinate() == xTile) && (t.getTileYCoordinate() == yTile))
			{
				if((y % 2 == 0))
				{
					
					if(t.getPrimitiveSites()[0] == null)
					{
						return t.getPrimitiveSites()[1];
					} 
					else 
					{
						return t.getPrimitiveSites()[0];
					}
				}
				else
				{
					if(t.getPrimitiveSites()[1] == null)
					{
						return t.getPrimitiveSites()[0];
					} 
					else 
					{
						return t.getPrimitiveSites()[1];
					}
				}
			} 
		}
		return null;
	}
	
	/**
	 * Returns the RAM sensitive bits. This method in only evaluated for Virtex5 and Virtex6 architectures.
	 * @return long :the RAM sensitive bits
	 */
	private long getRamSensitiveBits() {
		if(design.getFamilyType() == FamilyType.VIRTEX5) {
			return this.bramBlocks * 1280;
		}
		else if(design.getFamilyType() == FamilyType.VIRTEX6) {
			return this.bramBlocks * 1280;
		}
		else
			return 0;		
	}
	
	/**
	 * Returns the DSP sensitive bits. This method in only evaluated for Virtex5 and Virtex6 architectures.
	 * @return long :the DSP sensitive bits
	 */
	private long getDspSensitiveBits() {
		if(design.getFamilyType() == FamilyType.VIRTEX5) {
			return this.dspBlocks * 320;
		}
		else if(design.getFamilyType() == FamilyType.VIRTEX6) {
			return this.dspBlocks * 320;
		}
		else
			return 0;		
	}
}

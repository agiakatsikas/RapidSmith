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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.Bitstream;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.BitstreamParseException;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.BitstreamParser;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.FPGA;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.Frame;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.FrameAddressRegister;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.FrameData;
import edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification.BlockSubType;

import edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification.DeviceLookup;
import edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification.XilinxConfigurationSpecification;
import edu.byu.ece.rapidSmith.design.Design;
import edu.byu.ece.rapidSmith.device.Device;
import edu.byu.ece.rapidSmith.device.Tile;


public class EBD_analysis {

	// Nested class representing the column frames range
	class columnFramesRange
	{
		public int startAddress;
		public int endAddress;
		public int frames;
		public String columnType;
		public columnFramesRange()
		{
			this.startAddress = 0;
			this.endAddress = 0;
			this.frames = 0;
			this.columnType = "";
		}
	}
	// Nested class representing a column
	class column
	{
		int 				columnIndex;
		int					sensitiveConfigurationBits;
		int					sensitiveInterconnectionBits;
		int					sensitiveInterfaceBits;
		columnFramesRange 	frameAddressInfo;
		String				columnType;
		public column(int columnIndex, columnFramesRange frameAddressInfo)
		{
			this.columnIndex 					= columnIndex;
			this.frameAddressInfo 				= frameAddressInfo;
			this.sensitiveConfigurationBits		= 0;
			this.sensitiveInterconnectionBits	= 0;
			this.sensitiveInterfaceBits			= 0;
			this.columnType						= "";
		}
	}
	Logger                              logger;
	Bitstream 							bitstream;
	String 	  							bitStream_fileName;
	String 	  							ebd_fileName;
	String								xsba_fileName;
	String								address_fileName;
	String								sensitiveBits_distribution_fileName;
	XilinxConfigurationSpecification 	spec;
	HashMap<Integer, Frame>				ebdFrames;
	long 								sensitiveConfigurationBits_total;
	long 								sensitiveConfigurationBits;
	long 								sensitiveInterconnectionBits;
	long 								sensitiveInterfaceBits;
	long								sensitiveInterconnectionFrames;
	long								sensitiveInterfaceFrames;
	long								sensitiveConfigurationFrames;
	long								interconnectionFrames;
	long								interfaceFrames;
	long								configurationFrames;
	long								senstiveFrames;
	long								deviceFramesTotal;
	long 								sDUTBits;
	ArrayList<column>					columnFrames;
	FPGA								fpga;
	Design 								design;	
	Device								dev;
	HashMap<String, Integer>			ebdSitesAdded;
	List<Frame> 						sFrames;
	HashMap<Integer, ArrayList<Integer>>  bits2Inject;
	ArrayList<Integer> sAddressKey;
	Random randomGenerator;
	FrameAddressRegister far;
	
	
	/*=============================================================================================
	 * @fn			public EBD_analysis(String bitStream_fileName)
	 * @brief		Class constructor  
	 * @param[in]	String		:The full path of the design bitstream file
	 * @param[out]	None
	 * @returns		None
	 * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
	 *=============================================================================================*/	
	public EBD_analysis(String bitStream_fileName, Logger logger)
	{
		this.bitstream = null;
		this.bitStream_fileName = bitStream_fileName;
		this.ebd_fileName		= bitStream_fileName.replace(".bit", ".ebd");
		this.xsba_fileName		= bitStream_fileName.replace(".bit", ".xsba");
		this.address_fileName	= bitStream_fileName.replace(".bit", "_addresses.txt");
		this.sensitiveBits_distribution_fileName = bitStream_fileName.replace(".bit", "_distribution.csv");
		this.ebdFrames 			= new HashMap<Integer,Frame>();
		this.senstiveFrames		= 0;
		this.sFrames = new ArrayList<Frame>();
		this.randomGenerator = new Random(System.currentTimeMillis());
		this.bits2Inject =  new HashMap<>();
		this.sAddressKey = new ArrayList<>();
		this.sDUTBits = 0;
		this.logger = logger;
	}
	
	public EBD_analysis(String bitStream_fileName)
	{
		this.bitstream = null;
		this.bitStream_fileName = bitStream_fileName;
		this.ebd_fileName		= bitStream_fileName.replace(".bit", ".ebd");
		this.xsba_fileName		= bitStream_fileName.replace(".bit", ".xsba");
		this.address_fileName	= bitStream_fileName.replace(".bit", "_addresses.txt");
		this.sensitiveBits_distribution_fileName = bitStream_fileName.replace(".bit", "_distribution.csv");
		this.ebdFrames 			= new HashMap<Integer,Frame>();
		this.senstiveFrames		= 0;
		this.sFrames = new ArrayList<Frame>();
		this.randomGenerator = new Random(System.currentTimeMillis());
		this.bits2Inject =  new HashMap<>();
		this.sAddressKey = new ArrayList<>();
		this.sDUTBits = 0;
	}
	/*=============================================================================================
	 * @fn			public void loadBitStream()	
	 * @brief		This function parses the bitstream file.\n
	 * 				The bitstream file should be in debug format.  
	 * @param[in]	None
	 * @param[out]	None
	 * @returns		None
	 * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
	 *=============================================================================================*/	
	public void loadBitStream()
	{
		try {
            this.bitstream = BitstreamParser.parseBitstream(this.bitStream_fileName);
    		this.spec = DeviceLookup.lookupPartV4V5V6S7withPackageName(this.bitstream.getHeader().getPartName());
    		this.fpga = new FPGA(this.spec,false);
    		this.far = new FrameAddressRegister(this.spec);
        } catch (BitstreamParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}
	
	/*=============================================================================================
	 * @fn			public void loadEBD()	
	 * @brief		This function parses the EBD file using the information contained in the bitstream file.\n
	 * 				The bitstream file should be in debug format.  
	 * @param[in]	None
	 * @param[out]	None
	 * @returns		None
	 * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
	 *=============================================================================================*/
	public void loadEBD()
	{
		//FrameAddressRegister far = new FrameAddressRegister(this.spec);
		fpga.configureBitstream(this.bitstream);
		Frame ebdFrame = null;
		//FileInputStream bReader = new FileInputStream(this.ebd_fileName);
		File file = new File(this.ebd_fileName);
        RandomAccessFile bReader = null;
		try {
			bReader = new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        String line = "";
        int lBytesRead = 0;
		int bytesToRead;
		List<Byte> dataBytes;
		byte[] bytes = null;
		Byte[] trimBytes;
		//List<Integer> words;
		//int bitIndex;
		
		//int row_prv = 0;
		//int row_cur = 0;
		//int topbottom_prv = 0;
		//int topbottom_cur = 0;
		
		// TODO: Move the below device-dependent parameters to a database file
		// or from an XML file
		// @section FPGA device specific parameters
		//int dummyColumn   					= 0;
		int iobFrames	  					= 0;
		int clbFrames     					= 0;
		int bramFrames    					= 0;
		int dspFrames     					= 0;
		int clkFrames     					= 0;
		int interconnectionFrameIndexMin 	= 0;
		int interconnectionFrameIndexMax	= 0;
		int iobConfigurationFrameIndexMin	= 0;
		int iobConfigurationFrameIndexMax	= 0;
		int clbConfigurationFrameIndexMin	= 0;
		int clbConfigurationFrameIndexMax	= 0;
		int bramConfigurationFrameIndexMin	= 0;
		int bramConfigurationFrameIndexMax	= 0;
		int iobInterfaceFrameIndexMin	= 0;
		int iobInterfaceFrameIndexMax	= 0;
		int bramInterfaceFrameIndexMin	= 0;
		int bramInterfaceFrameIndexMax	= 0;
		int dspInterfaceFrameIndexMin	= 0;
		int dspInterfaceFrameIndexMax   = 0;
		boolean endOfEbd				= false;
		int columnsConfigured			= 0;
		int columnsConfiguredEbd		= 0;
		
		if(fpga.getDeviceSpecification().getDeviceFamily().contains("Virtex5"))
		{
			//dummyColumn = 65;
			iobFrames   = 54;
			clbFrames   = 36;
			bramFrames  = 30;
			dspFrames   = 28;
			clkFrames   = 4;
			
			interconnectionFrameIndexMin 	= 0;
			interconnectionFrameIndexMax	= 24;
			
			iobInterfaceFrameIndexMin		= interconnectionFrameIndexMax + 1;
			iobInterfaceFrameIndexMax		= 27;
			// CLBs have no block interface frames
			bramInterfaceFrameIndexMin		= interconnectionFrameIndexMax + 1;
			bramInterfaceFrameIndexMax		= 27;
			dspInterfaceFrameIndexMin		= interconnectionFrameIndexMax + 1;
			dspInterfaceFrameIndexMax		= 27;
		}
		else if(fpga.getDeviceSpecification().getDeviceFamily().contains("Virtex6"))
		{
			//dummyColumn = 103;
			iobFrames   = 44;
			clbFrames   = 36;
			bramFrames  = 28;
			dspFrames   = 28;
			clkFrames   = 38;
			
			interconnectionFrameIndexMin 	= 0;
			interconnectionFrameIndexMax	= 24;
			
			iobInterfaceFrameIndexMin		= interconnectionFrameIndexMax + 1;
			iobInterfaceFrameIndexMax		= 27;
			// CLBs have no block interface frames
			bramInterfaceFrameIndexMin		= interconnectionFrameIndexMax + 1;
			bramInterfaceFrameIndexMax		= 26;
			dspInterfaceFrameIndexMin		= interconnectionFrameIndexMax + 1;
			dspInterfaceFrameIndexMax		= 27;
		}
		
		else if(fpga.getDeviceSpecification().getDeviceFamily().contains("Artix7"))
		{
			//dummyColumn = 103;
			iobFrames   = 42;
			clbFrames   = 36;
			bramFrames  = 28;
			dspFrames   = 28;
			clkFrames   = 30;
			
			interconnectionFrameIndexMin 	= 0;
			interconnectionFrameIndexMax	= 24;
			
			iobInterfaceFrameIndexMin		= interconnectionFrameIndexMax + 1;
			iobInterfaceFrameIndexMax		= 27;
			// CLBs have no block interface frames
			bramInterfaceFrameIndexMin		= interconnectionFrameIndexMax + 1;
			bramInterfaceFrameIndexMax		= 26;
			dspInterfaceFrameIndexMin		= interconnectionFrameIndexMax + 1;
			dspInterfaceFrameIndexMax		= 27;
			
			//Integer[] bramc = new Integer[] {5, 16, 25, 36, 62, 73, 82, 93, 101};
			//Integer[] dspc  = new Integer[] {8, 13, 28, 33, 65, 70, 85, 90};
			//bramColumns = Arrays.asList(bramc);
			//dspColumns  = Arrays.asList(dspc);
		}
		
		iobConfigurationFrameIndexMin	= iobInterfaceFrameIndexMax + 1;
		iobConfigurationFrameIndexMax	= iobFrames - 1;
		clbConfigurationFrameIndexMin	= interconnectionFrameIndexMax + 1;
		clbConfigurationFrameIndexMax	= clbFrames - 1;
		bramConfigurationFrameIndexMin	= bramInterfaceFrameIndexMax + 1;
		bramConfigurationFrameIndexMax	= bramFrames - 1;
		// DSPs have no block configuration frames
	
		// end section FPGA device specific parameters
		
		this.sensitiveConfigurationBits_total = 0;
		
		// Read the header
		while(true)
		{
            try {
				line = bReader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            lBytesRead += (line.length() + 2);
            if (!line.contains(" ") && !line.contains(":"))
                break;
        }
        lBytesRead -= (line.length() + 2);
        try 
        {
			bReader.close();
			bReader = new RandomAccessFile(file, "rw");
			bReader.seek(lBytesRead);
	       
			// Read the dummy frame
			bytesToRead = spec.getFrameSize() * (32 + 2);
			//		fpga.getAllFrames().get(0).getData().size() * (32 + 2); //Aitzan
			System.out.println(bytesToRead);
	        bytes 		= new byte[bytesToRead];
			bReader.read(bytes);		
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        List<Frame> lstFrames 	= null;
		List<BlockSubType> blockSubTypes = fpga.getDeviceSpecification().getOverallColumnLayout();
		// Start from the TOP part of the FPGA
		int clkRows = fpga.getDeviceSpecification().getTopNumberOfRows();
		for(int rowIndex = 0; rowIndex < clkRows; rowIndex++)
		{
			if(endOfEbd == true) break;
			for(int blockSubTypeIndex = 0;  blockSubTypeIndex < blockSubTypes.size(); blockSubTypeIndex++)
			{
				if(endOfEbd == true) break;
				lstFrames	= fpga.getConfigurationBlockFrames(0, fpga.getDeviceSpecification().getLogicBlockType(), rowIndex, blockSubTypeIndex);
				if(lstFrames.get(0).isConfigured() == false) continue;
				columnsConfiguredEbd++;
				for(Frame frame : lstFrames)
				{
					/*if(frame.getData().isEmpty())
					{
						FrameData fData = new FrameData(frame.getData().size());
						ebdFrame = new Frame(frame.getData().size(), frame.getFrameAddress());
						//dataBytes = new ArrayList<Byte>();
						//for(int b = 0; b < frame.getData().size(); b++) dataBytes.add((byte)0);
						//fData 		= new FrameData(dataBytes);				
						ebdFrame.configure(fData);				
						this.ebdFrames.put(frame.getFrameAddress(),ebdFrame);
						continue;
					}*/
					if(endOfEbd == true)
					{
						FrameData fData = new FrameData(frame.getData().size());
						ebdFrame = new Frame(frame.getData().size(), frame.getFrameAddress());			
						ebdFrame.configure(fData);				
						this.ebdFrames.put(frame.getFrameAddress(),ebdFrame);
						continue;
					}
					try
					{
						far.setFAR(frame.getFrameAddress());
						/*if(blockSubTypes.get(blockSubTypeIndex).getName().contains("OVERHEAD"))
						{
							bReader.read(bytes);
							continue;
						}*/
						/*if(blockSubTypes.get(blockSubTypeIndex).getName().contains("GT"))
						{
							bReader.read(bytes);
							continue;
						}*/
						
						FrameData fData;
						ebdFrame = new Frame(frame.getData().size(), far.getAddress());
						bReader.read(bytes);
						trimBytes 	= bytesTrim(bytes);
						dataBytes 	= asciiBytes_toBinary(trimBytes);
						fData 		= new FrameData(dataBytes);				
						ebdFrame.configure(fData);				
						this.sensitiveConfigurationBits_total += ebdFrame.getData().countBitsSet();
						//createDUTSensitiveBits(ebdFrame);
						this.ebdFrames.put(frame.getFrameAddress(),ebdFrame);
						if(bReader.length() == bReader.getFilePointer())
						{
							endOfEbd = true;							
						}
					} catch(IOException ex) {
						
					}
				}
			}
		}
		
		
		// Start from the BOTTOM part of the FPGA
		clkRows = fpga.getDeviceSpecification().getBottomNumberOfRows();
		for(int rowIndex = 0; rowIndex < clkRows; rowIndex++)
		{
			if(endOfEbd == true) break;
			for(int blockSubTypeIndex = 0;  blockSubTypeIndex < blockSubTypes.size(); blockSubTypeIndex++)
			{
				if(endOfEbd == true) break;
				lstFrames	= fpga.getConfigurationBlockFrames(1, fpga.getDeviceSpecification().getLogicBlockType(), rowIndex, blockSubTypeIndex);
				if(lstFrames.get(0).isConfigured() == false) continue;
				columnsConfiguredEbd++;
				for(Frame frame : lstFrames)
				{
					if(endOfEbd == true)
					{
						FrameData fData = new FrameData(frame.getData().size());
						ebdFrame = new Frame(frame.getData().size(), frame.getFrameAddress());			
						ebdFrame.configure(fData);				
						this.ebdFrames.put(frame.getFrameAddress(),ebdFrame);
						continue;
					}
					try
					{
						far.setFAR(frame.getFrameAddress());
						/*if(blockSubTypes.get(blockSubTypeIndex).getName().contains("OVERHEAD"))
						{
							bReader.read(bytes);
							continue;
						}*/
						/*if(blockSubTypes.get(blockSubTypeIndex).getName().contains("GT"))
						{
							bReader.read(bytes);
							continue;
						}*/
						
						FrameData fData;
						ebdFrame = new Frame(frame.getData().size(), far.getAddress());
						bReader.read(bytes);
						trimBytes 	= bytesTrim(bytes);
						dataBytes 	= asciiBytes_toBinary(trimBytes);
						fData 		= new FrameData(dataBytes);				
						ebdFrame.configure(fData);				
						this.sensitiveConfigurationBits_total += ebdFrame.getData().countBitsSet();
						//createDUTSensitiveBits(ebdFrame);
						this.ebdFrames.put(frame.getFrameAddress(),ebdFrame);
						if(bReader.length() == bReader.getFilePointer())
						{
							endOfEbd = true;							
						}
					} catch(IOException ex) {}
					
				}
			}
		}
		
		try
		{	
			long position = bReader.getFilePointer();
			System.out.println(position);
			System.out.println(bReader.length());
			bReader.close();
			position = 0;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	public void EBD_saveMap()
	{
		
		int iosPerColumn = 40;
		int clbsPerColumn = 20;
		int bramPerColumn = 8;
		int dspPerColumn = 8;
		int clkPerColumn = 8;
		
		int column;
		int row;
		int topBottom;
		
		int maxRowsPerSide = 2;
		
		//FrameAddressRegister far = new FrameAddressRegister(this.spec);
		
		for(column c : this.columnFrames)
		{
			column = far.getColumnFromAddress(c.frameAddressInfo.startAddress);
			row = far.getRowFromAddress(c.frameAddressInfo.startAddress);
			topBottom = far.getTopBottomFromAddress(c.frameAddressInfo.startAddress);
			
			if(c.columnType.contains("IOB"))
			{
				
			}
			else if(c.columnType.contains("CLB"))
			{
				
			}
			else if(c.columnType.contains("BRAM"))
			{
				
			}
			else if(c.columnType.contains("DSP"))
			{
				
			}
			else if(c.columnType.contains("CLK"))
			{
				
			}
		}
	}
	
	public int get_IOB_columns()
	{
		int bits_iob = 0;
		int iobc = 0;
		///this.fpga.getDeviceSpecification().
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("IOB"))
				iobc++;
		}
		
		return bits_iob;
	}
	
	/*=============================================================================================
	 * @fn			private void findFramesPerColumn()	
	 * @brief		This function finds and maps the sensitive frames for each FPGA device column.\n
	 * 				\par
	 * 				The mapping is done by finding the start and end frame address for each column. This
	 * 				information is saved in the ArrayList "columnFrames" in the following format:\n
	 * 				<column>\n
	 * 				where <column> contains:\n
	 * 					<column index> , <columnFramesRange>\n
	 * 					where <column index> is the zero-based index of the column (0 to (device_columns - 1))\n
	 * 					where <columnFramesRange>:\n
	 * 					<start address> , <end address> , <number of frames>  
	 * @param[in]	None
	 * @param[out]	None
	 * @returns		None
	 * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
	 *=============================================================================================*/
	private void findFramesPerColumn()
	{
		this.columnFrames = new ArrayList<column>();
		//FrameAddressRegister far = new FrameAddressRegister(this.spec);
		columnFramesRange columnAddress = new columnFramesRange();
		//int columnIndex_prv = 0;
		//int columnIndex_cur = 0;
		//int startAddress 	= 0;
		//int endAddress		= 0;
		int framesNum		= 0;
		
		//Map<Integer, Frame> tMap = new TreeMap<Integer, Frame>(this.ebdFrames);
		//Set set = tMap.entrySet();
		//Iterator i = set.iterator();
		
		List<Frame> lstFrames 	= null;
		List<BlockSubType> blockSubTypes = fpga.getDeviceSpecification().getOverallColumnLayout();
		// Start from the TOP part of the FPGA
		int clkRows = fpga.getDeviceSpecification().getTopNumberOfRows();
		for(int rowIndex = 0; rowIndex < clkRows; rowIndex++)
		{
			for(int blockSubTypeIndex = 0;  blockSubTypeIndex < blockSubTypes.size(); blockSubTypeIndex++)
			{
				// Get the frames for the column
				framesNum 					= blockSubTypes.get(blockSubTypeIndex).getFramesPerConfigurationBlock();
				lstFrames 					= fpga.getConfigurationBlockFrames(0, fpga.getDeviceSpecification().getLogicBlockType(), rowIndex, blockSubTypeIndex);
				columnAddress 				= new columnFramesRange();
				columnAddress.startAddress  = lstFrames.get(0).getFrameAddress();
				columnAddress.endAddress    = lstFrames.get(framesNum - 1).getFrameAddress();
				columnAddress.frames		= framesNum;
				columnAddress.columnType	= blockSubTypes.get(blockSubTypeIndex).getName();
				
				this.columnFrames.add(new column(columnAddress.startAddress, columnAddress));
			}
		}
		
		clkRows = fpga.getDeviceSpecification().getBottomNumberOfRows();
		for(int rowIndex = 0; rowIndex < clkRows; rowIndex++)
		{
			for(int blockSubTypeIndex = 0;  blockSubTypeIndex < blockSubTypes.size(); blockSubTypeIndex++)
			{
				// Get the frames for the column
				framesNum 					= blockSubTypes.get(blockSubTypeIndex).getFramesPerConfigurationBlock();
				lstFrames 					= fpga.getConfigurationBlockFrames(1, fpga.getDeviceSpecification().getLogicBlockType(), rowIndex, blockSubTypeIndex);
				columnAddress 				= new columnFramesRange();
				columnAddress.startAddress  = lstFrames.get(0).getFrameAddress();
				columnAddress.endAddress    = lstFrames.get(framesNum - 1).getFrameAddress();
				columnAddress.frames		= framesNum;
				columnAddress.columnType	= blockSubTypes.get(blockSubTypeIndex).getName();
				
				this.columnFrames.add(new column(columnAddress.startAddress, columnAddress));
			}
		}
	}
	
	
	public void findFpgaResourceSensitiveBits()
	{
		this.ebdSitesAdded = new HashMap<String, Integer>();
		//FrameAddressRegister far = new FrameAddressRegister(this.spec);
		List<BlockSubType> blockSubTypes = fpga.getDeviceSpecification().getOverallColumnLayout();
		
		List<Frame> lstFrames 	= null;
		List<Frame> sFrames		= null;
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(this.xsba_fileName, false));
		}catch(IOException ex){ return;}
		
		// The number of frames for the column
		int columnFrames = 0;
		// The number of switch boxes in the column
		int columnHeightSW = 0;
		// The number of logic tiles in the column
		int columnHeight = 0;
		// The number of interconnection frames for the column
		int interconnectionFrames = 0;
		// The number of interface frames for the column
		int interfaceFrames = 0;
		// The number of block configuration frames for the column
		int blockConfiguration = 0;
		// The size of the frame (in words)
		int frameSize = fpga.getDeviceSpecification().getFrameSize();
		
		// Start from the TOP part of the FPGA
		int clkRows = fpga.getDeviceSpecification().getTopNumberOfRows();
		for(int rowIndex = 0; rowIndex < clkRows; rowIndex++)
		{
			for(int blockSubTypeIndex = 0;  blockSubTypeIndex < blockSubTypes.size(); blockSubTypeIndex++)
			{
				// Get the frames for the column
				lstFrames = fpga.getConfigurationBlockFrames(0, fpga.getDeviceSpecification().getLogicBlockType(), rowIndex, blockSubTypeIndex);
				
				// Get the sensitive frames for the column
				// NOTE: the sensitive frames are already placed in the ebdFrames list
				sFrames = new ArrayList<Frame>();
				for(Frame frame : lstFrames)
				{
					sFrames.add(this.ebdFrames.get(frame.getFrameAddress()));
				}
				// The number of frames for the column
				columnFrames = blockSubTypes.get(blockSubTypeIndex).getFramesPerConfigurationBlock();;
				// The number of switch boxes in the column
				columnHeightSW = 0;
				// The number of logic tiles in the column
				columnHeight = 0;
				// The number of interconnection frames for the column
				interconnectionFrames = 0;
				// The number of interface frames for the column
				interfaceFrames = 0;
				// The number of block configuration frames for the column
				blockConfiguration = 0;
				
				String deviceFamily = this.dev.getFamilyType().name();
				if(blockSubTypes.get(blockSubTypeIndex).getName().contains("IOB"))
				{
					if(deviceFamily.contains("VIRTEX5"))
					{
						columnHeightSW 			= 20;
						columnHeight			= 20;
						interconnectionFrames 	= 25;
						interfaceFrames			= 2;
						blockConfiguration		= 26;
					}
					else if(deviceFamily.contains("VIRTEX6"))
					{
						columnHeightSW 			= 40;
						columnHeight			= 40;
						interconnectionFrames 	= 25;
						interfaceFrames			= 2;
						blockConfiguration		= 26;
					}
				}
				else if(blockSubTypes.get(blockSubTypeIndex).getName().contains("CLB"))
				{
					if(deviceFamily.contains("VIRTEX5"))
					{
						columnHeightSW			= 20;
						columnHeight 			= 20;
						interconnectionFrames 	= 24;
						interfaceFrames			= 0;
						blockConfiguration		= 11;
					}
					else if(deviceFamily.contains("VIRTEX6"))
					{
						columnHeightSW 			= 40;
						columnHeight			= 40;
						interconnectionFrames 	= 24;
						interfaceFrames			= 0;
						blockConfiguration		= 11;
					}
				}
				else if(blockSubTypes.get(blockSubTypeIndex).getName().contains("BRAM"))
				{
					if(deviceFamily.contains("VIRTEX5"))
					{
						columnHeightSW			= 20;
						columnHeight 			= 4;
						interconnectionFrames 	= 25;
						interfaceFrames			= 2;
						blockConfiguration		= 2;
					}
					else if(deviceFamily.contains("VIRTEX6"))
					{
						columnHeightSW 			= 40;
						columnHeight			= 8;
						interconnectionFrames 	= 25;
						interfaceFrames			= 2;
						blockConfiguration		= 2;
					}
				}
				else if (blockSubTypes.get(blockSubTypeIndex).getName().contains("DSP"))
				{
					
					if(deviceFamily.contains("VIRTEX5"))
					{
						columnHeightSW			= 20;
						columnHeight 			= 8;
						interconnectionFrames 	= 25;
						interfaceFrames			= 2;
						blockConfiguration		= 0;
					}
					else if(deviceFamily.contains("VIRTEX6"))
					{
						columnHeightSW 			= 40;
						columnHeight			= 16;
						interconnectionFrames 	= 25;
						interfaceFrames			= 2;
						blockConfiguration		= 0;
					}
				}
				else if (blockSubTypes.get(blockSubTypeIndex).getName().contains("CLK"))
				{
					if(deviceFamily.contains("VIRTEX5"))
					{
						columnHeightSW			= 20;
						columnHeight 			= 20;
						interconnectionFrames 	= 0;
						interfaceFrames			= 4;
						blockConfiguration		= 0;
					}
					else if(deviceFamily.contains("VIRTEX6"))
					{
						columnHeightSW 			= 40;
						columnHeight			= 40;
						interconnectionFrames 	= 0;
						interfaceFrames			= 4;
						blockConfiguration		= 0;
					}
				}
				else
				{
					continue;
				}

				// The number of frame-words for a single tile
				int wordsPerResourceSW = (frameSize - 1) / columnHeightSW;
				int wordsPerResource   = (frameSize - 1) / columnHeight;				
				int word = 0;
				int x	 = 0;
				int y	 = 0;
				for(int resourceIndex = 0; resourceIndex < columnHeight; resourceIndex++)
				{
					int frameIndex = 0;
					int resourceBits = 0;
					int interconnectionBits 		= 0;
					int interfaceBits 				= 0;
					int blockConfigurationBits 		= 0;
					int blockConfigurationBitsA 	= 0;
					int blockConfigurationBitsB 	= 0;
					
					if(resourceIndex < columnHeightSW)
					{
						// Get the interconnection bits
						for(; frameIndex < sFrames.size(); frameIndex++)
						{
							if(frameIndex >= interconnectionFrames) { break; }
							for(int wordIndex = 0; wordIndex < wordsPerResourceSW; wordIndex++)
							{
								if(wordIndex == ((frameSize -1)/wordsPerResource))
								{
									wordIndex++;
								}
								word = sFrames.get(frameIndex).getData().getFrameWord((resourceIndex * wordsPerResourceSW) + wordIndex);
								if(word != 0)
									interconnectionBits += bitCount(word);
							}
						}
						
						// Compute the Y value for the interconnection matrix
						// Check the position of the column (top/bottom)
						if(this.spec.getTopBottomFromFAR(lstFrames.get(0).getFrameAddress()) == 0)
						{
							y = (clkRows * columnHeightSW) + (this.spec.getRowFromFAR(lstFrames.get(0).getFrameAddress()) * columnHeightSW) + (resourceIndex);
						}
						else
						{
							y = (this.spec.getRowFromFAR(lstFrames.get(0).getFrameAddress()) * columnHeightSW) + (resourceIndex);
						}
						// Calculate the X value for the interconnection matrix
						x = this.spec.getColumnFromFAR(lstFrames.get(0).getFrameAddress());
						
						try { writer.write("INT_X" + x + "Y" + y + "=" + interconnectionBits + "\n"); } catch (IOException e) { e.printStackTrace(); }
					}
					
					// Get the interface bits
					for(; frameIndex < sFrames.size(); frameIndex++)
					{
						if(frameIndex >= (interconnectionFrames + interfaceFrames)) { break; }
						for(int wordIndex = 0; wordIndex < wordsPerResource; wordIndex++)
						{
							if(wordIndex == ((frameSize -1)/wordsPerResource))
							{
								wordIndex++;
							}
							word = sFrames.get(frameIndex).getData().getFrameWord((resourceIndex * wordsPerResource) + wordIndex);
							if(word != 0)
								interfaceBits += bitCount(word);
						}
					}					
					
					// Get the block configuration bits
					for(; frameIndex < sFrames.size(); frameIndex++)
					{
						if(frameIndex >= (interconnectionFrames + interfaceFrames + blockConfiguration)) { break; }
						for(int wordIndex = 0; wordIndex < wordsPerResource; wordIndex++)
						{
							if(wordIndex == ((frameSize -1)/wordsPerResource))
							{
								wordIndex++;
							}
							// There are two slices inside a single CLB
							word = sFrames.get(frameIndex).getData().getFrameWord((resourceIndex * wordsPerResource) + wordIndex);
							if(word != 0)
								blockConfigurationBitsA += bitCount(word);
							
							if(blockSubTypes.get(blockSubTypeIndex).getName().equals("CLB"))
							{
								word = sFrames.get(frameIndex).getData().getFrameWord((resourceIndex * wordsPerResource) + (wordIndex + 1));
								if(word != 0)
									blockConfigurationBitsB += bitCount(word);
								wordIndex++;
							}
						}
					}
					
					// Compute the Y value for the interconnection matrix
					// Check the position of the column (top/bottom)
					if(this.spec.getTopBottomFromFAR(lstFrames.get(0).getFrameAddress()) == 0)
					{
						y = (clkRows * columnHeightSW) + (this.spec.getRowFromFAR(lstFrames.get(0).getFrameAddress()) * columnHeight) + (resourceIndex);
					}
					else
					{
						y = (this.spec.getRowFromFAR(lstFrames.get(0).getFrameAddress()) * columnHeightSW) + (resourceIndex);
					}
					// Calculate the X value for the interconnection matrix
					x = this.spec.getColumnFromFAR(lstFrames.get(0).getFrameAddress());
					
					try { 
							if(blockSubTypes.get(blockSubTypeIndex).getName().equals("CLB"))
							{
								Tile tile = findTile(blockSubTypes.get(blockSubTypeIndex), x, y);
								if(tile == null) { continue; }
								
								writer.write(tile.getPrimitiveSites()[0].getName() + "=" + (interfaceBits + blockConfigurationBitsA) + "\n");
								writer.write(tile.getPrimitiveSites()[1].getName() + "=" + (interfaceBits + blockConfigurationBitsB) + "\n");
							}
							else// if(blockSubTypes.get(blockSubTypeIndex).getName().contains("IOB"))
							{
								Tile tile = findTile(blockSubTypes.get(blockSubTypeIndex), x, y);
								if(tile == null) { continue; }
								
								writer.write(tile.getName() + "=" + (interfaceBits + blockConfigurationBitsA) + "\n");
							}
						} catch (IOException e) { e.printStackTrace(); }
					
					resourceBits = interconnectionBits + interfaceBits + blockConfigurationBitsA + blockConfigurationBitsB;
				}
			}
		}
		
		
		// Start from the BOTTOM part of the FPGA
		clkRows = fpga.getDeviceSpecification().getBottomNumberOfRows();
		for(int rowIndex = 0; rowIndex < clkRows; rowIndex++)
		{
			for(int blockSubTypeIndex = 0;  blockSubTypeIndex < blockSubTypes.size(); blockSubTypeIndex++)
			{
				// Get the frames for the column
				lstFrames = fpga.getConfigurationBlockFrames(1, fpga.getDeviceSpecification().getLogicBlockType(), rowIndex, blockSubTypeIndex);
				
				// Get the sensitive frames for the column
				// NOTE: the sensitive frames are already placed in the ebdFrames list
				sFrames = new ArrayList<Frame>();
				for(Frame frame : lstFrames)
				{
					sFrames.add(this.ebdFrames.get(frame.getFrameAddress()));
				}
				// The number of frames for the column
				columnFrames = blockSubTypes.get(blockSubTypeIndex).getFramesPerConfigurationBlock();
				// The number of switch boxes in the column
				columnHeightSW = 0;
				// The number of logic tiles in the column
				columnHeight = 0;
				// The number of interconnection frames for the column
				interconnectionFrames = 0;
				// The number of interface frames for the column
				interfaceFrames = 0;
				// The number of block configuration frames for the column
				blockConfiguration = 0;				
				
				if(blockSubTypes.get(blockSubTypeIndex).getName().contains("IOB"))
				{
					columnHeightSW 			= 20;
					columnHeight			= 20;
					interconnectionFrames 	= 25;
					interfaceFrames			= 2;
					blockConfiguration		= 26;
				}
				else if(blockSubTypes.get(blockSubTypeIndex).getName().contains("CLB"))
				{
					columnHeightSW			= 20;
					columnHeight 			= 20;
					interconnectionFrames 	= 24;
					interfaceFrames			= 0;
					blockConfiguration		= 11;
				}
				else if(blockSubTypes.get(blockSubTypeIndex).getName().contains("BRAM"))
				{
					columnHeightSW			= 20;
					columnHeight 			= 4;
					interconnectionFrames 	= 25;
					interfaceFrames			= 2;
					blockConfiguration		= 2;
				}
				else if (blockSubTypes.get(blockSubTypeIndex).getName().contains("DSP"))
				{
					columnHeightSW			= 20;
					columnHeight 			= 8;
					interconnectionFrames 	= 25;
					interfaceFrames			= 2;
					blockConfiguration		= 0;
				}
				else if (blockSubTypes.get(blockSubTypeIndex).getName().contains("CLK"))
				{
					columnHeightSW			= 20;
					columnHeight 			= 20;
					interconnectionFrames 	= 0;
					interfaceFrames			= 4;
					blockConfiguration		= 0;
				}
				else
				{
					continue;
				}

				// The number of frame-words for a single tile
				int wordsPerResourceSW = (frameSize - 1) / columnHeightSW;
				int wordsPerResource   = (frameSize - 1) / columnHeight;				
				int word = 0;
				int x	 = 0;
				int y	 = 0;
				for(int resourceIndex = 0; resourceIndex < columnHeight; resourceIndex++)
				{
					int frameIndex = 0;
					int resourceBits = 0;
					int interconnectionBits 		= 0;
					int interfaceBits 				= 0;
					int blockConfigurationBits 		= 0;
					int blockConfigurationBitsA 	= 0;
					int blockConfigurationBitsB 	= 0;
					
					if(resourceIndex < columnHeightSW)
					{
						// Get the interconnection bits
						for(; frameIndex < sFrames.size(); frameIndex++)
						{
							if(frameIndex > interconnectionFrames) { break; }
							for(int wordIndex = 0; wordIndex < wordsPerResourceSW; wordIndex++)
							{
								if(wordIndex == ((frameSize -1)/wordsPerResourceSW))
								{
									wordIndex++;
								}
								word = sFrames.get(frameIndex).getData().getFrameWord((resourceIndex * wordsPerResourceSW) + wordIndex);
								if(word != 0)
									interconnectionBits += bitCount(word);
							}
						}
						
						// Compute the Y value for the interconnection matrix
						// Check the position of the column (top/bottom)
						if(this.spec.getTopBottomFromFAR(lstFrames.get(0).getFrameAddress()) == 0)
						{
							y = (clkRows * columnHeightSW) + (this.spec.getRowFromFAR(lstFrames.get(0).getFrameAddress()) * columnHeightSW) + (resourceIndex);
						}
						else
						{
							y = (((fpga.getDeviceSpecification().getBottomNumberOfRows() - 1) - this.spec.getRowFromFAR(lstFrames.get(0).getFrameAddress())) * columnHeightSW) + (resourceIndex);
						}
						// Calculate the X value for the interconnection matrix
						x = this.spec.getColumnFromFAR(lstFrames.get(0).getFrameAddress());
						
						try { writer.write("INT_X" + x + "Y" + y + "=" + interconnectionBits + "\n"); } catch (IOException e) { e.printStackTrace(); }
					}
					
					// Get the interface bits
					for(; frameIndex < sFrames.size(); frameIndex++)
					{
						if(frameIndex > (interconnectionFrames + interfaceFrames)) { break; }
						for(int wordIndex = 0; wordIndex < wordsPerResource; wordIndex++)
						{
							if(wordIndex == ((frameSize -1)/wordsPerResource))
							{
								wordIndex++;
							}
							word = sFrames.get(frameIndex).getData().getFrameWord((resourceIndex * wordsPerResource) + wordIndex);
							if(word != 0)
							interfaceBits += bitCount(word);
						}
					}					
					
					// Get the block configuration bits
					for(; frameIndex < sFrames.size(); frameIndex++)
					{
						if(frameIndex > (interconnectionFrames + interfaceFrames + blockConfiguration)) { break; }
						for(int wordIndex = 0; wordIndex < wordsPerResource; wordIndex++)
						{
							if(wordIndex == ((frameSize -1)/wordsPerResource))
							{
								wordIndex++;
							}
							// There are two slices inside a single CLB
							word = sFrames.get(frameIndex).getData().getFrameWord((resourceIndex * wordsPerResource) + wordIndex);
							if(word != 0)
								blockConfigurationBitsA += bitCount(word);
							
							if(blockSubTypes.get(blockSubTypeIndex).getName().equals("CLB"))
							{
								word = sFrames.get(frameIndex).getData().getFrameWord((resourceIndex * wordsPerResource) + (wordIndex + 1));
								if(word != 0)
									blockConfigurationBitsB += bitCount(word);
								wordIndex++;
							}
						}
					}
					
					// Compute the Y value for the interconnection matrix
					// Check the position of the column (top/bottom)
					int bottomRows = fpga.getDeviceSpecification().getBottomNumberOfRows() - 1;
					int currentRow = this.spec.getRowFromFAR(lstFrames.get(0).getFrameAddress());
					
					if(this.spec.getTopBottomFromFAR(lstFrames.get(0).getFrameAddress()) == 0)
					{
						y = (clkRows * columnHeightSW) + (this.spec.getRowFromFAR(lstFrames.get(0).getFrameAddress()) * columnHeight) + (resourceIndex);
					}
					else
					{
						y = ((bottomRows - currentRow) * columnHeightSW) + (resourceIndex);
					}
					// Calculate the X value for the interconnection matrix
					x = this.spec.getColumnFromFAR(lstFrames.get(0).getFrameAddress());
					
					try { 
							if(blockSubTypes.get(blockSubTypeIndex).getName().equals("CLB"))
							{
								Tile tile = findTile(blockSubTypes.get(blockSubTypeIndex), x, y);
								if(tile == null) { continue; }
								
								writer.write(tile.getPrimitiveSites()[0].getName() + "=" + (interfaceBits + blockConfigurationBitsA) + "\n");
								writer.write(tile.getPrimitiveSites()[1].getName() + "=" + (interfaceBits + blockConfigurationBitsB) + "\n");
								
							}
							else //if(blockSubTypes.get(blockSubTypeIndex).getName().contains("IOB"))
							{
								Tile tile = findTile(blockSubTypes.get(blockSubTypeIndex), x, y);
								if(tile == null) { continue; }
								
								writer.write(tile.getName() + "=" + (interfaceBits + blockConfigurationBitsA) + "\n");
							}
						} catch (IOException e) { e.printStackTrace(); }
					
					resourceBits = interconnectionBits + interfaceBits + blockConfigurationBitsA + blockConfigurationBitsB;
				}
			}
		}		
		try {
			writer.close();
		}catch(IOException ex){ return;}
	}
	
	
	private Tile findTile(BlockSubType type, int x, int y)
	{			
		Tile tiles[][] = dev.getTiles();
		
		for(int xx = 0; xx < tiles.length; xx++)
		{
			for(int yy = 0; yy < tiles[xx].length; yy++)
			{
				//System.out.println(tiles[xx][yy].getName());
				if(tiles[xx][yy] == null) continue;
				
				
				 if((tiles[xx][yy].getTileXCoordinate() == x) && (tiles[xx][yy].getTileYCoordinate() == y) && (tiles[xx][yy].getName().contains(type.getName())))
				 {
					 if(tiles[xx][yy].getName().contains("_CLB"))
						 continue;
					 else if (tiles[xx][yy].getName().contains("_IOB"))
						 continue;
					 else if (tiles[xx][yy].getName().contains("_LIOB"))
						 continue;
					 else if (tiles[xx][yy].getName().contains("_RIOB"))
						 continue;
					 else if (tiles[xx][yy].getName().contains("_CIOB"))
						 continue;
					 
					 //System.out.println(tiles[xx][yy].getName());
					 if(this.ebdSitesAdded.containsKey(tiles[xx][yy].getName()))
						 continue;
					 
					 ebdSitesAdded.put(tiles[xx][yy].getName(), tiles[xx][yy].getUniqueAddress());
					 return tiles[xx][yy];
				 }
			}
			
		}
		
		return null;		
	}
	
	/**
	 * @param frameAddress	: The frame address to be checked for the sensitive bit
	 * @param word			: The word index of the sensitive bit
	 * @param bit			: The bit index (whithin the word) of the sensitive bit
	 * @return				: The value of the sensitive bit (1 = the bit is sensitive)
	 */
	public int isSensitiveBit(int frameAddress, int word, int bit)
	{
		FrameData frame = this.getSensitiveFrame(frameAddress);		
		int frameWord = frame.getFrameWord(word);
		int sensitiveBit = (frameWord >> bit) & 0x01;
		
		return sensitiveBit;
	}
	
    /*==============================================================================================================
     * 
     * @fn			public long get_sensitiveBits()	
     * @brief		Gets the total number of sensitive bits 
     * @param[in]	None 
     * @param[out]	None
     * @returns		long		:The number of the sensitive bits in the design
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/
	public long get_sensitiveBits()
	{
		return this.sensitiveConfigurationBits_total;
	}
 
	/*==============================================================================================================
     * 
     * @fn			public long get_sensitiveConfigurationBits()
     * @brief		Gets the block configuration sensitive bits 
     * @param[in]	None 
     * @param[out]	None
     * @returns		long		:The number of the block configuration sensitive bits in the design
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/	
	public long get_sensitiveConfigurationBits()
	{
		return this.sensitiveConfigurationBits;
	}

	/*==============================================================================================================
     * 
     * @fn			public long get_sensitiveInterconnectionBits()
     * @brief		Gets the interconnection sensitive bits 
     * @param[in]	None 
     * @param[out]	None
     * @returns		long		:The number of the interconnection sensitive bits in the design
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/
	public long get_sensitiveInterconnectionBits()
	{
		return this.sensitiveInterconnectionBits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_IOB_sensitiveInterconnectionBits()
     * @brief		Gets the sensitive interconnection bits for the IOBs 
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive interconnection bits for the IOBs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/
	public int get_IOB_sensitiveInterconnectionBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("IOB"))
				bits += c.sensitiveInterconnectionBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_IOB_sensitiveInterfaceBits()
     * @brief		Gets the sensitive interface bits for the IOBs 
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive interface bits for the IOBs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/
	public int get_IOB_sensitiveInterfaceBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("IOB"))
				bits += c.sensitiveInterfaceBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_IOB_sensitiveBlockConfigurationBits()
     * @brief		Gets the sensitive block configuration bits for the IOBs 
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive configuration bits for the IOBs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/
	public int get_IOB_sensitiveBlockConfigurationBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("IOB"))
				bits += c.sensitiveConfigurationBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_CLB_sensitiveInterconnectionBits()
     * @brief		Gets the sensitive interconnection bits for the CLBs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive interconnection bits for the CLBs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/	
	public int get_CLB_sensitiveInterconnectionBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("CLB"))
				bits += c.sensitiveInterconnectionBits;
		}
		return bits;
	}

	/*==============================================================================================================
     * 
     * @fn			public int get_CLB_sensitiveInterfaceBits()
     * @brief		Gets the sensitive interface bits for the CLBs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive interface bits for the CLBs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/		
	public int get_CLB_sensitiveInterfaceBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("CLB"))
				bits += c.sensitiveInterfaceBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_CLB_sensitiveBlockConfigurationBits()
     * @brief		Gets the sensitive block configuration bits for the CLBs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive block configuration bits for the CLBs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/		
	public int get_CLB_sensitiveBlockConfigurationBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("CLB"))
				bits += c.sensitiveConfigurationBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_BRAM_sensitiveInterconnectionBits()
     * @brief		Gets the sensitive interconnection bits for the BRAMs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive interconnection bits for the BRAMs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/		
	public int get_BRAM_sensitiveInterconnectionBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("BRAM"))
				bits += c.sensitiveInterconnectionBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_BRAM_sensitiveInterfaceBits()
     * @brief		Gets the sensitive interface bits for the BRAMs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive interface bits for the BRAMs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/
	public int get_BRAM_sensitiveInterfaceBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("BRAM"))
				bits += c.sensitiveInterfaceBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_BRAM_sensitiveBlockConfigurationBits()
     * @brief		Gets the sensitive block configuration bits for the BRAMs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive block configuration bits for the BRAMs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/	
	public int get_BRAM_sensitiveBlockConfigurationBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("BRAM"))
				bits += c.sensitiveConfigurationBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_DSP_sensitiveInterconnectionBits()
     * @brief		Gets the sensitive interconnection bits for the DSPs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive interconnection bits for the DSPs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/	
	public int get_DSP_sensitiveInterconnectionBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("DSP"))
				bits += c.sensitiveInterconnectionBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_DSP_sensitiveInterfaceBits()
     * @brief		Gets the sensitive interface bits for the DSPs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive interface bits for the DSPs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/	
	public int get_DSP_sensitiveInterfaceBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("DSP"))
				bits += c.sensitiveInterfaceBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_DSP_sensitiveBlockConfigurationBits()
     * @brief		Gets the sensitive block configuration bits for the DSPs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive block configuration bits for the DSPs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/	
	public int get_DSP_sensitiveBlockConfigurationBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("DSP"))
				bits += c.sensitiveConfigurationBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_CLK_sensitiveInterconnectionBits()
     * @brief		Gets the sensitive interconnection bits for the CLKs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive interconnection bits for the CLKs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/	
	public int get_CLK_sensitiveInterconnectionBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("CLK"))
				bits += c.sensitiveInterconnectionBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_CLK_sensitiveInterfaceBits()
     * @brief		Gets the sensitive interface bits for the CLKs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive interface bits for the CLKs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/	
	public int get_CLK_sensitiveInterfaceBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("CLK"))
				bits += c.sensitiveInterfaceBits;
		}
		return bits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_CLK_sensitiveBlockConfigurationBits()
     * @brief		Gets the sensitive block configuration bits for the CLKs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive block configuration bits for the CLKs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/	
	public int get_CLK_sensitiveBlockConfigurationBits()
	{
		int bits = 0;
		for(column c : this.columnFrames)
		{
			if(c.columnType.contains("CLK"))
				bits += c.sensitiveConfigurationBits;
		}
		return bits;
	}

	/****************************************************************************************************************
	 * @brief		Saves the addresses of the sensitive frames in a text file
	 * @author 		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
	 ****************************************************************************************************************/
	public void sensitive_addresses_save()
	{
		try
		{
			ArrayList<Integer> sensitiveFrames = this.get_SensitiveFrames();
			if(sensitiveFrames == null) { return; }
			
			Collections.sort(sensitiveFrames);
			
			int framesNum = 1;
            int frameStartAddress = sensitiveFrames.get(0);
            int framePrevious = frameStartAddress;
            ArrayList<String> lines = new ArrayList<String>();
            FileWriter txtFile = new FileWriter(this.address_fileName);
			PrintWriter wFile = new PrintWriter(txtFile);
			//FrameAddressRegister far = new FrameAddressRegister(this.spec);
			
			int topBottom = 0;
			int column = 0;
			int row = 0;
			int totalFrames = 0;
			for(Integer address : sensitiveFrames)
			{
				topBottom = far.getTopBottomFromAddress(address);
				column = far.getColumnFromAddress(address);
				row = far.getRowFromAddress(address);
				// Exclude any area
				if((topBottom == 1) && (row == 3))
				{
					continue;
				}
				
				if (address == (framePrevious + 1))
                 {
                     framesNum++;
                     framePrevious = address;
                 }
                 else if (address > (framePrevious + 1))
                 {
                     lines.add("0x" + Integer.toHexString(frameStartAddress) + ", " + "0x" + Integer.toHexString(framesNum) + ",");
                     totalFrames += framesNum;
                     framesNum = 1;
                     frameStartAddress = address;
                     framePrevious = frameStartAddress;
                 }
			}
			totalFrames += framesNum;
			lines.add("0x" + Integer.toHexString(frameStartAddress) + ", " + "0x" + Integer.toHexString(framesNum));
			
			wFile.println("// Number of sensitive columns to be scanned");
			wFile.println("#define FRAMES_SENSITIVE_ADDRESSES_NUM  (" + lines.size() + ")");
			wFile.println("// Array including the sensitive frames' addresses with the number of frames for each column");
			wFile.println("static const unsigned int sensitiveFrameAddress[] = {");
			for(String line:lines)
			{
				wFile.println(line);
			}
			wFile.println("};");
			
			wFile.println("\nTotal frames = " + totalFrames);
			wFile.close();			
		}
		catch(Exception ex)
		{
			
		}
	}
	
	public void SensitiveBits_Distribution_Save()
	{
		try
		{			
			//FrameAddressRegister far = new FrameAddressRegister(this.spec);
			ArrayList<Integer> sensitiveFrames = this.get_SensitiveFrames();
			ArrayList<Integer> deviceFrames = this.get_DeviceFrames();
			if(deviceFrames == null) 	{ return; }
			if(sensitiveFrames == null) { return; }
			
			List<FrameData> sensitiveFramesData = new ArrayList<FrameData>();
			for(int address : sensitiveFrames)
			{
				FrameData data = getSensitiveFrame(address);
				if(data != null)
					sensitiveFramesData.add(data);
			}			
			
			/*Comparator<FrameData> comparator = new Comparator<FrameData>() {
			    public int compare(FrameData f1, FrameData f2)
			    {
			        if(f1.countBitsSet() > f2.countBitsSet())
			        	return 1;
			        else if(f1.countBitsSet() < f2.countBitsSet())
			        	return -1;
			        else
			        	return 0;
			    }
			};
			Collections.sort(sensitiveFramesData, comparator);*/
			
			HashMap<Integer, Integer> sensitiveBitsMap = new HashMap<Integer, Integer>();
			int frameBits = sensitiveFramesData.get(0).size() * 32;
			int frames = deviceFrames.size() - sensitiveFrames.size();
			sensitiveBitsMap.put(0, frames);
			for(int index = 1; index < frameBits; index++)
			{
				frames = 0;
				for(FrameData d : sensitiveFramesData)
				{
					if(d.countBitsSet() == index)
						frames++;
				}
				sensitiveBitsMap.put(index, frames);
			}

			File file = new File(this.sensitiveBits_distribution_fileName);
			if(file.exists())
				file.delete();
			
            FileWriter csvFile = new FileWriter(this.sensitiveBits_distribution_fileName);
			PrintWriter wFile = new PrintWriter(csvFile);
			
			
			wFile.println("Sensitive bits,Sensitive frames");
			for(int index = 0; index < frameBits; index++)
			{
				wFile.println(index + "," + sensitiveBitsMap.get(index));
			}
				
			wFile.close();			
		}
		catch(Exception ex)
		{
			
		}
	}
	/*==============================================================================================================
     * 
     * @fn			public long get_sensitiveInterfaceBits()
     * @brief		Gets the interface sensitive bits 
     * @param[in]	None 
     * @param[out]	None
     * @returns		long		:The number of the interface sensitive bits in the design
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/
	public long get_sensitiveInterfaceBits()
	{
		return this.sensitiveInterfaceBits;
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_CLK_sensitiveBlockConfigurationBits()
     * @brief		Gets the sensitive block configuration bits for the CLKs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive block configuration bits for the CLKs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/	
	public ArrayList<Integer> get_SensitiveFrames()
	{
		ArrayList<Integer> sensitiveFrames = new ArrayList<Integer>();
		Iterator it = this.ebdFrames.entrySet().iterator();
		int address = 0;
		while(it.hasNext())
		{
			Map.Entry item = (Map.Entry)it.next();
			if(((Frame)item.getValue()).getData().countBitsSet() > 0)
			{
			address = ((Frame)item.getValue()).getFrameAddress();
			if((this.fpga.getFAR().getRowFromAddress(address) == 3) && (this.fpga.getFAR().getTopBottomFromAddress(address) == 1))
			{
				continue;
			}
			sensitiveFrames.add(address);
			}
		}
		return sensitiveFrames;
	}
	
	public List<Frame> get_FrameAddresses(int topBottom, int row, int column)
	{
		List<Frame> lstFrames = new ArrayList<Frame>();
		//List<BlockSubType> blockSubTypes = fpga.getDeviceSpecification().getOverallColumnLayout();
		lstFrames = fpga.getConfigurationBlockFrames(topBottom, fpga.getDeviceSpecification().getLogicBlockType(), row, column);
		
		return lstFrames;
	}
	
	public List<Integer> get_FrameData(Integer frameAddress)
	{
		if(this.ebdFrames.containsKey((int)frameAddress))
		{
			FrameData fData = this.ebdFrames.get(frameAddress).getData();
			return fData.getAllFrameWords();
		}
		return null;
	}
	
	public int get_Columns()
	{
		return fpga.getDeviceSpecification().getOverallColumnLayout().size();
	}
	
	public int get_Rows()
	{
		return fpga.getDeviceSpecification().getBottomNumberOfRows();
	}
	
	/*==============================================================================================================
     * 
     * @fn			public int get_CLK_sensitiveBlockConfigurationBits()
     * @brief		Gets the sensitive block configuration bits for the CLKs
     * @param[in]	None 
     * @param[out]	None
     * @returns		int		:The number of sensitive block configuration bits for the CLKs
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/	
	public ArrayList<Integer> get_DeviceFrames()
	{
		ArrayList<Integer> deviceFrames = new ArrayList<Integer>();
		ArrayList<Frame> frames = new ArrayList<Frame>();
		frames = this.fpga.getAllFrames();
		for(Frame frame : frames)
		{
			if(spec.getBlockTypeFromFAR(frame.getFrameAddress()) == 0)
			{
				deviceFrames.add(frame.getFrameAddress());
			}
		}
		return deviceFrames;
	}
	
	
	public void getResults(){
		
		long totalSensitiveBits = get_sensitiveBits();
	    /*long interconnectionSensitiveBits = get_sensitiveInterconnectionBits();
	    long interfaceSensitiveBits = get_sensitiveInterfaceBits();
	    long blockSensitiveBits = get_sensitiveConfigurationBits();*/
	   
	   /* int IOB_sensitiveInterconnectionBits = get_IOB_sensitiveInterconnectionBits();
	    int IOB_sensitiveInterfaceBits   = get_IOB_sensitiveInterfaceBits();
	    int IOB_sensitiveConfigurationBits  = get_IOB_sensitiveBlockConfigurationBits();
	   
	    int CLB_sensitiveInterconnectionBits = get_CLB_sensitiveInterconnectionBits();
	    int CLB_sensitiveInterfaceBits   = get_CLB_sensitiveInterfaceBits();
	    int CLB_sensitiveConfigurationBits  = get_CLB_sensitiveBlockConfigurationBits();
	   
	    int BRAM_sensitiveInterconnectionBits = get_BRAM_sensitiveInterconnectionBits();
	    int BRAM_sensitiveInterfaceBits   = get_BRAM_sensitiveInterfaceBits();
	    int BRAM_sensitiveConfigurationBits  = get_BRAM_sensitiveBlockConfigurationBits();
	   
	    int DSP_sensitiveInterconnectionBits = get_DSP_sensitiveInterconnectionBits();
	    int DSP_sensitiveInterfaceBits   = get_DSP_sensitiveInterfaceBits();
	    int DSP_sensitiveConfigurationBits  = get_DSP_sensitiveBlockConfigurationBits();
	    
	    int CLK_sensitiveInterconnectionBits = get_CLK_sensitiveInterconnectionBits();
	    int CLK_sensitiveInterfaceBits   = get_CLK_sensitiveInterfaceBits();
	    int CLK_sensitiveConfigurationBits  = get_CLK_sensitiveBlockConfigurationBits();*/
	    
	    this.senstiveFrames = this.sensitiveInterconnectionFrames + this.sensitiveInterfaceFrames + this.sensitiveConfigurationFrames;
	    
	    logger.filePrintln("\nSensitive bits analsysis for all configuration frames of the FPGA");
	    logger.filePrintln("\nSensitive bits (total) #01=" + totalSensitiveBits + "#");
/*	    logger.filePrintln("Sensitive interconnection bits #02=" + interconnectionSensitiveBits + "#");
		logger.filePrintln("Sensitive interface bits #03=" + interfaceSensitiveBits + "#");
	    logger.filePrintln("Sensitive block configuration bits #04=" + blockSensitiveBits + "#");

	    logger.filePrintln("IOBs Sensitive interconnection bits #05=" + IOB_sensitiveInterconnectionBits + "#");
	    logger.filePrintln("IOBs Sensitive interface bits #06=" + IOB_sensitiveInterfaceBits + "#");
	    logger.filePrintln("IOBs Sensitive block configuration bits 07=" + IOB_sensitiveConfigurationBits + "#");

	    logger.filePrintln("CLBs Sensitive interconnection bits #08=" + CLB_sensitiveInterconnectionBits + "#");
	    logger.filePrintln("CLBs Sensitive interface bits #09=" + CLB_sensitiveInterfaceBits + "#");
	    logger.filePrintln("CLBs Sensitive block configuration bits #0A=" + CLB_sensitiveConfigurationBits + "#");

	    logger.filePrintln("BRAMs Sensitive interconnection bits #0B=" + BRAM_sensitiveInterconnectionBits + "#");
	    logger.filePrintln("BRAMs Sensitive interface bits #0C=" + BRAM_sensitiveInterfaceBits + "#");
	    logger.filePrintln("BRAMs Sensitive block configuration bits #0D=" + BRAM_sensitiveConfigurationBits + "#");

	    logger.filePrintln("DSPs Sensitive interconnection bits #0E=" + DSP_sensitiveInterconnectionBits + "#");
	    logger.filePrintln("DSPs Sensitive interface bits #0F=" + DSP_sensitiveInterfaceBits + "#");
	    logger.filePrintln("DSPs Sensitive block configuration bits #10=" + DSP_sensitiveConfigurationBits + "#");

	    logger.filePrintln("CLKs Sensitive interconnection bits #11=" + CLK_sensitiveInterconnectionBits + "#");
	    logger.filePrintln("CLKs Sensitive interface bits #12=" + CLK_sensitiveInterfaceBits + "#");
	    logger.filePrintln("CLKs Sensitive block configuration bits #13=" + CLK_sensitiveConfigurationBits + "#");
*/
	    logger.filePrintln("\nSensitive interconnection frames #14=" + this.sensitiveInterconnectionFrames + "#");
	    logger.filePrintln("Sensitive interface frames #15=" + this.sensitiveInterfaceFrames + "#");
	    logger.filePrintln("Sensitive block configuration frames #16=" + this.sensitiveConfigurationFrames + "#");
	    logger.filePrintln("Sensitive frames #17=" + (this.senstiveFrames) + "#");

	    logger.filePrintln("\nDevice interconnection frames #18=" + this.interconnectionFrames + "#");
	    logger.filePrintln("Device interface frames #19=" + this.interfaceFrames + "#");
	    logger.filePrintln("Device block configuration frames #1A=" + this.configurationFrames + "#");
	    logger.filePrintln("Device frames #1B=" + (this.interconnectionFrames + this.interfaceFrames + this.configurationFrames) + "#");

	    logger.filePrintln("\nSensitive bits (total) #01=" + totalSensitiveBits + "#");
	   /* logger.filePrintln("Sensitive interconnection bits #02=" + interconnectionSensitiveBits + "#");
	    logger.filePrintln("Sensitive interface bits #03=" + interfaceSensitiveBits + "#");
	    logger.filePrintln("Sensitive block configuration bits #04=" + blockSensitiveBits + "#");

	    logger.filePrintln("IOBs Sensitive interconnection bits #05=" + IOB_sensitiveInterconnectionBits + "#");
	    logger.filePrintln("IOBs Sensitive interface bits #06=" + IOB_sensitiveInterfaceBits + "#");
	    logger.filePrintln("IOBs Sensitive block configuration bits 07=" + IOB_sensitiveConfigurationBits + "#");

	    logger.filePrintln("CLBs Sensitive interconnection bits #08=" + CLB_sensitiveInterconnectionBits + "#");
	    logger.filePrintln("CLBs Sensitive interface bits #09=" + CLB_sensitiveInterfaceBits + "#");
	    logger.filePrintln("CLBs Sensitive block configuration bits #0A=" + CLB_sensitiveConfigurationBits + "#");

	    logger.filePrintln("BRAMs Sensitive interconnection bits #0B=" + BRAM_sensitiveInterconnectionBits + "#");
	    logger.filePrintln("BRAMs Sensitive interface bits #0C=" + BRAM_sensitiveInterfaceBits + "#");
	    logger.filePrintln("BRAMs Sensitive block configuration bits #0D=" + BRAM_sensitiveConfigurationBits + "#");

	    logger.filePrintln("DSPs Sensitive interconnection bits #0E=" + DSP_sensitiveInterconnectionBits + "#");
	    logger.filePrintln("DSPs Sensitive interface bits #0F=" + DSP_sensitiveInterfaceBits + "#");
	    logger.filePrintln("DSPs Sensitive block configuration bits #10=" + DSP_sensitiveConfigurationBits + "#");

	    logger.filePrintln("CLKs Sensitive interconnection bits #11=" + CLK_sensitiveInterconnectionBits + "#");
	    logger.filePrintln("CLKs Sensitive interface bits #12=" + CLK_sensitiveInterfaceBits + "#");
	    logger.filePrintln("CLKs Sensitive block configuration bits #13=" + CLK_sensitiveConfigurationBits + "#");*/

	    logger.filePrintln("\nSensitive interconnection frames #14=" + this.sensitiveInterconnectionFrames + "#");
	    logger.filePrintln("Sensitive interface frames #15=" + this.sensitiveInterfaceFrames + "#");
	    logger.filePrintln("Sensitive block configuration frames #16=" + this.sensitiveConfigurationFrames + "#");
	    logger.filePrintln("Sensitive frames #17=" + (this.senstiveFrames) + "#");

	    logger.filePrintln("\nDevice interconnection frames #18=" + this.interconnectionFrames + "#");
	    logger.filePrintln("Device interface frames #19=" + this.interfaceFrames + "#");
	    logger.filePrintln("Device block configuration frames #1A=" + this.configurationFrames + "#");
	    logger.filePrintln("Device frames #1B=" + (this.interconnectionFrames + this.interfaceFrames + this.configurationFrames) + "#");
	    
	    System.out.println("\nSensitive bits analsysis for all configuration frames of the FPGA");
	    System.out.println("\nSensitive bits (total) #01=" + totalSensitiveBits + "#");
	    /*System.out.println("Sensitive interconnection bits #02=" + interconnectionSensitiveBits + "#");
	    System.out.println("Sensitive interface bits #03=" + interfaceSensitiveBits + "#");
	    System.out.println("Sensitive block configuration bits #04=" + blockSensitiveBits + "#");
	   
	    System.out.println("IOBs Sensitive interconnection bits #05=" + IOB_sensitiveInterconnectionBits + "#");
	    System.out.println("IOBs Sensitive interface bits #06=" + IOB_sensitiveInterfaceBits + "#");
	    System.out.println("IOBs Sensitive block configuration bits 07=" + IOB_sensitiveConfigurationBits + "#");
	   
	    System.out.println("CLBs Sensitive interconnection bits #08=" + CLB_sensitiveInterconnectionBits + "#");
	    System.out.println("CLBs Sensitive interface bits #09=" + CLB_sensitiveInterfaceBits + "#");
	    System.out.println("CLBs Sensitive block configuration bits #0A=" + CLB_sensitiveConfigurationBits + "#");
	   
	    System.out.println("BRAMs Sensitive interconnection bits #0B=" + BRAM_sensitiveInterconnectionBits + "#");
	    System.out.println("BRAMs Sensitive interface bits #0C=" + BRAM_sensitiveInterfaceBits + "#");
	    System.out.println("BRAMs Sensitive block configuration bits #0D=" + BRAM_sensitiveConfigurationBits + "#");
	   
	    System.out.println("DSPs Sensitive interconnection bits #0E=" + DSP_sensitiveInterconnectionBits + "#");
	    System.out.println("DSPs Sensitive interface bits #0F=" + DSP_sensitiveInterfaceBits + "#");
	    System.out.println("DSPs Sensitive block configuration bits #10=" + DSP_sensitiveConfigurationBits + "#");
	    
	    System.out.println("CLKs Sensitive interconnection bits #11=" + CLK_sensitiveInterconnectionBits + "#");
	    System.out.println("CLKs Sensitive interface bits #12=" + CLK_sensitiveInterfaceBits + "#");
	    System.out.println("CLKs Sensitive block configuration bits #13=" + CLK_sensitiveConfigurationBits + "#");
	    */
	    System.out.println("\nSensitive interconnection frames #14=" + this.sensitiveInterconnectionFrames + "#");
	    System.out.println("Sensitive interface frames #15=" + this.sensitiveInterfaceFrames + "#");
	    System.out.println("Sensitive block configuration frames #16=" + this.sensitiveConfigurationFrames + "#");
	    System.out.println("Sensitive frames #17=" + (this.senstiveFrames) + "#");
	    
	    System.out.println("\nDevice interconnection frames #18=" + this.interconnectionFrames + "#");
	    System.out.println("Device interface frames #19=" + this.interfaceFrames + "#");
	    System.out.println("Device block configuration frames #1A=" + this.configurationFrames + "#");
	    System.out.println("Device frames #1B=" + (this.interconnectionFrames + this.interfaceFrames + this.configurationFrames) + "#");
	    
	    System.out.println("\nSensitive bits (total) #01=" + totalSensitiveBits + "#");
	   /* System.out.println("Sensitive interconnection bits #02=" + interconnectionSensitiveBits + "#");
	    System.out.println("Sensitive interface bits #03=" + interfaceSensitiveBits + "#");
	    System.out.println("Sensitive block configuration bits #04=" + blockSensitiveBits + "#");
	   
	    System.out.println("IOBs Sensitive interconnection bits #05=" + IOB_sensitiveInterconnectionBits + "#");
	    System.out.println("IOBs Sensitive interface bits #06=" + IOB_sensitiveInterfaceBits + "#");
	    System.out.println("IOBs Sensitive block configuration bits 07=" + IOB_sensitiveConfigurationBits + "#");
	   
	    System.out.println("CLBs Sensitive interconnection bits #08=" + CLB_sensitiveInterconnectionBits + "#");
	    System.out.println("CLBs Sensitive interface bits #09=" + CLB_sensitiveInterfaceBits + "#");
	    System.out.println("CLBs Sensitive block configuration bits #0A=" + CLB_sensitiveConfigurationBits + "#");
	   
	    System.out.println("BRAMs Sensitive interconnection bits #0B=" + BRAM_sensitiveInterconnectionBits + "#");
	    System.out.println("BRAMs Sensitive interface bits #0C=" + BRAM_sensitiveInterfaceBits + "#");
	    System.out.println("BRAMs Sensitive block configuration bits #0D=" + BRAM_sensitiveConfigurationBits + "#");
	   
	    System.out.println("DSPs Sensitive interconnection bits #0E=" + DSP_sensitiveInterconnectionBits + "#");
	    System.out.println("DSPs Sensitive interface bits #0F=" + DSP_sensitiveInterfaceBits + "#");
	    System.out.println("DSPs Sensitive block configuration bits #10=" + DSP_sensitiveConfigurationBits + "#");
	    
	    System.out.println("CLKs Sensitive interconnection bits #11=" + CLK_sensitiveInterconnectionBits + "#");
	    System.out.println("CLKs Sensitive interface bits #12=" + CLK_sensitiveInterfaceBits + "#");
	    System.out.println("CLKs Sensitive block configuration bits #13=" + CLK_sensitiveConfigurationBits + "#");*/
	    
	    System.out.println("\nSensitive interconnection frames #14=" + this.sensitiveInterconnectionFrames + "#");
	    System.out.println("Sensitive interface frames #15=" + this.sensitiveInterfaceFrames + "#");
	    System.out.println("Sensitive block configuration frames #16=" + this.sensitiveConfigurationFrames + "#");
	    System.out.println("Sensitive frames #17=" + (this.senstiveFrames) + "#");
	    
	    System.out.println("\nDevice interconnection frames #18=" + this.interconnectionFrames + "#");
	    System.out.println("Device interface frames #19=" + this.interfaceFrames + "#");
	    System.out.println("Device block configuration frames #1A=" + this.configurationFrames + "#");
	    System.out.println("Device frames #1B=" + (this.interconnectionFrames + this.interfaceFrames + this.configurationFrames) + "#");
	    
	    
	    
	    
	    
	    /*System.out.println("\n\n" + totalSensitiveBits);
	    System.out.println(interconnectionSensitiveBits);
	    System.out.println(interfaceSensitiveBits);
	    System.out.println(blockSensitiveBits);
	   
	    System.out.println(IOB_sensitiveInterconnectionBits);
	    System.out.println(IOB_sensitiveInterfaceBits);
	    System.out.println(IOB_sensitiveConfigurationBits);
	   
	    System.out.println(CLB_sensitiveInterconnectionBits);
	    System.out.println(CLB_sensitiveInterfaceBits);
	    System.out.println(CLB_sensitiveConfigurationBits);
	   
	    System.out.println(BRAM_sensitiveInterconnectionBits);
	    System.out.println(BRAM_sensitiveInterfaceBits);
	    System.out.println(BRAM_sensitiveConfigurationBits);
	   
	    System.out.println(DSP_sensitiveInterconnectionBits);
	    System.out.println(DSP_sensitiveInterfaceBits);
	    System.out.println(DSP_sensitiveConfigurationBits);
	    
	    System.out.println(CLK_sensitiveInterconnectionBits);
	    System.out.println(CLK_sensitiveInterfaceBits);
	    System.out.println(CLK_sensitiveConfigurationBits);
	    
	    System.out.println("\n" + this.sensitiveInterconnectionFrames);
	    System.out.println(this.sensitiveInterfaceFrames);
	    System.out.println(this.sensitiveConfigurationFrames);
	    System.out.println((this.senstiveFrames));
	    
	    System.out.println("\n" + this.interconnectionFrames);
	    System.out.println(this.interfaceFrames);
	    System.out.println(this.configurationFrames);
	    System.out.println((this.interconnectionFrames + this.interfaceFrames + this.configurationFrames));*/
	    
	}
	
	public FrameData getSensitiveFrame(int frameAddress)
	{
		if(this.ebdFrames.containsKey(frameAddress))
		{
			FrameData fData = this.ebdFrames.get(frameAddress).getData();
			return fData;
		}
		return null;
	}
	
	/*==============================================================================================================
     * 
     * @fn			private Byte[] bytesTrim(byte[] bytes)
     * @brief		This function removes any padding bytes (0x0D) from the input byte array
     * @param[in]	bytes		:The byte array which contains the data read in ascii foramt 
     * @param[out]	None
     * @returns		Byte[]		:The byte array without the padding bytes (0x0D) 
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/
    private Byte[] bytesTrim(byte[] bytes)
    {
        List<Byte> frameBytes = new ArrayList<Byte>();
        for (byte b : bytes)
        {
            if (b > 0x0D)
                frameBytes.add((Byte)b);
        }

        return frameBytes.toArray(new Byte[frameBytes.size()]);
    }

    /*==============================================================================================================
     * 
     * @fn			private ArrayList<Integer> bytesToWords(Byte[] frameBytes)	
     * @brief		This function converts the data of the frame which is in ascii format to its binary equivalent (words)
     * @param[in]	frameBytes		:The bytes of the frame in ascii format 
     * @param[out]	List<Integer>	:The words of the frame in binary format
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/
    private ArrayList<Integer> bytesToWords(Byte[] frameBytes)
    {
        ArrayList<Integer> frame = new ArrayList<Integer>();
        Integer word;
        int bitIndex;
        int byteIndex = 0;
        for (byteIndex = 0; byteIndex < frameBytes.length; byteIndex += 32)
        {
            word = 0;
            for (bitIndex = 0; bitIndex < 32; bitIndex++)
            {
                word |= ((Integer)(frameBytes[byteIndex + bitIndex] - 0x30) << (31 - bitIndex));
            }
            frame.add(word);
        }
        return frame;
    }
    
    /*==============================================================================================================
     * 
     * @fn			private List<Byte> asciiBytes_toBinary(Byte[] frameBytes)	
     * @brief		This function converts the data of the frame which is in ascii format to its binary equivalent
     * @param[in]	frameBytes		:The bytes of the frame in ascii format 
     * @param[out]	List<Byte>		:The bytes of the frame in binary format
     * @author		University of Piraeus (Aitzan Sari, Dimitris Agiakatsikas)
     * 
     *==============================================================================================================*/
    private List<Byte> asciiBytes_toBinary(Byte[] frameBits)
    {
        List<Byte> frame = new ArrayList<Byte>();
        byte b;
        int bitIndex;
        int byteIndex = 0;
        int val;
        //System.out.println(frameBits.length);
        //for (byteIndex = 0; byteIndex < 2592; byteIndex += 8)
    	for (byteIndex = 0; byteIndex < frameBits.length; byteIndex += 8)
        {
            b = 0;
            for (bitIndex = 0; bitIndex < 8; bitIndex++)
            {
                val = (frameBits[byteIndex + bitIndex] - 0x30) << (7 - bitIndex);
                b   |= (byte)(val);
            }
            frame.add((Byte)b);
        }
        //System.out.println(frame.size());
        return frame;
    }
    
   
    private int bitCount(int word)
    {
    	int bits = 0;
    	
    	int bitIndex = 31;
    	while(bitIndex > -1)
    	{
    		if(((word >> bitIndex) & 0x01) == 1)
    			bits++;
    		bitIndex--;
    	}
    	
    	return bits;
    }
    
    public Device getDevice()
    {
    	return this.dev;
    }
    
    public long getSensitiveFrames()
    {
    	return this.senstiveFrames;
    }
    
    public long getSensitiveInterconnectionFrames()
    {
    	return this.sensitiveInterconnectionFrames;
    }
    
    public long getSensitiveInterfaceFrames()
    {
    	return this.sensitiveInterfaceFrames;
    }
    
    public long getSensitiveConfigurationFrames()
    {
    	return this.sensitiveConfigurationFrames;
    }
    
    public String getRandomSensitiveBit(){
    	if(sAddressKey.isEmpty() == true) { // When keys == empty then we have injected to all essential bits
    		return "S";
    	}
    	int rndKeyIndex; 
    	int frameAddress; 
    	int rndSbitIndex;
    	int bitPosition;
    	ArrayList<Integer> sBits; 
    	do {
    		rndKeyIndex = randomGenerator.nextInt(sAddressKey.size()); // grab a random index for the keyset
        	frameAddress = sAddressKey.get(rndKeyIndex); // grab a random address from the keyset
        	sBits = bits2Inject.get(frameAddress); // bring the sensitive bits for that frame address
        	if(sBits.isEmpty() == true) { // remove the hashkey if sbits list is empty
        		sAddressKey.remove(rndKeyIndex);
        	}
    	} while(sBits.isEmpty() == true );
    	rndSbitIndex = randomGenerator.nextInt(sBits.size());
		bitPosition = sBits.get(rndSbitIndex); // get a bit position from the list
		sBits.remove(rndSbitIndex); // remove that bit since we will inject a fault there
		if (sBits.isEmpty() == true ) { // if the sBits list is empty the remove  thar key frameAddress from our Hashsetlist
    		sAddressKey.remove(rndKeyIndex);
    	}
		
    /* 	FrameAddressRegister far = new FrameAddressRegister(this.spec); // check the row, topbottom etc. from the faddress
    	far.setFAR(frameAddress);
    	int column = far.getColumn();
		int row = far.getRow();
		int top = far.getTopBottom();
		int minor = far.getMinor();
		int type = far.getBlockType();
		System.out.println("top = " + top + " Column = " + column + " row = " + row + " minor = " + minor  + " type = " + type);*/    	
    	long num = (((long) frameAddress) << 12) | bitPosition;
    	//String str = "N" + address;
    	String comprAddress = "";
    	if(SRFrame(frameAddress) == true) {
    		comprAddress = String.format("M" + "%010X", num); // Sent to microblaze the frame and bitposition to inject
    	} else {
    		comprAddress = String.format("N" + "%010X", num); // Sent to microblaze the frame and bitposition to inject
    	}
    	
    	//comprAddress = String.format("N" + "%010X", num); // Sent to microblaze the frame and bitposition to inject
    	
    	return comprAddress;
    }
    
	public void createDUTSensitiveBits(){
 
//		IOB, CLB, CLB, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, DSP, CLB, CLB, CLB, CLB, DSP, CLB, CLB, 
//		BRAMINTERCONNECT, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, DSP, CLB, 
//		CLB, CLB, CLB, DSP, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, CLB, CLB, IOB, CLB, CLB, CLB, CLB, CLB, 
//		CLB, CLB, CLB, CLB, CLB, CLK, CLB, CLB, CLB, CLB, IOB, CLB, CLB, CLB, CLB, BRAMINTERCONNECT, CLB, 
//		CLB, DSP, CLB, CLB, CLB, CLB, DSP, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, CLB, CLB, CLB, CLB, CLB, 
//		CLB, BRAMINTERCONNECT, CLB, CLB, DSP, CLB, CLB, CLB, CLB, DSP, CLB, CLB, BRAMINTERCONNECT, CLB, 
		
		//Virtex6 240 has 95 columns. 47 + 1 + 47
		//left  clock columns: [0,46]
		//right clock columns: [48,94]

		//Frame frame = null;
    	int frameSize = spec.getFrameSize() * 32;
    	int column = 0;
    	int top = 0;
    	int row = 0;
    	int type = 0;
    	int minor =0;
    	//FrameAddressRegister far = new FrameAddressRegister(this.spec);
    	// find a frame located in the injection area
    	int frameAddress = 0;
    	Collection<Integer> keys = ebdFrames.keySet();
    	for(Integer key : keys) {
    		Frame frame = ebdFrames.get(key);
    		if (frame != null) {
    			if (frame.getData() != null) {
//    				while ( (top != 1) || (frame == null) || (row != 2) || (setBits.isEmpty() == true) || (type != 0) ); // (type != 0) 
	    			frameAddress = frame.getFrameAddress();
	    			far.setFAR(frameAddress);
	    			column = far.getColumn();
	    			row = far.getRow();
	    			top = far.getTopBottom();
	    			minor = far.getMinor();
	    			if (SRFrame(frameAddress)  == true) {
	    				//System.out.println("Minor = " + minor);
	    			}
	    			//if  ( ( ( (top == 0)  && ( column < 50) && (row == 0) )  == false ) )  {
    				if  ( ( (top == 0)  && ( column < 50) && (row == 0) )  == false && IOBFrame(frameAddress) == false ){ //&& SRFrame(frameAddress)  == false)   {
    				//if  ( ( (top == 1)  && (row > 1)  ) == true )      {
	    				ArrayList<Integer> setBits = new ArrayList<Integer>();
	    				for(int j = 0; j < frameSize; j++){
		    	    		if(frame.getData().getBit(j) == 1){
		    	    			setBits.add(j);
		    	    			sDUTBits ++;
		    	    		}		
		    	    	}
	    				if (setBits.isEmpty() == false) {
	    					bits2Inject.put(frameAddress, setBits);
	    				}
	    			}
    			}
    		}
    	}
    	for ( Integer key : bits2Inject.keySet() ) {
    	    sAddressKey.add(key);
    	}
	}
	
	public boolean SRFrame(int frameAddress) {
		boolean SRFrame = false;
		BlockSubType block = null;
    	//int minor =0;
		//FrameAddressRegister far = new FrameAddressRegister(this.spec);
		far.setFAR(frameAddress);
		//minor = far.getMinor();
		block = spec.getBlockSubtype(this.spec, far);
		//int blockFrames = block.getFramesPerConfigurationBlock();
		//if ( ( (block.toString() == "CLB") && ( minor == 0 ) )  == true ) { //>= (blockFrames - 20 ) ) ) == true ) {
		//if ( ( (block.toString() == "CLB") && ( minor >= (blockFrames - 27 ) ) ) == true ) {
		//if ( ( (block.toString() == "CLB") && ( minor >= 25)  ) == true ) {
		if ( (block.toString() == "CLB") == true ) {
		SRFrame = true;
		}
		return SRFrame;
	}
	
	public boolean IOBFrame(int frameAddress) {
		boolean IOB = false;
		BlockSubType block = null;
		//FrameAddressRegister far = new FrameAddressRegister(this.spec);
		far.setFAR(frameAddress);
		block = spec.getBlockSubtype(this.spec, far);
		if ( ( (block.toString() == "IOB") ) == true ) {
			IOB = true;
		}
		return IOB;
	}
    
	public long getSDutBits(){
		return this.sDUTBits;
	}
    
}

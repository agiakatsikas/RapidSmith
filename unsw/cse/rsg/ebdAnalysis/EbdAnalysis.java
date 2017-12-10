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
package unsw.cse.rsg.ebdAnalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.Bitstream;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.BitstreamParseException;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.BitstreamParser;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.Packet;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.PacketList;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.PacketOpcode;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.PacketType;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.RegisterType;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.FPGA;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.Frame;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.FrameAddressRegister;
import edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification.XilinxConfigurationSpecification;
import edu.byu.ece.rapidSmith.bitstreamTools.examples.FrameContents;
import edu.byu.ece.rapidSmith.bitstreamTools.examples.support.BitstreamOptionParser;
import joptsimple.OptionSet;

public class EbdAnalysis {
	
	ArrayList<Frame> ebdFrames = new ArrayList<Frame>();
	//Stting bitName = ""
	
	
	public static void main(String[] args) {
		
		/////////////////////////////////////////////////////////////////////
		// Setup class and Options
		/////////////////////////////////////////////////////////////////////
		
		/** Setup parser **/
		BitstreamOptionParser cmdLineParser = new BitstreamOptionParser();
		cmdLineParser.addInputBitstreamOption();
		
		OptionSet options = null;
		try {
			options = cmdLineParser.parse(args);
		}
		catch(Exception e){
			System.err.println(e.getMessage());
			System.exit(1);			
		}		

		BitstreamOptionParser.printExecutableHeaderMessage(FrameContents.class);
				
		/////////////////////////////////////////////////////////////////////
		// Begin basic command line parsing
		/////////////////////////////////////////////////////////////////////
		cmdLineParser.checkHelpOptionExitOnHelpMessage(options);

		
		/////////////////////////////////////////////////////////////////////
		// 1. Parse bitstream
		/////////////////////////////////////////////////////////////////////
		FPGA fpga = cmdLineParser.createFPGAFromBitstreamOrReadbackFileExitOnError(options);
		ArrayList<Frame> fpgaFrames = readEbd(fpga);
		
		Bitstream bitstream = null;
		try {
			bitstream = BitstreamParser.parseBitstream(cmdLineParser.getOutputFileNameStringExitOnError(options));
		} catch (BitstreamParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Get part packets
		PacketList packets = bitstream.getPackets();
		Iterator<Packet> pi = packets.iterator();
		Packet p;
		TreeSet<Integer> farAddresses = new TreeSet<Integer>();
		while (pi.hasNext()) {
			p = pi.next();
			if (p.getPacketType() == PacketType.ONE &&
				p.getOpcode() == PacketOpcode.WRITE &&
				p.getRegType() == RegisterType.FAR) {
				// Get FAR address
				Integer farAddress = p.getData().get(0);
				farAddresses.add(farAddress);
			}
		}
		String address = null;
		for (Integer ad : farAddresses) {
			address = Integer.toHexString(ad);
			System.out.println(address);
		}
		
		
	}	
	
	public static ArrayList<Frame> readEbd(FPGA fpga) {
		int count = 0;
		XilinxConfigurationSpecification spec = fpga.getDeviceSpecification();
		ArrayList<Frame> fpgaFrames = fpga.getAllFrames();
		for(Frame frame: fpgaFrames) {
			int frameAddress = frame.getFrameAddress();
			int blockType = FrameAddressRegister.getBlockTypeFromAddress(spec, frameAddress);
			if (blockType != 1) {
				count++;
			}
			//if(frame.getData().toString() == "FFFFFFFF")
				//System.out.println(frame.getData());
			
		}
		System.out.println(count);
		return fpgaFrames;
	}
	
	@SuppressWarnings("unused")
	private boolean checkIfBRAMContent(int frameAddress){
		
		if ( ((frameAddress >> 23) & 0x7) == 1)  {
			return true;
		} else {
			return false;
		}
	}
	
}

package unsw.cse.rsg.ebdAnalysis;

import java.io.IOException;

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

import java.io.PrintWriter;
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
import joptsimple.internal.Column;

/**
 * This example will search the bitstream for FDRI write commands and will
 * summarize the FAR locations for each FDRI command. It supports bitstreams
 * with multiple FDRI commands.
 *
 */
public class FrameAddressExtractor {
	/**
	 * Prints information about each FDRI write command (i.e., for each FDRI command,
	 * it prints a text location of the FAR address and the # of frames).
	 */
	
	public static final int S7_TOP_BOTTOM_BIT_POS = 22;
	public static final int S7_TOP_BOTTOM_MASK = 0x1 << S7_TOP_BOTTOM_BIT_POS;
	public static final int S7_BLOCK_TYPE_BIT_POS = 23;
	public static final int S7_BLOCK_TYPE_MASK = 0x7 << S7_BLOCK_TYPE_BIT_POS;
	public static final int S7_ROW_BIT_POS = 17;
	public static final int S7_ROW_MASK = 0x1F << S7_ROW_BIT_POS;
	public static final int S7_COLUMN_BIT_POS = 7;
	public static final int S7_COLUMN_MASK = 0x3FF << S7_COLUMN_BIT_POS;
	public static final int S7_MINOR_BIT_POS = 0;
	public static final int S7_MINOR_MASK = 0x7F << S7_MINOR_BIT_POS;
	
	public static void main(String[] args) {
		String bitstreamName = "E:\\Dimitris-PC\\Development\\aes_0\\fie\\fie.runs\\impl_1\\fie.bit";
		//String bitstreamName = "E:\\Dimitris-PC\\Development\\EBD\\dfadd_0_CRC_enabled\\fie.bit";
		// Get part packets
		Bitstream bitstream = null;
		try {
			bitstream = BitstreamParser.parseBitstream(bitstreamName);
		} catch (BitstreamParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PacketList packets = bitstream.getPackets();
		int columnTemp = 0;
		int counter = 0;
		for (Packet p : packets) {				
			if (p.getPacketType() == PacketType.ONE &&
					p.getOpcode() == PacketOpcode.WRITE &&
					p.getRegType() == RegisterType.FAR) {
				
				int far = p.getData().get(0);
				int blockType = (far >> 23) & 0x7;
				int top = (far >> 22) & 0x1;
				int row = (far >> 17) & 0x1F;
				int column = (far >> 7) & 0x3FF;
				int minor = far & 0x7F;
				if(top == 1 && row == 2 && blockType == 0) {
				//if(blockType != 1) {
					if(columnTemp != column) {
						columnTemp++;
						//System.out.println("BlockType: " + blockType + " #frames: "+ counter);
					}
					System.out.println("BlockType: " + blockType);
					System.out.println("top: " + top);
					System.out.println("row: " + row);
					System.out.println("column: " + column);
					System.out.println("minor: " + (minor +1));
					System.out.println("==================");
					counter++;			
				}			
			}
		}
				
		System.out.println(counter);

		
		/*String address = null;
		for (Integer ad : farAddresses) {
			address = Integer.toHexString(ad);
			//System.out.println(address);
		}*/
		//System.out.println("# of frames = " + farAddresses.size()); 
		//System.out.println("# of frames = " + ( (61104192 - (farAddresses.size() * 3232) )  / 3232 ));
	}
	
	private static boolean checkIfBRAMContent(int frameAddress){
		
		if ( ((frameAddress >> 23) & 0x7) == 1)  {
			return true;
		} else {
			return false;
		}
	}
	
//	private static void printIntermediateFAR(XilinxConfigurationSpecification spec,
//			int farAddress, int frames) {
//		FrameAddressRegister far = new FrameAddressRegister(spec,farAddress);
//		for (int i = 1; i < frames; i++) {
//			System.out.println(far);
//			far.incrementFAR();
//		}
//	}
}


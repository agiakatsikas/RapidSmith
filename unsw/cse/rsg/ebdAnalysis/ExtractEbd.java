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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.Bitstream;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.BitstreamParseException;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.BitstreamParser;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.FPGA;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.Frame;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.FrameAddressRegister;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.FrameData;
import edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification.DeviceLookup;
import edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification.XilinxConfigurationSpecification;


public class ExtractEbd {
	
	
	// Nested class representing the column frames range
	static class FrameExtended extends Frame
	{
		int LA;
		//static ArrayList<FrameData> data;
		boolean dummy;
		public FrameExtended(int frameSize, int frameAddress)
		{	
			super(frameSize, frameAddress);
			this.LA = 0;
			this.dummy = true;
		}
		
		public FrameExtended(int frameSize)
		{	
			super(frameSize, 0);
			this.LA = 0;
			this.dummy = true;
		}
	}
	
	static protected final int[] ARTIX7_200_COLUMN_FRAMES = { 
			42,30,36,36,36,36,28,36,36,28,36,36,36,36,28,36,36,28,36,36,36,36,36,36,30,36,36,36,28,36,36,28,36,36,36,
			36,36,36,36,36,28,36,36,28,36,36,36,36,28,36,36,28,36,36,36,30,36,36,28,36,36,28,36,36,36,36,28,36,36,28,
			36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,28,36,36,28,36,36,36,36,28,36,36,28,36,36,36,36,30,
			42, 2
	};
		
	static protected final int FRAME_SIZE_BITS = 3232;
	static protected final int FRAME_SIZE_WORDS = 101;
	
	static protected final int ROWS_TOP = 2;
	static protected final int ROWS_BOTTOM = 3;
	static protected final int COLUMNS = 106; // not including the overhead column
	
	
	static protected final String artix7 = "E:/artx7Frames_2017_10_03_18_55_51.txt";
	static protected final String bitstreamPath = "E:/fie.bit";
	static protected final String ebdFilePath = "E:/fie.ebd";
	static protected final String ebcFilePath = "E:/fie.ebc";
	static protected final String readbackFilePath = "E:/readbFrame1le.bin";
	
	static ArrayList<FrameExtended> framesListEBDType1 = new ArrayList<>();
	static ArrayList<FrameExtended> framesListEBCType1 = new ArrayList<>();
	static ArrayList<String> artix7Array = readFile(artix7);
	static ArrayList<String> ebdArray = readFile(ebdFilePath);
	static ArrayList<String> ebcArray = readFile(ebcFilePath);
	static ArrayList<FrameExtended> ebcFrames = getEbdEbcFrames(ebcArray);
	static ArrayList<FrameExtended> ebdFrames = getEbdEbcFrames(ebdArray);
	
	static Bitstream bitstream;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		System.out.println("Number of ebd frames = " + (ebdFrames.size()));
		System.out.println("Number of ebc frames = " + (ebcFrames.size()));
		
		
/*		XilinxConfigurationSpecification part = DeviceLookup.lookupPartV4V5V6S7withPackageName(bitstream.getHeader().getPartName());
		File readbackFile = new File(readbackFilePath);
		ReadbackFPGA readbackFPGA = null;
		try {
			readbackFPGA = new ReadbackFPGA(readbackFile , part);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<edu.byu.ece.rapidSmith.bitstreamTools.configuration.Frame> frames = readbackFPGA.getAllFrames();
		System.out.println(frames.size());
		
		for(edu.byu.ece.rapidSmith.bitstreamTools.configuration.Frame frame : frames) {
			int farAddress = frame.getFrameAddress();
			int h = part.getTopBottomFromFAR(farAddress);
			int r = part.getRowFromFAR(farAddress);
			int c = part.getColumnFromFAR(farAddress);
			int m = part.getMinorFromFAR(farAddress);
			//System.out.println("TOP = " + h + " ROW = " + r + " COLUMN = " + c + " MINOR = " + m);
		}*/
		
		//createAddress();
		
		compareData(bitstreamPath);
	}
	
	private static void compareData(String bitstreamPath) {
		Bitstream bitstream = null;
		try {
			bitstream = BitstreamParser.parseBitstream(bitstreamPath);
		} catch (BitstreamParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XilinxConfigurationSpecification spec = DeviceLookup.lookupPartV4V5V6S7withPackageName(bitstream.getHeader().getPartName());
		FPGA fpga = new FPGA(spec);
		fpga.configureBitstream(bitstream);
		
		FrameAddressRegister far = new FrameAddressRegister(spec);
		int index = 1; // skip the first frame
		int sensitiveBitsType1 = 0;
		int sensitiveBitsType234 = 0;
		int sensitiveBitsAtColumns25 = 0;
		for(int h = 0; h < 2; h++) {
			int rows = 0;
			if(h == 0){
				rows = ROWS_TOP;
			} else {
				rows = ROWS_BOTTOM;
			}
			for(int r = 0; r < rows; r++) {
				for(int c = 0; c < ARTIX7_200_COLUMN_FRAMES.length; c++){
					int m = 0;
					if( (c == 25)) {
						sensitiveBitsAtColumns25 += ebdFrames.get(index++).getData().countBitsSet();
						sensitiveBitsAtColumns25 += ebdFrames.get(index++).getData().countBitsSet();
						sensitiveBitsAtColumns25 += ebdFrames.get(index++).getData().countBitsSet();
						sensitiveBitsAtColumns25 += ebdFrames.get(index++).getData().countBitsSet();
						sensitiveBitsAtColumns25 += ebdFrames.get(index++).getData().countBitsSet();
						sensitiveBitsAtColumns25 += ebdFrames.get(index++).getData().countBitsSet();
					}
					for(; m < ARTIX7_200_COLUMN_FRAMES[c]; m++) {	
						int farAddress =  (0 << 23) | (h << 22) | (r << 17) | (c << 7) | m;
						far.setFAR(farAddress);
						FrameData ebcFrameData = ebcFrames.get(index).getData();
						
						FrameData ebdFrameData = ebdFrames.get(index).getData();
						
						sensitiveBitsType1 += ebdFrameData.countBitsSet();
						
						FrameData bitstreamData = fpga.getFrame(far).getData();
						if ( bitstreamData.isEqual(ebcFrameData) ) {
							//System.out.println("BINGO");
						} else {
							/*System.out.println("TOP = " + h + " ROW = " + r + " COLUMN = " + c + " MINOR = " + m);
							System.out.println("TOP = " + far.getTopBottom() + " ROW = " + far.getRow() + " COLUMN = " + far.getColumn() + " MINOR = " + far.getMinor());
							System.out.println("MISMATCH!!!!");*/
							//if ( (ebcFrameData.isEmpty() == false) && (bitstreamData.isEmpty() == false) ) {
								System.out.println("not empty");
								System.out.println("TOP = " + far.getTopBottom() + " ROW = " + far.getRow() + " COLUMN = " + far.getColumn() + " MINOR = " + far.getMinor());
								System.out.println("EBC current index = " + index);
								System.out.println("Correct index = " + findIndexEbc(bitstreamData));
								//counter++;
						//}
						}
						index++;
					}
					//counter += m;
				}
			}
		}
		
		System.out.println("Sensitive bits at columns 25 (we skip these for the moment) " + sensitiveBitsAtColumns25);
		
		System.out.println("Type 1 sensitive bits = " + sensitiveBitsType1);
		
		sensitiveBitsType234 = countBitsEbd(index-1, ebdFrames.size() );

		System.out.println("Type 2, 3 and 4 sensitive bits = " + sensitiveBitsType234 );
		
		System.out.println("All sensitive bits = " + (sensitiveBitsType1 + sensitiveBitsType234));
		
		System.out.println("Difference from the actual  = " + (6787478 - ( sensitiveBitsType1 + sensitiveBitsType234 + sensitiveBitsAtColumns25) )  );
	} 
	
	
	private static int findIndexEbc(FrameData frameDataBitstream) {
		for(int i = 0; i < ebcFrames.size(); i++) {
			FrameData frameDataEbc = ebcFrames.get(i).getData();
			if(frameDataBitstream.isEqual(frameDataEbc)) {
				return i;
			}
		}
		return -1;
	}
	
	private static int countBitsEbd(int startIndex, int stopIndex) {
		int count = 0;
		for(int i = startIndex; i < stopIndex; i++) {
			FrameData frameDataEbd = ebdFrames.get(i).getData();
			count += frameDataEbd.countBitsSet();
		}
		return count;
	}
	
	private static ArrayList<FrameExtended> getEbdEbcFrames(ArrayList<String> ebdEbcArray){
		//delete header in ebdArray
		ebdEbcArray.subList(0, 8).clear();
		ArrayList<FrameExtended> frames = new ArrayList<>();
		int numberOfFrames = (ebdEbcArray.size() * 32) / FRAME_SIZE_BITS;
		for(int la = 0; la < numberOfFrames; la++) {
			FrameExtended frame = new FrameExtended(FRAME_SIZE_WORDS);
			frame.LA = la;
			String data = "";
			for (int i = 0 + (la * FRAME_SIZE_WORDS); i < FRAME_SIZE_WORDS + (la * FRAME_SIZE_WORDS); i++) {
				data += ebdEbcArray.get(i);
			}
			FrameData frameData = getFrameData(data);
			frame.configure(frameData);
			frames.add(frame);
		}				
		return frames;
	}
	
	private static FrameData getFrameData(String frameBitsASCI)
    {
        List<Byte> framesData = new ArrayList<Byte>();
        byte[] frameBits = frameBitsASCI.getBytes();
        byte b;
        int bitIndex;
        int byteIndex = 0;
        int val;
    	for (byteIndex = 0; byteIndex < frameBits.length; byteIndex += 8)
        {
            b = 0;
            for (bitIndex = 0; bitIndex < 8; bitIndex++)
            {
                val = (frameBits[byteIndex + bitIndex] - 0x30) << (7 - bitIndex);
                b   |= (byte)(val);
            }
            framesData.add((Byte)b);
        }
    	FrameData f = new FrameData(framesData);
        return f;
    }
	

	private static ArrayList<String> readFile(String path){
		ArrayList<String> lines = new ArrayList<>();
		String line = "";
		File f = new File(path);
		BufferedReader b;
		try {

            b = new BufferedReader(new FileReader(f));
            while ((line = b.readLine()) != null) {               
            	lines.add(line.replace("\n", "").replace("\r", ""));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }	
		return lines;
	}
	
	
	/*
		H RRRRR CCCCCCCCCC MMMMMMM
		H = half address (1-bit)
		RRRRR = row address (5-bit)
		CCCCCCCCCC = column address (10-bit)
		MMMMMMM = minor address (7-bit)
	*/
	private static void createAddress() {	
		int index = 0;
		int counter = 0;
		for(int h = 0; h < 2; h++) {
			int rows = 0;
			if(h == 0){
				rows = ROWS_TOP;
			} else {
				rows = ROWS_BOTTOM;
			}
			for(int r = 0; r < rows; r++) {
				for(int c = 0; c < ARTIX7_200_COLUMN_FRAMES.length; c++){
					int m = 0;
					for(; m < ARTIX7_200_COLUMN_FRAMES[c]; m++) {
						//System.out.println("TOP = " + h + " ROW = " + r + " COLUMN = " + c + " MINOR = " + m);
						framesListEBDType1.add(ebdFrames.get(index++));
						framesListEBCType1.add(ebcFrames.get(index));
					}
					counter += m;
				}
			}
		}
		
		
		for(int h = 0; h < 2; h++) {
			int rows = 0;
			if(h == 0){
				rows = ROWS_TOP;
			} else {
				rows = ROWS_BOTTOM;
			}
			for(int r = 0; r < rows; r++) {
				for(int c = 0; c < ARTIX7_200_COLUMN_FRAMES.length - 1; c++){
					counter++;
				}
			}
		}
		
		System.out.println("Number of frames synthesized = " + (counter));
		
		counter = 0;
		for (FrameExtended frame : framesListEBDType1) {
			counter += frame.getData().countBitsSet();
		}
		System.out.println("Number of essentialBits = " + (counter));
	}
	
	
	
}
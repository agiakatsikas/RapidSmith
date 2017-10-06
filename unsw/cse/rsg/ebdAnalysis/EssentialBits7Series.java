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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.Bitstream;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.BitstreamParseException;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.BitstreamParser;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.FPGA;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.Frame;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.FrameAddressRegister;
import edu.byu.ece.rapidSmith.bitstreamTools.configuration.FrameData;
import edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification.BlockSubType;
import edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification.DeviceLookup;
import edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification.S7MaskConfigurationSpecification;
import edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification.XilinxConfigurationSpecification;

public class EssentialBits7Series {
	
	    // Nested class
		static class FrameExtended extends Frame
		{
			private int LA;
			
			public FrameExtended(int frameSize, int frameAddress)
			{	
				super(frameSize, frameAddress);
				this.setLA(0);
			}
			
			public FrameExtended(int frameSize)
			{	
				super(frameSize, 0);
				this.setLA(0);
			}
			
			public void setFrameAddress(int frameAddress) {
				super.frameAddress = frameAddress;
			}

			public int getLA() {
				return LA;
			}

			public void setLA(int lA) {
				LA = lA;
			}
		}
		
		private String bitstreamPath = "";
		private String ebdFilePath = "";
		private String ebcFilePath = "";
		private XilinxConfigurationSpecification spec;
		private FPGA fpga;
		private int FRAME_SIZE_BITS, FRAME_SIZE_WORDS, ROWS_TOP, ROWS_BOTTOM = 0;
		private List<BlockSubType> COLUMN_FRAMES = null;
		
		private int sensitiveBitsType1 = 0; // type1 sensitive bits
		private int sensitiveBitsType234 = 0; // type2, 3, 4 sensitive bits	
		
		ArrayList<FrameExtended> ebcFrames;
		ArrayList<FrameExtended> ebdFrames;		
		
		
		
		
		public EssentialBits7Series(String bitstreamPath, String ebdFilePath, String ebcFilePath) {
			this.bitstreamPath = bitstreamPath;
			this.ebcFilePath = ebcFilePath;
			this.ebdFilePath = ebdFilePath;
			this.configureFPGA();
			this.analyzeData();
		}
		
		
		private void configureFPGA(){
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
			spec = DeviceLookup.lookupPartV4V5V6S7withPackageName(bitstream.getHeader().getPartName());
			fpga = new FPGA(spec);
			fpga.configureBitstream(bitstream);
			FRAME_SIZE_BITS  = spec.getFrameSize() * 32;
			FRAME_SIZE_WORDS = spec.getFrameSize();
			ROWS_TOP = spec.getTopNumberOfRows();
			ROWS_BOTTOM = spec.getBottomNumberOfRows();
			COLUMN_FRAMES = spec.getOverallColumnLayout();
		}
		
		
		private ArrayList<FrameExtended> getEbdEbcFrames(ArrayList<String> ebdEbcArray){
			//delete header in ebdArray
			ebdEbcArray.subList(0, 8).clear();
			ArrayList<FrameExtended> frames = new ArrayList<>();
			int numberOfFrames = (ebdEbcArray.size() * 32) / FRAME_SIZE_BITS;
			for(int la = 0; la < numberOfFrames; la++) {
				FrameExtended frame = new FrameExtended(FRAME_SIZE_WORDS);
				frame.setLA(la);
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
		
		
		private FrameData getFrameData(String frameBitsASCI)
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
		
		
		private ArrayList<String> readFile(String path){
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
	
		
		// 
		private void analyzeData() {
			ArrayList<String> ebdArray = readFile(ebdFilePath);
			ArrayList<String> ebcArray = readFile(ebcFilePath);
			ebcFrames = getEbdEbcFrames(ebcArray);
			ebdFrames = getEbdEbcFrames(ebdArray);

			FrameData ebdFrameData = null;
			FrameData ebcFrameData  = null;
			FrameData bitstreamData = null;
			FrameAddressRegister far = new FrameAddressRegister(spec);
			
			int index = 1; // skip the first frame --> DUMMY
						
			for(int h = 0; h < 2; h++) {
				int rows = 0;
				if(h == 0){
					rows = ROWS_TOP;
				} else {
					rows = ROWS_BOTTOM;
				}
				for(int r = 0; r < rows; r++) {
					for(int c = 0; c < COLUMN_FRAMES.size(); c++){
						int m = 0;
						for(; m < COLUMN_FRAMES.get(c).getFramesPerConfigurationBlock(); m++) {	
							
							far.setFAR(getFrameAddress(0, h, r, c, m));
							bitstreamData = fpga.getFrame(far).getData();
							ebcFrameData = ebcFrames.get(index).getData();
							
							if ( bitstreamData.isEqual(ebcFrameData) ) {
								ebdFrameData = ebdFrames.get(index).getData();			
								
								ebcFrames.get(index).setLA(index);
								ebdFrames.get(index).setLA(index);							
								ebcFrames.get(index).setFrameAddress(getFrameAddress(0, h, r, c, m));
								ebdFrames.get(index).setFrameAddress(getFrameAddress(0, h, r, c, m));
								
								sensitiveBitsType1 += ebdFrameData.countBitsSet();
								index++;
							} else {
								System.out.println("EBC frame != BitstreamFrame. Probably it is a DUMMY or Type 2, 3, 4 frame");
								System.out.println("TOP = " + far.getTopBottom() + " ROW = " + far.getRow() + " COLUMN = " + far.getColumn() + " MINOR = " + far.getMinor());
								System.out.println("EBC current index = " + index);
								int indexNew = findIndexEbc(bitstreamData);
								sensitiveBitsType234 += countBitsEbd(index, indexNew);
								System.out.println("Adjusting ebc and ebd index = " + indexNew);
								index = indexNew;
								ebcFrameData = ebcFrames.get(index).getData();
								if ( bitstreamData.isEqual(ebcFrameData) ) {
									ebdFrameData = ebdFrames.get(index).getData();

									ebcFrames.get(index).setLA(index);
									ebdFrames.get(index).setLA(index);							
									ebcFrames.get(index).setFrameAddress(getFrameAddress(0, h, r, c, m));
									ebdFrames.get(index).setFrameAddress(getFrameAddress(0, h, r, c, m));
									
									sensitiveBitsType1 += ebdFrameData.countBitsSet();
									index++;
								} else {
									System.out.println("Error: Can't find the correct index in the ebc file");
									System.exit(0);
								}
							}
						}
					}
				}
			}		
			
			sensitiveBitsType234 += countBitsEbd(index-1, ebdFrames.size() );
		}
		
		// Create a map of the sensitive frames. Key = frame address, Value = List with sensitive bit positions in the frame.  
		public HashMap<Integer, ArrayList<Integer>> getSenitiveFrames() {
			HashMap<Integer, ArrayList<Integer>> sensitiveFrames = new HashMap<>();
			
			for(Frame frame : ebdFrames){
				FrameData frameData = frame.getData();
				ArrayList<Integer> sensitiveBits = new ArrayList<>();
				for(int i = 0; i < frameData.size(); i++) {
					if( frameData.getBit(i) == 1) {
						sensitiveBits.add(i);
					}	
				}
				sensitiveFrames.put(frame.getFrameAddress(), sensitiveBits);
			}
			return sensitiveFrames;
		}
		
		
		public void printResults() {
			System.out.println("Type 1 sensitive bits = " + sensitiveBitsType1);	

			System.out.println("Type 2, 3 and 4 sensitive bits = " + sensitiveBitsType234 );
			
			System.out.println("All sensitive bits = " + (sensitiveBitsType1 + sensitiveBitsType234));
			
			System.out.println("Difference from the actual  = " + (6787478 - ( sensitiveBitsType1 + sensitiveBitsType234) )  );	
		}
		
		// 7 series FAR mask
		private int getFrameAddress(int blockType, int topBottom, int row, int column, int minor){
			int frameAddress = 
			  (blockType << S7MaskConfigurationSpecification.S7_BLOCK_TYPE_BIT_POS) 
			| (topBottom << S7MaskConfigurationSpecification.S7_TOP_BOTTOM_BIT_POS) 
			| (row << S7MaskConfigurationSpecification.S7_ROW_BIT_POS) 
			| (column << S7MaskConfigurationSpecification.S7_COLUMN_BIT_POS) 
			| (minor << S7MaskConfigurationSpecification.S7_MINOR_BIT_POS) ;
			
			return frameAddress;
		}
		
		
		//Find the given FrameData in the ebc file. Return the index of the ebc data or -1 when not found  
		private  int findIndexEbc(FrameData frameDataBitstream) {
			for(int i = 0; i < ebcFrames.size(); i++) {
				FrameData frameDataEbc = ebcFrames.get(i).getData();
				if(frameDataBitstream.isEqual(frameDataEbc)) {
					return i;
				}
			}
			return -1;
		}
		
		//Count the number of essential bits in the following range of the arraylist 
		private int countBitsEbd(int startIndex, int stopIndex) {
			int count = 0;
			for(int i = startIndex; i < stopIndex; i++) {
				FrameData frameDataEbd = ebdFrames.get(i).getData();
				count += frameDataEbd.countBitsSet();
			}
			return count;
		}

}

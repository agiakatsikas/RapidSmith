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
package unsw.cse.rsg.tests;

import java.util.ArrayList;

import unsw.cse.rsg.ebdAnalysis.EssentialBits7Series;
import unsw.cse.rsg.frameGenerator.FrameGenerator;

public class PblockEssentialBits {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String bitstreamPath = "E:\\fmerImplementation\\PCC\\pcc_demo\\vivado\\checkpoints\\system_top_1RR_v_32.bit";
		String ebdFilePath = "E:\\fmerImplementation\\PCC\\pcc_demo\\vivado\\checkpoints\\system_top_1RR_v_32.ebd";
		String ebcFilePath = "E:\\fmerImplementation\\PCC\\pcc_demo\\vivado\\checkpoints\\system_top_1RR_v_32.ebc";
		String xdcFilePath = "E:\\fmerImplementation\\PCC\\pcc_demo\\vivado\\xdc_pblock_pcc_17c.xdc";
		EssentialBits7Series ebitAnalysis = new EssentialBits7Series(bitstreamPath, ebdFilePath, ebcFilePath);	
		ebitAnalysis.printResults();
		System.out.println();
		FrameGenerator frameGenerator = new FrameGenerator(xdcFilePath, "fip_inst");
		ArrayList<Integer> fip_inst = frameGenerator.getFrames();
		int frames = fip_inst.size();
		int eframes =  ebitAnalysis.countEssentialFrames(fip_inst);
		int ebits = ebitAnalysis.countEbdBits(fip_inst);
		System.out.println("MB: number of frames = "  + frames);
		System.out.println("MB: number of essential frames = "  + eframes);
		System.out.println("MB: number of ebits = "  + ebits);
		System.out.println();
	}

}

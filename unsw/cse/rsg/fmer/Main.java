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
package unsw.cse.rsg.fmer;

import java.util.ArrayList;

import unsw.cse.rsg.ebdAnalysis.EssentialBits7Series;
import unsw.cse.rsg.frameGenerator.FrameGenerator;

public class Main {
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String bitstreamPath = "E:\\fmerImplementation\\motion_1_FL_new\\fie\\fie.runs\\impl_1\\fie.bit";
		String ebdFilePath = "E:\\fmerImplementation\\motion_1_FL_new\\fie\\fie.runs\\impl_1\\fie.ebd";
		String ebcFilePath = "E:\\fmerImplementation\\motion_1_FL_new\\fie\\fie.runs\\impl_1\\fie.ebc";
		
		EssentialBits7Series ebitAnalysis = new EssentialBits7Series(bitstreamPath, ebdFilePath, ebcFilePath);	
		ebitAnalysis.printResults();
		
//		int slice_x_l = 0; 
//		int slice_y_b = 111;
//		int slice_x_r = 47; 
//		int slice_y_t = 144;
//		
//		int dsp_x_l = 0;
//		int dsp_y_b = 46;
//		int dsp_x_r = 2;
//		int dsp_y_t = 57;
//		
//		int bram_x_l = 0;
//		int bram_y_b = 46;
//		int bram_x_r = 2;
//		int bram_y_t = 57;
		
		
		FrameGenerator frameGenerator = new FrameGenerator();
		
		System.out.println();
		ArrayList<Integer> fip_inst = frameGenerator.getFrames(0, 111, 47, 144, 0, 46, 2, 57, 0, 46, 2, 57);
		System.out.println("MB: number of frames = "  + fip_inst.size());
		System.out.println("MB: number of ebits = "  + ebitAnalysis.countEbdBits(fip_inst));
		System.out.println();
		
		ArrayList<Integer> main_inst_r0 = frameGenerator.getFrames(56,150,163,199,3,60,8,79,3,60,8,79);
		System.out.println("main_inst_r0: number of frames = "  + main_inst_r0.size());
		System.out.println("main_inst_r0: number of ebits = "  + ebitAnalysis.countEbdBits(main_inst_r0));
		System.out.println();
		
		ArrayList<Integer> main_inst_r1 = frameGenerator.getFrames(54,50,163,99,3,20,8,39,3,20,8,39);
		System.out.println("main_inst_r1: number of frames = "  + main_inst_r1.size());
		System.out.println("main_inst_r1: number of ebits = "  + ebitAnalysis.countEbdBits(main_inst_r1));
		System.out.println();
		
		ArrayList<Integer> main_inst_r2 = frameGenerator.getFrames(56,100,163,149,3,40,8,59,3,40,8,59);
		System.out.println("main_inst_r2: number of frames = "  + main_inst_r2.size());
		System.out.println("main_inst_r2: number of ebits = "  + ebitAnalysis.countEbdBits(main_inst_r2));
		System.out.println();
		
		ArrayList<Integer> staticFrames = ebitAnalysis.getFpgaFrameAddresses();	
		staticFrames.removeAll(fip_inst);
		staticFrames.removeAll(main_inst_r0);
		staticFrames.removeAll(main_inst_r1);
		staticFrames.removeAll(main_inst_r2);
		
		System.out.println("staticFrames: number of frames = "  + staticFrames.size());
		System.out.println("staticFrames: number of ebits = "  + ebitAnalysis.countEbdBits(staticFrames));
		System.out.println();
		
		

	}
	

}

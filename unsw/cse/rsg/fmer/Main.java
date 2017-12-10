package unsw.cse.rsg.fmer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


import unsw.cse.rsg.ebdAnalysis.EssentialBits7Series;
import unsw.cse.rsg.frameGenerator.*;

public class Main {	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String project = "sha_1";
		String path = "E:\\fmerImplementation\\FL\\" + project;
		String bitstreamPath = path + "\\fie\\fie.runs\\impl_1\\fie.bit";
		String ebdFilePath = path + "\\fie\\fie.runs\\impl_1\\fie.ebd";
		String ebcFilePath = path + "\\fie\\fie.runs\\impl_1\\fie.ebc";
		
		//String bitstreamPath = "E:\\fmerImplementation\\PCC\\pcc_demo\\vivado\\checkpoints\\system_top_1RR_v_32.bit";
		//String ebdFilePath   = "E:\\fmerImplementation\\PCC\\pcc_demo\\vivado\\checkpoints\\system_top_1RR_v_32.ebd";
		//String ebcFilePath   = "E:\\fmerImplementation\\PCC\\pcc_demo\\vivado\\checkpoints\\system_top_1RR_v_32.bit";
		
		String xdcFilePath = path + "\\fie\\fie.srcs\\constrs_1\\imports\\constraints\\fie.xdc";
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(path + "\\analysis_" + project + ".csv", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		EssentialBits7Series ebitAnalysis = new EssentialBits7Series(bitstreamPath, ebdFilePath, ebcFilePath);	
		ebitAnalysis.printResults();
		
		ArrayList<Integer> cgfFrames = ebitAnalysis.getCfgFrameAddresses();
		ArrayList<Integer> iobFrames = ebitAnalysis.getIobFrameAddresses();
		ArrayList<Integer> clkFrames = ebitAnalysis.getClkFrameAddresses();
				
		int cgfEbits = ebitAnalysis.countEbdBits(cgfFrames);
		int iobEbits = ebitAnalysis.countEbdBits(iobFrames);
		int clkEbits = ebitAnalysis.countEbdBits(clkFrames);
		int cfgIobClkEbits = cgfEbits + iobEbits + clkEbits;
	
	    int total = 0;
		writer.println(
				"rc:frames, rc:eframes, rc:ebits, A:frames, A:eframes, A:ebits, B:frames, B:eframes, B:ebits, C:frames, C:eframes, C:ebits, SS:frames, SS:eframes, SS:ebits, SS:ebits_cfg, SS:ebits_iob, SS:ebits_clk");
		
		System.out.println();
		FrameGenerator frameGenerator = new FrameGenerator(xdcFilePath, "fip_inst");
		ArrayList<Integer> fip_inst = frameGenerator.getFrames();
		fip_inst.removeAll(cgfFrames);
		fip_inst.removeAll(iobFrames);
		fip_inst.removeAll(clkFrames);
		int frames = fip_inst.size();
		int eframes =  ebitAnalysis.countEssentialFrames(fip_inst);
		int ebits = ebitAnalysis.countEbdBits(fip_inst);
		total += ebits;
		System.out.println("RC: number of frames = "  + frames);
		System.out.println("RC: number of essential frames = "  + eframes);
		System.out.println("RC: number of ebits = "  + ebits);
		System.out.println();
		writer.print(frames + ", " + eframes + ", " + ebits + ", ");
		
		
		frameGenerator = new FrameGenerator(xdcFilePath, "main_inst_r0");
		ArrayList<Integer> main_inst_r0 = frameGenerator.getFrames();
		// Probably I should not delete the cfg, iob and clk frames from the TMR modules. I should check again some time!
		main_inst_r0.removeAll(cgfFrames);
		main_inst_r0.removeAll(iobFrames);
		main_inst_r0.removeAll(clkFrames);
		frames = main_inst_r0.size();                                  
		eframes =  ebitAnalysis.countEssentialFrames(main_inst_r0);    
		ebits = ebitAnalysis.countEbdBits(main_inst_r0);   
		total += ebits;
		System.out.println("main_inst_r0: number of frames = "  + frames);
		System.out.println("main_inst_r0: number of essential frames = "  + eframes);
		System.out.println("main_inst_r0: number of ebits = "  + ebits);
		System.out.println();
		writer.print(frames + ", " + eframes + ", " + ebits + ", ");
		
		frameGenerator = new FrameGenerator(xdcFilePath, "main_inst_r1");
		ArrayList<Integer> main_inst_r1 = frameGenerator.getFrames();
		main_inst_r1.removeAll(cgfFrames);
		main_inst_r1.removeAll(iobFrames);
		main_inst_r1.removeAll(clkFrames);
		frames = main_inst_r1.size();                                  
		eframes =  ebitAnalysis.countEssentialFrames(main_inst_r1);    
		ebits = ebitAnalysis.countEbdBits(main_inst_r1);
		total += ebits;
		System.out.println("main_inst_r1: number of frames = "  + frames);
		System.out.println("main_inst_r1: number of essential frames = "  + eframes);
		System.out.println("main_inst_r1: number of ebits = "  + ebits);
		System.out.println();
		writer.print(frames + ", " + eframes + ", " + ebits + ", ");
		
		frameGenerator = new FrameGenerator(xdcFilePath, "main_inst_r2");
		ArrayList<Integer> main_inst_r2 = frameGenerator.getFrames();
		main_inst_r2.removeAll(cgfFrames);
		main_inst_r2.removeAll(iobFrames);
		main_inst_r2.removeAll(clkFrames);
		frames = main_inst_r2.size();                                  
		eframes =  ebitAnalysis.countEssentialFrames(main_inst_r2);    
		ebits = ebitAnalysis.countEbdBits(main_inst_r2);
		total += ebits;
		System.out.println("main_inst_r2: number of frames = "  + frames);
		System.out.println("main_inst_r2: number of essential frames = "  + eframes);
		System.out.println("main_inst_r2: number of ebits = "  + ebits);
		System.out.println();
		writer.print(frames + ", " + eframes + ", " + ebits + ", ");
		
		ArrayList<Integer> staticFrames = ebitAnalysis.getFpgaFrameAddresses();	
		staticFrames.removeAll(fip_inst);
		staticFrames.removeAll(main_inst_r0);
		staticFrames.removeAll(main_inst_r1);
		staticFrames.removeAll(main_inst_r2);
		frames = staticFrames.size();                                  
		eframes =  ebitAnalysis.countEssentialFrames(staticFrames);    
		ebits = ebitAnalysis.countEbdBits(staticFrames) + ebitAnalysis.getSensitiveBitsType234();
		total += ebits;
		System.out.println("All Support Resources: number of frames = "  + frames);
		System.out.println("All Support Resources: number of essential frames = "  + eframes);
		System.out.println("All Support Resources: number of ebits = "  + ebits);
		System.out.println();
		writer.print(frames + ", " + eframes + ", " + ebits + ", ");		
		
		
		System.out.println("Only the cgf (i.e. ICAP) in the Support Resources: number of frames = "  + cgfFrames.size());
		System.out.println("Only the iob (i.e. IO) in the  Support Resources: number of frames = "  + iobFrames.size());
		System.out.println("Only the clk (i.e. MMCM) in the  Support Resources: number of frames = "  + clkFrames.size());
		
		System.out.println("Only the cgf (i.e. ICAP) in the Support Resources: number of ebits = "  + cgfEbits);
		System.out.println("Only the iob (i.e. IO) in the  Support Resources: number of ebits = "  + iobEbits);
		System.out.println("Only the clk (i.e. MMCM) in the  Support Resources: number of ebits = "  + clkEbits);
		System.out.println("Only the config, iob and clk primitive in the Support Resources: number of ebits = "  + cfgIobClkEbits);
		System.out.println();
		writer.print(cgfEbits + ", " + iobEbits + ", " + clkEbits);

		writer.close();
		
		System.out.println("Total ebits validation = " + total);
		

	}
	
	
	
	

}

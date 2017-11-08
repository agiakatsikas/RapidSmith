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
		String path = "E:\\fmerImplementation\\FL\\sha_1";
		String bitstreamPath = path + "\\fie\\fie.runs\\impl_1\\fie.bit";
		String ebdFilePath = path + "\\fie\\fie.runs\\impl_1\\fie.ebd";
		String ebcFilePath = path + "\\fie\\fie.runs\\impl_1\\fie.ebc";
		String xdcFilePath = path + "\\fie\\fie.srcs\\constrs_1\\imports\\constraints\\fie.xdc";
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(path + "\\analysis.csv", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		EssentialBits7Series ebitAnalysis = new EssentialBits7Series(bitstreamPath, ebdFilePath, ebcFilePath);	
		ebitAnalysis.printResults();
	
		writer.println(
				"rc:frames, rc:eframes, rc:ebits, A:frames, A:eframes, A:ebits, B:frames, B:eframes, B:ebits, C:frames, C:eframes, C:ebits, SS:frames, SS:eframes, SS:ebits");
		
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
		writer.print(frames + ", " + eframes + ", " + ebits + ", ");
		
		
		frameGenerator = new FrameGenerator(xdcFilePath, "main_inst_r0");
		ArrayList<Integer> main_inst_r0 = frameGenerator.getFrames();
		frames = main_inst_r0.size();                                  
		eframes =  ebitAnalysis.countEssentialFrames(main_inst_r0);    
		ebits = ebitAnalysis.countEbdBits(main_inst_r0);               
		System.out.println("main_inst_r0: number of frames = "  + frames);
		System.out.println("main_inst_r0: number of essential frames = "  + eframes);
		System.out.println("main_inst_r0: number of ebits = "  + ebits);
		System.out.println();
		writer.print(frames + ", " + eframes + ", " + ebits + ", ");
		
		frameGenerator = new FrameGenerator(xdcFilePath, "main_inst_r1");
		ArrayList<Integer> main_inst_r1 = frameGenerator.getFrames();
		frames = main_inst_r1.size();                                  
		eframes =  ebitAnalysis.countEssentialFrames(main_inst_r1);    
		ebits = ebitAnalysis.countEbdBits(main_inst_r1);               
		System.out.println("main_inst_r0: number of frames = "  + frames);
		System.out.println("main_inst_r0: number of essential frames = "  + eframes);
		System.out.println("main_inst_r0: number of ebits = "  + ebits);
		System.out.println();
		writer.print(frames + ", " + eframes + ", " + ebits + ", ");
		
		frameGenerator = new FrameGenerator(xdcFilePath, "main_inst_r2");
		ArrayList<Integer> main_inst_r2 = frameGenerator.getFrames();
		frames = main_inst_r2.size();                                  
		eframes =  ebitAnalysis.countEssentialFrames(main_inst_r2);    
		ebits = ebitAnalysis.countEbdBits(main_inst_r2);               
		System.out.println("main_inst_r0: number of frames = "  + frames);
		System.out.println("main_inst_r0: number of essential frames = "  + eframes);
		System.out.println("main_inst_r0: number of ebits = "  + ebits);
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
		System.out.println("Support Resources: number of frames = "  + frames);
		System.out.println("Support Resources: number of essential frames = "  + eframes);
		System.out.println("Support Resources: number of ebits = "  + ebits);
		System.out.println();
		writer.print(frames + ", " + eframes + ", " + ebits);
		
		writer.close();

	}
	
	
	
	

}

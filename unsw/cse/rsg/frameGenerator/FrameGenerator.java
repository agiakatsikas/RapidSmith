// Generates a list with the frame addresses for a pblock 
package unsw.cse.rsg.frameGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification.S7MaskConfigurationSpecification;

public class FrameGenerator {
	
	class Coordinates {
		int l, b, r, t = 0;
		Coordinates(){}
	}
	
	private int left, right, bottom, top = 0;
	
	protected final int[] ARTIX7_200_COLUMN_FRAMES = { 
			42,30,36,36,36,36,28,36,36,28,36,36,36,36,28,36,36,28,36,36,36,36,36,36,30,36,36,36,28,36,36,28,36,36,36,
			36,36,36,36,36,28,36,36,28,36,36,36,36,28,36,36,28,36,36,36,30,36,36,28,36,36,28,36,36,36,36,28,36,36,28,
			36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,28,36,36,28,36,36,36,36,28,36,36,28,36,36,36,36,30,
			42, 2
	};
	
	protected final int[] CLB_COL = { 
			2, 3, 4, 5, 7, 8, 10, 11, 12, 13, 15, 16, 18, 19, 20, 21, 22, 23, 25, 26, 27, 29, 30, 32, 33, 34, 35, 36, 37, 38, 39, 41, 42,
			44, 45, 46, 47, 49, 50, 52, 53, 54, 56, 57, 59, 60, 62, 63, 64, 65, 67, 68, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81,
			82, 83, 84, 85, 86, 87, 89, 90, 92, 93, 94, 95, 97, 98, 100, 101, 102, 103
	};
	
	protected final int[] DSP_COL = { 
			9, 14, 31, 43, 48, 61, 66, 91, 96
	};
	
	protected final int[] BRAM_COL = { 
			6, 17, 28, 40, 51, 58, 69, 88, 99
	};
	
	private int slice_x_l;
	private int slice_y_b;
	private int slice_x_r;
	private int slice_y_t;
	private int dsp_x_l ;
	private int dsp_y_b ;
	private int dsp_x_r ;
	private int dsp_y_t ;
	private int bram_x_l;
	private int bram_y_b;
	private int bram_x_r;
	private int bram_y_t;
	            
	
	public FrameGenerator(String xdcFilePath, String pBlockName) {
		setCoordinates(xdcFilePath, pBlockName);
	}
	
	public FrameGenerator()	{
//		for(int i = 0; i < ARTIX7_200_COLUMN_FRAMES.length; i++) {
//			System.out.println(ARTIX7_200_COLUMN_FRAMES[i]);
//		}
	}
	
	public void printCoordinates(){
		System.out.println("(" + left + ", " + bottom + "), (" + right + ", " + top + ")");
	}
	
	
	//slice, dsp or bram coordinates should be set to -1 when not exist 
	public void setCoordinates(Coordinates slices,   Coordinates dsp, Coordinates bram) {
		
		this.slice_x_l = slices.l;
		this.slice_y_b = slices.b;
		this.slice_x_r = slices.r;
		this.slice_y_t = slices.t;
		this.dsp_x_l = dsp.l;
		this.dsp_y_b = dsp.b;
		this.dsp_x_r = dsp.r;
		this.dsp_y_t = dsp.t;
		this.bram_x_l = bram.l;
		this.bram_y_b = bram.b;
		this.bram_x_r = bram.r;
		this.bram_y_t = bram.t;
	}
	
	public void setCoordinates(String xdcFilePath, String pBlockName) {
		
		Coordinates slices = getCoordinates(xdcFilePath, pBlockName, "slice");
		Coordinates dsp = getCoordinates(xdcFilePath, pBlockName, "dsp48");
		Coordinates bram = getCoordinates(xdcFilePath, pBlockName, "RAMB18");
		
		this.slice_x_l = slices.l;
		this.slice_y_b = slices.b;
		this.slice_x_r = slices.r;
		this.slice_y_t = slices.t;
		this.dsp_x_l = dsp.l;
		this.dsp_y_b = dsp.b;
		this.dsp_x_r = dsp.r;
		this.dsp_y_t = dsp.t;
		this.bram_x_l = bram.l;
		this.bram_y_b = bram.b;
		this.bram_x_r = bram.r;
		this.bram_y_t = bram.t;
	}
	
	//slice, dsp or bram coordinates should be set to -1 when not exist 
	public ArrayList<Integer> getFrames() {
		
		int clb_x_l_col = 10000;
		if (slice_x_l != -1) {
			clb_x_l_col = CLB_COL[slice_x_l/2];
		}
		
		int clb_x_r_col = -1;
		if (slice_x_r != -1) {
			clb_x_r_col =  CLB_COL[(slice_x_r-1)/2];
		}
		
		int clb_y_t_row = -1;
		if (slice_y_t != -1) {
			clb_y_t_row = (slice_y_t) / 50;
		}
		
		int clb_y_b_row = -1;
		if (slice_y_b != -1){
			clb_y_b_row =  slice_y_b / 50;
		}
		
		
		int dsp_x_l_col = 10000;
		if (dsp_x_l != -1){
			dsp_x_l_col = DSP_COL[dsp_x_l];
		}
		
		int dsp_x_r_col = -1;
		if (dsp_x_r != -1) {
			dsp_x_r_col = DSP_COL[dsp_x_r];
		}
		
		int dsp_y_t_row = -1;
		if (dsp_y_t != -1) {
			 dsp_y_t_row = (dsp_y_t) / 20;
		}
		
		int dsp_y_b_row = -1;
		if (dsp_y_b != -1) {
			dsp_y_b_row = dsp_y_b / 20;
		}

		
		int bram_x_l_col = 10000;
		if (bram_x_l != -1) {
			bram_x_l_col = BRAM_COL[bram_x_l];
		}
		
		int bram_x_r_col = -1;
		if (bram_x_r != -1) {
			bram_x_r_col = BRAM_COL[bram_x_r];
		}
		
		int bram_y_t_row = -1;
		if (bram_y_t != -1) {
			bram_y_t_row = (bram_y_t) / 20;
		}
		
		int bram_y_b_row = -1;
		if (bram_y_b != -1) {
			bram_y_b_row = bram_y_b / 20;
		}

		left = Collections.min(Arrays.asList(clb_x_l_col, dsp_x_l_col, bram_x_l_col));
		right = Collections.max(Arrays.asList(clb_x_r_col, dsp_x_r_col, bram_x_r_col));
		bottom = Collections.max(Arrays.asList(clb_y_b_row, dsp_y_b_row, bram_y_b_row));
		top = Collections.max(Arrays.asList(clb_y_t_row, dsp_y_t_row, bram_y_t_row));
		
		ArrayList<Integer> frameAddresses = new ArrayList<>();
		int h, r, c, m = 0;
		for (int y = bottom; y <= top; y++){
			h = getBottom(y);
			r = getRow(y);
			for (c = left ; c <= right; c++){
				for (m = 0; m < ARTIX7_200_COLUMN_FRAMES[c]; m++) {
					int farAddress =  getFrameAddress(0, h, r, c, m);
					frameAddresses.add(farAddress);
					//System.out.println("BOTTOM = " + h + " ROW = " + r + " COLUMN = " + c + " MINOR = " + m);
				}
			}
		}
		return frameAddresses;
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
	
/*	//Select between top-half rows (0) and bottom-half rows (1).
	private int getBottom(int y) {
		if (y >= 0 && y < 149)
			return 1;
		else
			return 0;
	}*/
	
	// return the rows of bottom and top. 
	// TopBottom = 1, row = 2 --> 0 - 49, row = 1 --> 50 - 99, row 0 --> 100 - 149
	// TopBottom = 0, row = 0 --> 150-199, row = 1 --> 200-249,  
//	private int getRow(int y) {
//		if (getBottom(y) == 1) {
//			if (y >= 0 && y < 50) {
//				return 2;
//			} else if (y >= 50 && y < 100) {
//				return 1;
//			} else {
//				return 0;
//			}
//		}
//			
//		else {
//			if (y >= 150 && y < 200) {
//				return 0;
//			} else {
//				return 1;
//			}
//		}
//	}
		
		//Select between top-half rows (0) and bottom-half rows (1).
		private int getBottom(int y) {
			if (y >= 0 && y <= 2)
				return 1;
			else
				return 0;
		}
	     
		// TopBottom = 1, row = 2 --> 0 - 49, row = 1 --> 50 - 99, row 0 --> 100 - 149
		// TopBottom = 0, row = 0 --> 150-199, row = 1 --> 200-249,  
		private int getRow(int y) {
			if (getBottom(y) == 1) {
				if (y == 0) {
					return 2;
				} else if (y == 1) {
					return 1;
				} else {
					return 0;
				}
			}			
			else {
				if (y == 3) {
					return 0;
				} else {
					return 1;
				}
			}
		}
		
		public Coordinates getCoordinates(String xdcFilePath, String pBlockName, String search){
			ArrayList<String> lines = readFile(xdcFilePath);
			String regex = "("+ search.toUpperCase() + "_X)(\\d{1,})(Y)(\\d{1,})(:" + search.toUpperCase() +"_X)(\\d{1,})(Y)(\\d{1,})";
			Coordinates coordinates = new Coordinates();
			Pattern checkRegex = null;
			Matcher regexMatcher = null;
			for (String line : lines) {
				if(line.contains(pBlockName) && line.contains(search.toUpperCase())){
					//System.out.println(line);
					
					checkRegex = Pattern.compile(regex);
					regexMatcher = checkRegex.matcher(line);
					while(regexMatcher.find()) {
						if(regexMatcher.group().length() != 0) {
							coordinates.l = Integer.valueOf(regexMatcher.group(2).trim());
							coordinates.b = Integer.valueOf(regexMatcher.group(4).trim());
							coordinates.r = Integer.valueOf(regexMatcher.group(6).trim());
							coordinates.t = Integer.valueOf(regexMatcher.group(8).trim());
						} else {
							coordinates.l = -1;	
							coordinates.b = -1;
							coordinates.r = -1;		
							coordinates.t = -1;
						}
					}
				}
			};
/*			System.out.println(pBlockName);
			System.out.println(search);
			System.out.println(coordinates.l);
			System.out.println(coordinates.b);
			System.out.println(coordinates.r);
			System.out.println(coordinates.t);*/
			
			return coordinates;
			
		}
		
		
		/**
		 * @param  the path of a file
		 * @return An array with strings. Each entry of the array is one line of the 
		 * file without the newline character            
		 */
		private ArrayList<String> readFile(String path){
			ArrayList<String> lines = new ArrayList<>();
			String line = "";
			File f = new File(path);
			BufferedReader b;
			try {

	            b = new BufferedReader(new FileReader(f));
	            while ((line = b.readLine()) != null) {               
	            	//lines.add(line.replace("\n", "").replace("\r", ""));
	            	lines.add(line);
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        }	
			return lines;
		}
	
}

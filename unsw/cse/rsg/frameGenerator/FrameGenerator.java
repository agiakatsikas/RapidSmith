// Generates a list with the frame addresses for a pblock 
package unsw.cse.rsg.frameGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FrameGenerator {

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
	
	
	//slice, dsp or bram coordinates should be set to -1 when not exist 
	public FrameGenerator(
			int slice_x_l, int slice_y_b, int slice_x_r, int slice_y_t,
			int dsp_x_l, int dsp_y_b, int dsp_x_r, int dsp_y_t,
			int bram_x_l, int bram_y_b, int bram_x_r, int bram_y_t) 
	{
		
		int clb_x_l_col = 10000;
		if (slice_x_l != -1) {
			clb_x_l_col = CLB_COL[slice_x_l];
		}
		
		int clb_x_r_col = -1;
		if (slice_x_r != -1) {
			clb_x_r_col =  CLB_COL[slice_x_r];
		}
		
		int clb_y_t_row = -1;
		if (slice_y_t != -1) {
			clb_y_t_row = (slice_y_t + 1) / 50;
		}
		
		int clb_y_b_row = -1;
		if (slice_y_b == -1){
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
			 dsp_y_t_row = (dsp_y_t + 1) / 20;
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
			bram_y_t_row = (bram_y_t + 1) / 20;
		}
		
		int bram_y_b_row = -1;
		if (bram_y_b != -1) {
			bram_y_b_row = bram_y_b / 20;
		}

		left = Collections.min(Arrays.asList(clb_x_l_col, dsp_x_l_col, bram_x_l_col));
		right = Collections.max(Arrays.asList(clb_x_r_col, dsp_x_r_col, bram_x_r_col));
		bottom = Collections.max(Arrays.asList(clb_y_b_row, dsp_y_b_row, bram_y_b_row));
		top = Collections.max(Arrays.asList(clb_y_t_row, dsp_y_t_row, bram_y_t_row));
		
	}
	
	
	public ArrayList<Integer> getFrames() {
		ArrayList<Integer> frameAddresses = new ArrayList<>();
		int h, r, c, m = 0;
		for (int y = bottom; y < top; y++){
			h = getBottom(y);
			r = getRow(y);
			for (c = left ; c <= right; c++){
				for (m = 0; m < ARTIX7_200_COLUMN_FRAMES[c]; m++) {
					int farAddress =  (0 << 23) | (h << 22) | (r << 17) | (c << 7) | m;
					frameAddresses.add(farAddress);
					System.out.println("BOTTOM = " + h + " ROW = " + r + " COLUMN = " + c + " MINOR = " + m);
				}
			}
		}
		System.out.println("# of Frammes = "  + frameAddresses.size());
		return frameAddresses;
	}
	
	private static int getBottom(int y) {
		if (y >= 0 && y < 150)
			return 0;
		else
			return 1;
	}
	
	// return the row
	private static int getRow(int y) {
		if (getBottom(y) == 1) {
			if (y >= 0 && y < 50) {
				return 2;
			} else if (y >= 50 && y < 100) {
				return 1;
			} else {
				return 0;
			}
		}
			
		else {
			if (y >= 150 && y < 200) {
				return 0;
			} else {
				return 1;
			}
		}
	}
	
}

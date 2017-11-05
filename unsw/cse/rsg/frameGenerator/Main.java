package unsw.cse.rsg.frameGenerator;


public class Main {
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//add_cells_to_pblock [get_pblocks pblock_sr_0] [get_cells -quiet [list xspace_inst/node_2/sr_0]]
		//resize_pblock [get_pblocks pblock_sr_0] -add {SLICE_X124Y200:SLICE_X163Y249}
		//resize_pblock [get_pblocks pblock_sr_0] -add {DSP48_X7Y80:DSP48_X8Y99}
		//resize_pblock [get_pblocks pblock_sr_0] -add {RAMB18_X7Y80:RAMB18_X8Y99}
		//resize_pblock [get_pblocks pblock_sr_0] -add {RAMB36_X7Y40:RAMB36_X8Y49}
		
		int slice_x_l = 62; // 124 /2
		int slice_y_b = 200;
		int slice_x_r = 81; // (163-1) / 2
		int slice_y_t = 249;
		
		int dsp_x_l = 7;
		int dsp_y_b = 80;
		int dsp_x_r = 8;
		int dsp_y_t = 99;
		
		int bram_x_l = 7;
		int bram_y_b = 80;
		int bram_x_r = 8;
		int bram_y_t = 99;
				
		FrameGenerator fg = new FrameGenerator(
				slice_x_l, slice_y_b, slice_x_r, slice_y_t, 
				dsp_x_l, dsp_y_b, dsp_x_r, dsp_y_t, 
				bram_x_l, bram_y_b, bram_x_r, bram_y_t) ;
		
		fg.getFrames();
	
	}
	
}

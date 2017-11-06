package unsw.cse.rsg.frameGenerator;

public class Main {
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//add_cells_to_pblock [get_pblocks pblock_sr_0] [get_cells -quiet [list xspace_inst/node_2/sr_0]]
		//resize_pblock [get_pblocks pblock_sr_0] -add {SLICE_X124Y200:SLICE_X163Y249}
		//resize_pblock [get_pblocks pblock_sr_0] -add {DSP48_X7Y80:DSP48_X8Y99}
		//resize_pblock [get_pblocks pblock_sr_0] -add {RAMB18_X7Y80:RAMB18_X8Y99}
		//resize_pblock [get_pblocks pblock_sr_0] -add {RAMB36_X7Y40:RAMB36_X8Y49}
		
				
		FrameGenerator fg = new FrameGenerator() ;
		fg.getFrames(0, 111, 47, 144, 0, 46, 2, 57, 0, 46, 2, 57);
		fg.printCoordinates();
		fg.getFrames(56,150,163,199,3,60,8,79,3,60,8,79);
		fg.printCoordinates();
		fg.getFrames(54,50,163,99,3,20,8,39,3,20,8,39);
		fg.printCoordinates();
		fg.getFrames(56,100,163,149,3,40,8,59,3,40,8,59);
		fg.printCoordinates();
	}
	
}

package unsw.cse.rsg.fmer;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.Bitstream;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.BitstreamParser;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.Packet;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.PacketList;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.PacketOpcode;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.RegisterType;


public class StaticFramesExtractor {
	
	private static ArrayList<String> partialBitstreams = null;
	
	public static void main(String[] args) {
		String partialBitstreamsPath = null;
		String staticBitstreamsPath = null;
		int numPartial = 0;
		int numTotal = 0;
		int numStatic = 0;
		partialBitstreams = new ArrayList<String>();
		// create the command line parser
		org.apache.commons.cli.CommandLineParser parser = new BasicParser();
		// create the Options
		org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
		
		//Parser options	
		options.addOption( "p", true, "Folder path containing the partial bitstreams");
		options.addOption( "t", true, "Path of top bitstream file");
		options.addOption( "h", false, "Help.");
	
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		
		try {
			
			CommandLine cmd = parser.parse(options, args);
			
			if(cmd.hasOption("h") || (cmd.getOptions().length == 0))
			{
				System.out.println("Argument p: Specify the partial bitstreams folder");
				System.out.println("Argument s: Path of static bitstream file");
				System.out.println("DO NOT STORE THE STATIC BISTREAM IN THE SAME FOLDER THAT THE PARTIALBITSTREAMS ARE INCLUDED");
				formatter.printHelp( "ant", options );
				return;
			}
			
			/*********************************************************/
			if(cmd.getOptionValue("p") == null) {
			    System.out.println("Specify the path with partial bitstreams folder");
			    formatter.printHelp( "ant", options );
			    return;
			}
			else {
			    partialBitstreamsPath = cmd.getOptionValue("p");
			    
			}
			
			if(cmd.getOptionValue("t") == null) {
			    System.out.println("Specify the Path of static bitstream file");
			    formatter.printHelp( "ant", options );
			    return;
			}
			else {
			    staticBitstreamsPath = cmd.getOptionValue("t");
			    
			}
						
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR ARGUMENTS");
			System.out.println("\n" + "Type StaticFramesExtractor.jar -h to see the usage help of the tool.");
			return;
		}
		
		final File folder = new File(partialBitstreamsPath);
		listFilesForFolder(folder);
		//System.out.println(partialBitstreams);
		//System.out.println(staticBitstreamsPath);
		
		ArrayList<Integer> staticFrames = getFrames(staticBitstreamsPath);
		numTotal = staticFrames.size();
		//System.out.println("This devices has " + staticFrames.size() + ". It does not include the BRAM frames.");
		
		for(String pathPartial : partialBitstreams){
			ArrayList<Integer> partialFrames = getFrames(pathPartial);
			System.out.println(pathPartial + " frames: " + partialFrames.size());
			numPartial += partialFrames.size();
			staticFrames.removeAll(partialFrames);
		}
		numStatic = staticFrames.size();
		System.out.println("\nDevice  frames :" + numTotal);
		System.out.println("\nStatic  frames :" + numStatic);
		System.out.println("\nPartial frames :" + numPartial);
		System.out.println("\nDevice frames = Static frames + Partial frames => " + (numPartial + numStatic) + " = " + numStatic + " + " + numPartial);
		//System.out.println("All frames");
		//print(staticFrames);
		printNonContinousFrames(staticFrames);

	}
	
	private static void print(ArrayList<Integer> l){
		int i,j = 0;	
		for (i = 0; i < l.size(); i++){
			if(j == 10){
				System.out.println();
				j = 0;
			}
			System.out.print(String.format("%7d", l.get(i)) + ", ");
			j++;
		}
		
	}
	
	private static void printNonContinousFrames(ArrayList<Integer> l){
		ArrayList<Integer> resultList= new ArrayList<Integer>();
		Integer prevNumber = l.get(0);
		resultList.add(prevNumber);
		int i;
		for (i = 1; i < l.size() - 1; i++) {
		    if (prevNumber.equals(l.get(i) - 1) && l.get(i + 1).equals(l.get(i) + 1)) {
		    } else {
		        resultList.add(l.get(i));
		    }
		    prevNumber= l.get(i);
		}
		prevNumber= l.get(i);
		resultList.add(prevNumber);
		i= 0;
		int j = 0;	
		
		//STATIC FRAMES ADDRESS (Column)
		System.out.println("\nu32 baseFrames[]:BASE FRAME ADDRESS (Column)\n");
		System.out.println("\nu32 baseFrames[] = {");
		for (i = 0; i < resultList.size() - 3;){
			if(j == 10){
				System.out.println();
				j = 0;
			}
			System.out.print(String.format("%7d", resultList.get(i)) + ", ");
			j++;
			i = i + 2;
		}
		System.out.println(resultList.get(i) + "\n};");
		System.out.println("\n\nu8 numberOfFrames[]:NUMBER OF FRAMES\n");
		//FRAMES PER COLUMN
		j = 0;
		System.out.print("\nu8 numberOfFrames[] = {\n");
		for (i = 0; i < resultList.size() - 3;){
			if(j == 10){
				System.out.println();
				j = 0;
			}	
			System.out.print(String.format("%3d", Math.abs(resultList.get(i) - resultList.get(i+1))) + ", ");
		    j++;
		    i = i + 2;
		}
		System.out.print(String.format("%3d", Math.abs(resultList.get(i) - resultList.get(i+1))) + "\n};\n");
		//MEMORY USAGE
		System.out.println("\nRequired memory for u32 baseFrames[] = " + (resultList.size() / 2) * 4 + " bytes");
		System.out.println("Required memory for u8 numberOfFrames[] = " + (resultList.size() / 2)      + " bytes");
		
		j = 0;
		Integer baseFrame = 0;
		Integer nubOfFrames = 0;
		Integer frame = 0;
		//BASE FRAME ADDRESS (Column) 31 TO 8. NUMBER OF FRAMES 7 TO 0 
		System.out.println("\n\nArray containing both BASE FRAME ADDRESS (Column) 31 TO 8 bit and NUMBER OF FRAMES 7 TO 0 bit\n");
		System.out.println("\nu32 frames[] = {");
		for (i = 0; i < resultList.size() - 3;){
			if(j == 10){
				System.out.println();
				j = 0;
			}
			baseFrame = resultList.get(i);
			nubOfFrames = resultList.get(i+1) - baseFrame;
			frame = (baseFrame << 8) | nubOfFrames;
			System.out.print(String.format("%10d",frame) + ", ");
			j++;
			i = i + 2;
		}
		baseFrame = resultList.get(i);
		nubOfFrames = resultList.get(i+1) - baseFrame;
		frame = (baseFrame << 8) | nubOfFrames;
		System.out.print(String.format("%10d",frame) + "\n};");
		//MEMORY USAGE
		System.out.println("\nRequired memory for u32 frames[] = " + (resultList.size() / 2) * 4 + " bytes");	
	}
	
	private static void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            partialBitstreams.add(fileEntry.getPath());
	        }
	    }
	}
	
	private static ArrayList<Integer> getFrames(String bitstreamPath) {
		Bitstream bitstream = BitstreamParser.parseBitstreamExitOnError(bitstreamPath); // Load bistream on the Bitream Object
		PacketList packets = bitstream.getPackets(); // Get all packets from Bitream 
		Iterator<Packet> pi = packets.iterator(); 
		Packet p;
		TreeSet<Integer> farAddresses = new TreeSet<Integer>();
		
		//For all packets of the bitstream, find the packets that write the conf memory and are of
		//"block type CLB, I/O, CLK (000) or CFG_CLB (010)" get the FAR address of that frame.

		while (pi.hasNext()) {
			p = pi.next();
			if (//p.getPacketType() == PacketType.ONE &&
				p.getOpcode() == PacketOpcode.WRITE &&
				p.getRegType() == RegisterType.FAR) {
				// Get FAR address
				Integer farAddress = p.getData().get(0);
				// Add only if frame's block type is 
				if( (farAddress >> 23 == 0) || (farAddress >> 23 == Integer.parseInt("010", 2)) ){ 
					farAddresses.add(farAddress);
				}
			}
		}
		
		ArrayList<Integer> frames = new ArrayList<Integer>();
		for (Integer ad : farAddresses) {
			frames.add(ad);
		}
		
		return frames; // Return list with all frames
	}
		
}
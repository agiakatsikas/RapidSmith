package FaultInjectionPlatform;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.Bitstream;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.BitstreamParser;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.Packet;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.PacketList;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.PacketOpcode;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.PacketType;
import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.RegisterType;

public class FrameExtractor {
	public ArrayList<Integer> getFrames(String bitstreamPath) {
		Bitstream bitstream = BitstreamParser.parseBitstreamExitOnError(bitstreamPath);
		PacketList packets = bitstream.getPackets();
		Iterator<Packet> pi = packets.iterator();
		Packet p;
		
		TreeSet<Integer> farAddresses = new TreeSet<Integer>();
		while (pi.hasNext()) {
			p = pi.next();
			if (p.getPacketType() == PacketType.ONE &&
				p.getOpcode() == PacketOpcode.WRITE &&
				p.getRegType() == RegisterType.FAR) {
				// Get FAR address
				Integer farAddress = p.getData().get(0);
				farAddresses.add(farAddress);
			}
		}
		ArrayList<Integer> frames = new ArrayList<Integer>();
		for (Integer ad : farAddresses) {
			frames.add(ad);
		}
		return frames;
	}
	
	public void subtractFrames(ArrayList<Integer> framesA, ArrayList<Integer> framesB) {
		framesA.removeAll(framesB);
	}
	
	public ArrayList<String> readVoterList(String voterListPath) {
		ArrayList<String> voterList = new ArrayList<String>();
		Path path = Paths.get(voterListPath);
		try {
			Scanner scanner =  new Scanner(path, "UTF-8");
			while (scanner.hasNextLine()) {
				//process each line in some way
				voterList.add(scanner.nextLine());
			}
			scanner.close();
		} catch (IOException e) {
			System.err.println("ERROR -- Unable to read voter list.");
			System.exit(1);
		}
		return voterList;
	}
}

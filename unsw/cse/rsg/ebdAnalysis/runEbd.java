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
package unsw.cse.rsg.ebdAnalysis;

import FaultInjectionPlatform.Logger;
import unipi.sevax.analysis.EBD_analysis;

public class runEbd {
	
	static protected final String bitstreamPath = "E:/fie.bit";
	static protected final String ebdFilePath = "E:/fie.ebd";
	static protected final String ebcFilePath = "E:/fie.ebc";
	static protected final String logFilePath = "E:/fie.log";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
/*		Logger logger = new Logger(logFilePath);
		EBD_analysis ebd = new EBD_analysis(bitstreamPath, logger);
		ebd.loadBitStream();
		ebd.loadEBD();
		ebd.getResults();*/
		//ebd.createDUTSensitiveBits();
		
		
		EssentialBits7Series ebdNew = new EssentialBits7Series(bitstreamPath, ebdFilePath, ebcFilePath);
		ebdNew.printResults();

	}

}

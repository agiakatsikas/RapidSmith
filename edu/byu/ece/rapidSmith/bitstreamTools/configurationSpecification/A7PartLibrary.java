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
/**
<<<<<<< HEAD
 * This file was auto-generated on Thu Sep 21 11:17:25 AEST 2017
=======
 * This file was auto-generated on Thu Sep 21 10:58:09 AEST 2017
>>>>>>> origin/master
 * by edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification.XilinxA7DeviceClassGenerator.
 * See the source code to make changes.
 *
 * Do not modify this file directly.
 */


package edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification;


import java.util.ArrayList;
import java.util.Arrays;

public class A7PartLibrary extends PartLibrary {

	public A7PartLibrary() {
		super();
	}

	protected void addParts() {
		addPart(new XC7A100T());
		addPart(new XC7A200T());
	}

	class XC7A100T extends A7ConfigurationSpecification {


		public XC7A100T() {
			super();
			_deviceName = "XC7A100T";
			_deviceIDCode = "03631093";
			_validPackages = new String[] {"csg324", "fgg484", "fgg676", "ftg256", };
			_validSpeedGrades = new String[] {"-3", "-2", "-1", "-2L", };
			_topRows = 2;
			_bottomRows = 2;
			_blockTypeLayouts = new ArrayList<BlockTypeInstance>(Arrays.asList(new BlockTypeInstance[] {
					new BlockTypeInstance(LOGIC_INTERCONNECT_BLOCKTYPE, new BlockSubType[] {
						IOB, CLK, CLB, CLB, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, DSP, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, DSP, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, CLB, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, CLB, DSP, CLB, CLB, BRAMINTERCONNECT, GTP, CLB, CLB, CLB, CLK, LOGIC_OVERHEAD, 
					}),
					new BlockTypeInstance(BRAM_CONTENT_BLOCKTYPE, new BlockSubType[] {
						BRAMCONTENT, BRAMCONTENT, BRAMCONTENT, BRAMCONTENT, BRAMOVERHEAD, 
					}),
			}));
			_overallColumnLayout = _blockTypeLayouts.get(0).getColumnLayout();
		}
	}

	class XC7A200T extends A7ConfigurationSpecification {


		public XC7A200T() {
			super();
			_deviceName = "XC7A200T";
			_deviceIDCode = "03636093";
			_validPackages = new String[] {"fbg484", "fbg676", "ffg1156", };
			_validSpeedGrades = new String[] {"-3", "-2", "-1", "-2L", };
			_topRows = 2;
			_bottomRows = 3;
			_blockTypeLayouts = new ArrayList<BlockTypeInstance>(Arrays.asList(new BlockTypeInstance[] {
					new BlockTypeInstance(LOGIC_INTERCONNECT_BLOCKTYPE, new BlockSubType[] {
						IOB, CLK, CLB, CLB, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, DSP, CLB, CLB, CLB, CLB, DSP, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, DSP, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, BRAMINTERCONNECT, GTP, CLB, CLB, DSP, CLB, CLB, CLB, CLB, DSP, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, CLB, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, DSP, CLB, CLB, CLB, CLB, DSP, GTP, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, DSP, CLB, CLB, CLB, CLB, DSP, CLB, CLB, BRAMINTERCONNECT, CLB, CLB, CLB, CLB, CLK, LOGIC_OVERHEAD, 
					}),
					new BlockTypeInstance(BRAM_CONTENT_BLOCKTYPE, new BlockSubType[] {
						BRAMCONTENT, BRAMCONTENT, BRAMCONTENT, BRAMCONTENT, BRAMCONTENT, BRAMCONTENT, BRAMCONTENT, BRAMCONTENT, BRAMCONTENT, BRAMOVERHEAD, 
					}),
			}));
			_overallColumnLayout = _blockTypeLayouts.get(0).getColumnLayout();
		}
	}

}

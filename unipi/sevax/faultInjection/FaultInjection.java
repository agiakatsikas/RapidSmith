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
package unipi.sevax.faultInjection;

import com.trolltech.qt.gui.QApplication;

public class FaultInjection {

	/**
	 * @param args
	 */
	static SerialComm serial;
	
	public static void main(String[] args)
	{
		QApplication.setGraphicsSystem("raster");
		QApplication.initialize(args);
		
		// TODO Auto-generated method stub
		serial = new SerialComm();
		serial.searchForPorts();
		serial.connect("COM6");
		if(serial.isConnected() == true)
		{
			if(serial.initIOStream() == true)
			{
				serial.initListener();
			}
		}
		
	}

}

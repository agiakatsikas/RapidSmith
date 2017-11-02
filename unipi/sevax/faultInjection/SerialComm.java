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

import edu.byu.ece.rapidSmith.device.browser.DeviceBrowser;
import gnu.io.*;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

public class SerialComm implements SerialPortEventListener
{

	//for containing the ports that will be found
    private Enumeration ports = null;
    //map the port names to CommPortIdentifiers
    private HashMap portMap = new HashMap();

    //this is the object that contains the opened port
    private CommPortIdentifier selectedPortIdentifier = null;
    private SerialPort serialPort = null;

    //input and output streams for sending and receiving data
    private InputStream input = null;
    private OutputStream output = null;

    //just a boolean flag that i use for enabling
    //and disabling buttons depending on whether the program
    //is connected to a serial port or not
    private boolean isConnected = false;

    //the timeout value for connecting with the port
    final static int TIMEOUT = 2000;

    //some ascii values for for certain things
    final static int SPACE_ASCII = 32;
    final static int DASH_ASCII = 45;
    final static int NEW_LINE_ASCII = 10;

    //a string for recording what goes on in the program
    //this string is written to the GUI
    String logText = "";
    
    ArrayList<String> serialPorts;
    
    //search for all the serial ports
    //pre style="font-size: 11px;": none
    //post: adds all the found ports to a combo box on the GUI
    public ArrayList<String> searchForPorts()
    {
        ports = CommPortIdentifier.getPortIdentifiers();
        
        this.serialPorts = new ArrayList<String>();
        while (ports.hasMoreElements())
        {
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();

            //get only serial ports
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                //window.cboxPorts.addItem(curPort.getName());
            	serialPorts.add(curPort.getName());
                portMap.put(curPort.getName(), curPort);
            }
        }
        
        return this.serialPorts;
    }
    
  //connect to the selected port in the combo box
    //pre style="font-size: 11px;": ports are already found by using the searchForPorts
    //method
    //post: the connected comm port is stored in commPort, otherwise,
    //an exception is generated
    public void connect(String comPort)
    {
    	searchForPorts();
        String selectedPort = comPort;//(String)window.cboxPorts.getSelectedItem();
        selectedPortIdentifier = (CommPortIdentifier)portMap.get(selectedPort);

        CommPort commPort = null;
        try
        {
            //the method below returns an object of type CommPort
            commPort = selectedPortIdentifier.open("TigerControlPanel", TIMEOUT);
            
            if( commPort instanceof SerialPort )
            {
            	 serialPort = ( SerialPort )commPort;
            	 serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            }
            else
            {
            	return;
            }

            //for controlling GUI elements
            this.isConnected = true;

            //logging
            logText = selectedPort + " opened successfully.";
            //window.txtLog.setForeground(Color.black);
            //window.txtLog.append(logText + "\n");

            //CODE ON SETTING BAUD RATE ETC OMITTED
            //XBEE PAIR ASSUMED TO HAVE SAME SETTINGS ALREADY

            //enables the controls on the GUI if a successful connection is made
            //window.keybindingController.toggleControls();
        }
        catch (PortInUseException e)
        {
            logText = selectedPort + " is in use. (" + e.toString() + ")";

            //window.txtLog.setForeground(Color.RED);
            //window.txtLog.append(logText + "\n");
        }
        catch (Exception e)
        {
            logText = "Failed to open " + selectedPort + "(" + e.toString() + ")";
            //window.txtLog.append(logText + "\n");
            //window.txtLog.setForeground(Color.RED);
        }
    }
    
    
    //open the input and output streams
    //pre style="font-size: 11px;": an open port
    //post: initialized input and output streams for use to communicate data
    public boolean initIOStream()
    {
        //return value for whether opening the streams is successful or not
        boolean successful = false;

        try {
            //
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            //writeData(0, 0);

            successful = true;
            return successful;
        }
        catch (IOException e) {
            logText = "I/O Streams failed to open. (" + e.toString() + ")";
            //window.txtLog.setForeground(Color.red);
            //window.txtLog.append(logText + "\n");
            return successful;
        }
    }
    
    
    //starts the event listener that knows whenever data is available to be read
    //pre style="font-size: 11px;": an open serial port
    //post: an event listener for the serial port that knows when data is received
    public void initListener()
    {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        }
        catch (TooManyListenersException e)
        {
            logText = "Too many listeners. (" + e.toString() + ")";
            //window.txtLog.setForeground(Color.red);
            //window.txtLog.append(logText + "\n");
        }
    }
    
    
    //disconnect the serial port
    //pre style="font-size: 11px;": an open serial port
    //post: closed serial port
    public void disconnect()
    {
        //close the serial port
        try
        {
            //writeData(0, 0);

            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            this.isConnected = false;
            //setConnected(false);
            //window.keybindingController.toggleControls();

            logText = "Disconnected.";
            //window.txtLog.setForeground(Color.red);
            //window.txtLog.append(logText + "\n");
        }
        catch (Exception e)
        {
            logText = "Failed to close " + serialPort.getName()
                              + "(" + e.toString() + ")";
            //window.txtLog.setForeground(Color.red);
            //window.txtLog.append(logText + "\n");
        }
    }
    
	
	@Override
	public void serialEvent(SerialPortEvent arg0)
	{
		int available = 0;
		try
        {                
			available = input.available();
			//if(available < 8)
			//return;

        }
        catch (Exception e)
        {
        }
		
		if (available > 0) //arg0.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {                
                byte[] data   = new byte[available];
                input.read(data);
                //logText = new String(data);
                //System.out.print(logText);
                
//                DeviceBrowser.dataReceived(data);

            }
            catch (Exception e)
            {
                logText = "Failed to read data. (" + e.toString() + ")";
                //window.txtLog.setForeground(Color.red);
                //window.txtLog.append(logText + "\n");
            }
        }
		
	}
	
	
	//method that can be called to send data
    //pre style="font-size: 11px;": open serial port
    //post: data sent to the other device
    public void writeData(byte[] data, int len)
    {
        try
        {
            output.write(data);
        	//output.write(leftThrottle);
            output.flush();
            //this is a delimiter for the data
            //output.write(DASH_ASCII);
            //output.flush();

            //output.write(rightThrottle);
            //output.flush();
            //will be read as a byte so it is a space key
            //output.write(SPACE_ASCII);
            //output.flush();
        }
        catch (Exception e)
        {
            logText = "Failed to write data. (" + e.toString() + ")";
            //window.txtLog.setForeground(Color.red);
            //window.txtLog.append(logText + "\n");
        }
    }
    
    
    /***********************************************************************************************
     *
     * 
     * 
     ***********************************************************************************************/
    public boolean isConnected()
    {
    	return this.isConnected;
    }

}

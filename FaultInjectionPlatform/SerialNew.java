package FaultInjectionPlatform;

import java.nio.ByteBuffer;

import jssc.*;

public class SerialNew implements SerialPortEventListener{
	private String portName;
	private int baudRate;
    private int retryCounter;
	
	private SerialPort serialPort;
	private StringBuffer buffer;
	
	SerialNew(String portName, int baudRate) {
		this.portName = portName;
		this.baudRate = baudRate;
		this.retryCounter = 0;
	}
	
	public void connect() {
		//Clear buffer
    	buffer = new StringBuffer(256);
    	
    	// Get port ID
    	serialPort = new SerialPort(portName);
    	try {
    		serialPort.openPort();
			serialPort.setParams(this.baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.setRTS(true);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		} catch (SerialPortException e) {
			try {
				serialPort.closePort();
			} catch (SerialPortException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void disconnect() {
		try {
			serialPort.removeEventListener();
			serialPort.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void initListeners() {
	    try {
	    	serialPort.addEventListener((SerialPortEventListener) this);
	    } catch (SerialPortException e) {
			e.printStackTrace();
			System.exit(1);
		}
    }
	

	
	public synchronized void writeDataInteger(int data) {
		byte[] bytes = ByteBuffer.allocate(4).putInt(data).array();

		try {
            serialPort.writeBytes(bytes);
        } catch (Exception e) {
        	e.printStackTrace();
        	System.err.println("ERROR -- Could not write data");
        	System.exit(1);
        }
    }
	
	public synchronized void writeData(byte[] data) {
        try {
            serialPort.writeBytes(data);
        } catch (Exception e) {
        	e.printStackTrace();
        	System.err.println("ERROR -- Could not write data");
        	System.exit(1);
        }
    }
	
	public synchronized void writeString(String str) {
		writeData(str.getBytes());
	}

	@Override
	public void serialEvent(SerialPortEvent arg0) {
        try {
            String data = serialPort.readString();
            if (data != null) {
				// concat with the buffer
            	buffer.append(data);
            }
        }
        catch (Exception e) {
        	e.printStackTrace();
        	System.err.println("ERROR -- Could not read data.");
        	System.exit(1);
        }
	}
	
	public BufferTuple readBuffer() {
    	long startTime = System.currentTimeMillis();
    	while(buffer.length() == 0) {
    		if ((System.currentTimeMillis() - startTime) > 10000) {
    			if (retryCounter < 10) {
    				retryCounter++;
    			} else {
    				System.err.println("TIMED OUT");
    				System.exit(1);
    			}
    			return new BufferTuple("", true);
    		}
    	}
    	retryCounter = 0;
    	String tmpBuffer = buffer.toString();
    	buffer = new StringBuffer(256);
    	return new BufferTuple(tmpBuffer, false);
    }
	
	public void RTS(boolean s){
		try {
			serialPort.setRTS(s);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void DTR(boolean s){
		try {
			serialPort.setDTR(s);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/*
 * Copyright (c) 2010 Brigham Young University
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
package unipi.sevax.analysis;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

//import saplacement.netBB;
import unipi.sevax.analysis.EBD_analysis;
//import unipi.faultInjection.FaultInjection;
//import unipi.faultInjection.SerialComm;

import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QDir.Filters;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.CheckState;
import com.trolltech.qt.core.Qt.DockWidgetArea;
import com.trolltech.qt.core.Qt.ItemDataRole;
import com.trolltech.qt.core.Qt.SortOrder;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QDockWidget;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QGroupBox;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLayoutItem;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QPaintEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QStatusBar;
import com.trolltech.qt.gui.QTextOption;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QDockWidget.DockWidgetFeature;

import edu.byu.ece.rapidSmith.device.Device;
import edu.byu.ece.rapidSmith.device.PrimitiveSite;
import edu.byu.ece.rapidSmith.device.Tile;
import edu.byu.ece.rapidSmith.device.WireConnection;
import edu.byu.ece.rapidSmith.device.WireEnumerator;
import edu.byu.ece.rapidSmith.gui.TileView;
import edu.byu.ece.rapidSmith.gui.WidgetMaker;
import edu.byu.ece.rapidSmith.util.FileTools;
import edu.byu.ece.rapidSmith.util.MessageGenerator;

/**
 * This class creates an interactive Xilinx FPGA device browser for all of the
 * devices currently installed on RapidSmith.  It provides the user with a 2D view
 * of all tile array in the device.  Allows each tile to be selected (double click)
 * and populate the primitive site and wire lists.  Wire connections can also be drawn
 * by selecting a specific wire in the tile (from the list) and the program will draw
 * all connections that can be made from that wire.  The wire positions on the tile
 * are determined by a hash and are not related to FPGA Editor positions.   
 * @author Chris Lavin and Marc Padilla
 * Created on: Nov 26, 2010
 */
public class SensitiveDeviceBrowser extends QMainWindow{
	/** The Qt View for the browser */
	protected TileView view;
	/** The Qt Scene for the browser */
	private SensitiveDeviceBrowserScene scene;
	/** The label for the status bar at the bottom */
	private QLabel statusLabel;
	/** The current device loaded */
	Device device;
	/** The current wire enumerator loaded */
	WireEnumerator we;
	/** The current part name of the device loaded */
	private String currPart;
	/** This is the tree of parts to select */
	//private QTreeWidget treeWidget;
	/** This is the list of primitive sites in the current tile selected */
	private QTreeWidget primitiveList;
	/** This is the list of wires in the current tile selected */
	//private QTreeWidget wireList;
	
	/** This is the current tile that has been selected */
	private Tile currTile = null;
	/** The working directory of the project */
	private QDir workingDirectory;
	/** The EBD analysis for the Xilinx Essential Bits */
	private EBD_analysis ebd;
	private QGroupBox gbSensitiveBits;
	private QLabel lbl1;
	private QLabel lbl2;
	private QLabel lbl3;
	private QVBoxLayout vbox;
	private QCheckBox cbEbd;
	private QCheckBox cbSeu;
	private boolean viewEbd;
	private boolean viewSeu;
	QDockWidget dockWidget;
	
	protected boolean hideTiles 		= false;	
	protected boolean drawPrimitives 	= true;
	
	/**
	 * Main method setting up the Qt environment for the program to run.
	 * @param args
	 */
	public static void main(String[] args){
		QApplication.setGraphicsSystem("raster");
		QApplication.initialize(args);
		SensitiveDeviceBrowser testPTB = new SensitiveDeviceBrowser(null);
		testPTB.show();
		QApplication.exec();
	}

	/**
	 * Constructor which initializes the GUI and loads the first part found.
	 * @param parent The Parent widget, used to add this window into other GUIs.
	 */
	public SensitiveDeviceBrowser(QWidget parent){
		super(parent);
		
		// set the title of the window
		setWindowTitle("Device Browser");
		
		initializeSideBar();
		
		// Gets the available parts in RapidSmith and populates the selection tree
		if(this.currPart == "")
		{
			ArrayList<String> parts = FileTools.getAvailableParts();
			if(parts.size() < 1){
				MessageGenerator.briefErrorAndExit("Error: No available parts. " +
					"Please generate part database files.");
			}
			if(parts.contains("xc4vlx100ff1148")){
				currPart = "xc4vlx100ff1148";
			}
			else{
				currPart = parts.get(0);
			}
			
			device = FileTools.loadDevice(currPart);
		}		
		
		we = FileTools.loadWireEnumerator(currPart);
		
		// Setup the scene and view for the GUI
		scene = new SensitiveDeviceBrowserScene(device, we, hideTiles, drawPrimitives, this, this.workingDirectory.path(), this.viewEbd, this.viewSeu);
		view = new TileView(scene);
		setCentralWidget(view);

		// Setup some signals for when the user interacts with the view
		scene.updateStatus.connect(this, "updateStatus(String, Tile)");
		scene.updateTile.connect(this, "updateTile(Tile)");
		
		// Initialize the status bar at the bottom
		statusLabel = new QLabel("Status Bar");
		statusLabel.setText("Status Bar");
		QStatusBar statusBar = new QStatusBar();
		statusBar.addWidget(statusLabel);
		setStatusBar(statusBar);
		
		// Set the opening default window size to 1024x768 pixels
		resize(1024, 768);
	}

	/**
	 * Populates the treeWidget with the various parts and families of devices
	 * currently available in this installation of RapidSmith.  It also creates
	 * the windows for the primitive site list and wire list.
	 */
	private void initializeSideBar(){
		//treeWidget = WidgetMaker.createAvailablePartTreeWidget("Select a part...");
		//treeWidget.doubleClicked.connect(this,"showPart(QModelIndex)");
		
		//QWidget panel = new QWidget(this);
		vbox = new QVBoxLayout();
		gbSensitiveBits = new QGroupBox();		
		gbSensitiveBits.setTitle("SEU sensitive bits analysis");
		
		/* Sensitive bits GUI */
		
		cbEbd			= new QCheckBox(gbSensitiveBits);
		cbEbd.setText("View EBD analysis sensitive layout");
		//cbEbd.move(10, 10);
		
		cbSeu			= new QCheckBox(gbSensitiveBits);
		cbSeu.setText("View SEU analysis sensitive layout");
		//cbSeu.move(10, 10 + cbEbd.height() + 1);
		
	    vbox.addWidget(this.cbEbd);
	    vbox.addWidget(this.cbSeu);	    
	    vbox.addStretch(0);
	    gbSensitiveBits.setLayout(vbox);
	    
		//gbSensitiveBits.resize(50, 200);
		//gbSensitiveBits.move(5, 5);
		
		dockWidget = new QDockWidget(tr("SEU sensitive analysis framework"), this);
		dockWidget.setWidget(gbSensitiveBits);
		dockWidget.setFixedWidth(250);//.setFeatures(DockWidgetFeature.DockWidgetMovable);
		addDockWidget(DockWidgetArea.LeftDockWidgetArea, dockWidget);
		
		
		lbl3 = new QLabel();
		lbl3.setText("EBD sensitive resources");
		lbl3.setStyleSheet("QLabel { background-color : yellow; color : black; font-size:9pt;}");
		this.vbox.addWidget(lbl3);
		
		lbl2 = new QLabel();
		lbl2.setText("SEU-framework sensitive resources");
		lbl2.setStyleSheet("QLabel { background-color : white; color : black; font-size:9pt;}");
		this.vbox.addWidget(lbl2);
		
		lbl1 = new QLabel();
		lbl1.setText("SEU-framework & EBD sensitive resources");
		lbl1.setStyleSheet("QLabel { background-color : red; color : black; font-size:9pt;}");
		this.vbox.addWidget(lbl1);
		
		lbl1.hide();
		lbl2.hide();
		lbl3.hide();
		
		// Create the primitive site list window
		/*primitiveList = new QTreeWidget();
		primitiveList.setColumnCount(2);
		ArrayList<String> headerList = new ArrayList<String>();
		headerList.add("Site");
		headerList.add("Type");
		primitiveList.setHeaderLabels(headerList);
		primitiveList.setSortingEnabled(true);*/
		
		//QDockWidget dockWidget2 = new QDockWidget(tr("Primitive List"), this);
		//dockWidget2.setWidget(primitiveList);
		//dockWidget2.setFeatures(DockWidgetFeature.DockWidgetMovable);
		//addDockWidget(DockWidgetArea.LeftDockWidgetArea, dockWidget2);
		//dockWidget2
		
		// Create the wire list window
		/*wireList = new QTreeWidget();
		wireList.setColumnCount(2);
		ArrayList<String> headerList2 = new ArrayList<String>();
		headerList2.add("Wire");
		headerList2.add("Sink Connections");
		wireList.setHeaderLabels(headerList2);
		wireList.setSortingEnabled(true);
		QDockWidget dockWidget3 = new QDockWidget(tr("Wire List"), this);
		dockWidget3.setWidget(wireList);
		dockWidget3.setFeatures(DockWidgetFeature.DockWidgetMovable);
		addDockWidget(DockWidgetArea.LeftDockWidgetArea, dockWidget3);*/
	
		
		// Get the working directory
		workingDirectory = new QDir();
		String dirPath = QFileDialog.getExistingDirectory (this, "Directory", workingDirectory.path());
		if(dirPath != null )
	    {
			workingDirectory.setPath(dirPath);
	    }
		this.loadDevice();
		if(this.ebd != null)
		{
			this.ebd.loadBitStream();
			this.ebd.loadEBD();
			this.ebd.findFpgaResourceSensitiveBits();
		}
		
		/* Event handlers */
		cbEbd.stateChanged.connect(this, "cbEbd_stateChanged()");
		cbSeu.stateChanged.connect(this, "cbSeu_stateChanged()");
		
		//String fileName = QFileDialog.getOpenFileName(this, tr("Open File"), tr("bit Files (*.bit)"));
		

		// Draw wire connections when the wire name is double clicked
		//wireList.doubleClicked.connect(this, "wireDoubleClicked(QModelIndex)");
	}

	/**
	 * This method will draw all of the wire connections based on the wire given.
	 * @param index The index of the wire in the wire list.
	 */
	public void wireDoubleClicked(QModelIndex index){
		scene.clearCurrentLines();
		if(currTile == null) return;
		int currWire = we.getWireEnum(index.data().toString());
		if(currWire < 0) return;
		if(currTile.getWireConnections(we.getWireEnum(index.data().toString())) == null) return;
		for(WireConnection wire : currTile.getWireConnections(we.getWireEnum(index.data().toString()))){
			scene.drawWire(currTile, currWire, wire.getTile(currTile), wire.getWire());
		}
	}
	
	/**
	 * This method gets called each time a user double clicks on a tile.
	 */
	protected void updateTile(Tile tile){
		currTile = tile;
		updatePrimitiveList();
		updateWireList();
	}
	
	/**
	 * This will update the primitive list window based on the current
	 * selected tile.
	 */
	protected void updatePrimitiveList(){
		primitiveList.clear();
		if(currTile == null || currTile.getPrimitiveSites() == null) return;
		for(PrimitiveSite ps : currTile.getPrimitiveSites()){
			QTreeWidgetItem treeItem = new QTreeWidgetItem();
			treeItem.setText(0, ps.getName());
			treeItem.setText(1, ps.getType().toString());
			primitiveList.insertTopLevelItem(0, treeItem);
		}
	}

	/**
	 * This will update the wire list window based on the current
	 * selected tile.
	 */
	protected void updateWireList(){
		//wireList.clear();
		if(currTile == null || currTile.getWireHashMap() == null) return;
		for(Integer wire : currTile.getWireHashMap().keySet()) {
			QTreeWidgetItem treeItem = new QTreeWidgetItem();
			treeItem.setText(0, we.getWireName(wire));
			WireConnection[] connections = currTile.getWireConnections(wire);
			treeItem.setText(1, String.format("%3d", connections == null ? 0 : connections.length));
			//wireList.insertTopLevelItem(0, treeItem);
		}
		//wireList.sortByColumn(0, SortOrder.AscendingOrder);
	}

	/**
	 * This method loads a new device based on the part name selected in the 
	 * treeWidget.
	 * @param qmIndex The index of the part to load.
	 */
	protected void showPart(QModelIndex qmIndex){
		Object data = qmIndex.data(ItemDataRole.AccessibleDescriptionRole);
		if( data != null){
			if(currPart.equals(data))
				return;
			currPart = (String) data;			
			device = FileTools.loadDevice(currPart);
			we = FileTools.loadWireEnumerator(currPart);
			scene.setDevice(device);
			scene.setWireEnumerator(we);
			scene.initializeScene(hideTiles, drawPrimitives);
			statusLabel.setText("Loaded: "+currPart.toUpperCase());
		}
	}
	
	/**
	 * This method updates the status bar each time the mouse moves from a 
	 * different tile.
	 */
	protected void updateStatus(String text, Tile tile){
		statusLabel.setText(text);
		//currTile = tile;
		//System.out.println("currTile=" + tile);
	}

	
	public void cbEbd_stateChanged()
	{
		if(this.cbEbd.checkState() == CheckState.Checked)
		{
			this.viewEbd = true;
		}
		else
		{
			this.viewEbd = false;
		}
		
		this.updateView();
	}
	
	public void cbSeu_stateChanged()
	{
		if(this.cbSeu.checkState() == CheckState.Checked)
		{
			this.viewSeu = true;
		}
		else
		{
			this.viewSeu = false;
		}
		
		this.updateView();
	}
	
	private void updateView()
	{
		// Setup the scene and view for the GUI
		scene = new SensitiveDeviceBrowserScene(device, we, hideTiles, drawPrimitives, this, this.workingDirectory.path(), this.viewEbd, this.viewSeu);
		view = new TileView(scene);
		setCentralWidget(view);
		
		// Setup some signals for when the user interacts with the view
		scene.updateStatus.connect(this, "updateStatus(String, Tile)");
		scene.updateTile.connect(this, "updateTile(Tile)");
		
		if(this.viewEbd && this.viewSeu)
		{
			this.lbl1.setText("SEU-framework & EBD sensitive resources");
			this.lbl1.show();
			this.lbl2.show();
			this.lbl3.show();
		}
		else if(this.viewEbd || this.viewSeu)
		{
			this.lbl1.setText("Sensitive resources");
			this.lbl1.show();
			this.lbl2.hide();
			this.lbl3.hide();
		}
		else
		{
			this.lbl1.hide();
			this.lbl2.hide();
			this.lbl3.hide();
		}
	}
	
	/**
	 * This method searches the working directory in order to find the FPGA part number.
	 */
	private void loadDevice()
	{
		// Return if we have not set the path of the working directory
		if(this.workingDirectory == null) { return; }
		
		String deviceName = "";
		this.currPart = "";
		ArrayList<String> nameFilters = new ArrayList<String>();
		
		// Search for a BIT file within the working directory.
		// We use the BIT file for the initialization of the EBD analysis procedure
		nameFilters.add("*.bit");
		ArrayList<String> files = (ArrayList<String>) this.workingDirectory.entryList(nameFilters);
		if(files.size() > 0)
		{
			this.ebd = new EBD_analysis(this.workingDirectory.path() + "\\" + files.get(0));
			deviceName = this.ebd.getDevice().getPartName();
		}
		
		// Search for an XDL file within the working directory.
		// We use the XDL file in order to get the FPGA part number of the design
		if(deviceName == "")
		{
			nameFilters.add("*.xdl");
			files = (ArrayList<String>) this.workingDirectory.entryList(nameFilters);
			if(files.size() > 0)
			{
				try
				{
					String line;
					BufferedReader bReader = new BufferedReader(new FileReader(this.workingDirectory.path() + "\\" + files.get(0)));
					
					while ((line = bReader.readLine()) != null)
					{	
						// The syntax for the design statement is:                
						// design <design_name> <part> <ncd version>;             
						// or                                                     
						// design <design_name> <device> <package> <speed> <ncd_version>
						if(line.contains("design") && (line.charAt(0) == 'd') && (line.charAt(5) == 'n'))
						{
							String lineArgs[] = line.split(" ");
							deviceName = lineArgs[2];
							break;
						}
					}
					
					bReader.close();
				}
				catch(IOException ex){}
			}
			else
			{
				// Search for an EBD file within the working directory.
				// We use the EBD file in order to get the FPGA part number of the design
				nameFilters.add("*.ebd");
				files = (ArrayList<String>) this.workingDirectory.entryList(nameFilters);
				if(files.size() > 0)
				{
					try
					{
						String line;
						BufferedReader bReader = new BufferedReader(new FileReader(this.workingDirectory.path() + "\\" + files.get(0)));
						
						while ((line = bReader.readLine()) != null)
						{	
							// The syntax for the design statement is:                
							// Part:		<part>
							if(line.contains("Part:"))
							{
								line = line.replace(" ", "");
								line = line.replace("\t", "");
								deviceName = line.substring(line.indexOf(':'), line.length() - 1);
								break;
							}
						}
						
						bReader.close();
					}
					catch(IOException ex){}
				}			
			}
		}
		
		// If we have found the device part number
		if(deviceName != "")
		{
			if(deviceName.contains("-"))
			{
				deviceName = deviceName.substring(0, deviceName.indexOf('-'));
			}
			
			currPart = deviceName;			
			device = FileTools.loadDevice(currPart);
		}
		
	}
}

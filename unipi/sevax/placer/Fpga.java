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
package unipi.sevax.placer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import java.util.Random;
import java.util.Set;


import unipi.sevax.placer.NetBB;
import unipi.sevax.utilities.Maths;

import edu.byu.ece.rapidSmith.design.Design;
import edu.byu.ece.rapidSmith.design.Instance;
import edu.byu.ece.rapidSmith.design.Net;
import edu.byu.ece.rapidSmith.design.Pin;
import edu.byu.ece.rapidSmith.device.PrimitiveSite;
import edu.byu.ece.rapidSmith.device.PrimitiveType;
import edu.byu.ece.rapidSmith.device.Tile;
import edu.byu.ece.rapidSmith.device.TileType;
import edu.byu.ece.rapidSmith.util.FamilyType;
/**
 * This FPGA class holds information about the targeting FPGA layout and the instances of the design.
 * It also contains a list with the bounding boxes of each net in the design and the design cost.
 * We load every tile of the targeting FPGA and the placed instances that rely in them. 
 * The class contains methods to swap the instances of two random tiles and automatically update the design cost. 
 * We also provide methods to restore the design to its last state (i.e, before swapping the last two random tiles).
 * This class also provides statistics about the numbers of slices, tiles, IOs, tiles with carry chains and resource utilisation.     
 * @author Aitzan Sari, Dimitris Agiakatsikas.
 * Created on: Oct 24, 2013
 */
public class Fpga {
	
	/************************************************************************
	********************  Global variables **********************************
	************************************************************************/
	/**An ArrayList of the complex blocks of the design. In other words the tiles of the design and their relying instances.*/ 
	public  ArrayList<ComplexBlock> blocks;
	/**The design that is going to be placed.*/
	private static Design design;
	/**The cost of the design.*/
	private double designCost;
	/**The HashMap containing the bounding box of each net of the design.*/
	public HashMap<String, NetBB> netsBB;
	/**The first random tile that was swapped.*/
	private int oldRndCbA;
	/**The second random tile that was swapped.*/
	private int oldRndCbB;
	/**A boolean indicating if a UCF file is provided in order to be taken into account in the placement phase.*/
	public boolean ucf;
	/**An ArrayList containing the tiles that must not be moved in the placement phase.*/
	public ArrayList<String> ucfList;
	
	/************************************************************************
	********************  Constructors **************************************
	************************************************************************/
	/**
	 * Constructor which initializes all member data structures.
	 */
	public Fpga(){
		design = new Design();
		this.blocks = new ArrayList<ComplexBlock>();
		this.netsBB = new HashMap<String, NetBB>();
		this.designCost = 0.0;
		/**The last random number for choosing the first random complexBlock(Tile). We save this random number in order to roll back the design.*/
		this.oldRndCbA = 0;
		/**The last random number for choosing the second random complexBlock(Tile). We save this random number in order to roll back the design.*/
		this.oldRndCbB = 0;
		/**A list with all user constrains.*/
		this.ucfList = new ArrayList<String>();
		/**A flag indicating if the UCF list will be used.*/ 
		this.ucf = false;
	}
	
	/**This constructor gets a design and the list with the UCF constraints. 
	 * Afterwards it loads the complex blocks of the design and calculates the bounding boxes of every net contained in the complex box.
	 * @param des The design that must be placed 
	 * @param ucfList The ArrayList with the UCF constraints. 
	 */
	public Fpga(Design des, ArrayList<String> ucfList){
		design = des;
		this.blocks = new ArrayList<ComplexBlock>();
		this.netsBB = new HashMap<String, NetBB>();
		this.ucfList = ucfList;
		this.ucf = true;
		loadCompexBlocks(false);
		this.designCost = 0.0;
		this.oldRndCbA = 0;
		this.oldRndCbB = 0;
	}
	
	/**This constructor gets the design that must be placed (It does not consider any user constraints rules).
	 * Afterwards it loads the complex blocks of the design and calculates the bounding boxes of every net contained in the complex box.
	 * @param des The design that must be placed. 
	 */
	public Fpga(Design des){
		design = des;
		this.blocks = new ArrayList<ComplexBlock>();
		this.netsBB = new HashMap<String, NetBB>();
		this.ucfList = new ArrayList<String>();
		this.ucf = false;
		loadCompexBlocks(false);
		this.designCost = 0.0;
		this.oldRndCbA = 0;
		this.oldRndCbB = 0;
	}
	
	
	
	/***********************************************************************
	********************  Public Methods ***********************************
	************************************************************************/
	/**
	 * Swaps the instances of two random tiles and automatically updates the current cost of the design. 
	 * @param debug is set true, we output to the screen console the locations of the instances before and after swapping.
	 */
	public void swapTwoRandomComplexBlocks(boolean debug) {
		Random rnd = new Random();
		/**An integer indicating the size of the blocks list.*/
		int blockListSize = this.blocks.size();
		/**Generating a random integer, in order to chose the first random ComplexBlock (Tile) for swapping.*/
		int rndCbA = rnd.nextInt(blockListSize);
		int rndCbB;
		/**Pick the first random tile for swapping.*/
		ComplexBlock cblA = this.blocks.get(rndCbA);
		ComplexBlock cblB;
		/**Try to find a second ComplexBlock (Tile) that has the same type of the first ComplexBlock (Tile) we randomly picked.*/
		do {
			/**Pick a second random ComplexBlock (Tile).*/
			rndCbB = rnd.nextInt(blockListSize);
			cblB = this.blocks.get(rndCbB);
			/**If this ComplexBlock(Tile) is not used pick another one. Maybe this is realy good to be done.*/
			//if(!cblB.used) continue;
		} while( (cblA.tileType != cblB.tileType) && (rndCbA != rndCbB));

		if(debug){
			/***************************************************************************************************************************************/
			System.out.println("cblA index = " + rndCbA);
			System.out.println("cblB index = " + rndCbB);
			
			System.out.println("---------------------------------------------------------------------------------------------------------------------" +
								   "\n--------------------------------- BEFORE SWAP  ----------------------------------------------------------------" +
								   "\n---------------------------------------------------------------------------------------------------------------");
			
			//Tile A
			System.out.println("---------------------------------------------------------------------------------------------------------------------" +
					   "\n-------------------------------- " + cblA.tileType + "_" + cblA.x + "Y" + cblA.y + " -------------------------------------" +
					   "\n---------------------------------------------------------------------------------------------------------------------------");
			for(Block blA : cblA.blocks)
			{
				System.out.println("Primitive site = " + blA.getSite().getName() + " Instance = " + design.getInstanceAtPrimitiveSite(blA.getSite()));
			}
			
			//Tile B
			System.out.println("---------------------------------------------------------------------------------------------------------------------" +
					   "\n-------------------------------- " + cblB.tileType + "_" + cblB.x + "Y" + cblB.y + " -------------------------------------" +
					   "\n---------------------------------------------------------------------------------------------------------------------------");
			for(Block blB : cblB.blocks)
			{
				System.out.println("Primitive site = " + blB.getSite().getName() + " Instance = " + design.getInstanceAtPrimitiveSite(blB.getSite()));
			}
			System.out.println("\n\n-----------------------------------------------------------------------------------------------------------------" +
					   "\n------------------------------------------------- SWAPPING ----------------------------------------------------------------" +
					   "\n---------------------------------------------------------------------------------------------------------------------------\n\n");
			
			/***************************************************************************************************************************************/
		}
		
		/**Swap the ComplexBlocks.*/
		swapComplexBlocks(cblA, cblB);
		/**Update the cost of all bounding boxes of the nets included at the complex boxes we swapped.*/
		updateNetsCost(this.blocks.get(rndCbA));
		updateNetsCost(this.blocks.get(rndCbB));
		/**Save the indexes of the random ComplexBlocks we swapped, in order to roll back the design when the accept criteria is not met from the
		 * simulated annnealing placer.*/
		this.oldRndCbA = rndCbA;
		this.oldRndCbB = rndCbB;
		
		if(debug){
			/***************************************************************************************************************************************/
		
			System.out.println("--------------------------------------------------------------------------------------------------------------------" +
								   "\n--------------------------------- AFTER SWAP  ----------------------------------------------------------------" +
								   "\n---------------------------------------------------------------------------------------------------------------");
			
			//Tile A
			System.out.println("---------------------------------------------------------------------------------------------------------------------" +
					   "\n--------------------------------- " + cblA.tileType + "_" + cblA.x + "Y" + cblA.y + " ------------------------------------" +
					   "\n---------------------------------------------------------------------------------------------------------------------------");
			for(Block blA : cblA.blocks)
			{
				System.out.println("Primitive site = " + blA.getSite().getName() + " Instance = " + design.getInstanceAtPrimitiveSite(blA.getSite()));
			}
		
			//Tile B
			System.out.println("---------------------------------------------------------------------------------------------------------------------" +
					   "\n------------------------------- " + cblB.tileType + "_" + cblB.x + "Y" + cblB.y + "  -------------------------------------" +
					   "\n---------------------------------------------------------------------------------------------------------------------------");
			for(Block blB : cblB.blocks)
			{
				System.out.println("Primitive site = " + blB.getSite().getName() + " Instance = " + design.getInstanceAtPrimitiveSite(blB.getSite()));
			}
			System.out.println("---------------------------------------------------------------------------------------------------------------------" +
					   "\n------------------------------------------------- Finished ----------------------------------------------------------------" +
					   "\n---------------------------------------------------------------------------------------------------------------------------\n\n");
			
			/***************************************************************************************************************************************/
		}		
	}

	/**
	 * Swaps the given tiles.
	 * @param cblA
	 * @param cblB
	 */
	private void swapComplexBlocks(ComplexBlock cblA, ComplexBlock cblB) {
		cblA.swap(cblB);
	}
	
	/**
	 * Restores the design to its last state, i.e before swapping the last two random complexBlocks (tiles).
	 */
	public void fallBack() {
		swapComplexBlocks(this.blocks.get(oldRndCbA), this.blocks.get(oldRndCbB));
		updateNetsCost(this.blocks.get(oldRndCbA));
		updateNetsCost(this.blocks.get(oldRndCbB));
	}
	
	/**
	 * A list with the used IOs of the design.
	 * @return Collection<Instance>
	 */
	public int getUsedIOs() {
		int ios = 0;
		Collection<Instance> instances = design.getInstances();
		for(Instance inst : instances) {
			if(inst.getType().toString().contains("IO")) {
				ios++;
			}
		}	
		return ios;
	}
	
	/**
	 * The number of used CLBs in the design.
	 * @return int
	 */
	public int getUsedCLBs() {
		Set<Tile> clbs = new HashSet<Tile>();
		Collection<Instance> instances = design.getInstances();
		for(Instance inst : instances) {
			if(inst.getTile().toString().contains("CLB")) {
				clbs.add(inst.getTile());
			}
		}
		return clbs.size();
	}
	
	
	/**
	 * A list with the used PrimitiveTypes.
	 * @return HashMap<PrimitiveType, Integer>
	 */
	public HashMap<PrimitiveType, Integer> getTypeFreq() {
		Set<PrimitiveType> types = new HashSet<PrimitiveType>();
		Collection<Instance> instances = design.getInstances();
		for(Instance inst : instances) {
			types.add(inst.getType());
		}
		HashMap<PrimitiveType, Integer> desTypes = new HashMap<PrimitiveType, Integer>();
		for (PrimitiveType type : types) {
			int freq = 0;
			for(Instance inst : instances) {
				if(inst.getType() == type) {
					freq ++;
				}
			}
			desTypes.put(type, freq);
		}
		return desTypes;
	}
	
	/**
	 * A flag designating if this instance has a carry chain.
	 * @param inst
	 * @return boolean
	 */
	public boolean hasCarry(Instance inst) {
		if(inst == null) return false;
		if(inst.getAttribute("CARRY4") == null) {
			//System.out.println("has a carry");
			return false;
		} else {
			return true;
		}
	} 
	
	/**
	 * Returns the number of CLBs that have a carry chain.
	 * @return int
	 */
	public int getCarryClbs() {
		boolean carry = false;
		int numOfCarries = 0; //how many CLBS have carry chain.
		ArrayList<Block> clbSlices;
		for(ComplexBlock cb : blocks) {
			carry = false;
			if( !(cb.tileType.toString().contains("CLB")) )continue; 
			clbSlices = cb.blocks;
			for(Block clbSlice : clbSlices) {
				if(clbSlice.getInstance() != null)
					if(hasCarry(clbSlice.getInstance()))
						carry = true;
			}
			if(carry) {
				numOfCarries++;
			}
		}
		return numOfCarries;
	}
	
	
	
	/**
	 * Returns the number of used slices.
	 * @return int
	 */
	public int getUsedSlices()
	{
		Collection<Instance> instances = design.getInstances();
		int slices = 0;
		for(Instance instance : instances) {
			if(instance.getType().toString().contains("SLICE")) slices++;
		}
		return slices;
	}
	
	/**
	 * Loads the complex blocks of the design in the block ArralList, except the tiles that contain instances with curry chains.
	 * @param debug while true, we output the tiles of the FPGA and their relying instances.
	 */
	public void loadCompexBlocks(boolean debug)
	{
		HashMap<String, Tile> tiles = design.getDevice().getTileMap();
		ComplexBlock cBlock = null;
		for(Tile t : tiles.values())
		{
				
			if(		(t.getType().toString().contains("IOI")  &&  t.getType() != TileType.IOI )   ) continue;
			if(		(t.getType().toString().contains("IOB") && design.getDevice().getFamilyType() == FamilyType.VIRTEX5)   ) continue;
			if		(t.getType() == TileType.INT)   continue;
			if( 	(t.getType().toString().contains("LIO")) ||
					(t.getType().toString().contains("RIO")) ||
					(t.getType().toString().contains("BIO")) ||
					(t.getType().toString().contains("CIO")) || 
					(t.getType().toString().contains("IOI")) 
					) 
			{
				cBlock = new ComplexBlock();
				Instance inst = null;
				Block block = null;	
				PrimitiveSite[] ioiSites = t.getPrimitiveSites();
				PrimitiveSite ioiSite;
				boolean add = true;
				for(int i = 0; i < ioiSites.length; i++)
				{	
					ioiSite = ioiSites[i];
					inst = design.getInstanceAtPrimitiveSite(ioiSite);		
					block = new Block(ioiSite);
					block.addInstance(inst);
					cBlock.add(block);
					/** Parse the name of the site to find its coordinates.*/
					PrimitiveSite iob = findIOB(t.getTileXCoordinate(), t.getTileYCoordinate(), ioiSite.getInstanceX(), ioiSite.getInstanceY());
					//System.out.println(iob);
					if( iob.toString().contains("_X") || (ucfList.contains(iob.toString().toUpperCase()) && ucf)) {
						add = false;
						System.out.println("Not adding " + iob.toString() + " to blocks list");
					} 
				
					if(iob != null)
					{
						inst = design.getInstanceAtPrimitiveSite(iob);
						block = new Block(iob);
						block.addInstance(inst);
						cBlock.add(block);
					}
				}
				if(add == true) {
					blocks.add(cBlock);
				}
			}
			else
			{
				boolean add = true;
				PrimitiveSite[] sites = t.getPrimitiveSites();
				PrimitiveSite site;
				if(sites != null) {	
					cBlock = new ComplexBlock();
					for(int i = 0; i < sites.length; i++) {
						site = sites[i];
						Instance inst = design.getInstanceAtPrimitiveSite(site);
						if(hasCarry(inst)) {
							add = false;
						}
						//if(inst != null) cBlock.used = true;
						cBlock.add(site, inst);
					}
					if(add == true) {
						blocks.add(cBlock);
					} else {
						
						System.out.println("Not adding\t" + cBlock.tileType + "_X" + cBlock.x +  "Y" + cBlock.y + " to blocks list")	;
					}
				}
			}
		}	
		
		for(ComplexBlock cb : blocks) {
			ArrayList<Block> blocksInCb = cb.blocks;
			for(Block bl : blocksInCb) {
				if(bl.getInstance() != null) {
					cb.used = true;
				}
			}
		}
			
		if(debug == true) {
			for(ComplexBlock cb : blocks) {
				System.out.println(cb.tileType.toString() + "_X" + cb.x + "Y" + cb.y);
				ArrayList<Block> blocksInCb = cb.blocks;
				for(Block bl : blocksInCb) {
					System.out.println(bl.getSite().toString());
					if(bl.getInstance() == null) System.out.println("Instance = null");
					else System.out.println(bl.getInstance().toString());
					System.out.println("*****************************");
				}
				System.out.println("***************************************************************");
			}
		}
	}
	
	/**
	 * Returns the correlated IODELAY or ILOGIC or OLOGIC PrimitiveSite of an IOB PrimitiveSite in order to be packed in one complex block.
	 * @param xTile
	 * @param yTile
	 * @param x
	 * @param y
	 * @return PrimitiveSite
	 */
	private PrimitiveSite findIOB(int xTile, int yTile, int x, int y)
	{
		HashMap<String, Tile> tiles = design.getDevice().getTileMap();

		for(Tile t : tiles.values())
		{
			if(t == null) continue;
			if(t.getPrimitiveSites() == null) continue;
			if(!(t.getType().toString().contains("IOB"))) continue;
			//System.out.println("Primitive sites = " + t.getPrimitiveSites().length);
			if((t.getTileXCoordinate() == xTile) && (t.getTileYCoordinate() == yTile))
			{
				if((y % 2 == 0))
				{
					
					if(t.getPrimitiveSites()[0] == null)
					{
						return t.getPrimitiveSites()[1];
					} 
					else 
					{
						return t.getPrimitiveSites()[0];
					}
				}
				else
				{
					if(t.getPrimitiveSites()[1] == null)
					{
						return t.getPrimitiveSites()[0];
					} 
					else 
					{
						return t.getPrimitiveSites()[1];
					}
				}
			} 
		}
		return null;
	}

	/**=========================================================================
	 * Class properties
	 *=========================================================================*/
	/**
	 * Sets the cost of the design.
	 * @param designCost
	 */
	public void set_cost(double designCost) {
		this.designCost = designCost;
	}
	
	/**
	 * Returns the cost of the design
	 * @return double
	 */
	public double get_cost() {
		return this.designCost;
	}
	
	/**
	 * Calculates and returns the cost of the design from scratch. More specific, it updates the cost of each net and returns the summary cost of all nets, i.e the design cost.
	 * @return double
	 */
	public double calculateCost()
	{
		calculate_nets_cost();
		return designCost;
	}
	
	/**
	 * Updates the cost of each net. 
	 */
	public void calculate_nets_cost()
	{
		designCost = 0;
		Collection<Net> nets = getDes().getNets();
		Iterator<Net> itn = nets.iterator();
		Net n;
		while(itn.hasNext()) {
			n = itn.next();
			ArrayList<Pin> pins = n.getPins();
			Iterator<Pin> itr = pins.iterator();
			Pin pin;// = itr.next();
			int terminals = pins.size();
			int[] x = new int[terminals];
			int[] y = new int[terminals];
			
			int i = 0;
			
			while(itr.hasNext()) {
				pin = itr.next();	
				x[i] = pin.getTile().getTileXCoordinate();
				y[i] = pin.getTile().getTileYCoordinate();
				i++;
			}
			
			if(x.length == 0) continue;
			int xmin=x[0]; //assume arr[0] is min value
			int xmax=x[0];//assume arr[0] is max value
			
			for(int j=0;j < terminals;j++) {
				if(x[j]<xmin) 
					xmin=x[j];
				if(x[j]>xmax)
					xmax=x[j];
			}
			
			int ymin=y[0]; //assume y[0] is min value
			int ymax=y[0];//assume y[0] is max value

			for(int j=0;j < terminals;j++) {
				if(y[j]<ymin) 
					ymin=y[j];
				if(y[j]>ymax)
					ymax=y[j];
			}
			
			/* Get the expected "crossing count" of a net, based on its number *
		     * of pins.  Extrapolate for very large nets.                      */
			double crossing = 0;
			double net_cost = 0;
		    if((terminals) > 50)
			{
			    crossing = 2.7933 + 0.02616 * (terminals - 50);
			    /*    crossing = 3.0;    Old value  */
			}
		    else
			{
			    crossing = Maths.getCrossCount(terminals -1);
			}
				
		    net_cost = crossing * Math.abs(xmax - xmin);
		    net_cost += crossing * Math.abs(ymax - ymin);
		    
		    this.designCost += net_cost;
		    
		    NetBB netbb = new NetBB();
		    netbb.setxMax(xmax);
		    netbb.setxMin(xmin);
		    netbb.setyMax(ymax);
		    netbb.setyMin(ymin);
		    netbb.setCost(net_cost);
		    this.netsBB.put(n.getName(),netbb);
		}
	}
	
	/**
	 * Gets a complex block and updates the cost of each net connected to it. 
	 * @param clb
	 */
	public void updateNetsCost(ComplexBlock clb)
	{
		HashSet<Net> nets = clb.getNets();
		double clbNetsCost_old = 0.0;
		double clbNetsCost_new = 0.0;
		Iterator<Net> itn = nets.iterator();
		Net n;
		while(itn.hasNext()) {
			n = itn.next();
			ArrayList<Pin> pins = n.getPins();
			Iterator<Pin> itr = pins.iterator();
			Pin pin;// = itr.next();
			int terminals = pins.size();
			int[] x = new int[terminals];
			int[] y = new int[terminals];
			
			int i = 0;
			
			while(itr.hasNext()) {
				pin = itr.next();	
				x[i] = pin.getTile().getTileXCoordinate();
				y[i] = pin.getTile().getTileYCoordinate();
				i++;
			}
			
			if(x.length == 0) continue;
			int xmin=x[0]; //assume arr[0] is min value
			int xmax=x[0];//assume arr[0] is max value
			
			for(int j=0;j < terminals;j++) {
				if(x[j]<xmin) 
					xmin=x[j];
				if(x[j]>xmax)
					xmax=x[j];
			}
			
			int ymin=y[0]; //assume y[0] is min value
			int ymax=y[0];//assume y[0] is max value

			for(int j=0;j < terminals;j++) {
				if(y[j]<ymin) 
					ymin=y[j];
				if(y[j]>ymax)
					ymax=y[j];
			}
			
			/* Get the expected "crossing count" of a net, based on its number *
		     * of pins.  Extrapolate for very large nets.                      */
			
			double crossing = 0;
			double net_cost = 0;	
		    if((terminals) > 50)
			{
			    crossing = 2.7933 + 0.02616 * (terminals - 50);
			    /*    crossing = 3.0;    Old value  */
			}
		    else
			{
			    crossing = Maths.getCrossCount(terminals - 1);
			}
				
		    net_cost = crossing * Math.abs(xmax - xmin);
		    net_cost += crossing * Math.abs(ymax - ymin);
		    
		    //net_cost  = crossing * Math.abs(xmax - xmin) * (1.0/100.0);
		    //net_cost += crossing * Math.abs(ymax - ymin) * (1.0/100.0);
		    
		    NetBB netbb = this.netsBB.get(n.getName());
		    clbNetsCost_old += netbb.getCost();
		    
		    netbb.setxMax(xmax);
		    netbb.setxMin(xmin);
		    netbb.setyMax(ymax);
		    netbb.setyMin(ymin);
		    netbb.setCost(net_cost);
		    
		    clbNetsCost_new += netbb.getCost();
		}
		
		this.designCost = this.designCost - clbNetsCost_old + clbNetsCost_new;
	}
	
	/**
	 * Returns the design.
	 * @return Design
	 */
	public static Design getDes() {
		return design;
	}

	/**
	 * Sets the design
	 * @param des
	 */
	public static void setDes(Design des) {
		Fpga.design = des;
	}
		
}

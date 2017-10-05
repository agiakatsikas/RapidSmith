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

/**
 * This NetBB class represents a bounding box of a net.
 * It has xmax, xmin, ymax, ymin dimensions. 
 * Its cost is the half perimeter of the bounding box that encapsulates all the attached logic blocks to the net.
 * @author Aitzan Sari, Agiakatsikas Dimitrios.
 * Created on: Oct 25, 2013
 */
public class NetBB {
	/*=========================================================================
	 * Class fields
	 *=========================================================================*/
	/**The maximum horizontal coordinate of the attached logic block to the net.*/
	private int xMax; 
	/**The minimum horizontal coordinate of the attached logic block to the net.*/
	private int xMin;
	/**The maximum vertical coordinate of the attached logic block to the net.*/
	private int	yMax;
	/**The minimum vertical coordinate of the attached logic block to the net.*/
	private int yMin;
	/**The half perimeter of the bounding box that encapsulates all the attached logic blocks to the net.*/
	private double cost;
	private String netName;
	private int terminals; 
	
	/*=========================================================================
	 * Class constructors
	 *=========================================================================*/
	/**Constructor which initializes all member data structures. Sets xmax, xmin, ymax, ymin and cost to zero.*/
	public NetBB()
	{
		this.setxMax(0);
		this.setxMin(0);
		this.setyMax(0);
		this.setyMin(0);
		this.setCost(0.0);
		this.setNetName("");
		this.setTerminals(0);
	}
	
	public NetBB(int xMax, int xMin, int yMax, int yMin) {
		this.setxMax(xMax);
		this.setxMin(xMin);
		this.setyMax(yMax);
		this.setyMin(yMin);
		this.setNetName("");
		this.setTerminals(0);
	}
		
	public NetBB(int xMax, int xMin, int yMax, int yMin, int terminals, String netName) {
		this.setxMax(xMax);
		this.setxMin(xMin);
		this.setyMax(yMax);
		this.setyMin(yMin);
		this.setNetName(netName);
		this.setTerminals(terminals);	
	}
	
	
	
	public NetBB(NetBB bbox) {
		this.setxMax(bbox.xMax);
		this.setxMin(bbox.xMin);
		this.setyMax(bbox.yMax);
		this.setyMin(bbox.yMin);
		this.setNetName(bbox.getNetName());
		this.setTerminals(bbox.getTerminals());
	}
		

	/*=========================================================================
	 * Class Properties
	 *=========================================================================*/
	/**
	 * Returns the maximum horizontal location of the attached logic block to the net.
	 * @return int
	 */
	public int getxMax() {
		return xMax;
	}

	/**
	 * Sets the maximum horizontal location of the attached logic block to the net.
	 * @param xMax
	 */
	public void setxMax(int xMax) {
		this.xMax = xMax;
	}

	/**
	 * Returns the minimum horizontal location of the attached logic block to the net.
	 * @return int
	 */
	public int getxMin() {
		return xMin;
	}

	/**
	 * Sets the minimum horizontal location of the attached logic block to the net.
	 * @param xMin
	 */
	public void setxMin(int xMin) {
		this.xMin = xMin;
	}

	/**
	 * Returns the maximum vertical location of the attached logic block to the net.
	 * @return int
	 */
	public int getyMax() {
		return yMax;
	}

	/**
	 * Sets the maximum vertical location of the attached logic block to the net.
	 * @param yMax
	 */
	public void setyMax(int yMax) {
		this.yMax = yMax;
	}

	/**
	 * Returns the minimum vertical location of the attached logic block to the net.
	 * @return int
	 */
	public int getyMin() {
		return yMin;
	}

	/**
	 * Sets the minimum vertical location of the attached logic block to the net.
	 * @param yMin
	 */
	public void setyMin(int yMin) {
		this.yMin = yMin;
	}

	/**
	 * Returns the cost of the bounding box which is the half perimeter of the bounding box that encapsulates all the attached logic blocks to the net.
	 * @return double
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * Sets the cost of the bounding box which is the half perimeter of the bounding box that encapsulates all the attached logic blocks to the net.
	 * @param cost
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getNetName() {
		return netName;
	}

	public void setNetName(String netName) {
		this.netName = netName;
	}

	public int getTerminals() {
		return terminals;
	}

	public void setTerminals(int terminals) {
		this.terminals = terminals;
	}
		
}

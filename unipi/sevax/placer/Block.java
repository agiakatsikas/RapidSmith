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

import java.util.HashSet;

import edu.byu.ece.rapidSmith.design.Instance;
import edu.byu.ece.rapidSmith.design.Net;
import edu.byu.ece.rapidSmith.device.PrimitiveSite;
/**
 * This Block class represents a primitive site with an instance.
 * When a instance of the design is placed in this primitive site we initialize the primitive site (location, type) with the properties of the instance.   
 * @author Aitzan Sari, Dimitris Agiakatsikas.
 * Created on: Oct 25, 2013
 */
public class Block implements Cloneable {
	/**The primitive site of the placed instance.*/
	private  PrimitiveSite site;
	/**The Instance that that is placed in this block primitive site.*/
	private  Instance	   instance;
	
	/**
	 * Constructor which initializes all member data structures. Sets site and instance to null.
	 */
	public Block()
	{
		this.site = null;
		this.instance = null;
	}
	/**
	 * Creates a new Block and populates it with the given PrimitiveSite
	 * @param site
	 */
	public Block(PrimitiveSite site)
	{
		this.site = site;
		this.instance = null;
	}
	
	public Block Clone()
	{
		try {
            final Block result = (Block) super.clone();
            result.site 	= this.site;
            result.instance = this.instance;
            return result;
        } catch (final CloneNotSupportedException ex) {
            throw new AssertionError();
        }
	}
	
	/**
	 * Creates a new Block and populates it with the given PrimitiveSite and instance
	 * @param site
	 * @param inst
	 */
	public Block(PrimitiveSite site, Instance inst)
	{
		this.site = site;
		this.instance = inst;
	}

	/**
	 * Adds a new instance to the block.
	 * @param instance
	 */
	public void addInstance(Instance instance)
	{
		this.instance = instance;
	}
	
	
	/**
	 * Sets the instance within the block to null.
	 */
	public void removeInstance()
	{
		this.instance = null;
	}
	
	/**
	 * Returns a list with nets attached on the blocks instance.
	 * @return HashSet<Net>
	 */
	public HashSet<Net> getNetList()
	{
		return this.instance.getNetList();
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Block)) return false;
		if(this.getSite().getName().equals(((Block)o).getSite().getName())) return true;
		else return false;
	}
	
	/**
	 * Returns the PrimitiveSite of the block.
	 * @return PrimitiveSite
	 */
	public PrimitiveSite getSite()
	{
		return this.site;
	}
	
	/**
	 * Returns the instance of the block.
	 */
	public Instance getInstance()
	{
		return this.instance;
	}
	
	/**
	 * Sets the instance of the block
	 * @param instance
	 */
	public void setInstance(Instance instance)
	{
		this.instance = instance;
	}
	
	/**
	 * Creates a new instance and initializes its primitiveSite.
	 * @param slice_inst
	 */
	public void setSlice_inst(Instance slice_inst) {
		this.instance = new Instance();
		this.instance = slice_inst;
	}

}

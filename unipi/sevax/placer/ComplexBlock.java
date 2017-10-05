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
import java.util.HashSet;
import java.util.Iterator;

import edu.byu.ece.rapidSmith.design.Instance;
import edu.byu.ece.rapidSmith.design.Net;
import edu.byu.ece.rapidSmith.device.PrimitiveSite;
import edu.byu.ece.rapidSmith.device.TileType;

/**This ComplexBlock class represents a FPGA Tile.
 * A complex block houses primitive sites. 
 * Placement occurs by assigning an instance to a specific primitive site.
 * We have grouped the instances in a complex block in order to keep the packing immutable.
 * @author Aitzan Sari, Dimitris Agiakatsikas. 
 * Created on: Oct 25, 2013
 */
public class ComplexBlock implements Cloneable
{
	/**This is the list of blocks in the ComplexBlock. A block contains a primitive site with a instance.*/ 
	public ArrayList<Block> blocks;
	/** XDL Tile Type (INT,CLB,...)*/
	public TileType 		 tileType;
	/**The horizontal coordinates of the tile in the FPGA layout.*/
	public int				 x;
	/**The vertical coordinates of the tile in the FPGA layout.*/
	public int				 y;
	/**A boolean indicating if the Tile is used or not.*/
	public boolean          used;
	
	/**Constructor which initializes all member data structures. TileType is set to null.*/
	public ComplexBlock()
	{
		this.blocks = new ArrayList<Block>();
		this.tileType = null;
		this.x = -1;
		this.y = -1;
		this.used = false;
	}
	
	/**Constructor which clones the ComplexBlock.*/
	public ComplexBlock Clone()
	{
		try {
            final ComplexBlock result = (ComplexBlock) super.clone();
            result.blocks = new ArrayList<Block>();
            Iterator<Block> it = blocks.iterator();
            while(it.hasNext()) {
            	result.blocks.add(it.next().Clone());
            }
            result.tileType 	= this.tileType;
            result.x = this.x;
            result.y = this.y;
            result.used = this.used;
            return result;
        } catch (final CloneNotSupportedException ex) {
            throw new AssertionError();
        }
	}
	
	/**
	 * Replaces this ComplexBlock with the given ComplexBlock cb.
	 * @param cb
	 */
	public void swap(ComplexBlock cb) {
		ComplexBlock this_tmp = this.Clone();
		ComplexBlock cbToSwap = cb.Clone();
		this.place(cbToSwap.blocks);			
		cb.place(this_tmp.blocks);
	}
	
	/**
	 * Places a list of blocks in the ComplexBlock.
	 * @param blocks
	 */
	public void place(ArrayList<Block> blocks)
	{
		for(int i = 0; i < blocks.size(); i++)
		{
			this.blocks.get(i).setSlice_inst(blocks.get(i).getInstance());
			if(this.blocks.get(i).getInstance() != null) {
				this.blocks.get(i).getInstance().place(this.blocks.get(i).getSite());
			}
		}
	}
	
	/**
	 * Adds a block in the ComplexBlock.
	 * @param block
	 */
	public void add(Block block)
	{
		if(!blocks.contains(block)) 
		{
			this.blocks.add(block);
			this.tileType = block.getSite().getTile().getType();
			this.x = block.getSite().getTile().getTileXCoordinate();
			this.y = block.getSite().getTile().getTileYCoordinate();
		}
		
	}
	
	/**
	 * Adds the PrimitiveSite and the Instance (i.e a block) in the ComplexBlock.
	 * @param site
	 * @param inst
	 */
	public void add(PrimitiveSite site, Instance inst)
	{
		Block block = new Block(site, inst);
		if(!blocks.contains(block)) 
		{
			this.blocks.add(block);
			this.tileType = block.getSite().getTile().getType();
			this.x = block.getSite().getTile().getTileXCoordinate();
			this.y = block.getSite().getTile().getTileYCoordinate();
		}
	}
	
	/**
	 * Returns a list with the nets attached to this ComplexBlock.
	 * @return HashSet<Net>
	 */
	public HashSet<Net> getNets()
	{
		HashSet<Net> netList = new HashSet<Net>();
		Iterator<Block> it = blocks.iterator();
		Block bl;
		while(it.hasNext())
		{
			bl = it.next();
			if(bl.getInstance() == null) continue;
			netList.addAll(bl.getNetList());
		}
		return netList;
	}

	/**
	 * Returns the blocks contained in this ComplexBlock. 
	 * @return ArrayList<Block>
	 */
	public ArrayList<Block> getBlocks() {
		return this.blocks;
	}

	/**
	 * Returns the tile type of this ComplexBlock.
	 * @return TileType
	 */
	public TileType getTileType() {
		return this.tileType;
	}

}

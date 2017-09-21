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
package edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification;

import edu.byu.ece.rapidSmith.bitstreamTools.bitstream.DummySyncData;

/**
 * Configuration specification common between all 7 families. 
 */
public abstract class S7MaskConfigurationSpecification extends AbstractConfigurationSpecification {
	
    public S7MaskConfigurationSpecification() {
        _dummySyncData = DummySyncData.V5_V6_STANDARD_DUMMY_SYNC_DATA;
        _minorMask = S7_MINOR_MASK;
        _minorBitPos = S7_MINOR_BIT_POS;
        _columnMask = S7_COLUMN_MASK;
        _columnBitPos = S7_COLUMN_BIT_POS;
        _rowMask = S7_ROW_MASK;
        _rowBitPos = S7_ROW_BIT_POS;
        _topBottomMask = S7_TOP_BOTTOM_MASK;
        _topBottomBitPos = S7_TOP_BOTTOM_BIT_POS;
        _blockTypeMask = S7_BLOCK_TYPE_MASK;
        _blockTypeBitPos = S7_BLOCK_TYPE_BIT_POS;
    }
    
	public static final int S7_TOP_BOTTOM_BIT_POS = 22;
	public static final int S7_TOP_BOTTOM_MASK = 0x1 << S7_TOP_BOTTOM_BIT_POS;
	public static final int S7_BLOCK_TYPE_BIT_POS = 23;
	public static final int S7_BLOCK_TYPE_MASK = 0x7 << S7_BLOCK_TYPE_BIT_POS;
	public static final int S7_ROW_BIT_POS = 17;
	public static final int S7_ROW_MASK = 0x1F << S7_ROW_BIT_POS;
	public static final int S7_COLUMN_BIT_POS = 7;
	public static final int S7_COLUMN_MASK = 0x3FF << S7_COLUMN_BIT_POS;
	public static final int S7_MINOR_BIT_POS = 0;
	public static final int S7_MINOR_MASK = 0x7F << S7_MINOR_BIT_POS;
	
}


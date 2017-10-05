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
package unipi.sevax.utilities;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
/**This class is used for the creation of an excel file in order to output the performance of the simulated annealing placement.*/
public class WriteExcel {

	/*
	 * =========================================================================
	 * Class fields
	 * =========================================================================
	 */
	/**Bold times new roman fonts.*/
	private WritableCellFormat timesBold10;
	/**Times new roman fonts.*/
	private WritableCellFormat times10;
	/**File class for creating the excel file.*/
	private File file;
	/**Setting of the excel file*/
	private WorkbookSettings wbSettings;
	private WritableWorkbook workbook;
	/**We save the excel file to this path. */
	private String pathName;
	private CellView cv;
	/**Creating a new writable excelSheet.*/
	public static WritableSheet excelSheet;
	/*
	 * =========================================================================
	 * Class constructors
	 * =========================================================================
	 */
	public WriteExcel() {
		this.cv = new CellView();
		this.wbSettings = new WorkbookSettings();
	}

	/*
	 * =========================================================================
	 * Class public methods
	 * =========================================================================
	 */
	/**
	 * Saves the excel file.
	 */
	public void saveExcel() {
		try {
			workbook.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			workbook.close();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		}
	}
	
	/**
	 * Creates a new excel.
	 */
	public void createExcel() {
		
		file = new File(pathName);
		
		wbSettings.setLocale(new Locale("en", "EN"));
		try {
			workbook = Workbook.createWorkbook(file, wbSettings);
			workbook.createSheet("Report", 0);
			excelSheet = workbook.getSheet(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		}
		
	}
	
	/**
	 * Adds a string to the given column and row.
	 * @param column
	 * @param row
	 * @param s
	 */
	public void addCaption(int column, int row, String s) {
		Label label;
		label = new Label(column, row, s, timesBold10);
		try {
			excelSheet.addCell(label);
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		}
	}

	/**
	 * Adds an integer to the given column and row.
	 * @param column
	 * @param row
	 * @param integer
	 */
	public void addInt(int column, int row, int integer) {
		Number number;
		number = new Number(column, row, integer, times10);
		try {
			excelSheet.addCell(number);
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		}
	}
	
	/**
	 * Adds a double number to the given column and row.
	 * @param column
	 * @param row
	 * @param d
	 */
	public void addDouble(int column, int row, double d) {
		Number number;
		number = new Number(column, row, d, times10);
		try {
			excelSheet.addCell(number);
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		}
	}
	
	/**
	 * Adds a long number to the given column and row.
	 * @param column
	 * @param row
	 * @param l
	 */
	public void addLong(int column, int row, long l) {
		Number number;
		number = new Number(column, row, l, times10);
		try {
			excelSheet.addCell(number);
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		}
	}

	/**
	 * Adds a bold string to the given column and row.
	 * @param column
	 * @param row
	 * @param s
	 */
	public void addLabel(int column, int row, String s) {
		Label label;
		label = new Label(column, row, s, timesBold10);
		try {
			excelSheet.addCell(label);
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		}
	}

	/**
	 * Initializes the excel file.
	 */
	public void initialize(){
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
	    // Define the cell format
	    times10 = new WritableCellFormat(times10pt);
	    // Lets automatically wrap the cells
	    try {
			times10.setWrap(true);
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		}

	    // Create create a times bold font 
	    WritableFont times12ptBold = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false);
	    timesBold10 = new WritableCellFormat(times12ptBold);
	    // Lets automatically wrap the cells
	    try {
			timesBold10.setWrap(true);
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage().toString());
		}

		cv.setFormat(times10);
		//cv.setFormat(timesBoldUnderline);
		cv.setAutosize(true);

	}
	
	/*=========================================================================
	 * Class private methods
	 *=========================================================================*/
	
	
	/*=========================================================================
	 * Class properties
	 *=========================================================================*/
	
	/**
	 * Sets the path of the excel file.
	 * @param pathName
	 */
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	
	/**
	 * Sets the strings or the numbers fonts to bold.
	 * @param timesBold
	 */
	public void setBold(boolean timesBold) {
		if (timesBold) {
			cv.setFormat(timesBold10);
		} else {
			cv.setFormat(times10);
		}
		
	}

	
}
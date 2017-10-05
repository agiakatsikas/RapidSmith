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

import java.text.ParseException;
import java.util.HashMap;
import java.util.Random;
import unipi.sevax.utilities.Maths;
import unipi.sevax.utilities.Utilities;

import edu.byu.ece.rapidSmith.design.Design;
import edu.byu.ece.rapidSmith.device.PrimitiveType;

/**
 * A <code>Placer</code> implements the simulated annealing algorithm to place all the available resources of the FPGA device.
 * <br>At first step, it unplaces all the instances of the design and continues with the simulated annealing placement. 
 * <br>It can read a UCF file containing the IO that must be locked to specific locations.
 * <br>The placer supports the movement of all programmable resources except Slices with carry chains, which would not be moved during the placement phase.
 *  
 * @author Aitzan Sari, Agiakatsikas Dimitrios.
 * Created on: September 2013
 */
public class Placer {
	/************************************************************************
	********************  Global variables **********************************
	************************************************************************/
	/**The design imported for simulated annealing placement*/
	private static Design design; 
	/**The FPGA class that holds all the resources of the specific FPGA and the instances of the design*/
	public static Fpga  fpga;
	/**This factor is multiplied by the number of swaps attempted in every temperature.
	 * The larger value o movesMult the higher effort will be attempted for placing the design. The default value is 10 
	 */
	private int movesMult;
	/**This factor determines when the placement will terminate. It has a range between 0.005 and 0.05. 
	 * The value 0.005 will terminate the placement quicker. As value tends to 0.05 the placement takes more time
	 * giving slightly higher quality placement. The performance of the placement is not very sensitive to the factor e.  
	 */
	private double epsilon;
	/**The path of the xdlFile that is going to be loaded in the Design class.*/
	private String xdlFile;

	/************************************************************************
	********************  Constructors **************************************
	************************************************************************/
    /**
     * Constructor which initializes all member data structures. It 
     * sets the default values of movesMults=10 and epsilon=0.05 factors.
     */
	public Placer(){
		design = new Design();
		movesMult = 10;
		epsilon = 0.05;
	}
	
	/************************************************************************
	********************  Public Methods ***********************************
	************************************************************************/
	
	/**
	 * Loads the XDL design. It also loads the constraints file UCF.
	 * @param xdlFile The full path of the XDL file.
	 * @param ucfPath The full path of the UCF file.
	 */
	public void loadDesign(String xdlFile, String ucfPath) {
		design = Utilities.loadDesign(xdlFile);
		if(ucfPath == null) {
			fpga = new Fpga(design);
		} else {
			fpga = new Fpga(design, Utilities.loadUcf(ucfPath));
		}
	}
	
	/**
	 * Saves the design in XDL file format.
	 * @param xdlFile The full path of the XDL file that will be saved.
	 */
	public void saveXDL(String xdlFile) {
		this.xdlFile = xdlFile; 
		design.saveXDLFile(this.xdlFile, true);
		System.out.println("\nSaving placed file at location " + this.xdlFile); 
	}
	
	/**
	 * Saves the design in NCD file format.
	 */
	public void saveNCD() {
		Utilities.convertXdl2Ncd(this.xdlFile);
	}
	
	/**
	 * Returns the path of the NCD design
	 * @return The full name of the NCD file.
	 */
	public String getNcdName(){
		return xdlFile.replaceAll(".xdl",".ncd");
	}
	
	/**
	 * Starts the placement of the design.
	 */
	public void place() {
		System.out.println("\nPlacement Started...\n");
		HashMap<PrimitiveType, Integer> typeFreq = fpga.getTypeFreq();
		System.out.println("\nResources = " + typeFreq.toString());
		// Get the initial cost of the placed design (the design has been placed by the CAD placer eg. using ISE, VPR).
		double inputCost = fpga.calculateCost();
		long estimatedTime = 0;
		long startTempTime = 0;
		long startPlacementTime = 0;
		startPlacementTime = System.currentTimeMillis();
		int nBlocks = fpga.getUsedIOs() + fpga.getUsedCLBs();
		// Make a random placement of the FPGA resources.
		double cost = randomPlacement(100000);
		// Calculate the movements per temperature
		int movesPerTemp = (int) (movesMult * Math.pow(nBlocks,(4.0/3.0)));
		// Calculates the initial temperature of the annealing schedule
		double temp = initTemp(movesPerTemp, nBlocks);
		double initTemp = temp;
		// Get the NETs number of the design.
		int numNets = getDesign().getNets().size();
		// Initialize the parameters of the simulated annealing placer
		double      DeltaC  = 0F;
		int 		movesAccepted = 0;
		double 		alfa = 0F;
		double		gama = 0F;
		double 		r = 0F;
		Random 		rnd = new Random();
		double		tmpCost;
		double 		ee;
		do
		{
			startTempTime = System.currentTimeMillis();
			movesAccepted = 0;
			for(int i = 0; i < movesPerTemp; i++)
			{
				// swap two ComplexBloks (randomly)
				fpga.swapTwoRandomComplexBlocks(false);
				// Get the (new) cost of the design
				tmpCost = fpga.get_cost();
				// Calculate ΔT
				DeltaC =  (tmpCost - cost);
				// Get a random double for the algorithm to escape local minima in the cost function
				r = rnd.nextDouble();
				// Calculate the acceptance probability
				ee = Math.exp(-DeltaC/temp);
				// Check if the new movement produced a better cost.
				// If not, accept the movement depending on the acceptance probability.
				if(tmpCost > cost)
				{
					if(ee < r)
					{
						//System.out.println("Move not accepted!");
						fpga.fallBack();
					}
					else
					{
						//System.out.println("Move accepted!");
						cost = tmpCost;
						movesAccepted++;						
					}
				}
				else
				{
					//System.out.println("Move accepted!");
					cost = tmpCost;
					movesAccepted++;
				}
			}			
			
			// Calculate alpha
			alfa = ((double) movesAccepted / (double)movesPerTemp);
		
			// Get gama
			gama = Utilities.getGama(alfa);			
			
			// Calculate the new temperature
			temp = temp * gama;
		
			estimatedTime = System.currentTimeMillis() - startTempTime;

			System.out.print("T = " + String.format("%6.4f", temp) + "\t\tCost = " + String.format("%6.2f", cost) + "\t\tMoverPerTemp = " + movesPerTemp + "\t\tAccepted = " + String.format("%8d", movesAccepted)
					+ "\t\tAlfa = " + String.format("%1.4f", alfa) + "\t\tGama = " + gama);
			try
			{
				System.out.println("\t\tTime = " + Utilities.milliseconds2hms(estimatedTime));
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		} while(temp > epsilon * cost / numNets);
		

		estimatedTime = System.currentTimeMillis() - startPlacementTime;
		try
		{
			System.out.println("\nPlacement finished. Estimated placement time = " + Utilities.milliseconds2hms(estimatedTime));
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\n\nPlacement Results\n");
		System.out.printf("%-15.15s  %15.15s%n","Init temp", String.format("%6.2f", initTemp));
		System.out.printf("%-15.15s  %15.15s%n","Input Cost", String.format("%6.2f", inputCost));
		System.out.printf("%-15.15s  %15.15s%n","Output Cost", String.format("%6.2f", fpga.get_cost()));
		System.out.printf("%-15.15s  %15.15s%n","Efficiency", String.format("%3.1f", (inputCost / cost) * 100));
		System.out.printf("%-15.15s  %15.15s%n","m", movesMult);
		System.out.printf("%-15.15s  %15.15s%n","e", epsilon);
		System.out.printf("%-15.15s  %15.15s%n","Moves per temp", movesPerTemp);
		System.out.printf("%-15.15s  %15.15s%n","NBlocks", nBlocks);
		System.out.printf("%-15.15s  %15.15s%n","Carry chains", fpga.getCarryClbs());
		System.out.println("\nResources = " + typeFreq.toString());
		
		System.out.println();
		
		return;
	}
		
	/************************************************************************
	********************  Private Methods ***********************************
	************************************************************************/
	/**
	 * Swaps randomly the tiles of the design <code>numOfSwaps</code> times . Finally, it returns the current cost of the design.
	 * @param numOfSwaps The number of swaps to be made
	 * @return The cost of the design
	 */
	private double randomPlacement(int numOfSwaps)
	{
		for(int i = 0; i < numOfSwaps; i++)
		{
			fpga.swapTwoRandomComplexBlocks(false);
		}
		return fpga.calculateCost();
	}
	
	/**
	 * Calculates the initial temperature given the <code>movesPerTemp</code> and <code>nBlocks</code> parameters.
	 * @param movesPerTemp The number of swaps that will be attempted in every temperature. 
	 * @param nBlocks Is the total number of logic blocks (CLBs) and IOs of the design. 
	 * @return The temperature of the design
	 */
	private static double initTemp(int movesPerTemp, int nBlocks) { 
		int move_lim = Math.min(movesPerTemp, nBlocks);
		int swaps = move_lim;
		double sum_of_squares = 0;
		double av = 0;
		double[] costs = new double[move_lim];
		
		while(move_lim > 0)
		{
			move_lim--;
			costs[move_lim] = fpga.get_cost();
			fpga.swapTwoRandomComplexBlocks(false);
			av += costs[move_lim];
			sum_of_squares += costs[move_lim] * costs[move_lim];
		}
		av /= swaps;
		double sd = Maths.get_std_dev(swaps,sum_of_squares, av);
		return ( 20 * sd);
	}
	
	/************************************************************************
	********************  Public Properties *********************************
	************************************************************************/
	/**
	 * Returns the design
	 * @return Design
	 */
	public static Design getDesign() {
		return design;
	}
	
	/**
	 * Sets the moves multiplier
	 * @param movesMult the value of moves multiplier. 
	 */
	public void setMovesMult(int movesMult)
	{
		this.movesMult = movesMult;
		
	}

	/**
	 * Sets the e (epsilon) value
	 * @param epsilon The value of epsilon.
	 */
	public void setEpsilon(double epsilon)
	{
		this.epsilon = epsilon;		
	}



}

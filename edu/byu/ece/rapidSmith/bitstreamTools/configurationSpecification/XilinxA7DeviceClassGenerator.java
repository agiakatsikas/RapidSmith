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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class generates Java source files that describe Xilinx device specifications
 * for the ARTIX7 architecture.
 */
public class XilinxA7DeviceClassGenerator extends XilinxDeviceClassGenerator {

    protected static final String kFilePrefix = "o7aTOwq3";
    
    /**
     * Construct a new class generator and collect all of the data needed
     * to write Java source code for a part library. Data is collected
     * using the Xilinx partgen, xdl, and bitgen tools.
     */
    public XilinxA7DeviceClassGenerator(){
        super("artix7", S7MaskConfigurationSpecification.S7_ROW_MASK, S7MaskConfigurationSpecification.S7_ROW_BIT_POS, S7MaskConfigurationSpecification.S7_TOP_BOTTOM_MASK, S7MaskConfigurationSpecification.S7_TOP_BOTTOM_BIT_POS);
    }
    
    /**
     * Create the Java source code for a part library of the architecture associated
     * with the class generator instance.
     */
    public void createJavaSourceFile() {
        String famNumber = Character.toString(_architecture.charAt(_architecture.length()-1));
        String namePrefix = "A" + famNumber;
        String fileName = namePrefix + "PartLibrary.java";
            
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(fileName));
            createFileHeader(buf);
            buf.write("package edu.byu.ece.bitstreamTools.configurationSpecification;\n\n");
    
            buf.write("import java.util.ArrayList;\nimport java.util.Arrays;\n\n");
            
            buf.write("public class " + namePrefix + "PartLibrary extends PartLibrary {\n\n");
            
            // constructor
            buf.write("\tpublic " + namePrefix + "PartLibrary() {\n");
            buf.write("\t\tsuper();\n");
            buf.write("\t}\n\n");
    
            // add parts method
            buf.write("\tprotected void addParts() {\n");
            for (String currPartName : _partNames) {
                buf.write("\t\taddPart(new " + currPartName.toUpperCase() + "());\n");
            }
            buf.write("\t}\n\n");
            
            // define parts
            int i = 0;
            for (String currPartName : _partNames) {
    
                buf.write("\tclass " + currPartName.toUpperCase() +" extends A"+famNumber+"ConfigurationSpecification {\n\n" );

                buf.write("\t\tpublic " +currPartName.toUpperCase() + "() {\n");
                buf.write("\t\t\tsuper();\n");
                buf.write("\t\t\t_deviceName = \""+currPartName.toUpperCase()+"\";\n");
                buf.write("\t\t\t_deviceIDCode = \""+ _deviceIDCodes.get(i)+"\";\n");
    
                // Write out Packages
                buf.write("\t\t\t_validPackages = new String[] {");
                for(String pkg : _validPackages.get(i)){
                    buf.write("\""+ pkg + "\", ");
                }
                buf.write("};\n");
    
                // Write out Speed Grades
                buf.write("\t\t\t_validSpeedGrades = new String[] {");
                for(String speeds : _validSpeedGrades.get(i)){
                    buf.write("\""+ speeds + "\", ");
                }
                buf.write("};\n");
    
                buf.write("\t\t\t_topRows = " + _numTopRows.get(i) + ";\n");
                buf.write("\t\t\t_bottomRows = " + _numBottomRows.get(i) + ";\n");
                
                buf.write("\t\t\t_blockTypeLayouts = new ArrayList<BlockTypeInstance>(Arrays.asList(new BlockTypeInstance[] {\n");
                buf.write("\t\t\t\t\tnew BlockTypeInstance(LOGIC_INTERCONNECT_BLOCKTYPE, new BlockSubType[] {\n\t\t\t\t\t\t");
                for (BlockSubType blk : _logicLayouts.get(i)) {
                    buf.write(blk + ", ");
                }
                buf.write("\n\t\t\t\t\t}),\n");
                buf.write("\t\t\t\t\tnew BlockTypeInstance(BRAM_CONTENT_BLOCKTYPE, new BlockSubType[] {\n\t\t\t\t\t\t");
                for (BlockSubType blk : _bramContentLayouts.get(i)){
                    buf.write(blk + ", ");
                }
                buf.write("\n\t\t\t\t\t}),\n");
                buf.write("\t\t\t}));\n");
                
                buf.write("\t\t\t_overallColumnLayout = _blockTypeLayouts.get(0).getColumnLayout();\n");                

                buf.write("\t\t}\n");
                buf.write("\t}\n\n");
                i++;
            }
            
            buf.write("}\n");
            buf.flush();
    
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error writing file: " + fileName);
            System.exit(1);
        }
    
    }

    protected void getPartColumnTypes(){
        BufferedReader in;
        String line;
        String[] tokens;
        
        for (String partName : _partNames) {
            System.out.println("Generating/parsing .xdlrc for " + partName);
            // Generate XDLRC first
            generateBriefXDLRCFile(partName, _xdlrcFile);
            
            //int currNumRows = 0;
            List<BlockSubType> currLogicLayout = new ArrayList<BlockSubType>();
            List<BlockSubType> currBramContentLayout = new ArrayList<BlockSubType>();
            
            Map<Integer, BlockSubType> columnMap = new TreeMap<Integer, BlockSubType>();
            
            try {
                in = new BufferedReader(new FileReader(_xdlrcFile));
                line = in.readLine();
                while(line != null){
                    tokens = line.split("\\s+");
    
                    if(tokens.length > 1 && tokens[1].equals("(tile")){
                        if(tokens[1].equals("(tile")){
                            //currNumRows = Integer.parseInt(tokens[2]) / 88;
                        }
    
                        String name = tokens[5];
                        int column = Integer.parseInt(tokens[3]);
                        
                        /*if ( 	name.contains("LIOI")      || name.contains("LIOB")    ||     
                        		name.contains("RIOI")      || name.contains("RIOB")       ) {
                            columnMap.put(column, V7ConfigurationSpecification.IOB);
                        }*/
                        
                        if ( name.contains("LIOB33") ) {
                            columnMap.put(column, A7ConfigurationSpecification.IOB);
                        }
                        
                        else if (name.equals("CLBLM_L") || name.equals("CLBLM_R")  || name.equals("CLBLL_L") || name.equals("CLBLL_R") ) {
                            columnMap.put(column, A7ConfigurationSpecification.CLB);
                        }
                        else if ( name.equals("DSP_L") || name.equals("DSP_R") ) {
                            columnMap.put(column, A7ConfigurationSpecification.DSP);
                        }
                        /*else if (name.equals("CMT_TOP_R_UPPER_T") || name.equals("CMT_TOP_R_UPPER_B") || name.equals("CMT_TOP_R_LOWER_T")
                        		|| name.equals("CMT_TOP_R_LOWER_B") || name.equals("CMT_TOP_L_UPPER_T") || name.equals("CMT_TOP_R_LOWER_T")) {*/
                        else if (name.contains("CMT_TOP_L") || name.contains("CMT_TOP_R") ){
                            columnMap.put(column, A7ConfigurationSpecification.CLK);
                        }
                        else if (name.equals("CLK_BUFG_REBUF") ) {
                            columnMap.put(column, A7ConfigurationSpecification.CLK);
                        }
                        
                        else if (name.equals("CFG_CENTER_TOP") || name.equals("CFG_CENTER_MID")) {
                            columnMap.put(column, A7ConfigurationSpecification.CFG);
                        }
              
                        
                        else if (name.contains("GTP_CHANNEL") || name.equals("GTP_COMMON") ) {
                            columnMap.put(column, A7ConfigurationSpecification.GTP);
                        }
                        else if (name.equals("BRAM_L") || name.equals("BRAM_R") || name.equals("BRAM")) {
                            columnMap.put(column, A7ConfigurationSpecification.BRAMINTERCONNECT);
                        }
                           
                    }
                    line = in.readLine();
                }
                in.close();
    
                for (Integer key : columnMap.keySet()) {
                    BlockSubType subType = columnMap.get(key);
                    currLogicLayout.add(subType);
                    if (subType == A7ConfigurationSpecification.BRAMINTERCONNECT) {
                        currBramContentLayout.add(A7ConfigurationSpecification.BRAMCONTENT);
                    }
                }                
                
                currLogicLayout.add(A7ConfigurationSpecification.LOGIC_OVERHEAD);
                currBramContentLayout.add(A7ConfigurationSpecification.BRAMOVERHEAD);

                _logicLayouts.add(currLogicLayout);
                _bramContentLayouts.add(currBramContentLayout);
                
            } 
            catch (FileNotFoundException e) {
                e.printStackTrace();
                System.err.println("Error opening temporary file: " + _xdlrcFile.getAbsolutePath());
                System.exit(1);
            }
            catch (IOException e){
                e.printStackTrace();
                System.err.println("Error reading temporary file: " + _xdlrcFile.getAbsolutePath());
                System.exit(1);         
            }
    
        }
    }
    
    /**
     * Allows this class to run stand alone from the rest of the project
     * @param args The architectures for which to generate the java files (ie. virtex4, virtex5 ...)
     */
    public static void main(String args[]){
        XilinxA7DeviceClassGenerator gen = new XilinxA7DeviceClassGenerator();
        gen.createJavaSourceFile();
    }
}

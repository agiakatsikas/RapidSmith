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
package edu.byu.ece.rapidSmith.device;

/*
 * This file was generated by:
 *   class edu.byu.ece.rapidSmith.util.TileAndPrimitiveEnumerator
 * Generated on:
 *   Wed Mar 02 15:25:20 2011
 * The following Xilinx families are supported:
 *   kintex7 spartan2 spartan2e spartan3 spartan3a spartan3adsp spartan3e spartan6 virtex virtex2 virtex2p virtex4 virtex5 virtex6 virtex7 virtexe 
 */

/**
 * This enum enumerates all of the Primitive types of the following FPGA families: 
 *   kintex7 spartan2 spartan2e spartan3 spartan3a spartan3adsp spartan3e spartan6 virtex virtex2 virtex2p virtex4 virtex5 virtex6 virtex7 virtexe 
 */
public enum PrimitiveType{
	ADC,
	BLOCKRAM,
	BSCAN,
	BUFDS,
	BUFG,
	BUFGCTRL,
	BUFGMUX,
	BUFH,
	BUFHCE,
	BUFIO,
	BUFIO2,
	BUFIO2FB,
	BUFIO2FB_2CLK,
	BUFIO2_2CLK,
	BUFIODQS,
	BUFO,
	BUFPLL,
	BUFPLL_MCB,
	BUFR,
	CAPTURE,
	CFG_IO_ACCESS,
	CLK_N,
	CLK_P,
	CRC32,
	CRC64,
	DCI,
	DCIRESET,
	DCM,
	DCM_ADV,
	DCM_CLKGEN,
	DIFFM,
	DIFFMI,
	DIFFMI_NDT,
	DIFFMLR,
	DIFFMTB,
	DIFFMTB_USB,
	DIFFS,
	DIFFSI,
	DIFFSI_NDT,
	DIFFSLR,
	DIFFSTB,
	DIFFSTB_USB,
	DLL,
	DLLIOB,
	DNA_PORT,
	DPM,
	DSP48,
	DSP48A,
	DSP48A1,
	DSP48E,
	DSP48E1,
	EFUSE_USR,
	EMAC,
	EMPTYIOB,
	EPB,
	FIFO16,
	FIFO18E1,
	FIFO36E1,
	FIFO36_72_EXP,
	FIFO36_EXP,
	FRAME_ECC,
	GCLK,
	GCLKIOB,
	GLOBALSIG,
	GT,
	GT10,
	GT11,
	GT11CLK,
	GTHE1_QUAD,
	GTIPAD,
	GTOPAD,
	GTPA1_DUAL,
	GTP_DUAL,
	GTXE1,
	GTX_DUAL,
	IBUF,
	IBUFDS_GTHE1,
	IBUFDS_GTXE1,
	ICAP,
	IDELAYCTRL,
	ILOGIC,
	ILOGIC2,
	ILOGICE1,
	IOB,
	IOBLR,
	IOBM,
	IOBS,
	IOB_USB,
	IODELAY,
	IODELAY2,
	IODELAYE1,
	IODRP2,
	IODRP2_MCB,
	IPAD,
	ISERDES,
	ISERDES2,
	ISERDESE1,
	JTAGPPC,
	KEY_CLEAR,
	LOWCAPIOB,
	MCB,
	MMCM_ADV,
	MONITOR,
	MULT18X18,
	MULT18X18SIO,
	NOMCAPIOB,
	OCT_CALIBRATE,
	OLOGIC,
	OLOGIC2,
	OLOGICE1,
	OPAD,
	OSERDES,
	OSERDES2,
	OSERDESE1,
	PCIE,
	PCIE_2_0,
	PCIE_A1,
	PCIIOB,
	PCILOGIC,
	PCILOGICSE,
	PLL_ADV,
	PMCD,
	PMV,
	PMVBRAM,
	PMVIOB,
	POST_CRC_INTERNAL,
	PPC405,
	PPC405_ADV,
	PPC440,
	PPR_FRAME,
	RAMB16,
	RAMB16BWE,
	RAMB16BWER,
	RAMB18E1,
	RAMB18X2,
	RAMB18X2SDP,
	RAMB36E1,
	RAMB36SDP_EXP,
	RAMB36_EXP,
	RAMB8BWER,
	RAMBFIFO18,
	RAMBFIFO18_36,
	RAMBFIFO36,
	RAMBFIFO36E1,
	RESERVED_ANDOR,
	RESERVED_LL,
	SLAVE_SPI,
	SLICE,
	SLICEL,
	SLICEM,
	SLICEX,
	SPI_ACCESS,
	STARTUP,
	SUSPEND_SYNC,
	SYSMON,
	TBUF,
	TEMAC,
	TEMAC_SINGLE,
	TIEOFF,
	USR_ACCESS,
	VCC,
	BSCAN_JTAG_MONE2,
	BUFMRCE,
	GCLK_TEST_BUF,
	GTXE2_CHANNEL,
	GTXE2_COMMON,
	IBUFDS_GTE2,
	IDELAYE2,
	ILOGICE2,
	ILOGICE3,
	IN_FIFO,
	IOB18,
	IOB18M,
	IOB18S,
	IOB33,
	IOB33M,
	IOB33S,
	ISERDESE2,
	MMCME2_ADV,
	ODELAYE2,
	OLOGICE2,
	OLOGICE3,
	OSERDESE2,
	OUT_FIFO,
	PCIE_2_1,
	PHASER_IN,
	PHASER_IN_ADV,
	PHASER_IN_PHY,
	PHASER_OUT,
	PHASER_OUT_ADV,
	PHASER_OUT_PHY,
	PHASER_REF,
	PHY_CONTROL,
	PLLE2_ADV,
	XADC,
	AMS_ADC,
	AMS_DAC,
	BUFG_LB,
	DRP_AMS_ADC,
	DRP_AMS_DAC,
	GTHE2_CHANNEL,
	GTHE2_COMMON,
	GTPE2_CHANNEL,
	GTPE2_COMMON,
	GTZE2_OCTAL,
	IDELAYE2_FINEDELAY,
	IOPAD,
	MTBF2,
	ODELAYE2_FINEDELAY,
	PCIE_3_0,
	PMV2,
	PMV2_SVT,
	PS7;
}


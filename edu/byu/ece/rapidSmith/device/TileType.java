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
 * This enum enumerates all of the Tile types of the following FPGA families: 
 *   kintex7 spartan2 spartan2e spartan3 spartan3a spartan3adsp spartan3e spartan6 virtex virtex2 virtex2p virtex4 virtex5 virtex6 virtex7 virtexe 
 */
public enum TileType{
	BBSTERM,
	BBTERM,
	BCLKTERM012,
	BCLKTERM123,
	BCLKTERM2,
	BCLKTERM3,
	BGCLKVTERM,
	BGIGABIT,
	BGIGABIT10,
	BGIGABIT10_INT0,
	BGIGABIT10_INT1,
	BGIGABIT10_INT2,
	BGIGABIT10_INT3,
	BGIGABIT10_INT4,
	BGIGABIT10_INT5,
	BGIGABIT10_INT6,
	BGIGABIT10_INT7,
	BGIGABIT10_INT8,
	BGIGABIT10_INT_TERM,
	BGIGABIT10_IOI_TERM,
	BGIGABIT10_TERM,
	BGIGABIT_INT0,
	BGIGABIT_INT1,
	BGIGABIT_INT2,
	BGIGABIT_INT3,
	BGIGABIT_INT4,
	BGIGABIT_INT_TERM,
	BGIGABIT_IOI_TERM,
	BGIGABIT_TERM,
	BIBUFS,
	BIOB,
	BIOB_SINGLE,
	BIOB_SINGLE_ALT,
	BIOIB,
	BIOIS,
	BIOI_INNER,
	BIOI_INNER_UNUSED,
	BIOI_OUTER,
	BIOI_OUTER_UNUSED,
	BL_DLLIOB,
	BOT,
	BPPC_X0Y0_INT,
	BPPC_X1Y0_INT,
	BRAM,
	BRAM0,
	BRAM0_SMALL,
	BRAM0_SMALL_BOT,
	BRAM1,
	BRAM1_SMALL,
	BRAM2,
	BRAM2_DN_GCLKH_FEEDTHRU,
	BRAM2_DN_GCLKH_FEEDTHRUA,
	BRAM2_FEEDTHRU,
	BRAM2_FEEDTHRUH,
	BRAM2_FEEDTHRUH_50A,
	BRAM2_FEEDTHRU_BRK,
	BRAM2_GCLKH_FEEDTHRU,
	BRAM2_GCLKH_FEEDTHRUA,
	BRAM2_MID_GCLKH_FEEDTHRU,
	BRAM2_MID_GCLKH_FEEDTHRUA,
	BRAM2_SMALL,
	BRAM2_UP_GCLKH_FEEDTHRU,
	BRAM2_UP_GCLKH_FEEDTHRUA,
	BRAM3,
	BRAM3_SMALL,
	BRAM3_SMALL_BRK,
	BRAM3_SMALL_TOP,
	BRAMS2E_BOT_NOGCLK,
	BRAMS2E_CLKH,
	BRAMS2E_TOP_NOGCLK,
	BRAMSITE,
	BRAMSITE2,
	BRAMSITE2H,
	BRAMSITE2H_50A,
	BRAMSITE2_3M,
	BRAMSITE2_3M_BOT,
	BRAMSITE2_3M_BRK,
	BRAMSITE2_3M_TOP,
	BRAMSITE2_BOT,
	BRAMSITE2_BRK,
	BRAMSITE2_DN_GCLKH,
	BRAMSITE2_DUMMY,
	BRAMSITE2_GCLKH,
	BRAMSITE2_MID_GCLKH,
	BRAMSITE2_TOP,
	BRAMSITE2_UP_GCLKH,
	BRAMSITEH,
	BRAMSITE_GCLKH,
	BRAMSITE_IOIS,
	BRAMSITE_PPC_X0Y0_INT,
	BRAMSITE_PPC_X1Y0_INT,
	BRAMSITE_PTERMB,
	BRAMSITE_PTERMT,
	BRAMSITE_RPPC_X0Y0_INT,
	BRAMSITE_RPPC_X1Y0_INT,
	BRAM_BOT,
	BRAM_BOT_BTERM_L,
	BRAM_BOT_BTERM_R,
	BRAM_BOT_GCLK,
	BRAM_BOT_NOGCLK,
	BRAM_CLKH,
	BRAM_HCLK_FEEDTHRU,
	BRAM_HCLK_FEEDTHRU_FOLD,
	BRAM_HCLK_FEEDTHRU_INTER,
	BRAM_HCLK_FEEDTHRU_INTER_FOLD,
	BRAM_INTER_BTERM,
	BRAM_INTER_TTERM,
	BRAM_INT_INTERFACE_BOT,
	BRAM_INT_INTERFACE_TOP,
	BRAM_IOIS,
	BRAM_IOIS_NODCM,
	BRAM_REGH_FEEDTHRU,
	BRAM_REGH_FEEDTHRU_INTER,
	BRAM_TOP,
	BRAM_TOP_GCLK,
	BRAM_TOP_NOGCLK,
	BRAM_TOP_TTERM_L,
	BRAM_TOP_TTERM_R,
	BRKH,
	BRKH_BRAM,
	BRKH_BUFGCTRL,
	BRKH_B_TERM_INT,
	BRKH_CLB,
	BRKH_CMT,
	BRKH_DCM,
	BRKH_DSP,
	BRKH_DUMMY,
	BRKH_GT3,
	BRKH_GTH,
	BRKH_GTX,
	BRKH_GTX_EMP,
	BRKH_GTX_L,
	BRKH_GTX_LEFT,
	BRKH_INT,
	BRKH_INT_INTERFACE,
	BRKH_IOB,
	BRKH_IOI,
	BRKH_IOIS,
	BRKH_MGT,
	BRKH_MGT11CLK_L,
	BRKH_MGT11CLK_R,
	BRKH_MGT_R,
	BRKH_PPC,
	BRKH_T_TERM_INT,
	BRKH_VBRK,
	BR_DLLIOB,
	BTERM,
	BTERM010,
	BTERM012,
	BTERM1,
	BTERM123,
	BTERM1_MACC,
	BTERM2,
	BTERM2CLK,
	BTERM3,
	BTERM323,
	BTERM4,
	BTERM4CLK,
	BTERM4_BRAM2,
	BTERMCLK,
	BTERMCLKA,
	BTERMCLKB,
	BTTERM,
	B_TERM_DSP,
	B_TERM_INT,
	B_TERM_INT_D,
	CCM,
	CENTER,
	CENTER_SMALL,
	CENTER_SMALL_BRK,
	CENTER_SPACE1,
	CENTER_SPACE2,
	CENTER_SPACE_HCLK1,
	CENTER_SPACE_HCLK2,
	CFG_CENTER,
	CFG_CENTER_0,
	CFG_CENTER_1,
	CFG_CENTER_2,
	CFG_CENTER_3,
	CFG_HCLK_INTERFACE,
	CFG_PPC_DL_BUFS,
	CFG_PPC_R_VBRK,
	CFG_PPC_VBRK,
	CFG_VBRK,
	CFG_VBRK_FRAME,
	CIOB,
	CLB,
	CLBLL,
	CLBLM,
	CLB_BUFFER,
	CLB_EMP_BTERM,
	CLB_INT_BTERM,
	CLEXL,
	CLEXL_DUMMY,
	CLEXM,
	CLEXM_DUMMY,
	CLKB,
	CLKB_2DLL,
	CLKB_4DLL,
	CLKB_LL,
	CLKC,
	CLKC_50A,
	CLKC_LL,
	CLKH,
	CLKH_50A,
	CLKH_DCM_CC,
	CLKH_DCM_FT,
	CLKH_DCM_LL,
	CLKH_LL,
	CLKL,
	CLKLH_DCM,
	CLKLH_DCM_LL,
	CLKL_IOIS,
	CLKL_IOIS_50A,
	CLKL_IOIS_LL,
	CLKR,
	CLKRH_DCM,
	CLKRH_DCM_LL,
	CLKR_IOIS,
	CLKR_IOIS_50A,
	CLKR_IOIS_LL,
	CLKT,
	CLKT_2DLL,
	CLKT_4DLL,
	CLKT_LL,
	CLKV,
	CLKVC,
	CLKVCU1,
	CLKVCU2,
	CLKVC_LL,
	CLKVD1,
	CLKVD2,
	CLKVU1,
	CLKVU2,
	CLKV_DCM,
	CLKV_DCM_B,
	CLKV_DCM_LL,
	CLKV_DCM_T,
	CLKV_LL,
	CLKV_MC,
	CLK_BUFGCTRL_B,
	CLK_BUFGCTRL_T,
	CLK_BUFGMUX,
	CLK_CMT_BOT,
	CLK_CMT_BOT_MGT,
	CLK_CMT_TOP,
	CLK_CMT_TOP_MGT,
	CLK_HROW,
	CLK_HROW_MGT,
	CLK_IOB_B,
	CLK_IOB_T,
	CLK_MGT_BOT,
	CLK_MGT_BOT_MGT,
	CLK_MGT_TOP,
	CLK_MGT_TOP_MGT,
	CLK_TERM_B,
	CLK_TERM_BOT,
	CLK_TERM_T,
	CLK_TERM_TOP,
	CMT_BOT,
	CMT_BUFG_BOT,
	CMT_BUFG_TOP,
	CMT_CAP,
	CMT_DCM2_BOT,
	CMT_DCM2_TOP,
	CMT_DCM_BOT,
	CMT_DCM_TOP,
	CMT_HCLK_BOT25,
	CMT_PLL1_BOT,
	CMT_PLL2_BOT,
	CMT_PLL2_TOP,
	CMT_PLL3_BOT,
	CMT_PLL3_TOP,
	CMT_PLL_BOT,
	CMT_PLL_TOP,
	CMT_PMVA,
	CMT_PMVA_BELOW,
	CMT_PMVB,
	CMT_PMVB_BUF_ABOVE,
	CMT_PMVB_BUF_BELOW,
	CMT_TOP,
	CNR_BR_BTERM,
	CNR_BTERM,
	CNR_LBTERM,
	CNR_LTERM,
	CNR_LTTERM,
	CNR_RBTERM,
	CNR_RTERM,
	CNR_RTTERM,
	CNR_TL_LTERM,
	CNR_TR_RTERM,
	CNR_TR_TTERM,
	CNR_TTERM,
	COB_NO_TERM,
	COB_TERM_B,
	COB_TERM_T,
	DCM,
	DCMAUX_BL_CENTER,
	DCMAUX_TL_CENTER,
	DCM_BGAP,
	DCM_BL_CENTER,
	DCM_BL_TERM,
	DCM_BOT,
	DCM_BR_CENTER,
	DCM_H_BL_CENTER,
	DCM_H_BR_CENTER,
	DCM_H_BR_TERM,
	DCM_H_TL_CENTER,
	DCM_H_TR_CENTER,
	DCM_H_TR_TERM,
	DCM_SPLY,
	DCM_TERM,
	DCM_TERM_MULT,
	DCM_TERM_NOMEM,
	DCM_TL_CENTER,
	DCM_TL_TERM,
	DCM_TR_CENTER,
	DSP,
	DSP_BOT_BTERM_L,
	DSP_BOT_BTERM_R,
	DSP_CLB_HCLK_FEEDTHRU,
	DSP_CLB_HCLK_FEEDTHRU_FOLD,
	DSP_EMP_BOT,
	DSP_EMP_TEMP,
	DSP_EMP_TOP,
	DSP_HCLK_FEEDTHRU_TOP,
	DSP_HCLK_GCLK_FOLD,
	DSP_HCLK_GCLK_NOFOLD,
	DSP_INTER_TTERM,
	DSP_INT_BTERM,
	DSP_INT_EMP_BOT,
	DSP_INT_EMP_TOP,
	DSP_INT_HCLK_FEEDTHRU,
	DSP_INT_HCLK_FEEDTHRU_FOLD,
	DSP_INT_TTERM,
	DSP_TOP_TTERM_L,
	DSP_TOP_TTERM_R,
	EMAC,
	EMAC_DUMMY,
	EMAC_INT_INTERFACE,
	EMPTY,
	EMPTY0X2,
	EMPTY0X22,
	EMPTY0X3,
	EMPTY0X4,
	EMPTY0X64,
	EMPTY16X2,
	EMPTY16X4,
	EMPTY64X76,
	EMPTY80X22,
	EMPTY80X64,
	EMPTY_BIOI,
	EMPTY_BRAM,
	EMPTY_BRAM_LL,
	EMPTY_BRKH,
	EMPTY_BTERM,
	EMPTY_CCM,
	EMPTY_CFG_CENTER,
	EMPTY_CLB,
	EMPTY_CLB_BUFFER,
	EMPTY_CLKC,
	EMPTY_CLKC_LL,
	EMPTY_CLKC_LLTB,
	EMPTY_CLKH_LL,
	EMPTY_CLKL_IOIS_LL,
	EMPTY_CLKL_LL,
	EMPTY_CLKR_IOIS_LL,
	EMPTY_CLKR_LL,
	EMPTY_CLKV_DCM,
	EMPTY_CLK_BUFGCTRL,
	EMPTY_CLK_IOB,
	EMPTY_CNR,
	EMPTY_CORNER,
	EMPTY_DCM,
	EMPTY_DCM_TERM,
	EMPTY_DSP,
	EMPTY_EDGE_BRAM,
	EMPTY_EDGE_CONFIG,
	EMPTY_EDGE_DSP,
	EMPTY_EDGE_MGT,
	EMPTY_GCLKH,
	EMPTY_GCLKVML_LL,
	EMPTY_GCLKVMR_LL,
	EMPTY_HCLK,
	EMPTY_HCLK_MGT,
	EMPTY_IOIS,
	EMPTY_MACC,
	EMPTY_MGT,
	EMPTY_MONITOR,
	EMPTY_PPC,
	EMPTY_TIOI,
	EMPTY_TTERM,
	EMPTY_VBRK,
	EMP_LIOB,
	EMP_RIOB,
	GBRKB,
	GBRKC,
	GBRKT,
	GBRKV,
	GCLKB,
	GCLKC,
	GCLKH,
	GCLKHL_50A,
	GCLKHR_50A,
	GCLKH_PCI_CE_N,
	GCLKH_PCI_CE_S,
	GCLKH_PCI_CE_S_50A,
	GCLKT,
	GCLKV,
	GCLKVC,
	GCLKVM,
	GCLKVML,
	GCLKVMR,
	GCLKV_IOIS,
	GCLKV_IOISL,
	GCLKV_IOISR,
	GIGABIT10_IOI,
	GIGABIT_IOI,
	GT3,
	GTH_BOT,
	GTH_L_BOT,
	GTH_L_TOP,
	GTH_TOP,
	GTPDUAL_BOT,
	GTPDUAL_BOT_UNUSED,
	GTPDUAL_CLB_FEEDTHRU,
	GTPDUAL_DSP_FEEDTHRU,
	GTPDUAL_INT_FEEDTHRU,
	GTPDUAL_LEFT_CLB_FEEDTHRU,
	GTPDUAL_LEFT_DSP_FEEDTHRU,
	GTPDUAL_LEFT_INT_FEEDTHRU,
	GTPDUAL_TOP,
	GTPDUAL_TOP_UNUSED,
	GTP_INT_INTERFACE,
	GTX,
	GTX_DUMMY,
	GTX_INT_INTERFACE,
	GTX_LEFT,
	GTX_LEFT_INT_INTERFACE,
	GTX_L_TERM_INT,
	GT_INT_INTERFACE_TERM,
	GT_L_INT_INTERFACE,
	HCLK,
	HCLK_BRAM,
	HCLK_BRAM_FEEDTHRU,
	HCLK_BRAM_FEEDTHRU_FOLD,
	HCLK_BRAM_FX,
	HCLK_BRAM_MGT,
	HCLK_BRAM_MGT_LEFT,
	HCLK_CENTER,
	HCLK_CENTER_ABOVE_CFG,
	HCLK_CLB,
	HCLK_CLBLL,
	HCLK_CLBLM,
	HCLK_CLBLM_MGT,
	HCLK_CLBLM_MGT_LEFT,
	HCLK_CLB_XL_CLE,
	HCLK_CLB_XL_CLE_FOLD,
	HCLK_CLB_XL_INT,
	HCLK_CLB_XL_INT_FOLD,
	HCLK_CLB_XM_CLE,
	HCLK_CLB_XM_CLE_FOLD,
	HCLK_CLB_XM_INT,
	HCLK_CLB_XM_INT_FOLD,
	HCLK_CMT_BOT,
	HCLK_CMT_CMT,
	HCLK_CMT_CMT_MGT,
	HCLK_CMT_IOI,
	HCLK_CMT_TOP,
	HCLK_DCM,
	HCLK_DCMIOB,
	HCLK_DSP,
	HCLK_FT,
	HCLK_GT3,
	HCLK_GTH,
	HCLK_GTH_LEFT,
	HCLK_GTX,
	HCLK_GTX_DUMMY,
	HCLK_GTX_LEFT,
	HCLK_GT_EMP,
	HCLK_INNER_IOI,
	HCLK_INT_INTERFACE,
	HCLK_IOB,
	HCLK_IOBDCM,
	HCLK_IOB_CMT_BOT,
	HCLK_IOB_CMT_BOT_MGT,
	HCLK_IOB_CMT_MID,
	HCLK_IOB_CMT_MID_MGT,
	HCLK_IOB_CMT_TOP,
	HCLK_IOB_CMT_TOP_MGT,
	HCLK_IOI,
	HCLK_IOIL_BOT_DN,
	HCLK_IOIL_BOT_SPLIT,
	HCLK_IOIL_BOT_UP,
	HCLK_IOIL_EMP,
	HCLK_IOIL_INT,
	HCLK_IOIL_INT_FOLD,
	HCLK_IOIL_TOP_DN,
	HCLK_IOIL_TOP_SPLIT,
	HCLK_IOIL_TOP_UP,
	HCLK_IOIR_BOT_DN,
	HCLK_IOIR_BOT_SPLIT,
	HCLK_IOIR_BOT_UP,
	HCLK_IOIR_EMP,
	HCLK_IOIR_INT,
	HCLK_IOIR_INT_FOLD,
	HCLK_IOIR_TOP_DN,
	HCLK_IOIR_TOP_SPLIT,
	HCLK_IOIR_TOP_UP,
	HCLK_IOIS_DCI,
	HCLK_IOIS_LVDS,
	HCLK_IOI_BOTCEN,
	HCLK_IOI_BOTCEN_MGT,
	HCLK_IOI_CENTER,
	HCLK_IOI_CMT,
	HCLK_IOI_CMT_MGT,
	HCLK_IOI_LTERM,
	HCLK_IOI_LTERM_BOT25,
	HCLK_IOI_RTERM,
	HCLK_IOI_RTERM_BOT25,
	HCLK_IOI_TOPCEN,
	HCLK_IOI_TOPCEN_MGT,
	HCLK_LIOB,
	HCLK_MGT,
	HCLK_OUTER_IOI,
	HCLK_PCIE_BRAM,
	HCLK_PPC,
	HCLK_PPC_TERM,
	HCLK_QBUF_L,
	HCLK_QBUF_R,
	HCLK_TERM,
	HCLK_TERM_L,
	HCLK_TERM_R,
	HCLK_VBRK,
	HCLK_VBRK_R,
	HCLK_VFRAME,
	INT,
	INT_BRAM,
	INT_BRAM_BRK,
	INT_BRK,
	INT_BUFS_L,
	INT_BUFS_R,
	INT_BUFS_R_MON,
	INT_GCLK,
	INT_HCLK_BUFS,
	INT_INTERFACE,
	INT_INTERFACE_CARRY,
	INT_INTERFACE_IOI,
	INT_INTERFACE_IOI_DCMBOT,
	INT_INTERFACE_LTERM,
	INT_INTERFACE_REGC,
	INT_INTERFACE_RTERM,
	INT_INTERFACE_TERM,
	INT_LTERM,
	INT_RTERM,
	INT_SO,
	INT_SO_DCM0,
	INT_TERM,
	INT_TERM_BRK,
	IOI,
	IOIS_LC,
	IOIS_LC_L,
	IOIS_NC,
	IOIS_NC_L,
	IOI_BTERM,
	IOI_BTERM_BUFPLL,
	IOI_BTERM_CLB,
	IOI_BTERM_REGB,
	IOI_INT,
	IOI_LTERM,
	IOI_LTERM_LOWER_BOT,
	IOI_LTERM_LOWER_TOP,
	IOI_LTERM_UPPER_BOT,
	IOI_LTERM_UPPER_TOP,
	IOI_L_INT_INTERFACE,
	IOI_L_INT_INTERFACE_TERM,
	IOI_PCI_CE_LEFT,
	IOI_PCI_CE_RIGHT,
	IOI_RTERM,
	IOI_RTERM_LOWER_BOT,
	IOI_RTERM_LOWER_TOP,
	IOI_RTERM_UPPER_BOT,
	IOI_RTERM_UPPER_TOP,
	IOI_TTERM,
	IOI_TTERM_BUFPLL,
	IOI_TTERM_CLB,
	IOI_TTERM_REGT,
	LBPPC,
	LBRAM,
	LBRAMS2E_BOTP,
	LBRAMS2E_TOPP,
	LBRAM_BOTP,
	LBRAM_BOTS,
	LBRAM_BOTS_GCLK,
	LBRAM_TOPP,
	LBRAM_TOPS,
	LBRAM_TOPS_GCLK,
	LEFT,
	LEFT_PCI_BOT,
	LEFT_PCI_TOP,
	LEMPTY0X2,
	LEMPTY16X4,
	LIBUFS,
	LIBUFS_CLK_PCI,
	LIBUFS_PCI,
	LIOB,
	LIOB_FT,
	LIOB_MON,
	LIOB_PCI,
	LIOB_RDY,
	LIOI,
	LIOIS,
	LIOIS_BRK,
	LIOIS_CLK_PCI,
	LIOIS_CLK_PCI_BRK,
	LIOIS_PCI,
	LIOI_BRK,
	LIOI_INT,
	LIOI_INT_BRK,
	LL,
	LLPPC_X0Y0_INT,
	LLPPC_X1Y0_INT,
	LPPC_X0Y0_INT,
	LPPC_X1Y0_INT,
	LR,
	LR_BRKH,
	LR_CLKH,
	LR_GCLKH,
	LR_IOIS,
	LR_LOWER,
	LR_UPPER,
	LTERM,
	LTERM010,
	LTERM1,
	LTERM2,
	LTERM210,
	LTERM210_PCI,
	LTERM3,
	LTERM321,
	LTERM323,
	LTERM323_PCI,
	LTERM4,
	LTERM4B,
	LTERM4CLK,
	LTERMCLK,
	LTERMCLKA,
	L_TERM_INT,
	L_TERM_PPC,
	L_TERM_PPC_EXT,
	MACC0_SMALL,
	MACC0_SMALL_BOT,
	MACC1_SMALL,
	MACC2_FEEDTHRUH,
	MACC2_GCLKH_FEEDTHRUA,
	MACC2_SMALL,
	MACC3_SMALL,
	MACC3_SMALL_BRK,
	MACC3_SMALL_TOP,
	MACCSITE2,
	MACCSITE2_BOT,
	MACCSITE2_BRK,
	MACCSITE2_DUMMY,
	MACCSITE2_TOP,
	MBRAM,
	MBRAMS2E,
	MCB_CAP_CLKPN,
	MCB_CAP_INT,
	MCB_CAP_INT_BRK,
	MCB_CNR_TOP,
	MCB_DUMMY,
	MCB_HCLK,
	MCB_INT,
	MCB_INT_BOT,
	MCB_INT_BOT25,
	MCB_INT_DQI,
	MCB_INT_DUMMY,
	MCB_INT_ULDM,
	MCB_L,
	MCB_L_BOT,
	MCB_MUI0R,
	MCB_MUI0W,
	MCB_MUI1R,
	MCB_MUI1W,
	MCB_MUI2,
	MCB_MUI3,
	MCB_MUI4,
	MCB_MUI5,
	MCB_MUI_DUMMY,
	MCB_REGH,
	MGT_AL,
	MGT_AL_BOT,
	MGT_AL_MID,
	MGT_AR,
	MGT_AR_BOT,
	MGT_AR_MID,
	MGT_BL,
	MGT_BR,
	MGT_R,
	MK_B_IOIS,
	MK_CLKB,
	MK_CLKT,
	MK_T_IOIS,
	ML_BCLKTERM012,
	ML_BCLKTERM123,
	ML_BRAMSITE_IOIS,
	ML_BRAM_IOIS,
	ML_CLKB,
	ML_CLKH,
	ML_CLKT,
	ML_CNR_BTERM,
	ML_LR_CLKH,
	ML_TBS_IOIS,
	ML_TB_IOIS,
	ML_TCLKTERM210,
	ML_TTERM010,
	NULL,
	PB,
	PCIE,
	PCIE_B,
	PCIE_BRAM,
	PCIE_BRKH,
	PCIE_INT_INTERFACE,
	PCIE_INT_INTERFACE_L,
	PCIE_INT_INTERFACE_R,
	PCIE_T,
	PCIE_TOP,
	PCIE_TOP_CLB_FEEDTHRU,
	PCIE_TOP_INT_FEEDTHRU,
	PCIE_TOP_UNUSED,
	PPC_B,
	PPC_BR,
	PPC_BRAM_B,
	PPC_BRAM_T,
	PPC_B_PB,
	PPC_B_TERM,
	PPC_L_INT_INTERFACE,
	PPC_R,
	PPC_R_INT_INTERFACE,
	PPC_R_PB,
	PPC_R_PT,
	PPC_T,
	PPC_TR,
	PPC_T_PT,
	PPC_T_TERM,
	PT,
	PTERMB,
	PTERMBR,
	PTERMR,
	PTERMT,
	PTERMTR,
	RAMB_BOT,
	RAMB_BOT_BTERM,
	RAMB_TOP,
	RAMB_TOP_TTERM,
	RBPPC,
	RBRAM,
	RBRAMS2E_BOTP,
	RBRAMS2E_TOPP,
	RBRAM_BOTP,
	RBRAM_BOTS,
	RBRAM_BOTS_GCLK,
	RBRAM_TOPP,
	RBRAM_TOPS,
	RBRAM_TOPS_GCLK,
	REGC_CLE,
	REGC_INT,
	REGH_BRAM_FEEDTHRU,
	REGH_BRAM_FEEDTHRU_L_GCLK,
	REGH_BRAM_FEEDTHRU_R_GCLK,
	REGH_CLEXL_CLE,
	REGH_CLEXL_INT,
	REGH_CLEXL_INT_CLK,
	REGH_CLEXM_CLE,
	REGH_CLEXM_INT,
	REGH_CLEXM_INT_GCLKL,
	REGH_DSP_CLB,
	REGH_DSP_INT,
	REGH_DSP_L,
	REGH_DSP_L_NOCLK,
	REGH_DSP_R,
	REGH_IOI,
	REGH_IOI_BOT25,
	REGH_IOI_LTERM,
	REGH_IOI_RTERM,
	REGH_LIOI_INT,
	REGH_LIOI_INT_BOT25,
	REGH_RIOI,
	REGH_RIOI_BOT25,
	REGH_RIOI_INT,
	REGH_RIOI_INT_BOT25,
	REG_B,
	REG_B_BTERM,
	REG_C_CMT,
	REG_L,
	REG_LB,
	REG_R,
	REG_RB,
	REG_T,
	REG_T_TTERM,
	REG_V,
	REG_V_BRK,
	REG_V_BTERM,
	REG_V_HCLK,
	REG_V_HCLKBUF_BOT,
	REG_V_HCLKBUF_TOP,
	REG_V_HCLK_BOT25,
	REG_V_MEMB_BOT,
	REG_V_MEMB_TOP,
	REG_V_MIDBUF_BOT,
	REG_V_MIDBUF_TOP,
	REG_V_TTERM,
	REMPTY0X2,
	REMPTY16X4,
	RIBUFS,
	RIBUFS_BRK,
	RIBUFS_CLK_PCI,
	RIBUFS_CLK_PCI_BRK,
	RIBUFS_PCI,
	RIGHT,
	RIGHT_PCI_BOT,
	RIGHT_PCI_TOP,
	RIOB,
	RIOB_PCI,
	RIOB_RDY,
	RIOI,
	RIOIS,
	RIOIS_CLK_PCI,
	RIOIS_PCI,
	RIOI_BRK,
	RPPC_X0Y0_INT,
	RPPC_X1Y0_INT,
	RTERM,
	RTERM010,
	RTERM1,
	RTERM2,
	RTERM210,
	RTERM210_PCI,
	RTERM3,
	RTERM321,
	RTERM323,
	RTERM323_PCI,
	RTERM4,
	RTERM4B,
	RTERM4CLK,
	RTERM4CLKB,
	RTERMCLKA,
	RTERMCLKB,
	R_TERM_INT,
	R_TERM_INT_D,
	R_TERM_PPC,
	SITE_FEEDTHRU,
	SYS_MON,
	TBSTERM,
	TB_IOIS,
	TCLKTERM2,
	TCLKTERM210,
	TCLKTERM3,
	TCLKTERM321,
	TGCLKVTERM,
	TGIGABIT,
	TGIGABIT10,
	TGIGABIT10_INT0,
	TGIGABIT10_INT1,
	TGIGABIT10_INT2,
	TGIGABIT10_INT3,
	TGIGABIT10_INT4,
	TGIGABIT10_INT5,
	TGIGABIT10_INT6,
	TGIGABIT10_INT7,
	TGIGABIT10_INT8,
	TGIGABIT10_INT_TERM,
	TGIGABIT10_IOI_TERM,
	TGIGABIT10_TERM,
	TGIGABIT_INT0,
	TGIGABIT_INT1,
	TGIGABIT_INT2,
	TGIGABIT_INT3,
	TGIGABIT_INT4,
	TGIGABIT_INT_TERM,
	TGIGABIT_IOI_TERM,
	TGIGABIT_TERM,
	TIBUFS,
	TIOB,
	TIOB_SINGLE,
	TIOIB,
	TIOIS,
	TIOI_INNER,
	TIOI_INNER_UNUSED,
	TIOI_OUTER,
	TL_DLLIOB,
	TOP,
	TPPC_X0Y0_INT,
	TPPC_X1Y0_INT,
	TR_DLLIOB,
	TTERM,
	TTERM010,
	TTERM1,
	TTERM1_MACC,
	TTERM2,
	TTERM210,
	TTERM2CLK,
	TTERM3,
	TTERM321,
	TTERM323,
	TTERM4,
	TTERM4CLK,
	TTERM4_BRAM2,
	TTERMCLK,
	TTERMCLKA,
	T_TERM_DSP,
	T_TERM_INT,
	T_TERM_INT_D,
	UL,
	ULPPC_X0Y0_INT,
	ULPPC_X1Y0_INT,
	UR,
	UR_LOWER,
	UR_UPPER,
	VBRK,
	VFRAME,
	VFRAME_NOMON,
	BRAM_INT_INTERFACE_L,
	BRAM_INT_INTERFACE_R,
	BRAM_L,
	BRAM_L_FLY,
	BRAM_R,
	BRKH_CLK,
	BRKH_DSP_L,
	BRKH_DSP_R,
	BRKH_TERM_INT,
	B_TERM_INT_NOUTURN,
	B_TERM_INT_SLV,
	CFG_CENTER_BOT,
	CFG_CENTER_MID,
	CFG_CENTER_MID_SLAVE,
	CFG_CENTER_TOP,
	CFG_CENTER_TOP_SLAVE,
	CLBLL_L,
	CLBLL_R,
	CLBLM_L,
	CLBLM_R,
	CLK_BALI_REBUF,
	CLK_BUFG_BOT_R,
	CLK_BUFG_REBUF,
	CLK_BUFG_TOP_R,
	CLK_FEED,
	CLK_HROW_BOT_R,
	CLK_HROW_TOP_R,
	CLK_PMV,
	CLK_TERM,
	CMT_FIFO_L,
	CMT_FIFO_R,
	CMT_PMV,
	CMT_PMV_L,
	CMT_TOP_L_LOWER_B,
	CMT_TOP_L_LOWER_T,
	CMT_TOP_L_UPPER_B,
	CMT_TOP_L_UPPER_T,
	CMT_TOP_R_LOWER_B,
	CMT_TOP_R_LOWER_T,
	CMT_TOP_R_UPPER_B,
	CMT_TOP_R_UPPER_T,
	DSP_L,
	DSP_R,
	GTX_CHANNEL_0,
	GTX_CHANNEL_1,
	GTX_CHANNEL_2,
	GTX_CHANNEL_3,
	GTX_COMMON,
	GTX_INT_INTERFACE_L,
	HCLK_CMT,
	HCLK_CMT_L,
	HCLK_DSP_L,
	HCLK_DSP_R,
	HCLK_FEEDTHRU_1,
	HCLK_FEEDTHRU_2,
	HCLK_FIFO_L,
	HCLK_IOI3,
	HCLK_L,
	HCLK_L_BOT_UTURN,
	HCLK_L_SLV,
	HCLK_R,
	HCLK_R_BOT_UTURN,
	HCLK_R_SLV,
	HCLK_TERM_GTX,
	INT_FEEDTHRU_1,
	INT_FEEDTHRU_2,
	INT_INTERFACE_L,
	INT_INTERFACE_R,
	INT_L,
	INT_L_SLV,
	INT_L_SLV_FLY,
	INT_R,
	INT_R_SLV,
	INT_R_SLV_FLY,
	IO_INT_INTERFACE_L,
	IO_INT_INTERFACE_R,
	LIOB18,
	LIOB18_SING,
	LIOB33,
	LIOB33_SING,
	LIOI3,
	LIOI3_SING,
	LIOI3_TBYTESRC,
	LIOI3_TBYTETERM,
	LIOI_SING,
	LIOI_TBYTESRC,
	LIOI_TBYTETERM,
	MONITOR_BOT,
	MONITOR_BOT_FUJI2,
	MONITOR_BOT_SLAVE,
	MONITOR_MID,
	MONITOR_MID_FUJI2,
	MONITOR_TOP,
	MONITOR_TOP_FUJI2,
	PCIE_BOT,
	PCIE_BOT_LEFT,
	PCIE_INT_INTERFACE_LEFT_L,
	PCIE_NULL,
	PCIE_TOP_LEFT,
	RIOB18,
	RIOB18_SING,
	RIOI_SING,
	RIOI_TBYTESRC,
	RIOI_TBYTETERM,
	R_TERM_INT_GTX,
	TERM_CMT,
	T_TERM_INT_NOUTURN,
	T_TERM_INT_SLV,
	VBRK_EXT,
	BRKH_INT_PSS,
	B_TERM_INT_PSS,
	B_TERM_VBRK,
	CFG_SECURITY_BOT_PELE1,
	CFG_SECURITY_MID_PELE1,
	CFG_SECURITY_TOP_PELE1,
	CLK_MTBF2,
	CLK_PMV2,
	CLK_PMV2_SVT,
	CLK_PMVIOB,
	GTP_CHANNEL_0,
	GTP_CHANNEL_0_MID_LEFT,
	GTP_CHANNEL_0_MID_RIGHT,
	GTP_CHANNEL_1,
	GTP_CHANNEL_1_MID_LEFT,
	GTP_CHANNEL_1_MID_RIGHT,
	GTP_CHANNEL_2,
	GTP_CHANNEL_2_MID_LEFT,
	GTP_CHANNEL_2_MID_RIGHT,
	GTP_CHANNEL_3,
	GTP_CHANNEL_3_MID_LEFT,
	GTP_CHANNEL_3_MID_RIGHT,
	GTP_COMMON,
	GTP_COMMON_MID_LEFT,
	GTP_COMMON_MID_RIGHT,
	GTP_INT_INTERFACE_L,
	GTP_INT_INTERFACE_R,
	GTP_INT_INT_TERM_L,
	GTP_INT_INT_TERM_R,
	GTP_MID_CHANNEL_STUB,
	GTP_MID_COMMON_STUB,
	HCLK_FEEDTHRU_1_PELE,
	INT_INTERFACE_PSS_L,
	MONITOR_BOT_PELE1,
	MONITOR_MID_PELE1,
	MONITOR_TOP_PELE1,
	PSS0,
	PSS1,
	PSS2,
	PSS3,
	PSS4,
	RIOB33,
	RIOB33_SING,
	RIOI3,
	RIOI3_SING,
	RIOI3_TBYTESRC,
	RIOI3_TBYTETERM;
}


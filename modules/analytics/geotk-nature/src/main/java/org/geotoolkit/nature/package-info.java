/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */


/**
 * Algorithms related to the observations of natural phenomenons. Most algorithms in this package are
 * derived from empirical measurements; their results are <u>approximative</u>. Algorithms include:
 * <p>
 * <UL>
 *   <LI>Sea water properties (density, sound velocity, fusion temperature,
 *       <i>etc.</i>) computed from salinity, temperature and pression
 *      using algorithms published by UNESCO.</LI>
 *   <LI>Values relative to earth calendar, like tropical year length.</LI>
 *   <LI><i>etc.</i></LI>
 * </UL>
 * <p>
 * <TABLE BORDER="3" ALIGN="center" CELLPADDING="6" BGCOLOR="#FEF3D6" WIDTH="75%">
 * <TR>
 * <TD ALIGN="center" BGCOLOR="#FFD6AC"><font COLOR="#804040" SIZE="5" FACE="Arial Black"><STRONG>References</STRONG></FONT></TD>
 * </TR><TR>
 * <TD>
 *   <P><FONT SIZE="2">Alain Poisson, M.H. Gashoumi and Selim Morcos.</FONT><BR>
 *      <FONT SIZE="1">&nbsp;&nbsp;&nbsp;&nbsp;<U>Salinity and density of seawater: table for high salinities (42 to 50)</U>.<BR>
 *                     &nbsp;&nbsp;&nbsp;&nbsp;Unesco technical papers in marine science #62 (1991).</FONT></P>
 *   <P><FONT SIZE="2">Unesco<BR></FONT>
 *      <FONT SIZE="1">&nbsp;&nbsp;&nbsp;&nbsp;<U>International oceanographic tables</U>, volume 4.<BR>
 *                     &nbsp;&nbsp;&nbsp;&nbsp;Unesco technical papers in marine science #40 (1987).</FONT></P>
 *   <P><FONT SIZE="2">Franck J. Millero.<BR></FONT>
 *      <FONT SIZE="1">&nbsp;&nbsp;&nbsp;&nbsp;<I>Solubility of oxygen in sea water</I> in <U>Progress on oceanographic tables and standards 1983-1986</U>.<BR>
 *                     &nbsp;&nbsp;&nbsp;&nbsp;Unesco technical papers in marine science #50 (1986) pp.13-14.</FONT></P>
 *   <P><FONT SIZE="2">Unesco<BR></FONT>
 *      <FONT SIZE="1">&nbsp;&nbsp;&nbsp;&nbsp;<U>The international system of units (SI) in oceanography</U>.<BR>
 *                     &nbsp;&nbsp;&nbsp;&nbsp;Unesco technical papers in marine science #45 (1985).</FONT></P>
 *   <P><FONT SIZE="2">N.P. Fofonoff and R.C. Millard Jr.<BR></FONT>
 *      <FONT SIZE="1">&nbsp;&nbsp;&nbsp;&nbsp;<U>Algorithms for computation of fundamental properties of seawater</U>.<BR>
 *                     &nbsp;&nbsp;&nbsp;&nbsp;Unesco technical papers in marine science #44 (1983).</FONT></P>
 *   <HR>
 *      <FONT SIZE="2">Tropical year length is computed
 *      using formula by <strong>Laskar (1986)</strong>. Synodic month length is
 *      computed using formula by <strong>Chapront-Touze and Chapront (1988)</strong>.
 *      Those formulas was taken from the following adress:</FONT><BR>
 *      <UL>
 *        <LI><A HREF="http://webexhibits.org/calendars/year-astronomy.html">
 *            <FONT SIZE="2">http://webexhibits.org/calendars/year-astronomy.html</FONT></A></LI>
 *        <LI><A HREF="http://www.treasure-troves.com/astro/TropicalYear.html">
 *            <FONT SIZE="2">http://www.treasure-troves.com/astro/TropicalYear.html</FONT></A></LI>
 *      </UL>
 *    </TD>
 *  </TR>
 *  </TABLE>
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @since 2.0
 *
 * @version 3.00
 * @module
 */
package org.geotoolkit.nature;

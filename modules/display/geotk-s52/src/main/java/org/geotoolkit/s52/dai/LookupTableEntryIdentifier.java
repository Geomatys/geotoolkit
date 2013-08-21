/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.dai;

/**
 * Identifies a look-up table Entry module.
 *
 * @author Johann Sorel (Geomatys)
 */
public class LookupTableEntryIdentifier extends DAIField{

    /** A(2) : Module Identifier (Module Name):
     * presently a constant string = 'LU';
     * labels a module of 'look-up table'‑type. */
    public String MODN;
    /** I(5) : Record Identifier: continuous numbering where x is 00000 < x < 32768;
     * uniquely identifies an instruction‑module within the data‑transfer‑set. */
    public int RCID;
    /** A(3) : status of the module contents:
     * 'NIL' no change, used for new editions and editions */
    public String STAT;
    /** A(6) : Name of the addressed object Class */
    public String OBCL;
    /** A(1) : Addressed Object Type ‑
     * 'A' Area
     * 'L' Line
     * 'P' Point */
    public String FTYP;
    /** I(5) : Display Priority */
    public int DPRI;  		  	
    /** A(1) : Radar Priority -
     * 'O' presentation on top radar
     * 'S' presentation suppressed by radar */
    public String RPRI;
    /** A(1/15) : Name of the addressed Look Up Table Set -
     * 'PLAIN_BOUNDARIES' or 'SYMBOLIZED_BOUNDARIES' (areas)
     * 'SIMPLIFIED' or 'PAPER_CHART' (points) and 'LINES' (lines) */
    public String TNAM;

    public LookupTableEntryIdentifier() {
        super("LUPT");
    }

    @Override
    protected void readSubFields(String str) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

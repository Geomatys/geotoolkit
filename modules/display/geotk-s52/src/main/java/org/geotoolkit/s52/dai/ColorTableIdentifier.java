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
 * Color table identifier.
 * Identifies a color‑table.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ColorTableIdentifier extends DAIField{

    /** A(2) : Module Name:
     * constant string ='CS';
     * marks a module of the'Colour Scheme'‑type */
    public String MODN;
    /** I(5) : Record Identifier :
     * continuous numbering where x is 00000 < x < 32768;
     * uniquely identifies a Colour‑Table‑Module within the transfer‑data‑set.*/
    public int RCID;
    /** A(3) : status of the module contents:
     * 'NIL' no change, used for new editions and editions */
    public String STAT;
    /** A(1/15) : Name of the addressed Color Table; valid keywords are:
     * 'DAY_BRIGHT';'DAY_WHITEBACK';'DAY_BLACKBACK';'DUSK';'NIGHT' */
    public String CTUS;

    public ColorTableIdentifier() {
        super("COLS");
    }

    @Override
    protected void readSubFields(String str) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

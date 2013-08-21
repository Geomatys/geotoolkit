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
 * Color Definition CIE.
 * Describes CIE‑System's colour‑definition
 *
 * @author Johann Sorel (Geomatys)
 */
public class ColorDefinitionCIE extends DAIField{

    /** A(5) : COLOUR (Color‑Token) */
    public String CTOK;
    /** R(1/15) : x-Coordinate (CIE‑System) */
    public double CHRX;
    /** R(1/15) : y‑Coordinate (CIE‑System */
    public double CHRY;
    /** R(1/15) : Luminance  (CIE‑System) */
    public double CLUM;
    /** A(1/15) : Use of color (free text) */
    public String CUSE;

    public ColorDefinitionCIE() {
        super("CCIE");
    }

    @Override
    protected void readSubFields(String str) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

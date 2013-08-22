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

import java.util.LinkedHashMap;
import java.util.Map;

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
    public Map<String, Object> getSubFields() {
        final Map<String,Object> map = new LinkedHashMap<>();
        map.put("CTOK", CTOK);
        map.put("CHRX", CHRX);
        map.put("CHRY", CHRY);
        map.put("CLUM", CLUM);
        map.put("CUSE", CUSE);
        return map;
    }

    @Override
    protected void readSubFields(String str) {
        final int[] offset = new int[1];
        CTOK = readStringBySize(str, offset, 5);
        CHRX = readDoubleByDelim(str, offset, DELIM_1F);
        CHRY = readDoubleByDelim(str, offset, DELIM_1F);
        CLUM = readDoubleByDelim(str, offset, DELIM_1F);
        CUSE = readStringByDelim(str, offset, DELIM_1F);
    }

}

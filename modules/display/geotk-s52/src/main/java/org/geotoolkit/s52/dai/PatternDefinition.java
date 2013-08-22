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
 * Pattern Definition.
 * defines several pattern‑parameters.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PatternDefinition extends DAIField{

    /** A(8) : name of the fill patern; */
    public String PANM;
    /** A(1) : type of pattern definition: V  Vector definition R  Raster definition */
    public String PADF;
    /** A(3) : type of the fill pattern: STG  staggered pattern LIN  linear  pattern */
    public String PATP;
    /** A(3) : pattern‑symbol spacing: CON  constant space SCL  scale dependent spacing */
    public String PASP;
    /** I(5) : minimum distance (units of 0.01 mm) between pattern symbols covers (bounding box + pivot point); where 0 <= PAMI <= 32767 */
    public int PAMI;
    /** I(5) : maximum distance (units of 0.01 mm) between pattern symbols covers(bounding box + pivot point); where 0 <= PAMA <= 32767; PAMA is meaningless if PASP = 'CON' */
    public int PAMA;
    /** I(5) : pivot‑point's column number; PACL is counted from the top, left corner of the vector/raster space to the right; ‑9999(left)<= PACL <= 32767(right) */
    public int PACL;
    /** I(5) : pivot‑point's row number; PARW is counted from the top, left corner of the vector/raster space to the bottom; ‑9999(top)<= PARW <= 32767(bottom) */
    public int PARW;
    /** I(5) : width of bounding box;where 1<= PAHL <=122 for raster and where 1<= PAHL <=32767 for vector Note:does not include vector line Width */
    public int PAHL;
    /** I(5) : height of bounding box; where 1<= PAVL <=122 for raster and where 1<= PAGL <=32767 for vector Note: does not include vector line width */
    public int PAVL;
    /** I(5) : bounding box upper left column number; where 0<= PBXC <=122 for raster and where 0<= PBXC <=32767 for vector */
    public int PBXC;
    /** I(5) : bounding box upper left row number; where 0<= PBXR <=122 for raster and where 0<= PBXR <=32767 for vector */
    public int PBXR;

    public PatternDefinition() {
        super("PATD");
    }

    @Override
    public Map<String, Object> getSubFields() {
        final Map<String,Object> map = new LinkedHashMap<>();
        map.put("PANM", PANM);
        map.put("PADF", PADF);
        map.put("PATP", PATP);
        map.put("PASP", PASP);
        map.put("PAMI", PAMI);
        map.put("PAMA", PAMA);
        map.put("PACL", PACL);
        map.put("PARW", PARW);
        map.put("PAHL", PAHL);
        map.put("PAVL", PAVL);
        map.put("PBXC", PBXC);
        map.put("PBXR", PBXR);
        return map;
    }

    @Override
    protected void readSubFields(String str) {
        final int[] offset = new int[1];
        PANM = readStringBySize(str, offset, 8);
        PADF = readStringBySize(str, offset, 1);
        PATP = readStringBySize(str, offset, 3);
        PASP = readStringBySize(str, offset, 3);
        PAMI = readIntBySize(str, offset, 5);
        PAMA = readIntBySize(str, offset, 5);
        PACL = readIntBySize(str, offset, 5);
        PARW = readIntBySize(str, offset, 5);
        PAHL = readIntBySize(str, offset, 5);
        PAVL = readIntBySize(str, offset, 5);
        PBXC = readIntBySize(str, offset, 5);
        PBXR = readIntBySize(str, offset, 5);
    }

}

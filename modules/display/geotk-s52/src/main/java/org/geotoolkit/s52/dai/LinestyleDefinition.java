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
 * Linestyle Definition.
 * Defines several linestyle‑parameters.
 *
 * @author Johann Sorel (Geomatys)
 */
public class LinestyleDefinition extends DAIField{

    /** A(8) : name of the linestyle; */
    public String LINM;
    /** I(5) : pivot-point's column‑number;
     * LICL is counted from the top,left corner of the vector space to the right;
     * ‑9999(left)<= LICL <= 32767(right) */
    public int LICL;
    /** I(5) : pivot-point's row‑number;
     * LIRW is counted from the top left corner of the vector space to the bottom;
     * ‑9999(top)<= LIRW <= 32767(bottom)*/
    public int LIRW;
    /** I(5) : width of bounding box;
     * where 1<= LIHL <=32767;
     * Note: does not include vector line width */
    public int LIHL;
    /** I(5) : height of bounding box;
     * where 1<= LIVL <=32767;
     * Note: does not include vector line width */
    public int LIVL;
    /** I(5) : bounding box upper left column number;
     * where 0<= LBXC <=32767; */
    public int LBXC;
    /** I(5) : bounding box upper left row number;
     * where 0<= LBXR <=32767; */
    public int LBXR;

    public LinestyleDefinition() {
        super("LIND");
    }

    @Override
    public Map<String, Object> getSubFields() {
        final Map<String,Object> map = new LinkedHashMap<>();
        map.put("LINM", LINM);
        map.put("LICL", LICL);
        map.put("LIRW", LIRW);
        map.put("LIHL", LIHL);
        map.put("LIVL", LIVL);
        map.put("LBXC", LBXC);
        map.put("LBXR", LBXR);
        return map;
    }

    @Override
    protected void readSubFields(String str) {
        final int[] offset = new int[1];
        LINM = readStringBySize(str, offset, 8);
        LICL = readIntBySize(str, offset, 5);
        LIRW = readIntBySize(str, offset, 5);
        LIHL = readIntBySize(str, offset, 5);
        LIVL = readIntBySize(str, offset, 5);
        LBXC = readIntBySize(str, offset, 5);
        LBXR = readIntBySize(str, offset, 5);
    }

}

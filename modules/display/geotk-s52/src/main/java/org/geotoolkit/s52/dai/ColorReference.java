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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the color reference for the bitmap or vector field.
 * @author Johann Sorel (Geomatys)
 */
public class ColorReference extends DAIField{

    /** *A(1) : Letter (ASCII >= 64) used as color index within
     * PBTM.BITR field or within the PBTM.VECT field. */
    public final List<String> CIDX = new ArrayList<>();
    /** A(5) : color token which is identified by the letter in CIDX. */
    public final List<String> CTOK = new ArrayList<>();

    public ColorReference(String code) {
        super(code);
    }

    @Override
    public Map<String, Object> getSubFields() {
        final Map<String,Object> map = new LinkedHashMap<>();
        map.put("CIDX", CIDX);
        map.put("CTOK", CTOK);
        return map;
    }

    @Override
    protected void readSubFields(String str) {
        final int[] offset = new int[1];
        while(str.length()-offset[0] >= 6){
            String cidx = readStringBySize(str, offset, 1);
            String ctok = readStringBySize(str, offset, 5);
            CIDX.add(cidx);
            CTOK.add(ctok);
        }
    }

}

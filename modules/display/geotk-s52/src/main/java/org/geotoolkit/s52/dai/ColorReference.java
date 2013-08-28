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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contains the color reference for the bitmap or vector field.
 * @author Johann Sorel (Geomatys)
 */
public class ColorReference extends DAIField{

    /** CIDX.
     * *A(1) : Letter (ASCII >= 64) used as color index within
     * PBTM.BITR field or within the PBTM.VECT field.
     * A(5) : color token which is identified by the letter in CIDX.
     */
    public final Map<String,String> colors = new HashMap<>();

    public ColorReference(String code) {
        super(code);
    }

    @Override
    public Map<String, Object> getSubFields() {
        final Map<String,Object> map = new LinkedHashMap<>();
        map.putAll(colors);
        return map;
    }

    @Override
    protected void readSubFields(String str) {
        final int[] offset = new int[1];
        while(str.length()-offset[0] >= 6){
            String cidx = readStringBySize(str, offset, 1);
            String ctok = readStringBySize(str, offset, 5);
            colors.put(cidx, ctok);
        }
    }

}

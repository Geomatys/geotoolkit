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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.geotoolkit.s52.dai.DAIField.DELIM_1F;

/**
 * Attribute Combination.
 * Describes individual characteristics of an object which lead to the
 * presentation described in theINST‑field.
 * The attributes of the object catalogue shall be used.
 *
 * @author Johann Sorel (Geomatys)
 */
public class AttributeCombination extends DAIField{

    /** ATTL.
     *  A(6) : 6 Character Attribute Code.
     *  ATTV : A(1/15) : Attribute Value; Shall be a valid value for the domain
     *         specified  by the attribute label in ATTL
     */
    public final Map<String,String> map = new HashMap<>();

    public AttributeCombination() {
        super("ATTC");
    }

    @Override
    public Map<String, Object> getSubFields() {
        final Map<String,Object> map = new LinkedHashMap<>();
        map.putAll(map);
        return map;
    }

    @Override
    protected void readSubFields(final String str) {
        final int[] offset = new int[1];
        while(str.length()-offset[0] >= 6){
            String attl = readStringBySize(str, offset, 6);
            String attv = readStringByDelim(str, offset, DELIM_1F);
            map.put(attl, attv);
        }

    }

}

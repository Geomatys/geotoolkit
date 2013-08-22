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
 * Symbol Identifier.
 * identifies a symbol‑module.
 *
 * @author Johann Sorel (Geomatys)
 */
public class SymbolIdentifier extends DAIField{

    /** A(2) : Module Identifier (Module Name): presently a constant string ='SY';
     * labels a module of the 'Symbol'‑type. */
    public String MODN;
    /** I(5) : Record Identifier: continuous numbering where x is 00000 < x < 32768;
     * uniquely identifies a symbol‑module within the data‑transfer‑set. */
    public int RCID;
    /** A(3) : status of the module contents:
     * 'NIL' no change, used for new editions and editions. */
    public String STAT;

    public SymbolIdentifier() {
        super("SYMB");
    }

    @Override
    public Map<String, Object> getSubFields() {
        final Map<String,Object> map = new LinkedHashMap<>();
        map.put("MODN", MODN);
        map.put("RCID", RCID);
        map.put("STAT", STAT);
        return map;
    }

    @Override
    protected void readSubFields(String str) {
        final int[] offset = new int[1];
        MODN = readStringBySize(str, offset, 2);
        RCID = readIntBySize(str, offset, 5);
        STAT = readStringBySize(str, offset, 3);
    }

}

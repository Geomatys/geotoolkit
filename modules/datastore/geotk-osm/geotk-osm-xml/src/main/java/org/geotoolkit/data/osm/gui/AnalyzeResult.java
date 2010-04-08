/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.osm.gui;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class AnalyzeResult {

    private static String OVER_1000 = " >> Others, over 1000 distinct values << ";

    public final String tableName;
    public final String tagKey;
    public int tagCount = 0;
    public final Map<String, Integer> values = new HashMap<String, Integer>();

    public AnalyzeResult(String tableName, String key) {
        this.tableName = tableName;
        this.tagKey = key;
    }

    public void incrementValue(String value) {
        if(values.size() > 1000){
            value = OVER_1000;
        }

        Integer count = values.get(value);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        values.put(value, count);
    }
}

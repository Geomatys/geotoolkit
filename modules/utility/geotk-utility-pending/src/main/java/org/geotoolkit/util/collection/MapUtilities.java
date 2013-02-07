/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.util.collection;

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.util.ArgumentChecks;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public final class MapUtilities {
    
    private MapUtilities() {
        
    }
    
    /**
     * Build a map using values in parameters, which must be grouped by key/value couple.
     * Example : buildMap(key1, value1, key2, value2...)
     * @param couples the values to put 
     * @return 
     */
    public static <T extends Object> Map buildMap(T ... couples) {
        ArgumentChecks.ensureNonNull("map values", couples);
        if (couples.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of values (not divisible per 2)");
        } 
        final Map result = new HashMap(couples.length/2);
        for(int i = 0 ; i < couples.length ;) {
            result.put(couples[i++], couples[i++]);
        }
        return result;
    } 
}

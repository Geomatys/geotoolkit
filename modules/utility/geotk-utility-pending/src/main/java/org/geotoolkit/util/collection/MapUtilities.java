/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

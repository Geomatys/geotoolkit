/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.googlemaps;

import org.junit.Test;
import org.opengis.geometry.DirectPosition;

import static org.junit.Assert.*;
import static org.geotoolkit.googlemaps.model.GoogleMapsPyramidSet.*;

/**
 * Testing class for GetMap requests.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GoogleUtilitiesTest {
    
    private final double DELTA = 0.0000001;
    
    /**
     * Ensures the generate URL contain all parameters.
     */
    @Test
    public void testCenter() {
        DirectPosition position = null;
        
        
        //ZOOM LEVEL 0 test
        
        //must be the center of the pseudo-mercator projection
        position = getCenter(0, 0, 0);        
        assertEquals(MERCATOR_EXTEND.getMedian(0), position.getOrdinate(0), DELTA);
        assertEquals(MERCATOR_EXTEND.getMedian(1), position.getOrdinate(1), DELTA);
        
        //ZOOM LEVEL 1 test
        double n = MERCATOR_EXTEND.getMaximum(0) / 2;
        
        position = getCenter(1, 1, 1);
        assertEquals(n, position.getOrdinate(0), DELTA);
        assertEquals(-n, position.getOrdinate(1), DELTA);
        
        position = getCenter(1, 0, 1);
        assertEquals(-n, position.getOrdinate(0), DELTA);
        assertEquals(-n, position.getOrdinate(1), DELTA);
        
        position = getCenter(1, 1, 0);
        assertEquals(n, position.getOrdinate(0), DELTA);
        assertEquals(n, position.getOrdinate(1), DELTA);
        
        position = getCenter(1, 0, 0);
        assertEquals(-n, position.getOrdinate(0), DELTA);
        assertEquals(n, position.getOrdinate(1), DELTA);
        
        
        //ZOOM LEVEL 2 test
        double k = MERCATOR_EXTEND.getMaximum(0) / 4;
        
        position = getCenter(2, 0, 0);
        assertEquals(-(n+k), position.getOrdinate(0), DELTA);
        assertEquals((n+k), position.getOrdinate(1), DELTA);        
        position = getCenter(2, 1, 0);
        assertEquals(-k, position.getOrdinate(0), DELTA);
        assertEquals((n+k), position.getOrdinate(1), DELTA);        
        position = getCenter(2, 2, 0);
        assertEquals(k, position.getOrdinate(0), DELTA);
        assertEquals((n+k), position.getOrdinate(1), DELTA);        
        position = getCenter(2, 3, 0);
        assertEquals((n+k), position.getOrdinate(0), DELTA);
        assertEquals((n+k), position.getOrdinate(1), DELTA);
        
        position = getCenter(2, 0, 1);
        assertEquals(-(n+k), position.getOrdinate(0), DELTA);
        assertEquals(k, position.getOrdinate(1), DELTA);        
        position = getCenter(2, 1, 1);
        assertEquals(-k, position.getOrdinate(0), DELTA);
        assertEquals(k, position.getOrdinate(1), DELTA);        
        position = getCenter(2, 2, 1);
        assertEquals(k, position.getOrdinate(0), DELTA);
        assertEquals(k, position.getOrdinate(1), DELTA);        
        position = getCenter(2, 3, 1);
        assertEquals((n+k), position.getOrdinate(0), DELTA);
        assertEquals(k, position.getOrdinate(1), DELTA);
        
        position = getCenter(2, 0, 2);
        assertEquals(-(n+k), position.getOrdinate(0), DELTA);
        assertEquals(-k, position.getOrdinate(1), DELTA);        
        position = getCenter(2, 1, 2);
        assertEquals(-k, position.getOrdinate(0), DELTA);
        assertEquals(-k, position.getOrdinate(1), DELTA);        
        position = getCenter(2, 2, 2);
        assertEquals(k, position.getOrdinate(0), DELTA);
        assertEquals(-k, position.getOrdinate(1), DELTA);        
        position = getCenter(2, 3, 2);
        assertEquals((n+k), position.getOrdinate(0), DELTA);
        assertEquals(-k, position.getOrdinate(1), DELTA);
        
        position = getCenter(2, 0, 3);
        assertEquals(-(n+k), position.getOrdinate(0), DELTA);
        assertEquals(-(n+k), position.getOrdinate(1), DELTA);        
        position = getCenter(2, 1, 3);
        assertEquals(-k, position.getOrdinate(0), DELTA);
        assertEquals(-(n+k), position.getOrdinate(1), DELTA);        
        position = getCenter(2, 2, 3);
        assertEquals(k, position.getOrdinate(0), DELTA);
        assertEquals(-(n+k), position.getOrdinate(1), DELTA);        
        position = getCenter(2, 3, 3);
        assertEquals((n+k), position.getOrdinate(0), DELTA);
        assertEquals(-(n+k), position.getOrdinate(1), DELTA);
        
        
    }
        
    
}

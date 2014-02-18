/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.metadata.landsat;

import java.io.IOException;
import java.io.InputStream;
import org.geotoolkit.gui.swing.tree.Trees;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test reading and accessing landsat nodes.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LandSatTest {
    
    @Test
    public void readingTest() throws IOException{
        
        final InputStream stream = LandSatTest.class.getResourceAsStream("/org/geotoolkit/metadata/landsat/L71172058_05820030108_MTL.txt");
        final LandSatMetaNode landsat = LandSat.parseMetadata(stream);        
        LandSatMetaNode node;
        
        //System.out.println(Trees.toString(landsat));
        
        //test searching for group nodes
        node = landsat.search("L1_METADATA_FILE");
        assertNotNull(node);
        assertEquals("L1_METADATA_FILE", node.getKey());
        
        node = landsat.search("L1_METADATA_FILE","PRODUCT_METADATA");
        assertNotNull(node);
        assertEquals("PRODUCT_METADATA", node.getKey());
        
        //test searching an arbitrary value
        node = landsat.search("L1_METADATA_FILE","PRODUCT_METADATA","SENSOR_ID");
        assertNotNull(node);
        assertEquals("SENSOR_ID", node.getKey());
        assertEquals("ETM+", node.getValue());
                
        node = landsat.search("L1_METADATA_FILE","MIN_MAX_PIXEL_VALUE","QCALMAX_BAND62");
        assertNotNull(node);
        assertEquals("QCALMAX_BAND62", node.getKey());
        assertEquals("255.0", node.getValue());
        
        node = landsat.search("L1_METADATA_FILE","PROJECTION_PARAMETERS","REFERENCE_ELLIPSOID");
        assertNotNull(node);
        assertEquals("REFERENCE_ELLIPSOID", node.getKey());
        assertEquals("WGS84", node.getValue());
        
        
    }
    
}

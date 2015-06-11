/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.coverage.xmlstore;

import java.util.Set;
import org.geotoolkit.storage.coverage.CoverageStoreFactory;
import org.geotoolkit.storage.coverage.CoverageStoreFinder;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Tests for XMLCoverageStore
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XMLCoverageStoreFactoryTest {
    
    public XMLCoverageStoreFactoryTest() {
    }

    @Test
    public void testFactory() {
                
        final Set<CoverageStoreFactory> set = CoverageStoreFinder.getAllFactories(null);
        
        boolean found = false;
        for(CoverageStoreFactory fact : set){
            if(fact instanceof XMLCoverageStoreFactory){
                found = true;
            }
        }
        
        if(!found){
            fail("Factory not found");
        }
    }
}

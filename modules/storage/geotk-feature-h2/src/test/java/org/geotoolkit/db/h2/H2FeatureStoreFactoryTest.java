/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.db.h2;

import java.util.Iterator;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Factory tests.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class H2FeatureStoreFactoryTest {
    
    public H2FeatureStoreFactoryTest() {
    }

    @Test
    public void testFactory() {
        
        final Iterator<FeatureStoreFactory> ite = FeatureStoreFinder.getAllFactories(null).iterator();
        
        boolean found = false;
        while (ite.hasNext()){
            if(ite.next() instanceof H2FeatureStoreFactory){
                found = true;
            }
        }
        
        if(!found){
            fail("Factory not found");
        }
    }
}

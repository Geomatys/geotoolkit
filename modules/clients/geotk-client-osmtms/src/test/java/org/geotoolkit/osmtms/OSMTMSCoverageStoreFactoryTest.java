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
package org.geotoolkit.osmtms;

import java.util.Iterator;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.CoverageStoreFactory;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Coverage store tests.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OSMTMSCoverageStoreFactoryTest extends org.geotoolkit.test.TestBase {

    public OSMTMSCoverageStoreFactoryTest() {
    }

    @Test
    public void testFactory() {

        final Iterator<CoverageStoreFactory> ite = DataStores.getAllFactories(CoverageStoreFactory.class).iterator();
        boolean found = false;
        while (ite.hasNext()){
            if(ite.next() instanceof OSMTMSClientFactory){
                found = true;
            }
        }

        if(!found){
            fail("Factory not found");
        }
    }
}

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
package org.geotoolkit.coverage.filestore;

import java.util.Set;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.CoverageStoreFactory;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Tests for FileCoverageStore
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FileCoverageStoreFactoryTest extends org.geotoolkit.test.TestBase {

    public FileCoverageStoreFactoryTest() {
    }

    @Test
    public void testFactory() {

        final Set<CoverageStoreFactory> set = DataStores.getAllFactories(CoverageStoreFactory.class);
        boolean found = false;
        for(CoverageStoreFactory fact : set){
            if(fact instanceof FileCoverageStoreFactory){
                found = true;
            }
        }

        if(!found){
            fail("Factory not found");
        }
    }
}

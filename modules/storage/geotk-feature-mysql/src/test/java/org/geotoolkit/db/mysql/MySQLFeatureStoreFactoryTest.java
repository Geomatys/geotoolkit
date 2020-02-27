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
package org.geotoolkit.db.mysql;

import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStores;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Factory tests.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MySQLFeatureStoreFactoryTest extends org.geotoolkit.test.TestBase {

    public MySQLFeatureStoreFactoryTest() {
    }

    @Test
    public void testFactory() {
        boolean found = false;
        for (DataStoreProvider provider : DataStores.providers()){
            if (provider instanceof MySQLProvider) {
                found = true;
            }
        }

        if (!found) {
            fail("Factory not found");
        }
    }
}

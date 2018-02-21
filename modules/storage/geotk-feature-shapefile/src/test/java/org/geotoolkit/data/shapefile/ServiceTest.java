/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile;

import org.junit.Test;
import java.util.HashMap;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.ShapeTestData;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStores;

import static org.junit.Assert.*;

/**
 *
 * @version $Id$
 * @author ian
 * @module
 */
public class ServiceTest extends AbstractTestCaseSupport {

    final String TEST_FILE = "shapes/statepop.shp";

    /**
     * Make sure that the loading mechanism is working properly.
     */
    @Test
    public void testIsAvailable() {
        boolean found = false;
        for (DataStoreProvider provider : DataStores.providers()) {
            if (provider instanceof ShapefileFeatureStoreFactory) {
                found = true;
                assertNotNull(((ShapefileFeatureStoreFactory)provider).getDescription());
                break;
            }
        }
        assertTrue("ShapefileDataSourceFactory not registered", found);
    }

    /**
     * Ensure that we can open a featurestore using url OR string url.
     */
    @Test
    public void testShapefileDataStore() throws Exception {
        HashMap params = new HashMap();
        params.put("path", ShapeTestData.url(TEST_FILE).toURI());
        FeatureStore ds = (FeatureStore) org.geotoolkit.storage.DataStores.open(params);
        assertNotNull(ds);
        params.put("path", ShapeTestData.url(TEST_FILE).toURI().toString());
        assertNotNull(ds);
    }

    @Test
    public void testBadURL() {
        HashMap params = new HashMap();
        params.put("path", "aaa://bbb.ccc");
        try {
            ShapefileFeatureStoreFactory f = new ShapefileFeatureStoreFactory();
            f.open(params);
            fail("did not throw error");
        } catch (DataStoreException ioe) {
            // this is actually good
        }

    }

}

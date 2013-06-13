/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
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
package org.geotoolkit.data.postgis;

import java.io.IOException;

import static org.geotoolkit.data.postgis.PostgisNGFeatureStoreFactory.PORT;
import static org.geotoolkit.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotoolkit.jdbc.JDBCDataStoreFactory.IDENTIFIER;
import static org.geotoolkit.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotoolkit.jdbc.JDBCDataStoreFactory.PASSWORD;
import static org.geotoolkit.jdbc.JDBCDataStoreFactory.USER;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.geotoolkit.jdbc.JDBCFeatureStore;
import org.geotoolkit.jdbc.JDBCTestSetup;
import org.geotoolkit.jdbc.JDBCTestSupport;
import org.geotoolkit.storage.DataStoreException;


public class PostgisNGDataStoreFactoryTest extends JDBCTestSupport {

    @Override
    protected JDBCTestSetup createTestSetup() {
        return new PostGISTestSetup();
    }
    
    public void testCreateConnection() throws DataStoreException, IOException {
        PostgisNGFeatureStoreFactory factory = new PostgisNGFeatureStoreFactory();
        
        Properties db = new Properties();
        db.load(getClass().getResourceAsStream("factory.properties"));
        Map params = new HashMap();
        params.put(HOST.getName().toString(), db.getProperty(HOST.getName().toString()));
        params.put(DATABASE.getName().toString(), db.getProperty(DATABASE.getName().toString()));
        params.put(PORT.getName().toString(), Integer.valueOf(db.getProperty(PORT.getName().toString())));
        params.put(USER.getName().toString(), db.getProperty(USER.getName().toString()));
        params.put(PASSWORD.getName().toString(), db.getProperty(PASSWORD.getName().toString()));
        
        params.put(IDENTIFIER.getName().toString(), "postgis");

        assertTrue(factory.canProcess(params));
        JDBCFeatureStore store = (JDBCFeatureStore) factory.open(params);
        assertNotNull(store);
        try {
            // check dialect
            assertTrue(store.getDialect() instanceof PostGISDialect);
            // force connection usage
            assertNotNull(store.getFeatureType(tname("ft1")));
        } finally {
            store.dispose();
        }
    }

}

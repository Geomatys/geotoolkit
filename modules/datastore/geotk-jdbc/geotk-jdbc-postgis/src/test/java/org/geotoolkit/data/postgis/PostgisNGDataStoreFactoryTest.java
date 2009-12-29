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

import static org.geotoolkit.data.postgis.PostgisNGDataStoreFactory.PORT;
import static org.geotoolkit.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotoolkit.jdbc.JDBCDataStoreFactory.DBTYPE;
import static org.geotoolkit.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotoolkit.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotoolkit.jdbc.JDBCDataStoreFactory.USER;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.geotoolkit.jdbc.JDBCDataStore;
import org.geotoolkit.jdbc.JDBCTestSetup;
import org.geotoolkit.jdbc.JDBCTestSupport;


public class PostgisNGDataStoreFactoryTest extends JDBCTestSupport {

    @Override
    protected JDBCTestSetup createTestSetup() {
        return new PostGISTestSetup();
    }
    
    public void testCreateConnection() throws Exception {
        PostgisNGDataStoreFactory factory = new PostgisNGDataStoreFactory();
        
        Properties db = new Properties();
        db.load(getClass().getResourceAsStream("factory.properties"));
        Map params = new HashMap();
        params.put(HOST.getName().toString(), db.getProperty(HOST.getName().toString()));
        params.put(DATABASE.getName().toString(), db.getProperty(DATABASE.getName().toString()));
        params.put(PORT.getName().toString(), db.getProperty(PORT.getName().toString()));
        params.put(USER.getName().toString(), db.getProperty(USER.getName().toString()));
        params.put(PASSWD.getName().toString(), db.getProperty(PASSWD.getName().toString()));
        
        params.put(DBTYPE.getName().toString(), factory.getDatabaseID());

        assertTrue(factory.canProcess(params));
        JDBCDataStore store = (JDBCDataStore) factory.createDataStore(params);
        assertNotNull(store);
        try {
            // check dialect
            assertTrue(store.getDialect() instanceof PostGISDialect);
            // force connection usage
            assertNotNull(store.getSchema(tname("ft1")));
        } finally {
            store.dispose();
        }
    }

}

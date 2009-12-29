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
package org.geotoolkit.data.postgis.ps;

import java.io.IOException;
import java.util.Properties;

import org.geotoolkit.data.postgis.PostGISDialect;
import org.geotoolkit.data.postgis.PostGISPSDialect;
import org.geotoolkit.data.postgis.PostGISTestSetup;
import org.geotoolkit.jdbc.JDBCDataStore;


public class PostGISPSTestSetup extends PostGISTestSetup {

    @Override
    protected void setUpDataStore(JDBCDataStore dataStore) {
        super.setUpDataStore(dataStore);
        
        // for this test we need a PS based dialect
        PostGISPSDialect dialect = new PostGISPSDialect(dataStore, (PostGISDialect) dataStore.getDialect());
        dialect.setLooseBBOXEnabled(false);
        dataStore.setDialect(dialect);
    }

    @Override
    protected void fillConnectionProperties(Properties db) throws IOException {
        // override to use the same property file as the non ps path
        db.load( PostGISTestSetup.class.getResourceAsStream( "db.properties") );
    }
}

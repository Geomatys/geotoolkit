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
package org.geotoolkit.jdbc;

import org.geotoolkit.data.FeatureSource;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.feature.collection.FeatureCollection;
import org.geotoolkit.feature.collection.FeatureIterator;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;


public abstract class JDBCEmptyTest extends JDBCTestSupport {

    @Override
    protected abstract JDBCEmptyTestSetup createTestSetup();
    
    public void testFeatureSource() throws Exception {
        
        FeatureSource fs = dataStore.getFeatureSource( tname("empty") );
        assertNotNull(fs);
        
        JTSEnvelope2D bounds = fs.getBounds();
        assertTrue( bounds.isNull() );
        
        int count = fs.getCount( Query.ALL );
        assertEquals( 0, count );
    }
    
    public void testFeatureCollection() throws Exception {
        FeatureSource fs = dataStore.getFeatureSource( tname("empty") );
        FeatureCollection features = fs.getFeatures();
        
        assertTrue( features.getBounds().isNull() );
        assertEquals( 0, features.size() );
        
        FeatureIterator i = features.features();
        assertFalse( i.hasNext() );
        features.close( i );
        
    }

}

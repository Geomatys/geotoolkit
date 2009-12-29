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

import com.vividsolutions.jts.geom.Envelope;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;


public abstract class JDBCEmptyTest extends JDBCTestSupport {

    @Override
    protected abstract JDBCEmptyTestSetup createTestSetup();
    
    public void testFeatureSource() throws Exception {

        FeatureCollection fs = dataStore.createSession(false).features(QueryBuilder.all(nsname("empty")));
        assertNotNull(fs);
        
        JTSEnvelope2D bounds = (JTSEnvelope2D) fs.getEnvelope();
        assertTrue( bounds.isNull() );
        
        int count = fs.size();
        assertEquals( 0, count );
    }
    
    public void testFeatureCollection() throws Exception {
        FeatureCollection features = dataStore.createSession(false).features(QueryBuilder.all(nsname("empty")));
        
        assertTrue( ((Envelope)features.getEnvelope()).isNull() );
        assertEquals( 0, features.size() );
        
        FeatureIterator i = features.iterator();
        assertFalse( i.hasNext() );
        i.close();
        
    }

}

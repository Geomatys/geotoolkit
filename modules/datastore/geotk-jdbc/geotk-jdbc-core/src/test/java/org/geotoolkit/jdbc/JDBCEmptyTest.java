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
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;


public abstract class JDBCEmptyTest extends JDBCTestSupport {

    @Override
    protected abstract JDBCEmptyTestSetup createTestSetup();
    
    public void testFeatureSource() throws DataStoreException {

        FeatureCollection fs = dataStore.createSession(false).features(QueryBuilder.all(nsname("empty")));
        assertNotNull(fs);
        
        Envelope bounds = (Envelope) fs.getEnvelope();

        //todo should be null or at least isNull true
        assertTrue(bounds.getMinX() == 0);
        assertTrue(bounds.getMinY() == 0);
        assertTrue(bounds.getMaxX() == 0);
        assertTrue(bounds.getMaxY() == 0);
        
        int count = fs.size();
        assertEquals( 0, count );

        FeatureIterator i = fs.iterator();
        assertFalse( i.hasNext() );
        i.close();
    }
    
}

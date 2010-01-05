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

import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.query.Query;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.data.query.QueryBuilder;


public abstract class JDBCFeatureReaderTest extends JDBCTestSupport {

    public void testNext() throws Exception {
        Query query = QueryBuilder.all(dataStore.getFeatureType("ft1").getName());
        FeatureReader reader = dataStore.getFeatureReader( query );
        
        assertTrue( reader.hasNext() );
        SimpleFeature feature = (SimpleFeature) reader.next();
        
        Geometry g = (Geometry) feature.getDefaultGeometry();
        assertNotNull( g );
        
        assertTrue( g.getUserData() instanceof CoordinateReferenceSystem );
        reader.close();
    }

}

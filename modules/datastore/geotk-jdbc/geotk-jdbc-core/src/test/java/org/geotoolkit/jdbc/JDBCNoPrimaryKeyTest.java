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

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;


public abstract class JDBCNoPrimaryKeyTest extends JDBCTestSupport {

    protected static final String LAKE = "lake";
    protected static final String ID = "id";
    protected static final String NAME = "name";
    protected static final String GEOM = "geom";
    
    protected FilterFactory ff = FactoryFinder.getFilterFactory(null); 
    protected SimpleFeatureType lakeSchema;

    @Override
    protected abstract JDBCNoPrimaryKeyTestSetup createTestSetup();
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        lakeSchema = FeatureTypeUtilities.createType(dataStore.getNamespaceURI() + "." + LAKE,
                ID + ":0," + GEOM + ":Polygon," + NAME +":String");
    }
    
    public void testSchema() throws Exception {
        SimpleFeatureType ft =  (SimpleFeatureType) dataStore.getSchema(tname(LAKE));
        assertFeatureTypesEqual(lakeSchema, ft);
    }
    
    public void testReadFeatures() throws Exception {
        FeatureCollection fc = dataStore.createSession(false).getFeatureCollection(QueryBuilder.all(nsname(LAKE)));
        assertEquals(1, fc.size());
        FeatureIterator<SimpleFeature> fr = fc.iterator();
        assertTrue(fr.hasNext());
        SimpleFeature f = fr.next();
        assertFalse(fr.hasNext());
        fr.close();
    }
    
    public void testGetBounds() throws Exception {
        // GEOT-2067 Make sure it's possible to compute bounds out of a view
        JTSEnvelope2D reference = (JTSEnvelope2D) dataStore.getEnvelope(QueryBuilder.all(nsname(LAKE)));
        assertEquals(12.0, reference.getMinX());
        assertEquals(16.0, reference.getMaxX());
        assertEquals(4.0, reference.getMinY());
        assertEquals(8.0, reference.getMaxY());
    }
    
    /**
     * Subclasses may want to override this in case the database has a native way, other
     * than the pk, to identify a row
     * @throws Exception
     */
    public void testReadOnly() throws Exception {
        try { 
            dataStore.getFeatureWriter(nsname(LAKE),Filter.INCLUDE);
            fail("Should not be able to pick a writer without a pk");
        } catch(Exception e) {
            // ok, fine
        }

        assertFalse(dataStore.isWriteable(nsname(LAKE)));
    }
    

}

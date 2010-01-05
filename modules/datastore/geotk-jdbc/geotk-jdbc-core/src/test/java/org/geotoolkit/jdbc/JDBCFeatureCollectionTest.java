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

import java.io.IOException;
import java.util.Iterator;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;


public abstract class JDBCFeatureCollectionTest extends JDBCTestSupport {
    FeatureCollection<SimpleFeature> collection;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        collection = dataStore.createSession(false).getFeatureCollection(QueryBuilder.all(nsname("ft1")));
    }

    public void testIterator() throws Exception {
        FeatureIterator i = collection.iterator();
        assertNotNull(i);

        int base = -1;

        for (int x = 0; x < 3; x++) {
            assertTrue(i.hasNext());

            SimpleFeature feature = (SimpleFeature) i.next();
            assertNotNull(feature);

            String fid = feature.getID();
            int id = Integer.parseInt(fid.substring(fid.indexOf('.') + 1));

            if (base == -1) {
                base = id;
            }

            assertEquals(base++, id);
            assertEquals(x,((Number)feature.getAttribute(aname("intProperty"))).intValue() );
        }

        assertFalse(i.hasNext());
        i.close();
    }

    public void testBounds() throws Exception {
        JTSEnvelope2D bounds = (JTSEnvelope2D) collection.getEnvelope();
        assertNotNull(bounds);

        assertEquals(0d, bounds.getMinX(), 0.1);
        assertEquals(0d, bounds.getMinY(), 0.1);
        assertEquals(2d, bounds.getMaxX(), 0.1);
        assertEquals(2d, bounds.getMaxY(), 0.1);
    }

    public void testSize() throws IOException {
        assertEquals(3, collection.size());
    }

    public void testSubCollection() throws Exception {
        FilterFactory ff = dataStore.getFilterFactory();
        Filter f = ff.equals(ff.property(aname("intProperty")), ff.literal(1));

        FeatureCollection<SimpleFeature> sub = collection.subCollection(QueryBuilder.filtered(collection.getFeatureType().getName(), f));
        assertNotNull(sub);

        assertEquals(1, sub.size());
        
        JTSEnvelope2D exp = new JTSEnvelope2D(1, 1, 1, 1, CRS.decode("EPSG:4326"));
        JTSEnvelope2D act = (JTSEnvelope2D) sub.getEnvelope();
        
        assertEquals(exp.getMinX(), act.getMinX(), 0.1);
        assertEquals(exp.getMinY(), act.getMinY(), 0.1);
        assertEquals(exp.getMaxX(), act.getMaxX(), 0.1);
        assertEquals(exp.getMaxY(), act.getMaxY(), 0.1);
        
        sub.clear();
        assertEquals(2, collection.size());
    }

    public void testAdd() throws IOException {
        SimpleFeatureBuilder b = new SimpleFeatureBuilder((SimpleFeatureType) collection.getFeatureType());
        b.set(aname("intProperty"), new Integer(3));
        b.set(aname("doubleProperty"), new Double(3.3));
        b.set(aname("stringProperty"), "three");
        b.set(aname("geometry"), new GeometryFactory().createPoint(new Coordinate(3, 3)));

        SimpleFeature feature = b.buildFeature(null);
        assertEquals(3, collection.size());

        collection.add(feature);
        assertEquals(4, collection.size());

        FeatureIterator i = collection.iterator();
        boolean found = false;

        while (i.hasNext()) {
            SimpleFeature f = (SimpleFeature) i.next();

            if ("three".equals(f.getAttribute(aname("stringProperty")))) {
                assertEquals(feature.getAttribute(aname("doubleProperty")),
                    f.getAttribute(aname("doubleProperty")));
                assertEquals(feature.getAttribute(aname("stringProperty")),
                    f.getAttribute(aname("stringProperty")));
                assertTrue(((Geometry) feature.getAttribute(aname("geometry"))).equals(
                        (Geometry) f.getAttribute(aname("geometry"))));
                found = true;
            }
        }

        assertTrue(found);

        i.close();
    }

    public void testClear() throws IOException {
        collection.clear();

        FeatureIterator i = collection.iterator();
        assertFalse(i.hasNext());

        i.close();
    }
}

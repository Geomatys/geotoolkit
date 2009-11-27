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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.data.DefaultFeatureCollection;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;


public abstract class JDBCFeatureStoreTest extends JDBCTestSupport {
    JDBCFeatureStore featureStore;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        featureStore = (JDBCFeatureStore) dataStore.getFeatureSource(tname("ft1"));
    }

    public void testAddFeatures() throws IOException {
        SimpleFeatureBuilder b = new SimpleFeatureBuilder(featureStore.getSchema());
        DefaultFeatureCollection collection = new DefaultFeatureCollection(null,
                featureStore.getSchema());

        for (int i = 3; i < 6; i++) {
            b.set(aname("intProperty"), new Integer(i));
            b.set(aname("geometry"), new GeometryFactory().createPoint(new Coordinate(i, i)));
            collection.add(b.buildFeature(null));
        }

        List<FeatureId> fids = featureStore.addFeatures(collection);
        assertEquals(3, fids.size());

        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureStore.getFeatures();
        assertEquals(6, features.size());

        FilterFactory ff = dataStore.getFilterFactory();

        for (Iterator f = fids.iterator(); f.hasNext();) {
            FeatureId identifier = (FeatureId) f.next();
            String fid = identifier.getID();
            Id filter = ff.id(Collections.singleton(identifier));

            features = featureStore.getFeatures(filter);
            assertEquals(1, features.size());

            Iterator iterator = features.iterator();
            assertTrue(iterator.hasNext());

            SimpleFeature feature = (SimpleFeature) iterator.next();
            assertEquals(fid, feature.getID());
            assertFalse(iterator.hasNext());

            features.close(iterator);
        }
    }
    
    /**
     * Check null encoding is working properly
     * @throws IOException
     */
    public void testAddNullAttributes() throws IOException {
        SimpleFeatureBuilder b = new SimpleFeatureBuilder(featureStore.getSchema());
        SimpleFeature nullFeature = b.buildFeature("testId");
        featureStore.addFeatures(Arrays.asList(nullFeature));
    }

    public void testSetFeatures() throws IOException {
        SimpleFeatureBuilder b = new SimpleFeatureBuilder(featureStore.getSchema());
        DefaultFeatureCollection collection = new DefaultFeatureCollection(null,
                featureStore.getSchema());

        for (int i = 3; i < 6; i++) {
            b.set(aname("intProperty"), new Integer(i));
            b.set(aname("geometry"), new GeometryFactory().createPoint(new Coordinate(i, i)));
            collection.add(b.buildFeature(null));
        }

        FeatureReader<SimpleFeatureType, SimpleFeature> reader = DataUtilities.wrapToReader(collection, collection.getSchema());
        featureStore.removeFeatures(Filter.INCLUDE);
        featureStore.addFeatures(reader);

        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureStore.getFeatures();
        assertEquals(3, features.size());

        Iterator iterator = features.iterator();
        HashSet numbers = new HashSet();
        numbers.add(new Integer(3));
        numbers.add(new Integer(4));
        numbers.add(new Integer(5));

        for (int i = 3; iterator.hasNext(); i++) {
            SimpleFeature feature = (SimpleFeature) iterator.next();
            assertTrue(numbers.contains(((Number)feature.getAttribute(aname("intProperty"))).intValue()));
            numbers.remove(feature.getAttribute(aname("intProperty")));
        }

        features.close(iterator);
    }

    public void testModifyFeatures() throws IOException {
        SimpleFeatureType t = featureStore.getSchema();
        featureStore.updateFeatures(new AttributeDescriptor[] { t.getDescriptor(aname("stringProperty")) },
            new Object[] { "foo" }, Filter.INCLUDE);

        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureStore.getFeatures();
        Iterator i = features.iterator();

        assertTrue(i.hasNext());

        while (i.hasNext()) {
            SimpleFeature feature = (SimpleFeature) i.next();
            assertEquals("foo", feature.getAttribute(aname("stringProperty")));
        }

        features.close(i);
    }
    
    public void testModifyGeometry() throws IOException {
        // GEOT-2371
        SimpleFeatureType t = featureStore.getSchema();
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(-10, 0));
		featureStore.updateFeatures(new AttributeDescriptor[] { t.getDescriptor(aname("geometry")) },
            new Object[] { point }, Filter.INCLUDE);

        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureStore.getFeatures();
        Iterator i = features.iterator();

        assertTrue(i.hasNext());

        while (i.hasNext()) {
            SimpleFeature feature = (SimpleFeature) i.next();
            assertTrue(point.equalsExact((Geometry) feature.getAttribute(aname("geometry"))));
        }

        features.close(i);
    }
    
    public void testModifyMadeUpGeometry() throws IOException {
        // GEOT-2371
        SimpleFeatureType t = featureStore.getSchema();
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(-10, 0));
        
        // make up a fake attribute with the same name, something that might happen
        // in chains of retyping where attributes are rebuilt
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final AttributeTypeBuilder ab = new AttributeTypeBuilder();
        ab.setBinding(Point.class);
        ab.setCRS(DefaultGeographicCRS.WGS84);
        adb.setType(ab.buildGeometryType());
        adb.setName(aname("geometry"));
        AttributeDescriptor madeUp = adb.buildDescriptor();
        
        featureStore.updateFeatures(new AttributeDescriptor[] { madeUp },
            new Object[] { point }, Filter.INCLUDE);

        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureStore.getFeatures();
        Iterator i = features.iterator();

        assertTrue(i.hasNext());

        while (i.hasNext()) {
            SimpleFeature feature = (SimpleFeature) i.next();
            assertTrue(point.equalsExact((Geometry) feature.getAttribute(aname("geometry"))));
        }

        features.close(i);
    }
    
    public void testModifyFeaturesSingleAttribute() throws IOException {
        SimpleFeatureType t = featureStore.getSchema();
        featureStore.updateFeatures(t.getDescriptor(aname("stringProperty")), "foo" , Filter.INCLUDE);

        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureStore.getFeatures();
        Iterator i = features.iterator();

        assertTrue(i.hasNext());

        while (i.hasNext()) {
            SimpleFeature feature = (SimpleFeature) i.next();
            assertEquals("foo", feature.getAttribute(aname("stringProperty")));
        }

        features.close(i);
    }
    
    public void testModifyFeaturesInvalidFilter() throws IOException {
        SimpleFeatureType t = featureStore.getSchema();
        FilterFactory ff = FactoryFinder.getFilterFactory(null);
        PropertyIsEqualTo f = ff.equals(ff.property("invalidAttribute"), ff.literal(5));
        
        try {
            featureStore.updateFeatures(new AttributeDescriptor[] { t.getDescriptor(aname("stringProperty")) },
            new Object[] { "foo" }, f);
            fail("This should have failed with an exception reporting the invalid filter");
        } catch(Exception e) {
            //  fine
        }
    }

    public void testRemoveFeatures() throws IOException {
        FilterFactory ff = dataStore.getFilterFactory();
        Filter filter = ff.equals(ff.property(aname("intProperty")), ff.literal(1));

        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureStore.getFeatures();
        assertEquals(3, features.size());

        featureStore.removeFeatures(filter);
        assertEquals(2, features.size());

        featureStore.removeFeatures(Filter.INCLUDE);
        assertEquals(0, features.size());
    }
    
    public void testRemoveFeaturesWithInvalidFilter() throws IOException {
        FilterFactory ff = FactoryFinder.getFilterFactory(null);
        PropertyIsEqualTo f = ff.equals(ff.property("invalidAttribute"), ff.literal(5));
        
        try {
            featureStore.removeFeatures(f);
            fail("This should have failed with an exception reporting the invalid filter");
        } catch(Exception e) {
            //  fine
        }
    }
}

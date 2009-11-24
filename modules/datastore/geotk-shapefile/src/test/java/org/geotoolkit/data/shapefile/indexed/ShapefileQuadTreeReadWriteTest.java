/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
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
package org.geotoolkit.data.shapefile.indexed;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.query.DefaultQuery;
import org.geotoolkit.data.FeatureSource;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.shapefile.ShapefileDataStoreFactory;
import org.geotoolkit.data.shapefile.AbstractTestCaseSupport;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.collection.FeatureIterator;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.shptest.test.UtilTestData;

/**
 * @version $Id$
 * @author Ian Schneider
 * @module pending
 */
public class ShapefileQuadTreeReadWriteTest extends AbstractTestCaseSupport {
    final String[] files = {"shapes/statepop.shp", "shapes/polygontest.shp",
            "shapes/pointtest.shp", "shapes/holeTouchEdge.shp", "shapes/stream.shp"};
    boolean readStarted = false;
    Exception exception = null;

    /**
     * Creates a new instance of ShapefileReadWriteTest
     */
    public ShapefileQuadTreeReadWriteTest( String name ) throws IOException {
        super(name);
    }

    public void testAll() throws Throwable {
        StringBuffer errors = new StringBuffer();
        Exception bad = null;

        for( int i = 0, ii = files.length; i < ii; i++ ) {
            try {
                test(files[i]);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                errors.append("\nFile " + files[i] + " : " + e.getMessage());
                bad = e;
            }
        }

        if (errors.length() > 0) {
            fail(errors.toString(), bad);
        }
    }

    public void fail( String message, Throwable cause ) throws Throwable {
        Throwable fail = new AssertionFailedError(message);
        fail.initCause(cause);
        throw fail;
    }

    public void testWriteTwice() throws Exception {
        copyShapefiles("shapes/stream.shp");
        ShapefileDataStoreFactory fac = new ShapefileDataStoreFactory();
        DataStore s1 = createDataStore(fac, ShapeTestData.url(AbstractTestCaseSupport.class, "shapes/stream.shp"), true);
        String typeName = s1.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> source = s1.getFeatureSource(typeName);
        SimpleFeatureType type = source.getSchema();
        FeatureCollection<SimpleFeatureType, SimpleFeature> one = source.getFeatures();

        ShapefileDataStoreFactory maker = new ShapefileDataStoreFactory();

         doubleWrite(type, one, getTempFile(), maker, false);
         doubleWrite(type, one, getTempFile(), maker, true);
    }

    private DataStore createDataStore( ShapefileDataStoreFactory fac, URL url, boolean memoryMapped )
            throws IOException {
        Map params = new HashMap();
        params.put(ShapefileDataStoreFactory.URLP.getName().toString(), url);
        params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.getName().toString(), new Boolean(true));
        DataStore createDataStore = fac.createDataStore(params);
        return createDataStore;
    }

    private void doubleWrite( SimpleFeatureType type, FeatureCollection<SimpleFeatureType, SimpleFeature> one, File tmp,
            ShapefileDataStoreFactory maker, boolean memorymapped ) throws IOException,
            MalformedURLException {
        DataStore s;
        s = createDataStore(maker, tmp.toURL(), memorymapped);

        s.createSchema(type);
        FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore<SimpleFeatureType, SimpleFeature>) s.getFeatureSource(type.getTypeName());

        store.addFeatures(one);
        store.addFeatures(one);

        s = createDataStore(maker, tmp.toURL(), true);
        assertEquals(one.size() * 2, store.getCount(QueryBuilder.all(store.getName())));
    }

    void test( String f ) throws Exception {
//        ShapeTestData.url(f)
//        File file = copyShapefiles(f); // Work on File rather than URL from
//        // JAR.
        DataStore s = createDataStore(new ShapefileDataStoreFactory(), ShapeTestData.url(f), true);
        String typeName = s.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> source = s.getFeatureSource(typeName);
        SimpleFeatureType type = source.getSchema();
        FeatureCollection<SimpleFeatureType, SimpleFeature> one = source.getFeatures();

        ShapefileDataStoreFactory maker = new ShapefileDataStoreFactory();
        test(type, one, getTempFile(), maker, false);
        test(type, one, getTempFile(), maker, true);
    }

    private void test( SimpleFeatureType type, FeatureCollection<SimpleFeatureType, SimpleFeature> one, File tmp,
            ShapefileDataStoreFactory maker, boolean memorymapped ) throws IOException,
            MalformedURLException, Exception {
        DataStore s;
        String typeName;
        s = createDataStore(maker, tmp.toURL(), memorymapped);

        s.createSchema(type);

        FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore<SimpleFeatureType, SimpleFeature>) s.getFeatureSource(type.getTypeName());

        store.addFeatures(one);

        s = createDataStore(new ShapefileDataStoreFactory(), tmp.toURL(), true);
        typeName = s.getTypeNames()[0];

        FeatureCollection<SimpleFeatureType, SimpleFeature> two = s.getFeatureSource(typeName).getFeatures();

        compare(one.features(), two.features());
    }

    static void compare( FeatureIterator<SimpleFeature> fs1, FeatureIterator<SimpleFeature> fs2 ) throws Exception {
        try {
            while( fs1.hasNext() ) {
                SimpleFeature f1 = fs1.next();
                SimpleFeature f2 = fs2.next();

                compare(f1, f2);
            }

        } finally {
            fs1.close();
            fs2.close();
        }
    }

    static void compare( SimpleFeature f1, SimpleFeature f2 ) throws Exception {
        if (f1.getAttributeCount() != f2.getAttributeCount()) {
            throw new Exception("Unequal number of attributes");
        }

        for( int i = 0; i < f1.getAttributeCount(); i++ ) {
            Object att1 = f1.getAttribute(i);
            Object att2 = f2.getAttribute(i);

            if (att1 instanceof Geometry && att2 instanceof Geometry) {
                Geometry g1 = ((Geometry) att1);
                Geometry g2 = ((Geometry) att2);
                g1.normalize();
                g2.normalize();

                if (!g1.equalsExact(g2)) {
                    throw new Exception("Different geometries (" + i + "):\n" + g1 + "\n" + g2);
                }
            } else {
                if (!att1.equals(att2)) {
                    throw new Exception("Different attribute (" + i + "): [" + att1 + "] - ["
                            + att2 + "]");
                }
            }
        }
    }

    /**
     * Test optimized getBounds(). Testing when filter is a bbox filter and a fidfilter
     * 
     * @throws Exception
     */
    public void testGetBoundsQuery() throws Exception {
        File file = copyShapefiles("shapes/streams.shp");

        ShapefileDataStoreFactory fac = new ShapefileDataStoreFactory();

        Map params = new HashMap();
        params.put(ShapefileDataStoreFactory.URLP.getName().toString(), file.toURL());
        params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.getName().toString(), new Boolean(true));
        IndexedShapefileDataStore ds = (IndexedShapefileDataStore) fac.createDataStore(params);

        FilterFactory2 ff = (FilterFactory2) FactoryFinder.getFilterFactory(null);

        FeatureId featureId = ff.featureId("streams.84");
        Id filter = ff.id(Collections.singleton(featureId));
        FeatureCollection<SimpleFeatureType, SimpleFeature> features = ds.getFeatureSource(ds.getTypeNames()[0]).getFeatures(filter);

        FeatureIterator<SimpleFeature> iter = features.features();
        JTSEnvelope2D bounds;
        try {
            bounds = new JTSEnvelope2D(iter.next().getBounds());
        } finally {
            iter.close();
        }

        FeatureId id = featureId;
        filter = ff.id(Collections.singleton(id));

        Query query = new DefaultQuery(ds.getTypeNames()[0], filter);

        Envelope result = ds.getFeatureSource(ds.getTypeNames()[0]).getBounds(query);

        assertTrue(result.equals(bounds));
    }

    public static final void main( String[] args ) throws Exception {
        junit.textui.TestRunner.run(suite(ShapefileQuadTreeReadWriteTest.class));
    }
}

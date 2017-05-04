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

import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.FeatureStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory;
import org.geotoolkit.data.shapefile.AbstractTestCaseSupport;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.data.query.QueryBuilder;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.Envelope;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.sis.feature.FeatureExt;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.test.TestData;
import org.opengis.util.GenericName;

import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * @version $Id$
 * @author Ian Schneider
 * @module
 */
public class ShapefileQuadTreeReadWriteTest extends AbstractTestCaseSupport {
    final String[] files = {"shapes/statepop.shp", "shapes/polygontest.shp",
            "shapes/pointtest.shp", "shapes/holeTouchEdge.shp", "shapes/stream.shp"};
    boolean readStarted = false;
    Exception exception = null;

    @Test
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

    public void fail( final String message, final Throwable cause ) throws Throwable {
        Throwable fail = new Exception(message);
        fail.initCause(cause);
        throw fail;
    }

    @Test
    public void testWriteTwice() throws Exception {
        copyShapefiles("shapes/stream.shp");
        ShapefileFeatureStoreFactory fac = new ShapefileFeatureStoreFactory();
        FeatureStore s1 = createDataStore(fac, TestData.url(AbstractTestCaseSupport.class, "shapes/stream.shp"), true);
        GenericName typeName = s1.getNames().iterator().next();
        FeatureType type = s1.getFeatureType(typeName.toString());
        FeatureCollection one = s1.createSession(true).getFeatureCollection(QueryBuilder.all(typeName.toString()));

        ShapefileFeatureStoreFactory maker = new ShapefileFeatureStoreFactory();

         doubleWrite(type, one, getTempFile(), maker, false);
         doubleWrite(type, one, getTempFile(), maker, true);
    }

    private FeatureStore createDataStore( final ShapefileFeatureStoreFactory fac, final URL url, final boolean memoryMapped )
            throws IOException, DataStoreException, URISyntaxException {
        Map params = new HashMap();
        params.put(ShapefileFeatureStoreFactory.PATH.getName().toString(), url.toURI());
        params.put(ShapefileFeatureStoreFactory.CREATE_SPATIAL_INDEX.getName().toString(), new Boolean(true));
        FeatureStore createFeatureStore = (FeatureStore) fac.open(params);
        return createFeatureStore;
    }

    private void doubleWrite( final FeatureType type, final FeatureCollection one, final File tmp,
            final ShapefileFeatureStoreFactory maker, final boolean memorymapped ) throws IOException,
            MalformedURLException,
            DataStoreException, URISyntaxException {
        FeatureStore s;
        s = createDataStore(maker, tmp.toURI().toURL(), memorymapped);

        s.createFeatureType(type);

        Session session = s.createSession(true);
        session.addFeatures(type.getName().toString(),one);
        session.addFeatures(type.getName().toString(),one);
        session.commit();

        s = createDataStore(maker, tmp.toURI().toURL(), true);
        assertEquals(one.size() * 2, s.getCount(QueryBuilder.all(s.getNames().iterator().next().toString())));
    }

    void test( final String f ) throws Exception {
//        ShapeTestData.url(f)
//        File file = copyShapefiles(f); // Work on File rather than URL from
//        // JAR.
        FeatureStore s = createDataStore(new ShapefileFeatureStoreFactory(), ShapeTestData.url(f), true);
        GenericName typeName = s.getNames().iterator().next();
        FeatureType type = s.getFeatureType(typeName.toString());
        FeatureCollection one = s.createSession(true).getFeatureCollection(QueryBuilder.all(typeName.toString()));

        ShapefileFeatureStoreFactory maker = new ShapefileFeatureStoreFactory();
        test(type, one, getTempFile(), maker, false);
        test(type, one, getTempFile(), maker, true);
    }

    private void test( final FeatureType type, final FeatureCollection one, final File tmp,
            final ShapefileFeatureStoreFactory maker, final boolean memorymapped ) throws IOException,
            MalformedURLException, Exception {
        FeatureStore s;
        s = createDataStore(maker, tmp.toURI().toURL(), memorymapped);

        s.createFeatureType(type);

        Session session = s.createSession(true);
        session.addFeatures(type.getName().toString(),one);
        session.commit();

        s = createDataStore(new ShapefileFeatureStoreFactory(), tmp.toURI().toURL(), true);
        GenericName typeName = s.getNames().iterator().next();

        FeatureCollection two = s.createSession(true).getFeatureCollection(QueryBuilder.all(typeName.toString()));

        //copy values, order is not tested here.
        Collection<Feature> cone = new ArrayList<>();
        Collection<Feature> ctwo = new ArrayList<>();
        FeatureStoreUtilities.fill(one, cone);
        FeatureStoreUtilities.fill(two, ctwo);
        one.containsAll(two);
        two.containsAll(one);
    }

    static void compare( final FeatureIterator fs1, final FeatureIterator fs2 ) throws Exception {
        try {
            while( fs1.hasNext() ) {
                Feature f1 = fs1.next();
                Feature f2 = fs2.next();

                ShapefileRTreeReadWriteTest.compare(f1, f2);
            }

        } finally {
            fs1.close();
            fs2.close();
        }
    }

    /**
     * Test optimized getBounds(). Testing when filter is a bbox filter and a fidfilter
     *
     * @throws Exception
     */
    @Test
    public void testGetBoundsQuery() throws Exception {
        File file = copyShapefiles("shapes/streams.shp");

        ShapefileFeatureStoreFactory fac = new ShapefileFeatureStoreFactory();

        Map params = new HashMap();
        params.put(ShapefileFeatureStoreFactory.PATH.getName().toString(), file.toURI());
        params.put(ShapefileFeatureStoreFactory.CREATE_SPATIAL_INDEX.getName().toString(), new Boolean(true));
        IndexedShapefileFeatureStore ds = (IndexedShapefileFeatureStore) fac.open(params);

        FilterFactory2 ff = (FilterFactory2) FactoryFinder.getFilterFactory(null);

        FeatureId featureId = ff.featureId("streams.84");
        Id filter = ff.id(Collections.singleton(featureId));

        FeatureIterator iter = ds.getFeatureReader(QueryBuilder.filtered(ds.getName().toString(), filter));
        JTSEnvelope2D bounds;
        try {
            bounds = new JTSEnvelope2D(FeatureExt.getEnvelope(iter.next()));
        } finally {
            iter.close();
        }

        FeatureId id = featureId;
        filter = ff.id(Collections.singleton(id));

        Query query = QueryBuilder.filtered(ds.getNames().iterator().next().toString(), filter);

        Envelope result = (Envelope) ds.getEnvelope(query);

        assertTrue(result.equals(bounds));
    }

}

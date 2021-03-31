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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.shapefile.AbstractTestCaseSupport;
import org.geotoolkit.data.shapefile.ShapefileProvider;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.feature.FeatureStore;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.storage.feature.query.QueryBuilder;
import org.geotoolkit.storage.feature.session.Session;
import org.geotoolkit.test.TestData;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.geotoolkit.filter.FilterFactory2;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.filter.ResourceId;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * @version $Id$
 * @author Ian Schneider
 * @module
 */
public class ShapefileQuadTreeReadWriteTest extends AbstractTestCaseSupport {
    private static final String[] files = {"shapes/statepop.shp", "shapes/polygontest.shp",
            "shapes/pointtest.shp", "shapes/holeTouchEdge.shp", "shapes/stream.shp"};

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
        ShapefileProvider fac = new ShapefileProvider();
        FeatureStore s1 = createDataStore(fac, TestData.url(AbstractTestCaseSupport.class, "shapes/stream.shp"), true);
        GenericName typeName = s1.getNames().iterator().next();
        FeatureType type = s1.getFeatureType(typeName.toString());
        FeatureCollection one = s1.createSession(true).getFeatureCollection(QueryBuilder.all(typeName.toString()));

        ShapefileProvider maker = new ShapefileProvider();

        doubleWrite(type, one, getTempFile(), maker, false);
        doubleWrite(type, one, getTempFile(), maker, true);
    }

    private FeatureStore createDataStore( final ShapefileProvider fac, final URL url, final boolean memoryMapped )
            throws IOException, DataStoreException, URISyntaxException {
        final ParameterValueGroup params = fac.getOpenParameters().createValue();
        params.parameter(ShapefileProvider.LOCATION).setValue(url.toURI());
        params.parameter(ShapefileProvider.CREATE_SPATIAL_INDEX.getName().toString()).setValue(Boolean.TRUE);
        return fac.open(params);
    }

    private void doubleWrite( final FeatureType type, final FeatureCollection one, final File tmp,
            final ShapefileProvider maker, final boolean memorymapped ) throws IOException,
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
        FeatureStore s = createDataStore(new ShapefileProvider(), ShapeTestData.url(f), true);
        GenericName typeName = s.getNames().iterator().next();
        FeatureType type = s.getFeatureType(typeName.toString());
        FeatureCollection one = s.createSession(true).getFeatureCollection(QueryBuilder.all(typeName.toString()));

        ShapefileProvider maker = new ShapefileProvider();
        test(type, one, getTempFile(), maker, false);
        test(type, one, getTempFile(), maker, true);
    }

    private void test( final FeatureType type, final FeatureCollection one, final File tmp,
            final ShapefileProvider maker, final boolean memorymapped ) throws IOException,
            MalformedURLException, Exception {
        FeatureStore s;
        s = createDataStore(maker, tmp.toURI().toURL(), memorymapped);

        s.createFeatureType(type);

        Session session = s.createSession(true);
        session.addFeatures(type.getName().toString(),one);
        session.commit();

        s = createDataStore(new ShapefileProvider(), tmp.toURI().toURL(), true);
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
    @Ignore //fails randomly, urgent need to write shapefile store in SIS
    public void testGetBoundsQuery() throws Exception {
        File file = copyShapefiles("shapes/streams.shp");

        ShapefileProvider fac = new ShapefileProvider();

        final ParameterValueGroup params = fac.getOpenParameters().createValue();
        params.parameter(ShapefileProvider.LOCATION).setValue(file.toURI());
        params.parameter(ShapefileProvider.CREATE_SPATIAL_INDEX.getName().toString()).setValue(Boolean.TRUE);
        IndexedShapefileFeatureStore ds = (IndexedShapefileFeatureStore) fac.open(params);

        FilterFactory2 ff = FilterUtilities.FF;

        ResourceId filter = ff.resourceId("streams.84");

        FeatureIterator iter = ds.getFeatureReader(QueryBuilder.filtered(ds.getName().toString(), filter));
        JTSEnvelope2D bounds;
        try {
            bounds = new JTSEnvelope2D(FeatureExt.getEnvelope(iter.next()));
        } finally {
            iter.close();
        }
        Query query = QueryBuilder.filtered(ds.getNames().iterator().next().toString(), filter);
        Envelope result = (Envelope) ds.getEnvelope(query);
        assertTrue(result.equals(bounds));
    }
}

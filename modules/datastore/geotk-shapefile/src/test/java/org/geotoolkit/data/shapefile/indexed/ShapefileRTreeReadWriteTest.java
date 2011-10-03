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

import junit.framework.AssertionFailedError;

import org.geotoolkit.ShapeTestData;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.shapefile.AbstractTestCaseSupport;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.test.TestData;
import org.opengis.feature.type.Name;

/**
 * @version $Id: ShapefileRTreeReadWriteTest.java 27228 2007-09-29 20:24:08Z
 *          jgarnett $
 * @author Ian Schneider
 * @module pending
 */
public class ShapefileRTreeReadWriteTest extends AbstractTestCaseSupport {
    final String[] files = { "shapes/statepop.shp", "shapes/polygontest.shp",
            "shapes/pointtest.shp", "shapes/holeTouchEdge.shp",
            "shapes/stream.shp" };
    boolean readStarted = false;
    Exception exception = null;

    /**
     * Creates a new instance of ShapefileReadWriteTest
     */
    public ShapefileRTreeReadWriteTest(final String name) throws IOException {
        super(name);
    }

    public void testAll() throws Throwable {
        StringBuffer errors = new StringBuffer();
        Exception bad = null;

        for (int i = 0, ii = files.length; i < ii; i++) {
            try {
                test(files[i]);
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

    public void fail(final String message, final Throwable cause) throws Throwable {
        Throwable fail = new AssertionFailedError(message);
        fail.initCause(cause);
        throw fail;
    }

    public void testWriteTwice() throws Exception {
        copyShapefiles("shapes/stream.shp");
        IndexedShapefileDataStore s1 = new IndexedShapefileDataStore(TestData
                .url(ShapeTestData.class, "shapes/stream.shp"));
        Name typeName = s1.getName();
        SimpleFeatureType type = s1.getFeatureType();
        FeatureCollection<SimpleFeature> one = s1.createSession(true).getFeatureCollection(QueryBuilder.all(typeName));


        doubleWrite(type, one, getTempFile(), false);
        doubleWrite(type, one, getTempFile(), true);

        s1.dispose();
    }

    private void doubleWrite(final SimpleFeatureType type, final FeatureCollection<SimpleFeature> one,
            final File tmp, final boolean memorymapped) throws IOException, MalformedURLException, DataStoreException {
        IndexedShapefileDataStore s;
        s = new IndexedShapefileDataStore(tmp.toURL(), memorymapped, true);

        s.createSchema(type.getName(),type);

        Session session = s.createSession(true);
        session.addFeatures(type.getName(),one);
        session.addFeatures(type.getName(),one);
        session.commit();

        s = new IndexedShapefileDataStore(tmp.toURL());
        assertEquals(one.size() * 2, s.getCount(QueryBuilder.all(s.getName())));
        
        s.dispose();
    }

    void test(final String f) throws Exception {
        File file = copyShapefiles(f); // Work on File rather than URL from JAR.
        IndexedShapefileDataStore s = new IndexedShapefileDataStore(file.toURI().toURL());
        SimpleFeatureType type = s.getFeatureType();
        FeatureCollection<SimpleFeature> one = s.createSession(true).getFeatureCollection(QueryBuilder.all(type.getName()));

        test(type, one, getTempFile(), false);
        test(type, one, getTempFile(), true);

        s.dispose();
    }

    private void test(final SimpleFeatureType type, final FeatureCollection<SimpleFeature> one, final File tmp, final boolean memorymapped)
            throws IOException, MalformedURLException, Exception {
        IndexedShapefileDataStore s;
        Name typeName;
        s = (IndexedShapefileDataStore) new IndexedShapefileDataStore(tmp.toURL(),
                memorymapped, true);

        s.createSchema(type.getName(),type);

        Session session = s.createSession(true);
        session.addFeatures(s.getName(),one);
        session.commit();
        
        s.dispose();

        s = new IndexedShapefileDataStore(tmp.toURL());
        typeName = s.getName();

        FeatureCollection<SimpleFeature> two = s.createSession(true).getFeatureCollection(QueryBuilder.all(typeName));

        //copy values, order is not tested here.
        Collection<SimpleFeature> cone = new ArrayList<SimpleFeature>();
        Collection<SimpleFeature> ctwo = new ArrayList<SimpleFeature>();
        DataUtilities.fill(one, cone);
        DataUtilities.fill(two, ctwo);
        one.containsAll(two);
        two.containsAll(one);

//        compare(one.iterator(), two.iterator());
        s.dispose();
    }

    static void compare(final FeatureIterator<SimpleFeature> fs1, final FeatureIterator<SimpleFeature> fs2)
            throws Exception {

        int i = 0;

        while (fs1.hasNext()) {
            SimpleFeature f1 = fs1.next();
            SimpleFeature f2 = fs2.next();

            compare(f1, f2);
        }
        fs1.close();
        fs2.close();
    }

    static void compare(final SimpleFeature f1, final SimpleFeature f2) throws Exception {
        if (f1.getAttributeCount() != f2.getAttributeCount()) {
            throw new Exception("Unequal number of attributes");
        }

        for (int i = 0; i < f1.getAttributeCount(); i++) {
            Object att1 = f1.getAttribute(i);
            Object att2 = f2.getAttribute(i);

            if (att1 instanceof Geometry && att2 instanceof Geometry) {
                Geometry g1 = ((Geometry) att1);
                Geometry g2 = ((Geometry) att2);
                g1.normalize();
                g2.normalize();

                if (!g1.equalsExact(g2)) {
                    throw new Exception("Different geometries (" + i + "):\n"
                            + g1 + "\n" + g2);
                }
            } else {
                if (!att1.equals(att2)) {
                    throw new Exception("Different attribute (" + i + "): ["
                            + att1 + "] - [" + att2 + "]");
                }
            }
        }
    }

}

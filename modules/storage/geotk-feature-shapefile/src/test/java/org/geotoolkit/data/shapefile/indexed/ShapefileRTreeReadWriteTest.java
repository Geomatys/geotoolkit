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

import org.geotoolkit.ShapeTestData;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.shapefile.AbstractTestCaseSupport;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.test.TestData;
import org.opengis.util.GenericName;

import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;

/**
 * @version $Id: ShapefileRTreeReadWriteTest.java 27228 2007-09-29 20:24:08Z
 *          jgarnett $
 * @author Ian Schneider
 * @module
 */
public class ShapefileRTreeReadWriteTest extends AbstractTestCaseSupport {
    final String[] files = { "shapes/statepop.shp", "shapes/polygontest.shp",
            "shapes/pointtest.shp", "shapes/holeTouchEdge.shp",
            "shapes/stream.shp" };
    boolean readStarted = false;
    Exception exception = null;

    @Test
    public void testAll() throws Throwable {
        final StringBuilder errors = new StringBuilder();
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
        Throwable fail = new Exception(message);
        fail.initCause(cause);
        throw fail;
    }

    @Test
    public void testWriteTwice() throws Exception {
        copyShapefiles("shapes/stream.shp");
        IndexedShapefileFeatureStore s1 = new IndexedShapefileFeatureStore(TestData
                .url(ShapeTestData.class, "shapes/stream.shp").toURI());
        GenericName typeName = s1.getName();
        FeatureType type = s1.getFeatureType();
        FeatureCollection one = s1.createSession(true).getFeatureCollection(QueryBuilder.all(typeName));


        doubleWrite(type, one, getTempFile(), false);
        doubleWrite(type, one, getTempFile(), true);

        s1.close();
    }

    private void doubleWrite(final FeatureType type, final FeatureCollection one,
            final File tmp, final boolean memorymapped) throws IOException, MalformedURLException, DataStoreException {
        IndexedShapefileFeatureStore s;
        s = new IndexedShapefileFeatureStore(tmp.toURI(), memorymapped, true);

        s.createFeatureType(type);

        Session session = s.createSession(true);
        session.addFeatures(type.getName().toString(),one);
        session.addFeatures(type.getName().toString(),one);
        session.commit();

        s = new IndexedShapefileFeatureStore(tmp.toURI());
        assertEquals(one.size() * 2, s.getCount(QueryBuilder.all(s.getName().toString())));

        s.close();
    }

    void test(final String f) throws Exception {
        File file = copyShapefiles(f); // Work on File rather than URL from JAR.
        IndexedShapefileFeatureStore s = new IndexedShapefileFeatureStore(file.toURI());
        FeatureType type = s.getFeatureType();
        FeatureCollection one = s.createSession(true).getFeatureCollection(QueryBuilder.all(type.getName()));

        test(type, one, getTempFile(), false);
        test(type, one, getTempFile(), true);

        s.close();
    }

    private void test(final FeatureType type, final FeatureCollection one, final File tmp, final boolean memorymapped)
            throws IOException, MalformedURLException, Exception {
        IndexedShapefileFeatureStore s;
        GenericName typeName;
        s = (IndexedShapefileFeatureStore) new IndexedShapefileFeatureStore(tmp.toURI(),
                memorymapped, true);

        s.createFeatureType(type);

        Session session = s.createSession(true);
        session.addFeatures(s.getName().toString(),one);
        session.commit();

        s.close();

        s = new IndexedShapefileFeatureStore(tmp.toURI());
        typeName = s.getName();

        FeatureCollection two = s.createSession(true).getFeatureCollection(QueryBuilder.all(typeName.toString()));

        //copy values, order is not tested here.
        Collection<Feature> cone = new ArrayList<>();
        Collection<Feature> ctwo = new ArrayList<>();
        FeatureStoreUtilities.fill(one, cone);
        FeatureStoreUtilities.fill(two, ctwo);
        one.containsAll(two);
        two.containsAll(one);

//        compare(one.iterator(), two.iterator());
        s.close();
    }

    static void compare(final FeatureIterator fs1, final FeatureIterator fs2)
            throws Exception {

        int i = 0;

        while (fs1.hasNext()) {
            Feature f1 = fs1.next();
            Feature f2 = fs2.next();
            compare(f1, f2);
        }
        fs1.close();
        fs2.close();
    }

    public static void compare(final Feature f1, final Feature f2) throws Exception {
        Collection<? extends PropertyType> descs = f1.getType().getProperties(true);
        if (descs.size() != f2.getType().getProperties(true).size()) {
            throw new Exception("Unequal number of attributes");
        }

        for(PropertyType desc : descs){
            final String name = desc.getName().toString();
            Object att1 = f1.getPropertyValue(name);
            Object att2 = f2.getPropertyValue(name);
            if (att1 instanceof Geometry && att2 instanceof Geometry) {
                Geometry g1 = ((Geometry) att1);
                Geometry g2 = ((Geometry) att2);
                g1.normalize();
                g2.normalize();
                if (!g1.equalsExact(g2)) {
                    throw new Exception("Different geometries (" + name + "):\n"
                            + g1 + "\n" + g2);
                }
            } else {
                if (!att1.equals(att2)) {
                    throw new Exception("Different attribute (" + name + "): ["
                            + att1 + "] - [" + att2 + "]");
                }
            }
        }

    }

}

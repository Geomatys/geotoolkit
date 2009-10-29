/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
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
import org.geotoolkit.data.FeatureSource;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.shapefile.AbstractTestCaseSupport;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.collection.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

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
    public ShapefileRTreeReadWriteTest(String name) throws IOException {
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

    public void fail(String message, Throwable cause) throws Throwable {
        Throwable fail = new AssertionFailedError(message);
        fail.initCause(cause);
        throw fail;
    }

    public void testWriteTwice() throws Exception {
        copyShapefiles("shapes/stream.shp");
        IndexedShapefileDataStore s1 = new IndexedShapefileDataStore(ShapeTestData
                .url(ShapeTestData.class, "shapes/stream.shp"));
        String typeName = s1.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> source = s1.getFeatureSource(typeName);
        SimpleFeatureType type = source.getSchema();
        FeatureCollection<SimpleFeatureType, SimpleFeature> one = source.getFeatures();


        doubleWrite(type, one, getTempFile(), false);
        doubleWrite(type, one, getTempFile(), true);
        
        s1.dispose();
    }

    private void doubleWrite(SimpleFeatureType type, FeatureCollection<SimpleFeatureType, SimpleFeature> one,
            File tmp, 
            boolean memorymapped) throws IOException, MalformedURLException {
        IndexedShapefileDataStore s;
        s = new IndexedShapefileDataStore(tmp.toURL(),
                memorymapped, true);

        s.createSchema(type);
        FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore<SimpleFeatureType, SimpleFeature>) s.getFeatureSource(type
                .getTypeName());

        store.addFeatures(one);
        store.addFeatures(one);

        s = new IndexedShapefileDataStore(tmp.toURL());
        assertEquals(one.size() * 2, store.getCount(Query.ALL));
    }

    void test(String f) throws Exception {
        File file = copyShapefiles(f); // Work on File rather than URL from JAR.
        IndexedShapefileDataStore s = new IndexedShapefileDataStore(file.toURI().toURL());
        String typeName = s.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> source = s.getFeatureSource(typeName);
        SimpleFeatureType type = source.getSchema();
        FeatureCollection<SimpleFeatureType, SimpleFeature> one = source.getFeatures();

        test(type, one, getTempFile(), false);
        test(type, one, getTempFile(), true);
        
        s.dispose();
    }

    private void test(SimpleFeatureType type, FeatureCollection<SimpleFeatureType, SimpleFeature> one, File tmp, boolean memorymapped)
            throws IOException, MalformedURLException, Exception {
        IndexedShapefileDataStore s;
        String typeName;
        s = (IndexedShapefileDataStore) new IndexedShapefileDataStore(tmp.toURL(),
                memorymapped, true);

        s.createSchema(type);

        FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore<SimpleFeatureType, SimpleFeature>) s.getFeatureSource(type
                .getTypeName());
        store.addFeatures(one);

        s = new IndexedShapefileDataStore(tmp.toURL());
        typeName = s.getTypeNames()[0];

        FeatureCollection<SimpleFeatureType, SimpleFeature> two = s.getFeatureSource(typeName).getFeatures();

        compare(one.features(), two.features());
        s.dispose();
    }

    static void compare(FeatureIterator<SimpleFeature> fs1, FeatureIterator<SimpleFeature> fs2)
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

    static void compare(SimpleFeature f1, SimpleFeature f2) throws Exception {
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

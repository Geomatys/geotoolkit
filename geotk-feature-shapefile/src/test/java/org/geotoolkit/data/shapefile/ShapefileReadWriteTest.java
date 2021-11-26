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
package org.geotoolkit.data.shapefile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.math.MathFunctions;
import org.apache.sis.util.Numbers;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.query.QueryBuilder;
import org.geotoolkit.storage.feature.session.Session;
import org.geotoolkit.test.TestData;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @version $Id$
 * @author Ian Schneider
 * @module
 */
public class ShapefileReadWriteTest extends AbstractTestCaseSupport {
    final String[] files = { "shapes/statepop.shp", "shapes/polygontest.shp",
            "shapes/pointtest.shp", "shapes/holeTouchEdge.shp",
            "shapes/stream.shp", "shapes/chinese_poly.shp" };

    @Test
    public void testReadWriteStatePop() throws Exception {
        test("shapes/statepop.shp");
    }

    @Test
    public void testReadWritePolygonTest() throws Exception {
        test("shapes/polygontest.shp");
    }

    @Test
    public void testReadWritePointTest() throws Exception {
        test("shapes/pointtest.shp");
    }

    @Test
    public void testReadWriteHoleTouchEdge() throws Exception {
        test("shapes/holeTouchEdge.shp");
    }

    @Test
    public void testReadWriteChinese() throws Exception {
        test("shapes/chinese_poly.shp", Charset.forName("GB18030"));
    }

    @Test
    public void testReadDanishPoint() throws Exception {
        test("shapes/danish_point.shp");
    }

    boolean readStarted = false;

    Exception exception = null;

    @Test
    public void testConcurrentReadWrite() throws Exception {
        System.gc();
        System.runFinalization(); // If some streams are still open, it may
        // help to close them.
        final File file = getTempFile();
        Runnable reader = new Runnable() {
            public void run() {
                int cutoff = 0;
                FileInputStream fr = null;
                try {
                    fr = new FileInputStream(file);
                    try {
                        fr.read();
                    } catch (IOException e1) {
                        exception = e1;
                        return;
                    }
                    // if (verbose) {
                    // System.out.println("locked");
                    // }
                    readStarted = true;
                    while (cutoff < 10) {
                        synchronized (this) {
                            try {
                                try {
                                    fr.read();
                                } catch (IOException e) {
                                    exception = e;
                                    return;
                                }
                                wait(500);
                                cutoff++;
                            } catch (InterruptedException e) {
                                cutoff = 10;
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    assertTrue(false);
                } finally {
                    if (fr != null) {
                        try {
                            fr.close();
                        } catch (IOException e) {
                            exception = e;
                            return;
                        }
                    }
                }
            }
        };
        Thread readThread = new Thread(reader);
        readThread.start();
        while (!readStarted) {
            if (exception != null) {
                throw exception;
            }
            Thread.sleep(100);
        }
        test(files[0]);
    }

    private static void fail(final String message, final Throwable cause) throws Throwable {
        Throwable fail = new Exception(message);
        fail.initCause(cause);
        throw fail;
    }

    private void test(final String f) throws Exception {
        test(f, null);
    }

    private void test(final String f, final Charset charset) throws Exception {
        copyShapefiles(f); // Work on File rather than URL from JAR.
        ShapefileFeatureStore s = new ShapefileFeatureStore(
                TestData.url(AbstractTestCaseSupport.class, f).toURI(), false, charset);
        GenericName typeName = s.getNames().iterator().next();
        Session session = s.createSession(true);
        FeatureType type = s.getFeatureType(typeName.toString());
        FeatureCollection one = session.getFeatureCollection(QueryBuilder.all(typeName));
        File tmp = getTempFile();

        ShapefileProvider maker = new ShapefileProvider();
        test(type, one, tmp, maker, true, charset);

        File tmp2 = getTempFile(); // TODO consider reuse tmp results in
        // failure
        test(type, one, tmp2, maker, false, charset);
    }

    private void test(final FeatureType type, final FeatureCollection original,
            final File tmp, final ShapefileProvider maker, final boolean memorymapped, final Charset charset)
            throws IOException, MalformedURLException, Exception {

        ShapefileFeatureStore shapefile;
        GenericName typeName = type.getName();

        final ParameterValueGroup params = maker.getOpenParameters().createValue();
        params.parameter(ShapefileProvider.LOCATION).setValue(tmp.toURI());
        params.parameter(ShapefileProvider.MEMORY_MAPPED.getName().toString()).setValue(memorymapped);
        params.parameter(ShapefileProvider.DBFCHARSET.getName().toString()).setValue(charset);

        shapefile = (ShapefileFeatureStore) maker.open(params);

        shapefile.createFeatureType(type);

        Session session = shapefile.createSession(true);
        session.addFeatures(typeName.toString(), original);
        session.commit();

        assertFalse(session.hasPendingChanges());

        FeatureCollection copy = session.getFeatureCollection(QueryBuilder.all(typeName));
        compare(original, copy);

        // review open
        ShapefileFeatureStore review = new ShapefileFeatureStore(tmp.toURI(), memorymapped, charset);
        typeName = review.getNames().iterator().next();
        FeatureCollection again = review.createSession(true).getFeatureCollection(QueryBuilder.all(typeName));

        compare(copy, again);
        compare(original, again);

    }

    static void compare(Collection<Feature> one, Collection<Feature> two) throws Exception {

        Assert.assertEquals("Compared feature collections have different size.", one.size(), two.size());

        final Iterator<Feature> it = one.iterator();
        try {
            while (it.hasNext()) {
                Assert.assertTrue("Content of compared collections differ.", approximatelyContains(it.next(), two));
            }

        } finally {
            if (it instanceof AutoCloseable)
                ((AutoCloseable) it).close();
        }
    }

    static boolean approximatelyContains(final Feature f, final Collection<Feature> col) throws Exception {
        final Iterator<Feature> it = col.iterator();
        try {
            while (it.hasNext()) {
                if (approximatelyEqual(f, it.next()))
                    return true;
            }

            return false;
        } finally {
            if (it instanceof AutoCloseable)
                ((AutoCloseable) it).close();
        }
    }

    static boolean approximatelyEqual(final Feature f1, final Feature f2) throws Exception {
        // Remove sis conventions, as they're not brut data but links and computed facilities.
        final Collection<String> f1Properties = f1.getType().getProperties(true).stream()
                .map(IdentifiedType::getName)
                .filter(name -> !AttributeConvention.contains(name))
                .map(GenericName::toString)
                .collect(Collectors.toList());
        final Collection<String> f2Properties = f2.getType().getProperties(true).stream()
                .map(IdentifiedType::getName)
                .filter(name -> !AttributeConvention.contains(name))
                .map(GenericName::toString)
                .collect(Collectors.toList());

        if (f1Properties.size() != f2Properties.size() || !f1Properties.containsAll(f2Properties))
            return false;

        for(String name : f1Properties) {
            Object att1 = f1.getPropertyValue(name);
            Object att2 = f2.getPropertyValue(name);
            if (!approximatelyEqual(att1, att2))
                return false;
        }

        return true;
    }

    private static boolean approximatelyEqual(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        } else if (o1 == null || o2 == null) {
            return false;

        } else if (o1 instanceof Geometry && o2 instanceof Geometry) {
            Geometry g1 = ((Geometry) o1);
            Geometry g2 = ((Geometry) o2);
            g1.normalize();
            g2.normalize();
            return g1.equalsExact(g2);

        } else if (Numbers.isFloat(o1.getClass()) || Numbers.isFloat(o2.getClass())) {
            return MathFunctions.epsilonEqual(((Number) o1).doubleValue(), ((Number) o2).doubleValue(), 1e-7);

        } else {
            return o1.equals(o2);
        }
    }
}

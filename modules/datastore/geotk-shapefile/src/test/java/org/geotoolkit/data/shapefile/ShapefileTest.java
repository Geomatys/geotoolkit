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

import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.data.shapefile.lock.AccessManager;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.shapefile.shx.ShxReader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.data.shapefile.lock.ShpFiles;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.test.TestData;

import org.opengis.feature.type.Name;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import static org.junit.Assert.*;

/**
 * 
 * @version $Id$
 * @author Ian Schneider
 * @author James Macgill
 * @module pending
 */
public class ShapefileTest extends AbstractTestCaseSupport {

    public final String STATEPOP = "shapes/statepop.shp";
    public final String STATEPOP_IDX = "shapes/statepop.shx";
    public final String POINTTEST = "shapes/pointtest.shp";
    public final String POLYGONTEST = "shapes/polygontest.shp";
    public final String HOLETOUCHEDGE = "shapes/holeTouchEdge.shp";
    public final String EXTRAATEND = "shapes/extraAtEnd.shp";

    @Test
    public void testLoadingStatePop() throws Exception {
        loadShapes(STATEPOP, 49);
        loadMemoryMapped(STATEPOP, 49);
    }

    @Test
    public void testLoadingSamplePointFile() throws Exception {
        loadShapes(POINTTEST, 10);
        loadMemoryMapped(POINTTEST, 10);
    }

    @Test
    public void testLoadingSamplePolygonFile() throws Exception {
        loadShapes(POLYGONTEST, 2);
        loadMemoryMapped(POLYGONTEST, 2);
    }

    @Test
    public void testLoadingTwice() throws Exception {
        loadShapes(POINTTEST, 10);
        loadShapes(POINTTEST, 10);
        loadShapes(STATEPOP, 49);
        loadShapes(STATEPOP, 49);
        loadShapes(POLYGONTEST, 2);
        loadShapes(POLYGONTEST, 2);
    }

    /**
     * It is posible for a point in a hole to touch the edge of its containing
     * shell This test checks that such polygons can be loaded ok.
     */
    @Test
    public void testPolygonHoleTouchAtEdge() throws Exception {
        loadShapes(HOLETOUCHEDGE, 1);
        loadMemoryMapped(HOLETOUCHEDGE, 1);
    }

    /**
     * It is posible for a shapefile to have extra information past the end of
     * the normal feature area, this tests checks that this situation is delt
     * with ok.
     */
    @Test
    public void testExtraAtEnd() throws Exception {
        loadShapes(EXTRAATEND, 3);
        loadMemoryMapped(EXTRAATEND, 3);
    }

    @Test
    public void testIndexFile() throws Exception {
        copyShapefiles(STATEPOP);
        copyShapefiles(STATEPOP_IDX);
        final URL url1 = ShapeTestData.url(STATEPOP); // Backed by InputStream
        final URL url2 = TestData.url(AbstractTestCaseSupport.class, STATEPOP); // Backed by File
        final URL url3 = TestData.url(AbstractTestCaseSupport.class, STATEPOP_IDX);
        final ShapefileReader reader1 = new ShpFiles(url1).createLocker().getSHPReader(false, false, true, null);
        final ShapefileReader reader2 = new ShpFiles(url2).createLocker().getSHPReader(false, false, true, null);
        final ShxReader index = new ShpFiles(url3).createLocker().getSHXReader(false);
        try {
            for (int i = 0; i < index.getRecordCount(); i++) {
                if (reader1.hasNext()) {

                    Geometry g1 = (Geometry) reader1.nextRecord().shape();
                    Geometry g2 = (Geometry) reader2.shapeAt(2 * (index
                            .getOffset(i)));
                    assertTrue(g1.equalsExact(g2));

                } else {
                    fail("uneven number of records");
                }
                // assertEquals(reader1.nextRecord().offset(),index.getOffset(i));
            }
        } finally {
            index.close();
            reader2.close();
            reader1.close();
        }
    }

    @Test
    public void testHolyPolygons() throws Exception {
        SimpleFeatureType type = FeatureTypeUtilities.createType("junk",
                "a:MultiPolygon");
        Collection<SimpleFeature> features = new ArrayList<SimpleFeature>();

        File tmpFile = getTempFile();
        tmpFile.delete();

        // write features
        ShapefileDataStoreFactory make = new ShapefileDataStoreFactory();
        DataStore s = make.createNewDataStore(Collections.singletonMap("url", tmpFile.toURI().toURL()));
        s.createSchema(type.getName(),type);
        Name typeName = type.getName();

        Session session = s.createSession(true);
        session.addFeatures(typeName,features);
        session.commit();

        s = new ShapefileDataStore(tmpFile.toURI().toURL());
        typeName = s.getNames().iterator().next();
        FeatureCollection<SimpleFeature> fc = s.createSession(true).getFeatureCollection(QueryBuilder.all(typeName));

        ShapefileReadWriteTest.compare(features, fc);
    }

    @Test
    public void testSkippingRecords() throws Exception {
        final URL url = ShapeTestData.url(STATEPOP);
        final ShpFiles shpFiles = new ShpFiles(url);
        final AccessManager locker = shpFiles.createLocker();
        ShapefileReader r = locker.getSHPReader(false, false, true, null);
        try {
            int idx = 0;
            while (r.hasNext()) {
                idx++;
                r.nextRecord();
            }
            assertEquals(49, idx);
        } finally {
            r.close();
        }
    }

    @Test
    public void testDuplicateColumnNames() throws Exception {
        File file = TestData.file(AbstractTestCaseSupport.class, "bad/state.shp");
        ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI().toURL());
        SimpleFeatureType schema = (SimpleFeatureType) dataStore.getFeatureType(dataStore.getNames().iterator().next());

        assertEquals(6, schema.getAttributeCount());
        assertTrue(dataStore.getCount(QueryBuilder.all(schema.getName())) > 0);
    }

    @Test
    public void testShapefileReaderRecord() throws Exception {
        final URL c1 = ShapeTestData.url(STATEPOP);
        final ShpFiles shpFiles = new ShpFiles(c1);
        final AccessManager locker = shpFiles.createLocker();
        ShapefileReader reader = locker.getSHPReader(false, false, true, null);
        URL c2;
        try {
            ArrayList offsets = new ArrayList();
            while (reader.hasNext()) {
                ShapefileReader.Record record = reader.nextRecord();
                offsets.add(new Integer(record.offset()));
                Geometry geom = (Geometry) record.shape();
                assertEquals(new Envelope(record.minX, record.maxX,
                        record.minY, record.maxY), geom.getEnvelopeInternal());
                record.toString();
            }
            copyShapefiles(STATEPOP);
            reader.close();
            c2 = TestData.url(AbstractTestCaseSupport.class, STATEPOP);
            final ShpFiles shpFiles2 = new ShpFiles(c2);
            final AccessManager locker2 = shpFiles.createLocker();
            reader = locker.getSHPReader(false, false, true, null);
            for (int i = 0, ii = offsets.size(); i < ii; i++) {
                reader.shapeAt(((Integer) offsets.get(i)).intValue());
            }
        } finally {
            reader.close();
        }
    }

    protected void loadShapes(final String resource, final int expected) throws Exception {
        final URL url = ShapeTestData.url(resource);
        final ShpFiles shpFiles = new ShpFiles(url);
        final AccessManager locker = shpFiles.createLocker();
        final ShapefileReader reader = locker.getSHPReader(false, false, true, null);
        int cnt = 0;
        try {
            while (reader.hasNext()) {
                reader.nextRecord().shape();
                cnt++;
            }
        } finally {
            reader.close();
        }
        assertEquals("Number of Geometries loaded incorect for : " + resource,
                expected, cnt);
    }

    protected void loadMemoryMapped(final String resource, final int expected)
            throws Exception {
        final URL url = ShapeTestData.url(resource);
        final ShpFiles shpFiles = new ShpFiles(url);
        final AccessManager locker = shpFiles.createLocker();
        final ShapefileReader reader = locker.getSHPReader(false, false, true, null);
        int cnt = 0;
        try {
            while (reader.hasNext()) {
                reader.nextRecord().shape();
                cnt++;
            }
        } finally {
            reader.close();
        }
        assertEquals("Number of Geometries loaded incorect for : " + resource,
                expected, cnt);
    }
}

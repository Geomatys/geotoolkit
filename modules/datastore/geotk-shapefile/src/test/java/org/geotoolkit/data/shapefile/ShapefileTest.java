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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.shapefile.shp.IndexFile;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.FeatureTypeUtilities;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.data.DefaultFeatureCollection;
import org.geotoolkit.data.session.Session;
import org.opengis.feature.type.Name;


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

    public ShapefileTest(String testName) throws IOException {
        super(testName);
    }

    public void testLoadingStatePop() throws Exception {
        loadShapes(STATEPOP, 49);
        loadMemoryMapped(STATEPOP, 49);
    }

    public void testLoadingSamplePointFile() throws Exception {
        loadShapes(POINTTEST, 10);
        loadMemoryMapped(POINTTEST, 10);
    }

    public void testLoadingSamplePolygonFile() throws Exception {
        loadShapes(POLYGONTEST, 2);
        loadMemoryMapped(POLYGONTEST, 2);
    }

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
    public void testPolygonHoleTouchAtEdge() throws Exception {
        loadShapes(HOLETOUCHEDGE, 1);
        loadMemoryMapped(HOLETOUCHEDGE, 1);
    }

    /**
     * It is posible for a shapefile to have extra information past the end of
     * the normal feature area, this tests checks that this situation is delt
     * with ok.
     */
    public void testExtraAtEnd() throws Exception {
        loadShapes(EXTRAATEND, 3);
        loadMemoryMapped(EXTRAATEND, 3);
    }

    public void testIndexFile() throws Exception {
        copyShapefiles(STATEPOP);
        copyShapefiles(STATEPOP_IDX);
        final URL url1 = ShapeTestData.url(STATEPOP); // Backed by InputStream
        final URL url2 = ShapeTestData.url(AbstractTestCaseSupport.class, STATEPOP); // Backed by File
        final URL url3 = ShapeTestData.url(AbstractTestCaseSupport.class, STATEPOP_IDX);
        final ShapefileReader reader1 = new ShapefileReader(new ShpFiles(url1),
                false, false);
        final ShapefileReader reader2 = new ShapefileReader(new ShpFiles(url2),
                false, false);
        final IndexFile index = new IndexFile(new ShpFiles(url3), false);
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

    public void testHolyPolygons() throws Exception {
        SimpleFeatureType type = FeatureTypeUtilities.createType("junk",
                "a:MultiPolygon");
        FeatureCollection<SimpleFeature> features = new DefaultFeatureCollection<SimpleFeature>("", type, SimpleFeature.class);

        File tmpFile = getTempFile();
        tmpFile.delete();

        // write features
        ShapefileDataStoreFactory make = new ShapefileDataStoreFactory();
        DataStore s = make.createNewDataStore(Collections.singletonMap("url", tmpFile.toURL()));
        s.createSchema(type.getName(),type);
        Name typeName = type.getName();

        Session session = s.createSession(true);
        session.addFeatures(typeName,features);
        session.commit();

        s = new ShapefileDataStore(tmpFile.toURL());
        typeName = s.getNames().iterator().next();
        FeatureCollection<SimpleFeature> fc = s.createSession(true).getFeatureCollection(QueryBuilder.all(typeName));

        ShapefileReadWriteTest.compare(features, fc);
    }

    public void testSkippingRecords() throws Exception {
        final URL url = ShapeTestData.url(STATEPOP);
        final ShapefileReader r = new ShapefileReader(new ShpFiles(url), false,
                false);
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

    public void testDuplicateColumnNames() throws Exception {
        File file = ShapeTestData.file(AbstractTestCaseSupport.class, "bad/state.shp");
        ShapefileDataStore dataStore = new ShapefileDataStore(file.toURL());
        SimpleFeatureType schema = (SimpleFeatureType) dataStore.getFeatureType(dataStore.getNames().iterator().next());

        assertEquals(6, schema.getAttributeCount());
        assertTrue(dataStore.getCount(QueryBuilder.all(schema.getName())) > 0);
    }

    public void testShapefileReaderRecord() throws Exception {
        final URL c1 = ShapeTestData.url(STATEPOP);
        ShapefileReader reader = new ShapefileReader(new ShpFiles(c1), false,
                false);
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
            c2 = ShapeTestData.url(AbstractTestCaseSupport.class, STATEPOP);
            reader = new ShapefileReader(new ShpFiles(c2), false, false);
            for (int i = 0, ii = offsets.size(); i < ii; i++) {
                reader.shapeAt(((Integer) offsets.get(i)).intValue());
            }
        } finally {
            reader.close();
        }
    }

    protected void loadShapes(String resource, int expected) throws Exception {
        final URL url = ShapeTestData.url(resource);
        ShapefileReader reader = new ShapefileReader(new ShpFiles(url), false,
                false);
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

    protected void loadMemoryMapped(String resource, int expected)
            throws Exception {
        final URL url = ShapeTestData.url(resource);
        ShapefileReader reader = new ShapefileReader(new ShpFiles(url), false,
                false);
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

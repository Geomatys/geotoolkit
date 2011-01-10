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
import java.util.ArrayList;

import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.shapefile.ShpFiles;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @version $Id$
 * @author Ian Schneider
 * @author James Macgill
 * @module pending
 */
public class ShapefileTest extends org.geotoolkit.data.shapefile.ShapefileTest {

    public ShapefileTest(final String testName) throws IOException {
        super(testName);
    }

    public void testShapefileReaderRecord() throws Exception {
        File file = copyShapefiles(STATEPOP);
        ShpFiles shpFiles = new ShpFiles(file.toURI().toURL());
        ShapefileReader reader = new ShapefileReader(shpFiles, false, false, true);
        ArrayList offsets = new ArrayList();

        while (reader.hasNext()) {
            ShapefileReader.Record record = reader.nextRecord();
            offsets.add(new Integer(record.offset()));

            Geometry geom = (Geometry) record.shape();
            assertEquals(new Envelope(record.minX, record.maxX, record.minY,
                    record.maxY), geom.getEnvelopeInternal());
            record.toString();
        }
        reader.close();
        copyShapefiles(STATEPOP);
        reader = new ShapefileReader(shpFiles, false, false, true);

        for (int i = 0, ii = offsets.size(); i < ii; i++) {
            reader.shapeAt(((Integer) offsets.get(i)).intValue());
        }
        reader.close();

    }

    protected void loadShapes(final String resource, final int expected) throws Exception {
        ShpFiles shpFiles = new ShpFiles(ShapeTestData.url(resource));
        ShapefileReader reader = new ShapefileReader(shpFiles, false, false, true);
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
        ShpFiles shpFiles = new ShpFiles(ShapeTestData.url(resource));
        ShapefileReader reader = new ShapefileReader(shpFiles, false, false, true);
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

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
import java.util.ArrayList;

import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.shapefile.lock.ShpFiles;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.data.shapefile.lock.AccessManager;

import static org.junit.Assert.*;

/**
 * @version $Id$
 * @author Ian Schneider
 * @author James Macgill
 * @module pending
 */
public class ShapefileTest extends org.geotoolkit.data.shapefile.ShapefileTest {

    @Override
    @Test
    public void testShapefileReaderRecord() throws Exception {
        final File file = copyShapefiles(STATEPOP);
        final ShpFiles shpFiles = new ShpFiles(file.toURI().toURL());
        final AccessManager locker = shpFiles.createLocker();
        
        ShapefileReader reader = locker.getSHPReader(false, false, true, null);
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
        reader = locker.getSHPReader(false, false, true, null);

        for (int i = 0, ii = offsets.size(); i < ii; i++) {
            reader.shapeAt(((Integer) offsets.get(i)).intValue());
        }
        reader.close();

    }

    @Override
    protected void loadShapes(final String resource, final int expected) throws Exception {
        final ShpFiles shpFiles = new ShpFiles(ShapeTestData.url(resource));
        final AccessManager locker = shpFiles.createLocker();
        ShapefileReader reader = locker.getSHPReader(false, false, true, null);
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

    @Override
    protected void loadMemoryMapped(final String resource, final int expected)
            throws Exception {
        final ShpFiles shpFiles = new ShpFiles(ShapeTestData.url(resource));
        final AccessManager locker = shpFiles.createLocker();
        ShapefileReader reader = locker.getSHPReader(false, false, true, null);
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

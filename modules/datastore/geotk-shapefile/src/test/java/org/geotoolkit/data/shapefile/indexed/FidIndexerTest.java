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

import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.shapefile.lock.ShpFiles;
import org.geotoolkit.data.shapefile.fix.IndexedFidReader;
import org.geotoolkit.data.shapefile.fix.IndexedFidWriter;
import org.geotoolkit.data.shapefile.lock.AccessManager;

import static org.junit.Assert.*;

public class FidIndexerTest extends FIDTestCase {
    

    /*
     * Test method for 'org.geotoolkit.index.fid.FidIndexer.generate(URL)'
     */
    @Test
    public void testGenerate() throws Exception {
        final ShpFiles shpFiles = new ShpFiles(backshp.toURI().toURL());
        IndexedFidWriter.generate(shpFiles);
        final AccessManager locker = shpFiles.createLocker();

        final IndexedShapefileDataStore ds = new IndexedShapefileDataStore(backshp
                .toURI().toURL(), null, false, false, IndexType.NONE,null);

        long features = ds.getCount(QueryBuilder.all(ds.getNames().iterator().next()));

        final IndexedFidReader reader = locker.getFIXReader(null);

        try {
            assertEquals(features, reader.getCount());

            int i = 1;

            while (reader.hasNext()) {
                assertEquals(shpFiles.getTypeName() + "." + i, reader.next());
                assertEquals(shpFiles.getTypeName() + "." + i, i - 1, reader.currentSHXIndex());
                i++;
            }

            assertEquals(features, i - 1);
        } finally {
            reader.close();
        }
    }
}

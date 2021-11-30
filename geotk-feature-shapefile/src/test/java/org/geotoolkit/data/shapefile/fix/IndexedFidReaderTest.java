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
package org.geotoolkit.data.shapefile.fix;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.data.shapefile.indexed.FIDTestCase;
import org.geotoolkit.data.shapefile.indexed.IndexType;
import org.geotoolkit.data.shapefile.indexed.IndexedShapefileFeatureStore;
import org.geotoolkit.data.shapefile.lock.AccessManager;
import org.geotoolkit.data.shapefile.lock.ShpFileType;
import org.geotoolkit.data.shapefile.lock.ShpFiles;
import org.geotoolkit.data.shapefile.shx.ShxReader;
import org.geotoolkit.storage.feature.query.Query;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Feature;

public class IndexedFidReaderTest extends FIDTestCase {

    private AccessManager locker;
    private IndexedFidReader reader;
    private ShxReader indexFile;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        final ShpFiles shpFiles = new ShpFiles(backshp.toURI().toURL());
        IndexedFidWriter.generate(shpFiles);

        locker = shpFiles.createLocker();
        indexFile = locker.getSHXReader(false);
        reader = locker.getFIXReader(null);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        locker.dispose();
        super.tearDown();
    }

    /*
     * Test method for 'org.geotoolkit.index.fid.IndexedFidReader.findFid(String)'
     */
    @Test
    public void testFindFid() throws Exception {
        long offset = reader.findFid(TYPE_NAME + ".4");
        assertEquals(3, offset);

        offset = reader.findFid(TYPE_NAME + ".1");
        assertEquals(0, offset);

        // test if the fid is too high
        offset = reader.findFid(TYPE_NAME + ".10000000");
        assertEquals(-1, offset);

        // test if the fid is negative
        offset = reader.findFid(TYPE_NAME + ".-1");
        assertEquals(-1, offset);

        // test if the fid does not match the <typeName>.<long> pattern
        offset = reader.findFid(TYPE_NAME + ".1ABC");
        assertEquals(-1, offset);

        offset = reader.findFid("prefix" + TYPE_NAME + ".1");
        assertEquals(-1, offset);
   }

    @Test
    public void testFindAllFids() throws Exception {
        int expectedCount = 0;
        final Set<String> expectedFids = new LinkedHashSet<>();

        final IndexedShapefileFeatureStore ds = new IndexedShapefileFeatureStore(backshp.toURI(),
                true, true, IndexType.NONE,null);
        final FeatureIterator features = ds.getFeatureReader(new Query(ds.getNames().iterator().next()));
        while (features.hasNext()) {
            final Feature next = features.next();
            expectedCount++;
            expectedFids.add(FeatureExt.getId(next).getIdentifier());
        }
        features.close();

        assertTrue(expectedCount > 0);
        assertEquals(expectedCount, reader.getCount());

        for(String fid : expectedFids){
            final long offset = reader.findFid(fid);
            assertFalse(-1 == offset);
        }
    }

    @Test
    public void testFindAllFidsReverseOrder() throws Exception {
        int expectedCount = 0;
        final Set<String> expectedFids = new TreeSet<>(Collections.reverseOrder());
        final IndexedShapefileFeatureStore ds = new IndexedShapefileFeatureStore(backshp.toURI(),
                true, true, IndexType.NONE,null);
        final FeatureIterator features = ds.getFeatureReader(new Query(ds.getNames().iterator().next()));
        while (features.hasNext()) {
            final Feature next = features.next();
            expectedCount++;
            expectedFids.add(FeatureExt.getId(next).getIdentifier());
        }
        features.close();

        assertTrue(expectedCount > 0);
        assertEquals(expectedCount, reader.getCount());

        assertFalse("findFid for archsites.5 returned -1",-1 == reader.findFid("archsites.5"));
        assertFalse("findFid for archsites.25 returned -1",-1 == reader.findFid("archsites.25"));

        for(String fid : expectedFids){
            final long offset = reader.findFid(fid);
            assertNotNull(offset);
//            System.out.println(fid + "=" + offset + ", ");
            assertFalse("findFid for " + fid + " returned -1", -1 == offset);
        }
    }

    // test if FID no longer exists.
    @Test
    public void testFindDeletedFID() throws Exception {
        reader.close();

        final ShpFiles shpFiles = new ShpFiles(fixFile);
        final AccessManager locker = shpFiles.createLocker();
        final IndexedFidWriter writer = locker.getFIXWriter(locker.getStorageFile(ShpFileType.FIX));
        try {
            writer.next();
            writer.next();
            writer.next();
            writer.remove();
            while( writer.hasNext() ) {
                writer.next();
            }
        } finally {
            locker.disposeReaderAndWriters();
            locker.replaceStorageFiles();
        }

        reader = locker.getFIXReader(null);

        long offset = reader.findFid(TYPE_NAME + ".11");
        assertEquals(9, offset);

        offset = reader.findFid(TYPE_NAME + ".4");
        assertEquals(2, offset);

        offset = reader.findFid(TYPE_NAME + ".3");
        assertEquals(-1, offset);

        locker.dispose();
    }

    @Test
    public void testHardToFindFid() throws Exception {
        final long offset = reader.search(5, 3, 7, 5);
        assertEquals(4, offset);
    }

    /*
     * Test method for 'org.geotoolkit.index.fid.IndexedFidReader.goTo(int)'
     */
    @Test
    public void testGoTo() throws IOException {
        reader.goTo(10);
        assertEquals(shpFiles.getTypeName() + ".11", reader.next());
        assertTrue(reader.hasNext());

        reader.goTo(15);
        assertEquals(shpFiles.getTypeName() + ".16", reader.next());
        assertTrue(reader.hasNext());

        reader.goTo(0);
        assertEquals(shpFiles.getTypeName() + ".1", reader.next());
        assertTrue(reader.hasNext());

        reader.goTo(3);
        assertEquals(shpFiles.getTypeName() + ".4", reader.next());
        assertTrue(reader.hasNext());
    }
}

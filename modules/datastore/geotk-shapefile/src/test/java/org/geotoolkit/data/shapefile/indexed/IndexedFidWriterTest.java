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

import java.io.IOException;
import java.net.MalformedURLException;

import org.geotoolkit.data.shapefile.fix.IndexedFidReader;
import org.geotoolkit.data.shapefile.fix.IndexedFidWriter;
import org.geotoolkit.data.shapefile.lock.AccessManager;
import org.geotoolkit.data.shapefile.lock.ShpFileType;
import org.geotoolkit.data.shapefile.shx.ShxReader;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class IndexedFidWriterTest extends FIDTestCase {
    
    private AccessManager locker;
    private ShxReader indexFile;
    private IndexedFidWriter writer;
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        close();
        super.tearDown();
    }

    private void initWriter() throws IOException, MalformedURLException {
        close();
        locker = shpFiles.createLocker();
        indexFile = locker.getSHXReader(false);
        writer = locker.getFIXWriter(locker.getStorageFile(ShpFileType.FIX));
    }
     
    private void close() throws IOException {
        //will close index reader and writer
        if(locker != null){
            locker.dispose();
            locker.replaceStorageFiles();
        }
    }

    /*
     * Test method for 'org.geotoolkit.index.fid.IndexedFidWriter.hasNext()'
     */
    @Test
    public void testHasNext() throws MalformedURLException, IOException {
        IndexedFidWriter.generate(backshp.toURI().toURL());
        initWriter();

        for( int i = 1, j = indexFile.getRecordCount(); i < j; i++ ) {
            assertTrue(i + "th record", writer.hasNext());
            assertEquals((long) i, writer.next());
        }
    }

    /*
     * Test method for 'org.geotoolkit.index.fid.IndexedFidWriter.remove()'
     */
    @Test
    public void testRemove() throws MalformedURLException, IOException {
        IndexedFidWriter.generate(backshp.toURI().toURL());
        initWriter();
        writer.next();
        writer.remove();

        for( int i = 2, j = indexFile.getRecordCount(); i < j; i++ ) {
            assertTrue(writer.hasNext());
            assertEquals((long) i, writer.next());
        }

        writer.write();
        close();

        initWriter();

        for( int i = 1, j = indexFile.getRecordCount() - 1; i < j; i++ ) {
            assertTrue(writer.hasNext());
            assertEquals((long) i + 1, writer.next());
        }
    }

    @Test
    public void testRemoveCounting() throws Exception {
        final AccessManager locker = shpFiles.createLocker();
        
        IndexedFidWriter.generate(backshp.toURI().toURL());
        initWriter();
        writer.next();
        writer.remove();
        writer.next();
        writer.remove();
        writer.next();
        writer.remove();

        while( writer.hasNext() ) {
            writer.next();
            writer.write();
        }

        close();
        IndexedFidReader reader = locker.getFIXReader(null);
        try {
            assertEquals(3, reader.getRemoves());
        } finally {
            reader.close();
        }

        // remove some more features
        initWriter();
        writer.next();
        writer.next();
        writer.next();
        writer.remove();
        writer.next();
        writer.remove();
        writer.next();
        writer.next();
        writer.next();
        writer.remove();
        while( writer.hasNext() ) {
            writer.next();
            writer.write();
        }

        close();

        reader = locker.getFIXReader(null);
        try {
            assertEquals(6, reader.getRemoves());
        } finally {
            reader.close();
        }

    }

    /*
     * Test method for 'org.geotoolkit.index.fid.IndexedFidWriter.write()'
     */
    @Test
    public void testWrite() throws IOException {
        initWriter();

        for( int i = 0; i < 5; i++ ) {
            writer.next();
            writer.write();
        }

        close();
        initWriter();

        for( int i = 1; i < 5; i++ ) {
            assertTrue(writer.hasNext());
            assertEquals((long) i, writer.next());
        }
    }

}

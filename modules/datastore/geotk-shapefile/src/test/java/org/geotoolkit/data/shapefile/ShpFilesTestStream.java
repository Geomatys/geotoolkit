/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.data.shapefile.lock.StorageFile;
import org.geotoolkit.data.shapefile.lock.ShpFiles;
import static org.geotoolkit.data.shapefile.lock.ShpFileType.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.geotoolkit.ShapeTestData;

import org.geotoolkit.data.shapefile.lock.ShpFileType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShpFilesTestStream {

    private String typeName;
    private Map<ShpFileType, File> map;
    private ShpFiles files;

    @Before
    public void setUp() throws Exception {
        map = ShpFilesTest.createFiles("shpFiles", ShpFileType.values(), false);

        typeName = map.get(SHP).getName();
        typeName = typeName.substring(0, typeName.lastIndexOf("."));

        files = new ShpFiles(map.get(SHP));
    }

    private void writeDataToFiles() throws IOException {
        Set<Entry<ShpFileType, File>> entries = map.entrySet();
        for (Entry<ShpFileType, File> entry : entries) {
            FileWriter out = new FileWriter(entry.getValue());
            try {
                out.write(entry.getKey().name());
            } finally {
                out.close();
            }
        }
    }

    @Test
    public void testIsLocalURL() throws IOException {
        ShpFiles files = new ShpFiles("http://someurl.com/file.shp");
        assertFalse(files.isLocal());
    }

    @Test
    public void testIsLocalFiles() throws IOException {
        assertTrue(files.isLocal());
    }

    @Test
    public void testDelete() throws IOException {

        assertTrue(files.delete());

        for (File file : map.values()) {
            assertFalse(file.exists());
        }
    }

    @Test
    public void testExceptionGetInputStream() throws Exception {
        ShpFiles shpFiles = new ShpFiles(new URL("http://blah/blah.shp"));
        try{
            shpFiles.getInputStream(SHP);
            fail("maybe test is bad?  We want an exception here");
        }catch(Throwable e){
        }
    }

    @Test
    public void testExceptionGetOutputStream() throws Exception {
        ShpFiles shpFiles = new ShpFiles(new URL("http://blah/blah.shp"));
        try{
            shpFiles.getOutputStream(SHP);
            fail("maybe test is bad?  We want an exception here");
        }catch(Throwable e){
        }
    }

    @Test
    public void testExceptionGetWriteChannel() throws Exception {
        ShpFiles shpFiles = new ShpFiles(new URL("http://blah/blah.shp"));
        try{
            shpFiles.getWriteChannel(SHP);
            fail("maybe test is bad?  We want an exception here");
        }catch(Throwable e){
        }
    }

    @Test
    public void testExceptionGetReadChannel() throws Exception {
        ShpFiles shpFiles = new ShpFiles(new URL("http://blah/blah.shp"));
        try{
            shpFiles.getReadChannel(SHP);
            fail("maybe test is bad?  We want an exception here");
        }catch(Throwable e){
        }
    }
    
    @Test
    public void testGetInputStream() throws IOException {
        writeDataToFiles();

        ShpFileType[] types = ShpFileType.values();
        for (ShpFileType shpFileType : types) {
            String read = "";
            InputStream in = files.getInputStream(shpFileType);
            InputStreamReader reader = new InputStreamReader(in);
            try {
                int current = reader.read();
                while (current != -1) {
                    read += (char) current;
                    current = reader.read();
                }
            } finally {
                reader.close();
                in.close();
            }
            assertEquals(shpFileType.name(), read);
        }
    }

    @Test
    public void testGetWriteStream() throws IOException {

        ShpFileType[] types = ShpFileType.values();
        for (ShpFileType shpFileType : types) {
            
            OutputStream out = files.getOutputStream(shpFileType);
            try {
                out.write((byte)2);
            } finally {
                out.close();
            }
        }
    }

    @Test
    public void testGetReadChannelFileChannel() throws IOException {
        writeDataToFiles();

        ShpFileType[] types = ShpFileType.values();
        for (ShpFileType shpFileType : types) {
            doRead(shpFileType);
        }
    }

    @Test
    public void testGetReadChannelURL() throws IOException {
        ShpFiles files = new ShpFiles(ShapeTestData.url("shapes/statepop.shp"));
        
//        assertFalse(files.isLocal());
        
        ReadableByteChannel read = files.getReadChannel(SHP);        
        read.close();
    }
    private void doRead(final ShpFileType shpFileType) throws IOException {
        ReadableByteChannel in = files.getReadChannel(shpFileType);
        assertTrue(in instanceof FileChannel);

        ByteBuffer buffer = ByteBuffer.allocate(10);
        in.read(buffer);
        buffer.flip();
        String read = "";
        try {
            while (buffer.hasRemaining()) {
                read += (char) buffer.get();
            }
        } finally {
            in.close();
            // verify that you can close multiple times without bad things
            // happening
            in.close();
        }
        assertEquals(shpFileType.name(), read);
    }

    private void doWrite(final ShpFileType shpFileType) throws IOException {
        WritableByteChannel out = files.getWriteChannel(shpFileType);
        assertTrue(out instanceof FileChannel);

        try {
            ByteBuffer buffer = ByteBuffer.allocate(10);
            buffer.put(shpFileType.name().getBytes());
            buffer.flip();
            out.write(buffer);
        } finally {
            out.close();
            // verify that you can close multiple times without bad things
            // happening
            out.close();
        }
    }

    @Test
    public void testGetWriteChannel() throws IOException {

        ShpFileType[] types = ShpFileType.values();
        for (ShpFileType shpFileType : types) {
            doWrite(shpFileType);
            doRead(shpFileType);
        }
    }

    @Test
    public void testGetStorageFile() throws Exception {
        StorageFile prj = files.createLocker().getStorageFile(PRJ);
        assertTrue(prj.getFile().getName().startsWith(typeName));
        assertTrue(prj.getFile().getName().endsWith(".prj"));
    }

    @Test
    public void testGetTypeName() throws Exception {
        assertEquals(typeName, files.getTypeName());
    }

    public String id() {
        return getClass().getName();
    }

}

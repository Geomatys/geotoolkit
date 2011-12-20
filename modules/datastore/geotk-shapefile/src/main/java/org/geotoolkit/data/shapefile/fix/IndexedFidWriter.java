/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.net.URL;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;

import org.geotoolkit.data.shapefile.lock.AccessManager;
import org.geotoolkit.data.shapefile.lock.ShpFiles;
import org.geotoolkit.data.shapefile.lock.StorageFile;
import org.geotoolkit.data.shapefile.shx.ShxReader;

import static org.geotoolkit.data.shapefile.lock.ShpFileType.*;
import static org.geotoolkit.data.shapefile.ShapefileDataStoreFactory.*;
import org.geotoolkit.io.Closeable;

/**
 * The Writer writes out the fid and record number of features to the fid index file.
 * 
 * @author Jesse
 * @module pending
 */
public class IndexedFidWriter implements Closeable {
    public static final int HEADER_SIZE = 13;
    public static final int RECORD_SIZE = 12;
    private FileChannel channel;
    private ByteBuffer writeBuffer;
    private IndexedFidReader reader;
    long fidIndex;
    private int recordIndex;
    private boolean closed;

    private long current;

    private long position;
    private int removes;

    /**
     * Create a new instance<br>
     * Note: {@link StorageFile#replaceOriginal()} is NOT called.  Call {@link #IndexedFidWriter(ShpFiles)} for that 
     * behaviour.  
     * @param shpFiles The shapefiles to used
     * @param storageFile the storage file that will be written to.  It will NOT be closed.
     * @throws IOException
     */
    public IndexedFidWriter(final URL fixUrl, final ReadableByteChannel readfixChannel, 
            final FileChannel writeChannel ) throws IOException {
        // Note do NOT assign storageFile so that it is closed because this method method requires that
        // the caller close the storage file.
        // Call the single argument constructor instead
        
        reader = new IndexedFidReader(fixUrl,readfixChannel,null);
        
        this.channel = writeChannel;
        allocateBuffers();
        removes = reader.getRemoves();
        writeBuffer.position(HEADER_SIZE);
        closed = false;
        position = 0;
        current = -1;
        recordIndex = 0;
        fidIndex = 0;
    }

    private IndexedFidWriter() {
    }

    /**
     * Allocate some buffers for writing.
     */
    private void allocateBuffers() {
        writeBuffer = ByteBuffer.allocateDirect(HEADER_SIZE + RECORD_SIZE * 1024);
    }

    /**
     * Drain internal buffers into underlying channels.
     * 
     * @throws IOException DOCUMENT ME!
     */
    private void drain() throws IOException {
        writeBuffer.flip();

        int written = 0;

        while( writeBuffer.remaining() > 0 )
            written += channel.write(writeBuffer, position);

        position += written;

        writeBuffer.flip().limit(writeBuffer.capacity());
    }

    private void writeHeader() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);

        buffer.put((byte) 1);

        buffer.putLong(recordIndex);
        buffer.putInt(removes);
        buffer.flip();
        channel.write(buffer, 0);
    }

    public boolean hasNext() throws IOException {
        return reader.hasNext();
    }

    public long next() throws IOException {

        if (current != -1)
            write();

        if (reader.hasNext()) {
            reader.next();
            fidIndex = reader.getCurrentFIDIndex();
        } else {
            fidIndex++;
        }

        current = fidIndex;

        return fidIndex;
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }

        try {
            finishLastWrite();
        } finally {
            try {
                reader.close();
            } finally {
                closeWriterChannels();
            }
        }

        closed = true;
    }

    private void closeWriterChannels() throws IOException {
        if (channel.isOpen()){
            channel.close();
        }
    }

    private void finishLastWrite() throws IOException {
        while( hasNext() )
            next();

        if (current != -1)
            write();

        drain();
        writeHeader();
    }

    /**
     * Increments the fidIndex by 1. Indicates that a feature was removed from the location. This is
     * intended to ensure that FIDs stay constant over time. Consider the following case of 5
     * features. feature 1 has fid typename.0 feature 2 has fid typename.1 feature 3 has fid
     * typename.2 feature 4 has fid typename.3 feature 5 has fid typename.4 when feature 3 is
     * removed/deleted the following usage of the write should take place: next(); (move to feature
     * 1) next(); (move to feature 2) next(); (move to feature 3) remove();(delete feature 3)
     * next(); (move to feature 4) // optional write(); (write feature 4) next(); (move to feature
     * 5) write(); (write(feature 5)
     * 
     * @throws IOException
     */
    public void remove() throws IOException {
        if (current == -1)
            throw new IOException("Current fid index is null, next must be called before remove");
        if (hasNext()) {
            removes++;
            current = -1;

            // reader.next();
        }
    }

    /**
     * Writes the current fidIndex. Writes to the same place in the file each time. Only
     * {@link #next()} moves forward in the file.
     * 
     * @throws IOException
     * @see #next()
     * @see #remove()
     */
    public void write() throws IOException {
        if (current == -1)
            throw new IOException("Current fid index is null, next must be called before write()");

        if (writeBuffer == null) {
            allocateBuffers();
        }

        if (writeBuffer.remaining() < RECORD_SIZE) {
            drain();
        }

        writeBuffer.putLong(current);
        writeBuffer.putInt(recordIndex);

        recordIndex++;
        current = -1;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    /**
     * Generates the FID index file for the shpFile
     */
    public static synchronized void generate(final URL shpURL) throws IOException {
        generate(new ShpFiles(shpURL));
    }

    /**
     * Generates the FID index file for the shpFiles
     */
    public static void generate(final ShpFiles shpFiles) throws IOException {
        LOGGER.log(Level.FINE, "Generating fids for {0}", shpFiles.get(SHP));

        final AccessManager locker = shpFiles.createLocker();
        
        ShxReader indexFile = null;
        StorageFile file = locker.getStorageFile(FIX);
        IndexedFidWriter writer = null;

        try {
            indexFile = locker.getSHXReader(false);

            // writer closes channel for you.
            writer = locker.getFIXWriter(file);

            for (int i = 0, j = indexFile.getRecordCount(); i < j; i++) {
                writer.next();
            }

        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                locker.disposeReaderAndWriters();
                locker.replaceStorageFiles();
            } finally {
                if (indexFile != null) {
                    indexFile.close();
                }
            }
        }
    }

    public static final IndexedFidWriter EMPTY_WRITER = new IndexedFidWriter(){
        @Override
        public void close() throws IOException {
        }
        @Override
        public boolean hasNext() throws IOException {
            return false;
        }
        @Override
        public boolean isClosed() {
            return false;
        }
        @Override
        public void write() throws IOException {
        }
        @Override
        public long next() throws IOException {
            return 0;
        }
        @Override
        public void remove() throws IOException {
        }
    };
}

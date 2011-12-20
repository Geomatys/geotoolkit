/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.shapefile.lock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.geotoolkit.data.shapefile.ShapefileDataStoreFactory.*;
import static org.geotoolkit.data.shapefile.lock.ShpFiles.*;

/**
 * Encapsulates the idea of a file for writing data to and then later updating the original.
 * 
 * @author jesse
 * @module pending
 */
public final class StorageFile implements Comparable<StorageFile> {
    private final ShpFiles shpFiles;
    private final File tempFile;
    private final ShpFileType type;

    StorageFile(final ShpFiles shpFiles, final File tempFile, final ShpFileType type) {
        this.shpFiles = shpFiles;
        this.tempFile = tempFile;
        this.type = type;
    }

    /**
     * Returns the storage file
     * 
     * @return the storage file
     */
    public File getFile() {
        return tempFile;
    }

    public FileChannel getWriteChannel() throws IOException {
        return new RandomAccessFile(tempFile, "rw").getChannel();
    }

    /**
     * Replaces the file that the temporary file is acting as a transactional type cache for. Acts
     * similar to a commit.
     * 
     * @see #replaceOriginals(StorageFile...)
     * @throws IOException
     */
    void replaceOriginal() throws IOException {
        replaceOriginals(this);
    }

    /**
     * Takes a collection of StorageFiles and performs the replace functionality described in
     * {@link #replaceOriginal()}. However, all files that are part of the same {@link ShpFiles}
     * are done within a lock so all of the updates for all the Files of a Shapefile can be updated
     * within a single lock.
     * 
     * @param storageFiles files to execute the replace functionality.
     * @throws IOException
     */
    static void replaceOriginals( final StorageFile... storageFiles ) throws IOException {
        SortedSet<StorageFile> files = new TreeSet<StorageFile>(Arrays.asList(storageFiles));

        ShpFiles currentShpFiles = null;
        for( StorageFile storageFile : files ) {
            if (currentShpFiles != storageFile.shpFiles) {
                // there's a new set of files so unlock old and lock new.
                currentShpFiles = storageFile.shpFiles;
            }

            final File storage = storageFile.getFile();

            final URL url = storageFile.getSrcURLForWrite();
            try {
                File dest = toFile(url);

                if (storage.equals(dest))
                    return;

                if (dest.exists()) {
                    if (!dest.delete()){
                        LOGGER.severe("Unable to delete the file: "+dest+" when attempting to replace with temporary copy.");
                    }
                }

                if (storage.exists() && !storage.renameTo(dest)) {
                    LOGGER.finer("Unable to rename temporary file to the file: "+dest+" when attempting to replace with temporary copy");
                    
                    copyFile(storage, url, dest);
                    if (!storage.delete()) {
                        storage.deleteOnExit();
                    }
                }
            } finally {
                if (storage.exists()) {
                    storage.delete();
                }
            }
        }

    }

    private static void copyFile( final File storage, final URL url, final File dest ) throws FileNotFoundException,
            IOException {
        FileChannel in = null;
        FileChannel out = null;
        try {
            in = new FileInputStream(storage).getChannel();
            out = new FileOutputStream(dest).getChannel();

            // magic number for Windows, 64Mb - 32Kb)
            int maxCount = (64 * 1024 * 1024) - (32 * 1024);
            long size = in.size();
            long position = 0;
            while( position < size ) {
                position += in.transferTo(position, maxCount, out);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private URL getSrcURLForWrite() {
        return shpFiles.getURL(type);
    }

    /**
     * Just groups together files that have the same ShpFiles instance
     */
    @Override
    public int compareTo( final StorageFile o ) {
        // group togheter files that have the same shpefile instance
        if (this == o) {
            return 0;
        }
        
        // assume two StorageFile that do not share the same ShpFiles
        // are not given the same temp file
        return getFile().compareTo(o.getFile());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + tempFile.getName();
    }

}

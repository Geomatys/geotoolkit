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

import org.geotoolkit.nio.IOUtilities;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import static java.nio.file.StandardOpenOption.*;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory.*;
import static org.geotoolkit.data.shapefile.lock.ShpFiles.*;

/**
 * Encapsulates the idea of a file for writing data to and then later updating the original.
 * 
 * @author jesse
 * @module pending
 */
public final class StorageFile implements Comparable<StorageFile> {
    private final ShpFiles shpFiles;
    private final Path tempFile;
    private final ShpFileType type;

    @Deprecated
    StorageFile(final ShpFiles shpFiles, final File tempFile, final ShpFileType type) {
       this(shpFiles, tempFile.toPath(), type);
    }

    StorageFile(final ShpFiles shpFiles, final Path tempFile, final ShpFileType type) {
        this.shpFiles = shpFiles;
        this.tempFile = tempFile;
        this.type = type;
    }

    /**
     * Returns the storage file
     * 
     * @return the storage file
     */
    public Path getFile() {
        return tempFile;
    }

    public FileChannel getWriteChannel() throws IOException {
        return FileChannel.open(tempFile, READ, CREATE, WRITE);
//        return new RandomAccessFile(tempFile, "rw").getChannel();
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
        SortedSet<StorageFile> files = new TreeSet<>(Arrays.asList(storageFiles));

        ShpFiles currentShpFiles = null;
        for( StorageFile storageFile : files ) {
            if (currentShpFiles != storageFile.shpFiles) {
                // there's a new set of files so unlock old and lock new.
                currentShpFiles = storageFile.shpFiles;
            }

            final Path storage = storageFile.getFile();

            final URI url = storageFile.getSrcURLForWrite();
            try {
                Path dest = toPath(url);

                if (storage.equals(dest))
                    return;

                try {
                    Files.deleteIfExists(dest);
                } catch (IOException ex) {
                    LOGGER.severe("Unable to delete the file: "+dest+" when attempting to replace with temporary copy.");
                }

                if (Files.exists(storage)) {
                    try {
                        Files.move(storage, dest, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException | UnsupportedOperationException e) {
                        LOGGER.log(Level.FINER, "Unable to replace temporary file to the file: " + dest +
                                " when attempting to replace with temporary copy", e);
                        IOUtilities.copy(storage, dest, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } finally {
                Files.deleteIfExists(storage);
            }
        }
    }

    private URI getSrcURLForWrite() {
        return shpFiles.getURI(type);
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
        return getClass().getSimpleName() + ": " + tempFile.getFileName().toString();
    }

}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2019, Geomatys
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
package org.geotoolkit.data.mapinfo.mif;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * An iterator to write features into MIF/MID files.
 *
 * @author Alexis Manin (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class MIFWriter implements Iterator<Feature>, AutoCloseable {

    private final MIFManager master;
    private MIFReader reader;

    private final FeatureType writeType;
    private Feature currentFeature;

    private Path tmpMifFile;
    private BufferedWriter tmpMifWriter;

    private Path tmpMidFile;
    private BufferedWriter tmpMidWriter;

    public MIFWriter(MIFManager parent, MIFReader readingIterator) throws DataStoreException {
        ArgumentChecks.ensureNonNull("File manager", parent);
        ArgumentChecks.ensureNonNull("MIF reader", readingIterator);
        master = parent;
        reader = readingIterator;
        writeType = reader.getFeatureType();

        // Initialize temp containers
        try {
            tmpMidFile = Files.createTempFile(UUID.randomUUID().toString(), ".mid");
            tmpMifFile = Files.createTempFile(UUID.randomUUID().toString(), ".mif");

            tmpMifWriter = Files.newBufferedWriter(tmpMifFile, master.getCharset());
            tmpMidWriter = Files.newBufferedWriter(tmpMidFile, master.getCharset());
        } catch (IOException e) {
            throw new DataStoreException("Unable to initialize Feature writer.", e);
        }

    }

    public FeatureType getFeatureType() {
        return writeType;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        if (reader == null) {
            throw new FeatureStoreRuntimeException("Writer has been closed");
        }

        // we have to write the current feature back into the stream
        if (currentFeature != null) {
            write();
        }

        // is there another? If so, return it
        if (reader.hasNext()) {
            try {
                return currentFeature = reader.next();
            } catch (IllegalArgumentException iae) {
                throw new FeatureStoreRuntimeException("Error in reading", iae);
            }
        }

        // reader has no more elements, switch to append mode.
        try {
            currentFeature = writeType.newInstance();
            return currentFeature;
        } catch (IllegalArgumentException iae) {
            throw new FeatureStoreRuntimeException("Error creating empty Feature", iae);
        }
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        if (reader == null) {
            throw new FeatureStoreRuntimeException("Writer has been closed");
        }
        return reader.hasNext();
    }

    @Override
    public void remove() throws FeatureStoreRuntimeException {
        if (reader == null) {
            throw new FeatureStoreRuntimeException("Writer has been closed.");
        }

        if (currentFeature == null) {
            throw new FeatureStoreRuntimeException("The feature writer does not point on any feature.");
        }

        // mark current feature as null, so it won't be rewritten to the stream
        currentFeature = null;
    }

    public void write() throws FeatureStoreRuntimeException {
        if (currentFeature == null) {
            throw new FeatureStoreRuntimeException("There's no feature to write (null value).");
        }

        if (reader == null) {
            throw new FeatureStoreRuntimeException("Writer has been closed");
        }

        try {
            final String mifGeom = MIFUtils.buildMIFGeometry(currentFeature);
            tmpMifWriter.write(mifGeom);
        } catch (Exception e) {
            throw new FeatureStoreRuntimeException("A problem occurred while building geometry.", e);
        }

        try {
            if (master.getBaseType() != null && master.getBaseType().getProperties(true).size()> 0) {
                final String midAttributes = master.buildMIDAttributes(currentFeature);
                tmpMidWriter.write(midAttributes);
            }
        } catch (Exception e) {
            throw new FeatureStoreRuntimeException("A problem occurred while building MID attributes.", e);
        }


        currentFeature = null;
    }

    @Override
    public void close() throws FeatureStoreRuntimeException {
        if (reader == null) {
            throw new FeatureStoreRuntimeException("Writer has already been closed");
        }

        // make sure to write the last feature...
        if (currentFeature != null) {
            write();
        }

        reader.close();
        reader = null;

        try {
            tmpMidWriter.close();
            tmpMidWriter = null;
        } catch (Exception e) {
            MIFManager.LOGGER.log(Level.WARNING, "Temporary MIF data writer can't be closed", e);
        }

        try {
            tmpMifWriter.close();
            tmpMifWriter = null;
        } catch (Exception e) {
            MIFManager.LOGGER.log(Level.WARNING, "Temporary MIF data writer can't be closed", e);
        }

        try {
            master.flushData(tmpMifFile, tmpMidFile);
        } catch(Exception ex) {
            throw new FeatureStoreRuntimeException("Data flushing impossible, there's a possibility of data loss.");
        } finally {
            try {
                Files.delete(tmpMidFile);
                Files.delete(tmpMifFile);
            } catch (IOException ex) {
                MIFManager.LOGGER.log(Level.FINE, "Cannot delete temporary files", ex);
            }
        }
    }

    /**
     * In case someone doesn't close me.
     */
    @Override
    protected void finalize() {
        if (reader != null) {
            try {
                close();
            } catch (Exception e) {
                MIFManager.LOGGER.log(Level.WARNING, "Can't close MIF feature writer", e);
            }
        }
    }
}

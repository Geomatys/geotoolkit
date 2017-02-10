/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
import java.util.UUID;
import java.util.logging.Level;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;

import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;

/**
 * An iterator to write features into MIF/MID files.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 06/03/13
 */
public class MIFFeatureWriter implements FeatureWriter {

    private final MIFManager master;
    private MIFFeatureReader reader;

    private final FeatureType writeType;
    private Feature currentFeature;

    private int featureCount = 0;

    private Path tmpMifFile;
    private BufferedWriter tmpMifWriter;

    private Path tmpMidFile;
    private BufferedWriter tmpMidWriter;

    public MIFFeatureWriter(MIFManager parent, MIFFeatureReader readingIterator) throws DataStoreException {
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

    @Override
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

        // reader has no more (no were are adding to the file)
        // so return an empty feature
        try {
            final String featureID = getFeatureType().getName().tip().toString()+"."+(featureCount++);
            return currentFeature = FeatureUtilities.defaultFeature(writeType, featureID);
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


    @Override
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
            if (master.getBaseType() != null && master.getBaseType().getDescriptors().size()> 0) {
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

        // Update real data files
        try {
            master.flushData(tmpMifFile, tmpMidFile);
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException("Data flushing impossible, there's a possibility of data loss.", ex);
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

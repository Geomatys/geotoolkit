/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.storage.geojson;

import com.fasterxml.jackson.core.JsonEncoding;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.locks.ReadWriteLock;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.internal.geojson.GeoJSONParser;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
final class GeoJSONFileWriter extends GeoJSONReader {

    private final GeoJSONWriter writer;

    private Feature edited;
    private Feature lastWritten;
    private Path tmpFile;

    public GeoJSONFileWriter(Path jsonFile, FeatureType featureType, ReadWriteLock rwLock,
            final String encoding, final int doubleAccuracy) throws DataStoreException {
        super(jsonFile, featureType, rwLock);

        JsonEncoding jsonEncoding = JsonEncoding.UTF8;

        try {
            final String name = featureType.getName().tip().toString();
            tmpFile = jsonFile.resolveSibling(name + ".wjson");
            writer = new GeoJSONWriter(tmpFile, GeoJSONParser.getFactory(jsonFile), jsonEncoding, doubleAccuracy, false);

            //start write feature collection.
            final FTypeInformation fti = ftInfos.get(featureType);
            writer.writeStartFeatureCollection(fti.crs, null, null, null, null);
            writer.flush();
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public FeatureType getFeatureType() {
        return super.getFeatureType();
    }

    @Override
    public Feature next() throws BackingStoreException {
        try {
            write();
            edited = super.next();
        } catch (BackingStoreException ex) {
            //we reach append mode
            //create empty feature
            edited = featureType.newInstance();
            final FTypeInformation fti = ftInfos.get(featureType);
            if (fti.hasIdentifier) {
                edited.setPropertyValue(AttributeConvention.IDENTIFIER, fti.idConverter.apply(currentFeatureIdx++));
            }
        }
        return edited;
    }

    public void write(Feature edited) throws BackingStoreException {
        this.edited = edited;
        write();
    }

    public void write() throws BackingStoreException {
        if (edited == null || edited.equals(lastWritten)) {
            return;
        }

        lastWritten = edited;
        try {
            writer.writeFeature(edited);
            writer.flush();
        } catch (IOException | IllegalArgumentException e) {
            throw new BackingStoreException(e.getMessage(), e);
        }
    }

    @Override
    public void remove() {
        edited = null;
    }

    @Override
    public void close() {
        try (GeoJSONWriter toClose = writer) {
            toClose.writeEndFeatureCollection();
            toClose.flush();
        } catch (IOException ex) {
            throw new BackingStoreException(ex);
        } finally {
            super.close();
        }

        //flip files
        rwlock.writeLock().lock();
        try {
            Files.move(tmpFile, jsonFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BackingStoreException(ex);
        } finally {
            rwlock.writeLock().unlock();
        }
    }
}

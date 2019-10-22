/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.geojson;

import com.fasterxml.jackson.core.JsonEncoding;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.feature.FeatureWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.locks.ReadWriteLock;
import org.apache.sis.internal.feature.AttributeConvention;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;


/**
 * @author Quentin Boileau (Geomatys)
 */
class GeoJSONFileWriter extends GeoJSONReader implements FeatureWriter {

    private final GeoJSONWriter writer;

    private Feature edited = null;
    private Feature lastWritten = null;
    private Path tmpFile;

    public GeoJSONFileWriter(Path jsonFile, FeatureType featureType, ReadWriteLock rwLock,
                             final String encoding, final int doubleAccuracy) throws DataStoreException {
        super(jsonFile, featureType, rwLock);

        JsonEncoding jsonEncoding = JsonEncoding.UTF8;

        try {
            final String name = featureType.getName().tip().toString();
            tmpFile = jsonFile.resolveSibling(name + ".wjson");
            writer = new GeoJSONWriter(tmpFile, jsonEncoding, doubleAccuracy, false);

            //start write feature collection.
            writer.writeStartFeatureCollection(crs, null);
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
    public Feature next() throws FeatureStoreRuntimeException {
        try{
            write();
            edited = super.next();
        }catch(FeatureStoreRuntimeException ex){
            //we reach append mode
            //create empty feature
            edited = featureType.newInstance();
            if (hasIdentifier) {
                edited.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), idConverter.apply(currentFeatureIdx++));
            }
        }
        return edited;
    }

    @Override
    public void write() throws FeatureStoreRuntimeException {
        if(edited == null || edited.equals(lastWritten)) return;

        lastWritten = edited;
        try {
            writer.writeFeature(edited);
            writer.flush();
        } catch (IOException | IllegalArgumentException e) {
            throw new FeatureStoreRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        try (final GeoJSONWriter toClose = writer) {
            toClose.writeEndFeatureCollection();
            toClose.flush();
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } finally {
            super.close();
        }

        //flip files
        rwlock.writeLock().lock();
        try {
            Files.move(tmpFile, jsonFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } finally {
            rwlock.writeLock().unlock();
        }
    }
}

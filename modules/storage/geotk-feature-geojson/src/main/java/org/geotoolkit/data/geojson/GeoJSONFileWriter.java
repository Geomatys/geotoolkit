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
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.IllegalAttributeException;
import org.geotoolkit.feature.*;
import org.geotoolkit.feature.type.FeatureType;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;


/**
 * @author Quentin Boileau (Geomatys)
 */
class GeoJSONFileWriter extends GeoJSONReader implements FeatureWriter {

    private ReadWriteLock tmpLock;
    private final GeoJSONWriter writer;

    private Feature edited = null;
    private Feature lastWritten = null;
    private File tmpFile;

    public GeoJSONFileWriter(File jsonFile, FeatureType featureType, ReadWriteLock rwLock, ReadWriteLock tmpLock,
                             final String encoding, final int doubleAccuracy) throws DataStoreException {
        super(jsonFile, featureType, rwLock);
        this.tmpLock = tmpLock;

        JsonEncoding jsonEncoding = JsonEncoding.UTF8;

        tmpLock.writeLock().lock();
        try {
            final String name = featureType.getName().getLocalPart();
            tmpFile = new File(jsonFile.getParent(), name + ".wjson");
            writer = new GeoJSONWriter(tmpFile, jsonEncoding, doubleAccuracy, false);

            //start write feature collection.
            writer.writeStartFeatureCollection(featureType.getCoordinateReferenceSystem(), null);
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
            edited = FeatureUtilities.defaultFeature(featureType, "id-"+currentFeatureIdx++);
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
        } catch (IOException e) {
            throw new FeatureStoreRuntimeException(e.getMessage(), e);
        } catch (IllegalAttributeException e) {
            throw new FeatureStoreRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void close() {

        try {
            writer.writeEndFeatureCollection();
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }

        super.close();

        //flip files
        rwlock.writeLock().lock();
        jsonFile.delete();
        tmpFile.renameTo(jsonFile);
        rwlock.writeLock().unlock();

        tmpLock.writeLock().unlock();
    }
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
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
package org.geotoolkit.data.shapefile.indexed;

import static org.geotoolkit.data.shapefile.ShpFileType.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.shapefile.ShapefileDataStoreFactory;
import org.geotoolkit.data.shapefile.ShapefileFeatureWriter;
import org.geotoolkit.data.shapefile.ShpFileType;
import org.geotoolkit.data.shapefile.ShpFiles;
import org.geotoolkit.data.shapefile.StorageFile;
import org.geotoolkit.data.shapefile.fix.IndexedFidWriter;
import org.geotoolkit.internal.io.IOUtilities;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import static org.geotoolkit.data.shapefile.ShapefileDataStoreFactory.*;

/**
 * A FeatureWriter for ShapefileDataStore. Uses a write and annotate technique
 * to avoid buffering attributes and geometries. Because the shape and dbf
 * require header information which can only be obtained by reading the entire
 * series of Features, the headers are updated after the initial write
 * completes.
 * @module pending
 */
class IndexedShapefileFeatureWriter extends ShapefileFeatureWriter{

    private IndexedShapefileDataStore indexedShapefileDataStore;
    private IndexedFidWriter fidWriter;

    private String currentFid;

    public IndexedShapefileFeatureWriter(final String typeName, final ShpFiles shpFiles,
            final IndexedShapefileAttributeReader attsReader,
             final FeatureReader<SimpleFeatureType, SimpleFeature> featureReader, final IndexedShapefileDataStore datastore,
             final Charset charset)
            throws DataStoreException,IOException {
        super(typeName, shpFiles, attsReader, featureReader, charset);
        this.indexedShapefileDataStore = datastore;
        if (!datastore.indexUseable(FIX)) {
            this.fidWriter = IndexedFidWriter.EMPTY_WRITER;
        } else {
            StorageFile storageFile = shpFiles.getStorageFile(FIX);
            storageFiles.put(FIX, storageFile);
            this.fidWriter = new IndexedFidWriter(shpFiles, storageFile);
        }
    }

    @Override
    public SimpleFeature next() throws DataStoreRuntimeException {
        // closed already, error!
        if (featureReader == null) {
            throw new DataStoreRuntimeException("Writer closed");
        }

        // we have to write the current feature back into the stream
        if (currentFeature != null) {
            write();
        }

        final long next;
        try {
            next = fidWriter.next();
        } catch (IOException ex) {
            throw new DataStoreRuntimeException(ex);
        }
        currentFid = getFeatureType().getTypeName() + "." + next;
        SimpleFeature feature = super.next();
        return feature;
    }

    @Override
    protected String nextFeatureId() {
        return currentFid;
    }

    @Override
    public void remove() throws DataStoreRuntimeException {
        try {
            fidWriter.remove();
        } catch (IOException ex) {
            throw new DataStoreRuntimeException(ex);
        }
        super.remove();
    }

    @Override
    public void write() throws DataStoreRuntimeException {
        try {
            fidWriter.write();
        } catch (IOException ex) {
            throw new DataStoreRuntimeException(ex);
        }
        super.write();
    }

    /**
     * Release resources and flush the header information.
     */
    @Override
    public void close() throws DataStoreRuntimeException {
        super.close();

        try {
            if (shpFiles.isLocal()) {
                if (indexedShapefileDataStore.needsGeneration(ShpFileType.FIX)) {
                    IndexedFidWriter.generate(shpFiles);
                }

                deleteFile(ShpFileType.QIX);

                if (indexedShapefileDataStore.treeType == IndexType.QIX) {
                    indexedShapefileDataStore
                            .buildQuadTree(indexedShapefileDataStore.maxDepth);
                }
            }
        } catch (Throwable e) {
            indexedShapefileDataStore.treeType = IndexType.NONE;
            ShapefileDataStoreFactory.LOGGER.log(Level.WARNING,
                    "Error creating Spatial index", e);
        }
    }

    @Override
    protected void doClose() throws DataStoreRuntimeException {
        super.doClose();
        try{
            fidWriter.close();
        }catch(Throwable e){
            indexedShapefileDataStore.treeType = IndexType.NONE;
            ShapefileDataStoreFactory.LOGGER.log(Level.WARNING,
                    "Error creating Feature ID index", e);
        }
    }
    
    private void deleteFile(final ShpFileType shpFileType) {
        URL url = shpFiles.acquireWrite(shpFileType, this);
        try {
            File toDelete = IOUtilities.toFile(url, ENCODING);

            if (toDelete.exists()) {
                toDelete.delete();
            }
        } catch(IOException ex){
            //should not happen
            throw new RuntimeException(ex);
        } finally {
            shpFiles.unlockWrite(url, this);
        }
    }

    public String id() {
        return getClass().getName();
    }
}

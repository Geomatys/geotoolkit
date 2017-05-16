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

import static org.geotoolkit.data.shapefile.lock.ShpFileType.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import org.apache.sis.internal.feature.AttributeConvention;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory;
import org.geotoolkit.data.shapefile.ShapefileFeatureWriter;
import org.geotoolkit.data.shapefile.lock.ShpFileType;
import org.geotoolkit.data.shapefile.lock.ShpFiles;
import org.geotoolkit.data.shapefile.lock.StorageFile;
import org.geotoolkit.data.shapefile.fix.IndexedFidWriter;
import org.geotoolkit.nio.IOUtilities;
import org.opengis.feature.Feature;

/**
 * A FeatureWriter for ShapefileDataStore. Uses a write and annotate technique
 * to avoid buffering attributes and geometries. Because the shape and dbf
 * require header information which can only be obtained by reading the entire
 * series of Features, the headers are updated after the initial write
 * completes.
 * @module
 */
class IndexedShapefileFeatureWriter extends ShapefileFeatureWriter{

    private IndexedShapefileFeatureStore indexedShapefileFeatureStore;
    private IndexedFidWriter fidWriter;

    private String currentFid;

    public IndexedShapefileFeatureWriter(final String typeName, final ShpFiles shpFiles,
            final IndexedShapefileAttributeReader attsReader,
             final FeatureReader featureReader, final IndexedShapefileFeatureStore featurestore,
             final Charset charset)
            throws DataStoreException,IOException {
        super(featurestore, typeName, shpFiles, attsReader, featureReader, charset);

        this.indexedShapefileFeatureStore = featurestore;
        if (!featurestore.indexUseable(FIX)) {
            this.fidWriter = IndexedFidWriter.EMPTY_WRITER;
        } else {
            final StorageFile storageFile = getLocker().getStorageFile(FIX);
            storageFiles.put(FIX, storageFile);
            this.fidWriter = getLocker().getFIXWriter(storageFile);
        }

        //runnable called when closing writer, we use it to rebuild id index and quadtree
        postClose = new Runnable() {
            @Override
            public void run() {
                try {
                    if (shpFiles.isWritable()) {
                        if (indexedShapefileFeatureStore.needsGeneration(ShpFileType.FIX)) {
                            IndexedFidWriter.generate(shpFiles);
                        }
                        deleteFile(ShpFileType.QIX);
                        if (indexedShapefileFeatureStore.treeType == IndexType.QIX) {
                            indexedShapefileFeatureStore
                                    .buildQuadTree(indexedShapefileFeatureStore.maxDepth);
                        }
                    }
                } catch (Throwable e) {
                    indexedShapefileFeatureStore.treeType = IndexType.NONE;
                    ShapefileFeatureStoreFactory.LOGGER.log(Level.WARNING,
                            "Error creating Spatial index", e);
                }
            }
        };

    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        // closed already, error!
        if (featureReader == null) {
            throw new FeatureStoreRuntimeException("Writer closed");
        }

        // we have to write the current feature back into the stream
        if (currentFeature != null) {
            write();
        }

        final long next;
        try {
            next = fidWriter.next();
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
        currentFid = getFeatureType().getName().tip() + "." + next;
        Feature feature = super.next();
        return feature;
    }

    @Override
    protected String nextFeatureId() {
        return currentFid;
    }

    @Override
    public void remove() throws FeatureStoreRuntimeException {
        try {
            fidWriter.remove();
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
        super.remove();
    }

    @Override
    public void write() throws FeatureStoreRuntimeException {
        try {
            fidWriter.write();
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
        //ensure the user did not modify the id between next() and write() calls
        currentFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), currentFid);
        super.write();
    }

    @Override
    protected void doClose() throws FeatureStoreRuntimeException {
        super.doClose();
        try{
            fidWriter.close();
        }catch(Throwable e){
            indexedShapefileFeatureStore.treeType = IndexType.NONE;
            ShapefileFeatureStoreFactory.LOGGER.log(Level.WARNING,
                    "Error creating Feature ID index", e);
        }
    }

    private void deleteFile(final ShpFileType shpFileType) {
        final URI uri = shpFiles.getURI(shpFileType);
        try {
            Path toDelete = IOUtilities.toPath(uri);
            Files.deleteIfExists(toDelete);
        } catch(IOException ex){
            //should not happen
            throw new RuntimeException(ex);
        }
    }

    public String id() {
        return getClass().getName();
    }
}

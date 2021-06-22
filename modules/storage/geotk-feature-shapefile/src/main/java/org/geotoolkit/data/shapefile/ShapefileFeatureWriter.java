/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile;

import org.geotoolkit.data.shapefile.lock.StorageFile;
import org.geotoolkit.data.shapefile.lock.ShpFiles;
import static org.geotoolkit.data.shapefile.lock.ShpFileType.DBF;
import static org.geotoolkit.data.shapefile.lock.ShpFileType.SHP;
import static org.geotoolkit.data.shapefile.lock.ShpFileType.SHX;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.storage.feature.FeatureReader;
import org.geotoolkit.storage.feature.FeatureWriter;
import org.geotoolkit.data.dbf.DbaseFileException;
import org.geotoolkit.data.dbf.DbaseFileHeader;
import org.geotoolkit.data.dbf.DbaseFileWriter;
import org.geotoolkit.data.shapefile.shp.JTSUtilities;
import org.geotoolkit.data.shapefile.shp.ShapeHandler;
import org.geotoolkit.data.shapefile.shp.ShapeType;
import org.geotoolkit.data.shapefile.shp.ShapefileWriter;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.sis.feature.Features;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.event.FeatureStoreContentEvent;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.data.shapefile.lock.AccessManager;
import org.geotoolkit.data.shapefile.lock.ShpFileType;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.ResourceId;

/**
 * A FeatureWriter for ShapefileDataStore. Uses a write and annotate technique
 * to avoid buffering attributes and geometries. Because the shapefile and dbf
 * require header information which can only be obtained by reading the entire
 * series of Features, the headers are updated after the initial write
 * completes.
 *
 * @author Jesse Eichar
 * @module
 */
public class ShapefileFeatureWriter implements FeatureWriter {

    protected final FilterFactory FF = FilterUtilities.FF;

    protected final ShapefileFeatureStore parent;

    // the  FeatureReader<SimpleFeatureType, SimpleFeature> to obtain the current Feature from
    protected FeatureReader featureReader;

    // the AttributeReader
    protected final ShapefileAttributeReader attReader;

    // the current Feature
    protected Feature currentFeature;

    /** Initial value for current feature */
    protected Feature originalFeature;

    // the FeatureType we are representing
    protected final FeatureType featureType;

    // an array for reuse in Feature creation
    //protected final Object[] emptyAtts;

    // an array for reuse in writing to dbf.
    protected final Object[] transferCache;
    protected ShapeType shapeType;
    protected ShapeHandler handler;

    // keep track of shapefile length during write, starts at 100 bytes for
    // required header
    protected int shapefileLength = 100;

    // keep track of the number of records written
    protected int records = 0;

    // hold 1 if dbf should write the attribute at the index, 0 if not
    protected final byte[] writeFlags;
    protected ShapefileWriter shpWriter;
    protected DbaseFileWriter dbfWriter;
    private DbaseFileHeader dbfHeader;

    protected final Set<ResourceId> deletedIds = new HashSet<>();
    protected final Set<ResourceId> updatedIds = new HashSet<>();
    protected final Set<ResourceId> addedIds   = new HashSet<>();
    protected final Map<ShpFileType, StorageFile> storageFiles = new HashMap<>();

    // keep track of bounds during write
    protected Envelope bounds = new Envelope();

    protected final ShpFiles shpFiles;
    private final FileChannel dbfChannel;
    private final Charset dbfCharset;

    //Runnable used after closing shapefile, to rebuild indexes for example
    protected Runnable postClose = null;


    public ShapefileFeatureWriter(final ShapefileFeatureStore parent, final String typeName, final ShpFiles shpFiles, final ShapefileAttributeReader attsReader,
            final FeatureReader featureReader, final Charset charset) throws IOException,DataStoreException {
        this.parent = parent;
        this.shpFiles = shpFiles;
        this.dbfCharset = charset;
        // set up reader
        this.attReader = attsReader;
        this.featureReader = featureReader;

        storageFiles.put(SHP, getLocker().getStorageFile(SHP));
        storageFiles.put(SHX, getLocker().getStorageFile(SHX));
        storageFiles.put(DBF, getLocker().getStorageFile(DBF));

        this.featureType = featureReader.getFeatureType();
        try {
            this.featureType.getProperty(AttributeConvention.IDENTIFIER_PROPERTY.toString());
        } catch(PropertyNotFoundException ex) {
            throw new DataStoreException("Missing identifier property in feature type");
        }

        // set up buffers and write flags
        List<AttributeType> attributes = parent.getAttributes(featureType,false);
        writeFlags = new byte[attributes.size()];

        int cnt = 0;
        for (int i=0, n=attributes.size(); i<n; i++) {
            // if its a geometry, we don't want to write it to the dbf...
            if (!( Geometry.class.isAssignableFrom(attributes.get(i).getValueClass()))) {
                cnt++;
                writeFlags[i] = (byte) 1;
            }
        }

        // dbf transfer buffer
        transferCache = new Object[cnt];

        // open underlying writers
        final FileChannel shpChannel = storageFiles.get(SHP).getWriteChannel();
        final FileChannel shxChannel = storageFiles.get(SHX).getWriteChannel();
        shpWriter = new ShapefileWriter(shpChannel, shxChannel);

        dbfHeader = DbaseFileHeader.createDbaseHeader(featureType);
        dbfChannel = storageFiles.get(DBF).getWriteChannel();
        dbfWriter = new DbaseFileWriter(dbfHeader, dbfChannel, dbfCharset);

        if(attReader != null) {
            // don't try to read a shx file we're writing to in parallel
            attReader.shp.disableShxUsage();
            if(attReader.hasNext()) {
                shapeType = attReader.shp.getHeader().getShapeType();
                handler = shapeType.getShapeHandler(true);
                shpWriter.writeHeaders(bounds, shapeType, records, shapefileLength);
            }
        }
    }

    public final AccessManager getLocker(){
        return attReader.getLocker();
    }

    /**
     * Go back and update the headers with the required info.
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void flush() throws IOException {
        // not sure the check for records <=0 is necessary,
        // but if records > 0 and shapeType is null there's probably
        // another problem.
        if ((records <= 0) && (shapeType == null)) {
            try {
                final PropertyType geom = FeatureExt.getDefaultGeometry(featureType);
                final Optional<Class> geomClass = Features.toAttribute(geom)
                        .map(AttributeType::getValueClass);
                shapeType = geomClass
                        .map(ShapeType::findBestGeometryType)
                        .orElse(ShapeType.NULL);

                if (shapeType == ShapeType.UNDEFINED) {
                    throw new IOException("Cannot handle geometry class : " + (geomClass.isPresent() ? geomClass.get() : "null"));
                }
            } catch (PropertyNotFoundException e) {
                shapeType = ShapeType.NULL;
            }
        }

        shpWriter.writeHeaders(bounds, shapeType, records, shapefileLength);
        dbfHeader.setNumRecords(records);
        dbfChannel.position(0);
        dbfHeader.writeHeader(dbfChannel);
    }

    /**
     * In case someone doesn't close me.
     *
     * @throws Throwable DOCUMENT ME!
     */
    @Override
    protected void finalize() throws Throwable {
        if (featureReader != null) {
            try {
                close();
            } catch (Exception e) {
                // oh well, we tried
            }
        }
    }

    /**
     * Clean up our temporary write if there was one
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void clean() throws IOException {
        getLocker().disposeReaderAndWriters();
        getLocker().replaceStorageFiles(postClose);
    }

    /**
     * Release resources and flush the header information.
     *
     * @throws IOException DOCUMENT ME!
     */
    @Override
    public void close() throws FeatureStoreRuntimeException {
        if (featureReader == null) {
            throw new FeatureStoreRuntimeException("Writer closed");
        }

        // make sure to write the last feature...
        FeatureStoreRuntimeException wEx = null;
        try{
            if (currentFeature != null) {
                write();
            }
        } catch (Throwable ex) {
            wEx = new FeatureStoreRuntimeException(ex);
        }

        // if the attribute reader is here, that means we may have some
        // additional tail-end file flushing to do if the Writer was closed
        // before the end of the file
        try{
            if (attReader != null && attReader.hasNext()) {
                shapeType = attReader.shp.getHeader().getShapeType();
                handler = shapeType.getShapeHandler(true);

                // handle the case where zero records have been written, but the
                // stream is closed and the headers
                if (records == 0) {
                    shpWriter.writeHeaders(bounds, shapeType, 0, 0);
                }

                // copy array for bounds
                final double[] env = new double[4];

                while (attReader.hasNext()) {
                    // transfer bytes from shapefile
                    shapefileLength += attReader.shp.transferTo(shpWriter,++records, env);

                    // bounds update
                    bounds.expandToInclude(env[0], env[1]);
                    bounds.expandToInclude(env[2], env[3]);

                    // transfer dbf bytes
                    attReader.dbf.transferTo(dbfWriter);
                }
            }
        } catch(IOException | DataStoreException ex) {
            if (wEx == null) {
                wEx = new FeatureStoreRuntimeException(ex);
            } else {
                wEx.addSuppressed(ex);
            }
        }

        doClose();
        try {
            clean();
        } catch (IOException ex) {
            if (wEx == null) {
                wEx = new FeatureStoreRuntimeException(ex);
            } else {
                wEx.addSuppressed(ex);
            }
        }

        fireDataChangeEvents();
        if (wEx != null) {
            throw wEx;
        }
    }

    private void fireDataChangeEvents(){
        if (!addedIds.isEmpty()) {
            final FeatureStoreContentEvent event = new FeatureStoreContentEvent(parent, FeatureStoreContentEvent.Type.ADD, featureType.getName(), addedIds);
            parent.forwardEvent(event);
        }

        if (!updatedIds.isEmpty()) {
            final FeatureStoreContentEvent event = new FeatureStoreContentEvent(parent, FeatureStoreContentEvent.Type.UPDATE, featureType.getName(), updatedIds);
            parent.forwardEvent(event);
        }

        if (!deletedIds.isEmpty()) {
            final FeatureStoreContentEvent event = new FeatureStoreContentEvent(parent, FeatureStoreContentEvent.Type.DELETE, featureType.getName(), deletedIds);
            parent.forwardEvent(event);
        }
    }

    protected void doClose() throws FeatureStoreRuntimeException {
        // close reader, flush headers, and copy temp files, if any
        try {
            featureReader.close();
        } finally {
            try {
                flush();
            } catch(IOException ex){
                throw new FeatureStoreRuntimeException(ex);
            }finally {
                try {
                    shpWriter.close();
                    dbfWriter.close();
                } catch (IOException ex) {
                    throw new FeatureStoreRuntimeException(ex);
                }
            }

            featureReader = null;
            shpWriter = null;
            dbfWriter = null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType() {
        return featureType;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        if (featureReader == null) {
            throw new FeatureStoreRuntimeException("Writer closed");
        }

        return featureReader.hasNext();
    }

    /**
     * {@inheritDoc }
     */
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

        // is there another? If so, return it
        if (featureReader.hasNext()) {
            try {
                currentFeature = featureReader.next();
                originalFeature = FeatureExt.copy(currentFeature);
                return currentFeature;
            } catch (IllegalArgumentException iae) {
                throw new FeatureStoreRuntimeException("Error in reading", iae);
            }
        }

        // reader has no more (no were are adding to the file)
        // so return an empty feature
        try {
            final String featureID = getFeatureType().getName().tip() + "." + (records+1);
            originalFeature = null;
            currentFeature = getFeatureType().newInstance();
            currentFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), featureID);
            return currentFeature;
        } catch (IllegalArgumentException iae) {
            throw new FeatureStoreRuntimeException("Error creating empty Feature", iae);
        }
    }

    /**
     * Called when a new feature is being created and a new fid is required
     *
     * @return a fid for the new feature
     */
    protected String nextFeatureId() {
        return getFeatureType().getName().tip() + "." + (records+1);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() throws FeatureStoreRuntimeException {
        if (featureReader == null) {
            throw new FeatureStoreRuntimeException("Writer closed");
        }

        if (currentFeature == null) {
            throw new FeatureStoreRuntimeException("Current feature is null");
        }

        deletedIds.add(FeatureExt.getId(currentFeature));
        // mark the current feature as null, this will result in it not
        // being rewritten to the stream
        currentFeature = null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write() throws FeatureStoreRuntimeException {
        if (currentFeature == null) {
            throw new FeatureStoreRuntimeException("Current feature is null");
        }

        if (featureReader == null) {
            throw new FeatureStoreRuntimeException("Writer closed");
        }

        // writing of Geometry
        Geometry g = FeatureExt.getDefaultGeometryValue(currentFeature)
                .filter(Geometry.class::isInstance)
                .map(Geometry.class::cast)
                .orElse(null);

        // if this is the first Geometry, find the shapeType and handler
        if (shapeType == null) {
            int dims = 2;
            if(g != null){
                dims = JTSUtilities.guessCoorinateDims(g.getCoordinates());
            }

            try {
                shapeType = JTSUtilities.getShapeType(g, dims);

                // we must go back and annotate this after writing
                shpWriter.writeHeaders(new Envelope(), shapeType, 0, 0);
                handler = shapeType.getShapeHandler(true);
            } catch (Exception se) {
                throw new FeatureStoreRuntimeException("Unexpected Error", se);
            }
        }

        // convert geometry
        g = JTSUtilities.convertToCollection(g, shapeType);

        // bounds calculations
        Envelope b = g.getEnvelopeInternal();

        if (!b.isNull()) {
            bounds.expandToInclude(b);
        }

        // file length update
        shapefileLength += (handler.getLength(g) + 8);

        try {
            // write it
            shpWriter.writeGeometry(g);
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }

        // writing of attributes
        int idx = 0;

        List<AttributeType> attributes = parent.getAttributes(featureType,false);
        for (int i = 0, ii = attributes.size(); i < ii; i++) {
            // skip geometries
            if (writeFlags[i] > 0) {
                transferCache[idx++] = currentFeature.getPropertyValue(attributes.get(i).getName().toString());
            }
        }

        try {
            dbfWriter.write(transferCache);
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (DbaseFileException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }

        // one more down...
        records++;

        if (originalFeature == null) {
            addedIds.add(FeatureExt.getId(currentFeature));
        } else if (!originalFeature.equals(currentFeature)) {
            updatedIds.add(FeatureExt.getId(currentFeature));
        }
        // clear the currentFeature
        currentFeature = null;
    }
}

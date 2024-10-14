/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2015, Geomatys
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

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.sis.storage.base.Capability;
import org.apache.sis.storage.base.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import static org.apache.sis.storage.DataStoreProvider.LOCATION;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.data.shapefile.indexed.IndexType;
import org.geotoolkit.data.shapefile.indexed.IndexedShapefileFeatureStore;
import org.geotoolkit.data.shapefile.lock.ShpFileType;
import org.geotoolkit.data.shapefile.lock.ShpFiles;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.ProviderOnFileSystem;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.geotoolkit.storage.feature.FeatureStore;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Implementation of the featurestore service provider interface for Shapefiles.
 * <p>
 * The specific implementation of ShapefileFeatureStore created by this class is
 * not specified. For more information on the connection parameters please
 * review the following public Param constants.
 * <ul>
 * <li>{@link #PATH}
 * <li>{@link #NAMESPACEP}
 * <li>{@link #CREATE_SPATIAL_INDEX}
 * <li>{@link #MEMORY_MAPPED}
 * <li>{@link #DBFCHARSET}
 * </ul>
 *
 * @author Chris Holmes, TOPP
 * @author Johann Sorel (Geomatys)
 * @module
 */
@StoreMetadata(
        formatName = ShapefileProvider.NAME,
        capabilities = {Capability.READ, Capability.WRITE, Capability.CREATE},
        resourceTypes = {FeatureSet.class})
@StoreMetadataExt(
        resourceTypes = ResourceType.VECTOR,
        geometryTypes ={Point.class,
                        MultiPoint.class,
                        MultiLineString.class,
                        MultiPolygon.class})
public class ShapefileProvider extends DataStoreProvider implements ProviderOnFileSystem {

    /** factory identification **/
    public static final String NAME = "shapefile";

    public static final String ENCODING = "UTF-8";
    public static final Logger LOGGER = Logger.getLogger("org.geotoolkit.data.shapefile");

    public static final String MIME_TYPE = "application/x-shapefile";

    /**
     * url to the file.
     */
    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName("path")
            .addName(org.geotoolkit.storage.Bundle.formatInternational(org.geotoolkit.storage.Bundle.Keys.paramPathAlias))
            .addName(LOCATION)
            .setRemarks(org.geotoolkit.storage.Bundle.formatInternational(org.geotoolkit.storage.Bundle.Keys.paramPathRemarks))
            .setRequired(true)
            .create(URI.class, null);

    /**
     * Optional - enable/disable the use of memory-mapped io
     */
    public static final ParameterDescriptor<Boolean> MEMORY_MAPPED = new ParameterBuilder()
            .addName("memory mapped buffer")
            .addName(Bundle.formatInternational(Bundle.Keys.memory_mapped_buffer))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.memory_mapped_buffer_remarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.FALSE);

    /**
     * Optional - Enable/disable the automatic creation of spatial index
     */
    public static final ParameterDescriptor<Boolean> CREATE_SPATIAL_INDEX = new ParameterBuilder()
            .addName("create spatial index")
            .addName(Bundle.formatInternational(Bundle.Keys.create_spatial_index))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.create_spatial_index_remarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.FALSE);

    /**
     * Optional - character used to decode strings from the DBF file
     */
    public static final ParameterDescriptor<Charset> DBFCHARSET = new ParameterBuilder()
            .addName("charset")
            .addName(Bundle.formatInternational(Bundle.Keys.charset))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.charset_remarks))
            .setRequired(false)
            .create(Charset.class, null);

    /**
     * Optional - load in memory the quadtree if exist.
     */
    public static final ParameterDescriptor<Boolean> LOAD_QIX = new ParameterBuilder()
            .addName("load qix")
            .addName(Bundle.formatInternational(Bundle.Keys.load_qix))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.load_qix_remarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.FALSE);


    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).addName("ShapefileParameters").createGroup(
                PATH,MEMORY_MAPPED,CREATE_SPATIAL_INDEX,DBFCHARSET,LOAD_QIX);

    @Override
    public String getShortName() {
        return NAME;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.datastoreTitle);
    }

    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.datastoreDescription);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<String> getSuffix() {
        return Arrays.asList("shp");
    }

    @Override
    public Collection<byte[]> getSignature() {
        return Collections.singleton(new byte[]{0x00,0x00,0x27,0x0A});
    }

    public boolean canProcess(final ParameterValueGroup params) {
        if (canProcessAbs(params)) {
            final Object obj = params.parameter(PATH.getName().toString()).getValue();
            if(obj != null && obj instanceof URI){
                return extensionMatch((URI)obj);
            }
        }

        return false;
    }

    /**
     * Check if the path of given URI ends with one of the file extensions
     * specified as manageable by {@link #getSuffix() } method.
     *
     * @param location The URI to test.
     * @return True if the path of given URI ends with a known extension. False
     * otherwise.
     */
    private boolean extensionMatch(final URI location) {
        final String path = location.getPath().toLowerCase();
        for (final String ext : getSuffix()) {
            if (path.endsWith(ext.toLowerCase()) && !path.endsWith("*" + ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc }
     * @param uri
     */
    public FeatureStore createDataStore(final URI uri) throws DataStoreException {
        FeatureStore result;
        final  Map params = Collections.singletonMap(PATH.getName().toString(), uri);
        try {
            result = (FeatureStore) DataStores.open(this,params);
        } catch (DataStoreException e) {
            result = (FeatureStore) this.create(Parameters.toParameter(params, getOpenParameters()));
        }
        return result;
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        ProbeResult result = FeatureStoreUtilities.probe(this, connector, MIME_TYPE);
        if (result.isSupported()) {
            //SHP and SHX files have the same signature, we only want to match on the SHP file.
            final Path path = connector.getStorageAs(Path.class);
            final String ext = IOUtilities.extension(path);
            if (!"shp".equalsIgnoreCase(ext)) {
                return ProbeResult.UNSUPPORTED_STORAGE;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ShapefileFeatureStore open(final ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);

        final URI uri = (URI) params.parameter(PATH.getName().toString()).getValue();
        Boolean isMemoryMapped = (Boolean) params.parameter(MEMORY_MAPPED.getName().toString()).getValue();
        Charset dbfCharset = (Charset) params.parameter(DBFCHARSET.getName().toString()).getValue();
        Boolean isCreateSpatialIndex = (Boolean) params.parameter(CREATE_SPATIAL_INDEX.getName().toString()).getValue();

        if (isCreateSpatialIndex == null) {
            // should not be needed as default is FALSE
            isCreateSpatialIndex = Boolean.FALSE;
        }

        if (isMemoryMapped == null) {
            isMemoryMapped = Boolean.FALSE;
        }

        //index loading hints
        final Boolean loadQix = (Boolean) params.parameter(LOAD_QIX.getName().toString()).getValue();

        final ShpFiles shpFiles;
        try{
            shpFiles = new ShpFiles(uri, (loadQix == null) ? false : loadQix );
        }catch(IllegalArgumentException ex){
            throw new DataStoreException(ex.getMessage(),ex);
        }

        if (!shpFiles.exists(ShpFileType.SHP)) {
            throw new DataStoreException("Shapefile not found:" + shpFiles.get(ShpFileType.SHP));
        }

        final boolean isWritable = shpFiles.isWritable();
        final boolean useMemoryMappedBuffer = shpFiles.exists(ShpFileType.SHP) && isMemoryMapped;
        final boolean createIndex = isCreateSpatialIndex && isWritable;

        IndexType treeIndex = IndexType.NONE;
        if (isWritable) {
            if (createIndex) {
                treeIndex = IndexType.QIX; // default
            } else {
                // lets check and see if any index file is avaialble
                if (shpFiles.exists(ShpFileType.QIX)) {
                    treeIndex = IndexType.QIX;
                }
            }
        }

        try {
            if (createIndex) {
                return new IndexedShapefileFeatureStore(uri, useMemoryMappedBuffer, createIndex, IndexType.QIX, dbfCharset);
            } else if (treeIndex != IndexType.NONE) {
                return new IndexedShapefileFeatureStore(uri, useMemoryMappedBuffer, false, treeIndex, dbfCharset);
            } else {
                return new ShapefileFeatureStore(uri, useMemoryMappedBuffer, dbfCharset);
            }
        } catch (MalformedURLException mue) {
            throw new DataStoreException("Url for shapefile malformed: " + uri, mue);
        }
    }

    public ShapefileFeatureStore create(final ParameterValueGroup params) throws DataStoreException {
        final URI uri = (URI) params.parameter(PATH.getName().toString()).getValue();
        Boolean isMemoryMapped = (Boolean) params.parameter(MEMORY_MAPPED.getName().toString()).getValue();
        Charset dbfCharset = (Charset) params.parameter(DBFCHARSET.getName().toString()).getValue();
        Boolean isCreateSpatialIndex = (Boolean) params.parameter(CREATE_SPATIAL_INDEX.getName().toString()).getValue();

        if (isCreateSpatialIndex == null) {
            // should not be needed as default is TRUE
            isCreateSpatialIndex = Boolean.TRUE;
        }
        if (dbfCharset == null) {
            // this should not happen as Charset.forName("ISO-8859-1") was used
            // as the param default?
            dbfCharset = Charset.forName("ISO-8859-1");
        }
        if (isMemoryMapped == null) {
            // this should not happen as false was the default
            isMemoryMapped = Boolean.FALSE;
        }
        final ShpFiles shpFiles = new ShpFiles(uri);

        final boolean isLocal = shpFiles.isWritable();
        if (!isLocal || shpFiles.exists(ShpFileType.SHP)) {
            LOGGER.fine("File already exists: " + shpFiles.get(ShpFileType.SHP));
        }

        final boolean useMemoryMappedBuffer = isLocal && isMemoryMapped.booleanValue();
        final boolean createIndex = isCreateSpatialIndex.booleanValue() && isLocal;

        try {
            if (createIndex) {
                return new IndexedShapefileFeatureStore(uri, useMemoryMappedBuffer, true, IndexType.QIX, dbfCharset);
            } else {
                return new ShapefileFeatureStore(uri, useMemoryMappedBuffer, dbfCharset);
            }
        } catch (MalformedURLException mue) {
            throw new DataStoreException("Uri for shapefile malformed: " + uri, mue);
        }
    }

    /**
     * @param params
     * @return
     * @see org.geotoolkit.storage.DataStoreFactory#canProcess(java.util.Map)
     */
    private boolean canProcessAbs(final ParameterValueGroup params) {
        if(params == null){
            return false;
        }

        final ParameterDescriptorGroup desc = getOpenParameters();
        if(!desc.getName().getCode().equalsIgnoreCase(params.getDescriptor().getName().getCode())){
            return false;
        }

        final ConformanceResult result = Parameters.isValid(params, desc);
        return (result != null) && Boolean.TRUE.equals(result.pass());
    }

    @Override
    public org.apache.sis.storage.DataStore open(StorageConnector connector) throws DataStoreException {
        GeneralParameterDescriptor desc;
        try {
            desc = getOpenParameters().descriptor(LOCATION);
        } catch (ParameterNotFoundException e) {
            throw new DataStoreException("Unsupported input");
        }

        if (!(desc instanceof ParameterDescriptor)) {
            throw new DataStoreException("Unsupported input");
        }

        try {
            final Object locationValue = connector.commit(((ParameterDescriptor)desc).getValueClass(), NAME);
            final ParameterValueGroup params = getOpenParameters().createValue();
            params.parameter(LOCATION).setValue(locationValue);

            if (canProcess(params)) {
                return open(params);
            }
        } catch(IllegalArgumentException ex) {
            throw new DataStoreException("Unsupported input:" + ex.getMessage());
        }

        throw new DataStoreException("Unsupported input");
    }

    /**
     * @param params
     * @see #checkIdentifier(org.opengis.parameter.ParameterValueGroup)
     * @throws DataStoreException if identifier is not valid
     */
    protected void ensureCanProcess(final ParameterValueGroup params) throws DataStoreException{
        final boolean valid = canProcess(params);
        if(!valid){
            throw new DataStoreException("Parameter values not supported by this factory.");
        }
    }

}

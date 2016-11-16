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

import org.geotoolkit.data.shapefile.lock.ShpFiles;
import org.geotoolkit.data.shapefile.lock.ShpFileType;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.geotoolkit.data.shapefile.indexed.IndexedShapefileFeatureStore;
import org.apache.sis.metadata.iso.quality.DefaultConformanceResult;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.shapefile.indexed.IndexType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

import java.util.Collections;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
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
public class ShapefileFeatureStoreFactory extends AbstractFileFeatureStoreFactory implements FileFeatureStoreFactory {

    /** factory identification **/
    public static final String NAME = "shapefile";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final String ENCODING = "UTF-8";
    public static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.shapefile");

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
            .create(Boolean.class, Boolean.TRUE);

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
            new ParameterBuilder().addName("ShapefileParameters").createGroup(
                IDENTIFIER, PATH,NAMESPACE,MEMORY_MAPPED,CREATE_SPATIAL_INDEX,DBFCHARSET,LOAD_QIX);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.datastoreTitle);
    }

    /**
     * Describes the type of data the featurestore returned by this factory works
     * with.
     *
     * @return String a human readable description of the type of restore
     *         supported by this datastore.
     */
    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.datastoreDescription);
    }

    /**
     * Test to see if this featurestore is available, if it has all the appropriate
     * libraries to construct a datastore.
     *
     * This featurestore just checks for the ShapefileDataStore,
     * IndexedShapefileFeatureStore and Geometry implementations.
     *
     * @return <tt>true</tt> if and only if this factory is available to
     *         open DataStores.
     */
    @Override
    public ConformanceResult availability() {
        final DefaultConformanceResult result = new DefaultConformanceResult();
        try {
            ShapefileFeatureStore.class.getName();
            IndexedShapefileFeatureStore.class.getName();
            Geometry.class.getName();
            result.setPass(true);
        } catch (Exception e) {
            result.setPass(false);
        }
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getFileExtensions() {
        return new String[] {".shp"};
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ShapefileFeatureStore open(final ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);

        final URI uri = (URI) params.parameter(PATH.getName().toString()).getValue();
        Boolean isMemoryMapped = (Boolean) params.parameter(MEMORY_MAPPED.getName().toString()).getValue();
        final String namespace = (String) params.parameter(NAMESPACE.getName().toString()).getValue();
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
                return new IndexedShapefileFeatureStore(uri, namespace, useMemoryMappedBuffer, createIndex, IndexType.QIX, dbfCharset);
            } else if (treeIndex != IndexType.NONE) {
                return new IndexedShapefileFeatureStore(uri, namespace, useMemoryMappedBuffer, false, treeIndex, dbfCharset);
            } else {
                return new ShapefileFeatureStore(uri, namespace, useMemoryMappedBuffer, dbfCharset);
            }
        } catch (MalformedURLException mue) {
            throw new DataStoreException("Url for shapefile malformed: " + uri, mue);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ShapefileFeatureStore create(final ParameterValueGroup params) throws DataStoreException {
        final URI uri = (URI) params.parameter(PATH.getName().toString()).getValue();
        Boolean isMemoryMapped = (Boolean) params.parameter(MEMORY_MAPPED.getName().toString()).getValue();
        final String namespace = (String) params.parameter(NAMESPACE.getName().toString()).getValue();
        Charset dbfCharset = (Charset) params.parameter(DBFCHARSET.getName().toString()).getValue();
        Boolean isCreateSpatialIndex = (Boolean) params.parameter(CREATE_SPATIAL_INDEX.getName().toString()).getValue();

        if (isCreateSpatialIndex == null) {
            // should not be needed as default is TRUE
            assert (true);
            isCreateSpatialIndex = Boolean.TRUE;
        }
        if (dbfCharset == null) {
            assert (true);
            // this should not happen as Charset.forName("ISO-8859-1") was used
            // as the param default?
            dbfCharset = Charset.forName("ISO-8859-1");
        }
        if (isMemoryMapped == null) {
            assert (true);
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
                return new IndexedShapefileFeatureStore(uri, namespace, useMemoryMappedBuffer, true, IndexType.QIX, dbfCharset);
            } else {
                return new ShapefileFeatureStore(uri, namespace, useMemoryMappedBuffer, dbfCharset);
            }
        } catch (MalformedURLException mue) {
            throw new DataStoreException("Uri for shapefile malformed: " + uri, mue);
        }
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.VECTOR, true, true, true, false, new Class[]{
            Point.class, MultiPoint.class, MultiLineString.class, MultiPolygon.class
        });
    }

}

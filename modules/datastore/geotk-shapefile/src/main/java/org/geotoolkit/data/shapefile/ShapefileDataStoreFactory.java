/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FileDataStoreFactory;
import org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStore;
import org.geotoolkit.metadata.iso.quality.DefaultConformanceResult;
import org.geotoolkit.data.AbstractFileDataStoreFactory;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.data.shapefile.indexed.IndexType;

import com.vividsolutions.jts.geom.Geometry;

import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Implementation of the DataStore service provider interface for Shapefiles.
 * <p>
 * The specific implementation of ShapefileDataStore created by this class is
 * not specified. For more information on the connection parameters please
 * review the following public Param constants.
 * <ul>
 * <li>{@link #URLP}
 * <li>{@link #NAMESPACEP}
 * <li>{@link #CREATE_SPATIAL_INDEX}
 * <li>{@link #MEMORY_MAPPED}
 * <li>{@link #DBFCHARSET}
 * </ul>
 * 
 * @author Chris Holmes, TOPP
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ShapefileDataStoreFactory extends AbstractFileDataStoreFactory implements FileDataStoreFactory {

    public static final String ENCODING = "UTF-8";
    public static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.shapefile");
    
    /**
     * Optional - enable/disable the use of memory-mapped io
     */
    public static final GeneralParameterDescriptor MEMORY_MAPPED =
            new DefaultParameterDescriptor("memory mapped buffer","enable/disable the use of memory-mapped io",Boolean.class,null,false);

    /**
     * Optional - Enable/disable the automatic creation of spatial index
     */
    public static final GeneralParameterDescriptor CREATE_SPATIAL_INDEX =
            new DefaultParameterDescriptor("create spatial index","enable/disable the automatic creation of spatial index",Boolean.class,null,false);

    /**
     * Optional - character used to decode strings from the DBF file
     */
    public static final GeneralParameterDescriptor DBFCHARSET =
            new DefaultParameterDescriptor("charset","character used to decode strings from the DBF file",Charset.class,Charset.forName("ISO-8859-1"),false);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("ShapefileParameters",
                new GeneralParameterDescriptor[]{URLP,NAMESPACE,MEMORY_MAPPED,CREATE_SPATIAL_INDEX,DBFCHARSET});

    public ShapefileDataStoreFactory(){
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
    public String getDisplayName() {
        return "Shapefile";
    }

    /**
     * Describes the type of data the datastore returned by this factory works
     * with.
     *
     * @return String a human readable description of the type of restore
     *         supported by this datastore.
     */
    @Override
    public String getDescription() {
        return "ESRI(tm) Shapefiles (*.shp)";
    }

    /**
     * Test to see if this datastore is available, if it has all the appropriate
     * libraries to construct a datastore.
     *
     * This datastore just checks for the ShapefileDataStore,
     * IndexedShapefileDataStore and Geometry implementations.
     *
     * @return <tt>true</tt> if and only if this factory is available to
     *         create DataStores.
     */
    @Override
    public ConformanceResult availability() {
        DefaultConformanceResult result = new DefaultConformanceResult();
        try {
            ShapefileDataStore.class.getName();
            IndexedShapefileDataStore.class.getName();
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
    public DataStore createDataStore(ParameterValueGroup params) throws DataStoreException {

        URL url = (URL) params.parameter(URLP.getName().toString()).getValue();
        Boolean isMemoryMapped = (Boolean) params.parameter(MEMORY_MAPPED.getName().toString()).getValue();
        String namespace = (String) params.parameter(NAMESPACE.getName().toString()).getValue();
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

        final ShpFiles shpFiles = new ShpFiles(url);
        final boolean isLocal = shpFiles.isLocal();

        if (isLocal && !shpFiles.exists(ShpFileType.SHP)) {
            throw new DataStoreException("Shapefile not found:" + shpFiles.get(ShpFileType.SHP));
        }

        final boolean useMemoryMappedBuffer = isLocal && shpFiles.exists(ShpFileType.SHP) && isMemoryMapped.booleanValue();
        final boolean createIndex = isCreateSpatialIndex.booleanValue() && isLocal;

        IndexType treeIndex = IndexType.NONE;
        if (isLocal) {
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
                return new IndexedShapefileDataStore(url, namespace, useMemoryMappedBuffer, createIndex, IndexType.QIX, dbfCharset);
            } else if (treeIndex != IndexType.NONE) {
                return new IndexedShapefileDataStore(url, namespace, useMemoryMappedBuffer, false, treeIndex, dbfCharset);
            } else {
                return new ShapefileDataStore(url, namespace, useMemoryMappedBuffer, dbfCharset);
            }
        } catch (MalformedURLException mue) {
            throw new DataStoreException("Url for shapefile malformed: " + url, mue);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStore createNewDataStore(ParameterValueGroup params) throws DataStoreException {
        URL url = (URL) params.parameter(URLP.getName().toString()).getValue();
        Boolean isMemoryMapped = (Boolean) params.parameter(MEMORY_MAPPED.getName().toString()).getValue();
        String namespace = (String) params.parameter(NAMESPACE.getName().toString()).getValue();
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
        ShpFiles shpFiles = new ShpFiles(url);

        boolean isLocal = shpFiles.isLocal();
        if (!isLocal || shpFiles.exists(ShpFileType.SHP)) {
            LOGGER.fine("File already exists: " + shpFiles.get(ShpFileType.SHP));
        }
        
        final boolean useMemoryMappedBuffer = isLocal && isMemoryMapped.booleanValue();
        final boolean createIndex = isCreateSpatialIndex.booleanValue() && isLocal;

        try {
            if (createIndex) {
                return new IndexedShapefileDataStore(url, namespace, useMemoryMappedBuffer, true, IndexType.QIX, dbfCharset);
            } else {
                return new ShapefileDataStore(url, namespace, useMemoryMappedBuffer, dbfCharset);
            }
        } catch (MalformedURLException mue) {
            throw new DataStoreException("Url for shapefile malformed: " + url, mue);
        }
    }

}

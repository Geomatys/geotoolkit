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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.geotoolkit.data.DataSourceException;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.FileDataStoreFactory;
import org.geotoolkit.data.shapefile.indexed.IndexType;
import org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStore;
import org.geotoolkit.metadata.iso.quality.DefaultConformanceResult;
import org.geotoolkit.data.AbstractDataStoreFactory;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.util.logging.Logging;

import com.vividsolutions.jts.geom.Geometry;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
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
 * <li>{@link URLP}
 * <li>{@link NAMESPACEP}
 * <li>{@link CREATE_SPATIAL_INDEX}
 * <li>{@link MEMORY_MAPPED}
 * <li>{@link DBFCHARSET}
 * </ul>
 * 
 * @author Chris Holmes, TOPP
 * @author Johann Sorel (Geomatys)
 * @source $URL:
 *         http://svn.geotools.org/geotools/trunk/gt/modules/plugin/shapefile/src/main/java/org/geotools/data/shapefile/ShapefileDataStoreFactory.java $
 * @version $Id: ShapefileDataStoreFactory.java 27856 2007-11-12 17:23:35Z
 *          desruisseaux $
 */
public class ShapefileDataStoreFactory extends AbstractDataStoreFactory implements FileDataStoreFactory {

    public static final Logger LOGGER = Logging.getLogger("org.geotools.data.shapefile");
    /**
     * url to the .shp file.
     */
    public static final GeneralParameterDescriptor URLP =
            new DefaultParameterDescriptor("url","url to a .shp file",URL.class,null,true);

    /**
     * Optional - uri of the FeatureType's namespace
     */
    public static final GeneralParameterDescriptor NAMESPACEP =
            new DefaultParameterDescriptor("namespace","uri to a the namespace",URI.class,null,false);

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
                new GeneralParameterDescriptor[]{URLP,NAMESPACEP,MEMORY_MAPPED,CREATE_SPATIAL_INDEX,DBFCHARSET});

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
    public DataStore<SimpleFeatureType, SimpleFeature> createDataStore(ParameterValueGroup params) throws IOException {

        URL url = (URL) params.parameter(URLP.getName().toString()).getValue();
        Boolean isMemoryMapped = (Boolean) params.parameter(MEMORY_MAPPED.getName().toString()).getValue();
        URI namespace = (URI) params.parameter(NAMESPACEP.getName().toString()).getValue();
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

        ShpFiles shpFiles = new ShpFiles(url);

        boolean isLocal = shpFiles.isLocal();
        if (isLocal && !shpFiles.exists(ShpFileType.SHP)) {
            throw new FileNotFoundException("Shapefile not found:" + shpFiles.get(ShpFileType.SHP));
        }
        boolean useMemoryMappedBuffer = isLocal
                && shpFiles.exists(ShpFileType.SHP)
                && isMemoryMapped.booleanValue();
        boolean createIndex = isCreateSpatialIndex.booleanValue() && isLocal;
        IndexType treeIndex = IndexType.NONE;
        if (isLocal) {
            if (createIndex) {
                treeIndex = IndexType.QIX; // default
            } else {
                // lets check and see if any index file is avaialble
                if (shpFiles.exists(ShpFileType.QIX)) {
                    treeIndex = IndexType.QIX;
                }
//                else if (shpFiles.exists(ShpFileType.GRX)) {
//                    treeIndex = IndexType.GRX;
//                }
            }
        }

        try {
            if (createIndex) {
                return new IndexedShapefileDataStore(url, namespace,
                        useMemoryMappedBuffer, createIndex, IndexType.QIX,
                        dbfCharset);
            } else if (treeIndex != IndexType.NONE) {
                return new IndexedShapefileDataStore(url, namespace,
                        useMemoryMappedBuffer, false, treeIndex, dbfCharset);
            } else {
                return new ShapefileDataStore(url, namespace,
                        useMemoryMappedBuffer, dbfCharset);
            }
        } catch (MalformedURLException mue) {
            throw new DataSourceException(
                    "Url for shapefile malformed: " + url, mue);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStore<SimpleFeatureType, SimpleFeature> createNewDataStore(ParameterValueGroup params) throws IOException {
        URL url = (URL) params.parameter(URLP.getName().toString()).getValue();
        Boolean isMemoryMapped = (Boolean) params.parameter(MEMORY_MAPPED.getName().toString()).getValue();
        URI namespace = (URI) params.parameter(NAMESPACEP.getName().toString()).getValue();
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
            LOGGER.fine("File already exists: "
                    + shpFiles.get(ShpFileType.SHP));
        }
        boolean useMemoryMappedBuffer = isLocal
                && isMemoryMapped.booleanValue();
        boolean createIndex = isCreateSpatialIndex.booleanValue() && isLocal;

        try {
            if (createIndex) {
                return new IndexedShapefileDataStore(url, namespace,
                        useMemoryMappedBuffer, true, IndexType.QIX, dbfCharset);
            } else {
                return new ShapefileDataStore(url, namespace,
                        useMemoryMappedBuffer, dbfCharset);
            }
        } catch (MalformedURLException mue) {
            throw new DataSourceException(
                    "Url for shapefile malformed: " + url, mue);
        }
    }

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
        return new String[] { ".shp", };
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canProcess(URL f) {
        return f.getFile().toUpperCase().endsWith("SHP");
    }

    /**
     * We may need to create a new datastore if the provided file does not
     * exist.
     * 
     * @see org.geotools.data.dir.FileDataStoreFactory#createDataStore(java.net.URL)
     */
    @Override
    public DataStore createDataStore(URL url) throws IOException {
        Map params = new HashMap();
        params.put(URLP.getName().toString(), url);

        boolean isLocal = url.getProtocol().equalsIgnoreCase("file");
        if (isLocal && !(new File(url.getFile()).exists())) {
            return createNewDataStore(params);
        } else {
            return createDataStore(params);
        }
    }

    /**
     * @see org.geotools.data.dir.FileDataStoreFactory#createDataStore(java.net.URL)
     */
    public DataStore createDataStore(URL url, boolean memorymapped)
            throws IOException {
        Map params = new HashMap();
        params.put(URLP.getName().toString(), url);
        params.put(MEMORY_MAPPED.getName().toString(), new Boolean(memorymapped));
        return createDataStore(params);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getTypeName(URL url) throws IOException {
        DataStore ds = createDataStore(url);
        String[] names = ds.getTypeNames(); // should be exactly one
        return ((names == null || names.length == 0) ? null : names[0]);
    }

}

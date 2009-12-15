/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Factory used to construct a DataStore from a set of parameters.
 * <p>
 * The following example shows how a user might connect to a PostGIS database,
 * and maintain the resulting DataStore in a Registry:
 * </p>
 *
 * <p>
 * <pre><code>
 * HashMap params = new HashMap();
 * params.put("namespace", "leeds");
 * params.put("dbtype", "postgis");
 * params.put("host","feathers.leeds.ac.uk");
 * params.put("port", "5432");
 * params.put("database","postgis_test");
 * params.put("user","postgis_ro");
 * params.put("passwd","postgis_ro");
 *
 * DefaultRegistry registry = new DefaultRegistry();
 * registry.addDataStore("leeds", params);
 *
 * DataStore postgis = registry.getDataStore( "leeds" );
 * FeatureSource<SimpleFeatureType, SimpleFeature> = postgis.getFeatureSource( "table" );
 * </code></pre>
 * </p> 
 * <h2>Implementation Notes</h2>
 * <p>
 * An instance of this interface should exist for all data stores which want to
 * take advantage of the dynamic plug-in system. In addition to implementing
 * this factory interface each DataStore implementation should have a services file:
 * </p>
 *
 * <p>
 * <code>META-INF/services/org.geotoolkit.data.DataStoreFactory</code>
 * </p>
 *
 * <p>
 * The file should contain a single line which gives the full name of the
 * implementing class.
 * </p>
 *
 * <p>
 * example:<br/><code>e.g.
 * org.geotoolkit.data.mytype.MyTypeDataSourceFacotry</code>
 * </p>
 *
 * <p>
 * The factories are never called directly by client code, instead the
 * DataStoreFinder class is used.
 * </p>
 * 
 * @author Jody Garnett, Refractions Research
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface DataStoreFactory {

    /**
     * Test to see if the implementation is available for use.
     * This method ensures all the appropriate libraries to construct
     * the DataAccess are available.
     * <p>
     * Most factories will simply return <code>true</code> as GeoToolkit will
     * distribute the appropriate libraries. Though it's not a bad idea for
     * DataStoreFactories to check to make sure that the  libraries are there.
     * <p>
     * OracleDataStoreFactory is an example of one that may generally return
     * <code>false</code>, since GeoToolkit can not distribute the oracle jars.
     * (they must be added by the client.)
     * <p>
     * One may ask how this is different than canProcess, and basically available
     * is used by the DataStoreFinder getAvailableDataStore method, so that
     * DataStores that can not even be used do not show up as options in gui
     * applications.
     *
     * @return <tt>true</tt> if and only if this factory has all the
     *         appropriate jars on the classpath to create DataStores.
     */
    ConformanceResult availability();

    /**
     * Name suitable for display to end user.
     *
     * <p>
     * A non localized display name for this data store type.
     * </p>
     *
     * @return A short name suitable for display in a user interface.
     */
    String getDisplayName();

    /**
     * Describe the nature of the datasource constructed by this factory.
     *
     * <p>
     * A non localized description of this data store type.
     * </p>
     *
     * @return A human readable description that is suitable for inclusion in a
     *         list of available datasources.
     */
    String getDescription();

    /**
     * Metadata about the required Parameters (for createDataStore).
     *
     * @return ParameterDescriptorGroup describing the parameters for createDataStore
     */
    ParameterDescriptorGroup getParametersDescriptor();

    /**
     * Test to see if this factory is suitable for processing the data pointed
     * to by the params map.
     *
     * <p>
     * If this datasource requires a number of parameters then this mehtod
     * should check that they are all present and that they are all valid. If
     * the datasource is a file reading data source then the extentions or
     * mime types of any files specified should be checked. For example, a
     * Shapefile datasource should check that the url param ends with shp,
     * such tests should be case insensative.
     * </p>
     *
     * @param params The full set of information needed to construct a live
     *        data source.
     *
     * @return booean true if and only if this factory can process the resource
     *         indicated by the param set and all the required params are
     *         pressent.
     */
    boolean canProcess(Map<String, ? extends Serializable> params);

    /**
     * @see DataStoreFactory#canProcess(org.opengis.parameter.ParameterValueGroup) 
     */
    boolean canProcess(ParameterValueGroup params);

    /**
     * @see DataStoreFactory#createDataStore(org.opengis.parameter.ParameterValueGroup)
     */
    DataStore createDataStore(Map<String, ? extends Serializable> params) throws IOException;

    /**
     * Construct a live DataStore using the connection parameters provided.
     * <p>
     * You can think of this class as setting up a connection to the back end data source. The
     * required parameters are described by the getParameterInfo() method.
     * </p>
     *
     * <p>
     * Magic Params: the following params are magic and should be honoured by convention.
     *
     * <ul>
     * <li>
     * "user": is taken to be the user name
     * </li>
     * <li>
     * "passwd": is taken to be the password
     * </li>
     * </ul>
     *
     * When we eventually move over to the use of OpperationalParam we will
     * have to find someway to codify this convention.
     * </p>
     *
     * @param params The full set of information needed to construct a live
     *        data store. Typical key values for the map include: url -
     *        location of a resource, used by file reading datasources. dbtype
     *        - the type of the database to connect to, e.g. postgis, mysql
     *
     * @return The created DataStore, this may be null if the required resource
     *         was not found or if insufficent parameters were given. Note
     *         that canProcess() should have returned false if the problem is
     *         to do with insuficent parameters.
     *
     * @throws IOException if there were any problems setting up (creating or
     *         connecting) the datasource.
     */
    DataStore createDataStore(ParameterValueGroup params) throws IOException;

    DataStore createNewDataStore(Map<String, ? extends Serializable> params) throws IOException;

    DataStore createNewDataStore(ParameterValueGroup params) throws IOException;

}

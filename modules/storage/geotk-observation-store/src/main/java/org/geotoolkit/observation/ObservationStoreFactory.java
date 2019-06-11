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

package org.geotoolkit.observation;

import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataStoreFactory;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Factory used to open instances of FeatureStore.
 * <br/>
 * Factories must be declared using standard java services.
 * <br/>
 * Example, for a factory : x.y.XYObservationStoreFactory<br/>
 * A service registration file must be declared like :<br/>
 * <code>META-INF/services/org.geotoolkit.data.ObservationStoreFactory</code>
 * And contain a single line :<br/>
 * <code>x.y.XYObservationStoreFactory</code>
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface ObservationStoreFactory {

    public String getShortName();

    /**
     * @return Description of the parameters required for the creation of a {@link org.apache.sis.storage.DataStore}.
     */
    public abstract ParameterDescriptorGroup getOpenParameters();

    /**
     * Test to see if this factory is suitable for processing the data pointed
     * to by the params map.
     *
     * <p>
     * If this data source requires a number of parameters then this method
     * should check that they are all present and that they are all valid. If
     * the data source is a file reading data source then the extensions or
     * mime types of any files specified should be checked. For example, a
     * Shapefile data source should check that the url param ends with shp,
     * such tests should be case insensitive.
     * </p>
     *
     * @param params The full set of information needed to construct a live
     *        data source.
     *
     * @return boolean true if and only if this factory can process the resource
     *         indicated by the param set and all the required params are
     *         present.
     */
    public abstract boolean canProcess(ParameterValueGroup params);

    /**
     * Open a link to the storage location.
     * This method is intended to open an existing storage.
     * <br/>
     * If the purpose is to create a new one storage use the create method :
     * @see DataStoreFactory#create(org.opengis.parameter.ParameterValueGroup)
     *
     * @param params
     * @return DataStore opened store
     * @throws DataStoreException if parameters are incorrect or connexion failed.
     */
    public abstract DataStore open(ParameterValueGroup params) throws DataStoreException;

    /**
     * Create a new storage location.
     * This method is intended to create from scratch a new storage location.
     * <br/>
     * If the purpose is to open an already existing  storage use the open method :
     * @see DataStoreFactory#open(org.opengis.parameter.ParameterValueGroup)
     *
     * @param params
     * @return FeatureStore created store
     * @throws DataStoreException if parameters are incorrect or creation failed.
     */
    public abstract DataStore create(ParameterValueGroup params) throws DataStoreException;
}

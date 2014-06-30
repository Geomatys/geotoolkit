/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataStoreFactory;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Factory used to construct a CoverageStore from a set of parameters.
 *
 * <h2>Implementation Notes</h2>
 * <p>
 * An instance of this interface should exist for all data stores which want to
 * take advantage of the dynamic plug-in system. In addition to implementing
 * this factory interface each CoverageStore implementation should have a services file:
 * </p>
 *
 * <p>
 * <code>META-INF/services/org.geotoolkit.coverage.CoverageStoreFactory</code>
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
 * CoverageStoreFinder class is used.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface CoverageStoreFactory extends DataStoreFactory {

    /**
     * @see CoverageStoreFactory#create(org.opengis.parameter.ParameterValueGroup)
     */
    CoverageStore open(Map<String, ? extends Serializable> params) throws DataStoreException;

    /**
     * Construct a live CoverageStore using the connection parameters provided.
     * <p>
     * You can think of this class as setting up a connection to the back end data source. The
     * required parameters are described by the getParameterInfo() method.
     * </p>
     *
     * @param params The full set of information needed to construct a live
     *        data store. Typical key values for the map include: url -
     *        location of a resource, used by file reading datasources.
     *
     * @return The created CoverageStore, this may be null if the required resource
     *         was not found or if insufficient parameters were given. Note
     *         that canProcess() should have returned false if the problem is
     *         to do with insufficient parameters.
     *
     * @throws IOException if there were any problems setting up (creating or
     *         connecting) the datasource.
     */
    CoverageStore open(ParameterValueGroup params) throws DataStoreException;

    CoverageStore create(Map<String, ? extends Serializable> params) throws DataStoreException;

    CoverageStore create(ParameterValueGroup params) throws DataStoreException;

}

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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.model.ProcedureDataset;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface ObservationStore {

    /**
     * Get the parameters used to initialize this source from it's factory.
     *
     * @return source configuration parameters
     */
    Optional<ParameterValueGroup> getOpenParameters();

    /**
     * Get the factory which created this source.
     *
     * @return this source original factory
     */
    DataStoreProvider getProvider();

    /**
     * Returns information about the data store as a whole. The returned metadata object can contain
     * information such as the spatiotemporal extent of all contained {@linkplain Resource resources},
     * contact information about the creator or distributor, data quality, update frequency, usage constraints,
     * file format and more.
     *
     * @return information about resources in the data store. Should not be {@code null}.
     * @throws DataStoreException if an error occurred while reading the metadata.
     *
     */
    Metadata getMetadata() throws DataStoreException;

    /**
     * Return the complete list of sensor identifiers.
     *
     * @return A list of sensor identifier.
     */
    Set<GenericName> getProcedureNames() throws DataStoreException;

    /**
     * Return the complete list of sensor description.
     *
     * @return A list of sensor description.
     */
    List<ProcedureDataset> getProcedures() throws DataStoreException;

    /**
     * Return the complete list of phenomons identifiers.
     *
     * @return A list of sensor identifier.
     */
    Set<String> getPhenomenonNames() throws DataStoreException;

    /**
     * Return the Global time span of the observations data.
     *
     * @return A time period or instant.
     */
    TemporalGeometricPrimitive getTemporalBounds() throws DataStoreException;

    /**
     * Extract All the procedures / observations / features of interest /
     * phenomenon / spatial informations in this store.
     *
     * @return
     * @throws DataStoreException
     */
    ObservationDataset getResults() throws DataStoreException;

    /**
     * Extract All the procedures / observations / features of interest /
     * phenomenon / spatial informations in this store. Allow to filter by
     * specifying a list of accepted sensor identifiers.
     *
     * @param sensorIds a filter on sensor identifiers or {@code null}.
     */
    ObservationDataset getResults(final List<String> sensorIds) throws DataStoreException;

    /**
     * Extract All the procedures / observations / features of interest / phenoemenon / spatial informations in this store.
     * Allow to filter by specifying a list of accepted sensor identifiers.
     * If specified the results will be asigned to a new/existing sensor.
     *
     * @param affectedSensorID a assigned sensor identifier or {@code null}.
     * @param sensorIds a filter on sensor identifiers or {@code null}.
     */
    ObservationDataset getResults(final String affectedSensorID, final List<String> sensorIds) throws DataStoreException;

    /**
     * Return an Observation Reader on the data.
     *
     * @return An Observation Reader.
     * @throws org.apache.sis.storage.DataStoreException if the reader creation fails
     */
    ObservationReader getReader() throws DataStoreException;

    /**
     * Return an Observation Filter on the data.
     *
     * @return An Observation Filter.
     * @throws org.apache.sis.storage.DataStoreException if the writer creation fails
     */
    ObservationFilterReader getFilter() throws DataStoreException;

    /**
     * Return an Observation Writer on the data.
     *
     * @return An Observation Writer.
     * @throws org.apache.sis.storage.DataStoreException if the writer creation fails
     */
    ObservationWriter getWriter() throws DataStoreException;

    ObservationStoreCapabilities getCapabilities();

    void close() throws DataStoreException;

}

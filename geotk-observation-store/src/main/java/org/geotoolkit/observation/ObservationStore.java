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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.model.Offering;
import org.geotoolkit.observation.model.ProcedureDataset;
import org.geotoolkit.observation.query.AbstractObservationQuery;
import org.geotoolkit.observation.query.DatasetQuery;
import org.geotoolkit.observation.query.HistoricalLocationQuery;
import org.geotoolkit.observation.query.IdentifierQuery;
import org.geotoolkit.observation.query.LocationQuery;
import org.geotoolkit.observation.query.ObservationQuery;
import org.geotoolkit.observation.query.ObservedPropertyQuery;
import org.geotoolkit.observation.query.OfferingQuery;
import org.geotoolkit.observation.query.ProcedureQuery;
import org.geotoolkit.observation.query.ResultQuery;
import org.geotoolkit.observation.query.SamplingFeatureQuery;
import org.locationtech.jts.geom.Geometry;
import org.opengis.metadata.Metadata;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.Procedure;
import org.geotoolkit.observation.model.SamplingFeature;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.TemporalGeometricPrimitive;

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
     * Return {@code true} if the specified entity identifier exist.
     *
     *
     * @param query an idntifier query.
     * @return {@code true} if the specified entity identifier exist.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    boolean existEntity(final IdentifierQuery query) throws DataStoreException;

    /**
     * Return the complete list of entity identifiers.
     *
     * @param query a query to filter the entities to list.
     * @return A list of identifier.
     */
    Set<String> getEntityNames(AbstractObservationQuery query) throws DataStoreException;

    /**
     * Execute the current query and return the matching count.
     *
     * @param query a query to filter the entities to list.
     *
     * @return The matching count of the query.
     */
    long getCount(AbstractObservationQuery query) throws DataStoreException;

    /**
     * Return the complete list of sensor description.
     *
     * @param query A query to filter the data extraction.
     * @return A list of sensor description.
     */
    List<ProcedureDataset> getProcedureDatasets(DatasetQuery query) throws DataStoreException;

    /**
     * return the locations list of sensors over the time.
     *
     * @param query A query to filter the data extraction.
     *
     * @return A map of procedure/time-location.
     */
    Map<String, Map<Date, Geometry>> getHistoricalSensorLocations(HistoricalLocationQuery query) throws DataStoreException;

    /**
     * return the times location list of sensors over the time.
     * this a lighter version of getHistoricalSensorLocations() wih no geometry extractions.
     *
     * @param query A query to filter the data extraction.
     *
     * @return A map of procedure/time-location.
     */
    Map<String, Set<Date>> getHistoricalSensorTimes(HistoricalLocationQuery query) throws DataStoreException;

    /**
     * return the locations list of sensors last position.
     *
     * @param query A query to filter the data extraction.
     *
     * @return A map of procedure/location.
     */
    Map<String, Geometry> getSensorLocations(LocationQuery query) throws DataStoreException;

    /**
     * Return a list of Sampling feature matching the query.
     *
     * @param query A query to filter the data extraction.
     *
     * @return A list of Sampling feature matching the query.
     */
    List<SamplingFeature> getFeatureOfInterest(SamplingFeatureQuery query) throws DataStoreException;

    /**
     * Return a list of Phenomenon matching the query.
     *
     * @param query A query to filter the data extraction.
     *
     * @return A list of Phenomenon matching the builded filter.
     */
    List<Phenomenon> getPhenomenons(ObservedPropertyQuery query) throws DataStoreException;

    /**
     * Return a list of Observation matching the query.
     *
     * @param query A query to filter the data extraction.
     *
     * @return A list of Observation matching the query.
     */
    List<Observation> getObservations(ObservationQuery query) throws DataStoreException;

    /**
     * Return a list of procedure matching the query.
     *
     * @param query A query to filter the data extraction.
     *
     * @return A list of procedure matching the query.
     */
    List<Procedure> getProcedures(ProcedureQuery query) throws DataStoreException;

    /**
     * Return a list of offering matching the query.
     *
     * @param query A query to filter the data extraction.
     *
     * @return A list of offering matching the query.
     */
    List<Offering> getOfferings(OfferingQuery query) throws DataStoreException;

    /**
     * Return direct observations results.
     * Object type depends on the response Mode, response formats, etc/
     *
     * @param query A query to filter the data extraction.
     *
     * @return observation results.
     */
    Object getResults(ResultQuery query) throws DataStoreException;

    /**
     * Return the Global time span of the observations data.
     *
     * @return A time period or instant.
     */
    TemporalGeometricPrimitive getTemporalBounds() throws DataStoreException;

    /**
     * Return the time span of the identified entity;
     *
     * @param query A query to filter the data extraction.
     *
     * @return A time period or instant.
     */
    TemporalGeometricPrimitive getEntityTemporalBounds(IdentifierQuery query) throws DataStoreException;

    /**
     * Extract All the procedures / observations / features of interest /
     * phenomenon / spatial informations in this store.
     *
     * @param query A query to filter the dataset extraction.
     * @return
     * @throws DataStoreException
     */
    ObservationDataset getDataset(DatasetQuery query) throws DataStoreException;

    /**
     * Return an Observation template for the specified sensor.
     *
     * @param sensorId A sensor identifier.
     *
     * @return 1n Observation template.
     */
    Observation getTemplate(String sensorId) throws DataStoreException;

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

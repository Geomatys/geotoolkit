/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.observation;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.observation.model.OMEntity;
import org.geotoolkit.observation.model.Offering;
import org.geotoolkit.observation.model.ResponseMode;
import org.geotoolkit.observation.model.Result;
import org.geotoolkit.observation.query.IdentifierQuery;
import org.locationtech.jts.geom.Geometry;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.Process;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface ObservationReader {

    public static final String ENTITY_TYPE = "entityType";
    public static final String SENSOR_TYPE = "sensorType";
    public static final String SOS_VERSION = "version";
    public static final String IDENTIFIER = "id";

    /**
     * Return the list of entity identifiers.
     *
     * @param entityType The type of entity to list.
     * @return A list of entity identifiers.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    Collection<String> getEntityNames(final OMEntity entityType) throws DataStoreException;

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
     * Return The offering for the specified identifier.
     *
     * @param offeringId An offering identifier.
     * @return The offering for the specified idntifier or {@code null}.
     *
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    Offering getObservationOffering(String offeringId) throws DataStoreException;

    /**
     * Return a identified phenomenon if exist.
     *
     * Implementation must return {@linkplain org.geotoolkit.observation.model.Phenomenon}
     *
     * @param phenomenonId A phenomenon identifier.
     *
     * @return a phenomenon or {@code null}.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    Phenomenon getPhenomenon(String phenomenonId) throws DataStoreException;

    /**
     * Return a process by its identifier.
     *
     * @param identifier process identifier.
     *
     * Implementation should always return instanceof {@linkplain org.geotoolkit.observation.model.Process}
     *
     * @return a process.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs
     * during retrieval.
     */
    Process getProcess(final String identifier) throws DataStoreException;

    /**
     * Return the temporal bounds for the specified procedure.
     *
     * @param sensorID an procedure identifier.
     * @return the temporal bounds for the specified procedure.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    TemporalGeometricPrimitive getProcedureTime(final String sensorID) throws DataStoreException;

    /**
     * Return a sampling feature for the specified sampling feature.
     *
     * @param samplingFeatureId The identifier of the feature of interest.
     *
     * @return the corresponding feature Of interest.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    SamplingFeature getFeatureOfInterest(final String samplingFeatureId) throws DataStoreException;

    /**
     * Return the time span for the specified sampling feature identifier.
     *
     * @param samplingFeatureName The identifier of the feature of interest.
     *
     * @return the time span a sampling feature.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    TemporalGeometricPrimitive getFeatureOfInterestTime(final String samplingFeatureName) throws DataStoreException;

    /**
     * Return an observation for the specified identifier.
     *
     * @param identifier Observation identifier.
     * @param resultModel Result model , like Measurements or complex observations.
     * @param mode Result mode (inline, result template...)
     *
     * @return An observation.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    Observation getObservation(final String identifier, final QName resultModel, final ResponseMode mode) throws DataStoreException;

    /**
     * return an observation template for the specified procedure.
     *
     * @param procedure a procedure identifier.
     * @return an observation template.
     * @throws DataStoreException If an error occurs during retrieval.
     */
    Observation getTemplateForProcedure(final String procedure) throws DataStoreException;

    /**
     * Return a result for the specified identifier.
     *
     * @param identifier Observation identifier.
     * @param resultModel Result model , like Measurements or complex observations.
     *
     * @return a result for the specified identifier.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    Result getResult(final String identifier, final QName resultModel) throws DataStoreException;

    /**
     * Return the minimal/maximal value for the offering event Time
     *
     * @return A time span.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    TemporalGeometricPrimitive getEventTime() throws DataStoreException;

    /**
     * Extract the geometry for a procedure.
     *
     * @param sensorID the procedure/sensor identifier
     *
     * @return sensor geometry.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    Geometry getSensorLocation(final String sensorID) throws DataStoreException;

    /**
     * Extract the locations through time for a procedure.
     *
     * @param sensorID the procedure/sensor identifier
     *
     * @return ssensor locations over time.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs
     * during retrieval.
     */
    Map<Date, Geometry> getSensorLocations(final String sensorID) throws DataStoreException;

    /**
     * free the resources and close the database connection if there is one.
     */
    void destroy();
}

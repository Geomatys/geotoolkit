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

// J2SE dependencies
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.sos.xml.ObservationOffering;
import org.geotoolkit.sos.xml.ResponseModeType;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.Process;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.temporal.TemporalPrimitive;

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
     * The hints can contains various filter such as :
     * - SOS version of the request (key: version)
     * - object entity (key: entityType)
     * - sensor type: (key: sensorType)
     *
     * @param hints a map of filters.
     * @return A list of entity identifiers.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    Collection<String> getEntityNames(final Map<String, Object> hints) throws DataStoreException;

    /**
     * Return {@code true} if the specified entity identifier exist.
     *
      * The hints can contains various filter such as :
     * - object entity (key: entityType)
     * - identifier: (key: id)
     *
     * @param hints a map of filters.
     * @return {@code true} if the specified entity identifier exist.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    boolean existEntity(final Map<String, Object> hints) throws DataStoreException;

    /**
     * Return The offerings for the specified names.
     *
     * The hints can contains various filter such as :
     * - SOS version of the request (key: version)
     * - sensor type: (key: sensorType)
     * - one ore many offering identifiers: (key: id)
     *
     * @param hints a map of filters.
     * @return The offerings for the specified names.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    List<ObservationOffering> getObservationOfferings(final Map<String, Object> hints) throws DataStoreException;

    /**
     * Return a filtered list of phenomenons.
     *
     * * The hints can contains various filter such as :
     * - SOS version of the request (key: version)
     * - one ore many offering identifiers: (key: id)
     *
     * @param hints a map of filters.
     * @return a list of phenomenons.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    Collection<Phenomenon> getPhenomenons(final Map<String, Object> hints) throws DataStoreException;

    /**
     * Return a process by its identifier.
     *
     * @param identifier process identifier.
     * @param version SOS version.
     *
     * @return a process.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs
     * during retrieval.
     */
    Process getProcess(final String identifier, final String version) throws DataStoreException;

    /**
     * Return the temporal bounds for the specified procedure.
     *
     * @param version SOS version of the request.
     * @param sensorID an procedure identifier.
     * @return the temporal bounds for the specified procedure.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    TemporalGeometricPrimitive getTimeForProcedure(final String version, final String sensorID) throws DataStoreException;

    /**
     * Return a sampling feature for the specified sampling feature.
     *
     * @param samplingFeatureName The identifier of the feature of interest.
     * @param version SOS version of the request.
     *
     * @return the corresponding feature Of interest.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    SamplingFeature getFeatureOfInterest(final String samplingFeatureName, final String version) throws DataStoreException;

    /**
     * Return the time span for the specified sampling feature (identified by its name and version).
     *
     * @param samplingFeatureName The identifier of the feature of interest.
     * @param version SOS version of the request.
     *
     * @return the time span a sampling feature.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    TemporalPrimitive getFeatureOfInterestTime(final String samplingFeatureName, final String version) throws DataStoreException;

    /**
     * Return an observation for the specified identifier.
     *
     * @param identifier Observation identifier.
     * @param resultModel Result model , like Measurements or complex observations.
     * @param mode Result mode (inline, result template...)
     * @param version SOS version of the request.
     *
     * @return An observation.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    Observation getObservation(final String identifier, final QName resultModel, final ResponseModeType mode, final String version) throws DataStoreException;

    /**
     * return an observation template for the specified procedure.
     *
     * @param procedure a procedure identifier.
     * @param version output version of the template.
     * @return an observation template.
     * @throws DataStoreException If an error occurs during retrieval.
     */
    Observation getTemplateForProcedure(final String procedure, final String version) throws DataStoreException;

    /**
     * Return a result for the specified identifier.
     *
     * @param identifier Observation identifier.
     * @param resultModel Result model , like Measurements or complex observations.
     * @param version SOS version of the request.
     *
     * @return a result for the specified identifier.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    Object getResult(final String identifier, final QName resultModel, final String version) throws DataStoreException;

    /**
     * Create a new identifier for an observation.
     *
     * @return an observation identifier.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    String getNewObservationId() throws DataStoreException;

    /**
     * Return the minimal/maximal value for the offering event Time
     *
     * @param version SOS version.
     * @return A time span.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    TemporalPrimitive getEventTime(String version) throws DataStoreException;

    /**
     * Return the list of supported response modes.
     *
     * @return supported response modes.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    List<ResponseModeType> getResponseModes() throws DataStoreException;

    /**
     * Return the list of supported response formats for each version.
     *
     * @return supported response formats by version.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    Map<String, List<String>> getResponseFormats() throws DataStoreException;

    /**
     * Extract the geometry for a procedure.
     *
     * @param sensorID the procedure/sensor identifier
     * @param version SOS version of the request.
     *
     * @return sensor geometry.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs during retrieval.
     */
    AbstractGeometry getSensorLocation(final String sensorID, final String version) throws DataStoreException;

    /**
     * Extract the locations through time for a procedure.
     *
     * @param sensorID the procedure/sensor identifier
     * @param version SOS version of the request.
     *
     * @return ssensor locations over time.
     * @throws org.apache.sis.storage.DataStoreException If an error occurs
     * during retrieval.
     */
    Map<Date, AbstractGeometry> getSensorLocations(final String sensorID, final String version) throws DataStoreException;

    /**
     * Return informations about the implementation class.
     * @return informations.
     */
    String getInfos();

    /**
     * free the resources and close the database connection if there is one.
     */
    void destroy();
}

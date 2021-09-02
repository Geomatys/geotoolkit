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
     * @throws org.apache.sis.storage.DataStoreException
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
     * @return
     * @throws org.apache.sis.storage.DataStoreException
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
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<ObservationOffering> getObservationOfferings(final Map<String, Object> hints) throws DataStoreException;

    /**
     * Return a list of the phenomenon.
     *
     * * The hints can contains various filter such as :
     * - SOS version of the request (key: version)
     * - one ore many offering identifiers: (key: id)
     *
     * @param hints a map of filters.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<Phenomenon> getPhenomenons(final Map<String, Object> hints) throws DataStoreException;

    /**
     * Return a process.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Process getProcess(final String identifier, final String version) throws DataStoreException;

    /**
     * Return a the temporal bounds for the specified procedure.
     *
     * @param version SOS version of the request.
     * @param sensorID an procedure identifier.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    TemporalGeometricPrimitive getTimeForProcedure(final String version, final String sensorID) throws DataStoreException;

    /**
     * Return a sampling feature for the specified sampling feature.
     *
     * @param samplingFeatureName The identifier of the feature of interest.
     * @param version SOS version of the request.
     *
     * @return the corresponding feature Of interest.
     * @throws org.apache.sis.storage.DataStoreException
     */
    SamplingFeature getFeatureOfInterest(final String samplingFeatureName, final String version) throws DataStoreException;

    /**
     * Return a sampling feature for the specified sampling feature.
     *
     * @param samplingFeatureName
     * @param version SOS version of the request.
     *
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    TemporalPrimitive getFeatureOfInterestTime(final String samplingFeatureName, final String version) throws DataStoreException;

    /**
     * Return an observation for the specified identifier.
     *
     * @param identifier
     * @param resultModel
     * @param mode
     * @param version
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Observation getObservation(final String identifier, final QName resultModel, final ResponseModeType mode, final String version) throws DataStoreException;

    /**
     * return an observation template for the specified procedure.
     *
     * @param procedure a procedure identifier.
     * @param version output version of the template.
     * @return
     * @throws DataStoreException
     */
    Observation getTemplateForProcedure(final String procedure, final String version) throws DataStoreException;

    /**
     * Return a result for the specified identifier.
     *
     * @param identifier
     * @param resultModel
     * @param version
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Object getResult(final String identifier, final QName resultModel, final String version) throws DataStoreException;

    /**
     * Create a new identifier for an observation.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    String getNewObservationId() throws DataStoreException;

    /**
     * Return the minimal/maximal value for the offering event Time
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    TemporalPrimitive getEventTime(String version) throws DataStoreException;

    /**
     * Return the list of supported response Mode
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<ResponseModeType> getResponseModes() throws DataStoreException;

    /**
     * Return the list of supported response formats for each version
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Map<String, List<String>> getResponseFormats() throws DataStoreException;

    /**
     * Extract the geometry for a procedure.
     *
     * @param sensorID the procedure/sensor identifier
     * @param version
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    AbstractGeometry getSensorLocation(final String sensorID, final String version) throws DataStoreException;

    /**
     * Extract the locations through time for a procedure.
     *
     * @param sensorID the procedure/sensor identifier
     * @param version
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Map<Date, AbstractGeometry> getSensorLocations(final String sensorID, final String version) throws DataStoreException;

    /**
     * Return informations about the implementation class.
     * @return
     */
    String getInfos();

    /**
     * free the resources and close the database connection if there is one.
     */
    void destroy();
}

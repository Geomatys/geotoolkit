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
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.sos.xml.ObservationOffering;
import org.geotoolkit.sos.xml.ResponseModeType;
import org.opengis.observation.Observation;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.temporal.TemporalPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface ObservationReader {
    
    /**
     * Return the list of offering names.
     *
     * @param version SOS version of the request
     * @return A list of offering name.
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<String> getOfferingNames(final String version) throws DataStoreException;
    
    /**
     * Return the list of offering names filtering on the procedure type.
     *
     * @param version SOS version of the request
     * @param sensorType A filter on the type of sensor or @{code null}
     * 
     * @return A list of offering name.
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<String> getOfferingNames(final String version, final String sensorType) throws DataStoreException;

    /**
     * Return The offering with the specified name.
     *
     * @param offeringName The identifier of the offering
     * @param version SOS version of the request
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    ObservationOffering getObservationOffering(final String offeringName, final String version) throws DataStoreException;
    
    /**
     * Return The offerings for the specified names.
     *
     * @param offeringNames The identifiers of the offerings
     * @param version SOS version of the request
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<ObservationOffering> getObservationOfferings(final List<String> offeringNames, final String version) throws DataStoreException;

    /**
     * Return a list of all the offerings.
     * 
     * @param version SOS version of the request
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<ObservationOffering> getObservationOfferings(final String version) throws DataStoreException;
    
    /**
     * Return a list of all the offerings filtering on the procedure type.
     * 
     * @param version SOS version of the request
     * @param sensorType A filter on the type of sensor or @{code null}
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<ObservationOffering> getObservationOfferings(final String version, final String sensorType) throws DataStoreException;

    /**
     * Return a list of the sensor identifiers.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<String> getProcedureNames() throws DataStoreException;
    
    /**
     * Return a list of the sensor identifiers.
     * @param sensorType A filter on the type of sensor or @{code null}
     * 
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<String> getProcedureNames(final String sensorType) throws DataStoreException;

    /**
     * Return a list of the phenomenon identifiers.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<String> getPhenomenonNames() throws DataStoreException;

    /**
     * Return a list of the sensor identifiers measuring the specified phenomenon.
     * 
     * @param observedProperty an observed phenomenon.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<String> getProceduresForPhenomenon(final String observedProperty) throws DataStoreException;
    
    /**
     * Return a list of the observedProperties identifiers measured by the specified procedure.
     * 
     * @param sensorID an procedure identifier.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<String> getPhenomenonsForProcedure(final String sensorID) throws DataStoreException;
    
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
     * 
     * @param phenomenonName
     * @return
     * @throws org.apache.sis.storage.DataStoreException 
     */
    boolean existPhenomenon(final String phenomenonName) throws DataStoreException;
    
    /**
     * Return a list of sampling feature identifiers.
     *
     * @return A list of sampling feature identifiers.
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<String> getFeatureOfInterestNames() throws DataStoreException;

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
     * Return a reference from the specified identifier
     * @param href
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    boolean existProcedure(final String href) throws DataStoreException;
    
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
    List<String> getEventTime() throws DataStoreException;

    /**
     * Return the list of supported response Mode
     * @return 
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<ResponseModeType> getResponseModes() throws DataStoreException;

    /**
     * Return the list of supported response Mode
     * @return 
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<String> getResponseFormats() throws DataStoreException;

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
     * Return informations about the implementation class.
     * @return 
     */
    String getInfos();
    
    /**
     * free the resources and close the database connection if there is one.
     */
    void destroy();
}

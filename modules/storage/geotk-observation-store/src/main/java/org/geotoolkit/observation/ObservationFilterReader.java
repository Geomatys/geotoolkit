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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.sos.xml.ResponseModeType;
import org.opengis.filter.temporal.BinaryTemporalOperator;
import org.opengis.geometry.Geometry;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.observation.Process;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface ObservationFilterReader {

    /**
     * Initialize the query for a full observation request.
     *
     * @param requestMode
     * @param resultModel
     * @param hints
     * @throws org.apache.sis.storage.DataStoreException
     */
    void initFilterObservation(final ResponseModeType requestMode, final QName resultModel, final Map<String,String> hints) throws DataStoreException;

    /**
     * Initialize the query for an extraction restricted to the results request.
     *
     * @param procedure
     * @param resultModel
     * @param hints
     * @throws org.apache.sis.storage.DataStoreException
     */
    void initFilterGetResult(final String procedure, final QName resultModel, final Map<String,String> hints) throws DataStoreException;

    /**
     * Initialize the query for extracting feature of interest request.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    void initFilterGetFeatureOfInterest() throws DataStoreException;

    /**
     * Initialize the query for extracting phenomenon request.
     * @throws org.apache.sis.storage.DataStoreException
     */
    void initFilterGetPhenomenon() throws DataStoreException;

    /**
     * Initialize the query for extracting procedure request.
     * @throws org.apache.sis.storage.DataStoreException
     */
    void initFilterGetSensor() throws DataStoreException;

    /**
     * Initialize the query for extracting offering request.
     * @throws org.apache.sis.storage.DataStoreException
     */
    void initFilterOffering() throws DataStoreException;

    /**
     * Initialize the query for extracting procedure locations request.
     * @throws org.apache.sis.storage.DataStoreException
     */
    void initFilterGetLocations() throws DataStoreException;

    /**
     * Initialize the query for extracting procedure locations request.
     * @throws org.apache.sis.storage.DataStoreException
     */
    void initFilterGetProcedureTimes() throws DataStoreException;

    /**
     * Add some procedure filter to the request.
     *
     * @param procedures
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setProcedure(final List<String> procedures) throws DataStoreException;

    /**
     * Add some filter ont procedure type to the request.
     *
     * @param type
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setProcedureType(final String type) throws DataStoreException;

    /**
     * Add some phenomenon filter to the request.
     *
     * @param phenomenon
     */
    void setObservedProperties(final List<String> phenomenon);

    /**
     * Add some feature of interest filter to the request.
     *
     * @param fois the feature of interest identifiers.
     */
    void setFeatureOfInterest(final List<String> fois);

    /**
     * Add some observation identifier filter to the request.
     *
     * @param ids the observations identifiers.
     */
    void setObservationIds(final List<String> ids);

    /**
     * Add a Temporal filter to the current request.
     *
     * @param tFilter
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setTimeFilter(BinaryTemporalOperator tFilter) throws DataStoreException;

    /**
     * Add a BBOX filter to the current request.
     * ( this method is implemented only if isBoundedObservation() return true)
     *
     * @param e
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setBoundingBox(Envelope e) throws DataStoreException;

    /**
     * Set the offering for the current request
     *
     * @param offerings
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setOfferings(final List<String> offerings) throws DataStoreException;

    /**
     * Add a filter on the result for the specified property.
     *
     * @param propertyName a property of the result.
     * @param value a literal value.
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setResultEquals(String propertyName, String value) throws DataStoreException;

    /**
     * Return the list of properties that can be applied on the result.
     *
     * @return  the list of properties that can be applied on the result.
     */
    List<String> supportedQueryableResultProperties();

    /**
     * Execute the current query and return a list of observation result.
     *
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<ObservationResult> filterResult() throws DataStoreException;

    /**
     * Execute the current query and return a list of observation ID.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Set<String> filterObservation() throws DataStoreException;


    /**
     * Execute the current query and return a list of FOI ID.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Set<String> filterFeatureOfInterest() throws DataStoreException;

    /**
     * Execute the current query and return a list of ObservedProperty ID.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Set<String> filterPhenomenon() throws DataStoreException;

    /**
     * Execute the current query and return a list of procedure ID.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Set<String> filterProcedure() throws DataStoreException;

    /**
     * Execute the current query and return a list of offering ID.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Set<String> filterOffering() throws DataStoreException;

    /**
     * Return informations about the implementation class.
     */
    String getInfos();

    /**
     * Refresh the index if it need it.
     * @throws org.apache.sis.storage.DataStoreException
     */
    void refresh() throws DataStoreException;

    /**
     * Return true if each observation has a position.
     */
    boolean isBoundedObservation();

    /**
     * Return true if template are filled with a default period when there is no eventTime suplied.
     */
    boolean isDefaultTemplateTime();

    void destroy();

    /**
     * Return a list of Observation templates matching the builded filter.
     *
     * @param hints extraction hints like the O&M version for the xml object returned.
     *
     * @return A list of Observation templates matching the builded filter.
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<Observation> getObservationTemplates(final Map<String,String> hints) throws DataStoreException;

     /**
     * Return a list of Observation matching the builded filter.
     *
     * @param hints extraction hints like the O&M version for the xml object returned.
     *
     * @return A list of Observation matching the builded filter.
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<Observation> getObservations(final Map<String,String> hints) throws DataStoreException;

    /**
     *
     * @param hints extraction hints like the O&M version for the xml object returned.
     *
     * @return
     * @throws DataStoreException
     */
    List<SamplingFeature> getFeatureOfInterests(final Map<String,String> hints) throws DataStoreException;

    /**
     *
     * @param hints extraction hints like the O&M version for the xml object returned.
     *
     * @return
     * @throws DataStoreException
     */
    List<Phenomenon> getPhenomenons(final Map<String,String> hints) throws DataStoreException;

    /**
     *
     * @param hints extraction hints like the O&M version for the xml object returned.
     *
     * @return
     * @throws DataStoreException
     */
    List<Process> getProcesses(final Map<String,String> hints) throws DataStoreException;

    /**
     *
     * @param hints extraction hints like the O&M version for the xml object returned.
     *
     * @return
     * @throws DataStoreException
     */
    Map<String, Map<Date, Geometry>> getSensorLocations(final Map<String,String> hints) throws DataStoreException;

    /**
     *
     * @param hints extraction hints like the O&M version for the xml object returned.
     *
     * @return
     * @throws DataStoreException
     */
    Map<String, List<Date>> getSensorTimes(final Map<String,String> hints) throws DataStoreException;

    /**
     * Return an encoded block of data in a string. The datas are the results of
     * the matching observations.
     *
     * @param hints hints like decimation size, algorithm etc.
     *
     * @return An encoded block of data in a string.
     * @throws org.apache.sis.storage.DataStoreException
     */
    Object getResults(final Map<String, String> hints) throws DataStoreException;

    /**
     * Return an encoded block of data in a string.
     * The datas are the results of the matching observations.
     * The datas are usually encoded as CSV (comma separated value) format.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Object getOutOfBandResults() throws DataStoreException;

    /**
     * MIME type of the data that will be returned as the result of a GetObservation request.
     * This is usually text/xml; subtype="om/1.0.0".
     * In the case  that data is delivered out of band it might be text/xml;subtype="tml/2.0" for TML or some
     * other MIME type.
     *
     * @param responseFormat the MIME type of the response.
     */
    void setResponseFormat(String responseFormat);

    /**
     * return true if the filter reader take in charge the calculation of the collection bounding shape.
     *
     * @return True if the filter compute itself the bounding shape of the collection.
     */
    boolean computeCollectionBound();

    /**
     * If the filter reader caompute itself the bounding shape of the obervation collection.
     * this methode return the current shape.
     * @return
     */
    Envelope getCollectionBoundingShape();
}


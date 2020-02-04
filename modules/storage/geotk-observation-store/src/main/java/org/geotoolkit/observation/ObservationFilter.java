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

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.sos.xml.ObservationOffering;
import org.geotoolkit.sos.xml.ResponseModeType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface ObservationFilter {

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
     * Add some procedure filter to the request.
     * if the list of procedure ID is empty it add all the offering procedure.
     *
     * @param procedures
     * @param offerings
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setProcedure(final List<String> procedures, final List<ObservationOffering> offerings) throws DataStoreException;

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
     * Add a TM_Equals filter to the current request.
     *
     * @param time
     * @throws org.apache.sis.storage.DataStoreException
     */
     void setTimeEquals(Object time) throws DataStoreException;

    /**
     * Add a TM_Before filter to the current request.
     *
     * @param time
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setTimeBefore(Object time) throws DataStoreException;

    /**
     * Add a TM_After filter to the current request.
     *
     * @param time
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setTimeAfter(Object time) throws DataStoreException;

    /**
     * Add a TM_During filter to the current request.
     *
     * @param time
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setTimeDuring(Object time) throws DataStoreException;

    /**
     * Add a latest time filter to the current request.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setTimeLatest() throws DataStoreException;

    /**
     * Add a first time filter to the current request.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setTimeFirst() throws DataStoreException;

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
    void setOfferings(final List<ObservationOffering> offerings) throws DataStoreException;

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
}

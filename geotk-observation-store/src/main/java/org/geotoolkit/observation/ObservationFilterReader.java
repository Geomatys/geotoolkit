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

import org.geotoolkit.observation.model.OMEntity;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.xml.Envelope;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.TemporalOperator;
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

    void init(OMEntity objectType, final Map<String,Object> hints) throws DataStoreException;

    /**
     * Add some procedure filter to the request.
     */
    void setProcedure(final List<String> procedures) throws DataStoreException;

    /**
     * Add some filter ont procedure type to the request.
     */
    void setProcedureType(final String type) throws DataStoreException;

    /**
     * Add some phenomenon filter to the request.
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
     */
    void setTimeFilter(TemporalOperator tFilter) throws DataStoreException;

    /**
     * Add a BBOX filter to the current request.
     * ( this method is implemented only if isBoundedObservation() return true)
     */
    void setBoundingBox(Envelope e) throws DataStoreException;

    /**
     * Set the offering for the current request
     */
    void setOfferings(final List<String> offerings) throws DataStoreException;

    /**
     * Add a filter on the result.
     *
     * @param filter a comparison filter the result.
     */
    void setResultFilter(final BinaryComparisonOperator filter) throws DataStoreException;

    /**
     * Return the list of properties that can be applied on the result.
     *
     * @return  the list of properties that can be applied on the result.
     */
    List<String> supportedQueryableResultProperties();

    /**
     * Execute the current query and return a list of observation result.
     */
    List<ObservationResult> filterResult() throws DataStoreException;

    /**
     * Execute the current query and return a list of entity identifiers.
     */
    Set<String> getIdentifiers() throws DataStoreException;

    /**
     * Execute the current query and return the matching count.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    long getCount() throws DataStoreException;

    /**
     * Refresh the index if it need it.
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
     * Return a list of Observation matching the builded filter.
     *
     * @return A list of Observation matching the builded filter.
     */
    List<Observation> getObservations() throws DataStoreException;

    /**
     *
     */
    List<SamplingFeature> getFeatureOfInterests() throws DataStoreException;

    /**
     *
     */
    List<Phenomenon> getPhenomenons() throws DataStoreException;

    /**
     *
     */
    List<Process> getProcesses() throws DataStoreException;

    /**
     *
     */
    Map<String, Geometry> getSensorLocations() throws DataStoreException;

    /**
     *
     * @return
     * @throws DataStoreException
     */
    Map<String, Map<Date, Geometry>> getSensorHistoricalLocations() throws DataStoreException;

    /**
     *
     */
    Map<String, List<Date>> getSensorTimes() throws DataStoreException;

    /**
     * Return direct observations results.
     * Object type depends on the response Mode, response formats, etc/
     *
     * @return An encoded block of data in a string.
     */
    Object getResults() throws DataStoreException;

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
     */
    Envelope getCollectionBoundingShape();
}


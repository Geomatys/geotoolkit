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
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.observation.model.Offering;
import org.geotoolkit.observation.query.AbstractObservationQuery;
import org.locationtech.jts.geom.Geometry;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.TemporalOperator;
import org.opengis.geometry.Envelope;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.SamplingFeature;
import org.geotoolkit.observation.model.Procedure;
import org.opengis.filter.LogicalOperatorName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface ObservationFilterReader {

    void init(AbstractObservationQuery query) throws DataStoreException;

    default void startFilterBlock() {
        startFilterBlock(null);
    }

    void startFilterBlock(LogicalOperatorName operator);

    void appendFilterOperator(LogicalOperatorName operator, FilterAppend merged);

    void removeFilterOperator(LogicalOperatorName operator, FilterAppend merged, FilterAppend previous);

    void endFilterBlock(LogicalOperatorName operator, FilterAppend merged);

    /**
     * Add some procedure filter to the request.
     *
     * @param procedure A procedure identifier.
     *
     * @return informations about if the filter has been append or not
     */
    FilterAppend setProcedure(final String procedure) throws DataStoreException;

    /**
     * Add some filter ont procedure type to the request.
     *
     * @param type procedure type like 'System' or 'component'.
     *
     * @return informations about if the filter has been append or not
     */
    FilterAppend setProcedureType(final String type) throws DataStoreException;

    /**
     * Add some phenomenon filter to the request.
     *
     * @param phenomenon A phenomenon identifiers.
     *
     * @return informations about if the filter has been append or not
     */
    FilterAppend setObservedProperty(final String phenomenon);

    /**
     * Add some feature of interest filter to the request.
     *
     * @param foiId A feature of interest identifier.
     *
     * @return informations about if the filter has been append or not
     */
    FilterAppend setFeatureOfInterest(final String foiId);

    /**
     * Add some observation identifier filter to the request.
     *
     * @param id the observation identifier.
     *
     * @return informations about if the filter has been append or not
     */
    FilterAppend setObservationId(final String id);

    /**
     * Add a Temporal filter to the current request.
     *
     * @return informations about if the filter has been append or not
     */
    FilterAppend setTimeFilter(TemporalOperator tFilter) throws DataStoreException;

    /**
     * Add a BBOX filter to the current request. ( this method is implemented
     * only if isBoundedObservation() return true)
     *
     * @return informations about if the filter has been append or not
     */
    FilterAppend setBoundingBox(BinarySpatialOperator boxFilter) throws DataStoreException;

    /**
     * Set the offering for the current request
     *
     * @param offering An offering identifier.
     *
     * @return informations about if the filter has been append or not
     */
    FilterAppend setOffering(final String offering) throws DataStoreException;

    /**
     * Add a filter on the result.
     *
     * @param filter a comparison filter the result.
     *
     * @return informations about if the filter has been append or not
     */
    FilterAppend setResultFilter(final BinaryComparisonOperator filter) throws DataStoreException;

    /**
     * Add a filter on the entity properties.
     *
     * @param filter a comparison filter the property.
     *
     * @return informations about if the filter has been append or not
     */
    FilterAppend setPropertiesFilter(final BinaryComparisonOperator filter) throws DataStoreException;

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

    void destroy();

    /**
     * Return a list of Observation matching the builded filter.
     *
     * @return A list of Observation matching the builded filter.
     */
    List<Observation> getObservations() throws DataStoreException;

    /**
     * Return a list of Sampling feature matching the builded filter.
     *
     * @return A list of Sampling feature matching the builded filter.
     */
    List<SamplingFeature> getFeatureOfInterests() throws DataStoreException;

    /**
     * Return a list of Phenomenon matching the builded filter.
     *
     * @return A list of Phenomenon matching the builded filter.
     */
    List<Phenomenon> getPhenomenons() throws DataStoreException;

    /**
     *  Return a list of Procedure matching the builded filter.
     *
     * @return A list of Procedure matching the builded filter.
     */
    List<Procedure> getProcesses() throws DataStoreException;

    /**
     *  Return a list of Offering matching the builded filter.
     *
     * @return A list of Offering matching the builded filter.
     */
    List<Offering> getOfferings() throws DataStoreException;

    /**
     * Return a Map of sensor locations matching the builded filter.
     *
     * @return A Map of sensor locations matching the builded filter.
     */
    Map<String, Geometry> getSensorLocations() throws DataStoreException;

    /**
     * Return a Map of sensor historical locations matching the builded filter.
     *
     * @return A Map of sensor locations matching the builded filter.
     */
    Map<String, Map<Date, Geometry>> getSensorHistoricalLocations() throws DataStoreException;

    /**
     * Return a Map of sensor times matching the builded filter.
     * This is a simpliest version of getSensorHistoricalLocations() without geometry extraction
     *
     * @return A Map of sensor times matching the builded filter.
     */
    Map<String, Set<Date>> getSensorHistoricalTimes() throws DataStoreException;

    /**
     * Return direct observations results.
     * Object type depends on the response Mode, response formats, etc/
     *
     * @return An encoded block of data in a string.
     */
    Object getResults() throws DataStoreException;

    /**
     * If the filter reader compute itself the bounding shape of the obervation collection.
     * this methode return the current shape.
     */
    Envelope getCollectionBoundingShape();
}

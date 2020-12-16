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
import org.geotoolkit.sos.netcdf.ExtractionResult;
import org.geotoolkit.sos.netcdf.ExtractionResult.ProcedureTree;
import org.opengis.metadata.Metadata;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.sampling.SamplingFeature;
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

    Metadata getMetadata() throws DataStoreException;

    public abstract Set<GenericName> getProcedureNames();

    public abstract List<ProcedureTree> getProcedures() throws DataStoreException;

    public abstract Set<String> getPhenomenonNames();

    public abstract TemporalGeometricPrimitive getTemporalBounds() throws DataStoreException;

    /**
     * Extract All the procedures / observations / features of interest /
     * phenomenon / spatial informations in this store.
     *
     * @return
     * @throws DataStoreException
     */
    public abstract ExtractionResult getResults() throws DataStoreException;

    /**
     * Extract All the procedures / observations / features of interest /
     * phenomenon / spatial informations in this store. Allow to filter by
     * specifying a list of accepted sensor identifiers.
     *
     * @param sensorIds a filter on sensor identifiers or {@code null}.
     */
    public abstract ExtractionResult getResults(final List<String> sensorIds) throws DataStoreException;

    /**
     * Extract All the procedures / observations / features of interest / phenoemenon / spatial informations in this store.
     * Allow to filter by specifying a list of accepted sensor identifiers.
     * If specified the results will be asigned to a new/existing sensor.
     *
     * @param affectedSensorID a assigned sensor identifier or {@code null}.
     * @param sensorIds a filter on sensor identifiers or {@code null}.
     */
    public abstract ExtractionResult getResults(final String affectedSensorID, final List<String> sensorIds) throws DataStoreException;

    /**
     * Extract All the procedures / observations / features of interest /
     * phenomenon / spatial informations in this store. Allow to filter by
     * specifying a list of accepted sensor identifiers. If specified the
     * results will be asigned to a new/existing sensor.
     *
     * A set of phenomenon / sampling features can be specified and will be used
     * if similar phenomenons/ sampling features are found in this store.
     *
     * @param affectedSensorID a assigned sensor identifier or {@code null}.
     * @param sensorIds a filter on sensor identifiers or {@code null}.
     * @param existingPhenomenons A set of existing phenomenons.
     * @param existingSamplingFeatures A set of existing sampling features.
     */
    public abstract ExtractionResult getResults(final String affectedSensorID, final List<String> sensorIds, final Set<Phenomenon> existingPhenomenons, final Set<SamplingFeature> existingSamplingFeatures) throws DataStoreException;

    /**
     * Return an Observation Reader on the data.
     *
     * @return An Observation Reader.
     */
    public abstract ObservationReader getReader();

    /**
     * Return an Observation Filter on the data.
     *
     * @return An Observation Filter.
     */
    public abstract ObservationFilterReader getFilter();

    /**
     * Return an Observation Writer on the data.
     *
     * @return An Observation Writer.
     */
    public abstract ObservationWriter getWriter();

    void close() throws DataStoreException;

}

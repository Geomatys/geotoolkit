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

import java.util.Arrays;
import org.geotoolkit.observation.model.ObservationDataset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import static org.geotoolkit.observation.AbstractObservationStoreFactory.*;
import static org.geotoolkit.observation.OMUtils.*;
import org.geotoolkit.observation.model.OMEntity;
import static org.geotoolkit.observation.model.OMEntity.PROCEDURE;
import static org.geotoolkit.observation.model.OMEntity.RESULT;
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
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.Process;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 * Basic implementation of Observation store.
 *
 *
 * Most of the methods of this class take a {@link AbstractObservationQuery} in input, but the query is ignored in this simple implementation with no filter capabilities.
 *
 * at least some procedure filter could be extracted => TODO.
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractObservationStore extends DataStore implements ObservationStore, Resource {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.observation");

    protected final ParameterValueGroup parameters;

    protected AbstractObservationStore(final ParameterValueGroup params) {
        this.parameters = params;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.ofNullable(parameters);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationFilterReader getFilter() throws DataStoreException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationWriter getWriter() throws DataStoreException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationReader getReader() throws DataStoreException {
        return null;
    }


    protected Map<String, Object> getBasicProperties() {
        final Map<String,Object> properties = new HashMap<>();
        extractParameter(parameters, PHENOMENON_ID_BASE, properties);
        extractParameter(parameters, OBSERVATION_ID_BASE, properties);
        extractParameter(parameters, OBSERVATION_TEMPLATE_ID_BASE, properties);
        extractParameter(parameters, SENSOR_ID_BASE, properties);
        return properties;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<String> getEntityNames(AbstractObservationQuery query) throws DataStoreException {
        if (query == null) throw new DataStoreException("Query must no be null");

        final ObservationDataset fullDs = getDataset(new DatasetQuery());
        return switch (query.getEntityType()) {
            case OBSERVED_PROPERTY   -> fullDs.phenomenons.stream().map(ph -> ph.getId()).collect(Collectors.toSet());
             // by default location ids are equals to procedure ids.
            case LOCATION, PROCEDURE -> fullDs.procedures.stream().map(pr -> pr.getId()).collect(Collectors.toSet());
            case FEATURE_OF_INTEREST -> fullDs.featureOfInterest.stream().map(foi -> foi.getId()).collect(Collectors.toSet());
            case OFFERING            -> fullDs.offerings.stream().map(off -> off.getId()).collect(Collectors.toSet());
            case OBSERVATION         -> fullDs.observations.stream().map(obs -> obs.getId()).collect(Collectors.toSet());
             // HISTORICAL_LOCATION could be computed => TODO
            case HISTORICAL_LOCATION, RESULT -> throw new DataStoreException("entity name listing not implemented yet for entity: " + query.getEntityType());
        };
    }

    /**
     * {@inheritDoc }
     *
     * Implementations notes: only supported for procedure for now
     */
    @Override
    public TemporalGeometricPrimitive getEntityTemporalBounds(IdentifierQuery query) throws DataStoreException {
        if (query == null) throw new DataStoreException("Query must no be null");

        if (query.getEntityType() != OMEntity.PROCEDURE) throw new DataStoreException("temporal bound not implemented yet for entity: " + query.getEntityType());

        final ObservationDataset fullDs = getDataset(new DatasetQuery(Arrays.asList(query.getIdentifier())));
        if (fullDs.procedures.size() == 1) {
            return fullDs.procedures.get(0).spatialBound.getTimeObject();
        } else if (fullDs.procedures.size() > 1) {
            throw new DataStoreException("Multiple procedure find for an identifier");
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean existEntity(IdentifierQuery query) throws DataStoreException {
        return getEntityNames(query).contains(query.getIdentifier());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long getCount(AbstractObservationQuery query) throws DataStoreException {
        return getEntityNames(query).size();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ProcedureDataset> getProcedureDatasets(DatasetQuery query) throws DataStoreException {
        final ObservationDataset fullDs = getDataset(query);
        return fullDs.procedures;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalGeometricPrimitive getTemporalBounds() throws DataStoreException {
        final ObservationDataset fullDs = getDataset(new DatasetQuery());
        return fullDs.spatialBound.getTimeObject();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, Map<Date, Geometry>> getHistoricalSensorLocations(HistoricalLocationQuery query) throws DataStoreException {
        final Map<String, Map<Date, Geometry>> results = new HashMap<>();
        List<ProcedureDataset> procedures = getProcedureDatasets(new DatasetQuery());
        for (ProcedureDataset proc : procedures) {
            results.put(proc.getId(), proc.spatialBound.getHistoricalLocations());
        }
        return results;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, Set<Date>> getHistoricalSensorTimes(HistoricalLocationQuery query) throws DataStoreException {
       final Map<String, Set<Date>> results = new HashMap<>();
        List<ProcedureDataset> procedures = getProcedureDatasets(new DatasetQuery());
        for (ProcedureDataset proc : procedures) {
            results.put(proc.getId(), proc.spatialBound.getHistoricalLocations().keySet());
        }
        return results;
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, Geometry> getSensorLocations(LocationQuery query) throws DataStoreException {
        final Map<String, Geometry> results = new HashMap<>();
        List<ProcedureDataset> procedures = getProcedureDatasets(new DatasetQuery());
        for (ProcedureDataset proc : procedures) {
            results.put(proc.getId(), proc.spatialBound.getLastGeometry());
        }
        return results;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Phenomenon> getPhenomenons(ObservedPropertyQuery query) throws DataStoreException {
        final ObservationDataset fullDs = getDataset(new DatasetQuery());
        return fullDs.phenomenons.stream().map(phen -> (Phenomenon) phen).toList();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<SamplingFeature> getFeatureOfInterest(SamplingFeatureQuery query) throws DataStoreException {
        final ObservationDataset fullDs = getDataset(new DatasetQuery());
        return fullDs.featureOfInterest.stream().map(foi -> (SamplingFeature) foi).toList();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Observation> getObservations(ObservationQuery query) throws DataStoreException {
        final ObservationDataset fullDs = getDataset(new DatasetQuery());
        return fullDs.observations.stream().map(obs -> (Observation) obs).toList();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Process> getProcedures(ProcedureQuery query) throws DataStoreException {
        final ObservationDataset fullDs = getDataset(new DatasetQuery());
        return fullDs.procedures.stream().map(proc -> (Process) proc).toList();
    }

    @Override
    public List<Offering> getOfferings(OfferingQuery query) throws DataStoreException {
        final ObservationDataset fullDs = getDataset(new DatasetQuery());
        return fullDs.offerings;
    }

    @Override
    public Object getResults(ResultQuery query) throws DataStoreException {
        // TODO implements
        throw new UnsupportedOperationException("not implemented yet.");
    }

    @Override
    public Observation getTemplate(String sensorId) throws DataStoreException {
        // TODO implements
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws DataStoreException {
        // do nothing
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        String identifier = getStoreIdentifier() != null ? getStoreIdentifier() : "unknown";
        return buildMetadata(identifier);
    }

    protected abstract String getStoreIdentifier();

}

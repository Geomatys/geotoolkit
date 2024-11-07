/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Query;
import static org.geotoolkit.observation.AbstractObservationStore.LOGGER;
import static org.geotoolkit.observation.OMUtils.geometryMatchEnvelope;
import org.geotoolkit.observation.model.OMEntity;
import static org.geotoolkit.observation.model.OMEntity.FEATURE_OF_INTEREST;
import static org.geotoolkit.observation.model.OMEntity.HISTORICAL_LOCATION;
import static org.geotoolkit.observation.model.OMEntity.LOCATION;
import static org.geotoolkit.observation.model.OMEntity.OBSERVATION;
import static org.geotoolkit.observation.model.OMEntity.OBSERVED_PROPERTY;
import static org.geotoolkit.observation.model.OMEntity.OFFERING;
import static org.geotoolkit.observation.model.OMEntity.PROCEDURE;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.model.Offering;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.Procedure;
import org.geotoolkit.observation.model.ProcedureDataset;
import org.geotoolkit.observation.model.SamplingFeature;
import org.geotoolkit.observation.query.AbstractObservationQuery;
import org.geotoolkit.observation.query.DatasetQuery;
import org.geotoolkit.observation.query.HistoricalLocationQuery;
import org.geotoolkit.observation.query.IdentifierQuery;
import org.geotoolkit.observation.query.LocationQuery;
import org.geotoolkit.observation.query.ObservationQuery;
import static org.geotoolkit.observation.query.ObservationQueryUtilities.*;
import org.geotoolkit.observation.query.ObservedPropertyQuery;
import org.geotoolkit.observation.query.OfferingQuery;
import org.geotoolkit.observation.query.ProcedureQuery;
import org.geotoolkit.observation.query.ResultQuery;
import org.geotoolkit.observation.query.SamplingFeatureQuery;
import org.locationtech.jts.geom.Geometry;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.Filter;
import org.opengis.filter.Literal;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.ResourceId;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.TemporalOperator;
import org.opengis.filter.ValueReference;
import org.opengis.geometry.Envelope;
import org.opengis.observation.Process;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.util.CodeList;

/**
 * Abstract parent class for observation store providing observation filterReader and/or reader.
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractFilteredObservationStore extends AbstractObservationStore {

    public AbstractFilteredObservationStore(ParameterValueGroup params) {
        super(params);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<String> getEntityNames(AbstractObservationQuery query) throws DataStoreException {
        return handleQuery(query).getIdentifiers();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean existEntity(IdentifierQuery query) throws DataStoreException {
        ObservationReader reader = getReader();
        return reader.existEntity(query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long getCount(AbstractObservationQuery query) throws DataStoreException {
        return handleQuery(query).getCount();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationDataset getDataset(DatasetQuery query) throws DataStoreException {

        if (query.getAffectedSensorID() != null) {
            LOGGER.warning("This ObservationStore does not allow to override sensor ID");
        }

        ObservationFilterReader currentFilter = (ObservationFilterReader) getFilter();
        final List<String> sensorIDs = query.getSensorIds();
        ObservationQuery observationQuery = new ObservationQuery(query.getResultModel(), query.getResponseMode(), query.getResponseFormat());
        observationQuery.setIncludeTimeForProfile(query.isIncludeTimeForProfile());
        observationQuery.setSeparatedProfileObservation(query.isSeparatedProfileObservation());
        currentFilter.init(observationQuery);
        currentFilter.setProcedure(sensorIDs);
        List<Observation> observations = currentFilter.getObservations().stream().map(obs -> (Observation)obs).toList();

        final ObservationDataset result = new ObservationDataset();
        result.spatialBound.initBoundary();

        for (Observation obs : observations) {
            final Procedure proc =  obs.getProcedure();
            String type         = obs.getProperties().getOrDefault("type", "timeseries").toString();
            String sensorType   = obs.getProperties().getOrDefault("sensorType", "Component").toString();

            if (sensorIDs.isEmpty() || sensorIDs.contains(proc.getId())) {
                final Phenomenon phen = obs.getObservedProperty();
                if (!result.phenomenons.contains(phen)) {
                    result.phenomenons.add(phen);
                }
                List<String> fields = OMUtils.getPhenomenonsFieldIdentifiers(phen);
                final ProcedureDataset procedure = new ProcedureDataset(proc.getId(), proc.getName(), proc.getDescription(), sensorType, type, fields, null);
                if (!result.procedures.contains(procedure)) {
                    result.procedures.add(procedure);
                }
                SamplingFeature foi = obs.getFeatureOfInterest();
                if (foi != null && !result.featureOfInterest.contains(foi)) {
                    result.featureOfInterest.add(foi);
                }
                result.spatialBound.appendLocation(obs.getSamplingTime(), foi);
                procedure.spatialBound.appendLocation(obs.getSamplingTime(), foi);

                // get historical locations for sensor
                HistoricalLocationQuery hquery = (HistoricalLocationQuery) buildQueryForSensor(OMEntity.HISTORICAL_LOCATION, proc.getId());
                Map<Date, Geometry> sensorLocations = getHistoricalSensorLocations(hquery).getOrDefault(proc.getId(), Collections.EMPTY_MAP);
                procedure.spatialBound.getHistoricalLocations().putAll(sensorLocations);

                result.observations.add(obs);
            }
        }

        // fill also offerings
        currentFilter = (ObservationFilterReader) getFilter();
        currentFilter.init(new OfferingQuery());
        currentFilter.setProcedure(sensorIDs);
        result.offerings.addAll(currentFilter.getOfferings());

        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ProcedureDataset> getProcedureDatasets(DatasetQuery query) throws DataStoreException {
        final List<ProcedureDataset> results = new ArrayList<>();

        final ObservationFilterReader procFilter = getFilter();
        procFilter.init(new ProcedureQuery());
        procFilter.setProcedure(query.getSensorIds());

        // TODO apply filter
        for (org.opengis.observation.Process p : procFilter.getProcesses()) {

            final Procedure proc  =  (Procedure) p;
            final String omType = (String) proc.getProperties().getOrDefault("type", "timeseries");
            final ProcedureDataset procedure = new ProcedureDataset(proc.getId(), proc.getName(), proc.getDescription(), "Component", omType, new ArrayList<>(), null);

            Observation template = (Observation) getReader().getTemplateForProcedure(proc.getId());

            // complete fields and location
            if (template != null) {
                final Phenomenon phenProp = template.getObservedProperty();
                if (phenProp != null) {
                    final List<String> fields = OMUtils.getPhenomenonsFieldIdentifiers(phenProp);
                    for (String field : fields) {
                        if (!procedure.fields.contains(field)) {
                            procedure.fields.add(field);
                        }
                    }
                }
                SamplingFeature foim = template.getFeatureOfInterest();
                procedure.spatialBound.appendLocation(template.getSamplingTime(), foim);
            }

                // get historical locations for sensor
            HistoricalLocationQuery hquery = (HistoricalLocationQuery) buildQueryForSensor(OMEntity.HISTORICAL_LOCATION, proc.getId());
            Map<Date, Geometry> sensorLocations = getHistoricalSensorLocations(hquery).getOrDefault(proc.getId(), Collections.EMPTY_MAP);
            procedure.spatialBound.getHistoricalLocations().putAll(sensorLocations);
            results.add(procedure);
        }
        return results;
    }

    /**
     * {@inheritDoc }
     *
     * Implementations notes: only supported for procedure and feature of interest for now
     */
    @Override
    public TemporalGeometricPrimitive getEntityTemporalBounds(IdentifierQuery query) throws DataStoreException {
        if (query == null) throw new DataStoreException("Query must no be null");
        switch (query.getEntityType()) {
            case FEATURE_OF_INTEREST: return getReader().getFeatureOfInterestTime(query.getIdentifier());
            case PROCEDURE:           return getReader().getProcedureTime(query.getIdentifier());
            default:                  throw new DataStoreException("temporal bound not implemented yet for entity: " + query.getEntityType());
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, Map<Date, Geometry>> getHistoricalSensorLocations(HistoricalLocationQuery query) throws DataStoreException {
        return handleQuery(query).getSensorHistoricalLocations();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, Set<Date>> getHistoricalSensorTimes(HistoricalLocationQuery query) throws DataStoreException {
        return handleQuery(query).getSensorHistoricalTimes();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<org.opengis.observation.sampling.SamplingFeature> getFeatureOfInterest(SamplingFeatureQuery query) throws DataStoreException {
        return handleQuery(query).getFeatureOfInterests();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, Geometry> getSensorLocations(LocationQuery query) throws DataStoreException {
        return handleQuery(query).getSensorLocations();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<org.opengis.observation.Observation> getObservations(ObservationQuery query) throws DataStoreException {
       return handleQuery(query).getObservations();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public org.opengis.observation.Observation getTemplate(String sensorId) throws DataStoreException {
        return getReader().getTemplateForProcedure(sensorId);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<org.opengis.observation.Phenomenon> getPhenomenons(ObservedPropertyQuery query) throws DataStoreException {
        return handleQuery(query).getPhenomenons();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Process> getProcedures(ProcedureQuery query) throws DataStoreException {
        return handleQuery(query).getProcesses();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Offering> getOfferings(OfferingQuery query) throws DataStoreException {
        return handleQuery(query).getOfferings();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getResults(ResultQuery query) throws DataStoreException {
        return handleQuery(query).getResults();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalGeometricPrimitive getTemporalBounds() throws DataStoreException {
        ObservationReader reader = getReader();
        return reader.getEventTime();
    }

    /**
     * Handle an observation query, and return an Observation filter reader with
     * all filter set.
     *
     * @param q An observation query.
     *
     * @return An Observation filter reader with all filter set.
     */
    protected ObservationFilterReader handleQuery(Query q) throws DataStoreException {
        if (q == null) throw new DataStoreException("Query must no be null");

        final ObservationFilterReader localOmFilter = getFilter();
        List<String> observedProperties = new ArrayList<>();
        List<String> procedures         = new ArrayList<>();
        List<String> fois               = new ArrayList<>();
        List<String> offerings          = new ArrayList<>();

        if (q instanceof AbstractObservationQuery query) {
            localOmFilter.init(query);
            handleFilter(query.getEntityType(), query.getSelection(), localOmFilter, observedProperties, procedures, fois, offerings);

        } else {
            throw new DataStoreException("Only ObservationQuery are supported for now");
        }

        // TODO Spatial BBOX
        localOmFilter.setObservedProperties(observedProperties);
        localOmFilter.setProcedure(procedures);
        localOmFilter.setFeatureOfInterest(fois);
        localOmFilter.setOfferings(offerings);
        return localOmFilter;
    }

    /**
     * Handle a filter, extract all observed properties / procedures / feature
     * of interest / offering filters. It also sets some filter on the
     * ObservationFilterReader.
     *
     * In long term this method should be removed, and its code should move into
     * the observation filter.
     *
     * @param mode Entity type of the query.
     * @param filter The query filter to apply.
     * @param localOmFilter An observation filter.
     * @param observedProperties A list of observed property ids to populate.
     * @param procedures A list of procedure ids to populate.
     * @param fois A list of feature of interest ids to populate.
     * @param offerings A list of offering ids to populate.
     */
    protected void handleFilter(OMEntity mode, Filter filter, final ObservationFilterReader localOmFilter, List<String> observedProperties, List<String> procedures, List<String> fois, List<String> offerings) throws DataStoreException {
        if (Filter.include().equals(filter) || filter == null) {
            return;
        }

        // actually there is no OR or AND filter properly supported
        CodeList type = filter.getOperatorType();
        if (type == LogicalOperatorName.AND) {
            for (Filter f : ((LogicalOperator<?>) filter).getOperands()) {
                handleFilter(mode, f, localOmFilter, observedProperties, procedures, fois, offerings);
            }

        } else if (type == LogicalOperatorName.OR) {
            for (Filter f : ((LogicalOperator<?>) filter).getOperands()) {
                handleFilter(mode, f, localOmFilter, observedProperties, procedures, fois, offerings);
            }

            // Temoral filter
        } else if (filter instanceof TemporalOperator tFilter) {

            localOmFilter.setTimeFilter(tFilter);

        } else if (filter instanceof ResourceId idf) {
            List<String> ids = new ArrayList<>();
            ids.add(idf.getIdentifier());

            switch (mode) {
                case FEATURE_OF_INTEREST           -> localOmFilter.setFeatureOfInterest(ids);
                case OBSERVATION                   -> localOmFilter.setObservationIds(ids);
                case OBSERVED_PROPERTY             -> localOmFilter.setObservedProperties(ids);
                case PROCEDURE                     -> localOmFilter.setProcedure(ids);
                case OFFERING                      -> localOmFilter.setOfferings(ids);
                case LOCATION, HISTORICAL_LOCATION -> localOmFilter.setProcedure(ids);
                default                            -> {}
            }

        } else if (type == SpatialOperatorName.BBOX && filter instanceof BinarySpatialOperator bbox) {
            handleBBOXFilter(mode, localOmFilter, fois, procedures, bbox);

        } else if (type == ComparisonOperatorName.PROPERTY_IS_EQUAL_TO) {
            final BinaryComparisonOperator ef = (BinaryComparisonOperator) filter;
            final ValueReference name    = (ValueReference) ef.getOperand1();
            final String pNameStr      = name.getXPath();
            final Literal value        = (Literal) ef.getOperand2();
            if (pNameStr.equals("observedProperty")) {
                observedProperties.add((String) value.getValue());
            } else if (pNameStr.equals("procedure")) {
                procedures.add((String) value.getValue());
            } else if (pNameStr.equals("featureOfInterest")) {
                fois.add((String) value.getValue());
            } else if (pNameStr.equals("observationId")) {
                localOmFilter.setObservationIds(Arrays.asList((String) value.getValue()));
            } else if (pNameStr.equals("offering")) {
                offerings.add((String) value.getValue());
            } else if (pNameStr.equals("sensorType")) {
                localOmFilter.setProcedureType((String) value.getValue());
            } else if (pNameStr.contains("properties/")) {
                localOmFilter.setPropertiesFilter((BinaryComparisonOperator) filter);
            }  else if (pNameStr.startsWith("result")) {
                localOmFilter.setResultFilter((BinaryComparisonOperator) filter);
            } else {
                throw new DataStoreException("Unsuported property for filtering:" + pNameStr);
            }
        } else if (filter instanceof BinaryComparisonOperator binC) {
            final ValueReference name    = (ValueReference) binC.getOperand1();
            final String pNameStr      = name.getXPath();

            if (pNameStr.contains("properties/")) {
                localOmFilter.setPropertiesFilter((BinaryComparisonOperator) filter);
            } else if (pNameStr.startsWith("result")) {
                localOmFilter.setResultFilter((BinaryComparisonOperator) filter);
            } else {
                throw new DataStoreException("Unsuported property for filtering:" + pNameStr);
            }
        } else {
            throw new DataStoreException("Unknown filter operation.\nAnother possibility is that the content of your time filter is empty or unrecognized.");
        }
    }

    /**
     * Handle BBOX filter in a query.
     *
     * Currently, if the entity type is LOCATION or HISTORICAL_LOCATION, the bbox will be set on the
     * observation filter. Otherwise, this method will perform a
     * featureOfInterest query to determine the matching fois, and the foi ids
     * list ill be set on the observation filter.
     *
     * This method is separated from handleFilter to allow sub-classes to
     * override its behavior.
     *
     * @param mode Entity type of the query.
     * @param localOmFilter An observation filter.
     * @param fois A list of feature of interest ids to populate.
     * @param procedures A list of procedure ids to populate.
     * @param bbox The spatial filter to apply.
     */
    protected void handleBBOXFilter(OMEntity mode, final ObservationFilterReader localOmFilter, List<String> fois, List<String> procedures, BinarySpatialOperator bbox) throws DataStoreException {
        switch (mode) {
            case LOCATION, HISTORICAL_LOCATION ->  localOmFilter.setBoundingBox(bbox);
            default       -> {
                if (getCapabilities().isBoundedObservation) {
                    localOmFilter.setBoundingBox(bbox);
                } else {
                    Collection<String> allfoi = getFeaturesOfInterestForBBOX(bbox);
                    if (!allfoi.isEmpty()) {
                        fois.addAll(allfoi);
                    } else {
                       fois.add("unexisting-foi");
                    }
                }
            }
        }
    }

    /**
     * Peform a Feature of intrest query on the specified bbox.
     *
     * @param box A bbox filter.
     *
     * @return A list of feature of interest ids.
     */
    protected List<String> getFeaturesOfInterestForBBOX(final BinarySpatialOperator box) throws DataStoreException {
        final Envelope env = OMUtils.getEnvelopeFromBBOXFilter(box);
        List<String> results = new ArrayList<>();
        SamplingFeatureQuery query = new SamplingFeatureQuery();
        final List<org.opengis.observation.sampling.SamplingFeature> stations = getFeatureOfInterest(query);
        for (org.opengis.observation.sampling.SamplingFeature offStation : stations) {
            // TODO for SOS 2.0 use observed area
            final org.geotoolkit.observation.model.SamplingFeature station = (org.geotoolkit.observation.model.SamplingFeature) offStation;

            // should not happen
            if (station == null) {
                throw new DataStoreException("the feature of interest is in offering list but not registered");
            }
            Geometry geom = station.getGeometry();
            if (geom != null) {
                if (geometryMatchEnvelope(geom, env)) {
                    results.add(station.getId());
                } else {
                    LOGGER.log(Level.FINER, " the feature of interest {0} is not in the BBOX", station.getId());
                }

            } else {
                LOGGER.log(Level.WARNING, "unknown implementation:{0}", station.getClass().getName());
            }
        }
        return results;
    }
}

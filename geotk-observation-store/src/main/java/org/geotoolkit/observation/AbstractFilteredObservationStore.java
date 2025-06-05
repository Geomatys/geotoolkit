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
import org.geotoolkit.observation.model.Field;
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
import org.opengis.filter.ComparisonOperator;
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
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.TemporalPrimitive;
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
        addProcedureFilters(sensorIDs, currentFilter);
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
                List<Field> fields = OMUtils.getPhenomenonsFields(phen);
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
        addProcedureFilters(sensorIDs, currentFilter);
        result.offerings.addAll(currentFilter.getOfferings());

        return result;
    }

    private void addProcedureFilters(List<String> sensorIDs, ObservationFilterReader currentFilter) throws DataStoreException {
        if (!sensorIDs.isEmpty()) {
            FilterAppend result = createNewFilterAppend();
            currentFilter.startFilterBlock();
            for (int i = 0; i < sensorIDs.size(); i++) {
                String sensorID = sensorIDs.get(i);

                // if not first we append the logical operator
                if (i > 0) currentFilter.appendFilterOperator(LogicalOperatorName.OR, result);
                FilterAppend fa = currentFilter.setProcedure(sensorID);

                // we may have to remove it
                if (i > 0) currentFilter.removeFilterOperator(LogicalOperatorName.OR, result, fa);
                result = result.merge(fa);
            }
            currentFilter.endFilterBlock(LogicalOperatorName.OR, result);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ProcedureDataset> getProcedureDatasets(DatasetQuery query) throws DataStoreException {
        final List<ProcedureDataset> results = new ArrayList<>();

        final ObservationFilterReader procFilter = getFilter();
        procFilter.init(new ProcedureQuery());
        addProcedureFilters(query.getSensorIds(), procFilter);

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
                    final List<Field> fields = OMUtils.getPhenomenonsFields(phenProp);
                    for (Field field : fields) {
                        // is this needed?
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
    public TemporalPrimitive getEntityTemporalBounds(IdentifierQuery query) throws DataStoreException {
        if (query == null) throw new DataStoreException("Query must no be null");
        return switch (query.getEntityType()) {
            case FEATURE_OF_INTEREST -> getReader().getFeatureOfInterestTime(query.getIdentifier());
            case PROCEDURE           -> getReader().getProcedureTime(query.getIdentifier());
            default                  -> throw new DataStoreException("temporal bound not implemented yet for entity: " + query.getEntityType());
        };
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
    public List<SamplingFeature> getFeatureOfInterest(SamplingFeatureQuery query) throws DataStoreException {
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
    public List<Observation> getObservations(ObservationQuery query) throws DataStoreException {
       return handleQuery(query).getObservations();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Observation getTemplate(String sensorId) throws DataStoreException {
        return getReader().getTemplateForProcedure(sensorId);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Phenomenon> getPhenomenons(ObservedPropertyQuery query) throws DataStoreException {
        return handleQuery(query).getPhenomenons();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Procedure> getProcedures(ProcedureQuery query) throws DataStoreException {
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
    public TemporalPrimitive getTemporalBounds() throws DataStoreException {
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

        if (q instanceof AbstractObservationQuery query) {
            localOmFilter.init(query);
            handleFilter(query.getEntityType(), query.getSelection(), localOmFilter);

        } else {
            throw new DataStoreException("Only ObservationQuery are supported for now");
        }

        // TODO Spatial BBOX
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
     * @param entityType Entity type of the query.
     * @param filter The query filter to apply.
     * @param localOmFilter An observation filter.
     *
     * @return informations about if the filter has been append or not.
     */
    protected FilterAppend handleFilter(OMEntity entityType, Filter filter, final ObservationFilterReader localOmFilter) throws DataStoreException {
        if (Filter.include().equals(filter) || filter == null) {
            return createNewFilterAppend();
        }

        CodeList type = filter.getOperatorType();

        // Logical filter
        if (filter instanceof LogicalOperator<?> logFilter) {
            return handleLogicalFilter(entityType, localOmFilter, logFilter);

            // Temporal filter
        } else if (filter instanceof TemporalOperator tFilter) {
            return localOmFilter.setTimeFilter(tFilter);

        } else if (filter instanceof ResourceId idf) {
            String id = idf.getIdentifier();

            return switch (entityType) {

                case FEATURE_OF_INTEREST           -> localOmFilter.setFeatureOfInterest(id);
                case OBSERVATION                   -> localOmFilter.setObservationId(id);
                case OBSERVED_PROPERTY             -> localOmFilter.setObservedProperty(id);
                case PROCEDURE                     -> localOmFilter.setProcedure(id);
                case OFFERING                      -> localOmFilter.setOffering(id);
                case LOCATION, HISTORICAL_LOCATION -> localOmFilter.setProcedure(id);
                default                            ->  throw new DataStoreException("Unknown entity type for resource id filter.");
            };

        } else if (type == SpatialOperatorName.BBOX && filter instanceof BinarySpatialOperator bbox) {
            return handleBBOXFilter(entityType, localOmFilter, bbox);

        } else if (type == ComparisonOperatorName.PROPERTY_IS_EQUAL_TO) {
            final BinaryComparisonOperator ef = (BinaryComparisonOperator) filter;
            final ValueReference name    = (ValueReference) ef.getOperand1();
            final String pNameStr      = name.getXPath();
            final Literal value        = (Literal) ef.getOperand2();
            if (pNameStr.equals("observedProperty")) {
                return localOmFilter.setObservedProperty((String) value.getValue());
            } else if (pNameStr.equals("procedure")) {
                return localOmFilter.setProcedure((String) value.getValue());
            } else if (pNameStr.equals("featureOfInterest")) {
                return localOmFilter.setFeatureOfInterest((String) value.getValue());
            } else if (pNameStr.equals("observationId")) {
                return localOmFilter.setObservationId((String) value.getValue());
            } else if (pNameStr.equals("offering")) {
                return localOmFilter.setOffering((String) value.getValue());
            } else if (pNameStr.equals("sensorType")) {
                return localOmFilter.setProcedureType((String) value.getValue());
            } else if (pNameStr.contains("properties/")) {
                return localOmFilter.setPropertiesFilter((BinaryComparisonOperator) filter);
            }  else if (pNameStr.startsWith("result")) {
                return localOmFilter.setResultFilter((BinaryComparisonOperator) filter);
            } else {
                throw new DataStoreException("Unsuported property for filtering:" + pNameStr);
            }
        } else if (filter instanceof ComparisonOperator binC) {
            if (binC.getExpressions().size() != 2) throw new DataStoreException("Comparison filter must have 2 expression (property name and literal)");
            final ValueReference name    = (ValueReference) binC.getExpressions().get(0);
            final String pNameStr      = name.getXPath();

            if (pNameStr.contains("properties/")) {
                return localOmFilter.setPropertiesFilter(binC);
            } else if (pNameStr.startsWith("result")) {
                return localOmFilter.setResultFilter(binC);
            } else {
                throw new DataStoreException("Unsuported property for filtering:" + pNameStr);
            }
        } else {
            throw new DataStoreException("Unknown filter operation.\nAnother possibility is that the content of your time filter is empty or unrecognized.");
        }
    }

    protected FilterAppend handleLogicalFilter(final OMEntity entityType, final ObservationFilterReader localOmFilter, LogicalOperator<?> logFilter) throws DataStoreException {
        FilterAppend result = createNewFilterAppend();
        LogicalOperatorName type = logFilter.getOperatorType();
        localOmFilter.startFilterBlock(type);
        int nbFilter = logFilter.getOperands().size();
        for (int i = 0; i < nbFilter; i++) {
            Filter f = logFilter.getOperands().get(i);

            // if not first we append the logical operator
            if (i > 0) localOmFilter.appendFilterOperator(type, result);

            FilterAppend fa = handleFilter(entityType, f, localOmFilter);

            // we may have to remove it
            if (i > 0) localOmFilter.removeFilterOperator(type, result, fa);

            result = result.merge(fa);
        }
        localOmFilter.endFilterBlock(type, result);
        return result;
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
     * @param entityType Entity type of the query.
     * @param localOmFilter An observation filter.
     * @param bbox The spatial filter to apply.
     *
     * @return informations about if the filter has been append or not.
     */
    protected FilterAppend handleBBOXFilter(OMEntity entityType, final ObservationFilterReader localOmFilter, BinarySpatialOperator bbox) throws DataStoreException {
        switch (entityType) {

            case LOCATION, HISTORICAL_LOCATION ->  {
                return localOmFilter.setBoundingBox(bbox);
            }
            default       -> {
                if (getCapabilities().isBoundedObservation) {
                    return localOmFilter.setBoundingBox(bbox);
                } else {
                    List<String> fois = getFeaturesOfInterestForBBOX(bbox);
                    if (!fois.isEmpty()) {
                        if (fois.size() == 1) {
                            return localOmFilter.setFeatureOfInterest(fois.get(0));
                        } else {
                            FilterAppend result = createNewFilterAppend();
                            localOmFilter.startFilterBlock();
                            for (int i = 0; i < fois.size(); i++) {
                                // if not first we append the logical operator
                                if (i > 0) localOmFilter.appendFilterOperator(LogicalOperatorName.OR, result);

                                FilterAppend fa = localOmFilter.setFeatureOfInterest(fois.get(i));

                                // we may have to remove it
                                if (i > 0) localOmFilter.removeFilterOperator(LogicalOperatorName.OR, result, fa);

                                result = result.merge(fa);
                            }
                            localOmFilter.endFilterBlock(LogicalOperatorName.OR, result);
                            return result;
                        }
                    } else {
                        // invalid the results
                        return localOmFilter.setFeatureOfInterest("unexisting-foi");
                    }
                }
            }
        }
    }

    /**
     * Perform a Feature of intrest query on the specified bbox.
     *
     * @param box A bbox filter.
     *
     * @return A list of feature of interest ids.
     */
    protected List<String> getFeaturesOfInterestForBBOX(final BinarySpatialOperator box) throws DataStoreException {
        final Envelope env = OMUtils.getEnvelopeFromBBOXFilter(box);
        List<String> results = new ArrayList<>();
        SamplingFeatureQuery query = new SamplingFeatureQuery();
        final List<SamplingFeature> stations = getFeatureOfInterest(query);
        for (SamplingFeature station : stations) {
            // TODO for SOS 2.0 use observed area

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

    /**
     * Create a new FilterAppend. This simple method allow sub-implementation to
     * create specific objects.
     *
     * @return A filterAppend.
     */
    protected FilterAppend createNewFilterAppend() {
        return new FilterAppend();
    }
}

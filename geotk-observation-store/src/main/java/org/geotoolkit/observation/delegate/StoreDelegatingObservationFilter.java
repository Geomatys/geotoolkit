/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.observation.delegate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.observation.FilterAppend;
import org.geotoolkit.observation.ObservationFilterReader;
import org.geotoolkit.observation.ObservationResult;
import org.geotoolkit.observation.ObservationStore;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.model.Offering;
import org.geotoolkit.observation.model.ProcedureDataset;
import org.geotoolkit.observation.query.AbstractObservationQuery;
import org.geotoolkit.observation.query.DatasetQuery;
import org.locationtech.jts.geom.Geometry;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.TemporalOperator;
import org.opengis.geometry.Envelope;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.Procedure;
import org.geotoolkit.observation.model.SamplingFeature;
import org.opengis.filter.LogicalOperatorName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class StoreDelegatingObservationFilter implements ObservationFilterReader {

    private final ObservationStore store;

    protected AbstractObservationQuery query = null;

    public StoreDelegatingObservationFilter(ObservationStore store) {
        this.store = store;
    }

    @Override
    public void init(AbstractObservationQuery query) throws DataStoreException {
         this.query = query;
    }

    @Override
    public FilterAppend setProcedure(String procedure) throws DataStoreException {
        if (procedure != null) throw new UnsupportedOperationException("Procedure filtering is not supported yet.");
        return new FilterAppend();
    }

    @Override
    public FilterAppend setProcedureType(String type) throws DataStoreException {
        if (type != null) throw new UnsupportedOperationException("Procedure type filtering is not supported yet.");
        return new FilterAppend();
    }

    @Override
    public FilterAppend setObservedProperty(String phenomenon) {
        if (phenomenon != null) throw new UnsupportedOperationException("Observed properties filtering is not supported yet.");
        return new FilterAppend();
    }

    @Override
    public FilterAppend setFeatureOfInterest(String foi) {
        if (foi != null) throw new UnsupportedOperationException("Feature of interest filtering is not supported yet.");
        return new FilterAppend();
    }

    @Override
    public FilterAppend setObservationId(String id) {
        if (id != null) throw new UnsupportedOperationException("Observed id filtering is not supported yet.");
        return new FilterAppend();
    }

    @Override
    public FilterAppend setTimeFilter(TemporalOperator tFilter) throws DataStoreException {
        if (tFilter != null) throw new UnsupportedOperationException("Time filtering is not supported yet.");
        return new FilterAppend();
    }

    @Override
    public FilterAppend setBoundingBox(BinarySpatialOperator e) throws DataStoreException {
        if (e != null) throw new UnsupportedOperationException("BBOX filtering is not supported yet.");
        return new FilterAppend();
    }

    @Override
    public FilterAppend setOffering(String offering) throws DataStoreException {
        if (offering != null) throw new UnsupportedOperationException("Offering filtering is not supported yet.");
        return new FilterAppend();
    }

    @Override
    public FilterAppend setResultFilter(BinaryComparisonOperator filter) throws DataStoreException {
        if (filter != null) throw new UnsupportedOperationException("Result filtering is not supported yet.");
        return new FilterAppend();
    }

    @Override
    public FilterAppend setPropertiesFilter(BinaryComparisonOperator filter) throws DataStoreException {
        if (filter != null) throw new UnsupportedOperationException("Properties filtering is not supported yet.");
        return new FilterAppend();
    }

    @Override
    public List<ObservationResult> filterResult() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> getIdentifiers() throws DataStoreException {
        return store.getEntityNames(query);
    }

    @Override
    public long getCount() throws DataStoreException {
        return store.getEntityNames(query).size();
    }

    @Override
    public void refresh() throws DataStoreException {
        //do nothing
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public List<Observation> getObservations() throws DataStoreException {
        //TODO add filters
        ObservationDataset dataset = store.getDataset(new DatasetQuery());
        return new ArrayList<>(dataset.observations);
    }

    @Override
    public List<SamplingFeature> getFeatureOfInterests() throws DataStoreException {
        //TODO add filters
        ObservationDataset dataset = store.getDataset(new DatasetQuery());
        return new ArrayList<>(dataset.featureOfInterest);
    }

    @Override
    public List<Phenomenon> getPhenomenons() throws DataStoreException {
        //TODO add filters
        ObservationDataset dataset = store.getDataset(new DatasetQuery());
        return new ArrayList<>(dataset.phenomenons);
    }

    @Override
    public List<Procedure> getProcesses() throws DataStoreException {
        List<Procedure> results = new ArrayList<>();
        //TODO add filters
        List<ProcedureDataset> procedures = store.getProcedureDatasets(new DatasetQuery());
        long limit;
        if (query.getLimit().isPresent()) {
            limit = query.getLimit().getAsLong() + query.getOffset();
        } else {
            limit = procedures.size();
        }
        for (long i = query.getOffset(); i < limit; i++) {
            ProcedureDataset proc = procedures.get((int) i);
            // TODO apply filter
            results.add(proc);
        }
        return results;
    }

    @Override
    public Map<String, Geometry> getSensorLocations() throws DataStoreException {
        // TODO apply filter
        Map<String, Geometry> results = new HashMap<>();
        List<ProcedureDataset> procedures = store.getProcedureDatasets(new DatasetQuery());
        long limit;
        if (query.getLimit().isPresent()) {
            limit = query.getLimit().getAsLong() + query.getOffset();
        } else {
            limit = procedures.size();
        }
        for (long i = query.getOffset(); i < limit; i++) {
            ProcedureDataset proc = procedures.get((int) i);
            results.put(proc.getId(), proc.spatialBound.getLastGeometry());
        }
        return results;
    }

    @Override
    public Map<String, Map<Date, Geometry>> getSensorHistoricalLocations() throws DataStoreException {
        // TODO apply filter
        Map<String, Map<Date, Geometry>> results = new HashMap<>();
        List<ProcedureDataset> procedures = store.getProcedureDatasets(new DatasetQuery());
        for (ProcedureDataset proc : procedures) {
            results.put(proc.getId(), proc.spatialBound.getHistoricalLocations());
        }
        return results;
    }

    @Override
    public Map<String, Set<Date>> getSensorHistoricalTimes() throws DataStoreException {
        // TODO apply filter
        Map<String, Set<Date>> results = new HashMap<>();
        List<ProcedureDataset> procedures = store.getProcedureDatasets(new DatasetQuery());
        for (ProcedureDataset proc : procedures) {
            results.put(proc.getId(), proc.spatialBound.getHistoricalLocations().keySet());
        }
        return results;
    }

    @Override
    public Object getResults() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Envelope getCollectionBoundingShape() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Offering> getOfferings() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void startFilterBlock(LogicalOperatorName operator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void appendFilterOperator(LogicalOperatorName operator, FilterAppend fa) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void endFilterBlock(LogicalOperatorName operator, FilterAppend merged) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeFilterOperator(LogicalOperatorName operator, FilterAppend merged, FilterAppend fa) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

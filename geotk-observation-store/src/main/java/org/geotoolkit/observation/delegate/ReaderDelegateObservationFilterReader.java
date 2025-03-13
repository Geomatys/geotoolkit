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
package org.geotoolkit.observation.delegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.observation.AbstractObservationFilterReader;
import org.geotoolkit.observation.FilterAppend;
import org.geotoolkit.observation.OMUtils;

import org.geotoolkit.observation.ObservationFilterReader;
import org.geotoolkit.observation.ObservationReader;
import org.geotoolkit.observation.ObservationResult;
import org.geotoolkit.observation.model.Offering;
import org.locationtech.jts.geom.Geometry;
import org.opengis.geometry.Envelope;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.Procedure;
import org.geotoolkit.observation.model.SamplingFeature;
import org.opengis.filter.LogicalOperatorName;

/**
 * An {@linkplain ObservationFilterReader} delegating all its methods on a {@linkplain ObservationReader}
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ReaderDelegateObservationFilterReader extends AbstractObservationFilterReader {

    private ObservationReader reader;

    public ReaderDelegateObservationFilterReader(final Map<String, Object> properties) throws DataStoreException {
        super(properties);
    }

    @Override
    public List<ObservationResult> filterResult() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Set<String> getIdentifiers() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public long getCount() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Observation> getObservations() throws DataStoreException {
        List<Observation> results = new ArrayList<>();
        Collection<String> oids;
        // priority to observation  ids
        if (!observationIds.isEmpty()) {
            oids = observationIds;
        } else {
            oids = reader.getEntityNames(entityType);
        }
        for (String oid : oids) {
            org.geotoolkit.observation.model.Observation obs = (org.geotoolkit.observation.model.Observation) reader.getObservation(oid, resultModel, responseMode);
            if (obs != null) {
                // filters
                if (!observedPhenomenons.isEmpty() && !observedPhenomenons.contains(obs.getObservedProperty().getId())) continue;
                if (!featureOfInterests.isEmpty()  && !featureOfInterests.contains(obs.getFeatureOfInterest().getId())) continue;
                if (!procedures.isEmpty()          && !procedures.contains(obs.getProcedure().getId())) continue;
                // TODO match time

                results.add(obs);
            }
        }
        return OMUtils.applyPostPagination(results, offset, limit);
    }

    @Override
    public List<SamplingFeature> getFeatureOfInterests() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Phenomenon> getPhenomenons() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Procedure> getProcesses() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Offering> getOfferings() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Map<String, Geometry> getSensorLocations() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Map<String, Map<Date, Geometry>> getSensorHistoricalLocations() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Map<String, Set<Date>> getSensorHistoricalTimes() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getResults() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Envelope getCollectionBoundingShape() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void refresh() throws DataStoreException {
        // do nothing
    }

    @Override
    public void destroy() {
        reader.destroy();
    }

    @Override
    public void startFilterBlock(LogicalOperatorName operator) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void appendFilterOperator(LogicalOperatorName operator, FilterAppend merged) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void endFilterBlock(LogicalOperatorName operator, FilterAppend merged) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void removeFilterOperator(LogicalOperatorName operator, FilterAppend merged, FilterAppend fa) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

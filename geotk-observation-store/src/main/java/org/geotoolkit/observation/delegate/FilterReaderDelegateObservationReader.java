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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import static org.geotoolkit.observation.OMUtils.OBSERVATION_QNAME;
import org.geotoolkit.observation.ObservationFilterReader;
import org.geotoolkit.observation.ObservationReader;
import org.geotoolkit.observation.model.OMEntity;
import org.geotoolkit.observation.model.Offering;
import org.geotoolkit.observation.model.ResponseMode;
import static org.geotoolkit.observation.model.ResponseMode.RESULT_TEMPLATE;
import org.geotoolkit.observation.query.HistoricalLocationQuery;
import org.geotoolkit.observation.query.IdentifierQuery;
import org.geotoolkit.observation.query.LocationQuery;
import org.geotoolkit.observation.query.ObservationQuery;
import org.geotoolkit.observation.query.ObservationQueryUtilities;
import org.geotoolkit.observation.query.ObservedPropertyQuery;
import org.geotoolkit.observation.query.OfferingQuery;
import org.geotoolkit.observation.query.ProcedureQuery;
import org.geotoolkit.observation.query.SamplingFeatureQuery;
import org.locationtech.jts.geom.Geometry;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.Procedure;
import org.geotoolkit.observation.model.SamplingFeature;
import org.opengis.temporal.TemporalPrimitive;

/**
 * An {@linkplain ObservationReader} delegating all its methods on a {@linkplain ObservationFilterReader}
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FilterReaderDelegateObservationReader implements ObservationReader {
    private final Supplier<ObservationFilterReader> filterSupplier;

    public FilterReaderDelegateObservationReader(Supplier<ObservationFilterReader> filterSupplier) {
        this.filterSupplier = filterSupplier;
    }

    @Override
    public Collection<String> getEntityNames(OMEntity entityType) throws DataStoreException {
        ObservationFilterReader filter = filterSupplier.get();
        filter.init(ObservationQueryUtilities.getQueryForEntityType(entityType));
        return filter.getIdentifiers();
    }

    @Override
    public boolean existEntity(IdentifierQuery query) throws DataStoreException {
        ObservationFilterReader filter = filterSupplier.get();
        OMEntity entityType = query.getEntityType();
        String identifier   =  query.getIdentifier();
        filter.init(ObservationQueryUtilities.getQueryForEntityType(entityType));
        return filter.getIdentifiers().contains(identifier);
    }

    @Override
    public Offering getObservationOffering(String identifier) throws DataStoreException {
        ObservationFilterReader filter = filterSupplier.get();
        filter.init(new OfferingQuery());
        filter.setObservedProperties(Arrays.asList(identifier));
        List<Offering> results = filter.getOfferings();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public Phenomenon getPhenomenon(String identifier) throws DataStoreException {
        ObservationFilterReader filter = filterSupplier.get();
        filter.init(new ObservedPropertyQuery());
        filter.setObservedProperties(Arrays.asList(identifier));
        List<Phenomenon> results = filter.getPhenomenons();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public Procedure getProcess(String identifier) throws DataStoreException {
        ObservationFilterReader filter = filterSupplier.get();
        filter.init(new ProcedureQuery());
        filter.setProcedure(Arrays.asList(identifier));
        List<Procedure> results = filter.getProcesses();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public TemporalPrimitive getProcedureTime(String sensorID) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SamplingFeature getFeatureOfInterest(String identifier) throws DataStoreException {
        ObservationFilterReader filter = filterSupplier.get();
        filter.init(new SamplingFeatureQuery());
        filter.setProcedure(Arrays.asList(identifier));
        List<SamplingFeature> results = filter.getFeatureOfInterests();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public TemporalPrimitive getFeatureOfInterestTime(String samplingFeatureName) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Observation getObservation(String identifier, QName resultModel, ResponseMode mode) throws DataStoreException {
        ObservationFilterReader filter = filterSupplier.get();
        filter.init(new ObservationQuery(resultModel, mode, null));
        filter.setObservationIds(Arrays.asList(identifier));
        List<Observation> results = filter.getObservations();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public Observation getTemplateForProcedure(String procedure) throws DataStoreException {
        ObservationFilterReader filter = filterSupplier.get();
        filter.init(new ObservationQuery(OBSERVATION_QNAME, RESULT_TEMPLATE, null));
        filter.setProcedure(Arrays.asList(procedure));
        List<Observation> results = filter.getObservations();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public TemporalPrimitive getEventTime() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Geometry getSensorLocation(String sensorID) throws DataStoreException {
        ObservationFilterReader filter = filterSupplier.get();
        filter.init(new LocationQuery());
        filter.setProcedure(Arrays.asList(sensorID));
        Map<String, Geometry> sensorLocations = filter.getSensorLocations();
        return sensorLocations.getOrDefault(sensorID, null);
    }

    @Override
    public Map<Date, Geometry> getSensorLocations(String sensorID) throws DataStoreException {
        ObservationFilterReader filter = filterSupplier.get();
        filter.init(new HistoricalLocationQuery());
        filter.setProcedure(Arrays.asList(sensorID));
        Map<String, Map<Date, Geometry>> sensorHistoricalLocations = filter.getSensorHistoricalLocations();
        return sensorHistoricalLocations.getOrDefault(sensorID, null);
    }

    @Override
    public void destroy() {
        // do nothing.
    }
}

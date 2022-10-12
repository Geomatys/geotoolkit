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
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.observation.ObservationReader;
import org.geotoolkit.observation.ObservationStore;
import org.geotoolkit.observation.model.OMEntity;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.model.ProcedureDataset;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.Offering;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.Procedure;
import org.geotoolkit.observation.model.ResponseMode;
import org.geotoolkit.observation.model.Result;
import org.geotoolkit.observation.model.SamplingFeature;
import org.geotoolkit.observation.query.DatasetQuery;
import org.geotoolkit.observation.query.IdentifierQuery;
import org.geotoolkit.observation.query.ObservationQueryUtilities;
import org.locationtech.jts.geom.Geometry;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class StoreDelegatingObservationReader implements ObservationReader {

    private final ObservationStore store;

    public StoreDelegatingObservationReader(ObservationStore store) {
        this.store = store;
    }

    @Override
    public Collection<String> getEntityNames(OMEntity entityType) throws DataStoreException {
        return store.getEntityNames(ObservationQueryUtilities.getQueryForEntityType(entityType));
    }

    @Override
    public boolean existEntity(IdentifierQuery query) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Offering getObservationOffering(String identifier) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Phenomenon getPhenomenon(String identifier) throws DataStoreException {
        ObservationDataset results = store.getDataset(new DatasetQuery());
        for (Phenomenon phen : results.phenomenons) {
            if (phen.getId().equals(identifier)) {
                return phen;
            }
        }
        return null;
    }

    @Override
    public Procedure getProcess(String identifier) throws DataStoreException {
        ObservationDataset results = store.getDataset(new DatasetQuery());
        for (ProcedureDataset proc : results.procedures) {
            if (proc.getId().equals(identifier)) {
                return proc;
            }
        }
        return null;
    }

    @Override
    public TemporalGeometricPrimitive getProcedureTime(String sensorID) throws DataStoreException {
        DatasetQuery query = new DatasetQuery(Arrays.asList(sensorID));
        ObservationDataset results = store.getDataset(query);
        return results.spatialBound.getTimeObject();
    }

    @Override
    public SamplingFeature getFeatureOfInterest(String identifier) throws DataStoreException {
        ObservationDataset results = store.getDataset(new DatasetQuery());
        for (SamplingFeature sf : results.featureOfInterest) {
            if (sf.getId().equals(identifier)) {
                return sf;
            }
        }
        return null;
    }

    @Override
    public TemporalGeometricPrimitive getFeatureOfInterestTime(String samplingFeatureName) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Observation getObservation(String identifier, QName resultModel, ResponseMode mode) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Observation getTemplateForProcedure(String procedure) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Result getResult(String identifier, QName resultModel) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TemporalGeometricPrimitive getEventTime() throws DataStoreException {
        ObservationDataset results = store.getDataset(new DatasetQuery());
        return results.spatialBound.getTimeObject();
    }

    @Override
    public Geometry getSensorLocation(String identifier) throws DataStoreException {
        ObservationDataset results = store.getDataset(new DatasetQuery());
        for (ProcedureDataset proc : results.procedures) {
            if (proc.getId().equals(identifier)) {
               return proc.spatialBound.getLastGeometry();
            }
        }
        return null;
    }

    @Override
    public Map<Date, Geometry> getSensorLocations(String identifier) throws DataStoreException {
        ObservationDataset results = store.getDataset(new DatasetQuery());
        for (ProcedureDataset proc : results.procedures) {
            if (proc.getId().equals(identifier)) {
               return proc.spatialBound.getHistoricalLocations();
            }
        }
        return new HashMap<>();
    }

    @Override
    public void destroy() {
        // do nothing
    }

}

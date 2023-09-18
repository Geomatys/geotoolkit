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
package org.geotoolkit.data.om.netcdf;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.observation.ObservationReader;
import org.geotoolkit.observation.model.OMEntity;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.Offering;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.Procedure;
import org.geotoolkit.observation.model.ResponseMode;
import org.geotoolkit.observation.model.SamplingFeature;
import org.geotoolkit.observation.query.IdentifierQuery;
import org.locationtech.jts.geom.Geometry;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author Guilhem (Geomatys)
 */
public class NetcdfObservationReader implements ObservationReader {

    private final Path dataFile;
    private final NCFieldAnalyze analyze;

    public NetcdfObservationReader(final Path dataFile, final NCFieldAnalyze analyze) {
        this.analyze = analyze;
        this.dataFile = dataFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getEntityNames(final OMEntity entityType) throws DataStoreException {
        switch (entityType) {
            case FEATURE_OF_INTEREST: return getFeatureOfInterestNames();
            case OBSERVED_PROPERTY:   return getPhenomenonNames();
            case PROCEDURE:           return getProcedureNames();
            case LOCATION:            throw new DataStoreException("not implemented yet.");
            case HISTORICAL_LOCATION: throw new DataStoreException("not implemented yet.");
            case OFFERING:            throw new DataStoreException("offerings are not handled in File observation reader.");
            case OBSERVATION:         throw new DataStoreException("not implemented yet.");
            case RESULT:              throw new DataStoreException("not implemented yet.");
            default: throw new DataStoreException("unexpected entity type:" + entityType);
        }
    }

    private Collection<String> getProcedureNames() throws DataStoreException {
        final Set<String> names = new HashSet<>();
        names.add(getProcedureID());
        return names;
    }

    private String getProcedureID() {
        return IOUtilities.filenameWithoutExtension(dataFile);
    }

    private Collection<String> getPhenomenonNames() throws DataStoreException {
        final Set<String> phenomenons = new HashSet<>();
        for (NCField field : analyze.phenfields) {
            phenomenons.add(field.name);
        }
        return phenomenons;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existEntity(final IdentifierQuery query) throws DataStoreException {
        OMEntity entityType = query.getEntityType();
        if (entityType == null) {
            throw new DataStoreException("Missing entity type parameter");
        }
        String identifier   = query.getIdentifier();
        switch (entityType) {
            case FEATURE_OF_INTEREST: return getFeatureOfInterestNames().contains(identifier);
            case OBSERVED_PROPERTY:   return existPhenomenon(identifier);
            case PROCEDURE:           return existProcedure(identifier);
            case LOCATION:            throw new DataStoreException("not implemented yet.");
            case HISTORICAL_LOCATION: throw new DataStoreException("not implemented yet.");
            case OFFERING:            throw new DataStoreException("offerings are not handled in File observation reader.");
            case OBSERVATION:         throw new DataStoreException("not implemented yet.");
            case RESULT:              throw new DataStoreException("not implemented yet.");
            default: throw new DataStoreException("unexpected entity type:" + entityType);
        }
    }

    @Override
    public Phenomenon getPhenomenon(String identifier) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet in netcdf implementation.");
    }

    @Override
    public Procedure getProcess(String identifier) throws DataStoreException {
        if (existProcedure(identifier)) {
            return new Procedure(identifier);
        }
        return null;
    }

    @Override
    public TemporalGeometricPrimitive getProcedureTime(final String sensorID) throws DataStoreException {
        try {
            final ObservationDataset result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null, null, new HashSet<>());
            if (result != null && result.spatialBound != null) {
                return result.spatialBound.getTimeObject();
            }
            return null;
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }

    private boolean existPhenomenon(final String phenomenonName) throws DataStoreException {
        for (NCField field : analyze.phenfields) {
            if (field.name.equals(phenomenonName)) {
                return true;
            }
        }
        return false;
    }

    private Collection<String> getFeatureOfInterestNames() throws DataStoreException {
        try {
            final ObservationDataset result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null, null, new HashSet<>());
            return result.featureOfInterest.stream().map(foi -> foi.getId()).toList();
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public SamplingFeature getFeatureOfInterest(final String identifier) throws DataStoreException {
        try {
            final ObservationDataset result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null, null, new HashSet<>());
            for (org.geotoolkit.observation.model.SamplingFeature feature : result.featureOfInterest) {
                if (feature.getId().equals(identifier)) {
                    return feature;
                }
            }
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
        return null;
    }

    @Override
    public TemporalGeometricPrimitive getFeatureOfInterestTime(String samplingFeatureName) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this this implementation.");
    }

    @Override
    public Observation getObservation(final String identifier, final QName resultModel, final ResponseMode mode) throws DataStoreException {
       try {
            final ObservationDataset result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null, null, new HashSet<>());
            for (Observation obs : result.observations) {
                if (obs.getId().equals(identifier)) {
                    return obs;
                }
            }
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
        return null;
    }

    private boolean existProcedure(final String href) throws DataStoreException {
        return href.equals(getProcedureID());
    }

    @Override
    public TemporalGeometricPrimitive getEventTime() throws DataStoreException {
        throw new DataStoreException("Not supported yet in this this implementation.");
    }

    @Override
    public Geometry getSensorLocation(String sensorID) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this this implementation.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Date, Geometry> getSensorLocations(String sensorID) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet in this implementation.");
    }

    @Override
    public void destroy() {
        //do nothing
    }

    @Override
    public Offering getObservationOffering(String identifier) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in File observation reader.");
    }

    @Override
    public Observation getTemplateForProcedure(final String procedure) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this this implementation.");
    }
}

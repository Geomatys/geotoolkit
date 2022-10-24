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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.nio.IOUtilities;
import static org.geotoolkit.observation.OMUtils.RESPONSE_FORMAT_V100;
import static org.geotoolkit.observation.OMUtils.RESPONSE_FORMAT_V200;
import org.geotoolkit.observation.ObservationReader;
import org.geotoolkit.observation.xml.AbstractObservation;
import org.geotoolkit.observation.model.OMEntity;
import static org.geotoolkit.observation.ObservationReader.ENTITY_TYPE;
import static org.geotoolkit.observation.ObservationReader.SENSOR_TYPE;
import static org.geotoolkit.observation.ObservationReader.SOS_VERSION;
import org.geotoolkit.observation.model.ExtractionResult;
import org.geotoolkit.sos.xml.ObservationOffering;
import org.geotoolkit.sos.xml.ResponseModeType;
import org.geotoolkit.sos.xml.SOSXmlFactory;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.Process;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.temporal.TemporalPrimitive;

/**
 *
 * @author guilhem
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
    public Collection<String> getEntityNames(final Map<String, Object> hints) throws DataStoreException {
        OMEntity entityType = (OMEntity) hints.get(ENTITY_TYPE);
        String sensorType   = (String) hints.get(SENSOR_TYPE);
        String version      = (String) hints.get(SOS_VERSION);
        switch (entityType) {
            case FEATURE_OF_INTEREST: return getFeatureOfInterestNames();
            case OBSERVED_PROPERTY:   return getPhenomenonNames();
            case PROCEDURE:           return getProcedureNames(sensorType);
            case LOCATION:            throw new DataStoreException("not implemented yet.");
            case HISTORICAL_LOCATION: throw new DataStoreException("not implemented yet.");
            case OFFERING:            throw new DataStoreException("offerings are not handled in File observation reader.");
            case OBSERVATION:         throw new DataStoreException("not implemented yet.");
            case RESULT:              throw new DataStoreException("not implemented yet.");
            default: throw new DataStoreException("unexpected entity type:" + entityType);
        }
    }

    private Collection<String> getProcedureNames(String sensorType) throws DataStoreException {
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
    public boolean existEntity(final Map<String, Object> hints) throws DataStoreException {
        OMEntity entityType = (OMEntity) hints.get(ENTITY_TYPE);
        if (entityType == null) {
            throw new DataStoreException("Missing entity type parameter");
        }
        String identifier   = (String) hints.get(IDENTIFIER);
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
    public Collection<Phenomenon> getPhenomenons(final Map<String, Object> hints) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet in netcdf implementation.");
    }

    @Override
    public Process getProcess(String identifier, String version) throws DataStoreException {
        if (existProcedure(identifier)) {
            return SOSXmlFactory.buildProcess(version, identifier);
        }
        return null;
    }

    @Override
    public TemporalGeometricPrimitive getTimeForProcedure(final String version, final String sensorID) throws DataStoreException {
        try {
            final ExtractionResult result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null, new HashSet<>());
            if (result != null && result.spatialBound != null) {
                return result.spatialBound.getTimeObject(version);
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
            final ExtractionResult result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null, new HashSet<>());
            return result.featureOfInterestNames;
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public SamplingFeature getFeatureOfInterest(final String samplingFeatureName, final String version) throws DataStoreException {
        try {
            final ExtractionResult result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null, new HashSet<>());
            for (SamplingFeature feature : result.featureOfInterest) {
                if (feature instanceof org.geotoolkit.sampling.xml.SamplingFeature &&
                   ((org.geotoolkit.sampling.xml.SamplingFeature)feature).getId().equals(samplingFeatureName)) {
                    return feature;
                }
            }
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
        return null;
    }

    @Override
    public TemporalPrimitive getFeatureOfInterestTime(String samplingFeatureName, String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this this implementation.");
    }

    @Override
    public Observation getObservation(final String identifier, final QName resultModel, final ResponseModeType mode, final String version) throws DataStoreException {
       try {
            final ExtractionResult result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null, new HashSet<>());
            for (Observation obs : result.observations) {
                final AbstractObservation o = (AbstractObservation) obs;
                if (o.getId().equals(identifier)) {
                    return o;
                }
            }
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
        return null;
    }

    @Override
    public Object getResult(final String identifier, final QName resultModel, final String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this this implementation.");
    }

    private boolean existProcedure(final String href) throws DataStoreException {
        return href.equals(getProcedureID());
    }

    @Override
    public TemporalPrimitive getEventTime(String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this this implementation.");
    }

    @Override
    public AbstractGeometry getSensorLocation(String sensorID, String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this this implementation.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Date, AbstractGeometry> getSensorLocations(String sensorID, String version) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet in this implementation.");
    }

    @Override
    public void destroy() {
        //do nothing
    }

    @Override
    public List<ObservationOffering> getObservationOfferings(final Map<String, Object> hints) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in File observation reader.");
    }

    @Override
    public Observation getTemplateForProcedure(final String procedure, final String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this this implementation.");
    }
}

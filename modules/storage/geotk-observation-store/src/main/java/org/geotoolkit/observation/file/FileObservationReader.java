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
package org.geotoolkit.observation.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.observation.ObservationReader;
import org.geotoolkit.observation.xml.AbstractObservation;
import static org.geotoolkit.observation.xml.XmlObservationUtils.*;
import org.geotoolkit.sos.netcdf.ExtractionResult;
import org.geotoolkit.sos.netcdf.Field;
import org.geotoolkit.sos.netcdf.NCFieldAnalyze;
import org.geotoolkit.sos.netcdf.NetCDFExtractor;
import org.geotoolkit.sos.netcdf.NetCDFParsingException;
import org.geotoolkit.sos.xml.ObservationOffering;
import org.geotoolkit.sos.xml.ResponseModeType;
import org.opengis.observation.Observation;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.temporal.TemporalPrimitive;

/**
 *
 * @author guilhem
 */
public class FileObservationReader implements ObservationReader {

    private final File dataFile;
    private final NCFieldAnalyze analyze;
    
    public FileObservationReader(final File dataFile, final NCFieldAnalyze analyze) {
        this.analyze = analyze;
        this.dataFile = dataFile;
    }
    
    @Override
    public Collection<String> getProcedureNames() throws DataStoreException {
        final Set<String> names = new HashSet<>();
        names.add(getProcedureID());
        return names;
    }
    
    private String getProcedureID() {
        String local;
        if (dataFile.getName().indexOf('.') != -1) {
            local = dataFile.getName().substring(0, dataFile.getName().lastIndexOf('.'));
        } else {
            local = dataFile.getName();
        }
        return local;
    }

    @Override
    public Collection<String> getPhenomenonNames() throws DataStoreException {
        final Set<String> phenomenons = new HashSet<>();
        for (Field field : analyze.phenfields) {
            phenomenons.add(field.label);
        }
        return phenomenons;
    }

    @Override
    public Collection<String> getProceduresForPhenomenon(final String observedProperty) throws DataStoreException {
        if (existPhenomenon(observedProperty)) {
            return Arrays.asList(getProcedureID());
        }
        return new ArrayList<>();
    }

    @Override
    public Collection<String> getPhenomenonsForProcedure(final String sensorID) throws DataStoreException {
        if (sensorID.equals(getProcedureID())) {
            return getPhenomenonNames();
        }
        return new ArrayList<>();
    }

    @Override
    public TemporalGeometricPrimitive getTimeForProcedure(final String version, final String sensorID) throws DataStoreException {
        try {
            final ExtractionResult result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null);
            if (result != null && result.spatialBound != null) {
                return result.spatialBound.getTimeObject(version);
            }
            return null;
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public boolean existPhenomenon(final String phenomenonName) throws DataStoreException {
        for (Field field : analyze.phenfields) {
            if (field.label.equals(phenomenonName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<String> getFeatureOfInterestNames() throws DataStoreException {
        try {
            final ExtractionResult result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null);
            return result.featureOfInterestNames;
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public SamplingFeature getFeatureOfInterest(final String samplingFeatureName, final String version) throws DataStoreException {
        try {
            final ExtractionResult result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null);
            for (org.geotoolkit.sampling.xml.SamplingFeature feature : result.featureOfInterest) {
                if (feature.getId().equals(samplingFeatureName)) {
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
            final ExtractionResult result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null);
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

    @Override
    public boolean existProcedure(final String href) throws DataStoreException {
        return href.equals(getProcedureID());
    }

    @Override
    public String getNewObservationId() throws DataStoreException {
        throw new DataStoreException("Not supported in this implementation.");
    }

    @Override
    public List<String> getEventTime() throws DataStoreException {
        throw new DataStoreException("Not supported yet in this this implementation.");
    }

    @Override
    public List<ResponseModeType> getResponseModes() throws DataStoreException {
        return Arrays.asList(ResponseModeType.INLINE);
    }

    @Override
    public List<String> getResponseFormats() throws DataStoreException {
        return Arrays.asList(RESPONSE_FORMAT_V100, RESPONSE_FORMAT_V200);
    }

    @Override
    public AbstractGeometry getSensorLocation(String sensorID, String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this this implementation.");
    }

    @Override
    public String getInfos() {
        return "NetCDF observation file Reader 4.x";
    }

    @Override
    public void destroy() {
        //do nothing
    }
    
    
    @Override
    public Collection<String> getOfferingNames(final String version) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in File observation reader.");
    }

    @Override
    public ObservationOffering getObservationOffering(final String offeringName, final String version) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in File observation reader.");
    }

    @Override
    public List<ObservationOffering> getObservationOfferings(final List<String> offeringNames, final String version) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in File observation reader.");
    }

    @Override
    public List<ObservationOffering> getObservationOfferings(final String version) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in File observation reader.");
    }

    @Override
    public Observation getTemplateForProcedure(final String procedure, final String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this this implementation.");
    }
}

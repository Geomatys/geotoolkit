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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.observation.AbstractObservationStore;
import org.geotoolkit.observation.ObservationReader;
import static org.geotoolkit.observation.file.FileObservationStoreFactory.FILE_PATH;
import org.geotoolkit.sos.netcdf.ExtractionResult;
import org.geotoolkit.sos.netcdf.Field;
import org.geotoolkit.sos.netcdf.NCFieldAnalyze;
import org.geotoolkit.sos.netcdf.NetCDFExtractor;
import org.geotoolkit.sos.netcdf.NetCDFParsingException;
import org.geotoolkit.storage.DataFileStore;
import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FileObservationStore extends AbstractObservationStore implements DataFileStore {
    
    private final File dataFile;
    private final NCFieldAnalyze analyze;
    
    public FileObservationStore(final ParameterValueGroup params) {
        super(params);
        dataFile = (File) params.parameter(FILE_PATH.getName().toString()).getValue();
        analyze = NetCDFExtractor.analyzeResult(dataFile, null);
    }
    
    public FileObservationStore(final File observationFile) {
        super(null);
        dataFile = observationFile;
        analyze = NetCDFExtractor.analyzeResult(dataFile, null);
    }

    /**
     * @return the dataFile
     */
    public File getDataFile() {
        return dataFile;
    }
    
    @Override
    public Set<GenericName> getProcedureNames() {
        final Set<GenericName> names = new HashSet<>();
        names.add(DefaultName.create(getProcedureID()));
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
    public ExtractionResult getResults() throws DataStoreException {
        try {
            return NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null);
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }
    
    @Override
    public ExtractionResult getResults(final List<String> sensorIDs) throws DataStoreException {
        try {
            return NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), sensorIDs);
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }
    
    @Override
    public ExtractionResult getResults(final String affectedSensorID, final List<String> sensorIDs) throws DataStoreException {
        try {
            return NetCDFExtractor.getObservationFromNetCDF(analyze, affectedSensorID, sensorIDs);
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }
    
    @Override
    public void close() throws DataStoreException {
        // do nothing
    }

    @Override
    public Set<String> getPhenomenonNames() {
        final Set<String> phenomenons = new HashSet<>();
        for (Field field : analyze.phenfields) {
            phenomenons.add(field.label);
        }
        return phenomenons;
    }
    
    @Override
    public TemporalGeometricPrimitive getTemporalBounds() throws DataStoreException {
        try {
            final ExtractionResult result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null);
            if (result != null && result.spatialBound != null) {
                return result.spatialBound.getTimeObject("2.0.0");
            }
            return null;
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public File[] getDataFiles() throws DataStoreException {
        return new File[]{dataFile};
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationReader getReader() {
        return new FileObservationReader(dataFile, analyze);
    }

    @Override
    public List<ExtractionResult.ProcedureTree> getProcedures() throws DataStoreException {
        try {
            return NetCDFExtractor.getProcedures(analyze, getProcedureID(), null);
            
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }
}
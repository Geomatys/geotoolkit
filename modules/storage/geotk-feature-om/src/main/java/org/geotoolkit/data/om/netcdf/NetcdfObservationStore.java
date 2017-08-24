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

import org.geotoolkit.data.om.OMFeatureTypes;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.internal.data.GenericNameIndex;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.observation.ObservationFilter;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.observation.ObservationReader;
import org.geotoolkit.observation.ObservationStore;
import org.geotoolkit.observation.ObservationWriter;
import static org.geotoolkit.data.om.netcdf.NetcdfObservationStoreFactory.FILE_PATH;
import org.geotoolkit.sos.netcdf.ExtractionResult;
import org.geotoolkit.sos.netcdf.Field;
import org.geotoolkit.sos.netcdf.NCFieldAnalyze;
import org.geotoolkit.sos.netcdf.NetCDFExtractor;
import org.geotoolkit.sos.netcdf.NetCDFParsingException;
import org.geotoolkit.storage.DataFileStore;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NetcdfObservationStore extends AbstractFeatureStore implements DataFileStore,ObservationStore {

    protected final GenericNameIndex<FeatureType> types;
    private static final QueryCapabilities capabilities = new DefaultQueryCapabilities(false);
    private final Path dataFile;
    private final NCFieldAnalyze analyze;

    public NetcdfObservationStore(final ParameterValueGroup params) {
        super(params);
        dataFile = Paths.get((URI) params.parameter(FILE_PATH.getName().toString()).getValue());
        analyze = NetCDFExtractor.analyzeResult(dataFile, null);
        types = OMFeatureTypes.getFeatureTypes(IOUtilities.filenameWithoutExtension(dataFile));
    }

    public NetcdfObservationStore(final Path observationFile) {
        super(null);
        dataFile = observationFile;
        analyze = NetCDFExtractor.analyzeResult(dataFile, null);
        types = OMFeatureTypes.getFeatureTypes(IOUtilities.filenameWithoutExtension(dataFile));
    }

    @Override
    public DataStoreFactory getProvider() {
        return DataStores.getFactoryById(NetcdfObservationStoreFactory.NAME);
    }

    /**
     * @return the dataFile
     */
    public Path getDataFile() {
        return dataFile;
    }

    ////////////////////////////////////////////////////////////////////////////
    // FEATURE STORE ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////


    /**
     * {@inheritDoc }
     */
    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        return types.getNames();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType(final String typeName) throws DataStoreException {
        typeCheck(typeName);
        return types.get(this, typeName);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public QueryCapabilities getQueryCapabilities() {
        return capabilities;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void refreshMetaModel() {
    }

    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final FeatureType sft = getFeatureType(query.getTypeName());
        try {
            return FeatureStreams.subset(new NetcdfFeatureReader(dataFile,sft), query);
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void createFeatureType(final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatureType(final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteFeatureType(final String typeName) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(String groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final String groupName, final Filter filter, final Map<String, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(String groupName, Filter filter) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }


    ////////////////////////////////////////////////////////////////////////////
    // OBSERVATION STORE ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public Set<GenericName> getProcedureNames() {
        final Set<GenericName> names = new HashSet<>();
        names.add(NamesExt.create(getProcedureID()));
        return names;
    }

    private String getProcedureID() {
        return IOUtilities.filenameWithoutExtension(dataFile);
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
    public Path[] getDataFiles() throws DataStoreException {
        return new Path[]{dataFile};
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationReader getReader() {
        return new NetcdfObservationReader(dataFile, analyze);
    }

    @Override
    public List<ExtractionResult.ProcedureTree> getProcedures() throws DataStoreException {
        try {
            return NetCDFExtractor.getProcedures(analyze, getProcedureID(), null);

        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationFilter getFilter() {
        throw new UnsupportedOperationException("Filtering is not supported on this observation store.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationWriter getWriter() {
        throw new UnsupportedOperationException("Writing is not supported on this observation store.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationFilter cloneObservationFilter(ObservationFilter toClone) {
        throw new UnsupportedOperationException("Filtering is not supported on this observation store.");
    }

}

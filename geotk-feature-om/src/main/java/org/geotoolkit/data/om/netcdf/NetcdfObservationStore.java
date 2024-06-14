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

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.base.ResourceOnFileSystem;
import org.apache.sis.storage.base.StoreResource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.Resource;
import static org.geotoolkit.data.om.netcdf.NetcdfObservationStoreFactory.FILE_PATH;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.observation.AbstractObservationStore;
import static org.geotoolkit.observation.OMUtils.RESPONSE_FORMAT_V100;
import static org.geotoolkit.observation.OMUtils.RESPONSE_FORMAT_V200;
import org.geotoolkit.observation.ObservationReader;
import org.geotoolkit.observation.ObservationStore;
import org.geotoolkit.observation.ObservationStoreCapabilities;
import org.geotoolkit.observation.feature.OMFeatureTypes;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.model.ProcedureDataset;
import org.geotoolkit.observation.model.ResponseMode;
import org.geotoolkit.observation.query.AbstractObservationQuery;
import org.geotoolkit.observation.query.DatasetQuery;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.TemporalPrimitive;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NetcdfObservationStore extends AbstractObservationStore implements Aggregate, ResourceOnFileSystem,ObservationStore {

    private final Path dataFile;
    private final NCFieldAnalyze analyze;

    public NetcdfObservationStore(final ParameterValueGroup params) {
        super(params);
        dataFile = Paths.get((URI) params.parameter(FILE_PATH.getName().toString()).getValue());
        analyze = NetCDFExtractor.analyzeResult(dataFile, null);
    }

    public NetcdfObservationStore(final Path observationFile) {
        this(toParams(observationFile));
    }

    private static ParameterValueGroup toParams(final Path observationFile) {
        ParameterValueGroup parameters = NetcdfObservationStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        parameters.parameter(FILE_PATH.getName().toString()).setValue(observationFile.toUri());
        return parameters;
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(NetcdfObservationStoreFactory.NAME);
    }

    @Override
    protected String getStoreIdentifier() {
        return "netcdf-observation";
    }

    @Override
    public synchronized Collection<? extends Resource> components() throws DataStoreException {
        if (featureSets == null) {
            featureSets = new ArrayList<>();
            GenericName name = NamesExt.create(OMFeatureTypes.OM_NAMESPACE, IOUtilities.filenameWithoutExtension(dataFile));
            featureSets.add(new FeatureView(this, OMFeatureTypes.buildSamplingFeatureFeatureType(name)));
        }
        return featureSets;
    }

    ////////////////////////////////////////////////////////////////////////////
    // OBSERVATION STORE ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private String getProcedureID() {
        return IOUtilities.filenameWithoutExtension(dataFile);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<String> getEntityNames(AbstractObservationQuery query) throws DataStoreException {
        if (query == null) throw new DataStoreException("Query must no be null");

        // no filters will be applied.
        ObservationReader reader = getReader();
        if (reader != null) {
            return new HashSet<>(reader.getEntityNames(query.getEntityType()));
        }

        throw new UnsupportedOperationException("Unable to list entity without a least a reader or a filterReader.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationDataset getDataset(final DatasetQuery query) throws DataStoreException {
        String affectedSensorID = query.getAffectedSensorID() != null ? query.getAffectedSensorID() : getProcedureID();
        try {
            return NetCDFExtractor.getObservationFromNetCDF(analyze, affectedSensorID, query.getSensorIds(), query.getResponseFormat(), new HashSet<>());
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ProcedureDataset> getProcedureDatasets(DatasetQuery query) throws DataStoreException {
        try {
            String affectedSensorID = query.getAffectedSensorID() != null ? query.getAffectedSensorID() : getProcedureID();
            return NetCDFExtractor.getProcedures(analyze, affectedSensorID, null);

        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalPrimitive getTemporalBounds() throws DataStoreException {
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

    /**
     * {@inheritDoc }
     */
    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        return new Path[]{dataFile};
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationReader getReader() {
        return new NetcdfObservationReader(dataFile, analyze);
    }

    private final class FeatureView extends AbstractFeatureSet implements StoreResource {

        private final FeatureType type;
        private final ObservationStore store;

        FeatureView(ObservationStore originator, FeatureType type) {
            super(null, false);
            this.type = type;
            this.store = originator;
        }

        @Override
        public FeatureType getType() throws DataStoreException {
            return type;
        }

        @Override
        public DataStore getOriginator() {
            return (DataStore) store;
        }

        @Override
        public Stream<Feature> features(boolean parallel) throws DataStoreException {
            try {
                final NetcdfFeatureReader reader = new NetcdfFeatureReader(dataFile, type);
                final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize(reader, Spliterator.ORDERED);
                final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
                return stream.onClose(reader::close);
            } catch (NetCDFParsingException ex) {
                throw new DataStoreException(ex);
            }
        }
    }

    @Override
    public ObservationStoreCapabilities getCapabilities() {
        final Map<String, List<String>> responseFormats = new HashMap<>();
        responseFormats.put("1.0.0", Arrays.asList(RESPONSE_FORMAT_V100));
        responseFormats.put("2.0.0", Arrays.asList(RESPONSE_FORMAT_V200));
        final List<ResponseMode> responseMode = Arrays.asList(ResponseMode.INLINE);
        return new ObservationStoreCapabilities(false, false, false, new ArrayList<>(), responseFormats, responseMode, false);
    }
}

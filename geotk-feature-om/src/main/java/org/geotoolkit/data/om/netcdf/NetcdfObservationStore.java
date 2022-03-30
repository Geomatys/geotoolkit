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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.Resource;
import org.geotoolkit.data.om.OMFeatureTypes;
import static org.geotoolkit.data.om.netcdf.NetcdfObservationStoreFactory.FILE_PATH;
import org.geotoolkit.storage.feature.GenericNameIndex;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.observation.ObservationFilterReader;
import org.geotoolkit.observation.ObservationReader;
import org.geotoolkit.observation.ObservationStore;
import org.geotoolkit.observation.ObservationWriter;
import org.geotoolkit.observation.model.ExtractionResult;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.metadata.Metadata;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NetcdfObservationStore extends DataStore implements Aggregate, ResourceOnFileSystem,ObservationStore {

    protected final GenericNameIndex<FeatureType> types;
    private final ParameterValueGroup parameters;
    private final Path dataFile;
    private final NCFieldAnalyze analyze;
    private final List<Resource> components = new ArrayList<>();

    public NetcdfObservationStore(final ParameterValueGroup params) {
        this.parameters = params;
        dataFile = Paths.get((URI) params.parameter(FILE_PATH.getName().toString()).getValue());
        analyze = NetCDFExtractor.analyzeResult(dataFile, null);
        types = OMFeatureTypes.getFeatureTypes(IOUtilities.filenameWithoutExtension(dataFile));

        for (GenericName name : types.getNames()) {
            components.add(new FeatureView(name));
        }
    }

    public NetcdfObservationStore(final Path observationFile) {
        parameters = NetcdfObservationStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        parameters.parameter(FILE_PATH.getName().toString()).setValue(observationFile.toUri());
        dataFile = observationFile;
        analyze = NetCDFExtractor.analyzeResult(dataFile, null);
        types = OMFeatureTypes.getFeatureTypes(IOUtilities.filenameWithoutExtension(dataFile));

        for (GenericName name : types.getNames()) {
            components.add(new FeatureView(name));
        }
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(NetcdfObservationStoreFactory.NAME);
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.of(parameters);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }

    /**
     * @return the dataFile
     */
    public Path getDataFile() {
        return dataFile;
    }

    @Override
    public List<Resource> components() {
        return components;
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
            return NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null, new HashSet<>());
        } catch (NetCDFParsingException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public ExtractionResult getResults(final List<String> sensorIDs) throws DataStoreException {
        return getResults(getProcedureID(), sensorIDs, new HashSet<>(), new HashSet<>());
    }

    @Override
    public ExtractionResult getResults(final String affectedSensorID, final List<String> sensorIDs) throws DataStoreException {
        return getResults(affectedSensorID, sensorIDs, new HashSet<>(), new HashSet<>());
    }

    @Override
    public ExtractionResult getResults(String affectedSensorID, List<String> sensorIds, Set<Phenomenon> phenomenons, Set<SamplingFeature> samplingFeatures) throws DataStoreException {
        try {
            // existing sampling features are not used yet
            return NetCDFExtractor.getObservationFromNetCDF(analyze, affectedSensorID, sensorIds, phenomenons);
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
        for (NCField field : analyze.phenfields) {
            phenomenons.add(field.name);
        }
        return phenomenons;
    }

    @Override
    public TemporalGeometricPrimitive getTemporalBounds() throws DataStoreException {
        try {
            final ExtractionResult result = NetCDFExtractor.getObservationFromNetCDF(analyze, getProcedureID(), null, new HashSet<>());
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
    public ObservationFilterReader getFilter() {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationWriter getWriter() {
        return null;
    }

    private final class FeatureView extends AbstractFeatureSet implements StoreResource {

        private final GenericName name;

        FeatureView(GenericName name) {
            super(null);
            this.name = name;
        }

        @Override
        public FeatureType getType() throws DataStoreException {
            return types.get(NetcdfObservationStore.this, name.toString());
        }

        @Override
        public DataStore getOriginator() {
            return NetcdfObservationStore.this;
        }

        @Override
        public Stream<Feature> features(boolean parallel) throws DataStoreException {
            final FeatureType sft = getType();
            try {
                final NetcdfFeatureReader reader = new NetcdfFeatureReader(dataFile, sft);
                final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize(reader, Spliterator.ORDERED);
                final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
                return stream.onClose(reader::close);
            } catch (NetCDFParsingException ex) {
                throw new DataStoreException(ex);
            }
        }
    }

}

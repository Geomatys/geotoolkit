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
package org.geotoolkit.data.om.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.logging.Level;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.util.Optional;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.base.StoreResource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.Resource;
import static org.geotoolkit.data.om.xml.XmlObservationStoreFactory.FILE_PATH;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.observation.AbstractObservationStore;
import static org.geotoolkit.observation.OMUtils.RESPONSE_FORMAT_V100;
import static org.geotoolkit.observation.OMUtils.RESPONSE_FORMAT_V200;
import org.geotoolkit.observation.ObservationReader;
import org.geotoolkit.observation.ObservationStore;
import org.geotoolkit.observation.ObservationStoreCapabilities;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.Procedure;
import org.geotoolkit.observation.model.ProcedureDataset;
import org.geotoolkit.observation.feature.OMFeatureTypes;
import org.geotoolkit.observation.model.ResponseMode;
import org.geotoolkit.observation.model.SamplingFeature;
import static org.geotoolkit.observation.model.ObservationTransformUtils.toModel;
import org.geotoolkit.observation.query.DatasetQuery;
import org.geotoolkit.sos.xml.SOSMarshallerPool;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class XmlObservationStore extends AbstractObservationStore implements Aggregate {

    private final Path xmlFile;

    public XmlObservationStore(final ParameterValueGroup params) throws IOException {
        super(params);
        xmlFile = Paths.get((URI) params.parameter(FILE_PATH.getName().toString()).getValue());
    }

    public XmlObservationStore(final Path xmlFile) throws IOException {
        this(toParams(xmlFile));
    }

    private static ParameterValueGroup toParams(final Path xmlFile) {
        ParameterValueGroup params = XmlObservationStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        params.parameter(FILE_PATH.getName().toString()).setValue(xmlFile.toUri());
        return params;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(XmlObservationStoreFactory.NAME);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected String getStoreIdentifier() {
        return "xml-observation";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized Collection<? extends Resource> components() throws DataStoreException {
        if (featureSets == null) {
            featureSets = new ArrayList<>();
            GenericName name = NamesExt.create(OMFeatureTypes.OM_NAMESPACE, IOUtilities.filenameWithoutExtension(xmlFile));
            featureSets.add(new FeatureView(this, OMFeatureTypes.buildSamplingFeatureFeatureType(name)));
        }
        return featureSets;
    }

    static Object unmarshallObservationFile(final Path f) throws JAXBException, IOException {
        try (InputStream in = Files.newInputStream(f)) {
            final Unmarshaller um = SOSMarshallerPool.getInstance().acquireUnmarshaller();
            Object obj = um.unmarshal(in);
            if (obj instanceof JAXBElement jb) {
                obj = jb.getValue();
            }
            if (obj != null) {
                return obj;
            }
            throw new JAXBException("the observation file does not contain a valid O&M object");
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // OBSERVATION STORE ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationDataset getDataset(DatasetQuery query) throws DataStoreException {
        final List<org.geotoolkit.observation.model.Observation> observations = new ArrayList<>();
        final Object obj = readFile();
        if (obj instanceof ObservationCollection collection) {
            observations.addAll(collection.getMember().stream().map(obs -> toModel(obs)).toList());
        } else if (obj instanceof Observation obs) {
            observations.add(toModel(obs));
        }

        final ObservationDataset result = new ObservationDataset();
        result.spatialBound.initBoundary();

        final List<String> sensorIDs = query.getSensorIds();
        for (org.geotoolkit.observation.model.Observation obs : observations) {
            final Procedure proc =  obs.getProcedure();
            final ProcedureDataset procedure = new ProcedureDataset(proc.getId(), proc.getName(), proc.getDescription(), "Component", "timeseries", new ArrayList<>(), null);
            if (sensorIDs.isEmpty() || sensorIDs.contains(procedure.getId())) {
                if (!result.procedures.contains(procedure)) {
                    result.procedures.add(procedure);
                }
                final Phenomenon phen = obs.getObservedProperty();
                if (!result.phenomenons.contains(phen)) {
                    result.phenomenons.add(phen);
                }
                SamplingFeature foi = obs.getFeatureOfInterest();
                result.spatialBound.appendLocation(obs.getSamplingTime(), foi);
                procedure.spatialBound.appendLocation(obs.getSamplingTime(), foi);
                result.observations.add(obs);
            }
        }
       return result;
    }

    private Object readFile() {
        try (InputStream fileStream = Files.newInputStream(xmlFile)){
            final Unmarshaller um = SOSMarshallerPool.getInstance().acquireUnmarshaller();
            Object obj = um.unmarshal(fileStream);
            if (obj instanceof JAXBElement jb) {
                obj = jb.getValue();
            }
            SOSMarshallerPool.getInstance().recycle(um);
            return obj;
        } catch (IOException | JAXBException ex) {
            LOGGER.log(Level.WARNING, "Error while reading  file", ex);
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws DataStoreException {
        // do nothing
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<FileSet> getFileSet() throws DataStoreException {
        return Optional.of(new FileSet(xmlFile));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationReader getReader() {
        final Object obj = readFile();
        return new XmlObservationReader(Arrays.asList(obj));
    }

    @Override
    public ObservationStoreCapabilities getCapabilities() {
        final Map<String, List<String>> responseFormats = new HashMap<>();
        responseFormats.put("1.0.0", Arrays.asList(RESPONSE_FORMAT_V100));
        responseFormats.put("2.0.0", Arrays.asList(RESPONSE_FORMAT_V200));
        final List<ResponseMode> responseMode = Arrays.asList(ResponseMode.INLINE);
        return new ObservationStoreCapabilities(false, false, false, new ArrayList<>(), responseFormats, responseMode, false);
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
                final XmlFeatureReader reader = new XmlFeatureReader(xmlFile, type);
                final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize(reader, Spliterator.ORDERED);
                final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
                return stream.onClose(reader::close);
            } catch (JAXBException | IOException ex) {
                throw new DataStoreException(ex);
            }
        }
    }
}

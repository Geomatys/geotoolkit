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
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.logging.Level;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.internal.storage.AbstractFeatureSet;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.Resource;
import org.geotoolkit.data.om.OMFeatureTypes;
import static org.geotoolkit.data.om.xml.XmlObservationStoreFactory.FILE_PATH;
import org.geotoolkit.storage.feature.GenericNameIndex;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.observation.AbstractObservationStore;
import org.geotoolkit.observation.OMUtils;
import org.geotoolkit.observation.ObservationReader;
import org.geotoolkit.observation.xml.AbstractObservation;
import org.geotoolkit.observation.xml.Process;
import org.geotoolkit.observation.model.ExtractionResult;
import org.geotoolkit.observation.model.ExtractionResult.ProcedureTree;
import org.geotoolkit.sos.xml.SOSMarshallerPool;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.metadata.Metadata;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;
import org.opengis.observation.Phenomenon;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class XmlObservationStore extends AbstractObservationStore implements Aggregate, ResourceOnFileSystem {

    protected final GenericNameIndex<FeatureType> types;
    private final Path xmlFile;
    private final List<Resource> components = new ArrayList<>();

    public XmlObservationStore(final ParameterValueGroup params) throws IOException {
        super(params);
        xmlFile = Paths.get((URI) params.parameter(FILE_PATH.getName().toString()).getValue());
        types = OMFeatureTypes.getFeatureTypes(IOUtilities.filenameWithoutExtension(xmlFile));

        for (GenericName name : types.getNames()) {
            components.add(new FeatureView(name));
        }
    }

    public XmlObservationStore(final Path xmlFile) {
        super(toParams(xmlFile));
        this.xmlFile = xmlFile;
        types = OMFeatureTypes.getFeatureTypes(IOUtilities.filenameWithoutExtension(xmlFile));

        for (GenericName name : types.getNames()) {
            components.add(new FeatureView(name));
        }
    }

    private static ParameterValueGroup toParams(final Path xmlFile) {
        ParameterValueGroup params = XmlObservationStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        params.parameter(FILE_PATH.getName().toString()).setValue(xmlFile.toUri());
        return params;
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(XmlObservationStoreFactory.NAME);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }

    @Override
    public Collection<? extends Resource> components() throws DataStoreException {
        return components;
    }

    static Object unmarshallObservationFile(final Path f) throws JAXBException, IOException {
        try (InputStream in = Files.newInputStream(f)) {
            final Unmarshaller um = SOSMarshallerPool.getInstance().acquireUnmarshaller();
            Object obj = um.unmarshal(in);
            if (obj instanceof JAXBElement) {
                obj = ((JAXBElement) obj).getValue();
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

    @Override
    public ExtractionResult getResults(String affectedSensorID, List<String> sensorIds, Set<Phenomenon> phenomenons, final Set<org.opengis.observation.sampling.SamplingFeature> samplingFeatures) throws DataStoreException {
        if (affectedSensorID != null) {
            LOGGER.warning("XMLObservation store does not allow to override sensor ID");
        }
        final ExtractionResult result = new ExtractionResult();
        result.spatialBound.initBoundary();
        final Object obj = readFile();
        if (obj instanceof ObservationCollection) {
            final ObservationCollection collection = (ObservationCollection)obj;
            for (Observation obs : collection.getMember()) {
                final AbstractObservation o = (AbstractObservation)obs;
                final Process proc          =  o.getProcedure();
                final ProcedureTree procedure = new ProcedureTree(proc.getHref(), proc.getName(), proc.getDescription(), "timeseries", "Component");
                if (sensorIds == null || sensorIds.contains(procedure.id)) {
                    if (!result.procedures.contains(procedure)) {
                        result.procedures.add(procedure);
                    }
                    final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                    final List<String> fields = OMUtils.getPhenomenonsFields(phenProp);
                    for (String field : fields) {
                        if (!result.fields.contains(field)) {
                            result.fields.add(field);
                        }
                    }
                    final Phenomenon phen = OMUtils.getPhenomenon(phenProp);
                    if (!result.phenomenons.contains(phen)) {
                        result.phenomenons.add(phen);
                    }
                    result.spatialBound.appendLocation(o.getSamplingTime(), o.getFeatureOfInterest());
                    procedure.spatialBound.appendLocation(o.getSamplingTime(), o.getFeatureOfInterest());
                    result.observations.add(o);
                }
            }

        } else if (obj instanceof AbstractObservation) {
            final AbstractObservation obs = (AbstractObservation)obj;
            final Process proc            =  obs.getProcedure();
            final ProcedureTree procedure = new ProcedureTree(proc.getHref(), proc.getName(), proc.getDescription(), "timeseries", "Component");
            if (sensorIds == null || sensorIds.contains(procedure.id)) {
                result.observations .add(obs);
                final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
                result.fields.addAll(OMUtils.getPhenomenonsFields(phenProp));
                result.phenomenons.add(OMUtils.getPhenomenon(phenProp));
                result.procedures.add(procedure);
                result.spatialBound.appendLocation(obs.getSamplingTime(), obs.getFeatureOfInterest());
                procedure.spatialBound.appendLocation(obs.getSamplingTime(), obs.getFeatureOfInterest());
            }

        }
        return result;
    }

    @Override
    public List<ProcedureTree> getProcedures() throws DataStoreException {
        final List<ProcedureTree> result = new ArrayList<>();
        final Object obj = readFile();
        if (obj instanceof ObservationCollection) {
            final ObservationCollection collection = (ObservationCollection)obj;
            for (Observation obs : collection.getMember()) {
                final AbstractObservation o = (AbstractObservation)obs;
                final Process proc          =  o.getProcedure();
                final ProcedureTree procedure = new ProcedureTree(proc.getHref(), proc.getName(), proc.getDescription(), "Component", "timeseries");

                if (!result.contains(procedure)) {
                    result.add(procedure);
                }
                final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                final List<String> fields = OMUtils.getPhenomenonsFields(phenProp);
                for (String field : fields) {
                    if (!procedure.fields.contains(field)) {
                        procedure.fields.add(field);
                    }
                }
                procedure.spatialBound.appendLocation(obs.getSamplingTime(), obs.getFeatureOfInterest());
            }

        } else if (obj instanceof AbstractObservation) {
            final AbstractObservation obs = (AbstractObservation)obj;
            final Process proc            =  obs.getProcedure();
            final ProcedureTree procedure = new ProcedureTree(proc.getHref(), proc.getName(), proc.getDescription(), "Component", "timeseries");

            final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
            procedure.fields.addAll(OMUtils.getPhenomenonsFields(phenProp));
            result.add(procedure);
            procedure.spatialBound.appendLocation(obs.getSamplingTime(), obs.getFeatureOfInterest());
        }
        return result;
    }

    private Object readFile() {
        try (InputStream fileStream = Files.newInputStream(xmlFile)){
            final Unmarshaller um = SOSMarshallerPool.getInstance().acquireUnmarshaller();
            Object obj = um.unmarshal(fileStream);
            if (obj instanceof JAXBElement) {
                obj = ((JAXBElement)obj).getValue();
            }
            SOSMarshallerPool.getInstance().recycle(um);
            return obj;
        } catch (IOException | JAXBException ex) {
            LOGGER.log(Level.WARNING, "Error while reading  file", ex);
        }
        return null;
    }

    @Override
    public void close() throws DataStoreException {
        // do nothing
    }

    @Override
    public TemporalGeometricPrimitive getTemporalBounds() {
        final ExtractionResult result = new ExtractionResult();
        result.spatialBound.initBoundary();
        final Object obj = readFile();
        if (obj instanceof ObservationCollection) {
            final ObservationCollection collection = (ObservationCollection)obj;
            for (Observation obs : collection.getMember()) {
                result.spatialBound.addTime(obs.getSamplingTime());
            }

        } else if (obj instanceof AbstractObservation) {
            final AbstractObservation obs = (AbstractObservation)obj;
            result.spatialBound.addTime(obs.getSamplingTime());
        }
        return result.spatialBound.getTimeObject("2.0.0");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        return new Path[]{xmlFile};
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationReader getReader() {
        final Object obj = readFile();
        return new XmlObservationReader(Arrays.asList(obj));
    }

    private final class FeatureView extends AbstractFeatureSet implements StoreResource {

        private final GenericName name;

        FeatureView(GenericName name) {
            super(null);
            this.name = name;
        }

        @Override
        public FeatureType getType() throws DataStoreException {
            return types.get(XmlObservationStore.this, name.toString());
        }

        @Override
        public DataStore getOriginator() {
            return XmlObservationStore.this;
        }

        @Override
        public Stream<Feature> features(boolean parallel) throws DataStoreException {
            final FeatureType sft = getType();
            try {
                final XmlFeatureReader reader = new XmlFeatureReader(xmlFile,sft);
                final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize(reader, Spliterator.ORDERED);
                final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
                return stream.onClose(reader::close);
            } catch (JAXBException | IOException ex) {
                throw new DataStoreException(ex);
            }
        }
    }
}

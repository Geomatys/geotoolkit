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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.gml.GMLUtilities;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.AbstractRing;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.LineString;
import org.geotoolkit.gml.xml.Point;
import org.geotoolkit.gml.xml.Polygon;
import org.geotoolkit.observation.ObservationFilter;
import org.geotoolkit.observation.ObservationReader;
import org.geotoolkit.observation.ObservationStore;
import org.geotoolkit.observation.ObservationWriter;
import org.geotoolkit.data.om.OMFeatureTypes;
import static org.geotoolkit.data.om.xml.XmlObservationStoreFactory.FILE_PATH;

import org.geotoolkit.observation.xml.*;
import org.geotoolkit.observation.xml.Process;
import org.geotoolkit.sampling.xml.SamplingFeature;
import org.geotoolkit.sos.netcdf.ExtractionResult;
import org.geotoolkit.sos.netcdf.ExtractionResult.ProcedureTree;
import org.geotoolkit.sos.netcdf.GeoSpatialBound;
import org.geotoolkit.sos.xml.SOSMarshallerPool;
import org.geotoolkit.storage.DataFileStore;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.util.GenericName;
import org.opengis.geometry.Geometry;
import org.opengis.observation.AnyFeature;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;
import org.opengis.observation.Phenomenon;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.temporal.TemporalObject;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class XmlObservationStore extends AbstractFeatureStore implements DataFileStore,ObservationStore {

    protected final GenericNameIndex<FeatureType> types;
    private static final QueryCapabilities capabilities = new DefaultQueryCapabilities(false);
    private final Path xmlFile;

    public XmlObservationStore(final ParameterValueGroup params) throws IOException {
        super(params);
        xmlFile = Paths.get((URI) params.parameter(FILE_PATH.getName().toString()).getValue());
        types = OMFeatureTypes.getFeatureTypes(IOUtilities.filenameWithoutExtension(xmlFile));
    }

    public XmlObservationStore(final Path xmlFile) {
        super(null);
        this.xmlFile = xmlFile;
        types = OMFeatureTypes.getFeatureTypes(IOUtilities.filenameWithoutExtension(xmlFile));
    }

    @Override
    public DataStoreFactory getProvider() {
        return DataStores.getFactoryById(XmlObservationStoreFactory.NAME);
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
            return FeatureStreams.subset(new XmlFeatureReader(xmlFile,sft), query);
        } catch (IOException | JAXBException ex) {
            throw new DataStoreException(ex);
        }
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
        final Object obj = readFile();
        if (obj instanceof ObservationCollection) {
            final ObservationCollection collection = (ObservationCollection)obj;
            for (Observation obs : collection.getMember()) {
                final org.geotoolkit.observation.xml.Process process = (Process)obs.getProcedure();
                names.add(NamesExt.create(process.getHref()));
            }

        } else if (obj instanceof Observation) {
            final Observation obs = (Observation)obj;
            final Process process = (Process)obs.getProcedure();
            names.add(NamesExt.create(process.getHref()));
        }
        return names;
    }

    @Override
    public ExtractionResult getResults() {
        return getResults(null);
    }

    @Override
    public ExtractionResult getResults(final String affectedSensorId, final List<String> sensorIDs) {
        getLogger().warning("XMLObservation store does not allow to override sensor ID");
        return getResults(sensorIDs);
    }

    @Override
    public ExtractionResult getResults(final List<String> sensorIDs) {
        final ExtractionResult result = new ExtractionResult();
        result.spatialBound.initBoundary();
        final Object obj = readFile();
        if (obj instanceof ObservationCollection) {
            final ObservationCollection collection = (ObservationCollection)obj;
            for (Observation obs : collection.getMember()) {
                final AbstractObservation o = (AbstractObservation)obs;
                final ProcedureTree procedure = new ProcedureTree(o.getProcedure().getHref(), "Component");
                if (sensorIDs == null || sensorIDs.contains(procedure.id)) {
                    if (!result.procedures.contains(procedure)) {
                        result.procedures.add(procedure);
                    }
                    final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                    final List<String> fields = XmlObservationUtils.getPhenomenonsFields(phenProp);
                    for (String field : fields) {
                        if (!result.fields.contains(field)) {
                            result.fields.add(field);
                        }
                    }
                    final Phenomenon phen = XmlObservationUtils.getPhenomenons(phenProp);
                    if (!result.phenomenons.contains(phen)) {
                        result.phenomenons.add(phen);
                    }
                    appendTime(obs.getSamplingTime(), result.spatialBound);
                    appendTime(obs.getSamplingTime(), procedure.spatialBound);
                    appendGeometry(obs.getFeatureOfInterest(), result.spatialBound);
                    appendGeometry(obs.getFeatureOfInterest(), procedure.spatialBound);
                    result.observations.add(o);
                }
            }

        } else if (obj instanceof AbstractObservation) {
            final AbstractObservation obs = (AbstractObservation)obj;
            final ProcedureTree procedure = new ProcedureTree(obs.getProcedure().getHref(), "Component");
            if (sensorIDs == null || sensorIDs.contains(procedure.id)) {
                result.observations .add(obs);
                final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
                result.fields.addAll(XmlObservationUtils.getPhenomenonsFields(phenProp));
                result.phenomenons.add(XmlObservationUtils.getPhenomenons(phenProp));
                result.procedures.add(procedure);
                appendTime(obs.getSamplingTime(), result.spatialBound);
                appendTime(obs.getSamplingTime(), procedure.spatialBound);
                appendGeometry(obs.getFeatureOfInterest(), result.spatialBound);
                appendGeometry(obs.getFeatureOfInterest(), procedure.spatialBound);
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
                final ProcedureTree procedure = new ProcedureTree(o.getProcedure().getHref(), "Component");

                if (!result.contains(procedure)) {
                    result.add(procedure);
                }
                final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                final List<String> fields = XmlObservationUtils.getPhenomenonsFields(phenProp);
                for (String field : fields) {
                    if (!procedure.fields.contains(field)) {
                        procedure.fields.add(field);
                    }
                }
                appendTime(obs.getSamplingTime(), procedure.spatialBound);
                appendGeometry(obs.getFeatureOfInterest(), procedure.spatialBound);
            }

        } else if (obj instanceof AbstractObservation) {
            final AbstractObservation obs = (AbstractObservation)obj;
            final ProcedureTree procedure = new ProcedureTree(obs.getProcedure().getHref(), "Component");

            final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
            procedure.fields.addAll(XmlObservationUtils.getPhenomenonsFields(phenProp));
            result.add(procedure);
            appendTime(obs.getSamplingTime(), procedure.spatialBound);
            appendGeometry(obs.getFeatureOfInterest(), procedure.spatialBound);
        }
        return result;
    }

    private void appendTime(final TemporalObject time, final GeoSpatialBound spatialBound) {
        if (time instanceof Instant) {
            final Instant i = (Instant) time;
            spatialBound.addDate(i.getDate());
        } else if (time instanceof Period) {
            final Period p = (Period) time;
            spatialBound.addDate(p.getBeginning().getDate());
            spatialBound.addDate(p.getEnding().getDate());
        }
    }

    private void appendGeometry(final AnyFeature feature, final GeoSpatialBound spatialBound){
        if (feature instanceof SamplingFeature) {
            final SamplingFeature sf = (SamplingFeature) feature;
            final Geometry geom = sf.getGeometry();
            final AbstractGeometry ageom;
            if (geom instanceof AbstractGeometry) {
                ageom = (AbstractGeometry)geom;
            } else if (geom != null) {
                ageom = GMLUtilities.getGMLFromISO(geom);
            } else {
                ageom = null;
            }
            spatialBound.addGeometry(ageom);
            spatialBound.addGeometry(ageom);
            extractBoundary(ageom, spatialBound);
            extractBoundary(ageom, spatialBound);
        }
    }

    private void extractBoundary(final AbstractGeometry geom, final GeoSpatialBound spatialBound) {
        if (geom instanceof Point) {
            final Point p = (Point) geom;
            if (p.getPos() != null) {
                spatialBound.addXCoordinate(p.getPos().getOrdinate(0));
                spatialBound.addYCoordinate(p.getPos().getOrdinate(1));
            }
        } else if (geom instanceof LineString) {
            final LineString ls = (LineString) geom;
            final Envelope env = ls.getBounds();
            if (env != null) {
                spatialBound.addXCoordinate(env.getMinimum(0));
                spatialBound.addXCoordinate(env.getMaximum(0));
                spatialBound.addYCoordinate(env.getMinimum(1));
                spatialBound.addYCoordinate(env.getMaximum(1));
            }
        } else if (geom instanceof Polygon) {
            final Polygon p = (Polygon) geom;
            AbstractRing ext = p.getExterior().getAbstractRing();
            // TODO
        }
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
            getLogger().log(Level.WARNING, "Error while reading  file", ex);
        }
        return null;
    }

    @Override
    public void close() throws DataStoreException {
        // do nothing
    }

    @Override
    public Set<String> getPhenomenonNames() {
        final Set<String> phenomenons = new HashSet<>();
        final Object obj = readFile();
        if (obj instanceof ObservationCollection) {
            final ObservationCollection collection = (ObservationCollection)obj;
            for (Observation obs : collection.getMember()) {
                final AbstractObservation o = (AbstractObservation)obs;
                final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                phenomenons.addAll(XmlObservationUtils.getPhenomenonsFields(phenProp));
            }

        } else if (obj instanceof AbstractObservation) {
            final AbstractObservation obs = (AbstractObservation)obj;
            final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
            phenomenons.addAll(XmlObservationUtils.getPhenomenonsFields(phenProp));
        }
        return phenomenons;
    }

    @Override
    public TemporalGeometricPrimitive getTemporalBounds() {
        final ExtractionResult result = new ExtractionResult();
        result.spatialBound.initBoundary();
        final Object obj = readFile();
        if (obj instanceof ObservationCollection) {
            final ObservationCollection collection = (ObservationCollection)obj;
            for (Observation obs : collection.getMember()) {
                appendTime(obs.getSamplingTime(), result.spatialBound);
            }

        } else if (obj instanceof AbstractObservation) {
            final AbstractObservation obs = (AbstractObservation)obj;
            appendTime(obs.getSamplingTime(), result.spatialBound);
        }
        return result.spatialBound.getTimeObject("2.0.0");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Path[] getDataFiles() throws DataStoreException {
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

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
package org.geotoolkit.observation.xml;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.gml.GMLUtilities;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.AbstractRing;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.LineString;
import org.geotoolkit.gml.xml.Point;
import org.geotoolkit.gml.xml.Polygon;
import org.geotoolkit.observation.AbstractObservationStore;
import static org.geotoolkit.observation.xml.XmlObservationStoreFactory.FILE_PATH;
import static org.geotoolkit.observation.xml.XmlObservationUtils.*;
import org.geotoolkit.sampling.xml.SamplingFeature;
import org.geotoolkit.sos.netcdf.ExtractionResult;
import org.geotoolkit.sos.netcdf.ExtractionResult.ProcedureTree;
import org.geotoolkit.sos.netcdf.GeoSpatialBound;
import org.geotoolkit.sos.xml.SOSMarshallerPool;
import org.geotoolkit.storage.DataFileStore;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.opengis.feature.type.Name;
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
public class XmlObservationStore extends AbstractObservationStore implements DataFileStore {

    private final File xmlFile;
    
    public XmlObservationStore(final ParameterValueGroup params) {
        super(params);
        xmlFile = (File) params.parameter(FILE_PATH.getName().toString()).getValue();
    }
    
    public XmlObservationStore(final File xmlFile) {
        super(null);
        this.xmlFile = xmlFile;
    }

    @Override
    public Set<Name> getProcedureNames() {
        final Set<Name> names = new HashSet<>();
        final Object obj = readFile();
        if (obj instanceof ObservationCollection) {
            final ObservationCollection collection = (ObservationCollection)obj;
            for (Observation obs : collection.getMember()) {
                final Process process = (Process)obs.getProcedure();
                names.add(new DefaultName(process.getHref()));
            }
            
        } else if (obj instanceof Observation) {
            final Observation obs = (Observation)obj;
            final Process process = (Process)obs.getProcedure();
            names.add(new DefaultName(process.getHref()));
        }
        return names;
    }

    @Override
    public ExtractionResult getResults() {
        return getResults(null);
    }
    
    @Override
    public ExtractionResult getResults(final String affectedSensorId, final List<String> sensorIDs) {
        LOGGER.warning("XMLObservation store does not allow to override sensor ID");
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
                    final List<String> fields = getPhenomenonsFields(phenProp);
                    for (String field : fields) {
                        if (!result.fields.contains(field)) {
                            result.fields.add(field);
                        }
                    }
                    final Phenomenon phen = getPhenomenons(phenProp);
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
                result.fields.addAll(getPhenomenonsFields(phenProp));
                result.phenomenons.add(getPhenomenons(phenProp));
                result.procedures.add(procedure);
                appendTime(obs.getSamplingTime(), result.spatialBound);
                appendTime(obs.getSamplingTime(), procedure.spatialBound);
                appendGeometry(obs.getFeatureOfInterest(), result.spatialBound);
                appendGeometry(obs.getFeatureOfInterest(), procedure.spatialBound);
            }
            
        }
        return result;
    }
    
    private void appendTime(final TemporalObject time, final GeoSpatialBound spatialBound) {
        if (time instanceof Instant) {
            final Instant i = (Instant) time;
            spatialBound.addDate(i.getPosition().getDate());
        } else if (time instanceof Period) {
            final Period p = (Period) time;
            spatialBound.addDate(p.getBeginning().getPosition().getDate());
            spatialBound.addDate(p.getEnding().getPosition().getDate());
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
        try {
            final Unmarshaller um = SOSMarshallerPool.getInstance().acquireUnmarshaller();
            Object obj = um.unmarshal(xmlFile);
            if (obj instanceof JAXBElement) {
                obj = ((JAXBElement)obj).getValue();
            }
            SOSMarshallerPool.getInstance().recycle(um);
            return obj;
        } catch (JAXBException ex) {
            LOGGER.log(Level.WARNING, "Error while reading  file", ex);
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
                phenomenons.addAll(getPhenomenonsFields(phenProp));
            }
            
        } else if (obj instanceof AbstractObservation) {
            final AbstractObservation obs = (AbstractObservation)obj;
            final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
            phenomenons.addAll(getPhenomenonsFields(phenProp));
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
    public File[] getDataFiles() throws DataStoreException {
        return new File[]{xmlFile};
    }
}

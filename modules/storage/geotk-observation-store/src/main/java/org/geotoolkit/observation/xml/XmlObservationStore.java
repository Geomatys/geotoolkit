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
import java.util.ArrayList;
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
import org.geotoolkit.sampling.xml.SamplingFeature;
import org.geotoolkit.sos.netcdf.ExtractionResult;
import org.geotoolkit.sos.netcdf.ExtractionResult.ProcedureTree;
import org.geotoolkit.sos.xml.SOSMarshallerPool;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.geotoolkit.swe.xml.v101.PhenomenonType;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Geometry;
import org.opengis.observation.AnyFeature;
import org.opengis.observation.CompositePhenomenon;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;
import org.opengis.observation.Phenomenon;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalObject;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class XmlObservationStore extends AbstractObservationStore {

    private final File xmlFile;
    
    public XmlObservationStore(final ParameterValueGroup params) {
        super(params);
        xmlFile = (File) params.parameter(FILE_PATH.getName().toString()).getValue();
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
        final ExtractionResult result = new ExtractionResult();
        result.spatialBound.initBoundary();
        final Object obj = readFile();
        if (obj instanceof ObservationCollection) {
            final ObservationCollection collection = (ObservationCollection)obj;
            result.observations.addAll(collection.getMember());
            for (Observation obs : collection.getMember()) {
                final AbstractObservation o = (AbstractObservation)obs;
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
                final ProcedureTree procedure = new ProcedureTree(o.getProcedure().getHref(), "Component");
                if (!result.procedures.contains(procedure)) {
                    result.procedures.add(procedure);
                }
                appendTime(obs.getSamplingTime(), result);
                appendGeometry(obs.getFeatureOfInterest(), result);
            }
            
        } else if (obj instanceof AbstractObservation) {
            final AbstractObservation obs = (AbstractObservation)obj;
            result.observations .add(obs);
            final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
            result.fields.addAll(getPhenomenonsFields(phenProp));
            result.phenomenons.add(getPhenomenons(phenProp));
            result.procedures.add(new ProcedureTree(obs.getProcedure().getHref(), "Component"));
            appendTime(obs.getSamplingTime(), result);
            appendGeometry(obs.getFeatureOfInterest(), result);
        }
        return result;
    }
    
    private List<String> getPhenomenonsFields(final PhenomenonProperty phenProp) {
        final List<String> results = new ArrayList<>();
        if (phenProp.getHref() != null) {
            results.add(phenProp.getHref());
        } else if (phenProp.getPhenomenon() instanceof CompositePhenomenon) {
            final CompositePhenomenon comp = (CompositePhenomenon) phenProp.getPhenomenon();
            for (Phenomenon phen : comp.getComponent()) {
                if (phen instanceof org.geotoolkit.swe.xml.Phenomenon) {
                    final org.geotoolkit.swe.xml.Phenomenon p = (org.geotoolkit.swe.xml.Phenomenon) phen;
                    results.add(p.getName());
                }
            }
        } else if (phenProp.getPhenomenon() instanceof org.geotoolkit.swe.xml.Phenomenon) {
            final org.geotoolkit.swe.xml.Phenomenon p = (org.geotoolkit.swe.xml.Phenomenon) phenProp.getPhenomenon();
            results.add(p.getName());
        }
        return results;
    }
    
    private Phenomenon getPhenomenons(final PhenomenonProperty phenProp) {
        if (phenProp.getHref() != null) {
            return new PhenomenonType(phenProp.getHref(), phenProp.getHref());
        } else if (phenProp.getPhenomenon() != null) {
            return phenProp.getPhenomenon();
            
        }
        return null;
    }
    
    private void appendTime(final TemporalObject time, final ExtractionResult result) {
        if (time instanceof Instant) {
            final Instant i = (Instant) time;
            result.spatialBound.addDate(i.getPosition().getDate());
        } else if (time instanceof Period) {
            final Period p = (Period) time;
            result.spatialBound.addDate(p.getBeginning().getPosition().getDate());
            result.spatialBound.addDate(p.getEnding().getPosition().getDate());
        }
    }
    
    private void appendGeometry(final AnyFeature feature, final ExtractionResult result){
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
            result.spatialBound.addGeometry(ageom);
            extractBoundary(ageom, result);
        }
    }
    
    private void extractBoundary(final AbstractGeometry geom, final ExtractionResult result) {
        if (geom instanceof Point) {
            final Point p = (Point) geom;
            if (p.getPos() != null) {
                result.spatialBound.addXCoordinate(p.getPos().getOrdinate(0));
                result.spatialBound.addYCoordinate(p.getPos().getOrdinate(1));
            }
        } else if (geom instanceof LineString) {
            final LineString ls = (LineString) geom;
            final Envelope env = ls.getBounds();
            if (env != null) {
                result.spatialBound.addXCoordinate(env.getMinimum(0));
                result.spatialBound.addXCoordinate(env.getMaximum(0));
                result.spatialBound.addYCoordinate(env.getMinimum(1));
                result.spatialBound.addYCoordinate(env.getMaximum(1));
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
}

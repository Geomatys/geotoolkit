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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.observation.ObservationReader;

import org.geotoolkit.observation.xml.*;
import org.geotoolkit.observation.xml.Process;
import org.geotoolkit.sos.netcdf.ExtractionResult;
import org.geotoolkit.sos.netcdf.GeoSpatialBound;
import org.geotoolkit.sos.xml.ObservationOffering;
import org.geotoolkit.sos.xml.ResponseModeType;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.temporal.TemporalObject;
import org.opengis.temporal.TemporalPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class XmlObservationReader implements ObservationReader {

    private final List<Object> xmlObjects;

    public XmlObservationReader(final List<Object> xmlObjects) {
        this.xmlObjects = xmlObjects;
    }

    @Override
    public Collection<String> getProcedureNames() throws DataStoreException {
        final Set<String> names = new HashSet<>();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final org.geotoolkit.observation.xml.Process process = (Process)obs.getProcedure();
                    names.add(process.getHref());
                }

            } else if (xmlObject instanceof Observation) {
                final Observation obs = (Observation)xmlObject;
                final Process process = (Process)obs.getProcedure();
                names.add(process.getHref());
            }
        }
        return names;
    }

     @Override
    public Collection<String> getProcedureNames(String sensorType) throws DataStoreException {
        // no filter yet
        return getProcedureNames();
    }

    @Override
    public Collection<String> getPhenomenonNames() throws DataStoreException {
        final Set<String> phenomenons = new HashSet<>();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                    phenomenons.addAll(XmlObservationUtils.getPhenomenonsFields(phenProp));
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
                phenomenons.addAll(XmlObservationUtils.getPhenomenonsFields(phenProp));
            }
        }
        return phenomenons;
    }

    @Override
    public Phenomenon getPhenomenon(String identifier, String version) throws DataStoreException {
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                    Phenomenon ph = XmlObservationUtils.getPhenomenons(phenProp);
                    if (ph instanceof org.geotoolkit.swe.xml.Phenomenon) {
                        org.geotoolkit.swe.xml.Phenomenon phe = (org.geotoolkit.swe.xml.Phenomenon) ph;
                        if (identifier.equals(phe.getName().getCode())) {
                            return ph;
                        }
                    }
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
                Phenomenon ph = XmlObservationUtils.getPhenomenons(phenProp);
                if (ph instanceof org.geotoolkit.swe.xml.Phenomenon) {
                    org.geotoolkit.swe.xml.Phenomenon phe = (org.geotoolkit.swe.xml.Phenomenon) ph;
                    if (identifier.equals(phe.getName().getCode())) {
                        return ph;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Collection<Phenomenon> getPhenomenons(final String version) throws DataStoreException {
        final Set<Phenomenon> phenomenons = new HashSet<>();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                    phenomenons.add(XmlObservationUtils.getPhenomenons(phenProp));
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
                phenomenons.add(XmlObservationUtils.getPhenomenons(phenProp));
            }
        }
        return phenomenons;
    }

    @Override
    public Collection<String> getProceduresForPhenomenon(final String observedProperty) throws DataStoreException {
        final Set<String> procedures = new HashSet<>();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                    final List<String> phen = XmlObservationUtils.getPhenomenonsFields(phenProp);
                    if (phen.contains(observedProperty)) {
                        procedures.add(o.getProcedure().getHref());
                    }
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
                final List<String> phen = XmlObservationUtils.getPhenomenonsFields(phenProp);
                if (phen.contains(observedProperty)) {
                    procedures.add(obs.getProcedure().getHref());
                }
            }
        }
        return procedures;
    }

    @Override
    public Collection<String> getPhenomenonsForProcedure(final String sensorID) throws DataStoreException {
        final Set<String> phenomenons = new HashSet<>();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    if (o.getProcedure().getHref().equals(sensorID)) {
                        final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                        phenomenons.addAll(XmlObservationUtils.getPhenomenonsFields(phenProp));
                    }
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                if (obs.getProcedure().getHref().equals(sensorID)) {
                    final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
                    phenomenons.addAll(XmlObservationUtils.getPhenomenonsFields(phenProp));
                }
            }
        }
        return phenomenons;
    }

    @Override
    public TemporalGeometricPrimitive getTimeForProcedure(final String version, final String sensorID) throws DataStoreException {
        final ExtractionResult result = new ExtractionResult();
        result.spatialBound.initBoundary();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation) obs;
                    if (o.getProcedure().getHref().equals(sensorID)) {
                        appendTime(obs.getSamplingTime(), result.spatialBound);
                    }
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                if (obs.getProcedure().getHref().equals(sensorID)) {
                    appendTime(obs.getSamplingTime(), result.spatialBound);
                }
            }
        }
        return result.spatialBound.getTimeObject("2.0.0");
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

    @Override
    public boolean existPhenomenon(final String phenomenonName) throws DataStoreException {
        return getPhenomenonNames().contains(phenomenonName);
    }

    @Override
    public Collection<String> getFeatureOfInterestNames() throws DataStoreException {
        final Set<String> featureOfInterest = new HashSet<>();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final FeatureProperty foiProp = o.getPropertyFeatureOfInterest();
                    featureOfInterest.add(XmlObservationUtils.getFOIName(foiProp));
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                final FeatureProperty foiProp = obs.getPropertyFeatureOfInterest();
                featureOfInterest.add(XmlObservationUtils.getFOIName(foiProp));
            }
        }
        return featureOfInterest;
    }

    @Override
    public Collection<SamplingFeature> getFeatureOfInterestForProcedure(String sensorID, String version) throws DataStoreException {
        final Set<SamplingFeature> featureOfInterest = new HashSet<>();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    if (o.getProcedure().getHref().equals(sensorID)) {
                        final FeatureProperty foiProp = o.getPropertyFeatureOfInterest();
                        if (foiProp != null && foiProp.getAbstractFeature() != null) {
                            featureOfInterest.add((SamplingFeature) foiProp.getAbstractFeature());
                        }
                    }
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                if (obs.getProcedure().getHref().equals(sensorID)) {
                    final FeatureProperty foiProp = obs.getPropertyFeatureOfInterest();
                    if (foiProp != null && foiProp.getAbstractFeature() != null) {
                        featureOfInterest.add((SamplingFeature) foiProp.getAbstractFeature());
                    }
                }
            }
        }
        return featureOfInterest;
    }

    @Override
    public SamplingFeature getFeatureOfInterest(final String samplingFeatureName, final String version) throws DataStoreException {
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final FeatureProperty foiProp = o.getPropertyFeatureOfInterest();
                    if (foiProp != null && foiProp.getAbstractFeature() != null && foiProp.getAbstractFeature().getId() != null &&
                            foiProp.getAbstractFeature().getId().equals(samplingFeatureName)) {
                        return (SamplingFeature) foiProp.getAbstractFeature();
                    }
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                final FeatureProperty foiProp = obs.getPropertyFeatureOfInterest();
                if (foiProp != null && foiProp.getAbstractFeature() != null && foiProp.getAbstractFeature().getId() != null &&
                        foiProp.getAbstractFeature().getId().equals(samplingFeatureName)) {
                    return (SamplingFeature) foiProp.getAbstractFeature();
                }
            }
        }
        return null;
    }

    @Override
    public TemporalPrimitive getFeatureOfInterestTime(final String samplingFeatureName, final String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this implementation.");
    }

    @Override
    public Observation getObservation(final String identifier, final QName resultModel, final ResponseModeType mode, final String version) throws DataStoreException {
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    if (o.getId().equals(identifier)) {
                        return o;
                    }
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation o = (AbstractObservation)xmlObject;
                if (o.getId().equals(identifier)) {
                    return o;
                }
            }
        }
        return null;
    }

    @Override
    public Object getResult(final String identifier, final QName resultModel, final String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this implementation.");
    }

    @Override
    public boolean existProcedure(final String href) throws DataStoreException {
        return getProcedureNames().contains(href);
    }

    @Override
    public org.opengis.observation.Process getProcess(String identifier, String version) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getNewObservationId() throws DataStoreException {
        throw new DataStoreException("Not supported in this implementation.");
    }

    @Override
    public TemporalPrimitive getEventTime(String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this implementation.");
    }

    @Override
    public List<ResponseModeType> getResponseModes() throws DataStoreException {
        return Arrays.asList(ResponseModeType.INLINE);
    }

    @Override
    public List<String> getResponseFormats() throws DataStoreException {
        return Arrays.asList(XmlObservationUtils.RESPONSE_FORMAT_V100, XmlObservationUtils.RESPONSE_FORMAT_V200);
    }

    @Override
    public AbstractGeometry getSensorLocation(final String sensorID, final String version) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getInfos() {
        return "O&M xml file Reader 4.x";
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public Collection<String> getOfferingNames(final String version) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in XML observation reader.");
    }

    @Override
    public ObservationOffering getObservationOffering(final String offeringName, final String version) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in XML observation reader.");
    }

    @Override
    public List<ObservationOffering> getObservationOfferings(final List<String> offeringNames, final String version) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in XML observation reader.");
    }

    @Override
    public List<ObservationOffering> getObservationOfferings(final String version) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in XML observation reader.");
    }

    @Override
    public Observation getTemplateForProcedure(final String procedure, final String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this implementation.");
    }

    @Override
    public Collection<String> getOfferingNames(String version, String sensorType) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in XML observation reader.");
    }

    @Override
    public List<ObservationOffering> getObservationOfferings(String version, String sensorType) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in XML observation reader.");
    }
}

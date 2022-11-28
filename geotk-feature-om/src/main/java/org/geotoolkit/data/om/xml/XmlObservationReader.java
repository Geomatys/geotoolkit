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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.observation.OMUtils;
import org.geotoolkit.observation.model.OMEntity;
import org.geotoolkit.observation.ObservationReader;
import static org.geotoolkit.observation.ObservationReader.ENTITY_TYPE;
import static org.geotoolkit.observation.ObservationReader.IDENTIFIER;
import static org.geotoolkit.observation.ObservationReader.SENSOR_TYPE;
import static org.geotoolkit.observation.ObservationReader.SOS_VERSION;

import org.geotoolkit.observation.xml.*;
import org.geotoolkit.observation.xml.Process;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.sos.xml.ObservationOffering;
import org.geotoolkit.sos.xml.ResponseModeType;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.temporal.TemporalGeometricPrimitive;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getEntityNames(final Map<String, Object> hints) throws DataStoreException {
        OMEntity entityType = (OMEntity) hints.get(ENTITY_TYPE);
        String sensorType   = (String) hints.get(SENSOR_TYPE);
        String version      = (String) hints.get(SOS_VERSION);
        switch (entityType) {
            case FEATURE_OF_INTEREST: return getFeatureOfInterestNames();
            case OBSERVED_PROPERTY:   return getPhenomenonNames();
            case PROCEDURE:           return getProcedureNames(sensorType);
            case LOCATION:            throw new DataStoreException("not implemented yet.");
            case HISTORICAL_LOCATION: throw new DataStoreException("not implemented yet.");
            case OFFERING:            throw new DataStoreException("offerings are not handled in XML observation reader.");
            case OBSERVATION:         throw new DataStoreException("not implemented yet.");
            case RESULT:              throw new DataStoreException("not implemented yet.");
            default: throw new DataStoreException("unexpected entity type:" + entityType);
        }
    }

    private Collection<String> getProcedureNames(String sensorType) throws DataStoreException {
        // no filter yet
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

    private Collection<String> getPhenomenonNames() throws DataStoreException {
        final Set<String> phenomenons = new HashSet<>();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                    phenomenons.addAll(OMUtils.getPhenomenonsFields(phenProp));
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
                phenomenons.addAll(OMUtils.getPhenomenonsFields(phenProp));
            }
        }
        return phenomenons;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existEntity(final Map<String, Object> hints) throws DataStoreException {
        OMEntity entityType = (OMEntity) hints.get(ENTITY_TYPE);
        if (entityType == null) {
            throw new DataStoreException("Missing entity type parameter");
        }
        String identifier   = (String) hints.get(IDENTIFIER);
        String sensorType   = (String) hints.get(SENSOR_TYPE);
        switch (entityType) {
            case FEATURE_OF_INTEREST: return getFeatureOfInterestNames().contains(identifier);
            case OBSERVED_PROPERTY:   return getPhenomenonNames().contains(identifier);
            case PROCEDURE:           return getProcedureNames(null).contains(identifier);
            case LOCATION:            throw new DataStoreException("not implemented yet.");
            case HISTORICAL_LOCATION: throw new DataStoreException("not implemented yet.");
            case OFFERING:            throw new DataStoreException("offerings are not handled in XML observation reader.");
            case OBSERVATION:         throw new DataStoreException("not implemented yet.");
            case RESULT:              throw new DataStoreException("not implemented yet.");
            default: throw new DataStoreException("unexpected entity type:" + entityType);
        }
    }

    @Override
    public Collection<Phenomenon> getPhenomenons(final Map<String, Object> hints) throws DataStoreException {
        final Set<Phenomenon> phenomenons = new HashSet<>();
        String version       = (String) hints.get(SOS_VERSION);
        Object identifierVal = hints.get(IDENTIFIER);
        List<String> identifiers = new ArrayList<>();
        if (identifierVal instanceof Collection) {
            identifiers.addAll((Collection<? extends String>) identifierVal);
        } else if (identifierVal instanceof String) {
            identifiers.add((String) identifierVal);
        }
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                    final Phenomenon ph = OMUtils.getPhenomenon(phenProp);
                    if (ph instanceof org.geotoolkit.swe.xml.Phenomenon) {
                        org.geotoolkit.swe.xml.Phenomenon phe = (org.geotoolkit.swe.xml.Phenomenon) ph;
                        if (identifiers.isEmpty() || identifiers.contains(phe.getName().getCode())) {
                            phenomenons.add(ph);
                        }
                    }
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
                final Phenomenon ph = OMUtils.getPhenomenon(phenProp);
                if (ph instanceof org.geotoolkit.swe.xml.Phenomenon) {
                    org.geotoolkit.swe.xml.Phenomenon phe = (org.geotoolkit.swe.xml.Phenomenon) ph;
                    if (identifiers.isEmpty() || identifiers.contains(phe.getName().getCode())) {
                        phenomenons.add(ph);
                    }
                }
            }
        }
        return phenomenons;
    }

    @Override
    public TemporalGeometricPrimitive getTimeForProcedure(final String version, final String sensorID) throws DataStoreException {
        final ObservationDataset result = new ObservationDataset();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation) obs;
                    if (sensorID.equals(o.getProcedure().getHref())) {
                        result.spatialBound.addTime(obs.getSamplingTime());
                    }
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                if (sensorID.equals(obs.getProcedure().getHref())) {
                    result.spatialBound.addTime(obs.getSamplingTime());
                }
            }
        }
        return result.spatialBound.getTimeObject("2.0.0");
    }

    private Collection<String> getFeatureOfInterestNames() throws DataStoreException {
        final Set<String> featureOfInterest = new HashSet<>();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection) {
                final ObservationCollection collection = (ObservationCollection)xmlObject;
                for (Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final FeatureProperty foiProp = o.getPropertyFeatureOfInterest();
                    featureOfInterest.add(OMUtils.getFOIId(foiProp));
                }

            } else if (xmlObject instanceof AbstractObservation) {
                final AbstractObservation obs = (AbstractObservation)xmlObject;
                final FeatureProperty foiProp = obs.getPropertyFeatureOfInterest();
                featureOfInterest.add(OMUtils.getFOIId(foiProp));
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
    public org.opengis.observation.Process getProcess(String identifier, String version) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TemporalPrimitive getEventTime(String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this implementation.");
    }

    @Override
    public AbstractGeometry getSensorLocation(final String sensorID, final String version) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet in this implementation.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Date, AbstractGeometry> getSensorLocations(String sensorID, String version) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet in this implementation.");
    }

    @Override
    public void destroy() {
        // do nothing
    }

   @Override
    public List<ObservationOffering> getObservationOfferings(final Map<String, Object> hints) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in XML observation reader.");
    }

    @Override
    public Observation getTemplateForProcedure(final String procedure, final String version) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this implementation.");
    }
}

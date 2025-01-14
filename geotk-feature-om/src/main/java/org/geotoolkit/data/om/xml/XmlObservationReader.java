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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.observation.model.OMEntity;
import org.geotoolkit.observation.ObservationReader;

import org.geotoolkit.observation.xml.*;
import org.geotoolkit.observation.xml.Process;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.model.Offering;
import org.geotoolkit.observation.model.Procedure;
import org.geotoolkit.observation.model.ResponseMode;
import static org.geotoolkit.observation.model.ObservationTransformUtils.toModel;
import org.geotoolkit.observation.query.IdentifierQuery;
import org.geotoolkit.sos.xml.OMXMLUtils;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.locationtech.jts.geom.Geometry;
import org.geotoolkit.observation.model.Observation;
import org.opengis.observation.ObservationCollection;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.SamplingFeature;
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
    public Collection<String> getEntityNames(final OMEntity entityType) throws DataStoreException {
        if (entityType == null) {
            throw new DataStoreException("Missing entity type parameter");
        }
        switch (entityType) {
            case FEATURE_OF_INTEREST: return getFeatureOfInterestNames();
            case OBSERVED_PROPERTY:   return getPhenomenonNames();
            case PROCEDURE:           return getProcedureNames();
            case LOCATION:            throw new DataStoreException("not implemented yet.");
            case HISTORICAL_LOCATION: throw new DataStoreException("not implemented yet.");
            case OFFERING:            throw new DataStoreException("offerings are not handled in XML observation reader.");
            case OBSERVATION:         throw new DataStoreException("not implemented yet.");
            case RESULT:              throw new DataStoreException("not implemented yet.");
            default: throw new DataStoreException("unexpected entity type:" + entityType);
        }
    }

    private Collection<String> getProcedureNames() throws DataStoreException {
        // no filter yet
        final Set<String> names = new HashSet<>();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection collection) {
                for (org.opengis.observation.Observation obs : collection.getMember()) {
                    final org.geotoolkit.observation.xml.Process process = (Process)obs.getProcedure();
                    names.add(process.getHref());
                }

            } else if (xmlObject instanceof AbstractObservation obs) {
                final Process process = (Process)obs.getProcedure();
                names.add(process.getHref());
            }
        }
        return names;
    }

    private Collection<String> getPhenomenonNames() throws DataStoreException {
        final Set<String> phenomenons = new HashSet<>();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection collection) {
                for (org.opengis.observation.Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                    phenomenons.addAll(OMXMLUtils.getPhenomenonsFields(phenProp));
                }

            } else if (xmlObject instanceof AbstractObservation obs) {
                final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
                phenomenons.addAll(OMXMLUtils.getPhenomenonsFields(phenProp));
            }
        }
        return phenomenons;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existEntity(final IdentifierQuery query) throws DataStoreException {
        OMEntity entityType = query.getEntityType();
        if (entityType == null) {
            throw new DataStoreException("Missing entity type parameter");
        }
        String identifier   = query.getIdentifier();
        switch (entityType) {
            case FEATURE_OF_INTEREST: return getFeatureOfInterestNames().contains(identifier);
            case OBSERVED_PROPERTY:   return getPhenomenonNames().contains(identifier);
            case PROCEDURE:           return getProcedureNames().contains(identifier);
            case LOCATION:            throw new DataStoreException("not implemented yet.");
            case HISTORICAL_LOCATION: throw new DataStoreException("not implemented yet.");
            case OFFERING:            throw new DataStoreException("offerings are not handled in XML observation reader.");
            case OBSERVATION:         throw new DataStoreException("not implemented yet.");
            case RESULT:              throw new DataStoreException("not implemented yet.");
            default: throw new DataStoreException("unexpected entity type:" + entityType);
        }
    }

    @Override
    public Phenomenon getPhenomenon(final String identifier) throws DataStoreException {
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection collection) {
                for (org.opengis.observation.Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                    final org.geotoolkit.observation.model.Phenomenon ph = toModel(phenProp);
                    if (identifier.equals(ph.getId())) {
                        return ph;
                    }
                }

            } else if (xmlObject instanceof AbstractObservation obs) {
                final PhenomenonProperty phenProp = obs.getPropertyObservedProperty();
                final org.geotoolkit.observation.model.Phenomenon ph = toModel(phenProp);
                if (identifier.equals(ph.getId())) {
                    return ph;
                }
            }
        }
        return null;
    }

    @Override
    public TemporalPrimitive getProcedureTime(final String sensorID) throws DataStoreException {
        final ObservationDataset result = new ObservationDataset();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection collection) {
                for (org.opengis.observation.Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation) obs;
                    if (sensorID.equals(o.getProcedure().getHref())) {
                        result.spatialBound.addTime(obs.getSamplingTime());
                    }
                }

            } else if (xmlObject instanceof AbstractObservation obs) {
                if (sensorID.equals(obs.getProcedure().getHref())) {
                    result.spatialBound.addTime(obs.getSamplingTime());
                }
            }
        }
        return result.spatialBound.getTimeObject();
    }

    private Collection<String> getFeatureOfInterestNames() throws DataStoreException {
        final Set<String> featureOfInterest = new HashSet<>();
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection collection) {
                for (org.opengis.observation.Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final FeatureProperty foiProp = o.getPropertyFeatureOfInterest();
                    featureOfInterest.add(OMXMLUtils.getFOIId(foiProp));
                }

            } else if (xmlObject instanceof AbstractObservation obs) {
                final FeatureProperty foiProp = obs.getPropertyFeatureOfInterest();
                featureOfInterest.add(OMXMLUtils.getFOIId(foiProp));
            }
        }
        return featureOfInterest;
    }

    @Override
    public SamplingFeature getFeatureOfInterest(final String identifier) throws DataStoreException {
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection collection) {
                for (org.opengis.observation.Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    final FeatureProperty foiProp = o.getPropertyFeatureOfInterest();
                    if (foiProp != null && foiProp.getAbstractFeature() != null && foiProp.getAbstractFeature().getId() != null &&
                            foiProp.getAbstractFeature().getId().equals(identifier)) {
                        return toModel((org.opengis.observation.sampling.SamplingFeature) foiProp.getAbstractFeature());
                    }
                }

            } else if (xmlObject instanceof AbstractObservation obs) {
                final FeatureProperty foiProp = obs.getPropertyFeatureOfInterest();
                if (foiProp != null && foiProp.getAbstractFeature() != null && foiProp.getAbstractFeature().getId() != null &&
                        foiProp.getAbstractFeature().getId().equals(identifier)) {
                    return toModel((org.opengis.observation.sampling.SamplingFeature) foiProp.getAbstractFeature());
                }
            }
        }
        return null;
    }

    @Override
    public TemporalPrimitive getFeatureOfInterestTime(final String samplingFeatureName) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this implementation.");
    }

    @Override
    public Observation getObservation(final String identifier, final QName resultModel, final ResponseMode mode) throws DataStoreException {
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection collection) {
                for (org.opengis.observation.Observation obs : collection.getMember()) {
                    final AbstractObservation o = (AbstractObservation)obs;
                    if (o.getId().equals(identifier)) {
                        return toModel(o);
                    }
                }

            } else if (xmlObject instanceof AbstractObservation obs) {
                if (obs.getId().equals(identifier)) {
                    return toModel(obs);
                }
            }
        }
        return null;
    }

    @Override
    public Procedure getProcess(String identifier) throws DataStoreException {
        for (Object xmlObject : xmlObjects) {
            if (xmlObject instanceof ObservationCollection collection) {
                for (org.opengis.observation.Observation obs : collection.getMember()) {
                    if (obs.getProcedure() instanceof org.geotoolkit.observation.xml.Process proc) {
                        if (identifier.equals(proc.getHref())) {
                            return new Procedure(proc.getHref());
                        }
                    }
                }

            } else if (xmlObject instanceof Observation obs) {
                if (obs.getProcedure() instanceof org.geotoolkit.observation.xml.Process proc) {
                    if (identifier.equals(proc.getHref())) {
                        return new Procedure(proc.getHref());
                    }
                }
            }
        }
        return null;
    }

    @Override
    public TemporalPrimitive getEventTime() throws DataStoreException {
        throw new DataStoreException("Not supported yet in this implementation.");
    }

    @Override
    public Geometry getSensorLocation(final String sensorID) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet in this implementation.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Date, Geometry> getSensorLocations(String sensorID) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet in this implementation.");
    }

    @Override
    public void destroy() {
        // do nothing
    }

   @Override
    public Offering getObservationOffering(String identifier) throws DataStoreException {
        throw new DataStoreException("offerings are not handled in XML observation reader.");
    }

    @Override
    public Observation getTemplateForProcedure(final String procedure) throws DataStoreException {
        throw new DataStoreException("Not supported yet in this implementation.");
    }
}

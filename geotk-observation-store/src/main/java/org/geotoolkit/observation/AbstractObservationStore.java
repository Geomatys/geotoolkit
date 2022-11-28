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
package org.geotoolkit.observation;

import java.util.ArrayList;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.model.OMEntity;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.sis.metadata.ModifiableMetadata;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.gml.xml.AbstractGeometry;
import static org.geotoolkit.observation.AbstractObservationStoreFactory.*;
import static org.geotoolkit.observation.OMUtils.OBSERVATION_QNAME;
import static org.geotoolkit.observation.ObservationReader.ENTITY_TYPE;
import static org.geotoolkit.observation.model.ObservationTransformUtils.*;
import org.geotoolkit.observation.model.ProcedureDataset;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.xml.AbstractObservation;
import org.geotoolkit.observation.xml.Process;
import org.geotoolkit.sos.xml.ResponseModeType;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.geotoolkit.util.NamesExt;
import org.opengis.metadata.Metadata;
import org.opengis.observation.Observation;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.util.GenericName;

/**
 * Basic implementation of Observation store.
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractObservationStore extends DataStore implements ObservationStore, Resource {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.observation");

    protected final ParameterValueGroup parameters;

    protected AbstractObservationStore(final ParameterValueGroup params) {
        this.parameters = params;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.ofNullable(parameters);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationFilterReader getFilter() throws DataStoreException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationWriter getWriter() throws DataStoreException {
        return null;
    }

    protected Map<String, Object> getBasicProperties() {
        final Map<String,Object> properties = new HashMap<>();
        extractParameter(parameters, PHENOMENON_ID_BASE, properties);
        extractParameter(parameters, OBSERVATION_ID_BASE, properties);
        extractParameter(parameters, OBSERVATION_TEMPLATE_ID_BASE, properties);
        extractParameter(parameters, SENSOR_ID_BASE, properties);
        return properties;
    }

    /**
     * Utility method to extract a a parameter value (if its present) and put it
     * in a Map.
     *
     * @param params Configuration parameters.
     * @param param The param descriptor to look for.
     * @param properties The trget map where to put the value.
     */
    protected static void extractParameter(final ParameterValueGroup params, ParameterDescriptor param, final Map<String,Object> properties) {
        try {
            String name = param.getName().toString();
            final Object value = (String) params.parameter(name).getValue();
            if (value != null) {
                properties.put(name, value);
            }
        } catch (ParameterNotFoundException ex) {}
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<GenericName> getProcedureNames() throws DataStoreException {
        final Set<GenericName> names = new HashSet<>();
        for (String process : getReader().getEntityNames(Collections.singletonMap(ENTITY_TYPE, OMEntity.OBSERVED_PROPERTY))) {
            names.add(NamesExt.create(process));
        }
        return names;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<String> getPhenomenonNames() throws DataStoreException {
        return new HashSet(getReader().getEntityNames(Collections.singletonMap(ENTITY_TYPE, OMEntity.OBSERVED_PROPERTY)));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationDataset getResults() throws DataStoreException {
        return getResults(null, null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationDataset getResults(final List<String> sensorIds) throws DataStoreException {
        return getResults(null, sensorIds);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationDataset getResults(String affectedSensorId, List<String> sensorIDs) throws DataStoreException {
        if (affectedSensorId != null) {
            LOGGER.warning("This ObservationStore does not allow to override sensor ID");
        }

        final ObservationDataset result = new ObservationDataset();
        result.spatialBound.initBoundary();

        final List<Observation> observations = getAllObservations(sensorIDs);
        for (Observation obs : observations) {
            final AbstractObservation o = (AbstractObservation) obs;
            final Process proc          =  o.getProcedure();
            final ProcedureDataset procedure = new ProcedureDataset(proc.getHref(), proc.getName(), proc.getDescription(), "Component", "timeseries", new ArrayList<>(), null);
            if (sensorIDs == null || sensorIDs.contains(procedure.getId())) {
                if (!result.procedures.contains(procedure)) {
                    result.procedures.add(procedure);
                }
                final PhenomenonProperty phenProp = o.getPropertyObservedProperty();
                final List<String> fields = OMUtils.getPhenomenonsFields(phenProp);
                for (String field : fields) {
                    if (!procedure.fields.contains(field)) {
                        procedure.fields.add(field);
                    }
                }
                final Phenomenon phen = toModel(OMUtils.getPhenomenon(phenProp));
                if (!result.phenomenons.contains(phen)) {
                    result.phenomenons.add(phen);
                }
                result.spatialBound.appendLocation(o.getSamplingTime(), o.getFeatureOfInterest());
                procedure.spatialBound.appendLocation(o.getSamplingTime(), o.getFeatureOfInterest());
                procedure.spatialBound.getHistoricalLocations().putAll(getSensorLocations(o.getProcedure().getHref(), "2.0.0"));
                result.observations.add(toModel(o));
            }
        }
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ProcedureDataset> getProcedures() throws DataStoreException {
        final List<ProcedureDataset> result = new ArrayList<>();

        final List<Observation> observations = getAllObservations(new ArrayList<>());
        for (Observation obs : observations) {
            final AbstractObservation o = (AbstractObservation)obs;
            final Process proc          =  o.getProcedure();
            final ProcedureDataset procedure = new ProcedureDataset(proc.getHref(), proc.getName(), proc.getDescription(), "Component", "timeseries", new ArrayList<>(), null);

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
            procedure.spatialBound.getHistoricalLocations().putAll(getSensorLocations(proc.getHref(), "2.0.0"));
        }
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalGeometricPrimitive getTemporalBounds() throws DataStoreException {
        final ObservationDataset result = new ObservationDataset();
        result.spatialBound.initBoundary();
        final List<Observation> observations = getAllObservations(new ArrayList<>());
        for (Observation obs :observations) {
            result.spatialBound.addTime(obs.getSamplingTime());
        }
        return result.spatialBound.getTimeObject("2.0.0");
    }

    /**
     * Return A list of the observations associated with the specified sensors
     * or all the observations if no sensor identifier is supplied.
     *
     * @param sensorIDs A filter on sensor identifiers. Can be empty.
     *
     * @return A list of observation.
     * @throws DataStoreException If the observation extraction fails.
     */
    protected List<Observation> getAllObservations(final List<String> sensorIDs) throws DataStoreException {
        final ObservationFilterReader currentFilter = (ObservationFilterReader) getFilter();
        final Map<String, Object> hints = new HashMap<>();
        hints.put("responseMode", ResponseModeType.INLINE);
        hints.put("resultModel", OBSERVATION_QNAME);
        currentFilter.init(OMEntity.OBSERVATION, hints);
        currentFilter.setProcedure(sensorIDs);
        return currentFilter.getObservations();
    }

    /**
     * return the locations list of a sensor over the time.
     *
     * @param sensorID Sensor identifier.
     * @param version SOS version used to determine the xml binding.
     *
     * @return A map of time/location.
     * @throws DataStoreException
     */
    protected Map<Date, AbstractGeometry> getSensorLocations(String sensorID, String version) throws DataStoreException {
        return Collections.EMPTY_MAP;
    }


    protected static Metadata buildMetadata(final String name) {
        final DefaultMetadata metadata = new DefaultMetadata();
        final DefaultDataIdentification identification = new DefaultDataIdentification();
        final NamedIdentifier identifier = new NamedIdentifier(new DefaultIdentifier(name));
        final DefaultCitation citation = new DefaultCitation(name);
        citation.setIdentifiers(Collections.singleton(identifier));
        identification.setCitation(citation);
        metadata.setIdentificationInfo(Collections.singleton(identification));
        metadata.transitionTo(ModifiableMetadata.State.FINAL);
        return metadata;
    }
}

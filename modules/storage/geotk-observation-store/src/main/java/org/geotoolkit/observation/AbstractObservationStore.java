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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.logging.Logging;
import static org.geotoolkit.observation.AbstractObservationStoreFactory.*;
import static org.geotoolkit.observation.ObservationReader.ENTITY_TYPE;
import org.geotoolkit.sos.netcdf.ExtractionResult;
import org.geotoolkit.util.NamesExt;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractObservationStore extends DataStore implements ObservationStore, Resource {

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.observation");

    protected final ParameterValueGroup parameters;

    protected AbstractObservationStore(final ParameterValueGroup params) {
        this.parameters = params;
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.ofNullable(parameters);
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

    protected Map<String, Object> getBasicProperties() {
        final Map<String,Object> properties = new HashMap<>();
        extractParameter(parameters, PHENOMENON_ID_BASE, properties);
        extractParameter(parameters, OBSERVATION_ID_BASE, properties);
        extractParameter(parameters, OBSERVATION_TEMPLATE_ID_BASE, properties);
        extractParameter(parameters, SENSOR_ID_BASE, properties);
        return properties;
    }

    /**
     * Utility method to extract a a parameter value (if its present) and put it in a Map.
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

    @Override
    public Set<GenericName> getProcedureNames() {
        final Set<GenericName> names = new HashSet<>();
        try {
            for (String process : getReader().getEntityNames(Collections.singletonMap(ENTITY_TYPE, OMEntity.OBSERVED_PROPERTY))) {
                names.add(NamesExt.create(process));
            }
        } catch (DataStoreException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return names;
    }

    @Override
    public Set<String> getPhenomenonNames() {
        try {
            return new HashSet(getReader().getEntityNames(Collections.singletonMap(ENTITY_TYPE, OMEntity.OBSERVED_PROPERTY)));
        } catch (DataStoreException ex) {
            LOGGER.log(Level.WARNING, "Error while retrieving phenomenons", ex);
        }
        return new HashSet<>();
    }

    @Override
    public ExtractionResult getResults() throws DataStoreException {
        return getResults(null, null, new HashSet<>(), new HashSet<>());
    }

    @Override
    public ExtractionResult getResults(final List<String> sensorIds) throws DataStoreException {
        return getResults(null, sensorIds, new HashSet<>(), new HashSet<>());
    }

    @Override
    public ExtractionResult getResults(String affectedSensorID, List<String> sensorIds) throws DataStoreException {
        return getResults(affectedSensorID, sensorIds, new HashSet<>(), new HashSet<>());
    }

}

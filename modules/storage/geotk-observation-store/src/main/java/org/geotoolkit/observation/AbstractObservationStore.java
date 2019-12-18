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

import java.util.Optional;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.logging.Logging;
import org.opengis.parameter.ParameterValueGroup;

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
        throw new UnsupportedOperationException("Filtering is not supported on this observation store.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationWriter getWriter() {
        throw new UnsupportedOperationException("Writing is not supported on this observation store.");
    }
}

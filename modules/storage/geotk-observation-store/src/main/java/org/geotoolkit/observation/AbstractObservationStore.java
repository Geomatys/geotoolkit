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

import org.apache.sis.storage.DataStoreException;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractObservationStore extends ObservationStore {
    
    protected final ParameterValueGroup parameters;
    
    protected AbstractObservationStore(final ParameterValueGroup params) {
        this.parameters = params;
    }
    
    @Override
    public Metadata getMetadata() throws DataStoreException {
        return null;
    }

    @Override
    public ParameterValueGroup getConfiguration() {
        return parameters;
    }
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.data.memory;

import java.util.Collection;
import java.util.Optional;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.WritableAggregate;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class InMemoryStore extends DataStore implements WritableAggregate {

    private final InMemoryAggregate aggregate = new InMemoryAggregate();

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.empty();
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }

    @Override
    public void close() throws DataStoreException {
    }

    @Override
    public Resource add(Resource resource) throws DataStoreException {
        return aggregate.add(resource);
    }

    @Override
    public void remove(Resource resource) throws DataStoreException {
        aggregate.remove(resource);
    }

    @Override
    public Collection<? extends Resource> components() throws DataStoreException {
        return aggregate.components();
    }

}

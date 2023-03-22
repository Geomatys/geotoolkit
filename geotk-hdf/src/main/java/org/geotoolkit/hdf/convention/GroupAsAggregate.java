/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.hdf.convention;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.hdf.api.Group;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;

/**
 * Decorate as HDF Group as Aggregate.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GroupAsAggregate implements Aggregate {

    private final Group group;
    private final List<Resource> components = new ArrayList<>();

    public GroupAsAggregate(Group group) {
        this.group = group;
    }

    /**
     * List of modifiable internal resources.
     */
    public List<Resource> resources() {
        return components;
    }

    @Override
    public Collection<? extends Resource> components() throws DataStoreException {
        return Collections.unmodifiableList(components);
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return group.getIdentifier();
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return group.getMetadata();
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> type, StoreListener<? super T> sl) {
    }

    @Override
    public <T extends StoreEvent> void removeListener(Class<T> type, StoreListener<? super T> sl) {
    }

}

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
import org.apache.sis.storage.base.StoreResource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.hdf.HDF5Store;
import org.geotoolkit.hdf.api.Group;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;

/**
 * Decorate an HDF Group as an Aggregate.
 * The components are the resources decorating the group children, added by the store.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GroupAsAggregate implements Aggregate, StoreResource{

    private final HDF5Store store;
    private final Group group;
    private final List<Resource> components = new ArrayList<>();

    /**
     * Creates an aggregate view of the given group, initially without components.
     *
     * @param store store owning the group.
     * @param group group to decorate.
     */
    public GroupAsAggregate(HDF5Store store, Group group) {
        this.store = store;
        this.group = group;
    }

    /**
     * Returns the modifiable list of components of this aggregate.
     *
     * @return the modifiable list of components of this aggregate
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

    @Override
    public DataStore getOriginator() {
        return store;
    }

    @Override
    public Optional<FileSet> getFileSet() throws DataStoreException {
        return store.getFileSet();
    }
}

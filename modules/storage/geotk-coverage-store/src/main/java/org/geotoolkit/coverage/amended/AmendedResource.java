/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.coverage.amended;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.storage.AbstractResource;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.storage.coverage.CoverageStoreManagementEvent;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;

/**
 * Wrap a DataNode and it's children.
 *
 * @author Johann Sorel (Geomatys)
 */
final class AmendedResource extends AbstractResource implements Aggregate {
    private final Aggregate base;
    private final AmendedCoverageStore store;
    private final List<Resource> resources = new CopyOnWriteArrayList<>();

    /**
     * Listen to the real node events and propage them.
     */
    private final ChangeListener subListener = new ChangeListener() {
        @Override
        public void changeOccured(ChangeEvent event) {
            if (event instanceof CoverageStoreManagementEvent) {
                try {
                    rebuildNodes();
                } catch (DataStoreException ex) {
                    store.getLogger().log(Level.WARNING, ex.getMessage(),ex);
                }
            }
            if (event instanceof StorageEvent) {
                sendEvent(((StorageEvent)event).copy(AmendedResource.this));
            }
        }
    };

    AmendedResource(Aggregate node, final AmendedCoverageStore store) throws DataStoreException {
        super(node.getIdentifier().get());
        this.store = store;
        this.base = node;
        node.addListener(new StorageListener.Weak(store, subListener), ChangeEvent.class);
        rebuildNodes();
    }

    /**
     * Wrap node children.
     */
    private void rebuildNodes() throws DataStoreException{
        if (!components().isEmpty()) components().clear();
        final Collection<? extends Resource> children = base.components();
        for (Resource n : children){
            if (n instanceof PyramidalCoverageResource){
                //TODO : create an amended reference which declares itself as a pyramid.
                resources.add(new AmendedCoverageResource((GridCoverageResource)n, store));
            } else if (n instanceof GridCoverageResource){
                resources.add(new AmendedCoverageResource((GridCoverageResource)n, store));
            } else if (n instanceof Aggregate){
                resources.add(new AmendedResource((Aggregate)n, store));
            }
        }
    }

    @Override
    public Collection<? extends Resource> components() throws DataStoreException {
        return Collections.unmodifiableList(resources);
    }
}

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
import java.util.logging.Level;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DefaultAggregate;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.storage.coverage.CoverageResource;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;

/**
 * Wrap a DataNode and it's children.
 *
 * @author Johann Sorel (Geomatys)
 */
final class AmendedResource extends DefaultAggregate {
    private final Aggregate base;
    private final AmendedCoverageStore store;

    /**
     * Listen to the real node events and propage them.
     */
    private final StorageListener subListener = new StorageListener(){
        @Override
        public void structureChanged(StorageEvent event) {
            try {
                rebuildNodes();
            } catch (DataStoreException ex) {
                store.getLogger().log(Level.WARNING, ex.getMessage(),ex);
            }
            sendStructureEvent(event.copy(AmendedResource.this));
        }
        @Override
        public void contentChanged(StorageEvent event) {
            sendStructureEvent(event.copy(AmendedResource.this));
        }
    };

    AmendedResource(Aggregate node, final AmendedCoverageStore store) throws DataStoreException {
        super(((org.geotoolkit.storage.Resource)node).getIdentifier());
        this.store = store;
        this.base = node;
        ((org.geotoolkit.storage.Resource)node).addStorageListener(new StorageListener.Weak(store, subListener));
        rebuildNodes();
    }

    /**
     * Wrap node children.
     */
    private void rebuildNodes() throws DataStoreException{
        if(!components().isEmpty()) components().clear();
        final Collection<Resource> children = base.components();
        for(Resource n : children){
            if(n instanceof PyramidalCoverageResource){
                //TODO : create an amended reference which declares itself as a pyramid.
                resources.add(new AmendedCoverageResource((CoverageResource)n, store));
            }else if(n instanceof CoverageResource){
                resources.add(new AmendedCoverageResource((CoverageResource)n, store));
            }else if(n instanceof Aggregate){
                resources.add(new AmendedResource((Aggregate)n, store));
            }
        }
    }

}

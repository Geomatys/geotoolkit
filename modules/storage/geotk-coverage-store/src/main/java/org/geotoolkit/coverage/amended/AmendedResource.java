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
import org.apache.sis.internal.simple.SimpleIdentifier;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.storage.DefaultAggregate;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.storage.coverage.CoverageStoreManagementEvent;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.identification.Identification;
import org.geotoolkit.storage.coverage.GridCoverageResource;

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
        super(getIdentifier(node));
        this.store = store;
        this.base = node;
        node.addListener(new StorageListener.Weak(store, subListener), ChangeEvent.class);
        rebuildNodes();
    }

    private static Identifier getIdentifier(Resource res) throws DataStoreException {
        if (res instanceof org.geotoolkit.storage.Resource) {
            return ((org.geotoolkit.storage.Resource)res).getIdentifier();
        } else {
            for (Identification id : res.getMetadata().getIdentificationInfo()) {
                final Citation citation = id.getCitation();
                if (citation != null) {
                    for (Identifier i : citation.getIdentifiers()) {
                        return i;
                    }
                    if (citation.getTitle() != null) {
                        return new SimpleIdentifier(null, citation.getTitle().toString(), false);
                    }
                }
            }
        }
        return null;
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
                resources.add(new AmendedCoverageResource((GridCoverageResource)n, store));
            }else if(n instanceof GridCoverageResource){
                resources.add(new AmendedCoverageResource((GridCoverageResource)n, store));
            }else if(n instanceof Aggregate){
                resources.add(new AmendedResource((Aggregate)n, store));
            }
        }
    }

}

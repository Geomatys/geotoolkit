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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * Decorates a coverage store adding possibility to override properties of each coverage reference.
 * <br>
 * <br>
 * List of properties which can be override : <br>
 * <ul>
 *  <li>CRS</li>
 *  <li>GridToCRS</li>
 *  <li>PixelInCell</li>
 *  <li>Sample dimensions</li>
 * </ul>
 *
 *
 * @author Johann Sorel (Geomatys)
 */
public class AmendedCoverageStore extends AbstractCoverageStore implements Aggregate {

    protected final CoverageStore store;
    protected List<Resource> resources;

    /**
     *
     * @param store wrapped store
     */
    public AmendedCoverageStore(CoverageStore store) {
        super(store.getOpenParameters());
        this.store = store;

        store.addListener(new ChangeListener() {
            @Override
            public void changeOccured(ChangeEvent event) {
                if (event instanceof StorageEvent) {
                    event = ((StorageEvent)event).copy(AmendedCoverageStore.this);
                }
                sendEvent(event);
            }
        }, ChangeEvent.class);

    }

    @Override
    public GenericName getIdentifier() {
        return store.getIdentifier();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterValueGroup getOpenParameters() {
        return store.getOpenParameters();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStoreFactory getProvider() {
        return store.getProvider();
    }

    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    @Override
    public synchronized Collection<Resource> components() throws DataStoreException {
        if (resources == null) {
            resources = new ArrayList<>();
            for (Resource res : DataStores.flatten(store,true)) {
                if (res instanceof GridCoverageResource) {
                    resources.add(new AmendedCoverageResource((GridCoverageResource) res, this));
                } else if(res == store) {
                    //skip it
                } else {
                    resources.add(new AmendedResource((Aggregate) res, this));
                }
            }
        }
        return Collections.unmodifiableList(resources);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Metadata getMetadata() throws DataStoreException {
        return store.getMetadata();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws DataStoreException {
        store.close();
    }

}

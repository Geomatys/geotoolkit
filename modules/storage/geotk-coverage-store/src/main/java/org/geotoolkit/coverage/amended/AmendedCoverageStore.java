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
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.storage.coverage.CoverageStoreContentEvent;
import org.geotoolkit.storage.coverage.CoverageStoreListener;
import org.geotoolkit.storage.coverage.CoverageStoreManagementEvent;
import org.geotoolkit.storage.coverage.CoverageType;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;
import org.geotoolkit.storage.coverage.CoverageResource;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;

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

        store.addStorageListener(new CoverageStoreListener() {
            @Override
            public void structureChanged(CoverageStoreManagementEvent event) {
                sendStructureEvent(event.copy(this));
            }
            @Override
            public void contentChanged(CoverageStoreContentEvent event) {
                sendContentEvent(event.copy(this));
            }
        });

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
                if (res instanceof CoverageResource) {
                    resources.add(new AmendedCoverageResource((CoverageResource) res, this));
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
    public CoverageResource create(GenericName name) throws DataStoreException {
        final CoverageResource cr = store.create(name);
        if(cr instanceof PyramidalCoverageResource){
            return new AmendedCoverageResource(cr, (DataStore)store);
        }else{
            return new AmendedCoverageResource(cr, (DataStore)store);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoverageType getType() {
        return store.getType();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete(GenericName name) throws DataStoreException {
        store.delete(name);
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

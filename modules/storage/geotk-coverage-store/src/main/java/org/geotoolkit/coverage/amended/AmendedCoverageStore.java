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

import java.util.logging.Logger;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataSet;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.Resource;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.storage.coverage.CoverageStoreContentEvent;
import org.geotoolkit.storage.coverage.CoverageStoreListener;
import org.geotoolkit.storage.coverage.CoverageStoreManagementEvent;
import org.geotoolkit.storage.coverage.CoverageType;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
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
public class AmendedCoverageStore extends AbstractCoverageStore{

    protected final CoverageStore store;
    protected Resource root;

    /**
     *
     * @param store wrapped store
     */
    public AmendedCoverageStore(CoverageStore store) {
        super(store.getConfiguration());
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
    public ParameterValueGroup getConfiguration() {
        return store.getConfiguration();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStoreFactory getFactory() {
        return store.getFactory();
    }

    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized Resource getRootResource() throws DataStoreException {
        if(root==null){
            final Resource res = store.getRootResource();
            if (res instanceof CoverageResource) {
                root = new AmendedCoverageResource((CoverageResource) res, this);
            } else {
                root = new AmendedResource((DataSet) res, this);
            }

        }
        return root;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean handleVersioning() {
        return store.handleVersioning();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public VersionControl getVersioning(GenericName typeName) throws VersioningException {
        return store.getVersioning(typeName);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoverageResource findResource(GenericName name, Version version) throws DataStoreException {
        final CoverageResource cr = (version==null) ? store.findResource(name) :store.findResource(name, version);
        if(cr instanceof PyramidalCoverageResource){
            return new AmendedCoverageResource(cr, store);
        }else{
            return new AmendedCoverageResource(cr, store);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoverageResource create(GenericName name) throws DataStoreException {
        final CoverageResource cr = store.create(name);
        if(cr instanceof PyramidalCoverageResource){
            return new AmendedCoverageResource(cr, store);
        }else{
            return new AmendedCoverageResource(cr, store);
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

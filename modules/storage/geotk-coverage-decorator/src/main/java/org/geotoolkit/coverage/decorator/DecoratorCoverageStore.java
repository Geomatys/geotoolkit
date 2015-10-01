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
package org.geotoolkit.coverage.decorator;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.storage.coverage.CoverageStoreContentEvent;
import org.geotoolkit.storage.coverage.CoverageStoreFactory;
import org.geotoolkit.storage.coverage.CoverageStoreListener;
import org.geotoolkit.storage.coverage.CoverageStoreManagementEvent;
import org.geotoolkit.storage.coverage.CoverageType;
import org.geotoolkit.storage.coverage.PyramidalCoverageReference;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DecoratorCoverageStore extends AbstractCoverageStore{

    protected final CoverageStore store;
    protected DecoratorDataNode root;
    
    /**
     *
     * @param store wrapped store
     */
    public DecoratorCoverageStore(CoverageStore store) {
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

    @Override
    public ParameterValueGroup getConfiguration() {
        return store.getConfiguration();
    }

    @Override
    public CoverageStoreFactory getFactory() {
        return store.getFactory();
    }

    @Override
    public synchronized DataNode getRootNode() throws DataStoreException {
        if(root==null){
            root = new DecoratorDataNode(store.getRootNode(), this);
        }
        return root;
    }

    @Override
    public boolean handleVersioning() {
        return store.handleVersioning();
    }

    @Override
    public VersionControl getVersioning(GenericName typeName) throws VersioningException {
        return store.getVersioning(typeName);
    }

    @Override
    public CoverageReference getCoverageReference(GenericName name, Version version) throws DataStoreException {
        final CoverageReference cr = (version==null) ? store.getCoverageReference(name) :store.getCoverageReference(name, version);
        if(cr instanceof PyramidalCoverageReference){
            return new DecoratorPyramidalCoverageReference(cr, store);
        }else{
            return new DecoratorCoverageReference(cr, store);
        }
    }

    @Override
    public CoverageReference create(GenericName name) throws DataStoreException {
        final CoverageReference cr = store.create(name);
        if(cr instanceof PyramidalCoverageReference){
            return new DecoratorPyramidalCoverageReference(cr, store);
        }else{
            return new DecoratorCoverageReference(cr, store);
        }
    }

    @Override
    public CoverageType getType() {
        return store.getType();
    }

    @Override
    public void delete(GenericName name) throws DataStoreException {
        store.delete(name);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return store.getMetadata();
    }

    @Override
    public void close() throws DataStoreException {
        store.close();
    }

}

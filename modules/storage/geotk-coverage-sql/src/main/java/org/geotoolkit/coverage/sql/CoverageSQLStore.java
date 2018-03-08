/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.AbstractCoverageResource;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.CoverageStoreContentEvent;
import org.geotoolkit.storage.coverage.CoverageStoreManagementEvent;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.Resource;
import org.geotoolkit.storage.StorageListener;
import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Wrap a coverage-sql database as a CoverageStore.
 * TODO : temporary binding waiting for CoverageStore interface to be revisited
 * and integrated in geotk.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CoverageSQLStore extends AbstractCoverageStore implements Aggregate {

    private final CoverageDatabase db;
    private final Set<StorageListener> listeners = new HashSet<StorageListener>();

    private static ParameterValueGroup adaptParameter(ParameterValueGroup parameters){
        final ParameterValueGroup params = CoverageDatabase.PARAMETERS.createValue();

        final StringBuilder url = new StringBuilder("jdbc:postgresql://");
        url.append(parameters.parameter("host").getValue());
        url.append(':');
        url.append(parameters.parameter("port").getValue());
        url.append('/');
        url.append(parameters.parameter("database").getValue());

        params.parameter("URL").setValue(url.toString());

        if(parameters.parameter("user")!=null){
            params.parameter("user").setValue(parameters.parameter("user").getValue());
        }
        if(parameters.parameter("password")!=null){
            params.parameter("password").setValue(parameters.parameter("password").getValue());
        }
        if(parameters.parameter("schema")!=null){
            params.parameter("schema").setValue(parameters.parameter("schema").getValue());
        }
        if(parameters.parameter("rootDirectory")!=null){
            params.parameter("rootDirectory").setValue(parameters.parameter("rootDirectory").getValue());
        }
        return params;
    }

    public CoverageSQLStore(ParameterValueGroup parameters) {
        super(adaptParameter(parameters));
        this.db = new CoverageDatabase(getOpenParameters());
    }

    @Override
    public DataStoreFactory getProvider() {
        return DataStores.getFactoryById(CoverageSQLStoreFactory.NAME);
    }

    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        final List<Resource> resources = new ArrayList<>();
        final Set<String> layers = db.getLayers().result();
        for (String layer : layers) {
            resources.add(new CoverageSQLLayerResource(NamesExt.create(layer)));
        }
        return Collections.unmodifiableList(resources);
    }

    @Override
    public void close() throws DataStoreException {
        db.dispose();
    }

    ////////////////////////////////////////////////////////////////////////////
    // listeners methods ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public void addStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Forward a structure event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(final CoverageStoreManagementEvent event){
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.structureChanged(event);
        }
    }

    /**
     * Forward a data event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(final CoverageStoreContentEvent event){
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.contentChanged(event);
        }
    }

    private class CoverageSQLLayerResource extends AbstractCoverageResource {


        private CoverageSQLLayerResource(GenericName name) {
            super(CoverageSQLStore.this,name);
        }


        @Override
        public int getImageIndex() {
            return 0;
        }

        @Override
        public boolean isWritable() {
            return false;
        }

        @Override
        public GridCoverageReader acquireReader() throws CoverageStoreException {
            final LayerCoverageReader reader = CoverageSQLStore.this.db.createGridCoverageReader(getName().tip().toString());
            return reader;
        }

        @Override
        public GridCoverageWriter acquireWriter() throws CoverageStoreException {
            throw new CoverageStoreException("Coverage is not writable.");
        }

        @Override
        public Image getLegend() throws DataStoreException {
            return null;
        }

    }

}

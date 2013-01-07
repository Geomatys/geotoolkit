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
import java.util.HashSet;
import java.util.Set;
import org.geotoolkit.coverage.AbstractCoverageReference;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageStoreContentEvent;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.coverage.CoverageStoreManagementEvent;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.storage.StorageListener;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Wrap a coverage-sql database as a CoverageStore.
 * TODO : temporary binding waiting for CoverageStore interface to be revisited
 * and integrated in geotk.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CoverageSQLStore extends CoverageDatabase implements CoverageStore {

    private final Set<StorageListener> listeners = new HashSet<StorageListener>();
    private final ParameterValueGroup parameters;

    public CoverageSQLStore(ParameterValueGroup parameters) {
        super(parameters);
        this.parameters = parameters;
    }

    @Override
    public ParameterValueGroup getConfiguration() {
        return parameters;
    }

    @Override
    public CoverageStoreFactory getFactory() {
        return CoverageStoreFinder.getFactoryById(CoverageSQLStoreFactory.NAME);
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        final Set<String> layers = getLayers().result();
        final Set<Name> names = new HashSet<Name>(layers.size());
        for (String layer : layers) {
            names.add(new DefaultName(layer));
        }
        return names;
    }

    @Override
    public CoverageReference getCoverageReference(Name name) throws DataStoreException {
        return new CoverageSQLLayerReference(name);
    }

    @Override
    public CoverageReference create(Name name) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void delete(Name name) throws DataStoreException {
        throw new DataStoreException("Not supported.");
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
    
    private class CoverageSQLLayerReference extends AbstractCoverageReference {

        private final Name name;

        private CoverageSQLLayerReference(Name name) {
            this.name = name;
        }

        @Override
        public Name getName() {
            return name;
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
        public CoverageStore getStore() {
            return CoverageSQLStore.this;
        }

        @Override
        public GridCoverageReader createReader() throws DataStoreException {
            final LayerCoverageReader reader = CoverageSQLStore.this.createGridCoverageReader(name.getLocalPart());
            return reader;
        }

        @Override
        public GridCoverageWriter createWriter() throws DataStoreException {
            throw new DataStoreException("Coverage is not writable.");
        }

        public Image getLegend() throws DataStoreException {
            return null;
        }

    }

}

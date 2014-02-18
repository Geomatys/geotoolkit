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

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.AbstractCoverageReference;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageStoreContentEvent;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.coverage.CoverageStoreManagementEvent;
import org.geotoolkit.coverage.CoverageType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
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
    public CoverageType getType() {
        return CoverageType.GRID;
    }

    @Override
    public DataNode getRootNode() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public boolean handleVersioning() {
        return false;
    }

    @Override
    public VersionControl getVersioning(Name typeName) throws VersioningException {
        throw new VersioningException("Versioning not supported");
    }

    @Override
    public CoverageReference getCoverageReference(Name name, Version version) throws DataStoreException {
        throw new DataStoreException("Versioning not supported");
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


        private CoverageSQLLayerReference(Name name) {
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
            final LayerCoverageReader reader = CoverageSQLStore.this.createGridCoverageReader(name.getLocalPart());
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

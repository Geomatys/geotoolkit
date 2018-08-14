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
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.storage.coverage.AbstractCoverageResource;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.Resource;
import org.opengis.parameter.ParameterValue;
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
    private final Set<ChangeListener> geotkListeners = new HashSet<>();

    private static ParameterValueGroup adaptParameter(ParameterValueGroup parameters) {
        final ParameterValueGroup params = CoverageDatabase.PARAMETERS.createValue();

        final StringBuilder url = new StringBuilder("jdbc:postgresql://");
        url.append(parameters.parameter("host").getValue());
        url.append(':');
        url.append(parameters.parameter("port").getValue());
        url.append('/');
        url.append(parameters.parameter("database").getValue());

        params.parameter("URL").setValue(url.toString());

        ParameterValue<?> p;
        if ((p = parameters.parameter("user")) != null) {
            params.parameter("user").setValue(p.getValue());
        }
        if ((p = parameters.parameter("password")) != null) {
            params.parameter("password").setValue(p.getValue());
        }
        if ((p = parameters.parameter("schema")) != null) {
            params.parameter("schema").setValue(p.getValue());
        }
        if ((p = parameters.parameter("rootDirectory")) != null) {
            params.parameter("rootDirectory").setValue(p.getValue());
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
     * {@inheritDoc}
     */
    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
        synchronized (geotkListeners) {
            geotkListeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
        synchronized (geotkListeners) {
            geotkListeners.remove(listener);
        }
    }

    /**
     * Forward an event to all listeners.
     * @param event event to send to listeners.
     */
    protected void sendEvent(final ChangeEvent event) {
        final ChangeListener[] lst;
        synchronized (geotkListeners) {
            lst = geotkListeners.toArray(new ChangeListener[geotkListeners.size()]);
        }
        for (final ChangeListener listener : lst) {
            listener.changeOccured(event);
        }
    }

    private final class CoverageSQLLayerResource extends AbstractCoverageResource {
        private CoverageSQLLayerResource(GenericName name) {
            super(CoverageSQLStore.this, name);
        }

        @Override
        public int getImageIndex() {
            return 0;
        }

        @Override
        public boolean isWritable() {
            return true;
        }

        @Override
        public GridCoverageReader acquireReader() throws CoverageStoreException {
            return db.createGridCoverageReader(getIdentifier().tip().toString());
        }

        @Override
        public GridCoverageWriter acquireWriter() throws CoverageStoreException {
            return db.createGridCoverageWriter(getIdentifier().tip().toString());
        }

        @Override
        public Image getLegend() throws DataStoreException {
            return null;
        }
    }
}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.opengis.metadata.Metadata;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.Resource;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;


/**
 * Wrap a coverage-sql database as a CoverageStore.
 * TODO : temporary binding waiting for CoverageStore interface to be revisited
 * and integrated in geotk.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CoverageSQLStore extends DataStore implements Aggregate {

    private final Parameters parameters;
    final CoverageDatabase db;
    private final Set<ChangeListener> geotkListeners = new HashSet<>();

    private static Parameters adaptParameter(ParameterValueGroup parameters) {
        final Parameters params = Parameters.castOrWrap(CoverageDatabase.PARAMETERS.createValue());

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
        this.parameters = adaptParameter(parameters);
        this.db = new CoverageDatabase(parameters);
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(CoverageSQLProvider.NAME);
    }

    @Override
    public ParameterValueGroup getOpenParameters() {
        return Parameters.unmodifiable(parameters);
    }

    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        final List<Resource> resources = new ArrayList<>();
        final Set<String> layers = db.getLayers().result();
        for (String layer : layers) {
            resources.add(new CoverageSQLResource(this, NamesExt.create(layer)));
        }
        return Collections.unmodifiableList(resources);
    }

    @Override
    public void close() throws DataStoreException {
        db.dispose();
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
        synchronized (geotkListeners) {
            geotkListeners.add(listener);
        }
    }

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

}

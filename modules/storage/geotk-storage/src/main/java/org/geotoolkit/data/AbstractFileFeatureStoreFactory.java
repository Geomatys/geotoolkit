/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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

package org.geotoolkit.data;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractFileFeatureStoreFactory extends DataStoreFactory implements FileFeatureStoreFactory {

    /**
     * url to the file.
     */
    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName("path")
            .addName(Bundle.formatInternational(Bundle.Keys.paramPathAlias))
            .addName(LOCATION)
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramPathRemarks))
            .setRequired(true)
            .create(URI.class, null);

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canProcess(final ParameterValueGroup params) {
        if (super.canProcess(params)) {
            final Object obj = params.parameter(PATH.getName().toString()).getValue();
            if(obj != null && obj instanceof URI){
                return extensionMatch((URI)obj);
            }
        }

        return false;
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        final URI uri = connector.getStorageAs(URI.class);
        if (uri != null && extensionMatch(uri)) {
            return new ProbeResult(true, null, null);
        }

        return new ProbeResult(false, null, null);
    }

    /**
     * Check if the path of given URI ends with one of the file extensions
     * specified as manageable by {@link #getSuffix() } method.
     *
     * @param location The URI to test.
     * @return True if the path of given URI ends with a known extension. False
     * otherwise.
     */
    private boolean extensionMatch(final URI location) {
        final String path = location.getPath().toLowerCase();
        for (final String ext : getSuffix()) {
            if (path.endsWith(ext.toLowerCase()) && !path.endsWith("*" + ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc }
     * @param uri
     */
    @Override
    public FeatureStore createDataStore(final URI uri) throws DataStoreException {
        FeatureStore result;
        final  Map params = Collections.singletonMap(PATH.getName().toString(), uri);
        try {
            result = (FeatureStore) DataStores.open(this,params);
        } catch (DataStoreException e) {
            result = (FeatureStore) DataStores.create(this,params);
        }
        return result;
    }

}

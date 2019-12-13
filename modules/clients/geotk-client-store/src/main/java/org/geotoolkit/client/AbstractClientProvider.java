/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *    (C) 2012, Johann Sorel
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
package org.geotoolkit.client;

import java.net.URL;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.security.ClientSecurity;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * Abstract Server factory.
 *
 * @author Johann Sorel
 * @module
 */
public abstract class AbstractClientProvider extends DataStoreProvider {

    /**
     * commonly used translation for version parameters.
     */
    public static final InternationalString I18N_VERSION = Bundle.formatInternational(Bundle.Keys.version);

    /**
     * Version, Mandatory.
     * Subclasses should redeclared this parameter with a different values.
     */
    public static final ParameterDescriptor<String> VERSION = new ParameterBuilder()
            .addName("version")
            .addName(Bundle.formatInternational(Bundle.Keys.version))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.versionRemarks))
            .setRequired(true)
            .create(String.class, null);

    /**
     * Create the version descriptor.
     *
     * @return a version descriptor.
     */
    public static ParameterDescriptor<String> createVersionDescriptor(String[] values, String defaultValue) {
        return new ParameterBuilder()
                    .addName(VERSION.getName().getCode())
                    .addName(VERSION.getAlias().iterator().next())
                    .setRemarks(VERSION.getRemarks())
                    .setRequired(true)
                    .createEnumerated(String.class, values,defaultValue);
    }

    /**
     * Server URL, Mandatory.
     */
    public static final ParameterDescriptor<URL> URL = new ParameterBuilder()
            .addName("url")
            .addName(Bundle.formatInternational(Bundle.Keys.url))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.urlRemarks))
            .setRequired(true)
            .create(URL.class, null);
    /**
     * Security, Optional.
     */
    public static final ParameterDescriptor<ClientSecurity> SECURITY = new ParameterBuilder()
            .addName("security")
            .addName(Bundle.formatInternational(Bundle.Keys.security))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.securityRemarks))
            .setRequired(false)
            .create(ClientSecurity.class, null);

    /**
     * Cache images in memory, Optional.
     */
    public static final ParameterDescriptor<Boolean> IMAGE_CACHE = new ParameterBuilder()
            .addName("imagecache")
            .addName(Bundle.formatInternational(Bundle.Keys.imageCache))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.imageCacheRemarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.FALSE);

    /**
     * Cache images in memory, Optional.
     * Default value is 20.000 millisecond.
     */
    public static final ParameterDescriptor<Integer> TIMEOUT = new ParameterBuilder()
            .addName("timeout")
            .addName(Bundle.formatInternational(Bundle.Keys.timeout))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.timeoutRemarks))
            .setRequired(false)
            .create(Integer.class, 20000);

    /**
     * Use NIO when possible for queries, Optional.
     */
    public static final ParameterDescriptor<Boolean> NIO_QUERIES = new ParameterBuilder()
            .addName("nio")
            .addName(Bundle.formatInternational(Bundle.Keys.nio))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.nioRemarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.FALSE);

    /**
     * @see #checkIdentifier(org.opengis.parameter.ParameterValueGroup)
     * @throws DataStoreException if identifier is not valid
     */
    protected void checkCanProcessWithError(final ParameterValueGroup params) throws DataStoreException{
        final boolean valid = canProcess(params);
        if(!valid){
            throw new DataStoreException("Parameter values not supported by this factory.");
        }
    }

    /**
     * @param params
     * @see #checkIdentifier(org.opengis.parameter.ParameterValueGroup)
     * @throws DataStoreException if identifier is not valid
     */
    protected void ensureCanProcess(final ParameterValueGroup params) throws DataStoreException{
        final boolean valid = canProcess(params);
        if(!valid){
            throw new DataStoreException("Parameter values not supported by this factory.");
        }
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        // TODO : properly implement in each sub-type
        return new ProbeResult(false, null, null);
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        try {
            final URL url = connector.getStorageAs(URL.class);
            Parameters parameters = Parameters.castOrWrap(getOpenParameters().createValue());
            parameters.getOrCreate(URL).setValue(url);
            return open(parameters);
        } catch (IllegalArgumentException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    private boolean canProcess(final ParameterValueGroup params) {
        if (params == null) {
            return false;
        }

        final ParameterDescriptorGroup desc = getOpenParameters();
        if (!desc.getName().getCode().equalsIgnoreCase(params.getDescriptor().getName().getCode())) {
            return false;
        }

        final ConformanceResult result = org.geotoolkit.parameter.Parameters.isValid(params, desc);
        return (result != null) && Boolean.TRUE.equals(result.pass());
    }
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.wmsc;

import java.net.URL;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.base.Capability;
import org.apache.sis.storage.base.StoreMetadata;
import org.geotoolkit.client.AbstractClientProvider;
import org.geotoolkit.internal.ClassLoaderInternationalString;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.geotoolkit.storage.multires.TiledResource;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 * WMS-C Server factory.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
@StoreMetadata(
        formatName = WMSCProvider.NAME,
        capabilities = {Capability.READ},
        resourceTypes = {GridCoverageResource.class, TiledResource.class})
@StoreMetadataExt(resourceTypes = ResourceType.COVERAGE)
public class WMSCProvider extends AbstractClientProvider {

    /** factory identification **/
    public static final String NAME = "wmsc";

    public static final ParameterDescriptorGroup PARAMETERS =
            new ParameterBuilder().addName(NAME).addName("WMSCParameters").createGroup(URL,SECURITY,IMAGE_CACHE,NIO_QUERIES,TIMEOUT);

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS;
    }

    public CharSequence getDescription() {
        return new ClassLoaderInternationalString(WMSCProvider.class,"org/geotoolkit/wmsc/bundle", "serverDescription");
    }

    public CharSequence getDisplayName() {
        return new ClassLoaderInternationalString(WMSCProvider.class,"org/geotoolkit/wmsc/bundle", "serverTitle");
    }

    @Override
    public WebMapClientCached open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        final URL url = Parameters.castOrWrap(params).getValue(URL);
        ClientSecurity security = null;
        try{
            final ParameterValue val = params.parameter(SECURITY.getName().getCode());
            security = (ClientSecurity) val.getValue();
        }catch(ParameterNotFoundException ex){}

        boolean cacheImage = false;
        try{
            final ParameterValue val = params.parameter(IMAGE_CACHE.getName().getCode());
            cacheImage = Boolean.TRUE.equals(val.getValue());
        }catch(ParameterNotFoundException ex){}

        return new WebMapClientCached(url,security,cacheImage);
    }
}

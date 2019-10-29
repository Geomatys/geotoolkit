/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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
package org.geotoolkit.wmts;

import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.client.AbstractClientProvider;
import org.geotoolkit.client.map.CachedPyramidSet;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.geotoolkit.wmts.xml.WMTSVersion;
import org.opengis.parameter.*;

/**
 * WMTS Server factory.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@StoreMetadata(
        formatName = WMTSProvider.NAME,
        capabilities = {Capability.READ},
        resourceTypes = {GridCoverageResource.class})
@StoreMetadataExt(resourceTypes = ResourceType.PYRAMID)
public class WMTSProvider extends AbstractClientProvider {

    public static final String NAME = "wmts";

    /**
     * Mandatory - the serveur verion
     */
    public static final ParameterDescriptor<String> VERSION;
    static{
        final WMTSVersion[] values = WMTSVersion.values();
        final String[] validValues =  new String[values.length];
        for(int i=0;i<values.length;i++){
            validValues[i] = values[i].getCode();
        }
        VERSION = createVersionDescriptor(validValues, WMTSVersion.v100.getCode());
    }

    public static final ParameterDescriptorGroup PARAMETERS =
            new ParameterBuilder().addName(NAME).addName("WMTSParameters").createGroup(
                URL,VERSION, SECURITY, IMAGE_CACHE,NIO_QUERIES,TIMEOUT);

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS;
    }

    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.coverageDescription);
    }

    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.coverageTitle);
    }

    @Override
    public WebMapTileClient open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        final WebMapTileClient server = new WebMapTileClient(params);

        try{
            final ParameterValue val = params.parameter(NIO_QUERIES.getName().getCode());
            boolean useNIO = Boolean.TRUE.equals(val.getValue());
            server.setUserProperty(CachedPyramidSet.PROPERTY_NIO, useNIO);
        }catch(ParameterNotFoundException ex){}

        return server;
    }
}

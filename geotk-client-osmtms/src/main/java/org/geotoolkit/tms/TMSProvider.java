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
package org.geotoolkit.tms;

import org.apache.sis.storage.base.Capability;
import org.apache.sis.storage.base.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.client.AbstractClientProvider;
import org.geotoolkit.client.map.CachedTileMatrixSets;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.parameter.*;

/**
 * TMS client provider.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@StoreMetadata(
        formatName = TMSProvider.NAME,
        capabilities = {Capability.READ},
        resourceTypes = {GridCoverageResource.class})
@StoreMetadataExt(resourceTypes = ResourceType.PYRAMID)
public class TMSProvider extends AbstractClientProvider {

    /** provider identification **/
    public static final String NAME = "tms";

    /**
     * Mandatory - the serveur max zoom level
     */
    public static final ParameterDescriptor<Integer> MAX_ZOOM_LEVEL = new ParameterBuilder()
            .addName("maxZoomLevel")
            .addName(Bundle.formatInternational(Bundle.Keys.maxZoomLevel))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.maxZoomLevelRemarks))
            .setRequired(true)
            .create(Integer.class, 18);
    /**
     * file pattern.
     */
    public static final ParameterDescriptor<String> PATTERN = new ParameterBuilder()
            .addName("pattern")
            .setRequired(true)
            .create(String.class, "{z}/{x}/{y}.png");

    public static final ParameterDescriptorGroup PARAMETERS =
            new ParameterBuilder().addName(NAME).addName("TMSParameters").createGroup(
                URL,MAX_ZOOM_LEVEL,SECURITY,IMAGE_CACHE,NIO_QUERIES,TIMEOUT,PATTERN);

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS;
    }

    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.coverageTitle);
    }

    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.coverageDescription);
    }

    @Override
    public TileMapClient open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);

        final TileMapClient server = new TileMapClient(params);

        try{
            final ParameterValue val = params.parameter(NIO_QUERIES.getName().getCode());
            boolean useNIO = Boolean.TRUE.equals(val.getValue());
            server.setUserProperty(CachedTileMatrixSets.PROPERTY_NIO, useNIO);
        }catch(ParameterNotFoundException ex){}

        return server;
    }

}

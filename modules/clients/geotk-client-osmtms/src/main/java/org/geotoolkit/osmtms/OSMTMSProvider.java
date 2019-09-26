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
package org.geotoolkit.osmtms;

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.AbstractClientProvider;
import org.geotoolkit.client.map.CachedPyramidSet;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.parameter.*;

/**
 * OSM TMS Server factory.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@StoreMetadataExt(resourceTypes = ResourceType.PYRAMID)
public class OSMTMSProvider extends AbstractClientProvider {

    /** factory identification **/
    public static final String NAME = "osm-tms";

    /**
     * Mandatory - the serveur max zoom level
     */
    public static final ParameterDescriptor<Integer> MAX_ZOOM_LEVEL = new ParameterBuilder()
            .addName("maxZoomLevel")
            .addName(Bundle.formatInternational(Bundle.Keys.maxZoomLevel))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.maxZoomLevelRemarks))
            .setRequired(true)
            .create(Integer.class, 18);

    public static final ParameterDescriptorGroup PARAMETERS =
            new ParameterBuilder().addName(NAME).addName("OSMTMSParameters").createGroup(
                URL,MAX_ZOOM_LEVEL,SECURITY,IMAGE_CACHE,NIO_QUERIES,TIMEOUT);

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
    public OSMTileMapClient open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);

        final OSMTileMapClient server = new OSMTileMapClient(params);

        try{
            final ParameterValue val = params.parameter(NIO_QUERIES.getName().getCode());
            boolean useNIO = Boolean.TRUE.equals(val.getValue());
            server.setUserProperty(CachedPyramidSet.PROPERTY_NIO, useNIO);
        }catch(ParameterNotFoundException ex){}

        return server;
    }

}

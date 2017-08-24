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

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.AbstractClientFactory;
import org.geotoolkit.client.map.CachedPyramidSet;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.geotoolkit.storage.coverage.CoverageStoreFactory;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.*;

/**
 * OSM TMS Server factory.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OSMTMSClientFactory extends AbstractClientFactory implements CoverageStoreFactory{

    /** factory identification **/
    public static final String NAME = "osm-tms";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

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
            new ParameterBuilder().addName("OSMTMSParameters").createGroup(
                IDENTIFIER,URL,MAX_ZOOM_LEVEL,SECURITY,IMAGE_CACHE,NIO_QUERIES,TIMEOUT);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS;
    }

    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.coverageTitle);
    }

    @Override
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

    @Override
    public OSMTileMapClient open(Map<String, ? extends Serializable> params) throws DataStoreException {
        return (OSMTileMapClient) super.open(params);
    }

    @Override
    public OSMTileMapClient create(ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Can not create new OSM TMS coverage store.");
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.PYRAMID, true, false, false);
    }
}

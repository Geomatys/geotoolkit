/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2015, Geomatys
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
package org.geotoolkit.coverage.xmlstore;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.Version;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.geotoolkit.storage.multires.TiledResource;

/**
 * Coverage store relying on an xml file.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@StoreMetadata(
        formatName = XMLCoverageStoreFactory.NAME,
        capabilities = {Capability.READ, Capability.WRITE, Capability.CREATE},
        resourceTypes = {GridCoverageResource.class, TiledResource.class})
@StoreMetadataExt(resourceTypes = ResourceType.PYRAMID)
public class XMLCoverageStoreFactory extends DataStoreProvider {

    /** factory identification **/
    public static final String NAME = "coverage-xml-pyramid";

    /**
     * Mandatory - the folder path
     */
    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName("path")
            .addName(Bundle.formatInternational(Bundle.Keys.coverageXMLPath))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.coverageXMLPathRemarks))
            .setRequired(true)
            .create(URI.class, null);
    /**
     * A parameter to specify if tile states will be checked using descriptor file (default) or not.
     */
    public static final ParameterDescriptor<Boolean> CACHE_TILE_STATE = new ParameterBuilder()
            .addName("cacheTileState")
            .addName(Bundle.formatInternational(Bundle.Keys.coverageXMLTileState))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.coverageXMLTileStateRemarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.FALSE);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).addName("XMLCoverageStoreParameters").createGroup(
                PATH, CACHE_TILE_STATE);

    @Override
    public String getShortName() {
        return NAME;
    }

    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.coverageXMLDescription);
    }

    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.coverageXMLTitle);
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }


    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        final Version version = new Version(XMLCoverageResource.CURRENT_VERSION);
        final String mime = "application/"+NAME;
        final Path root = connector.getStorageAs(Path.class);
        final String ext = connector.getFileExtension();
        if (ext != null && ext.equalsIgnoreCase("xml")) {
            return new ProbeResult(true, mime, version);
        }

        return new ProbeResult(false, mime, version);
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        try {
            final Path path = connector.getStorageAs(Path.class);
            connector.closeAllExcept(path);
            return new XMLCoverageStore(path);
        } catch (IOException | URISyntaxException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public XMLCoverageStore open(ParameterValueGroup params) throws DataStoreException {
        try {
            return new XMLCoverageStore(params);
        } catch (IOException | URISyntaxException ex) {
            throw new DataStoreException(ex);
        }
    }
}

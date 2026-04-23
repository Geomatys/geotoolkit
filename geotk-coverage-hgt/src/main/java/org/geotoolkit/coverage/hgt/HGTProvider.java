/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.coverage.hgt;

import java.net.URI;
import org.apache.sis.storage.base.Capability;
import org.apache.sis.storage.base.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * NASA HGT format for SRTM distribution.
 *
 * @author Johann Sorel (Geomatys)
 */
@StoreMetadata(
        formatName = HGTProvider.NAME,
        fileSuffixes = "hgt",
        capabilities = {Capability.READ},
        resourceTypes = {GridCoverageResource.class})
@StoreMetadataExt(resourceTypes = ResourceType.GRID)
public class HGTProvider extends DataStoreProvider {

    /** factory identification **/
    public static final String NAME = "hgt";
    public static final String MIME_TYPE = "application/x-ogc-srtmhgt";

    /**
     * Mandatory - the file uri
     */
    public static final ParameterDescriptor<URI> PATH;

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR;

    static {
        final ParameterBuilder builder = new ParameterBuilder();
        PATH = builder.setRequired(true).addName(DataStoreProvider.LOCATION).addName("path")
                      .setDescription("HGT file")
                      .create(URI.class, null);
        PARAMETERS_DESCRIPTOR = builder.addName(NAME).createGroup(PATH);
    }

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        if (ProbeResult.SUPPORTED.equals(connector.pathEndsWith(".hgt", true))) {
            return new ProbeResult(true, MIME_TYPE, null);
        }
        return ProbeResult.UNSUPPORTED_STORAGE;
    }

    @Override
    public HGTStore open(StorageConnector sc) throws DataStoreException {
        return new HGTStore(sc);
    }

}

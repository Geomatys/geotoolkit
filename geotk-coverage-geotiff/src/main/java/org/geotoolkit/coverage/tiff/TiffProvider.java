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
package org.geotoolkit.coverage.tiff;

import java.net.URI;
import java.nio.file.Path;
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
 * This is a temporary provider until geotiff is moved to Apache SIS.
 *
 * @author Johann Sorel (Geomatys)
 */
@StoreMetadata(
        formatName = TiffProvider.NAME,
        fileSuffixes = {"tiff","tif","geotif","geotiff"},
        capabilities = {Capability.READ, Capability.WRITE, Capability.CREATE},
        resourceTypes = {GridCoverageResource.class})
@StoreMetadataExt(resourceTypes = ResourceType.GRID)
public class TiffProvider extends DataStoreProvider {

    public static final String NAME = "geotk-geotiff";
    public static final String MIME_TYPE = "image/tiff;subtype=geotiff";

    // big endian signatures
    private static final byte[] SIGNATURE_BE_1 = new byte[]{'M', 0x00, 0x2B, 0x00, 0x08, 0x00, 0x00};
    private static final byte[] SIGNATURE_BE_2 = new byte[]{'M', 0x00, 0x2A};
    // little endian signatures
    private static final byte[] SIGNATURE_LE_1 = new byte[]{'I', 0x2B, 0x00, 0x08, 0x00, 0x00, 0x00};
    private static final byte[] SIGNATURE_LE_2 = new byte[]{'I', 0x2A, 0x00};

    /**
     * Mandatory - the file uri
     */
    public static final ParameterDescriptor<URI> PATH;

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR;

    static {
        final ParameterBuilder builder = new ParameterBuilder();
        PATH = builder.setRequired(true).addName(DataStoreProvider.LOCATION)
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
        if  (ProbeResult.SUPPORTED.equals(connector.contentStartsWith(SIGNATURE_BE_1))
          || ProbeResult.SUPPORTED.equals(connector.contentStartsWith(SIGNATURE_BE_2))
          || ProbeResult.SUPPORTED.equals(connector.contentStartsWith(SIGNATURE_LE_1))
          || ProbeResult.SUPPORTED.equals(connector.contentStartsWith(SIGNATURE_LE_2))) {
            return new ProbeResult(true, MIME_TYPE, null);
        }
        return ProbeResult.UNSUPPORTED_STORAGE;
    }

    @Override
    public TiffStore open(StorageConnector sc) throws DataStoreException {
        return new TiffStore(sc.commit(Path.class, NAME));
    }

}

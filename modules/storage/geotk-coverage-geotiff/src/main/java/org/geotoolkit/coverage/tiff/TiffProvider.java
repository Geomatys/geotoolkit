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
import java.util.Arrays;
import java.util.Collection;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.ProviderOnFileSystem;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.geotoolkit.storage.feature.FileFeatureStoreFactory;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * This is a temporary provider until geotiff is moved to Apache SIS.
 *
 * @author Johann Sorel (Geomatys)
 */
@StoreMetadataExt(resourceTypes = ResourceType.GRID)
public class TiffProvider extends DataStoreProvider implements ProviderOnFileSystem {

    public static final String NAME = "geotk-geotiff";
    public static final String MIME_TYPE = "image/tiff;subtype=geotiff";

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
        return FileFeatureStoreFactory.probe(this, connector, MIME_TYPE);
    }

    @Override
    public TiffStore open(StorageConnector sc) throws DataStoreException {
        return new TiffStore(sc.getStorageAs(Path.class));
    }

    /**
     * @return collection with tiff and geotiff extensions.
     */
    @Override
    public Collection<String> getSuffix() {
        return Arrays.asList("tiff", "tif", "geotiff", "geotif");
    }

    /**
     * @return signature of the tiff file
     */
    @Override
    public Collection<byte[]> getSignature() {
        return Arrays.asList(
                // big endian signatures
                new byte[]{'M', 0x00, 0x2B, 0x00, 0x08, 0x00, 0x00},
                new byte[]{'M', 0x00, 0x2A},
                // little endian signatures
                new byte[]{'I', 0x2B, 0x00, 0x08, 0x00, 0x00, 0x00},
                new byte[]{'I', 0x2A, 0x00}
        );
    }
}

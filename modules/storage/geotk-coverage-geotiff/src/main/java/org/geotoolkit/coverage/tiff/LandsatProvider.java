/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
import java.util.Collection;
import java.util.Collections;
import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.feature.FileFeatureStoreFactory;
import org.geotoolkit.storage.ProviderOnFileSystem;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 *
 * @author Remi Marechal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
@StoreMetadata(
        formatName = LandsatProvider.NAME,
        capabilities = {Capability.READ},
        resourceTypes = {GridCoverageResource.class})
@StoreMetadataExt(resourceTypes = ResourceType.GRID)
public class LandsatProvider extends DataStoreProvider implements ProviderOnFileSystem {

    /** factory identification **/
    public static final String NAME = "geotk-landsat";
    public static final String MIME_TYPE = "application/x-landsat";

    /**
     * Mandatory - the folder uri
     */
    public static final ParameterDescriptor<URI> PATH;

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR;

    static {
        final ParameterBuilder builder = new ParameterBuilder();
        PATH = builder.setRequired(true).addName(DataStoreProvider.LOCATION).addName("path")
                      .setDescription("Landsat product file : Landsat8, MTL.txt (*.txt)")
                      .create(URI.class, null);

        PARAMETERS_DESCRIPTOR = builder.addName(NAME).addName("LandSatParameters")
                      .createGroup(PATH);
    }

    @Override
    public String getShortName() {
        return NAME;
    }

    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.description);
    }

    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.title);
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
    public LandsatStore open(StorageConnector sc) throws DataStoreException {
        return new LandsatStore(sc);
    }

    /**
     * @return collection with the MTL.txt landsat extension.
     */
    @Override
    public Collection<String> getSuffix() {
        return Collections.singleton("txt");
    }

    /**
     * @return signature of the landsat MTL file, starting by 'GROUP'
     */
    @Override
    public Collection<byte[]> getSignature() {
        return Collections.singleton(new byte[]{'G','R','O','U','P'});
    }
}

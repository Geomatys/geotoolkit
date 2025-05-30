/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.om.netcdf;

import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.sis.storage.base.Capability;
import org.apache.sis.storage.base.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import static org.apache.sis.storage.DataStoreProvider.LOCATION;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.observation.AbstractObservationStoreFactory;
import org.geotoolkit.observation.Bundle;
import org.geotoolkit.storage.ProviderOnFileSystem;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@StoreMetadata(
        // This store is dedicated to a specific case for NetCDF data, and should not take priority over a generalist NetCDF datastore
        yieldPriority = true,
        formatName = NetcdfObservationStoreFactory.NAME,
        capabilities = {Capability.READ},
        resourceTypes = {})
@StoreMetadataExt(resourceTypes = ResourceType.SENSOR)
public class NetcdfObservationStoreFactory extends AbstractObservationStoreFactory implements ProviderOnFileSystem {

    /** factory identification **/
    public static final String NAME = "observationFile";

    public static final String MIME_TYPE = "application/x-netcdf";

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    /**
     * url to the file.
     */
    public static final ParameterDescriptor<URI> FILE_PATH = new ParameterBuilder()
            .addName("path")
            .addName(LOCATION)
            .addName(Bundle.formatInternational(Bundle.Keys.paramURLAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramURLRemarks))
            .setRequired(true)
            .create(URI.class, null);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR
            = new ParameterBuilder().addName(NAME).addName("ObservationFileParameters").createGroup(
                    IDENTIFIER, NAMESPACE, FILE_PATH);

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public NetcdfObservationStore open(ParameterValueGroup params) throws DataStoreException {
        return new NetcdfObservationStore(params);
    }

    @Override
    public Collection<String> getSuffix() {
        return Arrays.asList("nc", "cdf");
    }

    @Override
    public Collection<byte[]> getSignature() {
        return Collections.singletonList(new byte[]{'C', 'D', 'F'});
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        return FeatureStoreUtilities.probe(this, connector, MIME_TYPE);
    }

    @Override
    public DataStore open(StorageConnector sc) throws DataStoreException {
        final Path p = sc.commit(Path.class, NAME);
        return new NetcdfObservationStore(p);
    }
}

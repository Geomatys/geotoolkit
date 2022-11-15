/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.hdf;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.ProviderOnFileSystem;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * HDF-5 provider.
 *
 * Specification :https://docs.hdfgroup.org/hdf5/develop/_f_m_t3.html
 *
 * @author Johann Sorel (Geomatys)
 */
@StoreMetadata(formatName   = HDF5Provider.NAME,
               fileSuffixes = {"h5"},
               capabilities = Capability.READ,
               resourceTypes = Resource.class)
public class HDF5Provider extends DataStoreProvider implements ProviderOnFileSystem  {

    public static final String NAME = "gsmap";
    public static final String MIME_TYPE = "application/x-hdf5";

    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName(LOCATION)
            .setRequired(true)
            .create(URI.class, null);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR = new ParameterBuilder()
            .addName(NAME)
            .createGroup(PATH);

    private static HDF5Provider INSTANCE;

    /**
     * Get singleton instance of HDF-5 provider.
     *
     * <p>
     * Note : this method is named after Java 9 service loader provider method.
     * {@link https://docs.oracle.com/javase/9/docs/api/java/util/ServiceLoader.html}
     * </p>
     *
     * @return singleton instance of HDF5Provider
     */
    public static synchronized HDF5Provider provider() {
        if (INSTANCE == null) INSTANCE = new HDF5Provider();
        return INSTANCE;
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
        final Path path = connector.getStorageAs(Path.class);
        if (path == null) {
            return ProbeResult.UNSUPPORTED_STORAGE;
        }

        final String name = path.getFileName().toString().toLowerCase();

        if (name.endsWith(".h5")) {
            return new ProbeResult(true, MIME_TYPE, null);
        }
        return ProbeResult.UNSUPPORTED_STORAGE;
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        final Path path = connector.getStorageAs(Path.class);
        try {
            return new HDF5Store(path);
        } catch (IllegalArgumentException | IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public Collection<String> getSuffix() {
        return Collections.singletonList("h5");
    }

}

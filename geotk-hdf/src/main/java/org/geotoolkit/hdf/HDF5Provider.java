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
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.storage.base.Capability;
import org.apache.sis.storage.base.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * HDF-5 provider.
 *
 * <p>Specification: <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html">HDF5 File Format Specification Version 3.0</a></p>
 *
 * @author Johann Sorel (Geomatys)
 */
@StoreMetadata(formatName   = HDF5Provider.NAME,
               fileSuffixes = {"h5", "hdf5", "he5", "nc"},
               capabilities = Capability.READ,
               resourceTypes = Resource.class)
public class HDF5Provider extends DataStoreProvider  {

    /** HDF-5 format logger. */
    public static final Logger LOGGER = Logger.getLogger("org.geotoolkit.hdf");

    /** Format name, also used as provider short name and parameter group name. */
    public static final String NAME = "hdf5";
    /** MIME type of HDF-5 files. */
    public static final String MIME_TYPE = "application/x-hdf5";

    /** Location of the HDF-5 file, mandatory. */
    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName(LOCATION)
            .setRequired(true)
            .create(URI.class, null);

    /** Parameters required to open an HDF-5 store. */
    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR = new ParameterBuilder()
            .addName(NAME)
            .createGroup(PATH);

    private static HDF5Provider INSTANCE;

    /** Creates a new provider. Consider using the {@link #provider()} singleton instead. */
    public HDF5Provider() {
    }

    /**
     * Get singleton instance of HDF-5 provider.
     *
     * <p>
     * Note : this method is named after Java 9 service loader provider method.
     * See {@link java.util.ServiceLoader}.
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
        if (connector.getStorageAs(Path.class) == null) {
            return ProbeResult.UNSUPPORTED_STORAGE;
        }
        final ByteBuffer buffer = connector.getStorageAs(ByteBuffer.class);
        if (buffer == null) {
            return ProbeResult.UNSUPPORTED_STORAGE;
        }
        if (buffer.remaining() < SuperBlock.SIGNATURE.length) {
            return ProbeResult.INSUFFICIENT_BYTES;
        }
        final long offset = SuperBlock.findSignatureOffset(buffer);
        if (offset == 0) {
            return new ProbeResult(true, MIME_TYPE, null);
        }
        if (offset > 0) {
            /*
            The file is HDF5 but starts with a user block, which this reader can
            not follow because it treats the stored addresses as absolute.
            */
            LOGGER.log(Level.FINE, () -> "Found an HDF5 signature at offset " + offset
                    + ", meaning the file has a user block, which is not supported yet.");
        }
        return ProbeResult.UNSUPPORTED_STORAGE;
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        final Path path = connector.getStorageAs(Path.class);
        try {
            return new HDF5Store(this, path);
        } catch (IllegalArgumentException | IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

}

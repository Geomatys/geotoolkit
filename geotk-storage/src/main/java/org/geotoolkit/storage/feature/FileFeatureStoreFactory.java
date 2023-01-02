/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.storage.feature;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import org.apache.sis.internal.storage.io.IOUtilities;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.ProviderOnFileSystem;

/**
 * FileFeatureStoreFactory for working with formats based on a single URI.
 * <p>
 * This interface provides a mechanism of discovery for DataStoreFactories
 * which support singular files.
 * </p>
 *
 * @author dzwiers
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface FileFeatureStoreFactory extends ProviderOnFileSystem {

    /**
     * A FeatureStore attached to the provided uri, may be created if needed.
     * <p>
     * Please note that additional configuration options may be available
     * via the traditional createFeatureStore( Map ) method provided by the
     * superclass.
     * <p>
     * @param uri The data location for the
     *
     * @return Returns a FeatureStore created from the data source provided.
     *
     * @throws DataStoreException
     */
    FeatureStore createDataStore(URI uri) throws DataStoreException;

    public static <T extends DataStoreProvider & ProviderOnFileSystem> ProbeResult probe(T provider, StorageConnector connector, String mimeType) throws DataStoreException {
        return probe(provider, connector, mimeType, false);
    }

    public static <T extends DataStoreProvider & ProviderOnFileSystem> ProbeResult probe(T provider, StorageConnector connector, String mimeType, boolean alwaysCheckExtension) throws DataStoreException {

        final Collection<byte[]> signatures = provider.getSignature();
        if (signatures.isEmpty()) alwaysCheckExtension = true;

        boolean extValid = false;
        if (alwaysCheckExtension) {
            //we don't have any signature, check file extensions
            final Collection<String> suffix = provider.getSuffix();
            if (!suffix.isEmpty()) {
                final Path path = connector.getStorageAs(Path.class);
                if (path != null) {
                    final String extension = IOUtilities.extension(path).toLowerCase();
                    extValid = suffix.contains(extension);
                }
            }
        }

        if (alwaysCheckExtension) {
            if (!extValid) {
                return ProbeResult.UNSUPPORTED_STORAGE;
            }
        }
        if (signatures.isEmpty()) {
            return extValid ? new ProbeResult(true, mimeType, null) : ProbeResult.UNSUPPORTED_STORAGE;
        }

        final ByteBuffer buffer = connector.getStorageAs(ByteBuffer.class);
        if (buffer != null) {
            for (byte[] signature : signatures) {
                try {
                    buffer.mark();
                    if (buffer.remaining() < signature.length) {
                        continue;
                    }
                    final byte[] candidate = new byte[signature.length];
                    buffer.get(candidate);

                    //compare signatures
                    if (Arrays.equals(signature, candidate)) {
                        return new ProbeResult(true, mimeType, null);
                    }
                } finally {
                    buffer.rewind();
                }
            }
        }
        return ProbeResult.UNSUPPORTED_STORAGE;
    }

}

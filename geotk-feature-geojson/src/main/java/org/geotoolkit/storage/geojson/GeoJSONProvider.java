/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.storage.geojson;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.storage.base.Capability;
import org.apache.sis.storage.base.StoreMetadata;
import org.apache.sis.io.stream.IOUtilities;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import static org.apache.sis.storage.DataStoreProvider.LOCATION;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
@StoreMetadata(
        formatName = GeoJSONProvider.NAME,
        capabilities = {Capability.READ, Capability.WRITE, Capability.CREATE},
        fileSuffixes = {"json", "geojson", "topojson"},
        resourceTypes = {FeatureSet.class})
public final class GeoJSONProvider extends DataStoreProvider {

    public static final String NAME = "geojson";

    public static final String ENCODING = "UTF-8";

    /**
     * The {@value} MIME type.
     */
    public static final String MIME_TYPE = "application/json";
    public static final String MIME_TYPE_CBOR = "application/cbor";

    private static final List<String> EXTENSIONS = Arrays.asList("json", "geojson", "topojson");
    private static final String EXTENSION_CBOR = "cbor";

    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName(LOCATION)
            .addName("path")
            .setRequired(true)
            .create(URI.class, null);

    /**
     * Optional
     */
    public static final ParameterDescriptor<Integer> COORDINATE_ACCURACY = new ParameterBuilder()
            .addName("coordinate_accuracy")
            .addName(Bundle.formatInternational(Bundle.Keys.coordinate_accuracy))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.coordinate_accuracy_remarks))
            .setRequired(false)
            .create(Integer.class, 7);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder()
                    .addName(NAME)
                    .addName(Bundle.formatInternational(Bundle.Keys.datastoreTitle))
                    .setDescription(Bundle.formatInternational(Bundle.Keys.datastoreDescription))
                    .createGroup(PATH, COORDINATE_ACCURACY);

    @Override
    public String getShortName() {
        return NAME;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeoJSONStore open(final ParameterValueGroup params) throws DataStoreException {
        return new GeoJSONStore(this, params);
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        Path p = connector.getStorageAs(Path.class);
        if (p == null) return ProbeResult.UNSUPPORTED_STORAGE;

        String extension = IOUtilities.extension(p).toLowerCase();
        if (EXTENSIONS.contains(extension)) {
            try {
                final ByteBuffer buffer = connector.getStorageAs(ByteBuffer.class);
                final Reader reader;
                if (buffer != null) {
                    buffer.mark();
                    reader = null;
                } else {
                    // User gave us explicitely a Reader (e.g. a StringReader wrapping a String instance).
                    reader = connector.getStorageAs(Reader.class);
                    if (reader == null) {
                        return ProbeResult.UNSUPPORTED_STORAGE;
                    }
                    reader.mark(2048); // Should be no more than {@code StorageConnector.DEFAULT_BUFFER_SIZE / 2}
                }
                boolean ok = false;
                if (nextAfterSpaces(buffer, reader) == '{') {
                    ok = true;
                }
                if (buffer != null) {
                    buffer.reset();
                } else {
                    reader.reset();
                }
                if (ok) {
                    return new ProbeResult(true, MIME_TYPE, null);
                }
            } catch (IOException e) {
                throw new DataStoreException(e);
            }
        } else if (EXTENSION_CBOR.equals(extension)) {
            return new ProbeResult(true, MIME_TYPE_CBOR, null);
        }
        return ProbeResult.UNSUPPORTED_STORAGE;

    }

    /**
     * Returns the next character which is not a white space, or -1 if the end
     * of stream is reached. Exactly one of {@code buffer} and {@code reader}
     * shall be non-null.
     */
    private static int nextAfterSpaces(final ByteBuffer buffer, final Reader reader) throws IOException {
        if (buffer != null) {
            while (buffer.hasRemaining()) {
                final char c = (char) buffer.get();
                if (!Character.isWhitespace(c)) {
                    return c;
                }
            }
            return -1;
        }
        int c;
        while ((c = IOUtilities.readCodePoint(reader)) >= 0) {
            if (!Character.isWhitespace(c)) {
                break;
            }
        }
        return c;
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        try {
            final Path path = connector.getStorageAs(Path.class);
            return new GeoJSONStore(this, path, null);
        } catch (IllegalArgumentException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

}

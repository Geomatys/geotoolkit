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
package org.geotoolkit.data.geojson;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import org.apache.sis.internal.storage.io.IOUtilities;
import java.util.Arrays;
import java.util.Collection;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;

/**
 * @author Quentin Boileau (Geomatys)
 */
@StoreMetadataExt(
        resourceTypes = ResourceType.VECTOR,
        canCreate = true,
        canWrite = true,
        geometryTypes ={Geometry.class,
                        Point.class,
                        LineString.class,
                        Polygon.class,
                        MultiPoint.class,
                        MultiLineString.class,
                        MultiPolygon.class})
public class GeoJSONFeatureStoreFactory extends AbstractFileFeatureStoreFactory implements FileFeatureStoreFactory {

    public static final String NAME = "geojson";
    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final String ENCODING = "UTF-8";

    /**
     * The {@value} MIME type.
     */
    public static final String MIME_TYPE = "application/json";

    public static final String EXT = "json";

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
            new ParameterBuilder().addName(NAME).addName("GeoJSONParameters").createGroup(
                IDENTIFIER, PATH, COORDINATE_ACCURACY);

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
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.datastoreTitle);
    }

    /**
     * Describes the type of data the datastore returned by this factory works
     * with.
     *
     * @return String a human readable description of the type of restore
     *         supported by this datastore.
     */
    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.datastoreDescription);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<String> getSuffix() {
        return Arrays.asList("json", "geojson", "topojson");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeoJSONFeatureStore open(final ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new GeoJSONFeatureStore(params);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeoJSONFeatureStore create(final ParameterValueGroup params) throws DataStoreException {
        return open(params);
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        Path p = connector.getStorageAs(Path.class);
        if (EXT.equals(IOUtilities.extension(p))) {
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
        }
        return ProbeResult.UNSUPPORTED_STORAGE;

    }

    /**
     * Returns the next character which is not a white space, or -1 if the end of stream is reached.
     * Exactly one of {@code buffer} and {@code reader} shall be non-null.
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
            if (!Character.isWhitespace(c)) break;
        }
        return c;
    }

}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.referencing.operation.transform;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.opengis.util.FactoryException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.io.ContentFormatException;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.referencing.factory.NoSuchIdentifiedResource;

import static org.geotoolkit.internal.io.Installation.NTv2;


/**
 * Loaders of {@link NTV2Transform} data. This is a temporary object used only at loading time
 * and discarded once the transform is built.
 *
 * @author Simon Reynard (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.12
 * @module
 */
final class NTv2Loader extends GridLoader {
    /**
     * Size of a key in the header.
     */
    private static final int HEADER_KEY_LENGTH = 8;

    /**
     * Size of a record. This value applies to both the header records and the data
     * records. In the case of header records, this is the size of the key plus the
     * size of the value.
     */
    private static final int RECORD_LENGTH = 16;

    /**
     * The types of some know parameters. Parameters not in this list will be ignored.
     */
    private static final Map<String, Class<?>> TYPES;
    static {
        final Map<String, Class<?>> types = new HashMap<String, Class<?>>(32);
        types.put("NUM_OREC", Integer.class);
        types.put("NUM_SREC", Integer.class);
        types.put("NUM_FILE", Integer.class);
        types.put("GS_TYPE",  String .class);
        types.put("VERSION",  String .class);
        types.put("SYSTEM_F", String .class);
        types.put("SYSTEM_T", String .class);
        types.put("MAJOR_F",  Double .class);
        types.put("MINOR_F",  Double .class);
        types.put("MAJOR_T",  Double .class);
        types.put("MINOR_T",  Double .class);
        types.put("SUB_NAME", String .class);
        types.put("PARENT",   String .class);
        types.put("CREATED",  String .class);
        types.put("UPDATED",  String .class);
        types.put("S_LAT",    Double .class);
        types.put("N_LAT",    Double .class);
        types.put("E_LONG",   Double .class);
        types.put("W_LONG",   Double .class);
        types.put("LAT_INC",  Double .class);
        types.put("LONG_INC", Double .class);
        types.put("GS_COUNT", Integer.class);
        TYPES = types;
    }

    /**
     * The header content. Keys are strings like {@code VERSION}, {@code SYSTEM_F},
     * <var>etc.</var>. Values are {@link String}, {@link Integer} or {@link Double}.
     */
    private final Map<String,Comparable<?>> header;

    /**
     * The number of columns (width) and rows (height) in the grid.
     */
    private int width, height;

    /**
     * The minimum longitude and latitude value covered by this grid (decimal degrees).
     */
    private double xmin, ymin;

    /**
     * The difference between longitude (dx) and latitude (dy) grid points (decimal degrees).
     */
    private double dx, dy;

    /**
     * The latitude/longitude Shift and Precision (optional).
     */
    private float[] latitudeShift, longitudeShift, latitudePrecision, longitudePrecision;

    /**
     * The buffer, created from the {@link #longitudeShift} and {@link #latitudeShift}
     * when first needed.
     */
    private transient DataBuffer buffer;

    /**
     * Create a new loader
     */
    NTv2Loader() {
        super(NTv2Loader.class);
        header = new LinkedHashMap<String,Comparable<?>>();
    }

    /**
     * If a loader already exists for the given file, returns it. Otherwise loads
     * the data and returns a {@code NTv2Loader} instance containing the data.
     *
     * @param  gridFile Name or path to the longitude and latittude difference files.
     * @param  loadPrecision {@code true} if the precision should also be loaded.
     * @throws FactoryException If there is an error reading the grid files.
     */
    public static NTv2Loader loadIfAbsent(final String gridFile, final boolean loadPrecision)
            throws FactoryException
    {
        return loadIfAbsent(NTv2Loader.class, gridFile, gridFile, new Callable<NTv2Loader>() {
            @Override public NTv2Loader call() throws FactoryException {
                return load(gridFile, loadPrecision);
            }
        });
    }

    /**
     * Loads the data and returns a {@code NTv2Loader} instance containing the data.
     *
     * @param  gridFile Name or path to the longitude and latittude difference files.
     * @param  loadPrecision {@code true} if the precision should also be loaded.
     * @throws FactoryException If there is an error reading the grid files.
     */
    private static NTv2Loader load(final String gridFile, final boolean loadPrecision)
            throws FactoryException
    {
        final NTv2Loader loader = new NTv2Loader();
        try {
            final Object gridPath = NTv2.toFileOrURL(NTv2Loader.class, gridFile);
            loader.latitudeGridFile  = gridPath;
            loader.longitudeGridFile = gridPath;
            loader.load(loadPrecision);
            /*
             * After loading, replace the File or URL by the original String
             * argument given by the user. This is in order to discart the user-specific
             * directory or JAR URL that may has been prepend to the file names.
             */
            loader.longitudeGridFile = gridFile;
            loader.latitudeGridFile  = gridFile;
        } catch (IOException cause) {
            String message = Errors.format(Errors.Keys.CANT_READ_FILE_$1, gridFile);
            message = message + ' ' + Descriptions.format(Descriptions.Keys.DATA_NOT_INSTALLED_$3,
                    "NTv2", NTv2.directory(true), "geotk-setup");
            final FactoryException ex;
            if (cause instanceof FileNotFoundException) {
                ex = new NoSuchIdentifiedResource(message, gridFile, cause);
            } else {
                ex = new FactoryException(message, cause);
            }
            throw ex;
        }
        return loader;
    }

    /**
     * Loads the grid data.
     *
     * @param  loadPrecision {@code true} if the precision should also be loaded.
     * @throws IOException If there is an error reading the grid files.
     */
    private void load(final boolean loadPrecision) throws IOException {
        final ReadableByteChannel channel = Channels.newChannel(IOUtilities.open(latitudeGridFile));
        /*
         * Extracts the two first header records wich contain the length of the header.
         * Note that the buffer need to be large enough for containing fully the header.
         * The typical header length is 704 bytes.
         *
         * This code also tries to auto-detect the endieness.
         */
        final ByteBuffer buffer = ByteBuffer.allocate(4096);
        buffer.limit(2*RECORD_LENGTH);
        readFully(channel, buffer);
        int numRecords = buffer.getInt(HEADER_KEY_LENGTH) + buffer.getInt(RECORD_LENGTH + HEADER_KEY_LENGTH);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int altRecords = buffer.getInt(HEADER_KEY_LENGTH) + buffer.getInt(RECORD_LENGTH + HEADER_KEY_LENGTH);
        if (altRecords < numRecords) {
            numRecords = altRecords;
            // Keep the little endian order.
        } else {
            // Restore the big original order.
            buffer.order(ByteOrder.BIG_ENDIAN);
        }
        final int headerLimit = (numRecords - 2) * RECORD_LENGTH;
        /*
         * Initializes members with header's parameters values.
         */
        buffer.rewind().limit(headerLimit);
        readFully(channel, buffer);
        final byte[] array = buffer.array();
        final Charset charset = Charset.forName("US-ASCII");
        for (int i=0; i<headerLimit; i+=RECORD_LENGTH) {
            String key = new String(array, i, HEADER_KEY_LENGTH, charset).trim().toUpperCase(Locale.US);
            final Class<?> type = TYPES.get(key);
            if (type != null) {
                final int p = i + HEADER_KEY_LENGTH;
                final Comparable<?> value;
                if (type.equals(Double.class)) {
                    value = buffer.getDouble(p);
                } else if (type.equals(Integer.class)) {
                    value = buffer.getInt(p);
                } else {
                    value = new String(array, p, RECORD_LENGTH - HEADER_KEY_LENGTH, charset).trim();
                }
                key = key.intern(); // Same instance than the one in the TYPES map.
                header.put(key, value);
            }
        }
        /*
         * Get the bounding box in seconds of angle.
         */
        final double xmax, ymax;
        ymin   = getDouble("S_LAT");
        ymax   = getDouble("N_LAT");
        xmin   = getDouble("E_LONG");
        xmax   = getDouble("W_LONG");
        dy     = getDouble("LAT_INC");
        dx     = getDouble("LONG_INC");
        width  = (int) Math.round((xmax - xmin) / dx) + 1;
        height = (int) Math.round((ymax - ymin) / dy) + 1;
        xmin /= 3600;
        ymin /= 3600;
        dx   /= 3600;
        dy   /= 3600;
        /*
         * Initialize values tables.
         */
        final int count = getInteger("GS_COUNT");
        latitudeShift  = new float[count];
        longitudeShift = new float[count];
        if (loadPrecision) {
            latitudePrecision  = new float[count];
            longitudePrecision = new float[count];
        }
        /*
         * At this point, the header is read. Now prepare a buffer for reading the records.
         */
        final int rowsPerBulk = buffer.capacity() / RECORD_LENGTH;
        for (int index=0; index < count;) {
            buffer.rewind().limit(Math.min(rowsPerBulk, count - index) * RECORD_LENGTH);
            readFully(channel, buffer);
            buffer.rewind();
            while (buffer.hasRemaining()) {
                latitudeShift [index] = buffer.getFloat();
                longitudeShift[index] = buffer.getFloat();
                if (loadPrecision) {
                    latitudePrecision [index] = buffer.getFloat();
                    longitudePrecision[index] = buffer.getFloat();
                } else {
                    buffer.position(buffer.position() + 2*(Float.SIZE / Byte.SIZE));
                }
                index++;
            }
        }
        /*
         * Verify that the file ends with "END".
         */
        buffer.rewind().limit(RECORD_LENGTH);
        readFully(channel, buffer);
        channel.close();
        String key = new String(array, 0, HEADER_KEY_LENGTH, charset).trim().toUpperCase(Locale.US);
        if (!key.equals("END")) {
            throw new IOException(Errors.format(Errors.Keys.FILE_HAS_TOO_MANY_DATA));
        }
    }

    /**
     * Fills all remaining bytes in the given buffer from the given channel.
     *
     * @param  channel the channel to fill the buffer from.
     * @param  buffer The buffer to fill.
     * @throws IOException if there is a problem reading the channel.
     */
    private static void readFully(final ReadableByteChannel channel, final ByteBuffer buffer)
            throws IOException
    {
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) < 0) {
                channel.close();
                throw new EOFException(Errors.format(Errors.Keys.END_OF_DATA_FILE));
            }
        }
    }

    /**
     * Returns the string value for the given key, or null if none.
     */
    final String getString(final String key) {
        final Comparable<?> value = header.get(key);
        return (value != null) ? value.toString() : null;
    }

    /**
     * Returns the double value for the given key, or thrown an exception if the
     * value is not found.
     */
    private double getDouble(final String key) throws ContentFormatException {
        final Comparable<?> value = header.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new ContentFormatException(Errors.format(Errors.Keys.NO_SUCH_ATTRIBUTE_$1, key));
    }

    /**
     * Returns the integer value for the given key, or thrown an exception if the
     * value is not found.
     */
    private int getInteger(final String key) throws ContentFormatException {
        final Comparable<?> value = header.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        throw new ContentFormatException(Errors.format(Errors.Keys.NO_SUCH_ATTRIBUTE_$1, key));
    }

    /**
     * Return the grid dimension
     *
     * @return Dimension
     */
    public final Dimension getSize() {
        return new Dimension(width, height);
    }

    /**
     * Returns the geographic area covered by the grid.
     */
    public final Rectangle2D getArea() {
        return new Rectangle2D.Double(xmin, ymin, dx*width, dy*height);
    }

    /**
     * Creates and returns the data buffer.
     */
    public final synchronized DataBuffer getDataBuffer() {
        if (buffer == null) {
            final boolean hasPrecision = (latitudePrecision != null);
            final float[][] buffers = new float[hasPrecision ? 4 : 2][];
            if (hasPrecision) {
                buffers[3] = latitudePrecision;
                buffers[2] = longitudeShift;
            }
            buffers[1] = latitudeShift;
            buffers[0] = longitudeShift;
            buffer = new DataBufferFloat(buffers, width*height);
        }
        return buffer;
    }
}

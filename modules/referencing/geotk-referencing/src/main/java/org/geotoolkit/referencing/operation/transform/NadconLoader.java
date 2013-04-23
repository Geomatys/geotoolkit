/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.io.*;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Callable;
import java.util.StringTokenizer;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.awt.geom.Rectangle2D;
import java.awt.Dimension;
import java.util.Arrays;

import org.opengis.util.FactoryException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.io.ContentFormatException;
import org.geotoolkit.referencing.factory.NoSuchIdentifiedResource;

import static java.nio.channels.Channels.newChannel;
import static org.geotoolkit.internal.io.IOUtilities.*;
import static org.geotoolkit.internal.io.Installation.NADCON;


/**
 * Base class for loaders of {@link NadconTransform} data. This is a temporary
 * object used only at loading time and discarded once the transform is built.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.00
 * @module
 */
abstract class NadconLoader extends GridLoader {
    /**
     * {@code true} if we are in process of reading longitude, or {@link #false}
     * if we are reading latitude. This is used only in order to format an error
     * message in case of failure.
     */
    transient boolean rx;

    /**
     * The number of columns (width) and rows (height) in the grid.
     */
    int width, height;

    /**
     * The minimum longitude and latitude value covered by this grid (decimal degrees).
     */
    private float xmin, ymin;

    /**
     * The difference between longitude (dx) and latitude (dy) grid points (decimal degrees).
     */
    private float dx, dy;

    /**
     * The longitude and latitude shifts.
     */
    float[] longitudeShift, latitudeShift;

    /**
     * The buffer, created from the {@link #longitudeShift} and {@link #latitudeShift}
     * when first needed.
     */
    private transient DataBuffer buffer;

    /**
     * Creates a new loader.
     */
    NadconLoader() {
        super(NadconLoader.class);
    }

    /**
     * If a loader already exists for the given files, returns it. Otherwise guess the format
     * from the file extension, loads the data and returns a {@code NadconLoader} instance
     * containing the data.
     *
     * @param  longitudeGrid Name or path to the longitude difference file.
     * @param  latitudeGrid  Name or path to the latitude difference file.
     * @return The data.
     * @throws FactoryException If there is an error reading the grid files.
     */
    public static NadconLoader loadIfAbsent(final String longitudeGrid, final String latitudeGrid) throws FactoryException {
        return loadIfAbsent(NadconLoader.class, longitudeGrid, latitudeGrid, new Callable<NadconLoader>() {
            @Override public NadconLoader call() throws FactoryException {
                return load(longitudeGrid, latitudeGrid);
            }
        });
    }

    /**
     * Guesses the format from the file extension, loads the data and returns a
     * {@code NadconLoader} instance containing the data.
     *
     * @param  longitudeGrid Name or path to the longitude difference file.
     * @param  latitudeGrid  Name or path to the latitude difference file.
     * @return The data.
     * @throws FactoryException If there is an error reading the grid files.
     */
    private static NadconLoader load(final String longitudeGrid, final String latitudeGrid)
            throws FactoryException
    {
        boolean rx = false; // Same meaning than the rx field.
        try {
            /*
             * Open the files and instantiate the loader according the extension. We check for the
             * extension after we converted to a File or URL just as a paranoiac safety, in order
             * to discart the query part in an URL (the part after the question mark, if any).
             */
            final Object latitudeGridFile, longitudeGridFile;
            latitudeGridFile = NADCON.toFileOrURL(NadconLoader.class, latitudeGrid);
            final boolean latitudeIsBinary = isBinary(latitudeGridFile, "laa", "las");
            rx = true;
            longitudeGridFile = NADCON.toFileOrURL(NadconLoader.class, longitudeGrid);
            final boolean longitudeIsBinary = isBinary(longitudeGridFile, "loa", "los");
            if (latitudeIsBinary != longitudeIsBinary) {
                throw new FactoryException(Errors.format(Errors.Keys.INCONSISTENT_VALUE));
            }
            final NadconLoader loader;
            if (latitudeIsBinary) {
                loader = new Binary();
            } else {
                loader = new Text();
            }
            loader.latitudeGridFile  = latitudeGridFile;
            loader.longitudeGridFile = longitudeGridFile;
            /*
             * Load the data. After loading, replace the File or URL by the original String
             * argument given by the user. This is in order to discart the user-specific
             * directory or JAR URL that may has been prepend to the file names.
             */
            try {
                loader.load();
            } catch (IOException e) {
                rx = loader.rx;
                throw e; // Continue with the exception handling done below.
            }
            loader.longitudeGridFile = longitudeGrid;
            loader.latitudeGridFile  = latitudeGrid;
            return loader;
        } catch (IOException cause) {
            String message = Errors.format(Errors.Keys.CANT_READ_FILE_1, rx ? longitudeGrid : latitudeGrid);
            message = message + ' ' + Descriptions.format(Descriptions.Keys.DATA_NOT_INSTALLED_3,
                    "NADCON", NADCON.directory(true), "geotk-setup");
            final FactoryException ex;
            if (cause instanceof FileNotFoundException) {
                ex = new NoSuchIdentifiedResource(message, rx ? longitudeGrid : latitudeGrid, cause);
            } else {
                ex = new FactoryException(message, cause);
            }
            throw ex;
        }
    }

    /**
     * Returns {@code true} if the given file is binary, or {@code false} if it is a text file.
     *
     * @param  path   The path to examine.
     * @param  text   The filename extension for text files.
     * @param  binary The filename extension for binary files.
     * @return {@code true} if the given path denotes binary file, or {@code false} for a text file.
     * @throws IOException if the given path denotes neither a binary or text file.
     */
    private static boolean isBinary(final Object path, final String text, final String binary)
            throws IOException
    {
        final String ext = extension(path);
        if (ext.equalsIgnoreCase(binary)) {
            return true;
        } else if (ext.equalsIgnoreCase(text)) {
            return false;
        } else {
            throw new IOException(Errors.format(Errors.Keys.UNSUPPORTED_FILE_TYPE_1, ext));
        }
    }

    /**
     * Initializes the fields from the values read from a NADCON header.
     * This is invoked by subclasses only. The numbers are expected to be:
     * <p>
     * <table>
     *   <tr><th>Type</th>      <th>Meaning</th></tr>
     *   <tr><td>(Integer)</td> <td>Number of columns</td></tr>
     *   <tr><td>(Integer)</td> <td>Number of rows</td></tr>
     *   <tr><td>(Integer)</td> <td>Number of z</td></tr>
     *   <tr><td>(Float)</td>   <td>Minimal x value</td></tr>
     *   <tr><td>(Float)</td>   <td>Increment of x values</td></tr>
     *   <tr><td>(Float)</td>   <td>Minimal y value</td></tr>
     *   <tr><td>(Float)</td>   <td>Increment of y values</td></tr>
     *   <tr><td>(Float)</td>   <td>Angle</td></tr>
     * </table>
     *
     * @param header Numbers read from the NADCON header.
     */
    final void NADCON(final Number[] header) {
        width  = (Integer) header[0];
        height = (Integer) header[1];
        xmin   = (Float)   header[3];
        dx     = (Float)   header[4];
        ymin   = (Float)   header[5];
        dy     = (Float)   header[6];
        final int size = width * height;
        latitudeShift  = new float[size];
        longitudeShift = new float[size];
    }

    /**
     * Loads the grid data.
     *
     * @throws IOException If there is an error reading the grid files.
     */
    abstract void load() throws IOException;

    /**
     * Creates and returns the data buffer.
     */
    public final synchronized DataBuffer getDataBuffer() {
        if (buffer == null) {
            buffer = new DataBufferFloat(new float[][] {longitudeShift, latitudeShift}, width*height);
        }
        return buffer;
    }

    /**
     * Returns the grid dimension.
     */
    public final Dimension getSize() {
        return new Dimension(width, height);
    }

    /**
     * Returns the geographic area covered by the grid.
     */
    public final Rectangle2D getArea() {
        return new Rectangle2D.Float(xmin, ymin, width*dx, height*dy);
    }

    /**
     * Reads latitude and longitude text grid shift file data. The first two lines of the shift
     * data file contain the header, with the first being a description of the grid. The second
     * line contains 8 values separated by spaces. The values are described in the documentation
     * of {@link NadconLoader#NADCON}.
     * <p>
     * Shift data values follow this and are also separated by spaces. Row records are organized
     * from low y (latitude) to high and columns are ordered from low longitude to high.
     *
     * @author Rueben Schulz (UBC)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    private static final class Text extends NadconLoader {
        /**
         * Reads the header of a text file and returns the 8 numbers on the second line.
         *
         * @param  in The input stream.
         * @return The 8 numbers on the second line.
         * @throws IOException if the data files cannot be read.
         */
        private static Number[] readHeader(final BufferedReader in) throws IOException {
            in.readLine(); // Skip header description.
            String line = in.readLine();
            if (line == null) {
                throw new EOFException(Errors.format(Errors.Keys.END_OF_DATA_FILE));
            }
            final StringTokenizer tokens = new StringTokenizer(line);
            int tokenCount = tokens.countTokens();
            if (tokenCount != 8) {
                throw new ContentFormatException(Errors.format(
                        Errors.Keys.UNEXPECTED_HEADER_LENGTH_1, tokenCount));
            }
            String n = null;
            try {
                return new Number[] {
                    Integer.parseInt(n = tokens.nextToken()),
                    Integer.parseInt(n = tokens.nextToken()),
                    Integer.parseInt(n = tokens.nextToken()),
                    Float.parseFloat(n = tokens.nextToken()),
                    Float.parseFloat(n = tokens.nextToken()),
                    Float.parseFloat(n = tokens.nextToken()),
                    Float.parseFloat(n = tokens.nextToken()),
                    Float.parseFloat(n = tokens.nextToken())
                };
            } catch (NumberFormatException e) {
                throw new ContentFormatException(Errors.format(Errors.Keys.UNPARSABLE_NUMBER_1, n), e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        void load() throws IOException {
            final BufferedReader latitudeReader, longitudeReader;
            rx = true; longitudeReader = openLatin(longitudeGridFile);
            rx = false; latitudeReader = openLatin(latitudeGridFile);
            final Number[] header = readHeader(latitudeReader); rx = true;
            if (!Arrays.equals(header, readHeader(longitudeReader))) {
                throw new ContentFormatException(Errors.format(Errors.Keys.GRID_LOCATIONS_UNEQUAL));
            }
            NADCON(header);
            rx = false; read(latitudeReader,  latitudeShift);
            rx = true;  read(longitudeReader, longitudeShift);
        }

        /**
         * Reads all rows from the given file and stores the values in the given grid.
         */
        private static void read(final BufferedReader in, final float[] grid) throws IOException {
            int offset = 0;
            String line;
            while ((line = in.readLine()) != null) {
                final StringTokenizer tokens = new StringTokenizer(line);
                while (tokens.hasMoreElements()) {
                    final String token = tokens.nextToken();
                    final float value;
                    try {
                        value = Float.parseFloat(token);
                    } catch (NumberFormatException e) {
                        throw new ContentFormatException(Errors.format(
                                Errors.Keys.UNPARSABLE_NUMBER_1, token), e);
                    }
                    if (offset >= grid.length) {
                        throw new IOException(Errors.format(Errors.Keys.FILE_HAS_TOO_MANY_DATA));
                    }
                    grid[offset++] = value;
                }
            }
            in.close();
            if (offset < grid.length) {
                throw new EOFException(Errors.format(Errors.Keys.FILE_HAS_TOO_FEW_DATA));
            }
        }
    }

    /**
     * Reads latitude and longitude binary grid shift file data. The file is organized into
     * records, with the first record containing the header information, followed by the shift
     * data. The header values are:
     * <p>
     * <ul>
     *   <li>Text describing grid (64 bytes)</li>
     *   <li>Values described in the documentation of {@link NadconLoader#NADCON}</li>
     * </ul>
     * <p>
     * Each record is (<var>num. columns</var>) &times; (4 bytes) + (4 byte separator) long and
     * the file contains (<var>num.rows</var>) + 1 (for the header) records. The data records
     * (with the grid shift values) are all floats and have a 4 byte separator (0's) before the
     * data. Row records are organized from low <var>y</var> (latitude) to high and columns are
     * orderd from low longitude to high. Everything is written in low byte order.
     */
    private static final class Binary extends NadconLoader {
        /**
         * The header length, in bytes.
         */
        private static final int HEADER_LENGTH = 96;

        /**
         * The length of the description in the header, in bytes.
         */
        private static final int DESCRIPTION_LENGTH = 64;

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
            while ((buffer.remaining() != 0)) {
                if (channel.read(buffer) < 0) {
                    throw new EOFException(Errors.format(Errors.Keys.END_OF_DATA_FILE));
                }
            }
        }

        /**
         * Reads the header of a binary file.
         *
         * @param  in The input stream.
         * @return The 8 numbers extracted from the header.
         * @throws IOException if the data files cannot be read.
         */
        private static Number[] readHeader(final ReadableByteChannel in, final ByteBuffer buffer)
                throws IOException
        {
            readFully(in, buffer);
            buffer.position(DESCRIPTION_LENGTH); // Skip the header description.
            return new Number[] {
                Integer.valueOf(buffer.getInt()),
                Integer.valueOf(buffer.getInt()),
                Integer.valueOf(buffer.getInt()),
                Float  .valueOf(buffer.getFloat()),
                Float  .valueOf(buffer.getFloat()),
                Float  .valueOf(buffer.getFloat()),
                Float  .valueOf(buffer.getFloat()),
                Float  .valueOf(buffer.getFloat()),
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        void load() throws IOException {
            final ReadableByteChannel latitudeChannel, longitudeChannel;
            rx = true; longitudeChannel = newChannel(open(longitudeGridFile));
            rx = false; latitudeChannel = newChannel(open(latitudeGridFile));
            ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            final Number[] header = readHeader(latitudeChannel, buffer);
            rx = true;
            buffer.rewind();
            if (!Arrays.equals(header, readHeader(longitudeChannel, buffer))) {
                throw new ContentFormatException(Errors.format(Errors.Keys.GRID_LOCATIONS_UNEQUAL));
            }
            NADCON(header);
            /*
             * At this point, the headers are read in both files and consistent. Now prepare a
             * buffer for reading the records. This reader if long enough for at least one row,
             * and may be long enough for many rows up to a maximum of 4 kb of memory used.
             */
            final int recordLength = (width + 1) * (Float.SIZE / Byte.SIZE);
            final int rowsPerBulk  = Math.max(1, Math.min(height, 4096/recordLength));
            buffer = ByteBuffer.allocateDirect(rowsPerBulk * recordLength);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            final FloatBuffer floats = buffer.asFloatBuffer();
            /*
             * Following loop is executed twice, once for latitudes and once for longitude.
             * Before to read the data, we need to skip extra padding values after the header.
             */
            rx = false;
            do {
                final ReadableByteChannel channel;
                final float[] grid;
                if (rx) {
                    channel = longitudeChannel;
                    grid    = longitudeShift;
                } else {
                    channel = latitudeChannel;
                    grid    = latitudeShift;
                }
                if (recordLength > HEADER_LENGTH) {
                    buffer.rewind();
                    buffer.limit(recordLength - HEADER_LENGTH);
                    readFully(channel, buffer); // Discart those data.
                }
                buffer.clear();
                int offset = 0;
                int remaining = height;
                while (remaining != 0) {
                    int rowsToRead = rowsPerBulk;
                    if (remaining < rowsToRead) {
                        rowsToRead = remaining;
                        buffer.limit(rowsToRead * recordLength);
                    }
                    readFully(channel, buffer);
                    floats.rewind();
                    for (int i=0; i<rowsToRead; i++) {
                        final float check = floats.get();
                        if (check != 0) {
                            throw new ContentFormatException();
                        }
                        floats.get(grid, offset, width);
                        offset += width;
                    }
                    remaining -= rowsToRead;
                    buffer.rewind();
                }
                /*
                 * To be strict, ensure that there is no additional data to read.
                 */
                buffer.limit(1);
                final int r = channel.read(buffer);
                channel.close();
                if (r >= 0) {
                    throw new IOException(Errors.format(Errors.Keys.FILE_HAS_TOO_MANY_DATA));
                }
            } while ((rx = !rx) == true);
        }
    }
}

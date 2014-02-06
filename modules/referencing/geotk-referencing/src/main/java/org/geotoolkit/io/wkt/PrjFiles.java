/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.io.wkt;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.io.wkt.Convention;
import org.apache.sis.io.wkt.WKTFormat;

import org.geotoolkit.lang.Static;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.io.ContentFormatException;


/**
 * Parses and formats PRJ files. Those files usually have the {@code ".prj"} suffix and contain
 * the definition of exactly one <cite>Coordinate Reference System</cite> (CRS) in <cite>Well
 * Known Text</cite> (WKT) format. This class allows the definition to span many lines, but the
 * common practice is to provide the full WKT string on a single line.
 * <p>
 * If the definition is not a valid WKT string (during reads), or if a CRS can not be formatted as
 * a WKT (during writes), then the methods in this class throw a {@link ContentFormatException}.
 * This is a subclass of {@link IOException} and consequently does not require a <code>try &hellip;
 * catch</code> block different than the one for normal I/O operations.
 * <p>
 * Every files are expected to use the ISO-8859-1 (a.k.a. ISO-LATIN-1) encoding.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.05
 * @module
 */
public final class PrjFiles extends Static {
    /**
     * The encoding of PRJ files.
     */
    private static final String ENCODING = "ISO-8859-1";

    /**
     * Do not allow instantiation of this class.
     */
    private PrjFiles() {
    }

    /**
     * Parses a PRJ file and returns its content as a coordinate reference system.
     *
     * @param  file The file to parse. It usually has the {@code ".prj"} suffix,
     * @return The PRJ file content as a coordinate reference system.
     * @throws ContentFormatException If the content of the PRJ file is an invalid WKT.
     * @throws IOException If the parsing failed for an other raison.
     */
    public static CoordinateReferenceSystem read(final File file) throws ContentFormatException, IOException {
        return read(new FileInputStream(file), true);
    }

    /**
     * Parses a PRJ file from a URL and returns its content as a coordinate reference system.
     *
     * @param  file The file to parse. It usually has the {@code ".prj"} suffix,
     * @return The PRJ file content as a coordinate reference system.
     * @throws ContentFormatException If the content of the PRJ file is an invalid WKT.
     * @throws IOException If the parsing failed for an other raison.
     */
    public static CoordinateReferenceSystem read(final URL file) throws ContentFormatException, IOException {
        return read(file.openStream(), true);
    }

    /**
     * Parses a PRJ file from a channel and returns its content as a coordinate reference system.
     * The given channel is not closed by this method, unless the {@code close} argument is set to
     * {@code true}. The later case allows this method to close the channel sooner than what could
     * be achieved if the channel was closed by the caller: before the WKT parsing begin.
     *
     * @param  in The channel to read.
     * @param  close Whatever this method should close the channel.
     * @return The PRJ file content as a coordinate reference system.
     * @throws ContentFormatException If the content of the PRJ file is an invalid WKT.
     * @throws IOException If the parsing failed for an other raison.
     *
     * @since 3.07
     */
    public static CoordinateReferenceSystem read(final ReadableByteChannel in, final boolean close)
            throws ContentFormatException, IOException
    {
        return read(Channels.newInputStream(in), close);
    }

    /**
     * Parses a PRJ file from a stream and returns its content as a coordinate reference system.
     * The given stream is not closed by this method, unless the {@code close} argument is set to
     * {@code true}. The later case allows this method to close the stream sooner than what could
     * be achieved if the stream was closed by the caller: before the WKT parsing begin.
     *
     * @param  in The stream to read.
     * @param  close Whatever this method should close the stream.
     * @return The PRJ file content as a coordinate reference system.
     * @throws ContentFormatException If the content of the PRJ file is an invalid WKT.
     * @throws IOException If the parsing failed for an other raison.
     */
    public static CoordinateReferenceSystem read(final InputStream in, final boolean close)
            throws ContentFormatException, IOException
    {
        return read(new BufferedReader(new InputStreamReader(in, ENCODING)), close);
    }

    /**
     * Parses a PRJ file from a reader and returns its content as a coordinate reference system.
     * The given reader is not closed by this method, unless the {@code close} argument is set to
     * {@code true}. The later case allows this method to close the reader sooner than what could
     * be achieved if the reader was closed by the caller: before the WKT parsing begin.
     *
     * @param  in The reader.
     * @param  close Whatever this method should close the reader.
     * @return The PRJ file content as a coordinate reference system.
     * @throws ContentFormatException If the content of the PRJ file is an invalid WKT.
     * @throws IOException If the parsing failed for an other raison.
     */
    public static CoordinateReferenceSystem read(final BufferedReader in, final boolean close)
            throws ContentFormatException, IOException
    {
        StringBuilder buffer = null;
        String wkt=null, line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                if (wkt == null) {
                    wkt = line;
                } else {
                    if (buffer == null) {
                        buffer = new StringBuilder(wkt);
                    }
                    buffer.append('\n').append(line);
                }
            }
        }
        if (close) {
            in.close();
        }
        if (buffer != null) {
            wkt = buffer.toString();
        }
        if (wkt == null) {
            throw new EOFException(Errors.format(Errors.Keys.END_OF_DATA_FILE));
        }
        try {
            return CRS.parseWKT(wkt);
        } catch (FactoryException e) {
            throw new ContentFormatException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Formats the given CRS as a string.
     *
     * @throws ContentFormatException if the given CRS is not formattable as a WKT.
     */
    private static String format(final CoordinateReferenceSystem crs) throws ContentFormatException {
        final WKTFormat format = new WKTFormat(null, null);
        format.setConvention(Convention.WKT1);
        format.setIndentation(WKTFormat.SINGLE_LINE);
        final String wkt = format.format(crs);
        final String warning = format.getWarning();
        if (warning != null) {
            throw new ContentFormatException(warning);
        }
        return wkt;
    }

    /**
     * Formats a coordinate reference system as a PRJ file.
     * The file is created only if the given CRS is formattable.
     *
     * @param  crs The PRJ file content as a coordinate reference system.
     * @param  file The file to create. It usually has the {@code ".prj"} suffix,
     * @throws ContentFormatException if the given CRS is not formattable as a WKT.
     * @throws IOException If an other error occurred while writing the file.
     */
    public static void write(final CoordinateReferenceSystem crs, final File file)
            throws ContentFormatException, IOException
    {
        final String wkt = format(crs);
        // No need to buffer, because we will write everything (except EOL) in one shot.
        // In addition, OutputStreamWriter already manage its own internal buffer anyway.
        try (Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODING)) {
            out.write(wkt);
            out.write('\n'); // Use Unix EOL for cross-platform consistency.
        }
    }

    /**
     * Formats a coordinate reference system as a PRJ file in the given channel. The channel
     * is <strong>not</strong> closed by this method. It is caller responsibility to close it.
     *
     * @param  crs The PRJ file content as a coordinate reference system.
     * @param  out The channel where to write.
     * @throws ContentFormatException if the given CRS is not formattable as a WKT.
     * @throws IOException If an other error occurred while writing the file.
     *
     * @since 3.07
     */
    public static void write(final CoordinateReferenceSystem crs, final WritableByteChannel out)
            throws ContentFormatException, IOException
    {
        write(crs, Channels.newOutputStream(out));
    }

    /**
     * Formats a coordinate reference system as a PRJ file in the given stream. The stream
     * is <strong>not</strong> closed by this method. It is caller responsibility to close it.
     *
     * @param  crs The PRJ file content as a coordinate reference system.
     * @param  out The stream where to write.
     * @throws ContentFormatException if the given CRS is not formattable as a WKT.
     * @throws IOException If an other error occurred while writing the file.
     */
    public static void write(final CoordinateReferenceSystem crs, final OutputStream out)
            throws ContentFormatException, IOException
    {
        // No need to buffer - see the above method.
        final Writer writer = new OutputStreamWriter(out, ENCODING);
        write(crs, writer);
        writer.flush();
    }

    /**
     * Formats a coordinate reference system as a PRJ file in the given writer. The writer
     * is <strong>not</strong> closed by this method. It is caller responsibility to close it.
     *
     * @param  crs The PRJ file content as a coordinate reference system.
     * @param  out The writer.
     * @throws ContentFormatException if the given CRS is not formattable as a WKT.
     * @throws IOException If an other error occurred while writing the file.
     */
    public static void write(final CoordinateReferenceSystem crs, final Writer out)
            throws ContentFormatException, IOException
    {
        out.write(format(crs));
        out.write('\n'); // Use Unix EOL for cross-platform consistency.
        // No close - this is caller responsibility.
    }
}

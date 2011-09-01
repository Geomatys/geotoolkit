/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.RandomAccessFile;


/**
 * Read lines of text from various sources like {@link BufferedReader}, {@link RandomAccessFile}
 * or multi-lines {@link String}. Implementations of this interface accept the Unix and Windows
 * style of <cite>End Of Line</cite> (EOL).
 * <p>
 * Instances of {@code LineReader} are created by the static methods defined in
 * {@link LineReaders}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see java.io.DataInput#readLine()
 * @see BufferedReader#readLine()
 *
 * @since 3.02
 * @module
 */
public interface LineReader extends Closeable {
    /**
     * Returns the next line, or {@code null} if there is no more line to read.
     * A line is considered to be terminated by any one of a line feed ({@code '\n'}),
     * a carriage return ({@code '\r'}), or a carriage return followed immediately by
     * a linefeed.
     *
     * @return The next line, or {@code null} on <cite>End Of File</cite> (EOF).
     * @throws IOException If an error occurred while reading the line.
     */
    String readLine() throws IOException;
}

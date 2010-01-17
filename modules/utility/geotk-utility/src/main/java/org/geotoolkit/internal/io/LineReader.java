/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal.io;

import java.io.Reader;
import java.io.IOException;
import java.io.LineNumberReader;


/**
 * A buffered character-input stream that keeps track of line numbers
 * and stream position. This class can't be public for now, because I
 * can't figure out how to implement reliably {@link #readLine}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
public final class LineReader extends LineNumberReader {
    /**
     * The current stream position.
     */
    private long position;

    /**
     * The position of the mark, if any.
     */
    private long markedPosition;

    /**
     * Creates a new line reader, using the default input-buffer size.
     *
     * @param in a Reader object to provide the underlying stream.
     */
    public LineReader(final Reader in) {
        super(in);
    }

    /**
     * Creates a new line reader, reading characters into a buffer of the given size.
     *
     * @param in   a Reader object to provide the underlying stream.
     * @param size an int specifying the size of the buffer.
     */
    public LineReader(final Reader in, final int size) {
        super(in, size);
    }

    /**
     * Gets the current stream position.
     *
     * @return The current stream position.
     */
    public long getPosition() {
        return position;
    }

    /**
     * Reads a single character.
     *
     * @return The character read, or -1 if the end of the stream has been reached.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        synchronized (lock) {
            final int c = super.read();
            if (c >= 0) {
                position++;
            }
            return c;
        }
    }

    /**
     * Reads characters into a portion of an array.
     *
     * @param  cbuf  Destination buffer
     * @param  off   Offset at which to start storing characters
     * @param  len   Maximum number of characters to read
     * @return The number of bytes read, or -1 if the end of the stream has already been reached.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public int read(final char cbuf[], final int off, final int len) throws IOException {
        synchronized (lock) {
            final int n = super.read(cbuf, off, len);
            if (n >= 0) {
                position += n;
            }
            return n;
        }
    }

    /**
     * Reads a line of text.  A line is considered to be terminated by any one
     * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
     * followed immediately by a linefeed.
     *
     * @return A String containing the contents of the line, not including
     *         any line-termination characters, or null if the end of the
     *         stream has been reached
     * @throws IOException  If an I/O error occurs.
     */
    @Override
    public String readLine() throws IOException {
        synchronized (lock) {
            // TODO: Position update is **approximative**. There is no way to
            //       know if 'super.readLine()' found "\r", "\n" or "\r\n".
            final String line = super.readLine();
            if (line != null) {
                position += line.length() + 1;
            }
            return line;
        }
    }

    /**
     * Marks the present position in the stream.  Subsequent calls to {@link #reset}
     * will attempt to reposition the stream to this point, and will also reset
     * the stream position appropriately.
     *
     * @param  readAheadLimit Limit on the number of characters that may be read while still
     *         preserving the mark.  After reading this many characters, attempting to reset
     *         the stream may fail.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void mark(final int readAheadLimit) throws IOException {
        synchronized (lock) {
            super.mark(readAheadLimit);
            markedPosition = position;
        }
    }

    /**
     * Resets the stream to the most recent mark.
     *
     * @throws IOException If the stream has not been marked, or if the mark has been invalidated.
     */
    @Override
    public void reset() throws IOException {
        synchronized (lock) {
            super.reset();
            position = markedPosition;
        }
    }
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.io.Writer;
import java.io.FilterWriter;
import java.io.IOException;
import org.geotoolkit.lang.Decorator;
import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.resources.Errors;


/**
 * Wraps the lines at the specified maximal line length. The default line
 * length is 80 characters. The maximal line length can be changed by a call
 * to {@link #setMaximalLineLength}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@ThreadSafe
@Decorator(Writer.class)
public class LineWrapWriter extends FilterWriter {
    /**
     * Hyphen character. Line break may be inserted after this character.
     * The graphical symbol is similar to the usual {@code '-'} character.
     * For non-breaking hyphen, use the {@code '\u2011'} character.
     */
    public static final char HYPHEN = '\u2010';

    /**
     * Hyphen character to be visible only if there is a line break to insert after it.
     * Otherwise this character is invisible. When visible, the graphical symbol is similar
     * to the {@link #HYPHEN} character.
     * <p>
     * This is equivalent to the HTML {@code &shy;} entity.
     */
    public static final char SOFT_HYPHEN = '\u00AD';

    /**
     * The escape character. Characters following the escape one will not be counted up to
     * the first non-digit character (inclusive).
     */
    private static final char ESCAPE = '\u001B';

    /**
     * The character to ignore after the escape character, in addition of digits.
     * It should be the same than in {@link X364}.
     */
    private static final char IGNORE_AFTER_ESCAPE = '[';

    /**
     * The line separator.
     */
    private final String lineSeparator = System.getProperty("line.separator", "\n");

    /**
     * The maximal line length.
     */
    private int maximalLineLength = 80;

    /**
     * The length of the current line. It may be greater than the length
     * of {@link #buffer} because the later contains only the last word.
     */
    private int length;

    /**
     * {@code true} if an escape sequence is in progress. The escape sequence will stop
     * after the first non-digit character other than {@link #IGNORE_AFTER_ESCAPE}.
     */
    private boolean escape;

    /**
     * The buffer for the last word being written.
     */
    private final StringBuilder buffer = new StringBuilder();

    /**
     * Constructs a stream which will wraps the lines at 80 characters.
     * The maximal line length can be changed by a call to {@link #setMaximalLineLength}.
     *
     * @param out The underlying stream to write to.
     */
    public LineWrapWriter(final Writer out) {
        super(out);
    }

    /**
     * Constructs a stream which will wraps the lines at the given maximal amount of
     * characters. This is a convenience constructor invoking {@link #setMaximalLineLength}
     * immediately after construction.
     *
     * @param out The underlying stream to write to.
     * @param length The maximal line length.
     */
    public LineWrapWriter(final Writer out, final int length) {
        super(out);
        setMaximalLineLength(length);
    }

    /**
     * Returns the maximal line length. The default value is 80.
     *
     * @return The current maximal line length.
     */
    public int getMaximalLineLength() {
        return maximalLineLength;
    }

    /**
     * Sets the maximal line length.
     *
     * @param length The new maximal line length.
     */
    public void setMaximalLineLength(final int length) {
        if (length <= 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.NOT_GREATER_THAN_ZERO_$1, length));
        }
        maximalLineLength = length;
    }

    /**
     * Removes the {@value #SOFT_HYPHEN} characters from the given buffer. This is invoked
     * when the buffer is about to be written without being split on two lines.
     */
    private static void deleteSoftHyphen(final StringBuilder buffer) {
        for (int i=buffer.length(); --i>=0;) {
            if (buffer.charAt(i) == SOFT_HYPHEN) {
                buffer.deleteCharAt(i);
            }
        }
    }

    /**
     * Writes the specified character.
     *
     * @throws IOException If an I/O error occurs.
     */
    @SuppressWarnings("fallthrough")
    private void doWrite(final int c) throws IOException {
        assert Thread.holdsLock(lock);
        final StringBuilder buffer = this.buffer; // Avoid frequent "getField" op.
        switch (c) {
            case '\r': // fall through
            case '\n': {
                deleteSoftHyphen(buffer);
                out.append(buffer).write(c);
                buffer.setLength(0);
                length = 0;
                escape = false; // Handle line-breaks as "end of escape sequence".
                return;
            }
            case ESCAPE: {
                buffer.append(ESCAPE);
                escape = true;
                return;
            }
        }
        if (Character.isSpaceChar(c)) {
            deleteSoftHyphen(buffer);
            out.append(buffer);
            buffer.setLength(0);
            escape = false; // Handle spaces as "end of escape sequence".
        }
        buffer.append((char) c);
        /*
         * Special handling of ANSI X3.64 escape sequences. Since they are not visible
         * characters (they are used for controlling the colors), do not count them.
         */
        if (escape) {
            if (c < '0' || c > '9') {
                if (c == IGNORE_AFTER_ESCAPE) {
                    final int previous = buffer.length() - 2;
                    if (previous >= 0 && buffer.charAt(previous) == ESCAPE) {
                        return; // Found the character to ignore.
                    }
                }
                escape = false;
                // The first character after the digits is not counted neither,
                // so we exit this method for it too.
            }
            return;
        }
        /*
         * The remainder of this method is executed only if we have exceeded the maximal line
         * length. First search for the hyphen character, if any. If we find one and if it is
         * preceeded by a letter, split there. The "letter before" condition is a way to avoid
         * to split at the minus sign of negative numbers like "-99", assuming that the minus
         * sign is preceeded by a space. We can not look at the character after since we may
         * not know it yet.
         */
        if (++length > maximalLineLength) {
hyphen:     for (int i=buffer.length(); --i>=1;) {
                switch (buffer.charAt(i)) {
                    case '-': {
                        if (!Character.isLetter(buffer.charAt(i-1))) {
                            continue;
                        }
                        // fall through
                    }
                    case HYPHEN: // Fall through
                    case SOFT_HYPHEN: {
                        out.append(buffer.substring(0, ++i));
                        buffer.delete(0, i);
                        break hyphen;
                    }
                }
            }
            out.write(lineSeparator);
            length = buffer.length();
            for (int i=0; i<length; i++) {
                if (!Character.isSpaceChar(buffer.charAt(i))) {
                    buffer.delete(0, i);
                    length -= i;
                    return;
                }
            }
            // If we reach this point, only spaces were found in the buffer.
            buffer.setLength(0);
            length = 0;
        }
    }

    /**
     * Writes a single character.
     *
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void write(final int c) throws IOException {
        synchronized (lock) {
            doWrite(c);
        }
    }

    /**
     * Writes a portion of an array of characters.
     *
     * @param  buffer  Buffer of characters to be written.
     * @param  offset  Offset from which to start reading characters.
     * @param  length  Number of characters to be written.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void write(final char[] buffer, int offset, int length) throws IOException {
        synchronized (lock) {
            while (--length >= 0) {
                doWrite(buffer[offset++]);
            }
        }
    }

    /**
     * Writes a portion of a string.
     *
     * @param  string  String to be written.
     * @param  offset  Offset from which to start reading characters.
     * @param  length  Number of characters to be written.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void write(final String string, int offset, int length) throws IOException {
        synchronized (lock) {
            while (--length >= 0) {
                doWrite(string.charAt(offset++));
            }
        }
    }

    /**
     * Sends pending characters to the underlying stream. Note that this method should
     * preferably be invoked at the end of a word, sentence or line, since invoking it
     * may prevent {@code LineWrapWriter} to properly wrap the current line if it is in
     * the middle of a word.
     * <p>
     * Invoking this method also flush the {@linkplain #out underlying stream}. A cheapier
     * way to send pending characters is to make sure that the last character is a
     * line terminator ({@code '\r'} or {@code '\n'}).
     *
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void flush() throws IOException {
        synchronized (lock) {
            out.append(buffer);
            buffer.setLength(0);
            out.flush();
        }
    }

    /**
     * Sends pending characters to the underlying stream and close it.
     *
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        synchronized (lock) {
            out.append(buffer);
            buffer.setLength(0);
            out.close();
        }
    }
}

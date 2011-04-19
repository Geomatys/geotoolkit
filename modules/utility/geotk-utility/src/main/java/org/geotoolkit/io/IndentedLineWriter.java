/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2011, Open Source Geospatial Foundation (OSGeo)
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

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import net.jcip.annotations.ThreadSafe;

import org.geotoolkit.util.Strings;
import org.geotoolkit.lang.Decorator;
import org.geotoolkit.util.ArgumentChecks;


/**
 * A writer that put some spaces in front of every lines. The indentation is initially set
 * to 0 spaces. Users must invoke {@link #setIndentation(int)} or {@link #setMargin(String)}
 * in order to set a different value.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.4
 * @module
 */
@ThreadSafe
@Decorator(Writer.class)
public class IndentedLineWriter extends FilterWriter {
    /**
     * A string with a length equal to the indentation.
     */
    private String margin = "";

    /**
     * {@code true} if we are about to write a new line.
     */
    private boolean newLine = true;

    /**
     * {@code true} if we are waiting for a {@code '\n'} character.
     */
    private boolean waitLF;

    /**
     * Constructs a stream which will add spaces in front of each line.
     * The {@link #setIndentation(int)} or {@link #setMargin(String)}
     * method must be invoked after this constructor in order to specify
     * the amount of spaces to add.
     *
     * @param out The underlying stream to write to.
     */
    public IndentedLineWriter(final Writer out) {
        super(out);
    }

    /**
     * Constructs a stream which will the given amount of spaces in front of each line.
     * This is a convenience method invoking {@link #setIndentation(int)} right after the
     * construction.
     *
     * @param out The underlying stream to write to.
     * @param width The indentation.
     *
     * @since 3.00
     */
    public IndentedLineWriter(final Writer out, final int width) {
        super(out);
        setIndentation(width);
    }

    /**
     * Returns the current indentation. This is either the value given to the last call
     * to {@link #setIndentation(int)} method, or the length of the string given to the
     * {@link #setMargin(String)} method.
     *
     * @return The current indentation.
     */
    public int getIdentation() {
        synchronized (lock) {
            return margin.length();
        }
    }

    /**
     * Sets the indentation to the specified value. This method will {@linkplain #setMargin(String)
     * defines a margin} as the given number of white spaces.
     *
     * @param width The number of space to insert at the beginning of every line.
     */
    public void setIndentation(final int width) {
        synchronized (lock) {
            margin = Strings.spaces(width);
        }
    }

    /**
     * Returns the margin which is written at the beginning of every line. The default
     * value is an empty string. This value can be modified either explicitely by a call to
     * {@link #setMargin(String)}, or implicitly by a call to {@link #setIndentation(int)}.
     *
     * @return The string which is inserted at the beginning of every lines.
     *
     * @since 3.18
     */
    public String getMargin() {
        synchronized (lock) {
            return margin;
        }
    }

    /**
     * Sets the margin to be written at the beginning of every line.
     *
     * @param margin The string to be inserted at the beginning of every lines.
     *
     * @since 3.18
     */
    public void setMargin(final String margin) {
        ArgumentChecks.ensureNonNull("margin", margin);
        synchronized (lock) {
            this.margin = margin;
        }
    }

    /**
     * Invoked when a new line is beginning. The default implementation writes the
     * amount of spaces specified by the last call to {@link #setIndentation}.
     *
     * @throws IOException If an I/O error occurs
     */
    protected void beginNewLine() throws IOException {
        out.write(margin);
    }

    /**
     * Writes the specified character.
     *
     * @throws IOException If an I/O error occurs.
     */
    private void doWrite(final int c) throws IOException {
        assert Thread.holdsLock(lock);
        if (newLine && (c!='\n' || !waitLF)) {
            beginNewLine();
        }
        out.write(c);
        if ((newLine = (c=='\r' || c=='\n')) == true) {
            waitLF = (c=='\r');
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
    public void write(final char[] buffer, int offset, final int length) throws IOException {
        final int upper = offset + length;
        synchronized (lock) {
check:      while (offset < upper) {
                if (newLine) {
                    doWrite(buffer[offset++]);
                    continue;
                }
                final int lower = offset;
                do {
                    final char c = buffer[offset];
                    if (c=='\r' || c=='\n') {
                        out.write(buffer, lower, offset-lower);
                        doWrite(c);
                        offset++;
                        continue check;
                    }
                } while (++offset < upper);
                out.write(buffer, lower, offset-lower);
                break;
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
    public void write(final String string, int offset, final int length) throws IOException {
        final int upper = offset + length;
        synchronized (lock) {
check:      while (offset < upper) {
                if (newLine) {
                    doWrite(string.charAt(offset++));
                    continue;
                }
                final int lower = offset;
                do {
                    final char c = string.charAt(offset);
                    if (c=='\r' || c=='\n') {
                        out.write(string, lower, offset-lower);
                        doWrite(c);
                        offset++;
                        continue check;
                    }
                } while (++offset < upper);
                out.write(string, lower, offset-lower);
                break;
            }
        }
    }
}

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
package org.geotoolkit.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import net.jcip.annotations.ThreadSafe;

import org.geotoolkit.util.Strings;
import org.geotoolkit.lang.Decorator;
import org.geotoolkit.internal.io.IOUtilities;


/**
 * A writer that put line number in front of every line.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.io.LineAppender}
 * (developers need to override {@code onLineBegin(boolean)}).
 */
@Deprecated
@ThreadSafe
@Decorator(Writer.class)
public class NumberedLineWriter extends IndentedLineWriter {
    /**
     * A default numbered line writer to the {@linkplain System#out standard output stream}.
     * The {@link #close} method on this stream will only flush it without closing it.
     */
    private static PrintWriter stdout;

    /**
     * Returns a default numbered line writer to the {@linkplain System#out standard output stream}.
     * The {@link #close} method on this stream will only flush it without closing it.
     *
     * @return A unique numbered line writer to the standard output stream.
     */
    public static synchronized PrintWriter getStandardOutput() {
        if (stdout == null) {
            stdout = new PrintWriter(IOUtilities.standardWriter(), true);
        }
        return stdout;
    }

    /**
     * The with reserved for line numbers (not counting the space for "[ ]" brackets).
     */
    private int width = 3;

    /**
     * The current line number.
     */
    private int current = 1;

    /**
     * Constructs a stream which will write line number in front of each line.
     *
     * @param out The underlying stream to write to.
     */
    public NumberedLineWriter(final Writer out) {
        super(out);
    }

    /**
     * Returns the current line number.
     *
     * @return The current line number.
     */
    public int getLineNumber() {
        return current;
    }

    /**
     * Sets the current line number.
     *
     * @param line The current line number.
     */
    public void setLineNumber(final int line) {
        synchronized (lock) {
            this.current = line;
        }
    }

    /**
     * Invoked when a new line is beginning. The default implementation writes the
     * current line number.
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void beginNewLine() throws IOException {
        final String number = String.valueOf(current++);
        out.write('[');
        out.write(Strings.spaces(width - number.length()));
        out.write(number);
        out.write("] ");
    }
}

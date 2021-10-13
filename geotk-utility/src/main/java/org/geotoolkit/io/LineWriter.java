/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.io.Writer;
import org.apache.sis.io.IO;
import org.apache.sis.io.LineAppender;

import org.geotoolkit.lang.Decorator;


/**
 * Writes characters to a stream while replacing various EOL by a unique string. This class
 * catches all occurrences of {@code "\r"}, {@code "\n"} and {@code "\r\n"}, and replaces them
 * by the platform depend EOL string ({@code "\r\n"} on Windows, {@code "\n"} on Unix), or any
 * other EOL explicitly set at construction time. This writer also remove trailing blanks before
 * end of lines, but this behavior can be changed by overriding {@link #isWhitespace}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link LineAppender}.
 */
@Deprecated
@Decorator(Writer.class)
public class LineWriter extends FilterWriter {
    /**
     * The Apache SIS formatter on which to delegate the work.
     */
    private final LineAppender formatter;

    /**
     * Workaround for RFE #4093999 ("Relax constraint on placement of this()/super()
     * call in constructors")
     */
    private LineWriter(final LineAppender formatter) {
        super(IO.asWriter(formatter));
        this.formatter = formatter;
    }


    /**
     * Constructs a {@code LineWriter} object that will use the platform dependent line separator.
     *
     * @param  out A writer object to provide the underlying stream.
     * @throws IllegalArgumentException if {@code out} is {@code null}.
     */
    public LineWriter(final Writer out) {
        this(out, System.lineSeparator());
    }

    /**
     * Constructs a {@code LineWriter} object that will use the specified line separator.
     *
     * @param  out A writer object to provide the underlying stream.
     * @param  lineSeparator String to use as line separator.
     */
    public LineWriter(final Writer out, final String lineSeparator) {
        this(new LineAppender(out));
        formatter.setLineSeparator(lineSeparator);
    }

    /**
     * Returns the current line separator.
     *
     * @return The current line separator.
     */
    public String getLineSeparator() {
        return formatter.getLineSeparator();
    }

    /**
     * Changes the line separator. This is the string to insert in place of every occurrences of
     * {@code "\r"}, {@code "\n"} or {@code "\r\n"}.
     *
     * @param  lineSeparator The new line separator.
     */
    public void setLineSeparator(final String lineSeparator) {
        synchronized (lock) {
            formatter.setLineSeparator(lineSeparator);
        }
    }
}

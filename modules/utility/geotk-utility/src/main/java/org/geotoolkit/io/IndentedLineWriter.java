/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.io.Writer;
import org.apache.sis.io.IO;
import org.apache.sis.io.LineAppender;
import org.apache.sis.util.ArgumentChecks;

import org.apache.sis.util.CharSequences;
import org.geotoolkit.lang.Decorator;


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
 *
 * @deprecated Moved to Apache SIS as {@link LineAppender}.
 */
@Deprecated
@Decorator(Writer.class)
public class IndentedLineWriter extends FilterWriter {
    /**
     * The Apache SIS formatter on which to delegate the work, with delegation of
     * {@link #onLineBegin(boolean)} calls to the Geotk {@link #beginNewLine()}.
     */
    private static class Formatter extends LineAppender {
        IndentedLineWriter enclosing;

        Formatter(final Writer out) {
            super(out);
        }

        @Override
        protected void onLineBegin(boolean isContinuation) throws IOException {
            enclosing.beginNewLine((Writer) out);
        }
    }

    /**
     * A string with a length equal to the indentation.
     */
    private String margin = "";

    /**
     * Workaround for RFE #4093999 ("Relax constraint on placement of this()/super()
     * call in constructors")
     */
    private IndentedLineWriter(final Formatter formatter) {
        super(IO.asWriter(formatter));
        formatter.enclosing = this;
    }

    /**
     * Constructs a stream which will add spaces in front of each line.
     * The {@link #setIndentation(int)} or {@link #setMargin(String)}
     * method must be invoked after this constructor in order to specify
     * the amount of spaces to add.
     *
     * @param out The underlying stream to write to.
     */
    public IndentedLineWriter(final Writer out) {
        this(new Formatter(out));
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
        this(out);
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
            margin = CharSequences.spaces(width).toString();
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
     * Bridge between the Apache SIS and the Geotk API.
     */
    final void beginNewLine(final Writer writeTo) throws IOException {
        final Writer old = out;
        out = writeTo;
        try {
            beginNewLine();
        } finally {
            out = old;
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
}

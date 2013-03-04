/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
import net.jcip.annotations.ThreadSafe;
import org.apache.sis.io.IO;
import org.apache.sis.io.LineAppender;

import org.geotoolkit.lang.Decorator;


/**
 * Writes characters to a stream while expanding tabs ({@code '\t'}) into spaces.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 1.0
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link LineAppender}.
 */
@Deprecated
@ThreadSafe
@Decorator(Writer.class)
public class ExpandedTabWriter extends FilterWriter {
    /**
     * The Apache SIS formatter on which to delegate the work.
     */
    private final LineAppender formatter;

    /**
     * Workaround for RFE #4093999 ("Relax constraint on placement of this()/super()
     * call in constructors")
     */
    private ExpandedTabWriter(final LineAppender formatter) {
        super(IO.asWriter(formatter));
        this.formatter = formatter;
        formatter.setTabulationExpanded(true);
    }

    /**
     * Constructs a filter which replaces tab characters ({@code '\t'})
     * by spaces. Tab widths default to 8 characters.
     *
     * @param out A writer object to provide the underlying stream.
     */
    public ExpandedTabWriter(final Writer out) {
        this(new LineAppender(out));
    }

    /**
     * Constructs a filter which replaces tab characters ({@code '\t'})
     * by spaces, using the specified tab width.
     *
     * @param  out A writer object to provide the underlying stream.
     * @param  tabWidth The tab width. Must be greater than 0.
     * @throws IllegalArgumentException if {@code tabWidth} is not greater than 0.
     */
    public ExpandedTabWriter(final Writer out, final int tabWidth) throws IllegalArgumentException {
        this(out);
        setTabWidth(tabWidth);
    }

    /**
     * Sets the tab width.
     *
     * @param  tabWidth The tab width. Must be greater than 0.
     * @throws IllegalArgumentException if {@code tabWidth} is not greater than 0.
     */
    public void setTabWidth(final int tabWidth) throws IllegalArgumentException {
        synchronized (lock) {
            formatter.setTabulationWidth(tabWidth);
        }
    }

    /**
     * Returns the tab width.
     *
     * @return The tabulation width.
     */
    public int getTabWidth() {
        synchronized (lock) {
            return formatter.getTabulationWidth();
        }
    }
}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
import org.geotoolkit.lang.Decorator;
import org.apache.sis.io.IO;
import org.apache.sis.io.LineFormatter;
import org.apache.sis.util.Characters;


/**
 * Wraps the lines at the specified maximal line length. The default line
 * length is 80 characters. The maximal line length can be changed by a call
 * to {@link #setMaximalLineLength}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link LineFormatter}.
 */
@Deprecated
@ThreadSafe
@Decorator(Writer.class)
public class LineWrapWriter extends FilterWriter {
    /**
     * Hyphen character. Line break may be inserted after this character.
     * The graphical symbol is similar to the usual {@code '-'} character.
     * For non-breaking hyphen, use the {@code '\u2011'} character.
     *
     * @deprecated Moved to Apache SIS {@link Characters#HYPHEN}.
     */
    @Deprecated
    public static final char HYPHEN = Characters.HYPHEN;

    /**
     * Hyphen character to be visible only if there is a line break to insert after it.
     * Otherwise this character is invisible. When visible, the graphical symbol is similar
     * to the {@link #HYPHEN} character.
     * <p>
     * This is equivalent to the HTML {@code &shy;} entity.
     *
     * @deprecated Moved to Apache SIS {@link Characters#SOFT_HYPHEN}.
     */
    @Deprecated
    public static final char SOFT_HYPHEN = Characters.SOFT_HYPHEN;

    /**
     * The Apache SIS formatter on which to delegate the work.
     */
    private final LineFormatter formatter;

    /**
     * Workaround for RFE #4093999 ("Relax constraint on placement of this()/super()
     * call in constructors")
     */
    private LineWrapWriter(final LineFormatter formatter) {
        super(IO.asWriter(formatter));
        this.formatter = formatter;
    }

    /**
     * Constructs a stream which will wraps the lines at 80 characters.
     * The maximal line length can be changed by a call to {@link #setMaximalLineLength}.
     *
     * @param out The underlying stream to write to.
     */
    public LineWrapWriter(final Writer out) {
        this(out, 80);
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
        this(new LineFormatter(out));
        setMaximalLineLength(length);
    }

    /**
     * Returns the maximal line length. The default value is 80.
     *
     * @return The current maximal line length.
     */
    public int getMaximalLineLength() {
        return formatter.getMaximalLineLength();
    }

    /**
     * Sets the maximal line length.
     *
     * @param length The new maximal line length.
     */
    public void setMaximalLineLength(final int length) {
        formatter.setMaximalLineLength(length);
    }
}

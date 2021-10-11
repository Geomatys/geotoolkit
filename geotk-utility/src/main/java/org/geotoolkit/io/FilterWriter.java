/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import java.io.StringWriter;
import java.io.Writer;
import org.apache.sis.io.IO;


/**
 * An extension to {@link java.io.FilterWriter} with better {@link #toString()} capability.
 * Internal mechanic not to be visible in public API.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 *
 * @deprecated To remove after we removed the deprecated subclasses.
 */
@Deprecated
abstract class FilterWriter extends java.io.FilterWriter {
    /**
     * Create a new filtered writer.
     *
     * @param out  a Writer object to provide the underlying stream.
     * @throws NullPointerException if {@code out} is {@code null}.
     */
    protected FilterWriter(final Writer out) {
        super(out);
    }

    /**
     * Returns the content of the given writer, or {@code null} if none.
     */
    static String content(final Writer out) {
        if (out instanceof StringWriter) {
            return out.toString();
        }
        if (out instanceof FilterWriter) {
            return ((FilterWriter) out).content();
        }
        final CharSequence cs = IO.content(out);
        if (cs != null) {
            return cs.toString();
        }
        return null;
    }

    /**
     * Returns the content of the wrapped writer, or {@code null} if none.
     */
    String content() {
        return content(out);
    }

    /**
     * Returns a string representation if this writer.
     */
    @Override
    public String toString() {
        String s = content(out);
        if (s == null) {
            s = super.toString();
        }
        return s;
    }
}

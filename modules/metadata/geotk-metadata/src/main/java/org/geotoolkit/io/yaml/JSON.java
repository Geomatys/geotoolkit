/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.io.yaml;

import java.io.IOException;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.ArgumentChecks;


/**
 * Read and write objects using the <cite>JavaScript Object Notation</cite> (JSON).
 * In this library, JSON is considered as a subset of YAML.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @module
 */
public final class JSON extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private JSON() {
    }

    /**
     * Formats the given object in a string.
     * See {@linkplain org.geotoolkit.io.yaml package} javadoc for a description of valid objects.
     *
     * @param  object The object to format.
     * @return A JSON representation of the given object.
     * @throws ClassCastException If the given object is not an instance of a recognized standard.
     */
    public static String format(final Object object) throws ClassCastException {
        ArgumentChecks.ensureNonNull("object", object);
        final StringBuilder buffer = new StringBuilder();
        final Writer writer = new Writer(buffer);
        try {
            writer.format(object);
        } catch (IOException e) {
            throw new AssertionError(e); // Should never happen, since we are writing in a StringBuilder.
        }
        return buffer.toString();
    }

    /**
     * Formats the given object in a string.
     * See {@linkplain org.geotoolkit.io.yaml package} javadoc for a description of valid objects.
     *
     * @param  object The object to format.
     * @param  out Where to write the JSON object.
     * @throws ClassCastException If the given object is not an instance of a recognized standard.
     * @throws IOException if an error occurred while writing in {@code out}.
     */
    public static void format(final Object object, final Appendable out) throws ClassCastException, IOException {
        ArgumentChecks.ensureNonNull("object", object);
        ArgumentChecks.ensureNonNull("out", out);
        final Writer writer = new Writer(out);
        writer.format(object);
    }
}

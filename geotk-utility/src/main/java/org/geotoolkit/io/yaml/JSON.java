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
import java.text.ParseException;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.ArgumentChecks;


/**
 * Reads and writes objects using the <cite>JavaScript Object Notation</cite> (JSON).
 * In this library, JSON is considered as a subset of YAML.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @module
 *
 * @todo We have an ambiguity when writing {@code Party.name}: nothing distinguish an individual name
 *       from an organization name, since the {@code Party} type is lost at JSON writing time.
 *       We propose to format as {@code "individual.name"} and {@code "organisation.name"} in those
 *       particular cases.
 */
public final class JSON extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private JSON() {
    }

    /**
     * Parses the given JSON string as an ISO 19115 metadata object.
     *
     * @param  json The string to parse.
     * @param  type The type of the object to parse (typically <code>{@linkplain org.opengis.metadata.Metadata}.class</code>).
     * @return The parsed object, usually as an instance of {@code type} but not always. For example this method may return a
     *         {@link java.util.List} of instances of {@code type} if the given {@code json} string defines an array.
     * @throws ParseException If the parsing failed.
     */
    public static Object parse(final CharSequence json, final Class<?> type) throws ParseException {
        return new Reader(json).parse(type);
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

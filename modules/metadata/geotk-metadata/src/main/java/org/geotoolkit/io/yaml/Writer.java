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

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.IOException;
import org.opengis.util.Enumerated;
import org.apache.sis.measure.Angle;
import org.apache.sis.metadata.AbstractMetadata;
import org.apache.sis.metadata.KeyNamePolicy;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.metadata.ValueExistencePolicy;
import org.apache.sis.util.CharSequences;
import org.apache.sis.util.iso.Types;


/**
 * Formats objects in the JSON format.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @module
 *
 * @todo We have an ambiguity when writing {@code Party.name}: nothing distinguish an individual name
 *       from an organization name, since the {@code Party} type is lost at JSON writing time.
 *       We propose to format as {@code "individual.name"} and {@code "organisation.name"} in those
 *       particular cases.
 */
final class Writer {
    /**
     * The length of an escaped Unicode character.
     */
    static final int UNICODE_LENGTH = 4;

    /**
     * Number of spaces to use for the indentation.
     */
    private static final int INDENTATION = 4;

    /**
     * Where to write the formatted output.
     */
    private final Appendable out;

    /**
     * The line-separator to use.
     */
    private final String lineSeparator;

    /**
     * The ISO standard to use for listing the object properties.
     */
    private MetadataStandard standard;

    /**
     * Number of spaces to write before the first no-space character.
     * This value is increased or decreased when an object definition
     * begins or stops.
     */
    private int margin;

    /**
     * Guard against infinite recursivity.
     */
    private final Map<Object,Object> guard = new IdentityHashMap<>();

    /**
     * Creates a new writer for the given standard which will write in the given stream.
     *
     * @param out Where to format the object.
     */
    public Writer(final Appendable out) {
        this.out      = out;
        lineSeparator = System.lineSeparator();
    }

    /**
     * Suggests a metadata standard for the given object.
     */
    private static MetadataStandard getStandard(final Object value) {
        return (value instanceof AbstractMetadata) ? ((AbstractMetadata) value).getStandard() : MetadataStandard.ISO_19115;
    }

    /**
     * Writes the left margin at the beginning of a new line.
     */
    private void indent() throws IOException {
        out.append(CharSequences.spaces(margin));
    }

    /**
     * Formats the given object in the output stream specified at construction time.
     *
     * @param  object The object to format.
     * @throws ClassCastException If the given object is not an instance of a recognized standard.
     * @throws IOException If an error occurred while writing to the output stream.
     */
    public void format(final Object object) throws ClassCastException, IOException {
        if (guard.put(object, Boolean.TRUE) == null) {
            final MetadataStandard previous = standard;
            standard = getStandard(object);
            formatEntries(standard.asValueMap(object, KeyNamePolicy.UML_IDENTIFIER,
                    ValueExistencePolicy.NON_EMPTY).entrySet().iterator());
            standard = previous;
            if (guard.remove(object) != Boolean.TRUE) { // Identity check is okay here.
                throw new ConcurrentModificationException();
            }
        } else {
            // We have a recursivity, buy we can not express that in JSON.
            out.append(null);
        }
    }

    /**
     * Formats all (key, value) pairs of the given map, in iteration order.
     *
     * @param  it An iterator over the metadata properties to format.
     * @throws IOException If an error occurred while writing to the output stream.
     */
    private void formatEntries(final Iterator<Map.Entry<String,Object>> it) throws IOException {
        out.append('{').append(lineSeparator);
        margin += INDENTATION;
        for (boolean hasNext = it.hasNext(); hasNext;) {
            final Map.Entry<String,Object> entry = it.next();
            hasNext = it.hasNext();
            indent();
            out.append('"');
            escape(entry.getKey());
            out.append("\": ");
            final Object value = entry.getValue();
            if (value == null) {
                out.append(null);
            } else if (value instanceof Collection<?>) {
                formatArray(((Collection<?>) value).iterator());
            } else {
                formatValue(value);
            }
            if (hasNext) {
                out.append(',');
            }
            out.append(lineSeparator);
        }
        margin -= INDENTATION;
        indent();
        out.append('}');
    }

    /**
     * Formats an array of values.
     *
     * @param  it An iterator over the array elements to format.
     * @throws IOException If an error occurred while writing to the output stream.
     */
    private void formatArray(final Iterator<?> it) throws IOException {
        out.append('[');
        boolean hasNext = it.hasNext();
        while (hasNext) {
            final Object element = it.next();
            hasNext = it.hasNext();
            formatValue(element);
            if (hasNext) {
                out.append(',');
            }
        }
        out.append(']');
    }

    /**
     * Formats a single value.
     *
     * @param  value The value to format.
     * @throws IOException If an error occurred while writing to the output stream.
     */
    private void formatValue(final Object value) throws IOException {
        if (standard.isMetadata(value.getClass())) {
            format(value);
        } else {
            final String text;
            final boolean quote;
            if (value instanceof Enumerated) {
                text  = Types.getCodeName((Enumerated) value);
                quote = true;
            } else if (value instanceof Date) {
                text  = Long.toString(((Date) value).getTime());
                quote = false;
            } else if (value instanceof Angle) {
                text  = Double.toString(((Angle) value).degrees());
                quote = false;
            } else {
                text  = value.toString();
                quote = !(value instanceof Number || value instanceof Boolean);
            }
            if (quote) out.append('"');
            escape(text);
            if (quote) out.append('"');
        }
    }

    /**
     * Appends the given text, escaping characters if needed.
     */
    private void escape(final String text) throws IOException {
        int previous = 0;
        final int length = text.length();
        for (int i=0; i<length; i++) {
            final char c = text.charAt(i); // No need for codepoint API in this method.
            final char r;
            switch (c) {
                case '"' : // Fallthrough
                case '\\': r =  c;  break;
                case '\b': r = 'b'; break;
                case '\f': r = 'f'; break;
                case '\n': r = 'n'; break;
                case '\r': r = 'r'; break;
                case '\t': r = 't'; break;
                default: {
                    if (!Character.isISOControl(c)) {
                        continue; // Nothing to escape.
                    }
                    r = 'u';
                    break;
                }
            }
            out.append(text, previous, i);
            out.append('\\').append(r);
            if (r == 'u') {
                final String h = Integer.toHexString(c);
                for (int p=h.length(); p<UNICODE_LENGTH; p++) {
                    out.append('0');
                }
                out.append(h);
            }
            previous = i+1;
        }
        out.append(text, previous, length);
    }
}

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


/**
 * Formats objects in the JSON format.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @module
 */
final class Writer {
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
            out.append('"').append(entry.getKey()).append("\": ");
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
                text  = ((Enumerated) value).identifier();
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
            out.append(CharSequences.replace(text, "\"", "\\\""));
            if (quote) out.append('"');
        }
    }
}

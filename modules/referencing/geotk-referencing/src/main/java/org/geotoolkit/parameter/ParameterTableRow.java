/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.parameter;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.io.Writer;
import java.io.IOException;

import org.opengis.util.GenericName;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.IdentifiedObject;

import org.geotoolkit.io.X364;
import org.geotoolkit.metadata.iso.citation.Citations;

import static org.geotoolkit.io.X364.*;
import static org.geotoolkit.util.Utilities.spaces;
import static org.geotoolkit.util.Utilities.hashMapCapacity;


/**
 * A row in the table to be formatted by {@link ParameterWriter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
final class ParameterTableRow {
    /**
     * The value. May be a list if there is more than one.
     */
    private Object value;

    /**
     * {@code true} if {@link #value} has been converted to a list.
     * This will happen only if there is more than one value.
     */
    private boolean asList;

    /**
     * The largest width of authority names, in number of characters.
     */
    int width;

    /**
     * The (<var>authority</var>,<var>name(s)</var>) entries for the identifier and all aliases
     * declared in the constructor. The authority element may be null, but the names should never
     * be null.
     * <p>
     * Names are usually instance of {@link String}, but can also be arrays of strings
     * if more than one name has been found for the same authority. In such case, the
     * additional names are appended in the order they are declared in the identified
     * object.
     */
    private final Map<String,Object> identifiers;

    /**
     * Creates a new row in a table to be formatted by {@link ParameterWriter}.
     *
     * @param object The object for which to get the (<var>authority</var>,<var>name(s)</var>).
     * @param locale The locale for formatting the names.
     * @param value  The initial singleton value. More may be added later.
     */
    ParameterTableRow(final IdentifiedObject object, final Locale locale, final Object value) {
        this.value = value;
        /*
         * Creates a collection which will contain the identifier and all aliases
         * found for the given IdentifiedObject. We begin with the unique identifier.
         */
        final Collection<GenericName> alias = object.getAlias();
        identifiers = new LinkedHashMap<String,Object>(
                hashMapCapacity((alias != null) ? alias.size() : 0) + 1);
        final Identifier identifier = object.getName();
        String authority = Citations.getIdentifier(identifier.getAuthority());
        identifiers.put(authority, identifier.getCode());
        if (authority != null) {
            width = authority.length();
        }
        if (alias != null) {
            for (final GenericName candidate : alias) {
                if (identifier.equals(candidate)) {
                    // Do not duplicate the identifier.
                    continue;
                }
                authority = null;
                if (candidate instanceof Identifier) {
                    authority = Citations.getIdentifier(((Identifier) candidate).getAuthority());
                    if (authority != null) {
                        final int length = authority.length();
                        if (length > width) {
                            width = length;
                        }
                    }
                }
                /*
                 * At this point the authority is known (it may be null). Now get the name.
                 * If a name was already defined for the current authority, append to an array.
                 */
                final String name = candidate.tip().toInternationalString().toString(locale);
                Object previous = identifiers.put(authority, name);
                if (previous != null) {
                    String[] array;
                    if (previous instanceof String) {
                        array = new String[] {(String) previous, name};
                    } else {
                        array = (String[]) previous;
                        final int n = array.length;
                        array = Arrays.copyOf(array, n+1);
                        array[n] = name;
                    }
                    identifiers.put(authority, array);
                }
            }
        }
    }

    /**
     * Adds a value.
     *
     * @param value The value to add.
     */
    @SuppressWarnings("unchecked")
    final void addValue(final Object more) {
        final List<Object> values;
        if (!asList) {
            asList = true;
            values = new ArrayList<Object>(4);
            values.add(value);
            value = values;
        } else {
            values = (List<Object>) value;
        }
        values.add(more);
    }

    /**
     * Returns all values as an array. It may be an array of primitive type,
     * which is why the return type is not {@code object[]}.
     *
     * @param  singleton A buffer to use if there is only one element.
     * @return The values as an array.
     */
    final Object values(final Object[] singleton) {
        Object array = value;
        if (array instanceof Collection<?>) {
            array = ((Collection<?>) array).toArray();
        } else if (array == null || !array.getClass().isArray()) {
            singleton[0] = array;
            array = singleton;
        }
        return array;
    }

    /**
     * Writes the given color if {@code colorEnabled} is {@code true}.
     */
    private static void write(final Writer out, final X364 color, final boolean colorEnabled)
            throws IOException
    {
        if (colorEnabled) {
            out.write(color.sequence());
        }
    }

    /**
     * Writes the identifiers. At most one of {@code colorsForTitle} and {@code colorsForRows}
     * should be set to {@code true}.
     *
     * @param  out             Where to write.
     * @param  colorsForTitle  {@code true} if syntax coloring should be applied for table title.
     * @param  colorsForRows   {@code true} if syntax coloring should be applied for table rows.
     * @param  lineSeparator   The system-dependant line separator.
     * @throws IOException     If an exception occured while writting.
     */
    final void write(final Writer out, final boolean colorsForTitle,
            final boolean colorsForRows, final String lineSeparator) throws IOException
    {
        boolean continuing = false;
        for (final Map.Entry<String,Object> entry : identifiers.entrySet()) {
            if (continuing) {
                out.write(lineSeparator);
            }
            continuing = true;
            int length = width + 1;
            final String authority  = entry.getKey();
            write(out, FOREGROUND_GREEN, colorsForTitle);
            if (authority != null) {
                write(out, FAINT, colorsForRows);
                out.write(authority);
                out.write(':');
                write(out, NORMAL, colorsForRows);
                length -= authority.length();
            }
            out.write(spaces(length));
            write(out, BOLD, colorsForTitle);
            final Object identifier = entry.getValue();
            if (identifier instanceof String[]) {
                final String[] ids = (String[]) identifier;
                out.write(ids[0]);
                write(out, RESET, colorsForTitle);
                for (int i=1; i<ids.length; i++) {
                    final String id = ids[i];
                    out.write(i == 1 ? " (" : ", ");
                    final X364 color, normal;
                    if (isNumeric(id)) {
                        color  = FOREGROUND_YELLOW;
                        normal = FOREGROUND_DEFAULT;
                    } else {
                        color  = FAINT;
                        normal = NORMAL;
                    }
                    write(out, color, colorsForTitle);
                    out.write(id);
                    write(out, normal, colorsForTitle);
                }
                out.write(')');
            } else {
                out.write(identifier.toString());
            }
            write(out, RESET, colorsForTitle);
        }
    }

    /**
     * Returns {@code true} if the given value is likely to be an EPSG code.
     */
    static boolean isNumeric(final String value) {
        for (int i=value.length(); --i>=0;) {
            final char c = value.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}

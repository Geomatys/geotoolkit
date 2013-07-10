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
package org.geotoolkit.parameter;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.io.Writer;
import java.io.IOException;

import org.opengis.util.GenericName;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.IdentifiedObject;

import org.geotoolkit.io.X364;
import org.apache.sis.internal.util.Citations;

import static org.geotoolkit.io.X364.*;
import static org.apache.sis.util.CharSequences.spaces;


/**
 * A row in the table to be formatted by {@link ParameterWriter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
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
     * Names are usually instance of {@link String}, but can also be {@link Identifier}
     * if such identifier are defined. The names and identifiers appended in the order
     * they are declared in the identified object.
     */
    private final Map<String,Set<Object>> identifiers;

    /**
     * Creates a new row in a table to be formatted by {@link ParameterWriter}.
     *
     * @param object The object for which to get the (<var>authority</var>,<var>name(s)</var>).
     * @param locale The locale for formatting the names.
     * @param value  The initial singleton value. More may be added later.
     * @param brief  {@code true} for excluding aliases and identifiers.
     */
    ParameterTableRow(final IdentifiedObject object, final Locale locale, final Object value, final boolean brief) {
        this.value = value;
        /*
         * Creates a collection which will contain the identifier and all aliases
         * found for the given IdentifiedObject. We begin with the primary name.
         */
        identifiers = new LinkedHashMap<>();
        final Identifier identifier = object.getName();
        addIdentifier(getAuthority(identifier), identifier.getCode()); // Really want .getCode()
        if (!brief) {
            final Collection<GenericName> alias = object.getAlias();
            if (alias != null) {
                for (final GenericName candidate : alias) {
                    String authority = null;
                    if (candidate instanceof Identifier) {
                        authority = getAuthority((Identifier) candidate);
                    }
                    addIdentifier(authority, candidate.tip().toInternationalString().toString(locale));
                }
            }
            final Collection<? extends Identifier> ids = object.getIdentifiers();
            if (ids != null) {
                for (final Identifier id : ids) {
                    addIdentifier(getAuthority(id), id); // No .getCode() here.
                }
            }
        }
    }

    /**
     * Returns the authority of the given identifier, or {@code null} if none.
     * As a side effect, this method remembers the length of the widest identifier.
     */
    private String getAuthority(final Identifier identifier) {
        String authority = Citations.getIdentifier(identifier.getAuthority());
        if (authority != null) {
            final int length = authority.length();
            if (length > width) {
                width = length;
            }
        }
        return authority;
    }

    /**
     * Adds an identifier for the given authority.
     */
    private void addIdentifier(final String authority, final Object identifier) {
        Set<Object> ids = identifiers.get(authority);
        if (ids == null) {
            ids = new LinkedHashSet<>(8);
            identifiers.put(authority, ids);
        }
        ids.add(identifier);
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
            values = new ArrayList<>(4);
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
     * @param  lineSeparator   The system-dependent line separator.
     * @throws IOException     If an exception occurred while writing.
     */
    final void write(final Writer out, final boolean colorsForTitle,
            final boolean colorsForRows, final String lineSeparator) throws IOException
    {
        boolean continuing = false;
        for (final Map.Entry<String,Set<Object>> entry : identifiers.entrySet()) {
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
            out.append(spaces(length));
            write(out, BOLD, colorsForTitle);
            final Iterator<Object> it = entry.getValue().iterator();
            out.write(toString(it.next()));
            write(out, RESET, colorsForTitle);
            boolean hasMore = false;
            while (it.hasNext()) {
                out.write(hasMore ? ", " : " (");
                final Object id = it.next();
                final X364 color, normal;
                if (id instanceof Identifier) {
                    color  = FOREGROUND_YELLOW;
                    normal = FOREGROUND_DEFAULT;
                } else {
                    color  = FAINT;
                    normal = NORMAL;
                }
                write(out, color, colorsForTitle);
                out.write(toString(id));
                write(out, normal, colorsForTitle);
                hasMore = true;
            }
            if (hasMore) {
                out.write(')');
            }
            write(out, RESET, colorsForTitle);
        }
    }

    /**
     * Returns the string representation of the given parameter name.
     */
    private static String toString(Object parameter) {
        if (parameter instanceof Identifier) {
            parameter = ((Identifier) parameter).getCode();
        }
        return parameter.toString();
    }
}

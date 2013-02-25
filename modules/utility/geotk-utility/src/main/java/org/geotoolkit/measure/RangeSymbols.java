/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.measure;

import org.geotoolkit.util.Cloneable;


/**
 * Symbols used by {@link RangeFormat} when parsing and formatting a range.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @see RangeFormat
 *
 * @since 3.06
 * @module
 *
 * @deprecated No replacement.
 */
@Deprecated
public class RangeSymbols implements Cloneable {
    /**
     * The character opening a range in which the minimal value is inclusive.
     * The default value is {@code '['}.
     */
    public char openInclusive = '[';

    /**
     * The character opening a range in which the minimal value is exclusive.
     * The default value is {@code '('}. Note that the {@code ']'} character
     * is also sometime used.
     */
    public char openExclusive = '(';

    /**
     * An alternative character opening a range in which the minimal value is exclusive.
     * This character is not used for formatting (only {@link #openExclusive} is used),
     * but is accepted during parsing. The default value is {@code ']'}.
     */
    public char openExclusiveAlt = ']';

    /**
     * The character closing a range in which the maximal value is inclusive.
     * The default value is {@code ']'}.
     */
    public char closeInclusive = ']';

    /**
     * The character closing a range in which the maximal value is exclusive.
     * The default value is {@code ')'}. Note that the {@code '['} character
     * is also sometime used.
     */
    public char closeExclusive = ')';

    /**
     * An alternative character closing a range in which the maximal value is exclusive.
     * This character is not used for formatting (only {@link #closeExclusive} is used),
     * but is accepted during parsing. The default value is {@code '['}.
     */
    public char closeExclusiveAlt = '[';

    /**
     * The string to use as a separator between minimal and maximal value, not including
     * whitespaces. The default value is {@code "\u2026"} (unicode 2026).
     */
    public String separator = "\u2026";

    /**
     * Creates a new set of range symbols initialized to their default values.
     */
    public RangeSymbols() {
    }

    /**
     * Returns {@code true} if the given character is any of the opening bracket characters.
     */
    final boolean isOpen(final char c) {
        return (c == openInclusive) || (c == openExclusive) || (c == openExclusiveAlt);
    }

    /**
     * Returns {@code true} if the given character is any of the closing bracket characters.
     */
    final boolean isClose(final char c) {
        return (c == closeInclusive) || (c == closeExclusive) || (c == closeExclusiveAlt);
    }

    /**
     * Returns a clone of this set of symbols.
     */
    @Override
    public RangeSymbols clone() {
        try {
            return (RangeSymbols) super.clone();
        } catch (CloneNotSupportedException e) {
            // Should never happen since we are cloneable.
            throw new AssertionError(e);
        }
    }
}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.sql;

import org.apache.sis.util.CharSequences;


/**
 * The type of a SQL {@code CREATE} statement. The {@link #fromSQL(CharSequence)}
 * method tries to infer the enum from a given SQL statement.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.16
 * @module
 */
public enum CreateStatementType {
    /**
     * A {@code CREATE ... SCHEMA} statement.
     */
    SCHEMA,

    /**
     * A {@code CREATE ... TABLE} statement.
     */
    TABLE,

    /**
     * A {@code CREATE ... ENUM} statement.
     */
    ENUM,

    /**
     * A {@code CREATE ... CAST} statement.
     */
    CAST,

    /**
     * A {@code CREATE ... FUNCTION} statement.
     * <p>
     * Implementation note: must be declared before {@link #LANGUAGE}, because a function
     * creation statement often contains a {@code LANGUAGE} keyword after it.
     */
    FUNCTION,

    /**
     * A {@code CREATE ... LANGUAGE} statement.
     */
    LANGUAGE,

    /**
     * A {@code CREATE ... ROLE} statement.
     */
    ROLE;

    /**
     * If the given SQL statement is a {@code CREATE} statement (in upper cases), returns it.
     * Otherwise returns {@code null}.
     *
     * @param  statement The SQL statement to parse, <strong>in upper cases</strong>.
     * @return The type of the SQL {@code CREATE} statement, or {@code null} if none.
     */
    public static CreateStatementType fromSQL(final CharSequence statement) {
        final int length = statement.length();
        for (int i=0; i<length; i++) {
            char c = statement.charAt(i);
            if (!Character.isWhitespace(c)) {
                /*
                 * Found the first word. Verify that this word is "CREATE" followed
                 * by a space. The hard-coded constant 6 is the length of "CREATE".
                 */
                if (CharSequences.regionMatches(statement, i, "CREATE") && (i += 6) < length &&
                        Character.isWhitespace(statement.charAt(i)))
                {
                    for (final CreateStatementType candidate : values()) {
                        final int p = CharSequences.indexOf(statement, candidate.name(), i, statement.length());
                        if (p >= 0 && Character.isWhitespace(statement.charAt(p-1))) {
                            return candidate;
                        }
                    }
                }
                break;
            }
        }
        return null;
    }
}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.internal.sql.table;

import java.util.Objects;


/**
 * A reference from a {@linkplain #foreignerKey foreigner key} to a
 * {@linkplain #primaryKey primary key}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class CrossReference {
    /**
     * The column that reference a primary key.
     */
    public final Column foreignerKey;

    /**
     * The referenced primary key.
     */
    public final Column primaryKey;

    /**
     * Creates a new foreigner key.
     *
     * @param table The table name in which this column appears.
     * @param name  The column name.
     */
    CrossReference(final Column foreignerKey, final Column primaryKey) {
        this.foreignerKey = foreignerKey;
        this.primaryKey   = primaryKey;
    }

    /**
     * Returns a hash code value for this cross reference.
     */
    @Override
    public int hashCode() {
        return foreignerKey.hashCode() + 31 * primaryKey.hashCode();
    }

    /**
     * Compares the specified object with this cross reference for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof CrossReference) {
            final CrossReference that = (CrossReference) object;
            return Objects.equals(this.foreignerKey, that.foreignerKey) &&
                   Objects.equals(this.primaryKey,   that.primaryKey);
        }
        return false;
    }

    /**
     * Returns a string representation of this cross reference for debugging purpose.
     */
    @Override
    public String toString() {
        return "CrossReference[" + foreignerKey + " \u2192 " + primaryKey + ']';
    }
}

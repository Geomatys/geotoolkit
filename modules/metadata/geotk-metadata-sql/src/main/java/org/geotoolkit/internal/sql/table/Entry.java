/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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


/**
 * Interface for entries created from a record in a table. They are the elements returned by
 * {@link SingletonTable}. Each entry must have an identifier which is unique among all entries
 * in a given table. However the identifier may not be unique among entries in different tables.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.11
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public interface Entry {
    /**
     * Returns the textual or numeric identifier for this entry. It is often (but not always) the
     * primary key value in a database table. If the identifier is a {@link String}, then that name
     * should be meaningful enough for inclusion in a graphical user interface.
     *
     * @return The identifier, which is not allowed to be null.
     */
    Comparable<?> getIdentifier();

    /**
     * Returns a string representation of this entry. The returned string may be used in graphical
     * user interface, for example a <cite>Swing</cite> {@link javax.swing.JTree}. The current
     * implementation returns the entry {@linkplain #identifier}.
     *
     * @return A string representation of this entry suitable for use in GUI.
     */
    @Override
    String toString();

    /**
     * Returns a hash code value for this entry. The implementation shall compute the hash
     * code from the {@linkplain #getIdentifier() identifier} only. Because entry identifiers
     * should be unique, this is usually sufficient.
     *
     * @return A hash code value computed from the identifier.
     */
    @Override
    int hashCode();

    /**
     * Compares this entry with the specified object for equality. The implementation shall
     * compares at least the {@linkplain #getClass class} and {@linkplain #getIdentifier()
     * identifier}. If should be sufficient when every entries have a unique name, for example
     * when the name is the primary key in a database table. Implementation may compare other
     * attributes as a safety when affordable, but should avoid any comparison that may force
     * the loading of a large amount of data.
     *
     * @param  object The object to compare with this entry.
     * @return {@code true} if the given object is equals to this entry.
     */
    @Override
    boolean equals(Object object);
}

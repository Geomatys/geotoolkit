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

import java.util.Objects;
import java.io.Serializable;
import net.jcip.annotations.ThreadSafe;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.NullArgumentException;


/**
 * Base class for entries created from a record in a table.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.11
 *
 * @since 3.11 (derived from Seagis)
 * @module
 */
@ThreadSafe
public class DefaultEntry implements Entry, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -7119518186999674633L;

    /**
     * The textual or numeric identifier for this entry. It is often (but not always) the primary
     * key value in a database table. If the identifier is a {@link String}, then that name should
     * be meaningful enough for inclusion in a graphical user interface.
     * <p>
     * This field is not allowed to be null.
     */
    public final Comparable<?> identifier;

    /**
     * A description of this entry, of {@code null} if none. If provided, the description should
     * be suitable for use as "<cite>tooltip text</cite>" in a graphical user interface.
     */
    public final String description;

    /**
     * Creates an entry for the specified identifier and optional description.
     *
     * @param identifier  The numeric identifier.
     * @param description The description, or {@code null} if none.
     */
    protected DefaultEntry(final Comparable<?> identifier, final String description) {
        if (identifier == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, "identifier"));
        }
        this.identifier  = identifier;
        this.description = description;
    }

    /**
     * Returns the textual or numeric identifier for this entry.
     */
    @Override
    public Comparable<?> getIdentifier() {
        return identifier;
    }

    /**
     * Returns a string representation of this entry. The returned string may be used in graphical
     * user interface, for example a <cite>Swing</cite> {@link javax.swing.JTree}. The current
     * implementation returns the entry {@linkplain #identifier}.
     */
    @Override
    public final String toString() {
        return String.valueOf(identifier);
    }

    /**
     * Returns a hash code value for this entry. The current implementation computes the hash
     * code from the {@linkplain #identifier}. Because entry identifiers should be unique, this
     * is usually sufficient.
     */
    @Override
    public final int hashCode() {
        return identifier.hashCode() ^ (int) serialVersionUID;
    }

    /**
     * Compares this entry with the specified object for equality. The default implementation
     * compares the {@linkplain #getClass class}, {@linkplain #identifier} and {@linkplain
     * #description}. If should be sufficient when every entries have a unique name, for
     * example when the name is the primary key in a database table. Subclasses may compare
     * other attributes as a safety when affordable, but should avoid any comparison that may
     * force the loading of a large amount of data.
     *
     * @param  object The object to compare with this entry.
     * @return {@code true} if the given object is equals to this entry.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object != null && object.getClass() == getClass()) {
            final DefaultEntry that = (DefaultEntry) object;
            return Objects.equals(this.identifier,  that.identifier) &&
                   Objects.equals(this.description, that.description);
        }
        return false;
    }
}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.metadata;

import java.io.Serializable;
import org.geotoolkit.util.Utilities;


/**
 * An immutable ({@code "name"}, {@code "type"}) pair.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 * @module
 *
 * @deprecated Replaced by the standard metadata objects defined by ISO 19115-2. The
 *   {@link SpatialMetadata} class can convert automatically those metadata objects
 *   to {@code IIOMetadata}.
 */
@Deprecated
public class Identification implements CharSequence, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 7439545624472885445L;

    /**
     * The object name, or {@code null} if none.
     */
    public final String name;

    /**
     * The object type, or {@code null} if none.
     */
    public final String type;

    /**
     * Creates an identification from the specified object name and type.
     *
     * @param name The identified object name.
     * @param type The identified object type, typically related to the interface it implements.
     */
    public Identification(final String name, final String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Creates an identification from the {@code "name"} and {@code "type"} attributes
     * in the specified accessor.
     *
     * @param accessor The accessor from which to fetch the name and type.
     */
    public Identification(final MetadataAccessor accessor) {
        name = accessor.getAttribute("name");
        type = accessor.getAttribute("type");
    }

    /**
     * Returns the {@linkplain #name} length.
     */
    @Override
    public int length() {
        return (name != null) ? name.length() : 0;
    }

    /**
     * Returns the {@linkplain #name} character at the specified index.
     */
    @Override
    public char charAt(final int index) {
        return name.charAt(index);
    }

    /**
     * Returns a subsequence of this identification. The new identification will contains a
     * substring of the {@linkplain #name}, but the {@linkplain #type} will be unchanged.
     */
    @Override
    public CharSequence subSequence(final int start, final int end) {
        if (start == 0 && end == length()) {
            return this;
        }
        return new Identification(name.substring(start, end), type);
    }

    /**
     * Returns the {@linkplain #name}.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a hash value for this identification.
     */
    @Override
    public int hashCode() {
        int code = (int) serialVersionUID;
        if (name != null) code ^= name.hashCode();
        if (type != null) code += type.hashCode() * 37;
        return code;
    }

    /**
     * Compares the specified object with this identification for equality.
     *
     * @param  object The object to compare with this identification for equality.
     * @return {@code true} if the given object is equal to this identification.
     */
    @Override
    public boolean equals(final Object object) {
        if (object!=null && object.getClass().equals(getClass())) {
            final Identification that = (Identification) object;
            return Utilities.equals(this.name, that.name) &&
                   Utilities.equals(this.type, that.type);
        }
        return false;
    }
}

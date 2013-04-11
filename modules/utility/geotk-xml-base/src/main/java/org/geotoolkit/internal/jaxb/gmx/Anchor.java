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
package org.geotoolkit.internal.jaxb.gmx;

import java.net.URI;
import java.util.Locale;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.opengis.util.InternationalString;

import org.apache.sis.xml.XLink;
import org.geotoolkit.util.Utilities;


/**
 * The {@code Anchor} element, which is included in {@code CharacterString} elements.
 * This class extends {@link InternationalString} in an opportunist way, in order to allow
 * direct usage with public API expecting {@link CharSequence} or {@link InternationalString}
 * object.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see <a href="http://www.xml.com/pub/a/2000/09/xlink/part2.html">XLink introduction</a>
 *
 * @since 2.5
 * @module
 */
@XmlType(name = "AnchorType")
public final class Anchor extends XLink implements InternationalString {
    /**
     * Defined as a matter of principle (this class is not expected to be serialized).
     */
    private static final long serialVersionUID = -6101324942683322597L;

    /**
     * Often a short textual description of the URN target.
     * This is the value returned by {@link #toString()}.
     */
    @XmlValue
    private String value;

    /**
     * Creates a uninitialized {@code Anchor}.
     * This constructor is required by JAXB.
     */
    public Anchor() {
    }

    /**
     * Creates an {@code Anchor} initialized to the given value.
     *
     * @param href  A URN to an external resources or an identifier.
     * @param value Often a short textual description of the URN target.
     */
    public Anchor(final URI href, final String value) {
        setHRef(href);
        this.value = value;
    }

    /**
     * Returns the text as a string, or {@code null} if none.
     * The null value is expected by {@link GO_CharacterString#toString()}.
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * Returns the text as a string, or {@code null} if none.
     */
    @Override
    public String toString(final Locale locale) {
        return value;
    }

    /**
     * Returns the number of characters in the value.
     */
    @Override
    public int length() {
        return (value != null) ? value.length() : 0;
    }

    /**
     * Returns the character at the given index.
     */
    @Override
    public char charAt(final int index) {
        return (value != null ? value : "").charAt(index);
    }

    /**
     * Returns the sequence of characters in the given range of index.
     */
    @Override
    public CharSequence subSequence(final int start, final int end) {
        return (value != null ? value : "").subSequence(start, end);
    }

    /**
     * Compares the value of this object with the given international string for order.
     * Null values are sorted last.
     *
     * @param other The string to compare with this anchor type.
     */
    @Override
    public int compareTo(final InternationalString other) {
        final String ot;
        if (other == null || (ot = other.toString()) == null) {
            return (value != null) ? -1 : 0;
        }
        return (value != null) ? value.compareTo(ot) : +1;
    }

    /**
     * Compares this {@code Anchor} with the given object for equality.
     *
     * @param object The object to compare with this anchor type.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final Anchor that = (Anchor) object;
            return Utilities.equals(this.value, that.value);
        }
        return false;
    }

    /**
     * Returns a hash code value for this anchor type.
     */
    @Override
    public int hashCode() {
        return Utilities.hash(value, super.hashCode());
    }
}

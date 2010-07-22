/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal.jaxb.text;

import java.net.URI;
import java.util.Locale;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.opengis.util.InternationalString;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.xml.Namespaces;


/**
 * The {@code AnchorType} element, which is included in {@code CharacterString} elements.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @see <a href="http://www.xml.com/pub/a/2000/09/xlink/part2.html">XLink introduction</a>
 *
 * @since 2.5
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = Namespaces.GMX)
public final class AnchorType implements InternationalString {
    /**
     * The type of link. May have one of the following values:
     * <p>
     * <ul>
     *   <li>simple: a simple link</li>
     *   <li>extended: an extended, possibly multi-resource, link</li>
     *   <li>locator: a pointer to an external resource</li>
     *   <li>resource: an internal resource</li>
     *   <li>arc: a traversal rule between resources</li>
     *   <li>title: a descriptive title for another linking element</li>
     * </ul>
     *
     * @since 3.13
     */
    @XmlAttribute(namespace = Namespaces.XLINK)
    public String type;

    /**
     * A URN to an external resources, or to an other part of a XML document, or an identifier.
     * The {@code idref} attribute allows an XML element to refer to another XML element that
     * has a corresponding {@code id} attribute.
     */
    @XmlAttribute(namespace = Namespaces.XLINK)
    public URI href;

    /**
     * A URI reference for some description of the arc role.
     *
     * @since 3.13
     */
    @XmlAttribute(namespace = Namespaces.XLINK)
    public URI role;

    /**
     * A URI reference for some description of the arc role.
     *
     * @since 3.13
     */
    @XmlAttribute(namespace = Namespaces.XLINK)
    public URI arcrole;

    /**
     * Just as with resources, this is simply a human-readable string with a short description
     * for the arc.
     *
     * @since 3.13
     */
    @XmlAttribute(namespace = Namespaces.XLINK)
    public String title;

    /**
     * Communicates the desired presentation of the ending resource on traversal
     * from the starting resource. It's value should be treated as follows:
     * <p>
     * <ul>
     *   <li>new: load ending resource in a new window, frame, pane, or other presentation context</li>
     *   <li>replace: load the resource in the same window, frame, pane, or other presentation context</li>
     *   <li>embed: load ending resource in place of the presentation of the starting resource</li>
     *   <li>other: behavior is unconstrained; examine other markup in the link for hints</li>
     *   <li>none: behavior is unconstrained</li>
     * </ul>
     *
     * @since 3.13
     */
    @XmlAttribute(namespace = Namespaces.XLINK)
    public String show;

    /**
     * Communicates the desired timing of traversal from the starting resource to the ending
     * resource. It's value should be treated as follows:
     * <p>
     * <ul>
     *   <li>onLoad: traverse to the ending resource immediately on loading the starting resource</li>
     *   <li>onRequest: traverse from the starting resource to the ending resource only on a post-loading event triggered for this purpose</li>
     *   <li>other: behavior is unconstrained; examine other markup in link for hints</li>
     *   <li>none: behavior is unconstrained</li>
     * </ul>
     *
     * @since 3.13
     */
    @XmlAttribute(namespace = Namespaces.XLINK)
    public String actuate;

    /**
     * Often a short textual description of the URN target.
     */
    @XmlValue
    private String value;

    /**
     * Creates a uninitialized {@code AnchorType}.
     * This constructor is required by JAXB.
     */
    public AnchorType() {
    }

    /**
     * Creates an {@code AnchorType} initialized to the given value.
     *
     * @param href  A URN to an external resources or an identifier.
     * @param value Often a short textual description of the URN target.
     */
    public AnchorType(final URI href, final String value) {
        this.href  = href;
        this.value = value;
    }

    /**
     * Returns the text as a string, or {@code null} if none.
     * The null value is expected by {@link CharacterString#toString()}.
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
     * Compares this {@code AnchorType} with the given object for equality.
     *
     * @param object The object to compare with this anchor type.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AnchorType) {
            final AnchorType that = (AnchorType) object;
            return Utilities.equals(this.type,    that.type)    &&
                   Utilities.equals(this.href,    that.href)    &&
                   Utilities.equals(this.role,    that.role)    &&
                   Utilities.equals(this.arcrole, that.arcrole) &&
                   Utilities.equals(this.title,   that.title)   &&
                   Utilities.equals(this.show,    that.show)    &&
                   Utilities.equals(this.actuate, that.actuate) &&
                   Utilities.equals(this.value,   that.value);
        }
        return false;
    }

    /**
     * Returns a hash code value for this anchor type.
     */
    @Override
    public int hashCode() {
        return Utilities.hash(type,
               Utilities.hash(href,
               Utilities.hash(role,
               Utilities.hash(arcrole,
               Utilities.hash(title,
               Utilities.hash(show,
               Utilities.hash(actuate,
               Utilities.hash(value, 99911302))))))));
    }

    /*
     * Following methods are temporary. They are required by Constellation which expect setter
     * methods. A future version may remove those methods if we update Constellation for using
     * directly the field values.
     */
    @Deprecated public String getType()    {return type;}
    @Deprecated public URI    getHref()    {return href;}
    @Deprecated public URI    getRole()    {return role;}
    @Deprecated public URI    getArcrole() {return arcrole;}
    @Deprecated public String getTitle()   {return title;}
    @Deprecated public String getShow()    {return show;}
    @Deprecated public String getActuate() {return actuate;}
    @Deprecated public String getValue()   {return value;}

    @Deprecated public void setType   (String  type)   {this.type    = type;}
    @Deprecated public void setHref   (URI     href)   {this.href    = href;}
    @Deprecated public void setRole   (URI     role)   {this.role    = role;}
    @Deprecated public void setArcrole(URI  arcrole)   {this.arcrole = arcrole;}
    @Deprecated public void setTitle  (String title)   {this.title   = title;}
    @Deprecated public void setShow   (String show)    {this.show    = show;}
    @Deprecated public void setActuate(String actuate) {this.actuate = actuate;}
    @Deprecated public void setValue  (String value)   {this.value   = value;}
}

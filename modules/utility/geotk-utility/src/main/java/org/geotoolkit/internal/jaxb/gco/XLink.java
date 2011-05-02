/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.internal.jaxb.gco;

import java.net.URI;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.xml.Namespaces;


/**
 * The {@code xlink} attributes.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18 (derived from 3.13)
 *
 * @see <a href="http://www.xml.com/pub/a/2000/09/xlink/part2.html">XLink introduction</a>
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-165">GEOTK-165</a>
 *
 * @since 2.5
 * @module
 */
@XmlTransient
public abstract class XLink {
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
     * Creates a uninitialized {@code XLink}.
     * This constructor is required by JAXB.
     */
    XLink() {
    }

    /**
     * Compares this {@code XLink} with the given object for equality.
     *
     * @param object The object to compare with this XLink.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass().equals(getClass())) {
            final XLink that = (XLink) object;
            return Utilities.equals(this.type,    that.type)    &&
                   Utilities.equals(this.href,    that.href)    &&
                   Utilities.equals(this.role,    that.role)    &&
                   Utilities.equals(this.arcrole, that.arcrole) &&
                   Utilities.equals(this.title,   that.title)   &&
                   Utilities.equals(this.show,    that.show)    &&
                   Utilities.equals(this.actuate, that.actuate);
        }
        return false;
    }

    /**
     * Returns a hash code value for this XLink.
     */
    @Override
    public int hashCode() {
        return Utilities.hash(type,
               Utilities.hash(href,
               Utilities.hash(role,
               Utilities.hash(arcrole,
               Utilities.hash(title,
               Utilities.hash(show,
               Utilities.hash(actuate, 99911302)))))));
    }
}

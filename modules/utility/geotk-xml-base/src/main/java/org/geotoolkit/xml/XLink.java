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
package org.geotoolkit.xml;

import java.net.URI;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.opengis.util.InternationalString;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.resources.Errors;


/**
 * The XML attributes defined by OGC in the
 * <a href="http://schemas.opengis.net/xlink/1.0.0/xlinks.xsd">xlink</a> schema.
 *
 * The allowed combinations of any one attribute depend on the value of the special
 * {@link #getType() type} attribute. Following is a summary of the element types
 * (columns) on which the global attributes (rows) are allowed, with an indication
 * of whether a value is required (R) or optional (O)
 * (Source: <a href="http://www.w3.org/TR/xlink/">W3C</a>):
 *
 * <blockquote><table border="1" cellspacing="0" cellpadding="1">
 * <tr bgcolor="lightblue">
 *   <th>&nbsp;</th>
 *   <th width="14%">{@link XLink.Type#SIMPLE simple}</th>
 *   <th width="14%">{@link XLink.Type#EXTENDED extended}</th>
 *   <th width="14%">{@link XLink.Type#LOCATOR locator}</th>
 *   <th width="14%">{@link XLink.Type#ARC arc}</th>
 *   <th width="14%">{@link XLink.Type#RESOURCE resource}</th>
 *   <th width="14%">{@link XLink.Type#TITLE title}</th>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getType() type}</th>
 *   <td>R</td><td>R</td><td>R</td><td>R</td><td>R</td><td>R</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getHRef() href}</th>
 *   <td>O</td><td>&nbsp;</td><td>R</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getRole() role}</th>
 *   <td>O</td><td>O</td><td>O</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getArcRole() arcrole}</th>
 *   <td>O</td><td>&nbsp;</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getTitle() title}</th>
 *   <td>O</td><td>O</td><td>O</td><td>O</td><td>O</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getShow() show}</th>
 *   <td>O</td><td>&nbsp;</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getActuate() actuate}</th>
 *   <td>O</td><td>&nbsp;</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getLabel() label}</th>
 *   <td>&nbsp;</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getFrom() from}</th>
 *   <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getTo() to}</th>
 *   <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td><td>&nbsp;</td>
 * </tr></table></blockquote>
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see <a href="http://www.w3.org/TR/xlink/">XML Linking Language</a>
 * @see <a href="http://schemas.opengis.net/xlink/1.0.0/xlinks.xsd">OGC schema</a>
 *
 * @since 3.18 (derived from 2.5)
 * @module
 */
@XmlTransient
public class XLink implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -4349950135677857725L;

    /**
     * The type of link. If {@code null}, then the type will be inferred by {@link #getType()}.
     *
     * @see #getType()
     */
    private Type type;

    /**
     * A URN to an external resources, or to an other part of a XML document, or an identifier.
     *
     * @see #getHRef()
     * @category locator
     */
    private URI href;

    /**
     * A URI reference for some description of the arc role.
     *
     * @see #getRole()
     * @category semantic
     */
    private URI role;

    /**
     * A URI reference for some description of the arc role.
     *
     * @see #getArcRole()
     * @category semantic
     */
    private URI arcrole;

    /**
     * Just as with resources, this is simply a human-readable string with a short description
     * for the arc.
     *
     * @see #getTitle()
     * @category semantic
     */
    private InternationalString title;

    /**
     * Communicates the desired presentation of the ending resource on traversal
     * from the starting resource.
     *
     * @see #getShow()
     * @category behavior
     */
    private Show show;

    /**
     * Communicates the desired timing of traversal from the starting resource to the ending resource.
     *
     * @see #getActuate()
     * @category behavior
     */
    private Actuate actuate;

    /**
     * Identifies the target of a {@code from} or {@code to} attribute.
     *
     * @see #getLabel()
     * @category traversal
     */
    private String label;

    /**
     * The starting resource. The value must correspond to the same value for some
     * {@code label} attribute.
     *
     * @see #getFrom()
     * @category traversal
     */
    private String from;

    /**
     * The ending resource. The value must correspond to the same value for some
     * {@code label} attribute.
     *
     * @see #getTo()
     * @category traversal
     */
    private String to;

    /**
     * Creates a new link. The initial value of all attributes is {@code null}.
     */
    public XLink() {
    }

    /**
     * The type of a {@code xlink}. This type can be determined from the set of non-null
     * attribute values in an {@link XLink} instance.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.18
     *
     * @see XLink#getType()
     *
     * @since 3.18
     * @module
     */
    @XmlEnum
    public static enum Type {
        /**
         * A simple link. Allows the {@link XLink#getHRef() href}, {@link XLink#getRole() role},
         * {@link XLink#getArcRole() arcrole}, {@link #getTitle() title}, {@link XLink#getShow()
         * show} and {@link XLink#getActuate() actuate} attributes, all of them being optional.
         */
        @XmlEnumValue("simple") SIMPLE(0x1 | 0x2 | 0x4 | 0x8 | 0x10 | 0x20 | 0x40, 0x1),

        /**
         * An extended, possibly multi-resource, link. Allows the {@link XLink#getRole() role}
         * and {@link #getTitle() title} attributes, all of them being optional.
         */
        @XmlEnumValue("extended") EXTENDED(0x1 | 0x4 | 0x10, 0x1),

        /**
         * A pointer to an external resource. Allows the {@link XLink#getHRef() href},
         * {@link XLink#getRole() role}, {@link #getTitle() title} and {@link XLink#getLabel()
         * label} attributes, where {@code href} is mandatory and all other are optional.
         */
        @XmlEnumValue("locator") LOCATOR(0x1 | 0x2 | 0x4 | 0x10 | 0x80, 0x1 | 0x2),

        /**
         * An internal resource. Allows the {@link XLink#getRole() role},  {@link #getTitle() title}
         * and {@link #getLabel() label} attributes, all of them being optional.
         */
        @XmlEnumValue("resource") RESOURCE(0x1 | 0x4 | 0x10 | 0x80, 0x1),

        /**
         * A traversal rule between resources. Allows the {@link XLink#getArcRole() arcrole},
         * {@link #getTitle() title}, {@link XLink#getShow() show}, {@link XLink#getActuate()
         * actuate} {@link #getFrom() from} and {@link #getTo() to} attributes, all of them
         * being optional.
         */
        @XmlEnumValue("arc") ARC(0x1 | 0x8 | 0x10 | 0x20 | 0x40 | 0x100 | 0x200, 0x1),

        /**
         * A descriptive title for another linking element.
         */
        @XmlEnumValue("title") TITLE(0x1, 0x1);

        /**
         * A bitmask which specified the non-null fields expected for a given type.
         * The bit values are:
         * <ul>
         *   <li>{@code type}:     0x1</li>
         *   <li>{@code href}:     0x2</li>
         *   <li>{@code role}:     0x4</li>
         *   <li>{@code arcrole}:  0x8</li>
         *   <li>{@code title}:   0x10</li>
         *   <li>{@code show}:    0x20</li>
         *   <li>{@code actuate}: 0x40</li>
         *   <li>{@code label}:   0x80</li>
         *   <li>{@code from}:   0x100</li>
         *   <li>{@code to}:     0x200</li>
         * </ul>
         */
        final int fieldMask, mandatory;

        /**
         * Creates a new type which allows the fields specified by the given mask.
         */
        private Type(final int mask, final int mandatory) {
            this.fieldMask = mask;
            this.mandatory = mandatory;
        }
    }

    /**
     * Returns a mask of fields for which a non-null value has been defined.
     * The bit values are defined in the {@link XLink.Type#fieldMask} javadoc.
     */
    private int fieldMask() {
        int mask = 0;
        if (type    != null) mask |= 0x1;
        if (href    != null) mask |= 0x2;
        if (role    != null) mask |= 0x4;
        if (arcrole != null) mask |= 0x8;
        if (title   != null) mask |= 0x10;
        if (show    != null) mask |= 0x20;
        if (actuate != null) mask |= 0x40;
        if (label   != null) mask |= 0x80;
        if (from    != null) mask |= 0x100;
        if (to      != null) mask |= 0x200;
        return mask;
    }

    /**
     * Returns the type of link. May have one of the following values:
     * <p>
     * <ul>
     *   <li><b>simple:</b>   a simple link</li>
     *   <li><b>extended:</b> an extended, possibly multi-resource, link</li>
     *   <li><b>locator:</b>  a pointer to an external resource</li>
     *   <li><b>resource:</b> an internal resource</li>
     *   <li><b>arc:</b>      a traversal rule between resources</li>
     *   <li><b>title:</b>    a descriptive title for another linking element</li>
     * </ul>
     * <p>
     * If the {@link #setType(XLink.Type)} method has never been invoked, then this method
     * will infer a type from the attributes having a non-null value.
     *
     * @return The type of link, or {@code null}.
     */
    @XmlAttribute(name = "type", namespace = Namespaces.XLINK, required = true)
    public Type getType() {
        if (type != null) {
            return type;
        }
        Type best = null;
        int min = Integer.SIZE;
        final int defined = fieldMask();
        final int undefined = ~(defined | 0x1);
        for (final Type candidate : Type.values()) {
            // Test if this XLink instance defines only values allowed by the candidate type.
            if ((defined & ~candidate.fieldMask) != 0) {
                continue;
            }
            // Test if this XLink instance defines all mandatory fields.
            if ((undefined & candidate.mandatory) != 0) {
                continue;
            }
            // Select the type requerying the smallest amount of fields.
            final int n = Integer.bitCount(undefined & candidate.fieldMask);
            if (n < min) {
                min = n;
                best = candidate;
            }
        }
        return best; // May still null.
    }

    /**
     * Sets the type of link. A non-null value will overwrite the value inferred automatically
     * by {@link #getType()}. A {@code null} value will restore the automatic type detection.
     *
     * @param type The new type of link, or {@code null} for removing explicit setting.
     */
    public void setType(final Type type) {
        if (type != null && (fieldMask() & ~type.fieldMask) != 0) {
            throw new IllegalStateException(message(getType()));
        }
        this.type = type;
    }

    /**
     * Checks if the given property can be set.
     *
     * @param  field The property code, as documented in {@link XLink.Type#fieldMask}.
     * @throws IllegalStateException If the given field can not be set.
     */
    private void canWrite(final int field) throws IllegalStateException {
        final Type type = this.type;
        if (type != null && (type.fieldMask & field) == 0) {
            throw new IllegalStateException(message(type));
        }
    }

    /**
     * Returns an error message for an illegal operation applied on a link of the given type.
     */
    private static String message(final Type type) {
        return Errors.format(Errors.Keys.ILLEGAL_OPERATION_FOR_VALUE_CLASS_$1, type.name().toLowerCase());
    }

    /**
     * Returns a URN to an external resources, or to an other part of a XML document, or an
     * identifier.
     *
     * {@note This serves a role similar to <code>idref</code>. The <code>idref</code>
     * attribute allows an XML element to refer to another XML element that has a
     * corresponding <code>id</code> attribute.}
     *
     * @return A URN to a resources, or {@code null} if none.
     * @category locator
     */
    @XmlAttribute(name = "href", namespace = Namespaces.XLINK)
    public URI getHRef() {
        return href;
    }

    /**
     * Sets the URN to a resources.
     *
     * @param href A URN to a resources, or {@code null} if none.
     * @throws IllegalStateException If the link type {@linkplain #setType has been explicitely set}.
     *         and that type does not allow the {@code "href"} attribute.
     *
     * @category locator
     */
    public void setHRef(final URI href) throws IllegalStateException {
        canWrite(0x2);
        this.href = href;
    }

    /**
     * Returns a URI reference for some description of the arc role.
     *
     * @return A URI reference for some description of the arc role, or {@code null} if none.
     * @category semantic
     */
    @XmlAttribute(name = "role", namespace = Namespaces.XLINK)
    public URI getRole() {
        return role;
    }

    /**
     * Sets the URI reference for some description of the arc role.
     *
     * @param role A URI reference for some description of the arc role, or {@code null} if none.
     * @throws IllegalStateException If the link type {@linkplain #setType has been explicitely set}.
     *         and that type does not allow the {@code "role"} attribute.
     *
     * @category semantic
     */
    public void setRole(final URI role) throws IllegalStateException {
        canWrite(0x4);
        this.role = role;
    }

    /**
     * Returns a URI reference for some description of the arc role.
     *
     * @return A URI reference for some description of the arc role, or {@code null} if none.
     * @category semantic
     */
    @XmlAttribute(name = "arcrole", namespace = Namespaces.XLINK)
    public URI getArcRole() {
        return arcrole;
    }

    /**
     * Sets a URI reference for some description of the arc role.
     *
     * @param arcrole A URI reference for some description of the arc role, or {@code null} if none.
     * @throws IllegalStateException If the link type {@linkplain #setType has been explicitely set}.
     *         and that type does not allow the {@code "arcrole"} attribute.
     *
     * @category semantic
     */
    public void setArcRole(final URI arcrole) throws IllegalStateException {
        canWrite(0x8);
        this.arcrole = arcrole;
    }

    /**
     * Returns a human-readable string with a short description for the arc.
     *
     * @return A human-readable string with a short description for the arc, or {@code null} if none.
     * @category semantic
     */
    @XmlAttribute(name = "title", namespace = Namespaces.XLINK)
    public InternationalString getTitle() {
        return title;
    }

    /**
     * Sets a human-readable string with a short description for the arc.
     *
     * @param title A human-readable string with a short description for the arc,
     *        or {@code null} if none.
     * @throws IllegalStateException If the link type {@linkplain #setType has been explicitely set}.
     *         and that type does not allow the {@code "title"} attribute.
     *
     * @category semantic
     */
    public void setTitle(final InternationalString title) throws IllegalStateException {
        canWrite(0x10);
        this.title = title;
    }

    /**
     * Communicates the desired presentation of the ending resource on traversal
     * from the starting resource.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.18
     *
     * @see XLink#getShow()
     *
     * @since 3.18
     * @module
     */
    @XmlEnum
    public static enum Show {
        /**
         * Load ending resource in a new window, frame, pane, or other presentation context.
         */
        @XmlEnumValue("new") NEW,

        /**
         * Load the resource in the same window, frame, pane, or other presentation context.
         */
        @XmlEnumValue("replace") REPLACE,

        /**
         * Load ending resource in place of the presentation of the starting resource.
         */
        @XmlEnumValue("embed") EMBED,

        /**
         * Behavior is unconstrained; examine other markup in the link for hints.
         */
        @XmlEnumValue("other") OTHER,

        /**
         * Behavior is unconstrained.
         */
        @XmlEnumValue("none") NONE
    }

    /**
     * Returns the desired presentation of the ending resource on traversal
     * from the starting resource. It's value should be treated as follows:
     * <p>
     * <ul>
     *   <li><b>new:</b>     load ending resource in a new window, frame, pane, or other presentation context</li>
     *   <li><b>replace:</b> load the resource in the same window, frame, pane, or other presentation context</li>
     *   <li><b>embed:</b>   load ending resource in place of the presentation of the starting resource</li>
     *   <li><b>other:</b>   behavior is unconstrained; examine other markup in the link for hints</li>
     *   <li><b>none:</b>    behavior is unconstrained</li>
     * </ul>
     *
     * @return The desired presentation of the ending resource, or {@code null} if unspecified.
     * @category behavior
     */
    @XmlAttribute(name = "show", namespace = Namespaces.XLINK)
    public Show getShow() {
        return show;
    }

    /**
     * Sets the desired presentation of the ending resource on traversal from the starting resource.
     *
     * @param show The desired presentation of the ending resource, or {@code null} if unspecified.
     * @throws IllegalStateException If the link type {@linkplain #setType has been explicitely set}.
     *         and that type does not allow the {@code "show"} attribute.
     *
     * @category behavior
     */
    public void setShow(final Show show) throws IllegalStateException {
        canWrite(0x20);
        this.show = show;
    }

    /**
     * Communicates the desired timing of traversal from the starting resource to the ending
     * resource.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.18
     *
     * @see XLink#getActuate()
     *
     * @since 3.18
     * @module
     */
    @XmlEnum
    public static enum Actuate {
        /**
         * Traverse to the ending resource immediately on loading the starting resource.
         */
        @XmlEnumValue("onLoad") ON_LOAD,

        /**
         * Traverse from the starting resource to the ending resource only on a post-loading event
         * triggered for this purpose.
         */
        @XmlEnumValue("onRequest") ON_REQUEST,

        /**
         * Behavior is unconstrained; examine other markup in link for hints.
         */
        @XmlEnumValue("other") OTHER,

        /**
         * Behavior is unconstrained.
         */
        @XmlEnumValue("none") NONE
    }

    /**
     * Returns the desired timing of traversal from the starting resource to the ending
     * resource. It's value should be treated as follows:
     * <p>
     * <ul>
     *   <li><b>onLoad:</b>    traverse to the ending resource immediately on loading the starting resource</li>
     *   <li><b>onRequest:</b> traverse from the starting resource to the ending resource only on a post-loading event triggered for this purpose</li>
     *   <li><b>other:</b>     behavior is unconstrained; examine other markup in link for hints</li>
     *   <li><b>none:</b>      behavior is unconstrained</li>
     * </ul>
     *
     * @return The desired timing of traversal from the starting resource to the ending resource,
     *         or {@code null} if unspecified.
     * @category behavior
     */
    @XmlAttribute(name = "actuate", namespace = Namespaces.XLINK)
    public Actuate getActuate() {
        return actuate;
    }

    /**
     * Sets the desired timing of traversal from the starting resource to the ending resource.
     *
     * @param actuate The desired timing of traversal from the starting resource to the ending
     *        resource, or {@code null} if unspecified.
     * @throws IllegalStateException If the link type {@linkplain #setType has been explicitely set}.
     *         and that type does not allow the {@code "actuate"} attribute.
     *
     * @category behavior
     */
    public void setActuate(final Actuate actuate) throws IllegalStateException {
        canWrite(0x40);
        this.actuate = actuate;
    }

    /**
     * Returns an identification of the target of a {@code from} or {@code to} attribute.
     *
     * @return An identification of the target of a {@code from} or {@code to} attribute, or {@code null}.
     * @category traversal
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets an identification of the target of a {@code from} or {@code to} attribute.
     *
     * @param label An identification of the target of a {@code from} or {@code to} attribute,
     *        or {@code null}.
     * @throws IllegalStateException If the link type {@linkplain #setType has been explicitely set}.
     *         and that type does not allow the {@code "label"} attribute.
     *
     * @category traversal
     */
    public void setLabel(final String label) throws IllegalStateException {
        canWrite(0x80);
        this.label = label;
    }

    /**
     * Returns the starting resource. The value must correspond to the same value for some
     * {@code label} attribute.
     *
     * @return The starting resource, or {@code null}.
     * @category traversal
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the starting resource. The value must correspond to the same value for some
     * {@code label} attribute.
     *
     * @param from The starting resource, or {@code null}.
     * @throws IllegalStateException If the link type {@linkplain #setType has been explicitely set}.
     *         and that type does not allow the {@code "from"} attribute.
     *
     * @category traversal
     */
    public void setFrom(final String from) throws IllegalStateException {
        canWrite(0x100);
        this.from = from;
    }

    /**
     * Returns the ending resource. The value must correspond to the same value for some
     * {@code label} attribute.
     *
     * @return The ending resource, or {@code null}.
     * @category traversal
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the ending resource. The value must correspond to the same value for some
     * {@code label} attribute.
     *
     * @param to The ending resource, or {@code null}.
     * @throws IllegalStateException If the link type {@linkplain #setType has been explicitely set}.
     *         and that type does not allow the {@code "to"} attribute.
     *
     * @category traversal
     */
    public void setTo(final String to) throws IllegalStateException {
        canWrite(0x200);
        this.to = to;
    }

    /**
     * Compares this {@code XLink} with the given object for equality.
     *
     * @param object The object to compare with this XLink.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object != null && object.getClass().equals(getClass())) {
            final XLink that = (XLink) object;
            return Utilities.equals(this.type,    that.type)    &&
                   Utilities.equals(this.href,    that.href)    &&
                   Utilities.equals(this.role,    that.role)    &&
                   Utilities.equals(this.arcrole, that.arcrole) &&
                   Utilities.equals(this.title,   that.title)   &&
                   Utilities.equals(this.show,    that.show)    &&
                   Utilities.equals(this.actuate, that.actuate) &&
                   Utilities.equals(this.label,   that.label)   &&
                   Utilities.equals(this.from,    that.from)    &&
                   Utilities.equals(this.to,      that.to);
        }
        return false;
    }

    /**
     * Returns a hash code value for this XLink.
     */
    @Override
    public int hashCode() {
        return Utilities.hash(href,
               Utilities.hash(role,
               Utilities.hash(arcrole,
               Utilities.hash(title,
               Utilities.hash(show,
               Utilities.hash(actuate,
               Utilities.hash(label,
               Utilities.hash(from,
               Utilities.hash(to, (type != null ? type.hashCode() : 0) ^ (int) serialVersionUID)))))))));
    }

    /**
     * Returns a string representation of this object. The default implementation returns the
     * simple class name followed by non-null attributes, as in the example below:
     *
     * {@preformat text
     *     XLink[type="locator", href="urn:ogc:def:method:EPSG::4326"]
     * }
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this)).append('[');
        append(buffer, "type",    getType());
        append(buffer, "href",    getHRef());
        append(buffer, "role",    getRole());
        append(buffer, "arcrole", getArcRole());
        append(buffer, "title",   getTitle());
        append(buffer, "show",    getShow());
        append(buffer, "actuate", getActuate());
        append(buffer, "label",   getLabel());
        append(buffer, "from",    getFrom());
        append(buffer, "to",      getTo());
        return buffer.append(']').toString();
    }

    /**
     * Appends the given attribute in the given buffer if the attribute value is not null.
     * If the given value is an attribute, the XML name will be used rather than the Java
     * field name.
     */
    private static void append(final StringBuilder buffer, final String label, Object value) {
        if (value != null) {
            if (buffer.charAt(buffer.length() - 1) != '[') {
                buffer.append(", ");
            }
            if (value instanceof Enum<?>) try {
                final XmlEnumValue xml = value.getClass().getField(((Enum<?>) value).name()).getAnnotation(XmlEnumValue.class);
                if (xml != null) {
                    value = xml.value();
                }
            } catch (NoSuchFieldException e) {
                // Should never happen with Enums. But if it
                // happen anyway, this is not a fatal error.
                Logging.unexpectedException(XLink.class, "toString", e);
            }
            buffer.append(label).append("=\"").append(value).append('"');
        }
    }
}

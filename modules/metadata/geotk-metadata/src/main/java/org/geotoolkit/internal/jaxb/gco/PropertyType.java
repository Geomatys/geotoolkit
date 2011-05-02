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
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.UUIDs;


/**
 * Base class for adapters from GeoAPI interfaces to their Geotk implementation.
 * Implementation subclasses are actually both JAXB adapters and wrappers around
 * the value to be marshalled. Wrappers exist because ISO 19139 have the strange
 * habit to wrap every properties in an extra level, for example:
 *
 * {@preformat xml
 *   <CI_ResponsibleParty>
 *     <contactInfo>
 *       <CI_Contact>
 *         ...
 *       </CI_Contact>
 *     </contactInfo>
 *   </CI_ResponsibleParty>
 * }
 *
 * The {@code </CI_Contact>} level is not really necessary, and JAXB is not designed for inserting
 * such level since it is not the usual way to write XML. In order to get this output with JAXB,
 * we have to wrap metadata object in an additional object. So each {@code PropertyType} subclass
 * is both a JAXB adapter and a wrapper. We have merged those functionalities in order to avoid
 * doubling the amount of classes, which is already large.
 * <p>
 * In ISO 19139 terminology:
 * <ul>
 *   <li>the public classes defined in the {@code org.geotoolkit.metadata.iso} packages are defined
 *       as {@code Foo_Type} in ISO 19139, where <var>Foo</var> is the ISO name of a class.</li>
 *   <li>the {@code MetadataAdapter} subclasses are defined as {@code Foo_PropertyType} in
 *       ISO 19139 schemas.</li>
 * </ul>
 *
 * @param <ValueType> The adapter subclass.
 * @param <BoundType> The interface being adapted.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see XmlAdapter
 *
 * @since 2.5
 * @module
 */
public abstract class PropertyType<ValueType extends PropertyType<ValueType,BoundType>, BoundType>
        extends XmlAdapter<ValueType,BoundType>
{
    /**
     * The wrapped GeoAPI metadata interface.
     */
    protected BoundType metadata;

    /**
     * An identifier for the metadata, or {@code null} if none. This field is initialized
     * at construction time to the value registered in {@link UUIDs}, if any.
     *
     * @since 3.13
     */
    @XmlAttribute
    protected String uuid;

    /**
     * The {@code gco:ObjectReference} attributes, if any. Those attributes are
     * {@code uuidref}, {@code type}, {@code xlink:href}, {@code xlink:role},
     * {@code xlink:arcrole}, {@code xlink:title}, {@code xlink:show} and
     * {@code xlink:actuate}.
     *
     * @since 3.18
     */
    private ObjectReference reference;

    /**
     * Empty constructor for subclasses only.
     */
    protected PropertyType() {
    }

    /**
     * Builds an adapter for the given GeoAPI interface.
     *
     * @param metadata The interface to wrap.
     */
    protected PropertyType(final BoundType metadata) {
        this.metadata = metadata;
        uuid = UUIDs.DEFAULT.getUUID(metadata);
    }

    /**
     * Returns the object reference, which is guaranteed to be non-null.
     */
    private ObjectReference reference() {
        if (reference == null) {
            reference = new ObjectReference();
        }
        return reference;
    }

    /**
     * Returns {@code true} if the wrapped metadata has a non-null "{@code uuidref}" attribute.
     * This method is invoked by subclasses in order to determine if they shall marshall the
     * metadata itself, or just the UUID.
     *
     * @return {@code true} if the wrapped metadata has a "{@code uuidref}" attribute.
     *
     * @since 3.18
     */
    protected final boolean hasUUIDREF() {
        final ObjectReference reference = this.reference;
        return (reference != null) && (reference.uuidref != null);
    }

    /**
     * A URN to an external resources, or to an other part of a XML document, or an identifier.
     * The {@code uuidref} attribute is used to refer to an XML element that has a corresponding
     * {@code uuid} attribute.
     *
     * @return the current value, or {@code null} if none.
     * @since 3.18 (derived from 3.13)
     */
    @XmlAttribute(name = "uuidref")
    public final String getUUIDREF() {
        final ObjectReference reference = this.reference;
        return (reference != null) ? reference.uuidref : null;
    }

    /**
     * Sets the {@code uuidref} attribute value.
     *
     * @param uuidref The new attribute value.
     * @since 3.18
     */
    public final void setUUIDREF(final String uuidref) {
        reference().uuidref = uuidref;
    }

    /**
     * The reason why a mandatory attribute if left unspecified.
     *
     * @return the current value, or {@code null} if none.
     * @since 3.18
     */
    @XmlAttribute(name = "nilReason")
    public final String getNilReason() {
        final ObjectReference reference = this.reference;
        return (reference != null) ? reference.nilReason : null;
    }

    /**
     * Sets the {@code nilReason} attribute value.
     *
     * @param nilReason The new attribute value.
     * @since 3.18
     */
    public final void setNilReason(final String nilReason) {
        reference().nilReason = nilReason;
    }

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
     * @return the current value, or {@code null} if none.
     * @since 3.18
     */
    @XmlAttribute(name = "type", namespace = Namespaces.XLINK)
    public final String getType() {
        final ObjectReference reference = this.reference;
        return (reference != null) ? reference.type : null;
    }

    /**
     * Sets the {@code type} attribute value.
     *
     * @param type The new attribute value.
     * @since 3.18
     */
    public final void setType(final String type) {
        reference().type = type;
    }

    /**
     * A URN to an external resources, or to an other part of a XML document, or an identifier.
     * The {@code idref} attribute allows an XML element to refer to another XML element that
     * has a corresponding {@code id} attribute.
     *
     * @return the current value, or {@code null} if none.
     * @since 3.18
     */
    @XmlAttribute(name = "href", namespace = Namespaces.XLINK)
    public final URI getHREF() {
        final ObjectReference reference = this.reference;
        return (reference != null) ? reference.href : null;
    }

    /**
     * Sets the {@code href} attribute value.
     *
     * @param href The new attribute value.
     * @since 3.18
     */
    public final void setHREF(final URI href) {
        reference().href = href;
    }

    /**
     * A URI reference for some description of the arc role.
     *
     * @return the current value, or {@code null} if none.
     * @since 3.18
     */
    @XmlAttribute(name = "role", namespace = Namespaces.XLINK)
    public final URI getRole() {
        final ObjectReference reference = this.reference;
        return (reference != null) ? reference.role : null;
    }

    /**
     * Sets the {@code role} attribute value.
     *
     * @param role The new attribute value.
     * @since 3.18
     */
    public final void setRole(final URI role) {
        reference().role = role;
    }

    /**
     * A URI reference for some description of the arc role.
     *
     * @return the current value, or {@code null} if none.
     * @since 3.18
     */
    @XmlAttribute(name = "arcrole", namespace = Namespaces.XLINK)
    public final URI getArcRole() {
        final ObjectReference reference = this.reference;
        return (reference != null) ? reference.arcrole : null;
    }

    /**
     * Sets the {@code arcrole} attribute value.
     *
     * @param arcrole The new attribute value.
     * @since 3.18
     */
    public final void setArcRole(final URI arcrole) {
        reference().arcrole = arcrole;
    }

    /**
     * Just as with resources, this is simply a human-readable string with a short description
     * for the arc.
     *
     * @return the current value, or {@code null} if none.
     * @since 3.18
     */
    @XmlAttribute(name = "title", namespace = Namespaces.XLINK)
    public final String getTitle() {
        final ObjectReference reference = this.reference;
        return (reference != null) ? reference.title : null;
    }

    /**
     * Sets the {@code title} attribute value.
     *
     * @param title The new attribute value.
     * @since 3.18
     */
    public final void setTitle(final String title) {
        reference().title = title;
    }

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
     * @return the current value, or {@code null} if none.
     * @since 3.18
     */
    @XmlAttribute(name = "show", namespace = Namespaces.XLINK)
    public final String getShow() {
        final ObjectReference reference = this.reference;
        return (reference != null) ? reference.show : null;
    }

    /**
     * Sets the {@code show} attribute value.
     *
     * @param show The new attribute value.
     * @since 3.18
     */
    public final void setShow(final String show) {
        reference().show = show;
    }

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
     * @return the current value, or {@code null} if none.
     * @since 3.18
     */
    @XmlAttribute(name = "actuate", namespace = Namespaces.XLINK)
    public final String getActuate() {
        final ObjectReference reference = this.reference;
        return (reference != null) ? reference.actuate : null;
    }

    /**
     * Sets the {@code actuate} attribute value.
     *
     * @param actuate The new attribute value.
     * @since 3.18
     */
    public final void setActuate(final String actuate) {
        reference().actuate = actuate;
    }


    // ======== XmlAdapter methods ===============================================================


    /**
     * Creates a new instance of this class wrapping the given metadata.
     * This method is invoked by {@link #marshal} after making sure that
     * {@code value} is not null.
     *
     * @param value The GeoAPI interface to wrap.
     * @return The adapter.
     */
    protected abstract ValueType wrap(final BoundType value);

    /**
     * Converts a GeoAPI interface to the appropriate adapter for the way it will be
     * marshalled into an XML file or stream. JAXB calls automatically this method at
     * marshalling time.
     *
     * @param value The bound type value, here the interface.
     * @return The adapter for the given value.
     */
    @Override
    public final ValueType marshal(final BoundType value) {
        if (value == null) {
            return null;
        }
        return wrap(value);
    }

    /**
     * Converts an adapter read from an XML stream to the GeoAPI interface which will
     * contains this value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for a metadata value.
     * @return An instance of the GeoAPI interface which represents the metadata value.
     *
     * @todo We should replace the (BoundType) cast by a call to Class.cast(Object).
     */
    @Override
    @SuppressWarnings("unchecked")
    public final BoundType unmarshal(final ValueType value) {
        if (value == null) {
            return null;
        }
        if (value.metadata == null) {
            final String uuidref = value.getUUIDREF();
            if (uuidref != null) {
                value.metadata = (BoundType) UUIDs.DEFAULT.lookup(uuidref);
            }
        }
        return value.metadata;
    }

    /**
     * Returns the Geotk implementation class generated from the metadata value. The overriding
     * method in subclasses will be systematically called at marshalling time by JAXB.
     * <p>
     * The return value is usually an implementation of {@code BoundType}. But in
     * some situations this is Java type like {@link String}. For this raison the
     * return type is declared as {@code Object} here, but subclasses shall restrict
     * that to a more specific type.
     *
     * @return The metadata to be marshalled.
     *
     * @since 3.05
     */
    public abstract Object getElement();
}

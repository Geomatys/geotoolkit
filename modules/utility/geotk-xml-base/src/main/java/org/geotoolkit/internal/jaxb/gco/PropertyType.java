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
package org.geotoolkit.internal.jaxb.gco;

import java.util.UUID;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.sis.xml.XLink;
import org.apache.sis.xml.NilObject;
import org.apache.sis.xml.NilReason;
import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.xml.IdentifierMap;
import org.geotoolkit.xml.IdentifierSpace;
import org.geotoolkit.xml.IdentifiedObject;
import org.geotoolkit.internal.jaxb.MarshalContext;
import org.geotoolkit.util.SimpleInternationalString;


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
 *   <li>the {@code PropertyType} subclasses are defined as {@code Foo_PropertyType} in
 *       ISO 19139 schemas.</li>
 * </ul>
 *
 * @param <ValueType> The adapter subclass.
 * @param <BoundType> The interface being adapted.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
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
     * Either an {@link ObjectReference} or a {@link String}.
     * <p>
     * <ul>
     *   <li>{@link ObjectReference} defines the {@code idref}, {@code uuidref}, {@code xlink:href},
     *       {@code xlink:role}, {@code xlink:arcrole}, {@code xlink:title}, {@code xlink:show} and
     *       {@code xlink:actuate} attributes.</li>
     *   <li>{@link String} defines the {@code nilReason} attribute.</li>
     * </ul>
     * <p>
     * Those two properties are exclusive (if the user define an object reference, then the
     * attribute is not nil).
     *
     * @since 3.18 (derived from 3.13)
     */
    private Object reference;

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
        if (metadata instanceof IdentifiedObject) {
            final IdentifierMap map = ((IdentifiedObject) metadata).getIdentifierMap();
            XLink  link = map.getSpecialized(IdentifierSpace.XLINK);
            UUID   uuid = map.getSpecialized(IdentifierSpace.UUID);
            String anyUUID = (uuid != null) ? uuid.toString() : map.get(IdentifierSpace.UUID);
            if (anyUUID != null || link != null) {
                if (uuid == null) {
                    uuid = ObjectReference.toUUID(anyUUID); // May still null.
                }
                if (uuid == null || MarshalContext.linker().canUseReference(getBoundType(), metadata, uuid)) {
                    reference = new ObjectReference(uuid, anyUUID, link);
                    return;
                }
            }
        }
        if (metadata instanceof NilObject) {
            final NilReason reason = ((NilObject) metadata).getNilReason();
            if (reason != null) {
                reference = reason.toString();
            }
        }
    }

    /**
     * Returns the object reference, or {@code null} if none and the {@code create} argument is
     * {@code false}. If the {@code create} argument is {@code true}, then this method will create
     * the object reference when first needed. In the later case, any previous {@code gco:nilReason}
     * will be overwritten since the object is not nil.
     */
    private ObjectReference reference(final boolean create) {
        final Object ref = reference;
        if (ref instanceof ObjectReference) {
            return (ObjectReference) ref;
        } else if (create) {
            final ObjectReference newRef = new ObjectReference();
            reference = newRef;
            return newRef;
        } else {
            return null;
        }
    }

    /**
     * Returns the {@code xlink}, or {@code null} if none and {@code create} is {@code false}.
     * If the {@code create} argument is {@code true}, then this method will create the XLink
     * when first needed. In the later case, any previous {@code gco:nilReason} will be
     * overwritten since the object is not nil.
     */
    private XLink xlink(final boolean create) {
        final ObjectReference ref = reference(create);
        if (ref == null) {
            return null;
        }
        XLink xlink = ref.xlink;
        if (create && xlink == null) {
            ref.xlink = xlink = new XLink();
            xlink.setType(XLink.Type.SIMPLE); // The "simple" type is fixed in the "gco" schema.
        }
        return xlink;
    }

    /**
     * The reason why a mandatory attribute if left unspecified.
     *
     * @return the current value, or {@code null} if none.
     * @category gco:PropertyType
     * @since 3.18
     */
    @XmlAttribute(name = "nilReason", namespace = Namespaces.GCO)
    public final String getNilReason() {
        final Object ref = reference;
        return (ref instanceof String) ? (String) ref : null;
    }

    /**
     * Sets the {@code nilReason} attribute value. This method does nothing if reference
     * is specified, since in such case the object can not be nil.
     *
     * @param nilReason The new attribute value.
     * @category gco:PropertyType
     * @since 3.18
     */
    public final void setNilReason(final String nilReason) {
        if (!(reference instanceof ObjectReference)) {
            reference = nilReason;
        }
    }

    /**
     * Returns {@code true} if the wrapped metadata should not be marshalled. It may be because
     * a non-null "{@code uuidref}" attribute has been specified (in which case the UUID reference
     * will be marshalled in place of the full metadata), or any other reason that may be added in
     * future implementations.
     *
     * @return {@code true} if the wrapped metadata should not be marshalled.
     * @since 3.18
     */
    protected final boolean skip() {
        if (metadata instanceof NilObject) {
            return true;
        }
        final Object ref = reference;
        return (ref instanceof ObjectReference) && ((ObjectReference) ref).anyUUID != null;
    }

    /**
     * A URN to an external resources, or to an other part of a XML document, or an identifier.
     * The {@code uuidref} attribute is used to refer to an XML element that has a corresponding
     * {@code uuid} attribute.
     *
     * @return the current value, or {@code null} if none.
     * @category gco:ObjectReference
     * @since 3.18 (derived from 3.13)
     */
    @XmlAttribute(name = "uuidref") // Defined in "gco" as unqualified attribute.
    public final String getUUIDREF() {
        final ObjectReference ref = reference(false);
        return (ref != null) ? ref.anyUUID : null;
    }

    /**
     * Sets the {@code uuidref} attribute value.
     *
     * @param uuid The new attribute value.
     * @category gco:ObjectReference
     * @since 3.18
     */
    public final void setUUIDREF(final String uuid) {
        reference(true).anyUUID = uuid;
    }

    /**
     * Returns the given URI as a string, or returns {@code null} if the given argument is null.
     */
    private static String toString(final Object uri) {
        return (uri != null) ? uri.toString() : null;
    }

    /**
     * Parses the given URI, or returns {@code null} if the given argument is null or empty.
     */
    private static URI toURI(final String uri) throws URISyntaxException {
        return MarshalContext.converters().toURI(uri);
    }

    /**
     * A URN to an external resources, or to an other part of a XML document, or an identifier.
     * The {@code idref} attribute allows an XML element to refer to another XML element that
     * has a corresponding {@code id} attribute.
     *
     * @return the current value, or {@code null} if none.
     * @category xlink
     * @since 3.18
     */
    @XmlAttribute(name = "href", namespace = Namespaces.XLINK)
    public final String getHRef() {
        final XLink link = xlink(false);
        return (link != null) ? toString(link.getHRef()) : null;
    }

    /**
     * Sets the {@code href} attribute value.
     *
     * @param href The new attribute value.
     * @throws URISyntaxException If th given string can not be parsed as a URI.
     * @category xlink
     * @since 3.18
     */
    public final void setHRef(final String href) throws URISyntaxException {
        xlink(true).setHRef(toURI(href));
    }

    /**
     * A URI reference for some description of the arc role.
     *
     * @return the current value, or {@code null} if none.
     * @category xlink
     * @since 3.18
     */
    @XmlAttribute(name = "role", namespace = Namespaces.XLINK)
    public final String getRole() {
        final XLink link = xlink(false);
        return (link != null) ? toString(link.getRole()) : null;
    }

    /**
     * Sets the {@code role} attribute value.
     *
     * @param role The new attribute value.
     * @throws URISyntaxException If th given string can not be parsed as a URI.
     * @category xlink
     * @since 3.18
     */
    public final void setRole(final String role) throws URISyntaxException {
        xlink(true).setRole(toURI(role));
    }

    /**
     * A URI reference for some description of the arc role.
     *
     * @return the current value, or {@code null} if none.
     * @category xlink
     * @since 3.18
     */
    @XmlAttribute(name = "arcrole", namespace = Namespaces.XLINK)
    public final String getArcRole() {
        final XLink link = xlink(false);
        return (link != null) ? toString(link.getArcRole()) : null;
    }

    /**
     * Sets the {@code arcrole} attribute value.
     *
     * @param arcrole The new attribute value.
     * @throws URISyntaxException If th given string can not be parsed as a URI.
     * @category xlink
     * @since 3.18
     */
    public final void setArcRole(final String arcrole) throws URISyntaxException {
        xlink(true).setArcRole(toURI(arcrole));
    }

    /**
     * Just as with resources, this is simply a human-readable string with a short description
     * for the arc.
     *
     * @return the current value, or {@code null} if none.
     * @category xlink
     * @since 3.18
     */
    @XmlAttribute(name = "title", namespace = Namespaces.XLINK)
    public final String getTitle() {
        final XLink link = xlink(false);
        return (link != null) ? toString(link.getTitle()) : null;
    }

    /**
     * Sets the {@code title} attribute value.
     *
     * @param title The new attribute value.
     * @category xlink
     * @since 3.18
     */
    public final void setTitle(String title) {
        if (title != null && !(title = title.trim()).isEmpty()) {
            xlink(true).setTitle(new SimpleInternationalString(title));
        }
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
     * @category xlink
     * @since 3.18
     */
    @XmlAttribute(name = "show", namespace = Namespaces.XLINK)
    public final XLink.Show getShow() {
        final XLink link = xlink(false);
        return (link != null) ? link.getShow() : null;
    }

    /**
     * Sets the {@code show} attribute value.
     *
     * @param show The new attribute value.
     * @category xlink
     * @since 3.18
     */
    public final void setShow(final XLink.Show show) {
        xlink(true).setShow(show);
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
     * @category xlink
     * @since 3.18
     */
    @XmlAttribute(name = "actuate", namespace = Namespaces.XLINK)
    public final XLink.Actuate getActuate() {
        final XLink link = xlink(false);
        return (link != null) ? link.getActuate() : null;
    }

    /**
     * Sets the {@code actuate} attribute value.
     *
     * @param actuate The new attribute value.
     * @category xlink
     * @since 3.18
     */
    public final void setActuate(final XLink.Actuate actuate) {
        xlink(true).setActuate(actuate);
    }

    // Do NOT declare attributes xlink:label, xlink:from and xlink:to,
    // because they are not part of the xlink:simpleLink group.


    // ======== XmlAdapter methods ===============================================================


    /**
     * Returns the bound type, which is typically the GeoAPI interface. The default implementation
     * fetches the type using reflection, by looking at the second argument in the parameterized
     * types. However subclasses can override this method in order to return directly the type,
     * for type safety and performance reason.
     *
     * @return The bound type, which is typically the GeoAPI interface.
     */
    @SuppressWarnings("unchecked")
    protected Class<BoundType> getBoundType() {
        Class<?> classe = getClass();
        do {
            // Typically executed exactly once, but implemented as a loop anyway as a
            // safety in case we derive sub-classes from existing direct sub-classes.
            final Type type = classe.getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType) type;
                if (pt.getRawType() == PropertyType.class) {
                    return (Class) pt.getActualTypeArguments()[1];
                }
            }
        } while ((classe = classe.getSuperclass()) != null);
        throw new AssertionError(getClass()); // Should never happen.
    }

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
     * @param  value The adapter for a metadata value.
     * @return An instance of the GeoAPI interface which represents the metadata value.
     * @throws URISyntaxException If a URI can not be parsed.
     */
    @Override
    public final BoundType unmarshal(final ValueType value) throws URISyntaxException {
        return (value != null) ? value.resolve() : null;
    }

    /**
     * If the {@linkplain #metadata} is still null, tries to resolve it using UUID, XLink
     * or NilReason information. This method is invoked at unmarshalling time.
     *
     * @throws URISyntaxException If a URI can not be parsed.
     * @throws IllegalArgumentException If the UUID can not be parsed.
     */
    final BoundType resolve() throws URISyntaxException, IllegalArgumentException {
        final ObjectReference ref = reference(false);
        if (ref != null) {
            metadata = ref.resolve(getBoundType(), metadata);
        }
        if (metadata == null) {
            final String value = getNilReason();
            if (value != null) {
                final NilReason nilReason = MarshalContext.converters().toNilReason(value);
                if (nilReason != null) {
                    metadata = MarshalContext.linker().resolve(getBoundType(), nilReason);
                }
            }
        }
        return metadata;
    }

    /**
     * Returns the Geotk implementation class generated from the metadata value. The overriding
     * method in subclasses will be systematically called at marshalling time by JAXB.
     * <p>
     * The return value is usually an implementation of {@code BoundType}. But in
     * some situations this is Java type like {@link String}. For this raison the
     * return type is declared as {@code Object} here, but subclasses shall restrict
     * that to a more specific type.
     * <p>
     * Typical implementation ({@code BoundType} and {@code ValueType} need to be replaced
     * by the concrete class):
     *
     * {@preformat java
     *   •Override
     *   •XmlElementRef
     *   public BoundType getElement() {
     *       if (skip()) return null;
     *       final ValueType metadata = this.metadata;
     *       return (metadata instanceof BoundType) ? (BoundType) metadata : new BoundType(metadata);
     *   }
     * }
     *
     * The actual implementation may be slightly more complicated than the above if there is
     * various subclasses to check.
     *
     * @return The metadata to be marshalled.
     *
     * @since 3.05
     */
    public abstract Object getElement();
}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.internal.jaxb;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.lang.reflect.Field;

import org.opengis.metadata.Identifier;

import org.apache.sis.internal.simple.SimpleCitation;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.IdentifierSpace;

import static org.geotoolkit.util.collection.XCollections.addIfNonNull;


/**
 * The {@linkplain Identifier#getAuthority() authority of identifiers} that are not expected to be
 * marshalled in a {@code MD_Identifier} XML element. Those identifiers are also excluded from the
 * tree formatted by {@link org.geotoolkit.metadata.AbstractMetadata#asTree()}.
 * <p>
 * There is two kind of non-marshalled identifiers:
 *
 * <ul>
 *   <li><p>The XML attributes declared by ISO 19139 specification in the {@code gco:PropertyType}
 *       element: {@code gml:id}, {@code gco:uuid} and {@code xlink:href}. Those attributes are not
 *       part of the ISO 19115 specification. Those authorities are declared in the
 *       {@link IdentifierSpace} interfaces.</p></li>
 *
 *   <li><p>ISO 19115 attributes that we choose, for the Geotk implementation, to merge with
 *       other identifiers: ISBN and ISSN codes. Those attributes are declared in the
 *       {@link org.geotoolkit.metadata.iso.citation.Citations} class.</p></li>
 * </ul>
 *
 * In the current Geotk library, there is different places where identifiers are filtered on the
 * basis of this class, as below:
 *
 * {@preformat java
 *     if (identifier.getAuthority() instanceof NonMarshalledAuthority<?>) {
 *         // Omit that identifier.
 *     }
 * }
 *
 * @param <T> The type of object used as identifier values.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see IdentifierSpace
 *
 * @since 3.19
 * @module
 */
public final class NonMarshalledAuthority<T> extends SimpleCitation implements IdentifierSpace<T>, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -6309485399210742418L;

    /**
     * Ordinal values for switch statements. The constant defined here shall
     * mirror the constants defined in the {@link IdentifierSpace} interface
     * and {@link org.geotoolkit.metadata.iso.citation.DefaultCitation} class.
     */
    public static final int ID=0, UUID=1, HREF=2, XLINK=3, ISSN=4, ISBN=5;
    // If more codes are added, please update readResolve() below.

    /**
     * Ordinal values for switch statements, as one of the {@link #ID}, {@link #UUID},
     * <i>etc.</i> constants.
     * <p>
     * This value is not serialized because its value may not be consistent between different
     * versions of the Geotk library (the attribute name is more reliable). This instance
     * should be replaced by one of the exiting constants at deserialization time anyway.
     */
    final transient int ordinal;

    /**
     * Creates a new enum for the given attribute.
     *
     * @param attribute The XML attribute name, to be returned by {@link #getName()}.
     * @param ordinal   Ordinal value for switch statement, as one of the {@link #ID},
     *                  {@link #UUID}, <i>etc.</i> constants.
     */
    public NonMarshalledAuthority(final String attribute, final int ordinal) {
        super(attribute);
        this.ordinal = ordinal;
    }

    /**
     * Returns the XML attribute name with its prefix. Attribute names can be {@code "gml:id"},
     * {@code "gco:uuid"} or {@code "xlink:href"}.
     */
    @Override
    public String getName() {
        return title;
    }

    /**
     * Returns a string representation of this identifier space.
     */
    @Override
    public String toString() {
        return "IdentifierSpace[" + title + ']';
    }

    /**
     * Returns the first marshallable identifier from the given collection. This method omits
     * "special" identifiers (ISO 19139 attributes, ISBN codes...), which are recognized by
     * the implementation class of their authority.
     *
     * @param <T> The type of object used as identifier values.
     * @param  identifiers The collection from which to get identifiers, or {@code null}.
     * @return The first identifier, or {@code null} if none.
     */
    public static <T extends Identifier> T getMarshallable(final Collection<? extends T> identifiers) {
        if (identifiers != null) {
            for (final T id : identifiers) {
                if (id != null && !(id.getAuthority() instanceof NonMarshalledAuthority<?>)) {
                    return id;
                }
            }
        }
        return null;
    }

    /**
     * Sets the given identifier in the given collection. This method removes all identifiers
     * that are not ISO 19139 identifiers before to adds the given one in the collection. This
     * method is used when the given collection is expected to contains only one ISO 19115
     * identifier.
     *
     * @param <T> The type of object used as identifier values.
     * @param identifiers The collection in which to add the identifier.
     * @param id The identifier to add, or {@code null}.
     */
    public static <T extends Identifier> void setMarshallable(final Collection<T> identifiers, final T id) {
        final Iterator<T> it = identifiers.iterator();
        while (it.hasNext()) {
            final T old = it.next();
            if (old != null) {
                if (old.getAuthority() instanceof NonMarshalledAuthority<?>) {
                    continue; // Don't touch this identifier.
                }
            }
            it.remove();
        }
        addIfNonNull(identifiers, id);
    }

    /**
     * Returns a collection containing only the identifiers having an {@code NonMarshalledAuthority}.
     *
     * @param  <T> The type of object used as identifier values.
     * @param  identifiers The identifiers to getIdentifiers, or {@code null} if none.
     * @return The filtered identifiers, or {@code null} if none.
     */
    public static <T extends Identifier> Collection<T> getIdentifiers(final Collection<? extends T> identifiers) {
        Collection<T> filtered = null;
        if (identifiers != null) {
            int remaining = identifiers.size();
            for (final T candidate : identifiers) {
                if (candidate != null && candidate.getAuthority() instanceof NonMarshalledAuthority<?>) {
                    if (filtered == null) {
                        filtered = new ArrayList<>(remaining);
                    }
                    filtered.add(candidate);
                }
                remaining--;
            }
        }
        return filtered;
    }

    /**
     * Removes from the given collection every identifiers having an {@code NonMarshalledAuthority},
     * then adds the previously filtered identifiers (if any).
     *
     * @param <T> The type of object used as identifier values.
     * @param identifiers The collection from which to remove identifiers, or {@code null}.
     * @param filtered The previous filtered identifiers returned by {@link #getIdentifiers}.
     */
    public static <T extends Identifier> void setIdentifiers(final Collection<T> identifiers, final Collection<T> filtered) {
        if (identifiers != null) {
            for (final Iterator<T> it=identifiers.iterator(); it.hasNext();) {
                final T id = it.next();
                if (id == null || id.getAuthority() instanceof NonMarshalledAuthority<?>) {
                    it.remove();
                }
            }
            if (filtered != null) {
                identifiers.addAll(filtered);
            }
        }
    }

    /**
     * Returns one of the constants in the {@link DefaultCitation} class.
     */
    private static IdentifierSpace<?> getCitation(final String name) throws ObjectStreamException {
        try {
            final Field field = Class.forName("org.geotoolkit.metadata.iso.citation.DefaultCitation").getDeclaredField(name);
            field.setAccessible(true);
            return (IdentifierSpace<?>) field.get(null);
        } catch (ReflectiveOperationException e) {
            Logging.unexpectedException(NonMarshalledAuthority.class, "readResolve", e);
        }
        return null;
    }

    /**
     * Invoked at deserialization time in order to setIdentifiers the deserialized instance
     * by the appropriate instance defined in the {@link IdentifierSpace} interface.
     */
    private Object readResolve() throws ObjectStreamException {
        int code = 0;
        while (true) {
            final IdentifierSpace<?> candidate;
            switch (code) {
                case ID:    candidate = IdentifierSpace.ID;    break;
                case UUID:  candidate = IdentifierSpace.UUID;  break;
                case HREF:  candidate = IdentifierSpace.HREF;  break;
                case XLINK: candidate = IdentifierSpace.XLINK; break;
                case ISBN:  candidate = getCitation("ISBN");   break;
                case ISSN:  candidate = getCitation("ISSN");   break;
                default: return this;
            }
            if (candidate instanceof NonMarshalledAuthority<?> &&
                    ((NonMarshalledAuthority<?>) candidate).title.equals(title))
            {
                return candidate;
            }
            code++;
        }
    }
}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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

import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.lang.reflect.Field;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Series;
import org.opengis.util.InternationalString;

import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.IdentifierSpace;


/**
 * Implementation of authorities defined in the {@link IdentifierSpace} interfaces. This class
 * is actually a {@link org.opengis.metadata.citation.Citation} implementation. However it is
 * not designed for XML marshalling at this time.
 *
 * {@section Identifiers filtering}
 * In the current Geotk library, there is different places where identifiers are filtered on
 * the basis of this class, will code like:
 *
 * {@preformat java
 *     if (identifier.getAuthority() instanceof IdentifierAuthority<?>) {
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
public final class IdentifierAuthority<T> implements IdentifierSpace<T>, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -550506361603915113L;

    /**
     * Ordinal values for switch statements. The constant defined here shall
     * mirror the constants defined in the {@link IdentifierSpace} interface
     * and {@link org.geotoolkit.metadata.iso.citation.DefaultCitation} class.
     */
    public static final int ID=0, UUID=1, HREF=2, XLINK=3, ISSN=4, ISBN=5;
    // If more codes are added, please update readResolve() below.

    /**
     * The attribute name, to be returned by {@link #getName()}.
     */
    private final String attribute;

    /**
     * Ordinal values for switch statements, as one of the {@link #ID}, {@link #UUID},
     * <i>etc.</i> constants.
     *
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
    public IdentifierAuthority(final String attribute, final int ordinal) {
        this.attribute = attribute;
        this.ordinal   = ordinal;
    }

    /**
     * Returns the XML attribute name with its prefix. Attribute names can be {@code "gml:id"},
     * {@code "gco:uuid"} or {@code "xlink:href"}.
     */
    @Override
    public String getName() {
        return attribute;
    }

    /**
     * Returns the attribute name as an international string. This is the same value than the one
     * returned by {@link #getName()}, wrapped in a {@link SimpleInternationalString}
     * object.
     */
    @Override
    public InternationalString getTitle() {
        return new SimpleInternationalString(attribute);
    }

    /**
     * Unconditionally returns an empty collection.
     */
    @Override
    public Collection<InternationalString> getAlternateTitles() {
        return Collections.emptyList();
    }

    /**
     * Unconditionally returns an empty collection.
     */
    @Override
    public Collection<CitationDate> getDates() {
        return Collections.emptyList();
    }

    /**
     * Unconditionally returns {@code null}.
     */
    @Override
    public InternationalString getEdition() {
        return null;
    }

    /**
     * Unconditionally returns {@code null}.
     */
    @Override
    public Date getEditionDate() {
        return null;
    }

    /**
     * Unconditionally returns an empty collection.
     */
    @Override
    public Collection<Identifier> getIdentifiers() {
        return Collections.emptyList();
    }

    /**
     * Unconditionally returns an empty collection.
     */
    @Override
    public Collection<ResponsibleParty> getCitedResponsibleParties() {
        return Collections.emptyList();
    }

    /**
     * Unconditionally returns an empty collection.
     */
    @Override
    public Collection<PresentationForm> getPresentationForms() {
        return Collections.emptyList();
    }

    /**
     * Unconditionally returns {@code null}.
     */
    @Override
    public Series getSeries() {
        return null;
    }

    /**
     * Unconditionally returns {@code null}.
     */
    @Override
    public InternationalString getOtherCitationDetails() {
        return null;
    }

    /**
     * Unconditionally returns {@code null}.
     */
    @Override
    public InternationalString getCollectiveTitle() {
        return null;
    }

    /**
     * Unconditionally returns {@code null}.
     */
    @Override
    public String getISBN() {
        return null;
    }

    /**
     * Unconditionally returns {@code null}.
     */
    @Override
    public String getISSN() {
        return null;
    }

    /**
     * Returns a string representation of this identifier space.
     */
    @Override
    public String toString() {
        return "IdentifierSpace[" + attribute + ']';
    }

    /**
     * Returns the first identifier from the given collection which is not a "special" identifier
     * (ISO 19139 attributes).
     *
     * @param <T> The type of object used as identifier values.
     * @param  identifiers The collection from which to get identifiers, or {@code null}.
     * @return The first identifier, or {@code null} if none.
     */
    public static <T extends Identifier> T getIdentifier(final Collection<? extends T> identifiers) {
        if (identifiers != null) {
            for (final T id : identifiers) {
                if (id != null && !(id.getAuthority() instanceof IdentifierAuthority<?>)) {
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
    public static <T extends Identifier> void setIdentifier(final Collection<T> identifiers, final T id) {
        final Iterator<T> it = identifiers.iterator();
        while (it.hasNext()) {
            final T old = it.next();
            if (old != null) {
                if (old.getAuthority() instanceof IdentifierAuthority<?>) {
                    continue; // Don't touch this identifier.
                }
            }
            it.remove();
        }
        if (id != null) {
            identifiers.add(id);
        }
    }

    /**
     * Returns a collection containing only the identifiers having an {@code IdentifierAuthority}.
     *
     * @param  <T> The type of object used as identifier values.
     * @param  identifiers The identifiers to filter, or {@code null} if none.
     * @return The filtered identifiers, or {@code null} if none.
     */
    public static <T extends Identifier> Collection<T> filter(final Collection<? extends T> identifiers) {
        Collection<T> filtered = null;
        if (identifiers != null) {
            int remaining = identifiers.size();
            for (final T candidate : identifiers) {
                if (candidate != null && candidate.getAuthority() instanceof IdentifierAuthority<?>) {
                    if (filtered == null) {
                        filtered = new ArrayList<T>(remaining);
                    }
                    filtered.add(candidate);
                }
                remaining--;
            }
        }
        return filtered;
    }

    /**
     * Removes from the given collection every identifiers having an {@code IdentifierAuthority},
     * then add the previously filtered identifiers (if any).
     *
     * @param <T> The type of object used as identifier values.
     * @param identifiers The collection from which to remove identifiers, or {@code null}.
     * @param filtered The previous filtered identifiers returned by {@link #filter}.
     */
    public static <T extends Identifier> void replace(final Collection<T> identifiers, final Collection<T> filtered) {
        if (identifiers != null) {
            for (final Iterator<T> it=identifiers.iterator(); it.hasNext();) {
                final T id = it.next();
                if (id == null || id.getAuthority() instanceof IdentifierAuthority<?>) {
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
        } catch (Exception e) { // Too many possible exceptions for enumerating them all.
            Logging.unexpectedException(IdentifierAuthority.class, "readResolve", e);
        }
        return null;
    }

    /**
     * Invoked at deserialization time in order to replace the deserialized instance
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
            if (candidate instanceof IdentifierAuthority<?> &&
                    ((IdentifierAuthority<?>) candidate).attribute.equals(attribute))
            {
                return candidate;
            }
            code++;
        }
    }
}

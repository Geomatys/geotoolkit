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
package org.geotoolkit.xml;

import java.util.Date;
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


/**
 * Implementation of citations defined in the {@link IdentifierSpace} interfaces.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
final class IdentifierCitation<T> implements IdentifierSpace<T>, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -550506361603915113L;

    /**
     * The attribute name.
     */
    private final String attribute;

    /**
     * Creates a new enum for the given attribute.
     */
    IdentifierCitation(final String attribute) {
        this.attribute = attribute;
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
     * Returns a string representation of this identifier space. Current implementation returns
     * the XML attribute name in order to keep the content of {@link IdentifierMap#toString()}
     * easier to read.
     */
    @Override
    public String toString() {
        return attribute;
    }

    /**
     * Invoked at deserialization time in order to replace the deserialized instance
     * by the appropriate instance defined in the {@link IdentifierSpace} interface.
     */
    private Object readResolve() throws ObjectStreamException {
        for (final Field field : IdentifierSpace.class.getDeclaredFields()) {
            final IdentifierCitation<?> id;
            try {
                id = (IdentifierCitation<?>) field.get(null);
            } catch (IllegalAccessException e) {
                // We can still return 'this' as a fallback, so let continue.
                Logging.unexpectedException(IdentifierCitation.class, "readResolve", e);
                continue;
            }
            if (attribute.equals(id.attribute)) {
                return id;
            }
        }
        return this;
    }
}

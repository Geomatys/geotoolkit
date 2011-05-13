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

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Series;
import org.opengis.util.InternationalString;

import org.geotoolkit.util.SimpleInternationalString;


/**
 * Some identifier namespaces that are handled in a special way. The identifier namespaces are
 * usually defined as authorities in the {@link org.geotoolkit.metadata.iso.citation.Citations}
 * class. However a few identifiers defined in the {@code gco:ObjectIdentification} XML attribute
 * group are handled in a special way.
 * <p>
 * This enum can be used as keys in the map returned by {@link IdentifiedObject#getIdentifierMap()}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see org.geotoolkit.metadata.iso.citation.Citations
 *
 * @since 3.18
 * @module
 */
public enum IdentifierSpace implements Citation {
    /**
     * A standard GML attribute available on every object-with-identity. Its type is
     * {@code "xs:ID"} - i.e. it is a fragment identifier, unique within document scope only,
     * for internal cross-references. It is not useful by itself as a persistent unique identifier.
     */
    ID("gml:id"),

    /**
     * An optional attribute available on every object-with-identity provided in the GMD schemas
     * that implement ISO 19115 in XML. May be used as a persistent unique identifier, but only
     * available within GMD context.
     */
    UUID("gco:uuid"),

    /**
     * An optional attribute for URN to an external resources, or to an other part of a XML
     * document, or an identifier. This identifier is handled in a special way, since it is
     * defined in a {@link XLink} instance.
     *
     * @see XLink#getHRef()
     */
    HREF("xlink:href");

    /**
     * The attribute name.
     */
    private final String attribute;

    /**
     * Creates a new enum for the given attribute.
     */
    private IdentifierSpace(final String attribute) {
        this.attribute = attribute;
    }

    /**
     * Returns the attribute name with its prefix. Attribute name can be {@code "gml:id"},
     * {@code "gco:uuid"} or {@code "xlink:href"}.
     *
     * @return The attribute name.
     */
    @Override
    public String toString() {
        return attribute;
    }

    /**
     * Returns the attribute name as an international string. This is the same value than the
     * one returned by {@link #toString()}, wrapped in a {@link SimpleInternationalString} object.
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
}

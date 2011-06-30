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

import java.util.UUID;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.geotoolkit.util.SimpleInternationalString;


/**
 * Some identifier namespaces that are handled in a special way. The identifier namespaces are
 * usually defined as authorities in the {@link org.geotoolkit.metadata.iso.citation.Citations}
 * class. However a few identifiers defined in the {@code gco:ObjectIdentification} XML attribute
 * group are handled in a special way.
 * <p>
 * The values defined in this interface can be used as keys in the map returned by
 * {@link IdentifiedObject#getIdentifierMap()}.
 *
 * @param <T> The type of object used as identifier values.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see org.geotoolkit.metadata.iso.citation.Citations
 * @see IdentifiedObject
 *
 * @since 3.18
 * @module
 */
public interface IdentifierSpace<T> extends Citation {
    /**
     * A standard GML attribute available on every object-with-identity. Its type is
     * {@code "xs:ID"} - i.e. it is a fragment identifier, unique within document scope only,
     * for internal cross-references. It is not useful by itself as a persistent unique identifier.
     */
    IdentifierSpace<String> ID = new IdentifierCitation<String>("gml:id");

    /**
     * An optional attribute available on every object-with-identity provided in the GMD schemas
     * that implement ISO 19115 in XML. May be used as a persistent unique identifier, but only
     * available within GMD context.
     */
    IdentifierSpace<UUID> UUID = new IdentifierCitation<UUID>("gco:uuid");

    /**
     * An optional attribute for URN to an external resources, or to an other part of a XML
     * document, or an identifier.
     *
     * @see XLink#getHRef()
     */
    IdentifierSpace<XLink> HREF = new IdentifierCitation<XLink>("xlink:href");

    /**
     * Returns the attribute name as an international string. This is the same value than the
     * one returned by {@link #toString()}, wrapped in a {@link SimpleInternationalString} object.
     */
    @Override
    InternationalString getTitle();

    /**
     * Returns the attribute name with its prefix. Attribute name can be {@code "gml:id"},
     * {@code "gco:uuid"} or {@code "xlink:href"}.
     *
     * @return The attribute name.
     */
    @Override
    String toString();
}

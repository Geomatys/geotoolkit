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
package org.geotoolkit.xml;

import java.net.URI;
import java.util.UUID;
import org.opengis.metadata.citation.Citation;
import org.apache.sis.xml.XLink;
import org.geotoolkit.internal.jaxb.NonMarshalledAuthority;


/**
 * Some identifier namespaces that are handled in a special way. The identifier namespaces are
 * usually defined as authorities in the {@link org.geotoolkit.metadata.iso.citation.Citations}
 * class. However a few identifiers defined in the {@code gco:ObjectIdentification} XML attribute
 * group are handled in a special way. For example identifiers associated to the {@link #HREF}
 * space are marshalled in the outer property element, as in the example below:
 *
 * {@preformat xml
 *   <gmd:CI_Citation>
 *     <gmd:series xlink:href="http://myReference">
 *       <gmd:CI_Series>
 *         <gmd:name>...</gmd:name>
 *       </gmd:CI_Series>
 *     </gmd:series>
 *   </gmd:CI_Citation>
 * }
 *
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
 * @see IdentifierMap
 *
 * @since 3.18
 * @module
 *
 * @deprecated Moved to SIS as {@link org.apache.sis.xml.IdentifierSpace}.
 */
@Deprecated
public interface IdentifierSpace<T> extends Citation {
    /**
     * A standard GML attribute available on every object-with-identity. Its type is
     * {@code "xs:ID"} - i.e. it is a fragment identifier, unique within document scope only,
     * for internal cross-references. It is not useful by itself as a persistent unique identifier.
     * <p>
     * The XML {@linkplain #getName() attribute name} is {@code "gml:id"}.
     *
     * @see javax.xml.bind.annotation.XmlID
     */
    IdentifierSpace<String> ID = new NonMarshalledAuthority<>("gml:id", NonMarshalledAuthority.ID);

    /**
     * An optional attribute available on every object-with-identity provided in the GMD schemas
     * that implement ISO 19115 in XML. May be used as a persistent unique identifier, but only
     * available within GMD context.
     * <p>
     * The XML {@linkplain #getName() attribute name} is {@code "gco:uuid"}.
     *
     * @see UUID
     */
    IdentifierSpace<UUID> UUID = new NonMarshalledAuthority<>("gco:uuid", NonMarshalledAuthority.UUID);

    /**
     * An optional attribute for URN to an external resources, or to an other part of a XML
     * document, or an identifier. This is one of the many attributes available in the
     * {@link #XLINK} identifier space, but is provided as a special constant because
     * {@code href} is the most frequently used {@code xlink} attribute.
     * <p>
     * The XML {@linkplain #getName() attribute name} is {@code "xlink:href"}.
     *
     * @see XLink#getHRef()
     */
    IdentifierSpace<URI> HREF = new NonMarshalledAuthority<>("xlink:href", NonMarshalledAuthority.HREF);

    /**
     * Any XML attributes defined by OGC in the
     * <a href="http://schemas.opengis.net/xlink/1.0.0/xlinks.xsd">xlink</a> schema.
     * Note that the above {@link #HREF} identifier space is a special case of this
     * {@code xlink} identifier space.
     *
     * @see XLink
     *
     * @since 3.19
     */
    IdentifierSpace<XLink> XLINK = new NonMarshalledAuthority<>("xlink", NonMarshalledAuthority.XLINK);

    /**
     * Returns the name of this identifier space.
     *
     * <ul>
     *   <li><p>For the constants defined in this {@code IdentifierSpace} interface, this is
     *       the XML attribute name with its prefix. Attribute names can be {@code "gml:id"},
     *       {@code "gco:uuid"} or {@code "xlink:href"}.</p></li>
     *
     *   <li><p>For the constants defined in the {@link org.geotoolkit.metadata.iso.citation.Citations}
     *       class, this is the identifier namespace. They usually not XML attribute name because those
     *       identifiers marshalled as {@code <MD_Identifier>} XML elements rather than attributes.</p></li>
     * </ul>
     *
     * @return The name of this identifier space (may be XML attribute name).
     */
    String getName();
}

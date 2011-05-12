/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso;

import java.util.Map;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;

import org.geotoolkit.xml.XLink;
import org.geotoolkit.xml.IdentifiedObject;
import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.metadata.ModifiableMetadata;
import org.geotoolkit.metadata.UnmodifiableMetadataException;


/**
 * The base class of ISO 19115 implementation classes. Each sub-classes implements one
 * of the ISO Metadata interface provided by <A HREF="http://www.geoapi.org">GeoAPI</A>.
 * <p>
 * In addition to ISO 19115 elements, this base classes provides also some control on the
 * ISO 19139 attributes used during (un)marshalling to XML. The {@code xlink:href} attribute
 * and its friends can be accessed by the {@link XLink} object associated with this metadata.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.18
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
public class MetadataEntity extends ModifiableMetadata implements IdentifiedObject, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 5730550742604669102L;

    /**
     * The {@code xlink} attributes, or {@code null} if none.
     */
    private XLink xlink;

    /**
     * Constructs an initially empty metadata entity.
     */
    protected MetadataEntity() {
        super();
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     * The {@code source} metadata must implements the same metadata interface than this class.
     *
     * @param  source The metadata to copy values from, or {@code null} if none.
     * @throws ClassCastException if the specified metadata don't implements the expected
     *         metadata interface.
     *
     * @since 2.4
     */
    protected MetadataEntity(final Object source) throws ClassCastException {
        super(source);
    }

    /**
     * Returns the metadata standard implemented by subclasses,
     * which is {@linkplain MetadataStandard#ISO_19115 ISO 19115}.
     *
     * @since 2.4
     */
    @Override
    public MetadataStandard getStandard() {
        return MetadataStandard.ISO_19115;
    }

    /**
     * Returns all identifiers associated to this object.
     *
     * @return All identifiers associated to this object, or an empty collection if none.
     *
     * @see org.geotoolkit.metadata.iso.citation.DefaultCitation#getIdentifiers()
     * @see org.geotoolkit.metadata.acquisition.DefaultObjective#getIdentifiers()
     *
     * @since 3.18
     */
    @Override
    public Collection<? extends Identifier> getIdentifiers() {
        return Collections.emptySet();
    }

    /**
     * A map view of some or all {@linkplain #getIdentifiers() identifiers}.
     *
     * @return The identifiers as a map of (<var>authority</var>, <var>code</var>) entries,
     *         or an empty map if none.
     *
     * @since 3.18
     */
    @Override
    public Map<Citation,String> getIdentifierMap() {
        return Collections.emptyMap();
    }

    /**
     * Returns the XML {@code xlink} attributes associated to this metadata object.
     * This method returns {@code null} if there is no {@code xlink} attributes for
     * this metadata object.
     *
     * @return XML {@code xlink} attributes, or {@code null} if none.
     *
     * @since 3.18
     */
    @Override
    public synchronized XLink getXLink() {
        return xlink;
    }

    /**
     * Sets the XML {@code xlink} attributes for this metadata object. Callers should define
     * one or many {@link XLink} attributes ({@code href}, {@code role}, {@code arcrole},
     * {@code title}, {@code show} and {@code actuate}) before to invoke this method.
     *
     * @param link XML {@code xlink} attributes, or {@code null} if none.
     * @throws UnmodifiableMetadataException if this metadata is unmodifiable.
     *
     * @since 3.18
     */
    @Override
    public synchronized void setXLink(final XLink link) throws UnmodifiableMetadataException {
        checkWritePermission();
        xlink = link;
    }
}

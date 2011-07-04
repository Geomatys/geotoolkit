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

import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;

import org.geotoolkit.xml.XLink;
import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.metadata.IdentifiedMetadata;


/**
 * The base class of ISO 19115 implementation classes. Each sub-classes implements one
 * of the ISO Metadata interface provided by <A HREF="http://www.geoapi.org">GeoAPI</A>.
 * <p>
 * In addition to ISO 19115 elements, every subclasses can have implicit ISO 19139 attributes
 * used during (un)marshalling to XML. For example the {@code xlink:href} attribute can be
 * accessed by a {@link XLink} object associated with this metadata.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.19
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
public class MetadataEntity extends IdentifiedMetadata<Identifier> {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 5730550742604669102L;

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
     * Returns the kind of identifiers associated with the metadata objects,
     * which is {@code Identifier.class}.
     *
     * @since 3.19
     */
    @Override
    public final Class<Identifier> getIdentifierType() {
        return Identifier.class;
    }
}

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

import java.io.Serializable;
import java.util.Collection;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;

import org.geotoolkit.xml.XLink;
import org.geotoolkit.xml.IdentifierMap;
import org.geotoolkit.xml.IdentifierSpace;
import org.geotoolkit.xml.IdentifiedObject;
import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.metadata.ModifiableMetadata;
import org.geotoolkit.metadata.UnmodifiableMetadataException;
import org.geotoolkit.internal.jaxb.IdentifierMapAdapter;
import org.geotoolkit.internal.jaxb.IdentifierAuthority;


/**
 * The base class of ISO 19115 implementation classes. Each sub-classes implements one
 * of the ISO Metadata interface provided by <A HREF="http://www.geoapi.org">GeoAPI</A>.
 * <p>
 * In addition to ISO 19115 elements, every subclasses can have implicit ISO 19139 attributes
 * used during (un)marshaling to XML. For example the {@code xlink:href} attribute can be
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
public class MetadataEntity extends ModifiableMetadata implements IdentifiedObject, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 5730550742604669102L;

    /**
     * All identifiers associated with this metadata, or {@code null} if none.
     * This field is initialized to a non-null value when first needed.
     *
     * @see #getIdentifiers()
     *
     * @since 3.19
     */
    protected Collection<Identifier> identifiers;

    /**
     * The {@linkplain #getIdentifierMap() identifier map} as a wrapper around the
     * {@linkplain #identifiers} collection. This map is created only when first needed.
     *
     * @see #getIdentifierMap()
     *
     * @since 3.19
     */
    private transient IdentifierMap identifierMap;

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
     * {@inheritDoc}
     *
     * @since 3.19
     */
    @Override
    public synchronized Collection<Identifier> getIdentifiers() {
        return identifiers = nonNullCollection(identifiers, Identifier.class);
    }

    /**
     * Sets the identifiers associated to this object, except the {@linkplain IdentifierSpace XML
     * identifiers}. The XML identifiers ({@linkplain #ID}, {@linkplain #UUID}, <i>etc.</i>) are
     * ignored because they are associated to particular metadata instances.
     *
     * @param newValues The new identifiers, or {@code null} if none.
     *
     * @since 3.19
     */
    public synchronized void setIdentifiers(final Collection<? extends Identifier> newValues) {
        final Collection<Identifier> oldIds = IdentifierAuthority.filter(identifiers);
        identifiers = copyCollection(newValues, identifiers, Identifier.class);
        IdentifierAuthority.replace(identifiers, oldIds);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default implementation returns a wrapper around the {@linkplain #getIdentifiers()
     * identifier collection}. That map is <cite>live</cite>: changes in this metadata object
     * will be reflected in the map, and conversely.
     *
     * @since 3.19
     */
    @Override
    public synchronized IdentifierMap getIdentifierMap() {
        if (identifierMap == null) {
            final Collection<Identifier> identifiers = getIdentifiers();
            if (identifiers == null) {
                return IdentifierMapAdapter.EMPTY;
            }
            identifierMap = IdentifierMapAdapter.create(Identifier.class, identifiers);
        }
        return identifierMap;
    }

    /**
     * Returns the XML {@code xlink} attributes associated to this identified object,
     * or {@code null} if none. If non-null, the {@code xlink} attributes are marshalled
     * in the outer property element, as in the example below:
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
     * @return XML {@code xlink} attributes, or {@code null} if none.
     *
     * @deprecated Replaced by <code>getIdentifierMap().getSpecialized({@linkplain IdentifierSpace#XLINK})</code>.
     */
    @Override
    @Deprecated
    public XLink getXLink() {
        return getIdentifierMap().getSpecialized(IdentifierSpace.XLINK);
    }

    /**
     * Sets the XML {@code xlink} attributes for this metadata object. Callers should define
     * one or many {@link XLink} attributes ({@code href}, {@code role}, {@code arcrole},
     * {@code title}, {@code show} and {@code actuate}) before to invoke this method.
     *
     * @param link XML {@code xlink} attributes, or {@code null} if none.
     * @throws UnmodifiableMetadataException if this metadata is unmodifiable.
     *
     * @deprecated Replaced by <code>getIdentifierMap().putSpecialized({@linkplain IdentifierSpace#XLINK}, link)</code>.
     */
    @Override
    @Deprecated
    public void setXLink(final XLink link) throws UnmodifiableMetadataException {
        getIdentifierMap().putSpecialized(IdentifierSpace.XLINK, link);
    }
}

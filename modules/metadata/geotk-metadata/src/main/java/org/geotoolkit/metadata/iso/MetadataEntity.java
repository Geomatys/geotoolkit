/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;

import org.geotoolkit.xml.IdentifierMap;
import org.geotoolkit.xml.IdentifierSpace;
import org.geotoolkit.xml.IdentifiedObject;
import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.metadata.ModifiableMetadata;
import org.geotoolkit.internal.jaxb.MarshalContext;
import org.geotoolkit.internal.jaxb.IdentifierMapAdapter;
import org.geotoolkit.internal.jaxb.NonMarshalledAuthority;
import org.geotoolkit.internal.jaxb.gco.ObjectIdentification;
import org.geotoolkit.internal.jaxb.gco.StringConverter;


/**
 * The base class of ISO 19115 implementation classes. Each sub-classes implements one
 * of the ISO Metadata interface provided by <A HREF="http://www.geoapi.org">GeoAPI</A>.
 * <p>
 * This base class implements the {@link IdentifiedObject} interface, which implies that
 * every subclasses can be associated to one or many {@linkplain Identifier identifiers}.
 * Those identifiers fall in two categories:
 *
 * <ul>
 *   <li><p>The ISO 19115 standard associates identifiers to <strong>some</strong> metadata objects.
 *       The {@linkplain Identifier#getAuthority() authority} of those identifiers are often (but
 *       not limited to) one of the constants defined in the {@link org.geotoolkit.metadata.iso.citation.Citations}
 *       class. At XML marshalling time, those identifiers appear as {@code <MD_Identifier>} elements.</p></li>
 *   <li><p>The ISO 19139 standard associates identifiers to <strong>all</strong> metadata objects.
 *       The {@linkplain Identifier#getAuthority() authority} of those identifiers are one of the
 *       constants defined in the {@link IdentifierSpace} interface. At XML marshalling time, those
 *       identifiers appear as attributes.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package as {@code ISOMetadata}.
 */
@Deprecated
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
     * Returns an identifier unique for the XML document, or {@code null} if none.
     * This method is invoked automatically by JAXB and should never be invoked explicitely.
     *
     * @since 3.19
     */
    @XmlID
    @XmlAttribute  // Defined in "gco" as unqualified attribute.
    @XmlJavaTypeAdapter(StringConverter.class)
    private String getID() {
        return getIdentifierMap().getSpecialized(IdentifierSpace.ID);
    }

    /**
     * Sets an identifier unique for the XML document. This method is invoked
     * automatically by JAXB and should never be invoked explicitely.
     *
     * @since 3.19
     */
    private void setID(final String id) {
        getIdentifierMap().putSpecialized(IdentifierSpace.ID, id);
    }

    /**
     * Returns an unique identifier, or {@code null} if none. This method is
     * invoked automatically by JAXB and should never be invoked explicitely.
     *
     * @since 3.19
     */
    @XmlAttribute  // Defined in "gco" as unqualified attribute.
    @XmlJavaTypeAdapter(StringConverter.class)
    private String getUUID() {
        final UUID uuid = getIdentifierMap().getSpecialized(IdentifierSpace.UUID);
        return (uuid != null) ? uuid.toString() : null;
    }

    /**
     * Sets an unique identifier. This method is invoked automatically by JAXB
     * and should never be invoked explicitely.
     *
     * @throws IllegalArgumentException If the UUID is already assigned to an other object.
     *
     * @since 3.19
     */
    private void setUUID(final String id) {
        final UUID uuid = MarshalContext.converters().toUUID(id);
        if (uuid != null) {
            getIdentifierMap().putSpecialized(IdentifierSpace.UUID, uuid);
            ObjectIdentification.UUIDs.setUUID(this, uuid);
        }
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
     * Convenience method returning the first identifier which is not an ISO 19139 identifier. The
     * default implementation iterates over the {@linkplain #identifiers} collection, ignoring the
     * identifiers having one of the {@link org.geotoolkit.xml.IdentifierSpace} authority.
     *
     * @return The first ISO 19115 identifier, or {@code null} if none.
     *
     * @since 3.19
     */
    public synchronized Identifier getIdentifier() {
        return NonMarshalledAuthority.getMarshallable(identifiers);
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
}

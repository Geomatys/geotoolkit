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
package org.geotoolkit.metadata;

import java.io.Serializable;
import java.util.Collection;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;

import org.geotoolkit.xml.XLink;
import org.geotoolkit.xml.IdentifierMap;
import org.geotoolkit.xml.IdentifierSpace;
import org.geotoolkit.xml.IdentifiedObject;
import org.geotoolkit.internal.jaxb.IdentifierMapAdapter;


/**
 * Base class of all metadata objects than can be associated to one or many identifiers.
 *
 * @param <T> The type of identifiers to be associated with metadata objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
@ThreadSafe
public abstract class IdentifiedMetadata<T extends Identifier> extends ModifiableMetadata
        implements IdentifiedObject, Serializable
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -3411712925207581010L;

    /**
     * All identifiers associated with this metadata, or {@code null} if none.
     */
    protected Collection<T> identifiers;

    /**
     * The identifier map as a wrapper around the {@link #identifiers} collection.
     * Created only when first needed.
     */
    private transient IdentifierMap identifierMap;

    /**
     * Constructs an initially empty metadata entity.
     */
    protected IdentifiedMetadata() {
        super();
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     * The {@code source} metadata must implements the same metadata interface than this class.
     *
     * @param  source The metadata to copy values from, or {@code null} if none.
     * @throws ClassCastException if the specified metadata don't implements the expected
     *         metadata interface.
     */
    protected IdentifiedMetadata(final Object source) throws ClassCastException {
        super(source);
    }

    /**
     * Returns the class of identifiers.
     *
     * @return The type of identifiers to be associated with metadata objects.
     */
    public abstract Class<T> getIdentifierType();

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Collection<T> getIdentifiers() {
        return identifiers = nonNullCollection(identifiers, getIdentifierType());
    }

    /**
     * Sets all identifiers associated to this object.
     *
     * @param newValues The new identifiers.
     */
    public synchronized void setIdentifiers(final Collection<? extends T> newValues) {
        identifiers = copyCollection(newValues, identifiers, getIdentifierType());
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default implementation returns a wrapper around the {@linkplain #getIdentifiers()
     * identifiers collection}. Subclasses usually don't need to override.
     */
    @Override
    public synchronized IdentifierMap getIdentifierMap() {
        if (identifierMap == null) {
            final Collection<T> identifiers = getIdentifiers();
            if (identifiers == null) {
                return IdentifierMapAdapter.EMPTY;
            }
            identifierMap = IdentifierMapAdapter.create(getIdentifierType(), identifiers);
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
     * @deprecated Replaced by {@code getIdentifierMap().getSpecialized({@linkplain IdentifierSpace#XLINK})}.
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
     * @deprecated Replaced by {@code getIdentifierMap().putSpecialized({@linkplain IdentifierSpace#XLINK}, link)}.
     */
    @Override
    @Deprecated
    public void setXLink(final XLink link) throws UnmodifiableMetadataException {
        getIdentifierMap().putSpecialized(IdentifierSpace.XLINK, link);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void invalidate() {
        super.invalidate();
        identifierMap = null;
    }
}

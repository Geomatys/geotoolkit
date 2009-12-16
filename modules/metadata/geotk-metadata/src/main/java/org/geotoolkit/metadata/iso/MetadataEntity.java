/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.internal.jaxb.XmlUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.metadata.ModifiableMetadata;
import org.geotoolkit.metadata.InvalidMetadataException;


/**
 * A superclass for implementing ISO 19115 metadata interfaces. Subclasses
 * must implement at least one of the ISO Metadata interface provided by
 * <A HREF="http://geoapi.sourceforge.net">GeoAPI</A>.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Jody Garnett (Refractions)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
public class MetadataEntity extends ModifiableMetadata implements Serializable {
    /**
     * Serial number for interoperability with different versions.
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
     * @param  source The metadata to copy values from.
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
     * Makes sure that an argument is non-null. This is used for checking if
     * a mandatory attribute is presents.
     *
     * @param  name   Argument name.
     * @param  object User argument.
     * @throws InvalidMetadataException if {@code object} is null.
     *
     * @since 2.4
     */
    protected static void ensureNonNull(final String name, final Object object)
            throws InvalidMetadataException
    {
        if (object == null) {
            throw new InvalidMetadataException(Errors.format(Errors.Keys.NULL_ATTRIBUTE_$1, name));
        }
    }

    /**
     * If a XML marshalling is under progress and the given collection is empty, returns
     * {@code null}. Otherwise returns the collection unchanged. This method is invoked
     * by implementation having optional elements to ommit when empty.
     *
     * @param  <E> The type of elements in the given collection.
     * @param  elements The collection to return.
     * @return The given collection, or {@code null} if the collection is empty and a marshalling
     *         is under progress.
     *
     * @since 2.5
     * @level advanced
     */
    protected static <E> Collection<E> xmlOptional(final Collection<E> elements) {
        if (elements != null && elements.isEmpty() && XmlUtilities.marshalling()) {
            return null;
        }
        return elements;
    }
}

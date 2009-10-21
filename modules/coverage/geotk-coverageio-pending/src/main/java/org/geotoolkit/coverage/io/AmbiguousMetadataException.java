/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.coverage.io;

// Resources
import org.geotoolkit.image.io.text.TextMetadataParser;
import org.geotoolkit.resources.Errors;


/**
 * Thrown when one or more metadata have ambiguous values. This exception is typically
 * thrown when a metadata is defined twice with different values.  It may also be thrown
 * if a metadata can be computed from other metadata, but their values are inconsistent.
 *
 * @source $URL$
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 *
 * @module pending
 * @since 2.2
 */
public class AmbiguousMetadataException extends MetadataException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 9024148330467307209L;

    /**
     * Constructs an exception with the specified message. This exception is
     * usually raised because different values was found for the key {@code key}.
     *
     * @param message The message. If {@code null}, a message will be constructed from the alias.
     * @param key     The metadata key which was the cause for this exception, or {@code null} if
     *                none. This is a format neutral key, for example {@link MetadataBuilder#DATUM}.
     * @param alias   The alias used for for the key {@code key}, or {@code null} if none. This is
     *                usually the name used in the external file parsed.
     */
    public AmbiguousMetadataException(final String message, final TextMetadataParser.Key key,
                                      final String alias)
    {
        super((message!=null) ? message :  Errors.format(
              Errors.Keys.INCONSISTENT_PROPERTY_$1, alias), key, alias);
    }
}

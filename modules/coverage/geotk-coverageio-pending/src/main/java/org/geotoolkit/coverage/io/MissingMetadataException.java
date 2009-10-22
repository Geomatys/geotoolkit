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

import org.geotoolkit.image.io.text.TextMetadataParser;
import org.geotoolkit.resources.Errors;


/**
 * Thrown when a metadata is required but can't be found. This error typically occurs
 * when a raster is being read but the file doesn't contains enough information for
 * constructing the raster's coordinate system.
 *
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 *
 * @module pending
 * @since 2.2
 */
public class MissingMetadataException extends MetadataException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -5215286265847774754L;

    /**
     * Constructs an exception with the specified message. This exception is
     * usually raised because no value was defined for the key {@code key}.
     *
     * @param message The message. If {@code null}, a message will be constructed from the alias.
     * @param key     The metadata key which was the cause for this exception, or {@code null} if
     *                none. This is a format neutral key, for example {@link TextMetadataParser#DATUM}.
     * @param alias   The alias used for for the key {@code key}, or {@code null} if none. This is
     *                usually the name used in the external file parsed.
     */
    public MissingMetadataException(final String message, final TextMetadataParser.Key key,
                                    final String alias)
    {
        super((message!=null) ? message :  Errors.format(
                (alias!=null) ? Errors.Keys.UNDEFINED_PROPERTY_$1 :
                                Errors.Keys.UNDEFINED_PROPERTY, alias), key, alias);
    }
}

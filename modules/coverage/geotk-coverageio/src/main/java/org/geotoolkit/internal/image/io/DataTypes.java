/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.image.io;

import java.util.Locale;
import java.awt.image.DataBuffer;

import org.geotoolkit.lang.Static;


/**
 * Utilities methods related to data types.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 * @module
 */
@Static
public final class DataTypes {
    /**
     * Do not allow instantiation of this class.
     */
    private DataTypes() {
    }

    /**
     * Decodes the given name as a {@link DataBuffer} constant. If the name
     * is not recognized, then {@link DataBuffer#TYPE_UNDEFINED} is returned.
     *
     * @param name The name ({@code "BYTE"}, {@code "SHORT"}, {@code "FLOAT"}, <i>etc.</i>)
     * @return The corresponding {@link DataBuffer} constant.
     *
     * @todo Use switch on String.
     */
    public static int decode(String name) {
        name = name.toUpperCase(Locale.US);
        if (name.equals("BYTE" ))  return DataBuffer.TYPE_BYTE;
        if (name.equals("SHORT"))  return DataBuffer.TYPE_SHORT;
        if (name.equals("USHORT")) return DataBuffer.TYPE_USHORT;
        if (name.equals("INT"))    return DataBuffer.TYPE_INT;
        if (name.equals("FLOAT"))  return DataBuffer.TYPE_FLOAT;
        if (name.equals("DOUBLE")) return DataBuffer.TYPE_DOUBLE;
        return DataBuffer.TYPE_UNDEFINED;
    }
}

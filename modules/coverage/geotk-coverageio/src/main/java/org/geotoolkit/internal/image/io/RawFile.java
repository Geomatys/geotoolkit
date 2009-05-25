/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import java.io.File;
import java.io.Serializable;
import javax.imageio.ImageTypeSpecifier;
import org.geotoolkit.util.Utilities;


/**
 * An entry for a temporary RAW file associated with its color and sample model.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class RawFile implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 694564932577879529L;

    /**
     * The temporary file.
     */
    public final File file;

    /**
     * The color and sample model of the RAW image.
     */
    public final ImageTypeSpecifier type;

    /**
     * Creates a new entry for the given temporary file.
     *
     * @param file The temporary file.
     * @param type The color and sample model of the RAW image.
     */
    public RawFile(final File file, final ImageTypeSpecifier type) {
        this.file = file;
        this.type = type;
    }

    /**
     * Compares this {@code RawFile} with the given object for equality.
     *
     * @param  object The object to compare with {@code this}.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof RawFile) {
            final RawFile that = (RawFile) object;
            return Utilities.equals(this.file, that.file) &&
                   Utilities.equals(this.type, that.type);
        }
        return false;
    }

    /**
     * Returns a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Utilities.hash(type, file.hashCode());
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        return "RawFile[" + file + "\"]";
    }
}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.image.io;

import java.util.Objects;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import com.sun.media.imageio.stream.RawImageInputStream;

import java.awt.Dimension;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.image.io.stream.FileImageInputStream;


/**
 * An entry for a temporary RAW file associated with its color and sample model.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
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
    private final ImageTypeSpecifier type;

    /**
     * The image width and height. We store the size as a {@link Dimension} object
     * instead of {@code int} fields because the we will typically share a large
     * amount of references to the same dimension in various {@code RawSize} instances.
     */
    private final Dimension size;

    /**
     * Creates a new entry for the given temporary file. This constructor stores direct
     * references to the given arguments; they are not cloned. Consequently they should
     * not be changed after construction.
     *
     * @param file   The temporary file.
     * @param type   The color and sample model of the RAW image.
     * @param size   The image width and height, in pixels.
     */
    public RawFile(final File file, final ImageTypeSpecifier type, final Dimension size) {
        this.file = file;
        this.type = type;
        this.size = size;
    }

    /**
     * Returns the input stream to use for reading the RAW image represented by this object.
     *
     * @return The input stream.
     * @throws IOException If an error occurred while creating the input stream.
     *
     * @since 3.01
     */
    public ImageInputStream getImageInputStream() throws IOException {
        ImageInputStream in;
        in = new FileImageInputStream(file);
        in = new RawImageInputStream(in, type, new long[1], new Dimension[] {size});
        return in;
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
            return Objects.equals(this.file, that.file) &&
                   Objects.equals(this.type, that.type) &&
                   Objects.equals(this.size, that.size);
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

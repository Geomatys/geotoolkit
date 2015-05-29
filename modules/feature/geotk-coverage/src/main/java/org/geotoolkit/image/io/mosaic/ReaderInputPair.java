/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.mosaic;

import java.util.Objects;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageReaderSpi;

import org.apache.sis.util.Utilities;


/**
 * A pair of {@link ImageReader} with its input. Only used as keys in hash map.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
class ReaderInputPair {
    /**
     * The image reader, or the provider if none.
     */
    private final Object reader;

    /**
     * The input to be given to the image reader.
     */
    private final Object input;

    /**
     * Hash code computed at construction time in order to avoid computing it twice.
     */
    private final int hash;

    /**
     * Creates a provider/input pair.
     */
    ReaderInputPair(final ImageReaderSpi provider, final Object input) {
        this.reader = provider;
        this.input  = input;
        this.hash   = hash();
    }

    /**
     * Creates a reader/input pair.
     */
    ReaderInputPair(final ImageReader reader, final Object input) {
        this.reader = reader;
        this.input  = input;
        this.hash   = hash();
    }
    /**
     * Returns a hash value for this reader/input pair.
     */
    @Override
    public final int hashCode() {
        return hash;
    }

    /**
     * Computes the hash value for this reader/input pair.
     */
    private int hash() {
        return reader.hashCode() + 31*Utilities.deepHashCode(input);
    }

    /**
     * Compares this reader/input pair with the specified object for equality.
     */
    @Override
    public final boolean equals(final Object object) {
        if (object instanceof ReaderInputPair) {
            final ReaderInputPair that = (ReaderInputPair) object;
            return Objects.equals(this.reader, that.reader) &&
                   Objects.deepEquals(this.input, that.input);
        }
        return false;
    }

    /**
     * A {@link ReaderInputPair} associated with an {@link ImageWriter} to be set by the caller.
     * This is for internal use by {@link MosaicImageWriter} only. The writer is not used in hash
     * code value or comparisons.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.04
     *
     * @since 3.04
     * @module
     */
    static final class WithWriter extends ReaderInputPair {
        /**
         * The image writer. This is initially null and must be set by the caller.
         */
        ImageWriter writer;

        /**
         * {@code true} if the output given to the writer needs to be an output stream.
         * This is initially {@code false} and must be set by the caller if needed.
         */
        boolean needStream;

        /**
         * Creates a provider/input pair. The writer must be set by the caller after construction.
         */
        WithWriter(final ImageReaderSpi provider, final Class<?> inputType) {
            super(provider, inputType);
        }
    }
}

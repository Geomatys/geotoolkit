/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io;

import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.stream.ImageOutputStream;


/**
 * Wraps an {@link ImageOutputStream} into a standard {@link OutputStream}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
final class OutputStreamAdapter extends OutputStream {
    /**
     * The wrapped image output stream.
     */
    private final ImageOutputStream output;

    /**
     * Construct a new output stream.
     */
    public OutputStreamAdapter(final ImageOutputStream output) {
        this.output = output;
    }

    /**
     * Writes the specified byte to this output stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(final int b) throws IOException {
        output.write(b);
    }

    /**
     * Writes {@code b.length} bytes from the specified byte array.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(final byte[] b) throws IOException {
        output.write(b);
    }

    /**
     * Writes {@code len} bytes from the specified byte array.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        output.write(b, off, len);
    }

    /**
     * Forces any buffered output bytes to be written out.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void flush() throws IOException {
        output.flush();
    }

    /**
     * Closes this output stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        output.close();
    }
}

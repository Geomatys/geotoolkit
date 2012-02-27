/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.test.stress;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * A {@link ByteArrayOutputStream} extended only in order to get access to the underlying
 * buffer.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 */
final class MemoryOutputStream extends ByteArrayOutputStream {
    /**
     * Creates a new, initially empty, output stream.
     */
    public MemoryOutputStream() {
    }

    /**
     * Returns an input stream for the underlying buffer. No write operation shall
     * occur on this output stream while the given input stream is in use.
     */
    public synchronized InputStream getInputStream() {
        return new ByteArrayInputStream(buf, 0, count);
    }
}

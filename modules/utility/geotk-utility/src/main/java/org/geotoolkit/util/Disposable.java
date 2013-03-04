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
package org.geotoolkit.util;


/**
 * Interface of classes providing a {@link #dispose()} method.
 * This interface is similar to the standard {@link java.io.Closeable} interface, but serve
 * a slightly different purpose. The {@code Closeable} interface is for closing a source or
 * destination of data, while the {@code Disposable} interface is used for objects than can
 * be reused for different source or destination of data.
 * <p>
 * <b>Example:</b> An {@link javax.imageio.ImageReader} can be instantiated once, then reused
 * for reading many images of the same format. An {@code ImageReader} implementation could
 * provide both the {@code close()} and {@code dispose()} methods:
 * <p>
 * <ul>
 *   <li>The {@code close()} method for closing the stream of the current image being read,
 *       while keeping the {@code ImageReader} instance ready to accept new input streams for
 *       other images to read.</li>
 *   <li>The {@link javax.imageio.ImageReader#dispose() dispose()} method indicating that the
 *       {@code ImageReader} will not be used anymore and can release all internal resources.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 *
 * @see java.io.Closeable
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.util.Disposable}.
 */
@Deprecated
public interface Disposable extends org.apache.sis.util.Disposable {
    /**
     * Allows any resources held by this object to be released.
     */
    @Override
    void dispose();
}

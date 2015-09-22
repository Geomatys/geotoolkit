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
package org.geotoolkit.internal.image.io;

import java.util.Arrays;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

import org.geotoolkit.lang.Debug;
import org.geotoolkit.lang.Decorator;
import org.apache.sis.util.logging.Logging;


/**
 * An image input stream which checks if the stream has been closed. The check is performed
 * at garbage-collection time. This class is used only for debugging purpose, as a way to
 * trace the place where the input stream may not have been properly closed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 * @module
 */
@Debug
@Decorator(ImageInputStream.class)
public final class CheckedImageInputStream extends ImageInputStreamProxy {
    /**
     * {@code true} if {@link #close()} has been invoked.
     */
    private volatile boolean isClosed;

    /**
     * The creator of this input stream, used in order to format a log message if needed.
     */
    private final StackTraceElement[] creator;

    /**
     * Creates a new instance wrapping the given stream.
     *
     * @param in The image input stream to wrap.
     */
    private CheckedImageInputStream(final ImageInputStream in) {
        super(in);
        StackTraceElement[] creator = Thread.currentThread().getStackTrace();
        for (int i=0; i<creator.length; i++) {
            final String c = creator[i].getClassName();
            if (!c.equals("java.lang.Thread") && !c.equals("org.geotoolkit.internal.image.io.CheckedImageInputStream")) {
                creator = Arrays.copyOfRange(creator, i, creator.length);
                break;
            }
        }
        this.creator = creator;
    }

    /**
     * Creates a new instance wrapping the given stream, or returns {@code null}
     * if the given instance was null.
     *
     * @param  in The image input stream to wrap, or {@code null}.
     * @return The wrapped image input stream, or {@code null}.
     */
    public static ImageInputStream wrap(ImageInputStream in) {
        if (in != null && !(in instanceof CheckedImageInputStream)) {
            in = new CheckedImageInputStream(in);
        }
        return in;
    }

    /**
     * Returns {@code true} if the given input stream is null, or is a valid
     * {@link CheckedImageInputStream}. This method is used only for assertions purpose.
     *
     * @param  in The input stream to check.
     * @return {@code true} if the given input stream is valid.
     */
    public static boolean isValid(final ImageInputStream in) {
        return (in == null) || (in instanceof CheckedImageInputStream &&
                !((CheckedImageInputStream) in).isClosed);
    }

    /**
     * Closes the stream.
     */
    @Override
    public void close() throws IOException {
        isClosed = true;
        super.close();
    }

    /**
     * Checks if the input stream has been closed.
     */
    @Override
    protected void finalize() {
        if (!isClosed) {
            final StringBuilder buffer = new StringBuilder("ImageInputStream created below has not been closed:\n");
            for (final StackTraceElement element : creator) {
                buffer.append("  ").append(element).append('\n');
            }
            Logging.getLogger("org.geotoolkit.image.io").warning(buffer.toString());
        }
    }
}

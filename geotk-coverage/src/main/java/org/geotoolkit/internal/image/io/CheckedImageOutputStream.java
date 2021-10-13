/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.lang.Debug;
import org.geotoolkit.lang.Decorator;

import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.util.Arrays;


/**
 * An image output stream which checks if the stream has been closed. The check is performed
 * at garbage-collection time. This class is used only for debugging purpose, as a way to
 * trace the place where the output stream may not have been properly closed.
 *
 * @author Quentin Boileau (Geomatys)
 */
@Debug
@Decorator(ImageOutputStream.class)
public final class CheckedImageOutputStream extends ImageOutputStreamProxy {
    /**
     * {@code true} if {@link #close()} has been invoked.
     */
    private volatile boolean isClosed;

    /**
     * The creator of this output stream, used in order to format a log message if needed.
     */
    private final StackTraceElement[] creator;

    /**
     * Creates a new instance wrapping the given stream.
     *
     * @param out The image output stream to wrap.
     */
    private CheckedImageOutputStream(final ImageOutputStream out) {
        super(out);
        StackTraceElement[] creator = Thread.currentThread().getStackTrace();
        for (int i=0; i<creator.length; i++) {
            final String c = creator[i].getClassName();
            if (!c.equals("java.lang.Thread") && !c.equals("org.geotoolkit.internal.image.io.CheckedImageOutputStream")) {
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
     * @param  out The image output stream to wrap, or {@code null}.
     * @return The wrapped image output stream, or {@code null}.
     */
    public static ImageOutputStream wrap(ImageOutputStream out) {
        if (out != null && !(out instanceof CheckedImageOutputStream)) {
            out = new CheckedImageOutputStream(out);
        }
        return out;
    }

    /**
     * Returns {@code true} if the given output stream is null, or is a valid
     * {@link CheckedImageOutputStream}. This method is used only for assertions purpose.
     *
     * @param  out The output stream to check.
     * @return {@code true} if the given output stream is valid.
     */
    public static boolean isValid(final ImageOutputStream out) {
        return (out == null) || (out instanceof CheckedImageOutputStream &&
                !((CheckedImageOutputStream) out).isClosed);
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
     * Checks if the output stream has been closed.
     */
    @Override
    protected void finalize() {
        if (!isClosed) {
            final StringBuilder buffer = new StringBuilder("ImageOutputStream created below has not been closed:\n");
            for (final StackTraceElement element : creator) {
                buffer.append("  ").append(element).append('\n');
            }
            Logging.getLogger("org.geotoolkit.image.io").warning(buffer.toString());
        }
    }
}

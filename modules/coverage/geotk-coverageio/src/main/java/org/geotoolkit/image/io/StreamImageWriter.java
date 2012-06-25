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

import java.io.*; // Many imports, including some for javadoc only.
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import org.geotoolkit.resources.Errors;


/**
 * Base class for image writers that expect an {@link OutputStream} or channel output. This class
 * provides a {@link #getOutputStream()} method, which return the {@linkplain #output output} as
 * an {@link OutputStream} for convenience.
 * <p>
 * Different kinds of outputs are automatically handled.
 * The default implementation handles all the following types:
 *
 * <blockquote>
 * {@link String}, {@link File}, {@link URI}, {@link URL}, {@link URLConnection},
 * {@link OutputStream}, {@link ImageOutputStream}, {@link WritableByteChannel}.
 * </blockquote>
 *
 * Note the {@link TextImageWriter} subclass can go one step further by wrapping the
 * {@code InputStream} into a {@link java.io.BufferedWriter} using some character encoding.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see StreamImageReader
 *
 * @since 2.4
 * @module
 */
public abstract class StreamImageWriter extends SpatialImageWriter {
    /**
     * The stream to {@linkplain #close() close} on {@link #setOutput(.Object)}, {@link #reset()}
     * or {@link #dispose()} method invocation. This stream is typically an
     * {@linkplain OutputStream output stream} or a {@linkplain Writer writer}
     * created by {@link #getOutputStream()} or similar methods in subclasses.
     * <p>
     * This field is never equal to the user-specified {@linkplain #output output}, since the
     * usual {@link javax.imageio.ImageWriter} contract is to <strong>not</strong> close the
     * user-provided stream. It is set to a non-null value only if a stream has been created
     * from an other user object like {@link File} or {@link URL}.
     *
     * @see #getOutputStream()
     * @see TextImageWriter#getWriter(ImageWriteParam)
     * @see #close()
     */
    protected Closeable closeOnReset;

    /**
     * {@link #output} as an output stream, or {@code null} if none.
     *
     * @see #getOutputStream()
     */
    private OutputStream stream;

    /**
     * Constructs a new image writer.
     *
     * @param provider The {@link ImageWriterSpi} that is constructing this object, or {@code null}.
     */
    protected StreamImageWriter(final Spi provider) {
        super(provider);
    }

    /**
     * Sets the output source to use. Output can be any of the object documented in the
     * <a href="#overview">class javadoc</a>, namely {@link String}, {@link File},
     * {@link URI}, {@link URL}, {@link URLConnection}, {@link OutputStream}, {@link ImageOutputStream}
     * or {@link WritableByteChannel}.
     * <p>
     * If the given {@code output} is {@code null}, then any currently set output source will be
     * removed.
     *
     * @param output The output object to use for future writing.
     *
     * @see #getOutput
     * @see #getOutputStream
     */
    @Override
    public void setOutput(final Object output) {
        super.setOutput(output);
    }

    /**
     * Returns the {@linkplain #output output} as an {@linkplain OutputStream output stream} object.
     * If the output is already an output stream, it is returned unchanged. Otherwise this method
     * creates a new {@linkplain OutputStream output stream} (usually <strong>not</strong>
     * {@linkplain BufferedOutputStream buffered}) from {@link File}, {@link URI},
     * {@link URL}, {@link URLConnection}, {@link ImageOutputStream} or {@link WritableByteChannel}
     * outputs.
     * <p>
     * This method creates a new {@linkplain OutputStream output stream} only when first invoked.
     * All subsequent calls will returns the same instance. Consequently, the returned stream
     * should never be closed by the caller. It may be {@linkplain #close closed} automatically
     * when {@link #setOutput}, {@link #reset()} or {@link #dispose()} methods are invoked.
     *
     * @return {@link #getOutput} as an {@link OutputStream}. This output stream is usually
     *         not {@linkplain BufferedOutputStream buffered}.
     * @throws IllegalStateException if the {@linkplain #output output} is not set.
     * @throws IOException If the output stream can't be created for an other reason.
     *
     * @see #getOutput
     * @see TextImageWriter#getWriter(ImageWriteParam)
     */
    protected OutputStream getOutputStream() throws IllegalStateException, IOException {
        if (stream == null) {
            final Object output = getOutput();
            if (output == null) {
                throw new IllegalStateException(getErrorResources().getString(Errors.Keys.NO_IMAGE_OUTPUT));
            }
            if (output instanceof OutputStream) {
                stream = (OutputStream) output;
                closeOnReset = null; // We don't own the stream, so don't close it.
            } else if (output instanceof ImageOutputStream) {
                stream = new OutputStreamAdapter((ImageOutputStream) output);
                closeOnReset = null; // We don't own the ImageOutputStream, so don't close it.
            } else if (output instanceof String) {
                stream = new FileOutputStream((String) output);
                closeOnReset = stream;
            } else if (output instanceof File) {
                stream = new FileOutputStream((File) output);
                closeOnReset = stream;
            } else if (output instanceof URI) {
                stream = ((URI) output).toURL().openConnection().getOutputStream();
                closeOnReset = stream;
            } else if (output instanceof URL) {
                stream = ((URL) output).openConnection().getOutputStream();
                closeOnReset = stream;
            } else if (output instanceof URLConnection) {
                stream = ((URLConnection) output).getOutputStream();
                closeOnReset = stream;
            } else if (output instanceof WritableByteChannel) {
                stream = Channels.newOutputStream((WritableByteChannel) output);
                // Do not define closeOnReset since we don't want to close user-provided output.
            } else {
                throw new IllegalStateException(getErrorResources().getString(
                        Errors.Keys.ILLEGAL_CLASS_$2, output.getClass(), OutputStream.class));
            }
        }
        return stream;
    }

    /**
     * Closes the output stream created by {@link #getOutputStream()}. This method does nothing
     * if the output stream is the {@linkplain #output output} instance given by the user rather
     * than a stream created by this class from a {@link File} or {@link URL} output.
     * <p>
     * This method is invoked automatically by {@link #setOutput(Object)}, {@link #reset()},
     * {@link #dispose()} or {@link #finalize()} methods and doesn't need to be invoked explicitly.
     * It has protected access only in order to allow overriding by subclasses.
     *
     * @throws IOException if an error occurred while closing the stream.
     *
     * @see #closeOnReset
     */
    @Override
    protected void close() throws IOException {
        if (closeOnReset != null) {
            closeOnReset.close();
        }
        closeOnReset = null;
        stream       = null;
        super.close();
    }

    /**
     * Invokes the {@link #close()} method when this writer is garbage-collected.
     * Note that this will actually close the stream only if it has been created
     * by this writer, rather than supplied by the user.
     */
    @Override
    protected void finalize() throws Throwable {
        closeSilently();
        super.finalize();
    }




    /**
     * Service provider interface (SPI) for {@link StreamImageWriter}s. The constructor of
     * this class initializes the {@link #outputTypes} field to the value documented in the
     * {@link StreamImageWriter} javadoc, which are:
     * <p>
     * <table border="1">
     *   <tr bgcolor="lightblue">
     *     <th>Field</th>
     *     <th>Value</th>
     *   </tr><tr>
     *     <td>&nbsp;{@link #outputTypes}&nbsp;</td>
     *     <td>&nbsp;{@link String}, {@link File}, {@link URI}, {@link URL},
     *               {@link URLConnection}, {@link OutputStream}, {@link ImageOutputStream},
     *               {@link WritableByteChannel}&nbsp;</td>
     *   </tr><tr>
     *     <td colspan="2" align="center">See {@linkplain SpatialImageWriter.Spi super-class javadoc}
     *     for remaining fields</td>
     * </tr>
     * </table>
     * <p>
     *
     * It is up to subclass constructors to initialize all other instance variables
     * in order to provide working versions of every methods.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.20
     *
     * @see StreamImageReader.Spi
     *
     * @since 2.4
     * @module
     */
    protected abstract static class Spi extends SpatialImageWriter.Spi {
        /**
         * List of legal output types for {@link StreamImageWriter}.
         */
        private static final Class<?>[] OUTPUT_TYPES = new Class<?>[] {
            File.class,
            URL.class,
            URLConnection.class,
            OutputStream.class,
            ImageOutputStream.class,
            String.class // To be interpreted as file path.
        };

        /**
         * Constructs a quasi-blank {@code StreamImageWriter.Spi}. The {@link #outputTypes} field
         * is initialized as documented in the <a href="#skip-navbar_top">class javadoc</a>. It is
         * up to the subclass to initialize all other instance variables in order to provide working
         * versions of all methods.
         * <p>
         * For efficiency reasons, the above fields are initialized to shared arrays. Subclasses
         * can assign new arrays, but should not modify the default array content.
         */
        protected Spi() {
            outputTypes = OUTPUT_TYPES;
        }

        /**
         * Returns {@code true} if the image writer implementation associated with this service
         * provider is able to encode an image with the given layout. The default implementation
         * returns always {@code true}, which is accurate if the writer will fetch pixel values
         * with the help of an {@linkplain SpatialImageWriter#createRectIter iterator}.
         */
        @Override
        public boolean canEncodeImage(final ImageTypeSpecifier type) {
            return true;
        }
    }
}

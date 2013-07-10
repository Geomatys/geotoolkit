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
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.logging.Logging;


/**
 * Base class for image readers that expect an {@link InputStream} or channel input source. This
 * class provides a {@link #getInputStream()} method, which return the {@linkplain #input input}
 * as an {@link InputStream} for convenience.
 * <p>
 * Different kinds of outputs are automatically handled.
 * The default implementation handles all the following types:
 *
 * <blockquote>
 * {@link String}, {@link Path}, {@link File}, {@link URI}, {@link URL}, {@link URLConnection},
 * {@link InputStream}, {@link ImageInputStream}, {@link ReadableByteChannel}.
 * </blockquote>
 *
 * Note the {@link TextImageReader} subclass can go one step further by wrapping the
 * {@code InputStream} into a {@link java.io.BufferedReader} using some character encoding.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see StreamImageWriter
 *
 * @since 2.4
 * @module
 */
public abstract class StreamImageReader extends SpatialImageReader {
    /**
     * The stream to {@linkplain #close() close} on {@link #setInput(Object,boolean,boolean)
     * setInput(...)}, {@link #reset()} or {@link #dispose()} method invocation. This stream
     * is typically an {@linkplain InputStream input stream} or a {@linkplain Reader reader}
     * created by {@link #getInputStream()} or similar methods in subclasses.
     * <p>
     * This field is never equal to the user-specified {@linkplain #input input}, since the
     * usual {@link javax.imageio.ImageReader} contract is to <strong>not</strong> close the
     * user-provided stream. It is set to a non-null value only if a stream has been created
     * from an other user object like {@link File} or {@link URL}.
     *
     * @see #getInputStream()
     * @see TextImageReader#getReader()
     * @see #close()
     */
    protected Closeable closeOnReset;

    /**
     * The channel returned by {@link #getChannel()}, or {@code null} if none.
     */
    private ReadableByteChannel channel;

    /**
     * {@link #input} as an input stream, or {@code null} if none.
     *
     * @see #getInputStream()
     */
    private InputStream stream;

    /**
     * The stream position when {@link #setInput(Object, boolean, boolean)} has been invoked.
     */
    private long streamOrigin;

    /**
     * Constructs a new image reader.
     *
     * @param provider The {@link ImageReaderSpi} that is invoking this constructor,
     *        or {@code null} if none.
     */
    protected StreamImageReader(final Spi provider) {
        super(provider);
    }

    /**
     * Sets the input source to use. Input may be one of the object documented in the
     * <a href="#overview">class javadoc</a>, namely {@link String}, {@link Path}, {@link File},
     * {@link URI}, {@link URL}, {@link URLConnection}, {@link InputStream}, {@link ImageInputStream}
     * or {@link ReadableByteChannel}.
     * <p>
     * If the given {@code input} is {@code null}, then any currently set input source will be
     * removed.
     *
     * @param input           The input object to use for future decoding.
     * @param seekForwardOnly If {@code true}, images and metadata may only be read
     *                        in ascending order from this input source.
     * @param ignoreMetadata  If {@code true}, metadata may be ignored during reads.
     *
     * @see #getInput
     * @see #getInputStream
     */
    @Override
    public void setInput(final Object  input,
                         final boolean seekForwardOnly,
                         final boolean ignoreMetadata)
    {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        if (input instanceof ImageInputStream) {
            try {
                streamOrigin = ((ImageInputStream) input).getStreamPosition();
            } catch (IOException exception) {
                Logging.unexpectedException(LOGGER, StreamImageReader.class, "setInput", exception);
            }
        }
    }

    /**
     * Returns the stream length in bytes, or {@code -1} if unknown. This method checks the
     * {@linkplain #input input} type and invokes one of {@link File#length()},
     * {@link ImageInputStream#length()}, {@link URLConnection#getContentLength()} or
     * {@link Files#size(Path)} method accordingly.
     *
     * @return The stream length, or -1 is unknown.
     * @throws IOException if an I/O error occurred.
     */
    protected long getStreamLength() throws IOException {
        final Object input = getInput();
        if (input instanceof ImageInputStream) {
            long length = ((ImageInputStream) input).length();
            if (length >= 0) {
                length -= streamOrigin;
            }
            return length;
        }
        if (input instanceof Path) {
            return Files.size((Path) input);
        }
        if (input instanceof File) {
            return ((File) input).length();
        }
        if (input instanceof URL) {
            return ((URL) input).openConnection().getContentLength();
        }
        if (input instanceof URLConnection) {
            return ((URLConnection) input).getContentLength();
        }
        return -1;
    }

    /**
     * Returns the {@linkplain #input input} as an {@linkplain InputStream input stream} object.
     * If the input is already an input stream, it is returned unchanged. Otherwise this method
     * creates a new {@linkplain InputStream input stream} (usually <strong>not</strong>
     * {@linkplain BufferedInputStream buffered}) from {@link File}, {@link Path}, {@link URI},
     * {@link URL}, {@link URLConnection}, {@link ImageInputStream} or {@link ReadableByteChannel}
     * inputs.
     * <p>
     * This method creates a new {@linkplain InputStream input stream} only when first invoked.
     * All subsequent calls will return the same instance. Consequently, the returned stream
     * should never be closed by the caller. It will be {@linkplain #close() closed} automatically
     * (if the stream was not given directly by the user) when {@link #setInput setInput(...)},
     * {@link #reset() reset()} or {@link #dispose() dispose()} methods are invoked.
     *
     * @return {@link #getInput()} as an {@link InputStream}. This input stream is usually
     *         not {@linkplain BufferedInputStream buffered}.
     * @throws IllegalStateException if the {@linkplain #input input} is not set.
     * @throws IOException If the input stream can't be created for an other reason.
     *
     * @see #getInput()
     * @see #getChannel()
     * @see TextImageReader#getReader()
     */
    protected InputStream getInputStream() throws IllegalStateException, IOException {
        if (stream == null) {
            final Object input = getInput();
            if (input == null) {
                throw new IllegalStateException(getErrorResources().getString(Errors.Keys.NO_IMAGE_INPUT));
            }
            if (input instanceof InputStream) {
                stream = (InputStream) input;
                closeOnReset = null; // We don't own the stream, so don't close it.
            } else if (input instanceof ImageInputStream) {
                stream = new InputStreamAdapter((ImageInputStream) input);
                closeOnReset = null; // We don't own the ImageInputStream, so don't close it.
            } else if (input instanceof String) {
                stream = new FileInputStream((String) input);
                closeOnReset = stream;
            } else if (input instanceof File) {
                stream = new FileInputStream((File) input);
                closeOnReset = stream;
            } else if (input instanceof Path) {
                stream = Files.newInputStream((Path) input);
                closeOnReset = stream;
            } else if (input instanceof URI) {
                stream = ((URI) input).toURL().openStream();
                closeOnReset = stream;
            } else if (input instanceof URL) {
                stream = ((URL) input).openStream();
                closeOnReset = stream;
            } else if (input instanceof URLConnection) {
                stream = ((URLConnection) input).getInputStream();
                closeOnReset = stream;
            } else if (input instanceof ReadableByteChannel) {
                stream = Channels.newInputStream((ReadableByteChannel) input);
                // Do not define closeOnReset since we don't want to close user-provided input.
            } else {
                throw new IllegalStateException(getErrorResources().getString(
                        Errors.Keys.ILLEGAL_CLASS_2, input.getClass(), InputStream.class));
            }
        }
        return stream;
    }

    /**
     * Returns the {@linkplain #input input} as an {@linkplain ReadableByteChannel readable byte
     * channel}. If the input is already such channel, it is returned unchanged. Otherwise this
     * method creates a new channel from the {@link Path} input or the value returned by
     * {@link #getInputStream()}.
     * <p>
     * This method creates a new channel only when first invoked. All subsequent calls will return
     * the same instance. Consequently, the returned channel should never be closed by the caller.
     * It will be {@linkplain #close() closed} automatically (if the channel was not given directly
     * by the user) when {@link #setInput setInput(...)}, {@link #reset() reset()} or
     * {@link #dispose() dispose()} methods are invoked.
     *
     * @return {@link #getInput()} as a {@link ReadableByteChannel}.
     * @throws IllegalStateException if the {@linkplain #input input} is not set.
     * @throws IOException If the input stream can't be created for an other reason.
     *
     * @see #getInput()
     * @see #getInputStream()
     *
     * @since 3.07
     */
    protected ReadableByteChannel getChannel() throws IllegalStateException, IOException {
        if (channel == null) {
            final Object input = getInput();
            if (input instanceof ReadableByteChannel) {
                channel = (ReadableByteChannel) input;
            } else if (input instanceof Path) {
                channel = Files.newByteChannel((Path) input);
            } else {
                final InputStream stream = getInputStream();
                if (stream instanceof FileInputStream) {
                    channel = ((FileInputStream) stream).getChannel();
                } else {
                    channel = Channels.newChannel(stream);
                }
            }
        }
        return channel;
    }

    /**
     * Closes the input stream created by {@link #getInputStream()}. This method does nothing
     * if the input stream is the {@linkplain #input input} instance given by the user rather
     * than a stream created by this class from a {@link File} or {@link URL} input.
     * <p>
     * This method is invoked automatically by {@link #setInput(Object, boolean, boolean)
     * setInput(...)}, {@link #reset() reset()}, {@link #dispose() dispose()} or
     * {@link #finalize()} methods and doesn't need to be invoked explicitly.
     * It has protected access only in order to allow overriding by subclasses.
     * Overriding methods shall make sure that {@code super.close()} is invoked
     * even if their own code fail.
     *
     * @throws IOException if an error occurred while closing the stream.
     *
     * @see #closeOnReset
     */
    @Override
    protected void close() throws IOException {
        super.close(); // Should be first.
        if (closeOnReset != null) {
            // Close the channel only if it was not supplied explicitly by the user.
            if (channel != null) {
                channel.close();
            }
            closeOnReset.close();
            closeOnReset = null;
        }
        channel = null;
        stream  = null;
    }

    /**
     * Invokes the {@link #close()} method when this reader is garbage-collected.
     * Note that this will actually close the stream only if it has been created
     * by this reader, rather than supplied by the user.
     */
    @Override
    protected void finalize() throws Throwable {
        closeSilently();
        super.finalize();
    }




    /**
     * Service provider interface (SPI) for {@link StreamImageReader}s. The constructor of
     * this class initializes the {@link #inputTypes} field to the value documented in the
     * {@link StreamImageReader} javadoc, which are:
     * <p>
     * <table border="1">
     *   <tr bgcolor="lightblue">
     *     <th>Field</th>
     *     <th>Value</th>
     *   </tr><tr>
     *     <td>&nbsp;{@link #inputTypes}&nbsp;</td>
     *     <td>&nbsp;{@link String}, {@link Path}, {@link File}, {@link URI}, {@link URL},
     *               {@link URLConnection}, {@link InputStream}, {@link ImageInputStream},
     *               {@link ReadableByteChannel}&nbsp;</td>
     *   </tr><tr>
     *     <td colspan="2" align="center">See {@linkplain SpatialImageReader.Spi super-class javadoc}
     *     for remaining fields</td>
     * </tr>
     * </table>
     * <p>
     * It is up to subclass constructors to initialize all other instance variables
     * in order to provide working versions of every methods.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.20
     *
     * @see StreamImageWriter.Spi
     *
     * @since 2.4
     * @module
     */
    protected abstract static class Spi extends SpatialImageReader.Spi {
        /**
         * List of legal input types for {@link StreamImageReader}.
         */
        private static final Class<?>[] INPUT_TYPES = new Class<?>[] {
            File.class,
            Path.class,
            URI.class,
            URL.class,
            URLConnection.class,
            InputStream.class,
            ImageInputStream.class,
            ReadableByteChannel.class,
            String.class // To be interpreted as file path.
        };

        /**
         * Constructs a quasi-blank {@code StreamImageReader.Spi}. The {@link #inputTypes} field
         * is initialized as documented in the <a href="#skip-navbar_top">class javadoc</a>. It is
         * up to the subclass to initialize all other instance variables in order to provide working
         * versions of all methods.
         * <p>
         * For efficiency reasons, the {@code inputTypes} field is initialized to a shared array.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        protected Spi() {
            inputTypes = INPUT_TYPES;
        }
    }
}

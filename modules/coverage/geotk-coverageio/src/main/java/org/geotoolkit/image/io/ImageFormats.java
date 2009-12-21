/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.Closeable;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.IIOException;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.RenderedImage;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.internal.io.IOUtilities;


/**
 * A set of static methods for fetching an {@link ImageReader} or an {@link ImageWriter} for
 * a given input or output. The methods provided in this class serve a similar purpose than
 * the following methods provided in the standard JDK:
 * <p>
 * <ul>
 *   <li>{@link ImageIO#getImageReaders(Object)}</li>
 *   <li>{@link ImageIO#getImageWriters(Object)}</li>
 *   <li>{@link ImageIO#getImageReadersBySuffix(String)}</li>
 *   <li>{@link ImageIO#getImageWritersBySuffix(String)}</li>
 *   <li>{@link ImageIO#getImageReadersByFormatName(String)}</li>
 *   <li>{@link ImageIO#getImageWritersByFormatName(String)}</li>
 *   <li>{@link ImageIO#getImageReadersByMIMEType(String)}</li>
 *   <li>{@link ImageIO#getImageWritersByMIMEType(String)}</li>
 * </ul>
 * <p>
 * The differences between the methods in this class and the methods in the JDK are:
 * <p>
 * <ul>
 *   <li>All methods accepts an input/output argument, which is used for checking if the plugin
 *       can accept that input/output.</li>
 *   <li>The methods first check if a reader/writer accepts directly the given input/output.
 *       If none are found then the input/ouput is wrapped in an image input/output stream
 *       and checked again.</li>
 *   <li>The methods return a singleton instead than an iterator, for convenience.</li>
 *   <li>The input/output of the returned reader/writer is already set to the value given
 *       in argument, or to the image stream if such a stream has been created. It is the
 *       caller responsability to close the stream.</li>
 * </ul>
 * <p>
 * The raison why attempt to use directly the given input/output is made before to create a new
 * image stream is that some plugins can not work with streams. This include plugins which depend
 * on external (sometime native) libraries (like HDF), or plugins which need the name of the file
 * (like {@link org.geotoolkit.image.io.text.WorldFileImageReader}).
 *
 * {@section Usage example}
 * The following example reads a PNG image. Because we use an input of type {@link java.io.File} instead
 * than {@link ImageInputStream}, the {@link org.geotoolkit.image.io.text.WorldFileImageReader} can
 * be used. Consequently the metadata associated with the PNG image will contains geolocalization
 * information, providing that a {@code ".pgw"} file was found with the PNG file.
 *
 * {@preformat java
 *     File          input    = new File("my_image.png");
 *     ImageReader   reader   = ImageFormats.getReaderBySuffix(input, true, null);
 *     IIOMetadata   metadata = reader.getImageMetadata(0);
 *     BufferedImage image    = reader.read(0);
 *     ImageFormats.close(reader);
 *     reader.dispose();
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 * @module
 */
@Static
public final class ImageFormats {
    /**
     * Used in {@code switch} statements for selecting the method to invoke
     * for choosing an image reader or writer.
     */
    private static final int NAME=0, SUFFIX=1, MIME=2;

    /**
     * Do not allow instantiation of this class.
     */
    private ImageFormats() {
    }

    /**
     * Makes sure an argument is non-null.
     *
     * @param  name   Argument name.
     * @param  object User argument.
     * @throws NullArgumentException if {@code object} is null.
     */
    private static void ensureNonNull(String name, Object object) throws NullArgumentException {
        if (object == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
    }

    /**
     * Returns an iterator over all providers of the given category having the given name,
     * suffix or MIME type.
     *
     * @param <T>      The compile-time type of the {@code category} argument.
     * @param category Either {@link ImageReaderSpi} or {@link ImageWriterSpi}.
     * @param mode     Either {@link #NAME}, {@link #SUFFIX} or {@link #MIME}. 
     * @param suffix     The name, suffix or MIME type to look for, or {@code null}.
     * @return         An iterator over the requested providers.
     */
    private static <T extends ImageReaderWriterSpi> Iterator<T> getServiceProviders(
            final Class<T> category, final int mode, final String name)
    {
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        if (name == null) {
            return registry.getServiceProviders(category, true);
        }
        final IIORegistry.Filter filter = new IIORegistry.Filter() {
            @Override public boolean filter(final Object provider) {
                final ImageReaderWriterSpi spi = (ImageReaderWriterSpi) provider;
                final String[] names;
                switch (mode) {
                    case NAME:   names = spi.getFormatNames();  break;
                    case SUFFIX: names = spi.getFileSuffixes(); break;
                    case MIME:   names = spi.getMIMETypes();    break;
                    default: throw new AssertionError(mode);
                }
                return XArrays.contains(names, name);
            }
        };
        return registry.getServiceProviders(category, filter, true);
    }

    /**
     * Returns a code indication which kind of input/output the given reader/writer accepts.
     * The meaning of the return value are:
     * <p>
     * 0: No input/output fit.<br>
     * 1: The given input/output type can be used directly.<br>
     * 2: An image input/output stream can be used.</br>
     *
     * @param  allowedTypes The types allowed by the image reader/writer.
     * @param  givenType    The type of the input/output given by the user.
     * @param  streamType   The type of the image input/output stream.
     * @return A code indicating which kind of input is accepted.
     */
    private static int codeAllowedType(final Class<?>[] allowedTypes, final Class<?> givenType,
            final Class<? extends ImageInputStream> streamType)
    {
        int foundStream = 0;
        if (allowedTypes != null) {
            for (final Class<?> allowedType : allowedTypes) {
                if (allowedType.isAssignableFrom(givenType)) {
                    return 1; // The given type can be used directly.
                }
                if (allowedType.isAssignableFrom(streamType)) {
                    foundStream = 2;
                    // Continue the search in case the given type can be used directly.
                }
            }
        }
        return foundStream;
    }

    /**
     * Creates a new reader from the given provider, and initializes its input to the given value.
     * The {@code seekForwardOnly} and {@code ignoreMetadata} parameters are used only if they are
     * non-null, otherwise the plugin-dependent default is used.
     *
     * @param  spi The provider to use for creating a new reader instance.
     * @param  input The input to be given to the new reader instance.
     * @param  seekForwardOnly If {@code true}, images and metadata may only be read in ascending
     *         order from the input source.
     * @param  ignoreMetadata If {@code true}, metadata may be ignored during reads. 
     * @return The new image reader instance with its input initialized.
     * @throws IOException If an error occured while creating the image reader instance.
     */
    private static ImageReader createReaderInstance(final ImageReaderSpi spi, final Object input,
            Boolean seekForwardOnly, Boolean ignoreMetadata) throws IOException
    {
        final ImageReader reader = spi.createReaderInstance();
        if (input != null) {
            if (ignoreMetadata != null) {
                reader.setInput(input, (seekForwardOnly != null) && seekForwardOnly, ignoreMetadata);
            } else if (seekForwardOnly != null) {
                reader.setInput(input, seekForwardOnly);
            } else {
                reader.setInput(input);
            }
        }
        return reader;
    }

    /**
     * Creates a new reader for the given input. The {@code seekForwardOnly} and
     * {@code ignoreMetadata} parameters are used only if they are non-null, otherwise
     * the plugin-dependent default is used.
     *
     * @param  mode  Either {@link #NAME}, {@link #SUFFIX} or {@link #MIME}.
     * @param  name  The name, suffix or MIME type to look for, or {@code null}.
     * @param  input The input to be given to the new reader instance, or {@code null} if none.
     * @param  seekForwardOnly If {@code true}, images and metadata may only be read in ascending
     *         order from the input source.
     * @param  ignoreMetadata If {@code true}, metadata may be ignored during reads.
     * @return The new image reader instance with its input initialized.
     * @throws IOException If an error occured while creating the image reader instance.
     */
    private static ImageReader getReader(final int mode, final String name, final Object input,
            Boolean seekForwardOnly, Boolean ignoreMetadata) throws IOException
    {
        List<ImageReaderSpi> tryAgain = null; // Will be created only if needed.
        Iterator<ImageReaderSpi> it = getServiceProviders(ImageReaderSpi.class, mode, name);
        while (it.hasNext()) {
            final ImageReaderSpi spi = it.next();
            if (input == null || spi.canDecodeInput(input)) {
                return createReaderInstance(spi, input, seekForwardOnly, ignoreMetadata);
            }
            /*
             * The Spi has correct format name, MIME type or suffix but claims to be unable
             * to decode the given input. If the input was not already an ImageInputStream,
             * remember that Spi so we can try it again with an ImageInputStream later.
             */
            if (input instanceof ImageInputStream) {
                continue;
            }
            // We accept only case 2 below (not case 1) because if the
            // given type was a legal type, it should have succeed above.
            if (codeAllowedType(spi.getInputTypes(), input.getClass(), ImageInputStream.class) == 2) {
                if (tryAgain == null) {
                    tryAgain = new ArrayList<ImageReaderSpi>(2);
                }
                tryAgain.add(spi);
            }
        }
        /*
         * No Spi accept directly the given input. If at least one Spi accepts an
         * ImageInputStream, create the stream and check again.
         */
        if (tryAgain != null) {
            final ImageInputStream stream = ImageIO.createImageInputStream(input);
            if (stream != null) {
                it = tryAgain.iterator();
                while (it.hasNext()) {
                    final ImageReaderSpi spi = it.next();
                    if (spi.canDecodeInput(stream)) {
                        return createReaderInstance(spi, stream, seekForwardOnly, ignoreMetadata);
                    }
                }
                stream.close();
            }
        }
        throw new IIOException(Errors.format(Errors.Keys.NO_IMAGE_READER));
    }

    /**
     * Creates a new reader for the given input. If a reader is found, its input will be set
     * as in the code below before the reader is returned by this method:
     *
     * {@preformat java
     *     reader.setInput(input, seekForwardOnly, ignoreMetadata);
     * }
     *
     * The {@code seekForwardOnly} and {@code ignoreMetadata} parameters are used only if they
     * are non-null, otherwise the plugin-dependent default is used.
     *
     * @param  input The mandatory input to be given to the new reader instance.
     * @param  seekForwardOnly If {@code true}, images and metadata may only be read in ascending
     *         order from the input source. If {@code false}, they may be read in any order. If
     *         {@code null}, this parameter is not given to the reader which is free to use a
     *         plugin-dependent default (usually {@code false}).
     * @param  ignoreMetadata If {@code true}, metadata may be ignored during reads. If {@code false},
     *         metadata will be parsed. If {@code null}, this parameter is not given to the reader
     *         which is free to use a plugin-dependent default (usually {@code false}).
     * @return The new image reader instance with its input initialized.
     * @throws IOException If no suitable image reader has been found, or if an error occured
     *         while creating it.
     *
     * @see ImageIO#getImageReaders(Object)
     * @see ImageReader#setInput(Object, boolean, boolean)
     */
    public static ImageReader getReader(final Object input,
            final Boolean seekForwardOnly, final Boolean ignoreMetadata) throws IOException
    {
        ensureNonNull("input", input);
        return getReader(0, null, input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Creates a new reader for the given input, considering only the readers that claim to
     * decode files having the suffix found in the input. If a reader is found and the given input
     * is non-null, then the reader will be initialized to the given input as in the code below:
     *
     * {@preformat java
     *     reader.setInput(input, seekForwardOnly, ignoreMetadata);
     * }
     *
     * The {@code seekForwardOnly} and {@code ignoreMetadata} parameters are used only if they
     * are non-null, otherwise the plugin-dependent default is used.
     *
     * @param  input The input to be given to the new reader instance.
     * @param  seekForwardOnly If {@code true}, images and metadata may only be read in ascending
     *         order from the input source. If {@code false}, they may be read in any order. If
     *         {@code null}, this parameter is not given to the reader which is free to use a
     *         plugin-dependent default (usually {@code false}).
     * @param  ignoreMetadata If {@code true}, metadata may be ignored during reads. If {@code false},
     *         metadata will be parsed. If {@code null}, this parameter is not given to the reader
     *         which is free to use a plugin-dependent default (usually {@code false}).
     * @return The new image reader instance with its input initialized.
     * @throws IOException If no suitable image reader has been found, or if an error occured
     *         while creating it.
     *
     * @see ImageIO#getImageReadersBySuffix(String)
     * @see ImageReader#setInput(Object, boolean, boolean)
     */
    public static ImageReader getReaderBySuffix(final Object input,
            final Boolean seekForwardOnly, final Boolean ignoreMetadata) throws IOException
    {
        ensureNonNull("input", input);
        return getReaderBySuffix(IOUtilities.extension(input), input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Creates a new reader for the given input, considering only the readers that claim to
     * decode files having the given suffix. If a reader is found and the given input is non-null,
     * then the reader will be initialized to the given input as in the code below:
     *
     * {@preformat java
     *     reader.setInput(input, seekForwardOnly, ignoreMetadata);
     * }
     *
     * The {@code seekForwardOnly} and {@code ignoreMetadata} parameters are used only if they
     * are non-null, otherwise the plugin-dependent default is used.
     *
     * @param  suffix The file suffix for which we want a reader.
     * @param  input An optional input to be given to the new reader instance, or {@code null} if none.
     * @param  seekForwardOnly If {@code true}, images and metadata may only be read in ascending
     *         order from the input source. If {@code false}, they may be read in any order. If
     *         {@code null}, this parameter is not given to the reader which is free to use a
     *         plugin-dependent default (usually {@code false}).
     * @param  ignoreMetadata If {@code true}, metadata may be ignored during reads. If {@code false},
     *         metadata will be parsed. If {@code null}, this parameter is not given to the reader
     *         which is free to use a plugin-dependent default (usually {@code false}).
     * @return The new image reader instance with its input initialized.
     * @throws IOException If no suitable image reader has been found, or if an error occured
     *         while creating it.
     *
     * @see ImageIO#getImageReadersBySuffix(String)
     * @see ImageReader#setInput(Object, boolean, boolean)
     */
    public static ImageReader getReaderBySuffix(final String suffix, final Object input,
            final Boolean seekForwardOnly, final Boolean ignoreMetadata) throws IOException
    {
        ensureNonNull("suffix", suffix);
        return getReader(SUFFIX, suffix, input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Creates a new reader for the given input, considering only the readers of the given format
     * name. If a reader is found and the given input is non-null, then the reader will be
     * initialized to the given input as in the code below:
     *
     * {@preformat java
     *     reader.setInput(input, seekForwardOnly, ignoreMetadata);
     * }
     *
     * The {@code seekForwardOnly} and {@code ignoreMetadata} parameters are used only if they
     * are non-null, otherwise the plugin-dependent default is used.
     *
     * @param  name The name of the format looked for.
     * @param  input An optional input to be given to the new reader instance, or {@code null} if none.
     * @param  seekForwardOnly If {@code true}, images and metadata may only be read in ascending
     *         order from the input source. If {@code false}, they may be read in any order. If
     *         {@code null}, this parameter is not given to the reader which is free to use a
     *         plugin-dependent default (usually {@code false}).
     * @param  ignoreMetadata If {@code true}, metadata may be ignored during reads. If {@code false},
     *         metadata will be parsed. If {@code null}, this parameter is not given to the reader
     *         which is free to use a plugin-dependent default (usually {@code false}).
     * @return The new image reader instance with its input initialized.
     * @throws IOException If no suitable image reader has been found, or if an error occured
     *         while creating it.
     *
     * @see ImageIO#getImageReadersByFormatName(String)
     * @see ImageReader#setInput(Object, boolean, boolean)
     */
    public static ImageReader getReaderByFormatName(final String name, final Object input,
            final Boolean seekForwardOnly, final Boolean ignoreMetadata) throws IOException
    {
        ensureNonNull("name", name);
        return getReader(NAME, name, input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Creates a new reader for the given input, considering only the readers for the given
     * MIME type. If a reader is found and the given input is non-null, then the reader will
     * be initialized to the given input as in the code below:
     *
     * {@preformat java
     *     reader.setInput(input, seekForwardOnly, ignoreMetadata);
     * }
     *
     * The {@code seekForwardOnly} and {@code ignoreMetadata} parameters are used only if they
     * are non-null, otherwise the plugin-dependent default is used.
     *
     * @param  mime The MIME type of the format looked for.
     * @param  input An optional input to be given to the new reader instance, or {@code null} if none.
     * @param  seekForwardOnly If {@code true}, images and metadata may only be read in ascending
     *         order from the input source. If {@code false}, they may be read in any order. If
     *         {@code null}, this parameter is not given to the reader which is free to use a
     *         plugin-dependent default (usually {@code false}).
     * @param  ignoreMetadata If {@code true}, metadata may be ignored during reads. If {@code false},
     *         metadata will be parsed. If {@code null}, this parameter is not given to the reader
     *         which is free to use a plugin-dependent default (usually {@code false}).
     * @return The new image reader instance with its input initialized.
     * @throws IOException If no suitable image reader has been found, or if an error occured
     *         while creating it.
     *
     * @see ImageIO#getImageReadersByMIMEType(String)
     * @see ImageReader#setInput(Object, boolean, boolean)
     */
    public static ImageReader getReaderByMIMEType(final String mime, final Object input,
            final Boolean seekForwardOnly, final Boolean ignoreMetadata) throws IOException
    {
        ensureNonNull("mime", mime);
        return getReader(MIME, mime, input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Closes the input stream of the given reader.
     *
     * @param  reader The reader for which to close the input stream.
     * @throws IOException If an error occured while closing the stream.
     */
    public static void close(final ImageReader reader) throws IOException {
        close(reader.getInput());
    }

    /**
     * Closes the given stream.
     */
    private static void close(final Object stream) throws IOException {
        if (stream instanceof ImageInputStream) {
            ((ImageInputStream) stream).close();
        } else if (stream instanceof Closeable) {
            ((Closeable) stream).close();
        }
    }

    /**
     * Creates a new writer from the given provider, and initializes its output to the given value.
     *
     * @param  spi The provider to use for creating a new writer instance.
     * @param  output The output to be given to the new writer instance.
     * @return The new image writer instance with its output initialized.
     * @throws IOException If an error occured while creating the image writer instance.
     */
    private static ImageWriter createWriterInstance(final ImageWriterSpi spi, final Object output)
            throws IOException
    {
        final ImageWriter writer = spi.createWriterInstance();
        if (output != null) {
            writer.setOutput(output);
        }
        return writer;
    }

    /**
     * Creates a new writer for the given output.
     *
     * @param  mode   Either {@link #NAME}, {@link #SUFFIX} or {@link #MIME}.
     * @param  name   The name, suffix or MIME type to look for, or {@code null}.
     * @param  output The output to be given to the new writer instance.
     * @param  image  The image to encode, or {@code null} if unknown.
     * @return The new image writer instance with its output initialized.
     * @throws IOException If an error occured while creating the image writer instance.
     */
    private static ImageWriter getWriter(final int mode, final String name,
            final Object output, final RenderedImage image) throws IOException
    {
        ImageWriterSpi fallback = null;
        Iterator<ImageWriterSpi> it = getServiceProviders(ImageWriterSpi.class, mode, name);
        while (it.hasNext()) {
            final ImageWriterSpi spi = it.next();
            if (image == null || spi.canEncodeImage(image)) {
                if (output == null) {
                    return createWriterInstance(spi, output);
                }
                switch (codeAllowedType(spi.getOutputTypes(), output.getClass(), ImageOutputStream.class)) {
                    /*
                     * The Spi can write directly in the given output.
                     */
                    case 1: {
                        return createWriterInstance(spi, output);
                    }
                    /*
                     * The Spi has correct format name, MIME type or suffix but claims to be unable
                     * to encode the given output. If the output was not an ImageOutputStream,
                     * remember that Spi so we can try it again with an ImageOutputStream later.
                     */
                    case 2: {
                        if (fallback == null && !(output instanceof ImageOutputStream)) {
                            fallback = spi;
                        }
                        break;
                    }
                }
            }
        }
        /*
         * No Spi accept directly the given output. If at least one Spi accepts an
         * ImageOutputStream, create the stream and check again.
         */
        if (fallback != null) {
            final ImageOutputStream stream = ImageIO.createImageOutputStream(output);
            if (stream != null) {
                return createWriterInstance(fallback, stream);
            }
        }
        throw new IIOException(Errors.format(Errors.Keys.NO_IMAGE_WRITER));
    }

    /**
     * Creates a new writer for the given output, considering only the writers that claim to
     * encode files having the suffix of the given output. If a writer is found, then the writer
     * will be initialized to the given output by a call to its
     * {@link ImageWriter#setOutput setOutput} method.
     *
     * @param  output A mandatory output to be given to the new writer instance.
     * @param  image  The image to encode, or {@code null} if unknown.
     * @return The new image writer instance with its output initialized.
     * @throws IOException If no suitable image writer has been found, or if an error occured
     *         while creating it.
     *
     * @see ImageIO#getImageWritersBySuffix(String)
     * @see ImageWriter#setOutput(Object)
     */
    public static ImageWriter getWriterBySuffix(final Object output, final RenderedImage image)
            throws IOException
    {
        ensureNonNull("output", output);
        return getWriterBySuffix(IOUtilities.extension(output), output, image);
    }

    /**
     * Creates a new writer for the given output, considering only the writers that claim to
     * encode files having the given suffix. If a writer is found and the given output is
     * non-null, then the writer will be initialized to the given output by a call to its
     * {@link ImageWriter#setOutput setOutput} method.
     *
     * @param  suffix The file suffix for which we want a writer.
     * @param  output An optional output to be given to the new writer instance, or {@code null} if none.
     * @param  image  The image to encode, or {@code null} if unknown.
     * @return The new image writer instance with its output initialized.
     * @throws IOException If no suitable image writer has been found, or if an error occured
     *         while creating it.
     *
     * @see ImageIO#getImageWritersBySuffix(String)
     * @see ImageWriter#setOutput(Object)
     */
    public static ImageWriter getWriterBySuffix(final String suffix, final Object output,
            final RenderedImage image) throws IOException
    {
        ensureNonNull("suffix", suffix);
        return getWriter(SUFFIX, suffix, output, image);
    }

    /**
     * Creates a new writer for the given output, considering only the writers of the given format
     * name. If a writer is found and the given output is non-null, then the writer will be
     * initialized to the given output by a call to its {@link ImageWriter#setOutput setOutput}
     * method.
     *
     * @param  name The format name for which we want a writer.
     * @param  output An optional output to be given to the new writer instance, or {@code null} if none.
     * @param  image  The image to encode, or {@code null} if unknown.
     * @return The new image writer instance with its output initialized.
     * @throws IOException If no suitable image writer has been found, or if an error occured
     *         while creating it.
     *
     * @see ImageIO#getImageWritersByFormatName(String)
     * @see ImageWriter#setOutput(Object)
     */
    public static ImageWriter getWriterByFormatName(final String name, final Object output,
            final RenderedImage image) throws IOException
    {
        ensureNonNull("name", name);
        return getWriter(NAME, name, output, image);
    }

    /**
     * Creates a new writer for the given output, considering only the writers for the given MIME
     * type. If a writer is found and the given output is non-null, then the writer will be
     * initialized to the given output by a call to its {@link ImageWriter#setOutput setOutput}
     * method.
     *
     * @param  mime   The MIME type for which we want a writer.
     * @param  output An optional output to be given to the new writer instance, or {@code null} if none.
     * @param  image  The image to encode, or {@code null} if unknown.
     * @return The new image writer instance with its output initialized.
     * @throws IOException If no suitable image writer has been found, or if an error occured
     *         while creating it.
     *
     * @see ImageIO#getImageWritersByMIMEType(String)
     * @see ImageWriter#setOutput(Object)
     */
    public static ImageWriter getWriterByMIMEType(final String mime, final Object output,
            final RenderedImage image) throws IOException
    {
        ensureNonNull("mime", mime);
        return getWriter(MIME, mime, output, image);
    }

    /**
     * Closes the output stream of the given writer.
     *
     * @param  writer The writer for which to close the output stream.
     * @throws IOException If an error occured while closing the stream.
     */
    public static void close(final ImageWriter writer) throws IOException {
        close(writer.getOutput());
    }
}

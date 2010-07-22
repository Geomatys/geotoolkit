/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
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
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.image.io.CheckedImageInputStream;
import org.geotoolkit.resources.Errors;


/**
 * Extensions to the set of static methods provided in the standard {@link ImageIO} class.
 * The methods defined in this {@code XImageIO} class fetch an {@link ImageReader} or an
 * {@link ImageWriter} for a given input, output, suffix, format name or mime type. They
 * are equivalent in purpose to the methods defined in {@code ImageIO} except that:
 * <p>
 * <ul>
 *   <li>The methods in this class return a single {@code ImageReader} or {@code ImageWriter}
 *       instead than an iterator, and throw an {@link IIOException} if no reader or writer
 *       is found.</li>
 *   <li>The methods in this class accept an optional input or output argument, in order to
 *       check if the reader or writer can use directly that argument before to create an
 *       input or output stream.</li>
 *   <li>The methods in this class automatically create an {@linkplain ImageInputStream image input}
 *       or {@linkplain ImageOutputStream output stream} if the reader or writer requires such
 *       stream.</li>
 * </ul>
 * <p>
 * For example, the standard Java API provides the following method:
 *
 * <blockquote>
 * {@link ImageIO#getImageReadersBySuffix(String)} with <var>suffix</var> argument
 * </blockquote>
 *
 * while this class provides the following equivalent API:
 *
 * <blockquote>
 * {@link #getReaderBySuffix(String, Object, Boolean, Boolean)} with <var>suffix</var>,
 * <var>input</var>, <var>seekForwardOnly</var> and <var>ignoreMetadata</var> arguments.
 * </blockquote>
 *
 * The raison why the methods in this class expect an optional input or output argument is that
 * the standard Image I/O Java library expects every {@linkplain ImageReader image readers} and
 * {@linkplain ImageWriter writers} to accept an {@linkplain ImageInputStream image input} or
 * {@linkplain ImageOutputStream output stream}, while some plugins used in the Geotk library
 * can not work with streams. For example plugins which depend on native libraries (e.g. the HDF
 * format) can typically accept only a filename as input, since the file will be open in C/C++
 * code. Some other plugins like {@link org.geotoolkit.image.io.plugin.WorldFileImageReader} require
 * the filename because they need to open many files, instead than reading a single stream.
 * Consequently if the user wants to read an image in a {@link java.io.File}, it is preferable to
 * check if an {@code ImageReader} accepts directly a {@code File} input before to open the file
 * in an {@code ImageInputStream}.
 *
 * {@section How the input/output is defined in the returned reader/writer}
 * The {@link ImageReader} and {@link ImageWriter} returned by the methods in this class will have
 * their input or output set. This is different than the standard {@code ImageIO} API (which returns
 * uninitialized readers or writers) and is done that way because the input or output may be different
 * than the one specified by the caller if it was necessary to create an {@link ImageInputStream} or
 * {@link ImageOutputStream}. If such stream has been created, then it is the caller responsibility
 * to close it after usage. The {@link #close(ImageReader)} and {@link #close(ImageWriter)}
 * convenience methods can be used for this purpose.
 * <p>
 * The output in the returned image writer is defined by a call to the
 * {@link ImageWriter#setOutput(Object)} method. The input in the returned
 * image reader is defined by a call to the {@link ImageReader#setInput(Object)},
 * {@link ImageReader#setInput(Object, boolean) setInput(Object, boolean)} or
 * {@link ImageReader#setInput(Object, boolean, boolean) setInput(Object, boolean, boolean)}
 * method depending on whatever the {@code seekForwardOnly} and {@code ignoreMetadata} arguments
 * are non-null or not. For example if those two {@link Boolean} arguments are null, then the
 * {@code setInput(Object)} flavor is invoked. This is usually equivalent to setting the two
 * boolean argument to {@code false}, but this behavior could have been overridden by the plugin.
 *
 * {@section Example}
 * The following example reads a TIFF image. Because we use an input of type {@link java.io.File}
 * instead than {@link ImageInputStream}, the {@link org.geotoolkit.image.io.plugin.WorldFileImageReader}
 * can be used. Consequently the metadata associated with the TIFF image can contain geolocalization
 * information if a {@code ".tfw"} file was found together with the {@code ".tiff"} file.
 *
 * {@preformat java
 *     File          input    = new File("my_image.tiff");
 *     ImageReader   reader   = XImageIO.getReaderBySuffix(input, true, false);
 *     IIOMetadata   metadata = reader.getImageMetadata(0);
 *     BufferedImage image    = reader.read(0);
 *     XImageIO.close(reader);
 *     reader.dispose();
 * }
 *
 * {@section Mandatory and optional arguments}
 * Every methods defined in this class expect exactly one mandatory argument, which is always
 * the first argument. All other arguments are optional and can be {@code null}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.07
 * @module
 */
@Static
public final class XImageIO {
    /**
     * Used in {@code switch} statements for selecting the method to invoke
     * for choosing an image reader or writer.
     */
    private static final int NAME=0, SUFFIX=1, MIME=2;

    /**
     * Do not allow instantiation of this class.
     */
    private XImageIO() {
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
     * @throws IOException If an error occurred while creating the image reader instance.
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
     * @throws IOException If an error occurred while creating the image reader instance.
     */
    private static ImageReader getReader(final int mode, final String name, final Object input,
            Boolean seekForwardOnly, Boolean ignoreMetadata) throws IOException
    {
        List<ImageReaderSpi> tryAgain = null; // Will be created only if needed.
        Iterator<ImageReaderSpi> it = getServiceProviders(ImageReaderSpi.class, mode, name);
        boolean hasFound = false;
        while (it.hasNext()) {
            final ImageReaderSpi spi = it.next();
            if (Boolean.TRUE.equals(ignoreMetadata)) {
                /*
                 * If the caller is not interrested in metadata, avoid the WorldFileImageReader.Spi
                 * wrapper in order to avoid the cost of reading the TFW/PRJ files.  We will rather
                 * use directly the wrapped reader, which should be somewhere next in the iteration.
                 */
                if (spi instanceof WorldFileImageReader.Spi) {
                    continue;
                }
            }
            if (input == null || spi.canDecodeInput(input)) {
                return createReaderInstance(spi, input, seekForwardOnly, ignoreMetadata);
            }
            /*
             * The Spi has correct format name, MIME type or suffix but claims to be unable
             * to decode the given input. If the input was not already an ImageInputStream,
             * remember that Spi so we can try it again with an ImageInputStream later.
             */
            hasFound = true;
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
            final ImageInputStream stream = createImageInputStream(input);
            if (stream != null) {
                it = tryAgain.iterator();
                while (it.hasNext()) {
                    final ImageReaderSpi spi = it.next();
                    if (spi.canDecodeInput(stream)) {
                        return createReaderInstance(spi, stream, seekForwardOnly, ignoreMetadata);
                    }
                }
                stream.close();
            } else {
                /*
                 * The stream can not be created. It may be because the file doesn't exist.
                 * Try to build a helpful error message.
                 */
                if (input instanceof File) {
                    File file = (File) input;
                    int messageKey = Errors.Keys.FILE_DOES_NOT_EXIST_$1;
                    File parent;
                    while ((parent = file.getParentFile()) != null && !parent.isDirectory()) {
                        messageKey = Errors.Keys.NOT_A_DIRECTORY_$1;
                        file = parent;
                    }
                    throw new FileNotFoundException(Errors.format(messageKey, file));
                } else {
                    final int messageKey;
                    final Object argument;
                    if (IOUtilities.canProcessAsPath(input)) {
                        messageKey = Errors.Keys.CANT_READ_$1;
                        argument = IOUtilities.name(input);
                    } else {
                        messageKey = Errors.Keys.UNKNOWN_TYPE_$1;
                        argument = input.getClass();
                    }
                    throw new IOException(Errors.format(messageKey, argument));
                }
            }
        }
        throw new UnsupportedImageFormatException(errorMessage(false, mode, name, hasFound));
    }

    /**
     * Creates an error message for an {@link ImageReader} or {@link ImageWriter} not found.
     *
     * @param  write {@code true} for a message appropriate for writers, or {@code false} for readers.
     * @param  mode  Either {@link #NAME}, {@link #SUFFIX} or {@link #MIME}.
     * @param  name  The name, suffix or MIME type to look for, or {@code null}.
     * @param  hasFound {@code true} if at least one reader or writer was found.
     * @return The error message to declare in exception constructor.
     */
    private static String errorMessage(final boolean write, final int mode, final String name, final boolean hasFound) {
        if (name == null || hasFound) {
            return Errors.format(write ? Errors.Keys.NO_IMAGE_WRITER : Errors.Keys.NO_IMAGE_READER);
        }
        final int key;
        String[] choices;
        switch (mode) {
            case NAME:   {
                key = Errors.Keys.UNKNOWN_IMAGE_FORMAT_$1;
                choices = write ? ImageIO.getWriterFormatNames() : ImageIO.getReaderFormatNames();
                break;
            }
            case SUFFIX: {
                key = Errors.Keys.UNKNOWN_FILE_SUFFIX_$1;
                choices = write ? ImageIO.getWriterFileSuffixes() : ImageIO.getReaderFileSuffixes();
                break;
            }
            case MIME: {
                key = Errors.Keys.UNKNOWN_MIME_TYPE_$1;
                choices = write ? ImageIO.getWriterMIMETypes() : ImageIO.getReaderMIMETypes();
                break;
            }
            default: throw new AssertionError(mode);
        }
        choices = Formats.simplify(choices);
        final boolean hasChoices = (choices != null && choices.length != 0);
        final Errors resources = Errors.getResources(null);
        String message;
        if (mode == NAME && hasChoices) {
            message = resources.getString(Errors.Keys.NO_IMAGE_FORMAT_$2, name, Arrays.toString(choices));
        } else {
            message = resources.getString(key, name);
            if (hasChoices) {
                message = message + ' ' + resources.getString(Errors.Keys.EXPECTED_ONE_OF_$1, Arrays.toString(choices));
            }
        }
        return message;
    }

    /**
     * Creates a new reader for the given input. The {@code input} argument is mandatory and will be
     * used for initializing the reader by a call to one of the {@link ImageReader#setInput(Object)}
     * methods. The method invoked depends on whatever the {@code seekForwardOnly} and
     * {@code ignoreMetadata} parameters are null or non-null.
     *
     * @param input
     *          The mandatory input to be given to the new reader instance.
     * @param seekForwardOnly
     *          Optional parameter to be given (if non-null) to the
     *          {@link ImageReader#setInput(Object, boolean) setInput} method: if {@code true},
     *          images and metadata may only be read in ascending order from the input source.
     *          If {@code false}, they may be read in any order. If {@code null}, this parameter
     *          is not given to the reader which is free to use a plugin-dependent default
     *          (usually {@code false}).
     * @param ignoreMetadata
     *          Optional parameter to be given (if non-null) to the
     *          {@link ImageReader#setInput(Object, boolean, boolean) setInput} method:
     *          if {@code true}, metadata may be ignored during reads. If {@code false}, metadata
     *          will be parsed. If {@code null}, this parameter is not given to the reader which
     *          is free to use a plugin-dependent default (usually {@code false}).
     * @return The new image reader instance with its input initialized.
     * @throws IOException If no suitable image reader has been found, or if an error occurred
     *         while creating it.
     *
     * @see ImageIO#getImageReaders(Object)
     */
    public static ImageReader getReader(final Object input,
            final Boolean seekForwardOnly, final Boolean ignoreMetadata) throws IOException
    {
        ensureNonNull("input", input);
        return getReader(0, null, input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Creates a new reader for the given input, considering only the readers that claim to decode
     * files having the suffix found in the input. The {@code input} argument is mandatory and will
     * be used for initializing the reader as documented in the {@link #getReader getReader(...)}
     * method.
     * <p>
     * If this method doesn't know how to get the suffix from the given input (for example
     * if the input is an instance of {@link ImageInputStream}), then this method delegates
     * to {@link #getReader(Object, Boolean, Boolean)}.
     *
     * @param  input The mandatory input to be given to the new reader instance.
     * @param  seekForwardOnly Optional parameter to be given (if non-null) to the
     *         {@link ImageReader#setInput(Object, boolean) setInput} method.
     * @param  ignoreMetadata Optional parameter to be given (if non-null) to the
     *         {@link ImageReader#setInput(Object, boolean, boolean) setInput} method.
     * @return The new image reader instance with its input initialized.
     * @throws IOException If no suitable image reader has been found, or if an error occurred
     *         while creating it.
     *
     * @see ImageIO#getImageReadersBySuffix(String)
     */
    public static ImageReader getReaderBySuffix(final Object input,
            final Boolean seekForwardOnly, final Boolean ignoreMetadata) throws IOException
    {
        ensureNonNull("input", input);
        if (!IOUtilities.canProcessAsPath(input)) {
            return getReader(input, seekForwardOnly, ignoreMetadata);
        }
        return getReaderBySuffix(IOUtilities.extension(input), input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Creates a new reader for the given optional input, considering only the readers that claim
     * to decode files having the given suffix. Only the {@code suffix} argument is mandatory. The
     * other ones are optional and will be used (if non-null) for initializing the reader as
     * documented in the {@link #getReader getReader(...)} method.
     *
     * @param  suffix The file suffix for which we want a reader.
     * @param  input An optional input to be given to the new reader instance, or {@code null} if none.
     * @param  seekForwardOnly Optional parameter to be given (if non-null) to the
     *         {@link ImageReader#setInput(Object, boolean) setInput} method.
     * @param  ignoreMetadata Optional parameter to be given (if non-null) to the
     *         {@link ImageReader#setInput(Object, boolean, boolean) setInput} method.
     * @return The new image reader instance with its input initialized (if {@code input} is not null).
     * @throws IOException If no suitable image reader has been found, or if an error occurred
     *         while creating it.
     *
     * @see ImageIO#getImageReadersBySuffix(String)
     */
    public static ImageReader getReaderBySuffix(final String suffix, final Object input,
            final Boolean seekForwardOnly, final Boolean ignoreMetadata) throws IOException
    {
        ensureNonNull("suffix", suffix);
        return getReader(SUFFIX, suffix, input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Creates a new reader for the given optional input, considering only the readers of the given
     * format name. Only the {@code name} argument is mandatory. The other ones are optional and
     * will be used (if non-null) for initializing the reader as documented in the
     * {@link #getReader getReader(...)} method.
     *
     * @param  name The name of the format looked for.
     * @param  input An optional input to be given to the new reader instance, or {@code null} if none.
     * @param  seekForwardOnly Optional parameter to be given (if non-null) to the
     *         {@link ImageReader#setInput(Object, boolean) setInput} method.
     * @param  ignoreMetadata Optional parameter to be given (if non-null) to the
     *         {@link ImageReader#setInput(Object, boolean, boolean) setInput} method.
     * @return The new image reader instance with its input initialized (if {@code input} is not null).
     * @throws IOException If no suitable image reader has been found, or if an error occurred
     *         while creating it.
     *
     * @see ImageIO#getImageReadersByFormatName(String)
     */
    public static ImageReader getReaderByFormatName(final String name, final Object input,
            final Boolean seekForwardOnly, final Boolean ignoreMetadata) throws IOException
    {
        ensureNonNull("name", name);
        return getReader(NAME, name, input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Creates a new reader for the given optional input, considering only the readers for the
     * given MIME type. Only the {@code mime} argument is mandatory. The other ones are optional
     * and will be used (if non-null) for initializing the reader as documented in the
     * {@link #getReader getReader(...)} method.
     *
     * @param  mime The MIME type of the format looked for.
     * @param  input An optional input to be given to the new reader instance, or {@code null} if none.
     * @param  seekForwardOnly Optional parameter to be given (if non-null) to the
     *         {@link ImageReader#setInput(Object, boolean) setInput} method.
     * @param  ignoreMetadata Optional parameter to be given (if non-null) to the
     *         {@link ImageReader#setInput(Object, boolean, boolean) setInput} method.
     * @return The new image reader instance with its input initialized (if {@code input} is not null).
     * @throws IOException If no suitable image reader has been found, or if an error occurred
     *         while creating it.
     *
     * @see ImageIO#getImageReadersByMIMEType(String)
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
     * @throws IOException If an error occurred while closing the stream.
     */
    public static void close(final ImageReader reader) throws IOException {
        ensureNonNull("reader", reader);
        final Object input = reader.getInput();
        reader.setInput(null);
        IOUtilities.close(input);
    }

    /**
     * Creates a new writer from the given provider, and initializes its output to the given value.
     *
     * @param  spi The provider to use for creating a new writer instance.
     * @param  output The output to be given to the new writer instance.
     * @return The new image writer instance with its output initialized.
     * @throws IOException If an error occurred while creating the image writer instance.
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
     * @throws IOException If an error occurred while creating the image writer instance.
     */
    private static ImageWriter getWriter(final int mode, final String name,
            final Object output, final RenderedImage image) throws IOException
    {
        ImageWriterSpi fallback = null;
        Iterator<ImageWriterSpi> it = getServiceProviders(ImageWriterSpi.class, mode, name);
        boolean hasFound = false;
        while (it.hasNext()) {
            hasFound = true;
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
        throw new UnsupportedImageFormatException(errorMessage(true, mode, name, hasFound));
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
     * @throws IOException If no suitable image writer has been found, or if an error occurred
     *         while creating it.
     *
     * @see ImageIO#getImageWritersBySuffix(String)
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
     * @return The new image writer instance with its output initialized (if {@code output} is not null).
     * @throws IOException If no suitable image writer has been found, or if an error occurred
     *         while creating it.
     *
     * @see ImageIO#getImageWritersBySuffix(String)
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
     * @return The new image writer instance with its output initialized (if {@code output} is not null).
     * @throws IOException If no suitable image writer has been found, or if an error occurred
     *         while creating it.
     *
     * @see ImageIO#getImageWritersByFormatName(String)
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
     * @return The new image writer instance with its output initialized (if {@code output} is not null).
     * @throws IOException If no suitable image writer has been found, or if an error occurred
     *         while creating it.
     *
     * @see ImageIO#getImageWritersByMIMEType(String)
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
     * @throws IOException If an error occurred while closing the stream.
     */
    public static void close(final ImageWriter writer) throws IOException {
        ensureNonNull("writer", writer);
        final Object output = writer.getOutput();
        writer.setOutput(null);
        IOUtilities.close(output);
    }

    /**
     * Returns the image reader provider for the given format name.
     *
     * @param  format The name of the provider to fetch.
     * @return The reader provider for the given format.
     * @throws IllegalArgumentException If no provider is found for the given format.
     */
    public static ImageReaderSpi getReaderSpiByFormatName(final String format) {
        ensureNonNull("format", format);
        return Formats.getReaderByFormatName(format, null);
    }

    /**
     * Returns the image writer provider for the given format name.
     *
     * @param  format The name of the provider to fetch.
     * @return The reader provider for the given format.
     * @throws IllegalArgumentException If no provider is found for the given format.
     */
    public static ImageWriterSpi getWriterSpiByFormatName(final String format) {
        ensureNonNull("format", format);
        return Formats.getWriterByFormatName(format, null);
    }

    /**
     * Creates an image input stream for the given source. This method first delegates
     * to {@link ImageIO#createImageInputStream(Object)}, then wraps the result in a
     * {@link CheckedImageInputStream} if assertions are enabled.
     *
     * @param  input The input for which an image input is desired.
     * @return The image input stream, or {@code null}.
     * @throws IOException If an error occurred while creating the stream.
     *
     * @since 3.14
     */
    private static ImageInputStream createImageInputStream(final Object input) throws IOException {
        ImageInputStream in = ImageIO.createImageInputStream(input);
        assert CheckedImageInputStream.isValid(in = // Intentional side effect.
               CheckedImageInputStream.wrap(in));
        return in;
    }
}

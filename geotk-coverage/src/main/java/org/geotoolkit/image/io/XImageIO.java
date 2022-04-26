/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.collection.FrequencySortedSet;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.factory.Factories;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.lang.Static;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.collection.DeferringIterator;

/**
 * Extensions to the set of static methods provided in the standard {@link ImageIO} class.
 * The methods defined in this {@code XImageIO} class fetch an {@link ImageReader} or an
 * {@link ImageWriter} for a given input, output, suffix, format name or mime type. They
 * are equivalent in purpose to the methods defined in {@code ImageIO} except that:
 * <p>
 * <ul>
 *   <li>Return a single {@code ImageReader} or {@code ImageWriter} instead than an iterator.</li>
 *   <li>Throw an {@link IIOException} if no reader or writer is found.</li>
 *   <li>Accept an optional input or output argument, in order to check if the reader or writer
 *       can use directly that argument instead than creating an input or output stream.</li>
 *   <li>Create automatically an {@linkplain ImageInputStream image input} or
 *       {@linkplain ImageOutputStream output stream} if needed.</li>
 * </ul>
 * <p>
 * For example, the standard Java API provides the following method:
 *
 * <blockquote>
 * {@link ImageIO#getImageReadersBySuffix(String)} with {@code suffix} argument
 * </blockquote>
 *
 * while this class provides the following equivalent API:
 *
 * <blockquote>
 * {@link #getReaderBySuffix(String, Object, Boolean, Boolean)} with {@code suffix},
 * {@code input}, {@code seekForwardOnly} and {@code ignoreMetadata} arguments.
 * </blockquote>
 *
 * {@note <b>Why <code>XImageIO</code> methods expect an optional input argument:</b>
 * The standard <code>ImageIO</code> methods do not take input argument because the Java Image I/O
 * framework expects every <code>ImageReader</code>s to accept an <code>ImageInputStream</code>.
 * Actually, <code>ImageIO</code> creates image input streams unconditionally, without checking
 * whatever it was necessary or not.
 * <p>
 * However, some plugins used by the Geotk library can not work with streams. Some plugins (e.g.
 * the HDF format) open the file in native C/C++ code. Those libraries can not work with Java
 * streams - they require a plain filename. Even in pure Java code, sometime a filename or URL
 * is required. For example the Geotk <code>WorldFileImageReader</code> needs to open many files
 * for the same image. Consequently if the user wants to read an image from a <code>File</code>,
 * it is preferable to check if an <code>ImageReader</code> accepts directly such <code>File</code>
 * input before to create an <code>ImageInputStream</code>.}
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
 * {@section Mandatory and optional arguments}
 * Every methods defined in this class expect exactly one mandatory argument, which is always
 * the first argument. All other arguments are optional and can be {@code null}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.07
 * @module
 */
public final class XImageIO extends Static {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.image");

    /**
     * Used in {@code switch} statements for selecting the method to invoke
     * for choosing an image reader or writer.
     */
    private static final int NAME=0, SUFFIX=1, MIME=2;

    /**
     * Hack to avoid default java tiff reader.
     * Provider reader ordonnancement is aleatory.
     */
    private static final String JAI_TIFF_READER_HACK = "com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi";

    /**
     * Do not allow instantiation of this class.
     */
    private XImageIO() {
    }

    /**
     * Returns the names, suffixes of mime types of the given provider
     * depending on the value of the {@code mode} argument.
     *
     * @param  spi  The provider from which to get the identifiers.
     * @param  mode Either {@link #NAME}, {@link #SUFFIX} or {@link #MIME}.
     * @return The requested identifiers.
     */
    static String[] getIdentifiers(final ImageReaderWriterSpi spi, final int mode) {
        switch (mode) {
            case NAME:   return spi.getFormatNames();
            case SUFFIX: return spi.getFileSuffixes();
            case MIME:   return spi.getMIMETypes();
            default: throw new AssertionError(mode);
        }
    }

    /**
     * Returns an iterator over all providers of the given category having the given name,
     * suffix or MIME type.
     *
     * @param <T>       The compile-time type of the {@code category} argument.
     * @param category  Either {@link ImageReaderSpi} or {@link ImageWriterSpi}.
     * @param mode      Either {@link #NAME}, {@link #SUFFIX} or {@link #MIME}.
     * @param suffix    The name, suffix or MIME type to look for, or {@code null}.
     * @return          An iterator over the requested providers.
     */
    private static <T extends ImageReaderWriterSpi> Iterator<T> getServiceProviders(
            final Class<T> category, final int mode, final String name)
    {
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        if (name == null) {
            return orderForClassLoader(registry.getServiceProviders(category, true));
        }
        /*
         * This inner class filters the providers according the name given in argument to this method,
         * ignoring cases. However this filter will opportunistically keep trace of providers for which
         * the lower/upper cases don't match. We do that because the Image I/O API seems to be usually
         * case-sensitive (for example the PNG format declares both the ".png" and ".PNG" suffixes),
         * while some formats seem to forgot to declare the two variants (for example the TIFF format
         * declares the ".tiff" suffix but not the ".TIFF" one). As a safety, we will give precedence
         * to providers for which an exact match is found before to accept a provider which match only
         * when ignoring cases.
         */
        @SuppressWarnings("serial")
        final class Filter extends IdentityHashMap<Object,Object> implements IIORegistry.Filter {
            @Override public boolean filter(final Object provider) {
                final String[] identifiers = getIdentifiers((ImageReaderWriterSpi) provider, mode);
                boolean found = ArraysExt.contains(identifiers, name);
                if (!found) {
                    found = ArraysExt.containsIgnoreCase(identifiers, name);
                    if (found) {
                        // Remember that this provider matches only when ignoring case.
                        put(provider, null);
                    }
                }
                return found;
            }
        }
        /*
         * At this point, the map is still empty. It may be filled during the iteration process.
         * Any element stored in the map will be deferred to the end of the iteration.
         */
        final Filter filter = new Filter();
        return new DeferringIterator<T>(orderForClassLoader(registry.getServiceProviders(category, filter, true))) {
            @Override protected boolean isDeferred(final T element) {
                return filter.containsKey(element);
            }
        };
    }

    /**
     * Wraps the given iterator in order to give precedence to providers loaded by this
     * application class loader, before providers loaded by class loaders of other applications.
     *
     * @since 3.20
     */
    private static <T extends ImageReaderWriterSpi> Iterator<T> orderForClassLoader(final Iterator<T> iterator) {
        return Factories.orderForClassLoader(XImageIO.class.getClassLoader(), iterator);
    }

    /**
     * Returns a code indicating which kind of input/output the given reader/writer accepts.
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
        List<ImageReaderSpi> usingImageInputStream = null; // Will be created only if needed.
        Iterator<ImageReaderSpi> it = getServiceProviders(ImageReaderSpi.class, mode, name);
        boolean hasFound = false;
        while (it.hasNext()) {
            final ImageReaderSpi spi = it.next();

            //-- to avoid unexpected jai tiff reader
            if (JAI_TIFF_READER_HACK.equalsIgnoreCase(spi.getClass().getName())) continue;

            if (Boolean.TRUE.equals(ignoreMetadata)) {
                /*
                 * If the caller is not interested in metadata, avoid the WorldFileImageReader.Spi
                 * wrapper in order to avoid the cost of reading the TFW/PRJ files.  We will rather
                 * use directly the wrapped reader, which should be somewhere next in the iteration.
                 */
                if (spi instanceof ImageReaderAdapter.Spi) {
                    final Set<InformationType> info = ((ImageReaderAdapter.Spi) spi).getModifiedInformation(input);
                    if (!info.contains(InformationType.RASTER) && !info.contains(InformationType.IMAGE)) {
                        // Actually, skips the adapter only if it does not modify the pixel values.
                        continue;
                    }
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
                if (usingImageInputStream == null) {
                    usingImageInputStream = new ArrayList<>(2);
                }
                usingImageInputStream.add(spi);
            }
        }
        /*
         * No Spi accept directly the given input. If at least one Spi accepts an
         * ImageInputStream, create the stream and check again.
         */
        if (usingImageInputStream != null) {
            try {
                final ImageInputStream stream = CoverageIO.createImageInputStream(input);
                it = usingImageInputStream.iterator();
                while (it.hasNext()) {
                    final ImageReaderSpi spi = it.next();

                    //-- to avoid unexpected jai tiff reader
                    if (JAI_TIFF_READER_HACK.equalsIgnoreCase(spi.getClass().getName())) continue;

                    if (spi.canDecodeInput(stream)) {
                        return createReaderInstance(spi, stream, seekForwardOnly, ignoreMetadata);
                    }
                }
                //close stream if no spi support created stream
                stream.close();
            } catch (IOException e) {
                /*
                 * The stream can not be created. It may be because the file doesn't exist.
                 * From this point we consider that the operation failed, but we need to
                 * build an error message as helpful as possible.
                 */
                if (input instanceof File || input instanceof Path) {
                    Path file = (input instanceof File) ? ((File) input).toPath() : (Path) input;
                    short messageKey = Errors.Keys.FileDoesNotExist_1;
                    Path parent;
                    while ((parent = file.getParent()) != null && !Files.isDirectory(parent)) {
                        messageKey = Errors.Keys.NotADirectory_1;
                        file = parent;
                    }
                    throw new FileNotFoundException(Errors.format(messageKey, file));
                } else {
                    final short messageKey;
                    final Object argument;
                    if (IOUtilities.canProcessAsPath(input)) {
                        messageKey = Errors.Keys.CantReadFile_1;
                        argument = IOUtilities.filename(input);
                    } else {
                        messageKey = Errors.Keys.UnknownType_1;
                        argument = input.getClass();
                    }
                    throw new IIOException(Errors.format(messageKey, argument));
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
            return Errors.format(write ? Errors.Keys.NoImageWriter : Errors.Keys.NoImageReader);
        }
        final short key;
        String[] choices;
        switch (mode) {
            case NAME:   {
                key = Errors.Keys.UnknownImageFormat_1;
                choices = write ? ImageIO.getWriterFormatNames() : ImageIO.getReaderFormatNames();
                break;
            }
            case SUFFIX: {
                key = Errors.Keys.UnknownFileSuffix_1;
                choices = write ? ImageIO.getWriterFileSuffixes() : ImageIO.getReaderFileSuffixes();
                break;
            }
            case MIME: {
                key = Errors.Keys.UnknownMimeType_1;
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
            message = resources.getString(Errors.Keys.NoImageFormat_2, name, Arrays.toString(choices));
        } else {
            message = resources.getString(key, name);
            if (hasChoices) {
                message = message + ' ' + resources.getString(Errors.Keys.ExpectedOneOf_1, Arrays.toString(choices));
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
        return getReaderBySuffix(IOUtilities.extension(input),
                input, seekForwardOnly, ignoreMetadata);
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
     * Closes the input stream of the given reader, and
     * {@linkplain ImageReader#setInput(Object) sets the input} to {@code null}.
     *
     * @param  reader The reader for which to close the input stream.
     * @throws IOException If an error occurred while closing the stream.
     */
    public static void close(final ImageReader reader) throws IOException {
        ensureNonNull("reader", reader);
        final Object input = reader.getInput();
        if (reader instanceof SpatialImageReader) {
            reader.setInput(null);
        }
        IOUtilities.close(input);
    }

    /**
     * Like {@link #close(ImageReader)} this method close the input stream of the given reader
     * but also dispose ImageReader itself.
     *
     * @param reader The reader for which to close the input stream.
     * @throws IOException If an error occurred while closing the stream.
     */
    public static void dispose(final ImageReader reader) throws IOException {
        close(reader);
        reader.dispose();
    }

    /**
     * Like {@link #dispose(ImageReader)} this method close the input stream of the given reader
     * but also dispose ImageReader itself without throwing an exception.
     *
     * @param reader reader to dispose
     * @return dispose status. {@code true} if dispose succeed, {@code false} otherwise
     */
    public static boolean disposeSilently(final ImageReader reader) {
        if(reader == null) return true;
        try {
            dispose(reader);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.FINER, e.getLocalizedMessage(), e);
            return false;
        }
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
     * Closes the output stream of the given writer, and
     * {@linkplain ImageWriter#setOutput(Object) sets the output} to {@code null}.
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
     * Like {@link #close(ImageWriter)} this method close the input stream of the given writer
     * but also dispose ImageWriter itself.
     *
     * @param writer The writer for which to close the input stream.
     * @throws IOException If an error occurred while closing the stream.
     */
    public static void dispose(final ImageWriter writer) throws IOException {
        close(writer);
        writer.dispose();
    }

    /**
     * Like {@link #close(ImageWriter)} this method close the input stream of the given writer
     * but also dispose ImageWriter itself without throwing an exception.
     *
     * @param writer The writer for which to close the input stream.
     * @return dispose status. {@code true} if dispose succeed, {@code false} otherwise
     */
    public static boolean disposeSilently(final ImageWriter writer) {
        if(writer == null) return true;
        try {
            close(writer);
            writer.dispose();
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.FINER, e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * Returns the provider of {@code ImageReader}s corresponding to the given provider of
     * {@code ImageWriter}s, or {@code null} if none. This method performs the same work
     * than {@link ImageIO#getImageReader(ImageWriter)}, except that it works on providers
     * instead than reader/writer instances.
     *
     * @param writerSpi The provider of image writers for which to get the provider of image readers.
     *        Can be {@code null}, in which case this method will return {@code null}.
     * @return The provider of image readers, or {@code null} if none.
     *
     * @see ImageIO#getImageReader(ImageWriter)
     * @see ImageWriterSpi#getImageReaderSpiNames()
     *
     * @since 3.20
     */
    public static ImageReaderSpi getImageReaderSpi(final ImageWriterSpi writerSpi) {
        if (writerSpi == null) {
            return null;
        }
        return (ImageReaderSpi) getSpi(writerSpi.getImageReaderSpiNames(), "getImageReaderSpi");
    }

    /**
     * Returns the provider of {@code ImageWriter}s corresponding to the given provider of
     * {@code ImageReader}s, or {@code null} if none. This method performs the same work
     * than {@link ImageIO#getImageWriter(ImageReader)}, except that it works on providers
     * instead than reader/writer instances.
     *
     * @param readerSpi The provider of image readers for which to get the provider of image writers.
     *        Can be {@code null}, in which case this method will return {@code null}.
     * @return The provider of image writers, or {@code null} if none.
     *
     * @see ImageIO#getImageWriter(ImageReader)
     * @see ImageReaderSpi#getImageWriterSpiNames()
     *
     * @since 3.20
     */
    public static ImageWriterSpi getImageWriterSpi(final ImageReaderSpi readerSpi) {
        if (readerSpi == null) {
            return null;
        }
        return (ImageWriterSpi) getSpi(readerSpi.getImageWriterSpiNames(), "getImageWriterSpi");
    }

    /**
     * Implementation of {@link #getImageReaderSpi(ImageWriterSpi)} and
     * {@link #getImageWriterSpi(ImageReaderSpi)}.
     */
    private static Object getSpi(final String[] names, final String methodName) {
        if (names != null) {
            final IIORegistry registry = IIORegistry.getDefaultInstance();
            for (final String name : names) {
                try {
                    final Object spi = registry.getServiceProviderByClass(Class.forName(name));
                    if (spi != null) {
                        return spi;
                    }
                } catch (ClassNotFoundException e) {
                    Logging.recoverableException(null, XImageIO.class, methodName, e);
                }
            }
        }
        return null;
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
     * Returns the preferred suffix declared in the given provider, with a leading dot.
     * If the given provider is null, or if no preferred file suffix is found, then this
     * method returns {@code null}.
     *
     * @param  provider The provider for which to get the first file suffix, or {@code null}.
     * @return The first file suffix, or {@code null} if none.
     *
     * @since 3.20
     */
    public static String getFileSuffix(final ImageReaderWriterSpi provider) {
        if (provider != null) {
            final String[] suffixes = provider.getFileSuffixes();
            if (suffixes != null) {
                for (String suffix : suffixes) {
                    if (suffix != null && !(suffix = suffix.trim()).isEmpty()) {
                        if (suffix.charAt(0) != '.') {
                            suffix = '.' + suffix;
                        }
                        return suffix;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the format names of all {@link ImageReaderSpi} and/or {@link ImageWriterSpi}
     * instances registered for the given MIME type. If there is many format names for the
     * same MIME type, then the most frequently used names will be sorted first.
     *
     * @param mime  The MIME type to search for.
     * @param read  {@code true} if the format name is required to be supported by at least one {@code ImageReader}.
     * @param write {@code true} if the format name is required to be supported by at least one {@code ImageWriter}.
     * @return The format names, or an empty array if none.
     *
     * @since 3.14
     */
    public static String[] getFormatNamesByMimeType(final String mime, final boolean read, final boolean write) {
        ensureNonNull("mime", mime);
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        final FrequencySortedSet<String> formats = new FrequencySortedSet<>(true);
        if (read != write) {
            /*
             * Caller asked for read support, or write support, but not both.
             * Query only the appropriate type.
             */
            getFormatNamesByMimeType(registry, mime, read ? ImageReaderSpi.class : ImageWriterSpi.class, formats);
        } else if (!read) {
            /*
             * Read or write support was not explicitly required: returns the union of
             * all formats, regardless if they are supported by readers or writers.
             */
            getFormatNamesByMimeType(registry, mime, ImageReaderSpi.class, formats);
            getFormatNamesByMimeType(registry, mime, ImageWriterSpi.class, formats);
        } else {
            /*
             * Caller asked for read and write support.
             * Computes the intersection of both sets.
             */
            final List<String> readers = new ArrayList<>();
            final List<String> writers = new ArrayList<>();
            getFormatNamesByMimeType(registry, mime, ImageReaderSpi.class, readers);
            getFormatNamesByMimeType(registry, mime, ImageWriterSpi.class, writers);
            // First, add all formats in order to compute their frequencies.
            formats.addAll(readers);
            formats.addAll(writers);
            // Next, compute the intersection.
            formats.retainAll(readers);
            formats.retainAll(writers);
        }
        return formats.toArray(new String[formats.size()]);
    }

    /**
     * Adds to the given set the format names of every {@link ImageReaderSpi} or
     * {@link ImageWriterSpi} instances registered for the given MIME type.
     *
     * @param registry The registry from which to get the provider.
     * @param mime     The MIME type to search for.
     * @param type     {@link ImageReaderSpi} or {@link ImageWriterSpi}.
     * @param addTo    The set in which to add the format names.
     */
    private static void getFormatNamesByMimeType(final ServiceRegistry registry, final String mime,
            final Class<? extends ImageReaderWriterSpi> type, final Collection<String> addTo)
    {
        final Iterator<? extends ImageReaderWriterSpi> it = registry.getServiceProviders(type, false);
        while (it.hasNext()) {
            final ImageReaderWriterSpi spi = it.next();
            if (ArraysExt.containsIgnoreCase(spi.getMIMETypes(), mime)) {
                final String[] names = spi.getFormatNames();
                if (names != null) {
                    addTo.addAll(Arrays.asList(names));
                }
            }
        }
    }

    /**
     * Check if the provided object is an instance of one of the given classes.
     * Used test if an obect is compatible with {@link ImageWriterSpi#getOutputTypes()} or
     * {@link ImageReaderSpi#getInputTypes()} supported classes.
     *
     * @param validTypes list of supported classes
     * @param type candidate object
     * @return true if candidate object class is an instance of supported class.
     */
    public static boolean isValidType(final Class<?>[] validTypes, final Object type) {
        for (final Class<?> t : validTypes) {
            if (t.isInstance(type)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Convert given input to an input support by given spi.
     * In last case an ImageInputStream will be created.
     *
     * @param spi {@link ImageReaderSpi} reader spi
     * @param input candidate object
     * @return Input object if directly supported by ImageReaderSpi or input object converted into
     * an {@link org.geotoolkit.coverage.io.ImageCoverageReader}
     * @throws IOException if {@link org.geotoolkit.coverage.io.ImageCoverageReader} fail
     */
    public static Object toSupportedInput(ImageReaderSpi spi, Object input) throws IOException{
        final Class[] supportedTypes = spi.getInputTypes();
        Object in = null;

        //try to reuse input if it's supported
        for(Class type : supportedTypes){
            if(type.isInstance(input)){
                in = input;
                break;
            }
        }

        //use default image stream if necessary
        if (in == null) {
            //may throw IOException
            in = CoverageIO.createImageInputStream(input);
        }

        return in;
    }

    /**
     * Returns the mime type matching the extension of an image file.
     * For example, for a file "my_image.png" it will return "image/png", in most cases.
     *
     * @param extension The extension of an image file.
     * @return The mime type for the extension specified.
     *
     * @throws IIOException if no image reader are able to handle the extension given.
     */
    public static String fileExtensionToMimeType(final String extension) throws IIOException {
        final Iterator<ImageReaderSpi> readers = getImageReaderSpis();
        while (readers.hasNext()) {
            final ImageReaderSpi reader = readers.next();
            final String[] suffixes = reader.getFileSuffixes();
            for (String suffixe : suffixes) {
                if (extension.equalsIgnoreCase(suffixe)) {
                    final String[] mimeTypes = reader.getMIMETypes();
                    if (mimeTypes != null && mimeTypes.length > 0) {
                        return mimeTypes[0];
                    }
                }
            }
        }
        throw new IIOException("No available image reader able to handle the extension specified: "+ extension);
    }

    /**
     * Returns the mime type matching the format name of an image file.
     * For example, for a format name "png" it will return "image/png", in most cases.
     *
     * @param formatName name The format name of an image file.
     * @return The mime type for the format name specified.
     *
     * @throws IIOException if no image reader are able to handle the format name given.
     */
    public static String formatNameToMimeType(final String formatName) throws IIOException {
        final Iterator<ImageReaderSpi> readers = getImageReaderSpis();
        while (readers.hasNext()) {
            final ImageReaderSpi reader = readers.next();
            final String[] formats = reader.getFormatNames();
            for (String format : formats) {
                if (formatName.equalsIgnoreCase(format)) {
                    final String[] mimeTypes = reader.getMIMETypes();
                    if (mimeTypes != null && mimeTypes.length > 0) {
                        return mimeTypes[0];
                    }
                }
            }
        }
        throw new IIOException("No available image reader able to handle the format name specified: "+ formatName);
    }

    /**
     * Returns the format name matching the mime type of an image file.
     * For example, for a mime type "image/png" it will return "png", in most cases.
     *
     * @param mimeType The mime type of an image file.
     * @return The format name for the mime type specified.
     *
     * @throws IIOException if no image reader are able to handle the mime type given.
     */
    public static String mimeTypeToFormatName(final String mimeType) throws IIOException {
        final Iterator<ImageReaderSpi> readers = getImageReaderSpis();
        while (readers.hasNext()) {
            final ImageReaderSpi reader = readers.next();
            final String[] mimes = reader.getMIMETypes();
            for (String mime : mimes) {
                if (mimeType.equalsIgnoreCase(mime)) {
                    final String[] formats = reader.getFormatNames();
                    if (formats != null && formats.length > 0) {
                        return formats[0];
                    }
                }
            }
        }
        throw new IIOException("No available image reader able to handle the mime type specified: "+ mimeType);
    }

    /**
     * Commodity method to load image reader SPIs.
     * Note that we use {@link IIORegistry#getDefaultInstance() default registry instance}, because since Java 17, using
     * the alternative {@link IIORegistry#lookupProviders(Class)} does not return image readers from java.desktop module.
     * The reason is maybe because java.desktop module does not export/provides SPIs as public API. For a reason unknown,
     * the default IIORegistry loads them anyway. That's what we want for now.
     *
     * @return An iterator loading image readers from {@link IIORegistry#getDefaultInstance()}.
     */
    private static Iterator<ImageReaderSpi> getImageReaderSpis() {
        return IIORegistry.getDefaultInstance().getServiceProviders(ImageReaderSpi.class, true);
    }
}

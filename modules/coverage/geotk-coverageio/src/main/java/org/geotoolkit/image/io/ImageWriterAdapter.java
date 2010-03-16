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

import java.net.URI;
import java.net.URL;
import java.io.File;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.event.IIOWriteProgressListener;

import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.lang.Decorator;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.XArrays;


/**
 * Base class for writers which delegate most of their work to an other {@link ImageWriter}.
 * This is used for reusing an existing image writer while adding some extra functionalities,
 * like encoding geographic information found in {@link SpatialMetadata}.
 * <p>
 * The wrapped image writer is called the {@linkplain #main} writer. The output given to that
 * writer is determined by the {@link #createOutput(String)} method - it may or may not be the
 * same output than the one given to this {@code ImageWriterAdapter}.
 * <p>
 * The amount of methods declared in this class is large, but the only new methods are:
 * <p>
 * <ul>
 *   <li>{@link #createOutput(String)}</li>
 *   <li>{@link #initialize()}</li>
 *   <li>{@link #writeStreamMetadata(IIOMetadata)}</li>
 *   <li>{@link #writeImageMetadata(IIOMetadata, int, ImageWriteParam)}</li>
 * </ul>
 * <p>
 * All other methods override existing methods declared in parent classes, mostly
 * {@link ImageWriter} and {@link SpatialImageWriter}. The default implementation of
 * most methods delegate directly to the {@linkplain #main} writer.
 *
 * {@section Example}
 * The <cite>World File</cite> format is composed of a classical image file (usually with the
 * {@code ".tiff"}, {@code ".jpg"} or {@code ".png"} extension) together with a small text file
 * containing geolocalization information (often with the {@code ".tfw"} extension) and an other
 * small text file containing projection information ({@code ".prj"} extension). This
 * {@code ImageWriterAdapter} class can be used for wrapping a TIFF image writer, augmented with
 * the formatting of TFW and PRJ files. The information written in those files are fetched from
 * the {@link SpatialMetadata} object given to the {@link #write(IIOImage)} method in this class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @see ImageReaderAdapter
 *
 * @since 3.07
 * @module
 */
@Decorator(ImageWriter.class)
public abstract class ImageWriterAdapter extends SpatialImageWriter {
    /**
     * The writer to use for writing the pixel values.
     */
    protected final ImageWriter main;

    /**
     * The output types accepted by both the {@linkplain #main} writer and this writer.
     * If the output types are unspecified, then this field is {@code null}.
     */
    private final Class<?>[] outputTypes;

    /**
     * {@code true} if the {@link #main} writer accepts outputs of kind {@link ImageOutputStream}.
     */
    private final boolean acceptStream;

    /**
     * Constructs a new image writer. The provider argument is mandatory for this constructor.
     * If the provider is unknown, use the next constructor below instead.
     *
     * @param  provider The {@link ImageWriterSpi} that is constructing this object.
     * @throws IOException If an error occured while creating the {@linkplain #main} writer.
     */
    protected ImageWriterAdapter(final Spi provider) throws IOException {
        this(provider, provider.createWriterInstance());
    }

    /**
     * Constructs a new image writer wrapping the given writer.
     *
     * @param provider The {@link ImageWriterSpi} that is constructing this object, or {@code null}.
     * @param main The writer to use for writing the pixel values.
     */
    protected ImageWriterAdapter(final Spi provider, final ImageWriter main) {
        super(provider);
        this.main = main;
        Spi.ensureNonNull("main", main);
        if (provider != null) {
            outputTypes  = provider.getMainTypes();
            acceptStream = provider.acceptStream;
        } else {
            outputTypes  = null;
            acceptStream = true;
        }
    }

    /**
     * Creates the output to give to the {@linkplain #main} writer, or to an other writer identified
     * by the {@code writerID} argument. The default {@code ImageWriterAdapter} implementation
     * invokes this method with a {@code writerID} argument value equals to {@code "main"} when
     * the {@linkplain #main} writer needs to be used for the first time. This may happen at any
     * time after the {@link #setOutput(Object) setOutput} method has been invoked.
     * <p>
     * The only {@code writerID} argument value accepted by the default implementation is
     * {@code "main"}; any other argument value causes a {@code null} value to be returned.
     * However subclasses can override this method for supporting more writer types. The
     * table below summarizes a few types supported by this writer and different subclasses:
     * <p>
     * <table border="1">
     *   <tr bgcolor="lightblue">
     *     <th>Writer type</th>
     *     <th>Defined by</th>
     *     <th>Usage</th>
     *   </tr><tr>
     *     <td>&nbsp;{@code "main"}&nbsp;</td>
     *     <td>&nbsp;{@code ImageWriterAdapter}&nbsp;</td>
     *     <td>&nbsp;The output to be given to the {@linkplain #main} writer.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@code "tfw"}&nbsp;</td>
     *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.WorldFileImageWriter}&nbsp;</td>
     *     <td>&nbsp;The output for the <cite>World File</cite>.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@code "prj"}&nbsp;</td>
     *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.WorldFileImageWriter}&nbsp;</td>
     *     <td>&nbsp;The output for the <cite>Map Projection</cite> file.&nbsp;</td>
     *   </tr>
     * </table>
     * <p>
     * The default implementation first checks if the {@linkplain #main} writer accepts directly
     * the {@linkplain #output output} of this writer. If so, then that output is returned with
     * no change. Otherwise the output is wrapped in an {@linkplain ImageOutputStream}, which is
     * returned. The output stream assigned to the {@linkplain #main} writer will be closed by
     * the {@link #close()} method.
     *
     * @param  writerID Identifier of the writer for which an output is needed.
     * @return The output to give to the identified writer, or {@code null} if this method can
     *         not create such output.
     * @throws IllegalStateException if the {@linkplain #output output} has not been set.
     * @throws IOException If an error occured while creating the output for the writer.
     *
     * @see ImageReaderAdapter#createInput(String)
     */
    protected Object createOutput(final String writerID) throws IllegalStateException, IOException {
        final Object output = this.output;
        if (output == null) {
            throw new IllegalStateException(getErrorResources().getString(Errors.Keys.NO_IMAGE_OUTPUT));
        }
        if (!"main".equalsIgnoreCase(writerID)) {
            return null;
        }
        if (outputTypes != null) {
            for (final Class<?> type : outputTypes) {
                if (type.isInstance(output)) {
                    return output;
                }
            }
        }
        return acceptStream ? ImageIO.createImageOutputStream(output) : null;
    }

    /**
     * Invoked automatically when the {@linkplain #main} writer has been given a new output.
     * When this method is invoked, the main writer output has alwrity been set to the value
     * returned by <code>{@linkplain #createOutput(String) createOutput}("main")</code>.
     * <p>
     * The default implementation does nothing. Subclasses can override this method
     * for performing additional initialization.
     *
     * @throws IOException If an error occured while initializing the main writer.
     */
    protected void initialize() throws IOException {
    }

    /**
     * Ensures that the output of the {@linkplain #main} writer is initialized.
     * If not, this method tries to create an informative error message.
     */
    private void ensureOutputInitialized() throws IOException {
        if (main.getOutput() == null) {
            final Object mainOutput = createOutput("main");
            if (mainOutput == null) {
                throw new InvalidImageStoreException(getErrorResources(), output, outputTypes, true);
            }
            main.setOutput(mainOutput);
            initialize();
        }
    }

    /**
     * Returns a metadata object containing default values for encoding a stream of images.
     * The default implementation returns the union of the metadata formats declared by the
     * {@linkplain #main} writer, and the Geotk {@link SpatialMetadataFormat#STREAM STREAM}
     * format.
     * <p>
     * Subclasses can override the {@link #writeStreamMetadata(IIOMetadata)} method
     * for writing those metadata to the output.
     */
    @Override
    public SpatialMetadata getDefaultStreamMetadata(final ImageWriteParam param) {
        return new SpatialMetadata(SpatialMetadataFormat.STREAM, this,
                main.getDefaultStreamMetadata(param));
    }

    /**
     * Returns a metadata object containing default values for encoding an image of the given type.
     * The default implementation returns the union of the metadata formats declared by the
     * {@linkplain #main} writer, and the Geotk {@link SpatialMetadataFormat#IMAGE IMAGE} format.
     * <p>
     * Subclasses can override the {@link #writeImageMetadata(IIOMetadata, int, ImageWriteParam)}
     * method for writing those metadata to the output.
     */
    @Override
    public SpatialMetadata getDefaultImageMetadata(final ImageTypeSpecifier imageType,
                                                   final ImageWriteParam    param)
    {
        return new SpatialMetadata(SpatialMetadataFormat.IMAGE, this,
                main.getDefaultImageMetadata(imageType, param));
    }

    /**
     * Invoked by the {@code write} methods when stream metadata needs to be written.
     * The metadata written by the {@linkplain #main} writer are not concerned by this method.
     * <p>
     * The default implementation does nothing. Subclasses can override this method for
     * writing spatial metadata.
     *
     * @param  metadata    The stream metadata, or {@code null} if none.
     * @throws IOException If an error occured while writing the metadata.
     *
     * @see org.geotoolkit.image.io.metadata.MetadataHelper
     */
    protected void writeStreamMetadata(IIOMetadata metadata) throws IOException {
    }

    /**
     * Invoked by the {@code write} methods when image metadata needs to be written.
     * The metadata written by the {@linkplain #main} writer are not concerned by this method.
     * <p>
     * The default implementation does nothing. Subclasses can override this method for
     * writing spatial metadata.
     *
     * @param  metadata    The stream metadata, or {@code null} if none.
     * @param  imageIndex  The index of the image being written.
     * @param  param       The user-specified parameter, or {@code null} if none.
     * @throws IOException If an error occured while writing the metadata.
     *
     * @see org.geotoolkit.image.io.metadata.MetadataHelper
     */
    protected void writeImageMetadata(IIOMetadata metadata, int imageIndex, ImageWriteParam param)
            throws IOException
    {
    }

    /**
     * Appends a complete image stream containing a single image and associated stream and image
     * metadata and thumbnails to the output. The default implementation performs the following
     * steps:
     * <p>
     * <ol>
     *   <li>Ensures that the {@linkplain #main} writer has been
     *       {@linkplain #initialize() initialized}</li>
     *   <li>Invokes {@link #writeStreamMetadata(IIOMetadata)} with the given
     *       {@code streamMetadata} argument.</li>
     *   <li>Invokes {@link #writeImageMetadata(IIOMetadata, int, ImageWriteParam)}
     *       with the metadata obtained from the {@code image} argument.</li>
     *   <li>Delegates the writing of pixel values to the {@linkplain #main} writer.</li>
     * </ol>
     */
    @Override
    public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param)
            throws IOException
    {
        ensureOutputInitialized();
        writeStreamMetadata(streamMetadata);
        writeImageMetadata(image.getMetadata(), 0, param);
        main.write(streamMetadata, image, param);
    }

    /**
     * Returns true if the methods that take an {@link IIOImage} parameter are capable of dealing
     * with a {@link Raster}. The default implementation delegates to the {@linkplain #main} writer.
     * No output needs to be set for this method.
     */
    @Override
    public boolean canWriteRasters() {
        return main.canWriteRasters();
    }

    /**
     * Returns {@code true} if the writer is able to append an image to an image stream that
     * alwrity contains header information and possibly prior images. The default implementation
     * delegates to the {@linkplain #main} writer. No output needs to be set for this method.
     */
    @Override
    public boolean canWriteSequence() {
        return main.canWriteSequence();
    }

    /**
     * Prepares a stream to accept a series of subsequent {@link #writeToSequence writeToSequence}
     * calls. The default implementation performs the following steps:
     * <p>
     * <ol>
     *   <li>Ensures that the {@linkplain #main} writer has been
     *       {@linkplain #initialize() initialized}</li>
     *   <li>Invokes {@link #writeStreamMetadata(IIOMetadata)} with the given
     *       {@code streamMetadata} argument.</li>
     *   <li>Delegates to the {@linkplain #main} writer.</li>
     * </ol>
     */
    @Override
    public void prepareWriteSequence(IIOMetadata streamMetadata) throws IOException {
        ensureOutputInitialized();
        writeStreamMetadata(streamMetadata);
        main.prepareWriteSequence(streamMetadata);
    }

    /**
     * Appends a single image and possibly associated metadata to the output.
     * The default implementation performs the following steps:
     * <p>
     * <ol>
     *   <li>Invokes {@link #writeImageMetadata(IIOMetadata, int, ImageWriteParam)}
     *       with the metadata obtained from the {@code image} argument.</li>
     *   <li>Delegates to the {@linkplain #main} writer.</li>
     * </ol>
     */
    @Override
    public void writeToSequence(IIOImage image, ImageWriteParam param) throws IOException {
        writeImageMetadata(image.getMetadata(), imageIndex, param);
        main.writeToSequence(image, param);
        imageIndex++;
    }

    /**
     * Completes the writing of a sequence of images begun with {@link #prepareWriteSequence
     * prepareWriteSequence}. The default implementation delegates to the {@linkplain #main}
     * writer.
     */
    @Override
    public void endWriteSequence() throws IOException {
        main.endWriteSequence();
    }

    /**
     * Returns {@code true} if the writer allows pixels of the given image to be replaced
     * using the {@code replacePixels} methods. The default implementation ensures that the
     * {@linkplain #main} writer has been {@linkplain #initialize() initialized}, then
     * delegates to that writer.
     */
    @Override
    public boolean canReplacePixels(int imageIndex) throws IOException {
        ensureOutputInitialized();
        return main.canReplacePixels(imageIndex);
    }

    /**
     * Prepares the writer to handle a series of calls to the replacePixels methods.
     * The default implementation ensures that the {@linkplain #main} writer has been
     * {@linkplain #initialize() initialized}, then delegates to that writer.
     */
    @Override
    public void prepareReplacePixels(int imageIndex, Rectangle region) throws IOException {
        ensureOutputInitialized();
        main.prepareReplacePixels(imageIndex, region);
    }

    /**
     * Replaces a portion of an image alwrity present in the output with a portion of the
     * given image. The default implementation delegates to the {@linkplain #main} writer.
     */
    @Override
    public void replacePixels(RenderedImage image, ImageWriteParam param) throws IOException {
        main.replacePixels(image, param);
    }

    /**
     * Replaces a portion of an image alwrity present in the output with a portion of the
     * given raster. The default implementation delegates to the {@linkplain #main} writer.
     */
    @Override
    public void replacePixels(Raster raster, ImageWriteParam param) throws IOException {
        main.replacePixels(raster, param);
    }

    /**
     * Terminates a sequence of calls to {@code replacePixels}. The default implementation
     * delegates to the {@linkplain #main} writer.
     */
    @Override
    public void endReplacePixels() throws IOException {
        main.endReplacePixels();
    }

    /**
     * Returns the number of thumbnails suported by the format being written, or -1 if unknown.
     * The default implementation delegates to the {@linkplain #main} writer. No output needs to
     * be set for this method.
     */
    @Override
    public int getNumThumbnailsSupported(ImageTypeSpecifier imageType, ImageWriteParam param,
            IIOMetadata streamMetadata, IIOMetadata imageMetadata)
    {
        return main.getNumThumbnailsSupported(imageType, param, streamMetadata, imageMetadata);
    }

    /**
     * Returns the legal size ranges for thumbnail images as they will be encoded in the output
     * file or stream. The default implementation delegates to the {@linkplain #main} writer.
     * No output needs to be set for this method.
     */
    @Override
    public Dimension[] getPreferredThumbnailSizes(ImageTypeSpecifier imageType,
            ImageWriteParam param, IIOMetadata streamMetadata, IIOMetadata imageMetadata)
    {
        return main.getPreferredThumbnailSizes(imageType, param, streamMetadata, imageMetadata);
    }

    /**
     * Returns the locales that may be used to localize warning listeners.
     * The default implementation delegates to the {@linkplain #main} writer.
     * No output needs to be set for this method.
     */
    @Override
    public Locale[] getAvailableLocales() {
        return main.getAvailableLocales();
    }

    /**
     * Returns the locale used to localize warning listeners.
     * The default implementation delegates to the {@linkplain #main} writer.
     * No output needs to be set for this method.
     */
    @Override
    public Locale getLocale() {
        return main.getLocale();
    }

    /**
     * Sets the locale used to localize warning listeners.
     * The default implementation delegates to the {@linkplain #main} writer.
     * No output needs to be set for this method.
     */
    @Override
    public void setLocale(final Locale locale) {
        main.setLocale(locale);
        this.locale = locale; // Bypass the check for available locale.
    }

    /**
     * Adds the given listener to the list of registered warning listeners.
     * Thie listener is added both to this writer and to the {@linkplain #main} writer.
     */
    @Override
    public void addIIOWriteWarningListener(final IIOWriteWarningListener listener) {
        super.addIIOWriteWarningListener(listener);
        main .addIIOWriteWarningListener(listener);
    }

    /**
     * Removes the given listener from the list of registered warning listeners.
     */
    @Override
    public void removeIIOWriteWarningListener(final IIOWriteWarningListener listener) {
        super.removeIIOWriteWarningListener(listener);
        main .removeIIOWriteWarningListener(listener);
    }

    /**
     * Removes all currently registered warning listeners.
     */
    @Override
    public void removeAllIIOWriteWarningListeners() {
        super.removeAllIIOWriteWarningListeners();
        main .removeAllIIOWriteWarningListeners();
    }

    /**
     * Adds the given listener to the list of registered progress listeners. This method
     * adds the listener only to the {@linkplain #main} writer, not to this writer, in
     * order to ensure that progress methods are invoked only once.
     */
    @Override
    public void addIIOWriteProgressListener(final IIOWriteProgressListener listener) {
        main.addIIOWriteProgressListener(listener);
    }

    /**
     * Removes the given listener from the list of registered progress listeners.
     */
    @Override
    public void removeIIOWriteProgressListener(final IIOWriteProgressListener listener) {
        super.removeIIOWriteProgressListener(listener); // As a safety.
        main .removeIIOWriteProgressListener(listener);
    }

    /**
     * Removes all currently registered progress listeners.
     */
    @Override
    public void removeAllIIOWriteProgressListeners() {
        super.removeAllIIOWriteProgressListeners(); // As a safety.
        main .removeAllIIOWriteProgressListeners();
    }

    /**
     * Requests that any current write operation be aborted. The default implementation delegates
     * to both the {@linkplain #main} writer and to the super-class method.
     */
    @Override
    public void abort() {
        super.abort();
        main.abort();
    }

    /**
     * Restores the {@code ImageWriter} to its initial state. The default implementation
     * delegates to both the {@linkplain #main} writer and to the super-class method.
     */
    @Override
    public void reset() {
        super.reset();
        main.reset();
    }

    /**
     * Allows any resources held by this object to be released. The default implementation
     * delegates to both the {@linkplain #main} writer and to the super-class method.
     */
    @Override
    public void dispose() {
        super.dispose();
        main.dispose();
    }

    /**
     * Closes the output stream created by {@link #createOutput(String)}. This method does nothing
     * if the output used by the {@linkplain #main} writer is the one given by the user to the
     * {@link #setOutput(Object) setOutput} method. Otherwise, if the output of the main writer
     * is an instance of {@link ImageOutputStream} or {@link Closeable}, then it is closed.
     * <p>
     * This method is invoked automatically by {@link #setOutput(Object) setOutput(...)},
     * {@link #reset() reset()}, {@link #dispose() dispose()} or {@link #finalize()} methods
     * and doesn't need to be invoked explicitly. It has protected access only in order to allow
     * overriding by subclasses. Overriding methods shall make sure that {@code super.close()}
     * is invoked even in case of failure.
     *
     * @throws IOException if an error occured while closing the stream.
     */
    @Override
    protected void close() throws IOException {
        super.close();
        final Object mainOutput = main.getOutput();
        main.setOutput(null);
        if (mainOutput != null && mainOutput != output) {
            IOUtilities.close(mainOutput);
        }
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
     * Service provider interface (SPI) for {@link ImageWriterAdapter}s. The constructor of this
     * class initializes the {@link ImageWriterSpi#outputTypes} field to types that can represent
     * a filename, like {@link File} or {@link URL} (see the table below for the complete list),
     * rather than the usual {@linkplain #STANDARD_OUTPUT_TYPE standard output type}. The other
     * fields ({@link #names names}, {@link #suffixes suffixes}, {@link #MIMETypes MIMETypes})
     * are set to the same values than the wrapped provider.
     * <p>
     * Because the names are the same by default, an ordering needs to be etablished between this
     * provider and the wrapped one. By default this implementation conservatively gives precedence
     * to the original provider. Subclasses shall override the
     * {@link #onRegistration(ServiceRegistry, Class)} method if they want a different ordering.
     * <p>
     * The table below summarizes the initial values.
     * Those values can be modified by subclass constructors.
     * <p>
     * <table border="1">
     *   <tr bgcolor="lightblue">
     *     <th>Field</th>
     *     <th>Value</th>
     *   </tr><tr>
     *     <td>&nbsp;{@link #names}&nbsp;</td>
     *     <td>&nbsp;Same values than the {@linkplain #main} provider.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #suffixes}&nbsp;</td>
     *     <td>&nbsp;Same values than the {@linkplain #main} provider.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #MIMETypes}&nbsp;</td>
     *     <td>&nbsp;Same values than the {@linkplain #main} provider.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #outputTypes}&nbsp;</td>
     *     <td>&nbsp;{@link String}, {@link File}, {@link URI}, {@link URL},
     *         {@link ImageOutputStream}&nbsp;</td>
     * </tr>
     * </table>
     * <p>
     * It is up to subclass constructors to initialize all other instance variables
     * in order to provide working versions of every methods.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.10
     *
     * @see ImageReaderAdapter.Spi
     *
     * @since 3.07
     * @module
     */
    protected static abstract class Spi extends SpatialImageWriter.Spi {
        /**
         * List of legal input and output types for {@link ImageWriterAdapter}.
         * The {@link ImageOutputStream} type is mandatory even if the adapter
         * can't use it, because this type is created by {@link javax.imageio}
         * package and we have no way to filter {@code ImageWriter} by output
         * stream.
         */
        private static final Class<?>[] TYPES;
        static {
            final int n = ImageReaderAdapter.Spi.TYPES.length;
            TYPES = Arrays.copyOf(ImageReaderAdapter.Spi.TYPES, n + 1);
            TYPES[n] = ImageOutputStream.class;
        }

        /**
         * The provider of the writers to use for writing the pixel values.
         * This is the provider specified at the construction time.
         */
        protected final ImageWriterSpi main;

        /**
         * {@code true} if the {@link #main} provider accepts outputs of kind {@link ImageOutputStream}.
         */
        final boolean acceptStream;

        /**
         * Creates an {@code ImageWriterAdapter.Spi} wrapping the given provider. The fields are
         * initialized as documented in the <a href="#skip-navbar_top">class javadoc</a>. It is up
         * to the subclass to initialize all other instance variables in order to provide working
         * versions of all methods.
         * <p>
         * For efficienty reasons, the {@code outputTypes} field is initialized to a shared array.
         * Subclasses can assign new arrays, but should not modify the default array content.
         *
         * @param main The provider of the writers to use for writing the pixel values.
         */
        protected Spi(final ImageWriterSpi main) {
            ensureNonNull("main", main);
            this.main   = main;
            names       = main.getFormatNames();
            suffixes    = main.getFileSuffixes();
            MIMETypes   = main.getMIMETypes();
            outputTypes = TYPES;
            supportsStandardStreamMetadataFormat = main.isStandardStreamMetadataFormatSupported();
            supportsStandardImageMetadataFormat  = main.isStandardImageMetadataFormatSupported();
            nativeStreamMetadataFormatClassName  = main.getNativeStreamMetadataFormatName();
            nativeImageMetadataFormatClassName   = main.getNativeImageMetadataFormatName();
            extraStreamMetadataFormatClassNames  = XArrays.concatenate(
                    main.getExtraStreamMetadataFormatNames(), ImageReaderAdapter.Spi.EXTRA_METADATA);
            extraImageMetadataFormatClassNames  = XArrays.concatenate(
                    main.getExtraImageMetadataFormatNames(), ImageReaderAdapter.Spi.EXTRA_METADATA);
            boolean acceptStream = false;
            for (final Class<?> type : main.getOutputTypes()) {
                if (type.isAssignableFrom(ImageOutputStream.class)) {
                    acceptStream = true;
                    break;
                }
            }
            this.acceptStream = acceptStream;
        }

        /**
         * Creates a provider which will use the given format for writing pixel values.
         * This is a convenience constructor for the above constructor with a provider
         * fetched from the given format name.
         *
         * @param  format The name of the provider to use for writing the pixel values.
         * @throws IllegalArgumentException If no provider is found for the given format.
         */
        protected Spi(final String format) throws IllegalArgumentException {
            this(Formats.getWriterByFormatName(format, Spi.class));
        }

        /**
         * Makes sure an argument is non-null.
         *
         * @param  name   Argument name.
         * @param  object User argument.
         * @throws NullArgumentException if {@code object} is null.
         */
        static void ensureNonNull(String name, Object object) throws NullArgumentException {
            if (object == null) {
                throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
            }
        }

        /**
         * Returns the output types accepted by the {@linkplain #main} provider which are also
         * accepted by this provider, or {@code null} if none.
         */
        final Class<?>[] getMainTypes() {
            return ImageReaderAdapter.Spi.getMainTypes(outputTypes, main.getOutputTypes());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IIOMetadataFormat getStreamMetadataFormat(final String formatName) {
            if (SpatialMetadataFormat.FORMAT_NAME.equals(formatName) && isSpatialMetadataSupported(true)) {
                return SpatialMetadataFormat.STREAM;
            }
            return main.getStreamMetadataFormat(formatName);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IIOMetadataFormat getImageMetadataFormat(final String formatName) {
            if (SpatialMetadataFormat.FORMAT_NAME.equals(formatName) && isSpatialMetadataSupported(false)) {
                return SpatialMetadataFormat.IMAGE;
            }
            return main.getImageMetadataFormat(formatName);
        }

        /**
         * Returns {@code true} if the format that this writer outputs preserves pixel data
         * bit-accurately. The default implementation delegates to the {@linkplain #main}
         * provider.
         */
        @Override
        public boolean isFormatLossless() {
            return main.isFormatLossless();
        }

        /**
         * Returns {@code true} if the writer implementation associated with this service provider
         * is able to encode an image with the given layout. The default implementation delegates
         * to the {@linkplain #main} provider.
         */
        @Override
        public boolean canEncodeImage(final ImageTypeSpecifier type) {
            return main.canEncodeImage(type);
        }

        /**
         * Returns {@code true} if the writer implementation associated with this service provider
         * is able to encode the given image. The default implementation delegates to the
         * {@linkplain #main} provider.
         */
        @Override
        public boolean canEncodeImage(final RenderedImage im) {
            return main.canEncodeImage(im);
        }

        /**
         * A callback that will be called exactly once after the {@code Spi} class has been
         * instantiated and registered in a {@code ServiceRegistry}. The default implementation
         * conservatively gives precedence to the {@linkplain #main} provider, using the code
         * below:
         *
         * {@preformat java
         *     registry.setOrdering(category, main, this);
         * }
         *
         * Subclasses should override this method if they want to specify a different ordering.
         *
         * @see ServiceRegistry#setOrdering(Class, Object, Object)
         */
        @Override
        @SuppressWarnings({"unchecked","rawtypes"})
        public void onRegistration(final ServiceRegistry registry, final Class<?> category) {
            registry.setOrdering((Class) category, main, this);
        }
    }
}

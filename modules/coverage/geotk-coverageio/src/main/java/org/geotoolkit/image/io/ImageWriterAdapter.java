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

import java.net.URI;
import java.net.URL;
import java.io.File;
import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
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
import org.geotoolkit.util.converter.Classes;

import static org.geotoolkit.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Base class for writers which delegate most of their work to an other {@link ImageWriter}.
 * This is used for reusing an existing image writer while adding some extra functionalities,
 * like encoding geographic information found in {@link SpatialMetadata}.
 * <p>
 * The wrapped image writer is called the {@linkplain #main} writer. The output given to that
 * writer is determined by the {@link #createOutput(String)} method - it may or may not be the
 * same output than the one given to this {@code ImageWriterAdapter} {@link #setOutput(Object)
 * setOutput(Object)} method.
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
 * @version 3.18
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
     * @throws IOException If an error occurred while creating the {@linkplain #main} writer.
     */
    protected ImageWriterAdapter(final Spi provider) throws IOException {
        this(provider, provider.main.createWriterInstance());
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
        ensureNonNull("main", main);
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
     * @throws IOException If an error occurred while creating the output for the writer.
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
        ImageOutputStream out = null;
        if (acceptStream) {
            out = ImageIO.createImageOutputStream(output);
            if (out == null) {
                final Object alternate = IOUtilities.tryToFile(output);
                if (alternate != output) {
                    out = ImageIO.createImageOutputStream(alternate);
                }
            }
        }
        return out;
    }

    /**
     * Invoked automatically when the {@linkplain #main} writer has been given a new output.
     * When this method is invoked, the main writer output has already been set to the value
     * returned by <code>{@linkplain #createOutput(String) createOutput}("main")</code>.
     * <p>
     * The default implementation does nothing. Subclasses can override this method
     * for performing additional initialization.
     *
     * @throws IOException If an error occurred while initializing the main writer.
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
     * Returns a default parameter object appropriate for this format.
     */
    @Override
    public SpatialImageWriteParam getDefaultWriteParam() {
        final ImageWriteParam param = main.getDefaultWriteParam();
        if (param instanceof SpatialImageWriteParam) {
            return (SpatialImageWriteParam) param;
        }
        return new ImageWriteParamAdapter(this, main.getDefaultWriteParam());
    }

    /**
     * If the given parameter object is an instance of {@link ImageWriteParamAdapter},
     * returns the wrapped parameters.
     *
     * @since 3.18
     */
    private static ImageWriteParam unwrap(ImageWriteParam param) {
        if (param instanceof ImageWriteParamAdapter) {
            param = ((ImageWriteParamAdapter) param).param;
        }
        return param;
    }

    /**
     * Returns a metadata object containing default values for encoding a stream of images.
     * The default implementation returns the union of the metadata formats declared by the
     * {@linkplain #main} writer, and the Geotk
     * {@linkplain SpatialMetadataFormat#getStreamInstance(String) stream metadata format}.
     * <p>
     * Subclasses can override the {@link #writeStreamMetadata(IIOMetadata)} method
     * for writing those metadata to the output.
     */
    @Override
    public SpatialMetadata getDefaultStreamMetadata(final ImageWriteParam param) {
        final IIOMetadata metadata = main.getDefaultStreamMetadata(unwrap(param));
        if (metadata == null && !isSpatialMetadataSupported(true)) {
            return null;
        }
        return new SpatialMetadata(SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME), this, metadata);
    }

    /**
     * Returns a metadata object containing default values for encoding an image of the given type.
     * The default implementation returns the union of the metadata formats declared by the
     * {@linkplain #main} writer, and the Geotk
     * {@linkplain SpatialMetadataFormat#getImageInstance(String) image metadata format}.
     * <p>
     * Subclasses can override the {@link #writeImageMetadata(IIOMetadata, int, ImageWriteParam)}
     * method for writing those metadata to the output.
     */
    @Override
    public SpatialMetadata getDefaultImageMetadata(final ImageTypeSpecifier imageType,
                                                   final ImageWriteParam    param)
    {
        final IIOMetadata metadata = main.getDefaultImageMetadata(imageType, unwrap(param));
        if (metadata == null && !isSpatialMetadataSupported(false)) {
            return null;
        }
        return new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME), this, metadata);
    }

    /**
     * Invoked by the {@code write} methods when stream metadata needs to be written.
     * The metadata written by the {@linkplain #main} writer are not concerned by this method.
     * <p>
     * The default implementation does nothing. Subclasses can override this method for
     * writing spatial metadata.
     *
     * @param  metadata    The stream metadata, or {@code null} if none.
     * @throws IOException If an error occurred while writing the metadata.
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
     * @throws IOException If an error occurred while writing the metadata.
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
        main.write(streamMetadata, image, unwrap(param));
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
     * already contains header information and possibly prior images. The default implementation
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
        main.writeToSequence(image, unwrap(param));
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
     * Replaces a portion of an image already present in the output with a portion of the
     * given image. The default implementation delegates to the {@linkplain #main} writer.
     */
    @Override
    public void replacePixels(RenderedImage image, ImageWriteParam param) throws IOException {
        main.replacePixels(image, unwrap(param));
    }

    /**
     * Replaces a portion of an image already present in the output with a portion of the
     * given raster. The default implementation delegates to the {@linkplain #main} writer.
     */
    @Override
    public void replacePixels(Raster raster, ImageWriteParam param) throws IOException {
        main.replacePixels(raster, unwrap(param));
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
        return main.getNumThumbnailsSupported(imageType, unwrap(param), streamMetadata, imageMetadata);
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
        return main.getPreferredThumbnailSizes(imageType, unwrap(param), streamMetadata, imageMetadata);
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
     * @throws IOException if an error occurred while closing the stream.
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
     * a filename, like {@link File} or {@link URL}, rather than the usual
     * {@linkplain #STANDARD_OUTPUT_TYPE standard output type}. The {@link #names names} and
     * {@link #MIMETypes MIMETypes} fields are set to the values of the wrapped provider,
     * suffixed with the string given to the {@link #addFormatNameSuffix(String)} method.
     * <p>
     * <b>Example:</b> An {@code ImageWriterAdapter} wrapping the {@code "tiff"} image writer
     * with the {@code "-wf"} suffix will have the {@code "tiff-wf"} format name and the
     * {@code "image/x-tiff-wf"} MIME type.
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
     *     <td>&nbsp;Same values than the {@linkplain #main} provider, suffixed by the given string.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #suffixes}&nbsp;</td>
     *     <td>&nbsp;Same values than the {@linkplain #main} provider.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #MIMETypes}&nbsp;</td>
     *     <td>&nbsp;Same values than the {@linkplain #main} provider, suffixed by the given string.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #outputTypes}&nbsp;</td>
     *     <td>&nbsp;{@link String}, {@link File}, {@link URI}, {@link URL}<!--,
     *         {@link ImageOutputStream} TODO -->&nbsp;</td>
     * </tr>
     * </table>
     * <p>
     * It is up to subclass constructors to initialize all other instance variables
     * in order to provide working versions of every methods.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @see ImageReaderAdapter.Spi
     *
     * @since 3.07
     * @module
     */
    public abstract static class Spi extends SpatialImageWriter.Spi {
        /**
         * List of legal input and output types for {@link ImageWriterAdapter}.
         * The {@link ImageOutputStream} type is mandatory because this type is
         * created by {@link javax.imageio} package and we have no way to filter
         * {@code ImageWriter} by output stream.
         */
        private static final Class<?>[] TYPES = ImageReaderAdapter.Spi.TYPES; //.clone();
//      static {
//TODO      int n = TYPES.length;
//GEOTK-231 TYPES[--n] = ImageOutputStream.class;
//          TYPES[--n] = OutputStream.class;
//      }

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
         * For efficiency reasons, the {@code outputTypes} field is initialized to a shared array.
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
            nativeStreamMetadataFormatName       = main.getNativeStreamMetadataFormatName();
            nativeImageMetadataFormatName        = main.getNativeImageMetadataFormatName();
            extraStreamMetadataFormatNames       = main.getExtraStreamMetadataFormatNames();
            extraImageMetadataFormatNames        = main.getExtraImageMetadataFormatNames();
            acceptStream = Classes.isAssignableTo(ImageOutputStream.class, main.getOutputTypes());
        }

        /**
         * Creates a provider which will use the given format for writing pixel values.
         * This is a convenience constructor for the above constructor with a provider
         * fetched from the given format name.
         *
         * @param  format The name of the provider to use for writing the pixel values.
         * @throws IllegalArgumentException If no provider is found for the given format.
         */
        protected Spi(final String format) {
            this(Formats.getWriterByFormatName(format, Spi.class));
        }

        /**
         * Adds the given suffix to all {@linkplain #names format names} and
         * {@linkplain #MIMETypes MIME types}. Subclasses should invoke this
         * method in their constructor.
         *
         * @param suffix The suffix to append to format names and MIME types.
         *
         * @since 3.20
         */
        protected void addFormatNameSuffix(final String suffix) {
            ImageReaderAdapter.Spi.addFormatNameSuffix(names, MIMETypes, suffix);
        }

        /**
         * Adds the {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#GEOTK_FORMAT_NAME}
         * format to the list of extra stream or metadata format names, if not already present. This
         * method does nothing if the format is already listed as the native or an extra format.
         *
         * @param stream {@code true} for adding the spatial format to the list of stream formats.
         * @param image  {@code true} for adding the spatial format to the list of image formats.
         *
         * @since 3.20
         */
        protected void addSpatialFormat(final boolean stream, final boolean image) {
            if (stream) extraStreamMetadataFormatNames = ImageReaderAdapter.Spi.addSpatialFormat(nativeStreamMetadataFormatName, extraStreamMetadataFormatNames);
            if (image)  extraImageMetadataFormatNames  = ImageReaderAdapter.Spi.addSpatialFormat(nativeImageMetadataFormatName,  extraImageMetadataFormatNames);
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
            if (GEOTK_FORMAT_NAME.equals(formatName) && isSpatialMetadataSupported(true)) {
                return SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME);
            }
            return main.getStreamMetadataFormat(formatName);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IIOMetadataFormat getImageMetadataFormat(final String formatName) {
            if (GEOTK_FORMAT_NAME.equals(formatName) && isSpatialMetadataSupported(false)) {
                return SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME);
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
         *
         * @param type The layout of the image to be written.
         */
        @Override
        public boolean canEncodeImage(final ImageTypeSpecifier type) {
            return main.canEncodeImage(type);
        }

        /**
         * Returns {@code true} if the writer implementation associated with this service provider
         * is able to encode the given image. The default implementation delegates to the
         * {@linkplain #main} provider.
         *
        * @param image The image to be written.
         */
        @Override
        public boolean canEncodeImage(final RenderedImage image) {
            return main.canEncodeImage(image);
        }

        /**
         * Returns the kind of information that this wrapper will add or modify compared to the
         * {@linkplain #main} writer. If this method returns an empty set, then there is no
         * raison to use this adapter instead than the main writer.
         * <p>
         * The default implementation conservatively returns all of the {@link InformationType}
         * enum values. Subclasses should return more accurate information when possible.
         *
         * @param  type The layout of the image to be written.
         * @return The set of information to be written or modified by this adapter.
         *
         * @since 3.20
         */
        public Set<InformationType> getModifiedInformation(final ImageTypeSpecifier type) {
            return ImageReaderAdapter.Spi.INFO;
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
         * The plugin order matter when an {@linkplain javax.imageio.ImageIO#getImageWritersBySuffix(String)
         * image writer is selected by file suffix}, because the {@linkplain #getFileSuffixes() file suffixes}
         * of this adapter are the same than the file suffixes of the {@linkplain #main} provider by default.
         *
         * @see ServiceRegistry#setOrdering(Class, Object, Object)
         */
        @Override
        @SuppressWarnings({"unchecked","rawtypes"})
        public void onRegistration(final ServiceRegistry registry, final Class<?> category) {
            registry.setOrdering((Class) category, main, this);
        }

        /**
         * If the given provider is an instance of {@code ImageWriterAdapter.Spi}, returns the
         * underlying {@linkplain #main} provider. Otherwise returns the given provider unchanged.
         * <p>
         * This method is convenient when the caller is not interested in spatial metadata,
         * in order to ensure that the cost of writing TFW, PRJ or similar files is avoided.
         *
         * @param  spi An image writer provider, or {@code null}.
         * @return The wrapped image writer provider, or {@code null}.
         *
         * @since 3.14
         */
        public static ImageWriterSpi unwrap(ImageWriterSpi spi) {
            while (spi instanceof Spi) {
                spi = ((Spi) spi).main;
            }
            return spi;
        }
    }
}

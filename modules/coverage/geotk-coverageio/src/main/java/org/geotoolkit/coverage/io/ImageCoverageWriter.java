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
package org.geotoolkit.coverage.io;

import java.util.Arrays;
import java.util.Locale;
import java.util.Iterator;
import java.util.Collections;
import java.util.logging.Level;
import java.util.concurrent.CancellationException;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.JAI;
import javax.media.jai.Warp;
import javax.media.jai.PlanarImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.RenderedImageAdapter;
import javax.media.jai.operator.WarpDescriptor;

import org.opengis.util.InternationalString;
import org.opengis.geometry.Envelope;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.InterpolationMethod;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.util.XArrays;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.io.MultidimensionalImageStore;
import org.geotoolkit.image.io.mosaic.MosaicImageWriter;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.referencing.operation.transform.WarpFactory;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.AbstractCoverage;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.image.io.MultidimensionalImageStore.*;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * A {@link GridCoverageWriter} implementation which use an {@link ImageWriter} for writing
 * sample values. This implementation reads the sample values from a {@link RenderedImage},
 * and consequently is targeted toward two-dimensional slices of data.
 * <p>
 * {@code ImageCoverageWriter} basically works as a layer which converts <cite>geodetic
 * coordinates</cite> (for example the region to read) to <cite>pixel coordinates</cite>
 * before to pass them to the wrapped {@code ImageWriter}, and conversely: from pixel
 * coordinates to geodetic coordinates. The later conversion is called "<cite>grid to CRS</cite>"
 * and is determined from the {@link GridGeometry2D} provided by the {@link GridCoverage}.
 *
 * {@section Closing the output stream}
 * An {@linkplain ImageOutputStream Image Output Stream} may be created automatically from various
 * output types like {@linkplain java.io.File} or {@linkplain java.net.URL}. That output stream is
 * <strong>not</strong> closed after a write operation, because many consecutive write operations
 * may be performed for the same output. To ensure that the automatically generated output stream
 * is closed, user shall invoke the {@link #setOutput(Object)} method with a {@code null} input,
 * or invoke the {@link #reset()} or {@link #dispose()} methods.
 * <p>
 * Note that output streams explicitly given by the users are never closed. It is caller
 * responsibility to close them.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 3.20
 *
 * @since 3.14
 * @module
 */
public class ImageCoverageWriter extends GridCoverageWriter {
    /**
     * The {@link ImageWriter} to use for encoding {@link RenderedImage}s. This writer is initially
     * {@code null} and lazily created when first needed. Once created, it is reused for subsequent
     * outputs if possible.
     */
    protected ImageWriter imageWriter;

    /**
     * Creates a new instance.
     */
    public ImageCoverageWriter() {
    }

    /**
     * Sets the logging level to use for write operations. If the {@linkplain #imageWriter image
     * writer} implements the {@link org.geotoolkit.util.logging.LogProducer} interface, then it
     * is also set to the given level.
     *
     * @since 3.15
     */
    @Override
    public void setLogLevel(final Level level) {
        super.setLogLevel(level);
        copyLevel(imageWriter);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The given locale will also be given to the wrapped {@linkplain #imageWriter image writer},
     * providing that the image writer supports the locale language. If it doesn't, then the image
     * writer locale is set to {@code null}.
     */
    @Override
    public void setLocale(final Locale locale) {
        super.setLocale(locale);
        setLocale(imageWriter, locale);
    }

    /**
     * Sets the given locale to the given {@link ImageWriter}, provided that the image writer
     * supports the language of that locale. Otherwise sets the writer locale to {@code null}.
     *
     * @see ImageReader#setLocale(Locale)
     */
    private static void setLocale(final ImageWriter writer, final Locale locale) {
        if (writer != null) {
            writer.setLocale(select(locale, writer.getAvailableLocales()));
        }
    }

    /**
     * Sets the output destination to the given object. The output is typically a
     * {@link java.io.File}, {@link java.net.URL} or {@link String} object, but other
     * types (especially {@link ImageOutputStream}) may be accepted as well depending
     * on the {@linkplain #imageWriter image writer} implementation.
     * <p>
     * The given output can also be an {@link ImageWriter} instance with its output initialized,
     * in which case it is used directly as the {@linkplain #imageWriter image writer} wrapped
     * by this {@code ImageCoverageWriter}.
     *
     * @param  output The output (typically {@link java.io.File} or {@link String}) to be written.
     * @throws IllegalArgumentException if output is not a valid instance for this writer.
     * @throws CoverageStoreException if the operation failed.
     */
    @Override
    public void setOutput(final Object output) throws CoverageStoreException {
        try {
            close();
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(output, e, true), e);
        }
        super.setOutput(output);
    }

    /**
     * Sets the image writer output. This method ensures that the {@link #imageWriter} field is
     * set to a suitable {@link ImageWriter} instance. This is done by invoking the following
     * methods, which can be overridden by subclasses:
     * <p>
     * <ol>
     *   <li>If the current {@link #imageWriter} is non-null, invoke
     *       {@link #canReuseImageWriter(ImageWriterSpi, String, Object, RenderedImage)}
     *       for determining if it can be reused for the new output.</li>
     *   <li>If the current {@code imageWriter} was null or if the above method call
     *       returned {@code false}, invoke {@link #createImageWriter(String, Object, RenderedImage)}
     *       for creating a new {@link ImageWriter} instance for the given output.</li>
     * </ol>
     * <p>
     * Then this method {@linkplain ImageWriter#setOutput(Object) sets the output} of the
     * {@link #imageWriter} instance, if it was not already done by the above method calls.
     *
     * @param  output The output (typically {@link java.io.File} or {@link String}) to be written.
     * @param  image The image to be written, or {@code null} if unknown.
     * @param  formatName The format to use for fetching an {@link ImageWriter}, or {@code null}
     *         if unspecified.
     * @throws IllegalArgumentException if output is not a valid instance for this writer.
     * @throws CoverageStoreException if the operation failed.
     */
    private void setImageOutput(final RenderedImage image, final String formatName)
            throws CoverageStoreException
    {
        final Object output = this.output;
        if (output != null) try {
            final ImageWriter oldWriter = imageWriter;
            ImageWriter newWriter = null;
            if (output instanceof ImageWriter) {
                newWriter = (ImageWriter) output;
                // The old writer will be disposed and the locale will be set below.
            } else {
                /*
                 * First, check if the current writer can be reused. If the user
                 * didn't overridden the canReuseImageWriter(...) method, then the
                 * default implementation is to look at the file extension.
                 */
                if (oldWriter != null) {
                    final ImageWriterSpi provider = oldWriter.getOriginatingProvider();
                    if (provider != null && canReuseImageWriter(provider, formatName, output, image)) {
                        newWriter = oldWriter;
                    }
                }
                /*
                 * If we can't reuse the old writer, create a new one. If the user didn't
                 * overridden the createImageWriter(...) method, then the default behavior
                 * is to get an image writer by the extension.
                 */
                if (newWriter == null) {
                    newWriter = createImageWriter(formatName, output, image);
                }
                /*
                 * Set the output if it was not already done. In the default implementation,
                 * this is done by 'createImageWriter' but not by 'canReuseImageWriter'.
                 * However the user could have overridden the above-cited methods with a
                 * different behavior.
                 */
                if (newWriter != output && newWriter.getOutput() == null) {
                    Object imageOutput = output;
                    final ImageWriterSpi provider = newWriter.getOriginatingProvider();
                    if (provider != null) {
                        boolean needStream = false;
                        for (final Class<?> outputType : provider.getOutputTypes()) {
                            if (outputType.isInstance(imageOutput)) {
                                needStream = false;
                                break;
                            }
                            if (outputType.isAssignableFrom(ImageOutputStream.class)) {
                                needStream = true;
                                // Do not break - maybe the output type is accepted later.
                            }
                        }
                        if (needStream) {
                            imageOutput = ImageIO.createImageOutputStream(output);
                            if (imageOutput == null) {
                                final int messageKey;
                                final Object argument;
                                if (IOUtilities.canProcessAsPath(output)) {
                                    messageKey = Errors.Keys.CANT_WRITE_FILE_$1;
                                    argument = IOUtilities.name(output);
                                } else {
                                    messageKey = Errors.Keys.UNKNOWN_TYPE_$1;
                                    argument = output.getClass();
                                }
                                throw new CoverageStoreException(Errors.getResources(locale).getString(messageKey, argument));
                            }
                        }
                    }
                    newWriter.setOutput(imageOutput);
                }
            }
            /*
             * If the writer has changed, close the output of the old writer, unless the new
             * writer is using the same output. Note that if the output stream was explicitly
             * given by the user, then the new writer should have the same output (consequently
             * it will not be closed).
             */
            if (newWriter != oldWriter) {
                if (oldWriter != null) {
                    final Object oldOutput = oldWriter.getOutput();
                    oldWriter.dispose();
                    if (oldOutput != newWriter.getOutput()) {
                        IOUtilities.close(oldOutput);
                    }
                }
                copyLevel(newWriter);
                setLocale(newWriter, locale);
                if (LOGGER.isLoggable(getFineLevel())) {
                    ImageCoverageStore.logCodecCreation(this, ImageCoverageWriter.class,
                            newWriter, newWriter.getOriginatingProvider());
                }
            }
            imageWriter = newWriter;
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(output, e, true), e);
        }
    }

    /**
     * Returns {@code true} if the image writer created by the given provider can
     * be reused. This method is invoked automatically for determining if the current
     * {@linkplain #imageWriter image writer} can be reused for writing the given output.
     * <p>
     * The default implementation performs the following checks:
     * <p>
     * <ol>
     *   <li>If {@code formatName} is non-null, then this method checks if the given name is
     *       one of the {@linkplain ImageWriterSpi#getFormatNames() format names declared by
     *       the provider}. If not, then this method immediately returns {@code false}.</li>
     *
     *   <li>Next, this method returns {@code true} if the writer
     *       {@linkplain ImageWriterSpi#canEncodeImage(RenderedImage) can encode} the given image,
     *       or {@code false} otherwise.</li>
     * </ol>
     *
     * {@section Overriding}
     * Subclasses can override this method if they want to determine in another way whatever
     * the {@linkplain #imageWriter image writer} can be reused. Subclasses can optionally
     * {@linkplain ImageWriter#setOutput(Object) set the image writer output} or leave it
     * {@code null}, at their choice. If they set the output, then that output will be used.
     * Otherwise the caller will set the output automatically.
     *
     * @param  provider The provider of the image writer.
     * @param  formatName The format to use for fetching an {@link ImageWriter},
     *         or {@code null} if unspecified.
     * @param  output The output to set to the image writer.
     * @param  image The image to be written, or {@code null} if unknown.
     * @return {@code true} if the image writer can be reused.
     * @throws IOException If an error occurred while determining if the current
     *         image writer can write the given image to the given output.
     */
    protected boolean canReuseImageWriter(final ImageWriterSpi provider, final String formatName,
            final Object output, final RenderedImage image) throws IOException
    {
        if (formatName != null) {
            final String[] formats = provider.getFormatNames();
            if (formats == null || !XArrays.containsIgnoreCase(formats, formatName)) {
                return false;
            }
        }
        return provider.canEncodeImage(image);
    }

    /**
     * Creates an {@link ImageWriter} that claim to be able to encode the given output. This method
     * is invoked automatically for assigning a new value to the {@link #imageWriter} field.
     * <p>
     * The default implementation performs the following choice:
     * <p>
     * <ul>
     *   <li>If a non-null format name is specified, delegate to
     *       {@link XImageIO#getWriterByFormatName(String, Object, RenderedImage)}.</li>
     *   <li>Otherwise delegate to {@link XImageIO#getWriterBySuffix(Object, RenderedImage)}.</li>
     * </ul>
     *
     * {@section Overriding}
     * Subclasses can override this method if they want to create a new image writer in another way.
     * Subclasses can optionally {@linkplain ImageWriter#setOutput(Object) set the image writer output}
     * or leave it {@code null}, at their choice. If they set the output, then that output will be used.
     * Otherwise the caller will set the output automatically.
     *
     * @param  formatName The format to use for fetching an {@link ImageWriter},
     *         or {@code null} if unspecified.
     * @param  output The output destination.
     * @param  image The image to be written, or {@code null} if unknown.
     * @return An initialized image writer for writing to the given output.
     * @throws IOException If no suitable image writer has been found, or if an error occurred
     *         while creating it.
     */
    protected ImageWriter createImageWriter(final String formatName, final Object output,
            final RenderedImage image) throws IOException
    {
        if (MosaicImageWriter.Spi.DEFAULT.canEncodeOutput(output)) {
            return MosaicImageWriter.Spi.DEFAULT.createWriterInstance();
        }
        if (formatName != null) {
            return XImageIO.getWriterByFormatName(formatName, output, image);
        } else {
            return XImageIO.getWriterBySuffix(output, image);
        }
    }

    /**
     * Returns the default Java I/O parameters to use for writing an image. This method
     * is invoked by the {@link #write(GridCoverage, GridCoverageWriteParam)} method in
     * order to get the Java parameter object to use for controlling the writing process.
     * <p>
     * The default implementation returns {@link ImageWriter#getDefaultWriteParam()} with
     * tiling, progressive mode and compression set to {@link ImageWriteParam#MODE_DEFAULT}.
     * Subclasses can override this method in order to perform additional parameter
     * settings. Note however that any
     * {@linkplain ImageWriteParam#setSourceRegion source region},
     * {@linkplain ImageWriteParam#setSourceSubsampling source subsampling} and
     * {@linkplain ImageWriteParam#setSourceBands source bands} settings may be overwritten
     * by the {@code write} method, which perform its own computation.
     *
     * @param  image The image which will be written.
     * @return A default Java I/O parameters object to use for controlling the writing process.
     * @throws IOException If an I/O operation was required and failed.
     *
     * @see #write(GridCoverage, GridCoverageWriteParam)
     */
    protected ImageWriteParam createImageWriteParam(RenderedImage image) throws IOException {
        final ImageWriteParam param = imageWriter.getDefaultWriteParam();
        if (param.canWriteTiles()) {
            param.setTilingMode(ImageWriteParam.MODE_DEFAULT);
        }
        if (param.canWriteProgressive()) {
            param.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
        }
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
        }
        return param;
    }

    /**
     * Creates additional metadata to be merged with the one created by {@code ImageCoverageReader}.
     * This method is invoked automatically just before to write the image, with a {@code metadata}
     * argument containing basic information in the {@code RectifiedGridDomain} and {@code Dimensions}
     * nodes (see <a href="../../image/io/metadata/SpatialMetadataFormat.html#default-formats">Image
     * metadata</a> for a tree description). The default implementation does nothing. However
     * subclasses can override this method in order to create additional metadata that this
     * writer can not infer.
     *
     * @param  metadata The default metadata, to be modified in-place.
     * @param  coverage {@code null} for {@linkplain ImageWriter#getDefaultStreamMetadata stream metadata},
     *         or the coverage being written for {@linkplain ImageWriter#getDefaultImageMetadata image metadata}.
     * @throws IOException If an I/O operation was required and failed.
     *
     * @since 3.17
     */
    protected void completeImageMetadata(IIOMetadata metadata, GridCoverage coverage) throws IOException {
    }

    /**
     * Writes a single grid coverage using {@link ImageWriter#write(IIOMetadata, IIOImage, ImageWriteParam)}.
     * The default implementation wraps the given coverage in a {@linkplain Collections#singleton(Object)
     * singleton set} and delegates to {@link #write(Iterable, GridCoverageWriteParam)}.
     */
    @Override
    public void write(final GridCoverage coverage, final GridCoverageWriteParam param)
            throws CoverageStoreException, CancellationException
    {
        write(Collections.singleton(coverage), param);
    }

    /**
     * Writes a single or many grid coverages using {@link ImageWriter#write(IIOMetadata, IIOImage,
     * ImageWriteParam) ImageWriter.write} or {@link ImageWriter#writeToSequence(IIOImage, ImageWriteParam)
     * writeToSequence(IIOImage, ImageWriteParam)}. For each coverage in the given iterable, this
     * method performs the following steps:
     * <p>
     * <ul>
     *   <li>Get the coverage {@link RenderedImage} and {@linkplain GridGeometry2D}.</li>
     *   <li>Create an initially empty block of image parameters by
     *       invoking the {@link #createImageWriteParam(RenderedImage)} method.</li>
     *   <li>Convert the given {@linkplain GridCoverageWriteParam geodetic parameters} to
     *       {@linkplain ImageWriteParam image parameters} using the above grid geometry.
     *       The main properties to be set are:
     *       <ul>
     *         <li>{@linkplain ImageWriteParam#setSourceRegion source region}</li>
     *         <li>{@linkplain ImageWriteParam#setSourceSubsampling source subsampling}</li>
     *         <li>{@linkplain ImageWriteParam#setSourceBands source bands}</li>
     *       </ul></li>
     *   <li>Write the rendered image by invoking the image writer {@code write} or
     *       {@code writeSequence} method, depending on whatever the given iterable
     *       contains one or more coverages.</li>
     * </ul>
     *
     * @see ImageWriter#write(IIOMetadata, IIOImage, ImageWriteParam)
     * @see ImageWriter#writeToSequence(IIOImage, ImageWriteParam)
     */
    @Override
    public void write(final Iterable<? extends GridCoverage> coverages, final GridCoverageWriteParam param)
            throws CoverageStoreException, CancellationException
    {
        abortRequested = false;
        final long startTime = isLoggable() ? System.nanoTime() : Long.MIN_VALUE;
        final Iterator<? extends GridCoverage> it = coverages.iterator();
        if (!it.hasNext()) {
            throw new CoverageStoreException(Errors.format(Errors.Keys.NO_SUCH_ELEMENT_$1, GridCoverage.class));
        }
        boolean hasNext;
        write(it.next(), param, true, !(hasNext = it.hasNext()), startTime);
        while (hasNext) { // Happen only if writing many coverages in a sequence.
            write(it.next(), param, false, !(hasNext = it.hasNext()), startTime);
        }
    }

    /**
     * Writes a single coverage, which may be an element of a sequence. This method needs to be
     * informed when it is writing the first or the last coverage of a sequence. If there is only
     * one coverage to write, than both {@code isFirst} and {@code isLast} must be {@code true}.
     * <p>
     * In current implementation, the stream metadata are generated from the first image only
     * (when {@code isFirst == true}) and the log message (if any) shows the grid geometry of
     * the last coverage only (when {@code isLast == true}). It should not be an issue in the
     * common case where all coverage in the sequence have similar grid geometry or metadata.
     *
     * @param  coverages The coverages to write.
     * @param  param     Optional parameters used to control the writing process, or {@code null}.
     * @param  isFirst   {@code true} if writing the first coverage of a sequence.
     * @param  isLast    {@code true} if writing the last coverage of a sequence.
     * @param  startTime Nano time when the writing process started, or {@link Long#MIN_VALUE}
     *                   if the operation duration is not logged.
     * @throws IllegalStateException If the output destination has not been set.
     * @throws CoverageStoreException If the iterable contains an unsupported number of coverages,
     *         or if an error occurs while writing the information to the output destination.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @since 3.20
     */
    private void write(final GridCoverage coverage, final GridCoverageWriteParam param,
            final boolean isFirst, final boolean isLast, final long startTime)
            throws CoverageStoreException, CancellationException
    {
        /*
         * Prepares an initially empty ImageWriteParam, to be filled later with the values
         * provided in the GridCoverageWriteParam. In order to get the ImageWriteParam, we
         * need the ImageWriter, which need the RenderedImage, which need the GridGeometry.
         */
        GridGeometry2D gridGeometry = GridGeometry2D.castOrCopy(coverage.getGridGeometry());
        RenderedImage image = coverage.getRenderableImage(gridGeometry.gridDimensionX,
                gridGeometry.gridDimensionY).createDefaultRendering();
        while (image instanceof RenderedImageAdapter) {
            image = ((RenderedImageAdapter) image).getWrappedImage();
        }
        if (isFirst) {
            final String imageFormat = (param != null) ? param.getFormatName() : null;
            setImageOutput(image, imageFormat);
        }
        /*
         * The ImageWriter is created by the call to setImageOutput.
         * We can verify its validity only at this point.
         */
        final ImageWriter imageWriter = this.imageWriter; // Protect from changes.
        if (imageWriter == null) {
            throw new IllegalStateException(formatErrorMessage(Errors.Keys.NO_IMAGE_OUTPUT));
        }
        if (!isLast && !imageWriter.canWriteSequence()) {
            throw new CoverageStoreException(Errors.format(Errors.Keys.UNSUPPORTED_MULTI_OCCURRENCE_$1, GridCoverage.class));
        }
        /*
         * Convert the geodetic coordinates to pixel coordinates.
         */
        final ImageWriteParam imageParam;
        try {
            imageParam = createImageWriteParam(image);
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        MathTransform2D destToExtractedGrid = null;
        PlanarImage toDispose = null;
        if (param != null) {
            /*
             * Now convert the GridCoverageWriteParam values to ImageWriteParam value.
             * First of all, convert the ISO 119123 InterpolationMethod code to the JAI
             * code.
             */
            final int interp;
            final InterpolationMethod interpolation = param.getInterpolation();
            if (interpolation.equals(InterpolationMethod.NEAREST_NEIGHBOUR)) {
                interp = Interpolation.INTERP_NEAREST;
            } else if (interpolation.equals(InterpolationMethod.BILINEAR)) {
                interp = Interpolation.INTERP_BILINEAR;
            } else if (interpolation.equals(InterpolationMethod.BICUBIC)) {
                interp = Interpolation.INTERP_BICUBIC;
            } else {
                throw new CoverageStoreException(Errors.getResources(locale).getString(
                        Errors.Keys.ILLEGAL_ARGUMENT_$2, "interpolation", interpolation.name()));
            }
            destToExtractedGrid = geodeticToPixelCoordinates(gridGeometry, param, imageParam);
            imageParam.setSourceBands(param.getSourceBands());
            final Rectangle sourceRegion  = imageParam.getSourceRegion();
            final Rectangle requestRegion = requestedBounds;
            if (interp != Interpolation.INTERP_NEAREST || !isIdentity(destToExtractedGrid) ||
                    isGreater(requestRegion.width,  imageParam.getSourceXSubsampling(), sourceRegion.width) ||
                    isGreater(requestRegion.height, imageParam.getSourceYSubsampling(), sourceRegion.height))
            {
                /*
                 * We need to resample the image if:
                 *
                 *  - The transform from the source grid to the target grid is not affine;
                 *  - The above transform is affine but more complex than scale and translations;
                 *  - The translation or scale factors of the above transform are not integers;
                 *  - The requested envelope is greater than the coverage envelope;
                 */
                final InternationalString name = (coverage instanceof AbstractCoverage) ?
                        ((AbstractCoverage) coverage).getName() : null;
                final ImageLayout layout = new ImageLayout(
                        requestRegion.x,     requestRegion.y,
                        requestRegion.width, requestRegion.height);
                /*
                 * Some codecs (e.g. JPEG) require that the whole image is available
                 * as a single raster.
                 */
                layout.setTileWidth (requestRegion.width);
                layout.setTileHeight(requestRegion.height);
                final RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
                destToExtractedGrid = (MathTransform2D) destGridToSource; // Will be used for logging purpose.
                final Warp warp;
                try {
                    warp = WarpFactory.DEFAULT.create(name, destToExtractedGrid, sourceRegion);
                } catch (TransformException e) {
                    throw new CoverageStoreException(formatErrorMessage(e), e);
                }
                if (DEBUG) {
                    /*
                     * To be enabled only when debugging.
                     * Simplified output example from the writeSubsampledRegion() test:
                     *
                     * Grid to source:   ┌         ┐
                     *                   │ 2  0  9 │
                     *                   │ 0  3  9 │
                     *                   │ 0  0  1 │
                     *                   └         ┘
                     * Source region:    Rectangle[x=9, y=9, width=9, height=15]
                     * Warp origin:      [9.5, 10.0]
                     *
                     * If we had no scale factor, the Warp origin would be the same than the
                     * translations. If we have scale factors be were mapping pixel corners,
                     * then the warp origin would also be the same.
                     *
                     * But the JAI Warp operation maps pixel center. It does so by adding 0.5
                     * to pixel coordinates before applying the Warp, and removing 0.5 to the
                     * result (see WarpTransform2D.getWarp(...) javadoc). This is actually the
                     * desired behavior, as we can see with the picture below which represents
                     * only the first pixel of the destination image. The cell are the source
                     * pixels, the transform is the above matrix, and the coordinates are
                     * relative to the source grid:
                     *
                     *       (9,9)
                     *         ┌─────┬─────┐
                     *         │     │     │
                     *         ├─────┼─────┤
                     *         │ (10,10.5) │     after the -0.5 final offset, become (9.5, 10).
                     *         ├─────┼─────┤
                     *         │     │     │
                     *         └─────┴─────┘
                     *                   (11,12)
                     */
                    Object tr = destToExtractedGrid;
                    if (tr instanceof LinearTransform) {
                        tr = ((LinearTransform) tr).getMatrix();
                    }
                    final TableWriter table = new TableWriter(null, 1);
                    table.setMultiLinesCells(true);
                    table.writeHorizontalSeparator();
                    table.write("Warping coverage:");                         table.nextColumn();
                    table.write(String.valueOf(name));                        table.nextLine();
                    table.write("Grid to source:");                           table.nextColumn();
                    table.write(String.valueOf(tr));                          table.nextLine();
                    table.write("Source region:");                            table.nextColumn();
                    table.write(String.valueOf(sourceRegion));                table.nextLine();
                    table.write("Warp origin:");                              table.nextColumn();
                    table.write(Arrays.toString(warp.warpPoint(0, 0, null))); table.nextLine();
                    table.writeHorizontalSeparator();
                    System.out.println(table);
                }
                double[] backgroundValues = param.getBackgroundValues();
                if (backgroundValues == null) {
                    backgroundValues = CoverageUtilities.getBackgroundValues(coverage);
                }
                image = toDispose = WarpDescriptor.create(image, warp,
                        Interpolation.getInstance(interp), backgroundValues, hints);
                imageParam.setSourceRegion(null);
                imageParam.setSourceSubsampling(1, 1, 0, 0);
            }
            /*
             * Set other parameters inferred from the GridCoverageWriteParam.
             */
            if (imageParam.canWriteCompressed()) {
                final Float compression = param.getCompressionQuality();
                if (compression != null) {
                    imageParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    imageParam.setCompressionQuality(compression);
                }
            }
        }
        /*
         * Creates metadata with the information calculated so far. The code above this
         * point should have created an image having a grid geometry matching the user
         * request, so we will write that user request in the metadata.
         */
        final ImageTypeSpecifier imageType = ImageTypeSpecifier.createFromRenderedImage(image);
        final IIOMetadata streamMetadata = isFirst ? imageWriter.getDefaultStreamMetadata(imageParam) : null;
        final IIOMetadata imageMetadata  = imageWriter.getDefaultImageMetadata(imageType, imageParam);
        if (XArrays.contains(imageMetadata.getMetadataFormatNames(), GEOTK_FORMAT_NAME)) {
            CoordinateReferenceSystem crs = null;
            Envelope env = null;
            double[] res = null;
            if (param != null) {
                crs = param.getCoordinateReferenceSystem();
                env = param.getEnvelope();
                res = param.getResolution();
            }
            if (crs == null && gridGeometry.isDefined(GridGeometry2D.CRS)) {
                if (imageWriter instanceof MultidimensionalImageStore) {
                    crs = gridGeometry.getCoordinateReferenceSystem();
                } else {
                    crs = gridGeometry.getCoordinateReferenceSystem2D();
                }
            }
            if (env == null && gridGeometry.isDefined(GridGeometry2D.ENVELOPE)) {
                if (imageWriter instanceof MultidimensionalImageStore) {
                    env = gridGeometry.getEnvelope();
                } else {
                    env = gridGeometry.getEnvelope2D();
                }
            }
            if (crs != null) {
                final ReferencingBuilder builder = new ReferencingBuilder(imageMetadata);
                builder.setCoordinateReferenceSystem(crs);
            }
            if (env != null) {
                final GridDomainAccessor accessor = new GridDomainAccessor(imageMetadata);
                final Dimension size = getImageSize(image, imageParam);
                final double xmin = env.getMinimum(X_DIMENSION);
                final double ymax = env.getMaximum(Y_DIMENSION);
                if (res != null) {
                    final double[] p = new double[] {xmin, ymax};
                    accessor.setOrigin(p);
                    p[0] = +res[X_DIMENSION]; p[1]=0; accessor.addOffsetVector(p);
                    p[1] = -res[Y_DIMENSION]; p[0]=0; accessor.addOffsetVector(p);
                    p[0] = env.getMedian(X_DIMENSION);
                    p[1] = env.getMedian(Y_DIMENSION);
                    accessor.setSpatialRepresentation(p, null, PixelOrientation.UPPER_LEFT);
                    accessor.setLimits(new int[2], new int[] {size.width-1, size.height-1});
                } else {
                    // Let the accessor compute the resolution for us.
                    accessor.setAll(xmin, ymax, env.getMaximum(X_DIMENSION),
                            env.getMinimum(Y_DIMENSION), size.width, size.height, false, null);
                }
            }
            final int n = coverage.getNumSampleDimensions();
            final DimensionAccessor accessor = new DimensionAccessor(imageMetadata);
            for (int i=0; i<n; i++) {
                final SampleDimension band = coverage.getSampleDimension(i);
                accessor.selectChild(accessor.appendChild());
                if (band != null) {
                    accessor.setDimension(band, locale);
                }
            }
        }
        /*
         * Now process to the coverage writing. If the coverage is the only image  (i.e. is both
         * the first and the last image), then we will write everything in a single operation by
         * a call to ImageWriter.write(...). Otherwise we will need to use the
         * prepareWriteSequence() - writeSequence(...) - endWriteSequence() cycle.
         */
        checkAbortState();
        try {
            if (streamMetadata != null) {
                completeImageMetadata(streamMetadata, null);
            }
            completeImageMetadata(imageMetadata, coverage);
            final IIOImage iio = new IIOImage(image, null, imageMetadata);
            if (isFirst & isLast) {
                imageWriter.write(streamMetadata, iio, imageParam);
            } else {
                if (isFirst) {
                    imageWriter.prepareWriteSequence(streamMetadata);
                }
                imageWriter.writeToSequence(iio, imageParam);
                if (isLast) {
                    imageWriter.endWriteSequence();
                }
            }
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        checkAbortState();
        /*
         * Finally, logs the operation after the last image if logging are enabled.
         * The log level will depend on how long it took to write every images in
         * the sequence.
         */
        if (isLast && startTime != Long.MIN_VALUE) {
            final long time = System.nanoTime() - startTime;
            final Level level = getLogLevel(time);
            if (LOGGER.isLoggable(level)) {
                final Dimension size = getImageSize(image, imageParam);
                CoordinateReferenceSystem crs = null;
                if (param != null) {
                    crs = param.getCoordinateReferenceSystem();
                }
                ImageCoverageStore.logOperation(level, locale, ImageCoverageWriter.class, true,
                        output, 0, coverage, size, crs, destToExtractedGrid, time);
            }
        }
        if (toDispose != null) {
            toDispose.dispose();
        }
    }

    /**
     * Returns the size of the image to be written.
     *
     * @param  image The image to be written.
     * @param  param The parameter to use for controlling the writing process, or {@code null}.
     * @return The size of the image being written.
     */
    private static Dimension getImageSize(final RenderedImage image, final ImageWriteParam param) {
        final Dimension size = new Dimension(image.getWidth(), image.getHeight());
        if (param != null) {
            final Rectangle request = param.getSourceRegion();
            if (request != null) {
                size.width  = Math.min(size.width,  request.width  / param.getSourceXSubsampling());
                size.height = Math.min(size.height, request.height / param.getSourceXSubsampling());
            }
        }
        return size;
    }

    /**
     * Returns {@code true} if the given request dimension scaled by the given subsampling
     * is greater than the given source dimension.
     *
     * @param request     The span of the requested destination region.
     * @param subsampling The subsampling to be applied when reading the source.
     * @param source      The span of the source region.
     */
    private static boolean isGreater(final int request, final int subsampling, final int source) {
        return request * subsampling - (subsampling - 1) > source;
    }

    /**
     * Cancels the write operation. The default implementation forward the call to the
     * {@linkplain #imageWriter image writer}, if any. The content of the coverage
     * following the abort will be undefined.
     */
    @Override
    public void abort() {
        super.abort();
        final ImageWriter imageWriter = this.imageWriter; // Protect from changes.
        if (imageWriter != null) {
            imageWriter.abort();
        }
    }

    /**
     * Returns an error message for the given exception. If the {@linkplain #output output} is
     * known, this method returns "<cite>Can't write {the name}</cite>" followed by the cause
     * message. Otherwise it returns the localized message of the given exception.
     */
    @Override
    final String formatErrorMessage(final Throwable e) {
        return formatErrorMessage(output, e, true);
    }

    /**
     * Closes the output used by the {@link ImageWriter}, provided that the stream was not
     * given explicitly by the user. The {@link ImageWriter} is not disposed, so it can be
     * reused for the next image to write.
     *
     * @throws IOException if an error occurs while closing the output.
     */
    private void close() throws IOException {
        final Object oldOutput = output;
        output = null; // Clear now in case the code below fails.
        final ImageWriter imageWriter = this.imageWriter; // Protect from changes.
        if (imageWriter != null) {
            if (imageWriter.getOutput() != oldOutput) {
                XImageIO.close(imageWriter);
            } else {
                imageWriter.setOutput(null);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ImageWriter#reset()
     */
    @Override
    public void reset() throws CoverageStoreException {
        try {
            close();
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        if (imageWriter != null) {
            imageWriter.reset();
        }
        super.reset();
    }

    /**
     * Allows any resources held by this writer to be released. The result of calling any other
     * method subsequent to a call to this method is undefined.
     * <p>
     * The default implementation closes the {@linkplain #imageWriter image writer} output if
     * the later is a stream, then {@linkplain ImageWriter#dispose() disposes} that writer.
     *
     * @see ImageWriter#dispose()
     */
    @Override
    public void dispose() throws CoverageStoreException {
        try {
            close();
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        if (imageWriter != null) {
            imageWriter.dispose();
            imageWriter = null;
        }
        super.dispose();
    }
}

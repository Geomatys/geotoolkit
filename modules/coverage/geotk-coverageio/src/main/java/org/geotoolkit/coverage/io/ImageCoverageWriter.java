/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.InterpolationMethod;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.util.XArrays;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.io.mosaic.MosaicImageWriter;
import org.geotoolkit.referencing.operation.transform.WarpFactory;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.AbstractCoverage;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.resources.Errors;


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
 * @version 3.16
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
                                    messageKey = Errors.Keys.CANT_WRITE_$1;
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
     *       the provider}. If not, then this method returns {@code false}.</li>
     *
     *   <li>Next, this method checks if the writer
     *       {@linkplain ImageWriterSpi#canEncodeImage(RenderedImage) can encode} the given image.</li>
     * </ol>
     * <p>
     * Subclasses can override this method if they want to determine in another way
     * whatever the {@linkplain #imageWriter image writer} can be reused. Subclasses
     * don't need to set the image writer output; this will be done by the caller.
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
     * is invoked automatically for creating a new {@linkplain #imageWriter image writer}.
     * <p>
     * This method shall {@linkplain ImageWriter#setOutput(Object) set the output} of the
     * image writer that it create before returning it.
     * <p>
     * The default implementation delegates to
     * {@link XImageIO#getWriterByFormatName(String, Object, RenderedImage)} if a non-null format
     * is specified, or {@link XImageIO#getWriterBySuffix(Object, RenderedImage)} otherwise.
     * Subclasses can override this method if they want to create a new
     * {@linkplain #imageWriter image writer} in another way.
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
     * Converts geodetic parameters to image parameters, gets the image from the coverage and
     * writes it. First, this method creates an initially empty block of image parameters by
     * invoking the {@link #createImageWriteParam(RenderedImage)} method. The image parameter
     * {@linkplain ImageWriteParam#setSourceRegion source region},
     * {@linkplain ImageWriteParam#setSourceSubsampling source subsampling} and
     * {@linkplain ImageWriteParam#setSourceBands source bands} are computed from the
     * parameter given to this {@code write} method.
     */
    @Override
    public void write(final GridCoverage coverage, final GridCoverageWriteParam param)
            throws CoverageStoreException, CancellationException
    {
        final boolean loggingEnabled = isLoggable();
        long fullTime = (loggingEnabled) ? System.nanoTime() : 0;
        /*
         * Prepares an initially empty ImageWriteParam, to be filled later with the values
         * provided in the GridCoverageWriteParam. In order to get the ImageWriteParam, we
         * need the ImageWriter, which need the RenderedImage, which need the GridGeometry.
         */
        abortRequested = false;
        GridGeometry2D gridGeometry = GridGeometry2D.wrap(coverage.getGridGeometry());
        RenderedImage image = coverage.getRenderableImage(gridGeometry.gridDimensionX,
                gridGeometry.gridDimensionY).createDefaultRendering();
        while (image instanceof RenderedImageAdapter) {
            image = ((RenderedImageAdapter) image).getWrappedImage();
        }
        final String imageFormat = (param != null) ? param.getFormatName() : null;
        setImageOutput(image, imageFormat);
        final ImageWriter imageWriter = this.imageWriter; // Protect from changes.
        if (imageWriter == null) {
            throw new IllegalStateException(formatErrorMessage(Errors.Keys.NO_IMAGE_OUTPUT));
        }
        /*
         * Convert the geodetic coordinates to pixel coordinates.
         */
        final IIOMetadata streamMetadata = null; // TODO
        final IIOMetadata imageMetadata  = null; // TODO
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
                if (false) {
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
         * Now process to the coverage writing.
         * Finally, logs the operation (if logging are enabled).
         */
        final IIOImage bundle = new IIOImage(image, null, imageMetadata);
        try {
            imageWriter.write(streamMetadata, bundle, imageParam);
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        if (loggingEnabled) {
            fullTime = System.nanoTime() - fullTime;
            final Level level = getLogLevel(fullTime);
            if (LOGGER.isLoggable(level)) {
                final Dimension size = new Dimension(image.getWidth(), image.getHeight());
                if (imageParam != null) {
                    final Rectangle request = imageParam.getSourceRegion();
                    if (request != null) {
                        size.width  = Math.min(size.width,  request.width  / imageParam.getSourceXSubsampling());
                        size.height = Math.min(size.height, request.height / imageParam.getSourceXSubsampling());
                    }
                }
                CoordinateReferenceSystem crs = null;
                if (param != null) {
                    crs = param.getCoordinateReferenceSystem();
                }
                ImageCoverageStore.logOperation(level, locale, ImageCoverageWriter.class, true,
                        output, 0, coverage, size, crs, destToExtractedGrid, fullTime);
            }
        }
        if (toDispose != null) {
            toDispose.dispose();
        }
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
    final String formatErrorMessage(final Exception e) {
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
            throw new CoverageStoreException(e);
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
            throw new CoverageStoreException(e);
        }
        if (imageWriter != null) {
            imageWriter.dispose();
            imageWriter = null;
        }
        super.dispose();
    }
}

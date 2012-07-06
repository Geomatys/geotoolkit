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

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.util.logging.LogRecord;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.event.IIOWriteWarningListener;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;

import org.geotoolkit.image.ImageDimension;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.ImageUtilities;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.Disposable;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Locales;
import org.geotoolkit.resources.IndexedResourceBundle;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Base class for writers of spatial (usually geographic) data.
 * This base class provides the following restrictions or conveniences:
 * <p>
 * <ul>
 *   <li>Set the metadata type to {@link SpatialMetadata} and its format to
 *       {@linkplain SpatialMetadataFormat#getStreamInstance stream metadata} and
 *       {@linkplain SpatialMetadataFormat#getImageInstance image metadata}.</li>
 *   <li>Provide {@link #getSampleModel getSampleModel}, {@link #createRectIter createRectIter}
 *       and {@link #computeSize computeSize} static methods as helpers for the
 *       {@link #write(IIOImage) write(...)} implementations.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see SpatialImageReader
 *
 * @since 3.05 (derived from 2.4)
 * @module
 */
public abstract class SpatialImageWriter extends ImageWriter implements WarningProducer, Disposable {
    /**
     * Index of the image in process of being written.
     * This convenience index is reset to 0 by the {@link #close()} method.
     */
    int imageIndex;

    /**
     * Index of the thumbnail in process of being written.
     * This convenience index is reset to 0 by the {@link #close()} method.
     */
    private int thumbnailIndex;

    /**
     * Constructs a {@code SpatialImageWriter}.
     *
     * @param provider The {@link ImageWriterSpi} that is constructing this object, or {@code null}.
     */
    protected SpatialImageWriter(final Spi provider) {
        super(provider);
        availableLocales = Locales.getAvailableLocales();
    }

    /**
     * Returns {@code true} if this writer supports the {@linkplain SpatialMetadataFormat
     * spatial metadata format}. This method checks if a native or extra metadata format
     * named {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#GEOTK_FORMAT_NAME}
     * is declared in the {@linkplain #originatingProvider originating provider}.
     *
     * @param  stream {@code true} for testing stream metadata, or {@code false} for testing
     *         image metadata.
     * @return {@code true} if the writer declares to support the spatial metadata format.
     *
     * @see Spi#isSpatialMetadataSupported(boolean)
     *
     * @since 3.20
     */
    final boolean isSpatialMetadataSupported(final boolean stream) {
        final ImageWriterSpi spi = originatingProvider;
        return (spi instanceof Spi) && ((Spi) spi).isSpatialMetadataSupported(stream);
    }

    /**
     * Sets the destination to the given {@link javax.imageio.stream.ImageOutputStream}
     * or other {@code Object}.
     */
    @Override
    public void setOutput(final Object output) {
        closeSilently();
        super.setOutput(output);
    }

    /**
     * Returns the resources for formatting error messages.
     */
    final IndexedResourceBundle getErrorResources() {
        return Errors.getResources(getLocale());
    }

    /**
     * Returns a default parameter object appropriate for this format. The default
     * implementation constructs and returns a new {@link SpatialImageWriteParam}.
     *
     * @return An {@code ImageWriteParam} object which may be used.
     */
    @Override
    public SpatialImageWriteParam getDefaultWriteParam() {
        return new SpatialImageWriteParam(this);
    }

    /**
     * Returns a metadata object containing default values for encoding a stream of images.
     * The default implementation returns an instance of {@link SpatialMetadata} using
     * the {@linkplain SpatialMetadataFormat#getStreamInstance(String) stream format} if the
     * {@linkplain #getOriginatingProvider() originating provider} declares supporting that
     * format, or {@code null} otherwise.
     *
     * @param param Parameters that will be used to encode the image (in cases where
     *              it may affect the structure of the metadata), or {@code null}.
     * @return The metadata, or {@code null}.
     */
    @Override
    public SpatialMetadata getDefaultStreamMetadata(final ImageWriteParam param) {
        if (!isSpatialMetadataSupported(true)) {
            return null;
        }
        return new SpatialMetadata(SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME), this, null);
    }

    /**
     * Returns a metadata object containing default values for encoding an image of the given
     * type. The default implementation returns an instance of {@link SpatialMetadata} using
     * the {@linkplain SpatialMetadataFormat#getImageInstance(String) image format} if the
     * {@linkplain #getOriginatingProvider() originating provider} declares supporting that
     * format, or {@code null} otherwise.
     *
     * @param imageType The format of the image to be written later.
     * @param param Parameters that will be used to encode the image (in cases where
     *              it may affect the structure of the metadata), or {@code null}.
     * @return The metadata, or {@code null}.
     */
    @Override
    public SpatialMetadata getDefaultImageMetadata(final ImageTypeSpecifier imageType,
                                                   final ImageWriteParam    param)
    {
        if (!isSpatialMetadataSupported(false)) {
            return null;
        }
        return new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME), this, null);
    }

    /**
     * Returns a metadata object initialized to the specified data for encoding a stream of
     * images. The default implementation returns the given metadata unchanged if it is an
     * instance of {@link SpatialMetadata} using the
     * {@linkplain SpatialMetadataFormat#getStreamInstance stream format}, or wraps it otherwise.
     *
     * @param inData Stream metadata used to initialize the state of the returned object.
     * @param param Parameters that will be used to encode the image (in cases where
     *              it may affect the structure of the metadata), or {@code null}.
     * @return The metadata, or {@code null}.
     */
    @Override
    public SpatialMetadata convertStreamMetadata(final IIOMetadata     inData,
                                                 final ImageWriteParam param)
    {
        if (inData == null) {
            return null;
        }
        final SpatialMetadataFormat format = SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME);
        if (inData instanceof SpatialMetadata) {
            final SpatialMetadata sp = (SpatialMetadata) inData;
            if (format.equals(sp.format)) {
                return sp;
            }
        }
        return new SpatialMetadata(format, this, inData);
    }

    /**
     * Returns a metadata object initialized to the specified data for encoding an image of the
     * given type. The default implementation returns the given metadata unchanged if it is an
     * instance of {@link SpatialMetadata} using the
     * {@linkplain SpatialMetadataFormat#getImageInstance image format}, or wraps it otherwise.
     *
     * @param inData Image metadata used to initialize the state of the returned object.
     * @param imageType The format of the image to be written later.
     * @param param Parameters that will be used to encode the image (in cases where
     *              it may affect the structure of the metadata), or {@code null}.
     * @return The metadata, or {@code null}.
     */
    @Override
    public SpatialMetadata convertImageMetadata(final IIOMetadata        inData,
                                                final ImageTypeSpecifier imageType,
                                                final ImageWriteParam    param)
    {
        if (inData == null) {
            return null;
        }
        final SpatialMetadataFormat format = SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME);
        if (inData instanceof SpatialMetadata) {
            final SpatialMetadata sp = (SpatialMetadata) inData;
            if (format.equals(sp.format)) {
                return sp;
            }
        }
        return new SpatialMetadata(format, this, inData);
    }

    /**
     * Returns true if the methods that take an {@link IIOImage} parameter are capable of dealing
     * with a {@link Raster}. The default implementation returns {@code true} since it is assumed
     * that subclasses will fetch pixels using the iterator returned by
     * {@link #createRectIter(IIOImage, ImageWriteParam)}.
     */
    @Override
    public boolean canWriteRasters() {
        return true;
    }

    /**
     * Returns the sample model to use for the destination image to be written. Note that the
     * {@linkplain SampleModel#getWidth() width} and {@linkplain SampleModel#getHeight() height}
     * of the returned sample model are usually <strong>not</strong> valid, because they have
     * not been adjusted for the source or destination regions.
     *
     * @param  image The image or raster to be written.
     * @param  parameters The write parameters, or {@code null} if the whole image will be written.
     * @return The sample model of the destination image.
     *
     * @since 3.07
     */
    protected static SampleModel getSampleModel(final IIOImage image, final ImageWriteParam parameters) {
        if (parameters != null) {
            final ImageTypeSpecifier type = parameters.getDestinationType();
            if (type != null) {
                return type.getSampleModel();
            }
        }
        if (image.hasRaster()) {
            return image.getRaster().getSampleModel();
        }
        return image.getRenderedImage().getSampleModel();
    }

    /**
     * Returns an iterator over the pixels of the specified image, taking subsampling in account.
     *
     * @param  image The image or raster to be written.
     * @param  parameters The write parameters, or {@code null} if the whole image will be written.
     * @return An iterator over the pixel values of the image to be written.
     */
    protected static RectIter createRectIter(final IIOImage image, final ImageWriteParam parameters) {
        /*
         * Examines the parameters for subsampling in lines, columns and bands. If a subsampling
         * is specified, the source region will be translated by the subsampling offset (if any).
         */
        Rectangle bounds;
        int[] sourceBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        if (parameters != null) {
            bounds             = parameters.getSourceRegion(); // Needs to be a clone.
            sourceXSubsampling = parameters.getSourceXSubsampling();
            sourceYSubsampling = parameters.getSourceYSubsampling();
            if (sourceXSubsampling != 1 || sourceYSubsampling != 1) {
                if (bounds == null) {
                    if (image.hasRaster()) {
                        bounds = image.getRaster().getBounds(); // Needs to be a clone.
                    } else {
                        bounds = ImageUtilities.getBounds(image.getRenderedImage());
                    }
                }
                final int xOffset = parameters.getSubsamplingXOffset();
                final int yOffset = parameters.getSubsamplingYOffset();
                bounds.x      += xOffset;
                bounds.y      += yOffset;
                bounds.width  -= xOffset;
                bounds.height -= yOffset;
                // Fits to the smallest bounding box, which is
                // required by SubsampledRectIter implementation.
                bounds.width  -= (bounds.width  - 1) % sourceXSubsampling;
                bounds.height -= (bounds.height - 1) % sourceYSubsampling;
            }
            sourceBands = parameters.getSourceBands();
        } else {
            sourceBands        = null;
            bounds             = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
        }
        /*
         * Creates the JAI iterator which will iterate over all pixels in the source region.
         * If no subsampling is specified and the source bands do not move and band, then the
         * JAI iterator is returned directly.
         */
        final int numBands;
        RectIter iterator;
        if (image.hasRaster()) {
            final Raster raster = image.getRaster();
            numBands = raster.getNumBands();
            iterator = RectIterFactory.create(raster, bounds);
        } else {
            final RenderedImage raster = image.getRenderedImage();
            numBands = raster.getSampleModel().getNumBands();
            iterator = RectIterFactory.create(raster, bounds);
        }
        if (sourceXSubsampling == 1 && sourceYSubsampling == 1) {
            if (sourceBands == null) {
                return iterator;
            }
            if (sourceBands.length == numBands) {
                boolean identity = true;
                for (int i=0; i<numBands; i++) {
                    if (sourceBands[i] != i) {
                        identity = false;
                        break;
                    }
                }
                if (identity) {
                    return iterator;
                }
            }
        }
        /*
         * A subsampling is required. Wraps the JAI iterator into a subsampler.
         */
        if (sourceBands == null) {
            sourceBands = new int[numBands];
            for (int i=0; i<numBands; i++) {
                sourceBands[i] = i;
            }
        }
        return new SubsampledRectIter(iterator, sourceXSubsampling, sourceYSubsampling, sourceBands);
    }

    /**
     * Computes the size of the region to be written, taking subsampling in account.
     *
     * @param  image The image or raster to be written.
     * @param  parameters The write parameters, or {@code null} if the whole image will be written.
     * @return dimension The dimension of the image to be written.
     */
    protected static ImageDimension computeSize(final IIOImage image, final ImageWriteParam parameters) {
        final ImageDimension dimension;
        if (image.hasRaster()) {
            dimension = new ImageDimension(image.getRaster());
        } else {
            dimension = new ImageDimension(image.getRenderedImage());
        }
        if (parameters != null) {
            int width  = dimension.width;
            int height = dimension.height;
            final Rectangle bounds = parameters.getSourceRegion();
            if (bounds != null) {
                if (bounds.width < width) {
                    width = bounds.width;
                }
                if (bounds.height < height) {
                    height = bounds.height;
                }
            }
            final int sourceXSubsampling = parameters.getSourceXSubsampling();
            final int sourceYSubsampling = parameters.getSourceYSubsampling();
            width  -= parameters.getSubsamplingXOffset();
            height -= parameters.getSubsamplingYOffset();
            width   = (width  + sourceXSubsampling-1) / sourceXSubsampling;
            height  = (height + sourceYSubsampling-1) / sourceYSubsampling;
            dimension.setSize(width, height);
        }
        return dimension;
    }

    /**
     * Broadcasts the start of an image write to all registered listeners. The default
     * implementation invokes the {@linkplain #processImageStarted(int) super-class method}
     * with an image index maintained by this writer.
     */
    protected void processImageStarted() {
        processImageStarted(imageIndex);
    }

    /**
     * Broadcasts the completion of an image write to all registered listeners.
     */
    @Override
    protected void processImageComplete() {
        super.processImageComplete();
        thumbnailIndex = 0;
        imageIndex++;
    }

    /**
     * Broadcasts the start of a thumbnail write to all registered listeners. The default
     * implementation invokes the {@linkplain #processThumbnailStarted(int,int) super-class
     * method} with an image and thumbnail index maintained by this writer.
     */
    protected void processThumbnailStarted() {
        processThumbnailStarted(imageIndex, thumbnailIndex);
    }

    /**
     * Broadcasts the completion of a thumbnail write to all registered listeners.
     */
    @Override
    protected void processThumbnailComplete() {
        super.processThumbnailComplete();
        thumbnailIndex++;
    }

    /**
     * Broadcasts a warning message to all registered listeners. The default implementation
     * invokes the {@linkplain #processWarningOccurred(int,String) super-class method} with
     * an image index maintained by this writer.
     *
     * @param warning The warning message to broadcasts.
     */
    protected void processWarningOccurred(final String warning) {
        processWarningOccurred(imageIndex, warning);
    }

    /**
     * Broadcasts a warning message to all registered listeners. The default implementation
     * invokes the {@linkplain #processWarningOccurred(int,String,String) super-class method}
     * with an image index maintained by this writer.
     *
     * @param baseName The base name of a set of {@code ResourceBundle}s
     *                 containing localized warning messages.
     * @param keyword  The keyword used to index the warning message within the set of
     *                 {@code ResourceBundle}s.
     */
    protected void processWarningOccurred(final String baseName, final String keyword) {
        processWarningOccurred(imageIndex, baseName, keyword);
    }

    /**
     * Invoked when a warning occurred. The default implementation makes the following choice:
     * <p>
     * <ul>
     *   <li>If at least one {@linkplain IIOWriteWarningListener warning listener}
     *       has been {@linkplain #addIIOWriteWarningListener specified}, then the
     *       {@link IIOWriteWarningListener#warningOccurred warningOccurred} method is
     *       invoked for each of them and the log record is <strong>not</strong> logged.</li>
     *
     *   <li>Otherwise, the log record is sent to the {@code "org.geotoolkit.image.io"} logger.</li>
     * </ul>
     * <p>
     * Subclasses may override this method if more processing is wanted, or for
     * throwing exception if some warnings should be considered as fatal errors.
     *
     * @param record The warning record to log.
     * @return {@code true} if the message has been sent to at least one warning listener,
     *         or {@code false} if it has been sent to the logging system as a fallback.
     *
     * @see org.geotoolkit.image.io.metadata.MetadataNodeAccessor#warningOccurred(LogRecord)
     */
    @Override
    public boolean warningOccurred(final LogRecord record) {
        if (warningListeners == null) {
            record.setLoggerName(LOGGER.getName());
            LOGGER.log(record);
            return false;
        } else {
            processWarningOccurred(IndexedResourceBundle.format(record));
            return true;
        }
    }

    /**
     * Invokes {@link #close} and logs the exception if any. This method is invoked from
     * methods that do not allow {@link IOException} to be thrown. Since we will not use
     * the stream anymore after closing it, it should not be a big deal if an error occurred.
     */
    final void closeSilently() {
        try {
            close();
        } catch (IOException exception) {
            Logging.unexpectedException(LOGGER, getClass(), "close", exception);
        }
    }

    /**
     * Invoked when a new output is set or when the writer is disposed. The default implementation
     * clears the internal cache. Sub-classes can override this method if they have more resources
     * to dispose, but should always invoke {@code super.close()}.
     * <p>
     * This method is overridden and given {@code protected} access by {@link StreamImageWriter}
     * and {@link ImageWriterAdapter}. It is called "{@code close}" in order to match the
     * purpose which appear in the public API of those classes.
     *
     * @throws IOException If an error occurred while closing a stream (applicable to subclasses only).
     */
    void close() throws IOException {
        imageIndex = 0;
        thumbnailIndex = 0;
    }

    /*
     * There is no need to override reset(), because the default ImageReader.reset()
     * implementation invokes setInput(null, false, false), which in turn invokes our
     * closeSilently() method.
     */

    /**
     * Allows any resources held by this writer to be released. If an output stream were
     * created by {@link StreamImageWriter} or {@link ImageWriterAdapter}, it will be
     * {@linkplain StreamImageWriter#close() closed} before to dispose this reader.
     */
    @Override
    public void dispose() {
        closeSilently();
        super.dispose();
    }



    /**
     * Service provider interfaces (SPI) for {@link SpatialImageWriter}s.
     * This base class initializes fields to the values listed below:
     * <p>
     * <table border="1">
     *   <tr bgcolor="lightblue">
     *     <th>Field</th>
     *     <th>Value</th>
     *   </tr><tr>
     *     <td>&nbsp;{@link #nativeStreamMetadataFormatName}&nbsp;</td>
     *     <td>&nbsp;{@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#GEOTK_FORMAT_NAME}&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #nativeImageMetadataFormatName}&nbsp;</td>
     *     <td>&nbsp;{@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#GEOTK_FORMAT_NAME}&nbsp;</td>
     * </tr>
     * </table>
     * <p>
     * All other fields are left to their default values ({@code null} or {@code false}).
     * Subclasses are responsible for initializing those fields.
     * Some subclasses may also restore the {@link #nativeStreamMetadataFormatName} to
     * {@code null} if they do not support stream metadata.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.07
     *
     * @see SpatialImageReader.Spi
     *
     * @since 3.07
     * @module
     */
    protected abstract static class Spi extends ImageWriterSpi {
        /**
         * Initializes a default provider for {@link SpatialImageWriter}s.
         * <p>
         * For efficiency reasons, the fields are initialized to a shared array.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        protected Spi() {
            nativeStreamMetadataFormatName = GEOTK_FORMAT_NAME;
            nativeImageMetadataFormatName  = GEOTK_FORMAT_NAME;
        }

        /**
         * Returns {@code true} if this provider supports the {@linkplain SpatialMetadataFormat
         * spatial metadata format}. This method checks if a native or extra metadata format
         * named {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#GEOTK_FORMAT_NAME}
         * is declared. This is the case by default unless sublcasses modified the values of
         * the {@code xxxFormatName} fields.
         *
         * @param  stream {@code true} for testing stream metadata, or {@code false} for testing
         *         image metadata.
         * @return {@code true} if the provider declares to support the spatial metadata format.
         *
         * @see SpatialImageWriter#isSpatialMetadataSupported(boolean)
         */
        final boolean isSpatialMetadataSupported(final boolean stream) {
            final String   nativeFormat;
            final String[] extraFormats;
            if (stream) {
                nativeFormat = nativeStreamMetadataFormatName;
                extraFormats = extraStreamMetadataFormatNames;
            } else {
                nativeFormat = nativeImageMetadataFormatName;
                extraFormats = extraImageMetadataFormatNames;
            }
            return GEOTK_FORMAT_NAME.equals(nativeFormat) || XArrays.contains(extraFormats, GEOTK_FORMAT_NAME);
        }

        /**
         * Returns a description of the stream metadata of the given name.
         * If no description is available, then this method returns {@code null}.
         *
         * @param  formatName The desired stream metadata format.
         * @return The stream metadata format of the given name.
         */
        @Override
        public IIOMetadataFormat getStreamMetadataFormat(final String formatName) {
            if (GEOTK_FORMAT_NAME.equals(formatName) && isSpatialMetadataSupported(true)) {
                return SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME);
            }
            return super.getStreamMetadataFormat(formatName);
        }

        /**
         * Returns a description of the image metadata of the given name.
         * If no description is available, then this method returns {@code null}.
         *
         * @param  formatName The desired image metadata format.
         * @return The image metadata format of the given name.
         */
        @Override
        public IIOMetadataFormat getImageMetadataFormat(final String formatName) {
            if (GEOTK_FORMAT_NAME.equals(formatName) && isSpatialMetadataSupported(true)) {
                return SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME);
            }
            return super.getStreamMetadataFormat(formatName);
        }
    }
}

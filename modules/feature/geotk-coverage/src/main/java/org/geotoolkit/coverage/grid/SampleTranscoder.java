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
package org.geotoolkit.coverage.grid;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.RasterFormatException;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;
import javax.media.jai.iterator.RectIterFactory;
import javax.media.jai.iterator.WritableRectIter;
import javax.media.jai.registry.RenderedRegistryMode;

import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.image.TransfertRectIter;

import org.apache.sis.measure.NumberRange;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.Classes;



/**
 * An image that contains transformed samples.   It may be sample values after their
 * transformation to geophyics values, or the converse. Images are created using the
 * {@code SampleTranscoder.CRIF} inner class, where "CRIF" stands for
 * {@link java.awt.image.renderable.ContextualRenderedImageFactory}. The image
 * operation name is {@code "org.geotoolkit.SampleTranscode"}.
 *
 * @deprecated To be removed.
 */
@Deprecated
final class SampleTranscoder extends PointOpImage {
    /**
     * The operation name.
     */
    public static final String OPERATION_NAME = "org.geotoolkit.SampleTranscode";

    /**
     * Category lists for each bands.
     * The array length must matches the number of bands in source image.
     */
    private final SampleDimension[] categories;

    /**
     * {@code true} if the buffer is of kind {@code TYPE_USHORT} and the sample values
     * should be forced to signed integers.
     *
     * @since 3.11
     */
    private final boolean forceSigned;

    /**
     * Constructs a new {@code SampleTranscoder}.
     *
     * @param image      The source image.
     * @param categories The category lists, one for each image's band.
     * @param hints      The rendering hints.
     */
    private SampleTranscoder(final RenderedImage  image,
                             final SampleDimension[] categories,
                             final RenderingHints hints)
    {
        super(image, (ImageLayout) hints.get(JAI.KEY_IMAGE_LAYOUT), hints, false);
        this.categories = categories;
        if (categories.length != image.getSampleModel().getNumBands()) {
            // Should not happen, since SampleDimension$Descriptor has already checked it.
            throw new RasterFormatException(String.valueOf(categories.length));
        }
        boolean forceSigned = false;
        if (image.getSampleModel().getDataType() == DataBuffer.TYPE_USHORT) {
            for (final SampleDimension list : categories) {
                Optional<NumberRange<?>> range = list.getSampleRange();
                if (range.isPresent() && range.get().getMinDouble() < 0) {
                    forceSigned = true;
                    break;
                }
            }
        }
        this.forceSigned = forceSigned;
        permitInPlaceOperation();
    }

    /**
     * Returns the source images.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Vector<RenderedImage> getSources() {
        return super.getSources();
    }

    /**
     * Computes one of the destination image tile.
     *
     * @todo If the destination image is a single-banded, non-interleaved sample model, we could
     *       apply the transform directly in the {@link java.awt.image.DataBuffer}. We can even
     *       avoid to copy sample value if source and destination raster are the same.
     *
     * @param sources  An array of length 1 with source image.
     * @param dest     The destination tile.
     * @param destRect the rectangle within the destination to be written.
     */
    @Override
    protected void computeRect(final PlanarImage[] sources,
                               final WritableRaster   dest,
                               final Rectangle    destRect)
    {
        final PlanarImage source = sources[0];
        final Rectangle bounds = destRect.intersection(source.getBounds());
        assert destRect.equals(bounds) : destRect;
        final WritableRectIter iterator;
        if (forceSigned) {
            iterator = new SignedRectIter(
                    RectIterFactory.create(source, bounds),
                    RectIterFactory.createWritable(dest, bounds));
        } else {
            iterator = TransfertRectIter.create(source, dest, bounds);
        }
        int band = 0;
        if (!iterator.finishedBands()) do {
            categories[band++].getTransferFunction().ifPresent((t) -> transform(t, iterator));
        }
        while (!iterator.nextBandDone());
        assert band == categories.length : band;
    }

    /**
     * Transforms a raster. Only the current band in {@code iterator} will be transformed.
     * The transformed value are written back in the {@code iterator}. If a different
     * destination raster is wanted, a {@link org.geotoolkit.image.TransfertRectIter}
     * may be used.
     *
     * @param  iterator An iterator to iterate among the samples to transform.
     * @throws RasterFormatException if a problem occurs during the transformation.
     */
    private static void transform(final MathTransform1D tf, final WritableRectIter iterator) {
        try {
            iterator.startLines();
            if (!iterator.finishedLines()) do {
                iterator.startPixels();
                if (!iterator.finishedPixels()) do {
                    double value = iterator.getSampleDouble();
                    value = tf.transform(value);
                    iterator.setSample(value);
                }
                while (!iterator.nextPixelDone());
            }
            while (!iterator.nextLineDone());
        } catch (TransformException cause) {
            RasterFormatException exception = new RasterFormatException(Errors.format(
                    Errors.Keys.IllegalTransformForType_1, Classes.getClass(tf)));
            exception.initCause(cause);
            throw exception;
        }
    }




    /////////////////////////////////////////////////////////////////////////////////
    ////////                                                                 ////////
    ////////        REGISTRATION OF "SampleTranscode" IMAGE OPERATION        ////////
    ////////                                                                 ////////
    /////////////////////////////////////////////////////////////////////////////////
    /**
     * The operation descriptor for the "SampleTranscode" operation. This operation can apply the
     * {@link SampleDimension#getSampleToGeophysics sampleToGeophysics} transform on all pixels
     * in all bands of an image. The transformations are supplied as a list of
     * {@link SampleDimension}s, one for each band. The supplied {@code SampleDimension}
     * objects describe the categories in the <strong>source</strong> image. The target image
     * will matches sample dimension
     *
     *     <code>{@link SampleDimension#geophysics geophysics}(!isGeophysics)</code>,
     *
     * where {@code isGeophysics} is the previous state of the sample dimension.
     */
    private static final class Descriptor extends OperationDescriptorImpl {
        /**
         * For cross-version serialization.
         */
        private static final long serialVersionUID = -4204913600785080791L;

        /**
         * Construct the descriptor.
         */
        public Descriptor() {
            super(new String[][]{{"GlobalName",  OPERATION_NAME},
                                 {"LocalName",   OPERATION_NAME},
                                 {"Vendor",      "Geotoolkit.org"},
                                 {"Description", "Transformation from sample to geophysics values"},
                                 {"DocURL",      "http://www.geotoolkit.org/"},
                                 {"Version",     "1.0"}},
                  new String[]   {RenderedRegistryMode.MODE_NAME}, 1,
                  new String[]   {"sampleDimensions"},          // Argument names
                  new Class<?>[] {SampleDimension[].class},     // Argument classes
                  new Object[]   {NO_PARAMETER_DEFAULT},        // Default values for parameters,
                  null // No restriction on valid parameter values.
            );
        }

        /**
         * Returns {@code true} if the parameters are valid. This implementation check
         * that the number of bands in the source image is equals to the number of supplied
         * sample dimensions, and that all sample dimensions has categories.
         *
         * @param modeName The mode name (usually "Rendered").
         * @param param The parameter block for the operation to performs.
         * @param message A buffer for formatting an error message if any.
         */
        @Override
        protected boolean validateParameters(final String modeName,
                final ParameterBlock param, final StringBuffer message)
        {
            if (!super.validateParameters(modeName, param, message)) {
                return false;
            }
            final RenderedImage source = (RenderedImage) param.getSource(0);
            final SampleDimension[] bands = (SampleDimension[]) param.getObjectParameter(0);
            final int numBands = source.getSampleModel().getNumBands();
            if (numBands != bands.length) {
                message.append(Errors.format(Errors.Keys.MismatchedNumberOfBands_3,
                        numBands, bands.length, "SampleDimension"));
                return false;
            }
            for (int i=0; i<numBands; i++) {
                if (bands[i].getCategories() == null) {
                    message.append(Errors.format(Errors.Keys.IllegalParameterValue_2,
                            "sampleDimensions["+i+"].categories", null));
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * The {@link java.awt.image.renderable.RenderedImageFactory}
     * for the {@code "SampleTranscode"} operation.
     */
    private static final class CRIF extends CRIFImpl {
        /**
         * Creates a {@link RenderedImage} representing the results of an imaging
         * operation for a given {@link ParameterBlock} and {@link RenderingHints}.
         */
        @Override
        public RenderedImage create(final ParameterBlock param, final RenderingHints hints) {
            final RenderedImage image = (RenderedImage) param.getSource(0);
            final SampleDimension[] bands = (SampleDimension[]) param.getObjectParameter(0);
            if (image instanceof SampleTranscoder) {
                final SampleTranscoder other = (SampleTranscoder) image;
                if (isInverse(bands, other.categories)) {
                    return other.getSourceImage(0);
                }
            }
            return new SampleTranscoder(image, bands, hints);
        }

        /**
         * Checks if all categories in {@code categories1} are
         * equals to the inverse of {@code categories2}.
         */
        private static boolean isInverse(final SampleDimension[] categories1,
                                         final SampleDimension[] categories2)
        {
            if (categories1.length != categories2.length) {
                return false;
            }
            for (int i=0; i<categories1.length; i++) {
                // TODO: this is not a good check.
                if (!categories1[i].forConvertedValues(false).equals(categories2[i].forConvertedValues(false))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Register the "SampleTranscode" image operation to the operation registry of
     * the specified JAI instance. This method is invoked by the static initializer
     * of {@link SampleDimension}.
     */
    public static void register(final JAI jai) {
        final OperationRegistry registry = jai.getOperationRegistry();
        try {
            registry.registerDescriptor(new Descriptor());
            registry.registerFactory(RenderedRegistryMode.MODE_NAME, OPERATION_NAME,
                    org.geotoolkit.image.internal.Setup.PRODUCT_NAME, new CRIF());
        } catch (IllegalArgumentException exception) {
            final LogRecord record = Loggings.format(Level.SEVERE,
                   Loggings.Keys.CantRegisterJaiOperation_1, OPERATION_NAME);
            // Note: SampleDimension is the public class that use this transcoder.
            record.setSourceClassName(SampleDimension.class.getName());
            record.setSourceMethodName("<classinit>");
            record.setThrown(exception);
            final Logger logger = Logging.getLogger("org.geotoolkit.coverage");
            record.setLoggerName(logger.getName());
            logger.log(record);
        }
    }
}

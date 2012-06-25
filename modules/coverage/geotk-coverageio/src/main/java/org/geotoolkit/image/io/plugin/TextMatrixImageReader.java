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
package org.geotoolkit.image.io.plugin;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Collections;
import java.text.ParseException;
import java.io.IOException;
import java.io.BufferedReader;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.Version;
import org.geotoolkit.io.LineFormat;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.image.io.TextImageReader;
import org.geotoolkit.image.io.SampleConverter;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.DimensionAccessor;


/**
 * An image decoder for matrix of floating-point numbers. The default implementation creates
 * rasters of {@link DataBuffer#TYPE_FLOAT}. An easy way to change this type is to overwrite
 * the {@link #getRawDataType} method.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.08
 *
 * @see TextMatrixImageWriter
 *
 * @since 3.08 (derived from 1.2)
 * @module
 */
public class TextMatrixImageReader extends TextImageReader {
    /**
     * The matrix data loaded by {@link #load} method.
     */
    private float[] data;

    /**
     * The image width. This number is valid only if {@link #data} is non-null
     * or {@link #completed} is {@code true}.
     */
    private int width;

    /**
     * The image height. If only a part of the image has been read (typically only the first line
     * in order to determine the value of {@link #width}), then this is the number of lines read
     * so far. This field is the actual image height only when {@link #completed} is true.
     */
    private int height;

    /**
     * The expected height, or 0 if unknown. This number
     * has no signification if {@link #data} is null.
     */
    private int expectedHeight;

    /**
     * {@code true} if {@link #data} contains all data, or {@code false} if {@link #data}
     * contains only the first line. Note that this field may be {@code true} while the
     * {@link #data} are {@code null} if the {@link #width} and {@link #height} fields
     * are still valids.
     */
    private boolean completed;

    /**
     * Constructs a new image reader.
     *
     * @param provider The {@link ImageReaderSpi} that is constructing this object, or {@code null}.
     */
    protected TextMatrixImageReader(final Spi provider) {
        super(provider);
    }

    /**
     * Loads data. No subsampling is performed, and the pad value is not replaced by NaN.
     * This method sets the {@link #width} fields (or ensure that every row have a length
     * equals to {@code width} if at least one row was already read) and increment the
     * {@link #height} field by the amount of row read.
     * <p>
     * Once this method complete, the {@link #completed} field is set to the value of
     * {@code all} parameter.
     *
     * @param  imageIndex the index of the image to be read.
     * @param  all {@code true} to read all data, or {@code false} to read only one line.
     * @return {@code true} if reading has been aborted.
     * @throws IOException If an error occurs reading the width information from the input source.
     */
    private boolean load(final int imageIndex, final boolean all) throws IOException {
        clearAbortRequest();
        if (all) {
            processImageStarted(imageIndex);
        }
        // If some rows were already read, a non-null array
        // will force the next rows to have the same length.
        float[] values = (data != null) ? new float[width] : null;
        // If some data was already read, the offset where to continue. Otherwise 0.
        int offset = width * height;
        final BufferedReader input = getReader();
        final LineFormat format = getLineFormat(imageIndex);
        String line; while ((line = input.readLine()) != null) {
            if (isComment(line)) {
                continue;
            }
            try {
                format.setLine(line);
                values = format.getValues(values);
            } catch (ParseException exception) {
                throw new IIOException(getPositionString(exception.getLocalizedMessage()), exception);
            }
            final int newOffset = offset + (width = values.length);
            /*
             * Try to guess the expected height after the first line, then try to allocate
             * the right amout of memory. If the guess is not accurate, the amount of memory
             * will be adjusted as needed.
             */
            if (data == null) {
                final long streamLength = getStreamLength();
                if (streamLength >= 0) {
                    final int length = line.length() + 1; // Add 1 for the EOL character.
                    expectedHeight = (int) ((streamLength + length/2) / length);
                }
                data = new float[Math.max(1024, width * expectedHeight)];
            } else if (newOffset > data.length) {
                data = Arrays.copyOf(data, newOffset * 2);
            }
            System.arraycopy(values, 0, data, offset, width);
            offset = newOffset;
            height++;
            if (!all) {
                return false;
            }
            /*
             * Update progress.
             */
            if (height <= expectedHeight) {
                processImageProgress(height * 100f / expectedHeight);
            }
            if (abortRequested()) {
                processReadAborted();
                return true;
            }
        }
        data = XArrays.resize(data, offset);
        expectedHeight = height;
        completed = true;
        if (all) {
            processImageComplete();
        }
        return false;
    }

    /**
     * Returns the width in pixels of the given image within the input source.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return Image width.
     * @throws IOException If an error occurs reading the width information
     *         from the input source.
     */
    @Override
    public int getWidth(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        /*
         * The line below use && instead of || because we don't need the complete
         * set of data. The first line is enough, which is indicated by a non-null
         * data array with 'completed' set to false.
         */
        if (data == null && !completed) {
            load(imageIndex, false);
        }
        return width;
    }

    /**
     * Returns the height in pixels of the given image within the input source.
     * Calling this method may force loading of full image.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return Image height.
     * @throws IOException If an error occurs reading the height information
     *         from the input source.
     */
    @Override
    public int getHeight(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        if (!completed) {
            load(imageIndex, true);
        }
        return height;
    }

    /**
     * Returns metadata associated with the given image.
     * Calling this method may force loading of full image.
     *
     * @param  imageIndex The image index.
     * @return The metadata, or {@code null} if none.
     * @throws IOException If an error occurs reading the data information from the input source.
     */
    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        if (imageIndex >= 0) {
            if (data == null || !completed) {
                if (load(imageIndex, true)) {
                    return null;
                }
            }
            final float padValue = (float) getPadValue(imageIndex);
            float minimum = Float.POSITIVE_INFINITY;
            float maximum = Float.NEGATIVE_INFINITY;
            for (int i=0; i<data.length; i++) {
                final float value = data[i];
                if (value != padValue) {
                    if (value < minimum) minimum = value;
                    if (value > maximum) maximum = value;
                }
            }
            final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(null), this, null);
            final DimensionAccessor accessor = new DimensionAccessor(metadata);
            accessor.selectChild(accessor.appendChild());
            if (minimum < maximum) {
                accessor.setValueRange(minimum, maximum);
            }
            if (!Float.isNaN(padValue)) {
                accessor.setFillSampleValues(padValue);
            }
            return metadata;
        }
        return super.createMetadata(imageIndex);
    }

    /**
     * Reads the image indexed by {@code imageIndex}.
     *
     * @param  imageIndex  The index of the image to be retrieved.
     * @param  param       Parameters used to control the reading process, or null.
     * @return The desired portion of the image.
     * @throws IOException if an input operation failed.
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        /*
         * Parameters check.
         */
        final int numSrcBands = 1; // To be modified in a future version if we support multi-bands.
        final int numDstBands = 1;
        checkImageIndex(imageIndex);
        checkReadParamBandSettings(param, numSrcBands, numDstBands);
        /*
         * Extract user's parameters.
         */
        final int[]      sourceBands; // To be used in a future version if we support multi-bands.
        final int[] destinationBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        final int subsamplingXOffset;
        final int subsamplingYOffset;
        final int destinationXOffset;
        final int destinationYOffset;
        if (param != null) {
            sourceBands        = param.getSourceBands();
            destinationBands   = param.getDestinationBands();
            final Point offset = param.getDestinationOffset();
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();
            subsamplingXOffset = param.getSubsamplingXOffset();
            subsamplingYOffset = param.getSubsamplingYOffset();
            destinationXOffset = offset.x;
            destinationYOffset = offset.y;
        } else {
            sourceBands        = null;
            destinationBands   = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
            subsamplingXOffset = 0;
            subsamplingYOffset = 0;
            destinationXOffset = 0;
            destinationYOffset = 0;
        }
        final int dstBand;
        if (destinationBands == null) {
            dstBand = 0;
        } else {
            dstBand = destinationBands[0];
        }
        /*
         * Compute source region and check for possible optimization.
         */
        final Rectangle srcRegion = getSourceRegion(param, width, height);
        final boolean isDirect =
                sourceXSubsampling == 1 && sourceYSubsampling == 1   &&
                subsamplingXOffset == 0 && subsamplingYOffset == 0   &&
                destinationXOffset == 0 && destinationYOffset == 0   &&
                srcRegion.x        == 0 && srcRegion.width  == width &&
                srcRegion.y        == 0 && srcRegion.height == height;
        /*
         * Read data if it was not already done.
         */
        if (data == null || !completed) {
            if (load(imageIndex, true)) {
                return null;
            }
        }
        final float[] data   = this.data;
        final int     width  = this.width;
        final int     height = this.height;
        /*
         * Get the converter of sample values. In most cases, it will
         * just replace pad value (e.g. -9999) by NaN value.
         */
        final SampleConverter[] converters = new SampleConverter[numDstBands];
        final ImageTypeSpecifier type = getImageType(imageIndex, param, converters);
        final SampleConverter converter = converters[0];
        /*
         * If a direct mapping is possible, perform it. If a direct mapping is performed,
         * we will need to set the data array to null (and consequently force a new data
         * loading if the image is requested again) because the user could have modified
         * the sample values.
         */
        if (isDirect && (param == null || param.getDestination() == null) &&
                type.getSampleModel().getDataType() == DataBuffer.TYPE_FLOAT)
        {
            if (!SampleConverter.IDENTITY.equals(converter)) {
                for (int i=0; i<data.length; i++) {
                    data[i] = converter.convert(data[i]);
                }
            }
            final SampleModel    model  = type.getSampleModel(width, height);
            final DataBuffer     buffer = new DataBufferFloat(data, data.length);
            final WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
            this.data = null; // See the above block comment.
            minIndex = imageIndex + 1;
            return new BufferedImage(type.getColorModel(), raster, false, null);
        }
        /*
         * Copy data into a new image.
         */
        final BufferedImage  image = getDestination(param, Collections.singleton(type).iterator(), width, height);
        final WritableRaster dstRaster = image.getRaster();
        final Rectangle      dstRegion = new Rectangle();
        computeRegions(param, width, height, image, srcRegion, dstRegion);
        final int dstXMin = dstRegion.x;
        final int dstYMin = dstRegion.y;
        final int dstXMax = dstRegion.width  + dstXMin;
        final int dstYMax = dstRegion.height + dstYMin;

        int srcY = srcRegion.y;
        for (int y=dstYMin; y<dstYMax; y++) {
            assert srcY < srcRegion.y + srcRegion.height;
            final int offset = srcY * width;
            int srcX = srcRegion.x;
            for (int x=dstXMin; x<dstXMax; x++) {
                assert srcX < srcRegion.x + srcRegion.width;
                final float value = converter.convert(data[offset + srcX]);
                dstRaster.setSample(x, y, dstBand, value);
                srcX += sourceXSubsampling;
            }
            srcY += sourceYSubsampling;
        }
        return image;
    }

    /**
     * Closes the input stream and disposes the resources that was specific to that stream.
     *
     * @throws IOException If an error occurred while closing the reader.
     */
    @Override
    protected void close() throws IOException {
        completed      = false;
        data           = null;
        width          = 0;
        height         = 0;
        expectedHeight = 0;
        super.close();
    }




    /**
     * Service provider interface (SPI) for {@code TextMatrixImageReader}s. This SPI provides
     * the necessary implementation for creating default {@link TextMatrixImageReader} using
     * default locale and character set. The default constructor initializes the fields to the
     * values listed below:
     * <p>
     * <table border="1" cellspacing="0">
     *   <tr bgcolor="lightblue"><th>Field</th><th>Value</th></tr>
     *   <tr><td>&nbsp;{@link #names}           &nbsp;</td><td>&nbsp;{@code "matrix"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #MIMETypes}       &nbsp;</td><td>&nbsp;{@code "text/plain"}, {@code "text/x-matrix"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #pluginClassName} &nbsp;</td><td>&nbsp;{@code "org.geotoolkit.image.io.plugin.TextMatrixImageReader"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #vendorName}      &nbsp;</td><td>&nbsp;{@code "Geotoolkit.org"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #version}         &nbsp;</td><td>&nbsp;{@link Version#GEOTOOLKIT}&nbsp;</td></tr>
     *   <tr><td colspan="2" align="center">See
     *   {@linkplain org.geotoolkit.image.io.TextImageReader.Spi super-class javadoc} for remaining fields</td></tr>
     * </table>
     * <p>
     * Subclasses can set some fields at construction time
     * in order to tune the reader to a particular environment, e.g.:
     *
     * {@preformat java
     *     public final class MyCustomSpi extends TextMatrixImageReader.Spi {
     *         public MyCustomSpi() {
     *             names      = new String[] {"myformat"};
     *             MIMETypes  = new String[] {"text/plain"};
     *             vendorName = "Foo inc.";
     *             version    = "1.0";
     *             locale     = Locale.US;
     *             charset    = Charset.forName("ISO-8859-1"); // ISO-LATIN-1
     *             padValue   = -9999;
     *         }
     *     }
     * }
     *
     * {@note fields <code>vendorName</code> and <code>version</code> are only informatives.}
     *
     * There is no need to override any method in this example. However, developers
     * can gain more control by creating subclasses of {@link TextMatrixImageReader}
     * and {@code Spi}.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.08
     *
     * @see TextMatrixImageWriter.Spi
     *
     * @since 3.08 (derived from 2.1)
     * @module
     */
    public static class Spi extends TextImageReader.Spi {
        /**
         * The format names for the default {@link TextMatrixImageReader} configuration.
         */
        static final String[] NAMES = {"matrix"};

        /**
         * The mime types for the default {@link TextMatrixImageReader} configuration.
         */
        static final String[] MIME_TYPES = {"text/plain", "text/x-matrix"};

        /**
         * The provider of the corresponding image writer.
         */
        private static final String[] WRITERS = {"org.geotoolkit.image.io.plugin.TextMatrixImageWriter$Spi"};

        /**
         * Constructs a default {@code TextMatrixImageReader.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can modify
         * those values if desired.
         * <p>
         * For efficiency reasons, the above fields are initialized to shared arrays. Subclasses
         * can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            names           = NAMES;
            MIMETypes       = MIME_TYPES;
            pluginClassName = "org.geotoolkit.image.io.plugin.TextMatrixImageReader";
            writerSpiNames  = WRITERS;
            vendorName      = "Geotoolkit.org";
            version         = Version.GEOTOOLKIT.toString();
        }

        /**
         * Returns a brief, human-readable description of this service provider
         * and its associated implementation. The resulting string should be
         * localized for the supplied locale, if possible.
         *
         * @param  locale A Locale for which the return value should be localized.
         * @return A String containing a description of this service provider.
         */
        @Override
        public String getDescription(final Locale locale) {
            return Descriptions.getResources(locale).getString(Descriptions.Keys.CODEC_MATRIX);
        }

        /**
         * Returns an instance of the {@code ImageReader} implementation associated
         * with this service provider.
         *
         * @param  extension An optional extension object, which may be null.
         * @return An image reader instance.
         * @throws IOException if the attempt to instantiate the reader fails.
         */
        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new TextMatrixImageReader(this);
        }
    }
}

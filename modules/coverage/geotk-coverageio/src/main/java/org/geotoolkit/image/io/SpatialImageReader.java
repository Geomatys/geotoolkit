/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collections;
import java.util.logging.LogRecord;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.metadata.IIOMetadataFormat;

import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.resources.Locales;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SampleDimension;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.internal.image.io.Warnings;


/**
 * Base class for readers of spatial (usually geographic) data. This class extends the standard
 * {@link ImageReader} class in order to improve the support of file formats having the following
 * characteristics:
 * <p>
 * <ul>
 *   <li>Images may have metadata information that can be represented as ISO 19115-2 objects.</li>
 *   <li>Images may have no color information (e.g. RAW, ASCII or NetCDF files), in which case a
 *       colors palette can be specified in {@linkplain SpatialImageReadParam parameters}.</li>
 *   <li>Pixel values may be signed integers, in which case an offset needs to be applied
 *       since {@link java.awt.image.IndexColorModel} does not support negative values.</li>
 *   <li>Pixel values may be floating point values, in which case a non-standard color space
 *       is required.</li>
 * </ul>
 *
 * {@section New API}
 * This class provides the following API, which are new compared to the standard {@link ImageReader}
 * class:
 * <p>
 * <ul>
 *   <li>The return type of {@link #getStreamMetadata()} and {@link #getImageMetadata(int)} is
 *     restricted to {@link SpatialMetadata}.</li>
 *   <li>The return type of {@link #getDefaultReadParam()} is restricted to
 *     {@link SpatialImageReadParam}.</li>
 *   <li>A new method, {@link #getNumBands(int)}, returns the number of bands in the specified
 *     image. Note that the bands may not contain color components in scientific dataset.</li>
 *   <li>A new method, {@link #getDimension(int)}, returns the dimension of the given image.
 *     In some formats like NetCDF, an "image" is actually a dataset which may have more than
 *     2 dimensions.</li>
 * </ul>
 *
 * {@section Services for implementors}
 * This class provides the following conveniences for implementors. Note that the default behavior
 * described below assumes the simpliest file format: one image made of one band of floating point
 * values using a grayscale color palette scaled to fit the range of sample values. This behavior
 * can be changed by overriding the methods listed below.
 *
 * <ul>
 *   <li><p>Provides default {@link #getNumImages(boolean)} and {@link #getNumBands(int)}
 *     implementations, which return 1. This default behavior matches simple image formats
 *     like {@linkplain org.geotoolkit.image.io.plugin.RawImageReader RAW} or
 *     {@linkplain org.geotoolkit.image.io.plugin.AsciiGridReader ASCII} files.
 *     Those methods need to be overriden for more complex image formats.</p></li>
 *
 *   <li><p>Provides {@link #checkImageIndex(int)} and {@link #checkBandIndex(int,int)} convenience
 *     methods. Those methods are invoked by most implementation of public methods. They perform
 *     their checks based on the informations provided by the above-cited {@link #getNumImages(boolean)}
 *     and {@link #getNumBands(int)} methods.</p></li>
 *
 *   <li><p>Provides default implementations of {@link #getImageTypes(int)} and
 *     {@link #getRawImageType(int)} methods, which assume that only one image type is offered.
 *     The offered type is described by a default {@linkplain ImageTypeSpecifier image type
 *     specifier} created from the informations provided by {@link #getRawDataType(int)} and
 *     {@link #getImageMetadata(int)}.</p></li>
 *
 *   <li><p>Provides {@link #getStreamMetadata()} and {@link #getImageMetadata(int)} default
 *     implementations, which return {@code null} as authorized by the specification.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.08
 *
 * @see SpatialImageWriter
 *
 * @since 3.06 (derived from 1.2)
 * @module
 */
public abstract class SpatialImageReader extends ImageReader implements WarningProducer {
    /**
     * Stream and image metadata for each images, or {@code null} if not yet created.
     * The element at index 0 is the stream metadata, and next elements are image metadata
     * for the image at {@code index}-1.
     */
    private SpatialMetadata[] metadata;

    /**
     * Constructs a new image reader.
     *
     * @param provider The {@link ImageReaderSpi} that is constructing this object, or {@code null}.
     */
    protected SpatialImageReader(final Spi provider) {
        super(provider);
        availableLocales = Locales.getAvailableLocales();
    }

    /**
     * Sets the input source to use. If this image reader is an instance of
     * {@link StreamImageReader} or {@link ImageReaderAdapter}, then their
     * {@code close()} method is invoked before to set the new input.
     *
     * @param input           The input object to use for future decoding.
     * @param seekForwardOnly If {@code true}, images and metadata may only be read
     *                        in ascending order from this input source.
     * @param ignoreMetadata  If {@code true}, metadata may be ignored during reads.
     */
    @Override
    public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
        closeSilently();
        super.setInput(input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Returns the resources for formatting error messages.
     */
    final IndexedResourceBundle getErrorResources() {
        return Errors.getResources(getLocale());
    }

    /**
     * Ensures that the specified image index is inside the expected range.
     * The expected range is {@link #minIndex minIndex} inclusive (initially 0)
     * to <code>{@link #getNumImages getNumImages}(false)</code> exclusive.
     *
     * @param  imageIndex Index to check for validity.
     * @throws IndexOutOfBoundsException if the specified index is outside the expected range.
     * @throws IOException If the operation failed because of an I/O error.
     */
    protected void checkImageIndex(final int imageIndex) throws IOException, IndexOutOfBoundsException {
        final int numImages = getNumImages(false);
        final int minIndex  = getMinIndex();
        if (imageIndex < minIndex || (imageIndex >= numImages && numImages >= 0)) {
            throw new IndexOutOfBoundsException(indexOutOfBounds(imageIndex, minIndex, numImages));
        }
    }

    /**
     * Ensures that the specified band index is inside the expected range. The expected
     * range is 0 inclusive to <code>{@link #getNumBands getNumBands}(imageIndex)</code>
     * exclusive.
     *
     * @param  imageIndex The image index.
     * @param  bandIndex Index to check for validity.
     * @throws IndexOutOfBoundsException if the specified index is outside the expected range.
     * @throws IOException If the operation failed because of an I/O error.
     */
    protected void checkBandIndex(final int imageIndex, final int bandIndex)
            throws IOException, IndexOutOfBoundsException
    {
        // Call 'getNumBands' first in order to call 'checkImageIndex'.
        final int numBands = getNumBands(imageIndex);
        if (bandIndex >= numBands || bandIndex < 0) {
            throw new IndexOutOfBoundsException(indexOutOfBounds(bandIndex, 0, numBands));
        }
    }

    /**
     * Formats an error message for an index out of bounds.
     *
     * @param index The index out of bounds.
     * @param lower The lower legal value, inclusive.
     * @param upper The upper legal value, exclusive.
     */
    private String indexOutOfBounds(final int index, final int lower, final int upper) {
        return getErrorResources().getString(Errors.Keys.VALUE_OUT_OF_BOUNDS_$3,
                index, (lower < upper) ? lower : "EOF", upper-1);
    }

    /**
     * Returns the number of images available from the current input source.
     * The default implementation returns 1.
     *
     * @param  allowSearch If true, the number of images will be returned
     *         even if a search is required.
     * @return The number of images, or -1 if {@code allowSearch}
     *         is false and a search would be required.
     *
     * @throws IllegalStateException if the {@linkplain #input input} source has not been set.
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getNumImages(final boolean allowSearch) throws IllegalStateException, IOException {
        if (input != null) {
            return 1;
        }
        throw new IllegalStateException(getErrorResources().getString(Errors.Keys.NO_IMAGE_INPUT));
    }

    /**
     * Returns the number of bands available for the specified image.
     * The default implementation returns 1.
     *
     * @param  imageIndex The image index.
     * @return The number of bands available for the specified image.
     * @throws IOException if an error occurs reading the information from the input source.
     */
    public int getNumBands(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return 1;
    }

    /**
     * Returns the number of dimension of the image at the given index.
     * The default implementation returns 2.
     *
     * @param  imageIndex The image index.
     * @return The number of dimension for the image at the given index.
     * @throws IOException if an error occurs reading the information from the input source.
     *
     * @since 2.5
     */
    public int getDimension(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return 2;
    }

    /**
     * Returns metadata associated with the input source as a whole.
     * The default implementation performs the following choice:
     * <p>
     * <ul>
     *   <li>If the metadata from a previous call were cached, return those metadata.</li>
     *   <li>Otherwise if {@link #isIgnoringMetadata()} is {@code true}, return {@code null}.</li>
     *   <li>Otherwise invoke <code>{@linkplain #createMetadata(int) createMetadata}(-1)</code>
     *       and cache the result.</li>
     * </ul>
     *
     * @return The metadata, or {@code null} if none.
     * @throws IOException if an error occurs during reading.
     */
    @Override
    public SpatialMetadata getStreamMetadata() throws IOException {
        return getSpatialMetadata(-1);
    }

    /**
     * Returns metadata associated with the given image.
     * The default implementation performs the following choice:
     * <p>
     * <ul>
     *   <li>Invoke <code>{@linkplain #checkImageIndex(int) checkImageIndex}(imageIndex)</code>.</li>
     *   <li>If the metadata from a previous call were cached for the given index, return those metadata.</li>
     *   <li>Otherwise if {@link #isIgnoringMetadata()} is {@code true}, return {@code null}.</li>
     *   <li>Otherwise invoke <code>{@linkplain #createMetadata(int) createMetadata}(imageIndex)</code>
     *       and cache the result.</li>
     * </ul>
     *
     * @param  imageIndex The image index.
     * @return The metadata, or {@code null} if none.
     * @throws IOException if an error occurs during reading.
     */
    @Override
    public SpatialMetadata getImageMetadata(int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return getSpatialMetadata(imageIndex);
    }

    /**
     * Returns spatial metadata associated with the given image.
     * This method performs the following choice:
     * <p>
     * <ul>
     *   <li>If the metadata from a previous call were cached for the given index, return those metadata.</li>
     *   <li>Otherwise if {@link #isIgnoringMetadata()} is {@code true}, return {@code null}.</li>
     *   <li>Otherwise invoke <code>{@linkplain #createMetadata(int) createMetadata}(imageIndex)</code>
     *       and cache the result.</li>
     * </ul>
     *
     * @param  imageIndex The image index, or -1 for stream metadata.
     * @return The spatial metadata, or {@code null} if none.
     * @throws IOException if an error occurs during reading.
     */
    private SpatialMetadata getSpatialMetadata(final int imageIndex) throws IOException {
        /*
         * Checks if a cached instance is available.
         */
        final int cacheIndex = imageIndex + 1;
        if (metadata != null && cacheIndex >= 0 && cacheIndex < metadata.length) {
            final SpatialMetadata candidate = metadata[cacheIndex];
            if (candidate != null) {
                return (candidate != SpatialMetadata.EMPTY) ? candidate : null;
            }
        }
        if (isIgnoringMetadata()) {
            return null;
        }
        /*
         * Creates a new instance and cache it.
         */
        final SpatialMetadata candidate = createMetadata(imageIndex);
        if (candidate != null) {
            if (metadata == null) {
                metadata = new SpatialMetadata[Math.max(cacheIndex+1, 4)];
            }
            if (cacheIndex >= metadata.length) {
                metadata = Arrays.copyOf(metadata, Math.max(cacheIndex+1, metadata.length*2));
            }
            metadata[cacheIndex] = (candidate != null) ? candidate : SpatialMetadata.EMPTY;
        }
        return candidate;
    }

    /**
     * Creates a new stream or image metadata. This method is invoked by the default implementation
     * of {@link #getStreamMetadata()} and {@link #getImageMetadata(int)} when the requested metadata
     * were not cached by a previous call, and {@link #isIgnoringMetadata()} returns {@code false}.
     * <p>
     * The default implementation returns {@code null} if every cases. Subclasses should override
     * this method if they can provide metadata.
     *
     * @param  imageIndex -1 for stream metadata, or the image index for image metadata.
     * @return The requested metadata, or {@code null} if none.
     * @throws IOException If an error occured while reading metadata.
     */
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        return null;
    }

    /**
     * Returns a collection of {@link ImageTypeSpecifier} containing possible image types to which
     * the given image may be decoded. The default implementation returns a singleton containing
     * <code>{@link #getRawImageType(int) getRawImageType}(imageIndex)</code>.
     *
     * @param  imageIndex The index of the image to be retrieved.
     * @return A set of suggested image types for decoding the current given image.
     * @throws IOException If an error occurs reading the format information from the input source.
     */
    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(final int imageIndex) throws IOException {
        final ImageTypeSpecifier type = getRawImageType(imageIndex);
        final Set<ImageTypeSpecifier> types;
        if (type != null) {
            types = Collections.singleton(type);
        } else {
            // Should never occur in non-broken ImageReader, but experience
            // suggests that we are better to be assume that this case happen.
            types = Collections.emptySet();
        }
        return types.iterator();
    }

    /**
     * Returns an image type specifier indicating the {@link SampleModel} and {@link ColorModel}
     * which most closely represents the "raw" internal format of the image. The default
     * implementation delegates to the following:
     *
     * {@preformat java
     *     return getImageType(imageIndex, getDefaultReadParam(), null);
     * }
     *
     * If this method needs to be overriden, consider overriding the later instead.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The image type (never {@code null}).
     * @throws IOException If an error occurs reading the format information from the input source.
     *
     * @see #getImageType(int, ImageReadParam, SampleConverter[])
     * @see #getDefaultReadParam()
     */
    @Override
    public ImageTypeSpecifier getRawImageType(final int imageIndex) throws IOException {
        return getImageType(imageIndex, getDefaultReadParam(), null);
    }

    /**
     * Returns an image type specifier indicating the {@link SampleModel} and {@link ColorModel}
     * to use for reading the image. The default implementation applies the following steps:
     *
     * <ol>
     *   <li><p>The {@linkplain SampleDimension#getValidSampleValues() range of expected values}
     *       and the {@linkplain SampleDimension#getFillSampleValues() fill values} are extracted
     *       from the {@linkplain #getImageMetadata(int) image metadata}, if any.</p></li>
     *
     *   <li><p>If the given {@code parameters} argument is an instance of {@link SpatialImageReadParam},
     *       then the user-supplied {@linkplain SpatialImageReadParam#getPaletteName palette name}
     *       is fetched. Otherwise or if no palette name was explicitly set, then this method default
     *       to {@value org.geotoolkit.image.io.SpatialImageReadParam#DEFAULT_PALETTE_NAME}. The
     *       palette name will be used in order to {@linkplain PaletteFactory#getColors(String)
     *       read a predefined set of colors} (as [A]RGB values) to be given to the
     *       {@linkplain IndexColorModel index color model}.</p></li>
     *
     *   <li><p>If the {@linkplain #getRawDataType raw data type} is {@link DataBuffer#TYPE_FLOAT
     *       TYPE_FLOAT} or {@link DataBuffer#TYPE_DOUBLE TYPE_DOUBLE}, then this method builds
     *       a {@linkplain PaletteFactory#getContinuousPalette continuous palette} suitable for
     *       the range fetched at step 1. The data are assumed <cite>geophysics</cite> values
     *       rather than some packed values. Consequently, the {@linkplain SampleConverter sample
     *       converters} will replace no-data values by {@linkplain Float#NaN NaN}, but no other
     *       changes will be applied.</p></li>
     *
     *   <li><p>Otherwise, if the {@linkplain #getRawDataType raw data type} is a unsigned integer type
     *       like {@link DataBuffer#TYPE_BYTE TYPE_BYTE} or {@link DataBuffer#TYPE_USHORT TYPE_USHORT},
     *       then this method builds an {@linkplain PaletteFactory#getPalette indexed palette} (i.e. a
     *       palette backed by an {@linkplain IndexColorModel index color model}) with just the minimal
     *       {@linkplain IndexColorModel#getMapSize size} needed for containing fully the range and the
     *       no-data values fetched at step 1. The data are assumed <cite>packed</cite> values rather
     *       than geophysics values. Consequently, the {@linkplain SampleConverter sample converters}
     *       will be the {@linkplain SampleConverter#IDENTITY identity converter} except in the
     *       following cases:
     *       <ul>
     *         <li>The {@linkplain SampleDimension#getValidSampleValues() range of valid values} is
     *             outside the range allowed by the {@linkplain #getRawDataType raw data type} (e.g.
     *             the range of valid values contains negative integers). In this case, the sample
     *             converter will shift the values to a strictly positive range and replace fill
     *             values by 0.</li>
     *         <li>At least one {@linkplain SampleDimension#getFillSampleValues() fill value} is
     *             outside the range of values allowed by the {@linkplain #getRawDataType raw data
     *             type}. In this case, this method will try to only replace the fill values by 0,
     *             without shifting the valid values if this shift can be avoided.</li>
     *         <li>At least one {@linkplain SampleDimension#getFillSampleValues() fill value} is
     *             far away from the {@linkplain SampleDimension#getValidSampleValues() range of
     *             valid values} (for example 9999 while the range of valid values is [0&hellip;255]).
     *             The meaning of "far away" is determined by the {@link #collapseNoDataValues
     *             collapseNoDataValues} method.</li>
     *       </ul>
     *       </p></li>
     *
     *   <li><p>Otherwise, if the {@linkplain #getRawDataType raw data type} is a signed integer
     *       type like {@link DataBuffer#TYPE_SHORT TYPE_SHORT}, then this method builds an
     *       {@linkplain PaletteFactory#getPalette indexed palette} with the maximal {@linkplain
     *       IndexColorModel#getMapSize size} supported by the raw data type (note that this is
     *       memory expensive - typically 256 kilobytes). Negative values will be stored in their
     *       two's complement binary form in order to fit in the range of positive integers
     *       supported by the {@linkplain IndexColorModel index color model}.</p></li>
     * </ol>
     *
     * {@section Overriding this method}
     * Subclasses can override this method for example if the color {@linkplain Palette palette}
     * and range of values should be computed in a different way. The example below creates an
     * image type using hard-coded objects:
     *
     * {@preformat java
     *     int minimum     = -2000;      // Minimal expected value
     *     int maximum     = +2300;      // Maximal expected value
     *     int fillValue   = -9999;      // Value for missing data
     *     String colors   = "rainbow";  // Named set of RGB colors
     *     converters[0]   = SampleConverter.createOffset(1 - minimum, fillValue);
     *     Palette palette = PaletteFactory.getDefault().getPalettePadValueFirst(colors, maximum - minimum);
     *     return palette.getImageTypeSpecifier();
     * }
     *
     * @param imageIndex
     *          The index of the image to be queried.
     * @param parameters
     *          The user-supplied parameters, or {@code null}. Note: we recommand to supply
     *          {@link #getDefaultReadParam} instead of {@code null} since subclasses may
     *          override the later with default values suitable to a particular format.
     * @param converters
     *          If non-null, an array where to store the converters created by this method. The length
     *          of this array shall be equals to the number of target bands. The converters stored
     *          by this method in this array shall be used by {@link #read(int,ImageReadParam) read}
     *          method implementations for converting the values read in the datafile to values
     *          acceptable for the underling {@linkplain ColorModel color model}.
     * @return
     *          The image type (never {@code null}).
     * @throws IOException
     *          If an error occurs while reading the format information from the input source.
     *
     * @see #getRawDataType
     * @see #collapseNoDataValues
     * @see #getDestination(int, ImageReadParam, int, int, SampleConverter[])
     */
    protected ImageTypeSpecifier getImageType(final int               imageIndex,
                                              final ImageReadParam    parameters,
                                              final SampleConverter[] converters)
            throws IOException
    {
        ImageTypeSpecifier type = (parameters != null) ? parameters.getDestinationType() : null;
        /*
         * Gets the minimal and maximal values allowed for the target image type.
         * Note that this is meanless for floating point types, so the values in
         * that case are arbitrary.
         *
         * The only integer types that are signed are SHORT (not to be confused with
         * USHORT) and INT. Other types like BYTE and USHORT are treated as unsigned.
         */
        final boolean isFloat;
        final long floor, ceil;
        final int dataType = (type != null) ? type.getSampleModel().getDataType() : getRawDataType(imageIndex);
        switch (dataType) {
            case DataBuffer.TYPE_UNDEFINED: // Actually we don't really know what to do for this case...
            case DataBuffer.TYPE_DOUBLE:    // Fall through since we can treat this case as float.
            case DataBuffer.TYPE_FLOAT: {
                isFloat = true;
                floor   = Long.MIN_VALUE;
                ceil    = Long.MAX_VALUE;
                break;
            }
            case DataBuffer.TYPE_INT: {
                isFloat = false;
                floor   = Integer.MIN_VALUE;
                ceil    = Integer.MAX_VALUE;
                break;
            }
            case DataBuffer.TYPE_SHORT: {
                isFloat = false;
                floor   = Short.MIN_VALUE;
                ceil    = Short.MAX_VALUE;
                break;
            }
            default: {
                isFloat = false;
                floor   = 0;
                ceil    = (1L << DataBuffer.getDataTypeSize(dataType)) - 1;
                break;
            }
        }
        /*
         * Extracts all informations we will need from the user-supplied parameters, if any.
         */
        final String paletteName;
        final int[]  sourceBands;
        final int[]  targetBands;
        final int    visibleBand;
        if (parameters != null) {
            sourceBands = parameters.getSourceBands();
            targetBands = parameters.getDestinationBands();
        } else {
            sourceBands = null;
            targetBands = null;
        }
        if (parameters instanceof SpatialImageReadParam) {
            final SpatialImageReadParam geoparam = (SpatialImageReadParam) parameters;
            paletteName = geoparam.getNonNullPaletteName();
            visibleBand = geoparam.getVisibleBand();
        } else {
            paletteName = SpatialImageReadParam.DEFAULT_PALETTE_NAME;
            visibleBand = 0;
        }
        // Note: the number of bands in the target image (as requested by the caller)
        // may be different than the number of bands in the source image (on disk).
        final int numBands;
        if (sourceBands != null) {
            numBands = sourceBands.length; // == targetBands.length (assuming valid ImageReadParam).
        } else if (targetBands != null) {
            numBands = targetBands.length;
        } else {
            numBands = getNumBands(imageIndex);
        }
        /*
         * Computes a range of values for all bands, as the union in order to make sure that
         * we can stores every sample values. Also creates SampleConverters in the process.
         * The later is an opportunist action since we gather most of the needed information
         * during the loop.
         */
        NumberRange<?>  allRanges        = null;
        NumberRange<?>  visibleRange     = null;
        SampleConverter visibleConverter = SampleConverter.IDENTITY;
        double          maximumFillValue = 0; // Only in the visible band, and must be positive.
        final SpatialMetadata metadata;
        final boolean oldIgnore = ignoreMetadata;
        try {
            ignoreMetadata = false;
            metadata = getImageMetadata(imageIndex);
        } finally {
            ignoreMetadata = oldIgnore;
        }
        if (metadata != null) {
            final MetadataHelper helper = new MetadataHelper(this);
            final List<SampleDimension> bands = metadata.getListForType(SampleDimension.class);
            if (bands != null) {
                final int numMetadataBands = bands.size();
                if (numMetadataBands != 0) for (int i=0; i<numBands; i++) {
                    final int sourceBand = (sourceBands != null) ? sourceBands[i] : i;
                    if (sourceBand < 0 || sourceBand >= numMetadataBands) {
                        Warnings.log(this, null, SpatialImageReader.class, "getRawImageType",
                                indexOutOfBounds(sourceBand, 0, numMetadataBands));
                    }
                    final SampleDimension band = bands.get(Math.min(sourceBand, numMetadataBands-1));
                    final double[] nodataValues = band.getFillSampleValues();
                    final NumberRange<?> range = helper.getValidSampleValues(band, nodataValues);
                    double minimum, maximum;
                    if (range != null) {
                        minimum = range.getMinimum();
                        maximum = range.getMaximum();
                        if (!isFloat) {
                            // If the metadata do not contain any information about the range,
                            // treat as if we use the maximal range allowed by the data type.
                            if (minimum == Double.NEGATIVE_INFINITY) minimum = floor;
                            if (maximum == Double.POSITIVE_INFINITY) maximum = ceil;
                        }
                        final double extent = maximum - minimum;
                        if (extent >= 0 && (isFloat || extent <= (ceil - floor))) {
                            allRanges = (allRanges != null) ? allRanges.union(range) : range;
                        } else {
                            // Use range.getMin/MaxValue() because they may be integers rather than doubles.
                            Warnings.log(this, null, SpatialImageReader.class, "getRawImageType",
                                    Errors.Keys.BAD_RANGE_$2, range.getMinValue(), range.getMaxValue());
                            continue;
                        }
                    } else {
                        minimum = Double.NaN;
                        maximum = Double.NaN;
                    }
                    final int targetBand = (targetBands != null) ? targetBands[i] : i;
                    /*
                     * For floating point types, replaces no-data values by NaN because the floating
                     * point numbers are typically used for geophysics data, so the raster is likely
                     * to be a "geophysics" view for GridCoverage2D. All other values are stored "as
                     * is" without any offset.
                     *
                     * For integer types, if the range of values from the source data file fits into
                     * the range of values allowed by the destination raster, we will use an identity
                     * converter. If the only required conversion is a shift from negative to positive
                     * values, creates an offset converter with no-data values collapsed to 0.
                     */
                    final SampleConverter converter;
                    if (isFloat) {
                        converter = SampleConverter.createPadValuesMask(nodataValues);
                    } else {
                        final boolean isZeroValid = (minimum <= 0 && maximum >= 0);
                        boolean collapsePadValues = false;
                        if (nodataValues != null && nodataValues.length != 0) {
                            final double[] sorted = nodataValues.clone();
                            Arrays.sort(sorted);
                            double minFill = sorted[0];
                            double maxFill = minFill;
                            int indexMax = sorted.length;
                            while (--indexMax!=0 && Double.isNaN(maxFill = sorted[indexMax]));
                            assert minFill <= maxFill || Double.isNaN(minFill) : maxFill;
                            if (targetBand == visibleBand && maxFill > maximumFillValue) {
                                maximumFillValue = maxFill;
                            }
                            if (minFill < floor || maxFill > ceil) {
                                // At least one fill value is outside the range of acceptable values.
                                collapsePadValues = true;
                            } else if (minimum >= 0) {
                                /*
                                 * Arbitrary optimization of memory usage:  if there is a "large" empty
                                 * space between the range of valid values and a no-data value, then we
                                 * may (at subclass implementors choice) collapse the no-data values to
                                 * zero in order to avoid wasting the empty space.  Note that we do not
                                 * perform this collapse if the valid range contains negative values
                                 * because it would not save any memory. We do not check the no-data
                                 * values between 0 and 'minimum' for the same reason.
                                 */
                                int k = Arrays.binarySearch(sorted, maximum);
                                if (k >= 0) k++; // We want the first element greater than maximum.
                                else k = ~k; // Really ~ operator, not -
                                if (k <= indexMax) {
                                    double unusedSpace = Math.max(sorted[k] - maximum - 1, 0);
                                    while (++k <= indexMax) {
                                        final double delta = sorted[k] - sorted[k-1] - 1;
                                        if (delta > 0) {
                                            unusedSpace += delta;
                                        }
                                    }
                                    final int unused = (int) Math.min(Math.round(unusedSpace), Integer.MAX_VALUE);
                                    collapsePadValues = collapseNoDataValues(isZeroValid, sorted, unused);
                                    // We invoked 'collapseNoDataValues' inconditionnaly even if
                                    // 'unused' is zero because the user may decide on the basis
                                    // of other criterions, like 'isZeroValid'.
                                }
                            }
                        }
                        if (minimum < floor || maximum > ceil) {
                            // The range of valid values is outside the range allowed by raw data type.
                            converter = SampleConverter.createOffset(Math.ceil(1 - minimum), nodataValues);
                        } else if (collapsePadValues) {
                            if (isZeroValid) {
                                // We need to collapse the no-data values to 0, but it causes a clash
                                // with the range of valid values. So we also shift the later.
                                converter = SampleConverter.createOffset(Math.ceil(1 - minimum), nodataValues);
                            } else {
                                // We need to collapse the no-data values and there is no clash.
                                converter = SampleConverter.createPadValuesMask(nodataValues);
                            }
                        } else {
                            /*
                             * Do NOT take 'nodataValues' in account if there is no need to collapse
                             * them. This is not the converter's job to transform "packed" values to
                             * "geophysics" values. We just want them to fit in the IndexColorModel,
                             * and they already fit. So the identity converter is appropriate even
                             * in presence of pad values.
                             */
                            converter = SampleConverter.IDENTITY;
                        }
                    }
                    if (converters!=null && targetBand>=0 && targetBand<converters.length) {
                        converters[targetBand] = converter;
                    }
                    if (targetBand == visibleBand) {
                        visibleConverter = converter;
                        visibleRange = range;
                    }
                }
            }
        }
        /*
         * Ensure that all converters are defined. We typically have no converter if there
         * is no "ImageDescription/Dimensions" metadata. If the user specified explicitly
         * the image type, then we are done.
         */
        if (converters != null) {
            for (int i=Math.min(converters.length, numBands); --i>=0;) {
                if (converters[i] == null) {
                    converters[i] = visibleConverter;
                }
            }
        }
        if (type != null) {
            return type;
        }
        /*
         * Creates a color palette suitable for the range of values in the visible band.
         * The case for floating points is the simpliest: we should not have any offset,
         * at most a replacement of no-data values. In the case of integer values, we
         * must make sure that the indexed color map is large enough for containing both
         * the highest data value and the highest no-data value.
         */
        if (visibleRange == null) {
            visibleRange = (allRanges != null) ? allRanges : NumberRange.create(floor, ceil);
        }
        final PaletteFactory factory = PaletteFactory.getDefault();
        factory.setWarningLocale(locale);
        final Palette palette;
        if (isFloat) {
            assert visibleConverter.getOffset() == 0 : visibleConverter;
            palette = factory.getContinuousPalette(paletteName, (float) visibleRange.getMinimum(),
                    (float) visibleRange.getMaximum(), dataType, numBands, visibleBand);
        } else {
            final double offset  = visibleConverter.getOffset();
            final double minimum = visibleRange.getMinimum();
            final double maximum = visibleRange.getMaximum();
            long lower, upper;
            if (minimum == Double.NEGATIVE_INFINITY) {
                lower = floor;
            } else {
                lower = Math.round(minimum + offset);
                if (!visibleRange.isMinIncluded()) {
                    lower++; // Must be inclusive
                }
            }
            if (maximum == Double.POSITIVE_INFINITY) {
                upper = ceil;
            } else {
                upper = Math.round(maximum + offset);
                if (visibleRange.isMaxIncluded()) {
                    upper++; // Must be exclusive
                }
            }
            final long size = Math.max(upper, Math.round(maximumFillValue) + 1);
            /*
             * The target lower, upper and size parameters are usually in the range of SHORT
             * or USHORT data type. The Palette class will performs the necessary checks and
             * throws an exception if those variables are out of range.  However, because we
             * need to cast to int before passing the parameter values,  we restrict them to
             * the 'int' range as a safety in order to avoid results that accidently fall in
             * the SHORT or USHORT range.  Because Integer.MIN_VALUE or MAX_VALUE are out of
             * range,  it doesn't matter if those values are inaccurate since we will get an
             * exception anyway.
             */
            palette = factory.getPalette(paletteName,
                    (int) Math.max(lower, Integer.MIN_VALUE),
                    (int) Math.min(upper, Integer.MAX_VALUE),
                    (int) Math.min(size,  Integer.MAX_VALUE), numBands, visibleBand);
        }
        return palette.getImageTypeSpecifier();
    }

    /**
     * Returns the data type which most closely represents the "raw" internal data of the image.
     * It should be one of {@link DataBuffer} {@code TYPE_*} constants. This information is used
     * by {@link #getImageType(int, ImageReadParam, SampleConverter[]) getImageType(...)} in order
     * to create a default {@link ImageTypeSpecifier}.
     * <p>
     * The default {@code SpatialImageReader} implementation works better with
     *
     * {@link DataBuffer#TYPE_BYTE   TYPE_BYTE},
     * {@link DataBuffer#TYPE_SHORT  TYPE_SHORT},
     * {@link DataBuffer#TYPE_USHORT TYPE_USHORT} and
     * {@link DataBuffer#TYPE_FLOAT  TYPE_FLOAT}.
     *
     * Other types may work, but developers are advised to override the {@code getImageTypee(...)}
     * method as well.
     * <p>
     * The default implementation returns {@link DataBuffer#TYPE_FLOAT TYPE_FLOAT} in every cases.
     *
     * {@section The special case of negative integer sample values}
     * If the {@linkplain SampleDimension#getValidSampleValues() range of sample values} contains
     * negative values, then strictly speaking this method should return a signed type like
     * {@link DataBuffer#TYPE_SHORT TYPE_SHORT} or {@link DataBuffer#TYPE_INT TYPE_INT}.
     * If nevertheless this method return a unsigned integer type
     * ({@link DataBuffer#TYPE_BYTE TYPE_BYTE} or {@link DataBuffer#TYPE_USHORT TYPE_USHORT}), then
     * the default {@link #getImageType(int, ImageReadParam, SampleConverter[]) getImageType(...)}
     * implementation will add an offset in order to fit all sample values in the range of strictly
     * positive values. For example if range of sample value is [-23000 &hellip; +23000], then there
     * is a choice:
     *
     * <ul>
     *   <li><p>If this method returns {@link DataBuffer#TYPE_SHORT}, then the data will be
     *       stored "as is" without transformation. However the {@linkplain IndexColorModel
     *       index color model} will have the maximal length allowed by 16 bits integers, with
     *       positive values in the [0 &hellip; {@value java.lang.Short#MAX_VALUE}] range and negative
     *       values wrapped in the [{@value java.lang.Short#MIN_VALUE} &hellip; 65535] range in
     *       two's complement binary form. The results is a color model consuming 256 kilobytes
     *       in every cases. The space not used by the [-23000 &hellip; +23000] range (in the
     *       above example) is lost.</p></li>
     *
     *   <li><p>If this method returns {@link DataBuffer#TYPE_USHORT}, then the data will be
     *       translated to the smallest strictly positive range that can holds the data
     *       ([1 &hellip; 46000] for the above example). Value 0 is reserved for missing data.
     *       The result is a smaller {@linkplain IndexColorModel index color model} than the
     *       one used by untranslated data.</p></li>
     * </ul>
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The data type ({@link DataBuffer#TYPE_FLOAT} by default).
     * @throws IOException If an error occurs reading the format information from the input source.
     *
     * @see #getImageType(int, ImageReadParam, SampleConverter[])
     */
    protected int getRawDataType(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return DataBuffer.TYPE_FLOAT;
    }

    /**
     * Returns {@code true} if the no-data values should be collapsed to 0 in order to save memory.
     * This method is invoked automatically by the {@link #getImageType(int, ImageReadParam,
     * SampleConverter[]) getImageType(...)} method when it detected some unused space between the
     * {@linkplain SampleDimension#getValidSampleValues range of valid values} and at least one
     * {@linkplain SampleDimension#getFillSampleValues no-data value}.
     * <p>
     * The default implementation returns {@code false} in all cases, thus avoiding arbitrary
     * choice. Subclasses can override this method with some arbitrary threashold, as in the
     * example below:
     *
     * {@preformat java
     *     return unusedSpace >= 1024;
     * }
     *
     * @param isZeroValid
     *          {@code true} if 0 is a valid value. If this method returns {@code true} while
     *          {@code isZeroValid} is {@code true}, then the {@linkplain SampleConverter sample
     *          converter} to be returned by {@link #getImageType(int, ImageReadParam,
     *          SampleConverter[]) getImageType(...)} will offset all valid values by 1.
     * @param nodataValues
     *          The {@linkplain Arrays#sort(double[]) sorted}
     *          {@linkplain SampleDimension#getFillSampleValues no-data values} (never null and never empty).
     * @param unusedSpace
     *          The largest amount of unused space outside the range of valid values.
     * @return {@code true} if the no-data values should be collapsed to 0 in order to save memory.
     */
    protected boolean collapseNoDataValues(boolean isZeroValid, double[] nodataValues, int unusedSpace) {
        return false;
    }

    /**
     * Returns the buffered image to which decoded pixel data should be written. The image
     * is determined by inspecting the supplied parameters if it is non-null, as described
     * in the {@linkplain #getDestination(ImageReadParam,Iterator,int,int) super-class method}.
     * <p>
     * Implementations of the {@link #read(int, ImageReadParam)} method should invoke this
     * method instead of {@link #getDestination(ImageReadParam, Iterator, int, int)}.
     *
     * @param  imageIndex The index of the image to be retrieved.
     * @param  parameters The parameter given to the {@code read} method.
     * @param  width      The true width of the image or tile begin decoded.
     * @param  height     The true width of the image or tile being decoded.
     * @param  converters If non-null, an array where to store the converters required
     *                    for converting decoded pixel data into stored pixel data.
     * @return The buffered image to which decoded pixel data should be written.
     * @throws IOException If an error occurs reading the format information from the input source.
     *
     * @see #getImageType(int, ImageReadParam, SampleConverter[])
     */
    protected BufferedImage getDestination(final int imageIndex, final ImageReadParam parameters,
                            final int width, final int height, final SampleConverter[] converters)
            throws IOException
    {
        final ImageTypeSpecifier type = getImageType(imageIndex, parameters, converters);
        final Set<ImageTypeSpecifier> spi = Collections.singleton(type);
        return getDestination(parameters, spi.iterator(), width, height);
    }

    /**
     * Returns a default parameter object appropriate for this format. The default
     * implementation constructs and returns a new {@link SpatialImageReadParam}.
     *
     * @return An {@code ImageReadParam} object which may be used.
     */
    @Override
    public SpatialImageReadParam getDefaultReadParam() {
        return new SpatialImageReadParam(this);
    }

    /**
     * Modifies the given {@code srcRegion} before reading an image which is vertically flipped.
     * This is a helper method for handling file formats where the <var>y</var> pixel ordinates
     * are increasing upward, while the Image I/O API expects <var>y</var> pixel ordinates to be
     * increasing downward. This method applies the following modification:
     *
     * {@preformat java
     *     srcRegion.y = srcHeight - (srcRegion.y + srcRegion.height);
     * }
     *
     * plus an additional small <var>y</var> translation for taking subsampling in account,
     * if the given {@code param} is not null.
     * <p>
     * This method should be invoked right after {@link #computeRegions computeRegions}
     * as in the example below:
     *
     * {@preformat java
     *     computeRegions(param, srcWidth, srcHeight, image, srcRegion, destRegion);
     *     flipVertically(param, srcHeight, srcRegion);
     * }
     *
     * @param param     The {@code param}     argument given to {@code computeRegions}.
     * @param srcHeight The {@code srcHeight} argument given to {@code computeRegions}.
     * @param srcRegion The {@code srcRegion} argument given to {@code computeRegions}.
     */
    protected static void flipVertically(final ImageReadParam param, final int srcHeight,
                                         final Rectangle srcRegion)
    {
        final int spaceLeft = srcRegion.y;
        srcRegion.y = srcHeight - (srcRegion.y + srcRegion.height);
        /*
         * After the flip performed by the above line, we still have 'spaceLeft' pixels left for
         * a downward translation.  We usually don't need to care about it, except if the source
         * region is very close to the bottom of the source image,  in which case the correction
         * computed below may be greater than the space left.
         *
         * We are done if there is no vertical subsampling. But if there is subsampling, then we
         * need an adjustment. The flipping performed above must be computed as if the source
         * region had exactly the size needed for reading nothing more than the last line, i.e.
         * 'srcRegion.height' must be a multiple of 'sourceYSubsampling' plus 1. The "offset"
         * correction is computed below accordingly.
         */
        if (param != null) {
            int offset = (srcRegion.height - 1) % param.getSourceYSubsampling();
            srcRegion.y += offset;
            offset -= spaceLeft;
            if (offset > 0) {
                // Happen only if we are very close to image border and
                // the above translation bring us outside the image area.
                srcRegion.height -= offset;
            }
        }
    }

    /**
     * Invoked when a warning occured. The default implementation makes the following choice:
     * <p>
     * <ul>
     *   <li>If at least one {@linkplain IIOReadWarningListener warning listener}
     *       has been {@linkplain #addIIOReadWarningListener specified}, then the
     *       {@link IIOReadWarningListener#warningOccurred warningOccurred} method is
     *       invoked for each of them and the log record is <strong>not</strong> logged.</li>
     *
     *   <li>Otherwise, the log record is sent to the {@code "org.geotoolkit.image.io"} logger.</li>
     * </ul>
     * <p>
     * Subclasses may override this method if more processing is wanted, or for
     * throwing exception if some warnings should be considered as fatal errors.
     *
     * @param record The warning to log.
     * @return {@code true} if the message has been sent to at least one warning listener,
     *         or {@code false} if it has been sent to the logging system as a fallback.
     *
     * @see org.geotoolkit.image.io.metadata.MetadataAccessor#warningOccurred(LogRecord)
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
     * the stream anymore after closing it, it should not be a big deal if an error occured.
     */
    final void closeSilently() {
        try {
            close();
        } catch (IOException exception) {
            Logging.unexpectedException(LOGGER, getClass(), "close", exception);
        }
    }

    /**
     * Invoked when a new input is set or when the reader is disposed. The default implementation
     * clears the internal cache. Sub-classes can override this method if they have more resources
     * to dispose, but should always invoke {@code super.close()}.
     * <p>
     * This method is overriden and given {@code protected} access by {@link StreamImageReader}
     * and {@link ImageReaderAdapter}. It is called "{@code close}" in order to match the
     * purpose which appear in the public API of those classes.
     *
     * @throws IOException If an error occured while closing a stream (applicable to subclasses only).
     */
    void close() throws IOException {
        metadata = null;
    }

    /*
     * There is no need to override reset(), because the default ImageReader.reset()
     * implementation invokes setInput(null, false, false), which in turn invokes our
     * closeSilently() method.
     */

    /**
     * Allows any resources held by this reader to be released. If an input stream were
     * created by {@link StreamImageReader} or {@link ImageReaderAdapter}, it will be
     * {@linkplain StreamImageReader#close() closed} before to dispose this reader.
     */
    @Override
    public void dispose() {
        closeSilently();
        super.dispose();
    }



    /**
     * Service provider interfaces (SPI) for {@link SpatialImageReader}s.
     * This base class initializes fields to the values listed below:
     * <p>
     * <table border="1">
     *   <tr bgcolor="lightblue">
     *     <th>Field</th>
     *     <th>Value</th>
     *   </tr><tr>
     *     <td>&nbsp;{@link #nativeStreamMetadataFormatName}&nbsp;</td>
     *     <td>&nbsp;{@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#FORMAT_NAME}&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #nativeImageMetadataFormatName}&nbsp;</td>
     *     <td>&nbsp;{@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#FORMAT_NAME}&nbsp;</td>
     * </tr>
     * </table>
     * <p>
     * All other fields are left to their default values ({@code null} or {@code false}).
     * Subclasses are responsible for initializing those fields.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.07
     *
     * @see SpatialImageWriter.Spi
     *
     * @since 3.07
     * @module
     */
    protected abstract static class Spi extends ImageReaderSpi {
        /**
         * Initializes a default provider for {@link SpatialImageReader}s.
         * <p>
         * For efficienty reasons, the fields are initialized to a shared array.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        protected Spi() {
            nativeStreamMetadataFormatName = SpatialMetadataFormat.FORMAT_NAME;
            nativeImageMetadataFormatName  = SpatialMetadataFormat.FORMAT_NAME;
        }

        /**
         * Returns {@code true} if this provider supports the {@linkplain SpatialMetadataFormat
         * spatial metadata format}. This method checks if a native or extra metadata format
         * named {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#FORMAT_NAME}
         * is declared. This is the case by default unless sublcasses modified the values of
         * the {@code xxxFormatName} fields.
         *
         * @param  stream {@code true} for testing stream metadata, or {@code false} for testing
         *         image metadata.
         * @return {@code true} if the provider declares to support the spatial metadata format.
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
            return SpatialMetadataFormat.FORMAT_NAME.equals(nativeFormat) ||
                    XArrays.contains(extraFormats, SpatialMetadataFormat.FORMAT_NAME);
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
            if (SpatialMetadataFormat.FORMAT_NAME.equals(formatName) && isSpatialMetadataSupported(true)) {
                return SpatialMetadataFormat.STREAM;
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
            if (SpatialMetadataFormat.FORMAT_NAME.equals(formatName) && isSpatialMetadataSupported(true)) {
                return SpatialMetadataFormat.IMAGE;
            }
            return super.getStreamMetadataFormat(formatName);
        }
    }
}

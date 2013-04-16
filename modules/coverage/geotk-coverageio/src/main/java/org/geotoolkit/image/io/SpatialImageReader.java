/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collections;
import java.util.logging.LogRecord;

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

import org.opengis.coverage.grid.GridEnvelope;
import org.apache.sis.util.ArraysExt;

import org.geotoolkit.util.Utilities;
import org.apache.sis.util.Disposable;
import org.geotoolkit.util.NumberRange;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.logging.Logging;
import org.apache.sis.util.Locales;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SampleDimension;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SampleDomain;
import org.geotoolkit.internal.image.io.Warnings;

import static org.geotoolkit.image.io.SampleConversionType.*;
import static org.geotoolkit.image.io.MultidimensionalImageStore.*;
import static org.apache.sis.util.collection.Containers.isNullOrEmpty;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.ISO_FORMAT_NAME;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


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
 * described below assumes the simplest file format: one image made of one band of floating point
 * values using a grayscale color palette scaled to fit the range of sample values. This behavior
 * can be changed by overriding the methods listed below.
 *
 * <ul>
 *   <li><p>Provides default {@link #getNumImages(boolean)} and {@link #getNumBands(int)}
 *     implementations, which return 1. This default behavior matches simple image formats
 *     like {@linkplain org.geotoolkit.image.io.plugin.RawImageReader RAW} or
 *     {@linkplain org.geotoolkit.image.io.plugin.AsciiGridReader ASCII} files.
 *     Those methods need to be overridden for more complex image formats.</p></li>
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
 * <p>
 * See the {@link #getDestination(int, ImageReadParam, int, int, SampleConverter[])} method for an
 * example of code using some of the services provided by this class.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @see SpatialImageWriter
 *
 * @since 3.06 (derived from 1.2)
 * @module
 */
public abstract class SpatialImageReader extends ImageReader implements WarningProducer, Disposable {
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
        availableLocales = Locales.SIS.getAvailableLocales();
    }

    /**
     * Sets the input source to use. This method invokes {@link #close()}
     * before to set the new input.
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
     * If {@code getNumImages(false)} returned -1, then this method does not
     * check the upper bound.
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
     * @param  allowSearch If {@code true}, the number of images will be returned
     *         even if a search is required.
     * @return The number of images, or -1 if {@code allowSearch}
     *         is {@code false} and a search would be required.
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
     * {@link MultidimensionalImageStore} implementations provide a different value.
     *
     * @param  imageIndex The image index.
     * @return The number of dimension for the image at the given index.
     * @throws IOException if an error occurs reading the information from the input source.
     *
     * @see MultidimensionalImageStore
     *
     * @since 2.5
     */
    public int getDimension(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return 2; // max(X_DIMENSION, Y_DIMENSION) + 1
    }

    /**
     * Returns the grid envelope of the image at the given index. The default implementation
     * creates a grid envelope from the information provided by {@link #getDimension(int)},
     * {@link #getWidth(int)} and {@link #getHeight(int)} methods.
     * {@link MultidimensionalImageStore} implementations provide a different value.
     *
     * @param  imageIndex The image index.
     * @return The grid envelope for the image at the given index.
     * @throws IOException if an error occurs reading the information from the input source.
     *
     * @see MultidimensionalImageStore
     *
     * @since 3.19
     */
    @SuppressWarnings("fallthrough")
    public GridEnvelope getGridEnvelope(final int imageIndex) throws IOException {
        final int dimension = getDimension(imageIndex);
        final int[] lower = new int[dimension];
        final int[] upper = new int[dimension];
        switch (dimension) {
            default:             Arrays.fill(upper, 1); // Fall through in all cases.
            case Y_DIMENSION+1:  upper[Y_DIMENSION] = getHeight(imageIndex);
            case X_DIMENSION+1:  upper[X_DIMENSION] = getWidth (imageIndex);
            case 0:              break;
        }
        return new GeneralGridEnvelope(lower, upper, false);
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
        SpatialMetadata candidate = createMetadata(imageIndex);
        if (candidate != null) {
            candidate.setReadOnly(true);
        }
        if (metadata == null) {
            metadata = new SpatialMetadata[Math.max(cacheIndex+1, 4)];
        }
        if (cacheIndex >= metadata.length) {
            metadata = Arrays.copyOf(metadata, Math.max(cacheIndex+1, metadata.length*2));
        }
        metadata[cacheIndex] = (candidate != null) ? candidate : SpatialMetadata.EMPTY;
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
     * @throws IOException If an error occurred while reading metadata.
     */
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        return null;
    }

    /**
     * Returns {@code true} if the image at the given index has a color palette. Some formats like
     * {@linkplain org.geotoolkit.image.io.plugin.RawImageReader RAW},
     * {@linkplain org.geotoolkit.image.io.plugin.AsciiGridReader ASCII Grid} or
     * {@linkplain org.geotoolkit.image.io.plugin.NetcdfImageReader NetCDF} don't store any color
     * information with the pixel values, while other formats like PNG or JPEG (optionally wrapped
     * in a {@linkplain org.geotoolkit.image.io.plugin.WorldFileImageReader World File reader})
     * provide such color palette.
     * <p>
     * If this method returns {@code false}, no color information is included in the stream
     * to be read and users can provide their own color palette with a call to the
     * {@link SpatialImageReadParam#setPaletteName(String)} method. If this mehod returns
     * {@code true}, then the image to be read already have its own color information and
     * any call to the above-mentioned {@code setPaletteName} method are likely to be ignored.
     * <p>
     * The default implementation returns {@code false} in every cases. Subclasses shall override
     * this method if the implemented image format may have a color palette.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return {@code true} if the image at the given index has a color palette.
     * @throws IOException If an error occurs reading the information from the input source.
     *
     * @see SpatialImageReadParam#setPaletteName(String)
     *
     * @since 3.11
     */
    public boolean hasColors(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return false;
    }

    /**
     * Returns a collection of {@link ImageTypeSpecifier} containing possible image types to which
     * the given image may be decoded. The default implementation returns a singleton containing
     * <code>{@link #getRawImageType(int) getRawImageType}(imageIndex)</code>.
     *
     * @param  imageIndex The index of the image to be queried.
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
     * If this method needs to be overridden, consider overriding the later instead.
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
     * to use for reading the image. In addition, this method also detects if some conversions
     * (represented by {@link SampleConverter} instances) are required in order to store the
     * sample values using the selected models. The conversions (if any) are keept as small as
     * possible, but are sometime impossible to avoid for example because {@link IndexColorModel}
     * does not allow negative sample values.
     * <p>
     * The default implementation applies the following steps:
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
     * {@section Using the Sample Converters}
     * If the {@code converters} argument is non-null, then this method will store the
     * {@link SampleConverter} instances in the supplied array. The array length shall be equals
     * to the number of {@linkplain ImageReadParam#getSourceBands() source} and
     * {@linkplain ImageReadParam#getDestinationBands() destination bands}.
     * <p>
     * The converters shall be used by {@link #read(int,ImageReadParam) read} method
     * implementations for converting the values read in the datafile to values acceptable
     * by the {@linkplain ColorModel color model}. See the
     * {@link #getDestination(int, ImageReadParam, int, int, SampleConverter[]) getDestination}
     * method for code example.
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
     *          The user-supplied parameters, or {@code null}. Note: we recommend to supply
     *          {@link #getDefaultReadParam} instead of {@code null} since subclasses may
     *          override the later with default values suitable to a particular format.
     * @param converters
     *          If non-null, an array where to store the converters created by this method.
     *          The length of this array shall be equals to the number of target bands.
     * @return
     *          The image type (never {@code null}).
     * @throws IOException
     *          If an error occurs while reading the format information from the input source.
     *
     * @see #getRawDataType
     * @see #collapseNoDataValues
     * @see #getDestination(int, ImageReadParam, int, int, SampleConverter[])
     */
    @SuppressWarnings("fallthrough")
    protected ImageTypeSpecifier getImageType(final int               imageIndex,
                                              final ImageReadParam    parameters,
                                              final SampleConverter[] converters)
            throws IOException
    {
        /*
         * Extracts all informations we will need from the user-supplied parameters, if any.
         * Note: the number of bands in the target image (as requested by the caller)
         * may be different than the number of bands in the source image (on disk).
         */
        final ImageTypeSpecifier userType;
        final String paletteName;
        final int[]  sourceBands;
        final int[]  targetBands;
        final int    visibleBand;
        final int    numBands;
        if (parameters != null) {
            sourceBands = parameters.getSourceBands();
            targetBands = parameters.getDestinationBands();
            userType    = parameters.getDestinationType();
        } else {
            sourceBands = null;
            targetBands = null;
            userType    = null;
        }
        if (sourceBands != null) {
            numBands = sourceBands.length; // == targetBands.length (assuming valid ImageReadParam).
        } else if (targetBands != null) {
            numBands = targetBands.length;
        } else {
            numBands = getNumBands(imageIndex);
        }
        List<? extends SampleDomain> bands = null;
        if (parameters instanceof SpatialImageReadParam) {
            final SpatialImageReadParam geoparam = (SpatialImageReadParam) parameters;
            paletteName = geoparam.getNonNullPaletteName();
            visibleBand = geoparam.getVisibleBand();
            bands       = geoparam.getSampleDomains();
        } else {
            paletteName = SpatialImageReadParam.DEFAULT_PALETTE_NAME;
            visibleBand = 0;
        }
        /*
         * Gets the band metadata. If the user specified explicitly a SampleDomain in the
         * parameters, this is all the information we need - so we can avoid the cost of
         * querying IIOMetadata. Otherwise we will need to extract the image IIOMetadata.
         */
        boolean convertBandIndices = false;
        if (bands == null) {
            final SpatialMetadata metadata;
            final boolean oldIgnore = ignoreMetadata;
            try {
                ignoreMetadata = false;
                metadata = getImageMetadata(imageIndex);
            } finally {
                ignoreMetadata = oldIgnore;
            }
            if (metadata != null) {
                final List<SampleDimension> sd = metadata.getListForType(SampleDimension.class);
                if (!isNullOrEmpty(sd)) {
                    convertBandIndices = (sourceBands != null);
                    bands = sd;
                }
            }
        }
        /*
         * Gets the data type, and check if we should replace it by an other type. Type
         * replacements are allowed only if the appropriate SampleConversionType enum is set.
         */
        boolean replaceFillValues = false;
        int dataType = (userType != null) ? userType.getSampleModel().getDataType() : getRawDataType(imageIndex);
        if (userType == null && parameters instanceof SpatialImageReadParam) {
            final SpatialImageReadParam geoparam = (SpatialImageReadParam) parameters;
            switch (dataType) {
                case DataBuffer.TYPE_SHORT: {
                    if (geoparam.isSampleConversionAllowed(SHIFT_SIGNED_INTEGERS)) {
                        dataType = DataBuffer.TYPE_USHORT;
                    }
                    // Fall through
                }
                case DataBuffer.TYPE_USHORT:
                case DataBuffer.TYPE_INT:
                case DataBuffer.TYPE_BYTE: {
                    if (bands == null || !geoparam.isSampleConversionAllowed(STORE_AS_FLOATS)) {
                        break;
                    }
                    boolean hasFillValues = false;
                    for (final SampleDomain domain : bands) {
                        final double[] fillValues = domain.getFillSampleValues();
                        if (fillValues != null && fillValues.length != 0) {
                            hasFillValues = true;
                            break;
                        }
                    }
                    if (!hasFillValues) {
                        break;
                    }
                    dataType = DataBuffer.TYPE_FLOAT;
                    // Fall through
                }
                case DataBuffer.TYPE_FLOAT:
                case DataBuffer.TYPE_DOUBLE: {
                    replaceFillValues = geoparam.isSampleConversionAllowed(REPLACE_FILL_VALUES);
                }
            }
        }
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
         * Computes a range of values for all bands, as the union in order to make sure that
         * we can stores every sample values. Also creates SampleConverters in the process.
         * The later is an opportunist action since we gather most of the needed information
         * during the loop.
         */
        NumberRange<?>  allRanges        = null;
        NumberRange<?>  visibleRange     = null;
        SampleConverter visibleConverter = SampleConverter.IDENTITY;
        double          maximumFillValue = 0; // Only in the visible band, and must be positive.
        if (bands != null) {
            MetadataHelper helper = null;              // To be created only if needed.
            final int numMetadataBands = bands.size(); // Never 0 - check was performed above.
            for (int i=0; i<numBands; i++) {
                int bandIndex = convertBandIndices ? sourceBands[i] : i;
                if (bandIndex < 0 || bandIndex >= numMetadataBands) {
                    if (numMetadataBands != 1) {
                        // If there is exactly one metadata band, don't log any warning since
                        // we will assume that the metadata band apply to all data bands.
                        Warnings.log(this, null, SpatialImageReader.class, "getImageType",
                                indexOutOfBounds(bandIndex, 0, numMetadataBands));
                    }
                    bandIndex = numMetadataBands - 1;
                }
                /*
                 * Before to get the range, get the fill values with maximal precision.
                 * Some values may need to be casted from 'double' to 'float' in order
                 * to match the sample values in the raster. This cast to various types
                 * will be performed internally by the SampleConverter implementations.
                 */
                final SampleDomain band = bands.get(bandIndex);
                final double[] fillValues = band.getFillSampleValues();
                final NumberRange<?> range;
                if (band instanceof SampleDimension) {
                    if (helper == null) {
                        helper = new MetadataHelper(this);
                    }
                    range = helper.getValidSampleValues(bandIndex, (SampleDimension) band, fillValues);
                } else {
                    range = band.getValidSampleValues();
                }
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
                        Warnings.log(this, null, SpatialImageReader.class, "getImageType",
                                Errors.Keys.ILLEGAL_RANGE_$2, range.getMinValue(), range.getMaxValue());
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
                    // If the sample values are float values, we need to replace 99.99 fill value
                    // (for example) by 99.99f, which is 99.98999786376953 in double precision,
                    // otherwise the SampleConverter may not find them (denpending which method
                    // is invoked). This cast is done by the PadValueMask constructor.
                    converter = replaceFillValues ?
                            SampleConverter.createPadValuesMask(fillValues) : SampleConverter.IDENTITY;
                } else {
                    final boolean isZeroValid = (minimum <= 0 && maximum >= 0);
                    boolean collapsePadValues = false;
                    if (fillValues != null && fillValues.length != 0) {
                        final double[] sorted = fillValues.clone();
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
                                // We invoked 'collapseNoDataValues' unconditionally even if
                                // 'unused' is zero because the user may decide on the basis
                                // of other criterions, like 'isZeroValid'.
                            }
                        }
                    }
                    if (minimum < floor || maximum > ceil) {
                        // The range of valid values is outside the range allowed by raw data type.
                        converter = SampleConverter.createOffset(Math.ceil(1 - minimum), fillValues);
                    } else if (collapsePadValues) {
                        if (isZeroValid) {
                            // We need to collapse the no-data values to 0, but it causes a clash
                            // with the range of valid values. So we also shift the later.
                            converter = SampleConverter.createOffset(Math.ceil(1 - minimum), fillValues);
                        } else {
                            // We need to collapse the no-data values and there is no clash.
                            converter = SampleConverter.createPadValuesMask(fillValues);
                        }
                    } else {
                        /*
                         * Do NOT take 'fillValues' in account if there is no need to collapse
                         * them. This is not the converter's job to transform "packed" values to
                         * "geophysics" values. We just want them to fit in the IndexColorModel,
                         * and they already fit. So the identity converter is appropriate even
                         * in presence of pad values.
                         */
                        converter = SampleConverter.IDENTITY;
                    }
                }
                if (converters != null && i < converters.length) {
                    converters[i] = converter;
                }
                if (targetBand == visibleBand) {
                    visibleConverter = converter;
                    visibleRange = range;
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
        if (userType != null) {
            return userType;
        }
        /*
         * Creates a color palette suitable for the range of values in the visible band.
         * The case for floating points is the simplest: we should not have any offset,
         * at most a replacement of no-data values. In the case of integer values, we
         * must make sure that the indexed color map is large enough for containing both
         * the highest data value and the highest no-data value.
         */
        if (visibleRange == null) {
            visibleRange = (allRanges != null) ? allRanges : NumberRange.create(floor, ceil);
        }
        PaletteFactory factory = null;
        if (parameters instanceof SpatialImageReadParam) {
            factory = ((SpatialImageReadParam) parameters).getPaletteFactory();
        }
        if (factory == null) {
            factory = PaletteFactory.getDefault();
        }
        factory.setWarningLocale(locale);
        final double minimum = visibleRange.getMinimum();
        final double maximum = visibleRange.getMaximum();
        final Palette palette;
        if (isFloat) {
            assert visibleConverter.getOffset() == 0 : visibleConverter;
            palette = factory.getContinuousPalette(paletteName, (float) minimum,
                    (float) maximum, dataType, numBands, visibleBand);
        } else {
            final double offset  = visibleConverter.getOffset();
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
            long size = Math.max(upper, Math.round(maximumFillValue) + 1);
            if (lower < 0) {
                size -= lower;
            }
            /*
             * The target lower, upper and size parameters are usually in the range of SHORT
             * or USHORT data type.  The Palette class will perform the necessary checks and
             * throw an exception if those variables are out of range. However we may have
             * values out of this range for TYPE_INT, in which case we will use the same slow
             * color model than the one for floating point values.
             */
            if (lower >= Short.MIN_VALUE && (lower + size) <= (lower >= 0 ? IndexedPalette.MAX_UNSIGNED+1 : Short.MAX_VALUE+1)) {
                palette = factory.getPalette(paletteName, (int) lower, (int) upper, (int) size, numBands, visibleBand);
            } else {
                palette = factory.getContinuousPalette(paletteName, lower, upper, dataType, numBands, visibleBand);
            }
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
     * Other types may work, but developers are advised to override the {@code getImageType(...)}
     * method as well.
     * <p>
     * The default implementation returns {@link DataBuffer#TYPE_FLOAT TYPE_FLOAT} in every cases.
     *
     * {@section Special case for negative integer values (<code>TYPE_SHORT</code>)}
     * If the sample values are integers but the {@linkplain SampleDimension#getValidSampleValues()
     * range of valid values} contains negative values, then strictly speaking this method should
     * return a signed type ({@link DataBuffer#TYPE_SHORT TYPE_SHORT} or {@link DataBuffer#TYPE_INT
     * TYPE_INT}). If nevertheless this method return a unsigned type
     * ({@link DataBuffer#TYPE_BYTE TYPE_BYTE} or {@link DataBuffer#TYPE_USHORT TYPE_USHORT}), then
     * the default {@link #getImageType(int, ImageReadParam, SampleConverter[]) getImageType(...)}
     * implementation will add an offset in order to fit all sample values in the range of strictly
     * positive values.
     * <p>
     * <table border="1" cellspacing="0" cellpadding="9"><tr><td>
     * <b>Example:</b> if the range of sample values is [-23000 &hellip; +23000], then there
     * is a choice:
     *
     * <ol>
     *   <li><p><b>Signed integers storage:</b> If this method returns {@link DataBuffer#TYPE_SHORT
     *   TYPE_SHORT}, then the data will be stored "as is" without transformation. However the
     *   {@linkplain IndexColorModel#getMapSize() size of the Index Color Model} will be the maximal
     *   length allowed by 16 bits integers, which result in a Color Model consuming 256 kilobytes
     *   of memory no matter how large is the range of values actually used.</p>
     *
     *   <p>Positive values are stored in the [0 &hellip; {@value java.lang.Short#MAX_VALUE}] range
     *   directly, while the negative values are converted in their two's complement binary form
     *   before to be stored in the [32768 &hellip; 65535] range. The space not used by the
     *   [-23000 &hellip; +23000] range is lost.</p></li>
     *
     *   <li><p><b>Unsigned integers storage:</b> If this method returns {@link DataBuffer#TYPE_USHORT
     *   TYPE_USHORT}, then the data will be translated to the smallest strictly positive range that
     *   can holds the data ([1 &hellip; 46001] for the above example). The 0 value is reserved for
     *   missing data. The result is a smaller {@linkplain IndexColorModel Index Color Model} than
     *   the one used by untranslated data.</p></li>
     * </ol>
     * </td></tr></table>
     * <p>
     * Beware that signed integers (case 1 in the above example) used with {@link IndexColorModel}
     * require explicit casts to the {@code short} type as in the example below. Using directly the
     * {@link java.awt.image.Raster#getSample(int,int,int)} return value is not sufficient because
     * the returned value would be unsigned no matter what this {@code getRawDataType(int)} method
     * returned.
     *
     * {@preformat java
     *     int value = (short) myRaster.getSample(x, y, b); // Intentional casts int → short → int.
     * }
     *
     * Given this gotcha and the fact that signed integers require large color palette, users are
     * advised to prefer unsigned types if they can afford the offset applied on sample values.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The data type ({@link DataBuffer#TYPE_FLOAT} by default).
     * @throws IOException If an error occurs reading the format information from the input source.
     *
     * @see #getImageType(int, ImageReadParam, SampleConverter[])
     * @see SampleConversionType#SHIFT_SIGNED_INTEGERS
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
     * In addition, this method also detects if the sample values need to be converted before
     * to be stored in the {@link BufferedImage}, for example because {@link IndexColorModel}
     * does not support negative integers. The conversions (if any) are represented by
     * {@link SampleConverter} objects, which are computed as documented in the
     * {@link #getImageType getImageType} method.
     *
     * {@section Using the Sample Converters}
     * If the {@code converters} argument is non-null, then this method will store the
     * {@link SampleConverter} instances in the supplied array. The array length shall be equals
     * to the number of {@linkplain ImageReadParam#getSourceBands() source} and
     * {@linkplain ImageReadParam#getDestinationBands() destination bands}.
     * <p>
     * The converters shall be used by {@link #read(int,ImageReadParam) read} method
     * implementations for converting the values read in the datafile to values acceptable
     * by the {@linkplain ColorModel color model}.
     * Example (omitting the {@linkplain ImageReadParam#setSourceSubsampling subsamplings}
     * handling for simplicity):
     *
     * {@preformat java
     *     public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
     *         final int[] srcBands, dstBands;
     *         if (param != null) {
     *             srcBands = param.getSourceBands();
     *             dstBands = param.getDestinationBands();
     *         } else {
     *             srcBands = null;
     *             dstBands = null;
     *         }
     *         final int numSrcBands = (srcBands != null) ? srcBands.length : ...; // Image-dependant
     *         final int numDstBands = (dstBands != null) ? dstBands.length : numSrcBands;
     *         checkReadParamBandSettings(param, numSrcBands, numDstBands);
     *
     *         final int width  = ...;  // Image-dependant
     *         final int height = ...;  // Image-dependant
     *         final SampleConverter[] converters = new SampleConverter[numDstBands];
     *         final BufferedImage  image  = getDestination(imageIndex, param, width, height, converters);
     *         final WritableRaster raster = image.getRaster();
     *         final Rectangle   srcRegion = new Rectangle();
     *         final Rectangle  destRegion = new Rectangle();
     *         computeRegions(param, width, height, image, srcRegion, destRegion);
     *         final int xmin = destRegion.x;
     *         final int ymin = destRegion.y;
     *         final int xmax = destRegion.width  + xmin;
     *         final int ymax = destRegion.height + ymin;
     *         for (int band=0; band < numDstBands; band++) {
     *             final int srcBand = (srcBands == null) ? band : srcBands[band];
     *             final int dstBand = (dstBands == null) ? band : dstBands[band];
     *             final SampleConverter converter = converters[band];
     *             for (int y=ymin; y<ymax; y++) {
     *                 for (int x=xmin; x<xmax; x++) {
     *                     float value = ...; // Image-dependant
     *                     value = converter.convert(value);
     *                     raster.setSample(x, y, dstBand, value);
     *                 }
     *             }
     *         }
     *     }
     * }
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
     * Invoked when a warning occurred. The default implementation makes the following choice:
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
     * @see org.geotoolkit.image.io.metadata.MetadataNodeParser#warningOccurred(LogRecord)
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
     * Invoked when a new input is set or when the reader is disposed. The default implementation
     * clears the internal cache. Sub-classes can override this method if they have more resources
     * to dispose, but should always invoke {@code super.close()}.
     *
     * @throws IOException If an error occurred while closing a stream.
     *
     * @since 3.16
     */
    protected void close() throws IOException {
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
     * @see SpatialImageWriter.Spi
     *
     * @since 3.07
     * @module
     */
    protected abstract static class Spi extends ImageReaderSpi {
        /**
         * The {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#GEOTK_FORMAT_NAME} value
         * in an array, for optional assignment to {@code extra[Stream|Image]MetadataFormatNames} fields.
         */
        private static final String[] GEOTK = {
            GEOTK_FORMAT_NAME
        };

        /**
         * An array containing GEOTK and ISO metadata.
         */
        private static final String[] GEOTK_ISO = {
            SpatialMetadataFormat.GEOTK_FORMAT_NAME, SpatialMetadataFormat.ISO_FORMAT_NAME
        };

        /**
         * Initializes a default provider for {@link SpatialImageReader}s.
         * <p>
         * For efficiency reasons, the fields are initialized to a shared array.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        protected Spi() {
            nativeStreamMetadataFormatName = GEOTK_FORMAT_NAME;
            nativeImageMetadataFormatName  = GEOTK_FORMAT_NAME;
            if (getClass().getName().startsWith("org.geotoolkit.")) {
                vendorName = "Geotoolkit.org";
                version    = Utilities.VERSION.toString();
            }
        }

        /**
         * Adds the given format to the list of extra stream or metadata format names,
         * if not already present. This method does nothing if the format is already
         * listed as the native or an extra format.
         *
         * @param formatName
         * @param stream {@code true} for adding to the list of {@linkplain #extraStreamMetadataFormatNames extra stream formats}.
         * @param image  {@code true} for adding to the list of {@linkplain #extraImageMetadataFormatNames extra image formats}.
         *
         * @since 3.20
         */
        protected void addExtraMetadataFormat(final String formatName, final boolean stream, final boolean image) {
            if (stream) extraStreamMetadataFormatNames = addExtraMetadataFormat(formatName, nativeStreamMetadataFormatName, extraStreamMetadataFormatNames);
            if (image)  extraImageMetadataFormatNames  = addExtraMetadataFormat(formatName, nativeImageMetadataFormatName,  extraImageMetadataFormatNames);
        }

        /**
         * Adds the {@value SpatialMetadataFormat#GEOTK_FORMAT_NAME} to the given array, if
         * not already presents. This method returns a shared array for some common cases.
         */
        static String[] addExtraMetadataFormat(final String formatName, final String nativeName, final String[] formatNames) {
            ArgumentChecks.ensureNonNull("formatName", formatName);
            if (formatName.equals(nativeName) || ArraysExt.contains(formatNames, formatName)) {
                return formatNames;
            }
            if (formatNames == null || formatNames.length == 0) {
                return formatName.equals(GEOTK_FORMAT_NAME) ? GEOTK : new String[] {formatName};
            }
            if ((GEOTK_FORMAT_NAME.equals(formatNames[0]) && formatName.equals(ISO_FORMAT_NAME))) {
                return GEOTK_ISO;
            }
            return ArraysExt.append(formatNames, formatName);
        }

        /**
         * Returns a code indicating which kind of metadata to returns. The codes are:
         * <p>
         * <ul>
         *   <li>0: return {@code null}</li>
         *   <li>1: delegate to {@link SpatialMetadataFormat} static methods.</li>
         *   <li>2: delegate to default {@link ImageReadWriteSpi} methods.</li>
         *   <li>3: the given format name is unsupported.</li>
         * </ul>
         * <p>
         * We can not delegates to {@link ImageReadWriteSpi} directly, because its default
         * implementation is not null-safe (note that the Image I/O specification allows to
         * return {@code null} formats).
         */
        static int getMetadataFormatCode(final String formatName,
                final String   nativeName, final String  nativeClassName,
                final String[] extraNames, final String[] extraClassNames)
        {
            ArgumentChecks.ensureNonNull("formatName", formatName);
            if (formatName.equals(nativeName)) {
                return isSpatialFormat(formatName) ? 1 :
                        (nativeClassName != null) ? 2 : 0;
            }
            if (extraNames != null) {
                for (int i=0; i<extraNames.length; i++) {
                    if (formatName.equals(extraNames[i])) {
                        return isSpatialFormat(formatName) ? 1 :
                                (extraClassNames != null && extraClassNames[i] != null) ? 2 : 0;
                    }
                }
            }
            return 3;
        }

        /**
         * Returns {@code true} if the given format name is one of the Geotk hard-coded ones.
         */
        private static boolean isSpatialFormat(final String formatName) {
            return formatName.equals(GEOTK_FORMAT_NAME) || formatName.equals(ISO_FORMAT_NAME);
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
            switch (getMetadataFormatCode(formatName,
                    nativeStreamMetadataFormatName,
                    nativeStreamMetadataFormatClassName,
                    extraStreamMetadataFormatNames,
                    extraStreamMetadataFormatClassNames))
            {
                case 0:  return null;
                case 1:  return SpatialMetadataFormat.getStreamInstance(formatName);
                default: return super.getStreamMetadataFormat(formatName);
            }
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
            switch (getMetadataFormatCode(formatName,
                    nativeImageMetadataFormatName,
                    nativeImageMetadataFormatClassName,
                    extraImageMetadataFormatNames,
                    extraImageMetadataFormatClassNames))
            {
                case 0:  return null;
                case 1:  return SpatialMetadataFormat.getImageInstance(formatName);
                default: return super.getImageMetadataFormat(formatName);
            }
        }
    }
}

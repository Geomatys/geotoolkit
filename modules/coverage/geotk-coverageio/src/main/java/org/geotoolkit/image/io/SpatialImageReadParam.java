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

import java.awt.Point;
import java.util.Set;
import java.util.Map;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.logging.LogRecord;
import java.awt.Rectangle;
import java.awt.image.IndexColorModel;
import javax.imageio.IIOParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;

import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.geotoolkit.internal.image.io.Warnings;
import org.geotoolkit.util.converter.Classes;


/**
 * Default parameters for {@link SpatialImageReader}. This class extends the standard
 * {@link ImageReadParam} class with the following additional capabilities:
 * <p>
 * <ul>
 *   <li><p>Specify the plane to read in datasets having more than 2 dimensions, as for example
 *       in {@linkplain org.geotoolkit.image.io.plugin.NetcdfImageReader NetCDF} files.</p></li>
 *
 *   <li><p>Specify the name of a {@linkplain Palette color palette}. This is useful when
 *       reading an image from a file that doesn't contain such information, for example
 *       {@linkplain org.geotoolkit.image.io.plugin.AsciiGridReader ASCII grid}.</p></li>
 *
 *   <li><p>For images having more than one band where the bands are <strong>not</strong> color
 *       components, specify which band to use with the {@linkplain IndexColorModel Index
 *       Color Model}. For example an image may contain <cite>Sea Surface Temperature</cite> (SST)
 *       measurements in the first band, and an estimation of the measurement errors in the second
 *       band. Users may want to read both bands for computation purpose, while applying a color
 *       palette using only the values in the first band.</p></li>
 * </ul>
 *
 * {@section Handling more than two dimensions}
 * Some file formats like NetCDF can store dataset having more than two dimensions.
 * Geotk handles the supplemental dimensions with the following policies by default:
 *
 * <ol>
 *   <li><p>The two first dimensions - typically named (<var>x</var>, <var>y</var>) - are
 *     assigned to the (<var>columns</var>, <var>rows</var>) pixel indices.</p></li>
 *
 *   <li><p>The third dimension can optionally be assigned to band indices. This is typically the
 *     altitude (<var>z</var>) in a dataset having the (<var>x</var>, <var>y</var>, <var>z</var>,
 *     <var>t</var>) dimensions, but can be customized. The actual dimension assigned to band
 *     indices is returned by {@link DimensionSlice#findDimensionIndex(Iterable)}. See the
 *     next section below for more information.</p>
 *
 *   <li><p>Only one slice of every supplemental dimensions can be read. By default the data at index
 *     0 are loaded, but different indices can be selected (see {@link DimensionSlice}). The actual
 *     index used is the value returned by {@link #getSliceIndex(Object[])}.</p></li>
 * </ol>
 *
 * {@section Assigning a third dimension to bands}
 * Whatever a third dimension is assigned to bands or not is plugin-specific. Plugins that have
 * no concept of bands (like NetCDF which has the concept of <var>n</var>-dimensional data cube
 * instead) can do that. For example in a dataset having (<var>x</var>, <var>y</var>, <var>z</var>,
 * <var>t</var>) dimensions, it may be useful to handle the <var>z</var> dimension as bands. After
 * the method calls below, users can select one or many elevation indices through the standard
 * {@link #setSourceBands(int[])} API. Compared to the {@link DimensionSlice} API, it allows
 * loading more than one slice in one read operation.
 *
 * {@preformat java
 *     DimensionSlice thirdDimension = parameters.newDimensionSlice();
 *     thirdDimension.addDimensionId(2); // 0-based index of the third dimension.
 *     thirdDimension.setAPI(DimensionSlice.API.BANDS);
 * }
 *
 * Note that the above code has the side-effect to initialize the {@linkplain #sourceBands source
 * bands} array to {@code {0}} (meaning to load only the first band by default), which is desired
 * since the number of bands in NetCDF files is typically large and those bands are not color
 * components. This is different than the usual {@code ImageReadParam} behavior which is to
 * initialize source bands to {@code null} (meaning to load all bands).
 *
 * {@note The side-effect described above is not a special case. It is a natural consequence
 *        of the fact that the default index of <strong>every</strong> dimension slice in 0.}
 *
 * After the <var>z</var> dimension in the above example has been assigned to the bands API,
 * the bands can be used as below:
 * <p>
 * <ul>
 *   <li>The (<var>x</var>,<var>y</var>) plane at <var>z</var><sub>{@code sourceBands[0]}</sub> is stored in band 0.</li>
 *   <li>The (<var>x</var>,<var>y</var>) plane at <var>z</var><sub>{@code sourceBands[1]}</sub> is stored in band 1.</li>
 *   <li><i>etc.</i></li>
 * </ul>
 * <p>
 * Implementors can determine which (if any) dimension has been assigned to the bands API by
 * using the code below:
 *
 * {@preformat java
 *     DimensionSlice bandsDimension = parameters.getDimensionSliceForAPI(BANDS);
 *     if (bandsDimension != null) {
 *         Collection<?> propertiesOfAxes = ...; // This is plugin-specific.
 *         int index = bandsDimension.findDimensionIndex(propertiesOfAxes);
 *         if (index >= 0) {
 *             // We have found the dimension index of bands.
 *         }
 *     }
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.05 (derived from 2.4)
 * @module
 */
public class SpatialImageReadParam extends ImageReadParam implements WarningProducer {
    /**
     * The name of the default color palette to apply when none was explicitly specified.
     * The default palette is {@value}.
     *
     * @see #getPaletteName()
     * @see #setPaletteName(String)
     */
    public static final String DEFAULT_PALETTE_NAME = "grayscale";

    /**
     * The set of {@link DimensionSlice} instances, which contains also the
     * implementation of public API exposed in {@code SpatialImageReadParam}.
     *
     * @since 3.08
     */
    private DimensionSlices dimensionSlices;

    /**
     * The name of the color palette.
     */
    private String palette;

    /**
     * The band to display.
     */
    private int visibleBand;

    /**
     * The kind of sample conversions which are allowed, or {@code null} if none.
     */
    private Set<SampleConversionType> allowedConversions;

    /**
     * The image reader which created the parameters, or {@code null} if unknown.
     * This is used for emitting warnings.
     */
    private final ImageReader reader;

    /**
     * Creates a new, initially empty, set of parameters.
     *
     * @param reader The reader for which this parameter block is created, or {@code null}.
     */
    public SpatialImageReadParam(final ImageReader reader) {
        this.reader = reader;
    }

    /**
     * Returns the resources for formatting error messages.
     */
    private IndexedResourceBundle getErrorResources() {
        return Errors.getResources(getLocale());
    }

    /**
     * Creates a new handler for selecting a slice of the (<var>x</var>, <var>y</var>) plane to
     * read. This is relevant only for <var>n</var>-dimensional dataset where <var>n</var>&gt;2.
     * The caller should invoke one or many {@link DimensionSlice#addDimensionId(int)
     * addDimensionId(...)} methods for specifying the dimension, and invoke
     * {@link DimensionSlice#setSliceIndex(int)} for specifying the index of the
     * <cite>slice point</cite> (WMS 2.0 terminology).
     * <p>
     * A new handler can be created for each supplemental dimension above the two (<var>x</var>,
     * <var>y</var>) dimensions and the bands handled by Java2D. If no slice is specified for a
     * supplemental dimension, then the default is the slice at index 0.
     *
     * @return A new handler for specifying the index of the slice to read in a supplemental
     *         dimension.
     *
     * @since 3.08
     */
    public DimensionSlice newDimensionSlice() {
        if (dimensionSlices == null) {
            dimensionSlices = new DimensionSlices(this);
        }
        return new DimensionSlice(dimensionSlices);
    }

    /**
     * Returns all {@code DimensionSlice} instances known to this parameters block. They are
     * the instances created by {@link #newDimensionSlice()} and for which at least one
     * {@linkplain DimensionSlice#addDimensionId(String[]) identifier has been added}.
     * <p>
     * The returned collection is <cite>live</cite>: if {@linkplain #newDimensionSlice() new
     * dimension slices} are created, they will appear dynamically in the returned set.
     *
     * @return The dimensions registered in this parameters, or an empty set if none.
     *
     * @since 3.08
     */
    public Set<DimensionSlice> getDimensionSlices() {
        if (dimensionSlices == null) {
            dimensionSlices = new DimensionSlices(this);
        }
        return dimensionSlices;
    }

    /**
     * Returns the dimension slice which has been assigned to the given API, or {@code null}
     * if none. This method returns the last {@code DimensionSlice} instance created by the
     * {@link #newDimensionSlice()} method on which the {@link DimensionSlice#setAPI setAPI}
     * method has been invoked with the given {@code api} value.
     * <p>
     * This method is typically invoked by {@link SpatialImageReader} implementations together
     * with {@link DimensionSlice#findDimensionIndex(Iterable)} in order to locate the index of
     * the dimension to read as bands. See <cite>Assigning a third dimension to bands</cite> in
     * the <a href="#skip-navbar_top">class javadoc</a>.
     *
     * @param  api The API for which to test if a dimension slice has been assigned.
     * @return The dimension slice assigned to the given API, or {@code null} if none.
     *
     * @since 3.08
     */
    public DimensionSlice getDimensionSliceForAPI(final DimensionSlice.API api) {
        return (dimensionSlices != null) ? dimensionSlices.getDimensionSliceForAPI(this, api) : null;
    }

    /**
     * Returns the dimension slice identified by at least one of the given identifiers. This is
     * relevant mostly for <var>n</var>-dimensional dataset where <var>n</var>&gt;2. The dimension
     * can be identified by a zero-based index as an {@link Integer}, a dimension name as a
     * {@link String}, or an axis direction as an {@link AxisDirection}. More than one identifier
     * can be specified in order to increase the chance to get the index, for example:
     *
     * {@preformat java
     *     DimensionSlice slice = getDimensionSlice("time", AxisDirection.FUTURE);
     * }
     *
     * If different slices are found for the given identifiers, then a
     * {@linkplain SpatialImageReader#warningOccurred warning is emitted}
     * and this method returns the first slice matching the given identifiers.
     * If no slice is found, {@code null} is returned.
     *
     * @param  dimensionIds {@link Integer}, {@link String} or {@link AxisDirection}
     *         that identify the dimension for which the slice is desired.
     * @return The first slice found for the given dimension identifiers, or {@code null} if none.
     *
     * @since 3.08
     */
    public DimensionSlice getDimensionSlice(final Object... dimensionIds) {
        return (dimensionSlices != null) ? dimensionSlices.getDimensionSlice(this,
                SpatialImageReadParam.class, dimensionIds) : null;
    }

    /**
     * Returns the index of the slice in the dimension identified by at least one of the given
     * identifiers. This method is equivalent to the code below, except that a warning is emitted
     * only if index values are ambiguous:
     *
     * {@preformat java
     *     DimensionSlice slice = getDimensionSlice(dimensionIds);
     *     return (slice != null) ? slice.getSliceIndex() : 0;
     * }
     *
     * This method is relevant mostly for <var>n</var>-dimensional dataset where <var>n</var>&gt;2.
     * The dimension can be identified by a zero-based index as an {@link Integer}, a dimension
     * name as a {@link String}, or an axis direction as an {@link AxisDirection}. More than one
     * identifier can be specified in order to increase the chance to get the index, for example:
     *
     * {@preformat java
     *     int index = getSliceIndex("time", AxisDirection.FUTURE);
     * }
     *
     * If different indices are found for the given identifiers, then a
     * {@linkplain SpatialImageReader#warningOccurred warning is emitted}
     * and this method returns the index for the first slice matching the given identifiers.
     * If no index is found, 0 (which is the default index value) is returned.
     *
     * @param  dimensionIds {@link Integer}, {@link String} or {@link AxisDirection}
     *         that identify the dimension for which the index is desired.
     * @return The index set in the first slice found for the given dimension identifiers,
     *         or 0 if none.
     *
     * @see DimensionSlice#getSliceIndex()
     *
     * @since 3.08
     */
    public int getSliceIndex(final Object... dimensionIds) {
        return (dimensionSlices != null) ? dimensionSlices.getSliceIndex(this,
                SpatialImageReadParam.class, dimensionIds) : 0;
    }

    /**
     * Ensures that the specified band number is valid.
     */
    private void ensureValidBand(final int band) throws IllegalArgumentException {
        if (band < 0) {
            throw new IllegalArgumentException(getErrorResources().getString(
                    Errors.Keys.BAD_BAND_NUMBER_$1, band));
        }
    }

    /**
     * Returns the band to display in the target image. In theory, images backed by
     * {@linkplain java.awt.image.IndexColorModel index color model} should have only
     * one band. But sometime we want to load additional bands as numerical data, in
     * order to perform computations. In such case, we need to specify which band in
     * the destination image will be used as an index for displaying the colors. The
     * default value is 0.
     *
     * @return The band to display in the target image.
     */
    public int getVisibleBand() {
        return visibleBand;
    }

    /**
     * Sets the band to make visible in the destination image.
     *
     * @param  visibleBand The band to make visible.
     * @throws IllegalArgumentException if the specified band index is invalid.
     */
    public void setVisibleBand(final int visibleBand) throws IllegalArgumentException {
        ensureValidBand(visibleBand);
        this.visibleBand = visibleBand;
    }

    /**
     * Returns a name of the color palette, or a {@linkplain #DEFAULT_PALETTE_NAME default name}
     * if none were explicitly specified.
     */
    final String getNonNullPaletteName() {
        final String palette = getPaletteName();
        return (palette != null) ? palette : DEFAULT_PALETTE_NAME;
    }

    /**
     * Returns the name of the color palette to apply when creating an
     * {@linkplain java.awt.image.IndexColorModel index color model}.
     * This is the name specified by the last call to {@link #setPaletteName(String)}.
     * <p>
     * For a table of available palette names in the default Geotk installation,
     * see the {@link PaletteFactory} class javadoc.
     *
     * @return The name of the color palette to apply, or {@code null} if none.
     */
    public String getPaletteName() {
        return palette;
    }

    /**
     * Sets the color palette as one of the {@linkplain PaletteFactory#getAvailableNames available
     * names} provided by the {@linkplain PaletteFactory#getDefault default palette factory}. This
     * name will be given by the {@link SpatialImageReader} default implementation to the
     * {@linkplain PaletteFactory#getDefault default palette factory} for creating a
     * {@linkplain javax.imageio.ImageTypeSpecifier image type specifier}.
     * <p>
     * For a table of available palette names in the default Geotk installation,
     * see the {@link PaletteFactory} class javadoc.
     *
     * @param palette The name of the color palette to apply.
     *
     * @see PaletteFactory#getAvailableNames
     */
    public void setPaletteName(final String palette) {
        this.palette = palette;
    }

    /**
     * Returns {@code true} if the given kind of sample conversions is allowed. By default, newly
     * constructed {@code SpatialImageReadParam} instances return {@code false} for any given type
     * (i.e. {@link SpatialImageReader} will make its best effort for storing the sample values
     * with no change). However more efficient storage can be achieved if some changes are allowed
     * on the sample values. See {@link #setAllowedConversion setAllowedConversion} for examples.
     *
     * @param  type The kind of conversion.
     * @return Whatever the given kind of conversion is allowed.
     *
     * @since 3.11
     */
    public boolean isAllowedConversion(final SampleConversionType type) {
        return (allowedConversions != null) && allowedConversions.contains(type);
    }

    /**
     * Sets whatever the given kind of sample conversions is allowed. By default, the {@code false}
     * value is assigned to all conversion types (i.e. {@link SpatialImageReader} will make its best
     * effort for storing the sample values with no change). However more efficient storage can be
     * achieved if some changes are allowed on the sample values, for example
     * {@linkplain SampleConversionType#SHIFT_SIGNED_INTEGERS adding an offset to signed integers}
     * in order to ensure that all values are positive.
     *
     * @param type The kind of conversion.
     * @param allowed Whatever the given kind of conversion is allowed.
     *
     * @since 3.11
     */
    public void setAllowedConversion(final SampleConversionType type, final boolean allowed) {
        if (allowed) {
            if (allowedConversions == null) {
                allowedConversions = EnumSet.noneOf(SampleConversionType.class);
            }
            allowedConversions.add(type);
        } else if (allowedConversions != null) {
            allowedConversions.remove(type);
        }
    }

    /**
     * Returns the locale used for formatting error messages, or {@code null} if none.
     * The default implementation returns the locale used by the {@link ImageReader}
     * given at construction time, or {@code null} if none.
     */
    @Override
    public Locale getLocale() {
        return (reader != null) ? reader.getLocale() : null;
    }

    /**
     * Invoked when a warning occured. The default implementation
     * {@linkplain SpatialImageReader#warningOccurred forwards the warning to the image reader}
     * given at construction time if possible, or logs the warning otherwise.
     *
     * @since 3.08
     */
    @Override
    public boolean warningOccurred(final LogRecord record) {
        return Warnings.log(reader, record);
    }

    /**
     * Builds the first part of the string representation of the given parameters.
     * The closing bracket is missing from the buffer, in order to allow callers
     * to add more elements.
     */
    static StringBuilder toStringBegining(final IIOParam param,
            final Rectangle sourceRegion, final Point destinationOffset,
            final int sourceXSubsampling, final int sourceYSubsampling,
            final int[] sourceBands)
    {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(param));
        buffer.append('[');
        if (sourceRegion != null) {
            buffer.append("sourceRegion=(").append(sourceRegion.x).append(',').append(sourceRegion.y)
                  .append(" : ").append(sourceRegion.width).append(',').append(sourceRegion.height)
                  .append("), ");
        }
        if (sourceXSubsampling != 1 || sourceYSubsampling != 1) {
            buffer.append("sourceSubsampling=(").append(sourceXSubsampling).append(',').
                    append(sourceYSubsampling).append("), ");
        }
        if (sourceBands != null) {
            buffer.append("sourceBands={");
            for (int i=0; i<sourceBands.length; i++) {
                if (i != 0) {
                    buffer.append(',');
                }
                buffer.append(sourceBands[i]);
            }
            buffer.append("}, ");
        }
        if (destinationOffset != null && (destinationOffset.x != 0 || destinationOffset.y != 0)) {
            buffer.append("destinationOffset=(").append(destinationOffset.x)
                  .append(',').append(destinationOffset.y).append("), ");
        }
        return buffer;
    }

    /**
     * Returns a string representation of this block of parameters. The default implementation
     * formats the {@linkplain #sourceRegion source region}, subsampling values,
     * {@linkplain #sourceBands source bands}, {@linkplain #destinationOffset destination offset}
     * and the color palette on a single line, completed by the list of
     * {@linkplain DimensionSlice dimension slices} (if any) on the next lines.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = toStringBegining(this,
                sourceRegion, destinationOffset,
                sourceXSubsampling, sourceYSubsampling, sourceBands);
        if (palette != null) {
            buffer.append("palette=\"").append(palette).append('"');
        }
        return toStringEnd(buffer, dimensionSlices != null ? dimensionSlices.identifiersMap : null);
    }

    /**
     * Completes the string representation with the list of dimension slices. If the last character
     * in the given buffer is a space, then this method removes the two last characters on the
     * assumption that they are {@code ", "}. Then the closing {@code ']'} character is appended.
     */
    static String toStringEnd(final StringBuilder buffer, final Map<Object,DimensionSlice> identifiersMap) {
        final int length = buffer.length();
        if (buffer.charAt(length - 1) == ' ') {
            buffer.setLength(length - 2);
        }
        buffer.append(']');
        if (identifiersMap != null) {
            int last = 0;
            for (final DimensionSlice slice : new LinkedHashSet<DimensionSlice>(identifiersMap.values())) {
                last = buffer.append("\n\u00A0\u00A0\u251C\u2500\u00A0").length();
                buffer.append(slice);
            }
            if (last != 0) {
                buffer.append('\n').setCharAt(last - 3, '\u2514');
            }
        }
        return buffer.toString();
    }
}

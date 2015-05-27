/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.operator.ScaleDescriptor;
import org.geotoolkit.image.internal.ImageUtilities;


/**
 * Enumerates different "views" over a given coverage. Coverage views represent the same data
 * in different ways. Some views are more appropriate than others depending of the kind of work
 * to be performed. For example numerical computations on meteorological or oceanographical data
 * should be performed on the {@linkplain #GEOPHYSICS geophysics} view, while renderings are
 * better performed with the {@linkplain #RENDERED rendered} view.
 * <p>
 * Different views are sometime synonymous for a given coverage. For example the
 * {@linkplain #NATIVE native} and {@linkplain #RENDERED rendered} views are identical
 * when the coverage values are unsigned 8 or 16 bits integers, but distinct if the native
 * values are 32 bits integers. This is because in the later case, the 32 bits integer values
 * can not be processed directly by an {@linkplain IndexColorModel index color model}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see GridCoverage2D#view
 *
 * @since 2.4
 * @module
 */
public enum ViewType {
    /**
     * Coverage data come directly from some source (typically a file) and are unprocessed.
     * This view doesn't have any of the restrictions imposed by other views: values may be
     * integers or floating point values, negative values are allowed, and missing data may
     * be represented by "pad values" like -9999. This view is generally not suitable for
     * renderings or numerical computations. However in some special cases, this view may
     * be identical to an other view (see those other views for a more exhaustive list of
     * their conditions):
     * <p>
     * <ul>
     *   <li>If the values are stored as unsigned integers not greater than 16 bits, then the
     *       native view may be identical to the {@linkplain #RENDERED rendered} view.</li>
     *   <li>If all missing values are represented by {@linkplain Float#isNaN some kind of
     *       NaN values}, then the native view may be identical to the
     *       {@linkplain #GEOPHYSICS geophysics} view.</li>
     * </ul>
     * <p>
     * Interpolations other than {@linkplain InterpolationNearest nearest neighbor} are
     * not allowed. Conversions to the RGB color space are not allowed neither, for the
     * same reasons than the {@linkplain #RENDERED rendered} view.
     */
    NATIVE(false, false, false),

    /**
     * Coverage data are packed, usually as integers convertible to geophysics values. The conversion
     * is performed by the {@linkplain org.geotoolkit.coverage.GridSampleDimension#getSampleToGeophysics
     * sample to geophysics} transform (called "<cite>Transfer Function</cite>" in ISO 19115-2).
     * <p>
     * This view is often synonymous to {@link #RENDERED}, but may be different for some data types
     * that are incompatible with {@linkplain IndexColorModel index color model} (e.g. 32 bits
     * integer). This view is always exclusive with {@link #GEOPHYSICS}.
     *
     * @see org.opengis.metadata.content.CoverageContentType#IMAGE
     *
     * @since 2.5
     */
    PACKED(false, false, false),

    /**
     * Coverage data are compatible with common Java2D {@linkplain ColorModel color models}.
     * This usually imply that values are restricted to unsigned integers. This view is often
     * identical to the {@linkplain #PACKED packed} view if the values on the originating
     * device were already unsigned.
     * <p>
     * Conversions to the RGB color space are not allowed, because the data are often related
     * to {@linkplain #GEOPHYSICS geophysics} values in some way. For example the coverage may
     * contains <cite>Sea Surface Temperature</cite> (SST) data packed as 8 bits integers and
     * convertible to degrees Celsius using the following formula: <var>temperature</var> =
     * <var>pixel_value</var> &times; 0.15 - 3. A conversion to RGB space would lose this
     * relationship, and any oceanographical calculation accidentally performed on this space
     * would produce wrong results.
     * <p>
     * Interpolations other than {@linkplain InterpolationNearest nearest neighbor} are not
     * allowed, because some special values are often used as pad values for missing data. An
     * interpolation between a "real" value (for example a value convertible to the above-cited
     * SST) and "pad" value would produce a wrong result.
     *
     * @see org.opengis.metadata.content.CoverageContentType#IMAGE
     */
    RENDERED(false, false, false),

    /**
     * Coverage data are the values of some geophysics phenomenon, for example an elevation
     * in metres or a temperature in Celsius degrees. Values are typically floating point
     * numbers ({@code float} or {@code double} primitive type), but this is not mandatory
     * if there is never fractional parts or missing values in a particular coverage.
     * <p>
     * If the coverage contains some "<cite>no data</cite>" values, then those missing values
     * <strong>must</strong> be represented by {@link Float#NaN} or {@link Double#NaN}
     * constant, or any other value in the NaN range as {@linkplain Float#intBitsToFloat
     * explained there}. Real numbers used as "<cite>pad values</cite>" like {@code -9999} are
     * <strong>not</strong> allowed.
     * <p>
     * Interpolations ({@linkplain InterpolationBilinear bilinear},
     * {@linkplain InterpolationBicubic bicubic}, <i>etc.</i>) are allowed.
     * If there is some missing values around the interpolation point, then the
     * result is a {@code NaN} value.
     * <p>
     * Conversions to RGB color space is not allowed. All computations (including
     * interpolations) must be performed in this geophysics space.
     *
     * @see org.opengis.metadata.content.CoverageContentType#PHYSICAL_MEASUREMENT
     */
    GEOPHYSICS(true, false, false),

    /**
     * Coverage data have no meaning other than visual color. It is not an elevation map for
     * example (in which case the coverage would rather be described as {@linkplain #GEOPHYSICS
     * geophysics}).
     * <p>
     * Conversions to the RGB color space are allowed. Because the coverage has no geophysics
     * meaning other than visual color, there is no significant data lose in the replacement
     * of {@linkplain IndexColorModel index color model}.
     * <p>
     * Interpolation are not allowed on indexed values. They must be performed on the RGB
     * or similar color space instead.
     */
    PHOTOGRAPHIC(true, true, true),

    /**
     * Special value for returning the same coverage unchanged.
     * This value can be used as a "<cite>no operation</cite>" instruction.
     *
     * @since 2.5
     */
    SAME(false, false, false);

    /**
     * {@code true} if interpolations other than {@linkplain InterpolationNearest
     * nearest neighbor} are allowed.
     *
     * @see JAI#KEY_INTERPOLATION
     */
    private final boolean interpolationAllowed;

    /**
     * {@code true} if operations can be performed on the colormap rather than the values.
     *
     * @see JAI#KEY_TRANSFORM_ON_COLORMAP
     */
    private final boolean transformOnColormapAllowed;

    /**
     * {@code true} if the replacement of {@linkplain IndexColorModel index color model}
     * is allowed. This allows the replacement of indexed values by RGB values.
     *
     * @see JAI#KEY_REPLACE_INDEX_COLOR_MODEL
     */
    private final boolean replaceIndexColorModelAllowed;

    /**
     * Creates a new instance of {@code ViewType}.
     */
    private ViewType(final boolean interpolationAllowed,
                     final boolean transformOnColormapAllowed,
                     final boolean replaceIndexColorModelAllowed)
    {
        this.interpolationAllowed          = interpolationAllowed;
        this.transformOnColormapAllowed    = transformOnColormapAllowed;
        this.replaceIndexColorModelAllowed = replaceIndexColorModelAllowed;
    }

    /**
     * Returns {@code true} if interpolations other than {@linkplain InterpolationNearest
     * nearest neighbor} are allowed. Those interpolations require the following conditions:
     * <p>
     * <ul>
     *   <li>Values are either {@linkplain #GEOPHYSICS geophysics} values, or related to
     *       geophysics values through a linear relationship over all the range of possible
     *       values (including "no data" values).</li>
     *   <li>There is no "pad values". Missing values, if any, are represented by some
     *       {@link Float#NaN NaN} values}.</li>
     * </ul>
     * <p>
     * This method may conservatively returns {@code false} if unsure. If interpolations
     * are wanted but not allowed, then users should try to convert the coverage to the
     * {@linkplain #GEOPHYSICS geophysics} space, which supports interpolations. If no
     * geophysics view is available, then users may convert the image to the RGB space
     * if {@linkplain #isReplaceIndexColorModelAllowed replacement of index color model
     * is allowed}. Interpolations in the RGB space produce nice-looking images, but the
     * pixel values lose all geophysical meaning. If the color space conversion is not
     * allowed, then then users should stick with {@linkplain InterpolationNearest nearest
     * neighbor} interpolation.
     *
     * @return {@code true} if interpolations are allowed.
     *
     * @see JAI#KEY_INTERPOLATION
     */
    public boolean isInterpolationAllowed() {
        return interpolationAllowed;
    }

    /**
     * Returns {@code true} if the replacement of {@linkplain IndexColorModel index color model}
     * is allowed. Such replacements may occurs during some operations requirying interpolations,
     * like {@linkplain ScaleDescriptor scale}, in order to produce images that look nicer.
     * However such replacements should be attempted only in last resort (interpolations in the
     * {@linkplain #GEOPHYSICS geophysics} space should be preferred) and only if the coverage
     * data don't have any meaning other than visual color, as in {@linkplain #PHOTOGRAPHIC
     * photographic} images.
     *
     * @return {@code true} if replacement of {@code IndexColorModel} is allowed.
     *
     * @see JAI#KEY_REPLACE_INDEX_COLOR_MODEL
     *
     * @since 2.5
     */
    public boolean isReplaceIndexColorModelAllowed() {
        return replaceIndexColorModelAllowed;
    }

    /**
     * Returns {@code true} if operations can be performed on the colormap rather than the values.
     *
     * @return {@code true} if operations can be performed on the colormap.
     *
     * @see JAI#KEY_TRANSFORM_ON_COLORMAP
     *
     * @since 2.5
     */
    public boolean isTransformOnColormapAllowed() {
        return transformOnColormapAllowed;
    }

    /**
     * Returns suggested rendering hints for a JAI operation on the given image.
     * <p>
     * <ul>
     *   <li>{@link JAI#KEY_INTERPOLATION} is sets to "<cite>nearest neighbor</cite>" if
     *       {@link #isInterpolationAllowed} returns {@code false}, and left unchanged otherwise.</li>
     *   <li>{@link JAI#KEY_REPLACE_INDEX_COLOR_MODEL} is sets to the value returned by
     *       {@link #isReplaceIndexColorModelAllowed}.</li>
     *   <li>{@link JAI#KEY_TRANSFORM_ON_COLORMAP} is sets to the value returned by
     *       {@link #isTransformOnColormapAllowed}.</li>
     * </ul>
     *
     * @param  image The image on which to apply an operation.
     * @return Proposed rendering hints to pass to JAI operation.
     *
     * @since 2.5
     */
    public RenderingHints getRenderingHints(final RenderedImage image) {
        RenderingHints hints = ImageUtilities.getRenderingHints(image);
        if (hints == null) {
            hints = new RenderingHints(null);
        }
        if (!isInterpolationAllowed()) {
            hints.put(JAI.KEY_INTERPOLATION, Interpolation.getInstance(Interpolation.INTERP_NEAREST));
        }
        hints.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.valueOf(isReplaceIndexColorModelAllowed()));
        hints.put(JAI.KEY_TRANSFORM_ON_COLORMAP,     Boolean.valueOf(isTransformOnColormapAllowed()));
        return hints;
    }
}

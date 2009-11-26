/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.internal.image.io;

import org.geotoolkit.math.XMath;
import org.geotoolkit.lang.Static;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.image.io.metadata.SampleDimension;


/**
 * Utility methods related to metadata.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.06
 * @module
 */
@Static
public final class MetadataHelper {
    /**
     * Do not allow instantiation of this class.
     */
    private MetadataHelper() {
    }

    /**
     * Returns the range of sample values defined in the given {@code SampleDimension} object. This
     * method first looks at the value returned by {@link SampleDimension#getValidSampleValues()}.
     * If the later returns {@code null}, then this method tries to build the range from the other
     * metadata attributes.
     * <p>
     * The value of the {@code nodataValues} argument can be obtained by a call to
     * {@link SampleDimension#getFillSampleValues()}. It is requested explicitly by this method
     * because it is not an ISO 19115-2 method, and in many cases the caller already known the
     * fill values.
     *
     * @param  dimension The object from which to extract the range.
     * @param  nodataValues The no-data values, or {@code null} if none.
     * @return The range of sample values, or {@code null}.
     */
    public static NumberRange<?> getValidSampleValues(final SampleDimension dimension,
            final double[] nodataValues)
    {
        NumberRange<?> range = null;
        if (dimension != null) {
            range = dimension.getValidSampleValues();
            if (range == null) {
                Double minimum = dimension.getMinValue();
                Double maximum = dimension.getMaxValue();
                boolean isMinInclusive = true;
                boolean isMaxInclusive = true;
                if (nodataValues != null) {
                    isMinInclusive = inclusive(minimum, nodataValues);
                    isMaxInclusive = inclusive(maximum, nodataValues);
                }
                Double n = dimension.getScaleFactor();
                final double scale = (n != null) ? n : 1;
                n = dimension.getOffset();
                final double offset = (n != null) ? n : 0;
                if (scale != 1 || offset != 0) {
                    if (minimum != null) minimum = minimum * scale + offset;
                    if (maximum != null) maximum = maximum * scale + offset;
                }
                range = NumberRange.createBestFit(minimum, isMinInclusive, maximum, isMaxInclusive);
            }
        }
        return range;
    }

    /**
     * Returns {@code true} if the given {@code nodataValues} array does <strong>not</strong>
     * contains the given value. In such case, the value can be considered inclusive.
     */
    private static boolean inclusive(final Number value, final double[] nodataValues) {
        if (value != null) {
            final double n = value.doubleValue();
            for (final double c : nodataValues) {
                if (c == n) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Tries to make the given scale value a "nice" number. If the given number multiplied
     * by 360 (an arbitrary value choosen because it is a multiple of units commonly used)
     * is almost an integer, round that value to an integer and divide by 360. Otherwise
     * returns the original value unchanged (do not return the result of multiplication
     * followed by a division, in order to avoir rounding error).
     * <p>
     * This method is especially useful for reading the offset vectors, because they are
     * used as the scale factor in affine transform. Rendering an image with Java2D is
     * often faster when the affine transform coefficients are integers.
     */
    static double nice(final double value) {
        final double c1 = value * 360;
        final double c2 = XMath.roundIfAlmostInteger(c1, 3);
        return (c1 != c2) ? c2/360 : value;
    }
}

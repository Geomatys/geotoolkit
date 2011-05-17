/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.util;


/**
 * Indicates that this object can be compared for equality using different levels of strictness.
 * For example {@link org.opengis.referencing.operation.MathTransform} implementations can be
 * compared ignoring some properties (remarks, <i>etc.</i>) that are not relevant to the
 * coordinates calculation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
public interface LenientComparable {
    /**
     * Compares this object with the specified object for equality.
     * The strictness level is controlled by the second argument.
     *
     * @param  other The object to compare to {@code this}.
     * @param  mode The strictness level of the comparison.
     * @return {@code true} if both objects are equal.
     */
    boolean equals(Object other, ComparisonMode mode);

    /**
     * Returns {@code true} if this object is strictly equals to the given object.
     * This method is typically implemented as below:
     *
     * {@preformat
     *     return equals(other, ComparisonMode.STRICT);
     * }
     *
     * In Geotk implementations, this method is typically {@code final} in order to ensure that
     * subclasses override the above {@link #equals(Object, ComparisonMode)} method instead.
     *
     * @param  other The object to compare to {@code this}.
     * @return {@code true} if both objects are strictly equal.
     *
     * @see ComparisonMode#STRICT
     */
    @Override
    boolean equals(Object other);
}

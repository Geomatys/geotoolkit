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
package org.geotoolkit.util;


/**
 * Specifies the degree of strictness when comparing two {@link LenientComparable} objects
 * for equality. This enumeration is <strong>ordered</strong> from stricter to more lenient
 * degrees: {@link #STRICT}, {@link #BY_CONTRACT}, {@link #IGNORE_METADATA}.
 * <p>
 * if two objects are equal at some degree of strictness <var>E</var>, then they should also
 * be equal at all degrees listed below <var>E</var> in this page. For example if two objects
 * are equal at the degree {@link #BY_CONTRACT}, then they should also be equal at the degree
 * {@link #IGNORE_METADATA} but not necessarily at the degree {@link #STRICT}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see LenientComparable#equals(Object, ComparisonMode)
 * @see Utilities#deepEquals(Object, Object, ComparisonMode)
 *
 * @since 3.18 (derived from 3.14)
 * @module
 */
public enum ComparisonMode {
    /**
     * All attributes of the compared objects shall be strictly equal. This comparison mode
     * is equivalent to the {@link Object#equals(Object)} method, and must be compliant with
     * the contract documented in that method. In particular, this comparison mode shall be
     * consistent with {@link Object#hashCode()} and be symmetric ({@code A.equals(B)} implies
     * {@code B.equals(A)}).
     *
     * {@section Implementation note}
     * In the Geotk implementation, this comparison mode usually have the following
     * characteristics (not always, this is only typical):
     * <p>
     * <ul>
     *   <li>The objects being compared need to be the same implementation class.</li>
     *   <li>Private fields are compared directly instead than invoking public getter methods.</li>
     * </ul>
     *
     * @see Object#equals(Object)
     */
    STRICT,

    /**
     * Only the attributes published in some contract (typically a GeoAPI interface) need
     * to be compared. The implementation classes do not need to be the same and some private
     * attributes may be ignored.
     * <p>
     * Note that this comparison mode does <strong>not</strong> guaranteed {@link Object#hashCode()}
     * consistency, neither comparison symmetry (i.e. {@code A.equals(B)} and {@code B.equals(A)} may
     * return different results if the {@code equals} methods are implemented differently).
     *
     * {@section Implementation note}
     * In the Geotk implementation, this comparison mode usually have the following
     * characteristics (not always, this is only typical):
     * <p>
     * <ul>
     *   <li>The objects being compared need to implement the same GeoAPI interfaces.</li>
     *   <li>Public getter methods are used (no direct access to private fields).</li>
     * </ul>
     */
    BY_CONTRACT,

    /**
     * Only the attributes relevant to the object functionality are compared. Attributes that
     * are only informative can be ignored. This comparison mode is typically less strict than
     * {@link #BY_CONTRACT}.
     *
     * {@section Example}
     * If the objects being compared are
     * {@link org.opengis.referencing.operation.MathTransform}, then only the properties relevant
     * to the coordinate computation shall be compared. Metadata like the identifier or the domain
     * of validity, which have no impact on the coordinates being calculated, shall be ignored.
     *
     * @see org.geotoolkit.referencing.CRS#equalsIgnoreMetadata(Object, Object)
     */
    IGNORE_METADATA
}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import org.geotoolkit.lang.Debug;


/**
 * Specifies the degree of strictness when comparing two {@link LenientComparable} objects
 * for equality. This enumeration is <strong>ordered</strong> from stricter to more lenient
 * degrees: {@link #STRICT}, {@link #BY_CONTRACT}, {@link #IGNORE_METADATA}, {@link #APPROXIMATIVE}.
 * <p>
 * if two objects are equal at some degree of strictness <var>E</var>, then they should also
 * be equal at all degrees listed below <var>E</var> in this page. For example if two objects
 * are equal at the degree {@link #BY_CONTRACT}, then they should also be equal at the degree
 * {@link #IGNORE_METADATA} but not necessarily at the degree {@link #STRICT}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see LenientComparable#equals(Object, ComparisonMode)
 * @see Utilities#deepEquals(Object, Object, ComparisonMode)
 *
 * @since 3.18 (derived from 3.14)
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ComparisonMode}.
 */
@Deprecated
public final class ComparisonMode {
    private ComparisonMode() {
    }

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
    public static final org.apache.sis.util.ComparisonMode STRICT = org.apache.sis.util.ComparisonMode.STRICT;

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
    public static final org.apache.sis.util.ComparisonMode BY_CONTRACT = org.apache.sis.util.ComparisonMode.BY_CONTRACT;

    /**
     * Only the attributes relevant to the object functionality are compared. Attributes that
     * are only informative can be ignored. This comparison mode is typically less strict than
     * {@link #BY_CONTRACT}.
     *
     * {@section Examples}
     * If the objects being compared are
     * {@link org.opengis.referencing.crs.CoordinateReferenceSystem} instances, then only the
     * properties relevant to the coordinate localization shall be compared. Metadata like the
     * {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem#getIdentifiers() identifiers}
     * or the {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem#getDomainOfValidity()
     * domain of validity}, which have no impact on the coordinates being calculated, shall be ignored.
     * <p>
     * If the objects being compared are {@link org.opengis.referencing.operation.MathTransform}
     * instances, then two transforms defined in a different way may be considered equivalent.
     * For example it is possible to define a
     * {@linkplain org.geotoolkit.referencing.operation.projection.Mercator Mercator} projection
     * in two different ways, as a {@code "Mercator (1SP)"} or a {@code "Mercator (2SP)"} projection,
     * each having their own set of parameters. The {@link #STRICT} or {@link #BY_CONTRACT} modes
     * shall consider two projections as equal only if their
     * {@linkplain org.geotoolkit.referencing.operation.transform.AbstractMathTransform#getParameterValues()
     * parameter values} are strictly identical, while the {@code IGNORE_METADATA} mode can consider
     * those objects as equivalent despite difference in the set of parameters, as long as coordinate
     * transformations still produce the same results.
     *
     * <blockquote><font size="-1"><b>Example:</b> A {@code "Mercator (2SP)"} projection with a
     * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#standardParallels
     * standard parallel} value of 60° produces the same results than a {@code "Mercator (1SP)"} projection with a
     * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor scale
     * factor} value of 0.5.</font></blockquote>
     *
     * @see org.geotoolkit.referencing.CRS#equalsIgnoreMetadata(Object, Object)
     */
    public static final org.apache.sis.util.ComparisonMode IGNORE_METADATA = org.apache.sis.util.ComparisonMode.IGNORE_METADATA;

    /**
     * Only the attributes relevant to the object functionality are compared, with some tolerance
     * threshold on numerical values.
     *
     * {@section Application to coordinate transforms}
     * If two {@link org.opengis.referencing.operation.MathTransform} objects are considered equal
     * according this mode, then for any given identical source position, the two compared transforms
     * shall compute at least approximatively the same target position. A small difference is
     * tolerated between the target coordinates calculated by the two math transforms. How small
     * is "small" is implementation dependent - the threshold can not be specified in the current
     * implementation, because of the non-linear nature of map projections.
     */
    public static final org.apache.sis.util.ComparisonMode APPROXIMATIVE = org.apache.sis.util.ComparisonMode.APPROXIMATIVE;

    /**
     * Same as {@link #APPROXIMATIVE}, except that an {@link AssertionError} is thrown if the two
     * objects are not equal and assertions are enabled. The exception message and stack trace help
     * to locate which attributes are not equal. This mode is typically used in assertions like below:
     *
     * {@preformat java
     *     assert Utilities.deepEquals(object1, object2, ComparisonMode.DEBUG);
     * }
     *
     * Note that a comparison in {@code DEBUG} mode may still return {@code false} without
     * throwing an exception, since not all corner cases are tested. The exception is only
     * intended to provide more details for some common cases.
     *
     * @since 3.20
     */
    @Debug
    public static final org.apache.sis.util.ComparisonMode DEBUG = org.apache.sis.util.ComparisonMode.DEBUG;

    /**
     * If the two given objects are equals according one of the mode enumerated in this class,
     * then returns that mode. Otherwise returns {@code null}. This method is used mostly for
     * diagnostic purpose.
     *
     * @param  o1 The first object to compare, or {@code null}.
     * @param  o2 The second object to compare, or {@code null}.
     * @return The must suitable comparison mode, or {@code null} if the two given objects
     *         are not equal for any mode in this enumeration.
     */
    public static org.apache.sis.util.ComparisonMode equalityLevel(final Object o1, Object o2) {
        return org.apache.sis.util.ComparisonMode.equalityLevel(o1, o2);
    }
}

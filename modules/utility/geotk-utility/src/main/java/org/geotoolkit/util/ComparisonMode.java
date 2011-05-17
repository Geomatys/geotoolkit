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
 * for equality.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18 (derived from 3.14)
 * @module
 */
public enum ComparisonMode {
    /**
     * All attributes of the compared objects shall be strictly equal. The contract of
     * this comparison mode is the same than the {@link Object#equals(Object)} contract.
     * <p>
     * In the Geotk implementation, this comparison mode usually implies that the objects
     * being compared are of the same class, in order to ensure {@link Object#hashCode()}
     * consistency and comparison symmetry ({@code A.equals(B)} implies {@code B.equals(A)}).
     */
    STRICT,

    /**
     * Only the attributes published in some contract (typically a GeoAPI interface) need
     * to be compared. The implementation class doesn't need to be the same and some private
     * attributes may be ignored.
     * <p>
     * Note that this comparison mode does <strong>not</strong> guaranteed {@link Object#hashCode()}
     * consistency, neither comparison symmetry (i.e. {@code A.equals(B)} and {@code B.equals(A)} may
     * return different results if the {@code equals} methods are implemented differently).
     */
    BY_CONTRACT,

    /**
     * Only the attributes relevant to the object functionality are compared. Attributes that
     * are only informative can be ignored. This comparison mode is typically less strict than
     * {@link #BY_CONTRACT}.
     * <p>
     * For example if the objects being compared are
     * {@link org.opengis.referencing.operation.MathTransform}, then only the properties relevant
     * to the coordinate computation shall be compared. Metadata like the identifier or the domain
     * of validity, which have no impact on the coordinates being calculated, shall be ignored.
     *
     * @see org.geotoolkit.referencing.CRS#equalsIgnoreMetadata(Object, Object)
     */
    IGNORE_METADATA
}

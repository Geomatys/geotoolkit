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
 * {@section Conditions for equality}
 * <p>{@link org.geotoolkit.metadata.iso.MetadataEntity} subclasses</p>
 * <table border="1" cellspacing="0" cellpadding="2">
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#STRICT STRICT}</th>
 * <td>Objects must be of the same class and all attributes must be equal, including
 * {@linkplain org.geotoolkit.metadata.iso.MetadataEntity#getIdentifiers() identifiers}
 * and {@linkplain org.geotoolkit.metadata.iso.MetadataEntity#getXLink() xlinks}.</td></tr>
 *
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#BY_CONTRACT BY_CONTRACT}</th>
 * <td>The same attributes than the above {@code STRICT} mode must be equal, but the metadata
 * object don't need to be implemented by the same class, provided that they implement the
 * same GeoAPI interface.</td></tr>
 *
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA}</th>
 * <td>Only the attributes defined in the GeoAPI interfaces are compared. The above-cited identifiers
 * and {@code xlinks} attributes are ignored.</td></tr>
 *
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#APPROXIMATIVE APPROXIMATIVE}</th>
 * <td>The same attributes than the above {@code IGNORE_METADATA} mode are compared, but a slight
 * (implementation dependant) difference is tolerated in floating point numbers.</td></tr>
 * </table>
 *
 * <p>&nbsp;</p>
 * <p>{@link org.geotoolkit.referencing.AbstractIdentifiedObject} subclasses</p>
 * <table border="1" cellspacing="0" cellpadding="2">
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#STRICT STRICT}</th>
 * <td>Objects must be of the same class and all attributes must be equal.</td></tr>
 *
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#BY_CONTRACT BY_CONTRACT}</th>
 * <td>The same attributes than the above {@code STRICT} mode must be equal, but the referencing
 * object don't need to be implemented by the same class, provided that they implement the
 * same GeoAPI interface.</td></tr>
 *
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA}</th>
 * <td>The
 * {@linkplain org.geotoolkit.referencing.crs.AbstractCRS#getIdentifiers() identifiers},
 * {@linkplain org.geotoolkit.referencing.crs.AbstractCRS#getAlias() aliases},
 * {@linkplain org.geotoolkit.referencing.crs.AbstractCRS#getScope() scope},
 * {@linkplain org.geotoolkit.referencing.crs.AbstractCRS#getDomainOfValidity() domain of validity} and
 * {@linkplain org.geotoolkit.referencing.crs.AbstractCRS#getRemarks() remarks} are ignored because
 * they have no incidence on the coordinate values to be computed by
 * {@linkplain org.opengis.referencing.operation.ConcatenatedOperation coordinate operations}.
 * All other attributes that are relevant to coordinate calculations, must be equal.</td></tr>
 *
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#APPROXIMATIVE APPROXIMATIVE}</th>
 * <td>The same attributes than the above {@code IGNORE_METADATA} mode are compared, but a slight
 * (implementation dependant) difference is tolerated in floating point numbers.</td></tr>
 * </table>
 *
 * <p>&nbsp;</p>
 * <p>{@link org.geotoolkit.referencing.operation.transform.AbstractMathTransform} subclasses</p>
 * If the transforms implement the {@link org.geotoolkit.referencing.operation.transform.LinearTransform}
 * interface and the comparison mode is anything else than {@code STRICT}, then the transform
 * {@linkplain org.geotoolkit.referencing.operation.transform.LinearTransform#getMatrix() matrixes}
 * are compared as documented in the next section below. Otherwise:
 *
 * <table border="1" cellspacing="0" cellpadding="2">
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#STRICT STRICT}</th>
 * <td>Objects must be of the same class and all attributes must be equal, including the
 * {@linkplain org.geotoolkit.referencing.operation.transform.AbstractMathTransform#getParameterValues()
 * parameter values}.</td></tr>
 *
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#BY_CONTRACT BY_CONTRACT}</th>
 * <td>Synonymous to the {@code STRICT} mode, because there is no GeoAPI interfaces for the various
 * kind of math transforms.</td></tr>
 *
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA}</th>
 * <td>Objects must be of the same class, but the parameter values can be different if they are
 * different way to formulate the same transform. For example a {@code "Mercator (2SP)"} projection
 * with a {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#standardParallels
 * standard parallel} value of 60° produces the same results than a {@code "Mercator (1SP)"} projection with a
 * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor scale
 * factor} value of 0.5</td></tr>
 *
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#APPROXIMATIVE APPROXIMATIVE}</th>
 * <td>The same attributes than the above {@code IGNORE_METADATA} mode are compared, but a slight
 * (implementation dependant) difference is tolerated in floating point numbers.</td></tr>
 * </table>
 *
 * <p>&nbsp;</p>
 * <p>{@link org.geotoolkit.referencing.operation.matrix.XMatrix} implementations</p>
 * <table border="1" cellspacing="0" cellpadding="2">
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#STRICT STRICT}</th>
 * <td>Objects must be of the same class, matrixes must have the same size and all matrix
 * elements must be equal.</td></tr>
 *
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#BY_CONTRACT BY_CONTRACT}</th>
 * <td>Matrixes must have the same size and all matrix elements must be equal, but the matrixes
 * are not required to be the same implementation class
 * (any {@link org.opengis.referencing.operation.Matrix} is okay).</td></tr>
 *
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA}</th>
 * <td>Synonymous to the {@code BY_CONTRACT} mode, because matrixes don't have metadata.</td></tr>
 *
 * <tr><th bgcolor="lightblue" align="left">{@link ComparisonMode#APPROXIMATIVE APPROXIMATIVE}</th>
 * <td>The same attributes than the above {@code BY_CONTRACT} mode are compared, but a slight
 * (implementation dependant) difference is tolerated in floating point numbers.</td></tr>
 * </table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
public interface LenientComparable {
    /**
     * Compares this object with the given object for equality.
     * The strictness level is controlled by the second argument.
     *
     * @param  other The object to compare to {@code this}.
     * @param  mode The strictness level of the comparison.
     * @return {@code true} if both objects are equal.
     *
     * @see Utilities#deepEquals(Object, Object, ComparisonMode)
     */
    boolean equals(Object other, ComparisonMode mode);

    /**
     * Returns {@code true} if this object is strictly equals to the given object.
     * This method is typically implemented as below:
     *
     * {@preformat java
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

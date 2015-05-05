/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.crs;

import java.util.Map;
import java.util.Collections;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.DerivedCRS;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.geometry.MismatchedDimensionException;

import org.opengis.referencing.crs.SingleCRS;
import org.geotoolkit.referencing.operation.DefaultConversion;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.apache.sis.referencing.operation.DefaultOperationMethod;


/**
 * A coordinate reference system that is defined by its coordinate conversion from another
 * CRS but is not a projected CRS. This category includes coordinate reference systems derived
 * from a projected CRS, for example in order to use a {@link org.opengis.referencing.cs.PolarCS}.
 *
 * {@section Note on the inclined case (two-dimensional)}
 * Some methods like {@link org.geotoolkit.referencing.CRS#isHorizontalCRS(CoordinateReferenceSystem)}
 * assume that the axes of the two-dimensional derived CRS are coplanar with the axes of the base CRS.
 * This is not always the case; for example it is possible to define a {@code DerivedCRS} on a plane
 * which is inclined relative to the base CRS. ISO 19111 does not specify how to handle such cases.
 * In Geotk we suggest a slight departure from the ISO standard: assign to the inclined {@code DerivedCRS}
 * a datum which is different than the base datum and which is not a
 * {@link org.opengis.referencing.datum.GeodeticDatum}.
 *
 * @todo Provides an API for specifying the datum at construction time which is different
 *       than the datum of the base CRS.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultDerivedCRS extends AbstractDerivedCRS implements DerivedCRS {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8149602276542469876L;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultDerivedCRS() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new derived CRS with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param crs The coordinate reference system to copy.
     *
     * @since 2.2
     */
    public DefaultDerivedCRS(final DerivedCRS crs) {
        super(crs);
    }

    /**
     * Constructs a derived CRS from a name. A {@linkplain DefaultOperationMethod default
     * operation method} is inferred from the {@linkplain AbstractMathTransform math transform}.
     * This is a convenience constructor that is not guaranteed to work reliably for non-Geotk
     * implementations. Use the constructor expecting a {@linkplain DefiningConversion defining
     * conversion} for more determinist result.
     *
     * @param  name The name.
     * @param  base Coordinate reference system to base the derived CRS on.
     * @param  baseToDerived The transform from the base CRS to returned CRS.
     * @param  derivedCS The coordinate system for the derived CRS. The number of axes
     *         must match the target dimension of the transform {@code baseToDerived}.
     * @throws MismatchedDimensionException if the source and target dimension of
     *         {@code baseToDeviced} don't match the dimension of {@code base}
     *         and {@code derivedCS} respectively.
     *
     * @since 2.5
     */
    public DefaultDerivedCRS(final String                    name,
                             final SingleCRS base,
                             final MathTransform    baseToDerived,
                             final CoordinateSystem     derivedCS)
            throws MismatchedDimensionException
    {
        this(Collections.singletonMap(NAME_KEY, name), base, baseToDerived, derivedCS);
    }

    /**
     * Constructs a derived CRS from a set of properties. A {@linkplain DefaultOperationMethod
     * default operation method} is inferred from the {@linkplain AbstractMathTransform math
     * transform}. This is a convenience constructor that is not guaranteed to work reliably
     * for non-Geotk implementations. Use the constructor expecting a {@linkplain
     * DefiningConversion defining conversion} for more determinist result.
     * <p>
     * The properties are given unchanged to the
     * {@linkplain AbstractDerivedCRS#AbstractDerivedCRS(Map, CoordinateReferenceSystem,
     * MathTransform, CoordinateSystem) super-class constructor}.
     *
     * @param  properties Name and other properties to give to the new derived CRS object
     *         and to the underlying {@linkplain DefaultConversion conversion}.
     * @param  base Coordinate reference system to base the derived CRS on.
     * @param  baseToDerived The transform from the base CRS to returned CRS.
     * @param  derivedCS The coordinate system for the derived CRS. The number of axes
     *         must match the target dimension of the transform {@code baseToDerived}.
     * @throws MismatchedDimensionException if the source and target dimension of
     *         {@code baseToDeviced} don't match the dimension of {@code base}
     *         and {@code derivedCS} respectively.
     *
     * @since 2.5
     */
    public DefaultDerivedCRS(final Map<String,?>       properties,
                             final SingleCRS base,
                             final MathTransform    baseToDerived,
                             final CoordinateSystem     derivedCS)
            throws MismatchedDimensionException
    {
        super(properties, base, baseToDerived, derivedCS);
    }

    /**
     * Constructs a derived CRS from a {@linkplain DefiningConversion defining conversion}. The
     * properties are given unchanged to the {@linkplain AbstractDerivedCRS#AbstractDerivedCRS(Map,
     * Conversion, CoordinateReferenceSystem, MathTransform, CoordinateSystem) super-class constructor}.
     *
     * @param  properties Name and other properties to give to the new derived CRS object.
     * @param  conversionFromBase The {@linkplain DefiningConversion defining conversion}.
     * @param  base Coordinate reference system to base the derived CRS on.
     * @param  baseToDerived The transform from the base CRS to returned CRS.
     * @param  derivedCS The coordinate system for the derived CRS. The number of axes
     *         must match the target dimension of the transform {@code baseToDerived}.
     * @throws MismatchedDimensionException if the source and target dimension of
     *         {@code baseToDerived} don't match the dimension of {@code base}
     *         and {@code derivedCS} respectively.
     */
    public DefaultDerivedCRS(final Map<String,?>       properties,
                             final Conversion  conversionFromBase,
                             final SingleCRS base,
                             final MathTransform    baseToDerived,
                             final CoordinateSystem     derivedCS)
            throws MismatchedDimensionException
    {
        super(properties, conversionFromBase, base, baseToDerived, derivedCS);
    }

    /**
     * Returns a Geotk CRS implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultDerivedCRS castOrCopy(final DerivedCRS object) {
        return (object == null) || (object instanceof DefaultDerivedCRS)
                ? (DefaultDerivedCRS) object : new DefaultDerivedCRS(object);
    }

    /**
     * Returns the GeoAPI interface implemented by this class.
     * The SIS implementation returns {@code DerivedCRS.class}.
     *
     * {@note Subclasses usually do not need to override this method since GeoAPI does not define
     *        <code>DerivedCRS</code> sub-interface. Overriding possibility is left mostly for
     *        implementors who wish to extend GeoAPI with their own set of interfaces.}
     *
     * @return {@code DerivedCRS.class} or a user-defined sub-interface.
     */
    @Override
    public Class<? extends DerivedCRS> getInterface() {
        return DerivedCRS.class;
    }
}

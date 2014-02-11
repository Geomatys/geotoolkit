/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
import net.jcip.annotations.Immutable;
import javax.measure.unit.Unit;

import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.cs.CoordinateSystem;

import org.geotoolkit.internal.referencing.NilReferencingObject;
import org.apache.sis.referencing.AbstractReferenceSystem;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.io.wkt.Formatter;

import static org.apache.sis.util.Utilities.deepEquals;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Abstract coordinate reference system, consisting of a single
 * {@linkplain CoordinateSystem coordinate system} and a single
 * {@linkplain Datum datum} (as opposed to {@linkplain DefaultCompoundCRS compound CRS}).
 * <p>
 * A coordinate reference system consists of an ordered sequence of coordinate system
 * axes that are related to the earth through a datum. A coordinate reference system
 * is defined by one datum and by one coordinate system. Most coordinate reference system
 * do not move relative to the earth, except for engineering coordinate reference systems
 * defined on moving platforms such as cars, ships, aircraft, and spacecraft.
 * <p>
 * Coordinate reference systems are commonly divided into sub-types. The common classification
 * criterion for sub-typing of coordinate reference systems is the way in which they deal with
 * earth curvature. This has a direct effect on the portion of the earth's surface that can be
 * covered by that type of CRS with an acceptable degree of error. The exception to the rule is
 * the subtype "Temporal" which has been added by analogy.
 * <p>
 * This class is conceptually <cite>abstract</cite>, even if it is technically possible to
 * instantiate it. Typical applications should create instances of the most specific subclass with
 * {@code Default} prefix instead. An exception to this rule may occurs when it is not possible to
 * identify the exact type.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @see org.geotoolkit.referencing.cs.AbstractCS
 * @see org.geotoolkit.referencing.datum.AbstractDatum
 *
 * @since 2.1
 * @module
 */
@Immutable
public class AbstractSingleCRS extends AbstractCRS implements SingleCRS {
    /**
     * The datum. This field should be considered as final.
     * It is modified only by JAXB at unmarshalling time.
     */
    private Datum datum;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private AbstractSingleCRS() {
        this(NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new coordinate reference system with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param crs The coordinate reference system to copy.
     *
     * @since 2.2
     */
    public AbstractSingleCRS(final SingleCRS crs) {
        super(crs);
        datum = crs.getDatum();
    }

    /**
     * Constructs a coordinate reference system from a set of properties.
     * The properties are given unchanged to the
     * {@linkplain AbstractReferenceSystem#AbstractReferenceSystem(Map) base-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param datum The datum.
     * @param cs The coordinate system.
     */
    public AbstractSingleCRS(final Map<String,?> properties,
                             final Datum datum,
                             final CoordinateSystem cs)
    {
        super(properties, cs);
        this.datum = datum;
        ensureNonNull("datum", datum);
    }

    /**
     * Returns the GeoAPI interface implemented by this class.
     * The default implementation returns {@code SingleCRS.class}.
     * Subclasses implementing a more specific GeoAPI interface shall override this method.
     *
     * @return The coordinate reference system interface implemented by this class.
     */
    @Override
    public Class<? extends SingleCRS> getInterface() {
        return SingleCRS.class;
    }

    /**
     * Returns the datum.
     *
     * @return The datum.
     */
    @Override
    public Datum getDatum() {
        return datum;
    }

    /**
     * Sets the datum. This method is invoked only by JAXB at unmarshalling time
     * and can be invoked only if the datum has never been set.
     *
     * @throws IllegalStateException If the datum has already been set.
     */
    final void setDatum(final Datum datum) {
        if (this.datum != NilReferencingObject.INSTANCE) {
            throw new IllegalStateException();
        }
        ensureNonNull("datum", datum);
        this.datum = datum;
    }

    /**
     * Compares this coordinate reference system with the specified object for equality.
     * If the {@code mode} argument value is {@link ComparisonMode#STRICT STRICT} or
     * {@link ComparisonMode#BY_CONTRACT BY_CONTRACT}, then all available properties are
     * compared including the {@linkplain #getDomainOfValidity() domain of validity} and
     * the {@linkplain #getScope() scope}.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (super.equals(object, mode)) {
            switch (mode) {
                case STRICT: {
                    return datum.equals(((AbstractSingleCRS) object).datum);
                }
                default: {
                    final SingleCRS that = (SingleCRS) object;
                    return deepEquals(getDatum(), that.getDatum(), mode);
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected long computeHashCode() {
        return super.computeHashCode() + 31*datum.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatTo(final Formatter formatter) {  // TODO: should be protected.
        super.formatTo(formatter);
        formatter.append(datum);
        final Unit<?> unit = getUnit();
        formatter.newLine();
        formatter.append(unit);
        final CoordinateSystem cs = getCoordinateSystem();
        final int dimension = cs.getDimension();
        for (int i=0; i<dimension; i++) {
            formatter.newLine();
            formatter.append(cs.getAxis(i));
        }
        if (unit == null) {
            formatter.setInvalidWKT(cs, null);
        }
        formatter.newLine(); // For writing the ID[…] element on its own line.
        return null;
    }
}

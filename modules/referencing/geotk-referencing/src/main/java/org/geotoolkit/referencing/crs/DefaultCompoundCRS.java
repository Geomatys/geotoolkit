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
import java.util.List;
import java.util.Collections;
import javax.xml.bind.annotation.XmlTransient;

import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.Datum;

import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.cs.AxisRangeType;
import org.apache.sis.referencing.AbstractReferenceSystem;
import org.geotoolkit.internal.referencing.CRSUtilities;


/**
 * A coordinate reference system describing the position of points through two or more
 * independent coordinate reference systems. Thus it is associated with two or more
 * {@linkplain CoordinateSystem coordinate systems} and {@linkplain Datum datums} by
 * defining the compound CRS as an ordered set of two or more instances of
 * {@link CoordinateReferenceSystem}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 1.2
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@XmlTransient
public class DefaultCompoundCRS extends org.apache.sis.referencing.crs.DefaultCompoundCRS {
    /**
     * Coordinate reference systems equivalent to this one, except for a shift in the range of
     * longitude values. This field is computed by {@link #shiftAxisRange(AxisRangeType)}
     * when first needed.
     *
     * @since 3.20
     */
    private transient DefaultCompoundCRS[] shifted;

    /**
     * Constructs a new compound CRS with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param crs The coordinate reference system to copy.
     *
     * @since 2.2
     */
    public DefaultCompoundCRS(final CompoundCRS crs) {
        super(crs);
    }

    /**
     * Constructs a coordinate reference system from a name.
     *
     * @param name The name.
     * @param components The array of coordinate reference system making this compound CRS.
     */
    public DefaultCompoundCRS(final String name, final CoordinateReferenceSystem... components) {
        super(Collections.singletonMap(NAME_KEY, name), components);
    }

    /**
     * Constructs a coordinate reference system from a set of properties.
     * The properties are given unchanged to the
     * {@linkplain AbstractReferenceSystem#AbstractReferenceSystem(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param components The array of coordinate reference system making this compound CRS.
     */
    public DefaultCompoundCRS(final Map<String,?> properties, final CoordinateReferenceSystem... components) {
        super(properties, components);
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
    public static DefaultCompoundCRS castOrCopy(final CompoundCRS object) {
        return (object == null) || (object instanceof DefaultCompoundCRS)
                ? (DefaultCompoundCRS) object : new DefaultCompoundCRS(object);
    }

    /**
     * Returns the ordered list of single coordinate reference systems. If this compound CRS
     * contains other compound CRS, all of them are expanded in an array of {@code SingleCRS}
     * objects.
     *
     * @return The single coordinate reference systems as an unmodifiable list.
     */
    public List<SingleCRS> getSingleCRS() {
        return super.getSingleComponents();
    }

    /**
     * Returns the ordered list of single coordinate reference systems for the specified CRS.
     * The specified CRS doesn't need to be a Geotk implementation.
     *
     * @param  crs The coordinate reference system, or {@code null}.
     * @return The single coordinate reference systems, or an empty list if the
     *         given CRS is neither a {@link SingleCRS} or a {@link CompoundCRS}.
     */
    public static List<SingleCRS> getSingleCRS(final CoordinateReferenceSystem crs) {
        return org.apache.sis.referencing.CRS.getSingleComponents(crs);
    }

    /**
     * Returns a coordinate reference system with the same axes than this CRS, except that the
     * longitude axis is shifted to a positive or negative range. This method can be used in
     * order to shift between the [-180 … +180]° and [0 … 360]° ranges.
     * <p>
     * This method shifts the axis {@linkplain CoordinateSystemAxis#getMinimumValue() minimum}
     * and {@linkplain CoordinateSystemAxis#getMaximumValue() maximum} values by a multiple of
     * 180°, converted to the units of the axis.
     * <p>
     * This method does not change the meaning of ordinate values. For example a longitude of
     * -60° still locate the same longitude in the old and the new coordinate system. But the
     * preferred way to locate that longitude may become the 300° value if the range has been
     * shifted to positive values.
     *
     * @param  range {@link AxisRangeType#POSITIVE_LONGITUDE POSITIVE_LONGITUDE} for a range
     *         of positive longitude values, or {@link AxisRangeType#SPANNING_ZERO_LONGITUDE
     *         SPANNING_ZERO_LONGITUDE} for a range of positive and negative longitude values.
     * @return A coordinate reference system using the given kind of longitude range
     *         (may be {@code this}).
     *
     * @see DefaultGeographicCRS#shiftAxisRange(AxisRangeType)
     *
     * @since 3.20
     */
    public DefaultCompoundCRS shiftAxisRange(final AxisRangeType range) {
        DefaultCompoundCRS[] shifted;
        synchronized (this) {
            shifted = this.shifted;
            if (shifted == null) {
                this.shifted = shifted = new DefaultCompoundCRS[CRSUtilities.AXIS_RANGE_COUNT];
            }
        }
        final int ordinal = range.ordinal();
        DefaultCompoundCRS crs;
        synchronized (shifted) {
            crs = shifted[ordinal];
            if (crs == null) {
                boolean modified = false;
                final List<CoordinateReferenceSystem> components = super.getComponents();
                final CoordinateReferenceSystem[] cmp = components.toArray(new CoordinateReferenceSystem[components.size()]);
                for (int i=0; i<cmp.length; i++) {
                    final CoordinateReferenceSystem oldCRS = cmp[i];
                    if (oldCRS instanceof GeographicCRS) {
                        cmp[i] = DefaultGeographicCRS.castOrCopy((GeographicCRS) oldCRS).shiftAxisRange(range);
                        modified |= (cmp[i] != oldCRS);
                    }
                }
                if (modified) {
                    crs = new DefaultCompoundCRS(IdentifiedObjects.getProperties(this, null), cmp);
                    crs.shifted = shifted;
                    shifted[ordinal ^ CRSUtilities.AXIS_RANGE_RECIPROCAL_MASK] = this;
                } else {
                    crs = this;
                }
                shifted[ordinal] = crs;
            }
        }
        return crs;
    }
}

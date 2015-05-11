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
 */
package org.geotoolkit.internal.referencing;

import java.util.List;
import java.util.ArrayList;
import javax.measure.unit.Unit;
import javax.measure.unit.NonSI;
import javax.measure.quantity.Angle;

import org.opengis.referencing.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.geometry.Envelope;

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.ReferencingUtilities;
import org.apache.sis.internal.referencing.AxisDirections;

import org.geotoolkit.lang.Static;
import org.geotoolkit.lang.Workaround;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.measure.Measure;
import org.geotoolkit.resources.Errors;
import org.apache.sis.measure.Units;

import static java.util.Collections.singletonMap;


/**
 * A set of static methods working on OpenGIS&reg;
 * {@linkplain CoordinateReferenceSystem coordinate reference system} objects.
 * Some of those methods are useful, but not really rigorous. This is why they
 * do not appear in the "official" package, but instead in this private one.
 * <p>
 * <strong>Do not rely on this API!</strong> It may change in incompatible way
 * in any future release.
 *
 * @version 3.21
 *
 * @since 2.0
 * @module
 */
public final class CRSUtilities extends Static {
    /**
     * Version of the embedded database. This string must be updated when
     * the SQL scripts in the {@code geotk-epsg} module are updated.
     *
     * @see http://www.geotoolkit.org/build/tools/geotk-epsg-pack/index.html
     */
    public static final String EPSG_VERSION = "7.09";

    /**
     * Number of {@link org.geotoolkit.referencing.cs.AxisRangeType} values.
     * This is defined in order to avoid creating a useless array of enumeration
     * just for determining its length.
     *
     * @since 3.20
     */
    public static final int AXIS_RANGE_COUNT = 2;

    /**
     * Mask to apply on the {@link org.geotoolkit.referencing.cs.AxisRangeType} ordinate
     * value in order to have the "opposite" enum. The opposite of {@code POSITIVE_LONGITUDE}
     * is {@code SPANNING_ZERO_LONGITUDE}, and conversely.
     *
     * @since 3.20
     */
    public static final int AXIS_RANGE_RECIPROCAL_MASK = 1;

    /**
     * Do not allow creation of instances of this class.
     */
    private CRSUtilities() {
    }

    /**
     * Returns the dimension of the first coordinate reference system of the given type. The
     * {@code type} argument must be a sub-interface of {@link CoordinateReferenceSystem}.
     * If no such dimension is found, then this method returns {@code -1}.
     *
     * @param  crs  The coordinate reference system (CRS) to examine.
     * @param  type The CRS type to look for.
     *         Must be a subclass of {@link CoordinateReferenceSystem}.
     * @return The dimension range of the specified CRS type, or {@code -1} if none.
     * @throws IllegalArgumentException if the {@code type} is not legal.
     */
    public static int getDimensionOf(final CoordinateReferenceSystem crs,
            final Class<? extends CoordinateReferenceSystem> type)
            throws IllegalArgumentException
    {
        if (type.isAssignableFrom(crs.getClass())) {
            return 0;
        }
        if (crs instanceof CompoundCRS) {
            int offset = 0;
            for (final CoordinateReferenceSystem ci : ((CompoundCRS) crs).getComponents()) {
                final int index = getDimensionOf(ci, type);
                if (index >= 0) {
                    return index + offset;
                }
                offset += ci.getCoordinateSystem().getDimension();
            }
        }
        return -1;
    }

    /**
     * Returns a two-dimensional coordinate reference system representing the two first dimensions
     * of the specified coordinate reference system. If {@code crs} is already a two-dimensional
     * CRS, then it is returned unchanged. Otherwise, if it is a {@link CompoundCRS}, then the
     * head coordinate reference system is examined.
     *
     * @param  crs The coordinate system, or {@code null}.
     * @return A two-dimensional coordinate reference system that represents the two first
     *         dimensions of {@code crs}, or {@code null} if {@code crs} was {@code null}.
     * @throws TransformException if {@code crs} can't be reduced to a two-coordinate system.
     *         We use this exception class since this method is usually invoked in the context
     *         of a transformation process.
     */
    public static CoordinateReferenceSystem getCRS2D(CoordinateReferenceSystem crs)
            throws TransformException
    {
        if (crs != null) {
            final CoordinateReferenceSystem original = crs;
            while (crs.getCoordinateSystem().getDimension() != 2) {
                if (crs instanceof CompoundCRS) {
                    crs = ((CompoundCRS) crs).getComponents().get(0);
                    // Continue the loop, examining only the first component.
                } else {
                    crs = CRS.getHorizontalComponent(crs);
                    if (crs == null) {
                        throw new TransformException(Errors.format(
                                Errors.Keys.CANT_REDUCE_TO_TWO_DIMENSIONS_1, original.getName()));
                    }
                }
            }
        }
        return crs;
    }

    /**
     * Returns an envelope containing all dimensions of the given CRS, padding additional dimensions
     * with NaN values if necessary. This method is a workaround for the current Geotk behavior,
     * which is to thrown an exception when re-projecting an envelope to another CRS having more
     * dimensions. This method will hopefully be deleted in a future version.
     *
     * @param  envelope The envelope to expand.
     * @param  crs The target CRS.
     * @return An envelope having the dimensions of the given CRS.
     */
    @Workaround(library="Geotk", version="3.21")
    public static Envelope appendMissingDimensions(final Envelope envelope, final CompoundCRS crs) {
        final List<CoordinateReferenceSystem> toAdd = new ArrayList<>(4);
        final CoordinateReferenceSystem currentCRS = envelope.getCoordinateReferenceSystem();
        final CoordinateSystem currentCS = currentCRS.getCoordinateSystem();
        for (final SingleCRS subCRS : CRS.getSingleComponents(crs)) {
            final CoordinateSystem subCS = subCRS.getCoordinateSystem();
            if (subCS.getDimension() == 1 && AxisDirections.indexOfColinear(currentCS, subCS) < 0) {
                toAdd.add(subCRS);
            }
        }
        if (toAdd.isEmpty()) {
            return envelope;
        }
        toAdd.add(0, currentCRS);
        final GeneralEnvelope expanded = new GeneralEnvelope(new DefaultCompoundCRS(
                singletonMap(DefaultCompoundCRS.NAME_KEY, "Temporarily expanded"),
                toAdd.toArray(new CoordinateReferenceSystem[toAdd.size()])));
        expanded.setToNaN();
        expanded.subEnvelope(0, envelope.getDimension()).setEnvelope(envelope);
        return expanded;
    }

    /**
     * Returns the angular unit of the specified coordinate system.
     * The preference will be given to the longitude axis, if found.
     */
    public static Unit<Angle> getAngularUnit(final CoordinateSystem coordinateSystem) {
        Unit<Angle> unit = NonSI.DEGREE_ANGLE;
        for (int i=coordinateSystem.getDimension(); --i>=0;) {
            final CoordinateSystemAxis axis = coordinateSystem.getAxis(i);
            final Unit<?> candidate = axis.getUnit();
            if (Units.isAngular(candidate)) {
                unit = candidate.asType(Angle.class);
                if (AxisDirection.EAST.equals(AxisDirections.absolute(axis.getDirection()))) {
                    break; // Found the longitude axis.
                }
            }
        }
        return unit;
    }

    /**
     * Implementation of {@link CRS#getDatum(CoordinateReferenceSystem)}, defined here in order
     * to avoid a dependency of {@link org.geotoolkit.referencing.crs.AbstractDerivedCRS} to the
     * {@link CRS} class.
     *
     * @param  crs The coordinate reference system for which to get the datum. May be {@code null}.
     * @return The datum in the given CRS, or {@code null} if none.
     *
     * @see CRS#getEllipsoid(CoordinateReferenceSystem)
     */
    public static Datum getDatum(final CoordinateReferenceSystem crs) {
        Datum datum = null;
        if (crs instanceof SingleCRS) {
            datum = ((SingleCRS) crs).getDatum();
        } else if (crs instanceof CompoundCRS) {
            for (final CoordinateReferenceSystem component : ((CompoundCRS) crs).getComponents()) {
                final Datum candidate = getDatum(component);
                if (datum != null && !datum.equals(candidate)) {
                    if (isGeodetic3D(datum, candidate)) {
                        continue; // Keep the current datum unchanged.
                    }
                    if (isGeodetic3D(candidate, datum)) {
                        continue;
                    }
                    return null; // Can't build a 3D geodetic datum.
                }
                datum = candidate;
            }
        }
        return datum;
    }

    /**
     * Returns {@code true} if the given datum can form a three-dimensional geodetic datum.
     *
     * @param  geodetic The presumed geodetic datum.
     * @param  vertical The presumed vertical datum.
     * @return If the given datum can form a 3D geodetic datum.
     *
     * @deprecated This is not right: we can not said that we have a match if we do not known
     *             on which geodetic datum the ellipsoidal height is.
     */
    private static boolean isGeodetic3D(final Datum geodetic, final Datum vertical) {
        return (geodetic instanceof GeodeticDatum) && (vertical instanceof VerticalDatum) &&
                VerticalDatumTypes.ELLIPSOIDAL.equals(((VerticalDatum) vertical).getVerticalDatumType());
    }

    /**
     * Computes the resolution of the horizontal component, or {@code null} if it can not be
     * computed.
     *
     * @param  crs The full (potentially multi-dimensional) CRS.
     * @param  resolution The resolution along each CRS dimension, or {@code null} if none.
     *         The array length shall be equals to the CRS dimension.
     * @return The horizontal resolution, or {@code null}.
     *
     * @since 3.18
     */
    public static Measure getHorizontalResolution(final CoordinateReferenceSystem crs, double... resolution) {
        if (resolution != null) {
            final SingleCRS horizontalCRS = CRS.getHorizontalComponent(crs);
            if (horizontalCRS != null) {
                final CoordinateSystem hcs = horizontalCRS.getCoordinateSystem();
                final Unit<?> unit = ReferencingUtilities.getUnit(hcs);
                if (unit != null) {
                    final int s = AxisDirections.indexOfColinear(crs.getCoordinateSystem(), hcs);
                    if (s >= 0) {
                        final int dim = hcs.getDimension();
                        double min = Double.POSITIVE_INFINITY;
                        for (int i=s; i<dim; i++) {
                            final double r = resolution[i];
                            if (r > 0 && r < min) min = r;
                        }
                        if (min != Double.POSITIVE_INFINITY) {
                            return new Measure(min, unit);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Delegates to the {@code shiftAxisRange} method of the appropriate class. This method is not
     * in public API because the call to {@code castOrCopy} may involve useless objects creation.
     * Developers are encouraged to invoke {@code castOrCopy} themselves, then replace their old
     * reference by the new one before to invoke {@code shiftAxisRange}, because they Geotk
     * implementations cache the result of {@code shiftAxisRange} calls.
     *
     * @param  crs  The CRS on which to apply the shift, or {@code null}.
     * @param  type The desired range of ordinate values.
     * @return The shifted CRS, or {@code crs} if no range shift applied.
     *
     * @since 3.20
     */
    public static CoordinateReferenceSystem shiftAxisRange(final CoordinateReferenceSystem crs, final AxesConvention type) {
        if (crs instanceof GeographicCRS) {
            final DefaultGeographicCRS impl = DefaultGeographicCRS.castOrCopy((GeographicCRS) crs);
            final DefaultGeographicCRS shifted = impl.forConvention(type);
            if (shifted != impl) return shifted;
        } else if (crs instanceof CompoundCRS) {
            final DefaultCompoundCRS impl = DefaultCompoundCRS.castOrCopy((CompoundCRS) crs);
            final DefaultCompoundCRS shifted = impl.forConvention(type);
            if (shifted != impl) return shifted;
        }
        return crs;
    }

    /**
     * Retrieve index of the first axis of the geographic component in the input {@link CoordinateReferenceSystem}.
     *
     * @param crs {@link CoordinateReferenceSystem} which is analyzed.
     * @return Index of the first horizontal axis in this CRS
     * @throws IllegalArgumentException if input CRS has no horizontal component.
     *
     * @todo Inaccurate implementation: a Cartesian or spherical CS does not mean that the axes is horizontal.
     */
    public static int firstHorizontalAxis(final CoordinateReferenceSystem crs) {
        int tempOrdinate = 0;
        for (CoordinateReferenceSystem component : CRS.getSingleComponents(crs)) {
            final CoordinateSystem cs = component.getCoordinateSystem();
            if ((cs instanceof CartesianCS)
                    || (cs instanceof SphericalCS)
                    || (cs instanceof EllipsoidalCS)) // Inaccurate check: see TODO in javadoc.
            {
                return tempOrdinate;
            }
            tempOrdinate += cs.getDimension();
        }
        throw new IllegalArgumentException("crs doesn't have any horizontal crs");
    }

    /**
     * Returns the group of referencing objects to which the given type belong.
     * The {@code type} argument can be a GeoAPI interface or an implementation class.
     * The value returned by this method will be one of {@link CoordinateReferenceSystem},
     * {@link CoordinateSystem}, {@link CoordinateSystemAxis}, {@link Datum}, {@link Ellipsoid},
     * {@link PrimeMeridian} or {@link IdentifiedObject} classes. If the given type is assignable
     * to more than one group, then this method returns {@code IdentifiedObject} class.
     *
     * @param  type The type for which to get the referencing group, or {@code null}.
     * @return The group to which the given type belong, or {@code null} if the given argument
     *         was {@code null}.
     */
    public static Class<? extends IdentifiedObject> getReferencingGroup(final Class<? extends IdentifiedObject> type) {
        Class<? extends IdentifiedObject> found = null;
        if (type != null) {
search:     for (int i=0; ; i++) {
                final Class<? extends IdentifiedObject> candidate;
                switch (i) {
                    case 0:  candidate = CoordinateReferenceSystem.class; break;
                    case 1:  candidate = CoordinateSystem.class;          break;
                    case 2:  candidate = CoordinateSystemAxis.class;      break;
                    case 3:  candidate = Datum.class;                     break;
                    case 4:  candidate = Ellipsoid.class;                 break;
                    case 5:  candidate = PrimeMeridian.class;             break;
                    default: break search;
                }
                if (candidate.isAssignableFrom(type)) {
                    if (found != null) {
                        return IdentifiedObject.class;
                    }
                    found = candidate;
                }
            }
            if (found == null) {
                return IdentifiedObject.class;
            }
        }
        return found;
    }
}

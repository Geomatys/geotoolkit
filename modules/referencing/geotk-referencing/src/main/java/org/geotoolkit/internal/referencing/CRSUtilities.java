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

import java.util.Map;
import java.util.List;
import java.util.Collections;
import javax.measure.unit.Unit;
import javax.measure.unit.NonSI;
import javax.measure.quantity.Angle;

import org.opengis.referencing.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;

import org.geotoolkit.lang.Static;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.measure.Measure;
import org.geotoolkit.resources.Errors;


/**
 * A set of static methods working on OpenGIS&reg;
 * {@linkplain CoordinateReferenceSystem coordinate reference system} objects.
 * Some of those methods are useful, but not really rigorous. This is why they
 * do not appear in the "official" package, but instead in this private one.
 * <p>
 * <strong>Do not rely on this API!</strong> It may change in incompatible way
 * in any future release.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
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
     * Do not allow creation of instances of this class.
     */
    private CRSUtilities() {
    }

    /**
     * Returns the dimension within the coordinate system of the first occurrence of an axis
     * colinear with the specified axis. If an axis with the same
     * {@linkplain CoordinateSystemAxis#getDirection direction} or an
     * {@linkplain AxisDirections#opposite opposite} direction than {@code axis}
     * occurs in the coordinate system, then the dimension of the first such occurrence
     * is returned. That is, the value <var>k</var> such that:
     *
     * {@preformat java
     *     absolute(cs.getAxis(k).getDirection()) == absolute(axis.getDirection())
     * }
     *
     * is {@code true}. If no such axis occurs in this coordinate system,
     * then {@code -1} is returned.
     * <p>
     * For example, {@code dimensionColinearWith(DefaultCoordinateSystemAxis.TIME)}
     * returns the dimension number of time axis.
     *
     * @param  cs   The coordinate system to examine.
     * @param  axis The axis to look for.
     * @return The dimension number of the specified axis, or {@code -1} if none.
     */
    public static int dimensionColinearWith(final CoordinateSystem     cs,
                                            final CoordinateSystemAxis axis)
    {
        int candidate = -1;
        final int dimension = cs.getDimension();
        final AxisDirection direction = AxisDirections.absolute(axis.getDirection());
        for (int i=0; i<dimension; i++) {
            final CoordinateSystemAxis xi = cs.getAxis(i);
            if (direction.equals(AxisDirections.absolute(xi.getDirection()))) {
                candidate = i;
                if (axis.equals(xi)) {
                    break;
                }
            }
        }
        return candidate;
    }

    /**
     * Returns the index of the first dimension in {@code fullCS} where axes colinear with
     * the {@code subCS} axes are found. If no such dimension is found, returns -1.
     *
     * @param  fullCS The coordinate system which contains all axes.
     * @param  subCS  The coordinate system to search into {@code fullCS}.
     * @return The first dimension of a sequence of axes colinear with {@code subCS} axes,
     *         or {@code -1} if none.
     *
     * @since 3.10
     */
    public static int dimensionColinearWith(final CoordinateSystem fullCS, final CoordinateSystem subCS) {
        final int dim = dimensionColinearWith(fullCS, subCS.getAxis(0));
        if (dim >= 0) {
            int i = subCS.getDimension();
            if (dim + i <= fullCS.getDimension()) {
                while (--i > 0) { // Intentionally exclude 0.
                    if (!AxisDirections.absolute(subCS.getAxis(i).getDirection()).equals(
                         AxisDirections.absolute(fullCS.getAxis(i + dim).getDirection())))
                    {
                        return -1;
                    }
                }
                return dim;
            }
        }
        return -1;
    }

    /**
     * Returns the unit used for all axis in the specified coordinate system.
     * If not all axis uses the same unit, then this method returns {@code null}.
     * This convenience method is often used for Well Know Text (WKT) formatting.
     *
     * @param cs The coordinate system for which to get the unit.
     * @return The unit for all axis in the given coordinate system, or {@code null}.
     *
     * @since 2.2
     */
    public static Unit<?> getUnit(final CoordinateSystem cs) {
        Unit<?> unit = null;
        for (int i=cs.getDimension(); --i>=0;) {
            final Unit<?> candidate = cs.getAxis(i).getUnit();
            if (candidate != null) {
                if (unit == null) {
                    unit = candidate;
                } else if (!unit.equals(candidate)) {
                    return null;
                }
            }
        }
        return unit;
    }

    /**
     * Returns the components of the specified CRS, or {@code null} if none.
     * This method preserves the nested CRS hierarchy if there is one.
     *
     * @param  crs The coordinate reference system for which to get the components.
     * @return The components, or {@code null} if the given CRS is not a {@link CompoundCRS}.
     */
    public static List<? extends CoordinateReferenceSystem> getComponents(CoordinateReferenceSystem crs) {
        if (crs instanceof CompoundCRS) {
            final List<? extends CoordinateReferenceSystem> components;
            components = ((CompoundCRS) crs).getComponents();
            if (!components.isEmpty()) {
                return components;
            }
        }
        return null;
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
        final List<? extends CoordinateReferenceSystem> c = getComponents(crs);
        if (c != null) {
            int offset = 0;
            for (final CoordinateReferenceSystem ci : c) {
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
            while (crs.getCoordinateSystem().getDimension() != 2) {
                final List<? extends CoordinateReferenceSystem> c = getComponents(crs);
                if (c != null) {
                    crs = c.get(0);
                    // Continue the loop, examining only the first component.
                } else {
                    crs = CRS.getHorizontalCRS(crs);
                    if (crs == null) {
                        throw new TransformException(Errors.format(
                                Errors.Keys.CANT_REDUCE_TO_TWO_DIMENSIONS_$1, crs.getName()));
                    }
                }
            }
        }
        return crs;
    }

    /**
     * Changes the dimension declared in the name. For example if {@code name} is
     * "WGS 84 (geographic 3D)", {@code search} is "3D" and {@code replace} is "2D",
     * then this method returns "WGS 84 (geographic 2D)". If the string to search is
     * not found, then it is concatenated to the name.
     *
     * @param object The identified object having the original name.
     * @param search The dimension token to search in the {@code object} name.
     * @param replace The new token to substitute to the one we were looking for.
     * @return The name with the substitution performed.
     *
     * @since 2.6
     */
    public static Map<String,?> changeDimensionInName(final IdentifiedObject object,
            final String search, final String replace)
    {
        final StringBuilder name = new StringBuilder(object.getName().getCode());
        final int last = name.length() - search.length();
        boolean append = true;
        for (int i=name.lastIndexOf(search); i>=0; i=name.lastIndexOf(search, i-1)) {
            if (i != 0 && Character.isLetterOrDigit(name.charAt(i-1))) {
                continue;
            }
            if (i != last && Character.isLetterOrDigit(i + search.length())) {
                continue;
            }
            name.replace(i, i+search.length(), replace);
            i = name.indexOf(". ", i);
            if (i >= 0) {
                /*
                 * Stops the sentence after the dimension, since it may contains details that
                 * are not applicable anymore. For example the EPSG name for 3D EllipsoidalCS is:
                 *
                 *     Ellipsoidal 3D CS. Axes: latitude, longitude, ellipsoidal height.
                 *     Orientations: north, east, up.  UoM: DMSH, DMSH, m.
                 */
                name.setLength(i+1);
            }
            append = false;
            break;
        }
        if (append) {
            if (name.indexOf(" ") >= 0) {
                name.append(" (").append(replace).append(')');
            } else {
                name.append('_').append(replace);
            }
        }
        return Collections.singletonMap(IdentifiedObject.NAME_KEY, name.toString());
    }

    /**
     * Returns the longitude value relative to the Greenwich Meridian,
     * expressed in the specified units.
     *
     * @param  pm   The prime meridian from which to get the Greenwich longitude.
     * @param  unit The unit for the prime meridian to return.
     * @return The prime meridian in the given units, or 0 if the given prime meridian was null.
     *
     * @since 3.19
     */
    public static double getGreenwichLongitude(final PrimeMeridian pm, final Unit<Angle> unit) {
        if (pm == null) {
            return 0;
        }
        return pm.getAngularUnit().getConverterTo(unit).convert(pm.getGreenwichLongitude());
    }

    /**
     * Returns the longitude value relative to the Greenwich Meridian, expressed in decimal degrees.
     *
     * @param  pm The prime meridian from which to get the Greenwich longitude.
     * @return The prime meridian in the given units, or 0 if the given prime meridian was null.
     *
     * @since 3.19
     */
    public static double getGreenwichLongitude(final PrimeMeridian pm) {
        return getGreenwichLongitude(pm, NonSI.DEGREE_ANGLE);
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
        Datum datum;
        if (crs instanceof SingleCRS) {
            datum = ((SingleCRS) crs).getDatum();
        } else {
            datum = null;
            for (final SingleCRS component : DefaultCompoundCRS.getSingleCRS(crs)) {
                final Datum candidate = component.getDatum();
                if (datum != null && !datum.equals(candidate)) {
                    if (isGeodetic3D(datum, candidate)) {
                        continue; // Keep the current datum unchanged.
                    }
                    if (!isGeodetic3D(candidate, datum)) {
                        return null; // Can't build a 3D geodetic datum.
                    }
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
     */
    private static boolean isGeodetic3D(final Datum geodetic, final Datum vertical) {
        return (geodetic instanceof GeodeticDatum) && (vertical instanceof VerticalDatum) &&
                VerticalDatumTypes.ELLIPSOIDAL.equals(((VerticalDatum) vertical).getVerticalDatumType());
    }

    /**
     * Returns the ellipsoid used by the specified coordinate reference system, providing that
     * the two first dimensions use an instance of {@link GeographicCRS}. Otherwise (i.e. if the
     * two first dimensions are not geographic), returns {@code null}.
     *
     * @param  crs The coordinate reference system for which to get the ellipsoid.
     * @return The ellipsoid in the given CRS, or {@code null} if none.
     */
    public static Ellipsoid getHeadGeoEllipsoid(CoordinateReferenceSystem crs) {
        while (!(crs instanceof GeographicCRS)) {
            final List<? extends CoordinateReferenceSystem> c = getComponents(crs);
            if (c == null) {
                return null;
            }
            crs = c.get(0);
        }
        return ((GeographicCRS) crs).getDatum().getEllipsoid();
    }

    /**
     * Derives a geographic CRS with (<var>longitude</var>, <var>latitude</var>) axis order in
     * decimal degrees, relative to Greenwich. If no such CRS can be obtained of created, returns
     * {@link DefaultGeographicCRS#WGS84}.
     *
     * @param  crs A source CRS.
     * @return A two-dimensional geographic CRS with standard axis. Never {@code null}.
     */
    public static GeographicCRS getStandardGeographicCRS2D(CoordinateReferenceSystem crs) {
        while (crs instanceof GeneralDerivedCRS) {
            crs = ((GeneralDerivedCRS) crs).getBaseCRS();
        }
        if (!(crs instanceof SingleCRS)) {
            return DefaultGeographicCRS.WGS84;
        }
        final Datum datum = ((SingleCRS) crs).getDatum();
        if (!(datum instanceof GeodeticDatum)) {
            return DefaultGeographicCRS.WGS84;
        }
        GeodeticDatum geoDatum = (GeodeticDatum) datum;
        if (geoDatum.getPrimeMeridian().getGreenwichLongitude() != 0) {
            geoDatum = new DefaultGeodeticDatum(geoDatum.getName().getCode(), geoDatum.getEllipsoid());
        } else if (crs instanceof GeographicCRS) {
            if (CRS.equalsIgnoreMetadata(DefaultEllipsoidalCS.GEODETIC_2D, crs.getCoordinateSystem())) {
                return (GeographicCRS) crs;
            }
        }
        return new DefaultGeographicCRS(crs.getName().getCode(), geoDatum, DefaultEllipsoidalCS.GEODETIC_2D);
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
            final SingleCRS horizontalCRS = CRS.getHorizontalCRS(crs);
            if (horizontalCRS != null) {
                final CoordinateSystem hcs = horizontalCRS.getCoordinateSystem();
                final Unit<?> unit = getUnit(hcs);
                if (unit != null) {
                    final int s = dimensionColinearWith(crs.getCoordinateSystem(), hcs);
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
}

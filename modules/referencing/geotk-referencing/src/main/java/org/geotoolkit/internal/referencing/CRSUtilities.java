/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.util.Iterator;
import java.util.Collections;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import javax.measure.unit.Unit;

import org.opengis.referencing.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.geometry.DirectPosition;

import org.geotoolkit.lang.Static;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.datum.DefaultPrimeMeridian;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
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
 * @version 3.10
 *
 * @since 2.0
 * @module
 */
@Static
public final class CRSUtilities {
    /**
     * Do not allow creation of instances of this class.
     */
    private CRSUtilities() {
    }

    /**
     * Derives a {@link CRSFactory} for the given factory. If the given factory is already
     * a {@code CRSFactory} instance, then it is returned. Otherwise the implementation hints
     * are inspected. If no {@code CRSFactory} is found, then the default instance is fetched
     * from the {@link FactoryFinder}.
     *
     * @param  factory The factory from which to infer a {@link CRSFactory}.
     * @return The CRS factory.
     *
     * @since 3.10
     */
    public static CRSFactory getCRSFactory(final Factory factory) {
        if (factory instanceof CRSFactory) {
            return (CRSFactory) factory;
        }
        Hints hints = null;
        if (factory instanceof org.geotoolkit.factory.Factory) {
            final Map<RenderingHints.Key,?> impl =
                    ((org.geotoolkit.factory.Factory) factory).getImplementationHints();
            final Object candidate = impl.get(Hints.CRS_FACTORY);
            if (candidate instanceof CRSFactory) {
                return (CRSFactory) candidate;
            }
            hints = new Hints(impl);
        }
        return FactoryFinder.getCRSFactory(hints);
    }

    /**
     * Returns the dimension within the coordinate system of the first occurrence of an axis
     * colinear with the specified axis. If an axis with the same
     * {@linkplain CoordinateSystemAxis#getDirection direction} or an
     * {@linkplain AxisDirection#opposite opposite} direction than {@code axis}
     * ocurs in the coordinate system, then the dimension of the first such occurrence
     * is returned. That is, the a value <var>k</var> such that:
     *
     * {@preformat java
     *     cs.getAxis(k).getDirection().absolute() == axis.getDirection().absolute()
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
        final AxisDirection direction = axis.getDirection().absolute();
        for (int i=0; i<dimension; i++) {
            final CoordinateSystemAxis xi = cs.getAxis(i);
            if (direction.equals(xi.getDirection().absolute())) {
                candidate = i;
                if (axis.equals(xi)) {
                    break;
                }
            }
        }
        return candidate;
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
     * @param  crs The coordinate reference system for which to get the componennts.
     * @return The components, or {@code null} if the given CRS is not a {@link CompoundCRS}.
     */
    private static List<? extends CoordinateReferenceSystem> getComponents(CoordinateReferenceSystem crs) {
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
     * {@code type} argument must be a subinterface of {@link CoordinateReferenceSystem}.
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
     * Returns a sub-coordinate reference system for the specified dimension range.
     *
     * @param  crs   The coordinate reference system to decompose.
     * @param  lower The first dimension to keep, inclusive.
     * @param  upper The last  dimension to keep, exclusive.
     * @return The sub-coordinate system, or {@code null} if {@code crs} can't
     *         be decomposed for dimensions in the range {@code [lower..upper]}.
     */
    public static CoordinateReferenceSystem getSubCRS(CoordinateReferenceSystem crs,
                                                      int lower, int upper)
    {
        int dimension = crs.getCoordinateSystem().getDimension();
        if (lower<0 || lower>upper || upper>dimension) {
            throw new IndexOutOfBoundsException(Errors.format(
                    Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, lower<0 ? lower : upper));
        }
        while (lower!=0 || upper!=dimension) {
            final List<? extends CoordinateReferenceSystem> c = getComponents(crs);
            if (c == null) {
                return null;
            }
            for (final Iterator<? extends CoordinateReferenceSystem> it=c.iterator(); it.hasNext();) {
                crs = it.next();
                dimension = crs.getCoordinateSystem().getDimension();
                if (lower < dimension) {
                    break;
                }
                lower -= dimension;
                upper -= dimension;
            }
        }
        return crs;
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
                if (c == null) {
                    throw new TransformException(Errors.format(
                            Errors.Keys.CANT_REDUCE_TO_TWO_DIMENSIONS_$1, crs.getName()));
                }
                crs = c.get(0);
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
     * Returns the datum of the specified CRS, or {@code null} if none.
     *
     * @param  crs The coordinate reference system for which to get the datum. May be {@code null}.
     * @return The datum in the given CRS, or {@code null} if none.
     *
     * @see CRS#getEllipsoid(CoordinateReferenceSystem)
     */
    public static Datum getDatum(final CoordinateReferenceSystem crs) {
        return (crs instanceof SingleCRS) ? ((SingleCRS) crs).getDatum() : null;
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
            geoDatum = new DefaultGeodeticDatum(geoDatum.getName().getCode(),
                    geoDatum.getEllipsoid(), DefaultPrimeMeridian.GREENWICH);
        } else if (crs instanceof GeographicCRS) {
            if (CRS.equalsIgnoreMetadata(DefaultEllipsoidalCS.GEODETIC_2D, crs.getCoordinateSystem())) {
                return (GeographicCRS) crs;
            }
        }
        return new DefaultGeographicCRS(crs.getName().getCode(), geoDatum, DefaultEllipsoidalCS.GEODETIC_2D);
    }

    /**
     * Transforms the relative distance vector specified by {@code source} and stores
     * the result in {@code dest}.  A relative distance vector is transformed without
     * applying the translation components.
     *
     * @param transform The transform to apply.
     * @param origin The position where to compute the delta transform in the source CS.
     * @param source The distance vector to be delta transformed
     * @return       The result of the transformation.
     * @throws TransformException if the transformation failed.
     *
     * @since 2.3
     */
    public static DirectPosition deltaTransform(final MathTransform  transform,
                                                final DirectPosition origin,
                                                final DirectPosition source)
            throws TransformException
    {
        final int sourceDim = transform.getSourceDimensions();
        final int targetDim = transform.getTargetDimensions();
        DirectPosition P1 = new GeneralDirectPosition(sourceDim);
        DirectPosition P2 = new GeneralDirectPosition(sourceDim);
        for (int i=0; i<sourceDim; i++) {
            final double c = origin.getOrdinate(i);
            final double d = source.getOrdinate(i) * 0.5;
            P1.setOrdinate(i, c-d);
            P2.setOrdinate(i, c+d);
        }
        P1 = transform.transform(P1, (sourceDim==targetDim) ? P1 : null);
        P2 = transform.transform(P2, (sourceDim==targetDim) ? P2 : null);
        for (int i=0; i<targetDim; i++) {
            P2.setOrdinate(i, P2.getOrdinate(i) - P1.getOrdinate(i));
        }
        return P2;
    }

    /**
     * Transforms the relative distance vector specified by {@code source} and stores
     * the result in {@code dest}.  A relative distance vector is transformed without
     * applying the translation components.
     *
     * @param transform The transform to apply.
     * @param origin The position where to compute the delta transform in the source CS.
     * @param source The distance vector to be delta transformed
     * @param dest   The resulting transformed distance vector, or {@code null}
     * @return       The result of the transformation.
     * @throws TransformException if the transformation failed.
     *
     * @see AffineTransform#deltaTransform(Point2D,Point2D)
     */
    public static Point2D deltaTransform(final MathTransform2D transform,
                                         final Point2D         origin,
                                         final Point2D         source,
                                               Point2D         dest)
            throws TransformException
    {
        if (transform instanceof AffineTransform) {
            return ((AffineTransform) transform).deltaTransform(source, dest);
        }
        final double ox = origin.getX();
        final double oy = origin.getY();
        final double dx = source.getX()*0.5;
        final double dy = source.getY()*0.5;
        Point2D P1 = new Point2D.Double(ox-dx, oy-dy);
        Point2D P2 = new Point2D.Double(ox+dx, oy+dy);
        P1 = transform.transform(P1, P1);
        P2 = transform.transform(P2, P2);
        if (dest == null) {
            dest = P2;
        }
        dest.setLocation(P2.getX()-P1.getX(), P2.getY()-P1.getY());
        return dest;
    }
}

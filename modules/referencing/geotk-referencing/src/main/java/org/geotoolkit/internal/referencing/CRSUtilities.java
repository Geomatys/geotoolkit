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
import org.apache.sis.internal.metadata.ReferencingUtilities;
import org.apache.sis.internal.referencing.AxisDirections;

import org.geotoolkit.lang.Static;
import org.geotoolkit.lang.Workaround;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.referencing.cs.AxisRangeType;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.measure.Measure;
import org.geotoolkit.resources.Errors;

import static java.util.Collections.singletonMap;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;


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
     * The key for specifying explicitely the value to be returned by {@link #getParameterValues()}.
     * It is usually not necessary to specify those parameters because they are inferred either from
     * the {@link MathTransform}, or specified explicitely in a {@link DefiningConversion}. However
     * there is a few cases, for example the Molodenski transform, where none of the above can apply,
     * because Geotk implements those operations as a concatenation of math transforms, and such
     * concatenations don't have {@link ParameterValueGroup}.
     *
     * @since 3.20
     */
    public static final String PARAMETERS_KEY = "parameters";

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
        final int dim = AxisDirections.indexOfColinear(fullCS, subCS.getAxis(0).getDirection());
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
            while (crs.getCoordinateSystem().getDimension() != 2) {
                if (crs instanceof CompoundCRS) {
                    crs = ((CompoundCRS) crs).getComponents().get(0);
                    // Continue the loop, examining only the first component.
                } else {
                    crs = CRS.getHorizontalComponent(crs);
                    if (crs == null) {
                        throw new TransformException(Errors.format(
                                Errors.Keys.CANT_REDUCE_TO_TWO_DIMENSIONS_1, crs.getName()));
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
        for (final SingleCRS subCRS : DefaultCompoundCRS.getSingleCRS(crs)) {
            final CoordinateSystem subCS = subCRS.getCoordinateSystem();
            if (subCS.getDimension() == 1 && dimensionColinearWith(currentCS, subCS) < 0) {
                toAdd.add(subCRS);
            }
        }
        if (toAdd.isEmpty()) {
            return envelope;
        }
        toAdd.add(0, currentCRS);
        final GeneralEnvelope expanded = new GeneralEnvelope(new DefaultCompoundCRS("Temporarily expanded",
                toAdd.toArray(new CoordinateReferenceSystem[toAdd.size()])));
        expanded.setToNaN();
        expanded.subEnvelope(0, envelope.getDimension()).setEnvelope(envelope);
        return expanded;
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
     * Returns the ellipsoid used by the specified coordinate reference system, providing that
     * the two first dimensions use an instance of {@link GeographicCRS}. Otherwise (i.e. if the
     * two first dimensions are not geographic), returns {@code null}.
     *
     * @param  crs The coordinate reference system for which to get the ellipsoid.
     * @return The ellipsoid in the given CRS, or {@code null} if none.
     */
    public static Ellipsoid getHeadGeoEllipsoid(CoordinateReferenceSystem crs) {
        while (!(crs instanceof GeographicCRS)) {
            if (crs instanceof CompoundCRS) {
                crs = ((CompoundCRS) crs).getComponents().get(0);
            } else {
                return null;
            }
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
            geoDatum = new DefaultGeodeticDatum(singletonMap(NAME_KEY, geoDatum.getName().getCode()),
                    geoDatum.getEllipsoid(), CommonCRS.WGS84.primeMeridian());
        } else if (crs instanceof GeographicCRS) {
            if (org.geotoolkit.referencing.CRS.equalsIgnoreMetadata(DefaultEllipsoidalCS.GEODETIC_2D, crs.getCoordinateSystem())) {
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
            final SingleCRS horizontalCRS = CRS.getHorizontalComponent(crs);
            if (horizontalCRS != null) {
                final CoordinateSystem hcs = horizontalCRS.getCoordinateSystem();
                final Unit<?> unit = ReferencingUtilities.getUnit(hcs);
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
    public static CoordinateReferenceSystem shiftAxisRange(final CoordinateReferenceSystem crs, final AxisRangeType type) {
        if (crs instanceof GeographicCRS) {
            final DefaultGeographicCRS impl = DefaultGeographicCRS.castOrCopy((GeographicCRS) crs);
            final DefaultGeographicCRS shifted = impl.shiftAxisRange(type);
            if (shifted != impl) return shifted;
        } else if (crs instanceof CompoundCRS) {
            final DefaultCompoundCRS impl = DefaultCompoundCRS.castOrCopy((CompoundCRS) crs);
            final DefaultCompoundCRS shifted = impl.shiftAxisRange(type);
            if (shifted != impl) return shifted;
        }
        return crs;
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

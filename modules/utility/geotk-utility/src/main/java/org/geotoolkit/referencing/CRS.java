/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing;

import java.util.List;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import org.opengis.geometry.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

import org.geotoolkit.lang.Static;
import org.apache.sis.util.Version;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.internal.io.JNDI;
import org.apache.sis.geometry.Envelopes;
import org.geotoolkit.resources.Errors;
import org.apache.sis.internal.metadata.NameMeaning;
import org.apache.sis.internal.metadata.VerticalDatumTypes;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Utility class for making use of the {@linkplain CoordinateReferenceSystem Coordinate Reference
 * System} and associated {@linkplain org.opengis.util.Factory} implementations. This utility class
 * is made up of static functions working with arbitrary implementations of GeoAPI interfaces.
 * <p>
 * The methods defined in this class can be grouped in three categories:
 * <p>
 * <ul>
 *   <li>Methods working with factories, like {@link #decode(String)}.</li>
 *   <li>Methods providing informations, like {@link #isHorizontalCRS(CoordinateReferenceSystem)}.</li>
 *   <li>Methods performing coordinate transformations, like {@link #transform(CoordinateOperation,Envelope)}
 *       Note that many of those methods are also defined in the {@link Envelopes} class.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @author Andrea Aime (TOPP)
 * @version 3.19
 *
 * @see IdentifiedObjects
 * @see Envelopes
 *
 * @since 2.1
 * @module
 */
public final class CRS extends Static {
    static {
        JNDI.install();
    }

    /**
     * Do not allow instantiation of this class.
     */
    private CRS() {
    }


    //////////////////////////////////////////////////////////////
    ////                                                      ////
    ////        FACTORIES, CRS CREATION AND INSPECTION        ////
    ////                                                      ////
    //////////////////////////////////////////////////////////////

    /**
     * Returns the coordinate operation factory used by
     * {@link #findMathTransform(CoordinateReferenceSystem, CoordinateReferenceSystem)
     * findMathTransform} convenience methods.
     *
     * @param lenient ignored.
     * @return The coordinate operation factory used for finding math transform in this class.
     *
     * @category factory
     * @since 2.4
     */
    @Deprecated
    public static CoordinateOperationFactory getCoordinateOperationFactory(final boolean lenient) {
        return DefaultFactories.forBuildin(CoordinateOperationFactory.class);
    }

    /**
     * Returns the version number of the specified authority database, or {@code null} if
     * not available.
     *
     * @param  authority The authority name (typically {@code "EPSG"}).
     * @return The version number of the authority database, or {@code null} if unknown.
     * @throws FactoryRegistryException if no {@link CRSAuthorityFactory} implementation
     *         was found for the specified authority.
     *
     * @category factory
     * @since 2.4
     */
    public static Version getVersion(final String authority) throws FactoryRegistryException {
        final String version;
        try {
            version = NameMeaning.getVersion(org.apache.sis.referencing.CRS.getAuthorityFactory(authority).getAuthority());
        } catch (FactoryException e) {
            throw new FactoryRegistryException(e.getLocalizedMessage(), e);
        }
        return (version != null) ? new Version(version) : null;
    }

    /**
     * Returns the first compound CRS which contains only the given components, in any order.
     * First, this method gets the {@link SingleCRS} components of the given compound CRS. If
     * all those components are {@linkplain #equalsIgnoreMetadata equal, ignoring metadata}
     * and order, to the {@code SingleCRS} components given to this method, then the given
     * {@code CompoundCRS} is returned. Otherwise if the given {@code CompoundCRS} contains
     * nested {@code CompoundCRS}, then those nested CRS are inspected recursively by the same
     * algorithm. Otherwise, this method returns {@code null}.
     * <p>
     * This method is useful for extracting metadata about the 3D spatial CRS part in a 4D
     * spatio-temporal CRS. For example given the following CRS:
     *
     * {@preformat wkt
     *   COMPD_CS["Mercator + height + time",
     *     COMPD_CS["Mercator + height",
     *       PROJCS["Mercator", ...etc...]
     *       VERT_CS["Ellipsoidal height", ...etc...]]
     *     TemporalCRS["Modified Julian", ...etc...]]
     * }
     *
     * Then the following code will returns the nested {@code COMPD_CS["Mercator + height"]}
     * without prior knowledge of the CRS component order (the time CRS could be first, and
     * the vertical CRS could be before the horizontal one):
     *
     * {@preformat java
     *     CompoundCRS crs = ...;
     *     SingleCRS horizontalCRS = getHorizontalCRS(crs);
     *     VerticalCRS verticalCRS = getVerticalCRS(crs);
     *     if (horizontalCRS != null && verticalCRS != null) {
     *         CompoundCRS spatialCRS = getCompoundCRS(crs, horizontalCRS, verticalCRS);
     *         if (spatialCRS != null) {
     *             // ...
     *         }
     *     }
     * }
     *
     * @param  crs The compound CRS to compare with the given component CRS, or {@code null}.
     * @param  components The CRS which must be components of the returned CRS.
     * @return A CRS which contains the given components, or {@code null} if none.
     *
     * @see DefaultCompoundCRS#getSingleCRS()
     *
     * @since 3.16
     */
    public static CompoundCRS getCompoundCRS(final CompoundCRS crs, final SingleCRS... components) {
        final List<SingleCRS> actualComponents = org.apache.sis.referencing.CRS.getSingleComponents(crs);
        if (actualComponents.size() == components.length) {
            int firstValid = 0;
            final SingleCRS[] toSearch = components.clone();
compare:    for (final SingleCRS component : actualComponents) {
                for (int i=firstValid; i<toSearch.length; i++) {
                    if (Utilities.equalsIgnoreMetadata(component, toSearch[i])) {
                        /*
                         * Found a match: remove it from the search list. Note that we copy the
                         * remaining components to the end of the array (which is unusual) rather
                         * than to the begining (as usual), in order to reduce the length of the
                         * part to copy on the assumption that the components given to this method
                         * are most likely in the same order than the elements in the CompoundCRS.
                         */
                        System.arraycopy(toSearch, firstValid, toSearch, firstValid+1, i - firstValid);
                        toSearch[firstValid++] = null;
                        continue compare;
                    }
                }
                // No match found. We can stop the loop now.
                firstValid = -1;
                break;
            }
            /*
             * If we found all the requested components and nothing more,
             * returns the CRS.
             */
            if (firstValid == toSearch.length) {
                return crs;
            }
        }
        /*
         * Search recursively in the sub-components.
         */
        for (final CoordinateReferenceSystem component : crs.getComponents()) {
            if (component instanceof CompoundCRS) {
                final CompoundCRS candidate = getCompoundCRS((CompoundCRS) component, components);
                if (candidate != null) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * @todo Duplicate of {@link org.geotoolkit.referencing.factory.ReferencingFactoryContainer}?
     *
     * @see <a href="https://issues.apache.org/jira/browse/SIS-162">https://issues.apache.org/jira/browse/SIS-162</a>
     */
    public static CoordinateReferenceSystem getOrCreateSubCRS(CoordinateReferenceSystem crs, int lower, int upper) {
        if (crs == null) return crs;

        int dimension = crs.getCoordinateSystem().getDimension();
        if (lower < 0 || lower > upper || upper > dimension) {
            throw new IndexOutOfBoundsException(Errors.format(
                    Errors.Keys.IndexOutOfBounds_1, lower < 0 ? lower : upper));
        }

        // Dimension exactly matches, no need to decompse the CRS
        if (lower == 0 && dimension == upper) return crs;

        // CRS can not be decomposed
        if (!(crs instanceof CompoundCRS)) return null;

        final List<CoordinateReferenceSystem> parts = new ArrayList<>(1);
        final int res = decomposeCRS(crs, lower, upper, parts);
        if (res == -1) {
            // CRS could not be divided
            return null;
        }

        final int size = parts.size();
        if (size == 1) {
            return parts.get(0);
        } else try {
            return org.apache.sis.referencing.CRS.compound(parts.toArray(new CoordinateReferenceSystem[size]));
        } catch (FactoryException e) {
            throw new IllegalArgumentException("Illegal CRS.", e);
        }
    }

    /**
     * Internal use only.
     * Fill a list of CoordinateReferenceSystem with CRS parts in the given lower/upper range.
     *
     * @param crs CoordinateReferenceSystem to decompose
     * @param lower dimension start range
     * @param upper dimension start range
     * @param parts used to stack CoordinateReferenceSystem when decomposing CRS.
     * @return number of dimensions used, -1 if the current crs could not be decomposed to match lower/upper bounds
     */
    private static int decomposeCRS(CoordinateReferenceSystem crs, int lower, int upper, final List<CoordinateReferenceSystem> parts) {
        final int dimension = crs.getCoordinateSystem().getDimension();

        if (lower == 0 && dimension <= upper) {
            // Dimension is smaller or exactly match, no need to decompse the crs
            parts.add(crs);
            return dimension;
        } else if (lower >= dimension){
            // Skip this CRS
            return dimension;
        }

        // CRS can not be decomposed
        if (!(crs instanceof CompoundCRS)) return -1;

        int nbDimRead = 0;
        final List<CoordinateReferenceSystem> components = ((CompoundCRS) crs).getComponents();
        for (CoordinateReferenceSystem component : components) {
            int res = decomposeCRS(component, lower, upper, parts);
            if (res == -1) {
                // Sub element could not be decomposed
                return -1;
            }
            nbDimRead += res;
            lower = Math.max(0, lower-res);
            upper = Math.max(0, upper-res);
            if (upper == 0) break;
        }

        return nbDimRead;
    }

    /**
     * Returns the datum of the specified CRS, or {@code null} if none.
     * This method processes as below:
     * <p>
     * <ul>
     *   <li>If the given CRS is an instance of {@link SingleCRS}, then this method returns
     *       <code>crs.{@linkplain SingleCRS#getDatum() getDatum()}</code>.</li>
     *   <li>Otherwise if the given CRS is an instance of {@link CompoundCRS}, then:
     *       <ul>
     *         <li>If all components have the same datum, then that datum is returned.</li>
     *         <li>Otherwise if the CRS contains only a geodetic datum with a vertical datum
     *             of type <em>ellipsoidal height</em> (no other type accepted), then the
     *             geodetic datum is returned.</li>
     *       </ul></li>
     *   <li>Otherwise this method returns {@code null}.</li>
     * </ul>
     *
     * @param  crs The coordinate reference system for which to get the datum. May be {@code null}.
     * @return The datum in the given CRS, or {@code null} if none.
     *
     * @see #getEllipsoid(CoordinateReferenceSystem)
     *
     * @category information
     * @since 3.16
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
                        continue;                               // Keep the current datum unchanged.
                    }
                    if (isGeodetic3D(candidate, datum)) {
                        continue;
                    }
                    return null;                                // Can't build a 3D geodetic datum.
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
     * Returns the first ellipsoid found in a coordinate reference system,
     * or {@code null} if there is none. More specifically:
     * <p>
     * <ul>
     *   <li>If the given CRS is an instance of {@link SingleCRS} and its datum is a
     *       {@link GeodeticDatum}, then this method returns the datum ellipsoid.</li>
     *   <li>Otherwise if the given CRS is an instance of {@link CompoundCRS}, then this method
     *       invokes itself recursively for each component until a geodetic datum is found.</li>
     *   <li>Otherwise this method returns {@code null}.</li>
     * </ul>
     * <p>
     * Note that this method does not check if there is more than one ellipsoid
     * (it should never be the case).
     *
     * @param  crs The coordinate reference system, or {@code null}.
     * @return The ellipsoid, or {@code null} if none.
     *
     * @see #getDatum(CoordinateReferenceSystem)
     *
     * @category information
     * @since 2.4
     */
    public static Ellipsoid getEllipsoid(final CoordinateReferenceSystem crs) {
        if (crs instanceof SingleCRS) {
            final Datum datum = ((SingleCRS) crs).getDatum();
            if (datum instanceof GeodeticDatum) {
                return ((GeodeticDatum) datum).getEllipsoid();
            }
        }
        if (crs instanceof CompoundCRS) {
            for (final CoordinateReferenceSystem c : ((CompoundCRS) crs).getComponents()) {
                final Ellipsoid candidate = getEllipsoid(c);
                if (candidate != null) {
                    return candidate;
                }
            }
        }
        return null;
    }


    /////////////////////////////////////////////////
    ////                                         ////
    ////          COORDINATE OPERATIONS          ////
    ////                                         ////
    /////////////////////////////////////////////////

    /**
     * Compares the specified objects for equality, ignoring metadata and slight differences
     * in numerical values. If this method returns {@code true}, then:
     *
     * <ul>
     *   <li><p>If the two given objects are {@link MathTransform} instances, then transforming a
     *       set of coordinate values using one transform will produce <em>approximatively</em>
     *       the same results than transforming the same coordinates with the other transform.</p></li>
     *
     *   <li><p>If the two given objects are {@link CoordinateReferenceSystem} instances,
     *       then a call to <code>{@linkplain #findMathTransform(CoordinateReferenceSystem,
     *       CoordinateReferenceSystem) findMathTransform}(crs1, crs2)</code> will return
     *       a transform close to the identity transform.</p></li>
     * </ul>
     *
     * {@section Implementation note}
     * This is a convenience method for the following method call:
     *
     * {@preformat java
     *     return Utilities.deepEquals(object1, object2, ComparisonMode.APPROXIMATIVE);
     * }
     *
     * @param  object1 The first object to compare (may be null).
     * @param  object2 The second object to compare (may be null).
     * @return {@code true} if both objects are approximatively equal.
     *
     * @see Utilities#deepEquals(Object, Object, ComparisonMode)
     * @see ComparisonMode#APPROXIMATIVE
     *
     * @category information
     * @since 3.18
     *
     * @deprecated Moved to Apache SIS as {@link Utilities#equalsApproximatively(Object, Object)}.
     */
    @Deprecated
    public static boolean equalsApproximatively(final Object object1, final Object object2) {
        return Utilities.equalsApproximatively(object1, object2);
    }

    /**
     * Transforms the given relative distance using the given transform. A relative distance
     * vector is transformed without applying the translation components. However it needs to
     * be computed at a particular location, given by the {@code origin} parameter in units
     * of the source CRS.
     *
     * @param  transform The transformation to apply.
     * @param  origin The position where to compute the delta transform in the source CRS.
     * @param  vector The distance vector to be delta transformed.
     * @return The result of the delta transformation.
     * @throws TransformException if the transformation failed.
     *
     * @see AffineTransform#deltaTransform(Point2D, Point2D)
     *
     * @since 3.10 (derived from 2.3)
     */
    public static double[] deltaTransform(final MathTransform transform,
            final DirectPosition origin, final double... vector) throws TransformException
    {
        ensureNonNull("transform", transform);
        final int sourceDim = transform.getSourceDimensions();
        final int targetDim = transform.getTargetDimensions();
        final double[] result = new double[targetDim];
        if (vector.length != sourceDim) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.MismatchedDimension_3,
                    "vector", vector.length, sourceDim));
        }
        if (transform instanceof AffineTransform) {
            ((AffineTransform) transform).deltaTransform(vector, 0, result, 0, 1);
        } else {
            /*
             * If the optimized case in the previous "if" statement can't be used,
             * use a more generic (but more costly) algorithm.
             */
            final double[] coordinates = new double[2 * Math.max(sourceDim, targetDim)];
            for (int i=0; i<sourceDim; i++) {
                final double c = origin.getOrdinate(i);
                final double d = vector[i] * 0.5;
                coordinates[i] = c - d;
                coordinates[i + sourceDim] = c + d;
            }
            transform.transform(coordinates, 0, coordinates, 0, 2);
            for (int i=0; i<targetDim; i++) {
                result[i] = coordinates[i + targetDim] - coordinates[i];
            }
        }
        return result;
    }
}

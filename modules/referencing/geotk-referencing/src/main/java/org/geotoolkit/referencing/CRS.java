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

import java.util.Set;
import java.util.List;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opengis.geometry.*;
import org.opengis.referencing.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.metadata.extent.*;
import org.opengis.util.FactoryException;

import org.geotoolkit.lang.Static;
import org.apache.sis.util.Version;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.Factories;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.FactoryRegistryException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.internal.referencing.OperationContext;
import org.geotoolkit.resources.Errors;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.cs.AxesConvention;

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
    /**
     * A factory for default (non-lenient) operations.
     */
    private static volatile CoordinateOperationFactory strictFactory;

    /**
     * A factory for default lenient operations.
     */
    private static volatile CoordinateOperationFactory lenientFactory;

    /**
     * The default value for {@link Hints#LENIENT_DATUM_SHIFT},
     * or {@code null} if not yet determined.
     */
    private static volatile Boolean defaultLenient;

    /**
     * Registers a listener automatically invoked when the system-wide configuration changed.
     */
    static {
        Factories.addChangeListener(new ChangeListener() {
            @Override public void stateChanged(ChangeEvent e) {
                synchronized (CRS.class) {
                    strictFactory   = null;
                    lenientFactory  = null;
                    defaultLenient  = null;
                }
            }
        });
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
     * @param lenient {@code true} if the coordinate operations should be created
     *        even when there is no information available for a datum shift.
     * @return The coordinate operation factory used for finding math transform in this class.
     *
     * @see Hints#LENIENT_DATUM_SHIFT
     *
     * @category factory
     * @since 2.4
     */
    public static CoordinateOperationFactory getCoordinateOperationFactory(final boolean lenient) {
        CoordinateOperationFactory factory = (lenient) ? lenientFactory : strictFactory;
        if (factory == null) synchronized (CRS.class) {
            // Double-checked locking - was a deprecated practice before Java 5.
            // Is okay since Java 5 provided that the variables are volatile.
            factory = (lenient) ? lenientFactory : strictFactory;
            if (factory == null) {
                final Hints hints = new Hints(); // Get the system-width default hints.
                if (lenient) {
                    hints.put(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
                }
                factory = FactoryFinder.getCoordinateOperationFactory(hints);
                if (lenient) {
                    lenientFactory = factory;
                } else {
                    strictFactory = factory;
                }
            }
        }
        return factory;
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
     * @see Hints#VERSION
     *
     * @category factory
     * @since 2.4
     */
    public static Version getVersion(final String authority) throws FactoryRegistryException {
        ensureNonNull("authority", authority);
        if ("EPSG".equalsIgnoreCase(authority)) {
            return new Version("7.9");   // TODO: fetch information from database.
        }
        return null;
    }

    /**
     * Gets the list of the codes that are supported by the given authority. For example
     * {@code getSupportedCodes("EPSG")} may returns {@code "EPSG:2000"}, {@code "EPSG:2001"},
     * {@code "EPSG:2002"}, <i>etc</i>. It may also returns {@code "2000"}, {@code "2001"},
     * {@code "2002"}, <i>etc.</i> without the {@code "EPSG:"} prefix. Whatever the authority
     * name is prefixed or not is factory implementation dependent.
     * <p>
     * If there is more than one factory for the given authority, then this method merges the
     * code set of all of them. If a factory fails to provide a set of supported code, then
     * this particular factory is ignored. Please be aware of the following potential issues:
     * <p>
     * <ul>
     *   <li>If there is more than one EPSG databases (for example an Access and a PostgreSQL ones),
     *       then this method will connect to all of them even if their content are identical.</li>
     *
     *   <li>If two factories format their codes differently (e.g. {@code "4326"} and
     *       {@code "EPSG:4326"}), then the returned set will contain a lot of synonymous
     *       codes.</li>
     *
     *   <li>For any code <var>c</var> in the returned set, there is no warranty that
     *       <code>{@linkplain #decode decode}(c)</code> will use the same authority
     *       factory than the one that formatted <var>c</var>.</li>
     *
     *   <li>This method doesn't report connection problems since it doesn't throw any exception.
     *       {@link FactoryException}s are logged as warnings and otherwise ignored.</li>
     * </ul>
     * <p>
     * If a more determinist behavior is wanted, consider the code below instead.
     * The following code exploit only one factory, the "preferred" one.
     *
     * {@preformat java
     *     factory = AuthorityFactoryFinder.getCRSAuthorityFactory(authority, null);
     *     Set<String> codes = factory.getAuthorityCodes(CoordinateReferenceSystem.class);
     *     String code = ...  // Choose a code here.
     *     CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem(code);
     * }
     *
     * @param  authority The authority name (for example {@code "EPSG"}).
     * @return The set of supported codes. May be empty, but never null.
     *
     * @see AuthorityFactory#getAuthorityCodes(Class)
     * @see <a href="http://www.geotoolkit.org/modules/referencing/supported-codes.html">List of authority codes</a>
     *
     * @category factory
     *
     * @deprecated Moved to Apache SIS as {@link org.apache.sis.referencing.factory.MultiAuthoritiesFactory#getAuthorityCodes(Class)}.
     */
    @Deprecated
    public static Set<String> getSupportedCodes(final String authority) { // LGPL
        ensureNonNull("authority", authority);
        try {
            return org.apache.sis.referencing.CRS.getAuthorityFactory(authority)
                    .getAuthorityCodes(CoordinateReferenceSystem.class);
        } catch (FactoryException e) {
            throw new RuntimeException(e);  // TODO
        }
    }

    /**
     * Returns a Coordinate Reference System for the specified code.
     * Note that the code needs to mention the authority. Examples:
     * <p>
     * <ul>
     *   <li>{@code EPSG:4326}</li>
     *   <li>{@code AUTO:42001,9001,0,30}</li>
     * </ul>
     * <p>
     * If there is more than one factory implementation for the same authority, then all additional
     * factories are {@linkplain org.geotoolkit.referencing.factory.FallbackAuthorityFactory fallbacks}
     * to be used only when the first acceptable factory failed to create the requested CRS object.
     *
     * {@section Common codes}
     * A few commonly used codes are:
     * <p>
     * <ul>
     *   <li>Geographic CRS:
     *   <ul>
     *     <li>WGS 84 (2D only): EPSG:4326</li>
     *     <li>WGS 84 with ellipsoidal height: EPSG:4979</li>
     *   </ul></li>
     *   <li>Simple projected CRS:
     *   <ul>
     *     <li>Mercator: 3395</li>
     *   </ul></li>
     *   <li>Universal Transverse Mercator (UTM) projections:
     *   <ul>
     *     <li>WGS 84 (northern hemisphere): EPSG:32600 + <var>zone</var></li>
     *     <li>WGS 84 (southern hemisphere): EPSG:32700 + <var>zone</var></li>
     *     <li>WGS 72 (northern hemisphere): EPSG:32200 + <var>zone</var></li>
     *     <li>WGS 72 (southern hemisphere): EPSG:32300 + <var>zone</var></li>
     *     <li>NAD 83 (northern hemisphere): EPSG:26900 + <var>zone</var> (zone 1 to 23 only)</li>
     *     <li>NAD 27 (northern hemisphere): EPSG:26700 + <var>zone</var> (zone 1 to 22 only)</li>
     *   </ul></li>
     * </ul>
     *
     * {@section Caching}
     * CRS objects created by previous calls to this method are
     * {@linkplain org.geotoolkit.referencing.factory.CachingAuthorityFactory cached}
     * using {@linkplain java.lang.ref.WeakReference weak references}. Subsequent calls to this
     * method with the same authority code should be fast, unless the CRS object has been garbage
     * collected.
     *
     * @param  code The Coordinate Reference System authority code.
     * @return The Coordinate Reference System for the provided code.
     * @throws NoSuchAuthorityCodeException If the code could not be understood.
     * @throws FactoryException if the CRS creation failed for an other reason.
     *
     * @see #getSupportedCodes(String)
     * @see org.apache.sis.measure.Units#valueOfEPSG(int)
     * @see <a href="http://www.geotoolkit.org/modules/referencing/supported-codes.html">List of authority codes</a>
     *
     * @category factory
     *
     * @deprecated Moved to {@link org.apache.sis.referencing.CRS#forCode(String)}.
     */
    @Deprecated
    public static CoordinateReferenceSystem decode(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        return org.apache.sis.referencing.CRS.forCode(code);
    }

    /**
     * Returns a Coordinate Reference System for the specified code, maybe forcing the axis order
     * to (<var>longitude</var>, <var>latitude</var>). The {@code code} argument value is parsed
     * as in <code>{@linkplain #decode(String) decode}(code)</code>. The {@code longitudeFirst}
     * argument is the value to be given to the {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER
     * FORCE_LONGITUDE_FIRST_AXIS_ORDER} hint.
     * <p>
     * <b>Example:</b> by default, {@code CRS.decode("EPSG:4326")} returns a Geographic CRS with
     * (<var>latitude</var>, <var>longitude</var>) axis order, while {@code CRS.decode("EPSG:4326", true)}
     * returns the same CRS except for axis order, which is  (<var>longitude</var>, <var>latitude</var>).
     *
     * @param  code The Coordinate Reference System authority code.
     * @param  longitudeFirst {@code true} if axis order should be forced to
     *         (<var>longitude</var>, <var>latitude</var>), {@code false} if no order should
     *         be forced (i.e. the standard specified by the authority is respected).
     * @return The Coordinate Reference System for the provided code.
     * @throws NoSuchAuthorityCodeException If the code could not be understood.
     * @throws FactoryException if the CRS creation failed for an other reason.
     *
     * @see Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER
     * @see org.geotoolkit.referencing.factory.epsg.LongitudeFirstEpsgFactory
     * @see <a href="http://www.geotoolkit.org/modules/referencing/supported-codes.html">List of authority codes</a>
     *
     * @category factory
     * @since 2.3
     *
     * @deprecated "Longitude first factory" no longer supported. Use a standard factory instead,
     *             and invoke AbstractCRS.forConvention(AxesConvention) if desired.
     */
    @Deprecated
    public static CoordinateReferenceSystem decode(String code, final boolean longitudeFirst)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        CoordinateReferenceSystem crs = org.apache.sis.referencing.CRS.forCode(code);
        if (longitudeFirst) {
            crs = AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.RIGHT_HANDED);
        }
        return crs;
    }

    /**
     * Parses a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
     * Known Text</cite></A> (WKT) into a CRS object. This convenience method is a
     * shorthand for the following:
     *
     * {@preformat java
     *     FactoryFinder.getCRSFactory(null).createFromWKT(wkt);
     * }
     *
     * @param wkt The WKT string to parse.
     * @return The parsed coordinate reference system.
     * @throws FactoryException if the given WKT can't be parsed.
     *
     * @see Envelopes#parseWKT(String)
     * @see CoordinateReferenceSystem#toWKT()
     *
     * @category factory
     *
     * @deprecated Moved to Apache SIS as {@link org.apache.sis.referencing.CRS#fromWKT(String)}.
     */
    @Deprecated
    public static CoordinateReferenceSystem parseWKT(final String wkt) throws FactoryException {
        return org.apache.sis.referencing.CRS.fromWKT(wkt);
    }

    /**
     * Returns the domain of validity for the specified coordinate reference system,
     * or {@code null} if unknown. The returned envelope is expressed in terms of the
     * specified CRS.
     * <p>
     * This method looks in two places:
     * <p>
     * <ul>
     *   <li>First, it checks the {@linkplain CoordinateReferenceSystem#getDomainOfValidity domain
     *       of validity} associated with the given CRS. Only {@linkplain GeographicExtent
     *       geographic extents} of kind {@linkplain BoundingPolygon bounding polygon} are
     *       taken in account.</li>
     *   <li>If the above step does not found found any bounding polygon, then the
     *       {@linkplain #getGeographicBoundingBox geographic bounding boxes} are
     *       used as a fallback.</li>
     * </ul>
     * <p>
     * Note that this method is also accessible from the {@link Envelopes} class.
     *
     * @param  crs The coordinate reference system, or {@code null}.
     * @return The envelope in terms of the specified CRS, or {@code null} if none.
     *
     * @see #getGeographicBoundingBox(CoordinateReferenceSystem)
     * @see Envelopes#getDomainOfValidity(CoordinateReferenceSystem)
     * @see GeneralEnvelope#reduceToDomain(boolean)
     *
     * @category information
     * @since 2.2
     */
    public static Envelope getEnvelope(final CoordinateReferenceSystem crs) {
        Envelope envelope = null;
        GeneralEnvelope merged = null;
        if (crs != null) {
            final Extent domainOfValidity = crs.getDomainOfValidity();
            if (domainOfValidity != null) {
                for (final GeographicExtent extent : domainOfValidity.getGeographicElements()) {
                    if (Boolean.FALSE.equals(extent.getInclusion())) {
                        continue;
                    }
                    if (extent instanceof BoundingPolygon) {
                        for (final Geometry geometry : ((BoundingPolygon) extent).getPolygons()) {
                            final Envelope candidate = geometry.getEnvelope();
                            if (candidate != null) {
                                final CoordinateReferenceSystem sourceCRS =
                                        candidate.getCoordinateReferenceSystem();
                                if (sourceCRS == null || equalsIgnoreMetadata(sourceCRS, crs)) {
                                    if (envelope == null) {
                                        envelope = candidate;
                                    } else {
                                        if (merged == null) {
                                            envelope = merged = new GeneralEnvelope(envelope);
                                        }
                                        merged.add(envelope);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        /*
         * If no envelope was found, uses the geographic bounding box as a fallback. We will
         * need to transform it from WGS84 to the supplied CRS. This step was not required in
         * the previous block because the later selected only envelopes in the right CRS.
         */
        if (envelope == null) {
            final GeographicBoundingBox bounds = org.apache.sis.referencing.CRS.getGeographicBoundingBox(crs);
            if (bounds != null && !Boolean.FALSE.equals(bounds.getInclusion())) {
                /*
                 * We do not assign WGS84 unconditionally to the geographic bounding box, because
                 * it is not defined to be on a particular datum; it is only approximative bounds.
                 * We try to get the GeographicCRS from the user-supplied CRS in order to reduce
                 * the amount of transformation needed.
                 */
                final SingleCRS targetCRS = org.apache.sis.referencing.CRS.getHorizontalComponent(crs);
                final GeographicCRS sourceCRS = org.apache.sis.internal.referencing.ReferencingUtilities.toNormalizedGeographicCRS(targetCRS);
                if (sourceCRS != null) {
                    envelope = merged = new GeneralEnvelope(bounds);
                    merged.translate(-org.apache.sis.referencing.CRS.getGreenwichLongitude(sourceCRS), 0);
                    merged.setCoordinateReferenceSystem(sourceCRS);
                    try {
                        envelope = transform(envelope, targetCRS);
                    } catch (TransformException exception) {
                        /*
                         * The envelope is probably outside the range of validity for this CRS.
                         * It should not occurs, since the envelope is supposed to describe the
                         * CRS area of validity. Logs a warning and returns null, since it is a
                         * legal return value according this method contract.
                         */
                        unexpectedException("getEnvelope", exception);
                        envelope = null;
                    }
                }
            }
        }
        return envelope;
    }

    /**
     * Returns {@code true} if the given CRS is horizontal. This method is provided because there is
     * a direct way to determine if a CRS is vertical or temporal, but no direct way to determine if
     * it is horizontal. So this method complements the check for spatio-temporal components as below:
     * <p>
     * <ul>
     *   <li>{@code if (crs instanceof TemporalCRS)} determines if the CRS is for the temporal component.</li>
     *   <li>{@code if (crs instanceof VerticalCRS)} determines if the CRS is for the vertical component.</li>
     *   <li>{@code if (CRS.isHorizontalCRS(crs))} determines if the CRS is for the horizontal component.</li>
     * </ul>
     * <p>
     * This method considers a CRS as horizontal if it is two-dimensional and comply
     * with one of the following conditions:
     * <p>
     * <ul>
     *   <li>It is an instance of {@link GeographicCRS}.</li>
     *   <li>It is an instance of {@link ProjectedCRS} (actually this is not explicitly
     *       checked, since this condition is a special case of the condition below).</li>
     *   <li>It is an instance of {@link GeneralDerivedCRS} based on a horizontal CRS
     *       and using a {@link GeodeticDatum}.</li>
     * </ul>
     * <p>
     * The last condition ({@code GeneralDerivedCRS} based on a horizontal CRS) allows for example
     * to express the coordinates of a projected CRS (which use a Cartesian coordinate system) in
     * a {@linkplain org.opengis.referencing.cs.PolarCS polar coordinate system} and still consider
     * the result as horizontal. However this assumes that the axes of the derived CRS are coplanar
     * with the axes of the base CRS. This is not always true since a derived CRS could be created
     * for an inclined plane, for example a plane fitting the slope of a mountain. ISO 19111 does
     * not specify how to handle this case. In the Geotk implementation, we suggest to define a new
     * {@linkplain Datum datum} for inclined plane which is not a geodetic datum.
     *
     * @param  crs The coordinate reference system, or {@code null}.
     * @return {@code true} if the given CRS is non-null and comply with one of the above
     *         conditions, or {@code false} otherwise.
     *
     * @category information
     * @since 3.05
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.referencing.CRS} class,
     *             with a more conservative semantic.
     */
    @Deprecated
    public static boolean isHorizontalCRS(CoordinateReferenceSystem crs) {
        if (crs instanceof SingleCRS) {
            final int dimension = crs.getCoordinateSystem().getDimension();
            if (dimension == 2) {
                final Datum datum = ((SingleCRS) crs).getDatum();
                if (datum instanceof GeodeticDatum) {
                    while (crs instanceof GeneralDerivedCRS) {
                        crs = ((GeneralDerivedCRS) crs).getBaseCRS();
                    }
                    return (crs instanceof GeographicCRS);
                }
            }
        }
        return false;
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
                    if (equalsIgnoreMetadata(component, toSearch[i])) {
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
        } else {
            // Aggregate crs parts name
            final CoordinateReferenceSystem[] array = parts.toArray(new CoordinateReferenceSystem[size]);
            final StringBuilder sb = new StringBuilder(array[0].getName().toString());
            for (int i=1; i<size; i++) {
                sb.append(" with ").append(array[i].getName().toString());
            }
            return new DefaultCompoundCRS(Collections.singletonMap(DefaultCompoundCRS.NAME_KEY, sb.toString()), array);
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
        return CRSUtilities.getDatum(crs);
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
     * Compares the specified objects for equality, ignoring metadata. If this method returns
     * {@code true}, then:
     *
     * <ul>
     *   <li><p>If the two given objects are {@link MathTransform} instances, then transforming
     *       a set of coordinate values using one transform will produce the same results than
     *       transforming the same coordinates with the other transform.</p></li>
     *
     *   <li><p>If the two given objects are {@link CoordinateReferenceSystem} instances,
     *       then a call to <code>{@linkplain #findMathTransform(CoordinateReferenceSystem,
     *       CoordinateReferenceSystem) findMathTransform}(crs1, crs2)</code> will return
     *       an identity transform.</p></li>
     * </ul>
     *
     * If a more lenient comparison - allowing slight differences in numerical values - is wanted,
     * then {@link #equalsApproximatively(Object, Object)} can be used instead.
     *
     * {@section Implementation note}
     * This is a convenience method for the following method call:
     *
     * {@preformat java
     *     return Utilities.deepEquals(object1, object2, ComparisonMode.IGNORE_METADATA);
     * }
     *
     * @param  object1 The first object to compare (may be null).
     * @param  object2 The second object to compare (may be null).
     * @return {@code true} if both objects are equal, ignoring metadata.
     *
     * @see Utilities#deepEquals(Object, Object, ComparisonMode)
     * @see ComparisonMode#IGNORE_METADATA
     *
     * @category information
     * @since 2.2
     */
    public static boolean equalsIgnoreMetadata(final Object object1, final Object object2) {
        return Utilities.deepEquals(object1, object2, ComparisonMode.IGNORE_METADATA);
    }

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
     */
    public static boolean equalsApproximatively(final Object object1, final Object object2) {
        return Utilities.deepEquals(object1, object2, ComparisonMode.APPROXIMATIVE);
    }

    /**
     * Grabs a transform between two Coordinate Reference Systems. This convenience method is a
     * shorthand for the following:
     *
     * {@preformat java
     *     CoordinateOperationFactory factory = FactoryFinder.getCoordinateOperationFactory(null);
     *     CoordinateOperation operation = factory.createOperation(sourceCRS, targetCRS);
     *     MathTransform transform = operation.getMathTransform();
     * }
     *
     * Note that some metadata like {@linkplain CoordinateOperation#getCoordinateOperationAccuracy
     * coordinate operation accuracy} are lost by this method. If those metadata are wanted, use the
     * {@linkplain CoordinateOperationFactory coordinate operation factory} directly.
     * <p>
     * Sample use:
     *
     * {@preformat java
     *     CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:42102");
     *     CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
     *     MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
     * }
     *
     * @param  sourceCRS The source CRS.
     * @param  targetCRS The target CRS.
     * @return The math transform from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If no math transform can be created for the specified source and
     *         target CRS.
     *
     * @see CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)
     *
     * @category transform
     */
    // LGPL - we will define findOperation instead.
    public static MathTransform findMathTransform(final CoordinateReferenceSystem sourceCRS,
                                                  final CoordinateReferenceSystem targetCRS)
            throws FactoryException
    {
        // No need to synchronize; this is not a big deal if 'defaultLenient' is computed twice.
        Boolean lenient = defaultLenient;
        if (lenient == null) {
            defaultLenient = lenient = Boolean.TRUE.equals(
                    Hints.getSystemDefault(Hints.LENIENT_DATUM_SHIFT));
        }
        return findMathTransform(sourceCRS, targetCRS, lenient);
    }

    /**
     * Grab a transform between two Coordinate Reference Systems. This method is similar to
     * <code>{@linkplain #findMathTransform(CoordinateReferenceSystem, CoordinateReferenceSystem)
     * findMathTransform}(sourceCRS, targetCRS)</code>, except that it specifies whatever this
     * method should tolerate <cite>lenient datum shift</cite>. If the {@code lenient} argument
     * is {@code true}, then this method will not throw a "<cite>Bursa-Wolf parameters required</cite>"
     * exception during datum shifts if the Bursa-Wolf parameters are not specified.
     * Instead it will assume a no datum shift.
     *
     * @param  sourceCRS The source CRS.
     * @param  targetCRS The target CRS.
     * @param  lenient {@code true} if the math transform should be created even when there is
     *         no information available for a datum shift. if this argument is not specified,
     *         then the default value is determined from the {@linkplain Hints#getSystemDefault
     *         system default}.
     * @return The math transform from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If no math transform can be created for the specified source and
     *         target CRS.
     *
     * @see Hints#LENIENT_DATUM_SHIFT
     * @see CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)
     *
     * @category transform
     */
    public static MathTransform findMathTransform(final CoordinateReferenceSystem sourceCRS,
                                                  final CoordinateReferenceSystem targetCRS,
                                                  boolean lenient) // LGPL
            throws FactoryException
    {
        ensureNonNull("sourceCRS", sourceCRS);
        ensureNonNull("targetCRS", targetCRS);
        return getCoordinateOperationFactory(lenient).createOperation(sourceCRS, targetCRS).getMathTransform();
    }

    /**
     * Grab a transform between two Coordinate Reference Systems for the given area of interest.
     * This method may returns a more accurate transform than {@link #findMathTransform(CoordinateReferenceSystem,
     * CoordinateReferenceSystem, boolean)} for that area in some cases.
     *
     * @param  sourceCRS The source CRS.
     * @param  targetCRS The target CRS.
     * @param  areaOfInterest The geographic area of interest.
     * @param  lenient {@code true} if the math transform should be created even when there is
     *         no information available for a datum shift.
     * @return The math transform from {@code sourceCRS} to {@code targetCRS} in the given area of interest.
     * @throws FactoryException If no math transform can be created for the specified source and target CRS.
     *
     * @since 4.0-M2
     */
    public static MathTransform findMathTransform(final CoordinateReferenceSystem sourceCRS,
                                                  final CoordinateReferenceSystem targetCRS,
                                                  final GeographicBoundingBox areaOfInterest,
                                                  boolean lenient)
            throws FactoryException
    {
        /*
         * TODO: we use a ThreadLocal for now as an easy way to support this functionality without
         * breaking the DefaultCoordinateOperationFactory API. However we will need to find a better
         * approach in Apache SIS.
         */
        OperationContext.setAreaOfInterest(areaOfInterest);
        try {
            return findMathTransform(sourceCRS, targetCRS, true);
        } finally {
            OperationContext.clear();
        }
    }

    // Note: the above 4 transform methods simply delegate their work to the Envelopes class.
    // We keep those methods mostly for historical reasons.  Some Geotk code still reference
    // those methods instead than Envelopes. We do that when the CRS class is used anyway so
    // there is no advantage to reference one more class.

    /**
     * Transforms the given envelope to the specified CRS. If any argument is null, or if the
     * {@linkplain Envelope#getCoordinateReferenceSystem() envelope CRS} is null or the same
     * instance than the given target CRS, then the given envelope is returned unchanged.
     * Otherwise a new transformed envelope is returned.
     * <p>
     * See {@link Envelopes#transform(Envelope, CoordinateReferenceSystem)} for more information.
     * This method delegates its work to the above-cited {@code Envelopes} class and is defined
     * in this {@code CRS} class only for convenience.
     *
     * @param  envelope The envelope to transform (may be {@code null}).
     * @param  targetCRS The target CRS (may be {@code null}).
     * @return A new transformed envelope, or directly {@code envelope} if no change was required.
     * @throws TransformException If a transformation was required and failed.
     *
     * @category transform
     * @since 2.5
     */
    // See the above comment about why some Geotk code still reference this method.
    public static Envelope transform(Envelope envelope, final CoordinateReferenceSystem targetCRS)
            throws TransformException
    {
        return Envelopes.transform(envelope, targetCRS);
    }

    /**
     * Transforms an envelope using the given {@linkplain MathTransform math transform}.
     * The transformation is only approximative: the returned envelope may be bigger than
     * necessary, or smaller than required if the bounding box contains a pole.
     * <p>
     * See {@link Envelopes#transform(MathTransform, Envelope)} for more information.
     * This method delegates its work to the above-cited {@code Envelopes} class and
     * is defined in this {@code CRS} class only for convenience.
     *
     * @param  transform The transform to use.
     * @param  envelope Envelope to transform, or {@code null}. This envelope will not be modified.
     * @return The transformed envelope, or {@code null} if {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @category transform
     * @since 2.4
     */
    // See the above comment about why some Geotk code still reference this method.
    public static GeneralEnvelope transform(final MathTransform transform, final Envelope envelope)
            throws TransformException
    {
        return Envelopes.transform(transform, envelope);
    }

    /**
     * Transforms an envelope using the given {@linkplain CoordinateOperation coordinate operation}.
     * The transformation is only approximative: the returned envelope may be bigger than the
     * smallest possible bounding box, but should not be smaller in most cases.
     * <p>
     * See {@link Envelopes#transform(CoordinateOperation, Envelope)} for more information.
     * This method delegates its work to the above-cited {@code Envelopes} class and is defined
     * in this {@code CRS} class only for convenience.
     *
     * @param  operation The operation to use.
     * @param  envelope Envelope to transform, or {@code null}. This envelope will not be modified.
     * @return The transformed envelope, or {@code null} if {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @category transform
     * @since 2.4
     */
    // See the above comment about why some Geotk code still reference this method.
    public static GeneralEnvelope transform(final CoordinateOperation operation, Envelope envelope)
            throws TransformException
    {
        return Envelopes.transform(operation, envelope);
    }

    /**
     * Transforms a rectangular envelope using the given {@linkplain MathTransform math transform}.
     * The transformation is only approximative: the returned envelope may be bigger than
     * necessary, or smaller than required if the bounding box contains a pole.
     * <p>
     * See {@link Envelopes#transform(MathTransform2D, Rectangle2D, Rectangle2D)} for more
     * information. This method delegates its work to the above-cited {@code Envelopes} class
     * and is defined in this {@code CRS} class only for convenience.
     *
     * @param  transform   The transform to use. Source and target dimension must be 2.
     * @param  envelope    The rectangle to transform (may be {@code null}).
     * @param  destination The destination rectangle (may be {@code envelope}).
     *         If {@code null}, a new rectangle will be created and returned.
     * @return {@code destination}, or a new rectangle if {@code destination} was non-null
     *         and {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @category transform
     * @since 2.4
     */
    // See the above comment about why some Geotk code still reference this method.
    public static Rectangle2D transform(final MathTransform2D transform,
                                        final Rectangle2D     envelope,
                                              Rectangle2D     destination)
            throws TransformException
    {
        return org.geotoolkit.geometry.Envelopes.transform(transform, envelope, destination);
    }

    /**
     * Transforms a rectangular envelope using the given {@linkplain CoordinateOperation coordinate
     * operation}. The transformation is only approximative: the returned envelope may be bigger
     * than the smallest possible bounding box, but should not be smaller in most cases.
     * <p>
     * See {@link Envelopes#transform(CoordinateOperation, Rectangle2D, Rectangle2D)} for more
     * information. This method delegates its work to the above-cited {@code Envelopes} class
     * and is defined in this {@code CRS} class only for convenience.
     *
     * @param  operation The operation to use. Source and target dimension must be 2.
     * @param  envelope The rectangle to transform (may be {@code null}).
     * @param  destination The destination rectangle (may be {@code envelope}).
     *         If {@code null}, a new rectangle will be created and returned.
     * @return {@code destination}, or a new rectangle if {@code destination} was non-null
     *         and {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @category transform
     * @since 2.4
     */
    // See the above comment about why some Geotk code still reference this method.
    public static Rectangle2D transform(final CoordinateOperation operation,
                                        final Rectangle2D         envelope,
                                              Rectangle2D         destination)
            throws TransformException
    {
        return org.geotoolkit.geometry.Envelopes.transform(operation, envelope, destination);
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

    /**
     * Invoked when an unexpected exception occurred. Those exceptions must be non-fatal,
     * i.e. the caller <strong>must</strong> have a reasonable fallback (otherwise it
     * should propagate the exception).
     */
    static void unexpectedException(final String methodName, final Exception exception) {
        Logging.unexpectedException(null, CRS.class, methodName, exception);
    }
}

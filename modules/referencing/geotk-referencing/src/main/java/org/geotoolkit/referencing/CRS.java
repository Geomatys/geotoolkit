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
import java.util.Map;
import java.util.List;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opengis.geometry.*;
import org.opengis.referencing.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.metadata.extent.*;
import org.opengis.util.FactoryException;

import org.geotoolkit.lang.Static;
import org.apache.sis.util.Version;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.Factories;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;
import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.internal.referencing.AxisDirections;
import org.geotoolkit.resources.Errors;

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
     * The CRS factory to use for parsing WKT. Will be fetched when first needed
     * are stored for avoiding indirect synchronization lock in {@link #parseWKT}.
     */
    private static volatile CRSFactory crsFactory;

    /**
     * A factory for CRS creation as specified by the authority, which may have
     * (<var>latitude</var>, <var>longitude</var>) axis order. Will be created
     * only when first needed.
     */
    private static volatile CRSAuthorityFactory standardFactory;

    /**
     * A factory for CRS creation with (<var>longitude</var>, <var>latitude</var>) axis order.
     * Will be created only when first needed.
     */
    private static volatile CRSAuthorityFactory xyFactory;

    /**
     * A factory for default (non-lenient) operations.
     */
    private static volatile CoordinateOperationFactory strictFactory;

    /**
     * A factory for default lenient operations.
     */
    private static volatile CoordinateOperationFactory lenientFactory;

    /**
     * The default value for {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER},
     * or {@code null} if not yet determined.
     */
    private static volatile Boolean defaultOrder;

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
                    crsFactory      = null;
                    standardFactory = null;
                    xyFactory       = null;
                    strictFactory   = null;
                    lenientFactory  = null;
                    defaultOrder    = null;
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
     * Returns the CRS factory. This is used mostly for WKT parsing.
     */
    private static CRSFactory getCRSFactory() {
        CRSFactory factory = crsFactory;
        if (factory == null) {
            synchronized (CRS.class) {
                // Double-checked locking - was a deprecated practice before Java 5.
                // Is okay since Java 5 provided that the variable is volatile.
                factory = crsFactory;
                if (factory == null) {
                    crsFactory = factory = FactoryFinder.getCRSFactory(null);
                }
            }
        }
        return factory;
    }

    /**
     * Returns the CRS authority factory used by the {@link #decode(String,boolean) decode} methods.
     * This factory {@linkplain org.geotoolkit.referencing.factory.CachingAuthorityFactory uses a cache},
     * scans over {@linkplain org.geotoolkit.referencing.factory.AllAuthoritiesFactory all factories} and
     * uses additional factories as {@linkplain org.geotoolkit.referencing.factory.FallbackAuthorityFactory
     * fallbacks} if there is more than one {@linkplain AuthorityFactoryFinder#getCRSAuthorityFactories
     * registered factory} for the same authority.
     * <p>
     * This factory can be used as a kind of <cite>system-wide</cite> factory for all authorities.
     * However for more determinist behavior, consider using a more specific factory (as returned
     * by {@link AuthorityFactoryFinder#getCRSAuthorityFactory}) when the authority is known.
     *
     * @param  longitudeFirst {@code true} if axis order should be forced to
     *         (<var>longitude</var>, <var>latitude</var>), {@code false} if no order should be
     *         forced (i.e. the standard specified by the authority is respected), or {@code null}
     *         for the {@linkplain Hints#getSystemDefault system default}.
     * @return The CRS authority factory.
     * @throws FactoryRegistryException if the factory can't be created.
     *
     * @see Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER
     *
     * @category factory
     * @since 2.3
     */
    public static CRSAuthorityFactory getAuthorityFactory(Boolean longitudeFirst)
            throws FactoryRegistryException
    {
        // No need to synchronize; this is not a big deal if 'defaultOrder' is computed twice.
        if (longitudeFirst == null) {
            longitudeFirst = defaultOrder;
            if (longitudeFirst == null) {
                longitudeFirst = Boolean.TRUE.equals(Hints.getSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER));
                defaultOrder = longitudeFirst;
            }
        }
        CRSAuthorityFactory factory = (longitudeFirst) ? xyFactory : standardFactory;
        if (factory == null) synchronized (CRS.class) {
            // Double-checked locking - was a deprecated practice before Java 5.
            // Is okay since Java 5 provided that the variables are volatile.
            factory = (longitudeFirst) ? xyFactory : standardFactory;
            if (factory == null) try {
                factory = DefaultAuthorityFactory.create(longitudeFirst);
                if (longitudeFirst) {
                    xyFactory = factory;
                } else {
                    standardFactory = factory;
                }
            } catch (NoSuchElementException exception) {
                // No factory registered in FactoryFinder.
                throw new FactoryNotFoundException(null, exception);
            }
        }
        return factory;
    }

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
        Object candidate = AuthorityFactoryFinder.getCRSAuthorityFactory(authority, null);
        final Set<Factory> guard = new HashSet<>();
        while (candidate instanceof Factory) {
            final Factory factory = (Factory) candidate;
            if (!guard.add(factory)) {
                break; // Safety against never-ending recursivity.
            }
            final Map<RenderingHints.Key,?> hints = factory.getImplementationHints();
            final Object version = hints.get(Hints.VERSION);
            if (version instanceof Version) {
                return (Version) version;
            }
            candidate = hints.get(Hints.CRS_AUTHORITY_FACTORY);
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
     */
    public static Set<String> getSupportedCodes(final String authority) {
        ensureNonNull("authority", authority);
        return DefaultAuthorityFactory.getSupportedCodes(authority);
    }

    /**
     * Returns the set of the authority identifiers supported by registered authority factories.
     * This method search only for {@linkplain CRSAuthorityFactory CRS authority factories}.
     *
     * @param  returnAliases If {@code true}, the set will contain all identifiers for each
     *         authority. If {@code false}, only the first one
     * @return The set of supported authorities. May be empty, but never null.
     *
     * @category factory
     * @since 2.3.1
     */
    public static Set<String> getSupportedAuthorities(final boolean returnAliases) {
        return DefaultAuthorityFactory.getSupportedAuthorities(returnAliases);
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
     */
    public static CoordinateReferenceSystem decode(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        return getAuthorityFactory(null).createCoordinateReferenceSystem(code);
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
     */
    public static CoordinateReferenceSystem decode(String code, final boolean longitudeFirst)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        return getAuthorityFactory(longitudeFirst).createCoordinateReferenceSystem(code);
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
     */
    public static CoordinateReferenceSystem parseWKT(final String wkt) throws FactoryException {
        ensureNonNull("wkt", wkt);
        return getCRSFactory().createFromWKT(wkt);
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
            final GeographicBoundingBox bounds = getGeographicBoundingBox(crs);
            if (bounds != null && !Boolean.FALSE.equals(bounds.getInclusion())) {
                envelope = merged = new GeneralEnvelope(
                        new double[] {bounds.getWestBoundLongitude(), bounds.getSouthBoundLatitude()},
                        new double[] {bounds.getEastBoundLongitude(), bounds.getNorthBoundLatitude()});
                /*
                 * We do not assign WGS84 unconditionally to the geographic bounding box, because
                 * it is not defined to be on a particular datum; it is only approximative bounds.
                 * We try to get the GeographicCRS from the user-supplied CRS and fallback on WGS
                 * 84 only if we found none.
                 */
                final SingleCRS     targetCRS = getHorizontalCRS(crs);
                final GeographicCRS sourceCRS = CRSUtilities.getStandardGeographicCRS2D(targetCRS);
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
                    envelope = null;
                    unexpectedException("getEnvelope", exception);
                }
                /*
                 * If transform(...) created a new envelope, its CRS is already targetCRS so it
                 * doesn't matter if 'merged' is not anymore the right instance. If 'transform'
                 * returned the envelope unchanged, the 'merged' reference still valid and we
                 * want to ensure that it have the user-supplied CRS.
                 */
                merged.setCoordinateReferenceSystem(targetCRS);
            }
        }
        return envelope;
    }

    /**
     * Returns the valid geographic area for the specified coordinate reference system,
     * or {@code null} if unknown.
     *
     * This method fetches the {@linkplain CoordinateReferenceSystem#getDomainOfValidity domain
     * of validity} associated with the given CRS. Only {@linkplain GeographicExtent geographic
     * extents} of kind {@linkplain GeographicBoundingBox geographic bounding box} are taken in
     * account.
     *
     * @param  crs The coordinate reference system, or {@code null}.
     * @return The geographic area, or {@code null} if none.
     *
     * @see #getEnvelope(CoordinateReferenceSystem)
     *
     * @category information
     * @since 2.3
     */
    public static GeographicBoundingBox getGeographicBoundingBox(final CoordinateReferenceSystem crs) {
        GeographicBoundingBox bounds = null;
        DefaultGeographicBoundingBox merged = null;
        if (crs != null) {
            final Extent domainOfValidity = crs.getDomainOfValidity();
            if (domainOfValidity != null) {
                for (final GeographicExtent extent : domainOfValidity.getGeographicElements()) {
                    if (extent instanceof GeographicBoundingBox) {
                        final GeographicBoundingBox candidate = (GeographicBoundingBox) extent;
                        if (bounds == null) {
                            bounds = candidate;
                        } else {
                            if (merged == null) {
                                bounds = merged = new DefaultGeographicBoundingBox(bounds);
                            }
                            merged.add(candidate);
                        }
                    }
                }
            }
        }
        return bounds;
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
     * @see #getHorizontalCRS(CoordinateReferenceSystem)
     *
     * @category information
     * @since 3.05
     */
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
     * Returns the first horizontal coordinate reference system found in the given CRS,
     * or {@code null} if there is none. A horizontal CRS is usually a two-dimensional
     * {@linkplain GeographicCRS geographic} or {@linkplain ProjectedCRS projected} CRS.
     * See the {@link #isHorizontalCRS(CoordinateReferenceSystem) isHorizontalCRS} method for
     * a more accurate description about the conditions for a CRS to be considered horizontal.
     *
     * @param  crs The coordinate reference system, or {@code null}.
     * @return The horizontal CRS, or {@code null} if none.
     *
     * @category information
     * @since 2.4
     */
    public static SingleCRS getHorizontalCRS(final CoordinateReferenceSystem crs) {
        if (crs instanceof SingleCRS) {
            final CoordinateSystem cs = crs.getCoordinateSystem();
            final int dimension = cs.getDimension();
            if (dimension == 2) {
                /*
                 * For two-dimensional CRS, returns the CRS directly if it is either a
                 * GeographicCRS, or any kind of derived CRS having a GeographicCRS as
                 * its base and a geodetic datum.
                 */
                final Datum datum = ((SingleCRS) crs).getDatum();
                if (datum instanceof GeodeticDatum) {
                    CoordinateReferenceSystem base = crs;
                    while (base instanceof GeneralDerivedCRS) {
                        base = ((GeneralDerivedCRS) base).getBaseCRS();
                    }
                    // No need to test for ProjectedCRS, since the code above unwrap it.
                    if (base instanceof GeographicCRS) {
                        assert isHorizontalCRS(crs) : crs;
                        return (SingleCRS) crs; // Really returns 'crs', not 'base'.
                    }
                }
            } else if (dimension >= 3 && crs instanceof GeographicCRS) {
                /*
                 * For three-dimensional Geographic CRS, extracts the axis having a direction
                 * like "North", "North-East", "East", etc. If we find exactly two of them,
                 * we can build a new GeographicCRS using them.
                 */
                CoordinateSystemAxis axis0 = null, axis1 = null;
                int count = 0;
                for (int i=0; i<dimension; i++) {
                    final CoordinateSystemAxis axis = cs.getAxis(i);
search:             if (DefaultCoordinateSystemAxis.isCompassDirection(axis.getDirection())) {
                        switch (count++) {
                            case 0: axis0 = axis; break;
                            case 1: axis1 = axis; break;
                            default: break search;
                        }
                    }
                }
                if (count == 2) {
                    final GeodeticDatum datum = ((GeographicCRS) crs).getDatum();
                    Map<String,?> properties = CRSUtilities.changeDimensionInName(cs, "3D", "2D");
                    EllipsoidalCS horizontalCS;
                    try {
                        horizontalCS = FactoryFinder.getCSFactory(null).
                                createEllipsoidalCS(properties, axis0, axis1);
                    } catch (FactoryException e) {
                        Logging.recoverableException(CRS.class, "getHorizontalCRS", e);
                        horizontalCS = new DefaultEllipsoidalCS(properties, axis0, axis1);
                    }
                    properties = CRSUtilities.changeDimensionInName(crs, "3D", "2D");
                    GeographicCRS horizontalCRS;
                    try {
                        horizontalCRS = getCRSFactory().createGeographicCRS(properties, datum, horizontalCS);
                    } catch (FactoryException e) {
                        Logging.recoverableException(CRS.class, "getHorizontalCRS", e);
                        horizontalCRS = new DefaultGeographicCRS(properties, datum, horizontalCS);
                    }
                    assert isHorizontalCRS(horizontalCRS) : horizontalCRS;
                    return horizontalCRS;
                }
            }
        }
        if (crs instanceof CompoundCRS) {
            final CompoundCRS cp = (CompoundCRS) crs;
            for (final CoordinateReferenceSystem c : cp.getComponents()) {
                final SingleCRS candidate = getHorizontalCRS(c);
                if (candidate != null) {
                    assert isHorizontalCRS(candidate) : candidate;
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Returns the first projected coordinate reference system found in a the given CRS,
     * or {@code null} if there is none.
     *
     * @param  crs The coordinate reference system, or {@code null}.
     * @return The projected CRS, or {@code null} if none.
     *
     * @category information
     * @since 2.4
     */
    public static ProjectedCRS getProjectedCRS(final CoordinateReferenceSystem crs) {
        if (crs instanceof ProjectedCRS) {
            return (ProjectedCRS) crs;
        }
        if (crs instanceof CompoundCRS) {
            final CompoundCRS cp = (CompoundCRS) crs;
            for (final CoordinateReferenceSystem c : cp.getComponents()) {
                final ProjectedCRS candidate = getProjectedCRS(c);
                if (candidate != null) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Returns the first vertical coordinate reference system found in a the given CRS,
     * or {@code null} if there is none.
     *
     * @param  crs The coordinate reference system, or {@code null}.
     * @return The vertical CRS, or {@code null} if none.
     *
     * @category information
     * @since 2.4
     */
    public static VerticalCRS getVerticalCRS(final CoordinateReferenceSystem crs) {
        if (crs instanceof VerticalCRS) {
            return (VerticalCRS) crs;
        }
        if (crs instanceof CompoundCRS) {
            final CompoundCRS cp = (CompoundCRS) crs;
            for (final CoordinateReferenceSystem c : cp.getComponents()) {
                final VerticalCRS candidate = getVerticalCRS(c);
                if (candidate != null) {
                    return candidate;
                }
            }
        }
        if (crs instanceof GeographicCRS) {
            final CoordinateSystem cs = crs.getCoordinateSystem();
            if (cs.getDimension()  >= 3) {
                assert AxisDirections.indexOf(cs, AxisDirection.UP) >= 0 : cs;
                return DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;
            }
        }
        return null;
    }

    /**
     * Returns the first temporal coordinate reference system found in the given CRS,
     * or {@code null} if there is none.
     *
     * @param  crs The coordinate reference system, or {@code null}.
     * @return The temporal CRS, or {@code null} if none.
     *
     * @category information
     * @since 2.4
     */
    public static TemporalCRS getTemporalCRS(final CoordinateReferenceSystem crs) {
        if (crs instanceof TemporalCRS) {
            return (TemporalCRS) crs;
        }
        if (crs instanceof CompoundCRS) {
            final CompoundCRS cp = (CompoundCRS) crs;
            for (final CoordinateReferenceSystem c : cp.getComponents()) {
                final TemporalCRS candidate = getTemporalCRS(c);
                if (candidate != null) {
                    return candidate;
                }
            }
        }
        return null;
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
        final List<SingleCRS> actualComponents = DefaultCompoundCRS.getSingleCRS(crs);
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
     * Returns the coordinate reference system in the given range of dimension indices.
     * This method processes as below:
     * <p>
     * <ul>
     *   <li>If the given {@code crs} is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if {@code lower} is 0 and {@code upper} if the number of CRS dimensions,
     *       then this method returns the given CRS unchanged.</li>
     *   <li>Otherwise if the given CRS is an instance of {@link CompoundCRS}, then this method
     *       searches for a {@linkplain CompoundCRS#getComponents() component} where:
     *       <ul>
     *         <li>The {@linkplain CoordinateSystem#getDimension() number of dimensions} is
     *             equals to {@code upper - lower};</li>
     *         <li>The sum of the number of dimensions of all previous CRS is equals to
     *             {@code lower}.</li>
     *       </ul>
     *       If such component is found, then it is returned.</li>
     *   <li>Otherwise (i.e. no component match), this method returns {@code null}.</li>
     * </ul>
     * <p>
     * This method does <strong>not</strong> attempt to build new CRS from the components.
     * For example it does not attempt to create a 3D geographic CRS from a 2D one + a vertical
     * component. If such functionality is desired, consider using the utility methods in
     * {@link org.geotoolkit.referencing.factory.ReferencingFactoryContainer} instead.
     *
     * @param  crs   The coordinate reference system to decompose, or {@code null}.
     * @param  lower The first dimension to keep, inclusive.
     * @param  upper The last  dimension to keep, exclusive.
     * @return The sub-coordinate system, or {@code null} if the given {@code crs} was {@code null}
     *         or can't be decomposed for dimensions in the range {@code [lower..upper]}.
     * @throws IndexOutOfBoundsException If the given index are out of bounds.
     *
     * @see org.geotoolkit.referencing.factory.ReferencingFactoryContainer#separate(CoordinateReferenceSystem, int[])
     *
     * @since 3.16
     */
    public static CoordinateReferenceSystem getSubCRS(CoordinateReferenceSystem crs, int lower, int upper) {
        if (crs != null) {
            int dimension = crs.getCoordinateSystem().getDimension();
            if (lower < 0 || lower > upper || upper > dimension) {
                throw new IndexOutOfBoundsException(Errors.format(
                        Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, lower < 0 ? lower : upper));
            }
check:      while (lower != 0 || upper != dimension) {
                if (crs instanceof CompoundCRS) {
                    final List<CoordinateReferenceSystem> components=((CompoundCRS) crs).getComponents();
                    final int size = components.size();
                    for (int i=0; i<size; i++) {
                        crs = components.get(i);
                        dimension = crs.getCoordinateSystem().getDimension();
                        if (lower < dimension) {
                            // The requested dimensions may intersect the dimension of this CRS.
                            // The outer loop will perform the verification, and eventually go
                            // down again in the tree of sub-components.
                            continue check;
                        }
                        lower -= dimension;
                        upper -= dimension;
                    }
                }
                return null;
            }
        }
        return crs;
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
                                                  boolean lenient)
            throws FactoryException
    {
        if (equalsIgnoreMetadata(sourceCRS, targetCRS)) {
            // Slight optimization in order to avoid the overhead of loading the full referencing engine.
            return MathTransforms.identity(sourceCRS.getCoordinateSystem().getDimension());
        }
        ensureNonNull("sourceCRS", sourceCRS);
        ensureNonNull("targetCRS", targetCRS);
        CoordinateOperationFactory operationFactory = getCoordinateOperationFactory(lenient);
        return operationFactory.createOperation(sourceCRS, targetCRS).getMathTransform();
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
        return Envelopes.transform(transform, envelope, destination);
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
        return Envelopes.transform(operation, envelope, destination);
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
            throw new IllegalArgumentException(Errors.format(Errors.Keys.MISMATCHED_DIMENSION_$3,
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
        Logging.unexpectedException(CRS.class, methodName, exception);
    }
}

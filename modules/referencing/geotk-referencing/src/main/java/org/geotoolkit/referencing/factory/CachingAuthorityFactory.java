/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.referencing.factory;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import javax.measure.unit.Unit;
import java.lang.ref.WeakReference;
import java.awt.RenderingHints;

import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.InternationalString;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptor;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.lang.Buffered;
import org.geotoolkit.lang.Decorator;
import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.Exceptions;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.collection.Cache;


/**
 * An authority factory that caches all objects created by an other factory. All
 * {@code createFoo(String)} methods first looks if a previously created object
 * exists for the given code. If such an object exists, it is returned. Otherwise,
 * the object creation is delegated to the {@linkplain AbstractAuthorityFactory authority factory}
 * specified at creation time, and the result is cached in this factory.
 * <p>
 * Objects are cached by strong references, up to the amount of objects specified at
 * construction time. If a greater amount of objects are cached, the oldest ones will
 * be retained through a {@linkplain WeakReference weak reference} instead of a strong
 * one. This means that this caching factory will continue to returns them as long as
 * they are in use somewhere else in the Java virtual machine, but will be discarted
 * (and recreated on the fly if needed) otherwise.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@Buffered
@ThreadSafe(concurrent = true)
@Decorator(AbstractAuthorityFactory.class)
public class CachingAuthorityFactory extends AbstractAuthorityFactory {
    /**
     * The default value for {@link #maxStrongReferences}.
     */
    static final int DEFAULT_MAX = 100;

    /**
     * The underlying authority factory. This field may be {@code null} if this object was
     * created by the package-protected constructor. In this case, the subclass is responsible
     * for creating the backing store when {@link ThreadedAuthorityFactory#createBackingStore}
     * is invoked.
     *
     * @see #getBackingStore
     * @see ThreadedAuthorityFactory#createBackingStore
     */
    private final AbstractAuthorityFactory backingStore;

    /**
     * {@link Boolean#TRUE} if the backing store is available, {@link Boolean#FALSE} or a
     * {@link Throwable} if it is not available or {@code null} if this status has not yet
     * been determined.
     */
    private transient Object status;

    /**
     * The pool of cached objects. The keys are instances of {@link String} or {@link Pair}.
     */
    private final Cache<Object,Object> cache;

    /**
     * The pool of objects identified by {@link #find}. Every access to this pool
     * must be synchronized on {@code findPool}.
     */
    private final Map<IdentifiedObject,IdentifiedObject> findPool =
            new WeakHashMap<IdentifiedObject,IdentifiedObject>();

    /**
     * Constructs an instance wrapping the specified factory with a default number
     * of entries to keep by strong reference.
     * <p>
     * This constructor is protected because subclasses must declare which of the
     * {@link DatumAuthorityFactory}, {@link CSAuthorityFactory}, {@link CRSAuthorityFactory}
     * and {@link CoordinateOperationAuthorityFactory} interfaces they choose to implement.
     *
     * @param factory The factory to cache. Can not be {@code null}.
     */
    protected CachingAuthorityFactory(final AbstractAuthorityFactory factory) {
        this(factory, DEFAULT_MAX);
    }

    /**
     * Constructs an instance wrapping the specified factory. The {@code maxStrongReferences}
     * argument specify the maximum number of objects to keep by strong reference. If a greater
     * amount of objects are created, then the strong references for the eldest ones are replaced
     * by weak references.
     * <p>
     * This constructor is protected because subclasses must declare which of the
     * {@link DatumAuthorityFactory}, {@link CSAuthorityFactory}, {@link CRSAuthorityFactory}
     * and {@link CoordinateOperationAuthorityFactory} interfaces they choose to implement.
     *
     * @param factory The factory to cache. Can not be {@code null}.
     * @param maxStrongReferences The maximum number of objects to keep by strong reference.
     */
    protected CachingAuthorityFactory(final AbstractAuthorityFactory factory, final int maxStrongReferences) {
        super(factory);
        if (factory instanceof CachingAuthorityFactory) {
            LOGGER.warning("Factory is already caching."); // TODO: localize
        }
        backingStore = factory;
        ensureNotSmaller("maxStrongReferences", maxStrongReferences, 0);
        cache = new Cache<Object,Object>(20, maxStrongReferences, false);
        final Map<RenderingHints.Key, Object> hints = this.hints;
        if (factory instanceof DatumAuthorityFactory) {
            hints.put(Hints.DATUM_AUTHORITY_FACTORY, factory);
        }
        if (factory instanceof CSAuthorityFactory) {
            hints.put(Hints.CS_AUTHORITY_FACTORY, factory);
        }
        if (factory instanceof CRSAuthorityFactory) {
            hints.put(Hints.CRS_AUTHORITY_FACTORY, factory);
        }
        if (factory instanceof CoordinateOperationAuthorityFactory) {
            hints.put(Hints.COORDINATE_OPERATION_AUTHORITY_FACTORY, factory);
        }
    }

    /**
     * Constructs an instance without initial backing store. This constructor is for subclass
     * constructors only. Subclasses are responsible for creating an appropriate backing store
     * when the {@link ThreadedAuthorityFactory#createBackingStore} method is invoked.
     *
     * @param userHints An optional set of hints, or {@code null} for the default ones.
     * @param maxStrongReferences The maximum number of objects to keep by strong reference.
     *
     * @see ThreadedAuthorityFactory#createBackingStore
     */
    CachingAuthorityFactory(final Hints userHints, final int maxStrongReferences) {
        super(userHints);
        ensureNotSmaller("maxStrongReferences", maxStrongReferences, 0);
        cache = new Cache<Object,Object>(20, maxStrongReferences, false);
        backingStore = null;
    }

    /**
     * Ensures that the given value is equals or greater than the given minimum.
     */
    static void ensureNotSmaller(final String name, final int value, final int minimum) {
        if (value < minimum) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, name, value));
        }
    }

    /**
     * Returns the direct dependencies. The returned list contains the backing store
     * specified at construction time, or the exception if it can't be obtained.
     * <p>
     * Note that this method releases the factory immediately, so the factory contained in
     * the returned set may be invalid. This is acceptable if the factory is used only for
     * checking its type and getting its list of authority citations, which are the only
     * operations performed by {@link FactoryDependencies} (the only user of this method).
     * If nevertheless the returned factory is used, it should be safe because of the
     * synchronization lock in our EPSG factory implementations, while sub-optimal because
     * of the exclusive lock.
     */
    @Override
    final Collection<? super AuthorityFactory> dependencies() {
        Object dependency;
        try {
            final AbstractAuthorityFactory factory = getBackingStore();
            release(); // See javadoc comment.
            dependency = factory;
        } catch (FactoryException e) {
            dependency = e;
        }
        return Collections.singleton(dependency);
    }

    /**
     * Returns the backing store authority factory. The returned backing store must be thread-safe.
     * This method shall be used together with {@link #release} in a {@code try ... finally} block.
     *
     * {@note The default implementation is not synchronized because <code>backingStore</code> is
     *        already initialized at construction time and will not change, except for being set
     *        to the <code>null</code> value which is safe. However subclasses will synchronize
     *        this method.}
     *
     * @return The backing store to use in {@code createXXX(...)} methods.
     * @throws FactoryException if the creation of backing store failed.
     */
    AbstractAuthorityFactory getBackingStore() throws FactoryException {
        final AbstractAuthorityFactory backingStore = this.backingStore; // Protect from changes.
        if (backingStore == null) {
            throw new FactoryException(Errors.format(Errors.Keys.DISPOSED_FACTORY));
        }
        return backingStore;
    }

    /**
     * Releases the backing store previously obtained with {@link #getBackingStore}. The default
     * implementation does nothing. But if a subclass has overriden the {@code getBackingStore}
     * method, then it must override this method as well.
     */
    void release() {
    }

    /**
     * Returns whatever this factory is available. The factory is considered unavailable if it has
     * been {@linkplain #dispose disposed}, or if no backing store was specified at construction
     * time and {@link ThreadedAuthorityFactory#createBackingStore} threw an exception.
     *
     * @since 3.03
     */
    @Override
    public synchronized ConformanceResult availability() {
        Object s = status;
        if (s == null) {
            AbstractAuthorityFactory factory = null;
            try {
                factory = getBackingStore();
                try {
                    final ConformanceResult result = factory.availability();
                    if (result instanceof Availability) {
                        s = ((Availability) result).getFailureCause(); // May be null.
                    }
                    if (s == null) {
                        s = result.pass();
                    }
                    // Note that we can not let "result" escape from this scope,
                    // because we are going to release the backing store below.
                } finally {
                    release();
                }
            } catch (FactoryException exception) {
                unavailable(exception, factory);
                s = exception;
            }
            status = s;
        }
        if (s instanceof Throwable) {
            return new Availability((Throwable) s);
        }
        return new Availability() {
            @Override public boolean pass() {
                synchronized (CachingAuthorityFactory.class) {
                    return Boolean.TRUE.equals(status) && super.pass();
                }
            }
        };
    }

    /**
     * Invoked when this factory has been determined unavailable.
     * This method logs a message with the given exception.
     *
     * @param exception The exception thrown in our attempt to use the backing store.
     * @param factory The backing store, or {@code null} if we failed to get it.
     */
    final void unavailable(final FactoryException exception, final AbstractAuthorityFactory factory) {
        assert Thread.holdsLock(this);
        final Level level;
        if (exception instanceof NoSuchFactoryException) {
            /*
             * The factory is not available. This is error may be normal; it happens for
             * example if no geotk-epsg.jar (or similar JAR) are found on the classpath.
             */
            level = Level.CONFIG;
        } else {
            /*
             * The factory creation failed for an other reason, which may be more serious.
             */
            level = Level.WARNING;
        }
        final Citation citation;
        if (factory != null) {
            citation = factory.getAuthority();
        } else {
            citation = getAuthority(getClass());
        }
        InternationalString title = null;
        if (citation != null) {
            title = citation.getTitle();
            final Collection<? extends InternationalString> titles = citation.getAlternateTitles();
            if (titles != null) {
                for (final InternationalString candidate : titles) {
                    /*
                     * Uses the longuest title instead of the main one. In Geotk
                     * implementation, the alternate title may contains usefull informations
                     * like the EPSG database version number and the database engine.
                     */
                    if (title == null || candidate.length() > title.length()) {
                        title = candidate;
                    }
                }
            }
        }
        if (title == null) {
            title = Vocabulary.formatInternational(Vocabulary.Keys.UNTITLED);
        }
        final LogRecord record = new LogRecord(level, Exceptions.formatChainedMessages(Loggings.getResources(null).
                getString(Loggings.Keys.UNAVAILABLE_AUTHORITY_FACTORY_$1, title), exception));
        record.setSourceClassName(getClass().getCanonicalName());
        record.setSourceMethodName("isAvailable");
        record.setThrown(exception);
        record.setLoggerName(LOGGER.getName());
        LOGGER.log(record);
    }

    /**
     * If this factory is a wrapper for the specified factory that do not add any additional
     * {@linkplain #getAuthorityCodes authority codes}, returns {@code true}. This method is
     * for {@link FallbackAuthorityFactory} internal use only and should not be public. A
     * cheap test without {@link #getBackingStore} invocation is suffisient for our needs.
     */
    @Override
    final boolean sameAuthorityCodes(final AuthorityFactory factory) {
        final AbstractAuthorityFactory backingStore = this.backingStore; // Protect from changes.
        if (backingStore != null && backingStore.sameAuthorityCodes(factory)) {
            return true;
        }
        return super.sameAuthorityCodes(factory);
    }

    /**
     * Returns the vendor or the authority, or {@code null} if the information is not available.
     *
     * @param  method Either {@code "getAuthority"} or {@code "getVendor"}.
     * @return The authority or the vendor, or {@code null}.
     *
     * @see #isAvailable()
     */
    @Override
    final Citation getCitation(final String method) {
        if (availability().pass()) try {
            final AbstractAuthorityFactory factory = getBackingStore();
            try {
                return getCitation(factory, method);
            } finally {
                release();
            }
        } catch (FactoryException e) {
            Logging.recoverableException(LOGGER, CachingAuthorityFactory.class, method, e);
        }
        return super.getCitation(method);
    }

    /**
     * Returns the vendor responsible for creating the underlying factory implementation.
     */
    @Override
    public Citation getVendor() {
        return getCitation("getVendor");
    }

    /**
     * Returns the organization or party responsible for definition and maintenance of the
     * underlying database.
     */
    @Override
    public Citation getAuthority() {
        return getCitation("getAuthority");
    }

    /**
     * Returns a description of the underlying backing store, or {@code null} if unknow.
     * This is for example the database software used for storing the data.
     *
     * @throws FactoryException if a failure occured while fetching the engine description.
     */
    @Override
    public String getBackingStoreDescription() throws FactoryException {
        final AbstractAuthorityFactory factory = getBackingStore();
        try {
            return factory.getBackingStoreDescription();
        } finally {
            release();
        }
    }

    /**
     * Returns the set of authority codes of the given type. The {@code type}
     * argument specifies the base class.
     *
     * @param  type The spatial reference objects type.
     * @return The set of authority codes for spatial reference objects of the given type.
     *         If this factory doesn't contains any object of the given type, then this method
     *         returns an {@linkplain java.util.Collections#EMPTY_SET empty set}.
     * @throws FactoryException if access to the underlying database failed.
     */
    @Override
    public Set<String> getAuthorityCodes(final Class<? extends IdentifiedObject> type)
            throws FactoryException
    {
        final AbstractAuthorityFactory factory = getBackingStore();
        try {
            return factory.getAuthorityCodes(type);
            /*
             * In the particular case of EPSG factory, the returned Set maintains a live
             * connection to the database. But it still okay to release the factory anyway
             * because our implementation will really closes the connection only when the
             * iteration is over or the iterator has been garbage-collected.
             */
        } finally {
            release();
        }
    }

    /**
     * Gets a description of the object corresponding to a code.
     *
     * @param  code Value allocated by authority.
     * @return A description of the object, or {@code null} if the object
     *         corresponding to the specified {@code code} has no description.
     * @throws NoSuchAuthorityCodeException if the specified {@code code} was not found.
     * @throws FactoryException if the query failed for some other reason.
     */
    @Override
    public InternationalString getDescriptionText(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        final AbstractAuthorityFactory factory = getBackingStore();
        try {
            return factory.getDescriptionText(code);
        } finally {
            release();
        }
    }

    /**
     * Returns an object from a code using the given proxy. This method first look in the
     * cache. If no object exists in the cache for the given code, then a lock is created
     * and the object creation is delegated to the {@linkplain #getBackingStore backing store}.
     * The result is then stored in the cache and returned.
     *
     * @param  <T>   The type of the object to be returned.
     * @param  proxy The proxy to use for creating the object.
     * @param  code  The code of the object to create.
     * @return The object extracted from the cache or created.
     * @throws FactoryException If an error occured while creating the object.
     */
    private <T> T create(final AbstractAuthorityFactoryProxy<T> proxy, final String code)
            throws FactoryException
    {
        final Class<T> type = proxy.type;
        final String key = trimAuthority(code);
        Object value = cache.peek(key);
        if (!type.isInstance(value)) {
            final Cache.Handler<Object> handler = cache.lock(key);
            try {
                value = handler.peek();
                if (!type.isInstance(value)) {
                    final T result;
                    final AbstractAuthorityFactory factory = getBackingStore();
                    try {
                        result = proxy.create(factory, code);
                    } finally {
                        release();
                    }
                    value = result; // For the finally block below.
                    return result;
                }
            } finally {
                handler.putAndUnlock(value);
            }
        }
        return type.cast(value);
    }

    /**
     * Returns an arbitrary object from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public IdentifiedObject createObject(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.OBJECT, code);
    }

    /**
     * Returns an arbitrary datum from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Datum createDatum(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.DATUM, code);
    }

    /**
     * Returns an engineering datum from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public EngineeringDatum createEngineeringDatum(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.ENGINEERING_DATUM, code);
    }

    /**
     * Returns an image datum from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public ImageDatum createImageDatum(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.IMAGE_DATUM, code);
    }

    /**
     * Returns a vertical datum from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public VerticalDatum createVerticalDatum(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.VERTICAL_DATUM, code);
    }

    /**
     * Returns a temporal datum from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public TemporalDatum createTemporalDatum(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.TEMPORAL_DATUM, code);
    }

    /**
     * Returns a geodetic datum from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public GeodeticDatum createGeodeticDatum(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.GEODETIC_DATUM, code);
    }

    /**
     * Returns an ellipsoid from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Ellipsoid createEllipsoid(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.ELLIPSOID, code);
    }

    /**
     * Returns a prime meridian from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public PrimeMeridian createPrimeMeridian(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.PRIME_MERIDIAN, code);
    }

    /**
     * Returns an extent (usually an area of validity) from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Extent createExtent(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.EXTENT, code);
    }

    /**
     * Returns an arbitrary coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateSystem createCoordinateSystem(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.COORDINATE_SYSTEM, code);
    }

    /**
     * Returns a cartesian coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CartesianCS createCartesianCS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.CARTESIAN_CS, code);
    }

    /**
     * Returns a polar coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public PolarCS createPolarCS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.POLAR_CS, code);
    }

    /**
     * Returns a cylindrical coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CylindricalCS createCylindricalCS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.CYLINDRICAL_CS, code);
    }

    /**
     * Returns a spherical coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public SphericalCS createSphericalCS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.SPHERICAL_CS, code);
    }

    /**
     * Returns an ellipsoidal coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public EllipsoidalCS createEllipsoidalCS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.ELLIPSOIDAL_CS, code);
    }

    /**
     * Returns a vertical coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public VerticalCS createVerticalCS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.VERTICAL_CS, code);
    }

    /**
     * Returns a temporal coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public TimeCS createTimeCS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.TIME_CS, code);
    }

    /**
     * Returns a coordinate system axis from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateSystemAxis createCoordinateSystemAxis(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.AXIS, code);
    }

    /**
     * Returns an unit from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Unit<?> createUnit(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.UNIT, code);
    }

    /**
     * Returns an arbitrary coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateReferenceSystem createCoordinateReferenceSystem(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.CRS, code);
    }

    /**
     * Returns a 3D coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CompoundCRS createCompoundCRS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.COMPOUND_CRS, code);
    }

    /**
     * Returns a derived coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public DerivedCRS createDerivedCRS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.DERIVED_CRS, code);
    }

    /**
     * Returns an engineering coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public EngineeringCRS createEngineeringCRS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.ENGINEERING_CRS, code);
    }

    /**
     * Returns a geographic coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public GeographicCRS createGeographicCRS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.GEOGRAPHIC_CRS, code);
    }

    /**
     * Returns a geocentric coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public GeocentricCRS createGeocentricCRS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.GEOCENTRIC_CRS, code);
    }

    /**
     * Returns an image coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public ImageCRS createImageCRS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.IMAGE_CRS, code);
    }

    /**
     * Returns a projected coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public ProjectedCRS createProjectedCRS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.PROJECTED_CRS, code);
    }

    /**
     * Returns a temporal coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public TemporalCRS createTemporalCRS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.TEMPORAL_CRS, code);
    }

    /**
     * Returns a vertical coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public VerticalCRS createVerticalCRS(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.VERTICAL_CRS, code);
    }

    /**
     * Returns a parameter descriptor from a code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @since 2.2
     */
    @Override
    public ParameterDescriptor<?> createParameterDescriptor(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.PARAMETER, code);
    }

    /**
     * Returns an operation method from a code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @since 2.2
     */
    @Override
    public OperationMethod createOperationMethod(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.METHOD, code);
    }

    /**
     * Returns an operation from a single operation code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @since 2.2
     */
    @Override
    public CoordinateOperation createCoordinateOperation(final String code) throws FactoryException {
        return create(AbstractAuthorityFactoryProxy.OPERATION, code);
    }

    /**
     * Returns an operation from coordinate reference system codes.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @since 2.2
     */
    @Override
    public Set<CoordinateOperation> createFromCoordinateReferenceSystemCodes(
            final String sourceCRS, final String targetCRS) throws FactoryException
    {
        final CodePair key = new CodePair(trimAuthority(sourceCRS), trimAuthority(targetCRS));
        Object value = cache.peek(key);
        if (!(value instanceof Set<?>)) {
            final Cache.Handler<Object> handler = cache.lock(key);
            try {
                value = handler.peek();
                if (!(value instanceof Set<?>)) {
                    final AbstractAuthorityFactory factory = getBackingStore();
                    try {
                        final Set<CoordinateOperation> result = Collections.unmodifiableSet(
                                factory.createFromCoordinateReferenceSystemCodes(sourceCRS, targetCRS));
                        value = result;
                        return result;
                    } finally {
                        release();
                    }
                }
            } finally {
                handler.putAndUnlock(value);
            }
        }
        @SuppressWarnings({"unchecked", "rawtypes"})
        final Set<CoordinateOperation> result = (Set) value;
        return result;
    }

    /**
     * A pair of codes for operations to cache with
     * {@link #createFromCoordinateReferenceSystemCodes}.
     */
    private static final class CodePair {
        private final String source, target;

        public CodePair(final String source, final String target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public int hashCode() {
            int code = 0;
            if (source != null) code  = source.hashCode();
            if (target != null) code += target.hashCode() * 31;
            return code;
        }

        @Override
        public boolean equals(final Object other) {
            if (other instanceof CodePair) {
                final CodePair that = (CodePair) other;
                return Utilities.equals(this.source, that.source) &&
                       Utilities.equals(this.target, that.target);
            }
            return false;
        }

        @Override
        public String toString() {
            return source + " \u21E8 " + target;
        }
    }

    /**
     * Returns a finder which can be used for looking up unidentified objects.
     * The default implementation delegates lookup to the underlying backing
     * store and caches the result.
     *
     * @throws FactoryException if the finder can not be created.
     *
     * @since 2.4
     */
    @Override
    public IdentifiedObjectFinder getIdentifiedObjectFinder(
            final Class<? extends IdentifiedObject> type) throws FactoryException
    {
        final AbstractAuthorityFactory factory = getBackingStore();
        try {
            return new Finder(factory.getIdentifiedObjectFinder(type));
        } finally {
            /*
             * We are cheating a bit here since the factory may still in use. However our
             * backing store implementations are synchronized.  We may suffer from thread
             * contentions, but hopefully not much more problem than that.
             */
            release();
        }
    }

    /**
     * An implementation of {@link IdentifiedObjectFinder} which delegates
     * the work to the underlying backing store and caches the result.
     * <p>
     * <b>Implementation note:</b> we will create objects using directly the underlying backing
     * store, not using the cache. This is because hundred of objects may be created during a
     * scan while only one will be typically retained. We don't want to overload the cache with
     * every false candidates that we encounter during the scan.
     */
    private final class Finder extends IdentifiedObjectFinder.Adapter {
        /**
         * Creates a finder for the underlying backing store.
         */
        Finder(final IdentifiedObjectFinder finder) {
            super(finder);
        }

        /**
         * Looks up an object from this authority factory which is equal, ignoring metadata,
         * to the specified object. The default implementation performs the same lookup than
         * the backing store and caches the result.
         */
        @Override
        public IdentifiedObject find(final IdentifiedObject object) throws FactoryException {
            /*
             * Do not synchronize on 'CachingAuthorityFactory.this'. This method may take a
             * while to execute and we don't want to block other threads. The synchronizations
             * in the 'create' methods and in the 'findPool' map should be suffisient.
             *
             * TODO: avoid to search for the same object twice. For now we consider that this
             *       is not a big deal if the same object is searched twice; it is "just" a
             *       waste of CPU.
             */
            IdentifiedObject candidate;
            synchronized (findPool) {
                candidate = findPool.get(object);
            }
            if (candidate == null) {
                // Must delegates to 'finder' (not to 'super') in order to take
                // advantage of the method overriden by AllAuthoritiesFactory.
                candidate = finder.find(object);
                if (candidate != null) {
                    synchronized (findPool) {
                        findPool.put(object, candidate);
                    }
                }
            }
            return candidate;
        }

        /**
         * Returns the identifier for the specified object.
         */
        @Override
        public String findIdentifier(final IdentifiedObject object) throws FactoryException {
            IdentifiedObject candidate;
            synchronized (findPool) {
                candidate = findPool.get(object);
            }
            if (candidate != null) {
                return getIdentifier(candidate);
            }
            // We don't rely on super-class implementation, because we want to
            // take advantage of the method overriden by AllAuthoritiesFactory.
            return finder.findIdentifier(object);
        }
    }

    /**
     * {@code true} if different values may be assigned to the same key. This is usually
     * an error, so the default {@link Cache} behavior is to thrown an exception in such
     * case. However in some cases we may want to relax this check. For example the EPSG
     * database sometime assign the same key to different kind of objects.
     *
     * @param allowed {@code true} if key collisions are allowed.
     *
     * @see Cache#setKeyCollisionAllowed
     */
    protected void setKeyCollisionAllowed(final boolean allowed) {
        cache.setKeyCollisionAllowed(allowed);
    }

    /**
     * Releases resources immediately instead of waiting for the garbage collector.
     *
     * @param shutdown {@code false} for normal disposal, or {@code true} if
     *        this method is invoked during the process of a JVM shutdown.
     */
    @Override
    protected synchronized void dispose(final boolean shutdown) {
        cache.clear();
        final AbstractAuthorityFactory factory = backingStore;
        if (factory != null) {
            factory.dispose(shutdown);
        }
        super.dispose(shutdown);
    }
}

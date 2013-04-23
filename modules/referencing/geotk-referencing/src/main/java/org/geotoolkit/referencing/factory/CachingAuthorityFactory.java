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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.factory;

import java.util.Set;
import java.util.Map;
import java.util.EnumMap;
import java.util.Collection;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.Objects;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import javax.measure.unit.Unit;
import java.lang.ref.WeakReference;
import java.awt.RenderingHints;
import java.io.PrintWriter;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptor;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.lang.Debug;
import org.geotoolkit.lang.Buffered;
import org.geotoolkit.lang.Decorator;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.Exceptions;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.util.logging.Logging;
import org.apache.sis.util.collection.Cache;
import org.geotoolkit.util.collection.XCollections;
import org.geotoolkit.internal.referencing.NilReferencingObject;


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
 * they are in use somewhere else in the Java virtual machine, but will be discarded
 * (and recreated on the fly if needed) otherwise.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.14
 *
 * @since 2.1
 * @module
 */
@Buffered
@ThreadSafe
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
    private transient volatile Object status;

    /**
     * The authority, cached in order to avoid the synchronization performed by
     * {@link Availability#pass()}. Profiling show that it was a contention point.
     *
     * @since 3.14
     */
    private transient volatile Citation authority;

    /**
     * The pool of cached objects. The keys are instances of {@link Key} or {@link CodePair}.
     */
    private final Cache<Object,Object> cache;

    /**
     * The pool of objects identified by {@link #find} for each comparison modes. Values may be
     * {@link NilReferencingObject} if an object has been searched but has not been found.
     * <p>
     * Every access to this pool must be synchronized on {@code findPool}.
     */
    @GuardedBy("self")
    private final Map<IdentifiedObject, Map<ComparisonMode,IdentifiedObject>> findPool = new WeakHashMap<>();

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
        cache = new Cache<>(20, maxStrongReferences, false);
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
        cache = new Cache<>(20, maxStrongReferences, false);
        backingStore = null;
    }

    /**
     * Ensures that the given value is equals or greater than the given minimum.
     */
    static void ensureNotSmaller(final String name, final int value, final int minimum) {
        if (value < minimum) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_2, name, value));
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
     * @return The backing store to use in {@code createXXX(...)} methods.
     * @throws FactoryException if the creation of backing store failed.
     */
    AbstractAuthorityFactory getBackingStore() throws FactoryException {
        final AbstractAuthorityFactory backingStore = this.backingStore;
        if (backingStore == null) {
            throw new FactoryException(Errors.format(Errors.Keys.DISPOSED_FACTORY));
        }
        return backingStore;
    }

    /**
     * Releases the backing store previously obtained with {@link #getBackingStore}. The default
     * implementation does nothing. But if a subclass has overridden the {@code getBackingStore}
     * method, then it must override this method as well.
     */
    void release() {
    }

    /**
     * Returns {@code true} if {@link #availability()} has determined that this factory is not
     * available. If this factory <strong>may</strong> be available, then this method returns
     * {@code false}.
     *
     * @since 3.16
     */
    final boolean unavailable() {
        final Object s = status;
        return (s != null) && !Boolean.TRUE.equals(s);
    }

    /**
     * Returns whatever this factory is available. The factory is considered unavailable if it has
     * been {@linkplain #dispose disposed}, or if no backing store was specified at construction
     * time and {@link ThreadedAuthorityFactory#createBackingStore} threw an exception.
     *
     * @since 3.03
     */
    @Override
    public ConformanceResult availability() {
        /*
         * We do not synchronize since the "getBackingStore() ... release()" pattern
         * is thread-safe. It is not a big deal if the status is determined twice by
         * two concurrent threads. It should not happen anyway since this method is
         * usually invoked (indirectly) by FactoryFinder, which is synchronized. It
         * is much better to avoid synchronization (if not strictly necessary) in
         * order to avoid dead-lock issues.
         */
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
            @Override public Boolean pass() {
                return Boolean.TRUE.equals(status) && super.pass();
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
                     * Uses the longest title instead of the main one. In Geotk
                     * implementation, the alternate title may contains useful informations
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
        final LogRecord record = new LogRecord(level, Exceptions.formatChainedMessages(null, Loggings.getResources(null).
                getString(Loggings.Keys.UNAVAILABLE_AUTHORITY_FACTORY_1, title), exception));
        record.setSourceClassName(getClass().getCanonicalName());
        record.setSourceMethodName("availability");
        record.setThrown(exception);
        record.setLoggerName(LOGGER.getName());
        LOGGER.log(record);
    }

    /**
     * If this factory is a wrapper for the specified factory that do not add any additional
     * {@linkplain #getAuthorityCodes authority codes}, returns {@code true}. This method is
     * for {@link FallbackAuthorityFactory} internal use only and should not be public. A
     * cheap test without {@link #getBackingStore} invocation is sufficient for our needs.
     */
    @Override
    final boolean sameAuthorityCodes(final AuthorityFactory factory) {
        final AbstractAuthorityFactory backingStore = this.backingStore;
        if (backingStore != null && backingStore.sameAuthorityCodes(factory)) {
            return true;
        }
        return super.sameAuthorityCodes(factory);
    }

    /**
     * Returns the vendor or the authority, or {@code UNKNOWN} if the information is not available.
     *
     * @param  method Either {@code "getAuthority"} or {@code "getVendor"}.
     * @return The authority or the vendor, or {@code UNKNOWN}.
     *
     * @see #availability
     * @see org.geotoolkit.metadata.iso.citation.Citations#UNKNOWN
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
            /*
             * Log the exception as an unexpected one, because the caller should have
             * invoked this method only after having tested the factory availability.
             */
            Logging.unexpectedException(LOGGER, CachingAuthorityFactory.class, method, e);
        }
        return super.getCitation(method);
    }

    /**
     * Returns the vendor responsible for creating the underlying factory implementation.
     * This method should be invoked only if this factory is {@linkplain #availability()
     * available}.
     */
    @Override
    public Citation getVendor() {
        return getCitation("getVendor");
    }

    /**
     * Returns the organization or party responsible for definition and maintenance of the
     * underlying database. This method should be invoked only if this factory is
     * {@linkplain #availability() available}.
     */
    @Override
    public Citation getAuthority() {
        Citation authority = this.authority;
        if (authority == null) {
            authority = getCitation("getAuthority");
            if (Boolean.TRUE.equals(status)) {
                // Cache only in case of success. If we failed, we
                // will try again next time this method is invoked.
                this.authority = authority;
            }
        }
        return authority;
    }

    /**
     * Returns a description of the underlying backing store, or {@code null} if unknown.
     * This is for example the database software used for storing the data.
     *
     * @throws FactoryException if a failure occurred while fetching the engine description.
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
     * The key objects to use in the {@link CachingAuthorityFactory#cache}.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-2">GEOTK-2</a>
     *
     * @since 3.17
     */
    private static final class Key {
        /** The type of the cached object.    */ final Class<?> type;
        /** The cached object authority code. */ final String code;

        /** Creates a new key for the given type and code. */
        Key(final Class<?> type, final String code) {
            this.type = type;
            this.code = code;
        }

        /** Returns the hash code value for this key. */
        @Override public int hashCode() {
            return type.hashCode() ^ code.hashCode();
        }

        /** Compares this key with the given object for equality .*/
        @Override public boolean equals(final Object other) {
            if (other instanceof Key) {
                final Key that = (Key) other;
                return Objects.equals(type, that.type) &&
                       Objects.equals(code, that.code);
            }
            return false;
        }

        /** String representation used by {@link CacheRecord}. */
        @Override public String toString() {
            final StringBuilder buffer = new StringBuilder("Key[").append(code);
            if (buffer.length() > 15) { // Arbitrary limit in string length.
                buffer.setLength(15);
                buffer.append('\u2026');
            }
            return buffer.append(" : ").append(type.getSimpleName()).append(']').toString();
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
     * @throws FactoryException If an error occurred while creating the object.
     */
    private <T> T create(final AuthorityFactoryProxy<T> proxy, final String code)
            throws FactoryException
    {
        final Class<T> type = proxy.type;
        final Key key = new Key(type, trimAuthority(code));
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
        return create(AuthorityFactoryProxy.OBJECT, code);
    }

    /**
     * Returns an arbitrary datum from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Datum createDatum(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.DATUM, code);
    }

    /**
     * Returns an engineering datum from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public EngineeringDatum createEngineeringDatum(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.ENGINEERING_DATUM, code);
    }

    /**
     * Returns an image datum from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public ImageDatum createImageDatum(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.IMAGE_DATUM, code);
    }

    /**
     * Returns a vertical datum from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public VerticalDatum createVerticalDatum(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.VERTICAL_DATUM, code);
    }

    /**
     * Returns a temporal datum from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public TemporalDatum createTemporalDatum(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.TEMPORAL_DATUM, code);
    }

    /**
     * Returns a geodetic datum from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public GeodeticDatum createGeodeticDatum(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.GEODETIC_DATUM, code);
    }

    /**
     * Returns an ellipsoid from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Ellipsoid createEllipsoid(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.ELLIPSOID, code);
    }

    /**
     * Returns a prime meridian from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public PrimeMeridian createPrimeMeridian(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.PRIME_MERIDIAN, code);
    }

    /**
     * Returns an extent (usually an area of validity) from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Extent createExtent(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.EXTENT, code);
    }

    /**
     * Returns an arbitrary coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateSystem createCoordinateSystem(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.COORDINATE_SYSTEM, code);
    }

    /**
     * Returns a Cartesian coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CartesianCS createCartesianCS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.CARTESIAN_CS, code);
    }

    /**
     * Returns a polar coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public PolarCS createPolarCS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.POLAR_CS, code);
    }

    /**
     * Returns a cylindrical coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CylindricalCS createCylindricalCS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.CYLINDRICAL_CS, code);
    }

    /**
     * Returns a spherical coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public SphericalCS createSphericalCS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.SPHERICAL_CS, code);
    }

    /**
     * Returns an ellipsoidal coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public EllipsoidalCS createEllipsoidalCS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.ELLIPSOIDAL_CS, code);
    }

    /**
     * Returns a vertical coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public VerticalCS createVerticalCS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.VERTICAL_CS, code);
    }

    /**
     * Returns a temporal coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public TimeCS createTimeCS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.TIME_CS, code);
    }

    /**
     * Returns a coordinate system axis from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateSystemAxis createCoordinateSystemAxis(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.AXIS, code);
    }

    /**
     * Returns an unit from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Unit<?> createUnit(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.UNIT, code);
    }

    /**
     * Returns an arbitrary coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateReferenceSystem createCoordinateReferenceSystem(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.CRS, code);
    }

    /**
     * Returns a 3D coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CompoundCRS createCompoundCRS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.COMPOUND_CRS, code);
    }

    /**
     * Returns a derived coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public DerivedCRS createDerivedCRS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.DERIVED_CRS, code);
    }

    /**
     * Returns an engineering coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public EngineeringCRS createEngineeringCRS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.ENGINEERING_CRS, code);
    }

    /**
     * Returns a geographic coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public GeographicCRS createGeographicCRS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.GEOGRAPHIC_CRS, code);
    }

    /**
     * Returns a geocentric coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public GeocentricCRS createGeocentricCRS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.GEOCENTRIC_CRS, code);
    }

    /**
     * Returns an image coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public ImageCRS createImageCRS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.IMAGE_CRS, code);
    }

    /**
     * Returns a projected coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public ProjectedCRS createProjectedCRS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.PROJECTED_CRS, code);
    }

    /**
     * Returns a temporal coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public TemporalCRS createTemporalCRS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.TEMPORAL_CRS, code);
    }

    /**
     * Returns a vertical coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public VerticalCRS createVerticalCRS(final String code) throws FactoryException {
        return create(AuthorityFactoryProxy.VERTICAL_CRS, code);
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
        return create(AuthorityFactoryProxy.PARAMETER, code);
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
        return create(AuthorityFactoryProxy.METHOD, code);
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
        return create(AuthorityFactoryProxy.OPERATION, code);
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
                        final Set<CoordinateOperation> result = XCollections.unmodifiableSet(
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
                return Objects.equals(this.source, that.source) &&
                       Objects.equals(this.target, that.target);
            }
            return false;
        }

        /** String representation used by {@link CacheRecord}. */
        @Override
        public String toString() {
            return "CodePair[" + source + " \u21E8 " + target + ']';
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
        return new Finder(type);
    }

    /**
     * An implementation of {@link IdentifiedObjectFinder} which delegates
     * the work to the underlying backing store and caches the result.
     *
     * {@section Implementation note}
     * we will create objects using directly the underlying backing store, not using the cache.
     * This is because hundred of objects may be created during a scan while only one will be
     * typically retained. We don't want to flood the cache with every false candidates that we
     * encounter during the scan.
     *
     * {section Synchronization note}
     * our public API claims that {@link IdentifiedObjectFinder}s are not thread-safe. Nevertheless
     * we synchronize this particular implementation for safety, because the consequence of misuse
     * are more dangerous than other implementations. Furthermore this is also a way to assert that
     * no code path go to the {@link #create(AuthorityFactoryProxy, String)} method from a
     * non-overridden public method.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.18
     *
     * @since 2.4
     * @module
     */
    @ThreadSafe
    private final class Finder extends IdentifiedObjectFinder {
        /**
         * The finder on which to delegate the work. This is acquired by {@link #acquire()}
         * <strong>and must be released</strong> by call to {@link #release()} once finished.
         */
        private transient IdentifiedObjectFinder finder;

        /**
         * Number of time that {@link #acquire()} has been invoked. When
         * this count reaches zero, the {@linkplain #finder} is released.
         */
        private transient int acquireCount;

        /**
         * Creates a finder for the given type of objects.
         */
        Finder(final Class<? extends IdentifiedObject> type) {
            super(null, type);
        }

        /**
         * Acquires a new {@linkplain #finder}. The {@link #release()} method must
         * be invoked in a {@code finally} block after the call to {@code acquire}.
         * The pattern must be as below (note that the call to {@code acquire()} is
         * inside the {@code try} block):
         *
         * {@preformat java
         *     try {
         *         acquire();
         *         (finder or proxy).doSomeStuff();
         *     } finally {
         *         release();
         *     }
         * }
         */
        private void acquire() throws FactoryException {
            assert Thread.holdsLock(this);
            assert (acquireCount == 0) == (finder == null) : acquireCount;
            if (acquireCount == 0) {
                final AbstractAuthorityFactory factory = getBackingStore();
                /*
                 * Set 'acquireCount' only after we succeed in fetching the factory, and before
                 * any operation on it.  The intend is to get CachingAuthorityFactory.release()
                 * invoked if and only if the 'getBackingStore()' method succeed, no matter what
                 * happen after this point.
                 */
                acquireCount = 1;
                finder = factory.getIdentifiedObjectFinder(getObjectType());
                finder.copyConfiguration(this);
                finder.setParent(this);
            } else {
                acquireCount++;
            }
        }

        /**
         * Releases the {@linkplain #finder}.
         */
        private void release() {
            assert Thread.holdsLock(this);
            if (acquireCount == 0) {
                // May happen only if a failure occurred during getBackingStore() execution.
                return;
            }
            if (--acquireCount == 0) {
                finder = null;
                CachingAuthorityFactory.this.release();
            }
        }

        /**
         * Returns the authority of the factory examined by this finder.
         */
        @Override
        public synchronized Citation getAuthority() throws FactoryException {
            try {
                acquire();
                return finder.getAuthority();
            } finally {
                release();
            }
        }

        /**
         * Returns a set of authority codes that <strong>may</strong> identify the same
         * object than the specified one. This method delegates to the backing finder.
         */
        @Override
        protected synchronized Set<String> getCodeCandidates(final IdentifiedObject object)
                throws FactoryException
        {
            try {
                acquire();
                return finder.getCodeCandidates(object);
            } finally {
                release();
            }
        }

        /**
         * Creates an object from the given code. This method must delegate to the wrapped finder.
         * We may be tempted to not delegate and instead make use of the caching services at this
         * point, but such approach conflicts with {@link AuthorityFactoryAdapter} work. The later
         * (or to be more accurate, {@link OrderedAxisAuthorityFactory}) expects axes in (latitude,
         * longitude) order first, in order to test this CRS before to switch to the opposite order
         * and test again. If the {@link CachingAuthorityFactory} cache is used, we get directly
         * (longitude,latitude) order and miss an opportunity to identify the user's CRS.
         */
        @Override
        protected final synchronized IdentifiedObject create(final String code, final int attempt)
                throws FactoryException
        {
            try {
                acquire();
                return finder.create(code, attempt);
            } finally {
                release();
            }
        }

        /**
         * Looks up an object from this authority factory which is equal, ignoring metadata,
         * to the specified object. The default implementation performs the same lookup than
         * the backing store and caches the result.
         */
        @Override
        public IdentifiedObject find(final IdentifiedObject object) throws FactoryException {
            final ComparisonMode mode = getComparisonMode();
            final Map<IdentifiedObject, Map<ComparisonMode,IdentifiedObject>> pool = findPool;
            synchronized (pool) {
                final Map<ComparisonMode,IdentifiedObject> byMode = pool.get(object);
                if (byMode != null) {
                    final IdentifiedObject candidate = byMode.get(mode);
                    if (candidate != null) {
                        return (candidate == NilReferencingObject.INSTANCE) ? null : candidate;
                    }
                }
            }
            /*
             * Nothing has been found in the cache. Delegates the search to the backing store.
             */
            final IdentifiedObject candidate;
            synchronized (this) {
                try {
                    acquire();
                    // Must delegates to 'finder' (not to 'super') in order to take
                    // advantage of the method overridden by AllAuthoritiesFactory.
                    candidate = finder.find(object);
                } finally {
                    release();
                }
            }
            /*
             * If the full scan was allowed, then stores the result even if null so
             * we can remember that no object has been found for the given argument.
             */
            if (candidate != null || isFullScanAllowed()) {
                synchronized (pool) {
                    Map<ComparisonMode,IdentifiedObject> byMode = pool.get(object);
                    if (byMode == null) {
                        byMode = new EnumMap<>(ComparisonMode.class);
                        pool.put(object, byMode);
                    }
                    byMode.put(mode, (candidate == null) ? NilReferencingObject.INSTANCE : candidate);
                }
            }
            return candidate;
        }
    }

    /**
     * Prints the cache content to the {@linkplain System#out standard output stream}.
     * Keys are sorted by numerical order if possible, or alphabetical order otherwise.
     * This method is used for debugging purpose only.
     *
     * @param out The output printer, or {@code null} for the
     *            {@linkplain System#out standard output stream}.
     *
     * @since 3.17
     */
    @Debug
    public void printCacheContent(final PrintWriter out) {
        CacheRecord.printCacheContent(cache, out);
    }

    /**
     * {@code true} if different values may be assigned to the same key. This is usually
     * an error, so the default {@link Cache} behavior is to thrown an exception in such
     * case. However in some cases we may want to relax this check. For example the EPSG
     * database sometime assign the same key to different kind of objects.
     * <p>
     * This property can also be set in order to allow some recursivity. If during the creation of
     * an object, the program asks to this {@code CachingAuthorityFactory} for the same object
     * (using the same key), then the default {@code CachingAuthorityFactory} implementation will
     * consider this situation as a key collision unless this property has been set to {@code true}.
     *
     * @param allowed {@code true} if key collisions are allowed.
     *
     * @see Cache#setKeyCollisionAllowed(boolean)
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
    protected void dispose(final boolean shutdown) {
        super.dispose(shutdown); // Mark the factory as not available anymore.
        final AbstractAuthorityFactory factory = backingStore;
        if (factory != null) {
            factory.dispose(shutdown);
        }
        cache.clear();
        authority = null;
    }
}

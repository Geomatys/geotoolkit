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
package org.geotoolkit.referencing.factory;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.awt.RenderingHints;
import javax.measure.unit.Unit;

import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.util.InternationalString;
import org.opengis.util.FactoryException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.factory.FactoryNotFoundException;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.lang.Decorator;

import static org.geotoolkit.internal.FactoryUtilities.ATTEMPTS_DELAY;


/**
 * An authority factory which delegates {@linkplain CoordinateReferenceSystem CRS},
 * {@linkplain CoordinateSystem CS} or {@linkplain Datum datum} objects creation to
 * some other factory implementations.
 * <p>
 * All constructors are protected because this class must be subclassed in order to determine
 * which of the {@link DatumAuthorityFactory}, {@link CSAuthorityFactory} and
 * {@link CRSAuthorityFactory} interfaces to implement.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.03
 *
 * @since 2.2
 * @module
 */
@Decorator(AuthorityFactory.class)
public class AuthorityFactoryAdapter extends AbstractAuthorityFactory {
    /**
     * Number of factory types. This is the length of the all static arrays, and is
     * also the upper limit (exclusive) of <var>n</var> in {@link #getFactory(int)}.
     */
    static final int TYPE_COUNT = 4;

    /**
     * List of authority factory types, in the same order than {@link #HINT_KEYS}.
     */
    @SuppressWarnings({"unchecked", "rawtypes"}) // Generic array creation.
    private static final Class<? extends AuthorityFactory>[] TYPES = new Class[] {
        CRSAuthorityFactory.class,
        CSAuthorityFactory.class,
        DatumAuthorityFactory.class,
        CoordinateOperationAuthorityFactory.class
    };

    /**
     * List of hint keys related to authority factories, in the same order than {@link #TYPES}.
     */
    private static final Hints.Key[] HINT_KEYS = new Hints.Key[] {
        Hints.CRS_AUTHORITY_FACTORY,
        Hints.CS_AUTHORITY_FACTORY,
        Hints.DATUM_AUTHORITY_FACTORY,
        Hints.COORDINATE_OPERATION_AUTHORITY_FACTORY
    };

    /**
     * List of variable names, only for producing error messages.
     */
    private static final String[] NAMES = {
        "crsFactory",
        "csFactory",
        "datumFactory",
        "operationFactory"
    };

    /**
     * Returns the factory at the given index. For any value of <var>n</var>,
     * the returned factory must be the type {@code TYPES[n]}. Note that all
     * {@code setFactory(int, ...)} methods require the same numbering.
     */
    final AuthorityFactory getFactory(final int f) {
        switch (f) {
            case 0:  return crsFactory;  // TransformedAuthorityFactory expects index 0 for CRS.
            case 1:  return csFactory;   // TransformedAuthorityFactory expects index 1 for CS.
            case 2:  return datumFactory;
            case 3:  return operationFactory;
            default: throw new AssertionError(f);
        }
    }

    /**
     * Sets the factory at the given index. The index numbering must be the same than
     * {@link #getFactory}.
     */
    private void setFactory(final int f, final AuthorityFactory factory) {
        switch (f) {
            case 0:  crsFactory       = (CRSAuthorityFactory)                 factory; break;
            case 1:  csFactory        = (CSAuthorityFactory)                  factory; break;
            case 2:  datumFactory     = (DatumAuthorityFactory)               factory; break;
            case 3:  operationFactory = (CoordinateOperationAuthorityFactory) factory; break;
            default: throw new AssertionError(f);
        }
    }

    /**
     * Fetches the factory at the given index from {@link AuthorityFactoryFinder}.
     * The index numbering must be the same than {@link #getFactory}.
     *
     * throws FactoryRegistryException If the factory can not be obtained.
     *
     * @see #setFactories
     */
    private void setFactory(final int f, final String a, final Hints h) throws FactoryRegistryException {
        switch (f) {
            case 0:  crsFactory       = AuthorityFactoryFinder.getCRSAuthorityFactory                (a, h); break;
            case 1:  csFactory        = AuthorityFactoryFinder.getCSAuthorityFactory                 (a, h); break;
            case 2:  datumFactory     = AuthorityFactoryFinder.getDatumAuthorityFactory              (a, h); break;
            case 3:  operationFactory = AuthorityFactoryFinder.getCoordinateOperationAuthorityFactory(a, h); break;
            default: throw new AssertionError(f);
        }
    }

    /**
     * The underlying {@linkplain CoordinateReferenceSystem coordinate reference system}
     * authority factory, or {@code null} if none.
     */
    private CRSAuthorityFactory crsFactory;

    /**
     * The underlying {@linkplain CoordinateSystem coordinate system} authority factory,
     * or {@code null} if none.
     */
    private CSAuthorityFactory csFactory;

    /**
     * The underlying {@linkplain Datum datum} authority factory, or {@code null} if none.
     */
    private DatumAuthorityFactory datumFactory;

    /**
     * The underlying {@linkplain CoordinateOperation coordinate operation} authority factory,
     * or {@code null} if none.
     */
    private CoordinateOperationAuthorityFactory operationFactory;

    /**
     * If we failed to instantiated the backing factories, when the last attempt occurred.
     * This is used in order to wait a little while before to try a new attempt, in order
     * to avoid too many connection attempts.
     *
     * @see #NO_LAST_ATTEMPT
     * @see #LAST_ATTEMPT_SUCCEED
     */
    private long lastAttempt = Long.MIN_VALUE;

    /**
     * The value to assign to {@link #lastAttempt} after a successfull query of the backing
     * store. Any value greater than {@code LAST_ATTEMPT_SUCCEED} means that we had a failure.
     * The {@link Long#MIN_VALUE} is reserved for "no attempt yet".
     */
    private static final long LAST_ATTEMPT_SUCCEED = Long.MIN_VALUE + 1;

    /**
     * The hints of the factories to fetch with {@link AuthorityFactoryFinder}. This is
     * non-null only if the factories have not yet been initialized. Deferred assignation of
     * factories is needed in order to give {@code FactoryRegistry.scanForPlugins} a chance
     * to finish registration of all factories found on the classpath.
     */
    private volatile Hints deferred;

    /**
     * The key of the authority to stores to {@link #deferred}.
     */
    private static final Hints.Key AUTHORITY_KEY = new Hints.Key(String.class);

    /**
     * Creates a wrapper around no factory. This constructor should never be used except by
     * subclasses overriding the <code>get</code><var>Foo</var><code>AuthorityFactory</code>
     * methods.
     */
    AuthorityFactoryAdapter() {
        super(EMPTY_HINTS);
    }

    /**
     * Creates a wrapper around the specified factory.
     *
     * @param factory The factory to wrap.
     */
    protected AuthorityFactoryAdapter(final AuthorityFactory factory) {
        this(factory, null);
    }

    /**
     * Creates a wrapper around the specified factory and fallback. The fallback should be
     * non-null only when this contructor is invoked by {@link FallbackAuthorityFactory}.
     */
    AuthorityFactoryAdapter(final AuthorityFactory factory, final AuthorityFactory fallback) {
        super(EMPTY_HINTS);
        for (int f=0; f<TYPES.length; f++) {
            final Class<? extends AuthorityFactory> type = TYPES[f];
            final AuthorityFactory assign;
            if (type.isInstance(factory)) {
                assign = factory;
            } else if (type.isInstance(fallback)) {
                assign = fallback;
            } else {
                continue;
            }
            setFactory(f, assign);
        }
        putFactoryHints();
        // Do not invoke putCustomHints(), because subclass constructors need to
        // do their own initialisation before the later method can be effective.
    }

    /**
     * Creates a wrapper around the specified factories.
     *
     * @param crsFactory
     *          The {@linkplain CoordinateReferenceSystem coordinate reference system}
     *          authority factory, or {@code null}.
     * @param csFactory
     *          The {@linkplain CoordinateSystem coordinate system} authority factory,
     *          or {@code null}.
     * @param datumFactory
     *          The {@linkplain Datum datum} authority factory, or {@code null}.
     * @param operationFactory
     *          The {@linkplain CoordinateOperation coordinate operation} authority factory,
     *          or {@code null}.
     */
    protected AuthorityFactoryAdapter(final CRSAuthorityFactory crsFactory,
                                      final CSAuthorityFactory csFactory,
                                      final DatumAuthorityFactory datumFactory,
                                      final CoordinateOperationAuthorityFactory operationFactory)
    {
        super(EMPTY_HINTS);
        this.crsFactory       = crsFactory;
        this.csFactory        = csFactory;
        this.datumFactory     = datumFactory;
        this.operationFactory = operationFactory;
        putFactoryHints();
        // Do not invoke putCustomHints(), because subclass constructors need to
        // do their own initialisation before the later method can be effective.
    }

    /**
     * Creates a wrappers around the default factories for the specified authority.
     * The factories are fetched using {@link AuthorityFactoryFinder}.
     *
     * @param  authority The authority to wrap (example: {@code "EPSG"}). If {@code null},
     *         then all authority factories must be explicitly specified in the set of hints.
     * @param  userHints An optional set of hints, or {@code null} if none.
     * @throws FactoryRegistryException if at least one factory can not be obtained.
     *
     * @since 2.4
     */
    protected AuthorityFactoryAdapter(final String authority, final Hints userHints)
            throws FactoryRegistryException
    {
        super(userHints);
        if (authority == null) {
            // Fetches the factories immediately.
            setFactories(authority, userHints);
        } else {
            // Will fetch the factories later.
            deferred = (userHints != null ? userHints : EMPTY_HINTS).clone();
            deferred.put(AUTHORITY_KEY, authority);
        }
    }

    /**
     * Fetches the factories from the {@link AuthorityFactoryFinder} immediately.
     * This method completes its work by a call to {@link #putFactoryHints()}.
     *
     * @param  authority The authority to wrap (example: {@code "EPSG"}). If {@code null},
     *         then all authority factories must be explicitly specified in the set of hints.
     * @param  userHints An optional set of hints, or {@code null} if none.
     * @throws FactoryRegistryException if at least one factory can not be obtained.
     */
    private void setFactories(final String authority, final Hints userHints)
            throws FactoryRegistryException
    {
        for (int f=0; f<TYPE_COUNT; f++) {
            /*
             * Removes every AUTHORITY_FACTORY hints except the "kept" ones. The removal, if needed,
             * is performed in a copy of the supplied hints in order to keep user's map unmodified.
             *
             * This removal is performed because AUTHORITY_FACTORY hints are typically supplied to the
             * constructor in order to initialize the crsFactory, csFactory, etc. fields.  But because
             * the same hints are used for every call to AuthorityFactoryFinder methods, if we don't do
             * this removal, then the CRS_AUTHORITY_FACTORY hint is taken in account for fetching other
             * factories like CSAuthorityFactory. We may think that it is not a problem since the later
             * should not care about the CRS_AUTHORITY_FACTORY hint.  But... our EPSG authority factory
             * implements both CRSAuthorityFactory and CSAuthorityFactory interfaces, so it behave like
             * a CSAuthorityFactory implementation that do have CRS-related hints.
             *
             * Conclusion: if we do not remove those hints, it typically leads to failure to find
             * a CS authority factory using this specific CRS authority factory. We may argue that
             * this is a Geotk design problem. Maybe... this is not a trivial issue. So we are
             * better to not document that in public API for now.
             */
            final Hints.Key keep = HINT_KEYS[f];
            Hints reduced = userHints;
            if (userHints != null) {
                for (int i=0; i<HINT_KEYS.length; i++) {
                    final Hints.Key key = HINT_KEYS[i];
                    if (!keep.equals(key)) {
                        if (reduced == userHints) {
                            if (!userHints.containsKey(key)) {
                                continue;
                            }
                            // Copies the map only if we need to modify it.
                            reduced = userHints.clone();
                        }
                        reduced.remove(key);
                    }
                }
            }
            setFactory(f, authority, reduced);
        }
        putFactoryHints();
    }

    /**
     * Sets {@link Hints#CRS_AUTHORITY_FACTORY}, {@link Hints#CS_AUTHORITY_FACTORY} and similiar
     * hints to the values provided by {@link #crsFactory}, {@link #csFactory}, <i>etc</i>.
     * This method also ensure that required arguments are non-null.
     */
    private void putFactoryHints() {
        for (int f=0; f<TYPE_COUNT; f++) {
            final AuthorityFactory factory = getFactory(f);
            final Class<? extends AuthorityFactory> type = TYPES[f];
            if (type.isInstance(this)) {
                ensureNonNull(NAMES[f], factory);
            }
            if (factory != null) {
                final Hints.Key key = HINT_KEYS[f];
                final Object old = hints.put(key, factory);
                if (old != null && old != factory) {
                    throw new AssertionError(key);
                }
            }
        }
    }

    /**
     * Return {@code true} if this factory is initialized. Subclass constructors will invoke this
     * method this method in order to determine if they need to invoke {@link #putCustomHints()}.
     */
    final boolean isInitialized() {
        return deferred == null;
    }

    /**
     * Initializes this factory if it is not already initialized.  This method must be invoked
     * before any usage of {@link #crsFactory} and its friends, including indirect use through
     * {@link #getFactory}.
     *
     * @throws FactoryRegistryException if at least one factory can not be obtained.
     */
    final void ensureInitialized() throws FactoryRegistryException {
        if (!isInitialized()) {
            /*
             * Double-check: was a deprecated practice before Java 5, but it safe
             * since Java 5 provided that the "authority" field is volatile.
             */
            synchronized (this) {
                final Hints userHints = deferred;
                if (userHints != null) {
                    final String authority = (String) userHints.get(AUTHORITY_KEY);
                    toBackingFactoryHints(userHints);
                    setFactories(authority, userHints);
                    putCustomHints();
                    deferred = null; // Means that this factory has been initialized.
                }
            }
        }
    }

    /**
     * A hook to be overridden by {@link OrderedAxisAuthorityFactory} only. This method is invoked
     * soon after {@link #putFactoryHints}, but not from {@link AuthorityFactoryAdapter} constructors
     * because the construction of {@code OrderedAxisAuthorityFactory} is not completed at this time.
     */
    void putCustomHints() {
    }

    /**
     * Returns the {@linkplain #hints hints} extended with all hints specified in dependencies.
     * If the same hint is defined in many factory, then the CRS factory hints have precedence
     * over the CS, datum or operation factories. And the hints defined in this class have
     * precedence over all other hints.
     */
    final Hints dependencyHints() {
        ensureInitialized();
        final Hints merged = EMPTY_HINTS.clone();
        merged.putAll(hints);
        for (int f=TYPE_COUNT; --f>=0;) { // Must be in reverse order.
            final AuthorityFactory factory = getFactory(f);
            if (factory instanceof Factory) {
                merged.putAll(((Factory) factory).getImplementationHints());
            }
        }
        merged.putAll(hints); // Gives precedence to the hints from this class.
        return merged;
    }

    /**
     * Returns the direct dependencies. The returned list contains the backing store specified
     * at construction time, or the exception if the backing store can't be obtained.
     */
    @Override
    Collection<? super AuthorityFactory> dependencies() {
        // Need a modifiable list, because some subclasses
        // will add more elements to that list.
        final List<Object> dep = new ArrayList<>(2);
        Object factory;
        try {
            factory = getAuthorityFactory(null);
        } catch (FactoryException e) {
            factory = e;
        }
        dep.add(factory);
        return dep;
    }

    /**
     * If this factory is a wrapper for the specified factory that do not add any additional
     * {@linkplain #getAuthorityCodes authority codes}, returns {@code true}. This method is
     * for {@link FallbackAuthorityFactory} internal use only and should not be public. We
     * expect only a simple check, so we don't invoke the {@code getFooAuthorityFactory(...)}
     * methods.
     */
    @Override
    boolean sameAuthorityCodes(final AuthorityFactory factory) {
        if (super.sameAuthorityCodes(factory)) {
            return true;
        }
        if (!isCodeMethodOverridden()) {
            /*
             * Tests wrapped factories only if the 'toBackingFactoryCode(String)' method is not
             * overwritten, otherwise we can't assume that the authority codes are the same. The
             * impact on the main subclasses are usually as below:
             *
             *     URN_AuthorityFactory           - excluded
             *     HTTP_AuthorityFactory          - excluded
             *     OrderedAxisAuthorityFactory    - make the test below
             *     FallbackAuthorityFactory       - make the test below
             *
             * Note: in the particular case of FallbackAuthorityFactory, we test the
             *       primary factory only, not the fallback. This behavior matches the
             *       FallbackAuthorityFactory.create(boolean,int,Iterator) need, which
             *       will process this case in a special way.
             */
            ensureInitialized();
            for (int f=0; f<TYPE_COUNT; f++) {
                if (!sameAuthorityCodes(getFactory(f), factory)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Helper methods for {@link #sameAuthorityCodes(AuthorityFactory)} and
     * {@link FallbackAuthorityFactory#create(boolean,int,Iterator)} implementations. If there is no
     * backing store, returns {@code true} in order to take in account only the backing stores that
     * are assigned. This behavior match the need of the above-cited implementations.
     */
    static boolean sameAuthorityCodes(final AuthorityFactory backingStore,
                                      final AuthorityFactory factory)
    {
        if (backingStore instanceof AbstractAuthorityFactory) {
            if (((AbstractAuthorityFactory) backingStore).sameAuthorityCodes(factory)) {
                return true;
            }
        }
        return (factory == backingStore) || (backingStore == null);
    }

    /**
     * Returns {@code true} if {@link #availability()}, {@link #getAuthority()} or
     * {@link #getVendor()} were attempted recently and failed.
     *
     * @param  currentTime The value of {@link System#currentTimeMillis()}.
     * @return {@code true} if the last attempt failed recently.
     */
    private boolean failedRecently(final long currentTime) {
        final long last = lastAttempt;
        return (last > LAST_ATTEMPT_SUCCEED) && (currentTime - last) < ATTEMPTS_DELAY;
    }

    /**
     * Logs a message when an attempt to get the backing factory failed.
     *
     * @param method The method to declare in the logger.
     * @param e The exception.
     * @param currentTime The value to set into {@link #lastAttempt}.
     */
    private void logFailure(final String method, final Exception e, final long currentTime) {
        if (lastAttempt == LAST_ATTEMPT_SUCCEED) {
            /*
             * A factory which was previously working is not working anymore.
             * It may be a cause of serious problem in client code that didn't
             * expected the disparition of a their factory.
             */
            Logging.unexpectedException(LOGGER, AuthorityFactoryAdapter.class, method, e);
        } else {
            /*
             * A factory was not working before, and still not working.
             * Log at the FINE level.
             */
            Logging.recoverableException(LOGGER, AuthorityFactoryAdapter.class, method, e);
        }
        lastAttempt = currentTime;
    }

    /**
     * Returns whatever this factory is ready for use. This default implementation checks the
     * availability of CRS, CS, datum and operation authority factories specified at construction
     * time.
     *
     * @since 3.03
     */
    @Override
    public synchronized ConformanceResult availability() {
        final long currentTime = System.currentTimeMillis();
        if (failedRecently(currentTime)) {
            // If the last unsuccessful attempt was less than some arbitrary time
            // before current attempt, assume that the situation didn't changed.
            return new Unavailable(null);
        }
        try {
            ensureInitialized();
        } catch (RuntimeException e) {
            // The two main exceptions of interest are FactoryRegistryException
            // and NullArgumentException.
            logFailure("availability", e, currentTime);
            return new Availability(e);
        }
        for (int f=0; f<TYPE_COUNT; f++) {
            final AuthorityFactory factory = getFactory(f);
            if (factory instanceof Factory) {
                final ConformanceResult result = ((Factory) factory).availability();
                if (!Boolean.TRUE.equals(result.pass())) {
                    lastAttempt = currentTime;
                    return new Unavailable(result);
                }
            }
        }
        lastAttempt = LAST_ATTEMPT_SUCCEED;
        return super.availability();
    }

    /**
     * The conformance result when one of the factory dependency is not available. This class
     * wraps the result of the unavailable dependency and makes sure that {@link #pass()}
     * returns {@code false} in all cases, even if the dependency become available at a
     * later time, because we have not tested the other dependencies.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.14
     *
     * @since 3.03
     * @module
     */
    private final class Unavailable extends Availability {
        /**
         * Creates a new wrapper for the result of the unavailable dependency.
         */
        Unavailable(final ConformanceResult result) {
            super(result instanceof Availability ? ((Availability) result).getFailureCause() : null);
        }

        /**
         * Returns {@code false} in every case.
         */
        @Override
        public Boolean pass() {
            return Boolean.FALSE;
        }
    }

    /**
     * Returns the hints used by this factory.
     */
    @Override
    public Map<RenderingHints.Key, ?> getImplementationHints() {
        ensureInitialized();
        return super.getImplementationHints();
    }

    /**
     * Replaces the specified unit, if applicable.
     * To be overridden with {@code protected} access by {@link TransformedAuthorityFactory}.
     */
    Unit<?> replace(Unit<?> units) throws FactoryException {
        return units;
    }

    /**
     * Replaces (if needed) the specified axis by a new one.
     * To be overridden with {@code protected} access by {@link TransformedAuthorityFactory}.
     */
    CoordinateSystemAxis replace(CoordinateSystemAxis axis) throws FactoryException {
        return axis;
    }

    /**
     * Replaces (if needed) the specified coordinate system by a new one.
     * To be overridden with {@code protected} access by {@link TransformedAuthorityFactory}.
     */
    <T extends CoordinateSystem> T replace(T cs) throws FactoryException {
        return cs;
    }

    /**
     * Replaces (if needed) the specified datum by a new one.
     * To be overridden with {@code protected} access by {@link TransformedAuthorityFactory}.
     */
    <T extends Datum> T replace(T datum) throws FactoryException {
        return datum;
    }

    /**
     * Replaces (if needed) the specified coordinate reference system.
     * To be overridden with {@code protected} access by {@link TransformedAuthorityFactory}.
     */
    <T extends CoordinateReferenceSystem> T replace(T crs) throws FactoryException {
        return crs;
    }

    /**
     * Replaces (if needed) the specified coordinate operation.
     * To be overridden with {@code protected} access by {@link TransformedAuthorityFactory}.
     */
    <T extends CoordinateOperation> T replace(T operation) throws FactoryException {
        return operation;
    }

    /**
     * Delegates the work to an appropriate {@code replace} method for the given object.
     */
    private IdentifiedObject replaceObject(final IdentifiedObject object) throws FactoryException {
        if (object instanceof CoordinateReferenceSystem) {
            return replace((CoordinateReferenceSystem) object);
        }
        if (object instanceof CoordinateSystem) {
            return replace((CoordinateSystem) object);
        }
        if (object instanceof CoordinateSystemAxis) {
            return replace((CoordinateSystemAxis) object);
        }
        if (object instanceof Datum) {
            return replace((Datum) object);
        }
        if (object instanceof CoordinateOperation) {
            return replace((CoordinateOperation) object);
        }
        return object;
    }

    /**
     * Returns one of the underlying factories as an instance of the Geotk implementation. If
     * there is none of them, then returns {@code null} or throws an exception if {@code caller}
     * is not null.
     */
    private AbstractAuthorityFactory getGeotoolkitFactory(final String caller, final String code)
            throws FactoryException
    {
        final AuthorityFactory candidate = getAuthorityFactory(code);
        if (candidate instanceof AbstractAuthorityFactory) {
            return (AbstractAuthorityFactory) candidate;
        }
        if (caller == null) {
            return null;
        }
        throw new FactoryException(Errors.format(
                    Errors.Keys.GeotoolkitExtensionRequired_1, caller));
    }

    /**
     * Returns a description of the underlying backing store, or {@code null} if unknown.
     *
     * @throws FactoryException if a failure occurred while fetching the engine description.
     */
    @Override
    public String getBackingStoreDescription() throws FactoryException {
        final AbstractAuthorityFactory factory = getGeotoolkitFactory(null, null);
        return (factory != null) ? factory.getBackingStoreDescription() : null;
    }

    /**
     * Returns the vendor or the authority, or {@code UNKNOWN} if the information is not available.
     * This methods fetches the citation only if the last attempt is older than some arbitrary
     * delay, in order to avoid too many connection attempts and flooding the logger.
     *
     * @param  method Either {@code "getAuthority"} or {@code "getVendor"}.
     * @return The authority or the vendor, or {@code UNKNOWN}.
     *
     * @see #availability
     * @see org.geotoolkit.metadata.Citations#UNKNOWN
     */
    @Override
    final synchronized Citation getCitation(final String method) {
        final long currentTime = System.currentTimeMillis();
        if (!failedRecently(currentTime)) try {
            final Citation citation = getCitation(getAuthorityFactory(), method);
            lastAttempt = LAST_ATTEMPT_SUCCEED;
            return citation;
        } catch (FactoryNotFoundException e) {
            logFailure(method, e, currentTime);
        }
        // Fallback on the default implementation,
        // which returns UNKNOWN most of the time.
        return super.getCitation(method);
    }

    /**
     * Returns the vendor responsible for creating this factory implementation.
     * This implementation may return {@code null} if the factory is not
     * {@linkplain #availability available}.
     */
    @Override
    public Citation getVendor() {
        return getCitation("getVendor");
    }

    /**
     * Returns the organization or party responsible for definition and maintenance of
     * the database. This implementation may return {@code null} if the factory is not
     * {@linkplain #availability available}.
     */
    @Override
    public Citation getAuthority() {
        return getCitation("getAuthority");
    }

    /**
     * Returns the set of authority code for the specified type.
     *
     * @todo We should returns the union of authority codes from all underlying factories.
     */
    @Override
    public Set<String> getAuthorityCodes(final Class<? extends IdentifiedObject> type)
            throws FactoryException
    {
        return getAuthorityFactory(null).getAuthorityCodes(type);
    }

    /**
     * Returns a description for the object identified by the specified code.
     */
    @Override
    public InternationalString getDescriptionText(final String code) throws FactoryException {
        return getAuthorityFactory(code).getDescriptionText(toBackingFactoryCode(code));
    }

    /**
     * Returns an arbitrary object from a code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @see #createCoordinateReferenceSystem
     * @see #createDatum
     * @see #createEllipsoid
     * @see #createUnit
     */
    @Override
    public IdentifiedObject createObject(final String code) throws FactoryException {
        return replaceObject(getAuthorityFactory(code).createObject(toBackingFactoryCode(code)));
    }

    /**
     * Returns an arbitrary {@linkplain Datum datum} from a code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @see #createGeodeticDatum
     * @see #createVerticalDatum
     * @see #createTemporalDatum
     */
    @Override
    public Datum createDatum(final String code) throws FactoryException {
        return replace(getDatumAuthorityFactory(code).createDatum(toBackingFactoryCode(code)));
    }

    /**
     * Creates a {@linkplain EngineeringDatum engineering datum} from a code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @see #createEngineeringCRS
     */
    @Override
    public EngineeringDatum createEngineeringDatum(final String code) throws FactoryException {
        return replace(getDatumAuthorityFactory(code).createEngineeringDatum(toBackingFactoryCode(code)));
    }

    /**
     * Creates a {@linkplain ImageDatum image datum} from a code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @see #createImageCRS
     */
    @Override
    public ImageDatum createImageDatum(final String code) throws FactoryException {
        return replace(getDatumAuthorityFactory(code).createImageDatum(toBackingFactoryCode(code)));
    }

    /**
     * Creates a {@linkplain VerticalDatum vertical datum} from a code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @see #createVerticalCRS
     */
    @Override
    public VerticalDatum createVerticalDatum(final String code) throws FactoryException {
        return replace(getDatumAuthorityFactory(code).createVerticalDatum(toBackingFactoryCode(code)));
    }

    /**
     * Creates a {@linkplain TemporalDatum temporal datum} from a code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @see #createTemporalCRS
     */
    @Override
    public TemporalDatum createTemporalDatum(final String code) throws FactoryException {
        return replace(getDatumAuthorityFactory(code).createTemporalDatum(toBackingFactoryCode(code)));
    }

    /**
     * Returns a {@linkplain GeodeticDatum geodetic datum} from a code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @see #createEllipsoid
     * @see #createPrimeMeridian
     * @see #createGeographicCRS
     * @see #createProjectedCRS
     */
    @Override
    public GeodeticDatum createGeodeticDatum(final String code) throws FactoryException {
        return replace(getDatumAuthorityFactory(code).createGeodeticDatum(toBackingFactoryCode(code)));
    }

    /**
     * Returns an {@linkplain Ellipsoid ellipsoid} from a code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @see #createGeodeticDatum
     */
    @Override
    public Ellipsoid createEllipsoid(final String code) throws FactoryException {
        return getDatumAuthorityFactory(code).createEllipsoid(toBackingFactoryCode(code));
    }

    /**
     * Returns a {@linkplain PrimeMeridian prime meridian} from a code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @see #createGeodeticDatum
     */
    @Override
    public PrimeMeridian createPrimeMeridian(final String code) throws FactoryException {
        return getDatumAuthorityFactory(code).createPrimeMeridian(toBackingFactoryCode(code));
    }

    /**
     * Returns a {@linkplain Extent extent} (usually an area of validity) from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Extent createExtent(final String code) throws FactoryException {
        return getGeotoolkitFactory("createExtent", code).createExtent(toBackingFactoryCode(code));
    }

    /**
     * Returns an arbitrary {@linkplain CoordinateSystem coordinate system} from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateSystem createCoordinateSystem(final String code) throws FactoryException {
        return replace(getCSAuthorityFactory(code).createCoordinateSystem(toBackingFactoryCode(code)));
    }

    /**
     * Creates a Cartesian coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CartesianCS createCartesianCS(final String code) throws FactoryException {
        return replace(getCSAuthorityFactory(code).createCartesianCS(toBackingFactoryCode(code)));
    }

    /**
     * Creates a polar coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public PolarCS createPolarCS(final String code) throws FactoryException {
        return replace(getCSAuthorityFactory(code).createPolarCS(toBackingFactoryCode(code)));
    }

    /**
     * Creates a cylindrical coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CylindricalCS createCylindricalCS(final String code) throws FactoryException {
        return replace(getCSAuthorityFactory(code).createCylindricalCS(toBackingFactoryCode(code)));
    }

    /**
     * Creates a spherical coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public SphericalCS createSphericalCS(final String code) throws FactoryException {
        return replace(getCSAuthorityFactory(code).createSphericalCS(toBackingFactoryCode(code)));
    }

    /**
     * Creates an ellipsoidal coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public EllipsoidalCS createEllipsoidalCS(final String code) throws FactoryException {
        return replace(getCSAuthorityFactory(code).createEllipsoidalCS(toBackingFactoryCode(code)));
    }

    /**
     * Creates a vertical coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public VerticalCS createVerticalCS(final String code) throws FactoryException {
        return replace(getCSAuthorityFactory(code).createVerticalCS(toBackingFactoryCode(code)));
    }

    /**
     * Creates a temporal coordinate system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public TimeCS createTimeCS(final String code) throws FactoryException {
        return replace(getCSAuthorityFactory(code).createTimeCS(toBackingFactoryCode(code)));
    }

    /**
     * Returns a {@linkplain CoordinateSystemAxis coordinate system axis} from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateSystemAxis createCoordinateSystemAxis(final String code) throws FactoryException {
        return replace(getCSAuthorityFactory(code).createCoordinateSystemAxis(toBackingFactoryCode(code)));
    }

    /**
     * Returns an {@linkplain Unit unit} from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Unit<?> createUnit(final String code) throws FactoryException {
        return replace(getCSAuthorityFactory(code).createUnit(toBackingFactoryCode(code)));
    }

    /**
     * Returns an arbitrary {@linkplain CoordinateReferenceSystem coordinate reference system}
     * from a code.
     *
     * @throws FactoryException if the object creation failed.
     *
     * @see #createGeographicCRS
     * @see #createProjectedCRS
     * @see #createVerticalCRS
     * @see #createTemporalCRS
     * @see #createCompoundCRS
     */
    @Override
    public CoordinateReferenceSystem createCoordinateReferenceSystem(final String code) throws FactoryException {
        return replace(getCRSAuthorityFactory(code).createCoordinateReferenceSystem(toBackingFactoryCode(code)));
    }

    /**
     * Creates a 3D coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CompoundCRS createCompoundCRS(final String code) throws FactoryException {
        return replace(getCRSAuthorityFactory(code).createCompoundCRS(toBackingFactoryCode(code)));
    }

    /**
     * Creates a derived coordinate reference system from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public DerivedCRS createDerivedCRS(final String code) throws FactoryException {
        return replace(getCRSAuthorityFactory(code).createDerivedCRS(toBackingFactoryCode(code)));
    }

    /**
     * Creates a {@linkplain EngineeringCRS engineering coordinate reference system} from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public EngineeringCRS createEngineeringCRS(final String code) throws FactoryException {
        return replace(getCRSAuthorityFactory(code).createEngineeringCRS(toBackingFactoryCode(code)));
    }

    /**
     * Returns a {@linkplain GeographicCRS geographic coordinate reference system} from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public GeographicCRS createGeographicCRS(final String code) throws FactoryException {
        return replace(getCRSAuthorityFactory(code).createGeographicCRS(toBackingFactoryCode(code)));
    }

    /**
     * Returns a {@linkplain GeocentricCRS geocentric coordinate reference system} from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public GeocentricCRS createGeocentricCRS(final String code) throws FactoryException {
        return replace(getCRSAuthorityFactory(code).createGeocentricCRS(toBackingFactoryCode(code)));
    }

    /**
     * Creates a {@linkplain ImageCRS image coordinate reference system} from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public ImageCRS createImageCRS(final String code) throws FactoryException {
        return replace(getCRSAuthorityFactory(code).createImageCRS(toBackingFactoryCode(code)));
    }

    /**
     * Returns a {@linkplain ProjectedCRS projected coordinate reference system} from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public ProjectedCRS createProjectedCRS(final String code) throws FactoryException {
        return replace(getCRSAuthorityFactory(code).createProjectedCRS(toBackingFactoryCode(code)));
    }

    /**
     * Creates a {@linkplain TemporalCRS temporal coordinate reference system} from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public TemporalCRS createTemporalCRS(final String code) throws FactoryException {
        return replace(getCRSAuthorityFactory(code).createTemporalCRS(toBackingFactoryCode(code)));
    }

    /**
     * Creates a {@linkplain VerticalCRS vertical coordinate reference system} from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public VerticalCRS createVerticalCRS(final String code) throws FactoryException {
        return replace(getCRSAuthorityFactory(code).createVerticalCRS(toBackingFactoryCode(code)));
    }

    /**
     * Creates a parameter descriptor from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public ParameterDescriptor<?> createParameterDescriptor(final String code) throws FactoryException {
        return getGeotoolkitFactory("createParameterDescriptor", code).
                createParameterDescriptor(toBackingFactoryCode(code));
    }

    /**
     * Creates an operation method from a code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public OperationMethod createOperationMethod(final String code) throws FactoryException {
        return getGeotoolkitFactory("createOperationMethod", code).
                createOperationMethod(toBackingFactoryCode(code));
    }

    /**
     * Creates an operation from a single operation code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateOperation createCoordinateOperation(final String code) throws FactoryException {
        return replace(getCoordinateOperationAuthorityFactory(code).
                createCoordinateOperation(toBackingFactoryCode(code)));
    }

    /**
     * Creates an operation from coordinate reference system codes.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Set<CoordinateOperation> createFromCoordinateReferenceSystemCodes(
            final String sourceCRS, final String targetCRS) throws FactoryException
    {
        final CoordinateOperationAuthorityFactory factory, check;
        factory = getCoordinateOperationAuthorityFactory(sourceCRS);
        check   = getCoordinateOperationAuthorityFactory(targetCRS);
        if (factory != check) {
            /*
             * No coordinate operation because of mismatched factories. This is not
             * illegal - the result is an empty set - but it is worth to notify the
             * user since this case has some chances to be an user error.
             */
            final LogRecord record = Loggings.format(Level.WARNING,
                    Loggings.Keys.MismatchedCoordinateOperationFactories_2, sourceCRS, targetCRS);
            record.setSourceMethodName("createFromCoordinateReferenceSystemCodes");
            record.setSourceClassName(AuthorityFactoryAdapter.class.getName());
            record.setLoggerName(LOGGER.getName());
            LOGGER.log(record);
            return Collections.emptySet();
        }
        return factory.createFromCoordinateReferenceSystemCodes(
                toBackingFactoryCode(sourceCRS), toBackingFactoryCode(targetCRS));
    }

    /**
     * Returns a finder which can be used for looking up unidentified objects.
     * The default implementation delegates the lookups to the underlying factory.
     *
     * @throws FactoryException if the finder can not be created.
     *
     * @since 2.4
     */
    @Override
    public IdentifiedObjectFinder getIdentifiedObjectFinder(Class<? extends IdentifiedObject> type)
            throws FactoryException
    {
        return new Finder(this, type);
    }

    /**
     * A {@link IdentifiedObjectFinder} which compares two time the object created by the factory
     * with the object supplied by the user.
     * <p>
     * <ul>
     *   <li>First, the object created by the factory is compared without modification.</li>
     *   <li>If the previous comparison failed, then the object created by the factory is
     *       {@linkplain AuthorityFactoryAdapter#replaceObject modified} and the comparison
     *       is performed again.</li>
     * </ul>
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.06
     *
     * @since 2.4
     * @module
     */
    static class Finder extends IdentifiedObjectFinder {
        /**
         * The finder on which to delegate the work.
         */
        protected final IdentifiedObjectFinder finder;

        /**
         * The unmodified object created during the last call to {@link #create},
         * or {@code null} if none. This is used for derivating a modified object
         * during the second pass on {@code create(...)}.
         */
        private transient IdentifiedObject original;

        /**
         * The code of the {@linkplain #original} object.
         */
        private transient String originalCode;

        /**
         * Creates a finder for the underlying backing store. This constructor sets the
         * {@link #finder} field only, not {@link #factory}. The factory should be the same
         * than {@code finder.factory}, but we can't set it at construction time because it
         * may change at any time (e.g. {@link ThreadedAuthorityFactory}).
         */
        Finder(final AuthorityFactoryAdapter factory, final Class<? extends IdentifiedObject> type)
                throws FactoryException
        {
            super(factory, type);
            finder = factory.getGeotoolkitFactory("getIdentifiedObjectFinder", null).getIdentifiedObjectFinder(type);
            finder.setParent(this);
        }

        /**
         * Returns the type of the objects to be created by the proxy instance.
         */
        @Override
        final Class<? extends IdentifiedObject> getObjectType() {
            return finder.getObjectType();
        }

        /**
         * Returns the authority of the factory examined by this finder.
         */
        @Override
        public final Citation getAuthority() throws FactoryException {
            return finder.getAuthority();
        }

        /**
         * Returns a set of authority codes that <strong>may</strong> identify the same
         * object than the specified one.
         */
        @Override
        protected final Set<String> getCodeCandidates(IdentifiedObject object) throws FactoryException {
            return finder.getCodeCandidates(object);
        }

        /**
         * Propagates the configuration change to the underlying finder.
         *
         * @since 3.18
         */
        @Override
        public final void setComparisonMode(final ComparisonMode mode) {
            finder.setComparisonMode(mode);
            super .setComparisonMode(mode);
        }

        /**
         * Propagates the configuration change to the underlying finder.
         */
        @Override
        public final void setFullScanAllowed(final boolean fullScan) {
            finder.setFullScanAllowed(fullScan);
            super .setFullScanAllowed(fullScan);
        }

        /**
         * Creates an object from the given code. On the first attempt, this method create the
         * object using the backing store and returns it. On the second attempt, it modifies
         * the object and returns the modified version.
         *
         * @throws FactoryException if an error occurred while creating the object.
         */
        @Override
        protected final IdentifiedObject create(final String code, final int attempt) throws FactoryException {
            IdentifiedObject original = this.original;
            switch (attempt) {
                /*
                 * On the first attempt, checks if the previous object can be returned directly.
                 * This is acting like a cache, which we perform opportunistically only because
                 * we have this information anyway.
                 */
                case 0: {
                    if (code.equals(originalCode)) {
                        return original;
                    }
                    // Do not use directly the proxy; it may be null.
                    original = finder.create(code, attempt);
                    if (original != null) {
                        this.original = original;
                        this.originalCode = code;
                        return original;
                    }
                    break;
                }
                /*
                 * Second attempt. The 'original' field has been set by
                 * a previous call to this method with 'attempt == 0'.
                 */
                case 1: {
                    assert code.equals(originalCode) : code;
                    final IdentifiedObject object = ((AuthorityFactoryAdapter) factory).replaceObject(original);
                    if (object != original) {
                        return object;
                    }
                    break;
                }
            }
            return null;
        }
    }

    /**
     * Creates an exception for a missing factory. We actually returns an instance of
     * {@link NoSuchAuthorityCodeException} because this kind of exception is treated
     * especially by {@link FallbackAuthorityFactory}.
     */
    private FactoryException missingFactory(final Class<?> category, final String code) {
        return new NoSuchAuthorityCodeException(Errors.format(Errors.Keys.FactoryNotFound_1,
                category), Citations.getIdentifier(getAuthority()), trimAuthority(code), code);
    }

    /**
     * For internal use by {@link #getAuthority} and {@link #getVendor} only. Its only purpose
     * is to catch the {@link FactoryException} for methods that don't allow it. The protected
     * method should be used instead when this exception is allowed.
     *
     * @throws FactoryRegistryException If the search for factories has been deferred until now,
     *         and we failed to get those factories.
     */
    private AuthorityFactory getAuthorityFactory() throws FactoryRegistryException {
        try {
            return getAuthorityFactory(null);
        } catch (FactoryException cause) {
            throw new IllegalStateException(Errors.format(Errors.Keys.UndefinedProperty), cause);
        }
    }

    /**
     * Returns an authority factory of the specified type. The default implementation delegates to:
     * <p>
     * <ul>
     *   <li>{@link #getCRSAuthorityFactory} if {@code type} is
     *       {@code CRSAuthorityFactory.class};</li>
     *   <li>{@link #getCSAuthorityFactory} if {@code type} is
     *       {@code CSAuthorityFactory.class};</li>
     *   <li>{@link #getDatumAuthorityFactory} if {@code type} is
     *       {@code DatumAuthorityFactory.class};</li>
     *   <li>{@link #getCoordinateOperationAuthorityFactory} if {@code type} is
     *       {@code CoordinateOperationAuthorityFactory.class};</li>
     * </ul>
     *
     * @param  <T> The type of the authority factory to find.
     * @param  type The type of the authority factory to find.
     * @param  code The authority code given to a method of this class. Note that the code to be
     *         given to the returned factory {@linkplain #toBackingFactoryCode may be different}.
     * @return A factory for the specified authority code (never {@code null}).
     * @throws IllegalArgumentException if the specified {@code type} is invalid.
     * @throws FactoryException if no suitable factory were found.
     */
    protected <T extends AuthorityFactory> T getAuthorityFactory(final Class<T> type, final String code)
            throws FactoryException
    {
        final AuthorityFactory f;
        if (type == CRSAuthorityFactory.class) {
            f = getCRSAuthorityFactory(code);
        } else if (type == CSAuthorityFactory.class) {
            f = getCSAuthorityFactory(code);
        } else if (type == DatumAuthorityFactory.class) {
            f = getDatumAuthorityFactory(code);
        } else if (type == CoordinateOperationAuthorityFactory.class) {
            f = getCoordinateOperationAuthorityFactory(code);
        } else if (type == AuthorityFactory.class) {
            f = getAuthorityFactory(code);
        } else {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.UnknownType_1, type));
        }
        return type.cast(f);
    }

    /**
     * Returns an arbitrary object factory to use for the specified code. The default implementation
     * returns one of the factory specified at construction time. Subclasses can override
     * this method in order to select a different factory implementation depending on the
     * code value.
     * <p>
     * <strong>Note:</strong> The value of the {@code code} argument given to this
     * method may be {@code null} when a factory is needed for some global task,
     * like {@link #getAuthorityCodes} method execution.
     *
     * @param  code The authority code given to a method of this class. Note that the code to be
     *         given to the returned factory {@linkplain #toBackingFactoryCode may be different}.
     * @return A factory for the specified authority code (never {@code null}).
     * @throws FactoryException if no suitable factory were found.
     */
    protected AuthorityFactory getAuthorityFactory(final String code) throws FactoryException {
        ensureInitialized();
        for (int f=0; f<TYPE_COUNT; f++) {
            final AuthorityFactory factory = getFactory(f);
            if (factory != null) {
                return factory;
            }
        }
        throw missingFactory(AuthorityFactory.class, code);
    }

    /**
     * Returns the datum factory to use for the specified code. The default implementation
     * always returns the factory specified at construction time. Subclasses can override
     * this method in order to select a different factory implementation depending on the
     * code value.
     *
     * @param  code The authority code given to this class. Note that the code to be given
     *         to the returned factory {@linkplain #toBackingFactoryCode may be different}.
     * @return A factory for the specified authority code (never {@code null}).
     * @throws FactoryException if no datum factory were specified at construction time.
     *
     * @since 2.4
     */
    protected DatumAuthorityFactory getDatumAuthorityFactory(final String code)
            throws FactoryException
    {
        ensureInitialized();
        final DatumAuthorityFactory factory = datumFactory;
        if (factory == null) {
            throw missingFactory(DatumAuthorityFactory.class, code);
        }
        return factory;
    }

    /**
     * Returns the coordinate system factory to use for the specified code. The default
     * implementation always returns the factory specified at construction time. Subclasses
     * can override this method in order to select a different factory implementation
     * depending on the code value.
     *
     * @param  code The authority code given to this class. Note that the code to be given
     *         to the returned factory {@linkplain #toBackingFactoryCode may be different}.
     * @return A factory for the specified authority code (never {@code null}).
     * @throws FactoryException if no coordinate system factory were specified at construction time.
     *
     * @since 2.4
     */
    protected CSAuthorityFactory getCSAuthorityFactory(final String code)
            throws FactoryException
    {
        ensureInitialized();
        final CSAuthorityFactory factory = csFactory;
        if (factory == null) {
            throw missingFactory(CSAuthorityFactory.class, code);
        }
        return factory;
    }

    /**
     * Returns the coordinate reference system factory to use for the specified code. The default
     * implementation always returns the factory specified at construction time. Subclasses can
     * override this method in order to select a different factory implementation depending on
     * the code value.
     *
     * @param  code The authority code given to this class. Note that the code to be given
     *         to the returned factory {@linkplain #toBackingFactoryCode may be different}.
     * @return A factory for the specified authority code (never {@code null}).
     * @throws FactoryException if no coordinate reference system factory were specified
     *         at construction time.
     *
     * @since 2.4
     */
    protected CRSAuthorityFactory getCRSAuthorityFactory(final String code)
            throws FactoryException
    {
        ensureInitialized();
        final CRSAuthorityFactory factory = crsFactory;
        if (factory == null) {
            throw missingFactory(CRSAuthorityFactory.class, code);
        }
        return factory;
    }

    /**
     * Returns the coordinate operation factory to use for the specified code. The default
     * implementation always returns the factory specified at construction time. Subclasses can
     * override this method in order to select a different factory implementation depending on
     * the code value.
     *
     * @param  code The authority code given to this class. Note that the code to be given
     *         to the returned factory {@linkplain #toBackingFactoryCode may be different}.
     * @return A factory for the specified authority code (never {@code null}).
     * @throws FactoryException if no coordinate operation factory were specified
     *         at construction time.
     *
     * @since 2.4
     */
    protected CoordinateOperationAuthorityFactory getCoordinateOperationAuthorityFactory(final String code)
            throws FactoryException
    {
        ensureInitialized();
        final CoordinateOperationAuthorityFactory factory = operationFactory;
        if (factory == null) {
            throw missingFactory(CoordinateOperationAuthorityFactory.class, code);
        }
        return factory;
    }

    /**
     * Returns a coordinate operation factory for this adapter. This method will try to fetch
     * this information from the coordinate operation authority factory, or will returns the
     * default one if no explicit factory were found.
     */
    final CoordinateOperationFactory getCoordinateOperationFactory() throws FactoryException {
        ensureInitialized();
        final CoordinateOperationAuthorityFactory factory = operationFactory;
        if (factory instanceof Factory) {
            final Map<RenderingHints.Key, ?> hints = ((Factory) factory).getImplementationHints();
            final Object candidate = hints.get(Hints.COORDINATE_OPERATION_FACTORY);
            if (candidate instanceof CoordinateOperationFactory) {
                return (CoordinateOperationFactory) candidate;
            }
        }
        return AuthorityFactoryFinder.getCoordinateOperationFactory(dependencyHints());
    }

    /**
     * Modifies the given hints before to search for a backing store factory. This
     * method is invoked when first needed with copy of the hints given to the
     * {@linkplain #AuthorityFactoryAdapter(String, Hints) constructor}. They are
     * the hints which will be given to {@link AuthorityFactoryFinder} for searching
     * a factory. Subclasses can modify this set of hints in-place before they are
     * given to {@code AuthorityFactoryFinder}.
     * <p>
     * This is a hook to be overridden by {@link OrderedAxisAuthorityFactory}.
     *
     * @param hints The hints to modify in-place.
     */
    void toBackingFactoryHints(final Hints hints) {
    }

    /**
     * Returns the code to be given to the wrapped factories. This method is automatically
     * invoked by all {@code create} methods before to forward the code to the
     * {@linkplain #getCRSAuthorityFactory CRS}, {@linkplain #getCSAuthorityFactory CS},
     * {@linkplain #getDatumAuthorityFactory datum} or {@linkplain #operationFactory operation}
     * factory. The default implementation returns the {@code code} unchanged.
     *
     * @param  code The code given to this factory.
     * @return The code to give to the underlying factories.
     * @throws FactoryException if the code can't be converted.
     *
     * @since 2.4
     */
    protected String toBackingFactoryCode(final String code) throws FactoryException {
        return code;
    }

    /**
     * Returns {@code true} if the {@link #toBackingFactoryCode} method is overridden.
     */
    final boolean isCodeMethodOverridden() {
        final Class<?>[] arguments = new Class<?>[] {String.class};
        Class<?> type = getClass();
        while (type != AuthorityFactoryAdapter.class) {
            try {
                type.getDeclaredMethod("toBackingFactoryCode", arguments);
            } catch (NoSuchMethodException e) {
                // The method is not overridden in this class.
                // Checks in the super-class.
                type = type.getSuperclass();
                continue;
            } catch (SecurityException e) {
                // We are not allowed to get this information.
                // Conservatively assumes that the method is overridden.
            }
            return true;
        }
        return false;
    }
}

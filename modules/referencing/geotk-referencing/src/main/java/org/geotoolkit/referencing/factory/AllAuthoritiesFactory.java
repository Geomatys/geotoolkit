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
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.FactoryException;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;
import org.opengis.referencing.NoSuchAuthorityCodeException; // For javadoc

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.apache.sis.util.collection.UnmodifiableArrayList;
import org.apache.sis.util.collection.WeakHashSet;
import org.geotoolkit.internal.FactoryUtilities;

import static org.apache.sis.util.collection.CollectionsExt.isNullOrEmpty;


/**
 * An authority factory that delegates the object creation to an other factory determined from the
 * authority part in {@code "authority:code"} arguments. The set of factories to use as delegates
 * is determined from the {@link AuthorityFactoryFinder} with the hints given at construction time.
 * <p>
 * This factory requires that every codes given to a {@code createXXX(String)} method are prefixed
 * by the authority name, for example {@code "EPSG:4326"}. When a {@code createXXX(String)} method
 * is invoked, this class extracts the authority name from the {@code "authority:code"} argument.
 * Then is searches for a factory for that authority in the first of the following sets:
 * <p>
 * <ol>
 *   <li>The factories given at construction time under the {@link #USER_FACTORIES_KEY} key, if
 *       any. Those factories <cite>override</cite> the factories normally found by this class
 *       at the step below.</li>
 *   <li>The factories registered in {@link AuthorityFactoryFinder} and compatible with the
 *       hints given at construction time.</li>
 * </ol>
 * <p>
 * If a factory is found, then the work is delegated to that factory. Otherwise a
 * {@link NoSuchAuthorityCodeException} is thrown.
 * <p>
 * This class is not registered in {@code AuthorityFactoryFinder}. If this factory
 * is wanted, then users need to invoke explicitly the {@link #getInstance(Hints)} method.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.05
 *
 * @since 2.2
 * @module
 */
@ThreadSafe
public class AllAuthoritiesFactory extends MultiAuthoritiesFactory {
    /**
     * The key for an optional value in the {@link Hints} map specifying custom factories
     * in addition to the ones registered in {@link AuthorityFactoryFinder}. The value, if
     * defined, must be an instance of {@code Collection<? extends AuthorityFactory>}.
     * <p>
     * If the {@code Hints} map given to the {@linkplain #AllAuthoritiesFactory(Hints) constructor}
     * contains a value for {@code USER_FACTORIES_KEY}, then when a {@code create} method is invoked,
     * {@code AllAuthoritiesFactory} will first search in the given collection of factories. If no
     * factory is found in that collection, only then the usual {@link AuthorityFactoryFinder} will
     * be used as a fallback.
     *
     * @since 3.03
     */
    public static final Hints.Key USER_FACTORIES_KEY = new Hints.Key(Collection.class);

    /**
     * Pool of existing instances.
     */
    private static final WeakHashSet<AllAuthoritiesFactory> POOL =
            new WeakHashSet<>(AllAuthoritiesFactory.class);

    /**
     * The authority names. Used in order to detect changes in the set of registered factories.
     */
    private Collection<String> authorityNames;

    /**
     * Creates a new factory using the specified hints. This constructor is available for
     * subclass constructors only. In order to instantiate a {@code AllAuthoritiesFactory}
     * instance, use {@code getInstance(hints)} instead.
     *
     * @param userHints An optional set of hints, or {@code null} if none.
     */
    protected AllAuthoritiesFactory(final Hints userHints) {
        super(null);
        FactoryUtilities.addImplementationHints(userHints, hints);
        Collection<?> factories = (Collection<?>) hints.get(USER_FACTORIES_KEY);
        if (!isNullOrEmpty(factories)) {
            factories = UnmodifiableArrayList.wrap(factories.toArray(new AuthorityFactory[factories.size()]));
        } else {
            factories = Collections.EMPTY_LIST;
        }
        hints.put(USER_FACTORIES_KEY, factories);
    }

    /**
     * Returns a factory using the specified hints. This method returns a shared instance when
     * possible. Doing so simplify the tree of {@linkplain FactoryDependencies factory dependencies}.
     *
     * @param  hints An optional set of hints, or {@code null} if none.
     * @return A factory using the specified hints.
     *
     * @since 3.00
     */
    public static AllAuthoritiesFactory getInstance(final Hints hints) {
        AllAuthoritiesFactory factory = new AllAuthoritiesFactory(hints);
        if (factory.getUserFactories().isEmpty()) {
            // Cache only the instances having no user-factories, because the probability
            // to share AllAuthoritiesFactory having some user factories is lower.
            factory = POOL.unique(factory);
        }
        return factory;
    }

    /**
     * Returns the set of authority names.
     *
     * @since 2.4
     */
    @Override
    public Set<String> getAuthorityNames() {
        final Set<String> factories = getAuthorityNames(getUserFactories());
        // Do not use 'authorityNames' since it may be out-of-date.
        factories.addAll(AuthorityFactoryFinder.getAuthorityNames());
        return factories;
    }

    /**
     * Returns the user factories. This is the list of factories stored under
     * {@link #USER_FACTORIES_KEY}. This list may be empty but never {@code null}.
     *
     * @since 3.03
     */
    @SuppressWarnings("unchecked")
    private List<AuthorityFactory> getUserFactories() {
        return (List<AuthorityFactory>) hints.get(USER_FACTORIES_KEY);
    }

    /**
     * Returns the factories on which to delegate object creations. This list is determined from
     * the factories registered in the {@link AuthorityFactoryFinder}, merged with the list of
     * factories specified by the {@link #USER_FACTORIES_KEY} hint if any.
     *
     * @return The factories on which this {@code AllAuthoritiesFactory} will delegate object creations.
     *
     * @since 3.00
     */
    @Override
    public synchronized List<AuthorityFactory> getFactories() {
        final Collection<String> authorities = AuthorityFactoryFinder.getAuthorityNames();
        if (authorities != authorityNames) {
            authorityNames = authorities;
            final Hints hints = getHints();
            final Set<AuthorityFactory> factories = new LinkedHashSet<>(getUserFactories());
            final Set<String> names = getAuthorityNames(factories);
typeLoop:   for (int i=0; ; i++) {
                final Set<? extends AuthorityFactory> c;
                switch (i) {
                    case 0: c = AuthorityFactoryFinder.getCoordinateOperationAuthorityFactories(hints); break;
                    case 1: c = AuthorityFactoryFinder.getDatumAuthorityFactories(hints); break;
                    case 2: c = AuthorityFactoryFinder.getCSAuthorityFactories(hints); break;
                    case 3: c = AuthorityFactoryFinder.getCRSAuthorityFactories(hints); break;
                    default: break typeLoop;
                }
                /*
                 * Add only the factories that are not others MultiAuthoritiesFactory. To be
                 * more specific, the intend is actually to exclude the URN or HTTP wrappers.
                 * This is necessary because this method is invoked indirectly by the Finder
                 * inner class (see MultiAuthoritiesFactory.Finder.getFactories()) and we want
                 * to avoid duplicated searches.
                 */
                for (final AuthorityFactory f : c) {
                    if (!isExcluded(f)) {
                        // Add the factory only if it is not overridden by a user-specified one.
                        if (!names.contains(Citations.getIdentifier(f.getAuthority()))) {
                            factories.add(f);
                        }
                    }
                }
            }
            setFactories(factories);
        }
        return super.getFactories();
    }

    /**
     * Returns a factory for the specified authority and type.
     */
    @Override
    final <T extends AuthorityFactory> T fromFactoryRegistry(final String authority, final Class<T> type)
            throws FactoryRegistryException
    {
        final AuthorityFactory f;
        if (type == CRSAuthorityFactory.class) {
            f = AuthorityFactoryFinder.getCRSAuthorityFactory(authority, getHints());
        } else if (type == CSAuthorityFactory.class) {
            f = AuthorityFactoryFinder.getCSAuthorityFactory(authority, getHints());
        } else if (type == DatumAuthorityFactory.class) {
            f = AuthorityFactoryFinder.getDatumAuthorityFactory(authority, getHints());
        } else if (type == CoordinateOperationAuthorityFactory.class) {
            f = AuthorityFactoryFinder.getCoordinateOperationAuthorityFactory(authority, getHints());
        } else {
            f = super.fromFactoryRegistry(authority, type);
        }
        return type.cast(f);
    }

    /**
     * Returns a copy of the hints specified by the user at construction time.
     */
    private Hints getHints() {
        if (hints.isEmpty()) {
            return EMPTY_HINTS;
        } else {
            // Clones EMPTY_HINTS as a trick for getting Hints without system-wide defaults.
            final Hints hints = EMPTY_HINTS.clone();
            hints.putAll(this.hints);
            return hints;
        }
    }

    /**
     * Returns a finder which can be used for looking up unidentified objects.
     * The default implementation delegates the lookups to the underlying factories.
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
     * A {@link IdentifiedObjectFinder} which tests every factories. This {@code Finder}
     * does the same work than the super-class except that if no CRS were found using the
     * factories explicitly declared in the collection, then the factories registered in
     * {@link AuthorityFactoryFinder} will be tried. A mechanism avoid trying the same
     * factory twice.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.18
     *
     * @since 2.2
     * @module
     */
    private static final class Finder extends MultiAuthoritiesFactory.Finder {
        /**
         * Creates a finder for the specified type.
         */
        protected Finder(final MultiAuthoritiesFactory factory,
                         final Class<? extends IdentifiedObject> type)
        {
            super(factory, type);
        }

        /**
         * Returns all factories to try. This method is invoked by the {@code find} methods in
         * order to get the list of factories to try iteratively, with the iteration stopping
         * on the first factory that found the object.
         * <p>
         * For performance raison, the returned set should exclude wrappers around
         * {@link MultiAuthoritiesFactory}, because using those wrappers cause the
         * search to be performed (indirectly) twice for the same factories.
         */
        private Set<AuthorityFactory> fromFactoryRegistry() {
            final MultiAuthoritiesFactory factory = (MultiAuthoritiesFactory) this.factory;
            final Class<? extends AuthorityFactory> type = getFactoryType();
            final Set<AuthorityFactory> factories = new LinkedHashSet<>();
            for (final String authority : AuthorityFactoryFinder.getAuthorityNames()) {
                factory.fromFactoryRegistry(authority, type, factories);
            }
            // Removes the factories already tried by super-class.
            final Collection<AuthorityFactory> done = getFactories();
            if (done != null) {
                factories.removeAll(done);
            }
            return factories;
        }

        /**
         * Lookups for the specified object. First, this method uses the algorithm defined
         * in {@link MultiAuthoritiesFactory} super-class in order to scan the factories
         * that were explicitly specified by the user. Only if no suitable factory is found,
         * this method fallbacks on the factories registered in {@link AuthorityFactoryFinder}.
         */
        @Override
        public IdentifiedObject find(final IdentifiedObject object) throws FactoryException {
            IdentifiedObject candidate = super.find(object);
            if (candidate != null) {
                return candidate;
            }
            IdentifiedObjectFinder finder;
            final Iterator<AuthorityFactory> it = fromFactoryRegistry().iterator();
            while ((finder = next(it)) != null) {
                candidate = finder.find(object);
                if (candidate != null) {
                    break;
                }
            }
            return candidate;
        }
    }
}

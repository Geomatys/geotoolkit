/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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
import java.util.LinkedHashSet;

import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.util.collection.CanonicalSet;
import org.geotoolkit.internal.FactoryUtilities;


/**
 * An authority factory that delegates the object creation to an other factory determined
 * from the authority name in the code. This is similar to {@link MultiAuthoritiesFactory}
 * except that the set of factories is determined by calls to
 * <code>AuthorityFactoryFinder.{@linkplain AuthorityFactoryFinder#getCRSAuthorityFactory
 * get<var>Foo</var>AuthorityFactory}(<var>authority</var>, {@linkplain #hints hints})</code>.
 * <p>
 * This class is not registered in {@link AuthorityFactoryFinder}. If this "authority" factory
 * is wanted, then users need to invoke explicitly the {@link #getInstance} method.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
public class AllAuthoritiesFactory extends MultiAuthoritiesFactory {
    /**
     * Pool of existing instances.
     */
    private static final CanonicalSet<AllAuthoritiesFactory> POOL =
            CanonicalSet.newInstance(AllAuthoritiesFactory.class);

    /**
     * The authority names. Used in order to detect changes in the set of registered factories.
     */
    private Collection<String> authorityNames;

    /**
     * Creates a new factory using the specified hints.
     *
     * @param userHints An optional set of hints, or {@code null} if none.
     */
    protected AllAuthoritiesFactory(final Hints userHints) {
        super(null);
        FactoryUtilities.addImplementationHints(userHints, hints);
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
        return POOL.unique(new AllAuthoritiesFactory(hints));
    }

    /**
     * Returns the set of authority names.
     *
     * @since 2.4
     */
    @Override
    public Set<String> getAuthorityNames() {
        // Do not use 'authorityNames' since it may be out-of-date.
        return AuthorityFactoryFinder.getAuthorityNames();
    }

    /**
     * Returns the factories on which to delegate object creations. This list is determined from
     * the factories registered in the {@link AuthorityFactoryFinder}.
     *
     * @return The factories on which this {@code AllAuthoritiesFactory} will delegate
     *         object creations, in iteration order.
     *
     * @since 3.00
     */
    @Override
    public synchronized List<AuthorityFactory> getFactories() {
        final Collection<String> authorities = AuthorityFactoryFinder.getAuthorityNames();
        if (authorities != authorityNames) {
            authorityNames = authorities;
            final Hints hints = getHints();
            final Set<AuthorityFactory> factories = new LinkedHashSet<AuthorityFactory>();
typeLoop:   for (int i=0; ; i++) {
                final Set<? extends AuthorityFactory> c;
                switch (i) {
                    case 0: c = AuthorityFactoryFinder.getCoordinateOperationAuthorityFactories(hints); break;
                    case 1: c = AuthorityFactoryFinder.getDatumAuthorityFactories              (hints); break;
                    case 2: c = AuthorityFactoryFinder.getCSAuthorityFactories                 (hints); break;
                    case 3: c = AuthorityFactoryFinder.getCRSAuthorityFactories                (hints); break;
                    default: break typeLoop;
                }
                // Add to the set only the factories that are not excluded.
                for (final AuthorityFactory f : c) {
                    if (!isExcluded(f)) {
                        factories.add(f);
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
        if (CRSAuthorityFactory.class.equals(type)) {
            f = AuthorityFactoryFinder.getCRSAuthorityFactory(authority, getHints());
        } else if (CSAuthorityFactory.class.equals(type)) {
            f = AuthorityFactoryFinder.getCSAuthorityFactory(authority, getHints());
        } else if (DatumAuthorityFactory.class.equals(type)) {
            f = AuthorityFactoryFinder.getDatumAuthorityFactory(authority, getHints());
        } else if (CoordinateOperationAuthorityFactory.class.equals(type)) {
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
     * A {@link IdentifiedObjectFinder} which tests every factories.
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
         * Returns all factories to try.
         */
        private Set<AuthorityFactory> fromFactoryRegistry() {
            final MultiAuthoritiesFactory factory = (MultiAuthoritiesFactory) proxy.getAuthorityFactory();
            final Class<? extends AuthorityFactory> type = getFactoryType();
            final Set<AuthorityFactory> factories = new LinkedHashSet<AuthorityFactory>();
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
         * Lookups for the specified object.
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

        /**
         * Returns the identifier of the specified object, or {@code null} if none.
         */
        @Override
        public String findIdentifier(final IdentifiedObject object) throws FactoryException {
            String candidate = super.findIdentifier(object);
            if (candidate != null) {
                return candidate;
            }
            IdentifiedObjectFinder finder;
            final Iterator<AuthorityFactory> it = fromFactoryRegistry().iterator();
            while ((finder = next(it)) != null) {
                candidate = finder.findIdentifier(object);
                if (candidate != null) {
                    break;
                }
            }
            return candidate;
        }
    }
}

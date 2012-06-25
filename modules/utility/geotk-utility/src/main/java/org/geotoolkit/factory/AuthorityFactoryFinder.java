/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.factory;

import java.util.Set;
import java.util.LinkedHashSet;
import javax.imageio.spi.ServiceRegistry;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;

import org.geotoolkit.lang.Configuration;
import org.geotoolkit.internal.Citations;
import org.geotoolkit.util.collection.XCollections;


/**
 * Defines static methods used to access the application's {@linkplain AuthorityFactory authority
 * factory} implementations. This class provide access to the following services:
 * <p>
 * <ul>
 *   <li><b>Referencing</b></li><ul>
 *     <li>{@link CoordinateOperationAuthorityFactory}</li>
 *     <li>{@link CRSAuthorityFactory}</li>
 *     <li>{@link CSAuthorityFactory}</li>
 *     <li>{@link DatumAuthorityFactory}</li>
 *   </ul>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @level basic
 * @module
 */
@ThreadSafe
public final class AuthorityFactoryFinder extends FactoryFinder {
    /**
     * The authority names. Will be created only when first needed.
     */
    static Set<String> authorityNames;

    /**
     * Do not allow instantiation of this class.
     */
    private AuthorityFactoryFinder() {
    }

    /**
     * Returns the names of all currently registered authorities.
     *
     * @return The set of all currently registered authorities.
     */
    public static synchronized Set<String> getAuthorityNames() {
        /*
         * IMPORTANT: Return the same Set instance (unmodifiable) as long as there is no change
         * in the list of registered factories, and create a new instance in case of changes.
         * 'add/removeAuthorityFactory(...)' and 'scanForPlugins()' methods reset 'authorityNames'
         * to null, which will cause the creation of a new Set instance. Some implementations like
         * AllAuthoritiesFactory rely on this behavior as a way to be notified of registration
         * changes for clearing their cache.
         */
        if (authorityNames == null) {
            authorityNames = new LinkedHashSet<String>();
            final Hints hints = org.geotoolkit.factory.Factory.EMPTY_HINTS;
loop:       for (int i=0; ; i++) {
                final Set<? extends AuthorityFactory> factories;
                switch (i) {
                    case 0:  factories = getCRSAuthorityFactories(hints);                 break;
                    case 1:  factories = getCSAuthorityFactories(hints);                  break;
                    case 2:  factories = getDatumAuthorityFactories(hints);               break;
                    case 3:  factories = getCoordinateOperationAuthorityFactories(hints); break;
                    default: break loop;
                }
                for (final AuthorityFactory factory : factories) {
                    final Citation authority = factory.getAuthority();
                    if (authority != null) {
                        authorityNames.add(Citations.getIdentifier(authority));
                        for (final Identifier id : authority.getIdentifiers()) {
                            authorityNames.add(id.getCode());
                        }
                    }
                }
            }
            authorityNames = XCollections.unmodifiableSet(authorityNames);
        }
        return authorityNames;
    }

    /**
     * Returns the first implementation of a factory matching the specified hints. If no
     * implementation matches, a new one is created if possible or an exception is thrown
     * otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     *
     * @param  category  The authority factory type.
     * @param  authority The desired authority (e.g. "EPSG").
     * @param  hints     An optional map of hints, or {@code null} for the default ones.
     * @param  key       The hint key to use for searching an implementation.
     * @return The first authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         specfied interface.
     */
    private static <T extends AuthorityFactory> T getAuthorityFactory(
            final Class<T> category, final String authority, Hints hints, final Hints.ClassKey key)
            throws FactoryRegistryException
    {
        hints = mergeSystemHints(hints);
        ServiceRegistry.Filter filter = (ServiceRegistry.Filter) hints.remove(FILTER_KEY);
        filter = new AuthorityFilter(authority, filter);
        synchronized (FactoryFinder.class) {
            return getServiceRegistry().getServiceProvider(category, filter, hints, key);
        }
    }

    /**
     * Returns the first implementation of {@link DatumAuthorityFactory} matching the specified
     * hints. If no implementation matches, a new one is created if possible or an exception is
     * thrown otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     *
     * @param  authority The desired authority (e.g. "EPSG").
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first datum authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link DatumAuthorityFactory} interface.
     *
     * @category Referencing
     */
    public static DatumAuthorityFactory getDatumAuthorityFactory(final String authority, final Hints hints)
            throws FactoryRegistryException
    {
        return getAuthorityFactory(DatumAuthorityFactory.class, authority, hints, Hints.DATUM_AUTHORITY_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link DatumAuthorityFactory}
     * interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available datum authority factory implementations.
     *
     * @category Referencing
     */
    public static Set<DatumAuthorityFactory> getDatumAuthorityFactories(final Hints hints) {
        return getFactories(DatumAuthorityFactory.class, hints, Hints.DATUM_AUTHORITY_FACTORY);
    }

    /**
     * Returns the first implementation of {@link CSAuthorityFactory} matching the specified
     * hints. If no implementation matches, a new one is created if possible or an exception is
     * thrown otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     * <p>
     * Hints that may be understood includes
     * {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER FORCE_LONGITUDE_FIRST_AXIS_ORDER},
     * {@link Hints#FORCE_STANDARD_AXIS_UNITS        FORCE_STANDARD_AXIS_UNITS} and
     * {@link Hints#FORCE_STANDARD_AXIS_DIRECTIONS   FORCE_STANDARD_AXIS_DIRECTIONS} and
     * {@link Hints#VERSION                          VERSION}.
     *
     * @param  authority The desired authority (e.g. "EPSG").
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first coordinate system authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CSAuthorityFactory} interface.
     *
     * @category Referencing
     */
    public static CSAuthorityFactory getCSAuthorityFactory(final String authority, final Hints hints)
            throws FactoryRegistryException
    {
        return getAuthorityFactory(CSAuthorityFactory.class, authority, hints, Hints.CS_AUTHORITY_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link CSAuthorityFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available coordinate system authority factory implementations.
     *
     * @category Referencing
     */
    public static Set<CSAuthorityFactory> getCSAuthorityFactories(final Hints hints) {
        return getFactories(CSAuthorityFactory.class, hints, Hints.CS_AUTHORITY_FACTORY);
    }

    /**
     * Returns the first implementation of {@link CRSAuthorityFactory} matching the specified
     * hints. If no implementation matches, a new one is created if possible or an exception is
     * thrown otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     * <p>
     * Hints that may be understood includes
     * {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER FORCE_LONGITUDE_FIRST_AXIS_ORDER},
     * {@link Hints#FORCE_STANDARD_AXIS_UNITS        FORCE_STANDARD_AXIS_UNITS},
     * {@link Hints#FORCE_STANDARD_AXIS_DIRECTIONS   FORCE_STANDARD_AXIS_DIRECTIONS} and
     * {@link Hints#VERSION                          VERSION}.
     * <p>
     * <b>TIP:</b> The EPSG official factory and the EPSG extensions (additional CRS provided by
     * ESRI and others) are two distinct factories. Call to {@code getCRSAuthorityFactory("EPSG",
     * null)} returns only one of those, usually the official EPSG factory. If the union of those
     * two factories is wanted, then a chain of fallbacks is wanted. Consider using something like:
     *
     * {@preformat java
     *     FallbackAuthorityFactory.create(CRSAuthorityFactory.class, getCRSAuthorityFactories(hints));
     * }
     *
     * @param  authority The desired authority (e.g. "EPSG").
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first coordinate reference system authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CRSAuthorityFactory} interface.
     *
     * @see org.geotoolkit.referencing.factory.FallbackAuthorityFactory#create(Class, java.util.Collection)
     * @category Referencing
     */
    public static CRSAuthorityFactory getCRSAuthorityFactory(final String authority, final Hints hints)
            throws FactoryRegistryException
    {
        return getAuthorityFactory(CRSAuthorityFactory.class, authority, hints, Hints.CRS_AUTHORITY_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link CRSAuthorityFactory} interface.
     * This set can be used to list the available codes known to all authorities. In the event that
     * the same code is understood by more then one authority, user may assume both are close enough,
     * or make use of this set directly rather than use the {@link org.geotoolkit.referencing.CRS#decode}
     * convenience method.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available coordinate reference system authority factory implementations.
     *
     * @category Referencing
     */
    public static Set<CRSAuthorityFactory> getCRSAuthorityFactories(final Hints hints) {
        return getFactories(CRSAuthorityFactory.class, hints, Hints.CRS_AUTHORITY_FACTORY);
    }

    /**
     * Returns the first implementation of {@link CoordinateOperationAuthorityFactory} matching
     * the specified hints. If no implementation matches, a new one is created if possible or an
     * exception is thrown otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     *
     * @param  authority The desired authority (e.g. "EPSG").
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first coordinate operation authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CoordinateOperationAuthorityFactory} interface.
     *
     * @category Referencing
     */
    public static CoordinateOperationAuthorityFactory getCoordinateOperationAuthorityFactory(
            final String authority, final Hints hints) throws FactoryRegistryException
    {
        return getAuthorityFactory(CoordinateOperationAuthorityFactory.class, authority, hints,
                Hints.COORDINATE_OPERATION_AUTHORITY_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the
     * {@link CoordinateOperationAuthorityFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available coordinate operation authority factory implementations.
     *
     * @category Referencing
     */
    public static Set<CoordinateOperationAuthorityFactory>
            getCoordinateOperationAuthorityFactories(final Hints hints)
    {
        return getFactories(CoordinateOperationAuthorityFactory.class, hints,
                Hints.COORDINATE_OPERATION_AUTHORITY_FACTORY);
    }

    /**
     * Sets a pairwise ordering between two authorities. If one or both authorities are not
     * currently registered, or if the desired ordering is already set, nothing happens
     * and {@code false} is returned.
     * <p>
     * The example below said that EPSG {@linkplain AuthorityFactory authority factories}
     * are preferred over ESRI ones:
     *
     * {@preformat java
     *     FactoryFinder.setAuthorityOrdering("EPSG", "ESRI");
     * }
     *
     * @param  authority1 The preferred authority.
     * @param  authority2 The authority to which {@code authority1} is preferred.
     * @return {@code true} if the ordering was set for at least one category.
     *
     * @see #setVendorOrdering(String, String)
     */
    @Configuration
    public static boolean setAuthorityOrdering(final String authority1, final String authority2) {
        final AuthorityFilter filter1 = new AuthorityFilter(authority1, null);
        final AuthorityFilter filter2 = new AuthorityFilter(authority2, null);
        final boolean changed;
        synchronized (FactoryFinder.class) {
            changed = getServiceRegistry().setOrdering(AuthorityFactory.class, filter1, filter2);
        }
        if (changed) {
            Factories.fireConfigurationChanged(AuthorityFactoryFinder.class);
        }
        return changed;
    }

    /**
     * Unsets a pairwise ordering between two authorities. If one or both authorities are not
     * currently registered, or if the desired ordering is already unset, nothing happens
     * and {@code false} is returned.
     *
     * @param  authority1 The preferred authority.
     * @param  authority2 The vendor to which {@code authority1} is preferred.
     * @return {@code true} if the ordering was unset for at least one category.
     *
     * @see #unsetVendorOrdering(String, String)
     */
    @Configuration
    public static boolean unsetAuthorityOrdering(final String authority1, final String authority2) {
        final AuthorityFilter filter1 = new AuthorityFilter(authority1, null);
        final AuthorityFilter filter2 = new AuthorityFilter(authority2, null);
        final boolean changed;
        synchronized (FactoryFinder.class) {
            changed = getServiceRegistry().unsetOrdering(AuthorityFactory.class, filter1, filter2);
        }
        if (changed) {
            Factories.fireConfigurationChanged(AuthorityFactoryFinder.class);
        }
        return changed;
    }

    /**
     * A filter for factories provided for a given authority.
     */
    private static final class AuthorityFilter implements ServiceRegistry.Filter {
        /**
         * The authority to filter.
         */
        private final String authority;

        /**
         * A user-provided filter, or {@code null} if none.
         */
        private final ServiceRegistry.Filter filter;

        /**
         * Constructs a filter for the given authority.
         */
        public AuthorityFilter(final String authority, final ServiceRegistry.Filter filter) {
            this.authority = authority;
            this.filter    = filter;
        }

        /**
         * Returns {@code true} if the specified provider is for the {@link #authority}.
         * The user provided filter (if any) is also applied.
         */
        @Override
        public boolean filter(final Object provider) {
            if (authority != null) {
                final Citation declared = ((AuthorityFactory) provider).getAuthority();
                if (Citations.identifierMatches(declared, authority)) {
                    return (filter == null) || filter.filter(provider);
                }
            }
            // If the user didn't specified an authority name, then the factory to use must
            // be specified explicitly through a hint (e.g. Hints.CRS_AUTHORITY_FACTORY).
            return false;
        }
    }

    /**
     * Programmatic addition of authority factories.
     * Needed for user managed, not plug-in managed, authority factory.
     * Also useful for test cases.
     *
     * @param authority The authority factory to add.
     */
    @Configuration
    public static void addAuthorityFactory(final AuthorityFactory authority) {
        synchronized (AuthorityFactoryFinder.class) {
            authorityNames = null;
            synchronized (FactoryFinder.class) {
                final boolean needScan = (registry == null);
                final FactoryRegistry registry = getServiceRegistry();
                if (needScan) {
                    registry.scanForPlugins();
                }
                registry.registerServiceProvider(authority);
            }
        }
        Factories.fireConfigurationChanged(AuthorityFactoryFinder.class);
    }

    /**
     * Programmatic removal of authority factories.
     * Needed for user managed, not plug-in managed, authority factory.
     * Also useful for test cases.
     *
     * @param authority The authority factory to remove.
     */
    @Configuration
    public static void removeAuthorityFactory(final AuthorityFactory authority) {
        synchronized (AuthorityFactoryFinder.class) {
            authorityNames = null;
            synchronized (FactoryFinder.class) {
                getServiceRegistry().deregisterServiceProvider(authority);
            }
        }
        Factories.fireConfigurationChanged(AuthorityFactoryFinder.class);
    }
}

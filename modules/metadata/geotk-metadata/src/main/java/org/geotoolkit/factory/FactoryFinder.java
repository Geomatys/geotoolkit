/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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
import java.util.Locale;
import java.util.Iterator;
import java.util.Collections;
import javax.imageio.spi.ServiceRegistry;
import java.io.IOException;
import java.io.Writer;

import org.opengis.util.NameFactory;
import org.opengis.style.StyleFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.Factory;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;
import org.opengis.metadata.citation.CitationFactory;

import org.geotoolkit.lang.Static;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.internal.LazySet;


/**
 * Defines static methods used to access the application's {@linkplain Factory factory}
 * implementations. This class provide access to the following services:
 * <p>
 * <ul>
 *   <li>{@link CitationFactory} (metadata)</li>
 *   <li>{@link CoordinateOperationFactory} (referencing)</li>
 *   <li>{@link CRSFactory} (referencing)</li>
 *   <li>{@link CSFactory} (referencing)</li>
 *   <li>{@link DatumFactory} (referencing)</li>
 *   <li>{@link MathTransformFactory} (referencing)</li>
 *   <li>{@link NameFactory} (utilities)</li>
 * </ul>
 * <p>
 * This class is thread-safe but may have a high contention. Applications (or computational units in
 * an application) are encouraged to save references to the factories they need in their own private
 * fields. They would gain in performance and in stability, since the set of available factories may
 * change during the execution.
 * <p>
 * Some methods like {@link #setVendorOrdering setVendorOrdering} have a system-wide effect. Most
 * applications should not need to invoke them. If an application needs to protect itself against
 * configuration changes that may be performed by an other application sharing the Geotoolkit library,
 * it shall manage its own instance of {@link FactoryRegistry}. This {@code FactoryFinder} class
 * itself is just a convenience wrapper around a {@code FactoryRegistry} instance.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.1
 * @level basic
 * @module
 */
@Static
public class FactoryFinder {
    /**
     * The service registry for this manager.
     * Will be initialized only when first needed.
     */
    static FactoryRegistry registry;

    /**
     * Do not allow instantiation of this class.
     */
    FactoryFinder() {
    }

    /**
     * Returns new hints that combine user supplied hints with the default hints.
     * If a hint is specified in both user and default hints, then user hints have
     * precedence.
     * <p>
     * In a previous Geotoolkit version, a somewhat convolved lookup was performed here.
     * Now that default hints are filled right at {@link Hints} creation time, this
     * method just needs to ensure that the given hints are not-null.
     *
     * @param  hints The user hints, or {@code null} for the default ones.
     * @return The hints to use (never {@code null}).
     */
    static Hints mergeSystemHints(Hints hints) {
        if (hints == null) {
            hints = new Hints();
        }
        return hints;
    }

    /**
     * Returns the service registry. The registry will be created the first
     * time this method is invoked.
     *
     * @return The service registry.
     */
    static FactoryRegistry getServiceRegistry() {
        assert Thread.holdsLock(FactoryFinder.class);
        if (registry == null) {
            registry = new DynamicFactoryRegistry(new Class<?>[] {
                    NameFactory.class,
                    CitationFactory.class,
                    DatumFactory.class,
                    CSFactory.class,
                    CRSFactory.class,
                    MathTransformFactory.class,
                    CoordinateOperationFactory.class,
                    FilterFactory.class,
                    StyleFactory.class,

                    // Used by AuthorityFactoryFinder
                    DatumAuthorityFactory.class,
                    CSAuthorityFactory.class,
                    CRSAuthorityFactory.class,
                    CoordinateOperationAuthorityFactory.class
            });
            ShutdownHook.INSTANCE.register(registry);
        }
        return registry;
    }

    /**
     * Returns all providers of the specified category.
     *
     * @param  category The factory category.
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available factory implementations.
     */
    static <T> Set<T> getFactories(final Class<T> category, Hints hints) {
        hints = mergeSystemHints(hints);
        final Iterator<T> iterator;
        synchronized (FactoryFinder.class) {
            iterator = getServiceRegistry().getServiceProviders(category, null, hints);
        }
        return new LazySet<T>(iterator);
    }

    /**
     * Returns a provider of the specified category.
     *
     * @param  category The factory category.
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @param  key The hint key to use for searching an implementation.
     * @return The first factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         specified interface.
     */
    private static <T> T getFactory(final Class<T> category, Hints hints, final Hints.Key key)
            throws FactoryRegistryException
    {
        hints = mergeSystemHints(hints);
        synchronized (FactoryFinder.class) {
            return getServiceRegistry().getServiceProvider(category, null, hints, key);
        }
    }

    /**
     * Returns the first implementation of {@link NameFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first name factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link NameFactory} interface.
     *
     * @since 3.0
     * @category Metadata
     *
     * @see Hints#NAME_FACTORY
     */
    public static NameFactory getNameFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(NameFactory.class, hints, Hints.NAME_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link NameFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available name factory implementations.
     *
     * @since 3.0
     * @category Metadata
     */
    public static Set<NameFactory> getNameFactories(final Hints hints) {
        return getFactories(NameFactory.class, hints);
    }

    /**
     * Returns the first implementation of {@link CitationFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first citation factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CitationFactory} interface.
     *
     * @since 3.0
     * @category Metadata
     *
     * @see Hints#CITATION_FACTORY
     */
    public static CitationFactory getCitationFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(CitationFactory.class, hints, Hints.CITATION_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link CitationFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available citation factory implementations.
     *
     * @since 3.0
     * @category Metadata
     */
    public static Set<CitationFactory> getCitationFactories(final Hints hints) {
        return getFactories(CitationFactory.class, hints);
    }

    /**
     * Returns the first implementation of {@link DatumFactory} matching the specified hints.
     * If no implementation matches, a new one is created if possible or an exception is thrown
     * otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first datum factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link DatumFactory} interface.
     *
     * @category Referencing
     *
     * @see Hints#DATUM_FACTORY
     */
    public static DatumFactory getDatumFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(DatumFactory.class, hints, Hints.DATUM_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link DatumFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available datum factory implementations.
     *
     * @category Referencing
     */
    public static Set<DatumFactory> getDatumFactories(final Hints hints) {
        return getFactories(DatumFactory.class, hints);
    }

    /**
     * Returns the first implementation of {@link CSFactory} matching the specified hints.
     * If no implementation matches, a new one is created if possible or an exception is thrown
     * otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first coordinate system factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CSFactory} interface.
     *
     * @category Referencing
     *
     * @see Hints#CS_FACTORY
     */
    public static CSFactory getCSFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(CSFactory.class, hints, Hints.CS_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link CSFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available coordinate system factory implementations.
     *
     * @category Referencing
     */
    public static Set<CSFactory> getCSFactories(final Hints hints) {
        return getFactories(CSFactory.class, hints);
    }

    /**
     * Returns the first implementation of {@link CRSFactory} matching the specified hints.
     * If no implementation matches, a new one is created if possible or an exception is thrown
     * otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first coordinate reference system factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CRSFactory} interface.
     *
     * @category Referencing
     *
     * @see Hints#CRS_FACTORY
     */
    public static CRSFactory getCRSFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(CRSFactory.class, hints, Hints.CRS_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link CRSFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available coordinate reference system factory implementations.
     *
     * @category Referencing
     */
    public static Set<CRSFactory> getCRSFactories(final Hints hints) {
        return getFactories(CRSFactory.class, hints);
    }

    /**
     * Returns the first implementation of {@link CoordinateOperationFactory} matching the specified
     * hints. If no implementation matches, a new one is created if possible or an exception is
     * thrown otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     * <p>
     * Hints that may be understood includes
     * {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM_FACTORY},
     * {@link Hints#DATUM_SHIFT_METHOD     DATUM_SHIFT_METHOD},
     * {@link Hints#LENIENT_DATUM_SHIFT    LENIENT_DATUM_SHIFT} and
     * {@link Hints#VERSION                VERSION}.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first coordinate operation factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CoordinateOperationFactory} interface.
     *
     * @category Referencing
     *
     * @see Hints#COORDINATE_OPERATION_FACTORY
     */
    public static CoordinateOperationFactory getCoordinateOperationFactory(final Hints hints)
            throws FactoryRegistryException
    {
        return getFactory(CoordinateOperationFactory.class, hints, Hints.COORDINATE_OPERATION_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the
     * {@link CoordinateOperationFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available coordinate operation factory implementations.
     *
     * @category Referencing
     */
    public static Set<CoordinateOperationFactory> getCoordinateOperationFactories(final Hints hints) {
        return getFactories(CoordinateOperationFactory.class, hints);
    }

    /**
     * Returns the first implementation of {@link MathTransformFactory} matching the specified
     * hints. If no implementation matches, a new one is created if possible or an exception is
     * thrown otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first math transform factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link MathTransformFactory} interface.
     *
     * @category Referencing
     *
     * @see Hints#MATH_TRANSFORM_FACTORY
     */
    public static MathTransformFactory getMathTransformFactory(final Hints hints)
            throws FactoryRegistryException
    {
        return getFactory(MathTransformFactory.class, hints, Hints.MATH_TRANSFORM_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the
     * {@link MathTransformFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available math transform factory implementations.
     *
     * @category Referencing
     */
    public static Set<MathTransformFactory> getMathTransformFactories(final Hints hints) {
        return getFactories(MathTransformFactory.class, hints);
    }

    /**
     * Returns the first implementation of {@link FilterFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first filter factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link FilterFactory} interface.
     *
     * @since 3.0
     *
     * @see Hints#FILTER_FACTORY
     */
    public static FilterFactory getFilterFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(FilterFactory.class, hints, Hints.FILTER_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link FilterFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available filter factory implementations.
     *
     * @since 3.0
     */
    public static Set<FilterFactory> getFilterFactories(final Hints hints) {
        return getFactories(FilterFactory.class, hints);
    }

    /**
     * Returns the first implementation of {@link StyleFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first style factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link StyleFactory} interface.
     *
     * @since 3.0
     *
     * @see Hints#STYLE_FACTORY
     */
    public static StyleFactory getStyleFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(StyleFactory.class, hints, Hints.STYLE_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link StyleFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available style factory implementations.
     *
     * @since 3.0
     */
    public static Set<StyleFactory> getStyleFactories(final Hints hints) {
        return getFactories(StyleFactory.class, hints);
    }

    /**
     * Sets a pairwise ordering between two vendors. If one or both vendors are not
     * currently registered, or if the desired ordering is already set, nothing happens
     * and {@code false} is returned.
     * <p>
     * The example below said that an ESRI implementation (if available) is
     * preferred over the Geotoolkit one:
     *
     * {@preformat java
     *     FactoryFinder.setVendorOrdering("ESRI", "Geotoolkit");
     * }
     *
     * @param  vendor1 The preferred vendor.
     * @param  vendor2 The vendor to which {@code vendor1} is preferred.
     * @return {@code true} if the ordering was set for at least one category.
     *
     * @see AuthorityFactoryFinder#setAuthorityOrdering
     */
    @Configuration
    public static boolean setVendorOrdering(final String vendor1, final String vendor2) {
        final VendorFilter filter1 = new VendorFilter(vendor1);
        final VendorFilter filter2 = new VendorFilter(vendor2);
        final boolean changed;
        synchronized (FactoryFinder.class) {
            changed = getServiceRegistry().setOrdering(Factory.class, filter1, filter2);
        }
        if (changed) {
            Factories.fireConfigurationChanged(AuthorityFactoryFinder.class);
        }
        return changed;
    }

    /**
     * Unsets a pairwise ordering between two vendors. If one or both vendors are not
     * currently registered, or if the desired ordering is already unset, nothing happens
     * and {@code false} is returned.
     *
     * @param  vendor1 The preferred vendor.
     * @param  vendor2 The vendor to which {@code vendor1} is preferred.
     * @return {@code true} if the ordering was unset for at least one category.
     *
     * @see AuthorityFactoryFinder#unsetAuthorityOrdering
     */
    @Configuration
    public static boolean unsetVendorOrdering(final String vendor1, final String vendor2) {
        final VendorFilter filter1 = new VendorFilter(vendor1);
        final VendorFilter filter2 = new VendorFilter(vendor2);
        final boolean changed;
        synchronized (FactoryFinder.class) {
            changed = getServiceRegistry().unsetOrdering(Factory.class, filter1, filter2);
        }
        if (changed) {
            Factories.fireConfigurationChanged(AuthorityFactoryFinder.class);
        }
        return changed;
    }

    /**
     * A filter for factories provided by a given vendor.
     */
    private static final class VendorFilter implements ServiceRegistry.Filter {
        /**
         * The vendor to filter.
         */
        private final String vendor;

        /**
         * Constructs a filter for the given vendor.
         */
        public VendorFilter(final String vendor) {
            this.vendor = vendor;
        }

        /**
         * Returns {@code true} if the specified provider is built by the vendor.
         */
        @Override
        public boolean filter(final Object provider) {
            return Citations.titleMatches(((Factory)provider).getVendor(), vendor);
        }
    }

    /**
     * Returns {@code true} if the specified factory is registered. A factory may have been
     * registered by {@link #scanForPlugins()} if it was declared in a {@code META-INF/services}
     * file, or it may have been {@linkplain AuthorityFactoryFinder#addAuthorityFactory added
     * programmatically}.
     *
     * @param factory The factory to check for registration.
     * @return {@code true} if the given factory is registered.
     *
     * @since 2.4
     */
    public static boolean isRegistered(final Object factory) {
        final Object existing;
        synchronized (FactoryFinder.class) {
            existing = getServiceRegistry().getServiceProviderByClass(factory.getClass());
        }
        return factory.equals(existing);
    }

    /**
     * Lists all available factory implementations in a tabular format. For each factory interface,
     * the first implementation listed is the default one. This method provides a way to check the
     * state of a system, usually for debugging purpose.
     *
     * @param  out The output stream where to format the list.
     * @param  locale The locale for the list, or {@code null}.
     * @throws IOException if an error occurs while writting to {@code out}.
     *
     * @since 3.0
     */
    public static synchronized void listProviders(final Writer out, final Locale locale)
            throws IOException
    {
        new FactoryPrinter(Collections.singleton(getServiceRegistry())).list(out, locale);
    }

    /**
     * Scans for factory plug-ins on the application class path. This method is needed because the
     * application class path can theoretically change, or additional plug-ins may become available.
     * Rather than re-scanning the classpath on every invocation of the API, the class path is
     * scanned automatically only on the first invocation. Clients can call this method to prompt
     * a re-scan. Thus this method need only be invoked by sophisticated applications which
     * dynamically make new plug-ins available at runtime.
     *
     * @level advanced
     */
    @Configuration
    public static void scanForPlugins() {
        synchronized (AuthorityFactoryFinder.class) {
            AuthorityFactoryFinder.authorityNames = null;
            synchronized (FactoryFinder.class) {
                if (registry != null) {
                    registry.scanForPlugins();
                }
            }
        }
        Factories.fireConfigurationChanged(FactoryFinder.class);
    }
}

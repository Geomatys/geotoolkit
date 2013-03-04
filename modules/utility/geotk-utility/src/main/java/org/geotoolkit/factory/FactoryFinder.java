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
import java.util.Locale;
import java.util.Iterator;
import java.util.Collections;
import javax.imageio.spi.ServiceRegistry;
import java.io.IOException;
import java.io.Writer;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.Factory;
import org.opengis.util.NameFactory;
import org.opengis.style.StyleFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.CitationFactory;
import org.opengis.geometry.PositionFactory;
import org.opengis.geometry.primitive.PrimitiveFactory;
import org.opengis.geometry.coordinate.GeometryFactory;
import org.opengis.geometry.complex.ComplexFactory;
import org.opengis.geometry.aggregate.AggregateFactory;
import org.opengis.temporal.TemporalFactory;

import org.geotoolkit.lang.Debug;
import org.geotoolkit.lang.Static;
import org.geotoolkit.lang.Configuration;
import org.apache.sis.internal.util.Citations;
import org.geotoolkit.internal.LazySet;


/**
 * Defines static methods used to access the application {@linkplain Factory factory}
 * implementations. This class provide access to the following services:
 * <p>
 * <ul>
 *   <li><b>Utilities</b></li><ul>
 *     <li>{@link NameFactory}</li>
 *   </ul>
 *   <li><b>Metadata</b></li><ul>
 *     <li>{@link CitationFactory} (metadata)</li>
 *   </ul>
 *   <li><b>Referencing</b></li><ul>
 *     <li>{@link CoordinateOperationFactory}</li>
 *     <li>{@link CRSFactory}</li>
 *     <li>{@link CSFactory}</li>
 *     <li>{@link DatumFactory}</li>
 *     <li>{@link MathTransformFactory}</li>
 *   </ul>
 *   <li><b>Geometry</b></li><ul>
 *     <li>{@link PositionFactory}</li>
 *     <li>{@link PrimitiveFactory}</li>
 *     <li>{@link GeometryFactory}</li>
 *     <li>{@link ComplexFactory}</li>
 *     <li>{@link AggregateFactory}</li>
 *   </ul>
 *   <li><b>Feature</b></li><ul>
 *     <li>{@link FeatureTypeFactory}</li>
 *     <li>{@link FeatureFactory}</li>
 *     <li>{@link FilterFactory}</li>
 *     <li>{@link StyleFactory}</li>
 *   </ul>
 * </ul>
 * <p>
 * This class is thread-safe but may have a high contention. Applications (or computational units in
 * an application) are encouraged to save references to the factories they need in their own private
 * fields. They would gain in performance and in stability, since the set of available factories may
 * change during the execution.
 * <p>
 * Some methods like {@link #setVendorOrdering setVendorOrdering} have a system-wide effect. Most
 * applications should not need to invoke them. If an application needs to protect itself against
 * configuration changes that may be performed by an other application sharing the Geotk library,
 * it shall manage its own instance of {@link FactoryRegistry}. This {@code FactoryFinder} class
 * itself is just a convenience wrapper around a {@code FactoryRegistry} instance.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.1
 * @level basic
 * @module
 */
@ThreadSafe
public class FactoryFinder extends Static {
    /**
     * The key for a special hints specifying an additional
     * {@link javax.imageio.spi.ServiceRegistry.Filter Filter}.
     * If a value is given to this key in a {@link Hints} map, then every factory candidate
     * will be filtered by the given filter in addition of being checked for the other hints.
     *
     * @since 3.03
     */
    public static final Hints.Key FILTER_KEY = new Hints.Key(ServiceRegistry.Filter.class);

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
     * In a previous Geotk version, a somewhat convolved lookup was performed here.
     * Now that default hints are filled right at {@link Hints} creation time, this
     * method just needs to ensure that the given hints are non-null.
     * <p>
     * This method returns an object on which {@code hints.remove(FILTER_KEY)} can
     * be invoked, assuming that the map is not modified in a background thread.
     *
     * @param  hints The user hints, or {@code null} for the default ones.
     * @return The hints to use (never {@code null}).
     */
    static Hints mergeSystemHints(Hints hints) {
        if (hints == null) {
            hints = new Hints();
        } else if (hints.containsKey(FILTER_KEY) || hints.getClass() != Hints.class) {
            hints = hints.clone();
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
        FactoryRegistry registry = FactoryFinder.registry;
        if (registry == null) {
            registry = new DynamicFactoryRegistry(new Class<?>[] {
                    NameFactory.class,
                    CitationFactory.class,
                    DatumFactory.class,
                    CSFactory.class,
                    CRSFactory.class,
                    MathTransformFactory.class,
                    CoordinateOperationFactory.class,
                    TemporalFactory.class,
                    PositionFactory.class,
                    PrimitiveFactory.class,
                    GeometryFactory.class,
                    ComplexFactory.class,
                    AggregateFactory.class,
                    FeatureTypeFactory.class,
                    FeatureFactory.class,
                    FilterFactory.class,
                    StyleFactory.class,

                    // Used by AuthorityFactoryFinder
                    DatumAuthorityFactory.class,
                    CSAuthorityFactory.class,
                    CRSAuthorityFactory.class,
                    CoordinateOperationAuthorityFactory.class
            }) {
                /*
                 * Geotoolkit.org is a fork of GeoTools. If both appear on the classpath, conflicts
                 * may arise unless we specify which one should have precedence over the other.
                 */
                @Override void pluginScanned(final Class<?> category) {
                    final VendorFilter filter1 = new VendorFilter("org.geotoolkit.", true, true);
                    final VendorFilter filter2 = new VendorFilter("org.geotools.",   true, false);
                    setOrdering(category, filter1, filter2);
                }
            };
            ShutdownHook.register(registry);
            FactoryFinder.registry = registry;
        }
        return registry;
    }

    /**
     * Returns all providers of the specified category.
     *
     * @param  category The factory category.
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @param  key The hint key to use for searching an implementation.
     * @return Set of available factory implementations.
     */
    static <T> Set<T> getFactories(final Class<T> category, Hints hints, final Hints.ClassKey key) {
        hints = mergeSystemHints(hints);
        final ServiceRegistry.Filter filter = (ServiceRegistry.Filter) hints.remove(FILTER_KEY);
        final Iterator<T> iterator;
        synchronized (FactoryFinder.class) {
            iterator = getServiceRegistry().getServiceProviders(category, filter, hints, key);
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
    private static <T> T getFactory(final Class<T> category, Hints hints, final Hints.ClassKey key)
            throws FactoryRegistryException
    {
        hints = mergeSystemHints(hints);
        final ServiceRegistry.Filter filter = (ServiceRegistry.Filter) hints.remove(FILTER_KEY);
        synchronized (FactoryFinder.class) {
            return getServiceRegistry().getServiceProvider(category, filter, hints, key);
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
     * @since 3.00
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
     * @since 3.00
     * @category Metadata
     */
    public static Set<NameFactory> getNameFactories(final Hints hints) {
        return getFactories(NameFactory.class, hints, Hints.NAME_FACTORY);
    }

    /**
     * Returns the first implementation of {@link CitationFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first citation factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CitationFactory} interface.
     *
     * @since 3.00
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
     * @since 3.00
     * @category Metadata
     */
    public static Set<CitationFactory> getCitationFactories(final Hints hints) {
        return getFactories(CitationFactory.class, hints, Hints.CITATION_FACTORY);
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
        return getFactories(DatumFactory.class, hints, Hints.DATUM_FACTORY);
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
        return getFactories(CSFactory.class, hints, Hints.CS_FACTORY);
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
        return getFactories(CRSFactory.class, hints, Hints.CRS_FACTORY);
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
        return getFactories(CoordinateOperationFactory.class, hints, Hints.COORDINATE_OPERATION_FACTORY);
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
        return getFactories(MathTransformFactory.class, hints, Hints.MATH_TRANSFORM_FACTORY);
    }

    /**
     * Returns the first implementation of {@link TemporalFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first temporal factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link TemporalFactory} interface.
     *
     * @since 3.18
     * @category Temporal
     *
     * @see Hints#TEMPORAL_FACTORY
     */
    public static TemporalFactory getTemporalFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(TemporalFactory.class, hints, Hints.TEMPORAL_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link TemporalFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available temporal factory implementations.
     *
     * @since 3.18
     * @category Temporal
     */
    public static Set<TemporalFactory> getTemporalFactories(final Hints hints) {
        return getFactories(TemporalFactory.class, hints, Hints.TEMPORAL_FACTORY);
    }

    /**
     * Returns the first implementation of {@link PositionFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first position factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link PositionFactory} interface.
     *
     * @since 3.01
     * @category Geometry
     *
     * @see Hints#POSITION_FACTORY
     */
    public static PositionFactory getPositionFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(PositionFactory.class, hints, Hints.POSITION_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link PositionFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available position factory implementations.
     *
     * @since 3.01
     * @category Geometry
     */
    public static Set<PositionFactory> getPositionFactories(final Hints hints) {
        return getFactories(PositionFactory.class, hints, Hints.POSITION_FACTORY);
    }

    /**
     * Returns the first implementation of {@link PrimitiveFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first primitive factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link PrimitiveFactory} interface.
     *
     * @since 3.01
     * @category Geometry
     *
     * @see Hints#PRIMITIVE_FACTORY
     */
    public static PrimitiveFactory getPrimitiveFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(PrimitiveFactory.class, hints, Hints.PRIMITIVE_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link PrimitiveFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available primitive factory implementations.
     *
     * @since 3.01
     * @category Geometry
     */
    public static Set<PrimitiveFactory> getPrimitiveFactories(final Hints hints) {
        return getFactories(PrimitiveFactory.class, hints, Hints.PRIMITIVE_FACTORY);
    }

    /**
     * Returns the first implementation of {@link GeometryFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first geometry factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link GeometryFactory} interface.
     *
     * @since 3.01
     * @category Geometry
     *
     * @see Hints#GEOMETRY_FACTORY
     */
    public static GeometryFactory getGeometryFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(GeometryFactory.class, hints, Hints.GEOMETRY_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link GeometryFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available geometry factory implementations.
     *
     * @since 3.01
     * @category Geometry
     */
    public static Set<GeometryFactory> getGeometryFactories(final Hints hints) {
        return getFactories(GeometryFactory.class, hints, Hints.GEOMETRY_FACTORY);
    }

    /**
     * Returns the first implementation of {@link ComplexFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first complex factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link ComplexFactory} interface.
     *
     * @since 3.01
     * @category Geometry
     *
     * @see Hints#COMPLEX_FACTORY
     */
    public static ComplexFactory getComplexFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(ComplexFactory.class, hints, Hints.COMPLEX_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link ComplexFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available complex factory implementations.
     *
     * @since 3.01
     * @category Geometry
     */
    public static Set<ComplexFactory> getComplexFactories(final Hints hints) {
        return getFactories(ComplexFactory.class, hints, Hints.COMPLEX_FACTORY);
    }

    /**
     * Returns the first implementation of {@link AggregateFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first aggregate factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link ComplexFactory} interface.
     *
     * @since 3.01
     * @category Geometry
     *
     * @see Hints#AGGREGATE_FACTORY
     */
    public static AggregateFactory getAggregateFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(AggregateFactory.class, hints, Hints.AGGREGATE_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link AggregateFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available complex factory implementations.
     *
     * @since 3.01
     * @category Geometry
     */
    public static Set<AggregateFactory> getAggregateFactories(final Hints hints) {
        return getFactories(AggregateFactory.class, hints, Hints.AGGREGATE_FACTORY);
    }

    /**
     * Returns the first implementation of {@link FeatureTypeFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first feature type factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link FeatureTypeFactory} interface.
     *
     * @since 3.15
     * @category Feature
     *
     * @see Hints#FEATURE_TYPE_FACTORY
     */
    public static FeatureTypeFactory getFeatureTypeFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(FeatureTypeFactory.class, hints, Hints.FEATURE_TYPE_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link FeatureTypeFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available feature type factory implementations.
     *
     * @since 3.15
     * @category Feature
     */
    public static Set<FeatureTypeFactory> getFeatureTypeFactories(final Hints hints) {
        return getFactories(FeatureTypeFactory.class, hints, Hints.FEATURE_TYPE_FACTORY);
    }

    /**
     * Returns the first implementation of {@link FeatureFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first feature factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link FeatureFactory} interface.
     *
     * @since 3.01
     * @category Feature
     *
     * @see Hints#FEATURE_FACTORY
     */
    public static FeatureFactory getFeatureFactory(final Hints hints) throws FactoryRegistryException {
        return getFactory(FeatureFactory.class, hints, Hints.FEATURE_FACTORY);
    }

    /**
     * Returns a set of all available implementations for the {@link FeatureFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return Set of available feature factory implementations.
     *
     * @since 3.01
     * @category Feature
     */
    public static Set<FeatureFactory> getFeatureFactories(final Hints hints) {
        return getFactories(FeatureFactory.class, hints, Hints.FEATURE_FACTORY);
    }

    /**
     * Returns the first implementation of {@link FilterFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first filter factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link FilterFactory} interface.
     *
     * @since 3.00
     * @category Feature
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
     * @since 3.00
     * @category Feature
     */
    public static Set<FilterFactory> getFilterFactories(final Hints hints) {
        return getFactories(FilterFactory.class, hints, Hints.FILTER_FACTORY);
    }

    /**
     * Returns the first implementation of {@link StyleFactory} matching the specified hints.
     *
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first style factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link StyleFactory} interface.
     *
     * @since 3.00
     * @category Feature
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
     * @since 3.00
     * @category Feature
     */
    public static Set<StyleFactory> getStyleFactories(final Hints hints) {
        return getFactories(StyleFactory.class, hints, Hints.STYLE_FACTORY);
    }

    /**
     * Sets a pairwise ordering between two vendors. If one or both vendors are not
     * currently registered, or if the desired ordering is already set, then nothing
     * happens and {@code false} is returned.
     * <p>
     * The example below said that an ESRI implementation (if available) is
     * preferred over the Geotoolkit.org one:
     *
     * {@preformat java
     *     FactoryFinder.setVendorOrdering("ESRI", "Geotoolkit.org");
     * }
     *
     * @param  vendor1 The preferred vendor.
     * @param  vendor2 The vendor to which {@code vendor1} is preferred.
     * @return {@code true} if the ordering was set for at least one category.
     *
     * @see AuthorityFactoryFinder#setAuthorityOrdering(String, String)
     */
    @Configuration
    public static boolean setVendorOrdering(final String vendor1, final String vendor2) {
        return setOrUnsetOrdering(vendor1, vendor2, false, true);
    }

    /**
     * Unsets a pairwise ordering between two vendors. If one or both vendors are not
     * currently registered, or if the desired ordering is already unset, then nothing
     * happens and {@code false} is returned.
     *
     * @param  vendor1 The preferred vendor.
     * @param  vendor2 The vendor to which {@code vendor1} was preferred.
     * @return {@code true} if the ordering was unset for at least one category.
     *
     * @see AuthorityFactoryFinder#unsetAuthorityOrdering(String, String)
     */
    @Configuration
    public static boolean unsetVendorOrdering(final String vendor1, final String vendor2) {
        return setOrUnsetOrdering(vendor1, vendor2, false, false);
    }

    /**
     * Sets a pairwise ordering between two implementations defined by package names. If one or
     * both implementations are not currently registered, or if the desired ordering is already
     * set, then nothing happens and {@code false} is returned.
     * <p>
     * This method is preferred to {@link #setVendorOrdering(String, String)} when the package
     * name are known, because it avoid the potentially costly (on some implementations) call
     * to {@link org.opengis.util.Factory#getVendor()}.
     *
     * {@note An example of costly <code>getVendor()</code> implementation is the one in the
     * <code>CachingAuthorityFactory</code> class, because it needs to create the underlying
     * backing store in order to query its vendor property.}
     *
     * @param  package1 The package name of the preferred implementation.
     * @param  package2 The package name of the implementation to which {@code package1} is preferred.
     * @return {@code true} if the ordering was set for at least one category.
     *
     * @see #setVendorOrdering(String, String)
     *
     * @since 3.16
     */
    @Configuration
    public static boolean setImplementationOrdering(final String package1, final String package2) {
        return setOrUnsetOrdering(package1, package2, true, true);
    }

    /**
     * Unsets a pairwise ordering between two implementations defined by package names. If one or
     * both implementations are not currently registered, or if the desired ordering is already
     * unset, then nothing happens and {@code false} is returned.
     *
     * @param  package1 The preferred vendor.
     * @param  package2 The vendor to which {@code vendor1} was preferred.
     * @return {@code true} if the ordering was unset for at least one category.
     *
     * @see #unsetVendorOrdering(String, String)
     *
     * @since 3.16
     */
    @Configuration
    public static boolean unsetImplementationOrdering(final String package1, final String package2) {
        return setOrUnsetOrdering(package1, package2, true, false);
    }

    /**
     * Sets or unsets a pairwise ordering between two vendors or implementations.
     *
     * @param  vendor1 The preferred vendor.
     * @param  vendor2 The vendor to which {@code vendor1} is preferred.
     * @param  set {@code true} for setting the ordering, or {@code false} for unsetting.
     * @return {@code true} if the ordering was changed for at least one category.
     */
    private static boolean setOrUnsetOrdering(final String vendor1, final String vendor2,
            final boolean byPackageName, final boolean set)
    {
        final VendorFilter filter1 = new VendorFilter(vendor1, byPackageName, true);
        final VendorFilter filter2 = new VendorFilter(vendor2, byPackageName, false);
        final boolean changed;
        synchronized (FactoryFinder.class) {
            changed = getServiceRegistry().setOrUnsetOrdering(Factory.class, filter1, filter2, set);
        }
        if (changed) {
            Factories.fireConfigurationChanged(AuthorityFactoryFinder.class);
        }
        return changed;
    }

    /**
     * A filter for factories provided by a given vendor or implementation.
     */
    private static final class VendorFilter implements ServiceRegistry.Filter {
        /**
         * The vendor to filter.
         */
        private final String vendor;

        /**
         * {@code true} if the vendor should be checked by package name,
         * or {@code false} if it should be checked by citation.
         */
        private final boolean byPackageName;

        /**
         * The value to returns if the factory does not specify the vendor.
         */
        private final boolean defaultValue;

        /**
         * Constructs a filter for the given vendor.
         */
        public VendorFilter(final String vendor, final boolean byPackageName, final boolean defaultValue) {
            this.vendor        = vendor;
            this.byPackageName = byPackageName;
            this.defaultValue  = defaultValue;
        }

        /**
         * Returns {@code true} if the specified provider is built by the vendor.
         */
        @Override
        public boolean filter(final Object provider) {
            if (byPackageName) {
                return provider.getClass().getName().startsWith(vendor);
            }
            if (provider instanceof Factory) {
                final Citation candidate = ((Factory) provider).getVendor();
                if (candidate != null) {
                    return Citations.titleMatches(candidate, vendor);
                }
            }
            return defaultValue;
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
     * @throws IOException if an error occurs while writing to {@code out}.
     *
     * @since 3.00
     */
    @Debug
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

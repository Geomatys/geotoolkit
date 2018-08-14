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


import org.opengis.util.Factory;
import org.opengis.util.NameFactory;
import org.opengis.style.StyleFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.metadata.citation.CitationFactory;
import org.opengis.geometry.PositionFactory;
import org.opengis.geometry.primitive.PrimitiveFactory;
import org.opengis.geometry.coordinate.GeometryFactory;
import org.opengis.geometry.complex.ComplexFactory;
import org.opengis.geometry.aggregate.AggregateFactory;
import org.opengis.temporal.TemporalFactory;

import org.geotoolkit.lang.Static;
import org.geotoolkit.lang.Configuration;
import org.apache.sis.internal.system.DefaultFactories;


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
 * @module
 *
 * @deprecated Will be replaced by a more standard dependency injection mechanism.
 */
@Deprecated
public class FactoryFinder extends Static {
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
        } else if (hints.getClass() != Hints.class) {
            hints = hints.clone();
        }
        return hints;
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
        final Object factory = hints.get(key);
        if (category.isInstance(factory)) {
            return (T) factory;
        }
        return DefaultFactories.forBuildin(category);
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
        DefaultFactories.fireClasspathChanged();
        Factories.fireConfigurationChanged(FactoryFinder.class);
    }
}

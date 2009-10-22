/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.RenderingHints;
import java.util.Map;


/**
 * A set of hints providing control on factories to be used. Those hints are typically used by
 * renderers or {@linkplain org.opengis.coverage.processing.GridCoverageProcessor grid coverage
 * processors} for example. They provides a way to control low-level details. Example:
 * <p>
 * <blockquote><pre>
 * CoordinateOperationFactory myFactory = &amp;hellip
 * Hints hints = new Hints(Hints.{@linkplain #COORDINATE_OPERATION_FACTORY}, myFactory);
 * AbstractProcessor processor = new DefaultProcessor(hints);
 * </pre></blockquote>
 * <p>
 * Any hint mentioned by this class is considered to be API, failure to make
 * use of a hint by a GeoTools factory implementation is considered a bug (as
 * it will prevent the use of this library for application specific tasks).
 * <p>
 * When hints are used in conjunction with the {@linkplain FactoryRegistry factory service
 * discovery mechanism} we have the complete geotools plugin system. By using hints to
 * allow application code to effect service discovery we allow client code to
 * retarget the geotools library for their needs.
 *
 * @module pending
 * @since 2.1
 * @version $Id$
 * @author Martin Desruisseaux
 * @author Jody Garnett
 */
public class HintsPending extends Hints {
    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                     ISO Geometries                     ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The {@link org.opengis.referencing.crs.CoordinateReferenceSystem} to use in
     * ISO geometry factories.
     *
     * @see #JTS_SRID
     * @since 2.5
     */
    public static final Key CRS = new Key("org.opengis.referencing.crs.CoordinateReferenceSystem");

    /**
     * The {@link org.opengis.geometry.Precision} to use in ISO geometry factories.
     *
     * @see #JTS_PRECISION_MODEL
     * @since 2.5
     */
    public static final Key PRECISION = new Key("org.opengis.geometry.Precision");

    /**
     * The {@link org.opengis.geometry.PositionFactory} instance to use.
     *
     * @since 2.5
     */
    public static final Key POSITION_FACTORY = new Key("org.opengis.geometry.PositionFactory");

    /**
     * The {@link org.opengis.geometry.coordinate.GeometryFactory} instance to use.
     *
     * @see #JTS_GEOMETRY_FACTORY
     * @since 2.5
     */
    public static final Key GEOMETRY_FACTORY = new Key("org.opengis.geometry.coordinate.GeometryFactory");

    /**
     * The {@link org.opengis.geometry.complex.ComplexFactory} instance to use.
     *
     * @since 2.5
     */
    public static final Key COMPLEX_FACTORY = new Key("org.opengis.geometry.complex.ComplexFactory");

    /**
     * The {@link org.opengis.geometry.aggregate.AggregateFactory} instance to use.
     *
     * @since 2.5
     */
    public static final Key AGGREGATE_FACTORY = new Key("org.opengis.geometry.aggregate.AggregateFactory");

    /**
     * The {@link org.opengis.geometry.primitive.PrimitiveFactory} instance to use.
     *
     * @since 2.5
     */
    public static final Key PRIMITIVE_FACTORY = new Key("org.opengis.geometry.primitive.PrimitiveFactory");

    /**
     * If {@code true}, geometry will be validated on creation. A value of {@code false}
     * may speedup geometry creation at the cost of less safety.
     *
     * @since 2.5
     */
    public static final Key GEOMETRY_VALIDATE = new Key(Boolean.class);



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                     JTS Geometries                     ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The {@link com.vividsolutions.jts.geom.GeometryFactory} instance to use.
     *
     * @see #GEOMETRY_FACTORY
     * @see org.geotools.geometry.jts.FactoryFinder#getGeometryFactory
     */
    public static final ClassKey JTS_GEOMETRY_FACTORY = new ClassKey(
            "com.vividsolutions.jts.geom.GeometryFactory");

    /**
     * The {@link com.vividsolutions.jts.geom.CoordinateSequenceFactory} instance to use.
     *
     * @see org.geotools.geometry.jts.FactoryFinder#getCoordinateSequenceFactory
     */
    public static final ClassKey JTS_COORDINATE_SEQUENCE_FACTORY = new ClassKey(
            "com.vividsolutions.jts.geom.CoordinateSequenceFactory");

    /**
     * The {@link com.vividsolutions.jts.geom.PrecisionModel} instance to use.
     *
     * @see org.geotools.geometry.jts.FactoryFinder#getPrecisionModel
     * @see #PRECISION
     */
    public static final Key JTS_PRECISION_MODEL = new Key(
            "com.vividsolutions.jts.geom.PrecisionModel");

    /**
     * The spatial reference ID for {@link com.vividsolutions.jts.geom.GeometryFactory}.
     *
     * @see org.geotools.geometry.jts.FactoryFinder#getGeometryFactory
     * @see #CRS
     */
    public static final Key JTS_SRID = new Key(Integer.class);



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                        Features                        ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The {@link org.opengis.feature.FeatureFactory} instance to use.
     *
     * @see CommonFactoryFinder.getFeatureFactory()
     * @since 2.5
     */
    public static ClassKey FEATURE_FACTORY = new ClassKey( "org.opengis.feature.FeatureFactory");

    /**
     * The {@link org.opengis.feature.type.FeatureTypeFactory} instance to use.
     *
     * @see CommonFactoryFinder.getFeatureTypeFactory()
     * @since 2.4
     */
    public static ClassKey FEATURE_TYPE_FACTORY = new ClassKey( "org.opengis.feature.type.FeatureTypeFactory");


    /**
     * The {@link org.geotools.data.FeatureLockFactory} instance to use.
     *
     * @see CommonFactoryFinder#getFeatureLockFactory
     *
     * @since 2.4
     */
    public static final ClassKey FEATURE_LOCK_FACTORY = new ClassKey(
            "org.geotools.data.FeatureLockFactory");

    /**
     * The {@link org.geotools.feature.FeatureCollections} instance to use.
     *
     * @see CommonFactoryFinder#getFeatureCollections
     *
     * @since 2.4
     */
    public static final ClassKey FEATURE_COLLECTIONS = new ClassKey(
            "org.geotools.feature.FeatureCollections");

    /**
     * Used to provide the <cite>type name</cite> for the returned
     * {@link org.geotools.feature.FeatureTypeFactory}. Values should
     * be instances of {@link String}.
     * @deprecated This hint controls FeatureTypeBuilder which is now deprecated
     * @since 2.4
     */
    public static final Key FEATURE_TYPE_FACTORY_NAME = new Key(String.class);

    /**
     * Whether the features returned by the feature collections should be considered detached from
     * the datastore, that is, they are updatable without altering the backing store (makes sense
     * only if features are kept in memory or if there is some transparent persistent mechanism in
     * place, such as the Hibernate one)
     *
     * @since 2.4
     */
    public static final Key FEATURE_DETACHED = new Key(Boolean.class);

    /**
     * Request that the features returned by the feature collections should
     * be 2D only. Can be used to prevent the request of the third ordinate
     * when only two are going to be used.
     *
     * @since 2.4.1
     */
    public static final Key FEATURE_2D = new Key(Boolean.class);

    /**
     * Asks a datastore having a vector pyramid (pre-generalized geometries)
     * to return the geometry version whose points have been generalized
     * less than the spefiedi distance (further generalization might be
     * performed by the client in memory).<p>
     * The geometries returned are supposed to be topologically valid.
     */
    public static final Key GEOMETRY_DISTANCE = new Key(Double.class);

    /**
     * Asks a datastore to perform a topology preserving on the fly
     * generalization of the geometries. The datastore will return
     * geometries generalized at the specified distance.
     */
    public static final Key GEOMETRY_GENERALIZATION = new Key(Double.class);

    /**
     * Asks a datastore to perform a non topology preserving on the fly
     * generalization of the geometries (e.g., returning self crossing
     * polygons as a result of the geoneralization is considered valid).

     */
    public static final Key GEOMETRY_SIMPLIFICATION = new Key(Double.class);


    /**
     * The {@link org.geotools.styling.StyleFactory} instance to use.
     *
     * @see CommonFactoryFinder#getStyleFactory
     *
     * @since 2.4
     */
//    public static final ClassKey STYLE_FACTORY = new ClassKey(
//            "org.geotools.style.StyleFactory");

    /**
     * The {@link org.geotools.feature.AttributeTypeFactory} instance to use.
     *
     * @see CommonFactoryFinder#getAttributeTypeFactory
     *
     * @since 2.4
     */
    public static final ClassKey ATTRIBUTE_TYPE_FACTORY = new ClassKey(
            "org.geotools.feature.AttributeTypeFactory");

    /**
     * The {@link org.opengis.filter.FilterFactory} instance to use.
     *
     * @see CommonFactoryFinder#getFilterFactory
     *
     * @since 2.4
     */
//    public static final ClassKey FILTER_FACTORY = new ClassKey(
//            "org.opengis.filter.FilterFactory");



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                     Grid Coverages                     ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * Tells to the {@link org.opengis.coverage.grid.GridCoverageReader} instances to ignore
     * the built-in overviews when creating a {@link org.opengis.coverage.grid.GridCoverage2D}
     * object during a read. This hints also implied that no decimation on reading is performed.
     *
     * @since 2.3
     *
     * @deprecated use the correct {@link #OVERVIEW_POLICY} instead.
     */
    public static final Key IGNORE_COVERAGE_OVERVIEW = new Key(Boolean.class);

    /**
     * Key to control the maximum allowed number of tiles that we will load.
     * If this number is exceeded, i.e. we request an area which is too large
     * instead of getting stuck with opening thousands of files we throw an error.
     *
     * @since 2.5
     */
    public static final Key MAX_ALLOWED_TILES = new Key(Integer.class);

    /**
     * Key to control the name of the attribute that contains the location for
     * the tiles in the mosaic index.
     *
     * @since 2.5
     */
    public static final Key MOSAIC_LOCATION_ATTRIBUTE = new Key(String.class);

    /**
     * Key to control the name of the attribute that contains the red/green/blue channel
     * selections of a particular mosaic tile index.
     *
     * @since 2.6
     */
    public static final Key MOSAIC_BANDSELECTION_ATTRIBUTE = new Key(String.class);

    /**
     * Key to control the name of the attribute that contains the red/green/blue channel
     * color corrections of a particular mosaic tile index
     *
     * @since 2.6
     */
    public static final Key MOSAIC_COLORCORRECTION_ATTRIBUTE = new Key(String.class);

    /**
     * Tells to the {@link org.opengis.coverage.grid.GridCoverageReader} instances to read
     * the image using the JAI ImageRead operation (leveraging on Deferred Execution Model,
     * Tile Caching,...) or the direct {@code ImageReader}'s read methods.
     *
     * @since 2.4
     */
    public static final Key USE_JAI_IMAGEREAD = new Key(Boolean.class);

    /**
     * Overview policy, will choose the overview with the lower resolution among the ones
     * with higher resolution than one used for rendering.
     *
     * @since 2.5
     *
     * @deprecated Moved to {@link org.geotools.coverage.grid.io.OverviewPolicy#QUALITY}.
     */
    public static Object VALUE_OVERVIEW_POLICY_QUALITY;

    /**
     * Overview policy, will ignore the overviews.
     *
     * @since 2.5
     *
     * @deprecated Moved to {@link org.geotools.coverage.grid.io.OverviewPolicy#IGNORE}.
     */
    public static Object VALUE_OVERVIEW_POLICY_IGNORE;

    /**
     * Overview policy, will choose the overview with with the resolution closest to the one used
     * for rendering
     *
     * @since 2.5
     *
     * @deprecated Moved to {@link org.geotools.coverage.grid.io.OverviewPolicy#NEAREST}.
     */
    public static Object VALUE_OVERVIEW_POLICY_NEAREST;

    /**
     * Overview policy, will choose the overview with the higher resolution among the ones
     * with lower resolution than one used for rendering.
     *
     * @since 2.5
     *
     * @deprecated Moved to {@link org.geotools.coverage.grid.io.OverviewPolicy#SPEED}.
     */
    public static Object VALUE_OVERVIEW_POLICY_SPEED;
    static {
        try {
            final Class c = Class.forName("org.geotools.coverage.grid.io.OverviewPolicy");
            VALUE_OVERVIEW_POLICY_QUALITY = c.getField("QUALITY").get(null);
            VALUE_OVERVIEW_POLICY_IGNORE  = c.getField("IGNORE").get(null);
            VALUE_OVERVIEW_POLICY_NEAREST = c.getField("NEAREST").get(null);
            VALUE_OVERVIEW_POLICY_SPEED   = c.getField("SPEED").get(null);
        } catch (Exception e) {
            // Ignore since it is normal if the coverage module is not in the classpath.
            // This is just a temporary patch, so hopefull we will remove this ugly hack soon.
        }
    }

    /**
     * Overview choosing policy. The value most be one of
     * {link #org.geotools.coverage.grid.io.OverviewPolicy} enumeration.
     *
     * @since 2.5
     */
    public static final Key OVERVIEW_POLICY = new Key(
            "org.geotools.coverage.grid.io.OverviewPolicy");

    /**
     * @deprecated Replaced by {@link #COVERAGE_PROCESSING_VIEW} key with a
     *             {@link org.geotoolkit.coverage.grid.ViewType#PHOTOGRAPHIC} value.
     *
     * @since 2.4
     *
     * @todo We may need to find a more accurate name, especially when the enumeration in
     *       {@link org.geotoolkit.coverage.grid.ViewType} will be ready to work. Maybe
     *       something like {@code PROCESS_ON_VISUAL_VIEW}.
     */
    public static final Key REPLACE_NON_GEOPHYSICS_VIEW = new Key(Boolean.class);



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                      Data stores                       ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The maximum number of associations traversed in a datastore query.
     * <p>
     * This maps directly to the {@code traversalXlinkDepth} parameter in a WFS query.
     */
    public static final Hints.Key ASSOCIATION_TRAVERSAL_DEPTH = new Key(Integer.class);

    /**
     * The name of a property to traverse in a datastore query.
     * <p>
     * This maps directly to a {@code xlinkPropertyName} in a WFS query.
     */
    public static final Hints.Key ASSOCIATION_PROPERTY = new Key("org.opengis.filter.expression.PropertyName");



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                         Caches                         ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * Policy to use for caching referencing objects. Valid values are:
     * <p>
     * <ul>
     *   <li>{@code "weak"} for holding values through {@linkplain java.lang.ref.WeakReference
     *       weak references}. This option does not actually cache the objects since the garbage
     *       collector cleans weak references aggressively, but it allows sharing the instances
     *       already created and still in use.</li>
     *   <li>{@code "fixed") for holding a fixed number of values specified by {@link #CACHE_LIMIT}.
     *   <li>{@code "all"} for holding values through strong references.</li>
     *   <li>{@code "none"} for disabling the cache.</li>
     *   <li>{@code "soft"} for holding the value throuhg(@linkplain java.lang.ref.SoftReference
     *       soft references}.
     * </ul>
     *
     * @since 2.5
     */
    public static final OptionKey CACHE_POLICY = new OptionKey("weak", "all", "fixed","none","default","soft");

    /**
     * The recommended maximum number of referencing objects to hold in a
     * {@linkplain org.opengis.referencing.AuthorityFactory authority factory}.
     *
     * @since 2.5
     */
    public static final IntegerKey CACHE_LIMIT = new IntegerKey(50);

    /**
     * The maximum number of active {@linkplain org.opengis.referencing.AuthorityFactory authority
     * factories}. The default is the {@linkplain Runtime#availableProcessors number of available
     * processors} plus one.
     * <p>
     * This hint is treated as an absolute <strong>limit</strong> for
     * {@link org.geotools.referencing.factory.AbstractAuthorityMediator} instances such as
     * {@link org.geotools.referencing.factory.epsg.HsqlDialectEpsgMediator}. As such this
     * will be the absolute limit on the number of database connections the mediator will
     * make use of.
     * <p>
     * When this limit it reached, code will be forced to block while waiting
     * for a connection to become available.
     * <p>
     * When this value is non positive their is no limit to the number of
     * active authority factories deployed.
     *
     * @since 2.5
     */
    public static final IntegerKey AUTHORITY_MAX_ACTIVE =
            new IntegerKey(Runtime.getRuntime().availableProcessors() + 1);

    /**
     * Minimum number of objects required before the evictor will begin
     * removing objects.  This value is also used by
     * AUTHORITY_SOFTMIN_EVICT_IDLETIME to keep this many idle workers
     * around.
     * <p>
     * In practice this value indicates the number of database connections
     * the application will hold open "just in case".
     * <p>
     * Recomendations:
     * <ul>
     * <li>Desktop Application: 1
     * <li>Server Application: 2-3
     * </ul>
     * To agree with J2EE conventions you will want this value to be zero.
     *
     * @since 2.5
     */
    public static final IntegerKey AUTHORITY_MIN_IDLE = new IntegerKey(1);

    /**
     * The number of idle AuthorityFactories.
     * <p>
     * This hint is treated as a recommendation for AbstractAuthorityMediator
     * instances such as HsqlDialectEpsgMediator. As such this will control
     * the number of connections the mediator is comfortable having open.
     * <p>
     * If AUTHORITY_MAX_ACTIVE is set to 20, up to twenty connections will
     * be used during heavy load. If the AUTHORITY_MAX_IDLE is set to 10,
     * connections will be immediately reclaimed until only 10 are open.
     * As these 10 remain idle for AUTHORITY_
     * <p>
     * When the amount of time specified by AUTHORITY_IDLE_WAIT is non zero
     * Max idle controls the maximum number of objects that can sit idle in the
     * pool at any time. When negative, there is no limit to the number of
     * objects that may be idle at one time.
     *
     * @since 2.5
     */
    public static final IntegerKey AUTHORITY_MAX_IDLE = new IntegerKey(2);

    /**
     * When the evictor is run, if more time (in milliseconds) than the value in
     * {@code AUTHORITY_MIN_EVICT_IDLETIME} has passed, then the worker is destroyed.
     *
     * @since 2.5
     */
    public static final IntegerKey AUTHORITY_MIN_EVICT_IDLETIME = new IntegerKey(2 * 60 * 1000);

    /**
     * When the evictor is run, workers which have been idle for more than this
     * value will be destroyed if and only if the number of idle workers exceeds
     * AUTHORITY_MIN_IDLE.
     *
     * @since 2.5
     */
    public static final IntegerKey AUTHORITY_SOFTMIN_EVICT_IDLETIME = new IntegerKey(10 * 1000);

    /**
     * Time in milliseconds to wait between eviction runs.
     *
     * @since 2.5
     */
    public static final IntegerKey AUTHORITY_TIME_BETWEEN_EVICTION_RUNS = new IntegerKey(5 * 1000);

    /**
     * Constructs an initially empty set of hints.
     *
     * @since 2.5
     */
    public HintsPending() {
        super();
    }

    /**
     * Constructs a new object with the specified key/value pair.
     *
     * @param key   The key of the particular hint property.
     * @param value The value of the hint property specified with {@code key}.
     */
    public HintsPending(final RenderingHints.Key key, final Object value) {
        super(key, value);
    }

    /**
     * Constructs a new object with two key/value pair.
     *
     * @param key1   The key for the first pair.
     * @param value1 The value for the first pair.
     * @param key2   The key2 for the second pair.
     * @param value2 The value2 for the second pair.
     *
     * @since 2.4
     */
    public HintsPending(final RenderingHints.Key key1, final Object value1,
                 final RenderingHints.Key key2, final Object value2)
    {
        super(key1, value1, key2, value2);
    }

    /**
     * Constructs a new object from key/value pair.
     *
     * @param key1   The key for the first pair.
     * @param value1 The value for the first pair.
     * @param key2   The key2 for the second pair.
     * @param value2 The value2 for the second pair.
     * @param pairs  Additional pairs of keys and values.
     *
     * @since 2.4
     */
    public HintsPending(final RenderingHints.Key key1, final Object value1,
                 final RenderingHints.Key key2, final Object value2,
                 final Object... pairs)
    {
        super(key1, value1, key2, value2, pairs);
    }

    /**
     * Constructs a new object with keys and values initialized from the
     * specified map (which may be null).
     *
     * @param hints A map of key/value pairs to initialize the hints, or
     *              {@code null} if the object should be empty.
     */
    public HintsPending(final Map<? extends RenderingHints.Key, ?> hints) {
        super(hints);
    }

    /**
     * Constructs a new object with keys and values initialized from the
     * specified hints (which may be null).
     *
     * @param hints A map of key/value pairs to initialize the hints, or
     *              {@code null} if the object should be empty.
     *
     * @since 2.5
     */
    public HintsPending(final RenderingHints hints) {
        super(hints);
    }
}

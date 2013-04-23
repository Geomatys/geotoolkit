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
package org.geotoolkit.factory;

import java.awt.RenderingHints;
import javax.swing.event.ChangeListener; // For javadoc
import java.io.File;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.naming.Name;
import javax.sql.DataSource;
import net.jcip.annotations.Immutable;

import org.opengis.util.InternationalString;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;

import org.geotoolkit.lang.Configuration;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.util.collection.XCollections.unmodifiableOrCopy;


/**
 * A set of hints providing control on factories to be used. They provides a way to control
 * low-level details. When hints are used in conjunction with {@linkplain FactoryRegistry
 * factory registry} (the Geotk service discovery mechanism), we have the complete Geotk
 * plugin system. By using hints to allow application code to effect service discovery,
 * we allow client code to retarget the Geotk library for their needs.
 * <p>
 * The following example fetch a {@linkplain CoordinateOperationFactory coordinate operation factory}
 * which is tolerant to the lack of Bursa-Wolf parameters:
 *
 * {@preformat java
 *     Hints hints = new Hints(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
 *     CoordinateOperationFactory factory = FactoryFinder.getCoordinateOperationFactory(hints);
 * }
 *
 * Hints may be ignored if they do not apply to the object to be instantiated.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.18
 *
 * @see Factory
 * @see FactoryRegistry
 *
 * @since 2.0
 * @module
 */
public class Hints extends RenderingHints {
    /**
     * A set of system-wide hints to use by default. Only one thread is expected to write
     * (while more are allowed).
     */
    private static final Map<RenderingHints.Key,Object> GLOBAL = new ConcurrentHashMap<>(8, 0.75f, 1);



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                      Metadata                          ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The {@link org.opengis.util.NameFactory} instance to use.
     *
     * @see FactoryFinder#getNameFactory(Hints)
     *
     * @since 3.00
     * @category Metadata
     */
    public static final ClassKey NAME_FACTORY = new ClassKey(
            "org.opengis.util.NameFactory");

    /**
     * The {@link org.opengis.metadata.citation.CitationFactory} instance to use.
     *
     * @see FactoryFinder#getCitationFactory(Hints)
     *
     * @since 3.00
     * @category Metadata
     */
    public static final ClassKey CITATION_FACTORY = new ClassKey(
            "org.opengis.metadata.citation.CitationFactory");



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////              Coordinate Reference Systems              ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The {@link org.opengis.referencing.crs.CRSAuthorityFactory} instance to use.
     *
     * @see AuthorityFactoryFinder#getCRSAuthorityFactory(String, Hints)
     * @category Referencing
     */
    public static final ClassKey CRS_AUTHORITY_FACTORY = new ClassKey(
            "org.opengis.referencing.crs.CRSAuthorityFactory");

    /**
     * The {@link org.opengis.referencing.cs.CSAuthorityFactory} instance to use.
     *
     * @see AuthorityFactoryFinder#getCSAuthorityFactory(String, Hints)
     * @category Referencing
     */
    public static final ClassKey CS_AUTHORITY_FACTORY = new ClassKey(
            "org.opengis.referencing.cs.CSAuthorityFactory");

    /**
     * The {@link org.opengis.referencing.datum.DatumAuthorityFactory} instance to use.
     *
     * @see AuthorityFactoryFinder#getDatumAuthorityFactory(String, Hints)
     * @category Referencing
     */
    public static final ClassKey DATUM_AUTHORITY_FACTORY = new ClassKey(
            "org.opengis.referencing.datum.DatumAuthorityFactory");

    /**
     * The {@link org.opengis.referencing.crs.CRSFactory} instance to use.
     *
     * @see FactoryFinder#getCRSFactory(Hints)
     * @category Referencing
     */
    public static final ClassKey CRS_FACTORY = new ClassKey(
            "org.opengis.referencing.crs.CRSFactory");

    /**
     * The {@link org.opengis.referencing.cs.CSFactory} instance to use.
     *
     * @see FactoryFinder#getCSFactory(Hints)
     * @category Referencing
     */
    public static final ClassKey CS_FACTORY = new ClassKey(
            "org.opengis.referencing.cs.CSFactory");

    /**
     * The {@link org.opengis.referencing.datum.DatumFactory} instance to use.
     *
     * @see FactoryFinder#getDatumFactory(Hints)
     * @category Referencing
     */
    public static final ClassKey DATUM_FACTORY = new ClassKey(
            "org.opengis.referencing.datum.DatumFactory");

    /**
     * The {@link org.opengis.referencing.operation.CoordinateOperationFactory} instance to use.
     *
     * @see FactoryFinder#getCoordinateOperationFactory(Hints)
     * @category Referencing
     */
    public static final ClassKey COORDINATE_OPERATION_FACTORY = new ClassKey(
            "org.opengis.referencing.operation.CoordinateOperationFactory");

    /**
     * The {@link org.opengis.referencing.operation.CoordinateOperationAuthorityFactory} instance
     * to use.
     *
     * @see AuthorityFactoryFinder#getCoordinateOperationAuthorityFactory(String, Hints)
     * @category Referencing
     */
    public static final ClassKey COORDINATE_OPERATION_AUTHORITY_FACTORY = new ClassKey(
            "org.opengis.referencing.operation.CoordinateOperationAuthorityFactory");

    /**
     * The {@link org.opengis.referencing.operation.MathTransformFactory} instance to use.
     *
     * @see FactoryFinder#getMathTransformFactory(Hints)
     * @category Referencing
     */
    public static final ClassKey MATH_TRANSFORM_FACTORY = new ClassKey(
            "org.opengis.referencing.operation.MathTransformFactory");

    /**
     * The default {@link org.opengis.referencing.crs.CoordinateReferenceSystem}
     * to use. This is used by some factories capable to provide a default CRS
     * when no one were explicitly specified by the user.
     *
     * @since 2.2
     * @category Referencing
     */
    public static final Key DEFAULT_COORDINATE_REFERENCE_SYSTEM = new Key(
            "org.opengis.referencing.crs.CoordinateReferenceSystem");

    /**
     * Used to direct WKT CRS Authority to a directory containing extra definitions.
     * The value should be an instance of {@link File} or {@link String} refering to
     * an existing directory.
     * <p>
     * Filenames in the supplied directory should be of the form
     * <code><var>authority</var>.properties</code> where <var>authority</var>
     * is the authority name space to use. For example the
     * {@value org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory#FILENAME}
     * file contains extra CRS to add as new EPSG codes.
     *
     * @since 2.4
     * @category Referencing
     */
    public static final FileKey CRS_AUTHORITY_EXTRA_DIRECTORY = new FileKey(false);

    /**
     * The {@linkplain javax.sql.DataSource data source} name to lookup from JNDI when
     * initializing the {@linkplain org.geotoolkit.referencing.factory.epsg EPSG factory}.
     * Possible values:
     * <p>
     * <ul>
     *   <li>{@link javax.sql.DataSource} - used as is.</li>
     *   <li>{@link javax.naming.Name} - used with JNDI to locate data source. This hint has no
     *       effect if there is no {@linkplain javax.naming.InitialContext JNDI initial context}
     *       setup.</li>
     *   <li>{@link String} - used with JNDI to locate data source. This hint has no effect if
     *       there is no {@linkplain javax.naming.InitialContext JNDI initial context} setup.</li>
     * </ul>
     *
     * @since 2.4
     * @category Referencing
     */
    public static final Key EPSG_DATA_SOURCE = new DataSourceKey();

    /**
     * The preferred datum shift method to use for {@linkplain CoordinateOperation coordinate operations}.
     * Valid values include {@code "Molodensky"}, {@code "Abridged Molodensky"} and {@code "Geocentric"}.
     * Other values may be supplied if a {@linkplain MathTransform math transform} exists for that
     * name, but this is not guaranteed to work.
     *
     * @see FactoryFinder#getCoordinateOperationFactory(Hints)
     * @category Referencing
     */
    public static final OptionKey DATUM_SHIFT_METHOD = new OptionKey(
            "Molodensky", "Abridged Molodensky", // EPSG names
            "Molodenski", "Abridged_Molodenski", // OGC names
            "Geocentric", "*");

    /**
     * Tells if {@linkplain CoordinateOperation coordinate operations} should be allowed even when
     * a datum shift is required while no method is found applicable. It may be for example that no
     * {@linkplain org.geotoolkit.referencing.datum.BursaWolfParameters Bursa Wolf parameters} were
     * found for a datum shift. The default value is {@link Boolean#FALSE FALSE}, which means
     * that {@linkplain org.geotoolkit.referencing.operation.DefaultCoordinateOperationFactory
     * coordinate operation factory} throws an exception if such a case occurs.
     * <p>
     * If this hint is set to {@code TRUE}, then the users are encouraged to check the
     * {@linkplain CoordinateOperation#getCoordinateOperationAccuracy coordinate operation accuracy}
     * for every transformation created. If the set of operation accuracy contains
     * {@link org.geotoolkit.metadata.iso.quality.AbstractPositionalAccuracy#DATUM_SHIFT_OMITTED
     * DATUM_SHIFT_OMITTED}, this means that an "ellipsoid shift" were applied without real datum
     * shift method available, and the transformed coordinates may have one kilometer error. The
     * application should warn the user (e.g. popup a message dialog box) in such case.
     *
     * @see FactoryFinder#getCoordinateOperationFactory(Hints)
     * @category Referencing
     */
    public static final Key LENIENT_DATUM_SHIFT = new Key(Boolean.class);

    /**
     * Tells if the {@linkplain CoordinateSystem coordinate systems} created by
     * an {@linkplain CSAuthorityFactory authority factory} should be forced to
     * (<var>longitude</var>, <var>latitude</var>) axis order. This hint is especially useful
     * for creating {@linkplain CoordinateReferenceSystem coordinate reference system} objects
     * from <A HREF="http://www.epsg.org">EPSG</A> codes. Most {@linkplain GeographicCRS geographic
     * CRS} defined in the EPSG database use (<var>latitude</var>, <var>longitude</var>) axis order.
     * Unfortunately, many data sources available in the world use the opposite axis order and still
     * claim to use a CRS described by an EPSG code. This hint allows to handle such data.
     * <p>
     * This hint can be passed to the <code>{@linkplain AuthorityFactoryFinder#getCRSAuthorityFactory
     * AuthorityFactoryFinder.getCRSAuthorityFactory}(...)</code> method. Whatever this hint is
     * supported or not is authority dependent. In the default Geotk configuration, this hint
     * is honored for codes in the {@code "EPSG"} namespace but ignored for codes in the
     * {@code "urn:ogc"} namespace. See {@link #FORCE_AXIS_ORDER_HONORING} for changing this
     * behavior.
     *
     * {@note
     * The documentation saids "<cite>longitude first</cite>" for simplicity, because the axes
     * reordering apply mostly to geographic CRS (in contrast, most projected CRS already have
     * (<var>x</var>, <var>y</var>) axis order, in which case this hint has no effect). However,
     * what Geotk actually does is to force a <cite>right-handed</cite> coordinate system. This
     * approach works for projected CRS as well as geographic CRS ("<cite>longitude first</cite>"
     * is an inappropriate expression for projected CRS). It even works in cases like stereographic
     * projections, where the axes names look like (<var>South along 180°</var>, <var>South along 90°E</var>).
     * In such cases, aiming for "<cite>longitude first</cite>" would not make sense.}
     *
     * @see AuthorityFactoryFinder#getCSAuthorityFactory(String, Hints)
     * @see AuthorityFactoryFinder#getCRSAuthorityFactory(String, Hints)
     * @see org.geotoolkit.referencing.factory.OrderedAxisAuthorityFactory
     * @see org.geotoolkit.referencing.factory.epsg.LongitudeFirstEpsgFactory
     *
     * @since 2.3
     * @category Referencing
     */
    public static final Key FORCE_LONGITUDE_FIRST_AXIS_ORDER = new Key(Boolean.class);

    /**
     * Applies the {@link #FORCE_LONGITUDE_FIRST_AXIS_ORDER} hint to some factories that usually
     * ignore it. The <cite>axis order</cite> issue is of concern mostly to the {@code "EPSG"} name
     * space. Codes in the {@value org.geotoolkit.referencing.factory.web.HTTP_AuthorityFactory#BASE_URL}
     * or {@code "urn:ogc"} name space usually ignore the axis order hint, especially the later
     * which is clearly defined by international standards and does <strong>not</strong> allow
     * the {@code FORCE_LONGITUDE_FIRST_AXIS_ORDER} behavior in standard-compliant application.
     * <p>
     * If nevertheless a user really wants the {@code FORCE_LONGITUDE_FIRST_AXIS_ORDER} behavior
     * despite the violation of standards, then he must explicitly assigns a comma separated list
     * of authorities to this {@code FORCE_AXIS_ORDER_HONORING} hint. For example in order to apply
     * the (<var>longitude</var>, <var>latitude</var>) axis order to {@code "http://www.opengis.net/"}
     * and {@code "urn:ogc"} name spaces in addition of EPSG, use the following hints:
     *
     * {@preformat java
     *     hints.put(FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
     *     hints.put(FORCE_AXIS_ORDER_HONORING, "epsg, http, urn");
     * }
     *
     * Let stress again that the application of (<var>longitude</var>, <var>latitude</var>) axis
     * order to the {@code "urn:ogc"} name space is a clear violation of OGC specification, which
     * is why Geotk wants you to provide this additional hint meaning "I'm really sure". Note
     * also that {@code "epsg"} is implicit and doesn't need to be included in the above list,
     * but this example does so as a matter of principle.
     *
     * @since 2.4
     * @category Referencing
     */
    public static final Key FORCE_AXIS_ORDER_HONORING = new Key(String.class);

    /**
     * Tells if the {@linkplain CoordinateSystem coordinate systems} created by an
     * {@linkplain CSAuthorityFactory authority factory} should be forced to standard
     * {@linkplain CoordinateSystemAxis#getDirection axis directions}. If {@code true},
     * then {@linkplain AxisDirection#SOUTH South} axis directions are forced to
     * {@linkplain AxisDirection#NORTH North}, {@linkplain AxisDirection#WEST West} axis
     * directions are forced to {@linkplain AxisDirection#EAST East}, <i>etc.</i>
     * If {@code false}, then the axis directions are left unchanged.
     * <p>
     * This hint shall be passed to the
     * <code>{@linkplain AuthorityFactoryFinder#getCRSAuthorityFactory
     * AuthorityFactoryFinder.getCRSAuthorityFactory}(...)</code>
     * method. Whatever this hint is supported or not is authority dependent.
     *
     * @see FactoryFinder#getCSFactory(Hints)
     * @see FactoryFinder#getCRSFactory(Hints)
     * @see org.geotoolkit.referencing.factory.OrderedAxisAuthorityFactory
     *
     * @since 2.3
     * @category Referencing
     */
    public static final Key FORCE_STANDARD_AXIS_DIRECTIONS = new Key(Boolean.class);

    /**
     * Tells if the {@linkplain CoordinateSystem coordinate systems} created by an
     * {@linkplain CSAuthorityFactory authority factory} should be forced to standard
     * {@linkplain CoordinateSystemAxis#getUnit axis units}. If {@code true}, then all
     * angular units are forced to degrees and linear units to meters. If {@code false},
     * then the axis units are left unchanged.
     * <p>
     * This hint shall be passed to the
     * <code>{@linkplain AuthorityFactoryFinder#getCRSAuthorityFactory
     * AuthorityFactoryFinder.getCRSAuthorityFactory}(...)</code> method. Whatever this hint is
     * supported or not is authority dependent.
     *
     * @see FactoryFinder#getCSFactory(Hints)
     * @see FactoryFinder#getCRSFactory(Hints)
     * @see org.geotoolkit.referencing.factory.OrderedAxisAuthorityFactory
     *
     * @since 2.3
     * @category Referencing
     */
    public static final Key FORCE_STANDARD_AXIS_UNITS = new Key(Boolean.class);

    /**
     * Version number of the requested service. This hint is used for example in order to get
     * a {@linkplain org.opengis.referencing.crs.CRSAuthorityFactory CRS authority factory}
     * backed by a particular version of EPSG database. The value should be an instance of
     * {@link org.geotoolkit.util.Version}.
     *
     * @since 2.4
     * @category Referencing
     */
    public static final Key VERSION = new Key("org.geotoolkit.util.Version");



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                     Grid Coverages                     ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The {@link javax.media.jai.JAI} instance to use.
     */
    public static final Key JAI_INSTANCE = new Key("javax.media.jai.JAI");

    /**
     * The {@linkplain javax.media.jai.tilecodec.TileEncoder tile encoder} name
     * (as a {@link String} value) to use during serialization of image data in
     * a {@link org.geotoolkit.coverage.grid.GridCoverage2D} object. This encoding
     * is given to the {@link javax.media.jai.remote.SerializableRenderedImage}
     * constructor. Valid values include (but is not limited to) {@code "raw"},
     * {@code "gzip"} and {@code "jpeg"}.
     *
     * {@note We recommend to avoid the <code>"jpeg"</code> codec for grid coverages.}
     *
     * @see org.geotoolkit.coverage.CoverageFactoryFinder#getGridCoverageFactory(Hints)
     *
     * @since 2.3
     * @category Coverage
     */
    public static final Key TILE_ENCODING = new Key(String.class);

    /**
     * The {@link org.opengis.coverage.processing.GridCoverageProcessor} instance to use.
     *
     * @see org.geotoolkit.coverage.CoverageFactoryFinder#getCoverageProcessor(Hints)
     *
     * @category Coverage
     */
    public static final ClassKey GRID_COVERAGE_PROCESSOR = new ClassKey("org.opengis.coverage.processing.GridCoverageProcessor");

    /**
     * Forces the {@linkplain org.opengis.coverage.processing.GridCoverageProcessor grid coverage
     * processor} to perform operations on the specified view.
     * <p>
     * Some operation when called on a {@linkplain org.geotoolkit.coverage.grid.GridCoverage2D grid
     * coverage} tries to converts to {@linkplain org.geotoolkit.coverage.grid.ViewType#GEOPHYSICS
     * geophysics} view before to execute. The rationale behind this is that the other views are
     * just the rendered version of a coverage data, and operations like interpolations have a
     * physical meaning only when applied on the geophysics view (e.g. interpolate <cite>Sea
     * Surface Temperature</cite> (SST) values, not the RGB values that colorize the temperature).
     * <p>
     * However, in some cases like when doing pure rendering of images, we might want to force
     * operations to work on {@linkplain org.geotoolkit.coverage.grid.ViewType#PHOTOGRAPHIC
     * photographic} view directly, even performing color expansions as needed. This can be
     * accomplished by setting this hint to the desired view. Be aware that interpolations
     * after color expansions may produce colors that do not accuratly represent the geophysical
     * value.
     *
     * @since 2.5
     * @category Coverage
     */
    public static final Key COVERAGE_PROCESSING_VIEW = new Key("org.geotoolkit.coverage.grid.ViewType");

    /**
     * The {@link org.opengis.coverage.SampleDimensionType} to use.
     *
     * @category Coverage
     */
    public static final Key SAMPLE_DIMENSION_TYPE = new Key("org.opengis.coverage.SampleDimensionType");



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                        Temporal                        ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The {@link org.opengis.temporal.TemporalFactory} instance to use.
     *
     * @see FactoryFinder#getTemporalFactory(Hints)
     *
     * @category Temporal
     *
     * @since 3.18
     */
    public static final ClassKey TEMPORAL_FACTORY = new ClassKey("org.opengis.temporal.TemporalFactory");



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                        Geometry                        ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The {@link org.opengis.geometry.PositionFactory} instance to use.
     *
     * @see FactoryFinder#getPositionFactory(Hints)
     *
     * @category Geometry
     *
     * @since 3.01
     */
    public static final ClassKey POSITION_FACTORY = new ClassKey("org.opengis.geometry.PositionFactory");

    /**
     * The {@link org.opengis.geometry.primitive.PrimitiveFactory} instance to use.
     *
     * @see FactoryFinder#getPrimitiveFactory(Hints)
     *
     * @category Geometry
     *
     * @since 3.01
     */
    public static final ClassKey PRIMITIVE_FACTORY = new ClassKey("org.opengis.geometry.primitive.PrimitiveFactory");

    /**
     * The {@link org.opengis.geometry.coordinate.GeometryFactory} instance to use.
     *
     * @see FactoryFinder#getGeometryFactory(Hints)
     *
     * @category Geometry
     *
     * @since 3.01
     */
    public static final ClassKey GEOMETRY_FACTORY = new ClassKey("org.opengis.geometry.coordinate.GeometryFactory");

    /**
     * The {@link org.opengis.geometry.complex.ComplexFactory} instance to use.
     *
     * @see FactoryFinder#getComplexFactory(Hints)
     *
     * @category Geometry
     *
     * @since 3.01
     */
    public static final ClassKey COMPLEX_FACTORY = new ClassKey("org.opengis.geometry.complex.ComplexFactory");

    /**
     * The {@link org.opengis.geometry.aggregate.AggregateFactory} instance to use.
     *
     * @see FactoryFinder#getAggregateFactory(Hints)
     *
     * @category Geometry
     *
     * @since 3.01
     */
    public static final ClassKey AGGREGATE_FACTORY = new ClassKey("org.opengis.geometry.aggregate.AggregateFactory");



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////               Feature, Filter and Style                ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The {@link org.opengis.feature.type.FeatureTypeFactory} instance to use.
     *
     * @see FactoryFinder#getFeatureTypeFactory(Hints)
     *
     * @category Feature
     *
     * @since 3.15
     */
    public static final ClassKey FEATURE_TYPE_FACTORY = new ClassKey("org.opengis.feature.type.FeatureTypeFactory");

    /**
     * The {@link org.opengis.feature.FeatureFactory} instance to use.
     *
     * @see FactoryFinder#getFeatureFactory(Hints)
     *
     * @category Feature
     *
     * @since 3.01
     */
    public static final ClassKey FEATURE_FACTORY = new ClassKey("org.opengis.feature.FeatureFactory");

    /**
     * The {@link org.opengis.filter.FilterFactory} instance to use.
     *
     * @see FactoryFinder#getFilterFactory(Hints)
     *
     * @category Feature
     *
     * @since 3.00
     */
    public static final ClassKey FILTER_FACTORY = new ClassKey("org.opengis.filter.FilterFactory");

    /**
     * The {@link org.opengis.style.StyleFactory} instance to use.
     *
     * @see FactoryFinder#getStyleFactory(Hints)
     *
     * @category Feature
     *
     * @since 3.00
     */
    public static final ClassKey STYLE_FACTORY = new ClassKey("org.opengis.style.StyleFactory");

    /**
     * Creates an empty set of hints. This constructor is for {@link EmptyHints} usage only.
     * All public constructors are expected to create hints initialized to the system-wide
     * default. If an empty set of hints is really needed, use {@link EmptyHints#clone()}.
     *
     * @param dummy Ignored.
     */
    Hints(final boolean dummy) {
        super(null);
    }

    /**
     * Constructs a map of hints initialized with the system-wide default values. The default
     * values are those that were given to {@link #putSystemDefault putSystemDefault} and not
     * yet removed with {@link #removeSystemDefault removeSystemDefault}.
     *
     * @since 2.5
     */
    public Hints() {
        super(GLOBAL);
    }

    /**
     * Constructs a new map of hints with the specified key/value pair. First, an initial map
     * is created as with the {@linkplain #Hints() no-argument constructor}. This map may not
     * be empty. Then, the given key-value pair is added. If a default value was present for
     * the given key, then the given value replaces the default one.
     *
     * @param key   The key of the particular hint property.
     * @param value The value of the hint property specified with {@code key}.
     */
    public Hints(final RenderingHints.Key key, final Object value) {
        // Don't use 'super(key,value)' because it doesn't check validity.
        this();
        put(key, value);
    }

    /**
     * Constructs a new map of hints with two key/value pairs. First, an initial map is created
     * as with the {@linkplain #Hints() no-argument constructor}. This map may not be empty.
     * Then, the given key-value pairs are added. If a default value was present for a given
     * key, then the given value replaces the default one.
     *
     * @param key1   The key for the first pair.
     * @param value1 The value for the first pair.
     * @param key2   The key2 for the second pair.
     * @param value2 The value2 for the second pair.
     *
     * @since 2.4
     */
    public Hints(final RenderingHints.Key key1, final Object value1,
                 final RenderingHints.Key key2, final Object value2)
    {
        this(key1, value1);
        put (key2, value2);
    }

    /**
     * Constructs a new map of hints from key/value pairs. First, an initial map is created
     * as with the {@linkplain #Hints() no-argument constructor}. This map may not be empty.
     * Then, the given key-value pairs are added. If a default value was present for a given
     * key, then the given value replaces the default one.
     *
     * @param key1   The key for the first pair.
     * @param value1 The value for the first pair.
     * @param key2   The key2 for the second pair.
     * @param value2 The value2 for the second pair.
     * @param pairs  Additional pairs of keys and values.
     *
     * @since 2.4
     */
    public Hints(final RenderingHints.Key key1, final Object value1,
                 final RenderingHints.Key key2, final Object value2,
                 final Object... pairs)
    {
        this(key1, value1, key2, value2);
        if ((pairs.length & 1) != 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ODD_ARRAY_LENGTH_1, pairs.length));
        }
        for (int i=0; i<pairs.length;) {
            put(pairs[i++], pairs[i++]);
        }
    }

    /**
     * Constructs a new object with keys and values from the given map (which may be null).
     * First, an initial map is created as with the {@linkplain #Hints() no-argument constructor}.
     * This map may not be empty. Then, the given key-value pairs are added. If a default value
     * was presents for a given key, then the given value replace the default one.
     *
     * @param hints A map of key/value pairs to initialize the hints, or {@code null} if none.
     */
    public Hints(final Map<? extends RenderingHints.Key, ?> hints) {
        this();
        if (hints != null) {
            putAll(hints);
        }
    }

    /**
     * Constructs a new object with keys and values from the given map (which may be null).
     * First, an initial map is created as with the {@linkplain #Hints() no-argument constructor}.
     * This map may not be empty. Then, the given key-value pairs are added. If a default value
     * was presents for a given key, then the given value replace the default one.
     *
     * @param hints A map of key/value pairs to initialize the hints, or {@code null} if none.
     *
     * @since 2.5
     */
    public Hints(final RenderingHints hints) {
        this();
        if (hints != null) {
            putAll(hints);
        }
    }

    /**
     * Returns a new map of hints with the same content than this map.
     *
     * @since 2.5
     */
    @Override
    public Hints clone() {
        return (Hints) super.clone();
    }

    /**
     * Returns the system-wide default value for the given key. The Geotk library
     * initially contains no system default, so {@code getSystemDefault(key)} returns
     * null for all keys. Users can add default values using {@link #putSystemDefault
     * putSystemDefault}.
     * <p>
     * To get a map of all system defaults, use {@code new Hints()}.
     *
     * @param  key The hints key.
     * @return The system-wide default value for the given key,
     *         or {@code null} if the key did not have a mapping.
     *
     * @since 2.4
     */
    public static Object getSystemDefault(final RenderingHints.Key key) {
        return GLOBAL.get(key);
    }

    /**
     * Adds or modifies a system-wide default value. {@code Hints} instances created after
     * this method call will be initialized to the union of all values specified with
     * {@code putSystemDefault} and not yet {@linkplain #removeSystemDefault removed}.
     * <p>
     * If the given value is different than the previous one, then this method notifies
     * every listeners registered with {@link Factories#addChangeListener(ChangeListener)}.
     *
     * @param  key   The hint key.
     * @param  value The hint value to be used as the system-wide default for the given key.
     * @return The previous value for the given key, or {@code null} if none.
     * @throws IllegalArgumentException if {@link Hints.Key#isCompatibleValue(Object)}
     *         returns {@code false} for the given value.
     *
     * @since 2.4
     */
    @Configuration
    public static Object putSystemDefault(final RenderingHints.Key key, final Object value) {
        if (!key.isCompatibleValue(value)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_2, nameOf(key), value));
        }
        final Object old = GLOBAL.put(key, value);
        if (!Objects.equals(value, old)) {
            Factories.fireConfigurationChanged(Hints.class);
        }
        return old;
    }

    /**
     * Removes the specified hints from the set of system default values.
     * If the a value was present for the given key, then this method notifies
     * every listeners registered with {@link Factories#addChangeListener(ChangeListener)}.
     *
     * @param  key The hints key that needs to be removed.
     * @return The value to which the key had previously been mapped,
     *         or {@code null} if the key did not have a mapping.
     *
     * @since 2.4
     */
    @Configuration
    public static Object removeSystemDefault(final RenderingHints.Key key) {
        final Object old = GLOBAL.remove(key);
        if (old != null) {
            Factories.fireConfigurationChanged(Hints.class);
        }
        return old;
    }

    /**
     * Returns a string representation of the hints. The default implementation formats
     * the set of hints as a tree.
     *
     * @since 2.4
     */
    @Override
    public String toString() {
        return Factory.toString(this);
    }

    /**
     * Returns the enclosing class of the given key, or {@code null} if none. A special case
     * is applied for {@code sun.awt.SunHints}, which maps to {@link RenderingHints}.
     */
    private static Class<?> getEnclosingClass(final RenderingHints.Key key) {
        Class<?> c = key.getClass().getEnclosingClass();
        if (c != null && c.getName().startsWith("sun.")) {
            c = RenderingHints.class;
        }
        return c;
    }

    /**
     * Tries to find the name of the given key, using reflection.
     *
     * @param  key The key for which a name is wanted, or {@code null}.
     * @return The key name as declared in the static constants.
     */
    static String nameOf(final RenderingHints.Key key) {
        if (key == null) {
            return null;
        }
        if (!(key instanceof Key)) {
            final Field field = fieldOf(key);
            if (field != null) {
                return field.getName();
            }
        }
        return key.toString();
    }

    /**
     * Tries to find the field of the given key, using reflection. This method searches
     * for a constant declared in the enclosing class, which is typically one of:
     * <p>
     * <ul>
     *   <li>{@code org.geotoolkit.factory.Hints}  (this class)</li>
     *   <li>{@code javax.media.jai.JAI}           (assuming JAI is on the classpath)</li>
     *   <li>{@code java.awt.RenderingHints}       (actually sun.awt.SunHints at least on Sun JDK)</li>
     * </ul>
     *
     * @param  key The key for which a field is wanted, or {@code null}.
     * @return The key field as declared in the static constants.
     */
    static Field fieldOf(final RenderingHints.Key key) {
        Field field = null;
        if (key != null) {
            Class<?> c = getEnclosingClass(key);
            if (c == null || (field = fieldOf(c, key)) == null) {
                if (key instanceof Key && c != (c = ((Key) key).getValueClass())) {
                    field = fieldOf(c, key);
                }
            }
        }
        return field;
    }

    /**
     * If the given key is declared in the given class, returns its name.
     * Otherwise returns {@code null}.
     */
    private static String nameOf(final Class<?> type, final RenderingHints.Key key) {
        final Field f = fieldOf(type, key);
        return (f != null) ? f.getName() : null;
    }

    /**
     * If the given key is declared in the given class, returns its field.
     * Otherwise returns {@code null}.
     */
    private static Field fieldOf(final Class<?> type, final RenderingHints.Key key) {
        final Field[] fields = type.getFields();
        for (int i=0; i<fields.length; i++) {
            final Field f = fields[i];
            /*
             * Note: to be strict, the line below should ensure that the field is final in
             * addition of static. Unfortunately the JAI static fields are not final in JAI 1.1.
             */
            if (Modifier.isStatic(f.getModifiers())) {
                final Object v;
                try {
                    v = f.get(null);
                } catch (IllegalAccessException e) {
                    continue;
                }
                if (v == key) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * The type for keys used to control various aspects of the factory
     * creation. Factory creation impacts rendering (which is why extending
     * {@linkplain java.awt.RenderingHints.Key rendering key} is not a complete
     * non-sense), but may impact other aspects of an application as well.
     *
     * {@section Serialization}
     * Keys are serializable if the instance to serialize is declared as a public static
     * final constant in the {@linkplain Class#getEnclosingClass() enclosing class}.
     * Otherwise, an {@link java.io.NotSerializableException} will be thrown.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.05
     *
     * @since 2.1
     * @module
     */
    @Immutable
    @SuppressWarnings("serial") // Not relevant because of writeReplace()
    public static class Key extends RenderingHints.Key implements Serializable {
        /**
         * The number of keys created up to date.
         */
        private static int count;

        /**
         * The class name for {@link #valueClass}.
         */
        private final String className;

        /**
         * Base class of all values for this key. Will be created from {@link #className} only when
         * first required, in order to avoid too early class loading. This is significant for the
         * {@link #JAI_INSTANCE} key for example, in order to avoid JAI dependencies in applications
         * that do not need it.
         */
        private transient Class<?> valueClass;

        /**
         * Constructs a new key for values of the given class.
         *
         * @param classe The base class for all valid values.
         */
        public Key(final Class<?> classe) {
            this(classe.getName());
            valueClass = classe;
        }

        /**
         * Constructs a new key for values of the given class. The class is specified by name
         * instead of a {@link Class} object. This allows to defer class loading until needed.
         *
         * @param className Name of base class for all valid values.
         *
         * @since 3.00
         */
        public Key(final String className) {
            super(count());
            this.className = className;
        }

        /**
         * Workaround for RFE #4093999 ("Relax constraint on placement of this()/super()
         * call in constructors"): {@code count++} need to be executed in a synchronized
         * block since it is not an atomic operation.
         */
        private static synchronized int count() {
            return count++;
        }

        /**
         * Returns the expected class for values stored under this key.
         *
         * @return The class of values stored under this key.
         */
        public Class<?> getValueClass() {
            if (valueClass == null) {
                try {
                    valueClass = Class.forName(className);
                } catch (ClassNotFoundException exception) {
                    Logging.unexpectedException(Key.class, "getValueClass", exception);
                    valueClass = Object.class;
                }
            }
            return valueClass;
        }

        /**
         * Returns {@code true} if the specified object is a valid value for this key. The default
         * implementation checks if the specified value {@linkplain Class#isInstance is an instance}
         * of the {@linkplain #getValueClass value class}.
         * <p>
         * Note that many hint keys defined in the {@link Hints} class relax this rule and accept
         * {@link Class} object assignable to the expected {@linkplain #getValueClass value class}
         * as well.
         *
         * @param value The object to test for validity.
         * @return {@code true} if the value is valid; {@code false} otherwise.
         *
         * @see Hints.ClassKey#isCompatibleValue(Object)
         * @see Hints.FileKey#isCompatibleValue(Object)
         * @see Hints.IntegerKey#isCompatibleValue(Object)
         * @see Hints.OptionKey#isCompatibleValue(Object)
         */
        @Override
        public boolean isCompatibleValue(final Object value) {
            return getValueClass().isInstance(value);
        }

        /**
         * Returns a string representation of this key. This is mostly for debugging purpose.
         * The default implementation tries to infer the key name using reflection.
         */
        @Override
        public String toString() {
            Class<?> c = getEnclosingClass(this);
            String name = nameOf(c, this);
            if (name == null) {
                if (c != (c = getValueClass())) {
                    name = nameOf(c, this);
                }
                if (name == null) {
                    name = super.toString();
                }
            }
            return name;
        }

        /**
         * Invoked on serialization for writing a proxy instead than this {@code Key}
         * instance. The proxy will use reflection in order to restore the key as one
         * of the static constants defined in the {@linkplain Class#getEnclosingClass()
         * enclosing class} on deserialization.
         *
         * @return The proxy to be serialized instead than this {@code Key}.
         * @throws ObjectStreamException If this key can not be serialized
         *         because it is not a known constant.
         *
         * @since 3.05
         *
         * @level hidden
         */
        protected final Object writeReplace() throws ObjectStreamException {
            return new SerializedKey(this);
        }
    }

    /**
     * A key for value that may be specified either as instance of {@code T}, or as
     * {@code Class<T>}.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @Immutable
    @SuppressWarnings("serial") // Not relevant because of Key.writeReplace()
    public static final class ClassKey extends Key {
        /**
         * Constructs a new key for values of the given class.
         *
         * @param classe The base class for all valid values.
         */
        public ClassKey(final Class<?> classe) {
            super(classe);
        }

        /**
         * Constructs a new key for values of the given class. The class is specified by name
         * instead of a {@link Class} object. This allows to defer class loading until needed.
         *
         * @param className Name of base class for all valid values.
         *
         * @since 3.00
         */
        public ClassKey(final String className) {
            super(className);
        }

        /**
         * Returns {@code true} if the specified object is a valid value for this key. This
         * method checks if the specified value is non-null and is one of the following:
         * <p>
         * <ul>
         *   <li>An instance of the {@linkplain #getValueClass() expected value class}.</li>
         *   <li>A {@link Class} assignable to the expected value class.</li>
         *   <li>An array of {@code Class} objects assignable to the expected value class.</li>
         * </ul>
         */
        @Override
        public boolean isCompatibleValue(final Object value) {
            if (value == null) {
                return false;
            }
            /*
             * If the value is an array of classes, invokes this method recursively
             * in order to check the validity of each elements in the array.
             */
            if (value instanceof Class<?>[]) {
                final Class<?>[] types = (Class<?>[]) value;
                for (int i=0; i<types.length; i++) {
                    if (!isCompatibleValue(types[i])) {
                        return false;
                    }
                }
                return types.length != 0;
            }
            /*
             * If the value is a class, checks if it is assignable to the expected value class.
             * As a special case, if the value is not assignable but is an abstract class while
             * we expected an interface, we will accept this class anyway because the some sub-
             * classes may implement the interface (we dont't really know). For example the
             * AbstractAuthorityFactory class doesn't implements the CRSAuthorityFactory interface,
             * but sub-classe of it do. We make this relaxation in order to preserve compatibility,
             * but maybe we will make the check stricter in the future.
             */
            if (value instanceof Class<?>) {
                final Class<?> type = (Class<?>) value;
                final Class<?> expected = getValueClass();
                if (expected.isAssignableFrom(type)) {
                    return true;
                }
                if (expected.isInterface() && !type.isInterface()) {
                    final int modifiers = type.getModifiers();
                    if (Modifier.isAbstract(modifiers) && !Modifier.isFinal(modifiers)) {
                        return true;
                    }
                }
                return false;
            }
            return super.isCompatibleValue(value);
        }
    }

    /**
     * Key for hints to be specified as a {@link File}.
     * The file may also be specified as a {@link String} object.
     *
     * @author Martin Desruisseaux (IRD)
     * @author Jody Garnett (Refractions)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @Immutable
    @SuppressWarnings("serial") // Not relevant because of Key.writeReplace()
    public static final class FileKey extends Key {
        /**
         * {@code true} if write operations need to be allowed.
         */
        private final boolean writable;

        /**
         * Creates a new key for {@link File} value.
         *
         * @param writable {@code true} if write operations need to be allowed.
         */
        public FileKey(final boolean writable) {
            super(File.class);
            this.writable = writable;
        }

        /**
         * Returns {@code true} if the specified object is a valid file or directory.
         * The check performed depends on the value of the {@code writable} argument
         * given to the constructor:
         * <p>
         * <ul>
         *   <li>If {@code false}, then the file must exists and be {@linkplain File#canRead readable}.</li>
         *   <li>If {@code true}, then there is a choice:<ul>
         *       <li>If the file exists, it must be {@linkplain File#canWrite writeable}.</li>
         *       <li>Otherwise the file must have a {@linkplain File#getParent parent} and
         *           that parent must be writable.</li></ul></li>
         * </ul>
         */
        @Override
        public boolean isCompatibleValue(final Object value) {
            final File file;
            if (value instanceof File) {
                file = (File) value;
            } else if (value instanceof String) {
                file = new File((String) value);
            } else {
                return false;
            }
            if (writable) {
                if (file.exists()) {
                    return file.canWrite();
                } else {
                    final File parent = file.getParentFile();
                    return parent!=null && parent.canWrite();
                }
            } else {
                return file.canRead();
            }
        }
    }

    /**
     * A hint used to capture a configuration setting as an integer.
     * A default value is provided and may be checked with {@link #getDefault()}.
     *
     * @author Jody Garnett (Refractions)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @Immutable
    @SuppressWarnings("serial") // Not relevant because of Key.writeReplace()
    public static final class IntegerKey extends Key {
        /**
         * The default value.
         */
        private final int number;

        /**
         * Creates a new key with the specified default value.
         *
         * @param number The default value.
         */
        public IntegerKey(final int number) {
            super(Integer.class);
            this.number = number;
        }

        /**
         * Returns the default value.
         *
         * @return The default value.
         */
        public int getDefault(){
            return number;
        }

        /**
         * Returns the value from the specified hints as an integer. If no value were found
         * for this key, then this method returns the {@linkplain #getDefault default value}.
         *
         * @param  hints The map where to fetch the hint value, or {@code null}.
         * @return The hint value as an integer, or the default value if not hint
         *         was explicitly set.
         */
        public int toValue(final Hints hints) {
            if (hints != null) {
                final Object value = hints.get(this);
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                } else if (value instanceof CharSequence) {
                    return Integer.parseInt(value.toString());
                }
            }
            return number;
        }

        /**
         * Returns {@code true} if the specified object is a valid integer.
         */
        @Override
        public boolean isCompatibleValue(final Object value) {
            if (value instanceof Short || value instanceof Integer) {
                return true;
            }
            if (value instanceof String || value instanceof InternationalString) {
                try {
                    Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    Logging.getLogger(IntegerKey.class).finer(e.toString());
                }
            }
            return false;
        }
    }

    /**
     * Key that allows the choice of several options. The special value {@code "*"} can be used
     * as a wildcard to indicate that undocumented options may be supported (but there is no
     * assurances - see {@link Hints#DATUM_SHIFT_METHOD} for example).
     *
     * @author Jody Garnett (Refractions)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @Immutable
    @SuppressWarnings("serial") // Not relevant because of Key.writeReplace()
    public static final class OptionKey extends Key {
        /**
         * The set of options allowed.
         */
        private final Set<String> options;

        /**
         * {@code true} if the {@code "*"} wildcard was given in the set of options.
         */
        private final boolean wildcard;

        /**
         * Creates a new key for a configuration option.
         *
         * @param alternatives The available options.
         */
        public OptionKey(final String... alternatives) {
            super(String.class);
            final Set<String> options = new TreeSet<>(Arrays.asList(alternatives));
            this.wildcard = options.remove("*");
            this.options  = unmodifiableOrCopy(options);
        }

        /**
         * Returns the set of available options.
         *
         * @return The available options.
         */
        public Set<String> getOptions() {
            return options;
        }

        /**
         * Returns {@code true} if the specified object is one of the valid options. If the
         * options specified at construction time contains the {@code "*"} wildcard, then
         * this method returns {@code true} for every {@link String} object.
         */
        @Override
        public boolean isCompatibleValue(final Object value) {
            return wildcard ? (value instanceof String) : options.contains(value);
        }
    }

    /**
     * Key for hints to be specified as a {@link javax.sql.DataSource}. The file may also be
     * specified as a {@link String} or {@link Name} object. This key also allows null value,
     * which explicitly means "<cite>no data source provided</cite>".
     *
     * {@note Different JNDI implementations build up their name differently, so we may need
     *        to look for <code>"jdbc:EPSG"</code> in JBoss and <code>"jdbc/EPSG"</code> in
     *        Websphere.}
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @Immutable
    @SuppressWarnings("serial") // Not relevant because of Key.writeReplace()
    static final class DataSourceKey extends Key {
        /**
         * Creates a new key for {@link javax.sql.DataSource} value.
         */
        public DataSourceKey() {
            super(DataSource.class);
        }

        /**
         * Returns {@code true} if the specified object is a data source or data source name.
         * The {@code null} value is also accepted, which explicitly means "<cite>no data source
         * provided</cite>".
         */
        @Override
        public boolean isCompatibleValue(final Object value) {
            return (value == null) || (value instanceof DataSource) ||
                    (value instanceof String) || (value instanceof Name);
        }
    }
}

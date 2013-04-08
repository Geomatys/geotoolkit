/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.provider;

import net.jcip.annotations.Immutable;

import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.CylindricalProjection;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Transverse Mercator</cite>" projection (EPSG:9807).
 * The math transform implementations instantiated by this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.TransverseMercator}</li>
 * </ul>
 *
 * <!-- PARAMETERS TransverseMercator -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Transverse_Mercator}
 * <br><b>Area of use:</b> <font size="-1">(union of CRS domains of validity in EPSG database)</font></p>
 * <blockquote><table class="compact">
 *   <tr><td><b>in latitudes:</b></td><td class="onright">80°00.0′S</td><td>to</td><td class="onright">84°00.0′N</td></tr>
 *   <tr><td><b>in longitudes:</b></td><td class="onright">180°00.0′W</td><td>to</td><td class="onright">180°00.0′E</td></tr>
 * </table></blockquote>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code semi_major}</td><td></td></tr>
 *   <tr><td>{@code semi_minor}</td><td></td></tr>
 *   <tr><td>{@code roll_longitude}</td><td>false</td></tr>
 *   <tr><td>{@code central_meridian}</td><td>0°</td></tr>
 *   <tr><td>{@code latitude_of_origin}</td><td>0°</td></tr>
 *   <tr><td>{@code scale_factor}</td><td>1</td></tr>
 *   <tr><td>{@code false_easting}</td><td>0 metres</td></tr>
 *   <tr><td>{@code false_northing}</td><td>0 metres</td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.20
 *
 * @see <A HREF="http://www.remotesensing.org/geotiff/proj_list/transverse_mercator.html">Transverse Mercator on RemoteSensing.org</A>
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.1
 * @module
 */
@Immutable
public class TransverseMercator extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -3386587506686432398L;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     * central meridian} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-180 &hellip; 180]&deg; and default value is 0&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> CENTRAL_MERIDIAN =
            UniversalParameters.CENTRAL_MERIDIAN.select(null,
                "Longitude of natural origin",    // EPSG
                "central_meridian",               // OGC
                "Central_Meridian",               // ESRI
                "longitude_of_central_meridian",  // NetCDF
                "NatOriginLong");                 // GeoTIFF

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 0&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> LATITUDE_OF_ORIGIN = Mercator2SP.LATITUDE_OF_ORIGIN;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor
     * scale factor} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is (0 &hellip; &infin;) and default value is 1.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> SCALE_FACTOR =
            UniversalParameters.SCALE_FACTOR.select(null,
                "Scale factor at natural origin",   // EPSG
                "scale_factor_at_central_meridian", // NetCDF
                "ScaleAtNatOrigin");                // GeoTIFF

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseEasting
     * false easting} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> FALSE_EASTING = Mercator2SP.FALSE_EASTING;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing
     * false northing} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> FALSE_NORTHING = Mercator2SP.FALSE_NORTHING;

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>Transverse_Mercator</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Transverse Mercator</code></td></tr>
     *       <tr><td></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Gauss-Kruger</code></td></tr>
     *       <tr><td></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Gauss-Boaga</code></td></tr>
     *       <tr><td></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>TM</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Transverse_Mercator</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Gauss_Kruger</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>TransverseMercator</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>CT_TransverseMercator</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>tmerc</code></td></tr>
     *       <tr><td></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Transverse Mercator projection</code></td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9807</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>1</code></td></tr>
     *     </table>
     *   </th></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>semi_major</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Semi-major axis</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Semi_Major</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>semi_major_axis</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>SemiMajor</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>a</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>semi_minor</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Semi-minor axis</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Semi_Minor</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>semi_minor_axis</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>SemiMinor</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>b</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>roll_longitude</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Boolean}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Default value:</b></td><td>false</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>central_meridian</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Longitude of natural origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Central_Meridian</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>longitude_of_central_meridian</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>NatOriginLong</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>lon_0</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-180 … 180]°</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>latitude_of_origin</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Latitude of natural origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Latitude_Of_Origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>latitude_of_projection_origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>NatOriginLat</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>lat_0</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>scale_factor</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Scale factor at natural origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Scale_Factor</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>scale_factor_at_central_meridian</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>ScaleAtNatOrigin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>k</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞)</td></tr>
     *       <tr><td><b>Default value:</b></td><td>1</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>false_easting</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>False easting</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>False_Easting</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>false_easting</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>FalseEasting</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>x_0</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>false_northing</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>False northing</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>False_Northing</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>false_northing</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>FalseNorthing</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>y_0</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    public static final ParameterDescriptorGroup PARAMETERS = UniversalParameters.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,      "Transverse_Mercator"),
            new NamedIdentifier(Citations.EPSG,     "Transverse Mercator"),
            new NamedIdentifier(Citations.EPSG,     "Gauss-Kruger"),
            new NamedIdentifier(Citations.EPSG,     "Gauss-Boaga"),
            new NamedIdentifier(Citations.EPSG,     "TM"),
            new IdentifierCode (Citations.EPSG,      9807),
            new NamedIdentifier(Citations.ESRI,     "Transverse_Mercator"),
            new NamedIdentifier(Citations.ESRI,     "Gauss_Kruger"),
            new NamedIdentifier(Citations.NETCDF,   "TransverseMercator"),
            new NamedIdentifier(Citations.GEOTIFF,  "CT_TransverseMercator"),
            new IdentifierCode (Citations.GEOTIFF,   1),
            new NamedIdentifier(Citations.PROJ4,    "tmerc"),
            new NamedIdentifier(Citations.S57,      "Transverse Mercator"),
            new NamedIdentifier(Citations.S57,      "TME"),
            new IdentifierCode (Citations.S57,       13),
            new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                Vocabulary.Keys.TRANSVERSE_MERCATOR_PROJECTION)),
        }, null, new ParameterDescriptor<?>[] {
            SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
            CENTRAL_MERIDIAN, LATITUDE_OF_ORIGIN,
            SCALE_FACTOR, FALSE_EASTING, FALSE_NORTHING
        }, MapProjectionDescriptor.ADD_EARTH_RADIUS);

    /**
     * Constructs a new provider.
     */
    public TransverseMercator() {
        super(PARAMETERS);
    }

    /**
     * Constructs a new provider with the specified parameters.
     */
    TransverseMercator(final ParameterDescriptorGroup descriptor) {
        super(descriptor);
    }

    /**
     * Returns the operation type for this map projection.
     */
    @Override
    public Class<CylindricalProjection> getOperationType() {
        return CylindricalProjection.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MathTransform2D createMathTransform(ParameterValueGroup values) {
        return org.geotoolkit.referencing.operation.projection.TransverseMercator.create(getParameters(), values);
    }




    /**
     * The provider for <cite>Mercator Transverse (South Orientated)</cite> projection
     * (EPSG:9808). The coordinate axes are called <cite>Westings</cite> and <cite>Southings</cite>
     * and increment to the West and South from the origin respectively.
     * <p>
     * The terms <cite>false easting</cite> (FE) and <cite>false northing</cite> (FN) increase
     * the Westing and Southing value at the natural origin. In other words they are effectively
     * <cite>false westing</cite> (FW) and <cite>false southing</cite> (FS) respectively.
     *
     * <!-- PARAMETERS SouthOrientated -->
     * <p>The following table summarizes the parameters recognized by this provider.
     * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
     * <blockquote><p><b>Operation name:</b> {@code Transverse Mercator (South Orientated)}
     * <br><b>Area of use:</b> <font size="-1">(union of CRS domains of validity in EPSG database)</font></p>
     * <blockquote><table class="compact">
     *   <tr><td><b>in latitudes:</b></td><td class="onright">34°51.0′S</td><td>to</td><td class="onright">16°59.4′S</td></tr>
     *   <tr><td><b>in longitudes:</b></td><td class="onright">11°36.0′E</td><td>to</td><td class="onright">32°54.0′E</td></tr>
     * </table></blockquote>
     * <table class="geotk">
     *   <tr><th>Parameter name</th><th>Default value</th></tr>
     *   <tr><td>{@code semi_major}</td><td></td></tr>
     *   <tr><td>{@code semi_minor}</td><td></td></tr>
     *   <tr><td>{@code roll_longitude}</td><td>false</td></tr>
     *   <tr><td>{@code central_meridian}</td><td>0°</td></tr>
     *   <tr><td>{@code latitude_of_origin}</td><td>0°</td></tr>
     *   <tr><td>{@code scale_factor}</td><td>1</td></tr>
     *   <tr><td>{@code false_easting}</td><td>0 metres</td></tr>
     *   <tr><td>{@code false_northing}</td><td>0 metres</td></tr>
     * </table></blockquote>
     * <!-- END OF PARAMETERS -->
     *
     * @author Martin Desruisseaux (MPO, IRD, Geomatys)
     * @version 3.20
     *
     * @see org.geotoolkit.referencing.operation.projection.TransverseMercator
     * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
     *
     * @since 2.2
     * @module
     */
    @Immutable
    public static class SouthOrientated extends TransverseMercator {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -5938929136350638347L;

        /**
         * The group of all parameters expected by this coordinate operation.
         * The following table lists the operation names and the parameters recognized by Geotk.
         * Note that the terms <cite>false easting</cite> (FE) and <cite>false northing</cite> (FN)
         * increase the Westing and Southing value at the natural origin. In other words they are
         * effectively <cite>false westing</cite> (FW) and <cite>false southing</cite> (FS) respectively.
         * <p>
         * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
         * <table class="geotk" border="1">
         *   <tr><th colspan="2">
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Transverse Mercator (South Orientated)</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Transverse Mercator projection</code></td></tr>
         *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9808</code></td></tr>
         *     </table>
         *   </th></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>semi_major</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Semi-major axis</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>semi_minor</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Semi-minor axis</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>roll_longitude</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Boolean}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
         *       <tr><td><b>Default value:</b></td><td>false</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>central_meridian</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Longitude of natural origin</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[-180 … 180]°</td></tr>
         *       <tr><td><b>Default value:</b></td><td>0°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>latitude_of_origin</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Latitude of natural origin</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
         *       <tr><td><b>Default value:</b></td><td>0°</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>scale_factor</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Scale factor at natural origin</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[0…∞)</td></tr>
         *       <tr><td><b>Default value:</b></td><td>1</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>false_easting</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>False easting</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
         *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>false_northing</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>False northing</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
         *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
         *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
         *     </table>
         *   </td></tr>
         * </table>
         */
        @SuppressWarnings("hiding")
        public static final ParameterDescriptorGroup PARAMETERS;
        static {
            final Citation[] excludes = {
                Citations.ESRI, Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            };
            PARAMETERS = UniversalParameters.createDescriptorGroup(
                new ReferenceIdentifier[] {
                    new NamedIdentifier(Citations.EPSG, "Transverse Mercator (South Orientated)"),
                    new IdentifierCode (Citations.EPSG,  9808),
                    sameNameAs(Citations.GEOTOOLKIT, TransverseMercator.PARAMETERS)
            }, excludes, new ParameterDescriptor<?>[] {
                sameParameterAs(PseudoMercator.PARAMETERS, "semi_major"),
                sameParameterAs(PseudoMercator.PARAMETERS, "semi_minor"),
                ROLL_LONGITUDE,
                sameParameterAs(PseudoMercator.PARAMETERS, "central_meridian"),
                sameParameterAs(PseudoMercator.PARAMETERS, "latitude_of_origin"),
                SCALE_FACTOR,
                sameParameterAs(PseudoMercator.PARAMETERS, "false_easting"),
                sameParameterAs(PseudoMercator.PARAMETERS, "false_northing")
            }, MapProjectionDescriptor.ADD_EARTH_RADIUS);
        }

        /**
         * Constructs a new provider.
         */
        public SouthOrientated() {
            super(PARAMETERS);
        }
    }
}

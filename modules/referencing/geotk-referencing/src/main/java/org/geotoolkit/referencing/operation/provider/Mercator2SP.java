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
package org.geotoolkit.referencing.operation.provider;

import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.CylindricalProjection;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.projection.Mercator;
import org.geotoolkit.internal.referencing.DeprecatedName;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Mercator (2SP)</cite>" projection (EPSG:9805).
 * The math transform implementations instantiated by this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.Mercator}</li>
 * </ul>
 *
 * <!-- PARAMETERS Mercator2SP -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Mercator_2SP}
 * <br><b>Area of use:</b> <font size="-1">(union of CRS domains of validity in EPSG database)</font></p>
 * <blockquote><table class="compact">
 *   <tr><td><b>in latitudes:</b></td><td class="onright">55°57.0′S</td><td>to</td><td class="onright">47°06.6′N</td></tr>
 *   <tr><td><b>in longitudes:</b></td><td class="onright">157°24.6′W</td><td>to</td><td class="onright">170°53.4′E</td></tr>
 * </table></blockquote>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code semi_major}</td><td></td></tr>
 *   <tr><td>{@code semi_minor}</td><td></td></tr>
 *   <tr><td>{@code roll_longitude}</td><td>false</td></tr>
 *   <tr><td>{@code standard_parallel_1}</td><td></td></tr>
 *   <tr><td>{@code latitude_of_origin}</td><td>0°</td></tr>
 *   <tr><td>{@code central_meridian}</td><td>0°</td></tr>
 *   <tr><td>{@code false_easting}</td><td>0 metres</td></tr>
 *   <tr><td>{@code false_northing}</td><td>0 metres</td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.20
 *
 * @see <A HREF="http://www.remotesensing.org/geotiff/proj_list/mercator_2sp.html">Mercator 2SP on RemoteSensing.org</A>
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.2
 * @module
 */
@Immutable
public class Mercator2SP extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 6356028352681135786L;

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
    public static final ParameterDescriptor<Double> CENTRAL_MERIDIAN;

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
    public static final ParameterDescriptor<Double> LATITUDE_OF_ORIGIN;

    /**
     * The operation parameter descriptor for the standard parallel parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 0&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> STANDARD_PARALLEL;

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
    public static final ParameterDescriptor<Double> FALSE_EASTING;

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
    public static final ParameterDescriptor<Double> FALSE_NORTHING;

    /**
     * Parameters creation, which must be done before to initialize the {@link #PARAMETERS} field.
     */
    static {
        CENTRAL_MERIDIAN = UniversalParameters.CENTRAL_MERIDIAN.select(null,
                "Longitude of natural origin",    // EPSG
                "central_meridian",               // OGC
                "Central_Meridian",               // ESRI
                "longitude_of_projection_origin", // NetCDF
                "NatOriginLong");                 // GeoTIFF
        LATITUDE_OF_ORIGIN = UniversalParameters.LATITUDE_OF_ORIGIN.select(null,
                "Latitude of natural origin",     // EPSG
                "latitude_of_origin",             // OGC
                "Latitude_Of_Origin",             // ESRI
                "NatOriginLat");                  // GeoTIFF
        STANDARD_PARALLEL = UniversalParameters.STANDARD_PARALLEL_1.select(true, null, null, null,
                "Latitude of 1st standard parallel", // EPSG
                "standard_parallel_1",               // OGC
                "Standard_Parallel_1",               // ESRI
                "standard_parallel");                // NetCDF
        FALSE_EASTING = UniversalParameters.FALSE_EASTING.select(null,
                "False easting",    // EPSG
                "FalseEasting");    // GeoTIFF
        FALSE_NORTHING = UniversalParameters.FALSE_NORTHING.select(null,
                "False northing",   // EPSG
                "FalseNorthing");   // GeoTIFF
    }

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>Mercator_2SP</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Mercator (variant B)</code></td></tr>
     *       <tr><td></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code><del>Mercator (2SP)</del></code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Mercator</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>Mercator</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>CT_Mercator</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>merc</code></td></tr>
     *       <tr><td></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Cylindrical Mercator projection</code></td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9805</code></td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>standard_parallel_1</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Latitude of 1st standard parallel</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Standard_Parallel_1</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>standard_parallel</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>StdParallel1</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>lat_1</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>central_meridian</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Longitude of natural origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Central_Meridian</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>longitude_of_projection_origin</code></td></tr>
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
            new NamedIdentifier(Citations.OGC,     "Mercator_2SP"),
            new NamedIdentifier(Citations.EPSG,    "Mercator (variant B)"), // Starting from 7.6
            new DeprecatedName (Citations.EPSG,    "Mercator (2SP)"), // Prior to EPSG version 7.6
            new IdentifierCode (Citations.EPSG,     9805),
            new NamedIdentifier(Citations.ESRI,    "Mercator"),
            new NamedIdentifier(Citations.NETCDF,  "Mercator"),
                     sameNameAs(Citations.GEOTIFF,  Mercator1SP.PARAMETERS),
                     sameNameAs(Citations.PROJ4,    Mercator1SP.PARAMETERS),
            new NamedIdentifier(Citations.S57,     "Mercator"),
            new NamedIdentifier(Citations.S57,     "MER"),
            new IdentifierCode (Citations.S57,      8),
                     sameNameAs(Citations.GEOTOOLKIT, Mercator1SP.PARAMETERS)
        }, null, new ParameterDescriptor<?>[] {
            SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
            STANDARD_PARALLEL, LATITUDE_OF_ORIGIN, CENTRAL_MERIDIAN,
            FALSE_EASTING, FALSE_NORTHING
        }, MapProjectionDescriptor.ADD_EARTH_RADIUS);

    /**
     * Constructs a new provider.
     */
    public Mercator2SP() {
        super(PARAMETERS);
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
        return Mercator.create(getParameters(), values);
    }
}

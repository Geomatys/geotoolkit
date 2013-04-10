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

import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.ConicProjection;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.projection.LambertConformal;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Lambert Conic Conformal (2SP)</cite>" projection (EPSG:9802).
 * The math transform implementations instantiated by this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.LambertConformal}</li>
 * </ul>
 *
 * <!-- PARAMETERS LambertConformal2SP -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Lambert_Conformal_Conic_2SP}
 * <br><b>Area of use:</b> <font size="-1">(union of CRS domains of validity in EPSG database)</font></p>
 * <blockquote><table class="compact">
 *   <tr><td><b>in latitudes:</b></td><td class="onright">81°00.0′S</td><td>to</td><td class="onright">86°27.0′N</td></tr>
 *   <tr><td><b>in longitudes:</b></td><td class="onright">180°00.0′W</td><td>to</td><td class="onright">180°00.0′E</td></tr>
 * </table></blockquote>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code semi_major}</td><td></td></tr>
 *   <tr><td>{@code semi_minor}</td><td></td></tr>
 *   <tr><td>{@code roll_longitude}</td><td>false</td></tr>
 *   <tr><td>{@code central_meridian}</td><td>0°</td></tr>
 *   <tr><td>{@code latitude_of_origin}</td><td>0°</td></tr>
 *   <tr><td>{@code standard_parallel_1}</td><td><var>latitude of origin</var></td></tr>
 *   <tr><td>{@code standard_parallel_2}</td><td><var>standard parallel 1</var></td></tr>
 *   <tr><td>{@code Scale_Factor}</td><td>1</td></tr>
 *   <tr><td>{@code false_easting}</td><td>0 metres</td></tr>
 *   <tr><td>{@code false_northing}</td><td>0 metres</td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.20
 *
 * @see <A HREF="http://www.remotesensing.org/geotiff/proj_list/lambert_conic_conformal_2sp.html">Lambert Conic Conformal 2SP on RemoteSensing.org</A>
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.2
 * @module
 */
@Immutable
public class LambertConformal2SP extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 3240860802816724947L;

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
                "Longitude of false origin",     // EPSG
                "central_meridian",              // OGC
                "Central_Meridian",              // ESRI
                "longitude_of_central_meridian", // NetCDF
                "FalseOriginLong");              // GeoTIFF

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
    public static final ParameterDescriptor<Double> LATITUDE_OF_ORIGIN =
            UniversalParameters.LATITUDE_OF_ORIGIN.select(null,
                "Latitude of false origin",  // EPSG
                "latitude_of_origin",        // OGC
                "Latitude_Of_Origin",        // ESRI
                "FalseOriginLat");           // GeoTIFF

    /**
     * The operation parameter descriptor for the first {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#standardParallels
     * standard parallel} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">optional</a> - if omitted,
     * it takes the same value than the one given for {@link #LATITUDE_OF_ORIGIN}.
     * Valid values range is [-90 &hellip; 90]&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> STANDARD_PARALLEL_1 =
            UniversalParameters.STANDARD_PARALLEL_1.select(null,
                "Latitude of 1st standard parallel",  // EPSG
                "standard_parallel_1",                // OGC
                "Standard_Parallel_1",                // ESRI
                "standard_parallel[1]");              // NetCDF

    /**
     * The operation parameter descriptor for the second {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#standardParallels
     * standard parallel} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">optional</a> - if omitted,
     * it takes the same value than the one given for {@link #STANDARD_PARALLEL_1}.
     * Valid values range is [-90 &hellip; 90]&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> STANDARD_PARALLEL_2 =
            UniversalParameters.STANDARD_PARALLEL_2.select(null,
                    "Latitude of 2nd standard parallel");

    /**
     * The ESRI operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor
     * scale factor} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">optional</a>, because not
     * defined by EPSG. Valid values range is (0 &hellip; &infin;) and default value is 1.
     *
     * @since 3.20
     */
    static final ParameterDescriptor<Double> SCALE_FACTOR =
            UniversalParameters.SCALE_FACTOR.select(false, null, new Citation[] {
                Citations.EPSG, Citations.OGC, Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            }, null);

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
    public static final ParameterDescriptor<Double> FALSE_EASTING =
            UniversalParameters.FALSE_EASTING.select(null,
                "Easting at false origin",  // EPSG
                "FalseOriginEasting");      // GeoTIFF

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
    public static final ParameterDescriptor<Double> FALSE_NORTHING =
            UniversalParameters.FALSE_NORTHING.select(null,
                "Northing at false origin", // EPSG
                "FalseOriginNorthing");     // GeoTIFF

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>Lambert_Conformal_Conic_2SP</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Lambert Conic Conformal (2SP)</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Lambert_Conformal_Conic</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>LambertConformal</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>CT_LambertConfConic_2SP</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>CT_LambertConfConic</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>lcc</code></td></tr>
     *       <tr><td></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Lambert conformal conic projection</code></td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9802</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>9</code></td></tr>
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
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Longitude of false origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Central_Meridian</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>longitude_of_central_meridian</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>FalseOriginLong</code></td></tr>
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
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Latitude of false origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Latitude_Of_Origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>latitude_of_projection_origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>FalseOriginLat</code></td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>standard_parallel_1</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Latitude of 1st standard parallel</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Standard_Parallel_1</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>standard_parallel[1]</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>StdParallel1</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>lat_1</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
     *       <tr><td><b>Default value:</b></td><td><var>latitude of origin</var></td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>standard_parallel_2</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Latitude of 2nd standard parallel</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Standard_Parallel_2</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>standard_parallel[2]</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>StdParallel2</code></td></tr>
     *       <tr><td></td><td class="onright"><code>PROJ4</code>:</td><td class="onleft"><code>lat_2</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
     *       <tr><td><b>Default value:</b></td><td><var>standard parallel 1</var></td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Scale_Factor</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞)</td></tr>
     *       <tr><td><b>Default value:</b></td><td>1</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>false_easting</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Easting at false origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>False_Easting</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>false_easting</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>FalseOriginEasting</code></td></tr>
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
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Northing at false origin</code></td></tr>
     *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>False_Northing</code></td></tr>
     *       <tr><td></td><td class="onright"><code>NetCDF</code>:</td><td class="onleft"><code>false_northing</code></td></tr>
     *       <tr><td></td><td class="onright"><code>GeoTIFF</code>:</td><td class="onleft"><code>FalseOriginNorthing</code></td></tr>
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
            new NamedIdentifier(Citations.OGC,     "Lambert_Conformal_Conic_2SP"),
            new NamedIdentifier(Citations.EPSG,    "Lambert Conic Conformal (2SP)"),
            new IdentifierCode (Citations.EPSG,     9802),
            new NamedIdentifier(Citations.ESRI,    "Lambert_Conformal_Conic"),
            new NamedIdentifier(Citations.NETCDF,  "LambertConformal"),
            new NamedIdentifier(Citations.GEOTIFF, "CT_LambertConfConic_2SP"),
            new NamedIdentifier(Citations.GEOTIFF, "CT_LambertConfConic"),
            new IdentifierCode (Citations.GEOTIFF,  9), // The same code is used for 1SP.
                     sameNameAs(Citations.PROJ4,    LambertConformal1SP.PARAMETERS),
            new NamedIdentifier(Citations.S57,     "Lambert conformal conic"),
            new NamedIdentifier(Citations.S57,     "LCC"),
            new IdentifierCode (Citations.S57,      6),
                     sameNameAs(Citations.GEOTOOLKIT, LambertConformal1SP.PARAMETERS)
        }, null, new ParameterDescriptor<?>[] {
            SEMI_MAJOR,          SEMI_MINOR,
            ROLL_LONGITUDE,
            CENTRAL_MERIDIAN,    LATITUDE_OF_ORIGIN,
            STANDARD_PARALLEL_1, STANDARD_PARALLEL_2, SCALE_FACTOR,
            FALSE_EASTING,       FALSE_NORTHING
        }, MapProjectionDescriptor.ADD_EARTH_RADIUS |
           MapProjectionDescriptor.ADD_STANDARD_PARALLEL);

    /**
     * Constructs a new provider.
     */
    public LambertConformal2SP() {
        super(PARAMETERS);
    }

    /**
     * Constructs a new provider with the given descriptor.
     */
    LambertConformal2SP(final ParameterDescriptorGroup descriptor) {
        super(descriptor);
    }

    /**
     * Returns the operation type for this map projection.
     */
    @Override
    public Class<ConicProjection> getOperationType() {
        return ConicProjection.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MathTransform2D createMathTransform(ParameterValueGroup values) {
        return LambertConformal.create(getParameters(), values);
    }

    /**
     * The provider for "<cite>Lambert Conic Conformal (2SP Belgium)</cite>" projection (EPSG:9803).
     * The math transform implementations instantiated by this provider may be any of the following classes:
     * <p>
     * <ul>
     *   <li>{@link org.geotoolkit.referencing.operation.projection.LambertConformal}</li>
     * </ul>
     *
     * <!-- PARAMETERS Belgium -->
     * <p>The following table summarizes the parameters recognized by this provider.
     * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
     * <blockquote><p><b>Operation name:</b> {@code Lambert_Conformal_Conic_2SP_Belgium}
     * <br><b>Area of use:</b> <font size="-1">(union of CRS domains of validity in EPSG database)</font></p>
     * <blockquote><table class="compact">
     *   <tr><td><b>in latitudes:</b></td><td class="onright">49°30.6′N</td><td>to</td><td class="onright">51°30.0′N</td></tr>
     *   <tr><td><b>in longitudes:</b></td><td class="onright">2°32.4′E</td><td>to</td><td class="onright">6°24.0′E</td></tr>
     * </table></blockquote>
     * <table class="geotk">
     *   <tr><th>Parameter name</th><th>Default value</th></tr>
     *   <tr><td>{@code semi_major}</td><td></td></tr>
     *   <tr><td>{@code semi_minor}</td><td></td></tr>
     *   <tr><td>{@code roll_longitude}</td><td>false</td></tr>
     *   <tr><td>{@code central_meridian}</td><td>0°</td></tr>
     *   <tr><td>{@code latitude_of_origin}</td><td>0°</td></tr>
     *   <tr><td>{@code standard_parallel_1}</td><td><var>latitude of origin</var></td></tr>
     *   <tr><td>{@code standard_parallel_2}</td><td><var>standard parallel 1</var></td></tr>
     *   <tr><td>{@code false_easting}</td><td>0 metres</td></tr>
     *   <tr><td>{@code false_northing}</td><td>0 metres</td></tr>
     * </table></blockquote>
     * <!-- END OF PARAMETERS -->
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @author Rueben Schulz (UBC)
     * @version 3.20
     *
     * @see <A HREF="http://www.remotesensing.org/geotiff/proj_list/lambert_conic_conformal_2sp_belgium.html">Lambert Conic Conformal 2SP (Belgium) on RemoteSensing.org</A>
     * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
     *
     * @since 2.2
     * @module
     */
    @Immutable
    public static class Belgium extends LambertConformal2SP {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -6388030784088639876L;

        /**
         * The group of all parameters expected by this coordinate operation.
         * The following table lists the operation names and the parameters recognized by Geotk:
         * <p>
         * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
         * <table class="geotk" border="1">
         *   <tr><th colspan="2">
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>Lambert_Conformal_Conic_2SP_Belgium</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Lambert_Conformal_Conic_2SP_Belgium</code></td></tr>
         *       <tr><td></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Lambert Conic Conformal (2SP Belgium)</code></td></tr>
         *       <tr><td></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Lambert conformal conic projection</code></td></tr>
         *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9803</code></td></tr>
         *     </table>
         *   </th></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>semi_major</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Semi-major axis</code></td></tr>
         *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Semi_Major</code></td></tr>
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
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Longitude of false origin</code></td></tr>
         *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Central_Meridian</code></td></tr>
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
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Latitude of false origin</code></td></tr>
         *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Latitude_Of_Origin</code></td></tr>
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
         *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>standard_parallel_1</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Latitude of 1st standard parallel</code></td></tr>
         *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Standard_Parallel_1</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
         *       <tr><td><b>Default value:</b></td><td><var>latitude of origin</var></td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>standard_parallel_2</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Latitude of 2nd standard parallel</code></td></tr>
         *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>Standard_Parallel_2</code></td></tr>
         *     </table>
         *   </td><td>
         *     <table class="compact">
         *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
         *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
         *       <tr><td><b>Value range:</b></td><td>[-90 … 90]°</td></tr>
         *       <tr><td><b>Default value:</b></td><td><var>standard parallel 1</var></td></tr>
         *     </table>
         *   </td></tr>
         *   <tr><td>
         *     <table class="compact">
         *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>false_easting</code></td></tr>
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Easting at false origin</code></td></tr>
         *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>False_Easting</code></td></tr>
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
         *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Northing at false origin</code></td></tr>
         *       <tr><td></td><td class="onright"><code>ESRI</code>:</td><td class="onleft"><code>False_Northing</code></td></tr>
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
        public static final ParameterDescriptorGroup PARAMETERS = UniversalParameters.createDescriptorGroup(
            new ReferenceIdentifier[] {
                /*
                 * IMPORTANT: Do not put any name that could be confused with the 1SP or
                 * 2SP cases below, except for the Citations.GEOTOOLKIT authority which is
                 * ignored. The LambertConformal constructor relies on those names for
                 * distinguish the kind of projection being created.
                 */
                new NamedIdentifier(Citations.OGC,  "Lambert_Conformal_Conic_2SP_Belgium"),
                new NamedIdentifier(Citations.ESRI, "Lambert_Conformal_Conic_2SP_Belgium"),
                new NamedIdentifier(Citations.EPSG, "Lambert Conic Conformal (2SP Belgium)"),
                new IdentifierCode (Citations.EPSG,  9803),
                        sameNameAs(Citations.GEOTOOLKIT, LambertConformal2SP.PARAMETERS)
            }, new Citation[] { // Authorities to exclude from the parameter descriptors.
                Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            }, new ParameterDescriptor<?>[] {
                sameParameterAs(PlateCarree.PARAMETERS, "semi_major"),
                sameParameterAs(PlateCarree.PARAMETERS, "semi_minor"),
                ROLL_LONGITUDE,
                CENTRAL_MERIDIAN,    LATITUDE_OF_ORIGIN,
                STANDARD_PARALLEL_1, STANDARD_PARALLEL_2,
                FALSE_EASTING,       FALSE_NORTHING
            }, MapProjectionDescriptor.ADD_EARTH_RADIUS |
               MapProjectionDescriptor.ADD_STANDARD_PARALLEL);

        /**
         * Constructs a new provider.
         */
        public Belgium() {
            super(PARAMETERS);
        }
    }
}

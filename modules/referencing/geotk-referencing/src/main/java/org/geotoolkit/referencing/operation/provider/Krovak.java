/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.referencing.operation.ConicProjection;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.internal.referencing.DeprecatedName;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Krovak Oblique Conic Conformal</cite>" projection (EPSG:9819).
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/krovak.html">Krovak on RemoteSensing.org</A>.
 * The math transform implementations instantiated by this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.Krovak}</li>
 * </ul>
 *
 * @author Jan Jezek (HSRS)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
@Immutable
public class Krovak extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -278392856661204734L;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     * central meridian} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-180 &hellip; 180]&deg; and default value is
     * 24&deg;50' (which is 42&deg;30' from Ferro prime meridian).
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> LONGITUDE_OF_CENTRE;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 49.5&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> LATITUDE_OF_CENTRE;

    /**
     * The operation parameter descriptor for the {@code azimuth} parameter value. This has
     * been renamed "<cite>Co-latitude of cone axis</cite>" in latest EPSG database versions.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 30.28813972222&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> AZIMUTH;

    /**
     * The operation parameter descriptor for the first {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#standardParallels
     * standard parallel} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 78.5&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> PSEUDO_STANDARD_PARALLEL;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor
     * scale factor} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is (0 &hellip; &infin;) and default value is 0.9999.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> SCALE_FACTOR;

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
        final Citation[] excludes = new Citation[] {Citations.NETCDF};
        LONGITUDE_OF_CENTRE = Identifiers.CENTRAL_MERIDIAN.select(true, 42.5 - 17.66666666666667, excludes, null,
                "Longitude of origin",           // EPSG
                "longitude_of_center",           // OGC
                "Longitude_Of_Center",           // ESRI
                "CenterLong");                   // GeoTIFF
        LATITUDE_OF_CENTRE = Identifiers.LATITUDE_OF_ORIGIN.select(true, 49.5, excludes, null,
                "Latitude of projection centre", // EPSG
                "latitude_of_center",            // OGC
                "Latitude_Of_Center",            // ESRI
                "CenterLat");                    // GeoTIFF
        AZIMUTH = Identifiers.AZIMUTH.select(true, 30.28813972222222, excludes, null,
                "Co-latitude of cone axis",      // EPSG
                "azimuth",                       // OGC
                "AzimuthAngle");                 // GeoTIFF
        PSEUDO_STANDARD_PARALLEL = Identifiers.STANDARD_PARALLEL_1.select(true, 78.5, excludes, null,
                "Latitude of pseudo standard parallel",     // EPSG
                "pseudo_standard_parallel_1",               // OGC
                "Pseudo_Standard_Parallel_1");              // ESRI
        SCALE_FACTOR = Identifiers.SCALE_FACTOR.select(true, 0.9999, excludes, null,
                "Scale factor on pseudo standard parallel", // EPSG
                "ScaleAtCenter");                           // GeoTIFF
        // Following are the same than Mercator1SP except for the exclusion list.
        FALSE_EASTING  = EquidistantCylindrical.FALSE_EASTING;
        FALSE_NORTHING = EquidistantCylindrical.FALSE_NORTHING;
    }

    /**
     * The parameters group.
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table bgcolor="#F4F8FF" border="1" cellspacing="0" cellpadding="6">
     *   <tr bgcolor="#B9DCFF" valign="top"><td colspan="2">
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>Krovak</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Krovak</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code><del>Krovak Oblique Conic Conformal</del></code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Krovak</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>Krovak</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>krovak</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>Krovak Oblique Conformal Conic</code></td></tr>
     *       <tr><th align="left">Identifier:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>9819</code></td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>semi_major</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Semi-major axis</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Semi_Major</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>SemiMajor</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>a</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>semi_minor</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Semi-minor axis</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Semi_Minor</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>SemiMinor</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>b</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>roll_longitude</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Boolean</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>false</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>latitude_of_center</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Latitude of projection centre</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Latitude_Of_Center</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>CenterLat</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>lat_0</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-90 … 90]°</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>49.5°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>longitude_of_center</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Longitude of origin</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Longitude_Of_Center</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>CenterLong</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>lon_0</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-180 … 180]°</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>24.83333333333333°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>azimuth</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Co-latitude of cone axis</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Azimuth</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>AzimuthAngle</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-360 … 360]°</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>30.28813972222222°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>pseudo_standard_parallel_1</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Latitude of pseudo standard parallel</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Pseudo_Standard_Parallel_1</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>StdParallel1</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>lat_1</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-90 … 90]°</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>78.5°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>scale_factor</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Scale factor on pseudo standard parallel</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Scale_Factor</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>ScaleAtCenter</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>k</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞)</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0.9999</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>X_Scale</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>(-∞ … ∞)</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>1</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>Y_Scale</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>(-∞ … ∞)</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>1</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>XY_Plane_Rotation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-360 … 360]°</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0°</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>false_easting</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>False easting</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>False_Easting</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>FalseEasting</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>x_0</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>(-∞ … ∞) metres</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>false_northing</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>False northing</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>ESRI:</code></td><td><code>False_Northing</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>GeoTIFF:</code></td><td><code>FalseNorthing</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>PROJ4:</code></td><td><code>y_0</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>(-∞ … ∞) metres</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,        "Krovak"),
            new NamedIdentifier(Citations.EPSG,       "Krovak"), // Starting from EPSG version 7.6
            new DeprecatedName (Citations.EPSG,       "Krovak Oblique Conic Conformal"), // Legacy
            new IdentifierCode (Citations.EPSG,        9819),
            new NamedIdentifier(Citations.ESRI,       "Krovak"),
            new NamedIdentifier(Citations.GEOTIFF,    "Krovak"),
            new NamedIdentifier(Citations.PROJ4,      "krovak"),
            new NamedIdentifier(Citations.GEOTOOLKIT, "Krovak Oblique Conformal Conic"),
        }, null, new ParameterDescriptor<?>[] {
            (ParameterDescriptor<?>) ObliqueStereographic.PARAMETERS.descriptor("semi_major"),
            (ParameterDescriptor<?>) ObliqueStereographic.PARAMETERS.descriptor("semi_minor"),
            ROLL_LONGITUDE,
            LATITUDE_OF_CENTRE, LONGITUDE_OF_CENTRE,
            AZIMUTH, PSEUDO_STANDARD_PARALLEL, SCALE_FACTOR,
            X_SCALE, Y_SCALE, XY_PLANE_ROTATION,
            FALSE_EASTING, FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public Krovak() {
        super(PARAMETERS);
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
        return org.geotoolkit.referencing.operation.projection.Krovak.create(getParameters(), values);
    }
}
